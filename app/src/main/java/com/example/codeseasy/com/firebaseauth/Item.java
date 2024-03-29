package com.example.codeseasy.com.firebaseauth;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {
    private String itemId;
    private String title;
    private String description;
    private String category;
    private double price;
    private List<String> imageUrls;
    private String username;
    private String userProfileImageUrl;

    public Item(String itemId, String title, String description, double price, List<String> imageUrls, String username, String userProfileImageUrl) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
        this.username = username;
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public Item() {
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
