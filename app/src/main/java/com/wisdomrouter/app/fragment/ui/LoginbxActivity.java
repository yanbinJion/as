package com.wisdomrouter.app.fragment.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.Utils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.DeleteEditText;
import com.wisdomrouter.app.view.SendValidateButton;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.HashMap;
import java.util.Map;

public class LoginbxActivity extends BaseDetailActivity {
    DeleteEditText edtPass, edtEmial, edtPhone;
    String email = "";
    String phone = "";
    String pass = "";
    @ViewInject(id = R.id.left_btn, click = "exit")
    ImageView leftBtn;
    @ViewInject(id = R.id.title)
    TextView title;
    @ViewInject(id = R.id.yzm)
    EditText yzm;
    @ViewInject(id = R.id.send_button)
    SendValidateButton sendButton;
    private final static int BQXX_SUCCESSED = 1;
    private final static int BQXX_FAILED = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BQXX_SUCCESSED:
                    hideProgressDialog();
                    if (voUser != null) {
                        if (voUser.getState().equals("1")) {
                            HandApplication.user = voUser.getUserinfo();
                            HandApplication.getInstance().mSpUtil
                                    .saveAccount(voUser.getUserinfo());
                            HandApplication.user = voUser.getUserinfo();
                            Intent intent = new Intent(LoginbxActivity.this,
                                    LoginActivity.class);

                            intent.putExtra("mobile", voUser.getUserinfo()
                                    .getMobile());
                            intent.putExtra("password", voUser.getUserinfo()
                                    .getPassword());
                            intent.putExtra("state", "1");

                            setResult(RESULT_OK, intent);

                            LoginbxActivity.this.finish();
                            WarnUtils.toast(LoginbxActivity.this, "补全信息成功!");
                        } else {
                            WarnUtils.toast(LoginbxActivity.this, voUser.getMessage());
                        }
                    }

                    break;
                case BQXX_FAILED:

                    break;

            }
        }
    };
    String yzmStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loginbx);

        title.setText("完善信息");
        Button btn_OK = (Button) findViewById(R.id.btn_OK);

        edtPass = (DeleteEditText) findViewById(R.id.login_pass);
        edtEmial = (DeleteEditText) findViewById(R.id.login_email);
        edtPhone = (DeleteEditText) findViewById(R.id.login_phone);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmial.getText().toString().trim();
                phone = edtPhone.getText().toString().trim();
                pass = edtPass.getText().toString().trim();

                if (TextUtils.isEmpty(edtPhone.getText().toString().trim())) {
                    Toast.makeText(LoginbxActivity.this, "请输入您的手机号!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (yzm.getText().toString().trim() != null
                        && !yzm.getText().toString().trim().equals(yzmStr + "")) {
                    WarnUtils.toast(LoginbxActivity.this, "您输入的验证码不正确!");
                    yzm.requestFocus();
                    return;
                }
                if (phone != null
                        && !phone.equals(edtPhone.getText().toString().trim())) {
                    WarnUtils.toast(LoginbxActivity.this, "手机号修改后,请重新获取验证码!");
                    return;
                }

                if (TextUtils.isEmpty(edtPass.getText().toString().trim())) {
                    Toast.makeText(LoginbxActivity.this, "请设置您的密码!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtPass.getText().toString().trim().length() < 6
                        || edtPass.getText().toString().trim().length() > 12) {
                    Toast.makeText(LoginbxActivity.this, "密码位数6-12,请确认您的输入!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.isEmpty()) {
                    if (!Utils.patternEmail(email)) {
                        Toast.makeText(LoginbxActivity.this, "邮箱格式不正确!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                loginbqxx();
            }
        });

        sendButton.setmListener(new SendValidateButton.SendValidateButtonListener() {

            @Override
            public void onTick() {

            }

            @Override
            public void onClickSendValidateButton() {
                if (TextUtils.isEmpty(edtPhone.getText().toString().trim())) {
                    Toast.makeText(LoginbxActivity.this, "请输入您的手机号!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Utils.isMobileNO(edtPhone.getText().toString().trim())) {
                    Toast.makeText(LoginbxActivity.this, "手机号格式不正确!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                sendButton.startTickWork();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        getRequest2();
                    }
                }).start();
            }

        });
    }

    CommError commError;

    // 2.发送短信
    public void getRequest2() {
        phone = edtPhone.getText().toString();
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {
            @Override
            public void reqSuccess(String response) {
                commError = new Gson().fromJson(response, CommError.class);
                if (commError != null) {
                    if (commError.getState() == 0) {//发送失败
                        WarnUtils.toast(LoginbxActivity.this, commError.getMessage() == null ?
                                "发送验证码失败" : commError.getMessage());
                    } else if (commError.getState() == 1) {//发送成功
                        WarnUtils.toast(LoginbxActivity.this, commError.getMessage() == null ?
                                "发送验证码成功" : commError.getMessage());
                        if (commError.getData() != null && !TextUtils.isEmpty(commError.getData().getVcode()))
                            yzmStr = commError.getData().getVcode();
                        else
                            WarnUtils.toast(LoginbxActivity.this, "发送验证码失败Data==null");
                    }
                }
            }

            @Override
            public void reqError(String error) {
                WarnUtils.toast(LoginbxActivity.this,
                        "发送验证码失败" + error);
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        GlobalTools.getVcode(volleyRequest, map);

    }

    private UserDao voUser;

    // 补全信息
    protected void loginbqxx() {
        showProgressDialog("正在处理...");
        new Thread() {
            public void run() {
                try {
                    voUser = new GlobalTools(LoginbxActivity.this)
                            .LoginBqxx(getIntent()
                                    .getStringExtra("sinaid"), getIntent()
                                    .getStringExtra("sinaname"), phone, pass, email, getIntent()
                                    .getStringExtra("sinapic"));
                    if (voUser != null) {
                        mHandler.sendEmptyMessage(BQXX_SUCCESSED);
                    }
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(BQXX_FAILED);
                    e.printStackTrace();
                }
            }

            ;
        }.start();

    }

    public void exit(View v) {

        Intent intent = new Intent(LoginbxActivity.this, LoginActivity.class);

        intent.putExtra("state", "2");

        setResult(RESULT_OK, intent);

        finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent(LoginbxActivity.this,
                    LoginActivity.class);

            intent.putExtra("state", "2");

            setResult(RESULT_OK, intent);

            finish();
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sendButton != null) {
            sendButton.stopTickWork();
        }
    }
}