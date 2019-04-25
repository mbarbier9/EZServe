package com.example.ezserve;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class userProfile extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;
    private TextView firstNameText, lastNameText, emailText;
    private ImageView userPhoto;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRef = firebaseDatabase.getInstance().getReference("Users").child(CustomerMainActivity.userId);
        firstNameText = (TextView) findViewById(R.id.first_name_profile);
        lastNameText = (TextView) findViewById(R.id.last_name_profile);
        emailText = (TextView) findViewById(R.id.email_profile);

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);

        //userPhoto.setImageResource(R.drawable.ic_defaultuser_background);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("first name").getValue(String.class);
                String lastName = dataSnapshot.child("last name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                firstNameText.setText(firstName);
                lastNameText.setText(lastName);
                emailText.setText(email);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == saveButton){
            saveChanges();
        }
    }

    private void saveChanges() {
        try {
            mRef.child("first name").setValue(firstNameText.getText().toString());
            mRef.child("last name").setValue(lastNameText.getText().toString());
            mRef.child("email").setValue(emailText.getText().toString());
            Toast.makeText(this, "Information Updated Successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Information Update Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
