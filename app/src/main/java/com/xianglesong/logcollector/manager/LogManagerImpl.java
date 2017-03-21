
package com.xianglesong.logcollector.manager;

import android.content.Context;
import android.util.Log;


import com.xianglesong.logcollector.task.LogTask;
import com.xianglesong.logcollector.utils.LogFileUtils;
import com.xianglesong.logcollector.utils.LogUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class LogManagerImpl implements ILogManager, UncaughtExceptionHandler {
    private static final String TAG = "LogManagerImpl";
    private final static int EXECUTOR_HANDLE_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;

    private Context mContext;
    private ExecutorService mExecutorService = null;
    // The available numbers of threads in executor service.
    private int mAvailableProcessors = 1;

    private static Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    static {
        mDefaultUncaughtExceptionHandler = Thread
                .getDefaultUncaughtExceptionHandler();
    }

    LogManagerImpl(Context context) {
        mContext = context;
        mAvailableProcessors = Runtime.getRuntime().availableProcessors();
    }

    private void checkExecutor() {
        if (mExecutorService == null || mExecutorService.isShutdown()) {
            if (mAvailableProcessors < 0) {
                mAvailableProcessors = 1;
            }
            mExecutorService = Executors.newFixedThreadPool(
                    mAvailableProcessors, new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread t = new Thread(r);
                            t.setPriority(EXECUTOR_HANDLE_THREAD_PRIORITY);
                            t.setName(TAG);
                            return t;
                        }
                    });
        }
    }

    private void submitTask(final Runnable runnable) {
        checkExecutor();
        mExecutorService.submit(runnable);
    }

    @Override
    public boolean registerCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        return true;
    }

    @Override
    public boolean unregisterCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(mDefaultUncaughtExceptionHandler);
        return true;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            printWriter.append(ex.getMessage());
            ex.printStackTrace(printWriter);
            Log.getStackTraceString(ex);
            // If the exception was thrown in a background thread inside
            // AsyncTask, then the actual exception can be found with getCause.
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            String msg = result.toString();
            printWriter.close();

            LogFileUtils.save2File(mContext, LogUtil.getFormatLog(null, msg, LogUtil.TYPE_ERROR));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
    }

    @Override
    public void log(String tag, String msg, int logType) {
        submitTask(new LogTask(mContext, tag, msg, logType));
    }

}
