package com.rohail.apps.nearme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.rohail.apps.nearme.R;


public class SplashActivity extends AppCompatActivity {

    private Animation myAnimation;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvTitle = (TextView) findViewById(R.id.tv_title);

        myAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);

        tvTitle.setAnimation(myAnimation);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashActivity.this, MapActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}
