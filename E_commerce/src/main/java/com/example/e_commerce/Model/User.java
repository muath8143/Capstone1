package com.example.e_commerce.Model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    @NotEmpty(message = "The id is required")
    @Size(min = 4,max = 10,message = "The id must has at least 4 characters and no more 10 characters")
    private String id;

    @NotEmpty(message = "The username is required")
    @Size(min = 6,max = 20,message = "The username must be has at least 6 characters and no more than 20")
    private String userName;

    @NotEmpty(message = "The password is required")
    @Size(min = 7,message = "The password must be has at least 7 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
            message = "Password must contain lowercase, uppercase, number, and symbol")
    private String password;

    @NotEmpty(message = "The email is required")
    @Email(message = "Please enter a valid email")
    private String email;

    @NotEmpty(message = "The role is required")
    @Pattern(regexp = "Admin|Customer",message = "The role must be one of these Admin or Customer")
    private String role;

    @NotNull(message = "The balance is required")
    @Positive(message = "The balance must be positive")
    private Double balance;
}
