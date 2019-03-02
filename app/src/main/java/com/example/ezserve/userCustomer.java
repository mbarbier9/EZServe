package com.example.ezserve;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class userCustomer{
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FirebaseUser user;
    public String firstName, lastName, userId, email;

    public userCustomer(){
        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    public String getEmail()  {
        return user.getEmail();
    }

    public String getName(){

        
        return "name";
    }

}
