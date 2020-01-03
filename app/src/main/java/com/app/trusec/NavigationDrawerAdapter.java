package com.app.trusec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {


    public static final String NAV_ITEM_LOGOUT = "Logout";
    public static final String NAV_ITEM_ROSTER = "Roster";
    public static final String NAV_ITEM_LEAVES = "Manage Leaves";
    public static final String NAV_ITEM_PROFILE = "Profile";
    private List<NavigationDrawerItem> mDataList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private SessionManager session;
    TextView userDisplayName;
    TextView companyName;


    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mDataList = data;
        session = new SessionManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        userDisplayName = (TextView) ((Activity)context).findViewById(R.id.userDisplayName);
        companyName = (TextView) ((Activity)context).findViewById(R.id.companyName);
        userDisplayName.setText(session.getUserDetails().get(SessionManager.KEY_USER_DISPLAY_NAME));
        companyName.setText(session.getUserDetails().get(SessionManager.KEY_COMPANY_NAME));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        NavigationDrawerItem current = mDataList.get(position);

	    holder.imgIcon.setImageResource(current.getImageId());
        holder.title.setText(current.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            switch ( holder.title.getText().toString()){
                    case NAV_ITEM_ROSTER:
                        /*DrawerLayout mDrawerLayout;
                        mDrawerLayout = (DrawerLayout) ((Activity)context).findViewById(R.id.drawer_layout);
                        mDrawerLayout.closeDrawers();*/
                        HashMap<String, String> user = session.getUserDetails();
                        sendHttpReqToGetShiftData(user.get(SessionManager.KEY_COMPANY_URL), user.get(SessionManager.KEY_USER_ID));
                        break;
                    case NAV_ITEM_LOGOUT:
                        session.logoutUser();
                        break;
                    case NAV_ITEM_LEAVES:
                        Intent intent = new Intent(context, MyLeavesActivity.class);
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case NAV_ITEM_PROFILE:
                        Intent profileActivityIntent = new Intent(context, MyProfileActivity.class);
                        context.startActivity(profileActivityIntent);
                        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                }


	        }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
	    ImageView imgIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
	        imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
        }
    }

    private void sendHttpReqToGetShiftData(final String secureCompanyUrl, final String userId) {
        RequestQueue shiftDataRequestQ = Volley.newRequestQueue(context);


        StringRequest shiftDataRequest = new  StringRequest(Request.Method.POST, secureCompanyUrl+context.getString(R.string.URL_GET_SHIFT),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Intent displayShift = new Intent(context, DisplayShiftActivity.class);
                            displayShift.putExtra(context.getString(R.string.JSON_KEY_RESPONSE), jsonResponse.toString());
                            displayShift.putExtra(context.getString(R.string.USER_ID), userId);
                            displayShift.putExtra(context.getString(R.string.IS_CURRENT_WEEK), true);
                            /*displayShift.setFlags(displayShift.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);*/
                            context.startActivity(displayShift);
                            ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            // finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", "Error while fetching shift data. "+ error.getMessage());
                Toast.makeText(context, "Error while fetching shift data", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(context.getString(R.string.put_staffId), userId);
                return MyData;
            }
        };

        shiftDataRequestQ.add(shiftDataRequest);
    }
}
