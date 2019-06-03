package com.example.myapp;

import com.example.myapp.Controller.ActivityMain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
            InputStream is = ActivityMain.instance.getAssets().open(fileName);
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

    public String convertEpochToDatetime(int seconds){
        Date date = new Date(seconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
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
