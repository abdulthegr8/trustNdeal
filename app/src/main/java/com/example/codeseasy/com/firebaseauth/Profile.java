package com.example.codeseasy.com.firebaseauth;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.UploadTask;

public class Profile extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private ImageView userImage;
    private TextView username;
    private RatingBar ratingBar;

    private String userId;
    private String usernameStr;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        userImage = findViewById(R.id.user_image);
        username = findViewById(R.id.username);
        ratingBar = findViewById(R.id.rating);
        Button btnMap = findViewById(R.id.btn_map);
        Button btnHome = findViewById(R.id.btn_home);
        Button btnProfile = findViewById(R.id.btn_profile);
        Button logout = findViewById(R.id.logout);

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        btnMap.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Map.class);
            startActivity(intent);
        });

        btnHome.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(view -> {
            recreate();
        });



        userId = currentUser.getUid();

        getUserData();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("Enter Username");

                final EditText input = new EditText(Profile.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUsername = input.getText().toString().trim();
                        if (newUsername.isEmpty()) {
                            Toast.makeText(Profile.this, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                        } else {
                            mDatabase.child("users").child(userId).child("username").setValue(newUsername);
                            username.setText(newUsername);
                            usernameStr = newUsername;
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    mDatabase.child("users").child(userId).child("rating").setValue(rating);
                    updateAvgRating();
                }
            }
        });
    }

    private void getUserData() {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usernameStr = dataSnapshot.child("username").getValue(String.class);
                    imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    Float rating = dataSnapshot.child("rating").getValue(Float.class);

                    if (usernameStr != null) {
                        username.setText(usernameStr);
                    }

                    if (imageUrl != null) {
                        Glide.with(Profile.this).load(imageUrl).into(userImage);
                    }

                    if (rating != null && rating != 0.0f) {
                        ratingBar.setRating(rating);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateAvgRating() {
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int numRatings = 0;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Float rating = userSnapshot.child("rating").getValue(Float.class);
                    if (rating != null) {
                        totalRating += rating;
                        numRatings++;
                    }
                }

                if (numRatings > 0) {
                    float avgRating = totalRating / numRatings;
                    mDatabase.child("avgRating").setValue(avgRating);
                } else {
                    mDatabase.child("avgRating").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference fileRef = storageRef.child("images/" + userId + ".jpg");

            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = uri.toString();
                            mDatabase.child("users").child(userId).child("imageUrl").setValue(imageUrl);
                            Glide.with(Profile.this).load(imageUrl).into(userImage);
                        }
                    });
                }
            });
        }
    }
}
