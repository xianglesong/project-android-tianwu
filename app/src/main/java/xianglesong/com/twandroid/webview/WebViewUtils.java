package xianglesong.com.twandroid.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.utils.NetworkUtils;

/**
 * webview的工具类，可以返回一个定制好的webview
 */
public class WebViewUtils {
    // 进度条
    private ProgressBar mProgressbar;
    // webview主体
    private WebView mWebview = null;
    // 错误页面的layout
    private RelativeLayout mErrorLayout;
    // 错误页面里面的重新加载按钮
    private Button reloadButton;
    private Context mContext;

    private String failedUrl = null;

    private TextView urlTextView = null;

    /**
     * @param context
     * @return
     */
    public WebView initCustomWebView(Context context, final View parent, final WebViewInfo webViewInfo, String indexUrl) {
        mContext = context;
        mProgressbar = (ProgressBar) parent.findViewById(R.id.custom_webview_progress);
        mWebview = (WebView) parent.findViewById(R.id.custom_webview);
        mErrorLayout = (RelativeLayout) parent.findViewById(R.id.custom_network_error);
        reloadButton = (Button) parent.findViewById(R.id.custom_network_error_button);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetWorkConnected(mContext)) {
                    urlTextView = (TextView) parent.findViewById(R.id.custom_network_error_text);
                    if(!urlTextView.getText().toString().contains("failure")) {
                        urlTextView.setText(urlTextView.getText() + " failure " + failedUrl);
                    }
                    mErrorLayout.setVisibility(View.GONE);
                    mWebview.setVisibility(View.VISIBLE);
                    mWebview.loadUrl(failedUrl);
                }
            }
        });

        mProgressbar.setMax(100);

        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressbar.setProgress(newProgress);
            }
        });
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressbar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressbar.setVisibility(View.GONE);
                mWebview.getSettings().setLoadsImagesAutomatically(true);
                webViewInfo.setmWebviewUrl(mWebview.getUrl());
                webViewInfo.setmWebviewTitle(mWebview.getTitle());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadUrl(url);
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                failedUrl = failingUrl;
                view.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                view.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.VISIBLE);
            }
        });

        // 设置webview可以后退，当不能后退时，返回false，代表不消费这件事件，返回给activity处理
        mWebview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
                        //mWebview.goBack();
                        return goBackInWebView();
                    }
                }
                return false;
            }
        });

        mWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
                //((Activity) mContext).finish();
            }
        });
        mWebview.setHorizontalScrollBarEnabled(false);

        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.requestFocusFromTouch();
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        // 去掉滚动条
        mWebview.setVerticalScrollBarEnabled(false);

        if (Build.VERSION.SDK_INT > 19) {
            mWebview.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebview.getSettings().setLoadsImagesAutomatically(false);
        }

        mWebview.loadUrl(indexUrl);
        return mWebview;
    }

    /**
     * 返回到非空白页面
     */
    private boolean goBackInWebView() {
        WebBackForwardList history = mWebview.copyBackForwardList();
        int index = -1;
        while (mWebview.canGoBackOrForward(index)) {
            if (!history.getItemAtIndex(history.getCurrentIndex() + index).getUrl().equals("about:blank")) {
                mWebview.goBackOrForward(index);

                return true;
            }
            index--;

        }
        return false;
    }

}
