package com.example.myapp.Controller;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.myapp.Model.PlayerSearch;
import com.example.myapp.R;

public class PlayerInfoActivity extends AppCompatActivity {

    PlayerSearch data;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        data = (PlayerSearch) getIntent().getSerializableExtra("PlayerInfo");
        viewPager = findViewById(R.id.main_view_pager);
        tabLayout = findViewById(R.id.main_tab_layout);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putSerializable("player", data);

        // Create profile fragment
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);
        adapter.addFragment(profileFragment, "Profile");

        // Create matches fragment
        MatchesFragment matchesFragment = new MatchesFragment();
        matchesFragment.setArguments(bundle);
        adapter.addFragment(matchesFragment, "Matches");

        viewPager.setAdapter(adapter);
    }
}
