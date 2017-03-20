package com.wisdomrouter.app.fragment.bean;

import java.io.Serializable;
import java.util.List;

public class CountryListDao implements Serializable{
	private static final long serialVersionUID = 1L;
	public String key;
	public String name;
	public String towndesc;
	public String createtime;
	public List<Townpic> townpic;
	public String count;
	public List<Detail> detail;
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

	public String getTowndesc() {
		return towndesc;
	}

	public void setTowndesc(String towndesc) {
		this.towndesc = towndesc;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public List<Townpic> getTownpic() {
		return townpic;
	}

	public void setTownpic(List<Townpic> townpic) {
		this.townpic = townpic;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public List<Detail> getDetail() {
		return detail;
	}

	public void setDetail(List<Detail> detail) {
		this.detail = detail;
	}

	public class Townpic implements Serializable{
		private String pic;
		private String desc;
		private static final long serialVersionUID = 1L;
		public String getPic() {
			return pic;
		}

		public void setPic(String pic) {
			this.pic = pic;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
	public class Detail implements Serializable{
		private String key;
		private String name;
		private String listname;
		private String infocount;
		private static final long serialVersionUID = 1L;
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

		public String getListname() {
			return listname;
		}

		public void setListname(String listname) {
			this.listname = listname;
		}

		public String getInfocount() {
			return infocount;
		}

		public void setInfocount(String infocount) {
			this.infocount = infocount;
		}
	}
}
