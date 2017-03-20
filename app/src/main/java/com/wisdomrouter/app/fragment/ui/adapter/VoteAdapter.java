package com.wisdomrouter.app.fragment.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;

import java.util.List;

public class VoteAdapter extends BaseAdapter {
    public List<ArticleListDao> list;
    private Context context;
    LayoutInflater inflater;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public VoteAdapter(Context context, List<ArticleListDao> list) {
        super();
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(this.context);

    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int location) {
        return list.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
         ArticleListDao child = list.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_vote_detail, null);
            holder.tvTitle = (TextView) convertView
                    .findViewById(R.id.news_title);
            holder.tvTime = (TextView) convertView.findViewById(R.id.news_time);
            holder.news_img = (ImageView) convertView
                    .findViewById(R.id.news_img);
            holder.txt = (TextView) convertView.findViewById(R.id.txt);
            holder.txt.setTag(position);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(child.getTitle());
        holder.tvTime.setText((child.getClick() == null ? "0" : child
                .getClick()) + "人参与");
        imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(child.getVotepic(),"100","150"), holder.news_img);

        return convertView;
    }

    public final class ViewHolder {
        TextView tvTitle;
        TextView tvTime, txt;
        ImageView news_img;
    }
}
