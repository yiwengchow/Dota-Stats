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
import com.example.myapp.Model.Player;
import com.example.myapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentProfile extends Fragment {
    Player player;

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

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (player == null){
            player = (Player) getArguments().getSerializable("Player");
            getArguments().remove("Player");
        }

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        winLoseLayout = view.findViewById(R.id.win_lose_layout);
        mmrLayout = view.findViewById(R.id.mmr_layout);
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

    void loadData(){
        avatarImage.setImageBitmap(player.getBitmap());
        playerName.setText(player.name);
        double numWinRate = player.wins / ((double)player.wins + (double)player.loses) * 100;

        wins.setText(String.format("%s%s", player.wins , "W"));
        loses.setText(String.format("%s%s", player.loses , "L"));
        winRate.setText(String.format("(%.2f%s)", numWinRate, "%"));
        soloMMR.setText(!player.soloMMR.equals("null") ? player.soloMMR : "");
        partyMMR.setText(!player.partyMMR.equals("null") ? player.partyMMR : "");
        estimatedMMR.setText(player.estimateMMR != 0 ? String.valueOf(player.estimateMMR) : "");

        String packageName = getActivity().getPackageName();
        int rankRes = 0;
        if (player.rank != 80){
            rankRes = getResources().getIdentifier("drawable/rank_" + player.rank, null, packageName);
        }
        else {
            if (player.leaderboard <= 10)
                rankRes = getResources().getIdentifier("drawable/rank_" + player.rank + "_top_10", null, packageName);
            else if (player.leaderboard <= 100)
                rankRes = getResources().getIdentifier("drawable/rank_" + player.rank + "_top_100", null, packageName);
            else {
                rankRes = getResources().getIdentifier("drawable/rank_" + player.rank + "_top_1000", null, packageName);
            }

            leaderboardRank.setText(String.valueOf(player.leaderboard));
        }
        medalImage.setImageResource(rankRes);

//        DotaAPI.getInstance().getPlayerAsync(player.account_id, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try{
//                    JSONObject obj = new JSONObject(response);
//                    JSONObject estMMRObj = obj.getJSONObject("mmr_estimate");
//
//                    try{
//                        String mmr = obj.getString("solo_competitive_rank");
//                        soloMMR.setText(!mmr.equals("null") ? mmr : "");
//                    }
//                    catch (JSONException ex){}
//
//                    try{
//                        String mmr = obj.getString("competitive_rank");
//                        partyMMR.setText(!mmr.equals("null") ? mmr : "");
//                    }
//                    catch (JSONException ex){}
//
//                    try{
//                        String mmr = String.valueOf(estMMRObj.getInt("estimate"));
//                        estimatedMMR.setText(mmr);
//                    }
//                    catch (JSONException ex){}
//
//                    try {
//                        String packageName = getActivity().getPackageName();
//                        int rankTier = obj.getInt("rank_tier");
//                        int rankRes = 0;
//
//                        if (rankTier != 80){
//                            rankRes = getResources().getIdentifier("drawable/rank_" + rankTier, null, packageName);
//                        }
//                        else{
//                            try{
//                                int ranking = obj.getInt("leaderboard_rank");
//
//                                if (ranking <= 10)
//                                    rankRes = getResources().getIdentifier("drawable/rank_" + rankTier + "_top_10", null, packageName);
//                                else if (ranking <= 100)
//                                    rankRes = getResources().getIdentifier("drawable/rank_" + rankTier + "_top_100", null, packageName);
//                                else{
//                                    rankRes = getResources().getIdentifier("drawable/rank_" + rankTier + "_top_1000", null, packageName);
//                                }
//
//                                leaderboardRank.setText(String.valueOf(ranking));
//                            }
//                            catch(JSONException e){}
//                        }
//                        medalImage.setImageResource(rankRes);
//                    }
//                    catch (JSONException e){}
//                    catch (NullPointerException e){}
//                }
//                catch(JSONException e){}
//                setMMRLoaded(true);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error)
//            {
//            }
//        });
    }
}
