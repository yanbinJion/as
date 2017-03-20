package com.wisdomrouter.app.fragment.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.MyFavouriteDao;
import com.wisdomrouter.app.fragment.ui.adapter.UserFavouriteAdapter;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.utils.ActionUtils;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.CustomDialog;
import com.wisdomrouter.app.utils.NetUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView.OnLoadMoreListener;
import com.wisdomrouter.app.view.PullRefreshListView.OnRefreshListener;
import com.wisdomrouter.app.view.ViewPaperListView;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.List;

public class MyFavouriteActivity extends FinalActivity implements
        OnRefreshListener {
    /**
     * 新闻列表
     */
    @ViewInject(id = R.id.list_view)
    ViewPaperListView listView;
    @ViewInject(id = R.id.txt_blank)
    TextView txt_blank;
    /**
     * 新闻列表适配器 == ReadAdapter
     */
    private UserFavouriteAdapter listAdapter;

    private List<MyFavouriteDao> voGlobal = null;
    private List<MyFavouriteDao> favList = new ArrayList<MyFavouriteDao>();
    private int page = 1;
    private int pageSize = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_my_comment);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);

        initTitleBar();
        setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    private void initData() {
        listAdapter = new UserFavouriteAdapter(MyFavouriteActivity.this,
                favList);
        listView.setAdapter(listAdapter);
        page = 1;
        gson = new Gson();
        getData();
    }

    private void setListener() {
        listView.setOnRefreshListener(this);
        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getData();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                MyFavouriteDao favourite = favList.get(position - 1);


                Bundle bundle = new Bundle();
                bundle.putString("key", favourite.getKey());

                bundle.putString("title", "详情");
                bundle.putString("sharetitle", favourite.getTitle());
                bundle.putString("indexpic", favourite.getIndexpic());
                String url= Const.HTTP_HEADKZ + "/app/multimedia/"+favourite.getInfo_class()+"?key=" +favourite.getKey();
                if (favourite.getInfo_class().equals("article")) {
                    handleIntent(bundle,url,favourite);

                } else if (favourite.getInfo_class().equals("images")) {
                    handleIntent(bundle,url,favourite);

                }else if (favourite.getInfo_class().equals("video")) {
                    handleIntent(bundle,url,favourite);
                }
            }
        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                position = arg2;
                deleteItem();
                return true;
            }
        });
    }
    private void handleIntent(Bundle bundle,String url,MyFavouriteDao dao){
        bundle.putString("url", url);
        bundle.putString("content_api", "/images/content");
        bundle.putString("type", dao.getInfo_class());
        ActivityUtils.to(MyFavouriteActivity.this, ContentWebviewActivity.class, bundle);
    }
    private void deleteItem() {

        CustomDialog.Builder customDialog = new CustomDialog.Builder(this);
        customDialog.setMessage("确定要取消收藏吗？");
        customDialog.setTitle("取消收藏");
        customDialog.setPositiveButton("删除", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyFavouriteDao favourite = favList.get(position - 1);
                actionUtils = new ActionUtils(MyFavouriteActivity.this);
                new FavouriteInitTask(favourite.getKey(), HandApplication.user
                        .getOpenid(), favourite.getInfo_class(),
                        Const.NOCOLLECT).execute();

                dialog.dismiss();
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

    private ActionUtils actionUtils;
    FinalHttp fh;
    Gson gson;
    int position;

    private void getData() {
        fh = new FinalHttp();
        fh.addHeader("TOKEN", Const.APPTOKEN);
        if (NetUtil.NETWORN_NONE != NetUtil.getNetworkState(this)) {

            fh.get(Const.HTTP_HEADKZ+"/plugin/readactive-api/favorites?openid="
                    + HandApplication.user.getOpenid() + "&page=" + page
                    + "&pagesize=" + pageSize + "", new AjaxCallBack<Object>() {
                @Override
                public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
                    super.onLoading(count, current);
                }

                @Override
                public void onSuccess(Object t) {
                    try {
                        voGlobal = gson.fromJson(t.toString(),
                                new TypeToken<List<MyFavouriteDao>>() {
                                }.getType());
                        if (page == 1) {
                            favList.clear();
                            listView.onRefreshComplete();
                        } else {
                            listView.onLoadMoreComplete();
                        }
                        if (voGlobal != null && voGlobal.size() > 0) {
                            page += 1;
                            favList.addAll(voGlobal);
                            listAdapter.notifyDataSetChanged();
                            txt_blank.setVisibility(View.GONE);
                        } else {
                            listAdapter.notifyDataSetChanged();
                            if (page == 1) {
                                txt_blank.setVisibility(View.VISIBLE);
                                txt_blank.setText("您暂时还没有收藏过任何信息!");
                            } else {
                                WarnUtils.toast(MyFavouriteActivity.this,
                                        "已经加载完了,没有更多了哦!");
                                txt_blank.setVisibility(View.GONE);
                            }

                        }
                    } catch (Exception e) {
                        WarnUtils.toast(MyFavouriteActivity.this, "获取数据异常,稍后进入"
                                + e.getMessage());
                        listView.onRefreshComplete();
                        return;
                    }

                }

                @Override
                public void onStart() {
                    // 开始http请求的时候回调
                    if (page == 1) {
                        listView.onRefreshStart();
                    }

                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    // 加载失败的时候回调
                    // super.onFailure(t, errorNo, strMsg);
                    if (page == 1)
                        listView.onRefreshComplete();
                    else
                        listView.onLoadMoreComplete();
                    WarnUtils.toast(MyFavouriteActivity.this, "数据获取异常,稍后获取!"
                            + strMsg);
                }

            });
        } else {
            WarnUtils.toast(MyFavouriteActivity.this, "您的网络不通!");
        }
    }

    /**
     * 设置左右标签,专门设置title_bar
     */
    private void initTitleBar() {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText("我的收藏");
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

    String getResult;

    /**
     * 发送收藏
     */
    private class FavouriteInitTask extends AsyncTask<Void, Integer, String> {
        int ERRORCODE = 1;
        String infoid = null;
        String uid = null;
        String classtype = null;
        String message = null;
        String isCollect = null;

        public FavouriteInitTask(String infoid, String uid, String classtype,
                                 String isCollect) {
            this.infoid = infoid;
            this.uid = uid;
            this.classtype = classtype;
            this.isCollect = isCollect;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                getResult = new GlobalTools(MyFavouriteActivity.this)
                        .setFavourByUserIdAndInfoId(uid, infoid, classtype,
                                isCollect);
            } catch (Exception e) {
                e.printStackTrace();
                message = e.getMessage();
                publishProgress(ERRORCODE);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (getResult != null && getResult.contains("取消")) {
                favList.remove(position - 1);
                listAdapter.notifyDataSetChanged();
            }
            WarnUtils.toast(MyFavouriteActivity.this, getResult);

            super.onPostExecute(result);
        }
    }
}
