package com.example.myapp.Controller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapp.Model.DotaAPI;
import com.example.myapp.Model.Friend;
import com.example.myapp.Model.Match;
import com.example.myapp.Model.Player;
import com.example.myapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    Player player;
    RecyclerView friendsView;
    ArrayList<Friend> friendsList;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        if (player == null && friendsList == null){
            player = (Player) getArguments().getSerializable("Player");
            friendsList = (ArrayList<Friend>) getArguments().getSerializable("Friends");
            getArguments().remove("Player");
            getArguments().remove("Friends");
        }

        friendsView = view.findViewById(R.id.friends_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        friendsView.setLayoutManager(layoutManager);
        friendsView.setAdapter(new FriendsView(friendsList));

        return view;
    }
}
