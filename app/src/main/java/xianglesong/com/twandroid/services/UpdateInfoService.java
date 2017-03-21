package xianglesong.com.twandroid.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

import xianglesong.com.twandroid.acitvity.SplashActivity;
import xianglesong.com.twandroid.common.Constants;
import xianglesong.com.twandroid.utils.FileUtils;
import xianglesong.com.twandroid.utils.HttpUtils;

public class UpdateInfoService extends Service {
    private static final String TAG = "UpdateInfoService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 图片地址固定，从配置文件读取
                updateImageFromInternet(Constants.SPLASH_IMAGE_URL);
                updateSearchContentFromInternet(Constants.UPDATE_SEARCH_CONTENT_URL);
            }
        }).start();

        stopSelf();

        return Service.START_NOT_STICKY;
    }

    /**
     * 从网上下载图片，下次启动时更新启动页面
     */
    public void updateImageFromInternet(String url) {
        Log.i(TAG, url);
        InputStream is = HttpUtils.getStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        if (bitmap != null) {
            FileUtils.saveImageToLocal(new File(SplashActivity.openImageUrl), bitmap);
        }
    }

    /**
     * 从服务器更新搜索框应该显示的文本内容
     */
    public void updateSearchContentFromInternet(String url) {
        Log.i(TAG, url);
        String result = HttpUtils.getUrlContent(url);
        Log.i(TAG,"search content " + result);
        String searchContent = null;
        searchContent = result;
        if (searchContent != null) {
            SharedPreferences sp = getSharedPreferences(Constants.SETTINGS_FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Constants.SEARCH_CONENT_KEY, searchContent);
            Log.i(TAG,"search content " + result);
            editor.commit();
        }
    }
}
