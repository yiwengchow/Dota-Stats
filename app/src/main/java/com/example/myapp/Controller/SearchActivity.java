package com.example.myapp.Controller;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapp.Model.DotaAPI;
import com.example.myapp.Model.Search;
import com.example.myapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    volatile Boolean isSearching = false;

    TextInputEditText searchEditText;
    Button searchButton;
    RecyclerView searchView;

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

    private void initializeInterface(){
        searchEditText = findViewById(R.id.SearchEditText);
        searchButton = findViewById(R.id.SearchButton);
        searchView = findViewById(R.id.SearchView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchView.setLayoutManager(layoutManager);
    }

    private void initializeSearch(){
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
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

    private void Search(){
        if (isSearching) return;
        searchButton.setEnabled(false);
        isSearching = true;
        DotaAPI.getInstance().getPlayersByName(searchEditText.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList list = new ArrayList<Search>();

                try{
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Search search = new Search();
                        JSONObject searchObj = jsonArray.getJSONObject(i);
                        search.account_id = searchObj.getInt("account_id");
                        search.personaname = searchObj.getString("personaname");
                        search.avatarfull = searchObj.getString("avatarfull");

                        try{
                            // Some profiles may not have this
                            search.lastMatchTime = searchObj.getString("last_match_time");
                        }
                        catch (JSONException e){}

                        list.add(search);

                                /*DotaAPI.getInstance().getPlayerAsync(search.account_id, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObj = new JSONObject(response);
                                            search.solorank = jsonObj.getString("solo_competitive_rank");
                                            search.partyrank = jsonObj.getString("competitive_rank");

                                            JSONObject mmrEstimate = jsonObj.getJSONObject("mmr_estimate");
                                            search.estimateMMR = mmrEstimate.getInt("estimate");
                                            list.add(search);
                                        } catch (JSONException e) {
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });*/
                    }
                }
                catch (JSONException e){}
                if (searchView.getAdapter() == null) searchView.setAdapter(new SearchView(list));
                else ((SearchView)searchView.getAdapter()).updateList(list);
                searchView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.layout_fall_down));
                searchView.smoothScrollToPosition(0);

                searchButton.setEnabled(true);
                isSearching = false;
             }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
