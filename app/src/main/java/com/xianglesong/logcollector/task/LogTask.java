
package com.xianglesong.logcollector.task;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.xianglesong.logcollector.utils.LogFileUtils;
import com.xianglesong.logcollector.utils.LogUtil;

public class LogTask implements Runnable {
    private Context mContext;
    private String mTag;
    private String mMsg;
    private int mType = -1;

    /**
     * @param context
     * @param tag
     * @param msg
     * @param logType
     */
    public LogTask(Context context, String tag, String msg, int logType) {
        mContext = context;
        mMsg = msg;
        mTag = tag;
        mType = logType;
    }

    @Override
    public void run() {
        if (mContext == null || TextUtils.isEmpty(mMsg)
                || TextUtils.isEmpty(mTag)) {
            return;
        }
        Log.d(mTag, mMsg);
        LogFileUtils.save2File(mContext, LogUtil.getFormatLog(mTag, mMsg, mType));
    }

}


