package org.fishking0721.oss.auth;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class PermissionChecker {
    public boolean check(Authentication authentication, String requiredRole) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + requiredRole));
    }
}