package com.xianglesong.logcollector.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xianglesong.logcollector.services.UploadLogService;
import com.xianglesong.logcollector.utils.Utils;

public class UploadLogReceiver extends BroadcastReceiver {

    private static final String TAG = "UploadLogReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // his method is called when the BroadcastReceiver is receiving
        // 网络变化的时候，broadcast获得处理，检测当前为wifi则上传文件
        if (Utils.isWifi(context)) {
            Intent i = new Intent(context, UploadLogService.class);
            context.startService(i);
        }
    }
}
