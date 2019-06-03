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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ViewPlayers extends RecyclerView.Adapter<ViewPlayers.PlayerSearchViewHolder> {

    private ArrayList<Player> dataset;

    public static class PlayerSearchViewHolder extends RecyclerView.ViewHolder {
        public View mainView;
        public ImageView imageView;
        public TextView nameView;
        public TextView lastMatchView;

        public PlayerSearchViewHolder(View view) {
            super(view);
            mainView = view;
            nameView = view.findViewById(R.id.player_search_name_text);
            imageView = view.findViewById(R.id.player_search_image);
            lastMatchView = view.findViewById(R.id.player_search_last_played_text);
        }
    }

    public ViewPlayers(ArrayList<Player> dataset) {
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_players, viewGroup, false);
        return new PlayerSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayerSearchViewHolder playerSearchViewHolder, final int i) {
        final Player player = dataset.get(i);
        playerSearchViewHolder.nameView.setText(player.name);

        String displayDate = "Unknown";
        if (player.lastMatchTime != "None") {
            try {
                String s = player.lastMatchTime.replace("T"," ").replace("Z","");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdf.parse(s);

                long currentTime = System.currentTimeMillis();
                long gameTime = date.getTime();
                long timeDiff = currentTime - gameTime;

                // less than a hour, display as minutes
                if (timeDiff < TimeUnit.HOURS.toMillis(1)){
                    long minutes =  TimeUnit.MILLISECONDS.toMinutes(timeDiff);
                    displayDate = String.format("%d %s ago", minutes, minutes <= 1 ? "minute" : "minutes");
                }
                // if less than a day, display as hours
                else if (timeDiff < TimeUnit.DAYS.toMillis(1)){
                    long hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
                    displayDate = String.format("%d %s ago", hours, hours <= 1 ? "hour" : "hours");
                }
                // if less than a month (28 days), display as days
                else if (timeDiff < TimeUnit.DAYS.toMillis(28)){
                    long days = TimeUnit.MILLISECONDS.toDays(timeDiff);
                    displayDate = String.format("%d %s ago", days,  days <= 1 ? "day" : "days");
                }
                // if less than a year, display as months
                else if (timeDiff < TimeUnit.DAYS.toMillis(365)){
                    long months = TimeUnit.MILLISECONDS.toDays(timeDiff) / 28;
                    displayDate = String.format("%d %s ago", months, months <= 1 ? "month" : "months");
                }
                else {
                    long years = TimeUnit.MILLISECONDS.toDays(timeDiff) / 365;
                    displayDate = String.format("%d %s ago", years, years <= 1 ? "year" : "years");
                }
            }
            catch (ParseException e){}
        }

        playerSearchViewHolder.lastMatchView.setText("Last seen: " + displayDate);
        playerSearchViewHolder.imageView.setImageResource(R.drawable.default_search_avatar);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = new URL(player.avatarPath).openStream();
                    player.setBitmap(BitmapFactory.decodeStream(in));
                    ActivityMain.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playerSearchViewHolder.imageView.setImageBitmap(player.getBitmap());
                        }
                    });

                    ActivityMain.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playerSearchViewHolder.mainView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ActivityMain.instance, ActivityPlayer.class);
                                    intent.putExtra("PlayerInfo", player);
                                    ActivityMain.instance.startActivity(intent);
                                    ActivityMain.instance.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                }
                            });
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
