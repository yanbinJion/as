package com.wisdomrouter.app.fragment.bean;

public class NavDrawerItem {
	//标题
	private String title;
	//图标
	private int icon;	
	//判断是否显示
	private boolean isCounterVisible = false;
	//图标背景
	private String bg;

	public NavDrawerItem(){}

	public NavDrawerItem(String title){
		this.title = title;
	}
	
	public NavDrawerItem(String title, int icon){
		this.title = title;
		this.icon = icon;
	}
	
	public NavDrawerItem(String title, int icon, boolean isVisible){
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isVisible;
	}
	
	public NavDrawerItem(String title, int icon, boolean isVisible, String bg){
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isVisible;
		this.bg = bg;
	}
	
	public String getTitle(){
		return this.title;
	}

	public int getIcon(){
		return this.icon;
	}

	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public void setIcon(int icon){
		this.icon = icon;
	}

	public void setCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}

	public String getBg() {
		return bg;
	}

	public void setBg(String bg) {
		this.bg = bg;
	}
}
