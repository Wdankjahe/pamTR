package com.example.nfstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class myitems extends AppCompatActivity {

    String user;
    TextView userGreet,walletBalance;
    Button backButton;
    ListView historyList;
    private DatabaseReference mFirebaseDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myitems);
        user = getIntent().getStringExtra("User");
        userGreet = findViewById(R.id.usergreet);
        walletBalance = findViewById(R.id.walletbalance);
        backButton = findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        historyList = findViewById(R.id.items);
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstance.getReference("User");
        mFirebaseInstance.getReference("app_title").setValue("NFsTore");

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
        mFirebaseDB.child("Users").orderByChild("name").equalTo(user).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            Log.w("Data : ", "Exists");
                            HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();

                            for (String key : dataMap.keySet()) {

                                Object data = dataMap.get(key);

                                try {
                                    HashMap<String, Object> userData = (HashMap<String, Object>) data;
                                    //userData.get("name").toString()
                                    userGreet.setText(userData.get("name").toString());
                                    walletBalance.setText(userData.get("wallet").toString());
                                    List<String> historyString = (List<String>) userData.get("items");
                                    if (historyString ==null)
                                    {
                                        historyString = new ArrayList<>();
                                        historyString.add("Such empty space");
                                    }
                                    ArrayAdapter arDapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.historyitem,R.id.historystring,historyString);
                                    historyList.setAdapter(arDapter);


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
}