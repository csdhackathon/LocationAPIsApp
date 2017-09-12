package com.pb.locationapis.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.pb.locationapis.R;
import com.pb.locationapis.activity.SplashScreenActivity;
import com.pb.locationapis.preference.SharedPreferenceHelper;
import com.pb.locationapis.service.GpsLocationTracker;

/**
 * Created by NEX7IMH on 22-June-2017.
 */
public class DialogUtility {

    private static DialogUtility _instance;
    public static final int REQUEST_PERMISSION_SETTING = 999;
    private AlertDialog mAlertDialog;
    private Utility mUtility;

    /**
     * Constructor is defined as PRIVATE, as following the Singleton Design Pattern
     */
    private DialogUtility(Context context) {
        mUtility = Utility.getInstance(context);
    }

    /**
     * To get the instance object of the class
     * @return _instance
     */
    public static DialogUtility getInstance(Context context) {
        try {
            if (_instance == null) {
                _instance = new DialogUtility(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _instance;
    }

    /**
     * Declaring the constant enum Dialog type
     */
    public enum DialogType {
        NETWORK_ALERT_DIALOG,
        APP_PERMISSION_DIALOG,
        LOGOUT
    }

    /**
     * @param mCtx
     * @param dialogType
     */
    public void showCustomAlertDialog(final Context mCtx, DialogType dialogType) {
        try {
            //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mCtx, android.R.style.Theme_Holo_Light_DarkActionBar));
            AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
            builder.setCancelable(false);

            switch (dialogType) {

                case APP_PERMISSION_DIALOG:

                    builder.setTitle(mCtx.getResources().getString(R.string.allow_permissions));
                    builder.setPositiveButton(mCtx.getResources().getString(R.string.open_settings), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                dialog.cancel();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mCtx.getPackageName(), null);
                                intent.setData(uri);
                                ((Activity) mCtx).startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setMessage(mCtx.getResources().getString(R.string.accept_permissions_go_to_settings));

                    break;

                case NETWORK_ALERT_DIALOG:

                    builder.setTitle(mCtx.getResources().getString(R.string.network_error));
                    builder.setMessage(mCtx.getResources().getString(R.string.pleae_check_your_internet));

                    builder.setPositiveButton(mCtx.getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            if (mUtility.isConnectedToNetwork()) {
                                try {
                                    dialog.cancel();
                                    if(GpsLocationTracker.isGpEnabled(mCtx)) {
                                        ((SplashScreenActivity) mCtx).initializeActivityProcess();
                                    }
                                    else {
                                        GpsLocationTracker.initialize((mCtx), null);
                                        GpsLocationTracker.getInstance().connect();
                                        GpsLocationTracker.getInstance().startLocationUpdates();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                dialog.cancel();
                                showCustomAlertDialog(mCtx, DialogType.NETWORK_ALERT_DIALOG);
                            }
                        }
                    });

                    builder.setNegativeButton(mCtx.getResources().getString(R.string.exit_the_app), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                // exit the app and go to the HOME
                                dialog.cancel();
                                ((Activity) mCtx).finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    break;


                default:
                    break;
            }
            // Check if dialog is already open or not.
            if (null != mAlertDialog && mAlertDialog.isShowing())
                mAlertDialog.cancel();

            // Initialize AlertDialog.
            mAlertDialog = builder.create();
            mAlertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
