package com.example.e_commerce.Controller;

import com.example.e_commerce.Api.ApiResponse;
import com.example.e_commerce.Model.MerchantStock;
import com.example.e_commerce.Model.Product;
import com.example.e_commerce.Service.MerchantStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            case 400: // not found user id
                return ResponseEntity.status(400).body(new ApiResponse("The user id: "+userId+" is not exits"));
            case 401: //the user don't have money enough
                return ResponseEntity.status(400).body(new ApiResponse("Your balance is insufficient to complete this purchase"));
            case 402: // the stock of product less than 1
                return ResponseEntity.status(400).body(new ApiResponse("This product is out of stock"));
            case 403: // not found product in merchant stock
                return ResponseEntity.status(400).body(new ApiResponse("This product does not belong to this merchant"));
            case 404: // not found merchant id
                return ResponseEntity.status(400).body(new ApiResponse("The merchant id: "+merchantId+" is not exits"));
            case 405: // not found product id
                return ResponseEntity.status(400).body(new ApiResponse("The product id: "+productId+" is not exits"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The purchase was successfully,, Thank you for shopping"));
    }

    @PutMapping("/replacing/{userId}/{oldProductid}/{merchantId}/{newProductId}/{purchaseDate}")
    public ResponseEntity<?> replacing (@PathVariable String userId, @PathVariable String oldProductid, @PathVariable String merchantId, @PathVariable String newProductId,@PathVariable LocalDateTime purchaseDate){
        int flag= merchantStockService.replacing(userId, oldProductid, merchantId, newProductId, purchaseDate);
        switch (flag){
            case 400:
                return ResponseEntity.status(400).body(new ApiResponse("The user id: "+userId+" is not exits"));
            case 401:
                return ResponseEntity.status(400).body(new ApiResponse("The old product id: "+oldProductid+" is not exits"));
            case 402:
                return ResponseEntity.status(400).body(new ApiResponse("The new product id: "+newProductId+" is not exits"));
            case 403:
                return ResponseEntity.status(400).body(new ApiResponse("The merchant id: "+merchantId+" is not exits"));
            case 404:
                return ResponseEntity.status(400).body(new ApiResponse("the purchase date must be in past"));
            case 405:
                return ResponseEntity.status(400).body(new ApiResponse("You have exceeded the allowed replacement period"));
            case 406:
                return ResponseEntity.status(400).body(new ApiResponse("The product is out of stock"));
            case 407:
                return ResponseEntity.status(400).body(new ApiResponse("This product does not belong to this merchant"));
            case 408:
                return ResponseEntity.status(400).body(new ApiResponse("The new product is more expensive than the one you want to exchange and your balance is insufficient"));
            case 200:
                return ResponseEntity.status(200).body(new ApiResponse("The product was successfully replaced"));
            case 201:
                return ResponseEntity.status(200).body(new ApiResponse("The replacement was successful The new product is cheaper and the price difference has been added to your balance"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("The replacement was successful The new product is more expensive and the price difference has been deducted from your balance"));
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

    @GetMapping("/merchant-by-product/{productId}")
    public ResponseEntity<?> merchantsByproductId(@PathVariable String productId){
        ArrayList<MerchantStock> merchantByproduct=merchantStockService.searchMerchantsByProduct(productId);
        if (merchantByproduct==null){
            return ResponseEntity.status(400).body(new ApiResponse("The product id: "+productId+" is not exits"));
        }
        if (merchantByproduct.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("There are No merchants selling this product"));
        }
        return ResponseEntity.status(200).body(merchantByproduct);
    }
}
