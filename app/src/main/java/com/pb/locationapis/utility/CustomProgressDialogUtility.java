package com.pb.locationapis.utility;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pb.locationapis.R;

/**
 * Created by NEX7IMH on 06-Jun-17.
 */
public class CustomProgressDialogUtility {

    private static CustomProgressDialogUtility _instance;

    private AlertDialog mAlertDialog;
    private AlertDialog.Builder builder;
    private ConstantUnits mConstantUnits;

    /**
     * Constructor is defined as PRIVATE, as following the Singleton Design Pattern.
     */
    private CustomProgressDialogUtility() {
        mConstantUnits = ConstantUnits.getInstance();
    }

    /**
     * To get the instance object of the class
     * @return _instance
     */
    public synchronized static CustomProgressDialogUtility getInstance() {
        try {
            if (_instance == null) {
                _instance = new CustomProgressDialogUtility();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return _instance;
    }

    /**
     * To show the custom progress dialog with the given title and its message
     * @param context
     * @param title
     * @param message
     */
    public void showCustomProgressDialog(Context context, String title, String message)
    {
        try {
            builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.loader, null);
            TextView mTextView = (TextView) dialogView.findViewById(R.id.text_view_message);
            if(!message.equalsIgnoreCase(mConstantUnits.EMPTY)) {
                mTextView.setText(message);
            }

            builder.setView(dialogView);

            mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mAlertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To update the progress message of showing dialog
     * @param title
     * @param message
     */
    public void updateProgressMessage(Context context, String title, String message)
    {
        try {
            if(null != mAlertDialog && mAlertDialog.isShowing())
            {
                mAlertDialog.cancel();
                showCustomProgressDialog(context, title, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To dismiss the showing dialog
     */
    public void dismissProgressDialog()
    {
        if (null != mAlertDialog && mAlertDialog.isShowing())
        {
            mAlertDialog.cancel();
        }
    }

}
