package com.pb.locationapis.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pb.locationapis.R;
import com.pb.locationapis.utility.Utility;


/**
 * Created by NEX7IMH on 6/21/2017.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    protected TextView mTextViewName, mTextViewRouteOrder,  mTextViewAddress1;
    protected ImageView mImageViewRouteOrder;
    protected LinearLayout mLinearLayoutTaskItem;;

    public RecyclerViewHolder(Context context, View itemView) {
        super(itemView);
        try {
            mTextViewName = (TextView) itemView.findViewById(R.id.textView_name);
            mTextViewRouteOrder = (TextView) itemView.findViewById(R.id.textView_route_order);
            mTextViewAddress1 = (TextView) itemView.findViewById(R.id.textView_address_value);

            mImageViewRouteOrder = (ImageView) itemView.findViewById(R.id.imageView_route_order);
            mLinearLayoutTaskItem = (LinearLayout) itemView.findViewById(R.id.linear_layout_task_item);

            Utility.getInstance(context).setFontRegular(mTextViewName);
            Utility.getInstance(context).setFontRegular(mTextViewRouteOrder);
            Utility.getInstance(context).setFontRegular(mTextViewAddress1);
            Utility.getInstance(context).setFontRegular((TextView) itemView.findViewById(R.id.textView_address_level));

            Utility.getInstance(context).setFontRegular((TextView) itemView.findViewById(R.id.textView_address_colon));
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
}
