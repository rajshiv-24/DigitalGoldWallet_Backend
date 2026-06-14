package com.cg.controller;

import com.cg.dto.PaymentResponseDTO;
import com.cg.dto.UserRequestDTO;
import com.cg.dto.UserResponseDTO;
import com.cg.dto.WalletTopUpRequestDTO;
import com.cg.repo.UserRepository;
import com.cg.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // ADMIN ONLY — list all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // ADMIN or OWN USER — get user by id
    @GetMapping("/{userId}")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public UserResponseDTO getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    // ADMIN or OWN USER — update user
    @PutMapping("/{userId}")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public UserResponseDTO updateUser(@PathVariable Integer userId, @RequestBody UserRequestDTO request) {
        return userService.updateUser(userId, request);
    }

    // ADMIN ONLY — delete user
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // OWN USER ONLY — top up wallet
    @PostMapping("/wallet/top-up")
    @PreAuthorize("@userAccessService.canAccessOwnUser(authentication, #request.userId)")
    public ResponseEntity<PaymentResponseDTO> topUpWallet(@RequestBody WalletTopUpRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.topUpWallet(request));
    }

    // ADMIN or OWN USER — get balance
    @GetMapping("/{userId}/balance")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public Map<String, Object> getWalletBalance(@PathVariable Integer userId) {
        UserResponseDTO user = userService.getUserById(userId);
        return Map.of("userId", user.getUserId(), "balance", user.getBalance());
    }

    // ADMIN ONLY — count users
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> countUsers() {
        return Map.of("count", userRepository.count());
    }

    // ADMIN ONLY — check existence
    @GetMapping("/{userId}/exists")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Boolean> userExists(@PathVariable Integer userId) {
        return Map.of("exists", userRepository.existsById(userId));
    }

    // ADMIN ONLY — lookup by email (returns DTO, NOT raw entity)
    @GetMapping("/by-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    UserResponseDTO dto = userService.getUserById(user.getUserId());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
