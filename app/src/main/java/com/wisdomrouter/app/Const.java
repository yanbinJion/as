package com.wisdomrouter.app;


public class Const {

    //app_debug = false 说明是正式环境
    //app_debug = true 说明是测试环境
    public static boolean APP_DEBUG = true;

    public static String APP_DIR = "wisdomrouter/";
    //token正式数据
    public static String APPTOKEN = "8e523cf73be561731c69ee923ee7d0f0";//正式Android
    //测试token
//    public static String APPTOKEN = "e598073ba381d4d0be52481fe7ed68dd";//测试android

//    shareID
    public static String APPSHAREID = null;
    //接口前缀
    public static String HTTP_HEAD = "https://open.tmtsp.com/app";
    //测试前缀
//    public static String HTTP_HEAD = "http://open.rmt.test.routeryuncs.com/app";
    //接口根
    public static String HTTP_HEADKZ = "https://open.tmtsp.com";
//    测试接口根
//    public static String HTTP_HEADKZ = "http://open.rmt.test.routeryuncs.com";
    //正式config xml
    public static String HTTP_CONFIG = "https://open.tmtsp.com/app/main/config";
    //测试config
//    public static String HTTP_CONFIG = "http://open.rmt.test.routeryuncs.com/app/main/config";
    //正式config Json
    public static String HTTP_CONFIG_JSON = "https://open.tmtsp.com/app/main/config?format=json";
    //测试config Json
//    public static String HTTP_CONFIG_JSON = "http://open.rmt.test.routeryuncs.com/app/main/config?format=json";


// 正式分享前缀
    public static String SHARE_URL = "http://fenxiang.tmtsp.com/";

//    测试分享前缀
//  public static String SHARE_URL = "http://fenxiang.rmt.test.routeryuncs.com/";

    public static String SHARE_APP_URL = "http://fenxiang.tmtsp.com/";


    public static final String LIVECONTENT_URL = "/plugin/weilive-api/live";
    //活动
    public static final String REGLIST_URL = "/plugin/activity-api/list";
    public static final String REGBAO_URL = "/plugin/activity-api/post";
    public static String DB_NAME = "news.db";
    public static int MAX_SELECT_PICS = 6;


    //收藏
    public static final String NOCOLLECT = "NOCOLLECT";
    public static final String COLLECT = "COLLECT";
    public static final String ZAN = "ZAN";


    /**
     * class类别
     */
    public class HOME_API {
        public static final String ACTIVITY = "activity";
        public static final String IMAGE = "images";
        public static final String VIDEO = "video";
        public static final String ARTICALE = "article";
        public static final String ONEPAGE = "onepage";
        public static final String PLUGIN = "plugin";
        public static final String DISCOVER = "discover";
        public static final String MEDIA = "media";

    }
    /**
     * share类别
     */
    public class SHARE_API {
        public static final String FRIEND = "friend";
        public static final String ARTICLE = "article";
        public static final String PROJECT = "project";
        public static final String COUNTRY = "country";
        public static final String AVTIVITY = "activity";
        public static final String IMVITE = "imvite";
        public static final String VOTES = "votes";
        public static final String LIVES = "live";
        public static final String VOTE_BEHAVIOR = "share_votes";
        public static final String ACTIVITY_BEHAVIOR = "share_activities";


    }

    public static final String PROJECT = "PROJECT";
    public static final String TOWN = "TOWN";
    public static final String TOWNNEWS = "TOWNNEWS";
    public static final String CUSTOM = "CUSTOM";
    public static final String FOCUS = "FOCUS";
    public static final String LIVELIST = "LIVELIST";


}
