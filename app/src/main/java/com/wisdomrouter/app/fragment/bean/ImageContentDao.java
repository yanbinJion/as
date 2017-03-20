package com.wisdomrouter.app.fragment.bean;

import java.util.List;

import com.wisdomrouter.app.utils.TimeUtil;


public class ImageContentDao {
	private String title; //标题
	private String author;//作者
	private String source;//来源
	private String indexpic; //列表图片
	private List<Gallery> gallery;
	private String createtime; //日期
	private String click; //点击次数
	private String desc; //简介
	private String info_class; //类别
	private int  iscollect; //是否收藏
	
	public int getIscollect() {
		return iscollect;
	}
	public void setIscollect(int iscollect) {
		this.iscollect = iscollect;
	}
	public String getInfo_class() {
		return info_class;
	}
	public void setInfo_class(String info_class) {
		this.info_class = info_class;
	}
	private String key; 
	private Long comment;
	
	public String getCreatetime() {
		return TimeUtil.getContentTime2(Integer.parseInt(createtime));
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
	public Long getComment() {
		return comment;
	}
	public void setComment(Long comment) {
		this.comment = comment;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getIndexpic() {
		return indexpic;
	}
	public void setIndexpic(String indexpic) {
		this.indexpic = indexpic;
	}
	
	public class Gallery{
		private String url; //图片地址
		private String text;//图片描述
		private Resourceid resourceid;//来源
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public Resourceid getResourceid() {
			return resourceid;
		}
		public void setResourceid(Resourceid resourceid) {
			this.resourceid = resourceid;
		}
		
	}
	public class Resourceid{
		private String $id; //id

		public String get$id() {
			return $id;
		}

		public void set$id(String $id) {
			this.$id = $id;
		}
	}
	public List<Gallery> getGallery() {
		return gallery;
	}
	public void setGallery(List<Gallery> gallery) {
		this.gallery = gallery;
	}

}
