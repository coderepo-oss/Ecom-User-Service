package com.easybuy.UserService.Controller;

import com.easybuy.UserService.Annotation.AuthorizeUser;
import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/viewprofile/{id}")
    @AuthorizeUser(pathVariable = "id")
    public ResponseEntity<UserSignup> viewProfile(
            @PathVariable("id") Long id) {

        log.info("View profile request for id: {}", id);

        UserSignup userProfile = userService.viewProfile(id);

        log.info("Profile fetched successfully for id: {}", id);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/updateprofile/{id}")
    @AuthorizeUser(pathVariable = "id")
    public ResponseEntity<UserSignup> updateProfile(
            @PathVariable("id") Long id,
            @RequestBody UserSignup updatedUser) {

        log.info("Update profile request for id: {}", id);

        UserSignup updatedProfile = userService.updateProfile(id, updatedUser);
        log.info("Profile updated successfully for id: {}", id);
        return ResponseEntity.ok(updatedProfile);
    }
}