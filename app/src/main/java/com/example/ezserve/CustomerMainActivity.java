package com.example.ezserve;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerMainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button signOut;
    private TextView welcomeText;
    private DatabaseReference ref;
    private FirebaseDatabase firebaseDatabase;
    private String userId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        firebaseAuth = firebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDB = firebaseDatabase.getInstance()
                .getReference().child("Users").child(userId);


        //Get status of user and send to main activity if user is signed out
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        signOut = (Button) findViewById(R.id.signOutCustomer);
        signOut.setOnClickListener(this);

        welcomeText = (TextView) findViewById(R.id.welcomeCustomer);
        welcomeText.setText("Welcome " + user.getEmail());
    }

    @Override
    public void onClick(View view) {
        if(view == signOut){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
