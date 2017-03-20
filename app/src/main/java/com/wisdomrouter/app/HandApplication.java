package com.wisdomrouter.app;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.testin.agent.TestinAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.wisdomrouter.app.Const.HOME_API;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.fragment.ui.ContentWebviewActivity;
import com.wisdomrouter.app.fragment.ui.EventDetailActivity;
import com.wisdomrouter.app.interfases.EventHandler;
import com.wisdomrouter.app.utils.ImageOpUtils;
import com.wisdomrouter.app.utils.NetUtil;
import com.wisdomrouter.app.utils.SharePreferenceUtil;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class HandApplication extends Application {

    private static HandApplication instance;
    public static ArrayList<EventHandler> mListeners = new ArrayList<EventHandler>();
    private static String NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static HandApplication mApplication;


    public SharePreferenceUtil mSpUtil;
    public static int mNetWorkState;
    // 用户信息
    public static UserDao.Userinfo user;
    // 数据库
    public static FinalDb finalDB = null;
    // vote postion
    public static int voteId = 0;
    //    public static AppConfigDao appConfigDao = null;// 配置文件
    public AppConfigBean appConfigBean = null;// 配置文件
    public static int ordersize, degree;
    public static Map<String, String> videoDeatil;
    public static int autologin = 1;
    public static PushAgent mPushAgent;

    public static final String CALLBACK_RECEIVER_ACTION = "callback_receiver_action";

    public static IUmengRegisterCallback mRegisterCallback;

    public static IUmengUnregisterCallback mUnregisterCallback;

    /**
     * 01. 建立  请求队列
     * 02. 将 请求队列 加入到 AndroidMain.xml中
     * 03.
     */

    private static RequestQueue queue;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        queue = Volley.newRequestQueue(getApplicationContext());
//        Const.APP_DEBUG = true;
        Const.APP_DEBUG = false;
        initFinalDB();
        initImageLoader(this);
        initData();
        if (autologin == 0) {
            getInstance().mSpUtil.saveAccount(null);
            user = null;
        } else if (autologin == 1) {
            user = mSpUtil.getAccount();
        }
        if (user != null && user.getLikename() != null
                && !"".equals(user.getLikename())) {
            TestinAgent.setUserInfo(user.getLikename());
        }
        TestinAgent.setLocalDebug(false);// 设置为true，则在log中打印崩溃堆栈
        UMShareAPI.get(this);
        Config.REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
        initUMPush();

        // 注册网络监听
        IntentFilter filter = new IntentFilter(NET_CHANGE_ACTION);
        registerReceiver(netChangeReceiver, filter);

    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        //微信
        PlatformConfig.setWeixin("wx77c67572617d300d", "71f467a0dc025beadae242ecb27b5385");
        //新浪微博
        PlatformConfig.setSinaWeibo("4104124861", "35a6112ed50e44e51c35b1447b682af4");
        //QQ
        PlatformConfig.setQQZone("1104573104", "DjkKX7AR78TeTdJa");

    }

    public void setConfig(AppConfigBean appConfigBean) {
        this.appConfigBean = appConfigBean;
    }

    public AppConfigBean getConfig() {
        if (appConfigBean == null) {
            return mSpUtil.getAppConfigBean();
        }
        return appConfigBean;
    }

    public static HandApplication getInstance() {
        return instance;
    }

    /**
     * 推送消息处理
     */
    private void initUMPush() {
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        // 多条消息不会重复提醒
        mPushAgent.setMuteDurationSeconds(1);
        // 通知栏如何展示最新的一条
        mPushAgent.setMergeNotificaiton(true);

        /**
         * 该Handler是在IntentService中被调用，故 1.
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK 2.
         * IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
         * 或者可以直接启动Service
         * */
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context,
                                              final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        UTrack.getInstance(getApplicationContext())
                                .trackMsgClick(msg);
                    }
                });
            }

            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                context);
                        RemoteViews myNotificationView = new RemoteViews(
                                context.getPackageName(),
                                R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title,
                                msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text,
                                msg.text);
                        myNotificationView.setImageViewBitmap(
                                R.id.notification_large_icon,
                                getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(
                                R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        builder.setContent(myNotificationView);
                        builder.setAutoCancel(true);
                        Notification mNotification = builder.build();
                        // 由于Android
                        // v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                        mNotification.contentView = myNotificationView;
                        return mNotification;
                    default:
                        // 默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                // if (mSpUtil.getAppHead() != null) {
                String info_class = null;
                String info_title = null;
                String info_desc = null;
                String info_pic = null;
                String keyValue = null;
                // 用户点开了通知
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                for (Entry<String, String> entry : msg.extra.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (key != null && key.equals("info_key")) {
                        intent.putExtra("key", value);
                        keyValue = value;
                    }
                    if (key != null && key.equals("info_class")) {
                        info_class = value;
                    }
                    if (key != null && key.equals("title")) {
                        info_title = value;
                    }
                    if (key != null && key.equals("desc")) {
                        info_desc = value;
                    }
                    if (key != null && key.equals("indexpic")) {
                        info_pic = value;
                    }
                }

                intent.putExtra("key", keyValue);
                intent.putExtra("title", "详情");
                intent.putExtra("sharetitle", info_title);
                intent.putExtra("indexpic", info_pic);
                if (info_class != null) {
                    if (info_class.equals(HOME_API.ARTICALE)) {
                        String url;
                        url = Const.HTTP_HEADKZ + "/app/multimedia/article?key=" + keyValue;
                        intent.putExtra("content_api", "/article/content");
                        intent.putExtra("url", url);
                        intent.putExtra("type", "article");
                        intent.setClass(context, ContentWebviewActivity.class);
                        startActivity(intent);
                    } else if (info_class.equals(HOME_API.IMAGE)) {
                        intent.putExtra("content_api", "/images/content");
                        String url;
                        url = Const.HTTP_HEADKZ + "/app/multimedia/images?key=" + keyValue;
                        intent.putExtra("url", url);
                        intent.putExtra("type", "images");
                        intent.setClass(context, ContentWebviewActivity.class);
                        startActivity(intent);
                    } else if (info_class.equals(HOME_API.VIDEO)) {
                        intent.putExtra("content_api", "/video/content");
                        String url;
                        url = Const.HTTP_HEADKZ + "/app/multimedia/video?key=" + keyValue;
                        intent.putExtra("url", url);
                        intent.putExtra("type", "video");
                        intent.setClass(context, ContentWebviewActivity.class);
                        startActivity(intent);
                    } else if (info_class.equals(HOME_API.ACTIVITY)) {
                        intent.putExtra("activityId", keyValue);
                        intent.setClass(context, EventDetailActivity.class);
                        startActivity(intent);
                    }
                    ((Activity) context).overridePendingTransition(
                            R.anim.slide_left_in, R.anim.slide_right_out);
                }
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        mRegisterCallback = new IUmengRegisterCallback() {

            @Override
            public void onRegistered(String registrationId) {
                Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
                sendBroadcast(intent);
            }

        };
        mPushAgent.setRegisterCallback(mRegisterCallback);

        mUnregisterCallback = new IUmengUnregisterCallback() {

            @Override
            public void onUnregistered(String registrationId) {
                Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
                sendBroadcast(intent);
            }
        };
        mPushAgent.setUnregisterCallback(mUnregisterCallback);

    }

    private void initFinalDB() {
        finalDB = FinalDb.create(this, Const.DB_NAME);
    }

    private void initData() {
        mApplication = this;
        mNetWorkState = NetUtil.getNetworkState(this);
        mSpUtil = new SharePreferenceUtil(this,
                SharePreferenceUtil.CITY_SHAREPRE_FILE);
        appConfigBean = getConfig();

    }

    /**
     * 图片加载选项
     *
     * @param context
     */
    private void initImageLoader(Context context) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(ImageOpUtils.getImgOptions())
                .build();
        ImageLoader.getInstance().init(configuration);
    }


    BroadcastReceiver netChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(NET_CHANGE_ACTION)) {
                if (mListeners.size() > 0)// 通知接口完成加载
                    for (EventHandler handler : mListeners) {
                        handler.onNetChange();
                    }
            }
            mNetWorkState = NetUtil.getNetworkState(mApplication);
        }

    };


    //入口
    public static RequestQueue getQueue() {
        return queue;
    }

}
