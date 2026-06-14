package com.cg.test.controller;

import com.cg.controller.AuthController;
import com.cg.dto.AuthResponseDTO;
import com.cg.dto.LoginRequestDTO;
import com.cg.dto.UserRequestDTO;
import com.cg.dto.UserResponseDTO;
import com.cg.entity.User;
import com.cg.enums.Role;
import com.cg.exception.DuplicateEmailException;
import com.cg.repo.UserRepository;
import com.cg.security.JwtService;
import com.cg.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class AuthControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private AuthController authController;

    private User user;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setUserId(1);
        user.setName("Ravi Kumar");
        user.setEmail("ravi@test.com");
        user.setPassword("$2a$10$encodedHashedPassword");
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.ZERO);
        user.setCreatedAt(LocalDateTime.now());

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUserId(1);
        userResponseDTO.setName("Ravi Kumar");
        userResponseDTO.setEmail("ravi@test.com");
        userResponseDTO.setRole(Role.USER);
        userResponseDTO.setBalance(BigDecimal.ZERO);
        userResponseDTO.setCreatedAt(LocalDateTime.now());
    }

    // ── register ───────────────────────────────────────────────

    @Test
    public void testRegister_Success() {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Ravi Kumar");
        request.setEmail("ravi@test.com");
        request.setPassword("password123");
        request.setAddressId(5);

        Mockito.when(userService.createUser(Mockito.any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        UserResponseDTO result = authController.register(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Ravi Kumar", result.getName());
        Assertions.assertEquals("ravi@test.com", result.getEmail());
        Mockito.verify(userService).createUser(Mockito.any(UserRequestDTO.class));
    }

    @Test
    public void testRegister_BlankPassword_ThrowsBadRequest() {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Ravi Kumar");
        request.setEmail("ravi@test.com");
        request.setPassword("");
        request.setAddressId(5);

        Assertions.assertThrows(ResponseStatusException.class,
                () -> authController.register(request));

        Mockito.verify(userService, Mockito.never()).createUser(Mockito.any());
    }

    @Test
    public void testRegister_NullPassword_ThrowsBadRequest() {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Ravi Kumar");
        request.setEmail("ravi@test.com");
        request.setPassword(null);

        Assertions.assertThrows(ResponseStatusException.class,
                () -> authController.register(request));
    }

    @Test
    public void testRegister_DuplicateEmail_ThrowsException() {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Ravi Kumar");
        request.setEmail("ravi@test.com");
        request.setPassword("password123");
        request.setAddressId(5);

        Mockito.when(userService.createUser(Mockito.any(UserRequestDTO.class)))
               .thenThrow(new DuplicateEmailException("Email already registered: ravi@test.com"));

        Assertions.assertThrows(DuplicateEmailException.class,
                () -> authController.register(request));
    }

    // ── login ──────────────────────────────────────────────────

    @Test
    public void testLogin_Success() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("ravi@test.com");
        loginRequest.setPassword("password123");

        Mockito.when(userRepository.findByEmail("ravi@test.com")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        Mockito.when(jwtService.generateToken(Mockito.eq("ravi@test.com"), Mockito.anyList()))
               .thenReturn("jwt-token-string");

        AuthResponseDTO result = authController.login(loginRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ravi@test.com", result.getEmail()); // ✅ FIXED
        Assertions.assertEquals("Ravi Kumar", result.getName());     // optional extra check

        Mockito.verify(userRepository).findByEmail("ravi@test.com");
        Mockito.verify(jwtService).generateToken(Mockito.eq("ravi@test.com"), Mockito.anyList());
    }

    @Test
    public void testLogin_InvalidEmail_ThrowsUnauthorized() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("nobody@test.com");
        loginRequest.setPassword("password123");

        Mockito.when(userRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class,
                () -> authController.login(loginRequest));

        Mockito.verify(userRepository).findByEmail("nobody@test.com");
        Mockito.verify(jwtService, Mockito.never()).generateToken(Mockito.any(), Mockito.any());
    }

    @Test
    public void testLogin_WrongPassword_ThrowsUnauthorized() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("ravi@test.com");
        loginRequest.setPassword("wrongPassword");

        Mockito.when(userRepository.findByEmail("ravi@test.com")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        Assertions.assertThrows(ResponseStatusException.class,
                () -> authController.login(loginRequest));

        Mockito.verify(jwtService, Mockito.never()).generateToken(Mockito.any(), Mockito.any());
    }

    @Test
    public void testLogin_NullPassword_InUser_ThrowsUnauthorized() {
        user.setPassword(null);
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("ravi@test.com");
        loginRequest.setPassword("password123");

        Mockito.when(userRepository.findByEmail("ravi@test.com")).thenReturn(Optional.of(user));

        Assertions.assertThrows(ResponseStatusException.class,
                () -> authController.login(loginRequest));
    }
}