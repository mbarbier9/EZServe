package com.example.ezserve;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class userCustomer{
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    private String firstName, lastName, userId, email;

    public userCustomer(){
        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = firebaseAuth.getCurrentUser().getUid();
        ref = firebaseDatabase.getInstance().getReference().child("Users").child(userId);

    }

    public String getEmail()  {
        firstName = userId;
        return user.getEmail();
    }

    public String getUID(){
        return user.getUid();
    }

}
