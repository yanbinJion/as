package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ServiceItem;
import com.wisdomrouter.app.tools.VolleyUtils.MyVolley;

import java.util.List;

public class ServiceListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<ServiceItem> serviceItems;
    private ImageLoader imageLoader;

    public ServiceListAdapter(Context context, List<ServiceItem> serviceItems) {
        this.mContext = context;
        this.serviceItems = serviceItems;
        inflater = LayoutInflater.from(mContext);
        imageLoader = MyVolley.getInstance(mContext).getImageLoader();
    }

    @Override
    public int getCount() {
        return serviceItems != null ? serviceItems.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return serviceItems != null ? serviceItems.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ServiceItem item = serviceItems.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(
                    R.layout.find_server_item, parent, false);
            holder.title = (TextView) convertView
                    .findViewById(R.id.txt_item);
            holder.icon = (NetworkImageView) convertView
                    .findViewById(R.id.iv_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (item != null) {
            holder.title.setText(item.getName() == null ? "" : item.getName());
            holder.icon.setImageUrl(
                    item.getPic() == null ? "" : item.getPic(),
                    imageLoader);
        }
        return convertView;
    }

    class ViewHolder {
        NetworkImageView icon;
        TextView title;
    }
}
