package com.wisdomrouter.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.wisdomrouter.app.fragment.ArticleImageFragment;
import com.wisdomrouter.app.fragment.CountryFragment;
import com.wisdomrouter.app.fragment.CustomFragment;
import com.wisdomrouter.app.fragment.FragmentArtOnly;
import com.wisdomrouter.app.fragment.HomeFragment;
import com.wisdomrouter.app.fragment.ImagesFragment;
import com.wisdomrouter.app.fragment.LeftMenuFragment;
import com.wisdomrouter.app.fragment.LiveFragment;
import com.wisdomrouter.app.fragment.ProjectFragment;
import com.wisdomrouter.app.fragment.VideoFragment;
import com.wisdomrouter.app.fragment.VisionFragment;
import com.wisdomrouter.app.fragment.WebviewFragment;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.fragment.ui.MineActivity;
import com.wisdomrouter.app.services.NetChangeService;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.CustomDialog;
import com.wisdomrouter.app.utils.WarnUtils;

import java.util.HashMap;
import java.util.Map;

import static com.wisdomrouter.app.R.string.login;


public class MainActivity extends BaseActivity {

    private ImageView leftButton, rightButton;
    private TextView titleText;
    public DrawerLayout drawerLayout;
    private LeftMenuFragment leftMenuFragment;
    private Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.frame_content);
        startService(new Intent(this, NetChangeService.class));
        initBar();
        initTitleBar(getString(R.string.app_name));
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        initDrawerLayout();
        if (HandApplication.getInstance().appConfigBean.getApp_main_btns() != null&&HandApplication.getInstance().appConfigBean.getApp_main_btns().size()>0) {
            HomeFragment mainFragment = new HomeFragment();
            mainFragment.setOnChangeTitleListener(new HomeFragment.OnChangeTitleListener() {
                @Override
                public void ChangeTitle(String s) {
                    initTitleBar(s);
                }
            });
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, mainFragment).commit();
        } else {
            String className = HandApplication.getInstance().appConfigBean.getModules().get(0).getClassname();
            AppConfigBean.Module module = HandApplication.getInstance().appConfigBean.getModules().get(0);
            Fragment fragment = getFragment(className, module);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, fragment).commit();
        }
        leftMenuFragment = new LeftMenuFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.leftmain, leftMenuFragment).commit();
        leftMenuFragment.setOnCloseDrawerLayoutListener(new LeftMenuFragment.closeDrawerLayoutListener() {
            @Override
            public void close() {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        UserDao.Userinfo userinfo=HandApplication.getInstance().mSpUtil.getAccount();
        if (userinfo!=null&&!TextUtils.isEmpty(userinfo.getOpenid()))//{}
        login(userinfo.getMobile(),userinfo.getPassword());

    }


    public void initBar() {
        leftButton = (ImageView) findViewById(R.id.left_btn);
        rightButton = (ImageView) findViewById(R.id.right_btn);
        titleText = (TextView) findViewById(R.id.title);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.to(MainActivity.this, MineActivity.class);
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }


    public void initTitleBar(CharSequence title) {
        if (!TextUtils.isEmpty(title))
        titleText.setText(title);
    }

    /**
     * 设置侧拉效果
     */
    private void initDrawerLayout() {
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = drawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.9f + scale * 0.1f;
                float leftScale = 1 - 0.3f * scale;
                ViewHelper.setScaleX(mMenu, leftScale);
                ViewHelper.setScaleY(mMenu, leftScale);
                ViewHelper.setAlpha(mMenu, 1.0f);
                drawerLayout.setScrimColor(getResources().getColor(R.color.transparent));
                ViewHelper.setTranslationX(mContent,
                        mMenu.getMeasuredWidth() * (1 - scale));
                ViewHelper.setPivotX(mContent, 0);
                ViewHelper.setPivotY(mContent,
                        mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
                ViewHelper.setScaleX(mContent, rightScale);
                ViewHelper.setScaleY(mContent, rightScale);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//         String device_token = UmengRegistrar.getRegistrationId(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp(null, 2);
            return true;
        }
        return true;
    }

    WebView wv;

    /**
     * 退出提示
     */
    @SuppressWarnings("deprecation")
    public void exitApp(WebView wv, int open) {
        CustomDialog.Builder customDialog = new CustomDialog.Builder(this);
        customDialog.setMessage("确定要退出" + getString(R.string.app_name) + "吗？");
        customDialog.setTitle("退出提示");
        customDialog.setPositiveButton("确定", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (HandApplication.autologin == 0) {
                    HandApplication.getInstance().mSpUtil.saveAccount(null);
                }
                MobclickAgent.onKillProcess(MainActivity.this);
                MainActivity.this.stopService(new Intent(MainActivity.this, NetChangeService.class));
                finish();
                Process.killProcess(Process.myPid());

            }
        });
        customDialog.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Window window = new CustomDialog(this).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        // 设置透明度为0.3
        lp.alpha = 0.6f;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        customDialog.create().show();
    }

    public void switchContent(final Fragment fragment, CharSequence title) {
        initTitleBar(title);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, fragment).commit();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    Context mContext=this;
    UserDao userdao;
    /**
     * 登录
     * @param mobile
     * @param password
     */
    void login(String mobile,String password){
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                try {
                    userdao=new Gson().fromJson(response, UserDao.class);
                    if (userdao!=null&&userdao.getState().equals("2")){
                        HandApplication.autologin = 1;
                        HandApplication.getInstance().mSpUtil.saveLoginPassword(HandApplication.getInstance().mSpUtil.getLoginPassword());
                        userdao.getUserinfo().setPassword(HandApplication.getInstance().mSpUtil.getLoginPassword());
                        HandApplication.getInstance().mSpUtil.saveAccount(userdao.getUserinfo());
                        HandApplication.user=HandApplication.getInstance().mSpUtil.getAccount();
                    }else{
                        HandApplication.getInstance().mSpUtil.saveAccount(null);
                        HandApplication.user=null;
                    }
                } catch (Exception e) {
                    WarnUtils.toast(mContext, "解析数据失败" + e.getMessage());
                    HandApplication.getInstance().mSpUtil.saveAccount(null);
                    HandApplication.user=null;
                }

            }

            @Override
            public void reqError(String error) {
                // 失败的回调 ：失败的提醒
                WarnUtils.toast(mContext, "获取数据失败" + error);
                HandApplication.getInstance().mSpUtil.saveAccount(null);
                HandApplication.user=null;
            }
        };
        Map<String,String> map=new HashMap<>();
        map.put("username",mobile);
        map.put("password",password);
        map.put("client",Const.APPSHAREID);
        GlobalTools.login(volleyRequest,map);
    }

}
