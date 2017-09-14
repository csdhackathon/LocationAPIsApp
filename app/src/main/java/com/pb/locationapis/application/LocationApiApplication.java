package com.pb.locationapis.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by NEX7IMH on 6/20/2017.
 */

public class LocationApiApplication extends Application {

    private static Context mContext;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

}
