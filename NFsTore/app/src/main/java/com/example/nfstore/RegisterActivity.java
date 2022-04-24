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

public class RegisterActivity extends AppCompatActivity {

    private TextView login;
    private EditText userInput,passwordInput;
    private DatabaseReference mFirebaseDB;
    private Button createB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = findViewById(R.id.loginTxt);
        createB = findViewById(R.id.button);

        userInput = findViewById(R.id.usernameTxt);
        passwordInput = findViewById(R.id.passwordTxt2);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstance.getReference("User");
        mFirebaseInstance.getReference("app_title").setValue("NFsTore");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        createB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(userInput.getText().toString(),passwordInput.getText().toString());
            }
        });
    }

    private void createAccount(String username, String password)
    {
        if (!username.isEmpty() || !password.isEmpty())
        {
            User userEric = new User(username,900,null,password);
            boolean correct = false;
            mFirebaseDB.child("Users").orderByChild("name").equalTo(username).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists())
                            {
                                mFirebaseDB.child("Users").child(mFirebaseDB.push().getKey()).setValue(userEric);
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