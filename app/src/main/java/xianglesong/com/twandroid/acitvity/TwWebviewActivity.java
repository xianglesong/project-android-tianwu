package xianglesong.com.twandroid.acitvity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import xianglesong.com.twandroid.R;

public class TwWebviewActivity extends Activity {
    private static final String TAG = "BrowseActivity";

    private String link;
    private String title;
    private WebView webview;

    private TextView navTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tw_webview);

        Intent startingIntent = getIntent();

        if (startingIntent != null) {
            Bundle bundle = startingIntent
                    .getBundleExtra("android.intent.extra.browse");
            if (bundle == null) {
                title = "";
            } else {
                title = bundle.getString("title");
                link = bundle.getString("link");
            }
        } else {
            title = "";
        }

        Log.i(TAG, "title is: " + title);
        Log.i(TAG, "link is: " + link);
        setTitle(title);
        this.navTitle = (TextView) this.findViewById(R.id.tv_nav_title);
        navTitle.setText(title);

        webview = (WebView) this.findViewById(R.id.wb_browse);
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);

        //加载需要显示的网页
        webview.loadUrl(link);
        webview.getSettings().setAppCacheEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                // view.loadDataWithBaseURL(url,null,null,null,null);
                return true;
            }

            // 页面载入前调用
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            // 页面载入完成后调用
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }

        };
        // 设置setWebChromeClient对象
        webview.setWebChromeClient(wvcc);
    }

    public void back(View view) {
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        return false;
    }
}
