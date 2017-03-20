package com.wisdomrouter.app;

import com.wisdomrouter.app.fragment.ActivesFragment;
import com.wisdomrouter.app.fragment.ArticleFragment;
import com.wisdomrouter.app.fragment.ArticleImageFragment;
import com.wisdomrouter.app.fragment.CountryFragment;
import com.wisdomrouter.app.fragment.CustomFragment;
import com.wisdomrouter.app.fragment.FragmentArtOnly;
import com.wisdomrouter.app.fragment.FragmentDiscover;
import com.wisdomrouter.app.fragment.ImagesFragment;
import com.wisdomrouter.app.fragment.LiveFragment;
import com.wisdomrouter.app.fragment.ProjectFragment;
import com.wisdomrouter.app.fragment.VideoFragment;
import com.wisdomrouter.app.fragment.VisionFragment;
import com.wisdomrouter.app.fragment.VisionFragment2;
import com.wisdomrouter.app.fragment.WebviewFragment;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.interfases.DialogControl;
import com.wisdomrouter.app.utils.DialogHelper;
import com.wisdomrouter.app.view.ProgressDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

public class BaseFragment extends Fragment implements DialogControl {
    private ProgressDialog _waitDialog;
    private boolean _isVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _isVisible = true;
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
                _waitDialog = DialogHelper.getProgressDialog(getActivity(), text);
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
                _waitDialog = DialogHelper.getProgressDialog(getActivity(), text, type);
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
     *
     * @param classname
     * @param moudel
     * @return
     */
    public Fragment getFragment(String classname, AppConfigBean.Module moudel) {
        Fragment fragment = null;
        if (classname.equals(Const.HOME_API.ARTICALE)) {
            if (!TextUtils.isEmpty(moudel.getListmodel()) && moudel.getListmodel().equals("images")){
                fragment = ArticleImageFragment.getInstance(moudel.getChannel());
            }
            else {fragment = FragmentArtOnly.getInstance(moudel.getChannel());
            }
        }else if (classname.equals(Const.HOME_API.IMAGE)) {
            fragment = ImagesFragment.getInstance(moudel.getChannel());
        }else if (classname.equals(Const.HOME_API.VIDEO)) {
            fragment = VideoFragment.getInstance(moudel.getChannel());
        }else if (classname.equals(Const.HOME_API.ONEPAGE)) {
            Bundle bundle = new Bundle();
            bundle.putString("url", moudel.getPlusdata() == null ? "" : (moudel.getPlusdata().getLink() == null ? "" : moudel.getPlusdata().getLink()));
            fragment = new WebviewFragment();
            fragment.setArguments(bundle);
        } else if (classname.equals(Const.HOME_API.DISCOVER))
            fragment = new FragmentDiscover();
        else if (classname.equals(Const.HOME_API.MEDIA)) //多媒体
            fragment = VisionFragment2.getInstance(moudel.getMedia_modules());
        else if (classname.equals(Const.HOME_API.PLUGIN)) {//扩展应用
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
}
