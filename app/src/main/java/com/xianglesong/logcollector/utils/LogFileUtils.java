
package com.xianglesong.logcollector.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import xianglesong.com.twandroid.utils.FileUtils;

public final class LogFileUtils {

    private static final String TAG = "LogFileUtils";

    private static String LOG_FILE_NAME = null;

    // The name of log directory, the path is "/data/data/xianglesong.com.twandroid/files/"
    public final static String LOG_CACHE_DIRECTORY_NAME = "twlogs";

    /**
     * Delete file(file or folder).
     *
     * @param file
     */
    public static void delete(File file) {
        if (file == null) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }

        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                delete(f);
            } else {
                f.delete();
            }
        }
        file.delete();
    }

    /**
     * save message to file
     *
     * @param context
     * @param msg
     */
    public static void save2File(Context context, String msg) {
        synchronized (LogFileUtils.class) {
            try {
                if (LOG_FILE_NAME == null) {
                    LOG_FILE_NAME = getLogFileName(context);
                }
                File file = new File(LOG_FILE_NAME);
                if (!file.exists() || file.isDirectory()) {
                    LogFileUtils.delete(file);
                    file.createNewFile();
                }

                FileOutputStream trace = new FileOutputStream(file, true);
                OutputStreamWriter writer = new OutputStreamWriter(trace,
                        "utf-8");
                writer.write(msg);
                writer.flush();

                trace.flush();
                trace.close();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }


    /**
     * 获得log文件的文件夹路径
     *
     * @param context
     * @param dirName
     * @return
     */
    public static File getFileDir(Context context, String dirName) {
        // File filesDir = context.getFilesDir();
        File filesDir = FileUtils.getFileStorgeDir(context);
        File dir = new File(filesDir, dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (dir != null) {
            Log.i(TAG, dir.getAbsolutePath());
        }
        return dir;
    }

    /**
     * 通过Calendar.DAY_OF_YEAR来命名log文件名字，保证一天只生成一个文件
     */
    public static String getLogFileName(Context context) {
        if (LOG_FILE_NAME == null) {
            LOG_FILE_NAME = new File(getFileDir(context, LOG_CACHE_DIRECTORY_NAME), String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) + ".txt").getAbsolutePath();
        }
        return LOG_FILE_NAME;
    }

}
