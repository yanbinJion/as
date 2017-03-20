package com.wisdomrouter.app.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.ta.utdid2.android.utils.StringUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;

public class ShareUtils {

//    public static void initShareSdk(Activity context) {
//
//    }
//
//    public static void shareSdk(Activity context, String title,
//                                String imageUrl, String infoId, String type) {
//
//    }

    public static void shareSdk(Activity context, String title, String desc,
                                String imageUrl, String infoId, String type, UMShareListener umShareListener) {
        UMImage umImage;
        String shareUrl=null;
        if (TextUtils.isEmpty(imageUrl)) {
            umImage = new UMImage(context, R.drawable.ic_share_app);
        } else {
            umImage = new UMImage(context, R.drawable.ic_share_app);
        }
        if (TextUtils.isEmpty(desc)||desc.equals("")) {
            desc = title;
        }
        // 推荐给好友
        if (type.equals(Const.SHARE_API.FRIEND)) {
            shareUrl = "http://fenxiang.tmtsp.com/site/download.html?clientid=5551a6dd27f38e8c09000029";
        }
        //文章 图片
        else if (type.equals(Const.SHARE_API.ARTICLE)) {
            shareUrl = Const.SHARE_URL + Const.APPSHAREID + "-"
                    + infoId + ".html";
        }
        //专题
        else if (type.equals(Const.SHARE_API.PROJECT)) {
            shareUrl = Const.SHARE_URL + Const.APPSHAREID + "--"
                    + infoId + ".html";
        }
        //镇区内容列表分享
        else if(type.equals(Const.SHARE_API.COUNTRY)){
            shareUrl = Const.SHARE_URL + Const.APPSHAREID + "--"
                    + infoId + ".html?type=1";
        }
        //活动分享
        else if (type.equals(Const.SHARE_API.AVTIVITY)) {
            shareUrl = Const.SHARE_URL + Const.APPSHAREID + "-"
                    + infoId + ".html?type=2";
        }
        //投票分享
        else if (type.equals(Const.SHARE_API.VOTES)) {
            shareUrl = Const.SHARE_URL + Const.APPSHAREID + "-"
                    + infoId + ".html?type=3";
        }
        //直播分享
        else if (type.equals(Const.SHARE_API.LIVES)) {
            shareUrl = Const.SHARE_URL + "weilive/"+Const.APPSHAREID + "-"
                    + infoId + ".html";
        }
        new ShareAction(context).withText(desc).withTitle(title).withMedia(umImage).withTargetUrl(shareUrl)
                .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(umShareListener).open();
    }
}
