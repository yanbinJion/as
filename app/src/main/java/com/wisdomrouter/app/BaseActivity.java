package com.wisdomrouter.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.wisdomrouter.app.fragment.ActivesFragment;
import com.wisdomrouter.app.fragment.ArticleFragment;
import com.wisdomrouter.app.fragment.ArticleImageFragment;
import com.wisdomrouter.app.fragment.CountryFragment;
import com.wisdomrouter.app.fragment.CustomFragment;
import com.wisdomrouter.app.fragment.FragmentDiscover;
import com.wisdomrouter.app.fragment.ImagesFragment;
import com.wisdomrouter.app.fragment.LiveFragment;
import com.wisdomrouter.app.fragment.ProjectFragment;
import com.wisdomrouter.app.fragment.VideoFragment;
import com.wisdomrouter.app.fragment.VisionFragment2;
import com.wisdomrouter.app.fragment.WebviewFragment;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.interfases.DialogControl;
import com.wisdomrouter.app.utils.DialogHelper;
import com.wisdomrouter.app.utils.NetUtil;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.ProgressDialog;


public class BaseActivity extends FragmentActivity implements
        DialogControl {
    private ProgressDialog _waitDialog;
    private boolean _isVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _isVisible = true;
        // 统计启动次数test
        PushAgent.getInstance(this).onAppStart();
        // 闪退次数github仓库有变化
        MobclickAgent.updateOnlineConfig(this);
    }


    /**
     * 初始化标题
     */
    public void initTitleBar(String title, int rightId, OnClickListener onclick) {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.VISIBLE);
        if (rightId != 0)
            rightButton.setImageResource(rightId);
        if (onclick != null)
            rightButton.setOnClickListener(onclick);

    }


    @Override
    public void hideProgressDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public ProgressDialog showProgressDialog() {
        return showProgressDialog("加载中...");
    }

    @Override
    public ProgressDialog showProgressDialog(int resid) {
        return showProgressDialog(getString(resid));
    }

    @Override
    public ProgressDialog showProgressDialog(String text) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelper.getProgressDialog(this, text);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(text);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    @Override
    public ProgressDialog showProgressDialog(String text, String type) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelper.getProgressDialog(this, text, type);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(text);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    /**
     * fragment 获取
     * @param classname
     * @param moudel
     * @return
     */
    public Fragment getFragment(String classname, AppConfigBean.Module moudel) {
        Fragment fragment = null;
        if (classname.equals(Const.HOME_API.ARTICALE)){
            if (!TextUtils.isEmpty(moudel.getListmodel()) && moudel.getListmodel().equals("images")) {
                fragment = ArticleImageFragment.getInstance(moudel.getChannel());
            }else {
                fragment = ArticleFragment.getInstance(moudel.getChannel());
            }
    } else if (classname.equals(Const.HOME_API.IMAGE)) {
            fragment = ImagesFragment.getInstance(moudel.getChannel());
        }else if (classname.equals(Const.HOME_API.VIDEO)) {
            fragment = VideoFragment.getInstance(moudel.getChannel());
        }else if (classname.equals(Const.HOME_API.ONEPAGE)) {
            Bundle bundle = new Bundle();
            bundle.putString("url", moudel.getPlusdata() == null ? "" : (moudel.getPlusdata().getLink() == null ? "" : moudel.getPlusdata().getLink()));
            fragment = new WebviewFragment();
            fragment.setArguments(bundle);
        } else if (classname.equals(Const.HOME_API.DISCOVER))
            fragment =new FragmentDiscover();
        else if (classname.equals(Const.HOME_API.MEDIA)) //多媒体
            fragment = VisionFragment2.getInstance(moudel.getMedia_modules());
            //扩展应用
        else if (classname.equals(Const.HOME_API.PLUGIN)) {
            String plusdata = moudel.getPlusdata() == null ? "" : (moudel.getPlusdata().getPlugin_source() == null ? "" : moudel.getPlusdata().getPlugin_source());
            if (!TextUtils.isEmpty(plusdata)) {
                String[] plusArray = plusdata.split("\\|");
                String title = plusArray[0];
                if (title.contains("直播"))
                    fragment = new LiveFragment();
                else if (title.contains("自定义列表"))
                    fragment = CustomFragment.getInstance(plusArray[1]);
                else if (title.contains("专题"))
                    fragment = new ProjectFragment();
                else if (title.contains("镇区"))
                    fragment = new CountryFragment();
                else if (title.contains("活动"))
                    fragment = new ActivesFragment();
                else {
//                    WarnUtils.toast(getActivity(), "您暂未配置");
                }
            } else {
//                WarnUtils.toast(getActivity(), "您暂未配置");
            }
        }
        return fragment;
    }
    public String wrapHtml(String content) {
        // 需要做处理，因为pre会保持样式不变
        if (content != null) {
            content = content.replaceAll("<pre>", "<p>");
            content = content.replaceAll("</pre>", "</p>");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><head><title></title>");
        builder.append("<meta name=\"viewport\" content=\"initial-scale=1.0, maximum-scale=1.0,width=device-width,user-scalable=no\">");
        builder.append("<style>");

        // 判断字体大小
        String font_size = "18";
        builder.append("p{font-size:" + font_size
                + "px;color:#555;line-height:20px;}");
        builder.append(" img{display:block;margin: 0 auto;max-width: 100%;}");
        builder.append("</style>");
        builder.append("</head>");
        builder.append("<body><div id=\"app_content\" >");// ;width=\"100%\"
        builder.append(content == null ? "" : content);
        builder.append("</div></body></html>");

        return builder.toString();
    }
}
