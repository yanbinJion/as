package com.wisdomrouter.app.fragment.bean;

import java.util.List;

public class NewArticleListDao {
	private int state;
	private String message;
	private List<ArticleListDao> orders;
	private List<ArticleListDao> carousels;
	private List<ArticleListDao> normal;
	private List<ArticleListDao> activity;
	private List<ArticleListDao> newvote;

	public List<ArticleListDao> getActivity() {
		return activity;
	}

	public void setActivity(List<ArticleListDao> activity) {
		this.activity = activity;
	}

	public List<ArticleListDao> getNewvote() {
		return newvote;
	}

	public void setNewvote(List<ArticleListDao> newvote) {
		this.newvote = newvote;
	}

	public List<ArticleListDao> getOrders() {
		return orders;
	}

	public void setOrders(List<ArticleListDao> orders) {
		this.orders = orders;
	}

	public List<ArticleListDao> getCarousels() {
		return carousels;
	}

	public void setCarousels(List<ArticleListDao> carousels) {
		this.carousels = carousels;
	}

	public List<ArticleListDao> getNormal() {
		return normal;
	}

	public void setNormal(List<ArticleListDao> normal) {
		this.normal = normal;
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
