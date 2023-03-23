package com.example.codeseasy.com.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnProfile = findViewById(R.id.btn_profile);
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        Button btnHome = findViewById(R.id.btn_home);
        progressBar = findViewById(R.id.progressBar);
        Button btnMap = findViewById(R.id.btn_map);
        Button btnPost = findViewById(R.id.btn_post);

        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        btnMap.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Map.class);
            startActivity(intent);
        });

        btnPost.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Post.class);
            startActivity(intent);
        });

        button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Profile.class);
            startActivity(intent);
        });

        btnHome.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            EditText searchBar = findViewById(R.id.search_bar);
            searchBar.setText("");
            recreate();
        });

        loadItems();

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    private void loadItems() {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("items");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Item> itemList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    itemList.add(item);
                }

                ListView listView = findViewById(R.id.item_list);
                ItemAdapter itemAdapter = new ItemAdapter(MainActivity.this, itemList);
                listView.setAdapter(itemAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
