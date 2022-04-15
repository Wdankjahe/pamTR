package com.example.nfstore;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class Item {
    private String name;
    private float price;
    private String imgLink;
    private Map<Date , String> history;

    public Item()
    {}
    public Item(String name, float price, String imgLink, Map<Date , String>  history)
    {
        this.name = name;
        this.price = price;
        this.imgLink = imgLink;
        this.history = history;
    }

    public String getName() {return name;}
    public float getPrice() {return price;}
    public Map<Date , String>  getHistory(){return history;}


}
