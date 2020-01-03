package com.app.trusec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfirmDeclineActivity extends AppCompatActivity {

    private ImageView backArrow;
    private Button submitButton;
    private EditText reasonText;
    private ArrayList<DailyShiftData> selectedShifts;
    private String weekStartDate;
    private String weekEndDate;
    private SessionManager session;
    private String companyURL;
    private String currentVisiblePageUrl;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_decline);
        selectedShifts = (ArrayList<DailyShiftData>) getIntent().getSerializableExtra(this.getString(R.string.SELECTED_SHIFTS));
        weekStartDate =  getIntent().getStringExtra(this.getString(R.string.KEY_WEEK_START_DATE));
        weekEndDate =  getIntent().getStringExtra(this.getString(R.string.KEY_WEEK_END_DATE));
        currentVisiblePageUrl = getIntent().getStringExtra(getString(R.string.CURRENT_VISIBLE_PAGE_URL));;
        if(currentVisiblePageUrl == null){
            currentVisiblePageUrl = "";
        }
        session = new SessionManager(this);
        companyURL = session.getUserDetails().get(SessionManager.KEY_COMPANY_URL);
        userId = session.getUserDetails().get(SessionManager.KEY_USER_ID);
        initReasonText();
        initBackArrow();
        initSubmitButton();

    }

    private void initReasonText() {
        reasonText = findViewById(R.id.declineText);
    }

    private void initBackArrow() {
        backArrow = findViewById(R.id.backArrowDecline);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void initSubmitButton() {
        submitButton = findViewById(R.id.submitDecline);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidReasonText()){
                   displayWarning();
                   return;
                }
                for(DailyShiftData shiftData:selectedShifts){
                     sendHttpReqToDeclineRoster(shiftData);
                }

            }


        });
    }

    private void displayWarning() {
        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmDeclineActivity.this);
        alertDialogBuilder.setTitle("Failed");
        alertDialogBuilder.setMessage("Please provide valid reason and try again");
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(ConfirmDeclineActivity.this, android.R.drawable.ic_dialog_alert);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.checkInColor));
        alertDialogBuilder.setIcon(wrappedDrawable);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.googleBlue));
            }
        });
        alertDialog.show();
    }

    private boolean isValidReasonText() {
        if(reasonText.getText().toString().trim().equals(""))
            return false;
        return true;

    }

    private void sendHttpReqToDeclineRoster(DailyShiftData shiftData) {
        RequestQueue declineRosterRequestQ = Volley.newRequestQueue(ConfirmDeclineActivity.this);

        final String shiftId = shiftData.getShiftId();
        final String shiftDate = shiftData.getShiftDate();


        final StringRequest declineRosterRequest = new StringRequest(Request.Method.POST, companyURL +getResources().getString(R.string.URL_DECLINE_ROSTER),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Toast.makeText(ConfirmDeclineActivity.this, "Decline request successful", Toast.LENGTH_LONG).show();
                            new CommonUtil().sendHttpReqToGetShiftData(currentVisiblePageUrl, userId,ConfirmDeclineActivity.this);

                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ConfirmDeclineActivity.this, "Decline Request failed", Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(ConfirmDeclineActivity.this.getString(R.string.put_declineRosterSadMessage),reasonText.getText().toString());
                MyData.put(ConfirmDeclineActivity.this.getString(R.string.put_declineRosterSval), "{\"rosterInfo\":[\"" + shiftId + ":" + shiftDate + ":" + userId + "\"],\"start_date\":\"" + weekStartDate + "\",\"end_date\":\"" + weekEndDate + "\",\"user_id\":\"" + userId + "\"}");
                return MyData;
            }
        };

        declineRosterRequestQ.add(declineRosterRequest);
    }

}
