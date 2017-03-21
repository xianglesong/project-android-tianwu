
package com.xianglesong.logcollector.manager;

import android.content.Context;

import com.xianglesong.logcollector.utils.LogUtil;
import com.xianglesong.logcollector.utils.Utils;

public class LogManager {

    private static ILogManager iLogManager;

    /**
     * Get singleton manager. DO NOT GET MANAGER BY THE CONTEXT OF ACTIVITY.
     *
     * @param context The context of application.
     * @return
     */
    private synchronized static ILogManager getManager(Context context) {
        if (context == null) {
            return null;
        }

        if (iLogManager == null) {
            synchronized (ILogManager.class) {
                if (iLogManager == null) {
                    iLogManager = new LogManagerImpl(context);
                }
            }
        }

        return iLogManager;
    }

    /**
     * 自定义日志注册
     *
     * @param context
     */
    public static void register(Context context) {
        getManager(context).registerCrashHandler();
        // 获取手机唯一标识
        LogUtil.IMEI = Utils.getIMEI(context);

        // TODO init the id of user by login here
        // LogUtil.UserID = ?
    }

    /**
     * 程序关闭时注销
     *
     * @param context
     */
    public static void unregister(Context context) {
        getManager(context).unregisterCrashHandler();
    }

    /**
     * @param tag     打印的类名
     * @param msg     要打印的消息
     * @param logType log的类型，有LogUtil.TYPE_ERROR，LogUtil.TYPE_ACTIVITY，LogUtil.TYPE_SEACH , LogUtil.TYPE_FRAGMENT
     */
    public static void log(String tag, String msg, int logType) {
        if (iLogManager == null) {
            return;
        }
        iLogManager.log(tag, msg, logType);
    }

}
