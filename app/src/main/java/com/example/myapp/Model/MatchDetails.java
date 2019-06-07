package com.example.myapp.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class MatchDetails extends Match {
    public int barracksStatusDire;
    public int barracksStatusRadiant;
    public int cluster;
    public Cosmetics cosmetics;
    public int direScore;
    public int radiantScore;
    public boolean radiantWin;
    public Draft draft;
    public int engine;
    public int firstBloodTime;
    public int gameMode;
    public int totalPlayers;
    public int leagueId;
    public int matchSeqNumber;
    public int negativeVotes;
    public int positiveVotes;
    public Objectives objectives;
    public Bans bans;
    public RadiantGoldAdv radiantGoldAdv;
    public RadiantXpAdv radiantXpAdv;
    public Teamfights teamfights;
    public PlayerDetails[] playerDetails;
    public int towerStatusDire;
    public int towerStatusRadiant;
    public int replaySalt;
    public int seriesType;
    public int seriesId;
    public int patch;
    public int region;
    public int goldThrow;
    public int goldComeback;
    public int goldLoss;
    public int goldWin;
    public String replayUrl;
}

class PlayerDetails{
    public int accountId;
    public int playerSlot;
    public int[] abilityUpgrades;
    public AbilityUses abilityUses;
    public AbilityTargets abilityTargets;
    public DamageTargets damageTargets;
    public Actions actions;
    public AdditionalUnits additionalUnits;
}

class Actions{

}

class AdditionalUnits{
    public String unitName;
    public int item0;
    public int item1;
    public int item2;
    public int item3;
    public int item4;
    public int item5;
    public int backpack0;
    public int backpack1;
    public int backpack2;
}

class DamageTargets{

}

class AbilityUses{

}

class AbilityTargets{

}

class Teamfights{

}

class RadiantGoldAdv{

}

class RadiantXpAdv{

}

class Bans{
    public boolean isPicked;
    public int heroId;
    public int team;
    public int order;
}

class Objectives{

}

class Cosmetics{

}

class Draft{
    public int order;
    public boolean isPicked;
    public int activeTeam;
    public int heroPicked;
    public int slot;
    public int extraTime;
    public int totalTimeTaken;
}
