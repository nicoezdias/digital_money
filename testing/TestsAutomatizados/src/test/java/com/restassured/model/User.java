package com.restassured.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class User {
    private String name;
    private String last_name;
    private Integer dni;
    private String email;
    private String password;
    private Integer phone;
}


