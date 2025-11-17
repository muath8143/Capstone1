package com.example.e_commerce.Controller;

import com.example.e_commerce.Api.ApiResponse;
import com.example.e_commerce.Model.Category;
import com.example.e_commerce.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllCategory(){
        ArrayList<Category> allCategories=categoryService.getAllCategories();
        if (allCategories.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("No categories in system"));
        }
        return ResponseEntity.status(200).body(allCategories);
    }

    @PostMapping("/add-category")
    public ResponseEntity<?> addCategory(@RequestBody @Valid Category category, Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        categoryService.createCategory(category);
        return ResponseEntity.status(200).body(new ApiResponse("The category added successfully"));
    }

    @PutMapping("/update-category/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable String id,@RequestBody @Valid Category category,Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        boolean flag=categoryService.updateCategory(id, category);
        if (flag){
            return ResponseEntity.status(200).body(new ApiResponse("The category have id: "+id+" updated successfully"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("The id of category: "+id+" is not exits"));
    }

    @DeleteMapping("/delete-category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id){
        boolean flag=categoryService.deleteCategory(id);
        if (flag){
            return ResponseEntity.status(200).body(new ApiResponse("The category have id: "+id+" deleted successfully"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("The id of category: "+id+" is not exits"));
    }


}
