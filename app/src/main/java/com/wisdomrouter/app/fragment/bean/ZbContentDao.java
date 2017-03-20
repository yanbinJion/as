package com.wisdomrouter.app.fragment.bean;

import java.util.List;

import com.wisdomrouter.app.utils.TimeUtil;

public class ZbContentDao {
	private String title;
	private String indexpic;
	private String content;
	private String createtime;
	private List<ListContent> list;
	private String key;

	public class ListContent {
		private Postman postman;
		private String content;
		private String createtime;
		private String[] images;

		public class Postman{
			private String type;
			private String key;
			private String name;
			private String facepic;
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public String getKey() {
				return key;
			}
			public void setKey(String key) {
				this.key = key;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getFacepic() {
				return facepic;
			}
			public void setFacepic(String facepic) {
				this.facepic = facepic;
			}
		}

		

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getCreatetime() {
			return createtime;
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


		public Postman getPostman() {
			return postman;
		}

		public void setPostman(Postman postman) {
			this.postman = postman;
		}
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public java.util.List<ListContent> getList() {
		return list;
	}

	public void setList(java.util.List<ListContent> list) {
		this.list = list;
	}
}
