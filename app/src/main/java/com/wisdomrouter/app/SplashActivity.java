package com.wisdomrouter.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.interfases.EventHandler;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.tools.VolleyHttpRequest;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.CustomDialog;
import com.wisdomrouter.app.utils.NetUtil;
import com.wisdomrouter.app.utils.WarnUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


/**
 * @COMPANY:南京路特软件有限公司
 * @CLASS:SplashActivity
 * @DESCRIPTION:加载页activity
 * @AUTHOR:wangfanghui
 * @VERSION:v1.0
 * @DATE:2015-4-22 下午3:45:28
 */
public class SplashActivity extends BaseDetailActivity implements EventHandler {
    private Context mContext = this;// 上下文
    Gson gson;
    private AppConfigBean appConfigBeanNative;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        getData();

    }

    private void getData() {
        appConfigBeanNative = HandApplication.getInstance().mSpUtil.getAppConfigBean();
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                try {
                    refreshUI(gson.fromJson(response, AppConfigBean.class));
                } catch (Exception e) {
                    WarnUtils.toast(mContext, "解析数据失败" + e.getMessage());
                }

            }

            @Override
            public void reqError(String error) {
                // 失败的回调 ：失败的提醒
                WarnUtils.toast(mContext, "获取数据失败" + error);
            }
        };
        GlobalTools.getAppconfig(volleyRequest);
    }

    private void refreshUI(AppConfigBean appConfigbean) {
        if (appConfigbean != null && !TextUtils.isEmpty(appConfigbean.getState())) {
            if (appConfigbean.getState().equals("1")) {
                appConfigbean = quchong(appConfigbean);
                //当前的配置跟本地不相同保存到本地缓存中
                if (appConfigBeanNative == null || TextUtils.isEmpty(appConfigBeanNative.getConfig_hash()) || !
                        appConfigBeanNative.getConfig_hash().equals(appConfigbean.getConfig_hash())) {
                    HandApplication.getInstance().mSpUtil.saveAppConfigStr(gson.toJson(appConfigbean));
                    appConfigBeanNative = HandApplication.getInstance().mSpUtil.getAppConfigBean();
                    HandApplication.getInstance().setConfig(appConfigBeanNative);
                }
                Const.APPSHAREID = appConfigBeanNative.getId();
                Const.HTTP_HEAD = appConfigBeanNative.getApi_prefix();
                Const.HTTP_HEADKZ = appConfigBeanNative.getApi_root();
                versionRefresh(Integer.parseInt(appConfigBeanNative.getVersioncode() == null ? "1000" : appConfigBeanNative.getVersioncode()));

            } else {
                WarnUtils.toast(mContext, "获取数据失败!" + (appConfigbean.getMessage() == null ? "" : appConfigbean.getMessage()));
            }
        } else {
            WarnUtils.toast(mContext, "获取数据失败!");
        }
    }

    /**
     * 执行更新操作
     *
     * @param serVersionCode
     */
    private void versionRefresh(int serVersionCode) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (appConfigBeanNative.getForce_update() != null
                && appConfigBeanNative.getForce_update().equals("1")
                && loadVersion() < serVersionCode) {
            showUpdate(appConfigBeanNative.getVersionpath(), appConfigBeanNative.getVersionlogs());
        } else {
            openAdv();
        }
    }


    /**
     * 更新广告
     */
    public void openAdv() {

        final Bundle bundle = new Bundle();
        if (appConfigBeanNative.getIndexads() != null
                && appConfigBeanNative.getIndexads().size() > 0
                && appConfigBeanNative.getIndexads().get(0) != null && appConfigBeanNative.getIndexads().get(0).getImage() != null) {
            bundle.putString("key", appConfigBeanNative.getIndexads().get(0)
                    .getImage());
            bundle.putString("url", appConfigBeanNative.getIndexads().get(0).getUrl());
            ActivityUtils.to(mContext, AdvActivity.class,
                    bundle);
            finish();

        } else {
            openHome();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void openHome() {
        ActivityUtils.to(mContext, MainActivity.class);
        finish();
    }


    /**
     * 获取版本号
     *
     * @return
     */
    private int loadVersion() {
        int localVersionCode = 0;
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            if (pinfo != null) {
                localVersionCode = pinfo.versionCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localVersionCode;
    }

    /**
     * 更新提示
     *
     * @param url
     * @param msg
     */
    public void showUpdate(final String url, final String msg) {
        CustomDialog.Builder customDialog = new CustomDialog.Builder(mContext);
        customDialog.setMessage(msg);
        customDialog.setTitle("升级提示");
        customDialog.setPositiveButton("升级到最新版", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                openHtml(url);
//                android.os.Process.killProcess(Process.myPid());
                showProgressDialog("正在更新,请稍后...");
                downFile(url);
            }
        });
        customDialog.setNegativeButton("默默使用旧版", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openAdv();
            }
        });
        customDialog.create().show();
    }

    void downFile(final String url) {
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                path);
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                            }
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    String path = new Date().getTime()+"router.apk";
    Handler handler = new Handler();

    //下载完成，通过handler通知主ui线程将下载对话框取消。
    void down() {
        handler.post(new Runnable() {
            public void run() {
                hideProgressDialog();
                update();
            }
        });
    }

    // 5. 安装应用
    void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), path)),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onNetChange() {
        if (NetUtil.getNetworkState(mContext) == NetUtil.NETWORN_NONE) {
            WarnUtils.toast(mContext, R.string.net_err);
            return;
        }

    }


}