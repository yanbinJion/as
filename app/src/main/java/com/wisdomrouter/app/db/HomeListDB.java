/**
 * @FILE:HomeListDB.java
 * @AUTHOR:baifan
 * @DATE:2015-6-5 上午11:26:25
 **/
package com.wisdomrouter.app.db;

import com.wisdomrouter.app.fragment.bean.ArticleListDao;

import net.tsz.afinal.annotation.sqlite.Table;

import java.util.List;

/**
 * @COMPANY:南京路特软件有限公司
 * @CLASS:HomeListDB
 * @DESCRIPTION:	
 * @AUTHOR:wangfanghui
 * @VERSION:v1.0
 * @DATE:2015-6-5 上午11:26:31
 */
@Table(name="homelist")
public class HomeListDB {
	private int _id;
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
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
}

