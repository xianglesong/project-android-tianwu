package xianglesong.com.twandroid.acitvity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.umeng.analytics.MobclickAgent;
import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.utils.LogUtil;

import cn.jpush.android.api.JPushInterface;
import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.utils.FileUtils;
import xianglesong.com.twandroid.widgets.CustomDialog;

public class SettingsActivity extends Activity {

    private static final String TAG = "SettingsActivity";

    // 推送开关
    private ToggleButton toggleButton;

    // 显示缓存大小的文本控件
    private TextView sizeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toggleButton = (ToggleButton) findViewById(R.id.push_switch);
        // 判断jpush服务是否停止来设置toggleButton的初始状态
        if (JPushInterface.isPushStopped(getApplicationContext())) {
            Log.d("MyLog", "JPush is stop now");
            toggleButton.setChecked(false);
        }
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    JPushInterface.resumePush(getApplicationContext());
                    Log.d("MyLog", "JPush resume");
                } else {
                    JPushInterface.stopPush(getApplicationContext());
                    Log.d("MyLog", "JPush stop");
                }
            }
        });

        sizeTextView = (TextView) findViewById(R.id.clear_cache_size_textview);
        refleshTextview();
    }

    public void refleshTextview() {
        sizeTextView.setText(String.format("%.2f", FileUtils.getCacheTotal(getApplicationContext())) + "M");
    }

    /**
     * 清除缓存
     */
    public void clearCache(View view) {
        CustomDialog.CustomDialogClickListenr customDialogClickListenr = new CustomDialog.CustomDialogClickListenr() {
            @Override
            public void onPositiveButtonClick() {
                if (FileUtils.clearCache(SettingsActivity.this)) {
                    Toast.makeText(SettingsActivity.this, "清除完毕", Toast.LENGTH_SHORT).show();
                    refleshTextview();
                }
            }

            @Override
            public void onNegativeButtonClick() {

            }
        };
        CustomDialog customDialog = new CustomDialog(SettingsActivity.this, customDialogClickListenr, getResources().getString(R.string.clear_cache));
        customDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogManager.log(TAG, "open", LogUtil.TYPE_ACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogManager.log(TAG, "close", LogUtil.TYPE_ACTIVITY);
        MobclickAgent.onResume(this);
    }

    //返回按钮的响应函数
    public void back(View view) {
        finish();
    }
}
