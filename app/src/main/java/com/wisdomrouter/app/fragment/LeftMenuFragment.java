package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.wisdomrouter.app.BaseFragment;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.MainActivity;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.ui.LoginActivity;
import com.wisdomrouter.app.fragment.ui.PersonActivity;
import com.wisdomrouter.app.fragment.ui.SearchActivity;
import com.wisdomrouter.app.fragment.ui.SettingActivity;
import com.wisdomrouter.app.fragment.ui.WebviewActivity;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeftMenuFragment extends BaseFragment {

    private ListView leftList;
    private View mMenu;
    private CircleImageView iv_login;
    private TextView tx_login;
    private LinearLayout ll_setting, ll_search;
    SimpleAdapter simpleAdapter;
    RequestManager requestManager;

    public interface closeDrawerLayoutListener {
        void close();
    }

    private closeDrawerLayoutListener listener;

    public void setOnCloseDrawerLayoutListener(closeDrawerLayoutListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestManager = Glide.with(this.getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mMenu == null) {
            mMenu = inflater.inflate(R.layout.fragment_left_menu, container,
                    false);
        }
        initView();
        return mMenu;
    }

    private void initView() {
        leftList = (ListView) mMenu.findViewById(R.id.leftList);
        iv_login = (CircleImageView) mMenu.findViewById(R.id.iv_login);
        tx_login = (TextView) mMenu.findViewById(R.id.tx_login);
        ll_setting = (LinearLayout) mMenu.findViewById(R.id.ll_setting);
        ll_search = (LinearLayout) mMenu.findViewById(R.id.ll_search);
        ll_setting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityUtils.to(getActivity(), SettingActivity.class);
            }
        });
        ll_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityUtils.to(getActivity(), SearchActivity.class);
            }
        });
        initData();
        simpleAdapter = new SimpleAdapter(getActivity(), listData, R.layout.fragment_left_menu_item, new String[]{"title", "id"}, new int[]{R.id.title, R.id.id});
        leftList.setAdapter(simpleAdapter);
        leftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Fragment fragment=null;
                TextView  txttitle = (TextView) view.findViewById(R.id.title);
                String className = listData.get(position).get("classname").toString();
                if (position == 0&&appConfigBean.getApp_main_btns()!=null&&appConfigBean.getApp_main_btns().size()>0) //第一个进入home 底部有自定义按钮
                {
                    HomeFragment mainFragment = new HomeFragment();
                    List<AppConfigBean.Module> list = appConfigBean.getApp_main_btns();

                    int pos = 0;
                    boolean isExites = false;
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (appConfigBean.getModules().get(0).getModulekey().equals(list.get(i).getModulekey())) {
                                pos = i;
                                isExites = true;
                                break;
                            }
                        }
                    }
                    fragment = mainFragment;
                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", pos);
                    bundle.putBoolean("isfromleft", true);
                    bundle.putBoolean("isExites", isExites);
                    fragment.setArguments(bundle);
                    mainFragment.setOnChangeTitleListener(new HomeFragment.OnChangeTitleListener() {
                        @Override
                        public void ChangeTitle(String s) {
                            MainActivity fca = (MainActivity) getActivity();
                            if (fca != null) {
                                fca.initTitleBar(s);
                            }
                        }
                    });
                    if (listener != null) {
                        listener.close();
                    }
                }
             else if(className.equals(Const.HOME_API.ONEPAGE))

            {

                if((appConfigBean.getModules()==null)||(appConfigBean.getModules()
                        .get(position)==null)|| (appConfigBean.getModules()
                        .get(position).getPlusdata()==null)||appConfigBean.getModules()
                        .get(position).getPlusdata().getLink()==null){
                    WarnUtils.toast(getActivity(),"您暂未配置!");
                    return;
                }else{
                    toChange(
                            appConfigBean.getModules()
                                    .get(position).getTitle(),
                            appConfigBean.getModules()
                                    .get(position).getPlusdata().getLink());
                }            }else {
                    fragment=getFragment(className, appConfigBean.getModules().get(position));
                    if (fragment==null){
                        WarnUtils.toast(getActivity(),"您暂未配置!");
                        return;
                    }
                    if (listener != null) {
                        listener.close();
                    }



                }
                if (fragment != null) {

                    switchFragment(fragment, txttitle.getText().toString());
//                   switchFragment(fragment, txttitle.getText().toString());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        HandApplication.getInstance();
        HandApplication.user = HandApplication.getInstance().mSpUtil
                .getAccount();
        // 登录后返回的数据
        if (HandApplication.user != null
                && !StringUtil.isEmpty(HandApplication.user.getLikename())) {
            tx_login.setText(HandApplication.user.getLikename());
            requestManager.load(HandApplication.user.getFacepic()).placeholder(R.drawable.right_menu_person).error(R.drawable.right_menu_person).into(iv_login);
            iv_login.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ActivityUtils.to(getActivity(), PersonActivity.class);

                }
            });
        } else {
            tx_login.setText("登录");
            iv_login.setBackgroundResource(R.drawable.right_menu_person);
            iv_login.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ActivityUtils.to(getActivity(), LoginActivity.class);

                }
            });
        }

    }

    List<Map<String, Object>> listData = new ArrayList<>();
    AppConfigBean appConfigBean;

    /**
     * 加载数据
     */
    private void initData() {
        appConfigBean = HandApplication.getInstance().mSpUtil.getAppConfigBean();
        if (appConfigBean != null
                && appConfigBean.getModules() != null
                && appConfigBean.getModules().size() > 0) {
            for (int i = 0; i < appConfigBean.getModules().size(); i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("title", appConfigBean.getModules().get(i).getTitle());
                map.put("id", i + "");
                map.put("classname", appConfigBean.getModules().get(i).getClassname());
                listData.add(map);
            }

        }

    }


    private void toChange(String title, String url) {
        if(url==null){
            WarnUtils.toast(getActivity(),"您暂未配置!");
            return;
        }
            Bundle bundle = new Bundle();
            bundle.putString("url", url);

            bundle.putString("title", title);
            if (HandApplication.user != null
                    && HandApplication.user.getLikename() != null
                    && !"".equals(HandApplication.user.getLikename())) {
                bundle.putString("USEROPENID", HandApplication.user.getOpenid());
            }
            bundle.putString("CLIENTID", Const.APPSHAREID);
            ActivityUtils.to(getActivity(), WebviewActivity.class, bundle);

    }

    /**
     * the meat of switching the above fragment
     *
     * @param fragment 内容页
     * @param title
     */
    private void switchFragment(Fragment fragment, String title) {
        if (getActivity() == null)
            return;
        // 切换选项卡
        MainActivity fca = (MainActivity) getActivity();
        fca.switchContent(fragment, title);

    }

}
