package com.elu.authservicesuperfs.controller;


import com.elu.authservicesuperfs.dto.UserDtoOpenFeign;
import com.elu.authservicesuperfs.model.Users;
import com.elu.authservicesuperfs.repo.UserRepo;
import com.elu.authservicesuperfs.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/open-feign")
public class UserController {


    private final UserRepo userRepo;
    private UserService userService;

    public UserController(UserRepo userRepo, UserService userService) {
        this.userRepo = userRepo;
        this.userService = userService;
    }

    @GetMapping("/get-users")
    public List<UserDtoOpenFeign> getUsers() {
        System.out.println("inside getUsers");
        List<Users> users = userRepo.findAll();
        var x = users.stream()
                .map(user -> new UserDtoOpenFeign(
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getAddress()
                ))
                .toList();
        System.out.println("this is the response " + x.toString());
        return x;
    }

    @PostMapping("/update-user")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody UserDtoOpenFeign request) {
        try {
            userService.updateUser(request);
            return ResponseEntity.ok().body(Map.of("message", "success"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "error"));
        }
    }
}
