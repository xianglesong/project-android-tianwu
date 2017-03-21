package com.xianglesong.logcollector.utils;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LogUploadUtil {
    private static final String TAG = "LogUploadUtil";
    private static final int TIME_OUT = 5000;

    /**
     * 日志文件上传到服务器
     * @param requestUrl 服务器url
     * @param file 文件
     * @return
     */
    public static boolean uploadFile2Server(String requestUrl, File file) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        if (file != null) {
            try {
                URL url = new URL(requestUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                connection.setReadTimeout(TIME_OUT);
                connection.setConnectTimeout(TIME_OUT);

                // 设置http连接属性
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; "
                        + "name=\"file1\";filename=\"" + String.valueOf(System.currentTimeMillis()) + file.getName() + "\"" + end);
                dos.writeBytes(end);


                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = fis.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }

                dos.writeBytes(end);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                fis.close();
                dos.flush();

                // 取得Response内容
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return true;
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }
}
