package com.wisdomrouter.app.fragment.bean;

import java.util.ArrayList;
import java.util.List;

import com.wisdomrouter.app.fragment.bean.AppConfigDao.Channel;

public class ReadColumn {
	int configid;
	ArrayList<Channel> channel;

	public int getConfigid() {
		return configid;
	}

	public void setConfigid(int configid) {
		this.configid = configid;
	}

	public List<Channel> getChannel() {
		return channel;
	}

	public void setChannel(ArrayList<Channel> listEntity1) {
		this.channel = listEntity1;
	}
}
