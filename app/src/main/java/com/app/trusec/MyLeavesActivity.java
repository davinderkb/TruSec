package com.app.trusec;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyLeavesActivity extends CustomAppCompatActivity {

    private SessionManager session;
    private ArrayList<LeaveDetails> appliedLeaves;
    private RecyclerView leaveRecyclerView;
    private LeaveRecyclerViewAdapter leaveRecyclerViewAdapter;
    private SwipeRefreshLayout pullToRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_time_off);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        leaveRecyclerView = findViewById(R.id.leaveItemRecyclerView);
        session = new SessionManager(this);
        appliedLeaves = new ArrayList<>();
        setPullToRefresh();
        sendHttpRequestToGetAppliedLeaves();
        setUpDrawer();
    }

    private void setPullToRefresh() {
        pullToRefresh = findViewById(R.id.pullToRefreshLeaves);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            sendHttpRequestToGetAppliedLeaves();
            pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_timeoff_activity_toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.applyLeaveMenuItem: {
                Intent intent = new Intent(this,ApplyLeaveActivity.class);
                this.startActivity(intent);
                this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            }

        }
        return true;
    }
    private void setUpDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);

    }

    private void sendHttpRequestToGetAppliedLeaves() {
        RequestQueue getLeavesDetailsRequestQ = Volley.newRequestQueue(this);
        String url =  session.getUserDetails().get(SessionManager.KEY_COMPANY_URL) + getResources().getString(R.string.URL_GET_APPLIED_LEAVES)+  session.getUserDetails().get(SessionManager.KEY_USER_ID);

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            appliedLeaves.clear();
                            for(int i=0;i< response.length();i++){
                                JSONObject obj = response.getJSONObject(i).getJSONObject("ApplyForHoliday");
                                appliedLeaves.add(new LeaveDetails(obj.getString("on_date"),obj.getString("to_date"),obj.getString("reason"),obj.getString("status")));
                            }
                            leaveRecyclerViewAdapter = new LeaveRecyclerViewAdapter(appliedLeaves, MyLeavesActivity.this);
                            leaveRecyclerView.setAdapter(leaveRecyclerViewAdapter);
                            leaveRecyclerView.setLayoutManager(new LinearLayoutManager(MyLeavesActivity.this));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", "Error while fecthing applied leaves " + error.getMessage());
                        Toast.makeText(MyLeavesActivity.this, "Error while fecthing applied leaves, Try again", Toast.LENGTH_LONG).show();
                    }
                }
        );
        getLeavesDetailsRequestQ.add(getRequest);
    }
}
