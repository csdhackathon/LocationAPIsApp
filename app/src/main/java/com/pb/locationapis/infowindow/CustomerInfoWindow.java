package com.pb.locationapis.infowindow;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pb.locationapis.R;
import com.pb.locationapis.model.bo.routes.CustomerVo;
import com.pb.locationapis.utility.Utility;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

/**
 * Created by NEX7IMH on 7/6/2017.
 */

public class CustomerInfoWindow extends InfoWindow {

    private CustomerVo mCustomerVo;
    private TextView mTextViewName, mTextViewAddressLine1, mTextViewAddressLine2;
    private ImageView mImageViewInfo;
    private LinearLayout mLinearLayoutNameAddress;

    /**
     * @param layoutResId the id of the view resource.
     * @param mapView     the mapview on which is hooked the view
     */
    public CustomerInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    public void setCustomerListItem(CustomerVo customerVo) {
        this.mCustomerVo = customerVo;
    }

    @Override
    public void onOpen(Object item) {
        try {
            mTextViewName = (TextView) mView.findViewById(R.id.textView_name);
            mTextViewAddressLine1 = (TextView) mView.findViewById(R.id.textView_addressLine1);
            mTextViewAddressLine2 = (TextView) mView.findViewById(R.id.textView_addressLine2);

            mLinearLayoutNameAddress = (LinearLayout) mView.findViewById(R.id.linear_lay_name_address);
            mLinearLayoutNameAddress.bringToFront();

            Utility.getInstance(getMapView().getContext()).setFontRegular(mTextViewName);
            Utility.getInstance(getMapView().getContext()).setFontRegular(mTextViewAddressLine1);

            if(mCustomerVo == null) {
                mCustomerVo.setName("MarkerName");
                mCustomerVo.setAddressLine1("MarkerAddress");
            }
            mTextViewName.setText(mCustomerVo.getName());
            mTextViewAddressLine1.setText(mCustomerVo.getAddressLine1());
            mTextViewAddressLine2.setText(mCustomerVo.getAddressLine2());
            if(mCustomerVo.getAddressLine2().isEmpty()) {
                mTextViewAddressLine2.setVisibility(View.GONE);
            }

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
