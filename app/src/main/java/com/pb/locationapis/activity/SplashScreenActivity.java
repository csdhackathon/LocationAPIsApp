package com.pb.locationapis.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.pb.locationapis.R;
import com.pb.locationapis.service.GpsLocationTracker;
import com.pb.locationapis.utility.ConstantUnits;
import com.pb.locationapis.utility.DialogUtility;
import com.pb.locationapis.utility.PermissionsUtility;
import com.pb.locationapis.utility.Utility;

/**
 * This activity takes care of all the Permission related to Application.
 * The important permission for the application to run are:
 * WRITE_EXTERNAL_STORAGE
 * ACCESS_FINE_LOCATION
 * ACCESS_COARSE_LOCATION
 */
public class SplashScreenActivity extends Activity {

    private final String TAG = SplashScreenActivity.class.getSimpleName();
    private boolean isLoadingActivity = false;
    protected Utility mUtility;
    private ConstantUnits mConstantUnits;
    public boolean isAllPermissionGranted = false;
    private AlertDialog mAlertDialog;

    private String[] mPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);
            mUtility = Utility.getInstance();
            mConstantUnits = ConstantUnits.getInstance();
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
                if(mUtility.isConnectedToNetwork(SplashScreenActivity.this) && GpsLocationTracker.isGpEnabled(SplashScreenActivity.this)) {

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
            if(isAllPermissionGranted && mUtility.isConnectedToNetwork(SplashScreenActivity.this)
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
            /*if(mUtility.isConnectedToNetwork(SplashScreenActivity.this))  {

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
                            if(!mUtility.getMetaDataFromManifest(mConstantUnits.PBGEOMAP_ACCESS_TOKEN, SplashScreenActivity.this).equalsIgnoreCase(mConstantUnits.EMPTY)
                                    && !mUtility.getMetaDataFromManifest(mConstantUnits.PBGEOMAP_SECRET_KEY, SplashScreenActivity.this).equalsIgnoreCase(mConstantUnits.EMPTY)) {
                                Intent intent = null;
                                intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                SplashScreenActivity.this.finish();
                            }
                            else {
                                showCustomAlertDialog(SplashScreenActivity.this, getString(R.string.key_error), getString(R.string.api_key_not_found));
                            }

                        }
                    }, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCustomAlertDialog(Context context, String title, String message)
    {
        try {
            if(mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.cancel();
            }
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyAlertDialogStyle));
            mBuilder.setCancelable(false);
            if(!title.equalsIgnoreCase(ConstantUnits.getInstance().EMPTY)) {
                mBuilder.setTitle(title);
            }
            if(!message.equalsIgnoreCase(ConstantUnits.getInstance().EMPTY)) {
                mBuilder.setMessage(message);
            }
            String positiveText = context.getString(android.R.string.ok);
            mBuilder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // positive button logic
                    mAlertDialog.cancel();
                    SplashScreenActivity.this.finish();
                }
            });

            mAlertDialog = mBuilder.create();
            mAlertDialog.show();

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
