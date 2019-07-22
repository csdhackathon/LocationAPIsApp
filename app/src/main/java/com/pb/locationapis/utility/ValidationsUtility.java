package com.pb.locationapis.utility;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.pb.locationapis.R;

/**
 * Created by NEX7IMH on 14-Jun-17.
 */
public class ValidationsUtility {

    private static  ValidationsUtility _instance;

    /**
     * Constructor is defined as PRIVATE, as following the Singleton Design Pattern
     */
    private ValidationsUtility() {}

    /**
     * To get the instance object of the class
     * @return _instance
     */
    public static ValidationsUtility getInstance() {

        try
        {
            if (_instance == null) {
                _instance = new ValidationsUtility();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _instance;
    }

    /**
     * This method is used to set the given errorText message to the given EditText field
     * @param mEditText
     * @param errorText
     */
    public void setValidationError(Context context, EditText mEditText, String errorText)
    {
        try {
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            mEditText.startAnimation(shake);
            mEditText.requestFocus();
            mEditText.setError(errorText);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to set the given errorText message to the given EditText field
     * @param mTextView
     * @param errorText
     */
    public void setValidationError(Context context, TextView mTextView, String errorText)
    {
        try {
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            mTextView.startAnimation(shake);
            mTextView.requestFocus();
            mTextView.setError(errorText);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
