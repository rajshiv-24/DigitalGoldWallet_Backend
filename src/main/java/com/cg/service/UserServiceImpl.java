package com.cg.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cg.dto.PaymentResponseDTO;
import com.cg.dto.UserRequestDTO;
import com.cg.dto.UserResponseDTO;
import com.cg.dto.WalletTopUpRequestDTO;
import com.cg.entity.Address;
import com.cg.entity.Payment;
import com.cg.entity.User;
import com.cg.enums.PaymentStatus;
import com.cg.enums.Role;
import com.cg.enums.TransactionType;
import com.cg.exception.DuplicateEmailException;
import com.cg.exception.InsufficientBalanceException;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.AddressRepository;
import com.cg.repo.PaymentRepository;
import com.cg.repo.PhysicalGoldTransactionRepository;
import com.cg.repo.TransactionHistoryRepository;
import com.cg.repo.UserRepository;
import com.cg.repo.VirtualGoldHoldingRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AddressRepository addressRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepo;

    @Autowired
    private PhysicalGoldTransactionRepository physicalGoldTransactionRepo;

    @Autowired
    private VirtualGoldHoldingRepository virtualGoldHoldingRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ──────────────────────────────────────────────────
    // CREATE USER
    // ──────────────────────────────────────────────────
    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Check for duplicate email
        userRepo.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new DuplicateEmailException(
                    "Email already registered: " + request.getEmail());
        });

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.ZERO);
        user.setCreatedAt(LocalDateTime.now());

        // Address is optional during registration
        if (request.getAddressId() != null) {

            Address address = addressRepo.findById(request.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Address not found with id: " + request.getAddressId()));

            user.setAddress(address);
        }

        return toResponseDTO(userRepo.save(user));
    }

    // ──────────────────────────────────────────────────
    // GET USER BY ID
    // ──────────────────────────────────────────────────
    @Override
    public UserResponseDTO getUserById(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
        return toResponseDTO(user);
    }

    // ──────────────────────────────────────────────────
    // GET ALL USERS
    // ──────────────────────────────────────────────────
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────────
    // UPDATE USER
    // ──────────────────────────────────────────────────
    @Override
    public UserResponseDTO updateUser(Integer userId, UserRequestDTO request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getAddressId() != null) {
            Address address = addressRepo.findById(request.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Address not found with id: " + request.getAddressId()));
            user.setAddress(address);
        }

        return toResponseDTO(userRepo.save(user));
    }

    // ──────────────────────────────────────────────────
    // DELETE USER
    // ──────────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        physicalGoldTransactionRepo.deleteByUserId(userId);
        transactionHistoryRepo.deleteByUserId(userId);
        virtualGoldHoldingRepo.deleteByUserId(userId);
        paymentRepo.deleteByUserId(userId);

        userRepo.deleteById(userId);
    }

    // ──────────────────────────────────────────────────
    // WALLET TOP UP
    // Adds INR to user.balance and records in payments table
    // TransactionType.CREDIT = money coming into wallet
    // ──────────────────────────────────────────────────
    @Override
    public PaymentResponseDTO topUpWallet(WalletTopUpRequestDTO request) {

        // 🔒 Get logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User loggedInUser = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));

        // ❌ BLOCK if user tries another user's wallet
        if (!loggedInUser.getUserId().equals(request.getUserId())) {
            throw new AccessDeniedException("You can only top up your own wallet");
        }

        // ✅ Now safe to proceed
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getUserId()));

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientBalanceException("Top-up amount must be greater than zero");
        }

        user.setBalance(user.getBalance().add(request.getAmount()));
        userRepo.save(user);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionType(TransactionType.CREDITED_TO_WALLET);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now());

        Payment saved = paymentRepo.save(payment);

        return toPaymentResponseDTO(saved);
    }

    // ──────────────────────────────────────────────────
    // MAPPER: User entity → UserResponseDTO
    // ──────────────────────────────────────────────────
    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole() : Role.USER);
        dto.setBalance(user.getBalance());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getAddress() != null) {
            dto.setStreet(user.getAddress().getStreet());
            dto.setCity(user.getAddress().getCity());
            dto.setState(user.getAddress().getState());
            dto.setPostalCode(user.getAddress().getPostalCode());
            dto.setCountry(user.getAddress().getCountry());
        }
        return dto;
    }

    // ──────────────────────────────────────────────────
    // MAPPER: Payment entity → PaymentResponseDTO
    // ──────────────────────────────────────────────────
    private PaymentResponseDTO toPaymentResponseDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setUserId(payment.getUser().getUserId());
        dto.setUserName(payment.getUser().getName());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionType(payment.getTransactionType());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}
