package xianglesong.com.twandroid.acitvity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xianglesong.logcollector.manager.LogManager;
import com.xianglesong.logcollector.utils.LogUtil;

import xianglesong.com.twandroid.R;

public class AboutActivity extends Activity {
    private static final String TAG = "AboutActivity";

    private TextView tv_version;

    private RelativeLayout relayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        tv_version = (TextView) this.findViewById(R.id.tv_version);
        PackageInfo pkg = null;
        try {
            pkg = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
            String appName = pkg.applicationInfo.loadLabel(getPackageManager()).toString();
            String versionName = pkg.versionName;
            tv_version.setText("版本号: " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        //多次点击响应，启动模式选择页面。连续点击5次
        relayout = (RelativeLayout) findViewById(R.id.re_about);
        relayout.setOnClickListener(new View.OnClickListener() {
            //用数组记录点击时间
            long[] clickTimes = new long[5];

            @Override
            public void onClick(View v) {
                //数组左移一位
                System.arraycopy(clickTimes, 1, clickTimes, 0, clickTimes.length - 1);
                //把当前时间放入数组最后一个位置
                clickTimes[clickTimes.length - 1] = SystemClock.uptimeMillis();
                if (clickTimes[clickTimes.length - 1] - clickTimes[0] <= 1500) {
                    Intent intent = new Intent(AboutActivity.this, SelectModeActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    public void questions(View view) {
        // open in webview
        Intent intent = new Intent(AboutActivity.this, TwWebviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", "调查");
        bundle.putString("link", "http://form.mikecrm.com/f.php?t=VbD9hW");
        intent.putExtra("android.intent.extra.browse", bundle);
        startActivity(intent);
//        Uri uri = Uri.parse("http://form.mikecrm.com/f.php?t=VbD9hW");
//        Intent it = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(it);
    }

    public void feedback(View view) {
        Intent intent = new Intent(AboutActivity.this, TwWebviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", "反馈");
        bundle.putString("link", "http://form.mikecrm.com/f.php?t=CuBzE8");
        intent.putExtra("android.intent.extra.browse", bundle);
        startActivity(intent);

//        Uri uri = Uri.parse("http://form.mikecrm.com/f.php?t=CuBzE8");
//        Intent it = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(it);
    }

    public void bussiness(View view) {
        Intent intent = new Intent(AboutActivity.this, TwWebviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", "商务合作");
        bundle.putString("link", "http://form.mikecrm.com/f.php?t=QeMDGK");
        intent.putExtra("android.intent.extra.browse", bundle);
        startActivity(intent);

//        Uri uri = Uri.parse("http://form.mikecrm.com/f.php?t=QeMDGK");
//        Intent it = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(it);
    }


    public void back(View view) {
        finish();
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
}
