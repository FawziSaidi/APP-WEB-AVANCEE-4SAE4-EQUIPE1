package com.esprit.userservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String role;
}
