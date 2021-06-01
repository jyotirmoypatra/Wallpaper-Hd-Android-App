package com.jyotirmoy.wallpaperhd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FullScreenWallpaper extends AppCompatActivity {


    private static final String TAG ="AdMob" ;
    String originalUrl = "";
    PhotoView photoView;
    BottomNavigationView bottomNavigationView;
    String selectWallpaper = "Set Home Screen";
    ProgressDialog dialog;
    ProgressDialog p;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wallpaper);

        getSupportActionBar().hide();

        loadInterstitialAd();



        dialog = new ProgressDialog(FullScreenWallpaper.this);
        dialog.setMessage("Loading HD Image...");
        dialog.setCancelable(false);
        dialog.show();





        Intent intent = getIntent();
        originalUrl = intent.getStringExtra("originalUrl");

        photoView = findViewById(R.id.photoView);

        Glide.with(this).load(originalUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        dialog.dismiss();
                        Toast.makeText(FullScreenWallpaper.this, "Failed To Load HD Image", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        dialog.dismiss();
                        return false;
                    }
                }).into(photoView);


        bottomNavigationView = findViewById(R.id.bottomBar);

        bottomNavigationView.getMenu().findItem(R.id.download).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(originalUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                try{
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadManager.enqueue(request);
                    Toast.makeText(FullScreenWallpaper.this, "Download Started", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(FullScreenWallpaper.this, "Download Failed!!!", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        bottomNavigationView.getMenu().findItem(R.id.home).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(FullScreenWallpaper.this, MainActivity.class));
                finish();
                return true;
            }
        });


        bottomNavigationView.getMenu().findItem(R.id.setWallpaper).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                showSetWallpaperOptions();

                return true;
            }



            private void showSetWallpaperOptions() {

                p=new ProgressDialog(FullScreenWallpaper.this);
                p.setMessage("setting Wallpaper..");
                dialog.setCancelable(false);

                final String[] wallpaperOption = {"Set Home Screen", "Set Lock Screen", "Set Both"};
                AlertDialog.Builder builder = new AlertDialog.Builder(FullScreenWallpaper.this);
                builder.setTitle("Choose Options");
                builder.setSingleChoiceItems(wallpaperOption, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectWallpaper = wallpaperOption[which];

                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        p.show();
                        if (selectWallpaper == wallpaperOption[0]) {

                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullScreenWallpaper.this);
                            Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                            try {


                                final int i = wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                                Toast.makeText(FullScreenWallpaper.this, "Set Home Wallpaper Successfully", Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();

                            }

                        }
                        if (selectWallpaper == wallpaperOption[1]) {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullScreenWallpaper.this);
                            Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                            try {

                                final int i = wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                Toast.makeText(FullScreenWallpaper.this, "Set Lock Wallpaper Successfully", Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();

                            }

                        }
                        if (selectWallpaper == wallpaperOption[2]) {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullScreenWallpaper.this);
                            Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                            try {

                                wallpaperManager.setBitmap(bitmap);
                                Toast.makeText(FullScreenWallpaper.this, "Set Wallpaper Successfully", Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        }
                        p.dismiss();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });


    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent( AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

    @Override
    public void finish() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(FullScreenWallpaper.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
        super.finish();

    }
}
