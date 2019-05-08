package com.example.myapp.Controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapp.Model.Friend;
import com.example.myapp.R;

import java.util.ArrayList;

public class FriendsView extends RecyclerView.Adapter<FriendsView.FriendsViewHolder> {
    private ArrayList<Friend> dataset;

    public FriendsView(ArrayList<Friend> dataset) {
        this.dataset = dataset;
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;

        public FriendsViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.friend_name);
        }
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends, viewGroup, false);
        return new FriendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder friendsViewHolder, int i) {
        Friend friend = dataset.get(i);
        friendsViewHolder.nameText.setText(friend.name);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
