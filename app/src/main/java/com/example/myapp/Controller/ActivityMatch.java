package com.example.myapp.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.myapp.Model.Match;
import com.example.myapp.R;

public class ActivityMatch extends AppCompatActivity {
    Match match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        match = (Match)getIntent().getSerializableExtra("match");
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
