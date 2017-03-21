package xianglesong.com.twandroid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 把bitmap保存到本地中
     */
    public static void saveImageToLocal(File file, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 清除缓存
     */
    public static boolean clearCache(Context mContext) {
        //清除图片缓存
        File file1 = new File(Environment.getExternalStorageDirectory() + "/" + mContext.getPackageName().toString() + "/" + "searchimage");
        deleteFile(file1);

        //  /data/data/<application package>/cache目录
        File file2 = mContext.getCacheDir();
        deleteFile(file2);

        //  /data/data/<application package>/files目录
        //File file3 = mContext.getFilesDir();
        //file3.delete();

        //  SDCard/Android/data/<application package>/cache/
        File file4 = mContext.getExternalCacheDir();
        deleteFile(file4);
        //  SDCard/Android/data/<application package>/files/
        File file5 = mContext.getExternalFilesDir(null);
        deleteFile(file5);

        return true;
    }

    //java中没有直接删除非空目录的方法，所以用递归方法删除文件夹下的所有文件，然后再删除文件夹
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            for (File tmp : file.listFiles()) {
                deleteFile(tmp);
            }
            file.delete();
        } else {
            file.delete();
        }

    }

    //统计缓存大小,单位为 M
    public static double getCacheTotal(Context context) {
        double total;
        File file1 = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName().toString() + "/" + "searchimage");
        File file2 = context.getCacheDir();
        File file4 = context.getExternalCacheDir();
        File file5 = context.getExternalFilesDir(null);

        total = getFileSize(file1) + getFileSize(file2) + getFileSize(file4) + getFileSize(file5);

        return total / (1024 * 1024);
    }

    //获得文件或者文件夹的大小,单位为byte
    public static long getFileSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            for (File tmp : list) {
                size = size + getFileSize(tmp);
            }
        } else {
            size = file.length();
        }

        return size;
    }


    //把本地文件中转换成bitmap
    public static Bitmap getLocalBitmap(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 获得文件存储目录
     */
    public static File getFileStorgeDir(Context context) {
        // 如果有sd卡，则返回sd卡路径
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory();
        } else {
            return context.getFilesDir();
        }
    }

    /**
     * 保存文件到本地
     *
     * @param file
     * @param is
     * @return
     */
    public static File saveFile2Loacl(File file, InputStream is) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] buffer = new byte[128];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }

        return file;
    }


}
