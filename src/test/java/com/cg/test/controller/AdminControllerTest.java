package com.cg.test.controller;



import com.cg.controller.AdminController;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class AdminControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private GoldTransactionService goldTransactionService;

    @MockitoBean
    private VendorService vendorService;

    @MockitoBean
    private VendorBranchService vendorBranchService;

    @Autowired
    private AdminController adminController;

    private User user;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setUserId(1);
        user.setName("Admin User");
        user.setEmail("admin@gold.com");
        user.setRole(Role.USER);
        user.setBalance(new BigDecimal("50000.00"));
        user.setCreatedAt(LocalDateTime.now());

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUserId(1);
        userResponseDTO.setName("Admin User");
        userResponseDTO.setEmail("admin@gold.com");
        userResponseDTO.setRole(Role.USER);
        userResponseDTO.setBalance(new BigDecimal("50000.00"));
        userResponseDTO.setCreatedAt(LocalDateTime.now());
    }

    // ── getAllUsers ────────────────────────────────────────────

    @Test
    public void testGetAllUsers_ReturnsList() {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(userResponseDTO));

        List<UserResponseDTO> result = adminController.getAllUsers();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Admin User", result.get(0).getName());
        Mockito.verify(userService).getAllUsers();
    }

    @Test
    public void testGetAllUsers_EmptyList() {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of());

        List<UserResponseDTO> result = adminController.getAllUsers();

        Assertions.assertTrue(result.isEmpty());
    }

    // ── updateUserRole ─────────────────────────────────────────

    @Test
    public void testUpdateUserRole_Success() {
        user.setRole(Role.ADMIN);
        RoleUpdateRequestDTO roleRequest = new RoleUpdateRequestDTO();
        roleRequest.setRole(Role.ADMIN);

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        UserResponseDTO result = adminController.updateUserRole(1, roleRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Role.ADMIN, result.getRole());
        Mockito.verify(userRepository).findById(1);
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    public void testUpdateUserRole_NullRole_ThrowsBadRequest() {
        RoleUpdateRequestDTO roleRequest = new RoleUpdateRequestDTO();
        roleRequest.setRole(null);

        Assertions.assertThrows(ResponseStatusException.class,
                () -> adminController.updateUserRole(1, roleRequest));

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testUpdateUserRole_UserNotFound() {
        RoleUpdateRequestDTO roleRequest = new RoleUpdateRequestDTO();
        roleRequest.setRole(Role.ADMIN);

        Mockito.when(userRepository.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> adminController.updateUserRole(99, roleRequest));
    }

    // ── countUsers ─────────────────────────────────────────────

    @Test
    public void testCountUsers() {
        Mockito.when(userRepository.count()).thenReturn(20L);

        Map<String, Long> result = adminController.countUsers();

        Assertions.assertEquals(20L, result.get("count"));
        Mockito.verify(userRepository).count();
    }

    // ── getUsersByRole ─────────────────────────────────────────

    @Test
    public void testGetUsersByRole_ReturnsFilteredList() {
        user.setRole(Role.ADMIN);
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> result = adminController.getUsersByRole(Role.ADMIN);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(Role.ADMIN, result.get(0).getRole());
    }

    @Test
    public void testGetUsersByRole_NoMatch() {
        user.setRole(Role.USER);
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> result = adminController.getUsersByRole(Role.ADMIN);

        Assertions.assertTrue(result.isEmpty());
    }

    // ── getAllPayments ─────────────────────────────────────────

    @Test
    public void testGetAllPayments_ReturnsList() {
        PaymentResponseDTO paymentDTO = new PaymentResponseDTO();
        paymentDTO.setPaymentId(500);
        Mockito.when(paymentService.getAllPayments()).thenReturn(List.of(paymentDTO));

        List<PaymentResponseDTO> result = adminController.getAllPayments();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(500, result.get(0).getPaymentId());
        Mockito.verify(paymentService).getAllPayments();
    }

    // ── getAllTransactionHistory ────────────────────────────────

    @Test
    public void testGetAllTransactionHistory_ReturnsList() {
        TransactionHistoryResponseDTO histDTO = new TransactionHistoryResponseDTO();
        histDTO.setTransactionId(700);
        Mockito.when(goldTransactionService.getAllTransactionHistory()).thenReturn(List.of(histDTO));

        List<TransactionHistoryResponseDTO> result = adminController.getAllTransactionHistory();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(700, result.get(0).getTransactionId());
        Mockito.verify(goldTransactionService).getAllTransactionHistory();
    }

    // ── getAllVendors ──────────────────────────────────────────

    @Test
    public void testGetAllVendors_ReturnsList() {
        Vendors vendor = new Vendors();
        vendor.setVendorId(1L);
        Mockito.when(vendorService.getAllVendors()).thenReturn(List.of(vendor));

        List<Vendors> result = adminController.getAllVendors();

        Assertions.assertEquals(1, result.size());
        Mockito.verify(vendorService).getAllVendors();
    }

    // ── getAllBranches ─────────────────────────────────────────

    @Test
    public void testGetAllBranches_ReturnsList() {
        VendorBranch branch = new VendorBranch();
        branch.setBranchId(10);
        Mockito.when(vendorBranchService.getAllBranches()).thenReturn(List.of(branch));

        List<VendorBranch> result = adminController.getAllBranches();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(10, result.get(0).getBranchId());
        Mockito.verify(vendorBranchService).getAllBranches();
    }
}
