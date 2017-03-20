package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ProjectListDao;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;
import com.wisdomrouter.app.view.RatioImageView;

import java.util.List;

public class ProjectAdapter extends BaseAdapter {
    private Context mContext;
    private List<ProjectListDao> artlist;
    private LayoutInflater inflater;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public ProjectAdapter(Context context, List<ProjectListDao> artlist) {
        this.mContext = context;
        this.artlist = artlist;
        inflater = LayoutInflater.from(mContext);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolde viewHolde;
        ProjectListDao art = artlist.get(position);
		if(convertView == null){
			viewHolde = new ViewHolde();
			convertView = inflater.inflate(R.layout.fragment_project_item, null);
			viewHolde.ivHeader = (RatioImageView) convertView.findViewById(R.id.ivHeader);
			viewHolde.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolde.ivHeader.setAspectRatio(1.5f);
			convertView.setTag(viewHolde);
		}else{
			viewHolde = (ViewHolde) convertView.getTag();
		}
		
		viewHolde.tvTitle.setText(art.getName()==null?"":art.getName());
            //异步加载图片
        if (art.getIndexpic()!=null){
            imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(art.getIndexpic(),"400","600"), viewHolde.ivHeader);
        }
        return convertView;
    }

    class ViewHolde {
        RatioImageView ivHeader;
        TextView tvTitle;
        //TextView tvNum;
    }
}

