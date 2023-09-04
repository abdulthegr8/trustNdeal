package com.example.codeseasy.com.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.core.view.View;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        // Display instructions
        TextView instructionsTextView = findViewById(R.id.instructions_text_view);
        instructionsTextView.setText(getString(R.string.instructions_text));
    }

    // Handle close button click
    public void onCloseButtonClick(View view) {
        finish();
    }
}
