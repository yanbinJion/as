package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.TabEntity;
import com.wisdomrouter.app.view.CommonTabLayoutCustom;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VisionFragment extends Fragment {
    @Bind(R.id.tab)
    SegmentTabLayout tab;
    @Bind(R.id.realcontent)
    FrameLayout realcontent;
    private View mVision;
    /**
     * 页面title list
     **/
    List<AppConfigBean.MediaMoudels> mediaMoudelsList = new ArrayList<>();

    public static VisionFragment getInstance(List<AppConfigBean.MediaMoudels> mediaMoudelsList) {
        VisionFragment sf = new VisionFragment();
        sf.mediaMoudelsList = mediaMoudelsList;
        return sf;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mVision) {
            mVision = inflater.inflate(R.layout.fragment_vision, null);
        }
        ButterKnife.bind(this, mVision);
        return mVision;
    }

    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentList.clear();
        mTabEntities.clear();
        String titles[] = new String[mediaMoudelsList.size()];
        for (int i = 0; i < mediaMoudelsList.size(); i++) {
            mTabEntities.add(new TabEntity(mediaMoudelsList.get(i).getTitle()));
            titles[i] = mediaMoudelsList.get(i).getTitle();
            if (mediaMoudelsList.get(i).getClassname().equals(Const.HOME_API.IMAGE))
                fragmentList.add(ImagesFragment.getInstance(mediaMoudelsList.get(i).getChannel()));
            if (mediaMoudelsList.get(i).getClassname().equals(Const.HOME_API.VIDEO))
                fragmentList.add(VideoFragment.getInstance(mediaMoudelsList.get(i).getChannel(),true));

        }
//        for (AppConfigBean.MediaMoudels mediaMoudels : mediaMoudelsList) {
//
//            if (mediaMoudels.getClassname().equals(Const.HOME_API.IMAGE))
//            fragmentList.add(ImagesFragment.getInstance(mediaMoudels.getChannel()));
//            if (mediaMoudels.getClassname().equals(Const.HOME_API.VIDEO))
//            fragmentList.add(VideoFragment.getInstance(mediaMoudels.getChannel()));
//
//        }
        tab.setTabData(titles, getActivity(), R.id.realcontent, fragmentList);
//        tab.setTabData(mTabEntities,this.getActivity(),R.id.realcontent,fragmentList);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
