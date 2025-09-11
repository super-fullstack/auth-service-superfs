package com.elu.authservicesuperfs.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private RoleType role;
    private boolean blocked;
    private boolean loggedIn;
}
