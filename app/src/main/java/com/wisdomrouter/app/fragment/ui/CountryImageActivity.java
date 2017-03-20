package com.wisdomrouter.app.fragment.ui;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.view.RatioImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 镇区图片简介页
 */
public class CountryImageActivity extends BaseActivity {
    @Bind(R.id.img)
    RatioImageView imgview;
    @Bind(R.id.content)
    TextView contentview;
    private String img, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_country_img);
        ButterKnife.bind(this);
        initTitleBar("简介", 0, null);

        if (getIntent() != null) {
            img = getIntent().getStringExtra("img");
            content = getIntent().getStringExtra("content");
        }
        initView();

    }

    private void initView() {
        imgview.setAspectRatio(1.5f);
        Glide.with(this).load(img).placeholder(R.drawable.load_default).into(imgview);
        contentview.setText(content);
    }

}
