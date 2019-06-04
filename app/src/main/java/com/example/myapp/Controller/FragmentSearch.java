package com.example.myapp.Controller;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FragmentSearch extends Fragment {
    public RecyclerView searchView;
    volatile Boolean isSearching = false;

    TextInputEditText searchEditText;
    Button searchButton;
    ProgressBar searchProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initializeRepo();
        initializeInterface(view);
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

    private void initializeInterface(View view) {
        searchEditText = view.findViewById(R.id.SearchEditText);
        searchButton = view.findViewById(R.id.search_button);
        searchView = view.findViewById(R.id.search_view);
        searchProgress = view.findViewById(R.id.search_progress);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ActivityMain.instance);
        searchView.setLayoutManager(layoutManager);
        searchView.setItemAnimator(null);
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
                        final Player player = new Player();
                        player.account_id = profileObj.getInt("account_id");
                        player.name = profileObj.getString("personaname");
                        player.avatarPath = profileObj.getString("avatarfull");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    InputStream in = new URL(player.avatarPath).openStream();
                                    player.setBitmap(BitmapFactory.decodeStream(in));

                                    ActivityMain.instance.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(ActivityMain.instance, ActivityPlayer.class);
                                            intent.putExtra("PlayerInfo", player);
                                            ActivityMain.instance.startActivity(intent);
                                            ActivityMain.instance.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

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
                        player.name = playerObj.getString("personaname");
                        player.avatarPath = playerObj.getString("avatarfull");

                        try {
                            // Some profiles may not have this
                            player.lastMatchTime = playerObj.getString("last_match_time");
                        } catch (JSONException e) {}

                        playerList.add(player);
                    }
                } catch (JSONException e) {}

                if (searchView.getAdapter() == null) searchView.setAdapter(new ViewPlayers(playerList));
                else ((ViewPlayers) searchView.getAdapter()).updateList(playerList);

                searchView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(ActivityMain.instance, R.anim.layout_fall_down));
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
