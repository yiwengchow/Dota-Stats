package com.example.myapp.Controller;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapp.Model.Friend;
import com.example.myapp.Model.Player;
import com.example.myapp.R;
import com.example.myapp.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FriendsView extends RecyclerView.Adapter<FriendsView.FriendsViewHolder> {
    private ArrayList<Friend> dataset;

    public FriendsView(ArrayList<Friend> dataset) {
        this.dataset = dataset;
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        public View mainView;
        public ImageView avatar;
        public TextView nameText;
        public TextView gamesText;
        public TextView winLoseText;
        public TextView lastPlayedText;

        public FriendsViewHolder(View view) {
            super(view);
            mainView = view;
            avatar = view.findViewById(R.id.friend_avatar);
            nameText = view.findViewById(R.id.friend_name);
            gamesText = view.findViewById(R.id.friend_games);
            winLoseText = view.findViewById(R.id.friend_win_lose);
            lastPlayedText = view.findViewById(R.id.friend_last_played_together);
        }
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends, viewGroup, false);
        return new FriendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, final int i) {
        final Friend friend = dataset.get(i);
        friendsViewHolder.nameText.setText(friend.name);
        friendsViewHolder.gamesText.setText(String.valueOf(friend.games));

        int loses = friend.games - friend.wins;
        if (friend.wins != loses) friendsViewHolder.winLoseText.setTextColor(friend.wins > loses ? Color.GREEN : Color.RED);
        friendsViewHolder.winLoseText.setText((friend.wins > loses ? "+" : "") + String.valueOf(friend.wins - loses));

        friendsViewHolder.lastPlayedText.setText(Utility.getInstance().convertEpochToDatetime(friend.lastPlayed));
        friendsViewHolder.avatar.setImageResource(R.drawable.default_search_avatar);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Friend friend = dataset.get(i);
                try {
                    InputStream in = new URL(friend.avatarPath).openStream();
                    friend.setBitmap(BitmapFactory.decodeStream(in));
                    SearchActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            friendsViewHolder.avatar.setImageBitmap(friend.getBitmap());
                        }
                    });
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }

                PlayerInfoActivity.instance.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        friendsViewHolder.mainView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PlayerInfoActivity.instance, PlayerInfoActivity.class);
                                intent.putExtra("PlayerInfo", friend);
                                PlayerInfoActivity.instance.startActivity(intent);
                                PlayerInfoActivity.instance.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
