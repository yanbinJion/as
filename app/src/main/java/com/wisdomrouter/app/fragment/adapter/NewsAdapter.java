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
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.TimeUtil;
import com.wisdomrouter.app.utils.Utils;
import com.wisdomrouter.app.view.RatioImageView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends BaseAdapter {

    private static final int TYPE_ONE = 0;
    private static final int TYPE_TWO = 1;
    private static final int TYPE_THREE = 2;
    private static final int TYPE_FOUR = 3;

    private Context mContext;
    private List<ArticleListDao> artlist;
    private LayoutInflater inflater;
    protected ImageLoader imageLoader;
    private List<Integer> pList = new ArrayList<Integer>();

    public NewsAdapter(Context context, List<ArticleListDao> artlist) {
        this.mContext = context;
        this.artlist = artlist;
        inflater = LayoutInflater.from(mContext);
        imageLoader = ImageLoader.getInstance();
    }





    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) != null) {
            ArticleListDao articleListDao = (ArticleListDao) getItem(position);
            if (articleListDao.getShowtype() != null && articleListDao.getShowtype().equals("image")) {
                return TYPE_THREE;
            }
            if (articleListDao.getAdpic() != null) {
                return TYPE_FOUR;
            }
            if (StringUtil.isEmpty(articleListDao.getIndexpic())
                    || !Utils.patternUrl(articleListDao.getIndexpic())) {
                return TYPE_ONE;
            }
            return TYPE_TWO;
        }
        return TYPE_ONE;
    }

    @Override
    public int getCount() {
        return artlist != null ? artlist.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return artlist != null ? artlist.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void recordPosition(int position){
         pList.add(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ArticleListDao art = artlist.get(position);
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
                _holder.intro_comment.setText("浏览 "
                        + (art.getClick() == null ? "0" : art.getClick()));
                _holder.intro_title.setTextColor(mContext.getResources().getColor(R.color.text_color));
                for (int i = 0; i <pList.size() ; i++) {
                    if (pList.get(i)==position){
                        _holder.intro_title.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                    }
                }
            }
            return convertView;
        }
        if (getItemViewType(position) == TYPE_TWO) {
            ViewHolde _holder;
            if (convertView == null || convertView.getTag() == null) {
                _holder = new ViewHolde();
                convertView = inflater.inflate(R.layout.view_news_list_two, parent, false);
                _holder.pic_view = (RelativeLayout) convertView
                        .findViewById(R.id.pic_view);
                _holder.pic_title = (TextView) convertView.findViewById(R.id.pic_title);
                _holder.pic_img = (ImageView) convertView.findViewById(R.id.pic_img);
                _holder.pic_source = (TextView) convertView
                        .findViewById(R.id.pic_source);
                _holder.pic_time = (TextView) convertView.findViewById(R.id.pic_time);
                _holder.pic_comment = (TextView) convertView
                        .findViewById(R.id.pic_comment);
                convertView.setTag(_holder);
            } else {
                _holder = (ViewHolde) convertView.getTag();
            }
            if (art != null) {
                _holder.pic_title.setText(art.getTitle() == null ? "" : art.getTitle());
                _holder.pic_source.setText(art.getSource() == null ? ""
                        : art.getSource());
                if (art.getSource() == null || StringUtil.isEmpty(art.getSource())) {
                    _holder.pic_source.setVisibility(View.GONE);
                } else {
                    _holder.pic_source.setVisibility(View.VISIBLE);
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
                imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(art.getIndexpic(), "100", "150"), _holder.pic_img);
                _holder.pic_title.setTextColor(mContext.getResources().getColor(R.color.text_color));
                for (int i = 0; i <pList.size() ; i++) {
                    if (pList.get(i)==position){
                        _holder.pic_title.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                    }
                }
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
                _holder.three_time.setText(TimeUtil.getDay(art.getCreatetime()) == null ? ""
                        : TimeUtil.getDay(art.getCreatetime()));
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
                imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(art.getArticlepic()[0], "100", "150"),
                        _holder.pic_img01);
                imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(art.getArticlepic()[1], "100", "150"),
                        _holder.pic_img02);
                imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(art.getArticlepic()[2], "100", "150"),
                        _holder.pic_img03);
                _holder.pic_text.setTextColor(mContext.getResources().getColor(R.color.text_color));
                for (int i = 0; i <pList.size() ; i++) {
                    if (pList.get(i)==position){
                        _holder.pic_text.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                    }
                }
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
                _holder.top_time.setText(TimeUtil.getDay(art.getCreatetime()));
                _holder.intro_text.setText(art.getTitle() == null ? "" : art.getTitle());
                _holder.top_comment.setText("浏览 "
                        + (art.getClick() == null ? "0" : art
                        .getClick()));
                imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(art.getAdpic(), "300", "450"), _holder.top_img);
            }
            return convertView;
        }
        return convertView;
    }

    class ViewHolde {
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
        ImageView pic_img;
        RatioImageView pic_img01, pic_img02, pic_img03, top_img;
        TextView pic_time;
        TextView pic_comment;
        TextView pic_source;
    }
}
