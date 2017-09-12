package com.pb.locationapis.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pb.locationapis.R;
import com.pb.locationapis.infowindow.CustomerInfoWindow;
import com.pb.locationapis.listener.INotifyGPSLocationListener;
import com.pb.locationapis.model.bo.routes.CustomerVo;
import com.pb.locationapis.model.bo.routes.RoutesBO;
import com.pb.locationapis.preference.SharedPreferenceHelper;
import com.pb.locationapis.service.GpsLocationTracker;
import com.pb.locationapis.utility.ConstantUnits;
import com.pb.locationapis.utility.CustomAlertDialogUtility;
import com.pb.locationapis.utility.CustomProgressDialogUtility;
import com.pb.locationapis.utility.MapUtility;
import com.pb.locationapis.utility.Utility;
import com.pb.locationapis.utility.ValidationsUtility;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
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
 * Created by NEX7IMH on 9/12/2017.
 */

public class MapViewActivity extends Activity implements INotifyGPSLocationListener {

    private final String TAG = MapViewActivity.class.getSimpleName();

    private MapUtility mMapUtility;
    private MapTileProviderBase mProvider;

    private MapView mMapView;
    private RelativeLayout mRelativeLayoutMyLocationButton;
    private Button mButtonPreviewMarketingContent;
    private EditText mEditTextTravelTime;
    private Button mButtonFindCustomers;
    private Location mLocation;
    private ValidationsUtility mValidationsUtility;
    private Utility mUtility;
    private CustomAlertDialogUtility mCustomAlertDialogUtility;
    private CustomProgressDialogUtility mCustomProgressDialogUtility;
    private ConstantUnits mConstantUnits;
    private SharedPreferenceHelper mSharedPreferenceHelper;

    private List<List<Double>> mPolygonCoordinates = new ArrayList<>();
    private List<List<Double>> mCustomersCoordinates = new ArrayList<>();

    private List<Polygon> mPolygonList = new ArrayList<>();

    private List<Marker> mMarkersCustomers = new ArrayList<>();
    private CustomerVo mFirstCustomerVo = null;

    private RoutesBO mRoutesBO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_map_view);

            mValidationsUtility = ValidationsUtility.getInstance(this);
            mUtility = Utility.getInstance(this);
            mCustomAlertDialogUtility = CustomAlertDialogUtility.getInstance();
            mCustomProgressDialogUtility = CustomProgressDialogUtility.getInstance();
            mConstantUnits = ConstantUnits.getInstance();
            mSharedPreferenceHelper = SharedPreferenceHelper.getInstance();

            initializeLayoutViews();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeLayoutViews() {
        try {
            //important! set your user agent to prevent getting banned from the osm servers
            Configuration.getInstance().load(MapViewActivity.this.getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(MapViewActivity.this.getApplicationContext()));

            mEditTextTravelTime = (EditText) findViewById(R.id.editText_travel_time);
            mUtility.setFontRegular(mEditTextTravelTime);
            mUtility.setFontRegular((TextView) findViewById(R.id.textView_enter_travel_time));
            mUtility.setFontRegular((TextView) findViewById(R.id.textView_enter_travel_time_unit));
            mUtility.setFontRegular((TextInputLayout) findViewById(R.id.text_input_layout_travel_time));

            mButtonFindCustomers = (Button) findViewById(R.id.button_find_customers);
            mButtonFindCustomers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(validate(mEditTextTravelTime.getText().toString())) {
                        callGeoZoneServiceApi(mEditTextTravelTime.getText().toString());
                    }
                }
            });

            ((LinearLayout) findViewById(R.id.linear_lay_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            mButtonPreviewMarketingContent = (Button) findViewById(R.id.button_preview_marketing_content);
            mButtonPreviewMarketingContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MapViewActivity.this, MarketingContentActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable(mConstantUnits.customerVO, mFirstCustomerVo);
                    intent.putExtras(args);
                    startActivity(intent);
                    //((HomeActivity) mContext).replaceFragment(MarketingContentFragment.newInstance(mFirstCustomerVo, null));
                }
            });

            mMapView = (MapView) findViewById(R.id.map_view_tab_fragment);

            mMapUtility = new MapUtility(MapViewActivity.this, mMapView);

            if(mMapUtility != null) {
                mMapUtility.initializeMapViews(MapViewActivity.this, mProvider);
            }
            mMapUtility.setMyLocationMarker(GpsLocationTracker.getInstance().getLocation(), true);

            mRelativeLayoutMyLocationButton = (RelativeLayout) findViewById(R.id.relative_lay_my_location_button);
            mRelativeLayoutMyLocationButton.bringToFront();

            mRelativeLayoutMyLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(GpsLocationTracker.getInstance() != null) {
                        if(mMapUtility != null) {
                            mMapUtility.setMyLocationMarker(GpsLocationTracker.getInstance().getLocation(), true);
                        }
                    }
                }
            });

            mRoutesBO = HomeActivity.getmRoutesBO();

            //new LoadMapComponents(mRoutesBO).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validate(String inputTravelTime) {

        try {
            if (inputTravelTime.isEmpty()) {
                mValidationsUtility.setValidationError(mEditTextTravelTime, getResources().getString(R.string.enter_travel_time));
                return false;
            }
            else {
                mEditTextTravelTime.setError(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void callGeoZoneServiceApi(String travelTime) {
        try {
            mUtility.hideSoftKeyboard(MapViewActivity.this);
            if(!mUtility.isConnectedToNetwork()) {
                mCustomAlertDialogUtility.showCustomAlertDialog(MapViewActivity.this, mConstantUnits.EMPTY,
                        getResources().getString(R.string.check_your_internet_connectivity));
                return;
            }

            if(mLocation == null) {
                mLocation = GpsLocationTracker.getInstance().getLocation();
                //*************************************************************************//
                mLocation.setLatitude(Double.parseDouble("40.690549"));
                mLocation.setLongitude(Double.parseDouble("-73.966133"));
                //*************************************************************************//
            }

            if(mMapView != null) {
                mMapView.getOverlays().clear();
            }
            new MyGeoZoneAsyncTask(travelTime).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class MyGeoZoneAsyncTask extends AsyncTask<String, Void, String> {

        private String travelTime;
        private TravelBoundaries geoZoneResponse = null;

        public MyGeoZoneAsyncTask(String time) {
            this.travelTime = time;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCustomProgressDialogUtility.showCustomProgressDialog(MapViewActivity.this, getString(R.string.please_wait), "");
        }

        @Override
        protected String doInBackground(String... params) {
            String response=null;

            try {
                ApiClient defaultClient = pb.Configuration.getDefaultApiClient();

                defaultClient.setoAuthApiKey(mSharedPreferenceHelper.getStringPreference(MapViewActivity.this, mSharedPreferenceHelper.API_KEY));
                defaultClient.setoAuthSecret(mSharedPreferenceHelper.getStringPreference(MapViewActivity.this, mSharedPreferenceHelper.SECRET_KEY));

                //defaultClient.setoAuthApiKey("lyu5KVPcrO370lsOI8hHf40PGgboUkgM");
                //defaultClient.setoAuthSecret("XNrpGEnl9dFbrXb0");

                //defaultClient.setAccessToken("pRkSRE81OKGlj2icxgDI2LOnGfNT");
                //defaultClient.setAccessToken("ASG69kLorPQOJIf7QBtK2lmyLfU4");

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
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(geoZoneResponse == null) {
                    mCustomAlertDialogUtility.showCustomAlertDialog(MapViewActivity.this, getString(R.string.please_try_again_later), "");
                }
                else {
                    response= geoZoneResponse.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you*/
            //mCustomProgressDialogUtility.dismissProgressDialog();
            Log.i("Result",result);
            if(geoZoneResponse !=null) {
                addPolygonOnMap(geoZoneResponse);

                new MyGeoCodeAsyncTask().execute();
            }
            else {
                mCustomProgressDialogUtility.dismissProgressDialog();
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void addPolygonOnMap(TravelBoundaries resp) {
        try {
            List<List<List<Double>>> mCoordinatesList = resp.getTravelBoundary().getCosts().get(0).getGeometry().getCoordinates().get(0);

            for (int i=0; i< mCoordinatesList.size(); i++) {

                mPolygonCoordinates = mCoordinatesList.get(i);

                List<GeoPoint> geoPoints = new ArrayList<>();

                for (int j = 0; j< mPolygonCoordinates.size(); j++) {
                    double lat = mPolygonCoordinates.get(j).get(1);
                    double lng = mPolygonCoordinates.get(j).get(0);
                    geoPoints.add(new GeoPoint(lat, lng));
                }
                if(geoPoints.size() > 0) {

                    Polygon mPolygon = new Polygon();

                    mPolygon.setPoints(geoPoints);
                    mPolygon.setFillColor(Color.parseColor("#53009fff"));
                    mPolygon.setStrokeColor(Color.parseColor("#009fff"));
                    mPolygon.setStrokeWidth(3);

                    mPolygonList.add(mPolygon);
                }

            }

            if(mPolygonList.size() > 0 ) {
                if(mMapView != null) {
                    mMapView.getOverlays().clear();
                }
                for (int k = 0; k < mPolygonList.size(); k++) {
                    mMapView.getOverlays().add(mPolygonList.get(k));
                }
                mMapUtility.setMyLocationMarker(mLocation, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyGeoCodeAsyncTask extends AsyncTask<String, Void, String>{

        GeocodeServiceResponseList geoCodeResponse = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            final LIAPIGeocodeServiceApi api = new LIAPIGeocodeServiceApi();
            String datapackBundle = "premium";
            GeocodeRequest body = new GeocodeRequest();
            try {
                ApiClient defaultClient = pb.Configuration.getDefaultApiClient();

                defaultClient.setoAuthApiKey(mSharedPreferenceHelper.getStringPreference(MapViewActivity.this, mSharedPreferenceHelper.API_KEY));
                defaultClient.setoAuthSecret(mSharedPreferenceHelper.getStringPreference(MapViewActivity.this, mSharedPreferenceHelper.SECRET_KEY));

                //defaultClient.setoAuthApiKey("lyu5KVPcrO370lsOI8hHf40PGgboUkgM");
                //defaultClient.setoAuthSecret("XNrpGEnl9dFbrXb0");


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
                //address.mainAddressLine(" 6, Global ViewAddress , Troy, NY");

                body.setAddresses(addresses);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Log.i("GeoCode","geocodeBatch");
                geoCodeResponse = api.geocodeBatch(datapackBundle, body);
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(geoCodeResponse == null) {
                mCustomAlertDialogUtility.showCustomAlertDialog(MapViewActivity.this, getString(R.string.please_try_again_later), "");
            }
            return geoCodeResponse.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you*/
            try {
                Log.i("Result", result);

                if(geoCodeResponse != null) {
                    List<GeocodeServiceResponse> mGeocodeServiceResponseList = geoCodeResponse.getResponses();
                    for (int i=0; i<mGeocodeServiceResponseList.size(); i++) {

                        List<Candidate> mCandidateList = mGeocodeServiceResponseList.get(i).getCandidates();
                        mCustomersCoordinates.add(mCandidateList.get(0).getGeometry().getCoordinates());

                        if(isPointInPolygon(mCandidateList.get(0).getGeometry().getCoordinates(), mPolygonCoordinates)) {
                            Log.i(TAG, "Customer : " + mCustomersCoordinates.get(i).toString());

                            addMarkerOnMap(mCustomersCoordinates.get(i), mCandidateList.get(0).getFormattedLocationAddress());
                        }
                    }

                    if(mCustomersCoordinates != null && mCustomersCoordinates.size() > 0) {
                        for (int i=0; i<mCustomersCoordinates.size(); i++) {

                        }
                    }

                }
                mCustomProgressDialogUtility.dismissProgressDialog();
            } catch (Exception e) {
                mCustomProgressDialogUtility.dismissProgressDialog();
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void addMarkerOnMap(List<Double> coordinates, String formattedLocationAddress) {
        try {
            Marker customerMarker = new Marker(mMapView);
            GeoPoint geoPoint = new GeoPoint(coordinates.get(1), coordinates.get(0));
            customerMarker.setPosition(geoPoint);
            //customerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            Drawable mDrawable = ContextCompat.getDrawable(MapViewActivity.this, R.drawable.circle_marker_icon);

            customerMarker.setIcon(mUtility.scaleImage(MapViewActivity.this, mDrawable, ((float) 0.35)));
            //customerMarker.setTitle(mCustomerVo.getCustomerId()+"-"+mCustomerVo.getName());
            //customerMarker.setSnippet(mCustomerVo.getAddress());
            //customerMarker.setSubDescription("Hadapsar, Pune, Maharashtra, India 411028");

            CustomerInfoWindow mCustomerInfoWindow = new CustomerInfoWindow(R.layout.layout_custom_info_window, mMapView);
            CustomerVo mCustomerVo = null;
            mCustomerVo = new CustomerVo();
            mCustomerVo.setName("");
            mCustomerVo.setAddressLine1(formattedLocationAddress);
            for (int i=0; i<mRoutesBO.getCustomerVos().size(); i++) {
                if(geoPoint.equals(geoPoint))
                    if(mRoutesBO.getCustomerVos().get(i).getLatitude().equalsIgnoreCase(String.valueOf(coordinates.get(1)))
                            && mRoutesBO.getCustomerVos().get(i).getLongitude().equalsIgnoreCase(String.valueOf(coordinates.get(0)))) {
                        mCustomerVo = mRoutesBO.getCustomerVos().get(i);
                        if(mFirstCustomerVo != null) {
                            mFirstCustomerVo = mCustomerVo;
                        }
                        continue;
                    }
                    else if(mRoutesBO.getCustomerVos().get(i).getLatitude().equalsIgnoreCase(String.valueOf(coordinates.get(0)))
                            && mRoutesBO.getCustomerVos().get(i).getLongitude().equalsIgnoreCase(String.valueOf(coordinates.get(1)))) {
                        mCustomerVo = mRoutesBO.getCustomerVos().get(i);
                        if(mFirstCustomerVo != null) {
                            mFirstCustomerVo = mCustomerVo;
                        }
                        continue;
                    }
            }
            if(mFirstCustomerVo != null) {
                mFirstCustomerVo = mCustomerVo;
            }

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

            mMapView.getOverlays().add(customerMarker);
            mMarkersCustomers.add(customerMarker);
            mMapView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPointInPolygon(List<Double> tap, List<List<Double>> vertices) {
        int intersectCount = 0;
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onReceiveLocationDetails(Location location) {

        //*************************************************************************//
        mLocation.setLatitude(Double.parseDouble("40.690549"));
        mLocation.setLongitude(Double.parseDouble("-73.966133"));
        //*************************************************************************//

        this.mLocation = location;
        if(mMapUtility != null) {
            mMapUtility.setMyLocationMarker(mLocation, false);
        }
    }


}
