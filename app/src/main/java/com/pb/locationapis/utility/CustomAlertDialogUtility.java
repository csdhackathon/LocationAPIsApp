package com.pb.locationapis.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;

/**
 * Created by NEX7IMH on 06-Jun-17.
 */
public class CustomAlertDialogUtility {

    private static CustomAlertDialogUtility _instance;

    private AlertDialog mAlertDialog;

    /**
     * Constructor is defined as PRIVATE, as following the Singleton Design Pattern.
     */
    private CustomAlertDialogUtility() {
    }

    /**
     * To get the instance object of the class
     * @return _instance
     */
    public synchronized static CustomAlertDialogUtility getInstance() {
        try {
            if (_instance == null) {
                _instance = new CustomAlertDialogUtility();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return _instance;
    }

    /**
     * To show the custom alert dialog with the given title and its message.
     * @param context
     * @param title
     * @param message
     */
    public void showCustomAlertDialog(Context context, String title, String message)
    {
        try {
            if(mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.cancel();
            }
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.support.v7.appcompat.R.style.AlertDialog_AppCompat));
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

                }
            });

            mAlertDialog = mBuilder.create();
            mAlertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
