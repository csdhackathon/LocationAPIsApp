package com.pb.locationapis.manager;

import android.util.Log;

import com.pb.locationapis.model.bo.routes.RoutesBO;

public class AppManager {
    private static AppManager _instance;
    private AppManager() {
    }

    public static AppManager getInstance() {
        try {
            if(_instance == null) {
                synchronized (AppManager.class) {
                    if (_instance == null) {
                        _instance = new AppManager();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(AppManager.class.getSimpleName(), "getInstance Exception:", e);
        }
        return _instance;
    }

    private RoutesBO routesBO;

    public RoutesBO getRoutesBO() {
        return routesBO;
    }

    public void setRoutesBO(RoutesBO routesBO) {
        this.routesBO = routesBO;
    }
}
