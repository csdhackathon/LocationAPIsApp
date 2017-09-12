package com.pb.locationapis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pb.locationapis.R;
import com.pb.locationapis.adapter.RecyclerViewCustomAdapter;
import com.pb.locationapis.asynctask.ParseResponseAsyncTask;
import com.pb.locationapis.listener.IParserEventListener;
import com.pb.locationapis.model.bo.routes.CustomerVo;
import com.pb.locationapis.model.bo.routes.RoutesBO;
import com.pb.locationapis.parser.ParserType;
import com.pb.locationapis.preference.SharedPreferenceHelper;
import com.pb.locationapis.utility.Utility;
import com.pb.locationapis.utility.ValidationsUtility;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by NEX7IMH on 9/12/2017.
 */

public class HomeActivity extends Activity implements IParserEventListener {

    private final String TAG = HomeActivity.class.getSimpleName();
    private static RoutesBO mRoutesBO;

    private RecyclerView mRecyclerView;
    private RecyclerViewCustomAdapter mRecyclerViewCustomAdapter;
    private Button mButtonCreateMarketingContent;
    private ImageView mImageViewConfigApis;
    private AlertDialog mAlertDialog;
    private Utility mUtility;
    private ValidationsUtility mValidationsUtility;

    private boolean isAPIsKeySubmitted = false;
    private SharedPreferenceHelper mSharedPreferenceHelper;

    public static void setmRoutesBO(RoutesBO mRoutesBO) {
        HomeActivity.mRoutesBO = mRoutesBO;
    }

    public static RoutesBO getmRoutesBO() {
        return HomeActivity.mRoutesBO;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_home);

            mUtility = Utility.getInstance(this);
            mSharedPreferenceHelper = SharedPreferenceHelper.getInstance();
            mValidationsUtility = ValidationsUtility.getInstance(this);

            if(!isAPIsKeySubmitted) {
                showCustomConfigApiSecretKeyDialog();
            }

            JSONObject mJsonObject = new JSONObject(loadJSONFromAsset());
            new ParseResponseAsyncTask(mJsonObject, ParserType.PARSER_GET_ROUTE_FOR_AN_AGENT, HomeActivity.this).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeLayoutViews() {
        try {
            mImageViewConfigApis = (ImageView) findViewById(R.id.imageView_config_apis);
            mImageViewConfigApis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showCustomConfigApiSecretKeyDialog();
                }
            });


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

            if(getmRoutesBO() != null) {
                mCustomerVoList = getmRoutesBO().getCustomerVos();
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

                    setmRoutesBO(routesBO);
                    initializeLayoutViews();
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCustomConfigApiSecretKeyDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_config_apis_key_dialog, null);
            builder.setView(dialogView);
            builder.setCancelable(false);

            final EditText inputApiKey = (EditText) dialogView.findViewById(R.id.editText_api_key);
            final EditText inputSecretKey = (EditText) dialogView.findViewById(R.id.editText_secret_key);

            mUtility.setFontRegular(inputApiKey);
            mUtility.setFontRegular(inputSecretKey);

            inputApiKey.setText(mSharedPreferenceHelper.getStringPreference(HomeActivity.this, mSharedPreferenceHelper.API_KEY));
            inputSecretKey.setText(mSharedPreferenceHelper.getStringPreference(HomeActivity.this, mSharedPreferenceHelper.SECRET_KEY));

            RelativeLayout mRelativeLaySubmit = (RelativeLayout) dialogView.findViewById(R.id.relative_lay_submit_button);
            mRelativeLaySubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(inputApiKey.getText().toString().equalsIgnoreCase("")) {
                        mValidationsUtility.setValidationError(inputApiKey, getString(R.string.enter_api_key));
                    }
                    else if(inputSecretKey.getText().toString().equalsIgnoreCase("")) {
                        mValidationsUtility.setValidationError(inputSecretKey, getString(R.string.enter_secret_key));
                    }
                    else {
                        isAPIsKeySubmitted = true;
                        mSharedPreferenceHelper.setStringPreference(HomeActivity.this, mSharedPreferenceHelper.API_KEY, inputApiKey.getText().toString());
                        mSharedPreferenceHelper.setStringPreference(HomeActivity.this, mSharedPreferenceHelper.SECRET_KEY, inputSecretKey.getText().toString());

                        mAlertDialog.cancel();
                    }
                }
            });

            if(mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.cancel();
            }
            mAlertDialog = builder.create();
            mAlertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
