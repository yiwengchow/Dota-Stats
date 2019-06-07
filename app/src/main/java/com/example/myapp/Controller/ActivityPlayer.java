package com.example.myapp.Controller;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapp.Model.DotaAPI;
import com.example.myapp.Model.Friend;
import com.example.myapp.Model.Match;
import com.example.myapp.Model.Player;
import com.example.myapp.Model.PlayerMatch;
import com.example.myapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityPlayer extends AppCompatActivity {

    public static ActivityPlayer instance;

    ConstraintLayout loadingLayout;

    Player player;
    ViewPager viewPager;
    TabLayout tabLayout;
    ArrayList<Friend> friendsList;
    ArrayList<Match> matchList;


    final Object loadProfileLock = new Object();
    final Object loadMatchesLock = new Object();
    final Object loadFriendsLock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        instance = this;
        loadingLayout = findViewById(R.id.loading_player_layout);

        player = (Player) getIntent().getSerializableExtra("PlayerInfo");
        getIntent().removeExtra("myData");

        viewPager = findViewById(R.id.player_view_pager);
        tabLayout = findViewById(R.id.player_tab_layout);

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (loadProfileLock){
                    loadProfile();
                    try {
                        loadProfileLock.wait();
                    } catch (InterruptedException e) {}
                }

                synchronized (loadMatchesLock){
                    loadMatches();
                    try {
                        loadMatchesLock.wait();
                    } catch (InterruptedException e) {}
                }

                synchronized (loadFriendsLock){
                    loadFriends();
                    try {
                        loadFriendsLock.wait();
                    } catch (InterruptedException e) {}
                }

                final ViewPagerAdapter pageAdapter = setupPage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingLayout.setVisibility(View.GONE);
                        viewPager.setAdapter(pageAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume(){
        super.onResume();
        instance = this;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private ViewPagerAdapter setupPage() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Create profile fragment
        FragmentProfile fragmentProfile = new FragmentProfile();
        Bundle profileBundle = new Bundle();
        profileBundle.putSerializable("Player", player);
        fragmentProfile.setArguments(profileBundle);
        adapter.addFragment(fragmentProfile, "Profile");

        // Create matches fragment
        FragmentMatches fragmentMatches = new FragmentMatches();
        Bundle matchBundle = new Bundle();
        matchBundle.putSerializable("Matches", matchList);
        fragmentMatches.setArguments(matchBundle);
        adapter.addFragment(fragmentMatches, "Matches");

        FragmentFriends fragmentFriends = new FragmentFriends();
        Bundle friendsBundle = new Bundle();
        friendsBundle.putSerializable("Player", player);
        friendsBundle.putSerializable("Friends", friendsList);
        fragmentFriends.setArguments(friendsBundle);
        adapter.addFragment(fragmentFriends, "Friends");

        return adapter;
    }

    private void loadProfile(){
        final Object playerRecordLock = new Object();

        // Wait for win lose record data
        synchronized (playerRecordLock) {
            DotaAPI.getInstance().getPlayerRecord(player.account_id, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        player.wins = obj.getInt("win");
                        player.loses = obj.getInt("lose");

                        synchronized (playerRecordLock) {
                            playerRecordLock.notifyAll();
                        }
                    } catch (JSONException e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            try{
                playerRecordLock.wait();
            }
            catch (InterruptedException ex){}
        }

        DotaAPI.getInstance().getPlayerAsync(player.account_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject obj = new JSONObject(response);
                    JSONObject estMMRObj = obj.getJSONObject("mmr_estimate");

                    try{
                        player.soloMMR = obj.getString("solo_competitive_rank");
                    }
                    catch (JSONException ex){}

                    try{
                        player.partyMMR = obj.getString("competitive_rank");
                    }
                    catch (JSONException ex){}

                    try{
                        player.estimateMMR = estMMRObj.getInt("estimate");
                    }
                    catch (JSONException ex){}

                    try{
                        player.rank = obj.getInt("rank_tier");
                        player.leaderboard = obj.getInt("leaderboard_rank");
                    }
                    catch(JSONException ex){}

                    synchronized (loadProfileLock){
                        loadProfileLock.notifyAll();
                    }
                }
                catch(JSONException e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
            }
        });
    }

    private void loadMatches(){
        DotaAPI.getInstance().getPlayerMatches(player.account_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonMatchList = new JSONArray(response);
                    matchList = new ArrayList<>();

                    for (int i = 0; i < jsonMatchList.length(); i++){
                        JSONObject jsonMatchObj = jsonMatchList.getJSONObject(i);
                        PlayerMatch playerMatch = new PlayerMatch();
                        playerMatch.matchId = jsonMatchObj.getInt("match_id");
                        playerMatch.playerSlot = jsonMatchObj.getInt("player_slot");

                        boolean radiantWon = jsonMatchObj.getBoolean("radiant_win");
                        if (playerMatch.playerSlot >= 0 && playerMatch.playerSlot <= 127) playerMatch.gameWon = radiantWon;
                        else playerMatch.gameWon = !radiantWon;

                        playerMatch.duration = jsonMatchObj.getInt("duration");
                        playerMatch.gameMode = jsonMatchObj.getInt("game_mode");
                        playerMatch.lobbyType = jsonMatchObj.getInt("lobby_type");
                        playerMatch.heroId = jsonMatchObj.getInt("hero_id");
                        playerMatch.startTime = jsonMatchObj.getInt("start_time");
                        playerMatch.kills = jsonMatchObj.getInt("kills");
                        playerMatch.deaths = jsonMatchObj.getInt("deaths");
                        playerMatch.assists = jsonMatchObj.getInt("assists");

                        try{
                            playerMatch.version = jsonMatchObj.getInt("version");
                        }catch(JSONException e){}

                        try{
                            playerMatch.setSkillBracket(jsonMatchObj.getInt("skill"));
                        }catch(JSONException e){}

                        try{
                            playerMatch.partySize = jsonMatchObj.getInt("party_size");
                        }catch(JSONException e){}

                        matchList.add(playerMatch);
                    }

                    synchronized (loadMatchesLock){
                        loadMatchesLock.notifyAll();
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

    private void loadFriends(){
        DotaAPI.getInstance().getPlayerFriends(player.account_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonFriends = new JSONArray(response);
                    friendsList = new ArrayList<>();

                    for (int i = 0; i < jsonFriends.length(); i++) {
                        JSONObject jsonFriend = jsonFriends.getJSONObject(i);
                        Friend friend = new Friend();
                        friend.account_id = jsonFriend.getInt("account_id");
                        friend.avatarPath = jsonFriend.getString("avatarfull");
                        friend.name = jsonFriend.getString("personaname");
                        friend.games = jsonFriend.getInt("games");
                        friend.wins = jsonFriend.getInt("win");
                        friend.lastPlayed = jsonFriend.getInt("last_played");
                        friendsList.add(friend);
                    }
                }
                catch (JSONException e){}

                synchronized (loadFriendsLock){
                    loadFriendsLock.notify();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }
}
