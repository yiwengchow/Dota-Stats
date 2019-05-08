package com.example.myapp.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Player implements Serializable{
    public int account_id;
    public String name;
    public String avatarPath;
    public String lastMatchTime = "None";
    public int wins;
    public int loses;
    public String soloMMR;
    public String partyMMR;
    public int estimateMMR;
    public int rank;
    public int leaderboard;

    private int [] pixels;
    private int width , height;

    public void setBitmap(Bitmap bitmap){
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        pixels = new int [width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);
    }

    public Bitmap getBitmap(){
        try{
            return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        }
        catch (Exception e){
            return null;
        }
    }
}
