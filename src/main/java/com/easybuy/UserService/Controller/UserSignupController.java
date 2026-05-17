package com.easybuy.UserService.Controller;

import com.easybuy.UserService.DTO.ForgotPasswordRequest;
import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserSignupController {

    private final UserService userService;

    public UserSignupController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserSignup> signup(@RequestBody UserSignup user) {
        log.info("Signup request received");
        UserSignup savedUser = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        log.info("Forgot password request received");
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Temporary password sent to your email");
    }
}