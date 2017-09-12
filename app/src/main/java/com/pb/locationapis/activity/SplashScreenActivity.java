package com.pb.locationapis.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.pb.locationapis.R;
import com.pb.locationapis.preference.SharedPreferenceHelper;
import com.pb.locationapis.service.GpsLocationTracker;
import com.pb.locationapis.utility.DialogUtility;
import com.pb.locationapis.utility.PermissionsUtility;
import com.pb.locationapis.utility.Utility;

public class SplashScreenActivity extends Activity {

    private final String TAG = SplashScreenActivity.class.getSimpleName();
    private boolean isLoadingActivity = false;
    protected SharedPreferenceHelper mSharedPreferenceHelper;
    protected Utility mUtility;
    public boolean isAllPermissionGranted = false;

    private String[] mPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);

            mSharedPreferenceHelper = SharedPreferenceHelper.getInstance();
            mUtility = Utility.getInstance(SplashScreenActivity.this);

            Utility.getInstance(this).setFontRegular(((TextView) findViewById(R.id.textView_welcome_to)));
            Utility.getInstance(this).setFontRegular(((TextView) findViewById(R.id.textView_ffo)));
            Utility.getInstance(this).setFontRegular(((TextView) findViewById(R.id.textView_tm)));

            /*if(!mSharedPreferenceHelper.getBooleanPreference(SplashScreenActivity.this, mSharedPreferenceHelper.IS_LOGIN))
            {
                loadActivity();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            // Check if all permissions are granted or not.
            if (PermissionsUtility.getInstance(this).checkPermissions(mPermissions))
            {
                Log.i(TAG, "@onStart Permissions granted");
                if(mUtility.isConnectedToNetwork() && GpsLocationTracker.isGpEnabled(SplashScreenActivity.this)) {

                    GpsLocationTracker.initialize(SplashScreenActivity.this, null);
                    GpsLocationTracker.getInstance().connect();
                }
                initializeActivityProcess();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if(isAllPermissionGranted && mUtility.isConnectedToNetwork()
                    && GpsLocationTracker.getInstance() != null) {
                GpsLocationTracker.getInstance().disConnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to start the Activity process
     */
    public void initializeActivityProcess() {
        try {
            if(GpsLocationTracker.isGpEnabled(SplashScreenActivity.this)) {
                loadActivity();
            }
            else {
                GpsLocationTracker.initialize(SplashScreenActivity.this, null);
                GpsLocationTracker.getInstance().connect();
            }
            /*if(mUtility.isConnectedToNetwork())  {

            }
            else {
                DialogUtility.getInstance(this).showCustomAlertDialog(this, DialogUtility.DialogType.NETWORK_ALERT_DIALOG);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to load the activity
     */
    private void loadActivity() {
        try  {
            if(isLoadingActivity) {
                return;
            }
            else {
                isLoadingActivity = true;
            }
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            Intent intent = null;
                            intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            SplashScreenActivity.this.finish();
                        }
                    }, 800);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtility.getInstance(SplashScreenActivity.this).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (isAllPermissionGranted && GpsLocationTracker.getInstance() != null) {
                GpsLocationTracker.getInstance().onActivityResult(requestCode, resultCode, data);
            }
            if (requestCode == GpsLocationTracker.getInstance().PLAY_SERVICES_RESOLUTION_REQUEST) {
                if (GpsLocationTracker.isGpEnabled(SplashScreenActivity.this)) {
                    loadActivity();
                }
                else {
                    onBackPressed();
                }
            }
            else if (requestCode == DialogUtility.REQUEST_PERMISSION_SETTING) {
                //onStart();
            }
            else {
                SplashScreenActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
