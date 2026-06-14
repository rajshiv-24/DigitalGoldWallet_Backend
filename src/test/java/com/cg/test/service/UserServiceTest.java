package com.cg.test.service;

import com.cg.dto.PaymentResponseDTO;
import com.cg.dto.UserRequestDTO;
import com.cg.dto.UserResponseDTO;
import com.cg.dto.WalletTopUpRequestDTO;
import com.cg.entity.Address;
import com.cg.entity.Payment;
import com.cg.entity.User;
import com.cg.enums.PaymentMethod;
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
import com.cg.service.UserService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class UserServiceTest {

    @MockitoBean
    private UserRepository userRepo;

    @MockitoBean
    private AddressRepository addressRepo;

    @MockitoBean
    private PaymentRepository paymentRepo;

    @MockitoBean
    private TransactionHistoryRepository transactionHistoryRepo;

    @MockitoBean
    private PhysicalGoldTransactionRepository physicalGoldTransactionRepo;

    @MockitoBean
    private VirtualGoldHoldingRepository virtualGoldHoldingRepo;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private UserRequestDTO requestDTO;
    private User user;
    private Address address;

    @BeforeEach
    public void beforeEach() {
        address = new Address();
        address.setAddressId(10);
        address.setStreet("MG Road");
        address.setCity("Delhi");
        address.setState("Delhi");
        address.setPostalCode("110001");
        address.setCountry("India");

        user = new User();
        user.setUserId(1);
        user.setName("Arjun Singh");
        user.setEmail("arjun@test.com");
        user.setPassword("encodedPassword123");
        user.setRole(Role.USER);
        user.setAddress(address);
        user.setBalance(BigDecimal.ZERO);
        user.setCreatedAt(LocalDateTime.now());

        requestDTO = new UserRequestDTO();
        requestDTO.setName("Arjun Singh");
        requestDTO.setEmail("arjun@test.com");
        requestDTO.setPassword("rawPassword123");
        requestDTO.setAddressId(10);
    }

    // ── createUser ─────────────────────────────────────────────

    @Test
    public void testCreateUser_Success() {
        Mockito.when(userRepo.findByEmail("arjun@test.com")).thenReturn(Optional.empty());
        Mockito.when(addressRepo.findById(10)).thenReturn(Optional.of(address));
        Mockito.when(passwordEncoder.encode("rawPassword123")).thenReturn("encodedPassword123");
        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.createUser(requestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Arjun Singh", result.getName());
        Mockito.verify(userRepo).findByEmail("arjun@test.com");
        Mockito.verify(addressRepo).findById(10);
        Mockito.verify(userRepo).save(Mockito.any(User.class));
    }

    @Test
    public void testCreateUser_DuplicateEmail() {
        Mockito.when(userRepo.findByEmail("arjun@test.com")).thenReturn(Optional.of(user));

        Assertions.assertThrows(DuplicateEmailException.class,
                () -> userService.createUser(requestDTO));

        Mockito.verify(userRepo).findByEmail("arjun@test.com");
        Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testCreateUser_AddressNotFound() {
        Mockito.when(userRepo.findByEmail("arjun@test.com")).thenReturn(Optional.empty());
        Mockito.when(addressRepo.findById(10)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> userService.createUser(requestDTO));

        Mockito.verify(addressRepo).findById(10);
        Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testCreateUser_BlankPassword_ThrowsIllegalArgument() {
        requestDTO.setPassword("");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(requestDTO));

        Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
    }

    // ── getUserById ────────────────────────────────────────────

    @Test
    public void testGetUserById_Found() {
        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserById(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getUserId());
        Assertions.assertEquals("Arjun Singh", result.getName());
        Mockito.verify(userRepo).findById(1);
    }

    @Test
    public void testGetUserById_NotFound() {
        Mockito.when(userRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(99));

        Mockito.verify(userRepo).findById(99);
    }

    // ── getAllUsers ────────────────────────────────────────────

    @Test
    public void testGetAllUsers_ReturnsList() {
        Mockito.when(userRepo.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.getAllUsers();

        Assertions.assertEquals(1, result.size());
        Mockito.verify(userRepo).findAll();
    }

    @Test
    public void testGetAllUsers_EmptyList() {
        Mockito.when(userRepo.findAll()).thenReturn(List.of());

        List<UserResponseDTO> result = userService.getAllUsers();

        Assertions.assertTrue(result.isEmpty());
    }

    // ── updateUser ─────────────────────────────────────────────

    @Test
    public void testUpdateUser_Success() {
        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(addressRepo.findById(10)).thenReturn(Optional.of(address));
        Mockito.when(passwordEncoder.encode("rawPassword123")).thenReturn("encodedPassword123");
        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.updateUser(1, requestDTO);

        Assertions.assertNotNull(result);
        Mockito.verify(userRepo).findById(1);
        Mockito.verify(userRepo).save(Mockito.any(User.class));
    }

    @Test
    public void testUpdateUser_NotFound() {
        Mockito.when(userRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(99, requestDTO));

        Mockito.verify(userRepo, Mockito.never()).save(Mockito.any());
    }

    // ── deleteUser ─────────────────────────────────────────────

    @Test
    public void testDeleteUser_Success() {
        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepo).deleteById(1);

        Assertions.assertDoesNotThrow(() -> userService.deleteUser(1));

        Mockito.verify(userRepo).findById(1);
        Mockito.verify(physicalGoldTransactionRepo).deleteByUserId(1);
        Mockito.verify(transactionHistoryRepo).deleteByUserId(1);
        Mockito.verify(virtualGoldHoldingRepo).deleteByUserId(1);
        Mockito.verify(paymentRepo).deleteByUserId(1);
        Mockito.verify(userRepo).deleteById(1);
    }

    @Test
    public void testDeleteUser_NotFound() {
        Mockito.when(userRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(99));

        Mockito.verify(physicalGoldTransactionRepo, Mockito.never()).deleteByUserId(Mockito.any());
        Mockito.verify(transactionHistoryRepo, Mockito.never()).deleteByUserId(Mockito.any());
        Mockito.verify(virtualGoldHoldingRepo, Mockito.never()).deleteByUserId(Mockito.any());
        Mockito.verify(paymentRepo, Mockito.never()).deleteByUserId(Mockito.any());
        Mockito.verify(userRepo, Mockito.never()).deleteById(Mockito.any());
    }

    // ── topUpWallet ────────────────────────────────────────────

    @Test
    public void testTopUpWallet_Success() {
        // Mock Security Context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("arjun@test.com");
        SecurityContextHolder.setContext(securityContext);

        user.setBalance(new BigDecimal("1000.00"));

        WalletTopUpRequestDTO topUpRequest = new WalletTopUpRequestDTO();
        topUpRequest.setUserId(1);
        topUpRequest.setAmount(new BigDecimal("500.00"));
        topUpRequest.setPaymentMethod(PaymentMethod.PAYTM);

        Payment savedPayment = new Payment();
        savedPayment.setPaymentId(201);
        savedPayment.setUser(user);
        savedPayment.setAmount(new BigDecimal("500.00"));
        savedPayment.setPaymentMethod(PaymentMethod.PAYTM);
        savedPayment.setTransactionType(TransactionType.CREDITED_TO_WALLET);
        savedPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        savedPayment.setCreatedAt(LocalDateTime.now());

        Mockito.when(userRepo.findByEmail("arjun@test.com")).thenReturn(Optional.of(user));
        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(paymentRepo.save(Mockito.any(Payment.class))).thenReturn(savedPayment);

        PaymentResponseDTO result = userService.topUpWallet(topUpRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(201, result.getPaymentId());
        Assertions.assertEquals(new BigDecimal("500.00"), result.getAmount());
        Mockito.verify(userRepo).save(Mockito.any(User.class));
        Mockito.verify(paymentRepo).save(Mockito.any(Payment.class));
    }

    @Test
    public void testTopUpWallet_ZeroAmount_ThrowsInsufficientBalance() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("arjun@test.com");
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(userRepo.findByEmail("arjun@test.com")).thenReturn(Optional.of(user));
        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));

        WalletTopUpRequestDTO topUpRequest = new WalletTopUpRequestDTO();
        topUpRequest.setUserId(1);
        topUpRequest.setAmount(BigDecimal.ZERO);
        topUpRequest.setPaymentMethod(PaymentMethod.PAYTM);

        Assertions.assertThrows(InsufficientBalanceException.class,
                () -> userService.topUpWallet(topUpRequest));
    }
}
