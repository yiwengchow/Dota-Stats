package com.example.myapp.Controller;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapp.Model.Player;
import com.example.myapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlayerSearchView extends RecyclerView.Adapter<PlayerSearchView.PlayerSearchViewHolder> {

    private ArrayList<Player> dataset;

    public static class PlayerSearchViewHolder extends RecyclerView.ViewHolder {
        public View mainView;
        public ImageView imageView;
        public TextView nameView;
        public TextView lastMatchView;

        public PlayerSearchViewHolder(View view) {
            super(view);
            mainView = view;
            nameView = view.findViewById(R.id.SearchNameText);
            imageView = view.findViewById(R.id.SearchResultImage);
            lastMatchView = view.findViewById(R.id.SearchLastMatchText);
        }
    }

    public PlayerSearchView(ArrayList<Player> dataset) {
        this.dataset = dataset;
    }

    public void updateList(ArrayList<Player> newDataset) {
        dataset.clear();
        dataset = newDataset;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerSearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.player_search, viewGroup, false);
        return new PlayerSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayerSearchViewHolder playerSearchViewHolder, final int i) {
        playerSearchViewHolder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.instance, PlayerInfoActivity.class);
                intent.putExtra("PlayerInfo", dataset.get(i));
                SearchActivity.instance.startActivity(intent);
                SearchActivity.instance.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        playerSearchViewHolder.nameView.setText(dataset.get(i).name);
        playerSearchViewHolder.lastMatchView.setText("Last match: " + dataset.get(i).lastMatchTime);
        playerSearchViewHolder.imageView.setImageResource(R.drawable.default_search_avatar);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Player player = dataset.get(i);
                try {
                    InputStream in = new URL(player.avatarPath).openStream();
                    player.setBitmap(BitmapFactory.decodeStream(in));
                    SearchActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playerSearchViewHolder.imageView.setImageBitmap(player.getBitmap());
                        }
                    });
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
