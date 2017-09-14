package com.pb.locationapis.model.bo.routes;

import java.io.Serializable;

/**
 * Created by NEX7IMH on 6/20/2017.
 */

public class CheckInCheckOutVo implements Serializable {

    private String customerId;
    private String expectedStartTime;
    private String expectedFinishTime;
    private String costs;
    private String distanceCost;
    private String timeCost;
    private String serviceCost;
    private String cicoId;
    private String isCheckinDone;
    private String isCheckoutDone;
    private String routeId;
    private String agentArriveTime;
    private String agentFinishTime;
    private String routeOrder;
    private String checkinLat;
    private String checkinLong;
    private String checkinTime;
    private String checkoutLat;
    private String checkoutLong;
    private String checkoutTime;
    private String userComment;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getExpectedStartTime() {
        return expectedStartTime;
    }

    public void setExpectedStartTime(String expectedStartTime) {
        this.expectedStartTime = expectedStartTime;
    }

    public String getExpectedFinishTime() {
        return expectedFinishTime;
    }

    public void setExpectedFinishTime(String expectedFinishTime) {
        this.expectedFinishTime = expectedFinishTime;
    }

    public String getCosts() {
        return costs;
    }

    public void setCosts(String costs) {
        this.costs = costs;
    }

    public String getDistanceCost() {
        return distanceCost;
    }

    public void setDistanceCost(String distanceCost) {
        this.distanceCost = distanceCost;
    }

    public String getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(String timeCost) {
        this.timeCost = timeCost;
    }

    public String getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(String serviceCost) {
        this.serviceCost = serviceCost;
    }

    public String getCicoId() {
        return cicoId;
    }

    public void setCicoId(String cicoId) {
        this.cicoId = cicoId;
    }

    public String getIsCheckinDone() {
        return isCheckinDone;
    }

    public void setIsCheckinDone(String isCheckinDone) {
        this.isCheckinDone = isCheckinDone;
    }

    public String getIsCheckoutDone() {
        return isCheckoutDone;
    }

    public void setIsCheckoutDone(String isCheckoutDone) {
        this.isCheckoutDone = isCheckoutDone;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getAgentArriveTime() {
        return agentArriveTime;
    }

    public void setAgentArriveTime(String agentArriveTime) {
        this.agentArriveTime = agentArriveTime;
    }

    public String getAgentFinishTime() {
        return agentFinishTime;
    }

    public void setAgentFinishTime(String agentFinishTime) {
        this.agentFinishTime = agentFinishTime;
    }

    public String getRouteOrder() {
        return routeOrder;
    }

    public void setRouteOrder(String routeOrder) {
        this.routeOrder = routeOrder;
    }

    public String getCheckinLat() {
        return checkinLat;
    }

    public void setCheckinLat(String checkinLat) {
        this.checkinLat = checkinLat;
    }

    public String getCheckinLong() {
        return checkinLong;
    }

    public void setCheckinLong(String checkinLong) {
        this.checkinLong = checkinLong;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckoutLat() {
        return checkoutLat;
    }

    public void setCheckoutLat(String checkoutLat) {
        this.checkoutLat = checkoutLat;
    }

    public String getCheckoutLong() {
        return checkoutLong;
    }

    public void setCheckoutLong(String checkoutLong) {
        this.checkoutLong = checkoutLong;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }
}
