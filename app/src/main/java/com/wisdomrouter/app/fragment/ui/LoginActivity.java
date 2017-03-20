package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.analytics.social.UMSocialService;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.fragment.bean.UserDao.Userinfo;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.TaskHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.CustomDialog;
import com.wisdomrouter.app.utils.MD5Util;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.WarnUtils;


import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class LoginActivity extends BaseActivity {
    // 控件
    @Bind(R.id.login_account)
    EditText nameEdit;
    @Bind(R.id.login_psd)
    EditText passEdit;
    @Bind(R.id.btn_login)
    Button loginBtn;
    @Bind(R.id.zc_text)
    Button regBtn;
    @Bind(R.id.rl_parent)
    RelativeLayout rl_parent;
    @Bind(R.id.btn_pass)
    Button btn_pass;
    @Bind(R.id.img_login_sina)
    ImageView imgSina;
    @Bind(R.id.cb_register_agree)
    CheckBox cvAgree;
    @Bind(R.id.img_login_weixin)
    ImageView imgweixin;

    // 属性
    private GlobalTools globalTool;
    private UserDao voUser;

    private final static int NORMAL_LOGIN = 1;
    private final static int SINA_LOGIN = 2;
    private final static int LOGIN_SUCCESSED = 3;
    private final static int LOGIN_FAILED = 4;
    private final static int LOGIN_SINASUCCESS = 6;
    private final static int REQUEST_TYPE_A = 0;
    private UMSocialService mController;

    static class Handler1 extends TaskHandler<LoginActivity> {


        public Handler1(LoginActivity Activity) {
            super(Activity);
        }

        @Override
        public void onTaskOk(LoginActivity Activity, Message msg) {
            super.onTaskOk(Activity, msg);
            Activity.hideProgressDialog();
            switch (msg.arg1) {
                case LOGIN_SUCCESSED:
                    Activity.loginSuccess();
                    break;
                case LOGIN_SINASUCCESS:
                    Activity.sinaLoginResult();
                    break;
                case SINA_LOGIN:
                    Activity.sinaLogin();
                    break;
            }

        }

        @Override
        public void onTaskON(LoginActivity object, Message msg) {
            super.onTaskON(object, msg);
            switch (msg.arg1) {
                case SINA_LOGIN:
                    object.sinaLogin();
                    break;


            }
        }

        @Override
        public void onTaskFailed(LoginActivity object, Message msg) {
            super.onTaskFailed(object, msg);
            object.hideProgressDialog();
            WarnUtils.toast(object, msg.obj.toString());
        }


    }

    ;

    public Handler1 mHandler = new Handler1(LoginActivity.this);

    UMShareAPI  mShareAPI ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_login);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);
        mShareAPI= UMShareAPI.get( LoginActivity.this );
        ButterKnife.bind(this);
        initTitleBar("登录", 0, null);
        initView();
        intOtherLogin();
    }

    protected void sinaLoginResult() {
        if (voUser != null) {
            if (voUser.getState().equals("3")) {
                if (!StringUtil.isEmpty(voUser.getUserinfo().getKey())) {
                    Bundle bund = new Bundle();
                    bund.putString("key", voUser.getUserinfo().getKey());
                    bund.putString("pic", sinapic);
                    ActivityUtils.to(LoginActivity.this,
                            LoginAccessActivity.class, bund);
                    finish();
                }

            }
            if (voUser.getState().equals("2")) {

                if (voUser.getUserinfo().getMobile() == null) {

                    voUser.getUserinfo().setFacepic(sinapic);
                    HandApplication.user = voUser.getUserinfo();
                    if (HandApplication.user.getFacepic() == null || HandApplication.user.getFacepic().isEmpty()) {
                        HandApplication.user.setFacepic(sinapic);
                    } else {
                        sinapic = HandApplication.user.getFacepic();
                    }
//                    HandApplication.getInstance().mSpUtil
//                            .saveAccount(HandApplication.user);
                    WarnUtils.toast(LoginActivity.this, "登录成功!");
                    bqxx();//补全信息

                } else {
                    WarnUtils.toast(getApplicationContext(), "登录成功!");
                    voUser.getUserinfo().setFacepic(sinapic);
                    HandApplication.user = voUser.getUserinfo();
                    HandApplication.user.setFacepic(sinapic);
                    HandApplication.getInstance().mSpUtil
                            .saveAccount(HandApplication.user);
                    LoginActivity.this.setResult(RESULT_OK);
                    LoginActivity.this.finish();
                }

            } else {
                WarnUtils.toast(getApplicationContext(), voUser.getMessage());
            }
        }

    }

    private void intOtherLogin() {
        mShareAPI.deleteOauth(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
        mShareAPI.deleteOauth(this, SHARE_MEDIA.SINA, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
//        mController = UMServiceFactory.getUMSocialService("com.umeng.login");
//        // 设置新浪SSO handler
//        mController.getConfig().setSsoHandler(new SinaSsoHandler());
//        mController.deleteOauth(this, SHARE_MEDIA.SINA,
//                new SocializeClientListener() {
//                    @Override
//                    public void onStart() {
//                    }
//
//                    /**
//                     * 第三方登录
//                     *
//                     * @param
//                     */
//
//                    @Override
//                    public void onComplete(int status, SocializeEntity entity) {
//                        if (status == 200) {
//                            // Toast.makeText(LoginActivity.this, "删除成功.",
//                            // Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Toast.makeText(LoginActivity.this, "删除失败",
//                            // Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        mController.deleteOauth(this, SHARE_MEDIA.WEIXIN,
//                new SocializeClientListener() {
//                    @Override
//                    public void onStart() {
//                    }
//
//
//                    @Override
//                    public void onComplete(int status, SocializeEntity entity) {
//                        if (status == 200) {
//                            // Toast.makeText(LoginActivity.this, "删除成功.",
//                            // Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Toast.makeText(LoginActivity.this, "删除失败",
//                            // Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//        // 添加微信平台
//        UMWXHandler wxHandler = new UMWXHandler(this, ShareUtils.wxappId, ShareUtils.wxappSecret);
//        wxHandler.addToSocialSDK();

        imgweixin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginWEIXIN();
            }
        });
        imgSina.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginSina();
            }
        });
    }

    Userinfo user;

    /**
     * 初始化数据
     */
    private void initView() {
        globalTool = new GlobalTools(getApplicationContext());
        user = HandApplication.getInstance().mSpUtil.getAccount();
        if (user != null && !StringUtil.isEmpty(user.getLikename())) {
            nameEdit.setText(user.getLikename());
        }

    }


    /**
     * 第三方登录
     *
     * @param
     */

    public void loginWEIXIN() {
    mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> data) {
//        sinaId = data.get("uid").toString();
        mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {

                for (String key : map.keySet()) {
                    com.umeng.socialize.utils.Log.e("xxxxxx key = "+key+"    value= "+map.get(key));
                }
                //微信登录要重点测试，
//                sinaId = map.get("openid").toString();
//                sinaName = map.get("nickname").toString();
//                //当为空的时候会报错，要处理
//                sinapic = map.get("headimgurl").toString();
//                Message msg = new Message();
//                msg.what = Handler1.TASK_OK;
//                msg.arg1 = SINA_LOGIN;
//                mHandler.sendMessage(msg);
                sinaId = map.get("openid").toString();
                sinaName = map.get("screen_name").toString();
//                //当为空的时候会报错，要处理
                sinapic = map.get("profile_image_url").toString();
                Message msg = new Message();
                msg.what = Handler1.TASK_OK;
                msg.arg1 = SINA_LOGIN;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });


    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {

    }
});
//        mController.doOauthVerify(this, SHARE_MEDIA.WEIXIN,
//                new UMAuthListener() {
//                    @Override
//                    public void onStart(SHARE_MEDIA platform) {
//                        // Toast.makeText(LoginActivity.this, "授权开始",
//                        // Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(SocializeException e,
//                                        SHARE_MEDIA platform) {
//                        Toast.makeText(getApplicationContext(), "授权错误",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
//                        sinaId = value.getString("uid");
//                        // 获取相关授权信息
//                        mController.getPlatformInfo(getApplicationContext(),
//                                SHARE_MEDIA.WEIXIN, new UMDataListener() {
//                                    @Override
//                                    public void onStart() {
//                                        // Toast.makeText(LoginActivity.this,
//                                        // "获取平台数据开始...",
//                                        // Toast.LENGTH_SHORT).show();
//                                    }
//
//                                    @Override
//                                    public void onComplete(int status,
//                                                           Map<String, Object> info) {
////                                       WarnUtils.toast(LoginActivity.this, "info===="+info.toString());
//                                        if (status == 200 && info != null) {
//                                            sinaName = info.get("nickname")
//                                                    .toString();
//                                            sinapic = info.get("headimgurl")
//                                                    .toString();
//                                            Message msg = new Message();
//                                            msg.what = Handler1.TASK_OK;
//                                            msg.arg1 = SINA_LOGIN;
//                                            mHandler.sendMessage(msg);
//                                        } else {
//                                            Log.d("TestData", "发生错误：" + status);
//                                        }
//                                    }
//                                });
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA platform) {
//                        Toast.makeText(getApplicationContext(), "授权取消",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    public void loginSina() {
        mShareAPI.doOauthVerify(this, SHARE_MEDIA.SINA, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
                 @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                for (String key : map.keySet()) {
                 com.umeng.socialize.utils.Log.e("xxxxxx key = "+key+"    value= "+map.get(key));
            }
                sinaId = map.get("uid").toString();
                sinaName = map.get("screen_name").toString();
                //当为空的时候会报错，要处理
                sinapic=map.get("profile_image_url");
                Message msg = new Message();
                msg.what = Handler1.TASK_OK;
                msg.arg1 = SINA_LOGIN;
                mHandler.sendMessage(msg);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {

    }
});

            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
        // 登录授权
//        mController.doOauthVerify(this, SHARE_MEDIA.SINA,
//                new UMAuthListener() {
//                    @Override
//                    public void onError(SocializeException e,
//                                        SHARE_MEDIA platform) {
//                    }
//
//                    @Override
//                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
//                        if (value != null
//                                && !TextUtils.isEmpty(value.getString("uid"))) {
//                            sinaId = value.getString("uid");
//                            // 进行数据获取
//                            mController.getPlatformInfo(LoginActivity.this,
//                                    SHARE_MEDIA.SINA, new UMDataListener() {
//                                        @Override
//                                        public void onStart() {
//                                        }
//
//                                        @Override
//                                        public void onComplete(int status,
//                                                               Map<String, Object> info) {
//                                            if (status == 200 && info != null) {
//                                                sinaName = info.get(
//                                                        "screen_name")
//                                                        .toString();
//                                                sinapic = info.get(
//                                                        "profile_image_url")
//                                                        .toString();
//                                                Message msg = new Message();
//                                                msg.what = Handler1.TASK_ON;
//                                                msg.arg1 = SINA_LOGIN;
//                                                mHandler.sendMessage(msg);
//
//                                            } else {
//                                                Log.d("TestData", "发生错误："
//                                                        + status);
//                                            }
//                                        }
//                                    });
//
//                        } else {
//                            WarnUtils.toast(getApplicationContext(), "授权失败.");
//                        }
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA platform) {
//                    }
//
//                    @Override
//                    public void onStart(SHARE_MEDIA platform) {
//                    }
//                });

    }

    @OnClick({R.id.btn_pass, R.id.btn_login,R.id.zc_text})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.btn_pass:
                ActivityUtils.to(LoginActivity.this, ForgetActivity.class);
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.zc_text:
                ActivityUtils.to(this, RegisterActivity.class);
                finish();
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        if (TextUtils.isEmpty(nameEdit.getText().toString().trim())
                || TextUtils.isEmpty(passEdit.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "请输入账号和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        userName = nameEdit.getText().toString().trim();
        userPass = MD5Util.md5Encode(passEdit.getText().toString().trim());

        normalLogin();
    }

    private String userName = null;
    private String userPass = null;

    /**
     * 登录线程
     */
    private void normalLogin() {
        showProgressDialog("登录中..");
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    voUser = globalTool.Login(userName, userPass);
                    msg.arg1 = LOGIN_SUCCESSED;
                    msg.what = Handler1.TASK_OK;
                } catch (Exception e) {
                    msg.obj = "网络异常!";
                    msg.what = Handler1.TASK_FAILED;
                }
                mHandler.sendMessage(msg);
            }

            ;
        }.start();
    }

    private String sinaId = null;
    private String sinaName = null;
    private String sinapic = null;

    /**
     * 第三方登录成功以后,对接后台
     */
    private void sinaLogin() {
        showProgressDialog("登录中..");
        new Thread() {
            public void run() {
                Looper.prepare();
                Message msg = new Message();
                try {
                    //这里出问题了
                    voUser = globalTool.sinaLogin(sinaId, sinaName);
                    msg.what = Handler1.TASK_OK;
                    msg.arg1 = LOGIN_SINASUCCESS;
                } catch (Exception e) {
                    msg.what = Handler1.TASK_FAILED;
                    msg.obj = e.getMessage();
                }
                mHandler.sendMessage(msg);
            }

            ;
        }.start();
    }

    /**
     * 登录成功
     */
    public void loginSuccess() {
        HandApplication.getInstance().mSpUtil.saveLoginPassword(userPass);
        if (voUser != null && !StringUtil.isEmpty(voUser.getState())) {
            if (voUser.getState().equals("0")) {
                WarnUtils.toast(getApplicationContext(), voUser.getMessage() == null ? "登录失败,网络异常"
                        : voUser.getMessage());
            } else if (voUser.getState().equals("1")) {// 进行登录手动授权

                WarnUtils.toast(getApplicationContext(), voUser.getMessage() == null ? "登录成功!请进行授权!"
                        : voUser.getMessage());
                if (!StringUtil.isEmpty(voUser.getKey())) {
                    Bundle bund = new Bundle();
                    bund.putString("key", voUser.getKey());
                    bund.putString("pass", userPass);
                    ActivityUtils.to(LoginActivity.this,
                            LoginAccessActivity.class, bund);

                    finish();
                }

            } else if (voUser.getState().equals("2")) {
                WarnUtils.toast(getApplicationContext(), voUser.getMessage() == null ? "登录成功!"
                        : voUser.getMessage());
                voUser.getUserinfo().setPassword(userPass);
                // 讲基本信息写入handApplication
                HandApplication.user = voUser.getUserinfo();
                if (cvAgree.isChecked()) {
                    HandApplication.autologin = 1;
                    HandApplication.getInstance().mSpUtil.saveAccount(voUser
                            .getUserinfo());
                } else {
                    HandApplication.autologin = 0;

                }
                // 收缩
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(passEdit.getWindowToken(), 0);
                setResult(RESULT_OK);
                finish();
            } else {
                WarnUtils.toast(getApplicationContext(), "网络异常,登录失败");
            }

        } else {
            WarnUtils.toast(getApplicationContext(), "网络异常,登录失败[voUser==null]");
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);        /** 使用SSO授权必须添加如下代码 */
//        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
//                requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
        if (requestCode == REQUEST_TYPE_A) {
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras(); // data为B中回传的Intent
                String state = b.getString("state");
                if (state.equals("1")) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    HandApplication.getInstance().mSpUtil.saveAccount(null);
                    HandApplication.user = null;
                    finish();
                }
            }
        }
        /** 使用SSO授权必须添加如下代码 */
//        UMSsoHandler ssoHandler2 = mController.getConfig().getSsoHandler(
//                requestCode);
//        if (ssoHandler2 != null) {
//            ssoHandler2.authorizeCallBack(requestCode, resultCode, data);
//        }
    }

    /**
     * 补全信息
     *
     * @param
     */
    public void bqxx() {
        CustomDialog.Builder customDialog = new CustomDialog.Builder(
                LoginActivity.this);
        customDialog.setMessage("补全您的信息,便于更好的体验操作哦!");
        customDialog.setTitle("补全信息");
        customDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putString("sinaid", sinaId);
                        bundle.putString("sinaname", sinaName);
                        bundle.putString("sinapic", sinapic);
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this,
                                LoginbxActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, REQUEST_TYPE_A);
                    }
                });
        customDialog.create().show();
    }


}
