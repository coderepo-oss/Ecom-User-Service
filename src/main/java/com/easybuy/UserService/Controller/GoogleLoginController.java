package com.easybuy.UserService.Controller;

import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Repository.UserRepository;
import com.easybuy.UserService.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/users/oauth")
public class GoogleLoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/success")
    public ResponseEntity<?> handleSuccessfulGoogleLogin(Authentication authentication) {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // Null check for email
        if (email == null) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "Could not retrieve email from Google account"
            ));
        }

        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String gender = oAuth2User.getAttribute("gender");

        UserSignup user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setFirstName(firstName);
                    existingUser.setLastName(lastName);
                    existingUser.setProvider("GOOGLE");
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    UserSignup newUser = UserSignup.builder()
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .gender(gender != null ? gender : "Not provided")
                            .password(null)
                            .phoneNumber(null)
                            .dateOfBirth(null)
                            .provider("GOOGLE")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "message", "login successful"
        ));
    }
}