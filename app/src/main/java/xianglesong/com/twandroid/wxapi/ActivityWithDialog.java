package xianglesong.com.twandroid.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.json.JSONException;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.utils.FileUtils;
import xianglesong.com.twandroid.utils.HttpUtils;

/**
 * 一个背景透明，只包含一个等待对话框的activity
 */
public class ActivityWithDialog extends Activity {
    private static final String TAG = "ActivityWithDialog";
    // 连接的状态
    // 失败
    private static final int FAILURE = -1;
    // 成功
    private static final int OK = 1;

    private static class MyHandler extends Handler {
        private WeakReference<Activity> weakReference;

        public MyHandler(Activity activity) {
            super();
            weakReference = new WeakReference<Activity>(activity);
        }
    }

    private MyHandler myHandler;
    private WeixinManager weixinManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_dialog);
        // 模式设置
        setFinishOnTouchOutside(false);

        weixinManager = WeixinManager.getInstance(this);

        myHandler = new MyHandler(this) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FAILURE:
                        Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case OK:
                        Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                        finish();

                }
            }
        };

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String code = bundle.getString("code", null);
        if (code == null) {
            Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
            finish();
        }
        new MyThread(code).start();

    }

    /**
     * 与微信授权通讯，还有向服务器注册登陆的线程
     */
    private class MyThread extends Thread {
        private String data;

        public MyThread(String data) {
            this.data = data;
        }

        @Override
        public void run() {
            String accessToken = weixinManager.getAccessToken(data);
            if (accessToken == null) {
                myHandler.sendEmptyMessage(FAILURE);
                return;
            }

            String token = null;
            String openid = null;
            org.json.JSONObject jsonObject = null;
            try {
                jsonObject = new org.json.JSONObject(accessToken);
                token = jsonObject.getString("access_token");
                openid = jsonObject.getString("openid");
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

            if (token == null || openid == null) {
                myHandler.sendEmptyMessage(FAILURE);
                return;
            }

            String userInfo = weixinManager.getUserInfo(token, openid);
            // 和服务器通讯
            WXAccessDto wxAccessDto = JSONObject.toJavaObject(JSON.parseObject(userInfo), WXAccessDto.class);

            String regCode = weixinManager.register2Server(userInfo);
            if (!"ok".equalsIgnoreCase(regCode)) {
                myHandler.sendEmptyMessage(FAILURE);
                return;
            }

            String loginCode = weixinManager.login2Server(wxAccessDto.getUnionid());
            if (!"ok".equalsIgnoreCase(loginCode)) {
                myHandler.sendEmptyMessage(FAILURE);
                return;
            }

            saveInfo2Local(wxAccessDto);
            setCookies(wxAccessDto);

            // 改变登陆状态为已登录
            Constants.isLogged = true;
            boolean flag = downloadHeadPic(wxAccessDto.getHeadimgurl());
            if (flag) {
                myHandler.sendEmptyMessage(OK);
            }
        }
    }

    /**
     * 把登陆信息保存到本地
     */
    public void saveInfo2Local(WXAccessDto wxAccessDto) {
        // 把用户信息保存在sharepreferences中
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USER_NAME_KEY, wxAccessDto.getNickname());
        editor.putString("userid", wxAccessDto.getUnionid());
        editor.putBoolean(Constants.IS_LOGGED_KEY, true);
        editor.commit();
    }

    /**
     * 注入cookies
     */
    private void setCookies(WXAccessDto wxAccessDto) {
        String uionid = wxAccessDto.getUnionid();

        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        String url = "http://www.91tianwu.com";
        cookieManager.setCookie(url, "wx=" + uionid);
        cookieManager.setCookie(url, "bind=" + "y");
        String usernameCode = "";
        try {
//            usernameCode = URLEncoder.encode(wxAccessDto.getUnionid(), "utf-8");
            usernameCode = URLEncoder.encode(wxAccessDto.getNickname(), "utf-8");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        cookieManager.setCookie(url, "username=" + usernameCode);

        CookieSyncManager.getInstance().sync();
    }

    /**
     * 下载头像
     *
     * @param headImgUrl
     * @return
     */
    public boolean downloadHeadPic(String headImgUrl) {
        InputStream is = HttpUtils.getStream(headImgUrl);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        if (bitmap != null) {
            File headPicture = new File(getFilesDir(), "head.png");
            FileUtils.saveImageToLocal(headPicture, bitmap);
            return true;
        }
        return false;
    }

}



