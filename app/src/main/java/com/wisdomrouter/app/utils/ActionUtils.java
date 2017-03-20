package com.wisdomrouter.app.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.wisdomrouter.app.tools.GlobalTools;

public class ActionUtils {
	private GlobalTools globalTool;
	private Context mContext;
	private String getResult = null;
	private String zanResult = null;
	
	public ActionUtils(Context context){
		this.mContext = context;
		globalTool = new GlobalTools(mContext);
	}
	
	public void Digg(String id, String uid,String classType){
		new DiggInitTask(id, uid,classType).execute(); 
	}
	
	public void Favourite(String infoid, String uid,String classtype,String isCollect){
		new FavouriteInitTask(infoid, uid,classtype,isCollect).execute();
	}
	/**
	 * 发送赞请求
	 */
	private class DiggInitTask extends AsyncTask<Void, Integer, String> {
		int ERRORCODE =1;
		String message = null;
		String id;
		String uid;
		String classType;

		public DiggInitTask(String id, String uid,String classType){
			this.id = id;
			this.uid = uid;
			this.classType = classType;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				zanResult = globalTool.setDiggByUserIdAndInfoId(uid, id,classType);
			}			
			catch (Exception e) {
				e.printStackTrace();
				publishProgress(ERRORCODE);
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {			
			if(ERRORCODE == values[0]){
				WarnUtils.toast(mContext, message);
			}
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {	
			if(zanResult != null){
				WarnUtils.toast(mContext, zanResult);
			}
			super.onPostExecute(result);
		}
	}
	
	/**
	 * 发送收藏
	 */
	private class FavouriteInitTask extends AsyncTask<Void, Integer, String> {
		int ERRORCODE =1;
		String infoid = null;
		String uid = null;
		String classtype = null;
		String message = null;
		String isCollect = null;

		public FavouriteInitTask(String infoid, String uid,String classtype,String isCollect){
			this.infoid = infoid;
			this.uid = uid;
			this.classtype = classtype;
			this.isCollect = isCollect;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
					getResult = globalTool.setFavourByUserIdAndInfoId(uid, infoid,classtype,isCollect); 
			}			
			catch (Exception e) {
				e.printStackTrace();
				message = e.getMessage();
				publishProgress(ERRORCODE);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {			
			if(ERRORCODE == values[0]){
				WarnUtils.toast(mContext, message);
			}
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {	
			if(getResult != null){
				WarnUtils.toast(mContext, getResult);
			}
			super.onPostExecute(result);
		}
	}
}

