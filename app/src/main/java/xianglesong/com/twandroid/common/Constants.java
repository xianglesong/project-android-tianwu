package xianglesong.com.twandroid.common;

public class Constants {

    // app的不同开发模式，local developer，online
    public static final int APPMODE_LOCAL = 0;
    public static final int APPMODE_DEVELOPER = 1;
    public static final int APPMODE_ONLINE = 2;
    public static int APPMODE = 0;

    public static String APPS_HOME = "";

    public static final String REST_DEFALUT_VERSION = "1.0";

    //区别不同的frament
    public static final int Frag_Tuijian = 0;
    public static final int Frag_Search = 1;
    public static final int Frag_Profile = 2;

    //服务器端的地址
    public static String INDEX_HOME = "http://www.91tianwu.com/";
    public static String TAOBAO_HOME = "http://91tianwu.taobao.com/";
    public static String SEARCH_HOME = "http://search.91tianwu.com/";
    public static String SEARCH_PAGE = "http://search.91tianwu.com/searchm?qparam=";

    // SplashActivity加载的图片的本地地址
    // 文件目录
    public static String LOCAL_IMAGE_DIR;

    // 图片名字
    public static String OPEN_IMAGENAME;

    // 服务器端的图片地址
    public static String SPLASH_IMAGE_URL = "http://apps.91tianwu.com/splash/splash.png";

    //网上图片延时下载时间
    public static int DELAY_TIME;

    // 用户登陆的url
    public static String USER_URL;

    // 服务器端可以返回搜索框文本内容的地址
    public static String UPDATE_SEARCH_CONTENT_URL = "http://apps.91tianwu.com/apps?action=hotSearchText";

    // 服务器端获得最新版本号的地址
    public static String VERSION_UPDATE_URL = "http://apps.91tianwu.com/apps?versionUpdateInfo";

    // 微信appid
    // public static final String WX_APP_ID = "local";
    // public static final String WX_APP_ID = "local"; // local
    // public static final String WX_APP_ID = "test"; // online
    public static final String WX_APP_ID = "online"; // online

    // 微信appsecret
    // public static final String WX_APP_SECRET = "37b83943015be9746eb0c8391d3535d3";
    // public static final String WX_APP_SECRET = "eaf5471bbd66bb3a4d9f3c00dc3fb399"; // local
    // public static final String WX_APP_SECRET = "3a8c2eae28d892286fd063b8a4ac0d8c"; // online
    public static final String WX_APP_SECRET = "online"; // online

    // 记录app配置的sharedPrefrences的属性
    // 文件名
    public static final String SETTINGS_FILE_NAME = "my_settings";
    // 是否第一次安装的key
    public static final String INSTALLED_KEY = "isFirstInstalled";

    // app选择的模式的key
    public static final String APP_MODE_KEY = "appmode";

    // 搜索框默认显示文本的key
    public static final String SEARCH_CONENT_KEY = "searchcontent";
    // 搜索框显示的文本
    public static String SEACH_CONTENT_VALUE = "搜一搜";

    //版本号的key，用来保存最新更新的版本号，如果大于当前package版本号，则代表最新版本已经下载到本地，可以立即更新
    public static final String NEW_VERSION_CODE_KEY = "new_version_code";

    // 用户是否登陆的key
    public static final String IS_LOGGED_KEY = "is_logged";

    // 用户名的key
    public static final String USER_NAME_KEY = "username";

    //用户的unionid的key
    public static final String USER_ID_KEY = "userid";

    //用户是否登陆
    public static boolean isLogged = false;

    // 判断是否已经因为登陆而响应
    public static boolean isRefreshByLogIn = false;

    //用户名
    public static String username = null;

    //用户id
    public static String userid = null;


    // JPush传递intent的extra，要与jpush保持一致
    public static final String Extra_Key = "extra_key";

    // 最新版本号（下载到本地的apk的版本号）
    public static int NEW_VERSION_CODE = 0;
    // apk的名字
    public static String APK_NAME = "tianwu.apk";

    // 调用相机的requestcode
    public static final int CAMERA_REQUEST_CODE = 100;

}
