package com.easybuy.UserService.Controller;

import com.easybuy.UserService.Entity.UserLogin;
import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Service.LoginService;
import com.easybuy.UserService.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> generatetoken(@RequestBody UserLogin loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserSignup user = (UserSignup) loginService.loadUserByUsername(loginRequest.getEmail());

            String token = jwtUtil.generateToken(loginRequest.getEmail(), user.getId());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "userId", user.getId()
            ));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Invalid email/ password"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal server error"
            ));
        }
    }
}