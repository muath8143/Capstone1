package com.example.e_commerce.Controller;

import com.example.e_commerce.Api.ApiResponse;
import com.example.e_commerce.Model.MerchantStock;
import com.example.e_commerce.Model.Product;
import com.example.e_commerce.Service.MerchantStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/merchant-stock")
@RequiredArgsConstructor
public class MerchantStockController {
    private final MerchantStockService merchantStockService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        ArrayList<MerchantStock> merchantStocks=merchantStockService.getAllMerchantStocks();
        if (merchantStocks.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("No merchant stock in system"));
        }
        return ResponseEntity.status(200).body(merchantStocks);
    }

    @PostMapping("/add-merchant-stock")
    public ResponseEntity<?> addMerchantStock(@RequestBody @Valid MerchantStock merchantStock, Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        int flag=merchantStockService.createMerchantStock(merchantStock);
        switch(flag){
            case 401:
                return ResponseEntity.status(400).body(new ApiResponse("The merchant id: "+merchantStock.getMerchantId()+" is not exits"));
            case 402:
                return ResponseEntity.status(400).body(new ApiResponse("The product id: "+merchantStock.getProductId()+" is not exits"));
            case 403:
                return ResponseEntity.status(400).body(new ApiResponse("The product have id: "+merchantStock.getProductId()+" belong to another merchant you cannot take it"));
            case 405:
                return ResponseEntity.status(400).body(new ApiResponse("The stock must be at least 11"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The Merchant stock added successfully"));
    }

    @PutMapping("/update-merchant-stock/{id}")
    public ResponseEntity<?> updateMerchantStock(@PathVariable String id,@RequestBody @Valid MerchantStock merchantStock,Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        int flag=merchantStockService.updateMerchanStock(id, merchantStock);

        switch (flag){
            case 400: // the product belong to another merchant
                return ResponseEntity.status(400).body(new ApiResponse("The product have id: "+merchantStock.getProductId()+" belong to another merchant you cannot take it"));
            case 401: //not found id merchant
                return ResponseEntity.status(400).body(new ApiResponse("The merchant id: "+merchantStock.getMerchantId()+" is not exits"));
            case 402: // not found the product id
                return ResponseEntity.status(400).body(new ApiResponse("The product id: "+merchantStock.getProductId()+" is not exits"));
            case 403: // not found id of merchan stock
                return ResponseEntity.status(400).body(new ApiResponse("The merchant stock id: "+id+" is not exits"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The merchant stock have id: "+id+" updated successfully"));
    }

    @DeleteMapping("/delete-merchant-stock/{id}")
    public ResponseEntity<?> deleteMerchantStock(@PathVariable String id){
        boolean flag =merchantStockService.deleteMerchantStock(id);
        if (flag){
            return ResponseEntity.status(200).body(new ApiResponse("The merchant stock have id: "+id+" deleted successfully"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("The id of merchant stock: "+id+" is not exits"));
    }

    @PutMapping("/add-to-stock/{productId}/{merchantId}/{stock}")
    public ResponseEntity<?> increaseProductStock(@PathVariable String productId,@PathVariable String merchantId,@PathVariable int stock){
        int flag=merchantStockService.addStockForProduct(productId, merchantId, stock);
        switch (flag){
            case 400: // not found merchant id
                return ResponseEntity.status(400).body(new ApiResponse("The merchant id: "+merchantId+" is not exits"));
            case 401: // not found product id
                return ResponseEntity.status(400).body(new ApiResponse("The product id: "+productId+" is not exits"));
            case 402: // the stock should be positive number
                return ResponseEntity.status(400).body(new ApiResponse("The increase must be positive"));
            case 403: // the product belong to another merchant
                return ResponseEntity.status(400).body(new ApiResponse("The merchant have id: "+merchantId+" does not own this product"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("Product stock was successfully increased"));
    }

    @PutMapping("/purchase/{userId}/{productId}/{merchantId}")
    public ResponseEntity<?> buyProduct(@PathVariable String userId,@PathVariable String productId,@PathVariable String merchantId){
        int flag=merchantStockService.buyProductDirectly(userId, productId, merchantId);
        switch (flag){
            case 0: // not found user id
                return ResponseEntity.status(400).body(new ApiResponse("The user id: "+userId+" is not exits"));
            case 1: //the user don't have money enough
                return ResponseEntity.status(400).body(new ApiResponse("Your balance is insufficient to complete this purchase"));
            case 2: // the stock of product less than 1
                return ResponseEntity.status(400).body(new ApiResponse("This product is out of stock"));
            case 3: // not found product in merchant stock
                return ResponseEntity.status(400).body(new ApiResponse("This product does not belong to this merchant"));
            case 4: // not found merchant id
                return ResponseEntity.status(400).body(new ApiResponse("The merchant id: "+merchantId+" is not exits"));
            case 5: // not found product id
                return ResponseEntity.status(400).body(new ApiResponse("The product id: "+productId+" is not exits"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The purchase was successfully,, Thank you for shopping"));
    }

    @PutMapping("/refund/{userId}/{Productid}/{merchantId}")
    public ResponseEntity<?> refund (@PathVariable String userId, @PathVariable String Productid, @PathVariable String merchantId){
        int flag=merchantStockService.refund(userId, Productid, merchantId);
        switch (flag){
            case 0:
                return ResponseEntity.status(400).body(new ApiResponse("The user id: "+userId+" is not exits"));
            case 1:
                return ResponseEntity.status(400).body(new ApiResponse("The product id: "+Productid+" is not exits"));
            case 2:
                return ResponseEntity.status(400).body(new ApiResponse("the merchant don't have product"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The refund was successful"));
    }

    @GetMapping("/get-by-merchant-id/{merchantId}")
    public ResponseEntity<?> getByMerchantId(@PathVariable String merchantId){
        ArrayList<Product> byMerchantId=merchantStockService.getByMerchant(merchantId);
        if (byMerchantId==null){
            return ResponseEntity.status(400).body(new ApiResponse("This merchant id: "+merchantId+" is not exits"));
        }
        if (byMerchantId.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("This merchant don't have any products"));
        }
        return ResponseEntity.status(200).body(byMerchantId);
    }
    @PutMapping("/discount/{merchantId}/{productId}/{discount}/{startDiscount}/{endDiscount}")
    public ResponseEntity<?> addDiscount(@PathVariable String merchantId, @PathVariable String productId, @PathVariable int discount,
                                         @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startDiscount, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endDiscount){
        int flag=merchantStockService.addDiscount(merchantId,productId,discount,startDiscount,endDiscount);
        switch (flag){
            case 1: // the start date must be before end date
                return ResponseEntity.status(400).body(new ApiResponse("The discount start date must be before discount end date"));
            case 2: // the discount must be more than 0
                return ResponseEntity.status(400).body(new ApiResponse("The discount must be more than 0"));
            case 3: // the discount must be less than 100
                return ResponseEntity.status(400).body(new ApiResponse("The discount must be less than 100"));
            case 6: // not found merchant
                return ResponseEntity.status(400).body(new ApiResponse("you are not merchant"));
            case 4: // the role of user not admin
                return ResponseEntity.status(400).body(new ApiResponse( "you don't have this product"));
            case 0: // the start date must be in future
                return ResponseEntity.status(400).body(new ApiResponse("The discount start date must be in future"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The discount was successfully applied"));
    }

    @PutMapping("/transfer/{userid}/{email}/{amount}")
    public ResponseEntity<?> transferAmount(@PathVariable String userid,@PathVariable String email,@PathVariable double amount){
        int flag = merchantStockService.transferBalance(userid, email, amount);
        switch (flag){
            case 0:
                return ResponseEntity.status(400).body(new ApiResponse("The amount must be greater than 0"));
            case 1:
                return ResponseEntity.status(400).body(new ApiResponse("The user id: "+userid+" is not exists"));
            case 2:
                return ResponseEntity.status(400).body(new ApiResponse("your balance is less than the transfer amount"));
            case 3:
                return ResponseEntity.status(400).body(new ApiResponse("The email: "+email+" is not exists"));
            case 4:
                return ResponseEntity.status(400).body(new ApiResponse("You cannot transfer to yourself"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The transfer was successful"));
    }
}
