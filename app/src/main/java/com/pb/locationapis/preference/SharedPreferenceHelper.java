package com.pb.locationapis.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pb.locationapis.utility.ConstantUnits;

/**
 * Created by NEX4LDF on 12/22/2016.
 */
public class SharedPreferenceHelper
{
    private static SharedPreferenceHelper _instance = null;

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    private final String TAG = SharedPreferenceHelper.class.getName();

    // Context
    private Context mContext;

    //Shared Preference Name
    private final String SHARED_PREF_NAME = "sf_pref_ffo";

    /**
     * Constructor is defined as PRIVATE, as following the Singleton Design Pattern
     */
    private SharedPreferenceHelper() { }

    /**
     * To get the instance object of the class
     * @return _instance
     */
    public synchronized static SharedPreferenceHelper getInstance() {
        try {
            if (_instance == null) {
                synchronized (SharedPreferenceHelper.class) {
                    if (_instance == null) {
                        _instance = new SharedPreferenceHelper();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return _instance;
    }

    /**
     * Preference Keys
     */
    public final String BASE_URL = "base_url";

    public final String API_KEY = "api_key";
    public final String SECRET_KEY = "secret_key";

    public final String LAST_LOCATION_SAVED_TIME = "last_location_saved_time";


    /**
     * This method is used to save the Application Context
     * @param context
     */
    private void initializePreference(Context context) {
        try {
            this.mContext = context;
            int PRIVATE_MODE = 0;
            pref = mContext.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to get the Context
     * @return Context
     */
    public Context getContext() {
        try {
            return mContext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method is used to set String value on provided Key
     * @param key
     * @param value
     */
    public void setStringPreference(Context context, String key, String value) {
        try {
            if(editor == null) {
                initializePreference(context);
            }
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "@setPreference() key:" + key + " Exception::" + e.getMessage());
        }
    }

    /**
     * This method is used to get String value for provided Key
     * @param context
     * @param key
     * @return value
     */
    public String getStringPreference(Context context, String key) {
        String value = ConstantUnits.getInstance().EMPTY;
        try {
            if(pref == null) {
                initializePreference(mContext);
            }
            value = pref.getString(key, ConstantUnits.getInstance().EMPTY);
        } catch (Exception e) {
            Log.e(TAG, "@getPreference() key:" + key + " Exception::" + e.getMessage());
        }
        return value;
    }


    /**
     * This method is used to set Boolean value on provided Key
     * @param context
     * @param key
     * @param value
     */
    public void setBooleanPreference(Context context, String key, boolean value) {
        try {
            if(editor == null) {
                initializePreference(context);
            }
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "@setPreference() key:" + key + " Exception::" + e.getMessage());
        }
    }

    /**
     * This method is used to get Boolean value for provided Key
     * @param context
     * @param key
     * @return value
     */
    public boolean getBooleanPreference(Context context, String key) {
        boolean value = false;
        try {
            if(pref == null) {
                initializePreference(context);
            }
            value = pref.getBoolean(key, false);
        } catch (Exception e) {
            Log.e(TAG, "@getPreference() key:" + key + " Exception::" + e.getMessage());
        }
        return value;
    }

    /**
     * This method is used to set Integer value on provided Key
     * @param context
     * @param key
     * @param value
     */
    public void setIntegerPreference(Context context, String key, int value) {
        try {
            if(editor == null) {
                initializePreference(context);
            }
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "@setPreference() key:" + key + " Exception::" + e.getMessage());
        }
    }

    /**
     * This method is used to get Integer value for provided Key
     * @param context
     * @param key
     * @return value
     */
    public int getIntegerPreference(Context context, String key) {
        int value = 0;
        try {
            if(pref == null) {
                initializePreference(context);
            }
            value = pref.getInt(key, 0);
        } catch (Exception e) {
            Log.e(TAG, "@getPreference() key:" + key + " Exception::" + e.getMessage());
        }
        return value;
    }

    /**
     * This method is used to set Double value on provided Key
     * @param context
     * @param key
     * @param value
     */
    public void setLongPreference(Context context, String key, long value) {
        try {
            if(editor == null) {
                initializePreference(context);
            }
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "@setPreference() key:" + key + " Exception::" + e.getMessage());
        }
    }

    /**
     * This method is used to get Double value for provided Key
     * @param context
     * @param key
     * @return value
     */
    public double getLongPreference(Context context, String key) {
        double value = 0.0;
        try {
            if(pref == null) {
                initializePreference(context);
            }
            value = pref.getLong(key, 0);
        } catch (Exception e) {
            Log.e(TAG, "@getPreference() key:" + key + " Exception::" + e.getMessage());
        }
        return value;
    }

    /**
     *
     */
    public void clearSharedPreferences(Context context)
    {
        try {
            if(editor == null) {
                initializePreference(context);
            }
            editor.clear().commit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}