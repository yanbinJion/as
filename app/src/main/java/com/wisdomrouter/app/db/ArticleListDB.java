package com.wisdomrouter.app.db;

import net.tsz.afinal.annotation.sqlite.Table;

import com.wisdomrouter.app.utils.TimeUtil;
@Table(name="newsList")
public class ArticleListDB {
	private int _id;
	private String title; // 标题
	private String author;// 作者
	private String source;// 来源
	private String indexpic; // 列表图片
	private String createtime; // 日期
	private String click; // 点击次数
	private String desc; // 简介
	private String key;
	private String template;
	private Long comment;
	private String state;
	private String message;
	private String videopath;
	private String ColumnId;
	private String homeType;
	private int page;

	public String getColumnId() {
		return ColumnId;
	}

	public void setColumnId(String columnId) {
		ColumnId = columnId;
	}

	public Long getComment() {
		return comment;
	}

	public void setComment(Long comment) {
		this.comment = comment;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIndexpic() {
		return indexpic;
	}

	public void setIndexpic(String indexpic) {
		this.indexpic = indexpic;
	}

	public String getCreatetime() {
		String time = null;
		if (createtime != null) {
			time = TimeUtil.getContentTime2(Integer.parseInt(createtime));
		} else {
			time = createtime;
		}
		return time;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getClick() {
		return click;
	}

	public void setClick(String click) {
		this.click = click;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getHomeType() {
		return homeType;
	}

	public void setHomeType(String homeType) {
		this.homeType = homeType;
	}

	public String getVideopath() {
		return videopath;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setVideopath(String videopath) {
		this.videopath = videopath;
	}
}