package com.xianglesong.logcollector.utils;

/**
 * 把log信息格式化成固定的格式，用来保存在文件里
 */
public class LogUtil {

    // crash信息
    public static final int TYPE_ERROR = 0;
    // Activity发出的log
    public static final int TYPE_ACTIVITY = 1;
    // Search发出的log
    public static final int TYPE_SEACH = 2;
    // Fragment发出的log
    public static final int TYPE_FRAGMENT = 3;

    private static final String SEPARATOR = "\001";  //一条信息里的分隔符

    private static final String LINE_SEPARATOR = "\n";//换行符，多条信息直接的分隔符

    private static final String LOG = "Log";
    private static final String ERROR = "Error";
    private static final String ACTIVITY = "Activity";
    private static final String SEARCH = "Search";
    private static final String FRAGMENT = "Fragment";

    // 手机唯一标识
    public static String IMEI = null;
    
    public static String UserID = null;

    /**
     * @param tag
     * @param msg
     * @param logType log的类型，有TYPE_ERROR，TYPE_ACTIVITY，TYPE_SEACH , TYPE_FRAGMENT
     * @return
     */
    public static String getFormatLog(String tag, String msg, int logType) {
        switch (logType) {
            case TYPE_ERROR:
                return formatLogForError(msg);
            case TYPE_ACTIVITY:
                return formatLogForActivity(tag, msg);
            case TYPE_SEACH:
                return formatLogForSearch(msg);
            case TYPE_FRAGMENT:
                return formatLogForFragment(tag, msg);
            default:
                return null;
        }
    }

    /**
     * 返回格式化的activity的log信息，格式为：[Log]\001[Activity]\001[IMEI]\001[UserID]\001[Time]\001[ActivityName]\001[State]
     * 其中不包括[]，这里只是为了便于理解
     * ActivityName是Activity的名字，state是Activity的状态，只能是open和close的一种
     *
     * @param activityName
     * @param state
     * @return
     */
    public static String formatLogForActivity(String activityName, String state) {
        StringBuilder sb = new StringBuilder();
        sb.append(LOG);
        sb.append(SEPARATOR);
        sb.append(ACTIVITY);
        sb.append(SEPARATOR);
        sb.append(IMEI);
        sb.append(SEPARATOR);
        sb.append(UserID);
        sb.append(SEPARATOR);
        sb.append(Utils.getFromatDate("yyyy-MM-dd HH:mm:ss"));
        sb.append(SEPARATOR);
        sb.append(activityName);
        sb.append(SEPARATOR);
        sb.append(state);
        sb.append(LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * 返回格式化的search的log信息，格式为：[Log]\001[Search]\001[IMEI]\001[UserID]\001[Time]\001[searchcontent]
     * 其中不包括[]，这里只是为了便于理解
     *
     * @param searchContent 搜索内容
     * @return
     */
    public static String formatLogForSearch(String searchContent) {
        StringBuilder sb = new StringBuilder();
        sb.append(LOG);
        sb.append(SEPARATOR);
        sb.append(SEARCH);
        sb.append(SEPARATOR);
        sb.append(IMEI);
        sb.append(SEPARATOR);
        sb.append(UserID);
        sb.append(SEPARATOR);
        sb.append(Utils.getFromatDate("yyyy-MM-dd HH:mm:ss"));
        sb.append(SEPARATOR);
        sb.append(searchContent);
        sb.append(LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * 返回格式化的crash的log信息，格式为：[Error]\001[IMEI]\001[UserID]\001[Time]\001[CrashText]
     *
     * @param errorMsg
     * @return
     */
    public static String formatLogForError(String errorMsg) {
        errorMsg = errorMsg.replaceAll("\\n", "\t");
        StringBuilder sb = new StringBuilder();
        sb.append(ERROR);
        sb.append(SEPARATOR);
        sb.append(IMEI);
        sb.append(SEPARATOR);
        sb.append(UserID);
        sb.append(SEPARATOR);
        sb.append(Utils.getFromatDate("yyyy-MM-dd HH:mm:ss"));
        sb.append(SEPARATOR);
        sb.append(errorMsg);
        sb.append(LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * 返回格式化的fragment的log信息，格式为：[Log]\001[Activity]\001[IMEI]\001[UserID]\001[Time]\001[ActivityName]\001[State]
     * 其中不包括[]，这里只是为了便于理解
     * ActivityName是Activity的名字，state是Activity的状态，只能是open和close的一种
     *
     * @param fragmentName
     * @param state
     * @return
     */
    public static String formatLogForFragment(String fragmentName, String state) {
        StringBuilder sb = new StringBuilder();
        sb.append(LOG);
        sb.append(SEPARATOR);
        sb.append(FRAGMENT);
        sb.append(SEPARATOR);
        sb.append(IMEI);
        sb.append(SEPARATOR);
        sb.append(UserID);
        sb.append(SEPARATOR);
        sb.append(Utils.getFromatDate("yyyy-MM-dd HH:mm:ss"));
        sb.append(SEPARATOR);
        sb.append(fragmentName);
        sb.append(SEPARATOR);
        sb.append(state);
        sb.append(LINE_SEPARATOR);
        return sb.toString();
    }

}
