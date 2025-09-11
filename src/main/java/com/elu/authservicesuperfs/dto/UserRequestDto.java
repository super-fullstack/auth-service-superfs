package com.elu.authservicesuperfs.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class UserRequestDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
}
