package com.app.trusec;

import org.json.JSONException;
import org.json.JSONObject;

class VenueDetails {

    private String reportingManager;
    private String reportingSupervisor;

    private String street;
    private String area;
    private String state;
    private String postCode;
    private String uniformCompulsory;

    public VenueDetails(JSONObject response) {

        try {
            this.reportingManager = response.getString("manager_to_report");
            this.reportingSupervisor = response.getString("supervisor_to_report");
            this.street = response.getString("street");
            this.area = response.getString("suburb");
            this.state = response.getString("state");
            this.postCode = response.getString("postcode");
            this.uniformCompulsory = response.getString("uniform_required");



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getReportingManager() {
        return reportingManager;
    }

    public String getReportingSupervisor() {
        return reportingSupervisor;
    }

    public String getUniformCompulsory() {
        return uniformCompulsory;
    }



    public String getStreet() {
        return street;
    }

    public String getArea() {
        return area;
    }

    public String getState() {
        return state;
    }

    public String getPostCode() {
        return postCode;
    }
}
