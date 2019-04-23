package com.example.myapp.Controller;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputEditText;
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
import com.example.myapp.Model.Player;
import com.example.myapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    volatile Boolean isSearching = false;

    TextInputEditText searchEditText;
    Button searchButton;
    RecyclerView searchView;
    ProgressBar searchProgress;

    public static SearchActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        instance = this;
        /*MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");*/

        initializeInterface();
        initializeSearch();
    }

    private void initializeInterface() {
        searchEditText = findViewById(R.id.SearchEditText);
        searchButton = findViewById(R.id.SearchButton);
        searchView = findViewById(R.id.SearchView);
        searchProgress = findViewById(R.id.SearchProgress);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchView.setLayoutManager(layoutManager);
    }

    private void initializeSearch() {
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    Search();
                    return true;
                }
                return false;
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
    }

    private void setSearchProgress(boolean flag){
        searchProgress.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        searchButton.setVisibility(flag ? View.INVISIBLE : View.VISIBLE);
        isSearching = flag;
    }

    private void Search() {
        if (isSearching) return;
        setSearchProgress(true);

        final String target = searchEditText.getText().toString();

        // try to get if player exists first
        try{
            DotaAPI.getInstance().getPlayerAsync(Integer.parseInt(target), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject playerObj = new JSONObject(response);
                        JSONObject profileObj = playerObj.getJSONObject("profile"); //Exception will prompt if does not exist

                        // Go next page
                        final Player player = new Player();
                        player.account_id = profileObj.getInt("account_id");
                        player.personaname = profileObj.getString("personaname");
                        player.avatarfull = profileObj.getString("avatarfull");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    InputStream in = new URL(player.avatarfull).openStream();
                                    player.setBitmap(BitmapFactory.decodeStream(in));

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(SearchActivity.instance, PlayerInfoActivity.class);
                                            intent.putExtra("PlayerInfo", player);
                                            SearchActivity.instance.startActivity(intent);
                                            SearchActivity.instance.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                                            setSearchProgress(false);
                                        }
                                    });
                                } catch (MalformedURLException e) {
                                } catch (IOException e) {
                                }
                            }
                        }).start();
                    }
                    catch (JSONException e){
                        displayPlayers(target);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        }
        catch (NumberFormatException e)
        {
            displayPlayers(target);
        }
    }

    private void displayPlayers(String target){
        final ArrayList<Player> playerList = new ArrayList<>();
        DotaAPI.getInstance().getPlayersByName(target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        final Player player = new Player();
                        JSONObject playerObj = jsonArray.getJSONObject(i);
                        player.account_id = playerObj.getInt("account_id");
                        player.personaname = playerObj.getString("personaname");
                        player.avatarfull = playerObj.getString("avatarfull");

                        try {
                            // Some profiles may not have this
                            player.lastMatchTime = playerObj.getString("last_match_time");
                        } catch (JSONException e) {}

                        playerList.add(player);
                    }
                } catch (JSONException e) {}

                if (searchView.getAdapter() == null) searchView.setAdapter(new SearchView(playerList));
                else ((SearchView) searchView.getAdapter()).updateList(playerList);

                searchView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.layout_fall_down));
                searchView.smoothScrollToPosition(0);

                setSearchProgress(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setSearchProgress(false);
            }
        });
    }
}
