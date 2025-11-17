package com.example.e_commerce.Service;

import com.example.e_commerce.Model.Merchant;
import com.example.e_commerce.Model.MerchantStock;
import com.example.e_commerce.Model.Product;
import com.example.e_commerce.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MerchantStockService {
    ArrayList<MerchantStock> merchantStocks=new ArrayList<>();
    private final ProductService productService;
    private final MerchantService merchantService;
    private final UserService userService;

    public ArrayList<MerchantStock> getAllMerchantStocks(){
        return merchantStocks;
    }

    public int createMerchantStock (MerchantStock merchantStock){
        if (merchantStock.getStock()<=10){
            return 405; // the stock must be more than 10 at start
        }
        for (Product product:productService.getAllProducts()){
            if (merchantStock.getProductId().equals(product.getId())){
                for (MerchantStock merchantStock1:merchantStocks){
                    if (merchantStock1.getProductId().equals(merchantStock.getProductId())){
                        return 403; // the product belong to another merchant
                    }
                }
                for (Merchant merchant:merchantService.getAllMerchants()){
                    if (merchantStock.getMerchantId().equals(merchant.getId())){
                        merchantStocks.add(merchantStock);
                        return 200; // found id product and found merchant id and create merchant stock
                    }
                }
                return 401; // not found merchant id
            }
        }
        return 402; // not found product id
    }

    public int updateMerchanStock (String id,MerchantStock merchantStock){
        int counter=-1;
        for (MerchantStock merchantStockForGetId:merchantStocks){
            counter++;
            if (merchantStockForGetId.getId().equals(id)){
                for (Product product:productService.getAllProducts()){
                    if (merchantStock.getProductId().equals(product.getId())){

                        for (MerchantStock merchantStockForGetIdProduct:merchantStocks){
                            if (merchantStockForGetIdProduct.getProductId().equals(merchantStock.getProductId()) && !merchantStockForGetIdProduct.getId().equals(id)){
                                return 400; // the product belong to another merchant
                            }
                        }

                        for (Merchant merchantForGetIdMerchant:merchantService.getAllMerchants()){
                            if (merchantForGetIdMerchant.getId().equals(merchantStock.getMerchantId())){
                                merchantStocks.set(counter,merchantStock);
                                return 200; // found id and found product id and not belong to another merchant and found merchant id
                            }
                        }
                        return 401; //not found id merchant
                    }
                }
                return 402; // not found the product id
            }
        }
       return 403; // not found id of merchan stock
    }

    public boolean deleteMerchantStock(String id){
        int counter=-1;
        for (MerchantStock merchantStock:merchantStocks){
            counter++;
            if (merchantStock.getId().equals(id)){
                merchantStocks.remove(counter);
                return true; // found id and remove
            }
        }
        return false; // not found id
    }

    public int addStockForProduct(String productId,String merchantId ,int stock){
        if (stock<=0){
            return 402; // the stock should be positive number
        }
        if (!findMerchantId(merchantId)){
            return 400; // not found merchant id
        }
        if (!findProduct(productId)){
            return 401; //not found product id
        }
        for (MerchantStock merchantStock:merchantStocks){
            if (merchantStock.getProductId().equals(productId) && merchantStock.getMerchantId().equals(merchantId)){
                merchantStock.setStock(merchantStock.getStock()+stock);
                return 200; // found product id and found merchant id and add stock
            }
        }
        return 403; // the product belong to another merchant
    }

    public boolean findUserId(String userId){
        for (User user:userService.getAllUsers()){
            if (user.getId().equals(userId)){
                return true;
            }
        }
        return false;
    }

    public boolean findProduct(String productId){
        for (Product product:productService.getAllProducts()){
            if (product.getId().equals(productId)){
                return true;
            }
        }
        return false;
    }

    public boolean findMerchantId(String merchantId){
        for (Merchant merchant:merchantService.getAllMerchants()){
            if (merchant.getId().equals(merchantId)){
                return true;
            }
        }
        return false;
    }

    public int buyProductDirectly (String userId,String productId,String merchantId){
        for (User user:userService.getAllUsers()){
            if (user.getId().equals(userId)){
                for (Product product:productService.getAllProducts()){
                    if (product.getId().equals(productId)){
                        for (Merchant merchant:merchantService.getAllMerchants()) {
                            if (merchant.getId().equals(merchantId)) {
                                for (MerchantStock merchantStock : merchantStocks) {
                                    if (merchantStock.getProductId().equals(productId) && merchantStock.getMerchantId().equals(merchantId)) {
                                        if (merchantStock.getStock() >= 1) {
                                            if (user.getBalance() >= product.getPrice()) {
                                                user.setBalance(user.getBalance() - product.getPrice());
                                                merchantStock.setStock(merchantStock.getStock() - 1);
                                                return 200; // all conditions are met
                                            }
                                            return 401; //the user don't have money enough
                                        }
                                        return 402; // the stock of product less than 1
                                    }
                                }
                                return 403; // not found product in merchant stock
                            }
                        }
                        return 404; // not found merchant id
                    }
                }
                return 405; // not found product id
            }
        }
        return 400; // not found user id
    }
    public int replacing (String userId, String oldProductId,String merchantId, String newProductId, LocalDateTime purchaseDate){
        LocalDateTime oneWeek= LocalDateTime.now().minusWeeks(1);
       if (!findUserId(userId)){
           return 400; // not found user id
       }
       if (!findProduct(oldProductId)){
           return 401; //not found old product to replaced id
       }
       if (!findProduct(newProductId)){
           return 402; // not found new product id
       }
       if (!findMerchantId(merchantId)){
           return 403; // not found merchant
       }
       if (purchaseDate.isAfter(LocalDateTime.now())){
           return 404; // the purchase date must be in past
       }
       if (purchaseDate.isBefore(oneWeek)){
           return 405; // the purchase date before one week you cannot replace
       }
        double oldProductPrice=productService.getPrice(oldProductId);
        double newProductPrice=productService.getPrice(newProductId);
        double differenceOldAndNew=Math.abs(oldProductPrice-newProductPrice);
        for (Merchant merchant:merchantService.getAllMerchants()){
            if (merchant.getId().equals(merchantId)){
                for (MerchantStock merchantStock:merchantStocks){
                    if (merchantStock.getMerchantId().equals(merchantId) && merchantStock.getProductId().equals(newProductId)){
                        if (merchantStock.getStock()==0){
                            return 406; // no stock
                        }
                        if (oldProductPrice==newProductPrice){
                            merchantStock.setStock(merchantStock.getStock()-1);
                            return 200; // success
                        }
                        for (User user: userService.getAllUsers()){
                            if (user.getId().equals(userId)) {
                                if (oldProductPrice>newProductPrice) {
                                    user.setBalance(user.getBalance() + differenceOldAndNew);
                                    merchantStock.setStock(merchantStock.getStock() - 1);
                                    return 201; // success and add diff to user balance
                                }
                                if (oldProductPrice<newProductPrice){
                                    if (user.getBalance()<differenceOldAndNew){
                                        return 408; // you don't have money enough
                                    }
                                    user.setBalance(user.getBalance()-differenceOldAndNew);
                                    merchantStock.setStock(merchantStock.getStock()-1);
                                    return 202; // success and discount diff from user balance
                                }
                            }
                        }
                    }
                }
            }
        }
        return 407; // the merchant don't have product
    }

    public ArrayList<Product> getByMerchant(String merchantId){
        ArrayList<Product> byMerchantId=new ArrayList<>();
        boolean flag=findMerchantId(merchantId);
        if (!flag){
            return null;
        }
        for (MerchantStock merchantStock:merchantStocks){
            if (merchantStock.getMerchantId().equals(merchantId)){
                for (Product product:productService.getAllProducts()){
                    if (product.getId().equals(merchantStock.getProductId())){
                        byMerchantId.add(product);
                    }
                }
            }
        }
        return byMerchantId;
    }
    public ArrayList<MerchantStock> searchMerchantsByProduct(String productId){
        boolean flag=findProduct(productId);
        if (!flag){
            return null;
        }
        ArrayList<MerchantStock> merchantByProduct=new ArrayList<>();
        for (MerchantStock merchantStock:merchantStocks){
            if (merchantStock.getProductId().equals(productId)){
                merchantByProduct.add(merchantStock);
            }
        }
        return merchantByProduct;
    }
}
