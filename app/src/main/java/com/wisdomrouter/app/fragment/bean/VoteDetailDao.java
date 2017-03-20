package com.wisdomrouter.app.fragment.bean;

import java.util.List;

import com.wisdomrouter.app.utils.TimeUtil;


public class VoteDetailDao {
	
	private String title;
	private String content;
	private int createtime;
	private String key;
	private List<Items> items;
	
	public class Items{
		private String itemkey;
		private String title;
		public String getItemkey() {
			return itemkey;
		}
		public void setItemkey(String itemkey) {
			this.itemkey = itemkey;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		private int count;
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatetime() {
		if (createtime==0) {
			return null;
		}else{
		return TimeUtil.getStrDateG(createtime+"");}
	}

	public void setCreatetime(int createtime) {
		this.createtime = createtime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Items> getItems() {
		return items;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}
	

}
