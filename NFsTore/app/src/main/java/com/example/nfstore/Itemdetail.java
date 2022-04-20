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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Itemdetail extends AppCompatActivity {
    private DatabaseReference mFirebaseDB,mFirebaseUser;
    TextView textView,textprice,usersbalanceview;
    String itemIs="";
    ImageView itemImage;
    Button backButton,buyButton;
    String user;
    ListView historyList;
    float usersBalance,itemsPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetail);
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        user = getIntent().getStringExtra("User");
        Log.w("User",user );
        usersbalanceview = findViewById(R.id.yourmoney);

        historyList = findViewById(R.id.historylist);
        mFirebaseDB = mFirebaseInstance.getReference("Items");
        mFirebaseUser = mFirebaseInstance.getReference("User");

        checkBalance(user);
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
        textView = findViewById(R.id.itemname);
        textprice = findViewById(R.id.pricing);
        itemImage = findViewById(R.id.imageView2);
        backButton = findViewById(R.id.button);
        buyButton = findViewById(R.id.buybutton);
        Intent intent = getIntent();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String itemName = intent.getStringExtra("itemname");
        refreshData(itemName);


        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseUser.child("Users").orderByChild("name").equalTo(user).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                //if (snapshot.exists())
                                //{
                                if (usersBalance > itemsPrice)
                                {
                                    HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();

                                    for (String key : dataMap.keySet()) {

                                        Object data = dataMap.get(key);

                                        try {
                                            HashMap<String, Object> userData = (HashMap<String, Object>) data;

                                            List<String> myItems = (List<String>) userData.get("items");
                                            if (myItems==null)
                                            {
                                                myItems = new ArrayList<String>();
                                            }
                                            //List<String>
                                            if (!myItems.contains(itemIs))
                                            {
                                                myItems.add(itemIs);
                                                addDataToItemHistory(itemIs);
                                                usersBalance-=itemsPrice;
                                                mFirebaseUser.child("Users").child(key).child("wallet").setValue(usersBalance);
                                                mFirebaseUser.child("Users").child(key).child("items").setValue(myItems);
                                                buyButton.setEnabled(false);
                                            }






                                        } catch (ClassCastException cce) {
                                        }
                                    }

                                    checkBalance(user);
                                }else
                                {
                                    //action kalau bokek
                                }
                                    Log.w("Data : ", "Exists button");


                                //}

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.w("Data : ", "Failed" );
                            }
                        }
                );

            }
        });


    }
    private void refreshData(String itemName)
    {
        mFirebaseDB.child("Items").orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(
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

                                    Log.w("Item diterima : ", userData.get("name").toString() );

                                    itemIs = userData.get("name").toString();
                                    itemsPrice = Float.valueOf(userData.get("price").toString());

                                    textView.setText(itemIs);
                                    textprice.setText("Item Price : "+  userData.get("price").toString());
                                    new DownloadImageFromInternet(itemImage).
                                            execute( userData.get("imgLink").toString());
                                    List<String> historyData = (ArrayList)userData.get("history");
                                    if (historyData !=null)
                                    {
                                        List<String> historyString = new ArrayList<>();
                                        for (int i =0;i<historyData.size()-1;i+=2)
                                        {
                                            historyString.add(historyData.get(i)+"-by : " + historyData.get(i+1));
                                        }
                                        ArrayAdapter arDapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.historyitem,R.id.historystring,historyString);
                                        historyList.setAdapter(arDapter);
                                    }

                                    //check last owner here. write owner, if owner is equal to user -> button change to disable
                                    if (user.equals(historyData.get(historyData.size()-1)))
                                    {
                                        buyButton.setText("You Own This!");
                                        buyButton.setEnabled(false);
                                    }


                                } catch (ClassCastException cce) {
                                }
                            }

                        }
                        else
                        {
                            Log.w("Data ","No exist" );
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("Data : ", "Failed" );
                    }
                }
        );
    }
    private void changeOwner(String username, String itemname)
    {
        mFirebaseUser.child("Users").orderByChild("name").equalTo(username).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //if (snapshot.exists())
                        //{
                        Log.w("Data : ", "Exists changeowner");
                        HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();

                        for (String key : dataMap.keySet()) {

                            Object data = dataMap.get(key);

                            try {
                                HashMap<String, Object> userData = (HashMap<String, Object>) data;

                                List<String> myItems = (List<String>) userData.get("items");
                                for (int i=0;i<myItems.size();i++)
                                {
                                    if (myItems.get(i).equals(itemname))
                                    {

                                        myItems.set(i,"(Was an owner)"+itemname);
                                        mFirebaseUser.child("Users").child(key).child("items").setValue(myItems);
                                        break;
                                    }
                                }







                            } catch (ClassCastException cce) {
                            }
                        }

                        //}

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("Data : ", "Failed" );
                    }
                }
        );
    }


    private void checkBalance(String username)
    {
        mFirebaseUser.child("Users").orderByChild("name").equalTo(username).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //if (snapshot.exists())
                        //{
                        Log.w("Data : ", "Exists changeowner");
                        HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();

                        for (String key : dataMap.keySet()) {

                            Object data = dataMap.get(key);

                            try {
                                HashMap<String, Object> userData = (HashMap<String, Object>) data;

                                usersBalance = Float.valueOf(userData.get("wallet").toString());
                                usersbalanceview.setText("Your Balance : "+ usersBalance);

                            } catch (ClassCastException cce) {
                            }
                        }
                        //}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("Data : ", "Failed" );
                    }
                }
        );
    }

    private void addDataToItemHistory(String itemName)
    {
        mFirebaseDB.child("Items").orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            Log.w("Data : ", "Exists addtohistory");
                            HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();

                            for (String key : dataMap.keySet()) {

                                Object data = dataMap.get(key);

                                try {
                                    HashMap<String, Object> userData = (HashMap<String, Object>) data;

                                    List<String> history = (List<String>) userData.get("history");
                                    if (history==null)
                                    {
                                        history = new ArrayList<String>();
                                    }
                                    if (!history.isEmpty())
                                    {
                                        changeOwner(history.get(history.size()-1),itemName);
                                        Log.w("Cek: ", history.get(history.size()-1) + " " + itemName);
                                    }
                                    Date c = Calendar.getInstance().getTime();


                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    String formattedDate = df.format(c);
                                    //List<String> myItems = new ArrayList<String>();
                                    history.add(formattedDate);
                                    history.add(user);
                                        // myItems.add(itemIs);
                                    mFirebaseDB.child("Items").child(key).child("history").setValue(history);



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

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;

        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage= BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}