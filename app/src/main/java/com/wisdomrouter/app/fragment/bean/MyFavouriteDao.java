
package com.wisdomrouter.app.fragment.bean;

import com.wisdomrouter.app.utils.TimeUtil;


public class MyFavouriteDao {
	private String title; //标题
	private String key; //key
	private String createtime; //创建时间
	private String info_class;//类别
	private String indexpic;//图片
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCreatetime() {
		return TimeUtil.getStrDate2(createtime);
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getInfo_class() {
		return info_class;
	}
	public void setInfo_class(String info_class) {
		this.info_class = info_class;
	}
	public String getIndexpic() {
		return indexpic;
	}
	public void setIndexpic(String indexpic) {
		this.indexpic = indexpic;
	}

}

