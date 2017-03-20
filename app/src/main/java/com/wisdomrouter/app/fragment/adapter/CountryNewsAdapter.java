package com.wisdomrouter.app.fragment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.utils.ImageUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.TimeUtil;
import com.wisdomrouter.app.utils.Utils;

import java.util.List;

public class CountryNewsAdapter extends BaseAdapter {
    private static final int TYPE_ONE = 0;
    private static final int TYPE_TWO = 1;
    private Context mContext;
    private LayoutInflater inflater;
    private List<ArticleListDao> mData;

    private DisplayImageOptions options; // 配置图片加载及显示选项
    private ImageLoadingListener animateFirstListener = new ImageUtils.AnimateFirstDisplayListener();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public CountryNewsAdapter(Context mContext, List<ArticleListDao> mData) {
        super();
        this.mContext = mContext;
        this.mData = mData;
        inflater = LayoutInflater.from(mContext);

        // 配置图片加载及显示选项（还有一些其他的配置，查阅doc文档吧）
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.load_default) // 在ImageView加载过程中显示图片
                .showImageForEmptyUri(R.color.transparent) // image连接地址为空时
                .showImageOnFail(R.drawable.load_fail) // image加载失败
                .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                .imageScaleType(ImageScaleType.EXACTLY) // 设置图片以如何的编码方式显示
                .cacheInMemory(true) // 加载图片时会在内存中加载缓存
                .cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
                .build(); // 创建配置过得DisplayImageOption对象
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mData != null ? mData.get(position) : null;
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
        if (getItem(position) != null) {
            ArticleListDao articleListDao = (ArticleListDao) getItem(position);
            if (StringUtil.isEmpty(articleListDao.getIndexpic())
                    || !Utils.patternUrl(articleListDao.getIndexpic())) {
                return TYPE_ONE;
            }
            return TYPE_TWO;
        }
        return TYPE_ONE;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolde _holder;
        ArticleListDao art = mData.get(position);
        if (getItemViewType(position) == TYPE_ONE) {
            _holder = new ViewHolde();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.view_news_list_one, null);
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
                _holder.intro_title.setText(art.getTitle());
                //_holder.intro_content.setText(art.getDesc());
                _holder.intro_source.setText(art.getSource() == null ? "" : art
                        .getSource());
                _holder.intro_time.setText(TimeUtil.getContentTime2(Integer.parseInt(art.getCreatetime())));
                _holder.intro_comment.setText("浏览 "
                        + (art.getClick() == null ? "0" : art.getClick()));
                if (art.getIsRead() != null && art.getIsRead()) {
                    _holder.intro_title.setTextColor(mContext.getResources()
                            .getColor(R.color.dark_gray));
                }
            }
            return convertView;
        }
        if (getItemViewType(position) == TYPE_TWO) {
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
                _holder.pic_title.setText(art.getTitle());
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
                imageLoader.displayImage(art.getIndexpic(),
                        _holder.pic_img, options, animateFirstListener);
                if (art.getIsRead() != null && art.getIsRead()) {
                    _holder.pic_title.setTextColor(mContext.getResources()
                            .getColor(R.color.dark_gray));
                }
            }
            return convertView;
        }
        return convertView;
    }

    class ViewHolde {
        // 标题+简介
        RelativeLayout intro_view;
        TextView intro_title;
        TextView intro_content;
        TextView intro_time;
        TextView intro_comment;
        TextView intro_source;
        // 标题+简介+左图
        RelativeLayout pic_view;
        TextView pic_title;
        TextView pic_content;
        ImageView pic_img;
        TextView pic_time;
        TextView pic_comment;
        TextView pic_source;
    }
}




