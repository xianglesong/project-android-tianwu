
package com.xianglesong.logcollector.manager;

public interface ILogManager {
    /**
     * @param tag
     * @param msg
     * @param logType Type of the Log, can be LogUtil.TYPE_ERROR, LogUtil.TYPE_ACTIVITY, LogUtil.TYPE_SEACH.
     */
    public void log(String tag, String msg, int logType);

    /**
     * Register crash handler to handle exception.
     *
     * @return
     */
    public boolean registerCrashHandler();

    /**
     * Unregister crash handler to handle exception.
     *
     * @return
     */
    public boolean unregisterCrashHandler();


}
