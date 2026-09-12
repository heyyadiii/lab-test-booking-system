package com.labtest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.labtest.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Never serialize password in response
    private String password;
    
    // Role is optional - will default to PATIENT
    private UserRole role;
    
    private String fullName;
    
    @Email(message = "Invalid email format")
    private String email;
}
