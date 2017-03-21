package xianglesong.com.twandroid.wxapi;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.widget.Toast;


import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * 微信回调的activity
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeixinManager.getInstance(this).handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        WeixinManager.getInstance(this).handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 处理了微信回调
     *
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                // 如果是由微信登陆请求的返回值
                if (!(baseResp instanceof SendAuth.Resp)) {
                    finish();
                    return;
                }
                SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                if (resp.state.equals(WeixinManager.TW_WX_STATE_CODE)) {
                    // 微信返回code
                    String code = resp.code;
                    // 微信授权登陆与微信连接对话框activity
                    Intent intent = new Intent(this, ActivityWithDialog.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("code", code);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                break;
            // 用户取消
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
                finish();
                break;
            // 用户拒绝
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(getApplicationContext(), "拒绝", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default: finish();
        }
    }
}

