package com.example.e_commerce.Model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Category {
    @NotEmpty(message = "The category id is required")
    @Size(min = 2,message = "the category id must has at least 2 characters")
    private String id;

    @NotEmpty(message = "The category name is required")
    @Size(min = 4,max = 15,message = "The name must be has at least 4 characters and no more than 15")
    private String name;
}
