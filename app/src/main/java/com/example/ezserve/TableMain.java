package com.example.ezserve;

import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView tableNum, totalNum, restaurant;
    private FirebaseAuth firebaseAuth;
    public String userID,tableChild,restaurantChild;
    private DatabaseReference ref, resRef;
    private Button assistance, pay;
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
        restaurantChild = cM.restaurantReferenceString;
        ref = FirebaseDatabase.getInstance().getReference().child("Connection").child(restaurantChild).child(tableChild);

        assistance = (Button) findViewById(R.id.assistanceButton);
        assistance.setOnClickListener(this);

        pay = (Button)findViewById(R.id.payTableButton);
        pay.setOnClickListener(this);


        tableNumTitle();
        listView();
        df = new DecimalFormat("#.00");

    }

    public void tableNumTitle(){
        resRef = FirebaseDatabase.getInstance().getReference().child("Connection").child(restaurantChild);
        tableNum = (TextView) findViewById(R.id.tableNumberMain);
        restaurant = (TextView) findViewById(R.id.restaurantTableView);
        resRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int tableN = dataSnapshot.child(tableChild).child("Table").getValue(Integer.class);
                String name = dataSnapshot.child("Restaurant").getValue(String.class);
                restaurant.setText(name);
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
                    Toast.makeText(TableMain.this, "Assistance Requested!", Toast.LENGTH_SHORT).show();
                    assistance.setBackgroundColor(Color.RED);
                    assistance.setText("CANCEL");
                }
                else {
                    ref.child("Status").setValue("OFF");
                    assistance.setBackgroundColor(Color.GREEN);
                    assistance.setText("ASSISTANCE");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void listView(){
        final ListView itemsListView = (ListView) findViewById(R.id.tableMainList);
        totalNum = (TextView) findViewById(R.id.totalNumberTable);

        itemsList = new ArrayList<>();

        adapterItems = new ArrayAdapter<String>(this, R.layout.items_list, R.id.itemsListUnit, itemsList);

        DatabaseReference newRef = ref.child("Items");
        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemsList.clear();
                float total = 0;
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    String key = dS.getKey();
                    float value = dS.getValue(Float.class);

                    itemsList.add(key+" $" + df.format(value));
                    total = total+value;
                }
                itemsListView.setAdapter(adapterItems);
                totalNum.setText("$"+df.format(total));
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
        if(view == pay){
            startActivity(new Intent(TableMain.this, PayForItems.class));
        }
    }
}
