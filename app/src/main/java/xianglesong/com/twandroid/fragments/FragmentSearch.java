package xianglesong.com.twandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.utils.LogUtil;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.webview.WebViewUtils;

public class FragmentSearch extends BaseFragment {
    public static final String TAG = "FragmentSearch";

    private WebView webview;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_search, container, false);
        WebViewUtils webViewUtils = new WebViewUtils();
        webview = webViewUtils.initCustomWebView(getActivity(), view, this, Constants.SEARCH_HOME);

        return view;
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
