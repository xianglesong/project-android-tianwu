package com.xianglesong.logcollector.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xianglesong.logcollector.utils.LogFileUtils;
import com.xianglesong.logcollector.utils.LogUploadUtil;

import java.io.File;

public class UploadLogService extends Service {

    private static final String TAG = "UploadLogService";

    // 当天log存储文件的名字
    String currentName = null;

    // 上传文件的url
    public static String UPLOAD_URL = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentName = LogFileUtils.getLogFileName(getApplicationContext());
        Log.e(TAG, "currentName " + currentName);
        uploadLog();
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 上传log文件
     */
    public void uploadLog() {
        File logDir = LogFileUtils.getFileDir(getApplicationContext(), LogFileUtils.LOG_CACHE_DIRECTORY_NAME);
        final File[] logFileList = logDir.listFiles();
        Log.i(TAG, "UPLOAD_URL " + UPLOAD_URL);
        // 通过调用函数，构造一个当天存储log文件的文件名字，文件名字如果相同就不上传，不相同就上传，因为理论上一天上传一次
        if (logFileList != null && UPLOAD_URL != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < logFileList.length; i++) {
                        //if (!logFileList[i].getPath().equals(currentName))
                        {
                            // 如果上传成功，就删除本地的文件
                            if (LogUploadUtil.uploadFile2Server(UPLOAD_URL, logFileList[i])) {
                                LogFileUtils.delete(logFileList[i]);
                            }
                        }
                    }
                }
            }).start();

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
