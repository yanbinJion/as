package com.wisdomrouter.app.fragment.ui;

import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.CustomDialog;
import com.wisdomrouter.app.utils.DataCleanManager;
import com.wisdomrouter.app.utils.SharePreferenceUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.R;

import net.tsz.afinal.annotation.view.ViewInject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends BaseDetailActivity {
    @ViewInject(id = R.id.btn_exit, click = "exit")
    Button btn_exit;
    @ViewInject(id = R.id.txt_version)
    TextView txt_version;
    @ViewInject(id = R.id.txt_hc)
    TextView txt_hc;
    @ViewInject(id = R.id.rl_help, click = "help")
    RelativeLayout rl_help;
    @ViewInject(id = R.id.rl_clean, click = "clean")
    RelativeLayout rl_clean;
    @ViewInject(id = R.id.rl_update, click = "update", longClick = "versionLog")
    RelativeLayout rl_update;
    String version, versionName, currVersion;
    boolean isShowImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_setting);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        initTitleBarForLeft("设置");
        isShowImg = HandApplication.getInstance().mSpUtil.getIsShowImg();
        version = loadVersion();
        txt_version.setText("当前版本:"
                + (versionName == null ? "V1.0" : versionName));
        try {
            txt_hc.setText(DataCleanManager
                    .getTotalCacheSize(SettingActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void help(View v) {
        ActivityUtils.to(this, HelpActivity.class);
    }

    public void clean(View v) {
        try {
            SharePreferenceUtil.cleartitle();
            DataCleanManager.clearAllCache(SettingActivity.this);
            txt_hc.setText(DataCleanManager
                    .getTotalCacheSize(SettingActivity.this));
            WarnUtils.toast(SettingActivity.this, "清除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            WarnUtils.toast(SettingActivity.this, "清除成功!" + e.getMessage());
        }
    }

    public void update(View v) {
        currVersion = HandApplication.getInstance().appConfigBean.getVersioncode() != null ? HandApplication.getInstance().appConfigBean
                .getVersioncode() : "1000";
        if (HandApplication.getInstance().appConfigBean.getForce_update() != null
                && HandApplication.getInstance().appConfigBean.getForce_update().equals("1")
                && Integer.parseInt(version) < Integer.parseInt(currVersion)) {// 执行更新操作
            showUpdate(HandApplication.getInstance().appConfigBean.getVersionpath(),
                    HandApplication.getInstance().appConfigBean.getVersionlogs());
        } else {
            WarnUtils.toast(this, "当前是最新版本!");
        }
    }

    public void versionLog(View v) {
        ActivityUtils.to(SettingActivity.this, VersionActivity.class);
    }

    /**
     * 退出登录
     *
     * @param v
     */
    public void exit(View v) {
        exitLogin();
    }

    private void exitLogin() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                SettingActivity.this);
        builder.setTitle("注销");
        builder.setMessage("确定要注销个人用户信息吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                HandApplication.user = null;
                HandApplication.getInstance().mSpUtil.saveAccount(null);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private String loadVersion() {
        int localVersionCode = 1000;
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            if (pinfo != null) {
                localVersionCode = pinfo.versionCode;
                versionName = pinfo.versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(localVersionCode);
    }

    /**
     * 更新提示
     *
     * @param url
     * @param msg
     */
    public void showUpdate(final String url, final String msg) {
        CustomDialog.Builder customDialog = new CustomDialog.Builder(this);
        customDialog.setMessage(msg);
        customDialog.setTitle("升级提示");
        customDialog.setPositiveButton("升级到最新版", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openHtml(url);
                android.os.Process.killProcess(Process.myPid());
            }
        });
        customDialog.setNegativeButton("默默使用旧版", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customDialog.create().show();
    }
}
