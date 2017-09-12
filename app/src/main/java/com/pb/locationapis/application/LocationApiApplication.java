package com.pb.locationapis.application;

import android.app.Application;
import android.content.Context;

import com.pb.locationapis.activity.HomeActivity;
import com.pb.locationapis.preference.SharedPreferenceHelper;

/**
 * Created by NEX7IMH on 6/20/2017.
 */

public class LocationApiApplication extends Application {

    private static Context mContext;
    private SharedPreferenceHelper mSharedPreferenceHelper;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        LocationApiApplication.mContext = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            this.mContext = getApplicationContext();

            mSharedPreferenceHelper = SharedPreferenceHelper.getInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {

        //mSharedPreferenceHelper.setBooleanPreference(mContext, mSharedPreferenceHelper.IS_KEYS_SUBMITTED, false);
        super.onTerminate();

    }

}
