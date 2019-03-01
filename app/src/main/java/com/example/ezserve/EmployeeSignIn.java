package com.example.ezserve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class EmployeeSignIn extends AppCompatActivity implements View.OnClickListener {
    private Button signInEmployee;
    private EditText employeeID;

    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_sign_in);

        signInEmployee = (Button) findViewById(R.id.signInEmployeeButton);
        signInEmployee.setOnClickListener(this);

        employeeID = (EditText) findViewById(R.id.employeeIdInput);

    }

    public void employeeLogin(){
        String employeeNum = employeeID.getText().toString().trim();

        if (TextUtils.isEmpty(employeeNum)){
            Toast.makeText(this, "Please enter employee #", Toast.LENGTH_SHORT).show();
            return;
        }
        else{

        }

    }

    @Override
    public void onClick(View view){
        if(view == signInEmployee){
            employeeLogin();
        }

    }

}
