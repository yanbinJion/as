package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CountryListDao;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;
import com.wisdomrouter.app.view.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/2/22.
 */
public class CountryAdapter extends BaseAdapter {
    private List<CountryListDao> list;
    private Context mContext;
    private LayoutInflater inflater;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    public CountryAdapter(List<CountryListDao> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder _viewHold = null;
        if (convertView == null) {
            _viewHold = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_country_item,
                    null);
            _viewHold.tv = (CircleImageView) convertView.findViewById(R.id.town_img);
            _viewHold.name = (TextView) convertView.findViewById(R.id.town_name);
            convertView.setTag(_viewHold);
        } else {
            _viewHold = (ViewHolder) convertView.getTag();
        }
        CountryListDao listDao = list.get(position);
        if(listDao!=null){
            if (listDao.getTownpic().size()>0){
                imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(listDao.getTownpic().get(0).getPic(),"100","100"),_viewHold.tv);
            }
            _viewHold.name.setText(listDao.getName()==null?"":listDao.getName());
        }

        return convertView;
    }

    class ViewHolder {
        CircleImageView tv;
        TextView name;
    }

}
