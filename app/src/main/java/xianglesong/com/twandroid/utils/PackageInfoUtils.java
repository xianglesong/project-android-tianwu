package xianglesong.com.twandroid.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class PackageInfoUtils {
    private static final String TAG = "PackageInfoUtils";

    public static int getVersionCode(Context context) {
        int code = 0;
        try {
            code = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return -1;
        }
        return code;
    }
}
