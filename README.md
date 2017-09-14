![Pitney Bowes](/PitneyBowes_Logo.jpg)

# Pitney Bowes SMB Challenge 2017

### Sample App showcasing consumption of [Pitney Bowes Location Intelligence APIs](http://www.pitneybowes.com/us/developer/geocoding-apis.html)

### Use Case
Targeted Marketing to nearby customers

### Description
All address of the day’s shipments can be used to find out which addresses fall within a travel boundary (drive time) of 60 minutes. These customers can be sent out promotional leaflets for example, offering them same day delivery if they order before noon and ship to the same address. The shortlisted addresses along with customer name, can be fed into a marketing template and sent to printer connected to SendPro machine. These promotional leaflets can be sent along with the actual shipment.

### Locate APIs Used:

* [GeoCode](https://locate.pitneybowes.com/geocode): Used to geocode (retrieve latitude and longitude) of shipping addresses.

* [GeoZone](https://locate.pitneybowes.com/geozone): Used to get travel boundary around the place of business)

* [GeoMap](https://locate.pitneybowes.com/geomap): Used to show SMB business location and the targeted shipping addresses within the travel boundary on map.


### App Code Details

### Manifest
In most cases, the following authorizations need to be set in AndroidManifest.xml. 

#### The permissions required by the app are:
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

#### Location Intelligence API Key Setting:
<meta-data
	android:name="PBGEO_GEOMAP_THEME"
	android:value="bronze" />

<meta-data
	android:name="API_KEY"
	android:value="" />

<meta-data
	android:name="SECRET"
	android:value="" />
<meta-data
	android:name="PBGEOMAP_URL"
	android:value="https://api.pitneybowes.com/location-intelligence/geomap/v1/tile/osm/" />
	
* [Click Here](http://www.pitneybowes.com/us/developer/geocoding-apis.html) to Subscribe for free Trial and get your API_Key and Secret.

#### Location Intelligence API SDK Reference
Android SDK library for all Location Intelligence APIs can be found [here](http://locate.pitneybowes.com) under the 'SDK Downloads' Section. Detailed API documentation is also available under 'Documentation' section.

#### MapViewActivity
This activity is performing the GeoZone and Geocode API calls.

Call to GeoZone API
The GeoZone API call accept Time in Minutes as input and will return the Polygon Geometry that needs to be Overlayed on to the current MapView. The implementation of this can be found in MyGeoZoneAsyncTask Class in MapViewActivity that uses LocationIntelligenceJavaSDK-3.6.0.jar SDK library found in app/libs folder. 

private void callGeoZoneServiceApi(String travelTime) {
	try {
		mUtility.hideSoftKeyboard(MapViewActivity.this);
		if(!mUtility.isConnectedToNetwork()) {
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

Call to Geocode API
The Geocode API call will accept the address in batch and would return the location associated with the passed address. The implementation of this can be found in MyGeoCodeAsyncTask Class in MapViewActivity that uses LocationIntelligenceJavaSDK-3.6.0.jar SDK library found in app/libs folder.

protected void onPostExecute(String result) {
	if(geoZoneResponse !=null) {
		addPolygonOnMap(geoZoneResponse);

		new MyGeoCodeAsyncTask().execute();
	}
	else {
		mCustomProgressDialogUtility.dismissProgressDialog();
	}

}

MapUtility

The MapUtility class is used for loading the Raster Map tile provided by GeoMap API.

Call to GeoMap API

//Adding the Pitney Bowes GeoMap API as the Raster Map Tile Source and adding that TileSource to the current MapView.
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
