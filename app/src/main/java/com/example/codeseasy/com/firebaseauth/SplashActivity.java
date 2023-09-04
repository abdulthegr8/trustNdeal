package com.example.codeseasy.com.firebaseauth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawableResource(R.color.grey_background);
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_splash);

        mVideoView = findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.trustanime;
        mVideoView.setVideoURI(Uri.parse(videoPath));
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());
        mVideoView.start();

        int SPLASH_DURATION = 2900; // 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, Login.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private class MyOnCompletionListener implements android.media.MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(android.media.MediaPlayer mp) {
            mVideoView.start();
        }
    }
}
