package com.wisdomrouter.app.utils;

/**
 * Created by Administrator on 2016/5/18 0018.
 */
public class ChangeImgUrlUtils {
    public static String nativetoslt(String nativeurl, String height, String width) {
        if (nativeurl!=null){
        nativeurl = nativeurl.replaceAll("http://storage.tmtsp.com", "http://img.storage.tmtsp.com");
//        1e_1c_0o_1l_200h_300w.src //等比例缩放 宽高不够 留白

        nativeurl += "@1e_1c_0o_1l_" + height + "h_" + width + "w.png";} //等比例切
        return nativeurl;
    }
}
