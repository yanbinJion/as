
/**
 * @FILE:HomeListDao.java
 * @AUTHOR:baifan
 * @DATE:2015-6-5 上午11:22:24
 **/
package com.wisdomrouter.app.fragment.bean;

import java.util.List;

/**
 * @COMPANY:南京路特软件有限公司
 * @CLASS:HomeListDao
 * @DESCRIPTION:
 * @AUTHOR:wangfanghui
 * @VERSION:v1.0
 * @DATE:2015-6-5 上午11:22:32
 */
public class HomeListDao {
	private int state;
	private String message;
	List<ArticleListDao> list;
	List<ArticleListDao> toplb;

	public List<ArticleListDao> getList() {
		return list;
	}

	public void setList(List<ArticleListDao> list) {
		this.list = list;
	}

	public List<ArticleListDao> getToplb() {
		return toplb;
	}

	public void setToplb(List<ArticleListDao> toplb) {
		this.toplb = toplb;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

