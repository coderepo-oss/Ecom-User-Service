package com.easybuy.UserService.Service;

import com.easybuy.UserService.Exception.UserServiceException;
import com.easybuy.UserService.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService
{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return userRepository.findByEmail(username).orElseThrow(() -> new UserServiceException(
                "USER_NOT_FOUND",
                "User not found with email: " + username
        ));
    }
}
