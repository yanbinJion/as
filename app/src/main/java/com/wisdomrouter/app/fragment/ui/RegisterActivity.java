package com.wisdomrouter.app.fragment.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.MD5Util;
import com.wisdomrouter.app.utils.Utils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.DeleteEditText;
import com.wisdomrouter.app.view.SendValidateButton;
import com.wisdomrouter.app.view.SendValidateButton.SendValidateButtonListener;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity {
    @Bind(R.id.nickname)
    EditText nameEdit;
    @Bind(R.id.phone)
    EditText phoneEdit;
    @Bind(R.id.yzm)
    DeleteEditText yzm;
    @Bind(R.id.send_button)
    SendValidateButton sendButton;
    @Bind(R.id.pass)
    EditText passEdit;
    @Bind(R.id.cb_register_agree)
    CheckBox cvAgree;
    @Bind(R.id.tv_register_agree_name)
    TextView tv_register_agree_name;
    @Bind(R.id.btnOK)
    Button btnOK;
    @Bind(R.id.ll_gologin)
    LinearLayout ll_gologin;

    private UserDao voUser;
    private GlobalTools globalTool;
    private String sex = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);
        initTitleBar("注册", 0, null);
        initListener();
    }

    private void initListener() {

        tv_register_agree_name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ActivityUtils.to(RegisterActivity.this, AgreeUsActivity.class);
            }
        });
        ll_gologin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityUtils.to(RegisterActivity.this, LoginActivity.class);
                finish();
            }
        });
        btnOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cvAgree.isChecked()) {
                    reg();
                } else {
                    WarnUtils.toast(RegisterActivity.this, "您尚未同意注册条款哦!");
                }

            }
        });
        sendButton.setmListener(new SendValidateButtonListener() {

            @Override
            public void onTick() {
            }

            @Override
            public void onClickSendValidateButton() {

                if (!Utils.isMobileNO(phoneEdit.getText().toString().trim())) {
                    WarnUtils.toast(RegisterActivity.this, "手机号格式错误!");
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

    String yzmStr;


    /**
     * 注册
     */
    private void reg() {
        if (TextUtils.isEmpty(nameEdit.getText().toString().trim())) {
            WarnUtils.toast(this, "请输入昵称!");
            nameEdit.requestFocus();
            return;
        }
        if (nameEdit.getText().toString().trim().length() > 10) {
            WarnUtils.toast(this, "昵称长度不能大于10");
            nameEdit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passEdit.getText().toString().trim())) {
            WarnUtils.toast(this, "请输入密码!");
            passEdit.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(passEdit.getText().toString().trim())
                && passEdit.getText().toString().trim().length() < 6
                || passEdit.getText().toString().trim().length() > 12) {
            WarnUtils.toast(this, "密码长度在6-12位,请确认您的输入!");
            passEdit.requestFocus();
            return;
        }
        if (yzm.getText().toString().trim() != null
                && !yzm.getText().toString().trim().equals(yzmStr + "")) {
            WarnUtils.toast(this, "您输入的验证码不正确!");
            yzm.requestFocus();
            return;
        }
        if (phone != null
                && !phone.equals(phoneEdit.getText().toString().trim())) {
            WarnUtils.toast(this, "手机号修改后,请重新获取验证码!");
            return;
        }
        globalTool = new GlobalTools(this);
        new RegInitTask().execute();
    }

    private class RegInitTask extends AsyncTask<Void, Integer, String> {
        String name; // 用户名
        String pass; // 密码
        String phone; // 手机
        String errorInfo;
        int ERRORCODE = 1;

        @Override
        protected void onPreExecute() {
            // 加载数据等待中
            name = nameEdit.getText().toString().trim();
            // 密码使用MD5 32位小数
            pass = MD5Util.md5Encode(passEdit.getText().toString().trim());
            phone = phoneEdit.getText().toString().trim();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                voUser = globalTool.Register(name, pass, "", phone, "");
            } catch (Exception e) {
                e.printStackTrace();
                errorInfo = e.getMessage();
                publishProgress(ERRORCODE);
            } catch (Throwable t) {
                throw new RuntimeException("An error occured while executing "
                        + "doInBackground()", t);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (ERRORCODE == values[0]) {
                Toast.makeText(RegisterActivity.this, errorInfo,
                        Toast.LENGTH_LONG).show();
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (voUser != null) {
                if (voUser.getState().equals("0")) {
                    if (voUser.getErrors() != null) {
                        if (voUser.getErrors().getLikename() != null
                                && voUser.getErrors().getLikename()[0] != null) {
                            WarnUtils.toast(RegisterActivity.this, voUser
                                    .getErrors().getLikename()[0]);
                        } else if (voUser.getErrors().getEmail() != null
                                && voUser.getErrors().getEmail()[0] != null) {
                            WarnUtils.toast(RegisterActivity.this, voUser
                                    .getErrors().getEmail()[0]);
                        } else if (voUser.getErrors().getMobile() != null
                                && voUser.getErrors().getMobile()[0] != null) {
                            WarnUtils.toast(RegisterActivity.this, voUser
                                    .getErrors().getMobile()[0]);
                        } else {
                            WarnUtils.toast(RegisterActivity.this, voUser
                                    .getMessage() == null ? "注册失败,网络异常"
                                    : voUser.getMessage());
                        }

                    } else {
                        WarnUtils.toast(RegisterActivity.this,
                                voUser.getMessage() == null ? "注册失败,网络异常"
                                        : voUser.getMessage());
                    }

                } else if (voUser.getState().equals("1")) {// 注册成功,需要登录时手动授权,进入登录页
                    WarnUtils.toast(
                            RegisterActivity.this,
                            voUser.getMessage() == null ? "注册失败,网络异常" : voUser
                                    .getMessage());
                    finish();
                } else if (voUser.getState().equals("2")) {
                    // 写入全局变量
                    voUser.getUserinfo().setPassword(pass);
                    HandApplication.user = voUser.getUserinfo();
                    HandApplication.getInstance().mSpUtil.saveAccount(voUser
                            .getUserinfo());
                    WarnUtils.toast(
                            RegisterActivity.this,
                            voUser.getMessage() == null ? "注册成功" : voUser
                                    .getMessage());
                    if (voUser.getScore()!=0){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                Toast toast = Toast.makeText(getApplicationContext(),
//                                        ("注册成功  + "+voUser.getScore()+""), Toast.LENGTH_SHORT);
//
//                                toast.setGravity(Gravity.CENTER, 0, 0);
//                                LinearLayout toastView = (LinearLayout) toast.getView();
//                                ImageView imageCodeProject = new ImageView(getApplicationContext());
//                                imageCodeProject.setImageResource(R.drawable.my_score_ic);
//                                toastView.addView(imageCodeProject, 0);
//                                toast.show();
//                               WarnUtils.toast(CommentActivity.this, "发表评论"+resultDao.getScore());
                                WarnUtils.toast(RegisterActivity.this,"注册成功",voUser.getScore());
                            }
                        },1000);}

                    finish();
                }
            }
            super.onPostExecute(result);
        }
    }

    String phone;
    CommError commError;
    Handler mHandler=new Handler();

    // 2.发送短信
    public void getRequest2() {
        phone = phoneEdit.getText().toString();
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {
            @Override
            public void reqSuccess(String response) {
                commError = new Gson().fromJson(response, CommError.class);
                if (commError != null) {
                    if (commError.getState() == 0) {//发送失败
                        WarnUtils.toast(RegisterActivity.this, commError.getMessage() == null ?
                                "发送验证码失败" : commError.getMessage());
                    } else if (commError.getState() == 1) {//发送成功
                        WarnUtils.toast(RegisterActivity.this, commError.getMessage() == null ?
                                "发送验证码成功" : commError.getMessage());
                        if (commError.getData() != null && !TextUtils.isEmpty(commError.getData().getVcode()))
                            yzmStr = commError.getData().getVcode();
                        else
                            WarnUtils.toast(RegisterActivity.this, "发送验证码失败Data==null");
                    }
                }
            }

            @Override
            public void reqError(String error) {
                WarnUtils.toast(RegisterActivity.this,
                        "发送验证码失败" + error);
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        GlobalTools.getVcode(volleyRequest, map);

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (sendButton != null) {
            sendButton.stopTickWork();
        }
    }
}
