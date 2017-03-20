package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.VoteDetailsDao.Items;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.view.RatioImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VoteDetailAdapter extends BaseAdapter {
    private Context mContext;
    private List<Items> list;

    //已经选择的item数据
    private List<Items> checkedList = new ArrayList<>();

    private LayoutInflater inflater;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public interface OnDetailClickListener {
        void inDetail(int position);
    }

    private OnDetailClickListener detailClickListener;


    public void setOnDetailClickListener(OnDetailClickListener detailClickListener) {
        this.detailClickListener = detailClickListener;
    }

    public VoteDetailAdapter(Context context, List<Items> list) {
        this.mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list != null ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void select(Items info) {
        if (info != null) {
            if (checkedList.contains(info)) {
                checkedList.remove(info);
            } else {
                checkedList.add(info);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolde _holder;
        final Items itemVote = list.get(position);
        if (convertView == null) {
            _holder = new ViewHolde();

            convertView = inflater.inflate(R.layout.item_vote_detail, parent,
                    false);
            _holder.img = (RatioImageView) convertView
                    .findViewById(R.id.img_vote_detail);
            _holder.img.setAspectRatio(1.5f);
            _holder.tvName = (TextView) convertView
                    .findViewById(R.id.tv_vote_detail_item_name);
            _holder.tvNumber = (TextView) convertView
                    .findViewById(R.id.tv_vote_detail_item_number);
            _holder.tvCount = (TextView) convertView
                    .findViewById(R.id.tv_vote_detail_item_count);
            _holder.tvPost = (TextView) convertView
                    .findViewById(R.id.btn_vote_detail_item_do);
            _holder.vote_check = (CheckBox) convertView.findViewById(R.id.vote_check);
            convertView.setTag(_holder);
        } else {
            _holder = (ViewHolde) convertView.getTag();
        }
        if (itemVote != null) {
            if (!TextUtils.isEmpty(itemVote.getIndexpic()))
                imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(itemVote.getIndexpic(), "150", "100"), _holder.img);
            _holder.tvName.setText(itemVote.getTitle() == null ? "" : itemVote
                    .getTitle());
            if (!StringUtil.isEmpty(itemVote.getNo())) {
                int mposition = Integer.parseInt(itemVote.getNo());
                DecimalFormat nf = new DecimalFormat("000");
                _holder.tvNumber.setText("编号：" + nf.format(mposition));
            }
            _holder.tvCount.setText(itemVote.getCount() == 0 ? "得票数：0" : "得票数："
                    + itemVote.getCount());
            _holder.tvPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailClickListener.inDetail(position);
                }
            });
            if (checkedList.contains(itemVote)) {
                _holder.vote_check.setChecked(true);
            } else {
                _holder.vote_check.setChecked(false);
            }
        }
        return convertView;
    }

    class ViewHolde {
        RatioImageView img;
        TextView tvName;
        TextView tvNumber;
        TextView tvCount;
        TextView tvPost;
        CheckBox vote_check;

    }
}
