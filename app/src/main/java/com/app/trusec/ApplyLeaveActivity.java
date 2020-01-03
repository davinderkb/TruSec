package com.app.trusec;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ApplyLeaveActivity extends AppCompatActivity {

    private LinearLayout fromDate;
    private LinearLayout toDate;
    private TextView fromDateText;
    private TextView toDateText;
    private TextView reasonText;
    private DatePickerDialog.OnDateSetListener mFromDateSetListener;
    private DatePickerDialog.OnDateSetListener mToDateSetListener;
    private Button applyButton;
    private SessionManager session;
    private ImageView backArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_leave);
        session = new SessionManager(this);
        reasonText = findViewById(R.id.leaveReasonText);
        initFromDate();
        initToDate();
        initApplyButton();
        setBackPageListener();

    }

    private void setBackPageListener() {
        backArrow = findViewById(R.id.backArrowApplyLeave);
        backArrow.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             ApplyLeaveActivity.super.onBackPressed();
                                             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                         }
                                     }
        );
    }

    private void initApplyButton() {
        applyButton = findViewById(R.id.applyLeave);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromDateText.getText().toString().equals("")){
                    Toast.makeText(ApplyLeaveActivity.this,"Please select \"From Date\"", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(toDateText.getText().toString().equals("")){
                    toDateText.setText(fromDateText.getText());
                }
                Toast.makeText(ApplyLeaveActivity.this,"Request in progress...", Toast.LENGTH_SHORT).show();
                sendApplyLeaveRequest();
            }
        });
    }

    private void sendApplyLeaveRequest() {
        RequestQueue applyLeaveRequestQ = Volley.newRequestQueue(this);

        final StringRequest applyLeaveRequest = new StringRequest(Request.Method.POST, session.getUserDetails().get(SessionManager.KEY_COMPANY_URL) + getResources().getString(R.string.URL_APPLY_LEAVE),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = (String)jsonObject.get("output");
                            int resultCode = (Integer)jsonObject.get("response");

                            if(resultCode==201){
                                Toast.makeText(ApplyLeaveActivity.this, "Leave request sent for approval", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ApplyLeaveActivity.this, MyLeavesActivity.class);
                                ApplyLeaveActivity.this.startActivity(intent);
                                ApplyLeaveActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else{
                                Toast.makeText(ApplyLeaveActivity.this, result, Toast.LENGTH_SHORT).show();
                               // fromDateText.setText("");
                               // toDateText.setText("");
                                //reasonText.setText("");
                            }
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ApplyLeaveActivity.this, "Time-Off request failed, Try again", Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(ApplyLeaveActivity.this.getString(R.string.put_userId), session.getUserDetails().get(SessionManager.KEY_USER_ID));
                MyData.put(ApplyLeaveActivity.this.getString(R.string.put_fromDate), fromDateText.getText().toString());
                MyData.put(ApplyLeaveActivity.this.getString(R.string.put_toDate), toDateText.getText().toString());
                MyData.put(ApplyLeaveActivity.this.getString(R.string.put_Reason), reasonText.getText().toString());
                return MyData;
            }
        };

        applyLeaveRequestQ.add(applyLeaveRequest);
    }

    private void setUpDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
    }

    private void initFromDate() {
        fromDate = findViewById(R.id.fromDate);
        fromDateText = findViewById(R.id.fromDateText);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ApplyLeaveActivity.this,
                        R.style.DialogTheme,
                        mFromDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawableResource(R.color.white);
                dialog.show();
            }
        });

        mFromDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                fromDateText.setText(date);
            }
        };
    }


    private void initToDate() {
        toDate = findViewById(R.id.toDate);
        toDateText = findViewById(R.id.toDateText);


        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fromDateText.getText().equals("")) {
                    Toast.makeText(ApplyLeaveActivity.this, "Please choose \"From Date\" first", Toast.LENGTH_SHORT).show();

                } else {

                    Calendar cal = Calendar.getInstance();
                    int year = 0;
                    int month = 0;
                    int day = 0;
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        cal.setTime(dateFormat.parse(fromDateText.getText().toString()));
                        year = cal.get(Calendar.YEAR);
                        month = cal.get(Calendar.MONTH);
                        day = cal.get(Calendar.DAY_OF_MONTH);
                    } catch (ParseException e) {
                        year = cal.get(Calendar.YEAR);
                        month = cal.get(Calendar.MONTH);
                        day = cal.get(Calendar.DAY_OF_MONTH);
                    }


                    DatePickerDialog dialog = new DatePickerDialog(
                            ApplyLeaveActivity.this,
                            R.style.DialogTheme,
                            mToDateSetListener,
                            year, month, day);

                    dialog.getDatePicker().setMinDate(cal.getTimeInMillis());

                    dialog.getWindow().setBackgroundDrawableResource(R.color.white);
                    dialog.show();

                }
            }
        });

        mToDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                toDateText.setText(date);
            }
        };
    }
}
