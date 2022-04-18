package com.example.nfstore;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Item {
    private String name;//kepake di pilih
    private float price;//sama dengan atas
    private String imgLink;//dipake buat gambarnya
    private List<String> history;

    public Item()
    {}
    public Item(String name, float price, String imgLink, List<String>  history)
    {
        this.name = name;
        this.price = price;
        this.imgLink = imgLink;
        this.history = history;
    }

    public String getName() {return name;}
    public float getPrice() {return price;}
    public List<String>  getHistory(){return history;}
    public String getImgLink() { return imgLink;}

}
