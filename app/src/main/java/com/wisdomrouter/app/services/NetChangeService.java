package com.wisdomrouter.app.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wisdomrouter.app.utils.WarnUtils;

/**
 * Created by Administrator on 2016/6/13 0013.
 */
public class NetChangeService extends Service{
    private static final String tag="tag";
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//                Log.d(tag, "网络状态已经改变");
                connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if(info != null && info.isAvailable()) {
                    String name = info.getTypeName();
//                    WarnUtils.toast(context,"当前网络名称：" + name);
                } else {
                    WarnUtils.toast(context,"网络连接已断开!");
                }
            }
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册网络监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, filter);
    }
    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder {
       public NetChangeService getService() {
            return NetChangeService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    public void excute() {
        System.out.println("通过Binder得到Service的引用来调用Service内部的方法");

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionReceiver); //取消监听
    }
}
