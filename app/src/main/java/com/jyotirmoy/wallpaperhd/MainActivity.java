package com.jyotirmoy.wallpaperhd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    adapterWallpaper adapterWallpaper;
    List<WallpaperModel> wallpaperModelList;
    int pageNumber = 1;
    ProgressDialog dialog;
    Boolean isScroll = false;
    int currentItem, totalItem, scrollOutItems;
    String url="https://api.pexels.com/v1/curated/?page=" + pageNumber + "&per_page=80";
    Boolean search=false;
    CardView searchIcon;
    TextView appName;
    EditText searchText;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                super.onAdLoaded();
                Toast.makeText(MainActivity.this,"Ad loader",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                super.onAdOpened();
                Toast.makeText(MainActivity.this,"Ad open",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                super.onAdClicked();

                Toast.makeText(MainActivity.this,"Ad click",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
super.onAdClosed();
                Toast.makeText(MainActivity.this,"Ad closed",Toast.LENGTH_SHORT).show();
            }
        });





        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);


        recyclerView = findViewById(R.id.recyclerView);
        wallpaperModelList = new ArrayList<>();
        adapterWallpaper = new adapterWallpaper(this, wallpaperModelList);


        recyclerView.setAdapter(adapterWallpaper);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);




        if(!search){
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScroll = true;
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    currentItem = gridLayoutManager.getChildCount();
                    totalItem = gridLayoutManager.getItemCount();
                    scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (isScroll && (currentItem + scrollOutItems == totalItem)) {
                        isScroll = false;
                        dialog.show();
                        url="https://api.pexels.com/v1/curated/?page=" + pageNumber + "&per_page=80";
                        fetchWallpaper();
                    }
                }
            });
        }

        dialog.show();
        fetchWallpaper();


        searchIcon=findViewById(R.id.searchIcon);
        searchText=findViewById(R.id.searchBox);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search=true;
               String searchQuery=searchText.getText().toString().toLowerCase();

                searchString(searchQuery);
                closeKeyboard();

                if(searchQuery.isEmpty()){
                    Toast.makeText(MainActivity.this,"Enter your search query!!",Toast.LENGTH_SHORT).show();
                }else {
                    pageNumber=1;

                    url="https://api.pexels.com/v1/search/?page="+pageNumber+"&per_page=80&query="+searchQuery;
                    wallpaperModelList.clear();
                    dialog.show();
                    fetchWallpaper();
                }

            }

            private void searchString(String searchQuery) {

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            isScroll = true;
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        currentItem = gridLayoutManager.getChildCount();
                        totalItem = gridLayoutManager.getItemCount();
                        scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                        if (isScroll && (currentItem + scrollOutItems == totalItem)) {
                            isScroll = false;
                            dialog.show();

                            url="https://api.pexels.com/v1/search/?page="+pageNumber+"&per_page=80&query="+searchQuery;
                            fetchWallpaper();
                        }
                    }
                });
            }
        });


        appName=findViewById(R.id.appTitle);
        appName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
                wallpaperModelList.clear();
                dialog.show();
                pageNumber=1;
                url="https://api.pexels.com/v1/curated/?page=" + pageNumber + "&per_page=80";
                fetchWallpaper();
            }
        });


    }

    private void closeKeyboard() {
        View view=this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


    public void fetchWallpaper() {

        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("photos");

                    int length = jsonArray.length();

                    for (int i = 0; i < length; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        int id = object.getInt("id");

                        JSONObject objectImages = object.getJSONObject("src");
                        String originalUrl = objectImages.getString("original");
                        String mediumUrl = objectImages.getString("medium");

                        WallpaperModel wallpaperModel = new WallpaperModel(id, originalUrl, mediumUrl);
                        wallpaperModelList.add(wallpaperModel);

                    }
                    adapterWallpaper.notifyDataSetChanged();
                    pageNumber=pageNumber+1;


                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Failed To Load", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "563492ad6f9170000100000174f400583d2441ecb74761b76eb47361");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }
}