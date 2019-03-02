package com.example.ezserve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView imEmployeeButton, registerButton;
    private Button signInButton;
    private EditText emailInput, passwordInput;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);

        //This is to get info out of edit text email and password
        emailInput = (EditText) findViewById(R.id.emailSignUp);
        passwordInput = (EditText) findViewById(R.id.passwordSignUpOne);

        imEmployeeButton = (TextView) findViewById(R.id.imEmployee);
        imEmployeeButton.setOnClickListener(this);

        signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        registerButton = (TextView) findViewById(R.id.notMember);
        registerButton.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, CustomerMainActivity.class));
        }
    }

    public void userLogIn(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        //Check if they are empty
        if (TextUtils.isEmpty(email)){
            Toast.makeText(MainActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(MainActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Signing in...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.cancel();
                    startActivity(new Intent(MainActivity.this, CustomerMainActivity.class));
                }
                else{
                    progressDialog.cancel();
                    Toast.makeText(MainActivity.this, "Sign In error", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }


    @Override
    public void onClick(View view){
        if(view == imEmployeeButton){
            startActivity(new Intent(this, EmployeeSignIn.class));
        }
        if(view == signInButton){
            userLogIn();
        }

        if(view == registerButton){
            startActivity(new Intent(this, signUp.class));
        }
    }
}
