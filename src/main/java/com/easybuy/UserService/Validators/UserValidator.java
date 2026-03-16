package com.easybuy.UserService.Validators;

import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Exception.UserServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class UserValidator {

    private static final Logger log = LoggerFactory.getLogger(UserValidator.class);

    public static void validate(UserSignup user) {

        if (user == null) {
            throw new UserServiceException("USER_NULL", "User object cannot be null");
        }

        String provider = user.getProvider();
        if (provider == null || provider.trim().isEmpty()) {
            throw new UserServiceException("PROVIDER_REQUIRED", "Provider is required");
        }
        String userId = String.valueOf(user.getId());
        log.info("Validating userId: {} , provider: {}", userId, provider);

        if (blank(user.getEmail())) {
            throw new UserServiceException("EMAIL_REQUIRED", "Email is required");
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new UserServiceException("EMAIL_INVALID", "Invalid email format");
        }

        if ("LOCAL".equalsIgnoreCase(provider)) {

            String password = user.getPassword();
            if (blank(password)) {
                throw new UserServiceException("PASSWORD_REQUIRED", "Password is required");
            }
            if (password.length() < 6) {
                throw new UserServiceException("PASSWORD_WEAK", "Password must be at least 6 characters");
            }
            if (!password.matches(".*[A-Z].*")) {
                throw new UserServiceException("PASSWORD_WEAK", "Must contain uppercase letter");
            }
            if (!password.matches(".*[a-z].*")) {
                throw new UserServiceException("PASSWORD_WEAK", "Must contain lowercase letter");
            }
            if (!password.matches(".*\\d.*")) {
                throw new UserServiceException("PASSWORD_WEAK", "Must contain digit");
            }
            if (!password.matches(".*[!@#$%^&*()].*")) {
                throw new UserServiceException("PASSWORD_WEAK", "Must contain special character");
            }

            if (blank(user.getFirstName())) {
                throw new UserServiceException("FIRSTNAME_REQUIRED", "First name is required");
            }
            if (blank(user.getLastName())) {
                throw new UserServiceException("LASTNAME_REQUIRED", "Last name is required");
            }
            if (user.getPhoneNumber().length()<10 && blank(user.getPhoneNumber()) && !user.getPhoneNumber().matches("^[0-9]{10}$")) {
                throw new UserServiceException("PHONE_INVALID", "Phone must be 10 digits");
            }
            if (!blank(user.getGender()) && !user.getGender().matches("(?i)male|female|other")) {
                throw new UserServiceException("GENDER_INVALID", "Gender must be male, female, or other");
            }

            if (user.getDateOfBirth() != null && user.getDateOfBirth().isAfter(LocalDate.now())) {
                throw new UserServiceException("DOB_INVALID", "Date of birth cannot be in the future");
            }
        }
        log.info("UserId {} validation completed successfully", userId);
    }

    private static boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
