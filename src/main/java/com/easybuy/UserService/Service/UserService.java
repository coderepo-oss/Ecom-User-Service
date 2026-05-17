package com.easybuy.UserService.Service;

import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Exception.UserServiceException;
import com.easybuy.UserService.Repository.UserRepository;
import com.easybuy.UserService.Validators.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;


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
        return userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("USER_NOT_FOUND", "User not found"));
    }

    public UserSignup updateProfile(Long id, UserSignup user) {
        log.info("Updating profile for userId: {}", id);

        UserSignup existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("USER_NOT_FOUND", "User not found"));

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                log.error("Update failed: EMAIL_ALREADY_EXISTS");
                throw new UserServiceException("EMAIL_ALREADY_EXISTS", "Email already exists");
            }
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

    public void forgotPassword(String email) {
        log.info("Forgot password request for email: {}", email);

        UserSignup user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceException("USER_NOT_FOUND", "No account found with this email"));

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("EasyBuy - Password Reset");
        message.setText("Your temporary password is: " + tempPassword +
                "\nPlease log in and change your password immediately.");
        mailSender.send(message);

        log.info("Temporary password sent successfully");
    }
    public void deleteAccount(Long id) {
        log.info("Delete account request for userId: {}", id);

        UserSignup user = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("USER_NOT_FOUND", "User not found"));

        userRepository.delete(user);
        log.info("Account deleted successfully for userId: {}", id);
    }
    public UserSignup getUserById(Long userId) {
        log.info("Fetching user by id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserServiceException("USER_NOT_FOUND", "User not found"));
    }
}