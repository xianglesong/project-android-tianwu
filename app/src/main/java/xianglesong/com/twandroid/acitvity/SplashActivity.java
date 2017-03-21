package xianglesong.com.twandroid.acitvity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.services.UploadLogService;
import com.xianglesong.logcollector.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cn.jpush.android.api.JPushInterface;
import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.TWApplication;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.services.UpdateInfoService;
import xianglesong.com.twandroid.services.UpdateVersionService;
import xianglesong.com.twandroid.utils.FileUtils;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    // 动画持续时间
    private static final int DURATION = 3000;
    // 页面图片
    ImageView im_splash_open;
    // 页面图片的上层目录路径
    public static String openImageDir = null;
    // 页面图片的路径
    public static String openImageUrl = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        final View view = View.inflate(this, R.layout.activity_splash, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        im_splash_open = (ImageView) this.findViewById(R.id.im_splash_open);

        // 读取配置文件，初始化全局变量
        loadProperties();
        // 初始化图片文件并设置Imageview
        initFile();
        // 初始化搜索框文本
        initSearchContent();

        String deviceId = TWApplication.getDeviceInfo(getApplicationContext());
        Log.i(TAG, deviceId);

        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(DURATION);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 启动更新服务，从网上更新内容
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(Constants.DELAY_TIME);
                            // 更新基础信息和版本更新
                            updateInfo();
                            updateVersion();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                Intent i = new Intent(SplashActivity.this, IndexActivity.class);
                // 判断是从lancher启动还是点击通知启动的
                // jpush是点击启动
                Bundle b = getIntent().getExtras();
                if (b != null) {
                    String data = getIntent().getExtras().getString(Constants.Extra_Key);
                    if (data != null) {
                        i.putExtra(Constants.Extra_Key, data);
                    }
                }
                SplashActivity.this.startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        LogManager.log(TAG, "open", LogUtil.TYPE_ACTIVITY);
    }

    @Override
    public void onPause() {
        super.onPause();
        LogManager.log(TAG, "close", LogUtil.TYPE_ACTIVITY);

    }

    /**
     * 配置文件初始化：
     * 1 初始化应用程序模式
     * 2 恢复极光推送
     */
    public void loadProperties() {
        // 判断是不是第一次安装app，如果是则读取global.properties的APPMODE值为appmode
        SharedPreferences sp = getSharedPreferences(Constants.SETTINGS_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        boolean isFirstInstalled = sp.getBoolean(Constants.INSTALLED_KEY, true);
        // 如果是第一次安装
        if (isFirstInstalled) {
            // 开启极光推送功能，重新开启极光推送。
            JPushInterface.resumePush(getApplicationContext());
            try {
                Properties propGlobal = new Properties();
                InputStream isForGlobal = getAssets().open("global.properties");
                propGlobal.load(isForGlobal);
                // Constants.APPMODE = Integer.parseInt(propGlobal.getProperty("APP_MODE"));
                editor.putBoolean(Constants.INSTALLED_KEY, false);
                editor.putInt(Constants.APP_MODE_KEY, Constants.APPMODE);
                editor.commit();
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        // 正常读取
        {
            // 非第一次启动，读取配置
            Constants.APPMODE = sp.getInt(Constants.APP_MODE_KEY, 0);
            // 本机下载的最新apk的版本号
            Constants.NEW_VERSION_CODE = sp.getInt(Constants.NEW_VERSION_CODE_KEY, 0);
            Constants.SEACH_CONTENT_VALUE = sp.getString(Constants.SEARCH_CONENT_KEY, "搜一搜");
            Constants.isLogged = sp.getBoolean(Constants.IS_LOGGED_KEY, false);
            Constants.userid = sp.getString(Constants.USER_ID_KEY, null);
            Constants.username = sp.getString(Constants.USER_NAME_KEY, null);
            //
            Constants.isRefreshByLogIn = false;
        }

        Properties prop = new Properties();
        InputStream is;
        switch (Constants.APPMODE) {
            case Constants.APPMODE_LOCAL:
                try {
                    is = getAssets().open("local.properties");
                    prop.load(is);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                    e.printStackTrace();
                }
                break;
            case Constants.APPMODE_DEVELOPER:
                try {
                    is = getAssets().open("developer.properties");
                    prop.load(is);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                    e.printStackTrace();
                }
                break;
            case Constants.APPMODE_ONLINE:
                try {
                    is = getAssets().open("online.properties");
                    prop.load(is);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    is = getAssets().open("local.properties");
                    prop.load(is);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                    e.printStackTrace();
                }
                break;
        }

        // 把prop传给initConstantsByProp()来进行初始化
        initConstantsByProp(prop);
    }

    /**
     * 从配置文件对象里获取数据，并初始化Constants的值
     */
    public void initConstantsByProp(Properties prop) {
        Constants.LOCAL_IMAGE_DIR = prop.getProperty("LOCAL_IMAGE_DIR");
        Constants.OPEN_IMAGENAME = prop.getProperty("OPEN_IMAGE_NAME");
        Constants.SPLASH_IMAGE_URL = prop.getProperty("SPLASH_IMAGE_URL");
        Constants.DELAY_TIME = Integer.parseInt(prop.getProperty("DELAY_TIME"));
        Constants.INDEX_HOME = prop.getProperty("INDEX_URL");
        Constants.USER_URL = prop.getProperty("USER_URL");

        Constants.VERSION_UPDATE_URL = prop.getProperty("VERSION_UPDATE_URL");
        Constants.APPS_HOME = prop.getProperty("APPS_HOME");
        Constants.UPDATE_SEARCH_CONTENT_URL = prop.getProperty("UPDATE_SEARCH_CONTENT_URL");

        Constants.SEARCH_HOME = prop.getProperty("SEARCH_HOME");

        UploadLogService.UPLOAD_URL = prop.getProperty("UPLOAD_LOG_URL");
    }

    /**
     * 初始化搜索框文本
     */
    public void initSearchContent() {
        SharedPreferences sp = getSharedPreferences(Constants.SETTINGS_FILE_NAME, MODE_PRIVATE);
        String text = sp.getString(Constants.SEARCH_CONENT_KEY, null);
        Log.i(TAG, "default search text " + text);
        if (text != null) {
            Constants.SEACH_CONTENT_VALUE = text;
        }
    }

    /**
     * 初始化图片文件
     */
    @SuppressLint("SdCardPath")
    public void initFile() {
        openImageDir = this.getFilesDir() + Constants.LOCAL_IMAGE_DIR;
        openImageUrl = openImageDir + "/" + Constants.OPEN_IMAGENAME;
        File dir = new File(openImageDir);
        File file = new File(openImageUrl);
        Log.i(TAG, file.getAbsolutePath());
        // 目录存在
        if (dir.exists()) {
            //文件存在
            if (file.exists()) {
                im_splash_open.setImageBitmap(FileUtils.getLocalBitmap(file));
            } else { // 文件不存在，将本地的资源图片设为默认
                im_splash_open.setImageResource(R.drawable.splash);
            }
        } else { // 目录不存在
            dir.mkdirs();
            im_splash_open.setImageResource(R.drawable.splash);
        }
    }

    /**
     * 更新基本信息
     */
    public void updateInfo() {
        Intent intent = new Intent(getApplicationContext(), UpdateInfoService.class);
        getApplicationContext().startService(intent);
    }

    /**
     * 更新版本信息，从网络下载最新版本
     */
    public void updateVersion() {
        Intent intent = new Intent(getApplicationContext(), UpdateVersionService.class);
        getApplicationContext().startService(intent);
    }

}
