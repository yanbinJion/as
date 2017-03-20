package com.wisdomrouter.app.fragment.bean;

public class EventList {
	private String title;
	private String indexpic;
	private String createtime;
	private String desc;
	private String starttime;
	private String endtime;
	private String key;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public EventList(String title, String indexpic, String createtime,
			String desc, String starttime, String endtime, String key) {
		super();
		this.title = title;
		this.indexpic = indexpic;
		this.createtime = createtime;
		this.desc = desc;
		this.starttime = starttime;
		this.endtime = endtime;
		this.key = key;
	}

	public String getIndexpic() {
		return indexpic;
	}

	public void setIndexpic(String indexpic) {
		this.indexpic = indexpic;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	
	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
