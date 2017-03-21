package xianglesong.com.twandroid.acitvity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.utils.LogUtil;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.acitvity.search.SearchActivity;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.webview.WebViewInfo;
import xianglesong.com.twandroid.webview.WebViewUtils;

public class MallActivity extends Activity implements WebViewInfo {
    public static final String TAG = "MallActivity";

    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.activity_mall, null);
        WebViewUtils webViewUtils = new WebViewUtils();
        webview = webViewUtils.initCustomWebView(getApplicationContext(), linearLayout, this, Constants.TAOBAO_HOME);

        setContentView(linearLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        LogManager.log(TAG, "open", LogUtil.TYPE_ACTIVITY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        LogManager.log(TAG, "close", LogUtil.TYPE_ACTIVITY);
    }

    public void searchText(View view) {
        startActivity(new Intent(MallActivity.this, SearchActivity.class));
    }

    public void back(View view) {
        finish();
    }

    @Override
    public String getmWebviewTitle() {
        return null;
    }

    @Override
    public void setmWebviewTitle(String title) {

    }

    @Override
    public String getmWebviewUrl() {
        return null;
    }

    @Override
    public void setmWebviewUrl(String url) {

    }
}
