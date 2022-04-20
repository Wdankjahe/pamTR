package com.example.nfstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Catalog extends AppCompatActivity {
    private DatabaseReference mFirebaseDB,firebaseUser;
    String user;

    GridView gridView;
    Button logoutB, itemsB;
    TextView userW,wallet;
    String userKey;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        gridView = findViewById(R.id.gridy);
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstance.getReference("Items");
        firebaseUser = mFirebaseInstance.getReference("User");
        user = getIntent().getStringExtra("User");
        wallet = findViewById(R.id.yourbalance);
        setPersonalInfo(wallet,user);
        userW = findViewById(R.id.welcome);
        userW.setText("Hi " + user + "!");
        logoutB = findViewById(R.id.logoutbutton);
        itemsB = findViewById(R.id.youritembutton);

        logoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        itemsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Catalog.this, myitems.class);
                i.putExtra("User",user);
                startActivity(i);

            }
        });
        //kode buat data dummy
        /*List<String> history = new ArrayList<String>();
        history.add("10-04-2022");
        history.add("CatLady");
       // history.add("18-02-2022");
        //history.add("Kukang");
       // history.add("18-04-2022");
       // history.add("Udang");

        Item item = new Item("Kocheng",720,
                "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.N8EwSZlfSY6jardurn1rFAHaEK%26pid%3DApi&f=1"
                ,history);
        mFirebaseDB.child("Items").child(mFirebaseDB.push().getKey()).setValue(item);*/
        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String appTitle = dataSnapshot.getValue(String.class);

                // update toolbar title
                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });



        mFirebaseDB.child("Items").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {



                            HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();
                            if (dataMap!=null)
                            {
                                ArrayList<Item> modelList = new ArrayList<Item>();
                                for (String key : dataMap.keySet()) {

                                    Object data = dataMap.get(key);

                                    try {
                                        HashMap<String, Object> userData = (HashMap<String, Object>) data;

                                        modelList.add(new Item(userData.get("name").toString(),Float.valueOf(userData.get("price").toString()),userData.get("imgLink").toString(),(List<String>)userData.get("history")));


//                                        Log.w("Hasil : ", userData.get("name").toString() + " " + userData.get("password").toString());


                                    } catch (ClassCastException cce) {

// If the object can’t be casted into HashMap, it means that it is of type String.

                                        try {

                                            String mString = String.valueOf(dataMap.get(key));
                                            Log.w("Value Lain", mString);

                                        } catch (ClassCastException cce2) {

                                        }
                                    }
                                }
                                SpecialAdapter adapter = new SpecialAdapter(getApplicationContext(),0,modelList);
                                gridView.setAdapter(adapter);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        Item string = (Item)adapterView.getItemAtPosition(i);
                                        //intent putextra itemname;
                                        Intent intent = new Intent(Catalog.this,Itemdetail.class);
                                        intent.putExtra("itemname",string.getName());
                                        intent.putExtra("User",user);
                                        startActivity(intent);
                                        Log.w( "onItemClick: ", string.getName());
                                    }
                                });
                            }else
                            {

                            }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("Data : ", "Failed" );
                    }
                }
        );


    }

    void setPersonalInfo(TextView myview,String myUser)
    {
        firebaseUser.child("Users").orderByChild("name").equalTo(myUser).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            Log.w("Data info : ", "Exists");
                            HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();

                            for (String key : dataMap.keySet()) {

                                Object data = dataMap.get(key);
                                userKey = key;

                                try {
                                    HashMap<String, Object> userData = (HashMap<String, Object>) data;
                                    myview.setText("Your Balance : " + userData.get("wallet").toString());
                                } catch (ClassCastException cce) {
                                }
                            }

                        }else
                        {
                            Log.w("Data : ", "No Exist" );
                            //mFirebaseDB.child("Users").child(mFirebaseDB.push().getKey()).setValue(user);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("Data : ", "Failed" );
                    }
                }
        );




    }

    @Override
    protected void onResume() {
        super.onResume();
        setPersonalInfo(wallet,user);
    }
}