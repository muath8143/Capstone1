package com.example.e_commerce.Service;

import com.example.e_commerce.Model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProductService {
    ArrayList<Product> products=new ArrayList<>();
    private final CategoryService categoryService;

    public ArrayList<Product> getAllProducts(){
        return products;
    }

    public boolean createProduct(Product product){
        for (Category category:categoryService.getAllCategories()){
            if (product.getCategoryId().equals(category.getId())){
                products.add(product);
                return true; // find category id and create product
            }
        }
        return false; // not found category id please enter a correct category id
    }

    public int updateProduct(String id , Product product){
        int counter =-1;
        for (Product product1:products){
            counter++;
            if (product1.getId().equals(id)){
                for (Category category:categoryService.getAllCategories()){
                    if (product.getCategoryId().equals(category.getId())){
                        products.set(counter,product);
                        return 200; //find id and category id and update
                    }
                }
                return 400; // found id but not found category id
            }
        }
        return 401; //not found id
    }

    public boolean deleteProduct(String id){
        int counter =-1;
        for (Product product:products){
            counter++;
            if (product.getId().equals(id)){
                products.remove(counter);
                return true; // find id and remove
            }
        }
        return false; // not found id
    }

    public double getPrice(String productId){
        for (Product product:products){
            if (product.getId().equals(productId)){
                return product.getPrice();
            }
        }
        return 0;
    }
}
