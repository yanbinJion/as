package com.wisdomrouter.app.fragment.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wisdomrouter.app.fragment.bean.AppConfigBean.Module.Channel;
import com.wisdomrouter.app.R;

public class LifeTypeAdapter extends BaseAdapter {

    public List<Channel> list;
    private Context context;
    LayoutInflater inflater;
    
    public LifeTypeAdapter(Context context, List<Channel> list) {
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
    public Channel getItem(int location) {
        return list.get(location);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder holder = null;
        Channel sev = list.get(position);
        
        if(convertView == null){
            holder = new ViewHolder();
            
            convertView = inflater.inflate(R.layout.fragment_life_type, null); 
            holder.txtTitle = (TextView)convertView.findViewById(R.id.town_item);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        if(sev != null){
        	holder.txtTitle.setText(list.get(position).getName());
        }

        return convertView;  
    }  
    
    public final class ViewHolder{
    	public TextView txtTitle;
    }
    
}


