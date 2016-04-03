package com.directions.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivityChooser extends AppCompatActivity {

    Button single,multy;
    long lastPress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_chooser);

        single = (Button)findViewById(R.id.singleMap);
        multy = (Button)findViewById(R.id.multyMap);

        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sMap = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(sMap);
            }
        });

        multy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mMap = new Intent(getApplicationContext(),ShowMap.class);
                Intent mMap = new Intent(getApplicationContext(),MainActivity_ShowMap.class);

                startActivity(mMap);
            }
        });

    }

    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();
        if(currentTime - lastPress > 5000){
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        }else{
            super.onBackPressed();
        }

    }

}
