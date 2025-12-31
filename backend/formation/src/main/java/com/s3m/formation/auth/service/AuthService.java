package com.s3m.formation.auth.service;

import com.s3m.formation.auth.dto.LoginRequest;
import com.s3m.formation.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
