package com.easybuy.UserService.Controller;

import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Service.UserService;
import com.easybuy.UserService.Exception.UserServiceException;
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
    public ResponseEntity<UserSignup> viewProfile(
            @PathVariable("id") Long id,
            @RequestHeader("X-Username") String currentUser) {

        log.info("View profile request for id: {} by user: {}", id, currentUser);

        UserSignup userProfile = userService.viewProfile(id);

        if (!userProfile.getEmail().equals(currentUser)) {
            log.warn("Forbidden access - user: {} tried to view profile of id: {}", currentUser, id);
            throw new UserServiceException("FORBIDDEN", "You are not allowed to access this profile");
        }

        log.info("Profile fetched successfully for id: {}", id);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/updateprofile/{id}")
    public ResponseEntity<UserSignup> updateProfile(
            @PathVariable("id") Long id,
            @RequestBody UserSignup updatedUser,
            @RequestHeader("X-Username") String currentUser) {

        log.info("Update profile request for id: {} by user: {}", id, currentUser);

        UserSignup existingUser = userService.viewProfile(id);

        if (!existingUser.getEmail().equals(currentUser)) {
            log.warn("Forbidden access - user: {} tried to update profile of id: {}", currentUser, id);
            throw new UserServiceException("FORBIDDEN", "You are not allowed to update this profile");
        }

        UserSignup updatedProfile = userService.updateProfile(id, updatedUser);
        log.info("Profile updated successfully for id: {}", id);
        return ResponseEntity.ok(updatedProfile);
    }
}