package com.tiffino.tiffino.entity.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String email;
    private String token;
    private String role;

    @JsonIgnore
    private String managerId;
}
