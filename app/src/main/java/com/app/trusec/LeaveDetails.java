package com.app.trusec;

public class LeaveDetails {
    private String fromDate;
    private String toDate;
    private String reason;
    private String status;

    public LeaveDetails(String fromDate, String toDate, String reason, String status) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.status = status;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

}
