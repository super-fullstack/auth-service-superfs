package com.elu.authservicesuperfs.service;


import com.elu.authservicesuperfs.dto.UserRequestDto;
import com.elu.authservicesuperfs.model.RoleType;
import com.elu.authservicesuperfs.model.Users;
import com.elu.authservicesuperfs.repo.UserRepo;
import com.elu.authservicesuperfs.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AppUserDetailsService appUserDetailsService;
    private UserRepo userRepo;
    private AuthenticationManager authenticationManager;

    @Override
    public boolean saveUser(UserRequestDto request) {
        try {
            Optional<Users> existingUser = userRepo.findByEmail(request.getEmail());

            if (existingUser.isPresent()) {
                // Email already exists, cannot create new user
                return false;
            }
            Users newUser = Users.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phone(null)
                    .address(null)
                    .role(RoleType.ROLE_USER)
                    .blocked(false)
                    .loggedIn(false)
                    .build();
            userRepo.save(newUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResponseEntity<?> authenticate(String email, String password) {
        try {

            System.out.println("INCOMIGN DATA " + email + " " + password);
            authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email, password)
                    );

            final UserDetails userDetails = appUserDetailsService
                    .loadUserByUsername(email);

            final String jwtToken = jwtUtil.generateToken(userDetails);

            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            Users user = userRepo
                    .findByEmail(userDetails.getUsername())
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found"));

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken) // optional
                    .body("successfully logged in");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("invalid username or password");
        }
    }
}
