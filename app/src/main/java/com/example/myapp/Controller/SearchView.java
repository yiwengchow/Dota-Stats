package com.example.myapp.Controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapp.Model.Search;
import com.example.myapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchView extends RecyclerView.Adapter<SearchView.NameSearchViewHolder> {

    private ArrayList<Search> dataset;

    public static class NameSearchViewHolder extends RecyclerView.ViewHolder{
        public View mainView;
        public ImageView imageView;
        public TextView nameView;
        public TextView lastMatchView;

        public NameSearchViewHolder(View view){
            super(view);
            mainView = view;
            nameView = view.findViewById(R.id.SearchNameText);
            imageView = view.findViewById(R.id.SearchResultImage);
            lastMatchView =  view.findViewById(R.id.SearchLastMatchText);
        }
    }

    public SearchView(ArrayList<Search> dataset){
        this.dataset = dataset;
    }

    public void deleteAll(){
        final int size = dataset.size();
        dataset.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public NameSearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result, viewGroup, false);
        return new NameSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final NameSearchViewHolder nameSearchViewHolder, final int i) {
        nameSearchViewHolder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*View activityView = SearchActivity.instance.findViewById(R.id.ActivityLayout);
                activityView.startAnimation(AnimationUtils.loadAnimation(SearchActivity.instance.getApplicationContext(), R.anim.fade_out));
                v.startAnimation(AnimationUtils.loadAnimation(SearchActivity.instance.getApplicationContext(), R.anim.slide_up));

                activityView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SearchActivity.instance.setContentView(R.layout.player_page);
                    }
                }, 500);*/

                // Change activity
                Intent intent = new Intent(SearchActivity.instance, PlayerInfoActivity.class);
                intent.putExtra("PlayerInfo", dataset.get(i));
                SearchActivity.instance.startActivity(intent);
                SearchActivity.instance.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        nameSearchViewHolder.nameView.setText(dataset.get(i).personaname);
        nameSearchViewHolder.lastMatchView.setText("Last match: " + dataset.get(i).lastMatchTime);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    InputStream in = new URL(dataset.get(i).avatarfull).openStream();
                    final Bitmap bimage = BitmapFactory.decodeStream(in);
                    SearchActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nameSearchViewHolder.imageView.setImageBitmap(bimage);
                        }
                    });
                }
                catch(MalformedURLException e){
                }
                catch (IOException e){
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
