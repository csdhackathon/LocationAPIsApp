package com.pb.locationapis.model.bo.routes;

import java.io.Serializable;

/**
 * Created by NEX7IMH on 6/20/2017.
 */

public class StartEndTime implements Serializable {

    private String startTime;
    private String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
