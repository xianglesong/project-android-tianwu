package xianglesong.com.twandroid.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import xianglesong.com.twandroid.acitvity.IndexActivity;
import xianglesong.com.twandroid.common.Constants;

public class MyJPushReceiver extends BroadcastReceiver {

    private static final String TAG = "MyJPushReceiver";

    private static String message = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            //
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            // 收到通知
            Bundle bundle = intent.getExtras();
            message = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.d("MyLog", "onReceive - " + intent.getAction());
            Log.d("MyLog", "onReceive - message" + message);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            // 打开通知，在这里可以自己写代码去定义用户点击后的行为
            // 判断app进程是否存活
            if (isAlive(context, "xianglesong.com.twandroid")) {
                // 如果app进程存在，那么从IndexActivity开始启动
                Intent i = new Intent(context, IndexActivity.class);  //自定义打开的界面
                Bundle b = new Bundle();
                Log.d("MyLog", "onReceive - message now is " + message);
                b.putString(Constants.Extra_Key, message);
                i.putExtras(b);
                // 从service启动activity，必须在intent中添加此属性
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else {
                // 如果app进程不存在，那么从launcher启动app
                Intent i = context.getPackageManager().getLaunchIntentForPackage("xianglesong.com.twandroid");
                Bundle b = new Bundle();
                b.putString(Constants.Extra_Key, message);
                i.putExtras(b);
                Log.d("MyLog", "onReceive - message now is " + message);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } else {
            Log.d("MyLog", "Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * 判断app进程是否存活
     */
    public boolean isAlive(Context context, String packageName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = manager.getRunningAppProcesses();
        for (int i = 0; i < runningAppProcessInfoList.size(); i++) {
            if (runningAppProcessInfoList.get(i).processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

}

