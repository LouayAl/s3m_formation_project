package com.s3m.formation.auth.service;

import com.s3m.formation.auth.model.User;

public interface UserService {
    User findByEmail(String email);
    User findByEmailOrUsername(String identifier);
}
