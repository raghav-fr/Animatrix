package com.example.animatrix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animatrix.R;

public class SplashActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splsh);
        ImageView splashLogo = findViewById(R.id.splash_logo);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        splashLogo.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            splashLogo.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }, 2000);
    }
}
