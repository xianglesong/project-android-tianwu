package xianglesong.com.twandroid.acitvity.search.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.fragments.BaseFragment;
import xianglesong.com.twandroid.webview.WebViewUtils;


public class FragmentSearchResults extends BaseFragment {
    private static final String TAG = "FragmentSearchResults";

    private WebView webview;

    private Handler mHandler;
    private final static String UPDATE_URL = "update_url";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tuijian, container, false);
        WebViewUtils webViewUtils = new WebViewUtils();
        webview = webViewUtils.initCustomWebView(getActivity(), view, this, Constants.SEARCH_HOME);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        webview.loadUrl(Constants.SEARCH_PAGE + msg.getData().getString(UPDATE_URL));
                }
            }
        };

        return view;
    }


    public void updateWebview(final String keyword) {
        if (webview != null) {
            webview.loadUrl(Constants.SEARCH_PAGE + keyword);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (webview == null) ;
                    Message msg = new Message();
                    msg.what = 0;
                    Bundle b = new Bundle();
                    b.putString(UPDATE_URL, keyword);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                }
            }).start();
        }
    }

    public void clearWebviewHistory() {
        webview.clearHistory();
    }


}

