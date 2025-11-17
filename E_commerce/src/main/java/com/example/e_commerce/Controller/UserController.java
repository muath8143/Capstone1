package com.example.e_commerce.Controller;

import com.example.e_commerce.Api.ApiResponse;
import com.example.e_commerce.Model.User;
import com.example.e_commerce.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsers(){
        ArrayList<User> users=userService.getAllUsers();
        if (users.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("No users in system"));
        }
        return ResponseEntity.status(200).body(users);
    }

    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestBody @Valid User user, Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        userService.createUser(user);
        return ResponseEntity.status(200).body(new ApiResponse("The user added successfully"));
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id,@RequestBody @Valid User user,Errors error){
        if (error.hasErrors()){
            String message =error.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        boolean flag=userService.updateUser(id, user);
        if (flag){
            return ResponseEntity.status(200).body(new ApiResponse("The user have id: "+id+" updated successfully"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("The id of user: "+id+" is not exits"));
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id){
        boolean flag=userService.deleteUser(id);
        if (flag){
            return ResponseEntity.status(200).body(new ApiResponse("The user have id: "+id+" deleted successfully"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("The id of user: "+id+" is not exits"));
    }


}
