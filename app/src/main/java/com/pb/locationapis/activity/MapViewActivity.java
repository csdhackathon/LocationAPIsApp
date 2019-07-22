package com.pb.locationapis.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.pb.locationapis.R;
import com.pb.locationapis.infowindow.CustomerInfoWindow;
import com.pb.locationapis.listener.INotifyGPSLocationListener;
import com.pb.locationapis.manager.AppManager;
import com.pb.locationapis.model.bo.routes.CustomerVo;
import com.pb.locationapis.model.bo.routes.RoutesBO;
import com.pb.locationapis.service.GpsLocationTracker;
import com.pb.locationapis.utility.ConstantUnits;
import com.pb.locationapis.utility.CustomAlertDialogUtility;
import com.pb.locationapis.utility.CustomProgressDialogUtility;
import com.pb.locationapis.utility.MapUtility;
import com.pb.locationapis.utility.Utility;
import com.pb.locationapis.utility.ValidationsUtility;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pb.ApiClient;
import pb.ApiException;
import pb.locationintelligence.LIAPIGeoZoneServiceApi;
import pb.locationintelligence.LIAPIGeocodeServiceApi;
import pb.locationintelligence.model.Candidate;
import pb.locationintelligence.model.GeocodeAddress;
import pb.locationintelligence.model.GeocodeRequest;
import pb.locationintelligence.model.GeocodeServiceResponse;
import pb.locationintelligence.model.GeocodeServiceResponseList;
import pb.locationintelligence.model.TravelBoundaries;

/**
 * Map View Activity Class used to Show the Map alons with the GeoZone and Expected Customers
 */
public class MapViewActivity extends Activity implements INotifyGPSLocationListener {

    private final String TAG = MapViewActivity.class.getSimpleName();

    private MapUtility mMapUtility;
    private MapTileProviderBase mProvider;

    private MapView mMapView;

    private Button mButtonPreviewMarketingContent;
    private Button mButtonFindCustomers;
    private Location mLocation;
    private ValidationsUtility mValidationsUtility;
    private Utility mUtility;
    private CustomAlertDialogUtility mCustomAlertDialogUtility;
    private CustomProgressDialogUtility mCustomProgressDialogUtility;
    private ConstantUnits mConstantUnits;

    private Spinner mSpinner;

    private List<List<Double>> mPolygonCoordinates = new ArrayList<>();
    private List<List<Double>> mCustomersCoordinates = new ArrayList<>();

    private List<Double> mLats=new ArrayList<Double>();
    private List<Double> mLng=new ArrayList<Double>();

    private List<Polygon> mPolygonList = new ArrayList<>();

    private List<Marker> mMarkersCustomers = new ArrayList<>();
    private CustomerVo mFirstCustomerVo = null;

    private RoutesBO mRoutesBO;
    private List<GeoPoint> mGeoPoints;
    private GeocodeServiceResponseList geoCodeResponse = null;
    private String errorMessageForGeoCodeResp = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_map_view);

            mValidationsUtility = ValidationsUtility.getInstance();
            mUtility = Utility.getInstance();
            mCustomAlertDialogUtility = CustomAlertDialogUtility.getInstance();
            mCustomProgressDialogUtility = CustomProgressDialogUtility.getInstance();
            mConstantUnits = ConstantUnits.getInstance();

            initializeLayoutViews();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initializeLayoutViews() {
        try {
            //important! set your user agent to prevent getting banned from the osm servers
            Configuration.getInstance().load(MapViewActivity.this.getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(MapViewActivity.this.getApplicationContext()));

            mRoutesBO = AppManager.getInstance().getRoutesBO();

            mButtonFindCustomers = (Button) findViewById(R.id.button_find_customers);
            mButtonFindCustomers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mMarkersCustomers != null && mMarkersCustomers.size() > 0) {
                        if(mMapView != null) {
                            for (int k = 0; k < mMarkersCustomers.size(); k++) {
                                mMapView.getOverlays().remove(mMarkersCustomers.get(k));
                            }
                        }
                    }
                    if(mPolygonList != null && mPolygonList.size() > 0 ) {
                        if(mMapView != null) {
                            for (int k = 0; k < mPolygonList.size(); k++) {
                                mMapView.getOverlays().remove(mPolygonList.get(k));
                            }
                        }
                    }
                    String selectedTime = mSpinner.getSelectedItem().toString().replace(" Minutes", "").trim();
                    callGeoZoneServiceApi(selectedTime);
                }
            });

            ((LinearLayout) findViewById(R.id.linear_lay_back)).bringToFront();
            ((LinearLayout) findViewById(R.id.linear_lay_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            mSpinner = (Spinner) findViewById(R.id.spinner);

            // Spinner Drop down elements
            List<String> categories = new ArrayList();
            categories.add("30 Minutes");
            categories.add("60 Minutes");

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            mSpinner.setAdapter(dataAdapter);

            mButtonPreviewMarketingContent = (Button) findViewById(R.id.button_preview_marketing_content);
            mButtonPreviewMarketingContent.setEnabled(false);
            mButtonPreviewMarketingContent.setTextColor(ContextCompat.getColor(MapViewActivity.this, R.color.pb_gray_300_button_pressed));
            mButtonPreviewMarketingContent.setBackground(ContextCompat.getDrawable(MapViewActivity.this, R.drawable.inactive_blue_button_bg));
            mButtonPreviewMarketingContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MapViewActivity.this, MarketingContentActivity.class);
                    intent.putExtra(mConstantUnits.customerVO, mFirstCustomerVo);
                    startActivity(intent);
                }
            });

            mMapView = (MapView) findViewById(R.id.map_view_tab_fragment);

            mMapUtility = new MapUtility(MapViewActivity.this, mMapView);

            if(mMapUtility != null) {
                mMapUtility.initializeMapViews(MapViewActivity.this, mProvider);
            }
            if(mLocation == null) {
                if(GpsLocationTracker.getInstance() == null) {
                    GpsLocationTracker.initialize(MapViewActivity.this, MapViewActivity.this);
                }
                mLocation = GpsLocationTracker.getInstance().getLocation();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGeoZoneServiceApi(String travelTime) {
        try {
            mUtility.hideSoftKeyboard(MapViewActivity.this);
            if(!mUtility.isConnectedToNetwork(MapViewActivity.this)) {
                mCustomAlertDialogUtility.showCustomAlertDialog(MapViewActivity.this, mConstantUnits.EMPTY,
                        getResources().getString(R.string.check_your_internet_connectivity));
                return;
            }

            mCustomProgressDialogUtility.showCustomProgressDialog(MapViewActivity.this, "", getString(R.string.please_wait));
            if(mMapUtility != null) {
                mMapUtility.closeInfoWindowOfMyLocationMarker();
            }
            new MyGeoZoneAsyncTask(travelTime).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private class MyGeoZoneAsyncTask extends AsyncTask<String, Void, String> {

        private String travelTime;
        private TravelBoundaries geoZoneResponse = null;
        private String errorMessage = "";

        public MyGeoZoneAsyncTask(String time) {
            this.travelTime = time;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response="";
            try {
                ApiClient defaultClient = pb.Configuration.getDefaultApiClient();

                defaultClient.setoAuthApiKey(mUtility.getMetaDataFromManifest(mConstantUnits.PBGEOMAP_ACCESS_TOKEN, MapViewActivity.this));
                defaultClient.setoAuthSecret(mUtility.getMetaDataFromManifest(mConstantUnits.PBGEOMAP_SECRET_KEY, MapViewActivity.this));

                final LIAPIGeoZoneServiceApi api = new LIAPIGeoZoneServiceApi();
                String costs = travelTime;
                String point = mLocation.getLongitude() + "," + mLocation.getLatitude() + ",epsg:4326";
                String address = null;
                String costUnit = "min";
                String db = "DRIVING";
                String country = null;
                String maxOffroadDistance = null;
                String maxOffroadDistanceUnit = null;
                String destinationSrs = null;
                String majorRoads = "true";
                String returnHoles = null;
                String returnIslands = null;
                String simplificationFactor = null;
                String bandingStyle = null;
                String historicTrafficTimeBucket = null;

                try {
                    Log.i("GeoZone","getTravelBoundaryByTime");
                    geoZoneResponse = api.getTravelBoundaryByTime(costs, point, address, costUnit, db, country, maxOffroadDistance, maxOffroadDistanceUnit, destinationSrs, majorRoads, returnHoles, returnIslands, simplificationFactor, bandingStyle, historicTrafficTimeBucket);
                } catch (ApiException e) {
                    e.printStackTrace();
                    if(e != null && e.getMessage() != null) {
                        errorMessage = e.getMessage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(geoZoneResponse != null) {
                    response= geoZoneResponse.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Result",result);
            if(geoZoneResponse !=null) {
                addPolygonOnMap(geoZoneResponse);
                new MyGeoCodeAsyncTask().execute();
            }
            else {
                mCustomProgressDialogUtility.dismissProgressDialog();
                if(errorMessage.contains("Exception")) {
                    errorMessage = errorMessage.substring(errorMessage.indexOf("Exception") + "Exception".length() + 1).trim();
                }
                mCustomAlertDialogUtility.showCustomAlertDialog(MapViewActivity.this, getString(R.string.error), errorMessage);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void addPolygonOnMap(TravelBoundaries resp) {
        try {
            mGeoPoints =null;
            if(mPolygonList != null && mPolygonList.size() > 0 ) {
                if(mMapView != null) {
                    for (int k = 0; k < mPolygonList.size(); k++) {
                        mMapView.getOverlays().remove(mPolygonList.get(k));
                    }
                }
            }
            mPolygonList = new ArrayList<>();
            List<List<List<Double>>> mCoordinatesList = resp.getTravelBoundary().getCosts().get(0).getGeometry().getCoordinates().get(0);
            for (int i=0; i< mCoordinatesList.size(); i++) {
                mPolygonCoordinates = mCoordinatesList.get(i);
                mGeoPoints = new ArrayList<>();

                for (int j = 0; j< mPolygonCoordinates.size(); j++) {
                    double lat = mPolygonCoordinates.get(j).get(1);
                    double lng = mPolygonCoordinates.get(j).get(0);
                    mLats.add(lat);
                    mLng.add(lng);
                    mGeoPoints.add(new GeoPoint(lat, lng));
                }
                if(mGeoPoints.size() > 0) {
                    Polygon mPolygon = new Polygon();

                    mPolygon.setPoints(mGeoPoints);
                    mPolygon.setFillColor(Color.parseColor("#53009fff"));
                    mPolygon.setStrokeColor(Color.parseColor("#009fff"));
                    mPolygon.setStrokeWidth(3);
                    mPolygonList.add(mPolygon);
                }
            }

            loadPolygonOnMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPolygonOnMap() {
        if(mPolygonList != null && mPolygonList.size() > 0 ) {
            for (int i = 0; i < mPolygonList.size(); i++) {
                mMapView.getOverlays().add(mPolygonList.get(i));
            }
        }

        if(mGeoPoints !=null && mGeoPoints.size()>0){
            BoundingBox bounding=getBoundingBox(mGeoPoints);
            mMapView.zoomToBoundingBox(bounding,true);
        }
    }


    private BoundingBox getBoundingBox(List<GeoPoint> lstGeoPoint){
        double minLat = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double minLong = Double.MAX_VALUE;
        double maxLong = -Double.MAX_VALUE;

        for (GeoPoint point: lstGeoPoint) {

            if (point.getLatitude() < minLat)
                minLat = point.getLatitude();
            if (point.getLatitude() > maxLat)
                maxLat = point.getLatitude();
            if (point.getLongitude() < minLong)
                minLong = point.getLongitude();
            if (point.getLongitude() > maxLong)
                maxLong = point.getLongitude();
        }
        BoundingBox boundingBox = new BoundingBox(maxLat, maxLong, minLat, minLong);
        return  boundingBox;
    }


    private class MyGeoCodeAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            geoCodeResponse = null;
        }

        @Override
        protected String doInBackground(String... params) {

            final LIAPIGeocodeServiceApi api = new LIAPIGeocodeServiceApi();
            String datapackBundle = mConstantUnits.PREMIUM;
            GeocodeRequest body = new GeocodeRequest();
            try {
                ApiClient defaultClient = pb.Configuration.getDefaultApiClient();

                defaultClient.setoAuthApiKey(mUtility.getMetaDataFromManifest(mConstantUnits.PBGEOMAP_ACCESS_TOKEN, MapViewActivity.this));
                defaultClient.setoAuthSecret(mUtility.getMetaDataFromManifest(mConstantUnits.PBGEOMAP_SECRET_KEY, MapViewActivity.this));

                List<GeocodeAddress> addresses = new ArrayList<GeocodeAddress>();

                for(int i=0; i<mRoutesBO.getCustomerVos().size(); i++) {

                    GeocodeAddress address = new GeocodeAddress();
                    String customerAdd = "";
                    customerAdd = mRoutesBO.getCustomerVos().get(i).getAddressLine1();
                    if(!mRoutesBO.getCustomerVos().get(i).getAddressLine2().equalsIgnoreCase("")) {
                        customerAdd = customerAdd + " " + mRoutesBO.getCustomerVos().get(i).getAddressLine2();
                    }
                    if(!mRoutesBO.getCustomerVos().get(i).getCity().equalsIgnoreCase("")) {
                        customerAdd = customerAdd + " " + mRoutesBO.getCustomerVos().get(i).getCity();
                    }
                    if(!mRoutesBO.getCustomerVos().get(i).getCountry().equalsIgnoreCase("")) {
                        customerAdd = customerAdd + " " + mRoutesBO.getCustomerVos().get(i).getCountry();
                    }
                    address.mainAddressLine(customerAdd);
                    addresses.add(address);
                }

                body.setAddresses(addresses);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Log.i("GeoCode","geocodeBatch");
                geoCodeResponse = api.geocodeBatch(datapackBundle, body);
            } catch (ApiException e) {
                e.printStackTrace();
                if(e != null && e.getMessage() != null) {
                    Log.i("GeoCode", "Cause" + e.getMessage());
                    errorMessageForGeoCodeResp = e.getMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(geoCodeResponse != null) {
                return geoCodeResponse.toString();
            }
            else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Log.i("Result", result);
                mFirstCustomerVo = null;
                loadMapForGeoCodeResponse();
                mCustomProgressDialogUtility.dismissProgressDialog();
                if(geoCodeResponse == null && !errorMessageForGeoCodeResp.equalsIgnoreCase(mConstantUnits.EMPTY)) {
                    if(errorMessageForGeoCodeResp.contains("Exception")) {
                        errorMessageForGeoCodeResp = errorMessageForGeoCodeResp.substring(errorMessageForGeoCodeResp.indexOf("Exception") + "Exception".length() + 1).trim();
                    }
                    mCustomAlertDialogUtility.showCustomAlertDialog(MapViewActivity.this, mConstantUnits.EMPTY, errorMessageForGeoCodeResp);
                } else if(mFirstCustomerVo == null) {
                    //Toast.makeText(MapViewActivity.this, "No customer found for selected drive time!", Toast.LENGTH_LONG).show();
                    CustomAlertDialogUtility.getInstance().showCustomAlertDialog(MapViewActivity.this, "", "No customer found for selected drive time!");
                }
            } catch (Exception e) {
                mCustomProgressDialogUtility.dismissProgressDialog();
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void loadMapForGeoCodeResponse() {
        if(geoCodeResponse != null) {
            mButtonPreviewMarketingContent.setTextColor(ContextCompat.getColor(MapViewActivity.this, R.color.pb_gray_50_white));
            mButtonPreviewMarketingContent.setBackground(ContextCompat.getDrawable(MapViewActivity.this, R.drawable.selector_blue_button_bg));
            mButtonPreviewMarketingContent.setEnabled(true);
            List<GeocodeServiceResponse> mGeocodeServiceResponseList = geoCodeResponse.getResponses();
            if(mMarkersCustomers != null && mMarkersCustomers.size() > 0) {
                if(mMapView != null) {
                    for (int k = 0; k < mMarkersCustomers.size(); k++) {
                        mMapView.getOverlays().remove(mMarkersCustomers.get(k));
                    }
                }
            }
            mMarkersCustomers = new ArrayList<>();
            //Collections.reverse(mPolygonCoordinates);
            for (int i=0; i<mGeocodeServiceResponseList.size(); i++) {
                List<Candidate> mCandidateList = mGeocodeServiceResponseList.get(i).getCandidates();
                mCustomersCoordinates.add(mCandidateList.get(0).getGeometry().getCoordinates());
                if(isPointInPolygon(mCandidateList.get(0).getGeometry().getCoordinates(), mPolygonCoordinates )) {
                    Log.i(TAG, "Customer : " + mCustomersCoordinates.get(i).toString());
                    addMarkerOnMap(mCustomersCoordinates.get(i), mCandidateList.get(0).getFormattedLocationAddress());
                }
            }
            if(mFirstCustomerVo == null) {
                mButtonPreviewMarketingContent.setEnabled(false);
                mButtonPreviewMarketingContent.setTextColor(ContextCompat.getColor(MapViewActivity.this, R.color.pb_gray_300_button_pressed));
                mButtonPreviewMarketingContent.setBackground(ContextCompat.getDrawable(MapViewActivity.this, R.drawable.inactive_blue_button_bg));
            }
        }
    }

    private void addMarkerOnMap(List<Double> coordinates, String formattedLocationAddress) {
        try {
            Marker customerMarker = new Marker(mMapView);
            GeoPoint geoPoint = new GeoPoint(coordinates.get(1), coordinates.get(0));
            customerMarker.setPosition(geoPoint);
            Drawable mDrawable = ContextCompat.getDrawable(MapViewActivity.this, R.drawable.circle_marker_icon);
            customerMarker.setIcon(mUtility.scaleImage(MapViewActivity.this, mDrawable, ((float) 0.35)));
            CustomerInfoWindow mCustomerInfoWindow = new CustomerInfoWindow(R.layout.layout_custom_info_window, mMapView);
            CustomerVo mCustomerVo = getCustomerVoFromBO(coordinates, formattedLocationAddress);
            customerMarker.setTitle(mCustomerVo.getAddressLine1());

            mCustomerInfoWindow.setCustomerListItem(mCustomerVo);
            customerMarker.setInfoWindow(mCustomerInfoWindow);

            customerMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    try {
                        for(int i=0; i<mMapView.getOverlays().size(); ++i){
                            Overlay o = mMapView.getOverlays().get(i);
                            if(o instanceof Marker){
                                Marker m = (Marker) o;
                                if(m.getTitle() != null && !m.getTitle().equalsIgnoreCase(marker.getTitle())) {
                                    if (m != null) {
                                        m.closeInfoWindow();
                                    }
                                }
                                else {
                                    if(marker != null) {
                                        marker.showInfoWindow();
                                    }
                                }
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
            if(mMapView != null) {
                mMapView.getOverlays().add(customerMarker);
                mMarkersCustomers.add(customerMarker);
                mMapView.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private CustomerVo getCustomerVoFromBO(List<Double> coordinates, String formattedLocationAddress) {
        CustomerVo mCustomerVo = null;
        try {
            for (int i=0; i<mRoutesBO.getCustomerVos().size(); i++) {
                if(mRoutesBO.getCustomerVos().get(i).getLatitude().equalsIgnoreCase(String.valueOf(coordinates.get(1)))
                        && mRoutesBO.getCustomerVos().get(i).getLongitude().equalsIgnoreCase(String.valueOf(coordinates.get(0)))) {
                    mCustomerVo = mRoutesBO.getCustomerVos().get(i);
                    if(mFirstCustomerVo == null || TextUtils.isEmpty(mFirstCustomerVo.getName())) {
                        mFirstCustomerVo = mCustomerVo;
                    }
                    return mCustomerVo;
                }
                else if(mRoutesBO.getCustomerVos().get(i).getLatitude().equalsIgnoreCase(String.valueOf(coordinates.get(0)))
                        && mRoutesBO.getCustomerVos().get(i).getLongitude().equalsIgnoreCase(String.valueOf(coordinates.get(1)))) {
                    mCustomerVo = mRoutesBO.getCustomerVos().get(i);
                    if(mFirstCustomerVo == null || TextUtils.isEmpty(mFirstCustomerVo.getName())) {
                        mFirstCustomerVo = mCustomerVo;
                    }
                    return mCustomerVo;
                }
            }
            if(mCustomerVo == null) {
                mCustomerVo = new CustomerVo();
                mCustomerVo.setName("");
                mCustomerVo.setAddressLine1(formattedLocationAddress);
                if(mFirstCustomerVo == null) {
                    mFirstCustomerVo = mCustomerVo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCustomerVo;
    }


    private boolean isPointInPolygon(List<Double> tap, List<List<Double>> vertices) {
        int intersectCount = 0;
        Collections.reverse(vertices);
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(List<Double> tap, List<Double> vertA, List<Double> vertB) {

        double aY = vertA.get(0);
        double bY = vertB.get(0);
        double aX = vertA.get(1);
        double bX = vertB.get(1);
        double pY = tap.get(0);
        double pX = tap.get(1);

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(MapViewActivity.this, PreferenceManager.getDefaultSharedPreferences(MapViewActivity.this));
        if(mMapUtility != null) {
            mMapUtility.setMyLocationMarker(mLocation, true);
        }
        loadPolygonOnMap();
        loadMapForGeoCodeResponse();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onReceiveLocationDetails(Location location) {
        this.mLocation = location;
        if(mMapUtility != null) {
            mMapUtility.setMyLocationMarker(mLocation, false);
        }
    }




}
