package com.example.nfstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private TextView register;
    private Button loginBtn;
    private EditText userInput,passwordInput;
    private DatabaseReference mFirebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.registerTxt);
        loginBtn = findViewById(R.id.loginBtn);

        userInput = findViewById(R.id.editTextTextPersonName);
        passwordInput = findViewById(R.id.editTextTextPassword);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstance.getReference("User");
        mFirebaseInstance.getReference("app_title").setValue("NFsTore");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lompat ke Register
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cek User
                Login(userInput.getText().toString(),passwordInput.getText().toString());
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
                                            Intent i = new Intent(LoginActivity.this, BerandaActivity.class);
                                            i.putExtra("User",userName);
                                            i.putExtra("address", key);
                                            Log.w("INI COBA GET KEY", key);
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
                                //mFirebaseDB.child("Users").child(mFirebaseDB.push().getKey()).setValue(userEric);
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