package com.example.e_commerce.Model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Merchant {
    @NotEmpty(message = "The id is required")
    @Size(min = 2,message = "The id must be has at least 2 characters")
    private String id;

    @NotEmpty(message = "The name is required")
    @Size(min = 4,max = 20,message = "The name must be has at least 4 characters and no more than 20")
    private String name;
}
