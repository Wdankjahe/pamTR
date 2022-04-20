package com.example.nfstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
/*
Cara pakai adapter baru (contoh ) :
gridView = findViewById(R.id.gridTest);
        ArrayList<model> modelList = new ArrayList<model>();
        modelList.add(new model("Test1","https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.jtzLV8nbTiaPVkbonKgPJAHaDk%26pid%3DApi&f=1"));

        MyAdapter adapter = new MyAdapter(this,0,modelList);
        gridView.setAdapter(adapter);
 */


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
        createB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(userInput.getText().toString(),passwordInput.getText().toString());
            }
        });

    }

    private void Login(String userName, String Password)
    {
        if (!userName.isEmpty() || !Password.isEmpty())
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
                                        if (userData.get("name").toString().equals(userName) &&userData.get("password").toString().equals(Password) )
                                        {
                                            Log.w("Hasil : Login","Berhasil");
                                            Intent i = new Intent(MainActivity.this, Catalog.class);
                                            i.putExtra("User",userName);
                                            startActivity(i);
                                        }else
                                        {
                                            Log.w("Hasil : Login","Gagal");
                                        }



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

    private void createAccount(String username, String password)
    {
        if (!username.isEmpty() || !password.isEmpty())
        {
            User user = new User(username,900,null,password);
            boolean correct = false;
            mFirebaseDB.child("Users").orderByChild("name").equalTo(username).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (!snapshot.exists())
                            {
                                mFirebaseDB.child("Users").child(mFirebaseDB.push().getKey()).setValue(user);


                            }else
                            {
                                Log.w("Data : ", "Exist" );
                                //
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


}