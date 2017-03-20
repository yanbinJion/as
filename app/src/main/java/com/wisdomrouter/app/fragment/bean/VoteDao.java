package com.wisdomrouter.app.fragment.bean;

import com.wisdomrouter.app.utils.TimeUtil;

public class VoteDao {
	private int voteid;
	private String title;
	private String createtime;
	private int infoid;
	private int adminid;
	private int appid;
	private int count;
	private int no;

	
	public int getVoteid() {
		return voteid;
	}
	public void setVoteid(int voteid) {
		this.voteid = voteid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreatetime() {
		String time = TimeUtil.getContentTime(Integer.parseInt(createtime));	
		return time;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public int getInfoid() {
		return infoid;
	}
	public void setInfoid(int infoid) {
		this.infoid = infoid;
	}
	public int getAdminid() {
		return adminid;
	}
	public void setAdminid(int adminid) {
		this.adminid = adminid;
	}
	public int getAppid() {
		return appid;
	}
	public void setAppid(int appid) {
		this.appid = appid;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
}
