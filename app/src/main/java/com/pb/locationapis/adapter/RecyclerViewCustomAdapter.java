package com.pb.locationapis.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.pb.locationapis.R;
import com.pb.locationapis.model.bo.routes.CustomerVo;

import java.util.List;

/**
 * Created by NEX7IMH on 6/21/2017.
 */

public class RecyclerViewCustomAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private Context mContext;

    private List<CustomerVo> itemCustomerVoList;

    public RecyclerViewCustomAdapter(Context context, List<CustomerVo> itemList) {
        this.itemCustomerVoList = itemList;
        this.mContext = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = null;
        RecyclerViewHolder rcvHolder = null;
        try {
            if(parent != null) {

                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tasks_list_list_item, null);
                rcvHolder = new RecyclerViewHolder(mContext, layoutView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rcvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        try {
            initializeTaskListViewComponents(holder, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initializeTaskListViewComponents(RecyclerViewHolder holder, final int position) {
        try {
            if(itemCustomerVoList != null && itemCustomerVoList.size() > 0) {

                holder.mTextViewName.setText(itemCustomerVoList.get(position).getName());
                holder.mTextViewAddress1.setText(itemCustomerVoList.get(position).getAddressLine1());

                holder.mImageViewRouteOrder.setImageResource(R.drawable.circle_background_black);
                //holder.mImageViewRouteOrder.setImageResource(R.drawable.circle_background_magenta);
                //holder.mImageViewRouteOrder.setImageResource(R.drawable.circle_background_green);

                holder.mTextViewRouteOrder.setText(itemCustomerVoList.get(position).getCheckInCheckOutVo().getRouteOrder());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        try {
            if(itemCustomerVoList != null) {
                return itemCustomerVoList.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}