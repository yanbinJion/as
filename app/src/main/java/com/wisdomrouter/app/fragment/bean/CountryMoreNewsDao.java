package com.wisdomrouter.app.fragment.bean;

import com.wisdomrouter.app.utils.TimeUtil;

public class CountryMoreNewsDao {
	private String newstitle;
	private String newstype;
	private String newspic;
	private String nwscontent;
	private String newsid;
	private String nwstime;

	public String getNwstime() {
		String time = null;
		if (nwstime != null) {
			time = TimeUtil.getDay(nwstime);
		} else {
			time = nwstime;
		}
		return time;
	}

	public void setNwstime(String nwstime) {
		this.nwstime = nwstime;
	}

	public String getNewstitle() {
		return newstitle;
	}

	public void setNewstitle(String newstitle) {
		this.newstitle = newstitle;
	}

	public String getNewstype() {
		return newstype;
	}

	public void setNewstype(String newstype) {
		this.newstype = newstype;
	}

	public String getNewspic() {
		return newspic;
	}

	public void setNewspic(String newspic) {
		this.newspic = newspic;
	}

	public String getNwscontent() {
		return nwscontent;
	}

	public void setNwscontent(String nwscontent) {
		this.nwscontent = nwscontent;
	}

	public String getNewsid() {
		return newsid;
	}

	public void setNewsid(String newsid) {
		this.newsid = newsid;
	}
}
