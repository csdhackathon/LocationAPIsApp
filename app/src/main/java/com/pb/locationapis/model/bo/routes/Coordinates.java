package com.pb.locationapis.model.bo.routes;


import com.pb.locationapis.utility.ConstantUnits;

/**
 * Created by NEX7IMH on 6/20/2017.
 */

public class Coordinates {

    private String latitude = ConstantUnits.getInstance().ZERO;
    private String longitude = ConstantUnits.getInstance().ZERO;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
