package com.example.e_commerce.Model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    @NotEmpty(message = "The id is required")
    @Size(min = 4,max = 10,message = "The id must has at least 4 characters and no more 10 characters")
    private String id;

    @NotEmpty(message = "The name is required")
    @Size(min = 4,max = 20,message = "The name must has at least 4 characters and no more 20")
    private String name;

    @NotNull(message = "The price is required")
    @Positive(message = "The price must be positive number")
    private Double price;

    @NotEmpty(message = "The category id is required")
    @Size(min = 2,message = "the category id must has at least 2 characters")
    private String categoryId;
}
