package xianglesong.com.twandroid.wxapi;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.webview.WebViewInfo;

/**
 * 微信管理类
 */
public class WeixinManager {
    private static final String TAG = "WeixinManager";

    public static final String TW_WX_STATE_CODE = "tianwu_weixin_login";

    private static WeixinManager instance;
    private static IWXAPI iwxapi;

    // 分享的title
    public String mTitle;
    // 分享的描述,限制长度不超过1KB
    public String mDescription;
    // 分享的url,默认为www.baidu.com
    private String mUrl;

    Context mContext;

    private WeixinManager(Context context) {
        this.mContext = context;
        // 初始化微信接口
        iwxapi = WXAPIFactory.createWXAPI(context.getApplicationContext(), Constants.WX_APP_ID, true);
        iwxapi.registerApp(Constants.WX_APP_ID);
    }

    public static WeixinManager getInstance(Context context) {
        if (instance == null)
            synchronized (WeixinManager.class) {
                if (instance == null) {
                    instance = new WeixinManager(context);
                }
            }
        if (!iwxapi.isWXAppInstalled()) {
            Toast.makeText(context, "未安装微信客户端！", Toast.LENGTH_SHORT).show();
        }
        return instance;
    }

    /**
     * 分享到微信，需要传入SendMessageToWX.Req.WXSceneSession或SendMessageToWX.Req.WXSceneTimeline,表示分享到聊天或者分享到朋友圈中
     *
     * @param shareScene 微信定义
     * @param webViewInfo webinfo
     */
    public void shareToWx(int shareScene, WebViewInfo webViewInfo) {
        mTitle = webViewInfo.getmWebviewTitle();
        mUrl = webViewInfo.getmWebviewUrl();
        if (mTitle == null || mUrl == null) {
            mTitle = "添物科技";
            mUrl = Constants.INDEX_HOME;
        }

        WXMediaMessage msg = createMsgForWebpage(mUrl, mTitle);

        SendMessageToWX.Req request = new SendMessageToWX.Req();
        request.message = msg;
        request.scene = shareScene;
        request.transaction = String.valueOf(System.currentTimeMillis());

        // 通过api发送请求给微信
        iwxapi.sendReq(request);
    }

    /**
     * 创建一个分享网页的WXMediaMessage
     *
     * @param url
     * @param title
     * @return
     */
    private WXMediaMessage createMsgForWebpage(String url, String title) {
        // 初始化一个WXWebpageObject,需要给webpageUrl赋值一个url
        WXWebpageObject webObj = new WXWebpageObject();
        webObj.webpageUrl = url;

        // 把WXWebpageObject赋给msg的mediaObject
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = webObj;
        // 设置消息的title
        msg.title = title;
        // 设置消息的description
        // msg.description = "一家专门做导购的网站";
        // 设置消息的缩略图，限制内容大小不超过32KB
        msg.setThumbImage(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo));

        return msg;
    }

    public boolean handleIntent(Intent arg0, IWXAPIEventHandler arg1) {
        if (iwxapi == null)
            return false;
        return iwxapi.handleIntent(arg0, arg1);
    }

    public void sendWxAuthRequest() {
        Log.d("MyLog", "senWxAuthRequest");
        SendAuth.Req req = new SendAuth.Req();
        // 不能修改
        req.scope = "snsapi_userinfo";
        // 可以修改
        req.state = TW_WX_STATE_CODE;
        iwxapi.sendReq(req);
    }

    /**
     * 处理微信授权登陆
     *
     * @param code 微信返回的code
     * @return
     */
    public String getAccessToken(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.weixin.qq.com/sns/oauth2/access_token?").append("appid=")
                .append(Constants.WX_APP_ID).append("&secret=")
                .append(Constants.WX_APP_SECRET).append("&code=")
                .append(code).append("&grant_type=authorization_code");
        String url = sb.toString();
        String result = HttpsUtils.getUrlContent(url);

        return result;
    }

    public String refreshAccessToken(String refleshToken) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.weixin.qq.com/sns/oauth2/refresh_token?")
                .append("appid=").append(Constants.WX_APP_ID)
                .append("&grant_type=refresh_token&refresh_token=")
                .append(refleshToken);
        String url = sb.toString();
        String result = HttpsUtils.getUrlContent(url);

        return result;
    }

    /**
     * @param access_token 之前获取微信返回的token
     * @param openid       对一个应用唯一
     * @return
     */
    public String getUserInfo(String access_token, String openid) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.weixin.qq.com/sns/userinfo?access_token=")
                .append(access_token)
                .append("&openid=")
                .append(openid);
        String url = sb.toString();
        String result = HttpsUtils.getUrlContent(url);

        return result;
    }

    /**
     * 把微信返回的信息发给服务器端，验证登陆
     *
     * @param userInfo 微信返回的用户信息字符串
     * @return
     */
    public String register2Server(String userInfo) {
        String result = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = null;
            HttpPost post = new HttpPost(Constants.USER_URL + "users?action=appwxreg&ts=timestamp");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("userinfo", userInfo));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
            post.setEntity(uefEntity);
            httpResponse = httpClient.execute(post);
            if (httpResponse != null) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                }
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 用户登陆
     */
    public String login2Server(String unionid) {
        String result = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = null;
            HttpPost post = new HttpPost(Constants.USER_URL + "users?action=appwxlog&ts=timestamp");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("unionid", unionid));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
            post.setEntity(uefEntity);
            httpResponse = httpClient.execute(post);
            if (httpResponse != null) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
