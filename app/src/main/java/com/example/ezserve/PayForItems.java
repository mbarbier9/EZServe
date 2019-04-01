package com.example.ezserve;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayForItems extends AppCompatActivity  implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Button confirmPay;
    private DatabaseReference ref,resRef,mRef;
    private ArrayList<String> itemsList;
    private ArrayAdapter<String> adapterItems;
    private FirebaseAuth firebaseAuth;
    CustomerMainActivity cM;
    private String tableChild, restaurantChild;
    private TextView  totalNum, restaurant, tax;
    DecimalFormat df;
    private Spinner selectPayment;
    private ArrayAdapter<String> cardsAdapter;
    private List<String> cards;
    public static int creditCardAmount = 0, newPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_for_items);
        firebaseAuth = firebaseAuth.getInstance();
        tableChild = cM.tableReferenceString;
        restaurantChild = cM.restaurantReferenceString;
        ref = FirebaseDatabase.getInstance().getReference().child("Connection").child(restaurantChild).child(tableChild);
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(CustomerMainActivity.userId).child("payment");

        df = new DecimalFormat("#.00");



        confirmPay = (Button) findViewById(R.id.confirmPayment);
        confirmPay.setOnClickListener(this);

        selectPayment = (Spinner) findViewById(R.id.selectPayment);
        selectPayment.setOnItemSelectedListener(this);

        tableNumTitle();
        listView();
        choosePayment();

    }

    public void choosePayment(){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cards = new ArrayList<String>();

                for (DataSnapshot cardsSnapshot: dataSnapshot.getChildren()) {
                    String cardNum = cardsSnapshot.child("number").getValue(String.class);
                    String lastFourDigits = cardNum.substring(cardNum.length() - 4);
                    cards.add(lastFourDigits);
                    creditCardAmount++;
                }

                cardsAdapter = new ArrayAdapter<String>(PayForItems.this, android.R.layout.simple_list_item_1, cards);
                selectPayment.setAdapter(cardsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void tableNumTitle(){
        resRef = FirebaseDatabase.getInstance().getReference().child("Connection").child(restaurantChild);
        restaurant = (TextView) findViewById(R.id.restaurantPay);
        resRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Restaurant").getValue(String.class);
                restaurant.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void listView(){
        final ListView itemsListView = (ListView) findViewById(R.id.payItemsList);
        totalNum = (TextView) findViewById(R.id.totalNumberPay);
        tax = (TextView) findViewById(R.id.taxNumberPay);
        itemsList = new ArrayList<>();

        adapterItems = new ArrayAdapter<String>(this, R.layout.items_list, R.id.itemsListUnit, itemsList);

        DatabaseReference newRef = ref.child("Items");
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemsList.clear();
                double total = 0;
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    String key = dS.getKey();
                    double value = dS.getValue(Double.class);

                    itemsList.add(key+" $" + df.format(value));
                    total = total+value;
                }
                total = total+(total*.0805);
                itemsListView.setAdapter(adapterItems);
                totalNum.setText("$"+df.format(total));
                tax.setText("$"+df.format(total*.0805));
                ref.child("Total").setValue(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void payment(){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == confirmPay){

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        newPosition = position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }
}
