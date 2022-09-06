package com.jyotirmoy.wallpaperhd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class appInfo extends AppCompatActivity {

    TextView privacy,terms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo);
        getSupportActionBar().hide();


        privacy=findViewById(R.id.privacyPolicy);
        terms=findViewById(R.id.termsCondition);

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(appInfo.this,privacy_policy.class);
                String id="privacy";
                i.putExtra("id",id);
                startActivity(i);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(appInfo.this,privacy_policy.class);
                String id="terms";
                i.putExtra("id",id);
                startActivity(i);
            }
        });

    }
}