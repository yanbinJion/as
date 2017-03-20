package com.wisdomrouter.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.ImageUtils;

import java.util.Timer;
import java.util.TimerTask;

public class AdvActivity extends BaseDetailActivity {
    private ImageView iv;
    String key, url;
    private RelativeLayout tv;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new ImageUtils.AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adv);
        iv = (ImageView) findViewById(R.id.adv_image);
        tv = (RelativeLayout) findViewById(R.id.tv_adv);
        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                ActivityUtils.to(AdvActivity.this, MainActivity.class);
//                finish();
            }
        });

        if (getIntent() != null) {
            key = getIntent().getStringExtra("key");
            url = getIntent().getStringExtra("url");
        }
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.color.white)
                .showImageForEmptyUri(R.color.white)
                .showImageOnFail(R.color.white)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();

        imageLoader.displayImage(key, iv, options, animateFirstListener);
        if (!url.isEmpty()) {
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
            });
        }
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ActivityUtils.to(AdvActivity.this, MainActivity.class);
                finish();
            }
        };
        timer.schedule(timerTask, 2000);


    }


}
