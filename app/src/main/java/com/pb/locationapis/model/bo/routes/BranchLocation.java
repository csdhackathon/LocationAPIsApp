package com.pb.locationapis.model.bo.routes;

import java.util.List;

/**
 * Created by NEX7IMH on 6/20/2017.
 */

public class BranchLocation {

    private String type;
    private List<Coordinates> coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

}
