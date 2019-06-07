package com.example.myapp;

import android.content.Context;

import com.example.myapp.Controller.ActivityMain;
import com.example.myapp.Model.Player;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public void addToFavourites(Context context, Player player){
        player.isFavourited = true;

        Repository.getInstance().favouritesList.add(player);
        // Write to file
        try{
            ObjectOutputStream out = new ObjectOutputStream(context.openFileOutput("favourites.ser", context.MODE_PRIVATE));
            out.writeObject(Repository.getInstance().favouritesList);
            out.close();
        }
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }

    public void updateFavouritesList(Context context){
        try {
            ObjectInputStream in = new ObjectInputStream(context.openFileInput("favourites.ser"));
            Repository.getInstance().favouritesList = (ArrayList<Player>) in.readObject();
            in.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Serialized class not found");
            c.printStackTrace();
        }
    }

    public void deleteFavourite(Context context, Player player){
        player.isFavourited = false;

        try{
            for (Player p : Repository.getInstance().favouritesList){
                if (p.account_id == player.account_id){
                    Repository.getInstance().favouritesList.remove(p);
                    break;
                }
            }
            ObjectOutputStream out = new ObjectOutputStream(context.openFileOutput("favourites.ser", context.MODE_PRIVATE));
            out.writeObject(Repository.getInstance().favouritesList);
            out.close();
        }
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }

    public void clearFavourites(Context context){
        context.deleteFile("favourites.ser");
    }
}
