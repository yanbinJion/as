package com.wisdomrouter.app.fragment;

import java.util.ArrayList;
import java.util.List;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;

public class VisionFragment2 extends Fragment implements OnClickListener {
    private View mVision;
    /**
     * 页面title list
     **/
    List<String> titleList = new ArrayList<String>();
    FragmentManager fragmentManager;
    FrameLayout fl_content;
    TextView tv_img, tv_video;
    private ImagesFragment imagesFragment;
    private VideoFragment videoFragment;
    /**
     * 页面title list
     **/
    List<AppConfigBean.MediaMoudels> mediaMoudelsList = new ArrayList<>();

    public static VisionFragment2 getInstance(List<AppConfigBean.MediaMoudels> mediaMoudelsList) {
        VisionFragment2 sf = new VisionFragment2();
        sf.mediaMoudelsList = mediaMoudelsList;
        return sf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mVision) {
            mVision = inflater.inflate(R.layout.fragment_vision2, null);
        }
        return mVision;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fl_content = (FrameLayout) mVision.findViewById(R.id.fl_content);
        tv_img = (TextView) mVision.findViewById(R.id.tv_img);
        tv_img.setOnClickListener(this);
        tv_video = (TextView) mVision.findViewById(R.id.tv_video);
        tv_video.setOnClickListener(this);
        fragmentManager = getFragmentManager();
        setTabSelection(0);
    }

    @SuppressLint({"NewApi", "ResourceAsColor"})
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                tv_img.setTextColor(Color.WHITE);
                tv_video.setTextColor(Color.parseColor("#333333"));
                tv_img.setBackgroundResource(R.drawable.shape_rectangle_lanmu);
                tv_video.setBackground(null);
//                if (imagesFragment == null) {
                // 如果MessageFragment为空，则创建一个并添加到界面上
                if (mediaMoudelsList.get(0).getClassname().equals(Const.HOME_API.IMAGE))
                    imagesFragment = ImagesFragment.getInstance(mediaMoudelsList.get(0).getChannel());
                else
                    imagesFragment = ImagesFragment.getInstance(mediaMoudelsList.get(1).getChannel());
                transaction.add(R.id.fl_content, imagesFragment);
//                } else {
//                    // 如果MessageFragment不为空，则直接将它显示出来
//                    transaction.show(imagesFragment);
//                }
                break;
            case 1:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                tv_img.setTextColor(Color.parseColor("#333333"));
                tv_video.setTextColor(Color.WHITE);
                tv_video.setBackgroundResource(R.drawable.shape_rectangle_lanmu);
                tv_img.setBackground(null);
//                if (videoFragment == null) {
                // 如果ContactsFragment为空，则创建一个并添加到界面上
                if (mediaMoudelsList.get(1).getClassname().equals(Const.HOME_API.VIDEO))
                    videoFragment = VideoFragment.getInstance(mediaMoudelsList.get(1).getChannel());
                else
                    videoFragment = VideoFragment.getInstance(mediaMoudelsList.get(0).getChannel());
                transaction.add(R.id.fl_content, videoFragment);
//                } else {
//                    // 如果ContactsFragment不为空，则直接将它显示出来
//                    transaction.show(videoFragment);
//                }
                break;

        }
        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        tv_img.setTextColor(Color.RED);
        tv_video.setTextColor(Color.WHITE);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (imagesFragment != null) {
            transaction.hide(imagesFragment);
        }
        if (videoFragment != null) {
            transaction.hide(videoFragment);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_img:
                setTabSelection(0);
                break;
            case R.id.tv_video:
                setTabSelection(1);
                break;
            default:
                break;
        }

    }
}
