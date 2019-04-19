package com.example.myapp.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.myapp.Model.Search;
import com.example.myapp.R;

public class PlayerInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        Search searchData = (Search)getIntent().getSerializableExtra("PlayerInfo");
        System.out.println(searchData.account_id);
    }

    @Override
    public void onBackPressed(){
//        Intent intent = new Intent(this, SearchActivity.class);
//        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
