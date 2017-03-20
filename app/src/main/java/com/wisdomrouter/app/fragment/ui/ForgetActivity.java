package com.wisdomrouter.app.fragment.ui;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.HttpUtil;
import com.wisdomrouter.app.utils.MD5Util;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.Utils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.DeleteEditText;
import com.wisdomrouter.app.view.SendValidateButton;
import com.wisdomrouter.app.view.SendValidateButton.SendValidateButtonListener;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgetActivity extends BaseDetailActivity implements
        OnClickListener, OnTouchListener, TextWatcher {
    @ViewInject(id = R.id.forget_username)
    DeleteEditText etUserName;
    @ViewInject(id = R.id.forget_code)
    DeleteEditText forgetcode;
    @ViewInject(id = R.id.reset_password)
    DeleteEditText reset_password;
    @ViewInject(id = R.id.forget_send_button)
    SendValidateButton sendButton;
    @ViewInject(id = R.id.forget_bn)
    Button forgetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_forget);
        initview();
    }

    private void initview() {
        globalTool = new GlobalTools(this);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);
        initTitleBar("找回密码", 0, null);
        etUserName.addTextChangedListener(this);
        forgetBtn.setOnClickListener(this);
        sendButton.setmListener(new SendValidateButtonListener() {

            @Override
            public void onTick() {
            }

            @Override
            public void onClickSendValidateButton() {
                if (etUserName.getText().toString().trim().equals("")) {
                    WarnUtils.toast(ForgetActivity.this, "请输入手机号");
                    return;
                }
                if (!Utils.isMobileNO(etUserName.getText().toString().trim())) {
                    WarnUtils.toast(ForgetActivity.this, "手机号格式错误!");
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
    String phone;

    CommError commError;

    // 2.发送短信
    public void getRequest2() {
        phone = etUserName.getText().toString();
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {
            @Override
            public void reqSuccess(String response) {
                commError = new Gson().fromJson(response, CommError.class);
                if (commError != null) {
                    if (commError.getState() == 0) {//发送失败
                        WarnUtils.toast(ForgetActivity.this, commError.getMessage() == null ?
                                "发送验证码失败" : commError.getMessage());
                    } else if (commError.getState() == 1) {//发送成功
                        WarnUtils.toast(ForgetActivity.this, commError.getMessage() == null ?
                                "发送验证码成功" : commError.getMessage());
                        if (commError.getData() != null && !TextUtils.isEmpty(commError.getData().getVcode()))
                            yzmStr = commError.getData().getVcode();
                        else
                            WarnUtils.toast(ForgetActivity.this, "发送验证码失败Data==null");
                    }
                }
            }

            @Override
            public void reqError(String error) {
                WarnUtils.toast(ForgetActivity.this,
                        "发送验证码失败" + error);
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        GlobalTools.getVcode(volleyRequest, map);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.forget_bn:
                break;

            default:
                break;
        }
        return true;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_bn:
                if (PrepareForForget()) {
                    if (phone != null
                            && !phone
                            .equals(etUserName.getText().toString().trim())) {
                        WarnUtils.toast(this, "手机号修改后,请重新获取验证码!");
                        return;
                    }

                    new RegInitTask().execute();
                }
                break;
            default:
                break;
        }

    }

    private UserDao voUser;
    private GlobalTools globalTool;

    private class RegInitTask extends AsyncTask<Void, Integer, String> {
        String pass; // 密码
        String phone; // 手机
        String errorInfo;
        int ERRORCODE = 1;

        @Override
        protected void onPreExecute() {
            // 加载数据等待中
            phone = etUserName.getText().toString().trim();
            // 密码使用MD5 32位小数
            pass = MD5Util
                    .md5Encode(reset_password.getText().toString().trim());
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                voUser = globalTool.ResetPass(phone, pass);
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
                Toast.makeText(ForgetActivity.this, errorInfo,
                        Toast.LENGTH_LONG).show();
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            handlMessage(voUser);
        }
    }

    private void handlMessage(UserDao userDao) {

        if (userDao != null && !StringUtil.isEmpty(userDao.getState())) {

            if (userDao.getState().equals("1")) {
                WarnUtils.toast(
                        ForgetActivity.this,
                        userDao.getMessage() == null ? "修改密码成功！请重新登录" : userDao
                                .getMessage());
                HandApplication.user = userDao.getUserinfo();
                HandApplication.getInstance().mSpUtil.saveAccount(userDao
                        .getUserinfo());
                finish();
            } else if (userDao.getState().equals("-1")) {
                WarnUtils.toast(ForgetActivity.this,
                        userDao.getMessage() == null ? "修改密码失败！请检查重新输入新密码"
                                : userDao.getMessage());
                reset_password.requestFocus();
            } else if (userDao.getState().equals("0")) {
                WarnUtils.toast(
                        ForgetActivity.this,
                        userDao.getMessage() == null ? "用户不存在！" : userDao
                                .getMessage());
                reset_password.requestFocus();
                etUserName.requestFocus();
                finish();
            }
        }
    }

    public boolean PrepareForForget() {
        String code = forgetcode.getText().toString().trim();
        String phone = etUserName.getText().toString();
        if (!StringUtil.isNumberNO(phone)) {
            WarnUtils.toast(this, "请输入正确的手机号");
            etUserName.requestFocus();
            return false;
        }
        if (StringUtil.isEmpty(code)) {
            WarnUtils.toast(this, "请输入验证码");
            forgetcode.requestFocus();
            return false;
        }
        if (code != null && !code.equals(yzmStr + "")) {
            WarnUtils.toast(this, "您输入的验证码不正确!");
            forgetcode.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(reset_password.getText().toString().trim())) {
            WarnUtils.toast(this, "请输入密码!");
            reset_password.requestFocus();
            return false;
        }
        if (!TextUtils.isEmpty(reset_password.getText().toString().trim())
                && reset_password.getText().toString().trim().length() < 6
                || reset_password.getText().toString().trim().length() > 12) {
            WarnUtils.toast(this, "密码长度在6-12位,请确认您的输入!");
            reset_password.requestFocus();
            return false;
        }
        return true;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String userName = etUserName.getText().toString().trim();
        // if (!TextUtils.isEmpty(userName)) {
        // forgetBtn.setEnabled(true);
        // forgetBtn.setBackgroundColor(getResources().getColor(
        // R.color.orange_full));
        // } else {
        // forgetBtn.setBackgroundColor(getResources().getColor(
        // R.color.orange_low));
        // forgetBtn.setEnabled(false);
        // }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
