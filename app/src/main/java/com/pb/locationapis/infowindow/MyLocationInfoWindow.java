package com.pb.locationapis.infowindow;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pb.locationapis.R;
import com.pb.locationapis.utility.Utility;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

/**
 * Created by NEX7IMH on 7/6/2017.
 */

public class MyLocationInfoWindow extends InfoWindow {

    private TextView mTextViewName, mTextViewAddressLine1, mTextViewAddressLine2;
    private LinearLayout mLinearLayoutNameAddress;

    /**
     * @param layoutResId the id of the view resource.
     * @param mapView     the mapview on which is hooked the view
     */
    public MyLocationInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    @Override
    public void onOpen(Object item) {
        try {
            mTextViewName = (TextView) mView.findViewById(R.id.textView_name);
            mTextViewAddressLine1 = (TextView) mView.findViewById(R.id.textView_addressLine1);
            mTextViewAddressLine2 = (TextView) mView.findViewById(R.id.textView_addressLine2);

            mLinearLayoutNameAddress = (LinearLayout) mView.findViewById(R.id.linear_lay_name_address);
            mLinearLayoutNameAddress.bringToFront();


            mTextViewName.setText("My Location"+ "   ");
            //mTextViewAddressLine1.setText(getMapView().getContext().getString(R.string.i_am_here)+ "   ");
            mTextViewAddressLine1.setVisibility(View.GONE);
            mTextViewAddressLine2.setVisibility(View.GONE);

            mTextViewAddressLine1.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT; // For change `TextView` width to `WRAP_CONTENT`

            ((RelativeLayout) mView.findViewById(R.id.relative_lay_info_button)).setVisibility(View.GONE);

            mLinearLayoutNameAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        closeAllInfoWindowsOn(getMapView());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose() {

    }
}
