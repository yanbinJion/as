package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.CardsAnimationAdapter;
import com.wisdomrouter.app.fragment.adapter.ZbContentAdapter;
import com.wisdomrouter.app.fragment.bean.ZbContentDao;
import com.wisdomrouter.app.fragment.bean.ZbContentDao.ListContent;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;
import com.wisdomrouter.app.utils.ScrollUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView.OnRefreshListener;
import com.wisdomrouter.app.view.ViewPaperListView;
import com.wisdomrouter.app.view.pulltozoomview.PullToZoomBase;
import com.wisdomrouter.app.view.pulltozoomview.PullToZoomListViewEx;
import com.wisdomrouter.app.view.pulltozoomview.PullToZoomScrollViewEx;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.List;

import static com.wisdomrouter.app.fragment.ui.ImviteActivity.FILECHOOSER_RESULTCODE;

public class LiveActivity extends BaseDetailActivity {

    private String url, key;
    private Context mContext;
    private ZbContentDao zbContentDao;
    private ZbContentAdapter zbAdapter;
    private List<ListContent> list = new ArrayList<ListContent>();
    //    private AnimationAdapter animationAdapter;
    private Gson gson = null;
    /**
     * 列表控件
     */
    @ViewInject(id = R.id.zoomScr)
    PullToZoomListViewEx zoomScroll;
    @ViewInject(id = R.id.rl_living)
    RelativeLayout rl;
    @ViewInject(id = R.id.progressbar)
    ProgressBar progressBar;
    /**
     * 头部控件
     */
    @ViewInject(id = R.id.tv_living)
    TextView title;
    @ViewInject(id = R.id.left_btn)
    ImageView left_btn;
    @ViewInject(id = R.id.right_btn, click = "share")
    ImageView right_btn;
    /**
     * 直播内容标题
     */
    TextView txttitle;
    TextView txtContent;
    TextView pic_detail;
    ImageView iv_top;
    private DisplayImageOptions options; // 配置图片加载及显示选项
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private int mParallaxImageHeight;
    private boolean isRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_live);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.dimen_200);
        mContext = this;
        if (getIntent() == null) {
            WarnUtils.toast(mContext, "暂无信息!");
            return;
        }
        url = getIntent().getExtras().getString("url");
        key = getIntent().getExtras().getString("key");
//        View headViewHover = LayoutInflater.from(this).inflate(
//                R.layout.view_living_head_hover, null);
//        left_btn = (ImageView) headViewHover.findViewById(R.id.left_btn);
//        right_btn = (ImageView) headViewHover.findViewById(R.id.right_btn);
//        listView.addHeaderView(headViewHover);
//        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));
        txttitle = (TextView) zoomScroll.getZoomView().findViewById(R.id.title);
        iv_top = (ImageView) zoomScroll.getZoomView().findViewById(R.id.iv_top);


//        View contentView = LayoutInflater.from(this).inflate(R.layout.view_living_list, null, false);
//        ListView listView = (ListView) contentView.findViewById(R.id.mViewPager);
        ;
        View headView = LayoutInflater.from(this).inflate(R.layout.view_living_head_one, null, false);
        txtContent = (TextView) headView.findViewById(R.id.txtContent);
        pic_detail = (TextView) headView.findViewById(R.id.pic_detail);
        zoomScroll.setHead(headView);

        zbContentDao = new ZbContentDao();
        zbAdapter = new ZbContentAdapter(mContext, list);
//        animationAdapter = new CardsAnimationAdapter(zbAdapter);
//        animationAdapter.setAbsListView(listView);
        zoomScroll.setAdapter(zbAdapter);

//        zoomScroll.setScrollContentView(contentView);
        zoomScroll.setOnPullScrollListener(new PullToZoomListViewEx.onPullScrollListener() {
            @Override
            public void onPullScroll(int position) {
                if (position >= 1) {
                    int baseColor = getResources().getColor(R.color.app_topbg);
                    rl.setBackgroundColor(baseColor);
                } else {
                    int transColor = getResources().getColor(R.color.transparent);
                    rl.setBackgroundColor(transColor);
                }
            }
        });
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (mScreenWidth * 2 / 3F));
        zoomScroll.setHeaderLayoutParams(localObject);
        zoomScroll.setParallax(true);
        zoomScroll.setOnPullZoomListener(new PullToZoomBase.OnPullZoomListener() {
            @Override
            public void onPullZooming(int newScrollValue) {

            }

            @Override
            public void onPullZoomEnd() {
                if (isRefresh) {
                    progressBar.setVisibility(View.VISIBLE);
                    isRefresh = false;
                    getLiveContent();
                }
            }
        });

//        zoomScroll.setOnScrollListener(new PullToZoomScrollViewEx.OnScrollListener() {
//            @Override
//            public void scroolTop(int top) {
//                int baseColor = getResources().getColor(R.color.app_topbg);
//                float alpha = Math.min(1, (float) top / mParallaxImageHeight);
//                rl.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
//            }
//        });

        setListener();
        // 配置图片加载及显示选项（还有一些其他的配置，查阅doc文档吧）
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.live_bg) // 在ImageView加载过程中显示图片
                .showImageForEmptyUri(R.drawable.live_bg) // image连接地址为空时
                .showImageOnFail(R.drawable.live_bg) // image加载失败
                .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                .imageScaleType(ImageScaleType.EXACTLY) // 设置图片以如何的编码方式显示
                .cacheInMemory(true) // 加载图片时会在内存中加载缓存
                .cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
                .build(); // 创建配置过得DisplayImageOption对象
        gson = new Gson();
        showProgressDialog("加载中...", "vertical");
//        listView.onRefreshStart();
        getLiveContent();
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem >= 1) {
//                    rl.setVisibility(View.VISIBLE);
//                } else {
//                    rl.setVisibility(View.GONE);
//                }
//            }
//        });
//        listView.setOnRefreshListener(new OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                getLiveContent();
//            }
//        });
//        listView.setOnLoadMoreListener(new PullRefreshListView.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//
//            }
//        });
    }

    PopupWindow PopupWindow;

    /**
     * 直播详情
     *
     * @param v
     */
    public void detail(View v) {
        if (PopupWindow == null) {
            PopupWindow = new PopupWindows(mContext, pic_detail);

        } else
            PopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

    }

    /**
     * 直播详情
     *
     * @param v
     */
//    public void up(View v) {
//        // scrollView.smoothScrollTo(0, 0);
//        // scrollView.fullScroll(View.FOCUS_UP);
//        btn_up.setVisibility(View.GONE);
//        scrollView.post(new Runnable() {
//
//            @Override
//            public void run() {
//                scrollView.scrollTo(0, 0);
//
//            }
//        });
//    }

//    private class TouchListenerImpl implements OnTouchListener {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            switch (motionEvent.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    int scrollY = view.getScrollY();
//                    // int height=view.getHeight();
//                    // int
//                    // scrollViewMeasuredHeight=scrollView.getChildAt(0).getMeasuredHeight();
//                    if (scrollY > 0) {
//                        btn_up.setVisibility(View.VISIBLE);
//                    } else if (scrollY < 0) {
//                        btn_up.setVisibility(View.GONE);
//                    }
//                    // if((scrollY+height)==scrollViewMeasuredHeight){
//                    // System.out.println("滑动到了底部 scrollY="+scrollY);
//                    // System.out.println("滑动到了底部 height="+height);
//                    // System.out.println("滑动到了底部 scrollViewMeasuredHeight="+scrollViewMeasuredHeight);
//                    // }
//                    break;
//
//                default:
//                    break;
//            }
//            return false;
//        }
//
//    }

    ;

    /**
     * 打开简介页面
     *
     * @author Administrator
     */
    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            View view = View.inflate(mContext, R.layout.activity_live_detail,
                    null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            // 设置键盘不挡住Dialog
            setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            setWidth(LayoutParams.WRAP_CONTENT);
            setHeight(LayoutParams.WRAP_CONTENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.CENTER, 0, 0);
            update();

            final TextView txt_detail = (TextView) view
                    .findViewById(R.id.txt_detail);
            final ImageView iv_close = (ImageView) view
                    .findViewById(R.id.iv_close);
            iv_close.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    changeWindowAlpha(1f);
                    dismiss();
                }
            });
            txt_detail.setText(zbContentDao.getContent() == null ? ""
                    : zbContentDao.getContent());
        }
    }

    /**
     * @description: 设置监听
     * @author:wangfanghui
     * @return:void
     */
    private void setListener() {
        left_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (PopupWindow != null && PopupWindow.isShowing()) {
                    PopupWindow.dismiss();
                    return;
                }
                finish();

            }
        });
        pic_detail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zbContentDao != null) {
                    changeWindowAlpha(0.5f);
                    detail(v);
                }
            }
        });
    }

    private void changeWindowAlpha(float windowAlpha) {
        WindowManager.LayoutParams lp = this.getWindow()
                .getAttributes();
        lp.alpha = windowAlpha;
        this.getWindow().setAttributes(lp);
    }

    /**
     * @description: 获取直播内容
     * @author:wangfanghui
     * @return:void
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void getLiveContent() {
        FinalHttp fh = new FinalHttp();
        fh.addHeader("TOKEN", Const.APPTOKEN);
        fh.get(url + "?key=" + key, new AjaxCallBack() {
            @Override
            public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(Object t) {
                isRefresh = true;
                try {
                    progressBar.setVisibility(View.GONE);
                    zbContentDao = gson.fromJson(t.toString(),
                            ZbContentDao.class);
                    updateView();
                } catch (Exception e) {
                    WarnUtils.toast(mContext, "数据获取解析异常,请稍后进入!");
//                    listView.onRefreshComplete();
                    hideProgressDialog();
                    return;
                }

            }

            @Override
            public void onStart() {
                // 开始http请求的时候回调
                // if (page == 1)
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                // 加载失败的时候回调
                // super.onFailure(t, errorNo, strMsg);
                // if (page == 1)
//                listView.onRefreshComplete();
                progressBar.setVisibility(View.GONE);
                WarnUtils.toast(mContext, "网络异常,获取数据失败" + strMsg);
                isRefresh = true;
            }

        });
    }

    /**
     * @description: 更新UI
     * @author:wangfanghui
     * @return:void
     */
    protected void updateView() {
//        listView.onRefreshComplete();
        txtContent.setText(zbContentDao.getContent() == null ? ""
                : zbContentDao.getContent());
        txttitle.setText(zbContentDao.getTitle() == null ? "" : "#"
                + zbContentDao.getTitle() + "#");
        List<ListContent> listPage = zbContentDao.getList();
        if (listPage != null && listPage.size() > 0) {
            list.clear();
            list.addAll(listPage);
            zbAdapter.notifyDataSetChanged();
        } else {
            WarnUtils.toast(mContext, "该直播暂无信息!");
        }
        imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(getIntent().getStringExtra("pic"), "600", "900"), iv_top,
                options);

        hideProgressDialog();
    }


    public void refresh(View view) {
        showProgressDialog("正在刷新..", "vertical");
        getLiveContent();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, intent);

    }
    public void share(View view) {
        ShareUtils.shareSdk(LiveActivity.this, txttitle.getText().toString(),
                zbContentDao.getIndexpic(), zbContentDao.getContent(), zbContentDao.getKey(), Const.SHARE_API.LIVES, umShareListener);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Log.d("plat", "platform" + platform);
            WarnUtils.toast(mContext, "分享成功");
//            getShareScode(Const.SHARE_API.AVTIVITY);

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            WarnUtils.toast(mContext, "分享失败");
//            if (t != null) {
//                Log.d("throw", "throw:" + t.getMessage());
//            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            WarnUtils.toast(mContext, "分享取消了");
        }
    };
}
