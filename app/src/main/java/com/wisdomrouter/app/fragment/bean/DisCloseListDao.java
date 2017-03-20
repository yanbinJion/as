package com.wisdomrouter.app.fragment.bean;

import java.io.Serializable;
import java.util.List;

import com.wisdomrouter.app.utils.TimeUtil;

public class DisCloseListDao implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private _id _id;
	private List<Replyinfo> replyinfo;
	private String content;
	private int state;
	private String title;
	private String createtime;
	private String indexpic;
	private String[] images;

	public String getIndexpic() {
		return indexpic;
	}

	public void setIndexpic(String indexpic) {
		this.indexpic = indexpic;
	}

	public class Replyinfo implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String replytime;
		String replycontent;
		String workername;
		public String getReplytime() {
			return replytime;
		}
		public void setReplytime(String replytime) {
			this.replytime = replytime;
		}
		public String getReplycontent() {
			return replycontent;
		}
		public void setReplycontent(String replycontent) {
			this.replycontent = replycontent;
		}
		public String getWorkername() {
			return workername;
		}
		public void setWorkername(String workername) {
			this.workername = workername;
		}

	}

	public class _id {
		private String $id;

		public String get$id() {
			return $id;
		}

		public void set$id(String $id) {
			this.$id = $id;
		}
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String[] getImages() {
		return images;
	}

	public void setImages(String[] images) {
		this.images = images;
	}

	public _id get_id() {
		return _id;
	}

	public void set_id(_id _id) {
		this._id = _id;
	}

	public List<Replyinfo> getReplyinfo() {
		return replyinfo;
	}

	public void setReplyinfo(List<Replyinfo> replyinfo) {
		this.replyinfo = replyinfo;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
