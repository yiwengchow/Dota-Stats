package com.example.myapp.Controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.Model.Player;
import com.example.myapp.R;
import com.example.myapp.Repository;

import java.util.ArrayList;

public class FragmentFavourites extends Fragment {

    public RecyclerView favouritesView;

    public FragmentFavourites() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        favouritesView = view.findViewById(R.id.favourite_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ActivityMain.instance);
        favouritesView.setLayoutManager(layoutManager);

        if (favouritesView.getAdapter() == null) {
            favouritesView.setAdapter(new ViewPlayers(Repository.getInstance().favouritesList));
        }
        else ((ViewPlayers) favouritesView.getAdapter()).updateList(Repository.getInstance().favouritesList);

        favouritesView.setItemAnimator(null);
    }

    @Override
    public void onResume(){
        super.onResume();
        favouritesView.getAdapter().notifyDataSetChanged();
    }
}
