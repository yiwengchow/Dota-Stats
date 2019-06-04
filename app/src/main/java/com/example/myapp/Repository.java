package com.example.myapp;

import com.example.myapp.Model.Hero;
import com.example.myapp.Model.Player;

import java.util.ArrayList;

public class Repository {

    public ArrayList<Hero> heroList = new ArrayList<>();
    public ArrayList<Player> favouritesList = new ArrayList<>();

    static Repository instance = null;
    private Repository(){}
    public static Repository getInstance(){
        if (instance == null) instance = new Repository();
        return instance;
    }
}
