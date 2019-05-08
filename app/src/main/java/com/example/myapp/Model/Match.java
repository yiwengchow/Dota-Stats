package com.example.myapp.Model;

public class Match {
    public int matchId;
    public int playerSlot;
    public int heroId;
    public boolean gameWon;
    public int duration;
    public int gameMode;
    public int lobbyType;
    public int startTime;
    public int version;
    public int kills;
    public int deaths;
    public int assists;
    public int partySize;

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
