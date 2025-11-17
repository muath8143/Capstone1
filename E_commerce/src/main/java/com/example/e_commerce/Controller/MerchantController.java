package com.example.e_commerce.Controller;

import com.example.e_commerce.Api.ApiResponse;
import com.example.e_commerce.Model.Merchant;
import com.example.e_commerce.Service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantService merchantService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllMerchant(){
        ArrayList<Merchant> merchants=merchantService.getAllMerchants();
        if(merchants.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("No merchants in system"));
        }
        return ResponseEntity.status(200).body(merchants);
    }

    @PostMapping("/add-merchant")
    public ResponseEntity<?> addMerchant(@RequestBody @Valid Merchant merchant, Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        merchantService.createMerchant(merchant);
        return ResponseEntity.status(200).body(new ApiResponse("The merchant added successfully"));
    }

    @PutMapping("/update-merchant/{id}")
    public ResponseEntity<?> updateMerchant(@PathVariable String id,@RequestBody @Valid Merchant merchant ,Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        boolean flag= merchantService.updateMerchant(id, merchant);
        if (flag){
            return ResponseEntity.status(200).body(new ApiResponse("The merchant have id: "+id+" updated successfully"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("The id of merchant: "+id+" is not exits"));
    }

    @DeleteMapping("/delete-merchant/{id}")
    public ResponseEntity<?> deleteMerchant(@PathVariable String id){
        boolean flag=merchantService.deleteMerchant(id);
        if (flag){
            return ResponseEntity.status(200).body(new ApiResponse("The merchant have id: "+id+" deleted successfully"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("The id of merchant: "+id+" is not exits"));
    }

}
