package com.example.codeseasy.com.firebaseauth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;


import java.util.Locale;
public class ItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Find views by ID
        ImageView itemImage = findViewById(R.id.item_detail_image);
        TextView itemTitle = findViewById(R.id.item_detail_title);
        TextView itemDescription = findViewById(R.id.item_detail_description);
        TextView itemPrice = findViewById(R.id.item_detail_price);
        Button addToCartButton = findViewById(R.id.btn_add_to_cart);
        ImageView closeButton = findViewById(R.id.btn_close);

        // Get the item from the intent
        Item item = (Item) getIntent().getSerializableExtra("item");

        // Set the views with the item data
        Glide.with(this)
                .load(item.getImageUrls().get(0))
                .placeholder(R.drawable.rounded_placeholder_image)
                .transform(new RoundedCorners(10))
                .fitCenter()
                .into(itemImage);
        itemTitle.setText(item.getTitle());
        itemDescription.setText(item.getDescription());
        itemPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice()));

        // Set the click listener for the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the activity
                finish();
            }
        });

        // Set the click listener for the add to cart button
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement add to cart functionality
            }
        });
    }
}
