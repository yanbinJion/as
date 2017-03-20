package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CustomDao;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.TimeUtil;
import com.wisdomrouter.app.view.RatioImageView;

import java.util.List;


public class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ONE = 0;
    private static final int TYPE_TWO = 1;
    private static final int TYPE_THREE = 2;
    private static final int TYPE_FOUR = 3;

    private List<CustomDao> daoList;
    private Context mContext;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    public CustomAdapter(List<CustomDao> daoList, Context mContext) {
        this.daoList = daoList;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) != null) {
            CustomDao customDao = (CustomDao) getItem(position);
            if (customDao.getPic()!=null&&customDao.getPic().length==3&&customDao.getInfo_class().equals("article")){
                return TYPE_THREE;
            }
            if (customDao.getIndexpic()!=null){
                if (customDao.getInfo_class().equals("weilive")){
                    return TYPE_FOUR;
                }
                return TYPE_TWO;
            }
            return TYPE_ONE;
        }
        return TYPE_ONE;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getCount() {
        return daoList == null ? 0 : daoList.size();
    }

    @Override
    public Object getItem(int position) {
        return daoList==null?null:daoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomDao art = daoList.get(position);
        if (getItemViewType(position) == TYPE_ONE) {
            ViewHolde _holder;
            if (convertView == null || convertView.getTag() == null) {
                _holder = new ViewHolde();
                convertView = inflater.inflate(R.layout.view_news_list_one, parent, false);
                // 标题+简介
                _holder.intro_title = (TextView) convertView
                        .findViewById(R.id.intro_title);
                _holder.intro_time = (TextView) convertView
                        .findViewById(R.id.intro_time);
                _holder.intro_comment = (TextView) convertView
                        .findViewById(R.id.intro_comment);
                _holder.intro_source = (TextView) convertView
                        .findViewById(R.id.intro_source);
                convertView.setTag(_holder);
            } else {
                _holder = (ViewHolde) convertView.getTag();
            }
            if (art != null) {
                _holder.intro_title.setText(art.getTitle() == null ? "" : art.getTitle());
                _holder.intro_source.setText(art.getSource() == null ? "" : art
                        .getSource());
                if (art.getSource() == null || StringUtil.isEmpty(art.getSource())) {
                    _holder.intro_source.setVisibility(View.GONE);
                } else {
                    _holder.intro_source.setVisibility(View.VISIBLE);
                }
                _holder.intro_time.setText(TimeUtil.getDay(art.getCreatetime()));
            }
            return convertView;
        }
        if (getItemViewType(position) == TYPE_TWO) {
            ViewHolde _holder;
            if (convertView == null || convertView.getTag() == null) {
                _holder = new ViewHolde();
                convertView = inflater.inflate(R.layout.view_news_list_two, parent, false);
                _holder.pic_view = (RelativeLayout) convertView.findViewById(R.id.pic_view);
                _holder.pic_title = (TextView) convertView.findViewById(R.id.pic_title);
                _holder.pic_img = (ImageView) convertView.findViewById(R.id.pic_img);
                _holder.pic_video = (ImageView) convertView.findViewById(R.id.pic_video);
                _holder.pic_source = (TextView) convertView.findViewById(R.id.pic_source);
                _holder.pic_time = (TextView) convertView.findViewById(R.id.pic_time);
                _holder.pic_custom = (TextView) convertView.findViewById(R.id.pic_custom);
                _holder.pic_comment = (TextView) convertView.findViewById(R.id.pic_comment);
                convertView.setTag(_holder);
            } else {
                _holder = (ViewHolde) convertView.getTag();
            }
            if (art != null) {
                _holder.pic_title.setText(art.getTitle() == null ? "" : art.getTitle());
                _holder.pic_custom.setVisibility(View.VISIBLE);
                _holder.pic_video.setVisibility(View.GONE);
                _holder.pic_source.setText(art.getSource() == null ? ""
                        : art.getSource());
                if (art.getSource() == null || StringUtil.isEmpty(art.getSource())) {
                    _holder.pic_source.setVisibility(View.GONE);
                } else {
                    _holder.pic_source.setVisibility(View.VISIBLE);
                }
                if (art.getInfo_class().equals("images")){
                    _holder.pic_custom.setText("图片");
                }else if (art.getInfo_class().equals("article")){
                    _holder.pic_custom.setText("图文");
                    _holder.pic_custom.setVisibility(View.GONE);
                }else if (art.getInfo_class().equals("activity")){
                    _holder.pic_custom.setText("活动");
                }else if (art.getInfo_class().equals("newvote")){
                    _holder.pic_custom.setText("投票");
                }else if (art.getInfo_class().equals("township")){
                    _holder.pic_custom.setText("镇区");
                }else if (art.getInfo_class().equals("special")){
                    _holder.pic_custom.setText("专题");
                }else if (art.getInfo_class().equals("link")){
                    _holder.pic_custom.setText("网页");
                }else if (art.getInfo_class().equals("video")){
                    _holder.pic_custom.setVisibility(View.GONE);
                    _holder.pic_video.setVisibility(View.VISIBLE);
                    _holder.pic_video.setBackgroundResource(R.drawable.custom_video);
                }
                if (art.getCreatetime() == null) {
                    _holder.pic_time.setVisibility(View.GONE);
                } else {
                    _holder.pic_time.setVisibility(View.VISIBLE);
                    _holder.pic_time.setText(TimeUtil.getDay(art.getCreatetime()));
                }
                _holder.pic_comment.setText("浏览 "
                        + (art.getClick() == null ? "0" : art
                        .getClick()));
                imageLoader.displayImage(art.getIndexpic(), _holder.pic_img);
//                if (art.getIsRead() != null && art.getIsRead()) {
//                    _holder.pic_title.setTextColor(mContext.getResources()
//                            .getColor(R.color.text_gray));
//                }
            }
            return convertView;
        }
        if (getItemViewType(position) == TYPE_THREE) {
            ViewHolde _holder;
            if (convertView == null || convertView.getTag() == null) {
                _holder = new ViewHolde();
                convertView = inflater.inflate(R.layout.view_news_list_three, parent, false);
                _holder.three_view = (LinearLayout) convertView
                        .findViewById(R.id.three_view);
                _holder.pic_text = (TextView) convertView.findViewById(R.id.pic_text);
                _holder.pic_img01 = (RatioImageView) convertView
                        .findViewById(R.id.pic_img01);
                _holder.pic_img02 = (RatioImageView) convertView
                        .findViewById(R.id.pic_img02);
                _holder.pic_img03 = (RatioImageView) convertView
                        .findViewById(R.id.pic_img03);
                _holder.three_source = (TextView) convertView
                        .findViewById(R.id.three_source);
                _holder.three_time = (TextView) convertView
                        .findViewById(R.id.three_time);
                _holder.three_comment = (TextView) convertView
                        .findViewById(R.id.three_comment);
                _holder.pic_img01.setAspectRatio(1.5f);
                _holder.pic_img02.setAspectRatio(1.5f);
                _holder.pic_img03.setAspectRatio(1.5f);
                convertView.setTag(_holder);
            } else {
                _holder = (ViewHolde) convertView.getTag();
            }
            if (art != null) {
                _holder.three_time.setText(TimeUtil.getDay(art.getCreatetime()));
                _holder.pic_text.setText(art.getTitle() == null ? "" : art.getTitle());
                _holder.three_source.setText(art.getSource() == null ? ""
                        : art.getSource());
                if (art.getSource() == null || StringUtil.isEmpty(art.getSource())) {
                    _holder.three_source.setVisibility(View.GONE);
                } else {
                    _holder.three_source.setVisibility(View.VISIBLE);
                }
                _holder.three_comment.setText("浏览 "
                        + (art.getClick() == null ? "0" : art.getClick()));
                imageLoader.displayImage(art.getPic()[0],
                        _holder.pic_img01);
                imageLoader.displayImage(art.getPic()[1],
                        _holder.pic_img02);
                imageLoader.displayImage(art.getPic()[2],
                        _holder.pic_img03);
//                if (art.getIsRead() != null && art.getIsRead()) {
//                    _holder.pic_text.setTextColor(mContext.getResources()
//                            .getColor(R.color.text_gray));
//                }
            }
            return convertView;
        }
        if (getItemViewType(position) == TYPE_FOUR) {
            ViewHolde _holder;
            if (convertView == null || convertView.getTag() == null) {
                _holder = new ViewHolde();
                convertView = inflater.inflate(R.layout.view_news_list_four, parent, false);
                _holder.top_view = (LinearLayout) convertView
                        .findViewById(R.id.top_view);
                _holder.intro_text = (TextView) convertView
                        .findViewById(R.id.intro_text);
                _holder.top_img = (RatioImageView) convertView.findViewById(R.id.top_img);
                _holder.top_img.setAspectRatio(2f);
                _holder.top_source = (TextView) convertView
                        .findViewById(R.id.top_source);
                _holder.top_time = (TextView) convertView.findViewById(R.id.top_time);
                _holder.custom_pic = (TextView) convertView.findViewById(R.id.custom_pic);
                _holder.top_comment = (TextView) convertView
                        .findViewById(R.id.top_comment);
                convertView.setTag(_holder);
            } else {
                _holder = (ViewHolde) convertView.getTag();
            }
            if (art != null) {
                _holder.top_source.setText(art.getSource() == null ? ""
                        : art.getSource());
                if (art.getSource() == null || StringUtil.isEmpty(art.getSource())) {
                    _holder.top_source.setVisibility(View.GONE);
                } else {
                    _holder.top_source.setVisibility(View.VISIBLE);
                }
                _holder.custom_pic.setVisibility(View.VISIBLE);
                _holder.custom_pic.setText("直播");
                _holder.top_time.setText(TimeUtil.getDay(art.getCreatetime()));
                _holder.intro_text.setText(art.getTitle() == null ? "" : art.getTitle());
                _holder.top_comment.setText("浏览 "
                        + (art.getClick() == null ? "0" : art
                        .getClick()));
                imageLoader.displayImage(art.getIndexpic(), _holder.top_img);
            }
            return convertView;
        }
        return convertView;
    }
    static class ViewHolde {
        // 标题+简介
        TextView intro_title;
        TextView intro_time;
        TextView intro_comment;
        TextView intro_source;
        // 标题+简介+左图
        LinearLayout three_view, top_view;
        RelativeLayout pic_view;
        TextView pic_title;
        TextView pic_text, intro_text;
        TextView three_comment, three_time, three_source,
                top_comment, top_time, top_source;
        ImageView pic_img,pic_video;
        RatioImageView pic_img01, pic_img02, pic_img03, top_img;
        TextView pic_time;
        TextView pic_comment;
        TextView pic_source,pic_custom,custom_pic;
    }
}
