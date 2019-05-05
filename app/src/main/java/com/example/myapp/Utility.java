package com.example.myapp;

import android.app.Activity;

import com.example.myapp.Controller.SearchActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Utility {
    private static final Utility ourInstance = new Utility();

    public static Utility getInstance() {
        return ourInstance;
    }

    private Utility() {
    }

    public String readJsonFile(String fileName){
        String json = null;
        try {
            InputStream is = SearchActivity.instance.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public String getGameModeById(int id){
        try{
            JSONObject gameModeJson = new JSONObject(Utility.getInstance().readJsonFile("DotaConstants/game_mode.json"));
            return gameModeJson.getJSONObject(String.valueOf(id)).getString("name");
        }
        catch (JSONException e){
            return null;
        }
    }

    public String getLobbyTypeById(int id){
        try{
            JSONObject gameModeJson = new JSONObject(Utility.getInstance().readJsonFile("DotaConstants/lobby_type.json"));
            return gameModeJson.getJSONObject(String.valueOf(id)).getString("name");
        }
        catch (JSONException e){
            return null;
        }
    }
}
