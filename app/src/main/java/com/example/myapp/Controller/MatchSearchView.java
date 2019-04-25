package com.example.myapp.Controller;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapp.Model.Hero;
import com.example.myapp.Model.MatchSearch;
import com.example.myapp.Model.PlayerSearch;
import com.example.myapp.R;
import com.example.myapp.Repository;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MatchSearchView extends RecyclerView.Adapter<MatchSearchView.MatchSearchViewHolder> {

    private ArrayList<MatchSearch> dataset;

    public MatchSearchView(ArrayList<MatchSearch> dataset) {
        this.dataset = dataset;
    }

    public static class MatchSearchViewHolder extends RecyclerView.ViewHolder {
        public ImageView heroImage;
        public TextView heroNameText;

        public MatchSearchViewHolder(View view) {
            super(view);
            heroImage = view.findViewById(R.id.hero_image);
            heroNameText = view.findViewById(R.id.hero_name);
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
        for(Hero hero : Repository.getInstance().heroList){
            if (hero.id == dataset.get(i).heroId){
                matchSearchViewHolder.heroImage.setImageResource(getHeroImageRes(hero));
                matchSearchViewHolder.heroNameText.setText(hero.displayName);
                break;
            }
        }
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
