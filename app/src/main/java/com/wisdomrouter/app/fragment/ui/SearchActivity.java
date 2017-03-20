package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.ui.adapter.SearchAdapter;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView;
import com.wisdomrouter.app.view.ViewPaperListView;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseDetailActivity implements PullRefreshListView.OnLoadMoreListener {
    @ViewInject(id = R.id.edtSearch)
    AutoCompleteTextView edtSearch;
    @ViewInject(id = R.id.btnSearch)
    Button btnSearch;
    @ViewInject(id = R.id.deletimg)
    ImageView delet_img;
    @ViewInject(id = R.id.search_listview)
    ViewPaperListView listView;
    /**
     * 数据填充框
     */
    private SearchAdapter listAdapter;
    /**
     * 搜索内容
     */
    private List<ArticleListDao> searchdata;
    private List<ArticleListDao> listDao = new ArrayList<>();
    /**
     * 搜索的关键字
     */
    private String searchKey = null;
    /**
     * 获取数据工具
     */
    public int page = 1, pagesize = 8;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        initTitleBar();
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void initTitleBar() {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 收缩
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText("搜索");
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.INVISIBLE);
    }

    /**
     * 加载事件
     */
    private void initView() {
        btnSearch.addTextChangedListener(textWatcher);
        delet_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listDao.clear();
                page = 1;
                edtSearch.setText("");
                listAdapter.notifyDataSetChanged();

            }
        });
        listAdapter = new SearchAdapter(this, listDao);
        listView.setAdapter(listAdapter);
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchKey = edtSearch.getText().toString().trim();

                if (!StringUtil.isEmpty(searchKey)) {
                    page = 1;
                    hideKeyborder();
                    listDao.clear();
                    getSearch(searchKey, page, pagesize);

                } else {
                    Toast.makeText(SearchActivity.this, "请输入搜索内容！", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                ArticleListDao dao = listDao.get(pos-1);
                String key = listDao.get(pos - 1).getKey();
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                bundle.putString("title", "详情");
                bundle.putString("type",dao.getInfo_class());
                bundle.putString("sharetitle", dao.getTitle());
                bundle.putString("indexpic", dao.getIndexpic());
                String url= Const.HTTP_HEADKZ + "/app/multimedia/"+dao.getInfo_class()+"?key=" +dao.getKey();
                if (dao.getInfo_class().equals("article")) {
                    handleIntent(bundle,url);

                } else if (dao.getInfo_class().equals("images")) {
                    handleIntent(bundle,url);

                }else if (dao.getInfo_class().equals("video")) {
                    handleIntent(bundle,url);
                } else if (listDao.get(pos - 1).getInfo_class()
                        .equals("activity")) {
                    bundle.putString("activityId", dao.getKey());
                    ActivityUtils.to(SearchActivity.this,
                            EventDetailActivity.class, bundle);
                }

            }
        });
        listView.setOnLoadMoreListener(this);
        listView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                listDao.clear();
                getSearch(searchKey, page, pagesize);
            }
        });
    }
    private void handleIntent(Bundle bundle,String url){
        bundle.putString("url", url);
        bundle.putString("content_api", "/images/content");
        ActivityUtils.to(SearchActivity.this, ContentWebviewActivity.class, bundle);
    }
    String beforeStr;
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            beforeStr = edtSearch.getText().toString().trim();
            beforeStr = beforeStr.replace(" ", "");
            edtSearch.setText(beforeStr);
            if (!StringUtil.isEmpty(edtSearch.getText().toString().trim())
                    && edtSearch.getText().toString().trim().length() >= 30) {
                WarnUtils.toast(SearchActivity.this, "搜索内容最多可以输入30字!");
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length()==0){
                delet_img.setVisibility(View.GONE);
                listDao.clear();
            }else{
                delet_img.setVisibility(View.VISIBLE);
            }

        }
    };


    /**
     * 获取数据
     */

    FinalHttp finalHttp;
    Gson gson = new Gson();

    private void getSearch(String key, final int page, int pagesize) {
        finalHttp = new FinalHttp();
        finalHttp.addHeader("TOKEN", Const.APPTOKEN);
        String url = Const.HTTP_HEADKZ
                + "/plugin/basesearch-api/search?keyword=" + key + "&page="
                + page + "&pagesize=" + pagesize;
        finalHttp.get(url, new AjaxCallBack<Object>() {
            @Override
            public void onStart() {
                super.onStart();
                if (page == 1) {
                    listView.onRefreshStart();
                }


            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                try {
                    searchKey = searchKey.replaceAll(" ", "");
                    searchdata = gson.fromJson(t.toString(), new TypeToken<List<ArticleListDao>>() {
                    }.getType());
                    handleMessage(searchdata);
                } catch (Exception e) {
                    WarnUtils.toast(SearchActivity.this, "获取解析异常,请稍后进入!");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                WarnUtils.toast(SearchActivity.this, "数据获取异常,稍后获取!"
                        + strMsg);
            }
        });
    }


    @Override
    public void onLoadMore() {
        page++;
        try {
            searchKey = edtSearch.getText().toString().trim();
            getSearch(searchKey, page, pagesize);
        } catch (Exception e) {

        }

    }

    private void handleMessage(List<ArticleListDao> searchdata) {
        if(page==1){
            listView.onRefreshComplete();
            if(searchdata!=null&&searchdata.size()!=0){
                listDao.addAll(searchdata);
                listAdapter.notifyDataSetChanged();
            }else{
                WarnUtils.toast(this,"搜索无结果！");
            }
        }else{
            listView.onLoadMoreComplete();
            if (searchdata!=null){
                if (searchdata.size()==0){
                    WarnUtils.toast(this,"加载完成！");
                }
                listDao.addAll(searchdata);
                listAdapter.notifyDataSetChanged();
            }else{
                WarnUtils.toast(this,"加载完成！");

            }
        }


    }
    // 关闭软键盘
    @Override
    public void hideKeyborder() {
        super.hideKeyborder();
    }
}