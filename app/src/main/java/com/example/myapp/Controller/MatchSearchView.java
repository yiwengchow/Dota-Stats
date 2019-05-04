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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MatchSearchView extends RecyclerView.Adapter<MatchSearchView.MatchSearchViewHolder> {

    private ArrayList<Match> dataset;

    public MatchSearchView(ArrayList<Match> dataset) {
        this.dataset = dataset;
    }

    public static class MatchSearchViewHolder extends RecyclerView.ViewHolder {
        public ImageView heroImage;
        public TextView heroNameText;
        public TextView kdaText;
        public TextView timePlayedText;
        public TextView typeText;
        public TextView resultText;
        public TextView durationText;

        public MatchSearchViewHolder(View view) {
            super(view);
            heroImage = view.findViewById(R.id.hero_image);
            heroNameText = view.findViewById(R.id.hero_name);
            kdaText = view.findViewById(R.id.kda_text);
            timePlayedText = view.findViewById(R.id.timeplayed_text);
            typeText = view.findViewById(R.id.gametype_text);
            resultText = view.findViewById(R.id.result_text);
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
                matchSearchViewHolder.heroNameText.setText(hero.displayName);
//                matchSearchViewHolder.kdaText.setText();
                break;
            }
        }

        matchSearchViewHolder.kdaText.setText(String.format("%d/%d/%d", match.kills, match.deaths, match.assists));
        matchSearchViewHolder.resultText.setText(match.gameWon ? "Won" : "Lost");
        matchSearchViewHolder.resultText.setTextColor(match.gameWon ? Color.GREEN : Color.RED);

        int seconds = match.duration;
        long hours = TimeUnit.SECONDS.toHours(seconds);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
        matchSearchViewHolder.durationText.setText(String.format("%s%02d:%02d", hours != 0 ? hours + ":" : "", minute, second));
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
