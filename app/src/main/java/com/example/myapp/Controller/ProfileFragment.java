package com.example.myapp.Controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapp.Model.DotaAPI;
import com.example.myapp.Model.PlayerSearch;
import com.example.myapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {
    PlayerSearch player;

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

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        player = (PlayerSearch) getArguments().getSerializable("player");

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        winLoseLayout = view.findViewById(R.id.win_lose_layout);
        mmrLayout = view.findViewById(R.id.mmr_layout);
        loadingProgressBar = view.findViewById(R.id.loading_progress_bar);
        avatarImage = view.findViewById(R.id.avatar_image);
        leaderboardRank = view.findViewById(R.id.leaderboard_rank);
        medalImage = view.findViewById(R.id.medal_image);
        playerName = view.findViewById(R.id.player_name);
        wins = view.findViewById(R.id.record_wins);
        loses = view.findViewById(R.id.record_loses);
        winRate = view.findViewById(R.id.win_rate);
        soloMMR = view.findViewById(R.id.solo_mmr);
        partyMMR = view.findViewById(R.id.party_mmr);
        estimatedMMR = view.findViewById(R.id.estimate_mmr);

        loadData();
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

    void loadData(){
        winLoseLayout.setVisibility(View.INVISIBLE);
        mmrLayout.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        avatarImage.setImageBitmap(player.getBitmap());
        playerName.setText(player.personaname);

        DotaAPI.getInstance().getPlayerRecord(player.account_id, new Response.Listener<String>() {
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

        DotaAPI.getInstance().getPlayerAsync(player.account_id, new Response.Listener<String>() {
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
                        String packageName = getActivity().getPackageName();
                        int rankTier = obj.getInt("rank_tier");
                        int rankRes = 0;

                        if (rankTier != 80){
                            rankRes = getResources().getIdentifier("drawable/rank_" + rankTier, null, packageName);
                        }
                        else{
                            try{
                                int ranking = obj.getInt("leaderboard_rank");

                                if (ranking <= 10)
                                    rankRes = getResources().getIdentifier("drawable/rank_" + rankTier + "_top_10", null, packageName);
                                else if (ranking <= 100)
                                    rankRes = getResources().getIdentifier("drawable/rank_" + rankTier + "_top_100", null, packageName);
                                else{
                                    rankRes = getResources().getIdentifier("drawable/rank_" + rankTier + "_top_1000", null, packageName);
                                }

                                leaderboardRank.setText(String.valueOf(ranking));
                            }
                            catch(JSONException e){}
                        }
                        medalImage.setImageResource(rankRes);
                    }
                    catch (JSONException e){}
                    catch (NullPointerException e){}
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
