package com.pb.locationapis.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.pb.locationapis.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by NEX7IMH on 14-Jun-17.
 */
public class Utility {
    private static Utility _instance;
    private Context mContext;
    private ConstantUnits mConstantUnits;
    private static final String TAG = Utility.class.getName();

    /**
     * Constructor is defined as PRIVATE, as following the Singleton Design Pattern
     */
    private Utility(Context context) {
        this.mContext = context;
        this.mConstantUnits = ConstantUnits.getInstance();
    }

    /**
     * To get the instance object of the class
     *
     * @return _instance
     */
    public synchronized static Utility getInstance(Context context) {

        if (_instance == null) {
            _instance = new Utility(context);
        }
        return _instance;
    }

    /**
     * This method is use to check the device internet connectivity.
     *
     * @return true :if your device is connected to internet. false :if your
     * device is not connected to internet.
     */
    public boolean isConnectedToNetwork() {

        try {
            ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info == null)
                return false;
            if (info.getState() != NetworkInfo.State.CONNECTED)
                return false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * This method is used to set the font style to TextView font to Bold style.
     *
     * @param textView
     */
    public void setFontBold(TextView textView) {
        Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/PrecisionSans_W_Bd.ttf");
        textView.setTypeface(typeFace);
    }

    /**
     * This method is used to set the font style to TextInputLayout font to Bold style.
     *
     * @param textInputLayout
     */
    public void setFontBold(TextInputLayout textInputLayout) {
        Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/PrecisionSans_W_Bd.ttf");
        textInputLayout.setTypeface(typeFace);
    }

    /**
     * This method is used to set the font style to TextView font to Regular style.
     *
     * @param textView
     */
    public void setFontRegular(TextView textView) {
        Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/PrecisionSans_W_Rg.ttf");
        textView.setTypeface(typeFace);
    }

    /**
     * This method is used to set the font style to TextInputLayout font to Regular style.
     *
     * @param textInputLayout
     */
    public void setFontRegular(TextInputLayout textInputLayout) {
        Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/PrecisionSans_W_Rg.ttf");
        textInputLayout.setTypeface(typeFace);
    }

    /**
     * This method is used to get the device id or Secure ANDROID_ID if device id is null.
     *
     * @param context
     * @return - device id
     */
    public static String getDeviceId(Context context) {

        String deviceId = null;
        try {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null)
                    deviceId = telephonyManager.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (deviceId == null) {
                    deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (deviceId == null) {
                UUID uuid = UUID.randomUUID();
                deviceId = String.valueOf(uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceId;
    }


    /**
     * This method is used to hide the input soft keyboard is appearing
     *
     * @param mActivity
     */
    public void hideSoftKeyboard(Activity mActivity) {
        InputMethodManager im = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
            ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(
                    mContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            value = bundle.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * This method is used to get the current date in dd-MM-yyyy format
     *
     * @return currentDate
     */
    public String getCurrentDate() {
        String currentDate = mConstantUnits.EMPTY;
        try {
            //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            currentDate = sdf.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentDate;
    }


    /**
     * This method is used to get the current date in dd-MMM-yyyy format
     *
     * @return currentDate
     */
    public String getCurrentDateInNormalForm() {
        String currentDate = mConstantUnits.EMPTY;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            currentDate = sdf.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentDate;
    }

    /**
     * This method is used to get time from current date in MMM dd, yyyy hh:mm:ss a format
     *
     * @return currentDate
     */
    public String getTimeFromNormalFormDate(String inputDate) {
        String currentDate = mConstantUnits.EMPTY;
        try {
            SimpleDateFormat sdfInputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.US);
            SimpleDateFormat sdfOutputFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            currentDate = sdfOutputFormat.format(sdfInputFormat.parse(inputDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentDate;
    }

    /**
     * This method is used to get the error response message
     *
     * @param mResponseJsonObject
     * @return
     */
    public String getErrorResponseMessage(JSONObject mResponseJsonObject) {
        String message = ConstantUnits.getInstance().EMPTY;
        try {
            if (mResponseJsonObject != null) {
                if(mResponseJsonObject.has(mConstantUnits.errorMessage)) {
                    message = mResponseJsonObject.getString(mConstantUnits.errorMessage);
                }
                else if(mResponseJsonObject.has(mConstantUnits.error)) {
                    message = mResponseJsonObject.getString(mConstantUnits.error);
                }
                else if (mResponseJsonObject.has(ConstantUnits.getInstance().RESPONSE_MESSAGE)) {
                    message = mResponseJsonObject.getString(ConstantUnits.getInstance().RESPONSE_MESSAGE);
                }
                else {
                    message = mContext.getResources().getString(R.string.please_try_again_later);
                }
            }
            else {
                message = mContext.getResources().getString(R.string.please_try_again_later);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = mContext.getResources().getString(R.string.please_try_again_later);
        }
        return message;
    }

    /**
     * This method is used to know weather the given time in seconds has elapsed from the last saved location time
     *
     * @param lastLocationSavedDateTime
     * @param seconds
     * @return boolean
     */
    public boolean isLocationUpdateRequired(String lastLocationSavedDateTime, int seconds) {
        try {
            Date convertedSavedDateTimeDate = getConvertedSavedDateTimeDate(lastLocationSavedDateTime);
            if (convertedSavedDateTimeDate != null) {
                Calendar mCalendar = Calendar.getInstance();
                if ((mCalendar.getTimeInMillis() - convertedSavedDateTimeDate.getTime()) < (seconds * 1000)) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This method is used to get the Date object from the last saved location date time.
     *
     * @param lastLocationSavedDateTime
     * @return
     */
    private Date getConvertedSavedDateTimeDate(String lastLocationSavedDateTime) {
        Date convertedDate = null;
        try {
            if (!lastLocationSavedDateTime.isEmpty()) {
                Long longValue = Long.valueOf(lastLocationSavedDateTime);
                convertedDate = new Date(longValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    /**
     * This method is used to get the date time for next location update
     *
     * @return
     */
    public String getDateTimeForNextLocationUpdate(int seconds) {
        String currentDateTime = mConstantUnits.EMPTY;
        try {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.add(Calendar.SECOND, 5);
            currentDateTime = String.valueOf(mCalendar.getTime().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentDateTime;
    }


    /**
     * This method is used to scale the given Drawable image
     *
     * @param mActivity
     * @param image
     * @param scaleFactor
     * @return
     */
    public Drawable scaleImage(Activity mActivity, Drawable image, float scaleFactor) {
        try {
            if ((image == null) || !(image instanceof BitmapDrawable)) {
                return image;
            }
            Bitmap b = ((BitmapDrawable) image).getBitmap();
            int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
            int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);
            image = new BitmapDrawable(mActivity.getResources(), bitmapResized);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


}