package com.google.zxing.client.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.webview.WebViewInfo;
import xianglesong.com.twandroid.webview.WebViewUtils;

/**
 * scan result process.
 */
public class ScanResultActivity extends Activity {
    private WebView webview;
    private TextView contentText;
    private RelativeLayout errorLayout;
    private FrameLayout webviewLayout;
    private Handler mHandler;

    private WebViewInfo webViewInfo = new WebViewInfo() {
        String title;
        String url;

        @Override
        public String getmWebviewTitle() {
            return title;
        }

        @Override
        public void setmWebviewTitle(String title) {
            this.title = title;
        }

        @Override
        public String getmWebviewUrl() {
            return url;
        }

        @Override
        public void setmWebviewUrl(String url) {
            this.url = url;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_scan_result, null);
        setContentView(view);
        contentText = (TextView) findViewById(R.id.activity_scan_result_text);
        errorLayout = (RelativeLayout) findViewById(R.id.activity_scan_result_error);
        webviewLayout = (FrameLayout) view.findViewById(R.id.activity_scan_result_webview);
        WebViewUtils webViewUtils = new WebViewUtils();
        webview = webViewUtils.initCustomWebView(this, view, webViewInfo, null);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        webviewLayout.setVisibility(View.VISIBLE);
                        String url = msg.getData().getString(ScanConstants.KEY_CONTENT);
                        webview.loadUrl(url);
                        break;
                    case 1:
                        errorLayout.setVisibility(View.VISIBLE);
                }
            }
        };

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            // 根据code判断二维码类型，进行相应显示
            int code = bundle.getInt(ScanConstants.KEY_TYPE, ScanConstants.TYPE_0THER);
            switch (code) {
                case ScanConstants.TYPE_URI:
                    final String url = bundle.getString(ScanConstants.KEY_CONTENT);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 先用一个httpurlconnection测试该url是否可以连通,若不能连通则显示错误提示页面
                                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                                urlConnection.setConnectTimeout(5000);
                                urlConnection.setRequestMethod("GET");
                                urlConnection.getInputStream();
                                int code = urlConnection.getResponseCode();
                                String contentType = urlConnection.getContentType().toString();
                                Log.d("MyLog", "contentType is " + contentType);
                                if (code == 200) {
                                    Message msg = Message.obtain();
                                    msg.what = 0;
                                    Bundle b = new Bundle();
                                    b.putString(ScanConstants.KEY_CONTENT, url);
                                    msg.setData(b);
                                    mHandler.sendMessage(msg);

                                } else {
                                    throw new IOException();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                mHandler.sendEmptyMessage(1);
                            }
                        }
                    }).start();
                    break;
                case ScanConstants.TYPE_0THER:
                    contentText.setVisibility(View.VISIBLE);
                    contentText.setText((bundle.getString(ScanConstants.KEY_CONTENT).toString()));
                    break;
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.clearCache(true);
        mHandler.removeCallbacks(null);
    }

    public void back(View view) {
        finish();
    }
}
