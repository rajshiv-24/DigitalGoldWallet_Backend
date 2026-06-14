package com.cg.test.controller;

import com.cg.controller.UserController;
import com.cg.dto.*;
import com.cg.entity.User;
import com.cg.enums.PaymentMethod;
import com.cg.enums.Role;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.UserRepository;
import com.cg.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private UserResponseDTO userResponse;
    private UserRequestDTO userRequest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        userResponse = new UserResponseDTO();
        userResponse.setUserId(1);
        userResponse.setName("Kavya");
        userResponse.setEmail("kavya@test.com");
        userResponse.setRole(Role.USER);
        userResponse.setBalance(new BigDecimal("2000"));
        userResponse.setCreatedAt(LocalDateTime.now());

        userRequest = new UserRequestDTO(
                "Kavya",
                "kavya@test.com",
                "pass123",
                5
        );
    }

    // ✅ GET ALL
    @Test
    void testGetAllUsers() {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(userResponse));

        List<UserResponseDTO> result = userController.getAllUsers();

        Assertions.assertEquals(1, result.size());
    }

    // ✅ GET BY ID
    @Test
    void testGetUserById() {
        Mockito.when(userService.getUserById(1)).thenReturn(userResponse);

        UserResponseDTO result = userController.getUserById(1);

        Assertions.assertEquals(1, result.getUserId());
    }

    @Test
    void testGetUserById_NotFound() {
        Mockito.when(userService.getUserById(99))
                .thenThrow(new ResourceNotFoundException("Not found"));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> userController.getUserById(99));
    }

    // ✅ UPDATE
    @Test
    void testUpdateUser() {
        Mockito.when(userService.updateUser(Mockito.eq(1), Mockito.any()))
                .thenReturn(userResponse);

        UserResponseDTO result = userController.updateUser(1, userRequest);

        Assertions.assertEquals("Kavya", result.getName());
    }

    // ✅ DELETE
    @Test
    void testDeleteUser() {
        Mockito.doNothing().when(userService).deleteUser(1);

        ResponseEntity<Void> response = userController.deleteUser(1);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // ✅ TOP UP
    @Test
    void testTopUpWallet() {
        WalletTopUpRequestDTO dto = new WalletTopUpRequestDTO();
        dto.setUserId(1);
        dto.setAmount(new BigDecimal("1000"));
        dto.setPaymentMethod(PaymentMethod.PAYTM);

        PaymentResponseDTO payment = new PaymentResponseDTO();
        payment.setPaymentId(100);

        Mockito.when(userService.topUpWallet(Mockito.any())).thenReturn(payment);

        ResponseEntity<PaymentResponseDTO> response = userController.topUpWallet(dto);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // ✅ BALANCE
    @Test
    void testGetWalletBalance() {
        Mockito.when(userService.getUserById(1)).thenReturn(userResponse);

        Map<String, Object> result = userController.getWalletBalance(1);

        Assertions.assertEquals(1, result.get("userId"));
    }

    // ✅ COUNT
    @Test
    void testCountUsers() {
        Mockito.when(userRepository.count()).thenReturn(5L);

        Map<String, Long> result = userController.countUsers();

        Assertions.assertEquals(5L, result.get("count"));
    }

    // ✅ EXISTS
    @Test
    void testUserExists() {
        Mockito.when(userRepository.existsById(1)).thenReturn(true);

        Map<String, Boolean> result = userController.userExists(1);

        Assertions.assertTrue(result.get("exists"));
    }

    // ✅ BY EMAIL
    @Test
    void testGetUserByEmail() {
        User user = new User();
        user.setEmail("kavya@test.com");

        Mockito.when(userRepository.findByEmail("kavya@test.com"))
                .thenReturn(java.util.Optional.of(user));

        ResponseEntity<User> response = userController.getUserByEmail("kavya@test.com");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}