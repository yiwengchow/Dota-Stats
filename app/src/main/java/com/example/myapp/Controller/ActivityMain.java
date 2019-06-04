package com.example.myapp.Controller;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapp.Model.DotaAPI;
import com.example.myapp.Model.Hero;
import com.example.myapp.Model.Player;
import com.example.myapp.R;
import com.example.myapp.Repository;
import com.example.myapp.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {

    public static ActivityMain instance;
    public FragmentFavourites fragmentFavourites;
    public FragmentSearch fragmentSearch;

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        Utility.getInstance().updateFavouritesList(this);

        viewPager = findViewById(R.id.main_view_pager);
        tabLayout = findViewById(R.id.main_tab_layout);

        final ViewPagerAdapter pageAdapter = setupPage();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(pageAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        });

    }

    private ViewPagerAdapter setupPage() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        fragmentSearch = new FragmentSearch();
        adapter.addFragment(fragmentSearch, "Search");

        fragmentFavourites = new FragmentFavourites();
        adapter.addFragment(fragmentFavourites, "Favourites");

        return adapter;
    }


}
