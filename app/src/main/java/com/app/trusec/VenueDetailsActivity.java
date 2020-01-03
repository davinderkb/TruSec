package com.app.trusec;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VenueDetailsActivity extends AppCompatActivity implements LocationListener {
    private TextView locationTextView;
    private TextView mobileTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView reportingManagerTextView;
    private TextView reportingSupervisorTextView;
    private ImageView backArrow;
    private TextView dateTitle;
    private String selectedDate;
    private String selectedDay;
    private boolean isAllowCheckIn;
    private String shiftTime;
    private TextView clientNameText;
    private JSONObject jsonResponse;
    private ClientDetails clientDetails;
    private VenueDetails venueDetails;
    private View titleSideColorView;
    private String rosterStatus;
    private boolean isMobilePresent;
    private boolean isPhonePresent;
    private ImageView callIcon;
    private ImageView locationIcon;
    private ImageView emailIcon;
    private Button checkInButton;
    private Button checkOutButton;
    private TextView checkInTime;
    private TextView checkOutTime;
    private String checkInTimeValue;
    private String checkOutTimeValue;
    private  LocationManager locationManager;
    private String provider;
    private String shiftId;
    private double distance;
    private boolean isLocationAccess;
    SessionManager session;
    private String companyUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shift_details);
        try {
            jsonResponse = new JSONObject(getIntent().getStringExtra(getString(R.string.JSON_KEY_RESPONSE)));
            rosterStatus = getIntent().getStringExtra(getString(R.string.ROSTER_STATUS));
            clientDetails = new ClientDetails((JSONObject) jsonResponse.get("Client"));
            venueDetails = new VenueDetails((JSONObject) jsonResponse.get("ClientInformation"));
            isAllowCheckIn = getIntent().getBooleanExtra(getString(R.string.IS_ALLOW_CHECK_IN), false);
            selectedDate = getDateToday(getIntent().getStringExtra(getString(R.string.DATE_SELECTED)));
            shiftTime = getIntent().getStringExtra(getString(R.string.SHIFT_TIME));
            selectedDay = getIntent().getStringExtra(getString(R.string.DAY_SELECTED));
            shiftId = getIntent().getStringExtra(getString(R.string.SHIFT_ID));
            session = new SessionManager(this);
            companyUrl = session.getUserDetails().get(SessionManager.KEY_COMPANY_URL);
            initializeFields();
            setBackPageListener();
            setClientVenueDetails();
            initializeIcons();
            handleCheckIn();
            handleCheckOut();
            checkLocationPermission();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(new Criteria(), false);
            setCheckInOutState();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setCheckInOutState() {
        checkInTimeValue = "";
        checkOutTimeValue = "";
        RequestQueue getCheckInOutTimeRequestQ = Volley.newRequestQueue(this);

        final StringRequest getCheckInOutTimeRequest = new StringRequest(Request.Method.POST, companyUrl+getResources().getString(R.string.URL_GET_CHECK_IN_OUT_TIME),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            checkInTimeValue = (String) jsonResponse.get("check_in");
                            checkOutTimeValue = (String) jsonResponse.get("check_out");
                            if(!checkInTimeValue.equalsIgnoreCase("00:00:00"))
                                setCheckInState(checkInTimeValue);
                            if(!checkOutTimeValue.equalsIgnoreCase("00:00:00"))
                                setCheckOutState(checkOutTimeValue);
                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VenueDetailsActivity.this, "Get Check In/Out Time request failed", Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(VenueDetailsActivity.this.getString(R.string.put_userId),session.getUserDetails().get(SessionManager.KEY_USER_ID));
                MyData.put(VenueDetailsActivity.this.getString(R.string.put_shiftId),shiftId);
                return MyData;
            }
        };

        getCheckInOutTimeRequestQ.add(getCheckInOutTimeRequest);
    }


    private void handleCheckOut() {
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserConfirmation(getResources().getString(R.string.OPERATION_CHECK_OUT));
            }
        });
    }

    private void setCheckOutState(String checkOutTimeValue) {
        checkOutButton.setVisibility(View.GONE);
        checkOutTime.setVisibility(View.VISIBLE);
        checkOutTime.setText(checkOutTime.getText()+checkOutTimeValue);
    }

    private void handleCheckIn() {
        if (!isAllowCheckIn) {
            checkInButton.setVisibility(View.GONE);
        }
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllowCheckIn) {
                    getUserConfirmation(getResources().getString(R.string.OPERATION_CHECK_IN));
                }
            }
        });
    }

    private void getUserConfirmation(final String operationRequested) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VenueDetailsActivity.this);
        alertDialogBuilder.setTitle(" "+operationRequested);
        alertDialogBuilder.setMessage("You must be at work premises otherwise request will fail.");
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(VenueDetailsActivity.this, android.R.drawable.ic_dialog_info);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.checkInColor));
        alertDialogBuilder.setIcon(wrappedDrawable);


        alertDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                verifyLocationAndSendRequest(operationRequested);
            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        // Showing Alert Message
        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.googleBlue));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.googleBlue));

            }
        });
        alertDialog.show();
    }

    private void verifyLocationAndSendRequest(String operationRequested) {
        Toast.makeText(getApplicationContext(), "Verifying location.. Please wait", Toast.LENGTH_LONG).show();
        if (checkLocationPermission()) {
            Location location = getLastKnownLocation();
            double longitude = 0;
            double latitude = 0;
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            }

            float[] result = new float[1];
            if((clientDetails.getLocLatitude()!= null && clientDetails.getLocLatitude().equals(""))
                    || (clientDetails.getLocLongitude()!= null && clientDetails.getLocLongitude().equals(""))){
                Toast.makeText(getApplicationContext(), "Venue location does not exist. Please ask admin to upload correct location details", Toast.LENGTH_LONG).show();
                return;
            }
            Location.distanceBetween(latitude, longitude, Double.parseDouble(clientDetails.getLocLatitude()), Double.parseDouble(clientDetails.getLocLongitude()), result);
            distance = result[0];
            if(result[0]<=500) {
                Toast.makeText(getApplicationContext(), "Location verified successfully.\nRequest in progress..", Toast.LENGTH_LONG).show();
                sendCheckInOrOutRequest(operationRequested);
            }
            else {
                Toast.makeText(getApplicationContext(), operationRequested+" failed. \nYou are away by "+String.format("%.2f", distance/1000)+" KM.\nTry again once there.", Toast.LENGTH_LONG).show();
            }
        } else{
            if(!isLocationAccess){
                Toast.makeText(getApplicationContext(), "Please provide location access and then try again", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(VenueDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }
    }

    private void sendCheckInOrOutRequest(final String operationRequested) {
        final boolean isCheckInOperation = operationRequested.equals(getResources().getString(R.string.OPERATION_CHECK_IN));
        String url = "";
        if(isCheckInOperation){
            url = companyUrl+getResources().getString(R.string.URL_CHECK_IN);
        }else{
            url = companyUrl+getResources().getString(R.string.URL_CHECK_OUT);
        }

        RequestQueue requestQ = Volley.newRequestQueue(this);


        final StringRequest checkInOrOutRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            if(isCheckInOperation){
                                setCheckInState(response);
                            }else{
                                setCheckOutState(response);
                            }
                            Toast.makeText(VenueDetailsActivity.this, operationRequested+" Successful", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VenueDetailsActivity.this, "Request failed, Try again.", Toast.LENGTH_LONG).show();
            }

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(VenueDetailsActivity.this.getString(R.string.put_userId),session.getUserDetails().get(SessionManager.KEY_USER_ID));
                MyData.put(VenueDetailsActivity.this.getString(R.string.put_shiftId),shiftId);
                return MyData;
            }
        };

        requestQ.add(checkInOrOutRequest);
    }

    private void setCheckInState(String checkInTimeValue) {
        checkInButton.setVisibility(View.GONE);
        checkInTime.setVisibility(View.VISIBLE);
        checkInTime.setText(checkInTime.getText()+checkInTimeValue);
        checkOutButton.setVisibility(View.VISIBLE);
    }

    private void initializeIcons() {
        callIcon = findViewById(R.id.iconVenueCall);
        emailIcon = findViewById(R.id.iconVenueEmail);
        locationIcon = findViewById(R.id.iconVenueLocation);

        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMobilePresent && isPhonePresent){
                    displayAllNumbersToCall();
                } else if(isMobilePresent || isPhonePresent){
                    String numberToBeCalled = isMobilePresent? clientDetails.getMobile() : clientDetails.getPhone();
                    callNumber(numberToBeCalled);
                } else{

                }

            }

            private void callNumber(String numberToBeCalled) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", numberToBeCalled, null));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            private void displayAllNumbersToCall() {
                final Dialog chooseNumDialog = new Dialog(VenueDetailsActivity.this);
                //chooseNumDialog.setTitle("Select a phone number to call...");
                chooseNumDialog.setContentView(R.layout.call_dialog);
                LinearLayout phoneNumLayout = (LinearLayout) chooseNumDialog.findViewById(R.id.clickablePhoneNumber);
                LinearLayout mobileNumLayout = ( LinearLayout) chooseNumDialog.findViewById(R.id.clickableMobileNumber);
                TextView phoneNum =chooseNumDialog.findViewById(R.id.phoneNumText);
                phoneNum.setText(clientDetails.getPhone());
                TextView mobileNum =chooseNumDialog.findViewById(R.id.mobileNumText);
                mobileNum.setText(clientDetails.getMobile());
                chooseNumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                chooseNumDialog.show();
                phoneNumLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseNumDialog.dismiss();
                        callNumber(clientDetails.getPhone());
                    }
                });
                mobileNumLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseNumDialog.dismiss();
                        callNumber(clientDetails.getMobile());
                    }
                });
            }
        });

        emailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{clientDetails.getEmail()});
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        locationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    clientDetails.getLocLatitude();
                    clientDetails.getLocLongitude();
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+clientDetails.getLocLatitude()+","+  clientDetails.getLocLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private String getDateToday(String date) {
        if (isAllowCheckIn)
            return getString(R.string.TODAY);
        return date;
    }


    private void setBackPageListener() {
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             VenueDetailsActivity.super.onBackPressed();
                                             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                         }
                                     }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private Location getLastKnownLocation() {
        Location l=null;
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void setClientVenueDetails() {

        dateTitle.setText(selectedDate.equalsIgnoreCase(getString(R.string.TODAY)) ? getString(R.string.TODAY)+"\n"+ shiftTime :  selectedDate+" ("+selectedDay+")\n"+shiftTime );
        reportingManagerTextView.setText(venueDetails.getReportingManager());
        reportingSupervisorTextView.setText(venueDetails.getReportingSupervisor());
        clientNameText.setText(clientDetails.getFullName());
        emailTextView.setText(clientDetails.getEmail());
        locationTextView.setText(venueDetails.getStreet() + "\n" + venueDetails.getArea() + ", " + venueDetails.getPostCode() + "\n" + venueDetails.getState());

        int topPaddingMultiplePhoneNumbers = 8;
        if (!clientDetails.getMobile().equalsIgnoreCase("")) {
            mobileTextView.setPadding(getDpPaddingInInteger(8),getDpPaddingInInteger(8), 0, 0);
            mobileTextView.setText(clientDetails.getMobile());
            topPaddingMultiplePhoneNumbers = 4;
            isMobilePresent = true;
        } else{
            mobileTextView.setVisibility(View.GONE);
            isMobilePresent = false;
        }

        if (!clientDetails.getPhone().equalsIgnoreCase("")) {
            phoneTextView.setPadding(getDpPaddingInInteger(8),getDpPaddingInInteger(topPaddingMultiplePhoneNumbers), 0, 0);
            phoneTextView.setText(clientDetails.getPhone());
            isPhonePresent = true;
        } else{
            isPhonePresent = false;
        }


        /*if (rosterStatus.equals(getString(R.string.ROSTER_CONFIRM_ACCEPT))) {
            titleSideColorView.setBackgroundColor(getResources().getColor(R.color.acceptColor));
        } else if (rosterStatus.equals(getString(R.string.ROSTER_CONFIRM_DECLINE))) {
            titleSideColorView.setBackgroundColor(getResources().getColor(R.color.declineColor));
        } else {
            titleSideColorView.setBackgroundColor(getResources().getColor(R.color.skyblue));
        }*/

    }

    private void initializeFields() {
        locationTextView = findViewById(R.id.venueLocationText);
        mobileTextView = findViewById(R.id.venueMobileText);
        phoneTextView = findViewById(R.id.venuePhoneText);
        emailTextView = findViewById(R.id.venueEmailText);
        reportingManagerTextView = (findViewById(R.id.venueReportManagerText));
        reportingSupervisorTextView = (findViewById(R.id.venueReportSupervisorText));
        dateTitle = (findViewById(R.id.dateTitle));
        clientNameText = (findViewById(R.id.clientNameText));
        titleSideColorView = findViewById(R.id.venueSideColorView);
        checkInButton = findViewById(R.id.checkIn);
        checkOutButton = findViewById(R.id.checkOut);
        checkInTime = findViewById(R.id.checkInTime);
        checkOutTime = findViewById(R.id.checkOutTime);
    }


    private int getDpPaddingInInteger(int paddingInDp) {
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (paddingInDp * scale + 0.5f);
        return padding_in_px;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void     onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        isLocationAccess = true;
                        Toast.makeText(getApplicationContext(), "Location access granted successfully", Toast.LENGTH_LONG).show();
                    }
                }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                   isLocationAccess = false;
                   if(isAllowCheckIn)
                        Toast.makeText(getApplicationContext(), "Location access denied", Toast.LENGTH_LONG).show();
                   else
                       Toast.makeText(getApplicationContext(), "You denied the location access", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Location access denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            if ( provider == null ) {
                return;
            }
            locationManager.requestLocationUpdates(provider, 400, 1, VenueDetailsActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(VenueDetailsActivity.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

