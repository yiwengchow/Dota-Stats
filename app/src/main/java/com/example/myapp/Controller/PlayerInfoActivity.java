package com.example.myapp.Controller;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapp.Model.DotaAPI;
import com.example.myapp.Model.Player;
import com.example.myapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerInfoActivity extends AppCompatActivity {

    Player data;

    ConstraintLayout winLoseLayout;
    ConstraintLayout mmrLayout;
    ProgressBar loadingProgressBar;
    ImageView avatarImage;
    ImageView medalImage;
    TextView leaderboardRank;
    TextView playerName;
    TextView wins;
    TextView loses;
    TextView winRate;
    TextView soloMMR;
    TextView partyMMR;
    TextView estimatedMMR;

    boolean winRateLoaded;
    boolean mmrLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        data = (Player) getIntent().getSerializableExtra("PlayerInfo");
        winLoseLayout = findViewById(R.id.win_lose_layout);
        mmrLayout = findViewById(R.id.mmr_layout);
        loadingProgressBar = findViewById(R.id.loading_progress_bar);
        avatarImage = findViewById(R.id.avatar_image);
        leaderboardRank = findViewById(R.id.leaderboard_rank);
        medalImage = findViewById(R.id.medal_image);
        playerName = findViewById(R.id.player_name);
        wins = findViewById(R.id.record_wins);
        loses = findViewById(R.id.record_loses);
        winRate = findViewById(R.id.win_rate);
        soloMMR = findViewById(R.id.solo_mmr);
        partyMMR = findViewById(R.id.party_mmr);
        estimatedMMR = findViewById(R.id.estimate_mmr);
    }

    @Override
    protected void onStart(){
        super.onStart();
        initializeData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void setWinRateLoaded(boolean flag){
        winRateLoaded = flag;
        finishLoading();
    }

    private void setMMRLoaded(boolean flag){
        mmrLoaded = flag;
        finishLoading();
    }

    private void finishLoading(){
        if (winRateLoaded && mmrLoaded){
            winLoseLayout.setVisibility(View.VISIBLE);
            mmrLayout.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    void initializeData(){
        winLoseLayout.setVisibility(View.INVISIBLE);
        mmrLayout.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        avatarImage.setImageBitmap(data.getBitmap());
        playerName.setText(data.personaname);

        DotaAPI.getInstance().getPlayerRecord(data.account_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject obj = new JSONObject(response);
                    int numWin = obj.getInt("win");
                    int numLose = obj.getInt("lose");
                    double numWinRate = numWin / ((double)numWin + (double)numLose) * 100;

                    wins.setText(String.format("%s%s", numWin , "W"));
                    loses.setText(String.format("%s%s", numLose , "L"));
                    winRate.setText(String.format("(%.2f%s)", numWinRate, "%"));

                }
                catch(JSONException e){}
                setWinRateLoaded(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
            }
        });

        DotaAPI.getInstance().getPlayerAsync(data.account_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject obj = new JSONObject(response);
                    JSONObject estMMRObj = obj.getJSONObject("mmr_estimate");

                    try{
                        String mmr = obj.getString("solo_competitive_rank");
                        soloMMR.setText(!mmr.equals("null") ? mmr : "");
                    }
                    catch (JSONException ex){}

                    try{
                        String mmr = obj.getString("competitive_rank");
                        partyMMR.setText(!mmr.equals("null") ? mmr : "");
                    }
                    catch (JSONException ex){}

                    try{
                        String mmr = String.valueOf(estMMRObj.getInt("estimate"));
                        estimatedMMR.setText(mmr);
                    }
                    catch (JSONException ex){}

                    try {
                        int rankTier = obj.getInt("rank_tier");
                        int rankRes = 0;

                        if (rankTier != 80){
                            rankRes = getResources().getIdentifier("drawable/rank_" + rankTier, null, getPackageName());
                        }
                        else{
                            try{
                                int ranking = obj.getInt("leaderboard_rank");

                                if (ranking <= 100)
                                    rankRes = getResources().getIdentifier("drawable/rank_" + rankTier + "_top_100", null, getPackageName());
                                else if (ranking <= 10)
                                    rankRes = getResources().getIdentifier("drawable/rank_" + rankTier + "_top_10", null, getPackageName());
                                else{
                                    rankRes = getResources().getIdentifier("drawable/rank_" + rankTier + "_top_1000", null, getPackageName());
                                }

                                leaderboardRank.setText(String.valueOf(ranking));
                            }
                            catch(JSONException e){}
                        }
                        medalImage.setImageResource(rankRes);
                    }
                    catch (JSONException e){}
                }
                catch(JSONException e){}
                setMMRLoaded(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
            }
        });
    }
}
