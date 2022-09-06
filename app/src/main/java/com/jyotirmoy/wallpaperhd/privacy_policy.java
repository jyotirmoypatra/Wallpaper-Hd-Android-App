package com.jyotirmoy.wallpaperhd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class privacy_policy extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        Intent in = getIntent();
          String i= in.getStringExtra("id");

        webView=findViewById(R.id.webView);

        if(i.equals("privacy")){
            webView.loadUrl("file:///android_asset/privacy.html");
          }else{
            webView.loadUrl("file:///android_asset/terms.html");
          }

    }
}