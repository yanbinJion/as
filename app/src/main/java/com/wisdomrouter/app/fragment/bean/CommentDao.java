package com.wisdomrouter.app.fragment.bean;

import com.wisdomrouter.app.utils.TimeUtil;

public class CommentDao {
	private String content; // 标题
	private String info_key; // key
	private int ischeck; // 审核情况
	private String createtime; // 创建时间
	private String info_class;// 类别
	private String likename;// 姓名
	private String title;//
	private String indexpic;//

	public String getIndepic() {
		return indexpic;
	}

	public void setIndepic(String indexpic) {
		this.indexpic = indexpic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getInfo_key() {
		return info_key;
	}

	public void setInfo_key(String info_key) {
		this.info_key = info_key;
	}

	public int getIscheck() {
		return ischeck;
	}

	public void setIscheck(int ischeck) {
		this.ischeck = ischeck;
	}

	public String getLikename() {
		return likename;
	}

	public void setLikename(String likename) {
		this.likename = likename;
	}

}
