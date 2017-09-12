package com.pb.locationapis.utility;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.pb.locationapis.activity.SplashScreenActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NEX7IMH on 20-Jun-17.
 */
public class PermissionsUtility
{
    private Activity mActivity;
    private static PermissionsUtility _instance;
    private final String TAG = PermissionsUtility.class.getName();
    public final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private boolean isAllPermissionGranted = false;

    /**
     * Constructor is defined as PRIVATE, as following the Singleton Design Pattern
     */
    private PermissionsUtility(Activity activity) {
        mActivity = activity;
    }

    /**
     * To get the instance object of the class
     * @return _instance
     */
    public static PermissionsUtility getInstance(Activity mActivity) {
        try {
            mActivity = mActivity;
            if (_instance == null) {
                _instance = new PermissionsUtility(mActivity);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return _instance;
    }

    /**
     * This method is used to check the permission for Android M and higher versions
     * @param permissions
     * @return
     */
    public boolean checkPermissions(String[] permissions)
    {
        boolean isPermissionGranted = true;
        try
        {
            List<String> permissionsList = new ArrayList<>();
            List<String> permissionsNeeded = new ArrayList<>();

            for (int i = 0; i < permissions.length; i++)
            {
                if(!addPermission(permissionsList, permissions[i]))
                {
                    permissionsNeeded.add(permissions[i]);
                }
            }

            if (permissionsList.size() > 0)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mActivity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                }
                isPermissionGranted = false;
            }
            else
            {
                isPermissionGranted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isPermissionGranted;
    }

    /**
     * This method is used to add the permission to the existing permissions list
     * @param permissionsList
     * @param permission
     * @return
     */
    private boolean addPermission(List<String> permissionsList, String permission)
    {
        if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED)
        {
            permissionsList.add(permission);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!mActivity.shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    /**
     * This method is called by the android system after the permissions check
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode) {

                case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                    try
                    {
                        isAllPermissionGranted = true;
                        if(mActivity.getClass().getSimpleName().equalsIgnoreCase(SplashScreenActivity.class.getSimpleName())) {
                            ((SplashScreenActivity) mActivity).isAllPermissionGranted = isAllPermissionGranted;
                        }
                        for (int i = 0; i < grantResults.length; i++)
                        {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                            {
                                isAllPermissionGranted = false;
                                if(mActivity.getClass().getSimpleName().equalsIgnoreCase(SplashScreenActivity.class.getSimpleName())) {
                                    ((SplashScreenActivity) mActivity).isAllPermissionGranted = isAllPermissionGranted;
                                }
                                break;
                            }
                        }

                        if (isAllPermissionGranted) {
                            // permission granted!
                            // you may now do the action that requires this permission
                            if(mActivity.getClass().getSimpleName().equalsIgnoreCase(SplashScreenActivity.class.getSimpleName())) {
                                ((SplashScreenActivity) mActivity).initializeActivityProcess();
                            }
                        }
                        else {
                            Log.i(TAG, "Permission Denied");
                            DialogUtility.getInstance(mActivity).showCustomAlertDialog(mActivity, DialogUtility.DialogType.APP_PERMISSION_DIALOG);
                        }
                    }
                    catch (Exception e)	{
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
