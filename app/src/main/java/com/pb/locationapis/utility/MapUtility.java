package com.pb.locationapis.utility;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.pb.locationapis.R;
import com.pb.locationapis.infowindow.MyLocationInfoWindow;
import com.pb.locationapis.preference.SharedPreferenceHelper;
import com.pb.locationapis.tilesource.OnlineTileSource;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NEX7IMH on 14-Jun-17.
 */
public class MapUtility {
    private static MapUtility _instance;
    private static final String TAG = MapUtility.class.getName();

    private Activity mActivity;
    private ConstantUnits mConstantUnits;
    private SharedPreferenceHelper mSharedPreferenceHelper;
    private Utility mUtility;

    private MapView mMapView;
    private Marker mMarkerMyLocation;
    private Marker mMarkerBranchLocation;
    private Polyline mPolyline;

    private List<Marker> mMarkersCustomers = new ArrayList<>();
    private List<Polygon> mPolygonArrowsList = new ArrayList<>();
    private List<GeoPoint> mGeoPointsCoordinates = new ArrayList<>();

    /**
     * Constructor
     */
    public MapUtility(Activity activity, MapView mapView) {
        this.mActivity = activity;
        this.mConstantUnits = ConstantUnits.getInstance();
        this.mSharedPreferenceHelper = SharedPreferenceHelper.getInstance();
        this.mUtility = Utility.getInstance(mActivity);
        this.mMapView = mapView;
    }

    public Polyline getmPolyline() {
        return this.mPolyline;
    }

    public List<Polygon> getPolygonArrowsList () {
        return this.mPolygonArrowsList;
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

            mMapView.setBuiltInZoomControls(true);
            mMapView.setMultiTouchControls(true);

            /* PB GeoMap Overlay */
            {
                String mUrl = getMetaDataFromManifest(mConstantUnits.PBGEOMAP_URL)
                        + "{z}/{x}/{y}.png?api_key=" + getMetaDataFromManifest(mConstantUnits.PBGEOMAP_ACCESS_TOKEN)
                        + "&theme=" + getMetaDataFromManifest(mConstantUnits.PBGEO_GEOMAP_THEME);

                OnlineTileSource mOnlineTileSource = new OnlineTileSource("PBGeoMapTiles", 3, 18, 256, ".png", new String[]{mUrl});
                /*ITileSource tileSource = new XYTileSource("FietsRegionaal",  3, 18, 256, ".png",
                        new String[] { mUrl });*/
                mProvider = new MapTileProviderBasic(mActivity.getApplicationContext(), mOnlineTileSource);

                //mProvider = new MapTileProviderBasic(getActivity().getApplicationContext());
                mProvider.clearTileCache();
                mProvider.setTileSource(mOnlineTileSource);

                final TilesOverlay tilesOverlay = new TilesOverlay(mProvider, mActivity.getBaseContext());
                tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);

                mMapView.getOverlays().add(tilesOverlay);
                //mMapView.getTileProvider().setTileSource(mOnlineTileSource);

            }
            mMapView.setTilesScaledToDpi(true);

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

            RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
            mRotationGestureOverlay.setEnabled(true);

            mMapView.getOverlays().add(mRotationGestureOverlay);

            IMapController mapController = mMapView.getController();
            mapController.setZoom(12);
            mMapView.setMaxZoomLevel(21);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to get the meta data value stored in the manifest files for the provided key
     *
     * @param key
     * @return
     */
    public String getMetaDataFromManifest(String key) {
        String value = mConstantUnits.EMPTY;
        try {
            ApplicationInfo ai = mActivity.getPackageManager().getApplicationInfo(
                    mActivity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            value = bundle.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private double getDoubleValueOfCoordinates(String coordinateValue) {
        double latitude = 0;
        try {
            latitude = Double.parseDouble(coordinateValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latitude;
    }

    public void initPolygonArrows(int zoomLevel) {
        try {
            if(mPolygonArrowsList == null) {
                mPolygonArrowsList = new ArrayList<>();
            }
            if(mPolygonArrowsList != null && mPolygonArrowsList.size() > 0) {
                for (int i = 0; i< mPolygonArrowsList.size(); i++) {
                    mMapView.getOverlays().remove(mPolygonArrowsList.get(i));
                }
                mPolygonArrowsList.clear();
            }
            for (int i = 0; i< mGeoPointsCoordinates.size(); i++) {
                if((i-1 >= 0 && i+1 < mGeoPointsCoordinates.size() && i% 21 == 0)) {
                    drawArrowPolygon(mPolygonArrowsList, mGeoPointsCoordinates.get(i - 1), mGeoPointsCoordinates.get(i + 1), zoomLevel);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawArrowPolygon(List<Polygon> mPolygonArrowsList, GeoPoint fromGeoPoint, GeoPoint toGeoPoint, int zoomLevel){
        try {
            List<GeoPoint> geoPoints = new ArrayList<>();

            float angle=(float)finalBearing(fromGeoPoint.getLatitude(), fromGeoPoint.getLongitude(), toGeoPoint.getLatitude(), toGeoPoint.getLongitude());

            double xyDistance = 0.0007 * getArrowScaleFactor(zoomLevel);
            double tipDistance = 0.0014 * getArrowScaleFactor(zoomLevel);
            geoPoints.add(fromGeoPoint);
            geoPoints.add(getDestinationPoint(fromGeoPoint, angle+90, xyDistance));
            geoPoints.add(getDestinationPoint(fromGeoPoint, angle, tipDistance));
            geoPoints.add(getDestinationPoint(fromGeoPoint, angle-90, xyDistance));
            geoPoints.add(fromGeoPoint);

            Polygon mPolygon = new Polygon();

            mPolygon.setPoints(geoPoints);
            mPolygon.setFillColor(Color.parseColor("#009fff"));
            mPolygon.setStrokeColor(Color.parseColor("#009fff"));
            mPolygon.setStrokeWidth(4);

            mPolygonArrowsList.add(mPolygon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double finalBearing(double lat1, double long1, double lat2, double long2){

        return (getBearing(lat2, long2, lat1, long1) + 180.0) % 360;
    }

    private double getBearing(double lat1, double long1, double lat2, double long2) {
        double degToRad = Math.PI / 180.0;
        double phi1 = lat1 * degToRad;
        double phi2 = lat2 * degToRad;
        double lam1 = long1 * degToRad;
        double lam2 = long2 * degToRad;

        return Math.atan2(Math.sin(lam2 - lam1) * Math.cos(phi2),
                Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1)
        ) * 180 / Math.PI;
    }

    private int getArrowScaleFactor(int zoomLevel) {
        try {
            switch (zoomLevel) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    return 85;
                case 7:
                    return 77;
                case 8:
                    return 71;
                case 9:
                    return 60;
                case 10:
                    return 51;
                case 11:
                    return 43;
                case 12:
                    return 38;
                case 13:
                    return 30;
                case 14:
                    return 20;
                case 15:
                    return 13;
                case 16:
                    return 11;
                case 17:
                    return 6;
                case 18:
                    return 4;
                case 19:
                    return 2;
                default:
                    return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private GeoPoint getDestinationPoint(GeoPoint source, double bearing, double distance) {
        GeoPoint mGeoPoint = null;
        try {
            distance = distance / 6371;
            bearing = Math.toRadians(bearing);

            double lat1 = Math.toRadians(source.getLatitude()), lon1 = Math.toRadians(source.getLongitude());
            double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance) +
                    Math.cos(lat1) * Math.sin(distance) * Math.cos(bearing));
            double lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(distance) *
                            Math.cos(lat1),
                    Math.cos(distance) - Math.sin(lat1) *
                            Math.sin(lat2));
            if (Double.isNaN(lat2) || Double.isNaN(lon2)) {
                return null;
            }
            mGeoPoint = new GeoPoint(Math.toDegrees(lat2), Math.toDegrees(lon2));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mGeoPoint;
    }



    public void setMyLocationMarker(Location mLocation, boolean isAnimateToLocation) {
        try {
            if(mLocation != null && mMapView != null)  {

                //*************************************************************************//
                mLocation.setLatitude(Double.parseDouble("40.690549"));
                mLocation.setLongitude(Double.parseDouble("-73.966133"));
                //*************************************************************************//

                GeoPoint mGeoPointMyLocation = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
                if(mMarkerMyLocation != null) {
                    mMarkerMyLocation.setPosition(mGeoPointMyLocation);
                } else {
                    mMarkerMyLocation = new Marker(mMapView);
                    mMarkerMyLocation.setPosition(mGeoPointMyLocation);
                    //mMarkerMyLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                    Drawable mDrawable = ContextCompat.getDrawable(mActivity, R.drawable.agent_marker);
                    mMarkerMyLocation.setIcon(mUtility.scaleImage(mActivity, mDrawable, (float) 0.45));

                    mMarkerMyLocation.setTitle(mActivity.getString(R.string.my_location));

                    MyLocationInfoWindow mMyLocationInfoWindow = new MyLocationInfoWindow(R.layout.layout_custom_info_window, mMapView);
                    mMarkerMyLocation.setInfoWindow(mMyLocationInfoWindow);

                    mMarkerMyLocation.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                            try {
                                if (mPolyline != null) {
                                    mPolyline.closeInfoWindow();
                                }
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

                    if(mPolyline != null) {
                        mPolyline.closeInfoWindow();
                    }
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

    public void clearMapComponents() {
        try {
            if(mPolygonArrowsList != null && mPolygonArrowsList.size() > 0) {
                for (int i = 0; i< mPolygonArrowsList.size(); i++) {
                    mMapView.getOverlays().remove(mPolygonArrowsList.get(i));
                }
                mPolygonArrowsList.clear();
            }
            if(mMarkersCustomers != null && mMarkersCustomers.size() > 0) {
                for (int i = 0; i< mMarkersCustomers.size(); i++) {
                    mMarkersCustomers.remove(mMapView);
                }
            }
            if(mMarkerBranchLocation != null) {
                mMarkerBranchLocation.remove(mMapView);
            }
            if(mMarkerMyLocation != null) {
                mMarkerMyLocation.remove(mMapView);
                mMapView.getOverlays().add(mMarkerMyLocation);
            }
            mMapView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}