package com.app.trusec;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "EmesSecurity";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_COMPANY_URL = "companyUrl";

    // Email address (make variable public to access from outside)
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_DISPLAY_NAME = "userDisplayName";
    public static final String KEY_COMPANY_NAME = "companyName";


    // Constructor
    public SessionManager(Context context){
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String companyUrl, String userId, String userDisplayName, String companyName){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing companyUrl in pref
        editor.putString(KEY_COMPANY_URL, companyUrl);

        // Storing userId in pref
        editor.putString(KEY_USER_ID, userId);

        editor.putString(KEY_USER_DISPLAY_NAME, userDisplayName);
        editor.putString(KEY_COMPANY_NAME, companyName);

        // commit changes
        editor.commit();
    }

    /**
     *start Login Activity
     * */
    public void login(){
            Intent intent = new Intent(context, LoginActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
      //  ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_COMPANY_URL, pref.getString(KEY_COMPANY_URL, null));

        // user email id
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));

        user.put(KEY_USER_DISPLAY_NAME, pref.getString(KEY_USER_DISPLAY_NAME, null));
        user.put(KEY_COMPANY_NAME, pref.getString(KEY_COMPANY_NAME, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent intent = new Intent(this.context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        context.startActivity(intent);
       // ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}