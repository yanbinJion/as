package com.wisdomrouter.app.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wisdomrouter.app.R;

/**
 * Created by Administrator on 2016/1/14.
 */
public class ImageOpUtils {
    public static DisplayImageOptions getImgOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.load_default)
                .showImageForEmptyUri(R.drawable.transparent)
                .showImageOnFail(R.drawable.load_default)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        return options;
    }

    public static DisplayImageOptions getAvatarOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_man1)
                .showImageForEmptyUri(R.drawable.ic_man1)
                .showImageOnFail(R.drawable.ic_man1)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        return options;
    }
}
