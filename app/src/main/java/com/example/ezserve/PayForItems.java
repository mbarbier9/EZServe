package com.example.ezserve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simplify.android.sdk.Card;
import com.simplify.android.sdk.CardToken;
import com.simplify.android.sdk.Customer;
import com.simplify.android.sdk.Simplify;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PayForItems  extends AppCompatActivity  implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Button confirmPay;
    private DatabaseReference ref,resRef,mRef, billHRef;
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
    public static int creditCardAmount = 0;
    private int cardSelected;
    double total = 0.0;

    private Card myCard;

    private Simplify simplify;
    private String publicAPIkey = "sbpb_ZWQ0M2Q4ZWMtMmJhOC00N2ZjLThjMGMtYjljYTJkMWM2NzFm";
    public String paymentStatus = "Not Processed!";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_for_items);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = firebaseAuth.getInstance();
        tableChild = cM.tableReferenceString;
        restaurantChild = cM.restaurantReferenceString;
        ref = FirebaseDatabase.getInstance().getReference().child("Connection").child(restaurantChild).child(tableChild);
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(CustomerMainActivity.userId);

        df = new DecimalFormat("#.00");

        confirmPay = (Button) findViewById(R.id.confirmPayment);
        confirmPay.setOnClickListener(this);

        selectPayment = (Spinner) findViewById(R.id.selectPayment);
        selectPayment.setOnItemSelectedListener(this);

        tableNumTitle();
        paymentSpinner();
        listView();
    }

    //Fills the spinner with the payment methods
    public void paymentSpinner(){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cards = new ArrayList<String>();

                for (DataSnapshot cardsSnapshot: dataSnapshot.child("payment").getChildren()) {
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
                //...
            }
        });
    }

    //Get the table number and the restaurant name from Firebase
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

    //Fills out the listView with the items in the Bill
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
                //...
            }
        });
    }

    //Button pressed handler
    @Override
    public void onClick(View view) {
        if(view == confirmPay){
            progressDialog.setMessage("Processing...");
            progressDialog.show();
            final RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            simplify = new Simplify();
            simplify.setApiKey(publicAPIkey);

            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String number = dataSnapshot.child("payment").child(Integer.toString(cardSelected)).child("number").getValue(String.class);
                    String exp_month = dataSnapshot.child("payment").child(Integer.toString(cardSelected)).child("exp_month").getValue(String.class);
                    String exp_year = dataSnapshot.child("payment").child(Integer.toString(cardSelected)).child("exp_year").getValue(String.class);
                    String CVC = dataSnapshot.child("payment").child(Integer.toString(cardSelected)).child("CVV").getValue(String.class);
                    String zipcode = dataSnapshot.child("payment").child(Integer.toString(cardSelected)).child("zipcode").getValue(String.class);
                    final String payID = dataSnapshot.child("id").getValue(String.class);

                    myCard = new Card()
                            .setNumber(number.replace(" ",""))
                            .setExpMonth(exp_month)
                            .setExpYear(exp_year.substring(exp_year.length() - 2))
                            .setCvc(CVC)
                            .setAddressZip(zipcode);

                    paymentStatus = number.replace(" ","") + "|" + exp_month+ "|" + exp_year+ "|" + CVC+ "|" + zipcode;

                    // tokenize the card
                    simplify.createCardToken(myCard, new CardToken.Callback() {
                        @Override
                        public void onSuccess(final CardToken cardToken) {
                            try {
                                String url = "http://ezservepayment.herokuapp.com/charge.php";
                                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.cancel();
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            Toast.makeText(PayForItems.this, response, Toast.LENGTH_LONG).show();
                                            if (jsonObject.get("status").toString().equals("APPROVED")) {
                                                addToHistory();
                                                startActivity(new Intent(PayForItems.this, CustomerMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                //TODO: clear table and add it to customer's bills
                                            }
                                        } catch (JSONException e) {
                                            //For debugging
                                            //Toast.makeText(PayForItems.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //This code is executed if there is an error.
                                    }
                                }) {
                                    protected Map<String, String> getParams() {
                                        Map<String, String> MyData = new HashMap<String, String>();
                                        MyData.put("simplifyToken", cardToken.getId()); //Send the card token with the request
                                        MyData.put("amount", String.valueOf(total*100)); //send the amount in cents
                                        MyData.put("customer", payID); //send the customer id
                                        return MyData;
                                    }
                                };

                                MyRequestQueue.add(MyStringRequest);
                            } catch (Exception e) {
                                Toast.makeText(PayForItems.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onError(Throwable throwable) {
                            Toast.makeText(PayForItems.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    paymentStatus = "Error 001";
                }
            });




        }
    }

    public void addToHistory(){
        Random rand = new Random();
        int num = rand.nextInt(90000) + 1000000;
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        final LocalDate localDate = LocalDate.now();
        String numS = String.valueOf(num);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        resRef = FirebaseDatabase.getInstance().getReference();
        billHRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Bills").child(numS);
        resRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String restaurant = dataSnapshot.child("Connection").child(restaurantChild)
                        .child("Restaurant").getValue(String.class);
                final Double total = dataSnapshot.child("Connection").child(restaurantChild)
                        .child(tableChild).child("Total").getValue(Double.class);

                Map newInfo = new HashMap();
                newInfo.put("restaurant",restaurant);
                newInfo.put("total", String.valueOf(df.format(total)));
                newInfo.put("date", String.valueOf(dtf.format(localDate)));

                billHRef.setValue(newInfo);

                resRef.child("Connection").child(restaurantChild).child(tableChild).child("Items").removeValue();
                resRef.child("Connection").child(restaurantChild).child(tableChild).child("Connected Users").removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Manages the card index when the Spinner changes
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cardSelected = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

}
