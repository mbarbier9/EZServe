package com.example.ezserve;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerMainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button signOut, scanQR;
    private TextView welcomeText;
     DatabaseReference ref, billRef, tableRef;
    private FirebaseDatabase firebaseDatabase;
    public static String userId, tableReferenceString;
    private userCustomer userCustomer;
    private ArrayList<String> billList;
    private ArrayAdapter<String> adapterR;
    BillHistory billHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        firebaseAuth = firebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        ref = firebaseDatabase.getInstance().getReference("Users").child(userId);
        tableRef = firebaseDatabase.getInstance().getReference("Connection");
        billRef = firebaseDatabase.getInstance().getReference("Users").child(userId).child("Bills");
        tableReferenceString = "42019";

        userCustomer = new userCustomer();

        //Get status of user and send to main activity if user is signed out
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        signOut = (Button) findViewById(R.id.signOutCustomer);
        signOut.setOnClickListener(this);

        scanQR = (Button) findViewById(R.id.connectToTable);
        scanQR.setOnClickListener(this);

        welcomeTextView();
        billListView();
    }

    public void welcomeTextView(){
        //Get First Name and welcome the user
        welcomeText = (TextView) findViewById(R.id.welcomeCustomer);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("first name").getValue(String.class);
                welcomeText.setText("Welcome " + firstName+"!");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void billListView(){
        //List view
        final ListView customerList = (ListView) findViewById(R.id.customerMainList);
        billHistory = new BillHistory();
        billList = new ArrayList<>();
        adapterR = new ArrayAdapter<String>(this, R.layout.customer_bill_history, R.id.billRestaurant, billList);
        billRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot bH : dataSnapshot.getChildren()){
                    billHistory = bH.getValue(BillHistory.class);

                    billList.add("Restaurant: " + billHistory.getRestaurant() + "\nDate: "+
                            billHistory.getDate() + "\nTotal: $ " + billHistory.getTotal());
                }
                customerList.setAdapter(adapterR);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void compareCode(){
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(tableReferenceString)){
                    tableRef.child(tableReferenceString).child("Connected Users").child(userId).setValue(true);
                    return;
                }
                else{
                    Toast.makeText(CustomerMainActivity.this, "Error connecting...", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == signOut){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        if(view == scanQR){
            compareCode();
            startActivity(new Intent(CustomerMainActivity.this, TableMain.class));
        }
    }
}