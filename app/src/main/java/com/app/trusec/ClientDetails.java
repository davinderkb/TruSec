package com.app.trusec;

import org.json.JSONException;
import org.json.JSONObject;

class ClientDetails {
    private String fullName;
    private String mobile;
    private String email;
    private String phone;
    private String fax;
    private String locLongitude;
    private String locLatitude;

       public ClientDetails(JSONObject response) {

        try {
            fullName = response.getString("name");
            email = response.getString("email");
           // mobile = response.getString("mobile");
            mobile = "9910337023";
            phone = response.getString("phone");
            fax = response.getString("fax");
            this.locLongitude = response.getString("loc_long");
            this.locLatitude = response.getString("loc_lat");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getFullName() {
        return fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFax() {
        return fax;
    }
    public String getLocLongitude() {
        return locLongitude;
    }

    public String getLocLatitude() {
        return locLatitude;
    }

}
