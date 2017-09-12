package com.pb.locationapis.model.bo.routes;

import java.util.List;

/**
 * Created by NEX7IMH on 6/15/2017.
 */

public class GeometryJson {

    private String distance;
    private String distanceUnit;
    private String time;
    private String timeUnit;
    private Geometry geometry;
    private List<Coordinates> intermediatePointses;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public List<Coordinates> getIntermediatePointses() {
        return intermediatePointses;
    }

    public void setIntermediatePointses(List<Coordinates> intermediatePointses) {
        this.intermediatePointses = intermediatePointses;
    }
}
