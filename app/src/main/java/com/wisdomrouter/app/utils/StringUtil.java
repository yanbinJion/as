/**
 * @FILE:StringUtil.java
 * @AUTHOR:baifan
 * @DATE:2015-3-25 上午11:15:45
 **/
package com.wisdomrouter.app.utils;

import java.util.regex.Pattern;

public class StringUtil {

	private final static Pattern IMG_URL = Pattern
			.compile(".*?(gif|jpeg|png|jpg|bmp)");
	private final static Pattern number = Pattern.compile("^[0-9]+$");


	/**
	 * 判断一个url是否为图片url
	 *
	 * @param url
	 * @return
	 */
	public static boolean isImgUrl(String url) {
		if (url == null || url.trim().length() == 0)
			return false;
		return IMG_URL.matcher(url).matches();
	}

	public static Boolean isEmpty(String str) {
		if (str == null || str.equals("")) {
			return true;
		}
		return false;

	}

	/**
	 * 判断是否是手机号或者电话号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isNumberNO(String mobiles) {

		if (mobiles == null || mobiles.trim().length() == 0)
			return false;
		if (mobiles.length() <= 11) {
			return number.matcher(mobiles).matches();
		}
		return false;

	}
}
