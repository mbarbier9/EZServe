package com.example.ezserve;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class addCreditCard extends AppCompatActivity implements View.OnClickListener {
    private Spinner cardType;
    private List<String> typesArray =  new ArrayList<String>();
    private ArrayAdapter<String> typesAdapter;
    private Button addButton;
    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;
    private TextView cardNum, cardName, cardExp, cardCVV, cardZipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addButton = (Button) findViewById(R.id.add_credit_card);
        addButton.setOnClickListener(this);

        cardType = (Spinner) findViewById(R.id.Credit_Card_Type_spinner);

        cardNum = (TextView) findViewById(R.id.card_number_Text);
        cardName = (TextView) findViewById(R.id.card_name_text);
        cardExp = (TextView) findViewById(R.id.card_exp_Text);
        cardCVV = (TextView) findViewById(R.id.card_CVV_Text);
        cardZipcode = (TextView) findViewById(R.id.card_zipcode_text);

        //Credit Card types
        typesArray.add("Visa");
        typesArray.add("MasterCard");
        typesArray.add("Discover");

        typesAdapter = new ArrayAdapter<String>(addCreditCard.this, android.R.layout.simple_list_item_1, typesArray);
        cardType.setAdapter(typesAdapter);

        //Firebase adapter
        mRef = firebaseDatabase.getInstance().getReference("Users").child(CustomerMainActivity.userId).child("payment").child(Integer.toString(payments.creditCardAmount));
    }

    @Override
    public void onClick(View view) {
        if(view == addButton){
            addCard();
        }
    }

    private void addCard() {
        try {
            mRef.child("CVV").setValue(cardCVV.getText().toString());
            mRef.child("exp").setValue(cardExp.getText().toString());
            mRef.child("name").setValue(cardName.getText().toString());
            mRef.child("number").setValue(cardNum.getText().toString());
            mRef.child("type").setValue(cardType.getSelectedItem().toString());
            mRef.child("zipcode").setValue(cardZipcode.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }
}
