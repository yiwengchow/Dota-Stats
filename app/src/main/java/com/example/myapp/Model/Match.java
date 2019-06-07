package com.example.myapp.Model;

import android.net.wifi.p2p.WifiP2pManager;

import java.io.Serializable;

public class Match implements Serializable {
    public int matchId;
    public int playerSlot;
    public int heroId;
    public boolean gameWon;
    public int duration;
    public int gameMode;
    public int lobbyType;
    public int startTime;
    public int version;

    private int skillBracket;
    public void setSkillBracket(int value){
        skillBracket = value;
    }
    public String getSkillBracket(){
        if (skillBracket == 0)
            return "Unknown Skill";
        else if (skillBracket == 1)
            return "Normal Skill";
        else if (skillBracket == 2)
            return "High Skill";
        else
            return "Very High Skill";
    }
}
