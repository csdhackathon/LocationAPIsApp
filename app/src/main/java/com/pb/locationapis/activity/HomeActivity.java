package com.pb.locationapis.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pb.locationapis.R;
import com.pb.locationapis.adapter.RecyclerViewCustomAdapter;
import com.pb.locationapis.asynctask.ParseResponseAsyncTask;
import com.pb.locationapis.listener.INotifyGPSLocationListener;
import com.pb.locationapis.listener.IParserEventListener;
import com.pb.locationapis.manager.AppManager;
import com.pb.locationapis.model.bo.routes.CustomerVo;
import com.pb.locationapis.model.bo.routes.RoutesBO;
import com.pb.locationapis.parser.ParserType;
import com.pb.locationapis.service.GpsLocationTracker;
import com.pb.locationapis.utility.Utility;
import com.pb.locationapis.utility.ValidationsUtility;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by NEX7IMH on 9/12/2017.
 */

public class HomeActivity extends Activity implements IParserEventListener,INotifyGPSLocationListener {

    private final String TAG = HomeActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerViewCustomAdapter mRecyclerViewCustomAdapter;
    private Button mButtonCreateMarketingContent;
    private AlertDialog mAlertDialog;
    private Utility mUtility;
    private ValidationsUtility mValidationsUtility;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_home);

            mUtility = Utility.getInstance();
            mValidationsUtility = ValidationsUtility.getInstance();
            GpsLocationTracker.initialize(HomeActivity.this, HomeActivity.this);

            JSONObject mJsonObject = new JSONObject(loadJSONFromAsset());
            new ParseResponseAsyncTask(mJsonObject, ParserType.PARSER_GET_ROUTE_FOR_AN_AGENT, HomeActivity.this).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    private void initializeLayoutViews() {
        try {
            mButtonCreateMarketingContent = (Button) findViewById(R.id.button_select_customers);
            mButtonCreateMarketingContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(HomeActivity.this, MapViewActivity.class));
                }
            });

            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_tasks_list);

            GridLayoutManager mGridLayoutManager = new GridLayoutManager(HomeActivity.this, 1);

            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mGridLayoutManager);


            List<CustomerVo> mCustomerVoList = null;

            if(AppManager.getInstance().getRoutesBO() != null) {
                mCustomerVoList = AppManager.getInstance().getRoutesBO().getCustomerVos();
            }
            else {
                mCustomerVoList = new ArrayList<>();
            }

            if(mCustomerVoList != null && mCustomerVoList.size() > 0) {
                Collections.sort(mCustomerVoList, new Comparator<CustomerVo>() {
                    public int compare(CustomerVo result1, CustomerVo result2) {
                        String routeOrderResult1 = result1.getCheckInCheckOutVo().getRouteOrder();
                        String routeOrderResult2 = result2.getCheckInCheckOutVo().getRouteOrder();
                        if (routeOrderResult1.length() == 1) {
                            routeOrderResult1 = "0" + routeOrderResult1;
                        }
                        if (routeOrderResult2.length() == 1) {
                            routeOrderResult2 = "0" + routeOrderResult2;
                        }
                        return routeOrderResult1.compareTo(routeOrderResult2);
                    }
                });
            }

            if(mCustomerVoList != null && mCustomerVoList.size() == 0) {
                ((TextView) findViewById(R.id.textView_no_data_available)).setVisibility(View.VISIBLE);
            }
            else {
                ((TextView) findViewById(R.id.textView_no_data_available)).setVisibility(View.GONE);
            }
            mRecyclerViewCustomAdapter = new RecyclerViewCustomAdapter(HomeActivity.this, mCustomerVoList);

            mRecyclerView.setAdapter(mRecyclerViewCustomAdapter);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = HomeActivity.this.getAssets().open("polyline.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onParseCompleted(ParserType parserType, RoutesBO routesBO) {
        try {
            switch (parserType) {

                case PARSER_GET_ROUTE_FOR_AN_AGENT:

                    AppManager.getInstance().setRoutesBO(routesBO);
                    initializeLayoutViews();
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveLocationDetails(Location mLocation) {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "osmdroid");
            if (file.exists()) {
                delete(file);
            }
        }catch(Exception ex){

        }
    }
    boolean delete(File file) {
        try {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null)
                    for (File f : files) delete(f);
            }
            return file.delete();
        }catch (Exception ex){

        }
        return  true;
    }
}
