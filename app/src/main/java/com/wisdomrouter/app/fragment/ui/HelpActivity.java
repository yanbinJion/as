package com.wisdomrouter.app.fragment.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import net.tsz.afinal.annotation.view.ViewInject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.HttpUtil;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.DeleteEditText;
import com.wisdomrouter.app.R;

public class HelpActivity extends BaseDetailActivity implements TextWatcher {
    @ViewInject(id = R.id.et_help_phone)
    DeleteEditText etHelpPhone;
    @ViewInject(id = R.id.et_help_content)
    EditText etHelpContent;
    @ViewInject(id = R.id.help_btn)
    Button btnCommit;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    hideProgressDialog();
                    handlerMessage(commError);
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_help);
        ActivityUtils.addAct(this);
        initView();
    }

    private void initView() {
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        initTitleBarForLeft("帮助与反馈");

        etHelpContent.addTextChangedListener(this);
        btnCommit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (HandApplication.user != null
                        && !StringUtil.isEmpty(HandApplication.user
                        .getLikename())) {
                    postData();
                } else {
                    WarnUtils.toast(HelpActivity.this, "请先登录");
                    ActivityUtils.to(HelpActivity.this, LoginActivity.class);
                }

            }
        });
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (etHelpContent.getText().toString().length() == 200) {
            Toast.makeText(HelpActivity.this, "输入长度不能大于200字", Toast.LENGTH_LONG)
                    .show();
        }
    }

    String strFromServer = null;
    String url;
    Boolean isGet = false;
    CommError commError;

    private void postData() {
        String phone = etHelpPhone.getText().toString().trim();
        String content = etHelpContent.getText().toString().trim();
        if (prepareCommit(phone, content)) {
            url = Const.HTTP_HEADKZ + "/plugin/yhfk-api/post";
            final Map<String, String> map = new HashMap<String, String>();
            map.put("user_openid", HandApplication.user.getOpenid());
            map.put("username", HandApplication.user.getLikename());
            if (phone.isEmpty()) {
                phone = HandApplication.user.getMobile() == null ? ""
                        : HandApplication.user.getMobile();
            }
            if (phone.isEmpty()) {
                phone = HandApplication.user.getEmail() == null ? ""
                        : HandApplication.user.getEmail();
            }

            map.put("contact", phone);
            map.put("content", content);
            showProgressDialog("正在提交...");
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        strFromServer = HttpUtil.doPost(url, map);
                        isGet = true;
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                        strFromServer = e.getMessage();
                        isGet = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        strFromServer = e.getMessage();
                        isGet = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        strFromServer = e.getMessage();
                        isGet = false;
                    }
                    if (strFromServer != null && isGet) {
                        commError = new Gson().fromJson(strFromServer,
                                CommError.class);
                    } else {
                        commError = new CommError();
                        commError.setState(0);
                        commError.setMessage(strFromServer + "");
                    }
                    handler.sendEmptyMessage(1);
                }
            }).start();
        }
    }

    private boolean prepareCommit(String phone, String content) {
        if (TextUtils.isEmpty(content)) {
            WarnUtils.toast(this,"内容不能为空哦");
            etHelpContent.requestFocus();
            return false;
        }
        return true;
    }

    private void handlerMessage(CommError response) {

        if (response != null) {
            if (response.getMessage() != null) {
                WarnUtils.toast(HelpActivity.this, response.getMessage());
                int state = response.getState();
                if (state == 1) {
                    WarnUtils.toast(this, response.getMessage());
                    clearInfoMessage();
                } else if (state == 0) {
                    WarnUtils.toast(this, response.getMessage());
                } else {
                    WarnUtils.toast(this, "提交失败，请检查重新填写！");
                }
            }
        } else {
            WarnUtils.toast(this, "提交失败，请检查重新填写！");
        }
    }

    private void clearInfoMessage() {
        etHelpPhone.setText("");
        etHelpContent.setText("");
        etHelpPhone.clearFocus();
        etHelpContent.clearFocus();
    }

}
