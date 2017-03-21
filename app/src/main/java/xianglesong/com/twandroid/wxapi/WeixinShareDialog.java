package xianglesong.com.twandroid.wxapi;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.webview.WebViewInfo;

/**
 * 分享到微信的对话框
 */
public class WeixinShareDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = "WeixinShareDialog";

    private LinearLayout sLayout;
    private LinearLayout tLayout;
    private Context mContext;

    //默认分享的url
    public String mUrl = "http://www.91tianwu.com";
    public String mTitle = "添物科技";

    WebViewInfo webViewInfo;

    public WeixinShareDialog(Context context, WebViewInfo webViewInfo) {
        super(context);
        mContext = context;
        this.webViewInfo = webViewInfo;
        initDialog(mContext);
    }

    public void initDialog(Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
        sLayout = (LinearLayout) mView.findViewById(R.id.share_dialog_session);
        sLayout.setOnClickListener(this);
        tLayout = (LinearLayout) mView.findViewById(R.id.share_dialog_timeline);
        tLayout.setOnClickListener(this);

        super.setContentView(mView);

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.gravity = Gravity.CENTER;
        // 获取屏幕宽度
        int mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        lp.width = (int) (mScreenWidth * 0.8);

        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        // 根据点击事件，选择相应的分享模式，例如聊天和朋友圈
        switch (v.getId()) {
            case R.id.share_dialog_session:
                WeixinManager.getInstance(mContext).shareToWx(SendMessageToWX.Req.WXSceneSession, webViewInfo);
                Log.d("MyLog", "share to weixin is click");
                this.dismiss();
                break;
            case R.id.share_dialog_timeline:
                WeixinManager.getInstance(mContext).shareToWx(SendMessageToWX.Req.WXSceneTimeline, webViewInfo);
                this.dismiss();
                break;
            default:
        }
    }

}
