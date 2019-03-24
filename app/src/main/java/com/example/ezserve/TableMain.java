package com.example.ezserve;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TableMain extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private TextView tableNum;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_main);

        firebaseAuth = firebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Connection");

        tableNumTitle();

    }

    public void tableNumTitle(){
        tableNum = (TextView) findViewById(R.id.tableNumberMain);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dS : dataSnapshot.getChildren()){
                    if(dS.child("Connected Users").hasChild(userID)){
                        String tableN = dS.child("Table").getValue(String.class);
                        tableNum.setText("Table " + tableN);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
