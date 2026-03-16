package com.easybuy.UserService.Service;

import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Exception.UserServiceException;
import com.easybuy.UserService.Repository.UserRepository;
import com.easybuy.UserService.Validators.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserSignup register(UserSignup user) {

        log.info("Registering new User. Temp userId: {}", user.getId());
        user.setProvider("LOCAL");
        UserValidator.validate(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            log.error("Signup failed: EMAIL_ALREADY_EXISTS");
            throw new UserServiceException("EMAIL_ALREADY_EXISTS", "Email already exists");
        }

        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            log.error("Signup failed: PHONENUMBER_ALREADY_EXIST");
            throw new UserServiceException("PHONENUMBER_ALREADY_EXIST", "Phone number already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserSignup savedUser = userRepository.save(user);
        log.info("User created successfully with userId {}", savedUser.getId());

        return savedUser;
    }

    public UserSignup viewProfile(Long id) {
        log.info("Fetching profile for userId: {}", id);

        UserSignup user= userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException(
                        "USER_NOT_FOUND",
                        "User not found"
                ));
     return user;
    }

    public UserSignup updateProfile(Long id, UserSignup user)
    {
        log.info("Updating profile for userId: {}", id);

        UserSignup existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException(
                        "USER_NOT_FOUND",
                        "User not found"
                ));
        if(user.getEmail().equals(existingUser.getEmail())) {
            log.error("Update failed: EMAIL_ALREADY_EXISTS");
            throw new UserServiceException("EMAIL_ALREADY_EXISTS", "Email already exists");
        }
        else
        {
            existingUser.setEmail(user.getEmail());
            log.info("Email updated for userId: {}", id);
        }
        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        if (user.getPhoneNumber() != null) {
            if (!user.getPhoneNumber().equals(existingUser.getPhoneNumber()) &&
                    userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
                log.error("Update failed: PHONENUMBER_ALREADY_EXIST");
                throw new UserServiceException("PHONENUMBER_ALREADY_EXIST", "Phone number already exists");
            }
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }
        UserSignup updatedUser = userRepository.save(existingUser);
        log.info("Profile updated successfully for userId: {}", id);
        return updatedUser;
    }

    public UserSignup getUserById(Long userId)
    {
        log.info("Fetching user by id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserServiceException(
                        "USER_NOT_FOUND",
                        "User not found"
                ));
    }
}
