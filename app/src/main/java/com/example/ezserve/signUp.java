package com.example.ezserve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class signUp extends AppCompatActivity implements View.OnClickListener{

    private Button signUpButton;
    private EditText firstNameSignUp, lastNameSignUp, emailSignUp, passwordOneSignUp,
            passwordTwoSignUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    //Progress dialog works to let user know the status of action
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        //Edit text initialize
        firstNameSignUp = findViewById(R.id.firstNameSignUp);
        lastNameSignUp = findViewById(R.id.lastNameSignUp);
        emailSignUp = findViewById(R.id.emailSignUp);
        passwordOneSignUp = findViewById(R.id.passwordSignUpOne);
        passwordTwoSignUp = findViewById(R.id.passwordSignUpTwo);

        //Initialize and ready button for click
        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(this);
    }

    private void register(){
        final String firstName = firstNameSignUp.getText().toString().trim();
        final String lastName = lastNameSignUp.getText().toString().trim();
        final String email = emailSignUp.getText().toString().trim();
        String passwordOne = passwordOneSignUp.getText().toString().trim();
        String passwordTwo = passwordTwoSignUp.getText().toString().trim();

        //If any of the text views are not filled out there will be an error message to notify user
        if (TextUtils.isEmpty(firstName)||TextUtils.isEmpty(lastName)||TextUtils.isEmpty(email)
                ||TextUtils.isEmpty(passwordOne)||TextUtils.isEmpty(passwordTwo)){
            Toast.makeText(this, "Not all blanks are filled", Toast.LENGTH_SHORT).show();
            return;
        }
        //Checks if both passwords match before making the account
        if (passwordOne.equals(passwordTwo)==true){
            progressDialog.setMessage("Registering...");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email,passwordOne)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                //Store user info into database
                                String userID = firebaseAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDB = firebaseDatabase.getInstance()
                                        .getReference().child("Users").child(userID);

                                Map newInfo = new HashMap();
                                newInfo.put("first name", firstName);
                                newInfo.put("last name", lastName);
                                newInfo.put("email", email);
                                currentUserDB.setValue(newInfo);

                                //Tell user status of operation
                                Toast.makeText(signUp.this, "Registered Successfully",Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                            else{
                                Toast.makeText(signUp.this, "Register was not successful",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });


        }
        else{
            Toast.makeText(this, "Passwords do not match",Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    public void onClick(View view) {
        if(view == signUpButton){
            register();
        }
    }
}
