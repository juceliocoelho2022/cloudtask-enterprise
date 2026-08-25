package br.com.cloudtask.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String name,
        String email
) {}
