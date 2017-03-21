package xianglesong.com.twandroid.acitvity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;
import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.utils.LogUtil;

import xianglesong.com.twandroid.R;
import xianglesong.com.twandroid.common.Constants;

public class SelectModeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SelectModeActivity";

    //模式选择按钮
    Button btnLocal;
    Button btnDev;
    Button btnOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        btnLocal = (Button) findViewById(R.id.select_local);
        btnDev = (Button) findViewById(R.id.select_dev);
        btnOnline = (Button) findViewById(R.id.select_online);
        btnLocal.setOnClickListener(this);
        btnDev.setOnClickListener(this);
        btnOnline.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        LogManager.log(TAG, "open", LogUtil.TYPE_ACTIVITY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        LogManager.log(TAG, "close", LogUtil.TYPE_ACTIVITY);
    }

    @Override
    public void onClick(View v) {
        //根据按钮不同，设置不同的APPMODE值
        SharedPreferences sp = getSharedPreferences(Constants.SETTINGS_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        switch (v.getId()) {
            case R.id.select_local:
                editor.putInt(Constants.APP_MODE_KEY, Constants.APPMODE_LOCAL);
                break;
            case R.id.select_dev:
                editor.putInt(Constants.APP_MODE_KEY, Constants.APPMODE_DEVELOPER);
                break;
            case R.id.select_online:
                editor.putInt(Constants.APP_MODE_KEY, Constants.APPMODE_ONLINE);
                break;
            default:
        }
        editor.commit();


        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
