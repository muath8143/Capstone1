package com.example.e_commerce.Controller;

import com.example.e_commerce.Api.ApiResponse;
import com.example.e_commerce.Model.Product;
import com.example.e_commerce.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllProducts(){
        ArrayList<Product> products=productService.getAllProducts();
        if (products.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("No products in system"));
        }
        return ResponseEntity.status(200).body(products);
    }

    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(@RequestBody @Valid Product product, Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        boolean flag= productService.createProduct(product);
        if (flag){
            return ResponseEntity.status(200).body(new ApiResponse("The product added successfully"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("The id of category: "+product.getCategoryId()+" is not exits please enter correct id"));
    }

    @PutMapping("/update-product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody @Valid Product product,Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        int flag=productService.updateProduct(id,product);
        switch (flag){
            case 400: // found id but not found category id
                return ResponseEntity.status(400).body(new ApiResponse("The id of category: "+product.getCategoryId()+" is not exits please enter correct category id"));
            case 401: //not found id
                return ResponseEntity.status(400).body(new ApiResponse("The id of product: "+id+" is not exits"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The product have id: "+id+" updated successfully"));
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id){
        boolean flag=productService.deleteProduct(id);
        if (flag){
            return ResponseEntity.status(200).body(new ApiResponse("The product have id: "+id+" deleted successfully"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("The id of product: "+id+" is not exits"));
    }
}
