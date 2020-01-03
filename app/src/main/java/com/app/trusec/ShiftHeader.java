package com.app.trusec;

public class ShiftHeader {
    private String shiftTitle;
    private String startDate;
    private String endDate;

    public String getShiftTitle() {
        String[] startDateToken = startDate.split("/");
        String[] endDateToken = endDate.split("/");

        return getMonth(startDateToken[1])
                +startDateToken[0]
                +", "
                +startDateToken[2]
                +" - "

                + getMonth(endDateToken[1])
                +endDateToken[0]
                +", "
                +endDateToken[2];
    }

    public ShiftHeader(String startDate, String endDate){
        this.startDate = startDate;
        this.endDate = endDate;
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


}
