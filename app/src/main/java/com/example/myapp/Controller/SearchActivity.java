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
import com.example.myapp.Model.Hero;
import com.example.myapp.Model.PlayerSearch;
import com.example.myapp.R;
import com.example.myapp.Repository;

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

        initializeRepo();
        initializeInterface();
        initializeSearch();
    }

    private void initializeRepo(){
        DotaAPI.getInstance().getHeroes(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonHeroList = new JSONArray(response);
                    for (int i = 0; i < jsonHeroList.length(); i++){
                        JSONObject jsonHeroObj = jsonHeroList.getJSONObject(i);

                        Hero hero = new Hero();
                        hero.id = jsonHeroObj.getInt("id");
                        hero.name = jsonHeroObj.getString("name");
                        hero.displayName = jsonHeroObj.getString("localized_name");
                        Repository.getInstance().heroList.add(hero);
                    }
                }
                catch (JSONException e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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

        // try to get if playerSearch exists first
        try{
            DotaAPI.getInstance().getPlayerAsync(Integer.parseInt(target), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject playerObj = new JSONObject(response);
                        JSONObject profileObj = playerObj.getJSONObject("profile"); //Exception will prompt if does not exist

                        // Go next page
                        final PlayerSearch playerSearch = new PlayerSearch();
                        playerSearch.account_id = profileObj.getInt("account_id");
                        playerSearch.personaname = profileObj.getString("personaname");
                        playerSearch.avatarfull = profileObj.getString("avatarfull");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    InputStream in = new URL(playerSearch.avatarfull).openStream();
                                    playerSearch.setBitmap(BitmapFactory.decodeStream(in));

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(SearchActivity.instance, PlayerInfoActivity.class);
                                            intent.putExtra("PlayerInfo", playerSearch);
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
        final ArrayList<PlayerSearch> playerSearchList = new ArrayList<>();
        DotaAPI.getInstance().getPlayersByName(target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        final PlayerSearch playerSearch = new PlayerSearch();
                        JSONObject playerObj = jsonArray.getJSONObject(i);
                        playerSearch.account_id = playerObj.getInt("account_id");
                        playerSearch.personaname = playerObj.getString("personaname");
                        playerSearch.avatarfull = playerObj.getString("avatarfull");

                        try {
                            // Some profiles may not have this
                            playerSearch.lastMatchTime = playerObj.getString("last_match_time");
                        } catch (JSONException e) {}

                        playerSearchList.add(playerSearch);
                    }
                } catch (JSONException e) {}

                if (searchView.getAdapter() == null) searchView.setAdapter(new PlayerSearchView(playerSearchList));
                else ((PlayerSearchView) searchView.getAdapter()).updateList(playerSearchList);

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
