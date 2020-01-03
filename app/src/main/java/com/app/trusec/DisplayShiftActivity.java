package com.app.trusec;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class DisplayShiftActivity extends CustomAppCompatActivity {
    JSONObject jsonResponse;
    String userId;

    public boolean isCurrentWeek() {
        return isCurrentWeek;
    }

    boolean isCurrentWeek;
    WeeklyShiftData weeklyShiftData;
    RecyclerView shiftRecyclerView;
    ShiftListViewAdapter shiftListViewAdapter;
    private String currentVisibleRosterUrl;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_shift);
        //setLogout();
        setUpDrawer();
        try {
            jsonResponse = new JSONObject(getIntent().getStringExtra(getString(R.string.JSON_KEY_RESPONSE)));
            userId = getIntent().getStringExtra(getString(R.string.USER_ID));
            isCurrentWeek = getIntent().getExtras().getBoolean(getString(R.string.IS_CURRENT_WEEK));
            currentVisibleRosterUrl = getIntent().getStringExtra(getString(R.string.CURRENT_VISIBLE_PAGE_URL));
            weeklyShiftData = new WeeklyShiftData(jsonResponse, userId, isCurrentWeek);
            if(currentVisibleRosterUrl ==null){
                currentVisibleRosterUrl = "";
            }
            shiftListViewAdapter = new ShiftListViewAdapter(weeklyShiftData.getShiftHeader(), weeklyShiftData, currentVisibleRosterUrl);
            shiftRecyclerView = findViewById(R.id.shiftItemRecyclerView);
            shiftRecyclerView.setAdapter(shiftListViewAdapter);
            shiftRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shiftListViewAdapter.refreshData();
                pullToRefresh.setRefreshing(false);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void setUpDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);

    }


}
