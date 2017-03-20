package com.wisdomrouter.app;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.AppConfigDao;
import com.wisdomrouter.app.fragment.ui.LoginActivity;
import com.wisdomrouter.app.interfases.DialogControl;
import com.wisdomrouter.app.utils.ActionUtils;
import com.wisdomrouter.app.utils.DialogHelper;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.ProgressDialog;

import net.tsz.afinal.FinalActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseDetailActivity extends FinalActivity implements
        OnTouchListener, DialogControl {
    private PopupWindow popupwindow = null;
    private ActionUtils actionUtils;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    // 手指向右滑动时的最小速度
    private static final int XSPEED_MIN = 50;

    // 手指向右滑动时的最小距离
    private static final int XDISTANCE_MIN = 60;

    // 记录手指按下时的横坐标。
    private float xDown;

    // 记录手指移动时的横坐标。
    private float xMove;

    // 用于计算手指滑动的速度。
    private VelocityTracker mVelocityTracker;
    private ProgressDialog _waitDialog;
    private boolean _isVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionUtils = new ActionUtils(this);
        _isVisible = true;
        // 统计启动次数
        PushAgent.getInstance(this).onAppStart();
        // 闪退次数
        MobclickAgent.updateOnlineConfig(this);
    }

    public void hideKeyborder() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * 验证号码 手机号 固话均可
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 设置ListView高度
     *
     * @param listView
     */
    public void setListViewHeight(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 收藏和赞功能
     */
    public void FavOrDigg(String type, String id, String classType) {
        if (HandApplication.user == null
                || StringUtil.isEmpty(HandApplication.user.getOpenid())) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        if (type.equals(Const.COLLECT)) { // 收藏
            actionUtils.Favourite(id, HandApplication.user.getOpenid(),
                    classType, Const.COLLECT);
        } else if (type.equals(Const.NOCOLLECT)) { // 取消收藏
            actionUtils.Favourite(id, HandApplication.user.getOpenid(),
                    classType, Const.NOCOLLECT);
        } else if (type.equals(Const.ZAN)) { // 赞
            actionUtils.Digg(id, HandApplication.user.getOpenid(), classType);
        }
    }

    /**
     * 字体切换
     */
    public void FontChange(View v) {
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
            return;
        }

    }

    public Boolean back() {
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
            popupwindow = null;
            return true;
        }
        return false;

    }

    /**
     * 初始化fontPopupWindow
     */
    public void initmPopupWindowView2() {
        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.font_pop, null,
                false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView, LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);

        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupwindow.setAnimationStyle(R.style.AnimationFade);
        popupwindow.setOutsideTouchable(true);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                return false;
            }
        });

        /** 在这里可以实现自定义视图的功能 */
        SeekBar seekBar = (SeekBar) customView.findViewById(R.id.seekBar);
        seekBar.setMax(25);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
//				Const.SEEK_FONT = seekBar.getProgress();
                PopWithFont();
            }
        });

    }

    /**
     * 在子类中重写该类，用于改变WebView中的字体大小
     */
    public void PopWithFont() {
        Log.e("BaseDetailActivity", "PopWithFont()");
        // popupwindow.dismiss();
    }

    /**
     * 初始化标题
     */
    public void initTitleBar(String title, int rightId, OnClickListener onclick) {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.VISIBLE);
        if (rightId!=0)
        rightButton.setImageResource(rightId);
        if (onclick!=null)
        rightButton.setOnClickListener(onclick);

    }

    /**
     * 初始化标题
     */
    public void initTitleBar(String title, String rightBtnTxt,
                             OnClickListener onclick) {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);
        Button rightButton = (Button) findViewById(R.id.right_btn2);
        rightButton.setText(rightBtnTxt);
        rightButton.setOnClickListener(onclick);
        rightButton.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化标题
     */
    public void initTitleBarForLeft(String title) {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);
        Button rightButton = (Button) findViewById(R.id.right_btn2);
        rightButton.setVisibility(View.INVISIBLE);

    }

    /**
     * 打开评论页
     */
    public void showComment() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 对WebView进行样式设置
     *
     * @param content
     * @return
     */
    public String wrapHtml(String content) {
//		if (HandApplication.getInstance().mSpUtil.getIsShowImg()
//				&& NetUtil.getNetworkState(this) != 1) {// wifi下显示图片
//			content = content.replaceAll("<img[^>]*/>", "");// 去掉img标签
//		}
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
        String font_size = "16";
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        if (screenWidth < 640) {
            font_size = "14";
        } else if (screenWidth <= 1080) {
            font_size = "16";
        } else if (screenWidth <= 1600) {
            font_size = "18";
        } else {
            font_size = "20";
        }
        builder.append("p{font-size:" + font_size
                + "px;color:#555;line-height:20px;}");
        builder.append(" img{display:block;margin: 0 auto;max-width: 100%;}");
        builder.append("</style>");
        builder.append("</head>");
        builder.append("<body><div id=\"app_content\" >");
        builder.append(content == null ? "" : content);
        builder.append("</div></body></html>");
        return builder.toString();
    }

    /**
     * 获取当前网络状态的类型
     *
     * @param mContext
     * @return 返回网络类型
     */
    public static final int NETWORK_TYPE_NONE = -0x1; // 断网情况
    public static final int NETWORK_TYPE_WIFI = 0x1; // WiFi模式
    public static final int NETWOKR_TYPE_MOBILE = 0x2; // gprs模式

    public static int getCurrentNetType(Context mContext) {
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi1
        NetworkInfo gprs = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // gprs2

        if (wifi != null && wifi.getState() == State.CONNECTED) {
            // Log.d(TAG, "Current net type:  WIFI.");
            return NETWORK_TYPE_WIFI;
        } else if (gprs != null && gprs.getState() == State.CONNECTED) {
            // Log.d(TAG, "Current net type:  MOBILE.");
            return NETWOKR_TYPE_MOBILE;
        }
        // Log.e(TAG, "Current net type:  NONE.");
        return NETWORK_TYPE_NONE;
    }

    /*
     * 打开设置网络界面
     */
    public static void setNetworkMethod(final Context context) {
        // 提示对话框
        AlertDialog.Builder builder = new Builder(context);
        builder.setTitle("网络设置提示")
                .setMessage("网络连接不可用,是否进行设置?")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = null;
                        // 判断手机系统的版本 即API大于10 就是3.0或以上版本
                        if (android.os.Build.VERSION.SDK_INT > 10) {
                            intent = new Intent(
                                    android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        } else {
                            intent = new Intent();
                            ComponentName component = new ComponentName(
                                    "com.android.settings",
                                    "com.android.settings.WirelessSettings");
                            intent.setComponent(component);
                            intent.setAction("android.intent.action.VIEW");
                        }
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static final Pattern PATTERN = Pattern.compile(
            "<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE);

    @SuppressWarnings({"unchecked"})
    public static List<String> getImgSrc(String html) {
        Matcher matcher = PATTERN.matcher(html);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group == null) {
                continue;
            }
            // 这里可能还需要更复杂的判断,用以处理src="...."内的一些转义符
            if (group.startsWith("'")) {
                list.add(group.substring(1, group.indexOf("'", 1)));
            } else if (group.startsWith("\"")) {
                list.add(group.substring(1, group.indexOf("\"", 1)));
            } else {
                list.add(group.split("\\s")[0]);
            }
        }
        return list;

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashScreen"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this); // 统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashScreen"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
        // onPageEnd 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    /**
     * @description:关闭软键盘
     * @author:baifan
     * @return:void
     */
    public void hodesoft() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // 打开HTML文件
    public void openHtml(String path) {
        Uri uri = Uri.parse(path);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                // 活动的距离
                int distanceX = (int) (xMove - xDown);
                // 获取顺时速度
                int xSpeed = getScrollVelocity();
                // 当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
                if (distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
                    finish();
                }
                break;
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
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
     * 去重图片视频
     *
     * @param appConfigDao
     * @return
     */
    public AppConfigBean quchong(AppConfigBean appConfigDao) {
        List<AppConfigBean.Module> modulesList = appConfigDao.getModules();
        List<AppConfigBean.Module> modulesListnew = new ArrayList<>();
        List<AppConfigBean.Module> mideaList = new ArrayList<>();
        List<AppConfigBean.Module> ImagesList = new ArrayList<>();
        List<AppConfigBean.Module> VideoList = new ArrayList<>();
        for (int i = 0; i < modulesList.size(); i++) {
            if (modulesList.get(i).getClassname().equals("media")) {
                mideaList.add(modulesList.get(i));
            } else if (modulesList.get(i).getClassname().equals("images")) {
                ImagesList.add(modulesList.get(i));
            } else if (modulesList.get(i).getClassname().equals("video")) {
                VideoList.add(modulesList.get(i));
            }
        }
        for (int i = 0; i < ImagesList.size(); i++) {//去除image中的重复的
            for (int j = 0; j < mideaList.size(); j++) {
                int length=mideaList.get(j).getMedia_modules().size();
                for (int k = 0; k < length; k++) {
                    if (ImagesList.get(i).getModulekey().equals(mideaList.get(j).getMedia_modules().get(k).getModulekey())) {
                        modulesListnew.add(ImagesList.get(i));
                    }}
            }
        }
        for (int i = 0; i < VideoList.size(); i++) {//去除video中的重复的
            for (int j = 0; j < mideaList.size(); j++) {
                int length=mideaList.get(j).getMedia_modules().size();
                for (int k = 0; k <length; k++) {
                    if (VideoList.get(i).getModulekey().equals(mideaList.get(j).getMedia_modules().get(k).getModulekey())) {
                        modulesListnew.add(VideoList.get(i));
                    }
                }

            }
        }
        modulesList.removeAll(modulesListnew);
        appConfigDao.setModules(modulesList);
        return appConfigDao;
    }
}
