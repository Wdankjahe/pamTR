package com.example.nfstore;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;


@IgnoreExtraProperties
public class User {
    private String userName;
    private String password;
    private float wallet;
    private List<String> items;
    public User()
    {}
    public User(String userName, float wallet, List<String> items, String password)
    {
        this.userName = userName;
        this.wallet = wallet;
        this.password = password;
        this.items = items;
    }

    public String getName() {return userName;}
    public float getWallet() {return wallet;}
    public String getPassword(){return password;}
    public List<String> getItems(){return items;}

}
