package com.app.trusec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {
    public CommonUtil() {
    }

    void sendHttpReqToGetShiftDataForCurrentWeek(final String secureCompanyUrl, final String userId, final Context context) {
        RequestQueue shiftDataRequestQ = Volley.newRequestQueue(context);


        StringRequest shiftDataRequest = new StringRequest(Request.Method.POST, secureCompanyUrl + context.getString(R.string.URL_GET_SHIFT),
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
                            displayShift.putExtra(context.getString(R.string.CURRENT_VISIBLE_PAGE_URL), secureCompanyUrl + context.getString(R.string.URL_GET_SHIFT));
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
                VolleyLog.e("Error: ", "Error while fetching shift data. " + error.getMessage());
                Toast.makeText(context.getApplicationContext(), "Error while fetching shift data", Toast.LENGTH_LONG).show();
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

    void sendHttpReqToGetShiftData(final String url, final String userId, final Context context) {
        RequestQueue shiftDataRequestQ = Volley.newRequestQueue(context);


        StringRequest shiftDataRequest = new StringRequest(Request.Method.POST, url ,
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
                            displayShift.putExtra(context.getString(R.string.CURRENT_VISIBLE_PAGE_URL), url);
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
                VolleyLog.e("Error: ", "Error while fetching shift data. " + error.getMessage());
                Toast.makeText(context.getApplicationContext(), "Error while fetching shift data", Toast.LENGTH_LONG).show();
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