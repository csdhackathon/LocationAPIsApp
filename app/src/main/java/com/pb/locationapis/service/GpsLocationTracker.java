package com.pb.locationapis.service;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.pb.locationapis.application.LocationApiApplication;
import com.pb.locationapis.listener.INotifyGPSLocationListener;
import com.pb.locationapis.preference.SharedPreferenceHelper;
import com.pb.locationapis.utility.Utility;
import com.pb.locationapis.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by NEX4LDF on 12/26/2016.
 */

public class GpsLocationTracker extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener
{
    private static final String TAG = GpsLocationTracker.class.getName();
    private Context mContext;

    private INotifyGPSLocationListener iGPSListener;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    // flag for GPS status
    private boolean canGetLocation = false;
    private boolean googleServiceLocationApiFlag = false;

    private Location mLocation = null; // mLocation
    private double latitude; // latitude
    private double longitude; // longitude

    private Geocoder geocoder;
    private List<Address> addresses;

    // The minimum distance to change Updates in meters
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private final long MIN_TIME_BW_UPDATES = 1000 * 30; // 30 sec

    private final int REQUEST_CODE_LOCATION = 2;
    private final int TIME_INTERVAL_FOR_NEXT_LOCATION_UPDATE = 5;

    // Declaring a Location Manager
    private LocationManager locationManager;

    public final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public final int TURN_ON_GPS_REQUEST = 2000;

    private static GpsLocationTracker _instance = null;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SharedPreferenceHelper mSharedPreferenceHelper = SharedPreferenceHelper.getInstance();
    private HashMap<String, String> locationMapDetails;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static synchronized GpsLocationTracker getInstance()
    {
        return _instance;
    }

    public static synchronized void initialize(Context mContext, INotifyGPSLocationListener gpsListener)
    {
        try {
            /*if(_instance == null) {
                _instance = new GpsLocationTracker(mContext, gpsListener);
            }
            else {
                if(isGpEnabled(mContext)) {
                    _instance.iGPSListener = gpsListener;
                } else {
                    _instance = new GpsLocationTracker(mContext, gpsListener);
                }
            }*/
            _instance = new GpsLocationTracker(mContext, gpsListener);

            _instance.getLocation();
        }catch (Exception e) {
            Log.e(TAG, "@initialize() Exception::"+e.getMessage());
        }
    }

    public void setCallBackListener(INotifyGPSLocationListener gpsListener) {
        this.iGPSListener = gpsListener;
    }

    /**
     * This method is used to know weather the GPS is enabled or not
     * @return boolean
     */
    public static boolean isGpEnabled(Context mContext) {
        try {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private GpsLocationTracker(Context context, INotifyGPSLocationListener gpsListener)
    {
        try {
            this.mContext = context;
            this.iGPSListener = gpsListener;
            mSharedPreferenceHelper = SharedPreferenceHelper.getInstance();
            googleServiceLocationApiFlag = false;
            googleServiceLocationApiFlag = checkGooglePlayServices();
            if (googleServiceLocationApiFlag) {
                Log.i(TAG, "@GpsLocationTracker checkGooglePlayServices() true");
                buildGoogleApiClient();
                enableGpsByCreatingLocationRequest();
            } else {
                Log.i(TAG, "@GpsLocationTracker checkGooglePlayServices() false");
                //mActivity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), TURN_ON_GPS_REQUEST);
                getLocation();
                if(!canGetLocation()) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            turnOnGPS();
                        }
                    });
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "@GpsLocationTracker() Exception::"+e.getMessage());
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(GpsLocationTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() { return this.latitude; }

    /**
     * Function to get longitude
     * */
    public double getLongitude() { return this.longitude; }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to check GPS Google Location API enabled
     * @return boolean
     * */
    public boolean isGoogleServiceLocationApiFlag() {
        return this.googleServiceLocationApiFlag;
    }

    public void turnOnGPS()
    {
        try {
            final LocationManager manager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                // buildAlertMessageNoGps();
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.want_to_enable_your_gps))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                ((Activity) mContext).startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), TURN_ON_GPS_REQUEST);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                                ((Activity) mContext).finish();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }catch (Exception e) {
            Log.e(TAG, "@turnOnGPS Exception::"+e.getMessage());
            e.printStackTrace();
        }
    }

    public void connect()
    {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void disConnect()
    {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean checkGooglePlayServices()
    {
        try {
            int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
            if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
			/*
			* google play services is missing or update is required
			*  return code could be
			* SUCCESS,
			* SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
			* SERVICE_DISABLED, SERVICE_INVALID.
			*/
                /*if (GooglePlayServicesUtil.isUserRecoverableError(checkGooglePlayServices)) {
                    GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, ((Activity) mContext),
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    Log.i(TAG, "This device is not supported.");
                }*/
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, ((Activity) mContext), PLAY_SERVICES_RESOLUTION_REQUEST);
                //dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        ((Activity) mContext).finish();
                    }
                });
                dialog.show();
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "@checkGooglePlayServices() Exception::" + e.getMessage());
        }
        return true;
    }

    public synchronized void buildGoogleApiClient()
    {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(GpsLocationTracker.this)
                    .addOnConnectionFailedListener(GpsLocationTracker.this)
                    .addApi(LocationServices.API)
                    .build();
        } catch (Exception e) {
            Log.e(TAG, "@buildGoogleApiClient() Exception::" + e.getMessage());
        }
    }

    /**
     * Creating mLocation request object
     * */
    public void enableGpsByCreatingLocationRequest()
    {
        try {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(30000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            setLocationResultCallBack(builder);
        }catch (Exception e) {
            Log.e(TAG, "@createLocationRequest Exception::" + e.getMessage());
        }
    }

    private void setLocationResultCallBack(LocationSettingsRequest.Builder builder)
    {
        try {
            PendingResult<LocationSettingsResult> locationSettingResult =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

            locationSettingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult locationSettingsResult)
                {
                    final Status status = locationSettingsResult.getStatus();
                    final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();
                    switch (status.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All mLocation settings are satisfied. The client can initialize mLocation
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(((Activity) mContext), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }catch (Exception e) {
            Log.e(TAG, "@setLocationResultCallBack Exception::" + e.getMessage());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            Log.e(TAG, "@onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);
            if(requestCode == PLAY_SERVICES_RESOLUTION_REQUEST)
            {
                if(resultCode == Activity.RESULT_OK) {
                    if (mGoogleApiClient != null && !mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
                }
                else {
                    ((Activity) mContext).onBackPressed();
                }
            }
            else if(requestCode == TURN_ON_GPS_REQUEST)
            {
                if (resultCode == Activity.RESULT_CANCELED) {
                    ((Activity) mContext).onBackPressed();
                }
                else {
                    getLocation();
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "@onActivityResult Exception::" + e.getMessage());
        }
    }

    public void startLocationUpdates() {
        try {
            if(mGoogleApiClient!=null && mGoogleApiClient.isConnected())
            {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
            else
            {
                getLocation();
            }
        }catch (Exception e) {
            Log.e(TAG, "@startLocationUpdates Exception::" + e.getMessage());
        }
    }

    public void stopLocationUpdates() {
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
            }
        }catch (Exception e) {
            Log.e(TAG, "@stopLocationUpdates Exception::" + e.getMessage());
        }
    }

    public Location getLocation() {
        try {
            if(mLocation != null) {
                if (iGPSListener != null && Utility.getInstance(mContext)
                        .isLocationUpdateRequired(mSharedPreferenceHelper.getStringPreference(LocationApiApplication.getContext(), mSharedPreferenceHelper.LAST_LOCATION_SAVED_TIME)
                                , TIME_INTERVAL_FOR_NEXT_LOCATION_UPDATE))
                {
                    iGPSListener.onReceiveLocationDetails(mLocation);
                }
                return mLocation;
            }
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isNetworkEnabled || isGPSEnabled) {
                this.canGetLocation = true;
                mLocation = null;
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Check Permissions Now
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Display UI and wait for user interaction
                    } else {
                        ActivityCompat.requestPermissions(
                                (Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION);
                    }
                } else {
                    // permission has been granted, continue as usual
                }
                // First get mLocation from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            latitude = mLocation.getLatitude();
                            longitude = mLocation.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (mLocation == null && isGPSEnabled) {
                    if (mLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                latitude = mLocation.getLatitude();
                                longitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }
            if(mLocation != null && Utility.getInstance(mContext)
                    .isLocationUpdateRequired(mSharedPreferenceHelper.getStringPreference(LocationApiApplication.getContext(), mSharedPreferenceHelper.LAST_LOCATION_SAVED_TIME)
                            , TIME_INTERVAL_FOR_NEXT_LOCATION_UPDATE))
            {
                if(iGPSListener != null) {
                    iGPSListener.onReceiveLocationDetails(mLocation);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "@getLocation() Exception::" + e.getMessage());
        }
        return mLocation;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        try {
            if(Utility.getInstance(mContext).
                    isLocationUpdateRequired(mSharedPreferenceHelper.getStringPreference(LocationApiApplication.getContext(), mSharedPreferenceHelper.LAST_LOCATION_SAVED_TIME)
                            , TIME_INTERVAL_FOR_NEXT_LOCATION_UPDATE))
            {
                mSharedPreferenceHelper.setStringPreference(LocationApiApplication.getContext(), mSharedPreferenceHelper.LAST_LOCATION_SAVED_TIME,
                        Utility.getInstance(mContext).getDateTimeForNextLocationUpdate(TIME_INTERVAL_FOR_NEXT_LOCATION_UPDATE));
                mLocation = location;
                longitude = mLocation.getLongitude();
                latitude = mLocation.getLatitude();
                if(mLocation != null) {
                    if(iGPSListener != null) {
                        iGPSListener.onReceiveLocationDetails(mLocation);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        try {
            Log.i(TAG, "@onConnected()");
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                Log.i(TAG, "@onConnected() Latitude::" + mLocation.getLatitude()+", Longitude:"+mLocation.getLongitude());
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                mSharedPreferenceHelper.setStringPreference(LocationApiApplication.getContext(), mSharedPreferenceHelper.LAST_LOCATION_SAVED_TIME
                        , Utility.getInstance(mContext).getDateTimeForNextLocationUpdate(TIME_INTERVAL_FOR_NEXT_LOCATION_UPDATE));
            }
            else {
                Log.i(TAG, "@onConnected() Location not detected");
            }
            if(mLocation != null && Utility.getInstance(mContext).
                    isLocationUpdateRequired(mSharedPreferenceHelper.getStringPreference(LocationApiApplication.getContext(), mSharedPreferenceHelper.LAST_LOCATION_SAVED_TIME)
                            , TIME_INTERVAL_FOR_NEXT_LOCATION_UPDATE))
            {
                if(iGPSListener != null) {
                    iGPSListener.onReceiveLocationDetails(mLocation);
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "@onConnected() Exception::" + e.getMessage());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "@onConnectionSuspended() i:" + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "@onConnectionFailed() connectionResult:" + connectionResult.toString());
    }

}
