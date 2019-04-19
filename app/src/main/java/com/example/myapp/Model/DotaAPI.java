package com.example.myapp.Model;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapp.Controller.SearchActivity;

public class DotaAPI {
    private static DotaAPI instance;
    public static DotaAPI getInstance(){
        if (instance == null) instance = new DotaAPI();
        return instance;
    }

    private RequestQueue requestQueue;

    public DotaAPI(){
        requestQueue = Volley.newRequestQueue(SearchActivity.instance);
    }

    public StringRequest getPlayerAsync(int id, Response.Listener<String> onSuccess, Response.ErrorListener onFailure){
        StringRequest stringReq = new StringRequest(Request.Method.GET, "https://api.opendota.com/api/players/" + id, onSuccess, onFailure);
        requestQueue.add(stringReq);
        return stringReq;
    }

    public StringRequest getPlayersByName(String name, Response.Listener<String> onSuccess, Response.ErrorListener onFailure){
        StringRequest stringReq = new StringRequest(Request.Method.GET, "https://api.opendota.com/api/Search?q=" + name, onSuccess, onFailure);
        requestQueue.add(stringReq);
        return stringReq;
    }
}
