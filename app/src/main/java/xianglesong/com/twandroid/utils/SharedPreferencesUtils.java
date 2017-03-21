package xianglesong.com.twandroid.utils;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SharedPreferencesUtils extends ContextWrapper {
    private static final String TAG = "SharedPreferencesUtils";

    SharedPreferences sp;

    SharedPreferences.Editor edit;

    public SharedPreferencesUtils(Context base) {
        super(base);

    }

    public void init() {

    }

    public void setSharePerferences(String name, String key, String value) {
        sp = this.getSharedPreferences(name, Context.MODE_PRIVATE);
        edit = sp.edit();
        edit.putString(key, value);
        Log.i(TAG, "key:" + key + " value:" + value);
        edit.commit();
    }

    public String getSharePerferences(String name, String key) {
        SharedPreferences sharedata = getSharedPreferences(name, 0);
        String data = sharedata.getString(key, null);
        Log.i(TAG, "data:" + data);
        return data;
    }

}
