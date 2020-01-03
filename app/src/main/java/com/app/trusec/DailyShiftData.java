package com.app.trusec;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

class DailyShiftData implements Serializable {
    private String shiftDay;
    private String shiftDate;
    private String shiftTiming;
    private String clientName;
    private String rosterConfirmCode;
    private boolean isAllowCheckIn;
    private String clientId;

    public String getShiftId() {
        return shiftId;
    }

    private String shiftId;
    private boolean isShiftAvailable;
    private boolean isSelected = false;

    public String getRosterConfirmCode() {
        return rosterConfirmCode;
    }

    public DailyShiftData(JSONObject jsonObject) {
        try {
            shiftDate = (String) jsonObject.get("work_date");
            shiftId = (String) jsonObject.get("shift_id");
            shiftTiming = ((String) jsonObject.get("staff_time_on")) + " - " + ((String) jsonObject.get("staff_time_off"));
            try {
                rosterConfirmCode = (String) jsonObject.get("is_confirm");
            }catch (ClassCastException cce){
                rosterConfirmCode = "0";
            }
            clientId = (String) jsonObject.get("client_id");
            isAllowCheckIn = parseIsAllowCheckin((Integer) jsonObject.get("allowCheckin"));
            clientName = (String) jsonObject.get("client_name");
            shiftDay = getShiftDay((String) jsonObject.get("day_of_shift"));
            isShiftAvailable = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public DailyShiftData(String shiftDay, String shiftDate) {
        this.shiftDay = getShiftDay(shiftDay);
        this.shiftDate = shiftDate;
        this.clientName = "";
        this.shiftTiming = "";
        isShiftAvailable = false;
        clientId = null;
        isAllowCheckIn = false;

    }

    public String getShiftDate() {
        return shiftDate;
    }

    public String getShortShiftDate() {
        String[] token = shiftDate.split("-");
        return getMonth(token[1]) +token[2] ;
    }

    private String getMonth(String numMon) {
        switch (Integer.parseInt(numMon)) {
            case 1:
                return "Jan ";
            case 2:
                return "Feb ";
            case 3:
                return "Mar ";
            case 4:
                return "Apr ";
            case 5:
                return "May ";
            case 6:
                return "Jun ";
            case 7:
                return "Jul ";
            case 8:
                return "Aug ";
            case 9:
                return "Sep ";
            case 10:
                return "Oct ";
            case 11:
                return "Nov ";
            case 12:
                return "Dec ";
            default:
                return  numMon + "-";
        }
    }

    public String getShiftTiming() {
        return shiftTiming;
    }

    public String getClientName() {
        return clientName;
    }

    public String getShiftDay() {
        return shiftDay;
    }

    public boolean isShiftAvailable() {
        return isShiftAvailable;
    }

    private String getShiftDay(String dayOfShiftNo) {
        switch (dayOfShiftNo) {
            case "0":
                return "Sun";
            case "1":
                return "Mon";
            case "2":
                return "Tue";
            case "3":
                return "Wed";
            case "4":
                return "Thu";
            case "5":
                return "Fri";
            case "6":
                return "Sat";
            default:
                return "";
        }
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
    public String getClientId() {
        return clientId;
    }
    public boolean isAllowCheckIn() {
        return isAllowCheckIn;
    }

    private boolean parseIsAllowCheckin(Integer allowCheckin) {
        return allowCheckin==1;

    }

}
