package com.example.myapp.Controller;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapp.Model.Hero;
import com.example.myapp.Model.Match;
import com.example.myapp.R;
import com.example.myapp.Repository;
import com.example.myapp.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MatchSearchView extends RecyclerView.Adapter<MatchSearchView.MatchSearchViewHolder> {

    private ArrayList<Match> dataset;

    public MatchSearchView(ArrayList<Match> dataset) {
        this.dataset = dataset;
    }

    public static class MatchSearchViewHolder extends RecyclerView.ViewHolder {
        public ImageView heroImage;
        //public TextView heroNameText;
        public TextView kdaText;
        public TextView startTimeText;
        public TextView lobbyTypeText;
        public TextView gameTypeText;
        public TextView resultText;
        public TextView skillText;
        public TextView durationText;

        public MatchSearchViewHolder(View view) {
            super(view);
            heroImage = view.findViewById(R.id.hero_image);
            //heroNameText = view.findViewById(R.id.hero_name);
            kdaText = view.findViewById(R.id.kda_text);
            startTimeText = view.findViewById(R.id.starttime_text);
            lobbyTypeText = view.findViewById(R.id.lobbytype_text);
            gameTypeText = view.findViewById(R.id.gametype_text);
            resultText = view.findViewById(R.id.result_text);
            skillText = view.findViewById(R.id.skill_text);
            durationText = view.findViewById(R.id.duration_text);
        }
    }

    @NonNull
    @Override
    public MatchSearchView.MatchSearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.matches_search, viewGroup, false);
        return new MatchSearchView.MatchSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchSearchView.MatchSearchViewHolder matchSearchViewHolder, int i) {
        Match match = dataset.get(i);
        for(Hero hero : Repository.getInstance().heroList){
            if (hero.id == match.heroId){
                matchSearchViewHolder.heroImage.setImageResource(getHeroImageRes(hero));
                //matchSearchViewHolder.heroNameText.setText(hero.displayName);
//                matchSearchViewHolder.kdaText.setText();
                break;
            }
        }

        matchSearchViewHolder.kdaText.setText(String.format("%d/%d/%d", match.kills, match.deaths, match.assists));
        matchSearchViewHolder.resultText.setText(match.gameWon ? "Game Won" : "Game Lost");
        matchSearchViewHolder.resultText.setTextColor(match.gameWon ? Color.GREEN : Color.RED);

        long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        long gameTime = match.startTime;
        long timeDiff = currentTime - gameTime;

        // less than a hour, display as minutes
        if (timeDiff < TimeUnit.HOURS.toSeconds(1)){
            long minutes =  TimeUnit.SECONDS.toMinutes(timeDiff);
            matchSearchViewHolder.startTimeText.setText(String.format("%d %s ago", minutes, minutes <= 1 ? "minute" : "minutes"));
        }
        // if less than a day, display as hours
        else if (timeDiff < TimeUnit.DAYS.toSeconds(1)){
            long hours = TimeUnit.SECONDS.toHours(timeDiff);
            matchSearchViewHolder.startTimeText.setText(String.format("%d %s ago", hours, hours <= 1 ? "hour" : "hours"));
        }
        // if less than a month (28 days), display as days
        else if (timeDiff < TimeUnit.DAYS.toSeconds(28)){
            long days = TimeUnit.SECONDS.toDays(timeDiff);
            matchSearchViewHolder.startTimeText.setText(String.format("%d %s ago", days,  days <= 1 ? "day" : "days"));
        }
        // if less than a year, display as months
        else if (timeDiff < TimeUnit.DAYS.toSeconds(365)){
            long months = TimeUnit.SECONDS.toDays(timeDiff) / 28;
            matchSearchViewHolder.startTimeText.setText(String.format("%d %s ago", months, months <= 1 ? "month" : "months"));
        }
        else {
            long years = TimeUnit.SECONDS.toDays(timeDiff) / 365;
            matchSearchViewHolder.startTimeText.setText(String.format("%d %s ago", years, years <= 1 ? "year" : "years"));
        }

        int seconds = match.duration;
        long hours = TimeUnit.SECONDS.toHours(seconds);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
        matchSearchViewHolder.durationText.setText(String.format("%s%02d:%02d", hours != 0 ? hours + ":" : "", minute, second));

        matchSearchViewHolder.skillText.setText(match.getSkillBracket());
        matchSearchViewHolder.lobbyTypeText.setText(Utility.getInstance().getLobbyTypeById(match.lobbyType));
        matchSearchViewHolder.gameTypeText.setText(Utility.getInstance().getGameModeById(match.gameMode));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    private int getHeroImageRes(Hero hero){
        String packageName = SearchActivity.instance.getPackageName();
        return SearchActivity.instance.getResources().getIdentifier("drawable/" + hero.name.split("npc_dota_hero_")[1], null, packageName);
    }

}
