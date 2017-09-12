package com.pb.locationapis.listener;

import android.location.Location;

/**
 * Created by Prime6 on 1/6/2016.
 */
public interface INotifyGPSLocationListener {

    void onReceiveLocationDetails(Location mLocation);
}
