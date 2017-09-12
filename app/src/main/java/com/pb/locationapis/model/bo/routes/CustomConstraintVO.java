package com.pb.locationapis.model.bo.routes;

/**
 * Created by NEX7IMH on 6/20/2017.
 */

public class CustomConstraintVO {

    private String capacityDemand;
    private String distance;
    private String endTime;
    private String fixed;
    private String isCostsInvolve;
    private String service;
    private String startTime;
    private String time;
    private String type;
    private String wait;
    private StartEndTime startEndTime;


    public String getCapacityDemand() {
        return capacityDemand;
    }

    public void setCapacityDemand(String capacityDemand) {
        this.capacityDemand = capacityDemand;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public String getIsCostsInvolve() {
        return isCostsInvolve;
    }

    public void setIsCostsInvolve(String isCostsInvolve) {
        this.isCostsInvolve = isCostsInvolve;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWait() {
        return wait;
    }

    public void setWait(String wait) {
        this.wait = wait;
    }

    public StartEndTime getStartEndTime() {
        return startEndTime;
    }

    public void setStartEndTime(StartEndTime startEndTime) {
        this.startEndTime = startEndTime;
    }
}
