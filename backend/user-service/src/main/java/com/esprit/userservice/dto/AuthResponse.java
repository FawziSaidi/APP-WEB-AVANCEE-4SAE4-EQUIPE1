package com.esprit.userservice.dto;

import com.esprit.userservice.entities.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {

    private String token;

    private Role role;

    @JsonProperty("userId")
    private Integer userId;

    // ✅ FIX : ajout du refresh token et de l'expiration
    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("expiresIn")
    private Long expiresIn;  // en secondes (ex: 300 = 5 minutes)

    public AuthResponse(String token, Role role, Integer userId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
    }

    public AuthResponse(String token, Role role, Integer userId, String refreshToken, Long expiresIn) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }
}