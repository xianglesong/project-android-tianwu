package xianglesong.com.twandroid.fragments;

import android.support.v4.app.Fragment;

import xianglesong.com.twandroid.webview.WebViewInfo;

public class BaseFragment extends Fragment implements WebViewInfo {

    private String mWebviewUrl;
    private String mWebviewTitle;

    @Override
    public String getmWebviewTitle() {
        return mWebviewTitle;
    }

    @Override
    public void setmWebviewTitle(String title) {
        this.mWebviewTitle = title;
    }

    @Override
    public String getmWebviewUrl() {
        return mWebviewUrl;
    }

    @Override
    public void setmWebviewUrl(String url) {
        this.mWebviewUrl = url;
    }
}
