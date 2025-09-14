package com.elu.authservicesuperfs.controller;


import com.elu.authservicesuperfs.dto.UserRequestDto;
import com.elu.authservicesuperfs.repo.UserRepo;
import com.elu.authservicesuperfs.service.AuthService;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepo userRepo;
    private final AuthService authService;
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    public AuthController(UserRepo userRepo, AuthService authService) {
        this.userRepo = userRepo;
        this.authService = authService;
    }

    @GetMapping("/test")
    public String test() {
        System.out.println(SECRET_KEY);
        var x = userRepo.findAll();
        return "test (this is from the auth service) " + SECRET_KEY + " " + x.toString();
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody UserRequestDto request) {
        System.out.println("INSIDE signup");
        try {
            boolean saveSuccessfull = authService.saveUser(request);
            if (saveSuccessfull) {
                Map<String, String> map = new HashMap<>();
                map.put("status", "success");
                return ResponseEntity.ok(map);
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("status", "fail");
                map.put("message", "Email already exists");
                return ResponseEntity.badRequest().body(map);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto request) {

        try {
            ResponseEntity<?> x = authService
                    .authenticate(
                            request.getEmail(),
                            request.getPassword());
            return x;

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(){
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok( )
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logout successful");
    }

    @GetMapping("/blah")
    public ResponseEntity<String> blah() {
        return ResponseEntity.ok("wow u successfully logged in ");
    }

}
