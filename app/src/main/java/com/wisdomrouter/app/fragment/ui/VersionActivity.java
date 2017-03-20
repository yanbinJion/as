package com.wisdomrouter.app.fragment.ui;

import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/31.
 */
public class VersionActivity extends BaseActivity {
    private ListView listView;
    private SimpleAdapter adapter;
    private String[] time = {"v-2.8", "v-3.0", "v-3.10", "v-3.15", "v-3.17", "v-3.22", "v-3.24", "v-3.25"};
    private String[] content = {"APP端调整镇区的门户页，呈现出3X3或4X4布局的镇区LOGO信息,调整正文字体大小和间距，参考人民日报",
            "新增分享到QQ功能,查看活动，下面有大部分空白（IOS）,APP端数字报广告显示，屏幕下方有留白，尺寸已经修改，微信端显示无空白，但APP端仍存在",
            "后端开发制作一个H5的视频页，移动开发在APP里开发视频分享功能,专题支持子栏目（需后端开发修改接口）",
            "实现自定义列表呈现（后端开发一个新的应用插件[自定义列表]，由操作员自定义一个列表，包括视频、新闻、图集、活动、投票、直播等。）",
            "APP端新增话题呈现页（相关功能由后端开发新增一个应用插件[话题]进行实现）", "轮播图由客户自定义（后端开发一个新的应用插件，所有客户端进行相关接口调用）",
            "新闻内容里面嵌入图集（后端开发完内容页的H5版本后，所有APP进行替换）",  "1.首页自定义按钮 2.自定义添加专题,镇区类别 3.专题镇区内容列表添加分享 4.直播添加视频音频等"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_version);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);
        initTitleBar("版本日志",0,null);
        listView = (ListView) findViewById(R.id.lv_version);
        initView();
    }

    private void initView() {
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = time.length-1; i >-1; i--) {
            Map<String, String> map = new HashMap<>();
            map.put("time", time[i]);
            map.put("content", content[i]);
            list.add(map);
        }
        adapter = new SimpleAdapter(this, list, R.layout.activity_version_item, new String[]{"content", "time"}, new int[]{R.id.content, R.id.time});
        listView.setAdapter(adapter);
    }

}
