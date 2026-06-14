package com.cg.security;

import com.cg.enums.Role;
import com.cg.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserAccessService {

    private final UserRepository userRepository;

    public UserAccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean canAccessUser(Authentication authentication, Integer userId) {
        if (authentication == null || !authentication.isAuthenticated() || userId == null) {
            return false;
        }

        if (hasAdminRole(authentication)) {
            return true;
        }

        return userRepository.findByEmail(authentication.getName())
                .map(user -> user.getUserId().equals(userId))
                .orElse(false);
    }

    public boolean canAccessOwnUser(Authentication authentication, Integer userId) {
        if (authentication == null || !authentication.isAuthenticated() || userId == null) {
            return false;
        }

        return userRepository.findByEmail(authentication.getName())
                .map(user -> user.getUserId().equals(userId))
                .orElse(false);
    }

    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + Role.ADMIN.name()));
    }
}
