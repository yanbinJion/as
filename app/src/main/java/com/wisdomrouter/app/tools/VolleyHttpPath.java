package com.wisdomrouter.app.tools;

public class VolleyHttpPath {
   
	
	//��ַ
	private static final String IP="192.168.75.1:8081";
	
	//get�ύ��װ ����
	public static String getSharedIfo(int id){
		return "http://"+IP+"/GoodsServers/app/getSharedIfo.php?id="+id;
	}
	//post�ύֻ��װ ��ַ url
	public static String getSharedIfo_post(){
		return "http://"+IP+"/GoodsServers/app/getSharedIfo.php";
	}
	//һ��ͼƬ�ĵ�ַ
	public static String getPic(){
		return "http://"+IP+"/GoodsServers/image/201508171125128512.jpg";
	}
	
	//������ַ
	public static String getBasicPath(){
		return "http://"+IP+"/GoodsServers";
	}
	
	public static String getSharedAll(int ps,int pg){
		return "http://"+IP+"/GoodsServers/app/getAllIfo.php?pg="+pg+"& ps="+ps;
	}
	
	public static String getSharedAll(){
		return "http://"+IP+"/GoodsServers/app/getAllIfo.php?ps=10&pg=1";
	}
	
}
