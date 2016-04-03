package com.directions.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash_screen extends Activity {

    ImageView slashImg;
    Animation zoomFade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zoomFade = AnimationUtils.loadAnimation(this,R.anim.animationfile);
        setContentView(R.layout.activity_splash__screen);


        findViewById(R.id.splash_image).startAnimation(zoomFade);

        Thread splashScreen = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(5000);
                    }
                } catch (Exception e) {
                } finally {

                    Intent startpt = new Intent(getApplicationContext(), ActivityChooser.class);
                    startActivity(startpt);
                    finish();

                }
            }
        };
        splashScreen.start();


    }
}

