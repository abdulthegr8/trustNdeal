package com.example.codeseasy.com.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
