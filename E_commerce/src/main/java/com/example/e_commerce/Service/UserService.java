package com.example.e_commerce.Service;

import com.example.e_commerce.Model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {
    ArrayList<User> users=new ArrayList<>();

    public ArrayList<User> getAllUsers(){
        return users;
    }


    public void createUser(User user){
        users.add(user);
    }


    public boolean updateUser (String id,User user){
        int counter=-1;
        for (User user1:users){
            counter++;
            if (user1.getId().equals(id)){
                users.set(counter,user);
                return true; //find id and update
            }
        }
        return false; //not found id
    }


    public boolean deleteUser(String id){
        int counter=-1;
        for (User user:users){
            counter++;
            if (user.getId().equals(id)){
                users.remove(counter);
                return true; //find user and remove it
            }
        }
        return false; //not found id
    }
}
