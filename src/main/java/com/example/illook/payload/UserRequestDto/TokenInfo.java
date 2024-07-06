package com.example.illook.payload.UserRequestDto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenInfo {
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpirationTime;
}
