package com.elu.authservicesuperfs.service;

import com.elu.authservicesuperfs.dto.UserDtoOpenFeign;
import com.elu.authservicesuperfs.model.Users;
import com.elu.authservicesuperfs.repo.UserRepo;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean updateUser(UserDtoOpenFeign request) throws Exception {
        try {
            Users exisitingUser = userRepo.findByEmail(request.getEmail())
                    .orElse(null);
            if (exisitingUser != null) {
                exisitingUser.setUsername(request.getUsername());
                exisitingUser.setFirstName(request.getFirstName());
                exisitingUser.setLastName(request.getLastName());
                exisitingUser.setAddress(request.getAddress());
                exisitingUser.setPhone(request.getPhone());
                userRepo.save(exisitingUser);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
