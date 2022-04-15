package com.example.nfstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText userInput,passwordInput;
    private Button loginB,createB;
    private DatabaseReference mFirebaseDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = findViewById(R.id.insertUser);
        passwordInput = findViewById(R.id.insertPassword);
        loginB = findViewById(R.id.loginB);
        createB = findViewById(R.id.createUserB);

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
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Login(userInput.getText().toString(),passwordInput.getText().toString());

            }
        });

    }

    private void Login(String userName, String Password)
    {
        User user = new User(userName,0,null,Password);
        boolean correct = false;
        mFirebaseDB.child("Users").orderByChild("name").equalTo(userName).addListenerForSingleValueEvent(
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

                                        Log.w("Hasil : ", userData.get("name").toString() + " " + userData.get("password").toString());


                                    } catch (ClassCastException cce) {

// If the object can’t be casted into HashMap, it means that it is of type String.

                                        try {

                                            String mString = String.valueOf(dataMap.get(key));
                                            Log.w("Value Lain", mString);

                                        } catch (ClassCastException cce2) {

                                        }
                                    }
                                }

                            }else
                            {
                                Log.w("Data : ", "No Exist" );
                                mFirebaseDB.child("Users").child(mFirebaseDB.push().getKey()).setValue(user);
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