package io.github.septianrin.hidrocon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import io.github.septianrin.hidrocon.adapter.IntroViewPagerAdapter;
import io.github.septianrin.hidrocon.model.ScreenItem;

public class IntroActivity extends AppCompatActivity {

    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    private ViewPager screenPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //make activity fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //hide action bar
        //getSupportActionBar().hide();

        //add view
        btnNext = findViewById(R.id.btn_next);
        tabIndicator = findViewById(R.id.tab_indicator);

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Grow Your Plant", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam vel dictum enim. Suspendisse ac viverra orci. Praesent commodo libero ac sagittis sodales. ", R.drawable.gardening));
        mList.add(new ScreenItem("Monitor Them Easily", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam vel dictum enim. Suspendisse ac viverra orci. Praesent commodo libero ac sagittis sodales. ", R.drawable.analytics));
        mList.add(new ScreenItem("Control and Give\nWhat They Want", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam vel dictum enim. Suspendisse ac viverra orci. Praesent commodo libero ac sagittis sodales. ", R.drawable.control));

        //setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        //setup tabLayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        //next buttom click listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if (position == mList.size()) {
                    // when reach to n item
                    loadLastScreen();

                }
            }
        });
    }

    private void loadLastScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
