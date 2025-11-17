package com.example.e_commerce.Service;

import com.example.e_commerce.Model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProductService {
    ArrayList<Product> products=new ArrayList<>();
    private final CategoryService categoryService;
    private final UserService userService;

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

    public int addDiscount(String userId,String productId ,int discount, LocalDateTime startDiscount,LocalDateTime endDiscount){
        if (startDiscount.isBefore(LocalDateTime.now())){
            return 405; // the start date must be in future
        }
        if (endDiscount.isBefore(startDiscount)){
            return 400; // the start date must be before end date
        }
        if (discount<=0){
            return 401; // the discount must be more than 0
        }
        if (discount>=100){
            return 402; // the discount must be less than 100
        }
        for (User user:userService.getAllUsers()){
            if (user.getId().equals(userId)){
                if (!user.getRole().equals("Admin")){
                    return 404; // the role of user not admin
                }
                double percentage=discount/100.0;
                for (Product product:products){
                    if (product.getId().equals(productId)){
                        product.setPrice(product.getPrice()-(product.getPrice()*percentage));
                        return 200;
                    }
                }
                return 406; // not found product id
            }
        }
        return 403; // not found user id
    }
    public ArrayList<Product> productsByNameOfCategory(String categoryId){
        ArrayList<Product> byCategoryId=new ArrayList<>();
        for (Product product:products){
            if (product.getCategoryId().equals(categoryId)){
                byCategoryId.add(product);
            }
        }
        return byCategoryId;
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
