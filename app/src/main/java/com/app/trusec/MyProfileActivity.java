package com.app.trusec;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends CustomAppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView mobile;
    private TextView nameInitials;
    private SessionManager session;
    private ImageView imageView;
    private Button editProfileButton;
    private ImageView uploadLicence;
    private int PICK_IMAGE_REQUEST = 1;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        initComponents();
        session = new SessionManager(this);
        sendHttpReqToGetProfileDetail();
        setUpDrawer();

    }

    private void initComponents() {
        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        mobile = findViewById(R.id.profilePhone);
        nameInitials = findViewById(R.id.profileInitials);
        imageView = findViewById(R.id.imageView);
        editProfileButton = findViewById(R.id.editProfileButton);
        uploadLicence = findViewById(R.id.uploadLicence);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                intent.putExtra(getString(R.string.EDIT_PROFILE_FIRST_NAME), firstName);
                intent.putExtra(getString(R.string.EDIT_PROFILE_LAST_NAME), lastName);
                intent.putExtra(getString(R.string.EDIT_PROFILE_EMAIL), email.getText().toString());
                intent.putExtra(getString(R.string.EDIT_PROFILE_MOBILE), mobile.getText().toString());
                MyProfileActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        uploadLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog chooseIntent = new Dialog(MyProfileActivity.this);
                //chooseNumDialog.setTitle("Select a phone number to call...");
                chooseIntent.setContentView(R.layout.upload_license_choose_intent_popup);
                LinearLayout camera =  chooseIntent.findViewById(R.id.chooseCamera);
                LinearLayout gallery = chooseIntent.findViewById(R.id.chooseGallery);
                chooseIntent.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Window window = chooseIntent.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.gravity = Gravity.BOTTOM;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                chooseIntent.show();

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        chooseIntent.dismiss();
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseIntent.dismiss();
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*|file/*");
                        startActivityForResult(intent, 1);
                    }
                });
            }
        });
    }


    void sendHttpReqToGetProfileDetail() {
        RequestQueue profileDataRequestQ = Volley.newRequestQueue(MyProfileActivity.this);

        String userId = session.getUserDetails().get(SessionManager.KEY_USER_ID);
        final String url = session.getUserDetails().get(SessionManager.KEY_COMPANY_URL) + this.getString(R.string.URL_GET_PROFILE) + userId;
        StringRequest profileDataRequest = new StringRequest(Request.Method.POST, url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            firstName = (String) jsonResponse.get("first_name");
                            lastName = (String) jsonResponse.get("last_name");
                            name.setText(firstName + " " + lastName);
                            mobile.setText((String)jsonResponse.get("mobile"));
                            email.setText((String)jsonResponse.get("email"));
                            if(firstName != null && lastName != null)
                                nameInitials.setText(firstName.substring(0,1) + lastName.substring(0,1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", "Error while fetching profile info. " + error.getMessage());
                Toast.makeText(MyProfileActivity.this, "Error while fetching profile info", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                return MyData;
            }
        };

        profileDataRequestQ.add(profileDataRequest);
    }

    private void setUpDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                }
                break;
        }
    }

}
