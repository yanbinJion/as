package com.wisdomrouter.app.fragment.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.WarnUtils;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

/**
 * @COMPANY:南京路特软件有限公司
 * @CLASS:LoginAccessActivity
 * @DESCRIPTION:登录授权
 * @AUTHOR:wangfanghui
 * @VERSION:v1.0
 * @DATE:2015-5-6 下午5:00:32
 */
public class LoginAccessActivity extends FinalActivity {
    @ViewInject(id = R.id.btnOK, click = "btnAccess")
    Button btnOK;
    private UserDao voUser;
    private String key;
    private GlobalTools globalTool;
    private final static int LOGIN_GETKEY = 1;
    private final static int LOGIN_GETKRYSUCCESSED = 2;
    private final static int LOGIN_GETKRYFAIL = 3;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case LOGIN_GETKRYSUCCESSED:
                    updateUI();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_login_access);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        initTitleBar();
//		LightnessControl.SetLightness(this, SharePreferenceUtil.getLightness());
        if (getIntent().getExtras() == null) {
            WarnUtils.toast(LoginAccessActivity.this,
                    "网络异常 [accessbundle==null]");
            return;
        }
        key = getIntent().getExtras().getString("key");
        if (StringUtil.isEmpty(key)) {
            WarnUtils.toast(LoginAccessActivity.this, "网络异常 [accesskey==null]");
            return;
        }
    }

    /**
     * @description:key结果处理
     * @author:wangfanghui
     * @return:void
     */
    protected void updateUI() {
        if (voUser != null && !StringUtil.isEmpty(voUser.getState())) {
            if (voUser.getState().equals("0")) {
                WarnUtils.toast(this, voUser.getMessage() == null ? "授权失败,网络异常"
                        : voUser.getMessage());
            } else if (voUser.getState().equals("1")) {
                WarnUtils.toast(this, voUser.getMessage() == null ? "成功授权!"
                        : voUser.getMessage());
                voUser.getUserinfo().setPassword(getIntent().getExtras().getString("pass"));
                HandApplication.user = voUser.getUserinfo();
                HandApplication.user.setFacepic(getIntent().getStringExtra("pic"));
                HandApplication.getInstance().mSpUtil.saveAccount(HandApplication.user);
                ActivityUtils.to(LoginAccessActivity.this,
                        PersonActivity.class);
                finish();
            } else {
                WarnUtils.toast(LoginAccessActivity.this,
                        "网络异常,授权失败 [state==504]");
            }
        } else {
            WarnUtils.toast(LoginAccessActivity.this,
                    "网络异常,授权失败 [voUser==null]");
        }
    }

    /**
     * @param v
     * @description:授权
     * @author:wangfanghui
     * @return:void
     */
    public void btnAccess(View v) {
        if (globalTool == null) {
            globalTool = new GlobalTools(this);
        }
        new Thread() {
            public void run() {
                try {
                    voUser = globalTool.LoginGetkey(key);
                    mHandler.sendEmptyMessage(LOGIN_GETKRYSUCCESSED);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(LOGIN_GETKRYFAIL);
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    /**
     * 设置左右标签,专门设置title_bar
     */
    private void initTitleBar() {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText("授权登录");
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.GONE);
    }

}
