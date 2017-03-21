package xianglesong.com.twandroid.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.utils.FileUtils;
import xianglesong.com.twandroid.utils.HttpUtils;
import xianglesong.com.twandroid.utils.PackageInfoUtils;

public class UpdateVersionService extends Service {

    private static final String TAG = "UpdateVersionService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 开启线程运行
        new Thread(new Runnable() {
            @Override
            public void run() {
                int oldVersionCode = 0;
                oldVersionCode = PackageInfoUtils.getVersionCode(getApplicationContext());
                Log.i(TAG, "old version " + oldVersionCode);
                // 访问服务器端，获得关于版本更新的内容，包括版本号和下载地址
                String result = HttpUtils.getUrlContent(Constants.VERSION_UPDATE_URL);
                Log.i(TAG, "result " + result);
                int newVersionCode = getNewVersionCode(result);
                String downloadUrl = Constants.APPS_HOME + getDownloadUrl(result);

                if (newVersionCode > oldVersionCode && downloadUrl != null) {
                    Log.i(TAG, "downloadUrl " + downloadUrl);
                    InputStream inputStream = HttpUtils.getStream(downloadUrl);
                    if (inputStream != null) {
                        File dir = FileUtils.getFileStorgeDir(getApplicationContext());
                        if (dir != null) {
                            Log.i(TAG, "dir path " + dir.getAbsolutePath());
                        }
                        File file = new File(dir, Constants.APK_NAME);
                        FileUtils.saveFile2Loacl(file, inputStream);
                        Log.i(TAG, "app file path " + file.getAbsolutePath());
                        if (file.length() != 0) {
                            Log.i(TAG, "app file length: " + file.length());
                            SharedPreferences sp = getSharedPreferences(Constants.SETTINGS_FILE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            PackageManager packageManager = getPackageManager();
                            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
                            // null
                            if (packageInfo != null) {
                                Log.i(TAG, "download version" + packageInfo.versionCode);
                                editor.putInt(Constants.NEW_VERSION_CODE_KEY, packageInfo.versionCode);
                                editor.commit();
                            } else {
                                Log.i(TAG, "packageInfo is null");
                            }
                        }
                    }
                }
            }
        }).start();

        // 只运行一次
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    public int getNewVersionCode(String result) {
        int code = 0;
        try {
            if (result != null) {
                String[] rs = result.split(";");
                if (rs != null && rs.length > 0) {
                    code = Integer.parseInt(rs[0]);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.getMessage();
        }
        return code;
    }

    public String getDownloadUrl(String result) {
        String url = null;
        try {
            if (result != null) {
                String[] rs = result.split(";");
                if (rs != null && rs.length > 1) {
                    url = rs[1];
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.getMessage();
        }
        if (url != null) {
            Log.i(TAG, url);
        }
        return url;
    }

}
