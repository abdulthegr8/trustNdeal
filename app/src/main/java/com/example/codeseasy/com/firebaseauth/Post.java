package com.example.codeseasy.com.firebaseauth;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Post extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private ImageView imageView;
    private Button submitButton;
    private Button buttonAddImage;
    private Uri imageUri;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private String userId;
    private String username;
    private String userProfileImageUrl;


    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            // Redirect the user to the login activity if they're not logged in
            Intent intent = new Intent(Post.this, Login.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);
        priceEditText = findViewById(R.id.editTextPrice);
        imageView = findViewById(R.id.imageView);
        submitButton = findViewById(R.id.buttonSubmit);
        buttonAddImage = findViewById(R.id.buttonAddImage);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");
        databaseReference = FirebaseDatabase.getInstance().getReference("items");

        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Post.this);
                builder.setTitle("Choose an option");

                final String[] options = {"Take photo", "Choose from gallery"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Take photo option selected
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                                break;
                            case 1:
                                // Choose from gallery option selected
                                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
                                break;
                        }
                    }
                });

                builder.show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String priceString = priceEditText.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    titleEditText.setError("Title is required.");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    descriptionEditText.setError("Description is required.");
                    return;
                }

                if (TextUtils.isEmpty(priceString)) {
                    priceEditText.setError("Price is required.");
                    return;
                }

                double price = Double.parseDouble(priceString);

                if (imageUris.isEmpty()) {
                    Toast.makeText(Post.this, "Please select at least one image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(Post.this);
                progressDialog.setTitle("Uploading images...");
                progressDialog.show();

                List<String> imageUrls = new ArrayList<>();
                AtomicInteger uploadedImages = new AtomicInteger(0);

                for (Uri imageUri : imageUris) {
                    StorageReference imageReference = storageReference.child(imageUri.getLastPathSegment());
                    imageReference.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    uploadedImages.incrementAndGet();
                                    imageReference.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    imageUrls.add(uri.toString());

                                                    if (uploadedImages.get() == imageUris.size()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(Post.this, "Images uploaded successfully.", Toast.LENGTH_SHORT).show();

                                                        String itemId = databaseReference.push().getKey();
                                                        Item item = new Item(itemId, title, description, price, imageUrls, username, userProfileImageUrl);
                                                        databaseReference.child(itemId).setValue(item)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(Post.this, "Item added successfully.", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(Post.this, MainActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(Post.this, "Failed to add item.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Post.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            Uri imageUri = getImageUri(Post.this, imageBitmap);
            imageUris.add(imageUri);
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
            imageUris.add(imageUri);
        }
    }

    private Uri getImageUri(Post context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
}
