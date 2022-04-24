package com.example.nfstore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String user;
    private View view;
    TextView walletBalance;
    ListView historyList;
    private DatabaseReference mFirebaseDB;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AssetFragment() {
        // Required empty public constructor
    }

    public AssetFragment(String user){
        this.user = user;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AssetFragment newInstance(String param1, String param2) {
        AssetFragment fragment = new AssetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_asset, container, false);
        walletBalance = view.findViewById(R.id.walletbalancee);
        historyList = view.findViewById(R.id.itemss);
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstance.getReference("User");
        mFirebaseInstance.getReference("app_title").setValue("NFsTore");
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
                                    Log.w("Data : ", "INI TRY");
                                    HashMap<String, Object> userData = (HashMap<String, Object>) data;
                                    walletBalance.setText(userData.get("wallet").toString());

                                    List<String> historyString = (List<String>) userData.get("items");
                                    if (historyString ==null)
                                    {
                                        historyString = new ArrayList<>();
                                        historyString.add("Such empty space");
                                    }
                                    ArrayAdapter arDapter = new ArrayAdapter<String>(getContext(),R.layout.historyitem,R.id.historystring,historyString);
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
        return view;
    }
}