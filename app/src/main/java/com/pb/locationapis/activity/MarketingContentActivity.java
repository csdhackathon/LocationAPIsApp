package com.pb.locationapis.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pb.locationapis.R;
import com.pb.locationapis.model.bo.routes.CustomerVo;
import com.pb.locationapis.utility.ConstantUnits;
import com.pb.locationapis.utility.CustomAlertDialogUtility;
import com.pb.locationapis.utility.CustomProgressDialogUtility;
import com.pb.locationapis.utility.Utility;

/**
 * Created by NEX7IMH on 9/12/2017.
 */

public class MarketingContentActivity extends Activity {

    private WebView webview;
    private CustomerVo mFirstCustomerVo;
    private Button mButtonPrint, mButtonBack;
    private final String PREVIEW_MARKETING_CONTENT_URL = "file:///android_asset/marketingmailer.html";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_marketing_content);
            if(getIntent() != null) {
                mFirstCustomerVo = (CustomerVo) getIntent().getSerializableExtra(ConstantUnits.getInstance().customerVO);
            }

            webview = (WebView) findViewById(R.id.marketing_content_webview);
            mButtonPrint = (Button) findViewById(R.id.button_print);
            mButtonPrint.bringToFront();
            mButtonPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomAlertDialogUtility.getInstance().showCustomAlertDialog(MarketingContentActivity.this, "", "Send to Printer.");
                }
            });

            ((LinearLayout) findViewById(R.id.linear_lay_back)).bringToFront();
            ((LinearLayout) findViewById(R.id.linear_lay_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            Utility.getInstance().hideSoftKeyboard(this);
            CustomProgressDialogUtility.getInstance().showCustomProgressDialog(this, "", "");
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    try {
                        CustomProgressDialogUtility.getInstance().dismissProgressDialog();
                        if(mFirstCustomerVo != null) {
                            //view.loadUrl("javascript:populate('customername', 'Joe Smith')");
                            String name;
                            if(TextUtils.isEmpty(mFirstCustomerVo.getName())) {
                                name = "Joe Smith";
                            } else {
                                name = mFirstCustomerVo.getName();

                            }
                            view.loadUrl("javascript:populate('customername', \"" + name + "\")");
                            view.loadUrl("javascript:populate('addressdetails', \"" + mFirstCustomerVo.getAddressLine1() +"\")");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            webview.loadUrl(PREVIEW_MARKETING_CONTENT_URL);
                        }
                    }, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
