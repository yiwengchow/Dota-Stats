package com.example.myapp.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.myapp.Model.Search;
import com.example.myapp.R;

public class PlayerInfoActivity extends AppCompatActivity {

    Search data;
    ImageView avatarImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        data = (Search)getIntent().getSerializableExtra("PlayerInfo");
        System.out.println(data.account_id);

        avatarImage = findViewById(R.id.avatar_image);
        avatarImage.setImageBitmap(data.getBitmap());
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
