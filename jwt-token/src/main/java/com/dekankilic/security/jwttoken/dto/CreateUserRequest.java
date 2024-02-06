package com.dekankilic.security.jwttoken.dto;


import com.dekankilic.security.jwttoken.model.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record CreateUserRequest(
        String name,
        String username,
        String password,
        Set<Role> authorities
) {
}
