package com.elu.authservicesuperfs.service;

import com.elu.authservicesuperfs.dto.UserRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    public boolean saveUser(UserRequestDto user);


    ResponseEntity<?> authenticate(String email, String password);
}
