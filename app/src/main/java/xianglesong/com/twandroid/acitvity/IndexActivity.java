package xianglesong.com.twandroid.acitvity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.acitvity.fx.AddPopWindow;
import xianglesong.com.twandroid.acitvity.search.SearchActivity;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.fragments.FragmentProfile;
import xianglesong.com.twandroid.fragments.FragmentSearch;
import xianglesong.com.twandroid.fragments.FragmentTuijian;
import xianglesong.com.twandroid.utils.FileUtils;
import xianglesong.com.twandroid.utils.PackageInfoUtils;
import xianglesong.com.twandroid.widgets.CustomDialog;
import xianglesong.com.twandroid.wxapi.WeixinManager;

public class IndexActivity extends FragmentActivity {

    private static final String TAG = "IndexActivity";

    FragmentTuijian tuijianfragment;
    FragmentProfile profilefragment;
    FragmentSearch searchfragment;

    private ImageView iv_add;
    private int index = 0;
    private int currentTabIndex = 0;
    private Fragment[] fragments;
    private long exitTime = 0;
    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private TextView searchTextView;

    private RelativeLayout re_tuijian;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Bundle b = msg.getData();
                        String data = b.getString(Constants.Extra_Key);
                        // 使用webview打开网址
                        tuijianfragment.updateWebview(getReceiverUrl(data));
                        onTabClicked(re_tuijian);
                }
            }
        };

        initView();
        final Bundle b = getIntent().getExtras();
        if (b != null) {
            Log.i(TAG, "bundle is not null");
            doIfByNotification(b);
        }

        installNewVersion();
    }

    /**
     * 启动已经存在的activity
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        final Bundle b = getIntent().getExtras();
        if (b != null) {
            doIfByNotification(b);
        }
    }

    /**
     * 拍照使用的
     *
     * @param requestCode 哪个activity返回
     * @param resultCode
     * @param data        相机传回的intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 根据返回值调用后续操作
        switch (requestCode) {
            // 相机调用后返回
            case Constants.CAMERA_REQUEST_CODE:
                if (data != null) {
                    // 获得缩略图
                    File file = null;
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        File dir = new File(FileUtils.getFileStorgeDir(getApplicationContext()) + "/" + "searchimage");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
                        // 获取缩略图，data固定不变，data是相机程序默认返回
                        Bitmap bitmap = data.getParcelableExtra("data");
                        // 缩略图保存到文件
                        if (bitmap != null) {
                            FileUtils.saveImageToLocal(file, bitmap);
                        }
                    }
                } else {
                    // 图片保存在传入的uri中，可以通过uri获得
                    // 程序一般走这里进行处理
                    File file = null;
                    File dir = new File(FileUtils.getFileStorgeDir(getApplicationContext()) + "/" + "searchimage");
                    file = new File(dir, "image2Search" + ".jpg");
                    if (file != null) {
                        Log.i(TAG, file.getAbsolutePath());
                    }

                    Log.d("MyLog", "data is null");
                }
        }
    }

    private void initView() {
        searchTextView = (TextView) findViewById(R.id.tv_search);

        // 初始化搜索框的文本
        if (Constants.SEACH_CONTENT_VALUE != null) {
            searchTextView.setText(Constants.SEACH_CONTENT_VALUE);
            final ViewGroup.LayoutParams lp = searchTextView.getLayoutParams();
            WindowManager wm = this.getWindowManager();

            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            Log.i(TAG, "width: " + width + " height: " + height);
            int tvWidth = lp.width;
            // 480 1080
            if (width > 480) {
                tvWidth += 120;
            }
            lp.width = tvWidth;
            searchTextView.setLayoutParams(lp);
        }

        tuijianfragment = new FragmentTuijian();
        profilefragment = new FragmentProfile();
        searchfragment = new FragmentSearch();
        fragments = new Fragment[]{tuijianfragment, searchfragment
                , profilefragment};
        imagebuttons = new ImageView[3];
        imagebuttons[0] = (ImageView) findViewById(R.id.ib_tuijian);
        imagebuttons[1] = (ImageView) findViewById(R.id.ib_search);
        imagebuttons[2] = (ImageView) findViewById(R.id.ib_profile);

        imagebuttons[0].setSelected(true);
        textviews = new TextView[3];
        textviews[0] = (TextView) findViewById(R.id.tv_tuijian);
        textviews[1] = (TextView) findViewById(R.id.re_search_tv);
        textviews[2] = (TextView) findViewById(R.id.tv_profile);
        textviews[0].setTextColor(0xFF45C01A);
        // show first fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, tuijianfragment)
                .add(R.id.fragment_container, searchfragment)
                .add(R.id.fragment_container, profilefragment)
                .hide(profilefragment)
                .hide(searchfragment).show(tuijianfragment).commit();

        re_tuijian = (RelativeLayout) findViewById(R.id.re_tuijian);

        iv_add = (ImageView) this.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPopWindow addPopWindow = null;
                switch (index) {
                    case Constants.Frag_Tuijian:
                        addPopWindow = new AddPopWindow(IndexActivity.this, tuijianfragment);
                        break;
                    case Constants.Frag_Search:
                        addPopWindow = new AddPopWindow(IndexActivity.this, searchfragment);
                        break;
                    case Constants.Frag_Profile:
                        addPopWindow = new AddPopWindow(IndexActivity.this, profilefragment);
                    default:
                }

                addPopWindow.showPopupWindow(iv_add);
            }

        });
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.re_tuijian:
                index = Constants.Frag_Tuijian;
                break;
            case R.id.re_search:
                index = Constants.Frag_Search;
                break;
            case R.id.re_profile:
                index = Constants.Frag_Profile;
                break;
        }

        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;

    }

    public void searchText(View view) {
        startActivity(new Intent(IndexActivity.this, SearchActivity.class));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                moveTaskToBack(false);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 切换activity时调用
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        // 自定义跟踪日志
        LogManager.log(TAG, "open", LogUtil.TYPE_ACTIVITY);

        Log.d("MyLog", "isRefreshByLogIn is " + Constants.isRefreshByLogIn);
        // 已经过响应用户的登陆事件
        if (Constants.isRefreshByLogIn) {
            return;
        }
        Log.d("MyLog", "islogged is " + Constants.isLogged);
        if (Constants.isLogged) {
            if (profilefragment == null) {
                initView();
            }
            profilefragment.refleshByLogIn();
            Constants.isRefreshByLogIn = true;
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        LogManager.log(TAG, "close", LogUtil.TYPE_ACTIVITY);
    }

    public void about(View view) {
        Intent aboutIntent = new Intent(IndexActivity.this, AboutActivity.class);
        startActivity(aboutIntent);
    }

    public void settings(View view) {
        Intent settingsIntent = new Intent(IndexActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void mall(View view) {
        Intent mallIntent = new Intent(IndexActivity.this, MallActivity.class);
        startActivity(mallIntent);
    }

    public void weidian(View view) {
        Intent mallIntent = new Intent(IndexActivity.this, WeidianActivity.class);
        startActivity(mallIntent);
    }


    /**
     * 微信登录
     *
     * @param view
     */
    public void wxLogIn(View view) {
        WeixinManager.getInstance(this).sendWxAuthRequest();
    }

    /**
     * 用户退出登陆
     *
     * @param view
     */
    public void wxLogOut(View view) {
        Constants.isLogged = false;
        Constants.isRefreshByLogIn = false;

        SharedPreferences sp = getSharedPreferences(Constants.SETTINGS_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.IS_LOGGED_KEY, false);
        editor.putString(Constants.USER_NAME_KEY, null);
        editor.putString(Constants.USER_ID_KEY, null);
        editor.commit();

        if (profilefragment == null) {
            initView();
        }

        //清除cookie
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        profilefragment.refleshByLogOut();

        Toast.makeText(getApplicationContext(), "退出成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 解析json，获得url
     *
     * @param data 推送数据
     * @return url
     */
    public String getReceiverUrl(String data) {
        String url = null;
        try {
            JSONObject obj = new JSONObject(data);
            url = obj.getString(Constants.Extra_Key);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return url;
    }

    public void doIfByNotification(final Bundle b) {
        // 判断是从lancher启动还是点击通知启动的
        String data = b.getString(Constants.Extra_Key);
        if (data == null) {
            if (tuijianfragment == null) {
                initView();
            }
            onTabClicked(re_tuijian);
            return;
        }
        Log.d("MyLog", "data is " + data);
        if (tuijianfragment == null) {
            initView();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (tuijianfragment.webview == null) {
                    Log.d(TAG, "webview is null");
                }
                while (tuijianfragment.webview == null) ;
                Message msg = Message.obtain();
                msg.setData(b);
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        }).start();


    }

    /**
     * 启动时检测安装新版本
     */
    public void installNewVersion() {
        int oldVersion = 0;
        Log.i(TAG, "begin install new version");
        oldVersion = PackageInfoUtils.getVersionCode(getApplicationContext());
        Log.i(TAG, "oldVersion " + oldVersion);
        // get new_version_code
        if (Constants.NEW_VERSION_CODE > oldVersion) {
            File dir = FileUtils.getFileStorgeDir(getApplicationContext());
            if (dir != null) {
                Log.i(TAG, "dir path " + dir.getAbsolutePath());
            }
            final File file = new File(dir, Constants.APK_NAME);
            // final File file = new File(FileUtils.getFileStorgeDir(getApplicationContext()) + "/" + Constants.APK_NAME);
            if (file != null) {
                // 本地存在apk文件时
                CustomDialog.CustomDialogClickListenr customDialogClickListenr = new CustomDialog.CustomDialogClickListenr() {
                    @Override
                    public void onPositiveButtonClick() {
                        // Uri uri = Uri.fromFile(file);
                        // 安装程序标准写法
                        Intent installIntent = new Intent();
                        installIntent.setAction(Intent.ACTION_VIEW);
                        installIntent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
                        IndexActivity.this.startActivity(installIntent);
                    }

                    @Override
                    public void onNegativeButtonClick() {
                    }
                };
                CustomDialog customDialog = new CustomDialog(IndexActivity.this, customDialogClickListenr, getResources().getString(R.string.update_new_version));
                customDialog.show();
            }
        }
    }

}
