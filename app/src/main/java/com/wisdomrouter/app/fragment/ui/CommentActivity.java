package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CommentDao;
import com.wisdomrouter.app.fragment.bean.ResultDao;
import com.wisdomrouter.app.fragment.ui.adapter.CommentAdapter;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView.OnLoadMoreListener;
import com.wisdomrouter.app.view.PullRefreshListView.OnRefreshListener;
import com.wisdomrouter.app.view.ViewPaperListView;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends BaseDetailActivity implements
        OnTouchListener {
    @ViewInject(id = R.id.list_view)
    ViewPaperListView listView;
    @ViewInject(id = R.id.ll_root)
    RelativeLayout ll_root;
    @ViewInject(id = R.id.txt_send, click = "sendComment")
    TextView txt_send;
    @ViewInject(id = R.id.txt_comblank)
    TextView txt_comblank;
    @ViewInject(id = R.id.txt_write_review)
    EditText txt_write_review;

    private String id = "", type;
    private int page = 1;
    private int pageSize = 12;
    private CommentAdapter commentAdapter;
    private GlobalTools globalTool;
    private List<CommentDao> voGlobal = null;
    private List<CommentDao> commentList = new ArrayList<CommentDao>();
    /**
     * 加载更多
     */
    private LinearLayout mLoadView;
    private LayoutInflater mInflater;

    /**
     * 判断页面是否滑动到页面
     */
    private boolean isLast = false;

    // 收集错误信息
    private String errorInfo = null;
    private boolean isRefresh = false;

    private static final int DATA_FAILED = 1; // 获取数据
    private static final int DATA_SUCCESSED = 2; // 更新数据
    private static final int TO_COMMENT = 3; // 提交评论
    private static final int REFRESH_DATA = 4; // 刷新数据
    private static final int LOAD_MORE_DATA = 5; // 加载更多数据
    private static final int COMMENT_OK = 6; // 提交评价成功
    private static final int COMMENT_NO = 7;

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_DATA:
                    isRefresh = true;
                    Refresh();
                    break;
                case LOAD_MORE_DATA:
                    isRefresh = false;
                    LoadMore();
                    break;
                case TO_COMMENT:
                    hideKeyborder();
                    ToComment();
                    break;
                case DATA_FAILED:
                    WarnUtils.toast(CommentActivity.this, errorInfo);

                    if (isRefresh)
                        listView.onRefreshComplete();
                    else
                        listView.onLoadMoreComplete();
                    break;
                case DATA_SUCCESSED:
                    updateViews();

                    if (isRefresh)
                        listView.onRefreshComplete();
                    else
                        listView.onLoadMoreComplete();
                    break;
                case COMMENT_OK:
                    CommentOK();
                    break;
                case COMMENT_NO:
                    CommentNO();
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);
        initTitleBar(true);
//		LightnessControl.SetLightness(this, SharePreferenceUtil.getLightness());
        ll_root.setOnTouchListener(this);
        if (getIntent() == null) {
            WarnUtils.toast(this, "暂无数据！");
        } else {
            id = getIntent().getStringExtra("id");
            type = getIntent().getStringExtra("type");
            findViewById();
            setListener();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (voGlobal == null) {
            initData();
        }
    }

    private void initData() {
        globalTool = new GlobalTools(this);
        commentAdapter = new CommentAdapter(CommentActivity.this, commentList);
        listView.setAdapter(commentAdapter);
        page = 1;
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    private void findViewById() {
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoadView = (LinearLayout) mInflater.inflate(
                R.layout.pull_to_load_footer, null);
        txt_write_review.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if (txt_write_review.getText().toString() != null
                    && !"".equals(txt_write_review.getText().toString())) {
                txt_send.setTextColor(getResources().getColor(R.color.blue));
                if (txt_write_review.getText().toString().length() >= 140) {
                    WarnUtils.toast(CommentActivity.this, "评论内容最多可以输入140个字段!");
                }
            } else {
                txt_send.setTextColor(getResources()
                        .getColor(R.color.dark_gray));

            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {


        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };

    private void setListener() {
        listView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mHandler.sendEmptyMessage(REFRESH_DATA);
            }
        });
        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mHandler.sendEmptyMessage(LOAD_MORE_DATA);
            }
        });
        mLoadView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View paramView) {
                if (!isLast) {
                    mHandler.sendEmptyMessage(LOAD_MORE_DATA);
                }
            }
        });

    }

    public void sendComment(View v) {
        if (HandApplication.user == null
                || "".equals(HandApplication.user.getLikename())) {
            ActivityUtils.to(CommentActivity.this, LoginActivity.class);
            return;
        }
        if (TextUtils.isEmpty(txt_write_review.getText().toString().trim())) {
            WarnUtils.toast(CommentActivity.this, "请输入评论内容");
            return;
        }
        // 提交评论操作
        inputStr = txt_write_review.getText().toString().trim();
        if (inputStr.length() < 2) {
            WarnUtils.toast(CommentActivity.this, "评论内容最少两个字!");
            return;
        }

        mHandler.sendEmptyMessage(TO_COMMENT);
    }

    /**
     * 刷新数据
     */
    private void Refresh() {
        listView.onRefreshStart();

        new Thread() {
            public void run() {
                try {
                    voGlobal = globalTool.getCommentByIdOrinfokey(false, id,
                            type, page + "", pageSize + "");

                    mHandler.sendEmptyMessage(DATA_SUCCESSED);
                } catch (Exception e) {
                    errorInfo = e.getMessage();
                    mHandler.sendEmptyMessage(DATA_FAILED);
                }
            }

            ;
        }.start();
    }

    // 加载更多数据
    private List<CommentDao> more = null;

    /**
     * 加载更多
     */
    private void LoadMore() {
        new Thread() {
            public void run() {
                try {
                    more = globalTool.getCommentByIdOrinfokey(false, id, type,
                            page + "", pageSize + "");
                    mHandler.sendEmptyMessage(DATA_SUCCESSED);
                } catch (Exception e) {
                    errorInfo = e.getMessage();
                    mHandler.sendEmptyMessage(DATA_FAILED);
                }
            }

            ;
        }.start();
    }

    /**
     * 更新控件
     */
    private void updateViews() {
        if (isRefresh) {
            if (voGlobal != null && voGlobal.size() > 0) {
                page += 1;
                commentList.clear();
                commentList.addAll(voGlobal);
                commentAdapter.notifyDataSetChanged();
//				listView.setVisibility(View.VISIBLE);
                txt_comblank.setVisibility(View.GONE);
            } else {
//				listView.setVisibility(View.GONE);
                txt_comblank.setVisibility(View.VISIBLE);
                txt_comblank.setText("暂时没有评论信息!");
            }
        } else {
            if (more != null && more.size() > 0) {
//				listView.setVisibility(View.VISIBLE);
                txt_comblank.setVisibility(View.GONE);
                page++;
                commentList.addAll(more);
                commentAdapter.notifyDataSetChanged();
            } else {
//				listView.setVisibility(View.VISIBLE);
                txt_comblank.setVisibility(View.GONE);
                WarnUtils.toast(CommentActivity.this, "已经加载完了,没有了哦!");
            }

        }
    }

    private String commentResult = null;
private ResultDao resultDao;
    /**
     * 提交评论数据
     */
    private void ToComment() {
        new Thread() {
            public void run() {
                try {
                    if (inputStr == null || inputStr.equals("")) {
                        mHandler.sendEmptyMessage(COMMENT_NO);
                    } else {
                        resultDao = globalTool.ToComment2(id, inputStr,
                                HandApplication.user.getOpenid());
                        mHandler.sendEmptyMessage(COMMENT_OK);
                    }
                } catch (Exception e) {
                    errorInfo = e.getMessage();
                    mHandler.sendEmptyMessage(COMMENT_NO);
                }
            }

            ;
        }.start();
    }

    /**
     * 提交评论事件成功
     */
    private void CommentOK() {
        if (resultDao != null) {
            txt_write_review.setText("");
            hodesoft();
            WarnUtils.toast(CommentActivity.this, resultDao.getMessage());
            if (resultDao.getScore()!=0){
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    WarnUtils.toast(CommentActivity.this,"发表评论",resultDao.getScore());
//                    Toast toast = Toast.makeText(getApplicationContext(),
//                            ("发表评论  + "+resultDao.getScore()+""), Toast.LENGTH_SHORT);
//
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    LinearLayout toastView = (LinearLayout) toast.getView();
//                    ImageView imageCodeProject = new ImageView(getApplicationContext());
//                    imageCodeProject.setImageResource(R.drawable.my_score_ic);
//                    toastView.addView(imageCodeProject, 0);
//                    toast.show();
//                    WarnUtils.toast(CommentActivity.this, "发表评论"+resultDao.getScore());
                }
//            },1000);}

            if (resultDao.getState().equals("1")){
                page = 1;
            mHandler.sendEmptyMessage(REFRESH_DATA);
            }
        }
    }

    private void CommentNO() {
        WarnUtils.toast(CommentActivity.this, "提交失败");
    }

    private String inputStr = null;

    /**
     * 初始化标题框
     */
    private void initTitleBar(boolean isPrivate) {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText("评论");
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouch(v, event);
    }

}
