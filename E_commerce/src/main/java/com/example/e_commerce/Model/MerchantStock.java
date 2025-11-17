package com.example.e_commerce.Model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantStock {
    @NotEmpty(message = "The id is required")
    private String id;

    @NotEmpty(message = "The product id is required")
    @Size(min = 4,max = 10,message = "The product id must has at least 4 characters and no more 10 characters")
    private String productId;

    @NotEmpty(message = "The merchant id is required")
    @Size(min = 2,message = "The merchant id must be has at least 2 characters")
    private String merchantId;

    @NotNull(message = "The stock is required")
    private Integer stock;
}
