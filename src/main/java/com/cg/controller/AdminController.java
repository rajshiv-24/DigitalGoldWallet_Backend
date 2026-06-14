package com.cg.controller;

import com.cg.dto.PaymentResponseDTO;
import com.cg.dto.RoleUpdateRequestDTO;
import com.cg.dto.TransactionHistoryResponseDTO;
import com.cg.dto.UserResponseDTO;
import com.cg.entity.User;
import com.cg.entity.VendorBranch;
import com.cg.entity.Vendors;
import com.cg.enums.Role;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.UserRepository;
import com.cg.service.GoldTransactionService;
import com.cg.service.PaymentService;
import com.cg.service.UserService;
import com.cg.service.VendorBranchService;
import com.cg.service.VendorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final GoldTransactionService goldTransactionService;
    private final VendorService vendorService;
    private final VendorBranchService vendorBranchService;

    public AdminController(UserService userService,
                           UserRepository userRepository,
                           PaymentService paymentService,
                           GoldTransactionService goldTransactionService,
                           VendorService vendorService,
                           VendorBranchService vendorBranchService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.goldTransactionService = goldTransactionService;
        this.vendorService = vendorService;
        this.vendorBranchService = vendorBranchService;
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/users/{userId}/role")
    public UserResponseDTO updateUserRole(@PathVariable Integer userId, @RequestBody RoleUpdateRequestDTO request) {
        if (request.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setRole(request.getRole());
        User saved = userRepository.save(user);

        return toUserResponseDTO(saved);
    }

    @GetMapping("/users/count")
    public Map<String, Long> countUsers() {
        return Map.of("count", userRepository.count());
    }

    @GetMapping("/users/by-role/{role}")
    public List<UserResponseDTO> getUsersByRole(@PathVariable Role role) {
        return userRepository.findAll().stream()
                .filter(user -> (user.getRole() != null ? user.getRole() : Role.USER) == role)
                .map(this::toUserResponseDTO)
                .toList();
    }

    @GetMapping("/payments")
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/transactions")
    public List<TransactionHistoryResponseDTO> getAllTransactionHistory() {
        return goldTransactionService.getAllTransactionHistory();
    }

    @GetMapping("/vendors")
    public List<Vendors> getAllVendors() {
        return vendorService.getAllVendors();
    }

    @GetMapping("/branches")
    public List<VendorBranch> getAllBranches() {
        return vendorBranchService.getAllBranches();
    }

    private UserResponseDTO toUserResponseDTO(User user) {
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
}
