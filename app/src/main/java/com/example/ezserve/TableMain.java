package com.example.ezserve;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.TimeUnit;

public class TableMain extends AppCompatActivity implements View.OnClickListener{

    private FirebaseDatabase firebaseDatabase;
    private TextView tableNum;
    private FirebaseAuth firebaseAuth;
    private String userID, tableChild;
    private DatabaseReference ref;
    private Button assistance;
    private ArrayList<String> itemsList;
    private ArrayAdapter<String> adapterItems;
    CustomerMainActivity cM;
    DecimalFormat df;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_main);


        firebaseAuth = firebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        tableChild = cM.tableReferenceString;
        ref = FirebaseDatabase.getInstance().getReference().child("Connection").child(tableChild);

        assistance = (Button) findViewById(R.id.assistanceButton);
        assistance.setOnClickListener(this);

        tableNumTitle();
        listView();
        df = new DecimalFormat("#.00");

    }

    public void tableNumTitle(){
        tableNum = (TextView) findViewById(R.id.tableNumberMain);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tableN = dataSnapshot.child("Table").getValue(String.class);
                tableNum.setText("Table " + tableN);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void requestAssistance(){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Status").getValue().equals("OFF")){
                    ref.child("Status").setValue("ON");
                }
                else {
                    ref.child("Status").setValue("OFF");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void listView(){
        final ListView itemsListView = (ListView) findViewById(R.id.tableMainList);
        itemsList = new ArrayList<>();
        adapterItems = new ArrayAdapter<String>(this, R.layout.items_list, R.id.itemsListUnit, itemsList);

        DatabaseReference newRef = ref.child("Items");
        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    String key = dS.getKey();
                    float value = dS.getValue(Float.class);

                    itemsList.add(key + " $" + df.format(value));
                }
                itemsListView.setAdapter(adapterItems);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == assistance){

            requestAssistance();
        }
    }
}
