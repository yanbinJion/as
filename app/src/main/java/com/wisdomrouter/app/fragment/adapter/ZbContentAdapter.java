package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.NavDrawerItem;
import com.wisdomrouter.app.fragment.bean.ZbContentDao.ListContent;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;
import com.wisdomrouter.app.utils.TimeUtil;
import com.wisdomrouter.app.view.BaseGridView;
import com.wisdomrouter.app.view.RatioImageView;
import com.wisdomrouter.app.view.image.ImageShowActivity;

import java.util.ArrayList;
import java.util.List;

public class ZbContentAdapter extends BaseAdapter {
    private Context mContext;
    private List<ListContent> artlist;
    private LayoutInflater inflater;
    protected static ImageLoader imageLoader = ImageLoader.getInstance();
    SampleAdapter2 sampleAdapter2;

    public ZbContentAdapter(Context context, List<ListContent> artlist) {
        this.mContext = context;
        this.artlist = artlist;
        inflater = LayoutInflater.from(mContext);
//
//        //  1.完成ImageLoaderConfiguration的配置
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
//                .memoryCacheExtraOptions(400, 400)          // default = device screen dimensions
//                .discCacheExtraOptions(400, 400, Bitmap.CompressFormat.JPEG, 75, null)
////                .taskExecutor(...)
////                .taskExecutorForCachedImages(...)
//                .threadPoolSize(3)                          // default
//                .threadPriority(Thread.NORM_PRIORITY - 1)   // default
//                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//                .memoryCache(new WeakMemoryCache())
//                .memoryCacheSize(2 * 1024 * 1024)
//                .memoryCacheSizePercentage(13)              // default
////                .discCache(new UnlimitedDiscCache(cacheDir))// default
//                .discCacheSize(50 * 1024 * 1024)        // 缓冲大小
//                .discCacheFileCount(100)                // 缓冲文件数目
//
//                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
//                .imageDownloader(new BaseImageDownloader(context)) // default
//                .imageDecoder(new BaseImageDecoder()) // default
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
////                .writeDebugLogs()
//                .build();
//        imageLoader.init(config);
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolde _holder;
        ListContent art = artlist.get(position);
        if (convertView == null) {
            _holder = new ViewHolde();
            convertView = inflater.inflate(R.layout.activity_live_item,
                    null);
            // 标题+简介+左图
            _holder.txt_person = (TextView) convertView
                    .findViewById(R.id.txt_person);
            _holder.txt_day = (TextView) convertView.findViewById(R.id.txt_day);
            _holder.txt_time = (TextView) convertView
                    .findViewById(R.id.txt_time);

            _holder.webView = (TextView) convertView.findViewById(R.id.webView);
            _holder.gv_item = (BaseGridView) convertView
                    .findViewById(R.id.gv_item);
//            _holder.iv_01 = (ImageView) convertView
//                    .findViewById(R.id.iv_01);
            convertView.setTag(_holder);
        } else
            _holder = (ViewHolde) convertView.getTag();

        if (art != null) {
            if (art.getPostman() != null) {
                if (art.getPostman() != null) {
                    if (art.getPostman().getName().length() > 3) {
                        _holder.txt_person.setText(art.getPostman().getName().substring(0, 3) + "...");
                    } else {
                        _holder.txt_person.setText(art.getPostman().getName());
                    }
                }
            }
            _holder.txt_time.setText(TimeUtil.getContentTime3(art
                    .getCreatetime()));
            _holder.txt_day.setText(TimeUtil.getStrDateG(art.getCreatetime()));
            _holder.webView.setText(art.getContent() == null ? "" : art
                    .getContent());
            if (art.getImages() != null && art.getImages().length != 0) {
                sampleAdapter2 = new SampleAdapter2(mContext);
                for (int i = 0; i < art.getImages().length; i++) {
                    sampleAdapter2.add(new NavDrawerItem(art.getImages()[i]));
                }
                _holder.gv_item.setAdapter(sampleAdapter2);
            }
//            imageLoader.displayImage(art.getImages()[0],
//                    _holder.iv_01, options);
            _holder.gv_item.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg, long arg3) {
                    ArrayList<String> imgsUrl = new ArrayList<String>();
                    for (int i = 0; i < artlist.get(position).getImages().length; i++) {
                        imgsUrl.add(artlist.get(position).getImages()[i]
                                .toString());
                    }
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("infos", imgsUrl);
                    intent.putExtra("pos", arg);
                    intent.setClass(mContext, ImageShowActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });

        }

        return convertView;
    }

    static class ViewHolde {
        // 标题+简介+右图
        TextView txt_person;
        TextView txt_time, txt_day;
        TextView webView;
        //       ImageView  iv_01;
        BaseGridView gv_item;
    }


    static class SampleAdapter2 extends ArrayAdapter<NavDrawerItem> {
        public SampleAdapter2(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            holder = new ViewHolder();
            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.live_image_item, null);
                holder.icon = (RatioImageView) convertView
                        .findViewById(R.id.iv_item);
                holder.icon.setAspectRatio(1f);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(getItem(position).getTitle(),"150","100"),
                    holder.icon);

            return convertView;
        }

        static class ViewHolder {
            RatioImageView icon;
        }
    }
}
