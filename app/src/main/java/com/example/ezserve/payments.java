package com.example.ezserve;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class payments extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;
    private Spinner userCards;
    private ArrayAdapter<String> cardsAdapter;
    private List<String> cards;
    private TextView cardNumber, cardName, cardExp;
    private ImageView cardType;
    private Button addButton;
    public static int creditCardAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRef = firebaseDatabase.getInstance().getReference("Users").child(CustomerMainActivity.userId).child("payment");
        userCards = (Spinner) findViewById(R.id.userCards_spinner);
        userCards.setOnItemSelectedListener(this);
        cardNumber = (TextView) findViewById(R.id.card_number);
        cardName = (TextView) findViewById(R.id.card_name);
        cardExp = (TextView) findViewById(R.id.card_exp);
        cardType = (ImageView) findViewById(R.id.card_type);
        addButton = (Button) findViewById(R.id.button2);
        addButton.setOnClickListener(this);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cards = new ArrayList<String>();

                for (DataSnapshot cardsSnapshot: dataSnapshot.getChildren()) {
                    String cardNum = cardsSnapshot.child("number").getValue(String.class);
                    cards.add(cardNum);
                    creditCardAmount++;
                }

                cardsAdapter = new ArrayAdapter<String>(payments.this, android.R.layout.simple_list_item_1, cards);
                userCards.setAdapter(cardsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String number = dataSnapshot.child(Integer.toString(position)).child("number").getValue(String.class);
                String name = dataSnapshot.child(Integer.toString(position)).child("name").getValue(String.class);
                String exp_month = dataSnapshot.child(Integer.toString(position)).child("exp_month").getValue(String.class);
                String exp_year = dataSnapshot.child(Integer.toString(position)).child("exp_year").getValue(String.class);
                String type = dataSnapshot.child(Integer.toString(position)).child("type").getValue(String.class);

                if (type.equals("Visa")) {
                    cardType.setImageResource(R.drawable.visa);
                } else if (type.equals("MasterCard")) {
                    cardType.setImageResource(R.drawable.mastercard);
                } else if (type.equals("Discover")) {
                    cardType.setImageResource(R.drawable.discover);
                }

                cardNumber.setText(number.replace(" ", "   "));
                cardName.setText(name.toUpperCase());
                cardExp.setText(exp_month + "/" + exp_year);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    @Override
    public void onClick(View view) {
        if(view == addButton){
            addCreditCard();
        }
    }

    private void addCreditCard() {
        try {
            startActivity(new Intent(payments.this, addCreditCard.class));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }
}
