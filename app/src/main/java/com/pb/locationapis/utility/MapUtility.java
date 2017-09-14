package com.pb.locationapis.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.pb.locationapis.R;
import com.pb.locationapis.infowindow.MyLocationInfoWindow;
import com.pb.locationapis.service.GpsLocationTracker;
import com.pb.locationapis.tilesource.PBGeoMapCopyrightOverlay;
import com.pb.locationapis.tilesource.PBGeoMapTileSource;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * Map Utility class that initialize the MapView with Pitney Bowes GeoMap API (Raster Tiles). This class also
 * takes care of Location and all the method as a Map helper.
 */
public class MapUtility {
    private static MapUtility _instance;
    private static final String TAG = MapUtility.class.getName();

    private Activity mActivity;
    private ConstantUnits mConstantUnits;
    private Utility mUtility;

    private MapView mMapView;
    private Marker mMarkerMyLocation;

    /**
     * Constructor
     */
    public MapUtility(Activity activity, MapView mapView) {
        this.mActivity = activity;
        this.mConstantUnits = ConstantUnits.getInstance();
        this.mUtility = Utility.getInstance(mActivity);
        this.mMapView = mapView;
    }


    /**
     * This method is used to initialize the OSM Droid Map
     *
     * @param mActivity
     * @param mProvider
     */
    public void initializeMapViews(Activity mActivity, MapTileProviderBase mProvider) {
        try {
            //important! set your user agent to prevent getting banned from the osm servers
            Configuration.getInstance().load(mActivity.getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));

            mMapView.getOverlays().clear();
            mMapView.getTileProvider().clearTileCache();
            mMapView.getOverlayManager().clear();
            IMapController mapController = mMapView.getController();
            mapController.setZoom(6);
            GeoPoint startPoint = new GeoPoint(40.730610,-73.935242);
            mapController.setCenter(startPoint);

            mMapView.setBuiltInZoomControls(true);
            mMapView.setMultiTouchControls(true);

            /* PB GeoMap Overlay */
            {

                Context context=mActivity.getApplicationContext();
                ITileSource tileSource = new PBGeoMapTileSource(context);
                mMapView.setTileSource(tileSource);

                //Adding the Pitney Bowes Map Attributions
                PBGeoMapCopyrightOverlay overlay=new PBGeoMapCopyrightOverlay(context);
                Resources r = context.getResources();
                Bitmap bm = BitmapFactory.decodeResource(r, R.drawable.pitneyboweslogo);
                Bitmap newBp= Bitmap.createScaledBitmap(bm,(int)(bm.getWidth()*0.8), (int)(bm.getHeight()*0.8), true);
                overlay.setCopyRightText("@Carto ©OpenStreetMap Contributors");
                overlay.setTextColor(Color.DKGRAY);
                overlay.setLogo(newBp);
                mMapView.getOverlays().add(overlay);

                mMapView.invalidate();

            }

            /* Scale Bar Overlay */
            {
                ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mMapView);
                mScaleBarOverlay.setCentred(true);
                mMapView.getOverlays().add(mScaleBarOverlay);
                // Scale bar tries to draw as 1-inch, so to put it in the top center, set x offset to
                // half screen width, minus half an inch.
                mScaleBarOverlay.setScaleBarOffset(
                        (int) (mActivity.getResources().getDisplayMetrics().widthPixels / 2
                                - mActivity.getResources().getDisplayMetrics().xdpi / 2), 10);
            }
/*
            *//* SingleLocation-Overlay *//*
            {
			*//*
			 * Create a static Overlay showing a single location. (Gets updated in
			 * onLocationChanged(Location loc)!
			 *//*
                SimpleLocationOverlay mMyLocationOverlay = new SimpleLocationOverlay(((BitmapDrawable)mActivity.getResources().getDrawable(org.osmdroid.library.R.drawable.person)).getBitmap());
                mMapView.getOverlays().add(mMyLocationOverlay);
            }*/

            /*RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
            mRotationGestureOverlay.setEnabled(true);

            mMapView.getOverlays().add(mRotationGestureOverlay);
*/

           /* mMapView.setMaxZoomLevel(21);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMyLocationMarker(Location mLocation, boolean isAnimateToLocation) {
        try {
            if(mLocation == null) {
                mLocation = GpsLocationTracker.getInstance().getLocation();
            }
            if(mMapView != null)  {

                GeoPoint mGeoPointMyLocation = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
                if(mMarkerMyLocation != null) {
                    mMarkerMyLocation.setPosition(mGeoPointMyLocation);
                } else {
                    mMarkerMyLocation = new Marker(mMapView);
                    mMarkerMyLocation.setPosition(mGeoPointMyLocation);

                    Drawable mDrawable = ContextCompat.getDrawable(mActivity, R.drawable.location_marker_green);
                    mMarkerMyLocation.setIcon(mUtility.scaleImage(mActivity, mDrawable, (float) 0.65));
                    mMarkerMyLocation.setTitle(mActivity.getString(R.string.my_location));

                    MyLocationInfoWindow mMyLocationInfoWindow = new MyLocationInfoWindow(R.layout.layout_custom_info_window, mMapView);
                    mMarkerMyLocation.setInfoWindow(mMyLocationInfoWindow);

                    mMarkerMyLocation.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                            try {
                                for (int i = 0; i < mMapView.getOverlays().size(); ++i) {
                                    Overlay o = mMapView.getOverlays().get(i);
                                    if (o instanceof Marker) {
                                        Marker m = (Marker) o;
                                        if (m.getTitle() != null && m.getTitle().equalsIgnoreCase(marker.getTitle())) {
                                            if (marker != null) {
                                                marker.showInfoWindow();
                                            }
                                        } else {
                                            if (m != null) {
                                                m.closeInfoWindow();
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    });
                    mMapView.getOverlays().add(mMarkerMyLocation);
                }

                if(isAnimateToLocation) {
                    mMapView.getController().setCenter(mGeoPointMyLocation);
                    mMapView.getController().animateTo(mGeoPointMyLocation);
                    String myLocationTitle = mActivity.getString(R.string.my_location);
                    for(int i=0; i<mMapView.getOverlays().size(); ++i){
                        Overlay o = mMapView.getOverlays().get(i);
                        if(o instanceof Marker){
                            Marker m = (Marker) o;
                            if(m.getTitle() != null && m.getTitle().equalsIgnoreCase(myLocationTitle)) {
                                m.showInfoWindow();
                                mMapView.getController().animateTo(m.getPosition());
                                mMapView.getController().setZoom(13);
                                mMapView.getController().setCenter(m.getPosition());
                            }
                            else {
                                if (m != null) {
                                    m.closeInfoWindow();
                                }
                            }
                        }
                    }
                }
                mMapView.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeInfoWindowOfMyLocationMarker() {
        try {
            if(mMarkerMyLocation != null) {
                mMarkerMyLocation.closeInfoWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}