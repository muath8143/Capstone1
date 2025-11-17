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

    @PutMapping("/discount/{userID}/{productId}/{discount}/{startDiscount}/{endDiscount}")
    public ResponseEntity<?> addDiscount(@PathVariable String userID, @PathVariable String productId, @PathVariable int discount, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startDiscount, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endDiscount){
        int flag=productService.addDiscount(userID,productId,discount,startDiscount,endDiscount);
        switch (flag){
            case 400: // the start date must be before end date
                return ResponseEntity.status(400).body(new ApiResponse("The discount start date must be before discount end date"));
            case 401: // the discount must be more than 0
                return ResponseEntity.status(400).body(new ApiResponse("The discount must be more than 0"));
            case 402: // the discount must be less than 100
                return ResponseEntity.status(400).body(new ApiResponse("The discount must be less than 100"));
            case 403: // not found user id
                return ResponseEntity.status(400).body(new ApiResponse("The user id: "+userID+" is not found"));
            case 404: // the role of user not admin
                return ResponseEntity.status(400).body(new ApiResponse("Your are not admin you cannot add discount"));
            case 405: // the start date must be in future
                return ResponseEntity.status(400).body(new ApiResponse("The discount start date must be in future"));
            case 406: // not found product id
                return ResponseEntity.status(400).body(new ApiResponse("The product have id:"+productId+" is not exits"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The discount was successfully applied"));
    }

    @GetMapping("/get-by-category/{categoryId}")
    public ResponseEntity<?> productsByCategoryId(@PathVariable String categoryId){
        ArrayList<Product> byCategoryId=productService.productsByNameOfCategory(categoryId);
        if (byCategoryId.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("There are no products in this category"));
        }
        return ResponseEntity.status(200).body(byCategoryId);
    }
}
