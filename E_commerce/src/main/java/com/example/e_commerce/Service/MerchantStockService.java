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
                                                return 6; // all conditions are met
                                            }
                                            return 1; //the user don't have money enough
                                        }
                                        return 2; // the stock of product less than 1
                                    }
                                }
                                return 3; // not found product in merchant stock
                            }
                        }
                        return 4; // not found merchant id
                    }
                }
                return 5; // not found product id
            }
        }
        return 0; // not found user id
    }

    public int refund (String userId, String productId,String merchantId){
       if (!findUserId(userId)){
           return 0; // not found user id
       }
       if (!findProduct(productId)){
           return 1; //not found product
       }
      for (MerchantStock merchantStock:merchantStocks){
          if (merchantStock.getProductId().equals(productId)&& merchantStock.getMerchantId().equals(merchantId)){
              for (User user: userService.getAllUsers()){
                  if (user.getId().equals(userId)){
                      for (Product product: productService.getAllProducts()){
                          if (product.getId().equals(productId)){
                              user.setBalance(user.getBalance()+product.getPrice());
                              merchantStock.setStock(merchantStock.getStock()+1);
                              return 10;
                          }
                      }
                  }
              }
          }
      }
        return 2; // the merchant don't have product
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

    public int addDiscount(String merchantId,String productId ,int discount, LocalDateTime startDiscount,LocalDateTime endDiscount){
        if (startDiscount.isBefore(LocalDateTime.now())){
            return 0; // the start date must be in future
        }
        if (endDiscount.isBefore(startDiscount)){
            return 1; // the start date must be before end date
        }
        if (discount<=0){
            return 2; // the discount must be more than 0
        }
        if (discount>=100){
            return 3; // the discount must be less than 100
        }
        if (!findMerchantId(merchantId)){
            return 6; // not found merchant
        }


        for (MerchantStock merchantStock:merchantStocks){
            if (merchantStock.getMerchantId().equals(merchantId) && merchantStock.getProductId().equals(productId)){
                double percentage=discount/100.0;
                for (Product product: productService.getAllProducts()){
                    if (product.getId().equals(productId)){
                        product.setPrice(product.getPrice()-(product.getPrice()*percentage));
                        return 200;
                    }
                }
            }
        }
            return 4; // you don't have this product
    }

    public boolean findByEmail(String email){
        for (User user:userService.getAllUsers()){
            if (user.getEmail().equals(email)){
                return true;
            }
        }
        return false;
    }

    public int transferBalance(String userId,String email,double amount){
        if (amount<=0){
            return 0; // the amount must be positive
        }
        if (!findUserId(userId)){
            return 1; // not found user id
        }

        if (!findByEmail(email)){
            return 3; // not found email
        }

        for (User user: userService.getAllUsers()){
            if (user.getId().equals(userId) && user.getEmail().equals(email)){
                return 4; // cannot transfer to you self
            }
            if (user.getId().equals(userId)){
                if (user.getBalance() < amount) {
                    return 2; // the balance must be more than amount
                }
                user.setBalance(user.getBalance()-amount);
                continue;
            }
            if (user.getEmail().equals(email)){
                user.setBalance(user.getBalance()+amount);
            }
        }
        return 10;
    }
}
