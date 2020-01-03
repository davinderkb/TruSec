package com.app.trusec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.net.InetAddress;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends CustomAppCompatActivity {

    Button signUp;
    Button signIn;
    EditText mEmail;
    EditText mPassword;
    EditText mCompanyId;
    SessionManager session;

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleSSLHandshake();
        setContentView(R.layout.activity_login);
        initialize();
        session = new SessionManager(getApplicationContext());


    }

    private void initialize() {
        setLoginFieldsListeners();
        setSignUpButtonListener();
        setSignInButtonListener();
    }



    private void setLoginFieldsListeners() {

        setEmailFieldListener();
        setCompanyIdFieldListener();
        setPasswordFieldListener();
    }

    private void setCompanyIdFieldListener() {
        final TextInputLayout tilCompanyId = (TextInputLayout) findViewById(R.id.input_layout_company);
        mCompanyId = findViewById(R.id.company_id);
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

    private void setPasswordFieldListener() {
        final TextInputLayout tilPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        mPassword = findViewById(R.id.password);
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mPassword.setError(null);
                    tilPassword.setError(null);
                    mPassword.getBackground().clearColorFilter();
                }
            }
        });
    }

    private void setEmailFieldListener() {
        final TextInputLayout tilEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        mEmail = findViewById(R.id.email);

        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mEmail.setError(null);
                    tilEmail.setError(null);
                    mEmail.getBackground().clearColorFilter();
                } else {
                    if (!mEmail.getText().toString().trim().matches(LoginActivity.this.getString(R.string.emailValidatorRegex)) && !LoginActivity.this.getString(R.string.EMPTY_STRING).equals(mEmail.getText().toString())) {
                        mEmail.setError("");
                        mEmail.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                        tilEmail.setError("Invalid Email");
                    }
                }
            }
        });
    }


    private void setSignInButtonListener() {
        signIn = findViewById(R.id.sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCompanyIdBlank() | isPasswordBlank() | (isEmailBlank() || isInvalidEmail()) )
                    return;
                if(isOnline())
                        sendLoginRequest();
                else
                    Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.INTERNET_NOT_CONNECTED), Toast.LENGTH_LONG).show();
            }


        });

    }

    private void sendLoginRequest() {
        RequestQueue companyUrlQ = Volley.newRequestQueue(LoginActivity.this);
        StringRequest companyUrlRequest = new StringRequest(Request.Method.POST, LoginActivity.this.getString(R.string.apiGetInfoUrl), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.equals(LoginActivity.this.getString(R.string.EMPTY_STRING))) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(LoginActivity.this.getString(R.string.put_companyID), ((EditText) findViewById(R.id.company_id)).getText().toString());
                    sendHttpRequestToGetUserInfo(response , params);
                } else {
                    Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.INVALID_COMPANY_ID), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                if(error.toString().contains("TimeoutError"))
                    Toast.makeText(getApplicationContext(),"Failed! Try again.. Please check internet connection before proceeding.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(),"Invalid Company ID", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(LoginActivity.this.getString(R.string.put_companyID), ((EditText) findViewById(R.id.company_id)).getText().toString()); //Add the data you'd like to send to the server.
                return MyData;
            }
        };
        companyUrlQ.add(companyUrlRequest);
    }


    private void sendHttpRequestToGetUserInfo(final String companyUrl, HashMap<String, String> params) {

        RequestQueue userInfoRequestQ = Volley.newRequestQueue(LoginActivity.this);
        final String secureCompanyUrl = companyUrl.replace("http", "https");
        StringRequest userInfoRequest = new  StringRequest(Request.Method.POST, secureCompanyUrl +LoginActivity.this.getString(R.string.URL_LOGIN),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if(response!=null)
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                if(!jsonResponse.get(LoginActivity.this.getString(R.string.JSON_KEY_RESPONSE)).toString().equals(LoginActivity.this.getString(R.string.WRONG_CREDENTIAL_RESPONSE_CODE))){
                                    JSONObject userInfo = (JSONObject) jsonResponse.get(LoginActivity.this.getString(R.string.JSON_KEY_OUTPUT));
                                    String userId = (String) userInfo.get(LoginActivity.this.getString(R.string.JSON_KEY_ID));
                                    String userDisplayName = (String) userInfo.get("first_name") +" " + (String) userInfo.get("last_name");
                                    String companyName = (String) ((JSONObject)userInfo.get("setting")).get("APPNAME");
                                    session.createLoginSession(secureCompanyUrl, userId, userDisplayName, companyName);

                                    // Staring MainActivity
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                } else{
                                    Toast.makeText(getApplicationContext(), (CharSequence) jsonResponse.get(LoginActivity.this.getString(R.string.JSON_KEY_OUTPUT)), Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(), "Invalid User Name or Password", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put(LoginActivity.this.getString(R.string.put_email), ((EditText) findViewById(R.id.email)).getText().toString());
                MyData.put(LoginActivity.this.getString(R.string.put_password), ((EditText) findViewById(R.id.password)).getText().toString());
                return MyData;
            }
        };

        userInfoRequestQ.add(userInfoRequest);
    }

    private boolean isPasswordBlank() {
        boolean isPasswdBlank = false;
        final TextInputLayout tilPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        mPassword = findViewById(R.id.password);
        if (LoginActivity.this.getString(R.string.EMPTY_STRING).equals(mPassword.getText().toString())) {
            isPasswdBlank = true;
            mPassword.setError("");
            mPassword.clearFocus();
            mPassword.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            tilPassword.setError("Password cannot be blank");
        }
        return isPasswdBlank;
    }

    private boolean isCompanyIdBlank() {
        boolean isCompanyIdBlank = false;
        final TextInputLayout tilCompanyId = (TextInputLayout) findViewById(R.id.input_layout_company);
        mCompanyId = findViewById(R.id.company_id);
        if (LoginActivity.this.getString(R.string.EMPTY_STRING).equals(mCompanyId.getText().toString())) {
            isCompanyIdBlank = true;
            mCompanyId.setError("");
            mCompanyId.clearFocus();
            mCompanyId.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            tilCompanyId.setError("Company ID cannot be empty");
        }
        return isCompanyIdBlank;
    }

    private boolean isEmailBlank() {
        boolean isBlank = false;
        final TextInputLayout tilEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        mEmail = findViewById(R.id.email);
        if (LoginActivity.this.getString(R.string.EMPTY_STRING).equals(mEmail.getText().toString())) {
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
        final TextInputLayout tilEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        mEmail = findViewById(R.id.email);
        if (!mEmail.getText().toString().trim().matches(LoginActivity.this.getString(R.string.emailValidatorRegex)) && !LoginActivity.this.getString(R.string.EMPTY_STRING).equals(mEmail.getText().toString())) {
            isInvalidEmail=true;
            mEmail.setError("");
            mEmail.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            tilEmail.setError("Invalid Email");
            mEmail.clearFocus();
        }
        return isInvalidEmail;
    }

    private void setSignUpButtonListener() {
        signUp = findViewById(R.id.sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUp.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
