package com.elu.authservicesuperfs.service;


import com.elu.authservicesuperfs.model.Users;
import com.elu.authservicesuperfs.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public AppUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public UserDetails
        loadUserByUsername(String username)
            throws UsernameNotFoundException {
        System.out.println("INSIDE loadUserByUsername");
        System.out.println("INCOMOING DATA " + username);
        Users existingUser = userRepo.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username));

        System.out.println("user found is " + existingUser.getEmail());

        GrantedAuthority grantedAuthority
                = new SimpleGrantedAuthority(existingUser.getRole().name());

        return new User(
                existingUser.getEmail(),
                existingUser.getPassword(),
                List.of(grantedAuthority)
        );
    }
}
