package com.outgo.api.web.auth.dto;

import java.util.UUID;

public record AuthResponse(String token, UUID userId) {
}
