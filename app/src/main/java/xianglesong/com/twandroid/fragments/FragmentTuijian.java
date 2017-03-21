package xianglesong.com.twandroid.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.utils.LogUtil;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.webview.WebViewUtils;

public class FragmentTuijian extends BaseFragment {

    public static final String TAG = "FragmentTuijian";

    public WebView webview;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView begin");
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_tuijian, container, false);
        WebViewUtils webViewUtils = new WebViewUtils();
        webview = webViewUtils.initCustomWebView(getActivity(), view, this, Constants.INDEX_HOME);
        Log.i(TAG, "onCreateView end");
        return view;
    }

    /**
     * 更新webview
     */
    public void updateWebview(String url) {
        Log.i(TAG, "updateWebview begin");
        if (webview == null) {

            return;
        }
        webview.loadUrl(url);
    }


    /**
     * fragment切换时回调的函数
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            LogManager.log(TAG, "close", LogUtil.TYPE_FRAGMENT);
        } else {
            LogManager.log(TAG, "open", LogUtil.TYPE_FRAGMENT);
        }
    }

}
