package com.app.trusec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class WeeklyShiftData {
    private String shiftTitle;
    private String preUrl;
    private String currentShift;
    private String userId;
    private boolean isCurrentWeek;
    private String nextUrl;
    private String startDate;
    private String endDate;

    public boolean isPastWeek() {
        return isPastWeek;
    }

    private boolean isPastWeek;


    private JSONObject allShifts;
    private ArrayList<DailyShiftData> shiftData = new ArrayList<DailyShiftData>();
    private ShiftHeader shiftHeader;

    public WeeklyShiftData(JSONObject jsonResponse, String userId, boolean isCurrentWeek) {
        try {
            startDate = (String) jsonResponse.get("start_date");
            endDate = (String) jsonResponse.get("end_date");
            shiftHeader = new ShiftHeader(startDate, endDate);
            preUrl = (String) jsonResponse.get("preUrl");
            currentShift = (String) jsonResponse.get("currentShift");
            nextUrl = (String) jsonResponse.get("nextUrl");
            this.userId = userId;
            this.isCurrentWeek = isCurrentWeek;
            isPastWeek = initPastWeek();
            allShifts = new JSONObject((String) jsonResponse.get("allShifts"));
            for (int i = 1; i <= 7; i++) {
                try {
                    shiftData.add(new DailyShiftData((JSONObject) ((JSONArray) allShifts.get(String.valueOf(i))).get(0)));
                } catch (JSONException e) {
                    //Shift not available for the day
                    try {

                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = dateFormat.parse(startDate);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        cal.add(Calendar.DATE, i - 1);
                        shiftData.add(new DailyShiftData(String.valueOf(i), new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime())));
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                }
            }
            System.out.println(allShifts);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean initPastWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
           // Date startDateObj = sdf. parse(startDate);
            Date endDateObj = sdf. parse(endDate);
            Date currentDate =Calendar.getInstance().getTime();
            if (currentDate.after(endDateObj)){
                return true;
            }
            return false;
        } catch (ParseException e) {
                return false;
        }
    }

    public String getShiftTitle() {
        return shiftTitle;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isCurrentWeek() {
        return isCurrentWeek;
    }

    public String getPreUrl() {
        return preUrl;
    }

    public String getCurrentShift() {
        return currentShift;
    }

    public String getNextUrl() {
        return nextUrl;
    }



    public String getUserId() {
        return userId;
    }

    public ShiftHeader getShiftHeader() {
        return shiftHeader;
    }


    public ArrayList<DailyShiftData> getShiftData() {
        return shiftData;
    }
}
