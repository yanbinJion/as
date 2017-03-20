package com.wisdomrouter.app.test;

import com.wisdomrouter.app.tools.GlobalTools;

import android.test.AndroidTestCase;

public class GlobalQueryTest extends AndroidTestCase{
	
	public void testQuery() throws Exception{
		GlobalTools globalTool = new GlobalTools(getContext());
//		GlobalDao globalList = globalTool.getDataById(API.NEWS, "1", "1", "12");
		
//		for (ArticleDao newsDao : globalList.getInfo()) {
//			Logger.e("NewsDao:" + newsDao.getInfoid());
//		}
	}
}
