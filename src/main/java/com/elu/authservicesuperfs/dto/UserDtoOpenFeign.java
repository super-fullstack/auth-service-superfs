package com.elu.authservicesuperfs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class UserDtoOpenFeign {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;


}
