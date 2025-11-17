package com.example.e_commerce.Service;

import com.example.e_commerce.Model.Merchant;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MerchantService {
    ArrayList<Merchant> merchants=new ArrayList<>();

    public ArrayList<Merchant> getAllMerchants(){
        return merchants;
    }

    public void createMerchant(Merchant merchant){
        merchants.add(merchant);
    }

    public boolean updateMerchant(String id, Merchant merchant){
        int counter=-1;
        for (Merchant merchant1:merchants){
            counter++;
            if (merchant1.getId().equals(id)){
                merchants.set(counter,merchant);
                return true; // find and update
            }
        }
        return false; // not found id
    }

    public boolean deleteMerchant(String id){
        int counter=-1;
        for (Merchant merchant:merchants){
            counter++;
            if (merchant.getId().equals(id)){
                merchants.remove(counter);
                return true; // find id and remove
            }
        }
        return false; // not found id
    }
}
