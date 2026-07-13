package com.s3m.formation.auth.service;


import com.s3m.formation.auth.model.User;
import com.s3m.formation.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByEmail(String email) {


        String cleanedEmail = email.trim().toLowerCase();


        return userRepository.findByEmail(cleanedEmail)
                .orElse(null);
    }
    @Override
    public User findByEmailOrUsername(String identifier) {
        return userRepository.findByEmailOrUsername(identifier.trim().toLowerCase())
                .orElse(null);
    }
}
