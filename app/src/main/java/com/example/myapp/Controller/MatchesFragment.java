package com.example.myapp.Controller;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapp.Model.DotaAPI;
import com.example.myapp.Model.MatchSearch;
import com.example.myapp.Model.PlayerSearch;
import com.example.myapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MatchesFragment extends Fragment {

    PlayerSearch player;
    RecyclerView matchView;

    public MatchesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        player = (PlayerSearch) getArguments().getSerializable("player");
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        matchView = view.findViewById(R.id.matches_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        matchView.setLayoutManager(layoutManager);

        DotaAPI.getInstance().getPlayerMatches(player.account_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonMatchList = new JSONArray(response);
                    ArrayList<MatchSearch> matchList = new ArrayList<>();

                    for (int i = 0; i < jsonMatchList.length(); i++){
                        JSONObject jsonMatchObj = jsonMatchList.getJSONObject(i);
                        MatchSearch match = new MatchSearch();
                        match.heroId = jsonMatchObj.getInt("hero_id");
                        matchList.add(match);
                    }

                    matchView.setAdapter(new MatchSearchView(matchList));
                }
                catch (JSONException e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }
}
