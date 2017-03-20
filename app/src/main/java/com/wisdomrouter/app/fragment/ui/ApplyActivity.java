package com.wisdomrouter.app.fragment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.Utils;
import com.wisdomrouter.app.utils.WarnUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class ApplyActivity extends BaseDetailActivity {
    @ViewInject(id = R.id.apply_name)
    EditText apply_name;
    @ViewInject(id = R.id.apply_mobile)
    EditText apply_mobile;
    @ViewInject(id = R.id.apply_remark)
    EditText apply_remark;
    @ViewInject(id = R.id.apply_ok)
    Button apply_ok;

    private CommError sendEvent;
    FinalHttp fh;
    Gson gson = new Gson();
    String key, urlPost;
    String name = "";
    String phone = "";
    String remark = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_apply);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        initTitleBarForLeft("我要报名");

        apply_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(apply_name.getText().toString().trim())) {
                    Toast.makeText(ApplyActivity.this, "请输入您的姓名!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(apply_mobile.getText().toString().trim())) {
                    Toast.makeText(ApplyActivity.this, "请输入您的手机号!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(apply_remark.getText().toString().trim())) {
                    Toast.makeText(ApplyActivity.this, "请输入备注!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Utils.isMobileNO(apply_mobile.getText().toString().trim())) {
                    Toast.makeText(ApplyActivity.this, "手机号格式不正确!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (apply_name != null && apply_mobile != null
                        && apply_remark != null) {
                    sendEvent();

                    finish();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null) {
            key = getIntent().getStringExtra("key");
        }
        urlPost = Const.HTTP_HEADKZ + Const.REGBAO_URL;
    }

    private void sendEvent() {
        if (StringUtil.isEmpty(apply_remark.getText().toString())) {
            WarnUtils.toast(this, "报名备注不可为空!");
            apply_remark.requestFocus();
            return;
        }
        name = apply_name.getText().toString().trim();
        phone = apply_mobile.getText().toString().trim();
        remark = apply_remark.getText().toString().trim();
        FinalHttp fh = new FinalHttp();
        fh.addHeader("TOKEN", Const.APPTOKEN);
        AjaxParams params = new AjaxParams();
        String note = "姓名：" + name + "  手机号：" + phone + "  备注：" + remark;
        params.put("user_openid", HandApplication.user.getOpenid());
        params.put("info_key", key);
        params.put("note", note);
        fh.post(urlPost, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
            }

            @Override
            public void onSuccess(Object t) {
                try {
                    sendEvent = gson.fromJson(t.toString(), CommError.class);
                    if (sendEvent != null) {

                        if (sendEvent.getState() == 0
                                && sendEvent.getErrors() != null
                                && sendEvent.getErrors().getInfo_key() != null
                                && sendEvent.getErrors().getInfo_key().length != 0) {
                            String meg = "";
                            meg = sendEvent.getErrors().getInfo_key()[0];
                            WarnUtils.toast(ApplyActivity.this, meg);
                        } else {
                            WarnUtils.toast(ApplyActivity.this,
                                    sendEvent.getMessage());
                        }

                        if (sendEvent.getState() == 1) {
                            Intent intent = new Intent();
                            if (sendEvent.getCount() != null) {
                                intent.putExtra("COUNT", sendEvent.getCount());
                                setResult(RESULT_OK, intent);
                            }

                            WarnUtils.toast(ApplyActivity.this,
                                    sendEvent.getMessage());

                            if (sendEvent.getScore()!=0)
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        WarnUtils.toast(ApplyActivity.this,"报名成功!",sendEvent.getScore());
                                    }
                                },1000);


                        }
                    }
                } catch (Exception e) {
                    WarnUtils.toast(ApplyActivity.this,
                            "数据获取解析异常,请稍后进入!" + e.toString());
                    return;
                }

            }

        });
    }
    Handler mHandler=new Handler();
}
