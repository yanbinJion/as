package com.wisdomrouter.app.fragment.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ta.utdid2.android.utils.StringUtils;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CameraSdkParameterInfo;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.fragment.bean.ImageInfo;
import com.wisdomrouter.app.fragment.ui.adapter.GridViewAdapter;
import com.wisdomrouter.app.utils.FileUtil;
import com.wisdomrouter.app.utils.ImageUtils;
import com.wisdomrouter.app.utils.WarnUtils;

import net.tsz.afinal.annotation.view.ViewInject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BaoliaoActivity extends BaseDetailActivity {
    @ViewInject(id = R.id.tv_title)
    EditText tv_title;
    @ViewInject(id = R.id.tv_broking)
    EditText tvbroking;
    @ViewInject(id = R.id.noScrollgridview)
    GridView gvbroking;
    @ViewInject(id = R.id.left_btn)
    ImageView leftBtn;
    @ViewInject(id = R.id.title)
    TextView title;
    @ViewInject(id = R.id.right_btn)
    Button rightBtn;
    @ViewInject(id = R.id.btnOK, click = "Baoliao")
    Button btnOK;

    private MsgHandler handler;
    private List<ImageInfo> pic_list;
    private CameraSdkParameterInfo mCameraSdkParameterInfo = new CameraSdkParameterInfo();
    GridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.avtivity_listdisclose);
        initTitleBarForLeft("爆个料");
        handler = new MsgHandler(this);
        Const.MAX_SELECT_PICS = 6;

        initView();
    }

    public void Baoliao(View view) {
        if (StringUtils.isEmpty(tv_title.getText().toString())) {
            WarnUtils.toast(BaoliaoActivity.this, "爆料标题不可为空!");
            return;
        }
        if (StringUtils.isEmpty(tvbroking.getText().toString())) {
            WarnUtils.toast(BaoliaoActivity.this, "爆料内容不可为空!");
            return;
        }


        sendData();

    }

    protected void sendData() {
//		WarnUtils.toast(BaoliaoActivity.this, pic_list.get(0).getSource_image());
        showProgressDialog("正在提交...");
        new Thread(new Runnable() {

            @Override
            public void run() {
                handlerPic();
                String result = submitData(HandApplication.user.getOpenid(),
                        tvbroking.getText().toString(), Const.HTTP_HEADKZ
                                + "/plugin/broke-api/post");
                Message message = Message.obtain();
                message.obj = result;
                message.what = 1;
                if (handler != null) {
                    handler.sendMessage(message);
                }

            }
        }).start();

    }

    private List<String> sendPics = new ArrayList<>();

    protected String submitData(String openid, String content, String url) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_openid", openid));
        params.add(new BasicNameValuePair("title", tv_title.getText()
                .toString()));

        params.add(new BasicNameValuePair("content", content));
        int picSize = sendPics.size();
        if (picSize != 0) {
            for (int i = 0; i < sendPics.size(); i++) {
                params.add(new BasicNameValuePair("pic" + (i + 1), sendPics
                        .get(i).toString()));
            }
        }
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("TOKEN", Const.APPTOKEN);
        int responsecode;
        try {
            MultipartEntity entity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            for (int index = 0; index < params.size(); index++) {
                if (params.get(index).getName().equalsIgnoreCase("pic1")) {
                    entity.addPart(params.get(index).getName(), new FileBody(
                            new File(params.get(index).getValue())));

                }
                if (params.get(index).getName().equalsIgnoreCase("pic2")) {
                    entity.addPart(params.get(index).getName(), new FileBody(
                            new File(params.get(index).getValue())));

                }
                if (params.get(index).getName().equalsIgnoreCase("pic3")) {
                    entity.addPart(params.get(index).getName(), new FileBody(
                            new File(params.get(index).getValue())));

                }
                if (params.get(index).getName().equalsIgnoreCase("pic4")) {
                    entity.addPart(params.get(index).getName(), new FileBody(
                            new File(params.get(index).getValue())));

                }
                if (params.get(index).getName().equalsIgnoreCase("pic5")) {
                    entity.addPart(params.get(index).getName(), new FileBody(
                            new File(params.get(index).getValue())));

                }
                if (params.get(index).getName().equalsIgnoreCase("pic6")) {
                    entity.addPart(params.get(index).getName(), new FileBody(
                            new File(params.get(index).getValue())));

                } else {
                    entity.addPart(params.get(index).getName(),
                            new StringBody(params.get(index).getValue(),
                                    Charset.forName("UTF-8")));
                }
            }
            String Response = null;
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            responsecode = response.getStatusLine().getStatusCode();
            if (responsecode == 200) {
                HttpEntity resEntity = response.getEntity();
                Response = EntityUtils.toString(resEntity);
            }
            return Response;
        } catch (Exception e) {
            WarnUtils.toast(BaoliaoActivity.this, "图片选择出错!");
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //pic_list.clear();
        adapter.notifyDataSetChanged();
    }

    private String savePath;

    private void handlerPic() {
        File savedir = new File(savePath);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }

        for (int i = 0; i < pic_list.size(); i++) {
            ImageInfo imageInfo = pic_list.get(i);
            String imageUrl = imageInfo.getSource_image();
            //Bitmap bitmap = null;
            String theLarge = "";
            String theThumbnail = "";
            File imgFile = null;
            if (!StringUtils.isEmpty(imageUrl)) {
                theLarge = imageUrl;
                String largeFileName = FileUtil.getFileName(theLarge);
                // 生成上传的800宽度图片
                String thumbFileName = "thumb_" + largeFileName;
                theThumbnail = savePath + thumbFileName;
                try {
                    if (new File(theThumbnail).exists()) {
                        imgFile = new File(theThumbnail);
                    } else {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(theLarge, options);
                        int actualWidth = options.outWidth;
                        int actualHeight = options.outHeight;
                        if (actualWidth == 0 && actualHeight == 0) {
                            continue;
                        }
                        // 压缩上传的图片
                        ImageUtils.SaveBitmap(BaoliaoActivity.this, theLarge,
                                theThumbnail, 800, 80);
                        imgFile = new File(theThumbnail);
                    }
                    sendPics.add(theThumbnail);
                } catch (Exception e) {
                    WarnUtils.toast(BaoliaoActivity.this, "图片选择出错,请重选!" + e.getMessage());
                    return;
                }


            }
        }
    }

    private void initView() {
        savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/router/Camera/";
        pic_list = new ArrayList<ImageInfo>();
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setIsAddButton(true);
        pic_list.add(imageInfo);

        gvbroking.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridViewAdapter(this, pic_list);
        gvbroking.setAdapter(adapter);

        gvbroking.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                //这个方法的作用：
                hideKeyborder();
                ImageInfo info = (ImageInfo) arg0.getAdapter().getItem(pos);
                if (info.isAddButton()) {

                    ArrayList<String> list = new ArrayList<String>();
                    for (ImageInfo pic : pic_list) {
                        if (!pic.isAddButton()) {
                            list.add(pic.getSource_image());
                        }
                    }
                    openCameraSDKPhotoPick(BaoliaoActivity.this);// 图片选择
                } else {
                    openCameraSDKImagePreview(BaoliaoActivity.this,
                            info.getSource_image(), pos);// 照片
                }
            }

        });
    }

    // 图片预览
    public void openCameraSDKImagePreview(Activity activity, String path,
                                          int position) {

        Intent intent = new Intent();
        intent.setClass(BaoliaoActivity.this, PhotoActivity.class);
        Bundle b = new Bundle();
//		 b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER,
//		 mCameraSdkParameterInfo);
        b.putInt("pos", position);
        b.putString("url", path);
        intent.putExtras(b);
        startActivityForResult(intent,
                CameraSdkParameterInfo.TAKE_PICTURE_PREVIEW);
    }

    // 本地相册选择
    public void openCameraSDKPhotoPick(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(BaoliaoActivity.this, PhotoPickActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER,
                mCameraSdkParameterInfo);
        intent.putExtras(b);
        startActivityForResult(intent,
                CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);

    }

    CommError commError;
    Handler mHandler = new Handler();

    private class MsgHandler extends Handler {

        private WeakReference<BaoliaoActivity> weakReference;

        public MsgHandler(BaoliaoActivity activity) {
            weakReference = new WeakReference<BaoliaoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaoliaoActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }
            hideProgressDialog();
            switch (msg.what) {
                case 1:

                    String result = (String) msg.obj;
                    if (StringUtils.isEmpty(result)) {
                        WarnUtils.toast(BaoliaoActivity.this, "保存失败，请检查填写");
                        return;
                    }
                    commError = new Gson().fromJson(result, CommError.class);
                    if (commError.getState() == 1) {
                        WarnUtils.toast(BaoliaoActivity.this,
                                commError.getMessage() == null ? "爆料成功!"
                                        : commError.getMessage());
                        if (commError.getScore() != 0)
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    WarnUtils.toast(BaoliaoActivity.this, "爆料成功", commError.getScore());
                                }
                            }, 1000);

                        FileUtil.clearFileWithPath(savePath);
                        clearInfoMessage();


                    } else {
                        if (commError.getErrors() != null) {
                            if (commError.getErrors().getTitle() != null) {
                                WarnUtils.toast(BaoliaoActivity.this, commError
                                        .getErrors().getTitle()[0]);
                            } else if (commError.getErrors().getUser_openid() != null) {
                                WarnUtils.toast(BaoliaoActivity.this, commError
                                        .getErrors().getUser_openid()[0]);
                            } else if (commError.getErrors().getContent() != null) {
                                WarnUtils.toast(BaoliaoActivity.this, commError
                                        .getErrors().getContent()[0]);
                            } else if (commError.getErrors().getPic1() != null) {
                                WarnUtils.toast(BaoliaoActivity.this, commError
                                        .getErrors().getPic1()[0]);
                            } else if (commError.getErrors().getPic2() != null) {
                                WarnUtils.toast(BaoliaoActivity.this, commError
                                        .getErrors().getPic2()[0]);
                            } else {
                                WarnUtils.toast(BaoliaoActivity.this,
                                        commError.getMessage() == null ? "爆料失败!"
                                                : commError.getMessage());
                            }
                        }

                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void clearInfoMessage() {
        tvbroking.setText("");
        tv_title.setText("");
        pic_list.clear();
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setIsAddButton(true);
        pic_list.add(imageInfo);
        mCameraSdkParameterInfo = new CameraSdkParameterInfo();
        sendPics.clear();
        adapter.notifyDataSetChanged();
//		pic_list.clear();
//		adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    // 删除一个项
    private void deleteItem(int position) {
        pic_list.remove(position);
        boolean hasAddButton = false;
        for (ImageInfo info : pic_list) {
            if (info.isAddButton()) {
                hasAddButton = true;
            }
        }
        if (!hasAddButton) {
            ImageInfo info = new ImageInfo();
            info.setIsAddButton(true);
            pic_list.add(info);
        }
        adapter.notifyDataSetChanged();
    }

    private void getBundle(Bundle bundle) {
        if (bundle != null) {
            pic_list.clear();
            mCameraSdkParameterInfo = (CameraSdkParameterInfo) bundle
                    .getSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER);
            ArrayList<String> list = mCameraSdkParameterInfo.getImage_list();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    ImageInfo img = new ImageInfo();
                    img.setSource_image(list.get(i));
                    pic_list.add(img);
                }

            }
            if (pic_list.size() < Const.MAX_SELECT_PICS) {
                ImageInfo item = new ImageInfo();
                item.setIsAddButton(true);
                pic_list.add(item);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY:
                if (data != null) {
                    getBundle(data.getExtras());
                }
                break;
            case CameraSdkParameterInfo.TAKE_PICTURE_PREVIEW:
                if (data != null) {
                    int position = data.getIntExtra("position", -1);
                    if (position >= 0) {
                        mCameraSdkParameterInfo.getImage_list().remove(position);
                        deleteItem(position);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tv_title.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tvbroking.getWindowToken(), 0);
        return true;
    }
}
