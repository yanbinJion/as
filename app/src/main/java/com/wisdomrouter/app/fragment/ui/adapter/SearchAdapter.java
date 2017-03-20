package com.wisdomrouter.app.fragment.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.TimeUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/12/15.
 */
public class SearchAdapter extends BaseAdapter {
    HashMap<Integer, View> lmap = new HashMap<Integer, View>();
    private Context mContext;
    private List<ArticleListDao> artlist;
    private LayoutInflater inflater;

    public SearchAdapter(Context mContext, List<ArticleListDao> artlist) {
        this.mContext = mContext;
        this.artlist = artlist;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return artlist == null ? 0 : artlist.size();
    }

    @Override
    public Object getItem(int position) {
        return artlist == null ? null : artlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHold;
        ArticleListDao art = artlist.get(position);
        if (lmap.get(position) == null) {
            viewHold = new ViewHolder();
            convertView = inflater.inflate(R.layout.view_news_list_one, null);
            // 标题+简介
            viewHold.intro_title = (TextView) convertView
                    .findViewById(R.id.intro_title);
            viewHold.intro_content = (TextView) convertView
                    .findViewById(R.id.intro_content);
            viewHold.intro_time = (TextView) convertView
                    .findViewById(R.id.intro_time);
            viewHold.intro_source = (TextView) convertView
                    .findViewById(R.id.intro_source);
            lmap.put(position, convertView);
            convertView.setTag(viewHold);
        } else {
            convertView = lmap.get(position);
            viewHold = (ViewHolder) convertView.getTag();
        }
        if (art != null) {
            viewHold.intro_title.setText(art.getTitle() == null ? "" : art.getTitle());
            viewHold.intro_source.setText(art.getSource() == null ? "" : art
                    .getSource());
            if (art.getSource() == null || StringUtil.isEmpty(art.getSource())) {
                viewHold.intro_source.setVisibility(View.GONE);
            } else {
                viewHold.intro_source.setVisibility(View.VISIBLE);
            }
            viewHold.intro_time.setText(TimeUtil.getDay(art.getCreatetime() == null ? "" : art.getCreatetime()));
        }
        return convertView;
    }

    class ViewHolder {
        TextView intro_title, intro_content, intro_source, intro_time;
    }
}
