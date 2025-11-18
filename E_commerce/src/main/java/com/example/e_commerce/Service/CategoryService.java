package com.example.e_commerce.Service;

import com.example.e_commerce.Model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CategoryService {
    ArrayList<Category> categories=new ArrayList<>();

    public ArrayList<Category> getAllCategories(){
        return categories;
    }

    public void createCategory(Category category){
        categories.add(category);
    }

    public boolean updateCategory(String id, Category category){
        int counter=-1;
        for (Category category1:categories){
            counter++;
            if (category1.getId().equals(id)){
                categories.set(counter,category);
                return true; //find id and update
            }
        }
        return false; // not found id
    }

    public boolean deleteCategory(String id){
        int counter =-1;
        for (Category category:categories){
            counter++;
            if (category.getId().equals(id)){
                categories.remove(counter);
                return true; // find and delete
            }
        }
        return false; // not found id
    }
}
