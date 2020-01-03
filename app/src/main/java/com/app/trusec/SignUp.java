package com.app.trusec;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SignUp extends CustomAppCompatActivity {


    private EditText mEmail;
    private EditText mCompanyId;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();
    }

    private void initialize() {
        setCompanyIdFieldListener();
        setMobileFieldListener();
        setEmailFieldListener();
        setFirstNameFieldListener();
        setLastNameFieldListener();
        setBackToLoginButtonListener();
        setCreateAccountButtonListener();
    }

    private void setBackToLoginButtonListener() {
        Button backToLoginBtn = findViewById(R.id.signup_back_to_login);
        backToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    private void setCreateAccountButtonListener() {
        Button createAccButton = findViewById(R.id.create_account);
        createAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCompanyIdBlank() | isFirstNameBlank() | isLastNameBlank() | isMobileBlank() | (isEmailBlank() || isInvalidEmail()) )
                    return;
                sendRequestToGetCompanyUrl();
            }
        });
    }

    private void sendRequestToGetCompanyUrl() {
        RequestQueue companyUrlQ = Volley.newRequestQueue(SignUp.this);
        StringRequest companyUrlRequest = new StringRequest(Request.Method.POST, SignUp.this.getString(R.string.apiGetInfoUrl), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.equals(SignUp.this.getString(R.string.EMPTY_STRING))) {
                    sendRequestToCreateAccount(response);
                } else {
                    Toast.makeText(SignUp.this, SignUp.this.getString(R.string.INVALID_COMPANY_ID), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(getApplicationContext(), "Company ID does not exist", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(SignUp.this.getString(R.string.put_companyID), ((EditText) findViewById(R.id.signup_companyId)).getText().toString()); //Add the data you'd like to send to the server.
                return MyData;
            }
        };
        companyUrlQ.add(companyUrlRequest);
    }

    private void sendRequestToCreateAccount(String companyUrl) {
        RequestQueue userInfoRequestQ = Volley.newRequestQueue(SignUp.this);
        final String secureCompanyUrl = companyUrl.replace("http", "https");
        StringRequest userInfoRequest = new  StringRequest(Request.Method.POST, secureCompanyUrl + SignUp.this.getString(R.string.URL_SIGNUP),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if(response!=null)
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                Toast.makeText(getApplicationContext(), (CharSequence) jsonResponse.get(SignUp.this.getString(R.string.JSON_KEY_OUTPUT)), Toast.LENGTH_LONG).show();

                                if(jsonResponse.get(SignUp.this.getString(R.string.JSON_KEY_RESPONSE)).toString().equals(SignUp.this.getString(R.string.SUCCESSFUL_RESPONSE_CODE))){
                                    Intent i = new Intent(SignUp.this, LoginActivity.class);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(), "Create account request failed, Please enter correct details.", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(SignUp.this.getString(R.string.put_email), ((EditText) findViewById(R.id.signup_email)).getText().toString());
                MyData.put(SignUp.this.getString(R.string.put_firstName), ((EditText) findViewById(R.id.first_name)).getText().toString());
                MyData.put(SignUp.this.getString(R.string.put_lastName), ((EditText) findViewById(R.id.last_name)).getText().toString());
                MyData.put(SignUp.this.getString(R.string.put_mobile), ((EditText) findViewById(R.id.signup_mobile)).getText().toString());
                return MyData;
            }
        };

        userInfoRequestQ.add(userInfoRequest);
    }


    private boolean isEmailBlank() {
        boolean isBlank = false;
        final TextInputLayout tilEmail = (TextInputLayout) findViewById(R.id.input_layout_signup_email);
        mEmail = findViewById(R.id.signup_email);
        if (SignUp.this.getString(R.string.EMPTY_STRING).equals(mEmail.getText().toString())) {
            isBlank = true;
            mEmail.setError("");
            mEmail.clearFocus();
            mEmail.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            tilEmail.setError("Email cannot be empty");
        }
        return isBlank;
    }


    private boolean isInvalidEmail() {
        boolean isInvalidEmail = false;
        final TextInputLayout tilEmail = (TextInputLayout) findViewById(R.id.input_layout_signup_email);
        mEmail = findViewById(R.id.signup_email);
        if (!mEmail.getText().toString().trim().matches(SignUp.this.getString(R.string.emailValidatorRegex)) && !SignUp.this.getString(R.string.EMPTY_STRING).equals(mEmail.getText().toString())) {
            isInvalidEmail=true;
            mEmail.setError("");
            mEmail.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            tilEmail.setError("Invalid Email");
            mEmail.clearFocus();
        }
        return isInvalidEmail;
    }


    private boolean isCompanyIdBlank() {
        boolean isCompanyIdBlank = false;
        final TextInputLayout tilCompanyId = (TextInputLayout) findViewById(R.id.input_layout_signup_company);
        mCompanyId = findViewById(R.id.signup_companyId);
        if (SignUp.this.getString(R.string.EMPTY_STRING).equals(mCompanyId.getText().toString())) {
            isCompanyIdBlank = true;
            mCompanyId.setError("");
            mCompanyId.clearFocus();
            mCompanyId.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            tilCompanyId.setError("Company ID cannot be empty");
        }
        return isCompanyIdBlank;
    }

    private boolean isFirstNameBlank() {
        boolean isFirstNameBlank = false;
        final TextInputLayout tilFirstName = (TextInputLayout) findViewById(R.id.input_layout_firstName);
        mFirstName = findViewById(R.id.first_name);
        if (SignUp.this.getString(R.string.EMPTY_STRING).equals(mFirstName.getText().toString())) {
            isFirstNameBlank = true;
            mFirstName.setError("");
            mFirstName.clearFocus();
            mFirstName.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            tilFirstName.setError("First name cannot be empty");
        }
        return isFirstNameBlank;
    }

    private boolean isLastNameBlank() {
        boolean isLastNameBlank = false;
        final TextInputLayout tilFirstName = (TextInputLayout) findViewById(R.id.input_layout_lastName);
        mLastName = findViewById(R.id.last_name);
        if (SignUp.this.getString(R.string.EMPTY_STRING).equals(mLastName.getText().toString())) {
            isLastNameBlank = true;
            mLastName.setError("");
            mLastName.clearFocus();
            mLastName.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            tilFirstName.setError("Last name cannot be empty");
        }
        return isLastNameBlank;
    }
    private boolean isMobileBlank() {
        boolean isMobileBlank = false;
        final TextInputLayout tilMobile = (TextInputLayout) findViewById(R.id.input_layout_signup_mobile);
        mMobile = findViewById(R.id.signup_mobile);
        if (SignUp.this.getString(R.string.EMPTY_STRING).equals(mMobile.getText().toString())) {
            isMobileBlank = true;
            mMobile.setError("");
            mMobile.clearFocus();
            mMobile.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            tilMobile.setError("Mobile no. cannot be blank");
        }
        return isMobileBlank;
    }

    private void setCompanyIdFieldListener() {
        final TextInputLayout tilCompanyId = (TextInputLayout) findViewById(R.id.input_layout_signup_company);
        mCompanyId = findViewById(R.id.signup_companyId);
        mCompanyId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mCompanyId.setError(null);
                    tilCompanyId.setError(null);
                    mCompanyId.getBackground().clearColorFilter();
                }
            }
        });
    }

    private void setFirstNameFieldListener() {
        final TextInputLayout tilFirstName = (TextInputLayout) findViewById(R.id.input_layout_firstName);
        mFirstName = findViewById(R.id.first_name);
        mFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFirstName.setError(null);
                    tilFirstName.setError(null);
                    mFirstName.getBackground().clearColorFilter();
                }
            }
        });
    }
    private void setLastNameFieldListener() {
        final TextInputLayout tilLastName = (TextInputLayout) findViewById(R.id.input_layout_lastName);
        mLastName = findViewById(R.id.last_name);
        mLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mLastName.setError(null);
                    tilLastName.setError(null);
                    mLastName.getBackground().clearColorFilter();
                }
            }
        });
    }
    private void setMobileFieldListener() {
        final TextInputLayout tilMobile = (TextInputLayout) findViewById(R.id.input_layout_signup_mobile);
        mMobile = findViewById(R.id.signup_mobile);
        mMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mMobile.setError(null);
                    tilMobile.setError(null);
                    mMobile.getBackground().clearColorFilter();
                }
            }
        });
    }

    private void setEmailFieldListener() {
        final TextInputLayout tilEmail = (TextInputLayout) findViewById(R.id.input_layout_signup_email);
        mEmail = findViewById(R.id.signup_email);

        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mEmail.setError(null);
                    tilEmail.setError(null);
                    mEmail.getBackground().clearColorFilter();
                } else {
                    if (!mEmail.getText().toString().trim().matches(SignUp.this.getString(R.string.emailValidatorRegex)) && !SignUp.this.getString(R.string.EMPTY_STRING).equals(mEmail.getText().toString())) {
                        mEmail.setError("");
                        mEmail.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                        tilEmail.setError("Invalid Email");
                    }
                }
            }
        });
    }



}
