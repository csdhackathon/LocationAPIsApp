package com.pb.locationapis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
                Bundle bundle = getIntent().getExtras();
                mFirstCustomerVo = (CustomerVo) bundle.getParcelable(ConstantUnits.getInstance().customerVO);
            }

            webview = (WebView) findViewById(R.id.marketing_content_webview);
            mButtonPrint = (Button) findViewById(R.id.button_print);
            mButtonPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomAlertDialogUtility.getInstance().showCustomAlertDialog(MarketingContentActivity.this, "", "Send to Printer.");
                }
            });

            ((LinearLayout) findViewById(R.id.linear_lay_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            Utility.getInstance(this).hideSoftKeyboard(this);
            CustomProgressDialogUtility.getInstance().showCustomProgressDialog(this, "", "");

            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    try {
                        CustomProgressDialogUtility.getInstance().dismissProgressDialog();

                        view.loadUrl("javascript:populate('customername', '" + mFirstCustomerVo.getName() + "')");
                        view.loadUrl("javascript:populate('addressdetails', '" + mFirstCustomerVo.getAddressLine1() +"')");
					/*view.loadUrl("javascript:populate('customername', 'Mahender Singh Shah')");
					view.loadUrl("javascript:populate('addressdetails', 'Ahinsa Khand 1<br />Ghaziabad<br />Uttar Pradesh<br />201014')");*/

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
