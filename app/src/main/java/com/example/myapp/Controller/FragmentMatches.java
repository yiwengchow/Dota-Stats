package com.example.myapp.Controller;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.Model.Match;
import com.example.myapp.R;

import java.util.ArrayList;

public class FragmentMatches extends Fragment {

    RecyclerView matchView;
    ArrayList<Match> matchList;

    public FragmentMatches() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (matchList == null){
            matchList = (ArrayList<Match>) getArguments().getSerializable("Matches");
            getArguments().remove("Matches");
        }

        matchView = view.findViewById(R.id.matches_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        matchView.setLayoutManager(layoutManager);
        matchView.setAdapter(new ViewMatches(matchList));
    }
}
