package com.example.istiaque.bustracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class openingactivity extends AppCompatActivity {

    private Button driver,passanger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openingactivity);

        driver = (Button) findViewById(R.id.driver);
        passanger = (Button) findViewById(R.id.passanger);

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(openingactivity.this,login.class));
            }
        });

        passanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(openingactivity.this,passanger.class));
            }
        });
    }
}
