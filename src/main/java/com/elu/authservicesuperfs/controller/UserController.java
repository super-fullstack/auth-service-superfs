package com.elu.authservicesuperfs.controller;


import com.elu.authservicesuperfs.dto.UserDtoOpenFeign;
import com.elu.authservicesuperfs.model.Users;
import com.elu.authservicesuperfs.repo.UserRepo;
import com.elu.authservicesuperfs.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get-user")
    public ResponseEntity<UserDtoOpenFeign> getUser(@RequestParam("email") String email) {

        try {
            System.out.println("inside getUser");
            Users user = userRepo.findByEmail(email)
                    .orElseThrow(RuntimeException::new);
            UserDtoOpenFeign userDtoOpenFeign = new UserDtoOpenFeign();
            userDtoOpenFeign.setUsername(user.getUsername());
            userDtoOpenFeign.setFirstName(user.getFirstName());
            userDtoOpenFeign.setLastName(user.getLastName());
            userDtoOpenFeign.setEmail(email);
            userDtoOpenFeign.setPhone(user.getPhone());
            userDtoOpenFeign.setAddress(user.getAddress());
            return new ResponseEntity<>(userDtoOpenFeign, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
