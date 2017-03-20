package com.wisdomrouter.app.fragment.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ta.utdid2.android.utils.StringUtils;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.fragment.bean.UserDao.Userinfo;
import com.wisdomrouter.app.utils.CustomDialog;
import com.wisdomrouter.app.utils.FileUtil;
import com.wisdomrouter.app.utils.ImageUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.CircleImageView;
import com.wisdomrouter.app.view.Crop;
import com.wisdomrouter.app.view.CropUtil;
import com.wisdomrouter.app.view.Log;
import com.wisdomrouter.app.view.RotateBitmap;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonActivity extends BaseDetailActivity {
    @ViewInject(id = R.id.iv_nick)
    CircleImageView iv_nick;
    @ViewInject(id = R.id.nickname)
    TextView nickname;
    @ViewInject(id = R.id.email)
    TextView email;
    @ViewInject(id = R.id.phone)
    TextView phone;
    @ViewInject(id = R.id.btn_exit, click = "exit")
    Button btn_exit;
    private final static String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/ROUTER/Portrait/";
    /**
     * 使用照相机拍照获取图
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /**
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    /**
     * 请求裁剪
     */
    public static final int REQUEST_CODE_GETIMAGE_BYCROP = 3;

    /**
     * 获取到的图片路径
     */
    private Uri origUri;
    private String protraitPath;
    private DisplayImageOptions options; // 配置图片加载及显示选项
    Handler1 handler1;

    private class Handler1 extends Handler {
        private WeakReference<PersonActivity> weakReference;

        public Handler1(PersonActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PersonActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case 1:
                    FileUtil.clearFileWithPath(FILE_SAVEPATH);
                    hideProgressDialog();
                    String result = (String) msg.obj;
                    if (!StringUtils.isEmpty(result)) {
                        CommError resultDao = new Gson().fromJson(result,
                                CommError.class);
                        if (resultDao != null) {
                            if (resultDao.getState() == 1) {
                                Userinfo userInfo = HandApplication.getInstance().mSpUtil
                                        .getAccount();
                                userInfo.setFacepic(resultDao.getPic());
                                HandApplication.getInstance().mSpUtil
                                        .saveAccount(userInfo);
                                HandApplication.user = HandApplication
                                        .getInstance().mSpUtil.getAccount();
                                imageLoader.displayImage(resultDao.getPic(),
                                        iv_nick, options);
                            }
                            WarnUtils.toast(PersonActivity.this,
                                    resultDao.getMessage());
                        }
                    } else {
                        WarnUtils.toast(PersonActivity.this, "更换头像失败,请稍后再试!");
                    }
                    break;

                default:
                    break;
            }
        }
    }
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_person);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        initTitleBar();
        initViews();
        handler1 = new Handler1(this);
    }

    private void initViews() {
        // 配置图片加载及显示选项（还有一些其他的配置，查阅doc文档吧）
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_man1) // 在ImageView加载过程中显示图片
                .showImageForEmptyUri(R.drawable.ic_man1) // image连接地址为空时
                .showImageOnFail(R.drawable.ic_man1) // image加载失败
                .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                .imageScaleType(ImageScaleType.EXACTLY) // 设置图片以如何的编码方式显示
                .cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
                .build(); // 创建配置过得DisplayImageOption对象

    }

    /**
     * 加载用户信息
     */
    private void loadUser() {
        ImageLoadingListener animateFirstListener = new ImageUtils.AnimateFirstDisplayListener();
        HandApplication.user = HandApplication.getInstance().mSpUtil
                .getAccount();
        if (HandApplication.user != null
                && HandApplication.user.getLikename() != null
                && !"".equals(HandApplication.user.getLikename())) {
            nickname.setText(HandApplication.user.getLikename());
            email.setText(HandApplication.user.getEmail());
            phone.setText(HandApplication.user.getMobile());
            imageLoader.displayImage(HandApplication.user.getFacepic(),
                    iv_nick, options, animateFirstListener);
            // 修改头像
            iv_nick.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    CustomDialog.Builder customDialog = new CustomDialog.Builder(
                            PersonActivity.this);
                    customDialog.setMessage("确定要修改头像？");
                    customDialog.setTitle("修改提示");
                    customDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    showPop();
                                }
                            });
                    customDialog.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    dialog.dismiss();
                                }
                            });
                    customDialog.create().show();
                }

            });
        }

    }

    private View mPopView;
    private PopupWindow mPopupWindow;
    private TextView tvFromCamera, tvTakeCamera, tvCancel;

    private void showPop() {

        mPopView = LayoutInflater.from(this).inflate(
                R.layout.popup_car_insurance, null);
        tvFromCamera = (TextView) mPopView.findViewById(R.id.tv_from_camera);// 相册选择
        tvTakeCamera = (TextView) mPopView
                .findViewById(R.id.tv_from_take_camera);// 拍照
        tvCancel = (TextView) mPopView.findViewById(R.id.tv_cancel);
        mPopupWindow = new PopupWindow(mPopView, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(mPopView, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
        changeWindowAlpha(0.5f);
        mPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                changeWindowAlpha(1f);
            }
        });
        tvFromCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {//相册选择
                hidenPop();
                pickPhoto();
            }
        });
        tvTakeCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {//拍照选择照片
                hidenPop();
                takePhoto();

            }
        });
        tvCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hidenPop();
            }
        });

    }

    private void hidenPop() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    private void changeWindowAlpha(float windowAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = windowAlpha;
        this.getWindow().setAttributes(lp);
    }

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        Intent intent;
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
//            savePath = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + "/router/Camera/";
            savePath=Environment.getExternalStorageState()+"/router/Camera/";
        }

        // 没有挂载SD卡，无法保存文件
        if (StringUtils.isEmpty(savePath)) {
            WarnUtils.toast(this, "无法保存照片，请检查SD卡是否挂载");
            return;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String fileName = "wisdomrouter_" + timeStamp + ".png";// 照片命名
        File out = new File(savePath, fileName);
        urill = Uri.fromFile(out);
        //实例化一个intent，并指定action
//        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, urill);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);


        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, SELECT_PIC_BY_TACK_PHOTO);
        }

    }

    Uri urill;

    /**
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    REQUEST_CODE_GETIMAGE_BYCROP);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    REQUEST_CODE_GETIMAGE_BYCROP);
        }
    }

    File savedir;

    /**
     * 通过uri获取文件的绝对路径
     *
     * @param uri
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getAbsoluteImagePath(Activity context, Uri uri) {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, proj, // Which columns to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }

        return imagePath;
    }

    private Uri sourceUri;
    private Uri saveUri;

    /**
     * 上传新照片
     */
    private void uploadNewPhoto() {
        // 图片旋转
        // protraitPath;
        sourceUri = outputUri;
        int exifRotation;
        RotateBitmap rotateBitmap = null;
        if (sourceUri != null) {
            exifRotation = HandApplication.degree;
            // exifRotation = CropUtil.getExifRotation(
            // CropUtil.getFromMediaUri( getContentResolver(), sourceUri));
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(sourceUri);
                BitmapFactory.Options option = new BitmapFactory.Options();
                rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is,
                        null, option), exifRotation);
            } catch (IOException e) {
                Log.e("Error reading image: " + e.getMessage(), e);
            } catch (OutOfMemoryError e) {
                Log.e("OOM reading image: " + e.getMessage(), e);
            } finally {
                CropUtil.closeSilently(is);
            }

            // 旋转图片 动作
            Matrix matrix = new Matrix();
            matrix.postRotate(exifRotation);

            // 创建新的图片
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    rotateBitmap.getBitmap(), 0, 0, rotateBitmap.getBitmap()
                            .getWidth(), rotateBitmap.getBitmap().getHeight(),
                    matrix, true);
            compressHeadPhoto(resizedBitmap);
        }

        postHeadPic();
    }

    private File rotateFile;

    private void compressHeadPhoto(final Bitmap bm) {

        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            WarnUtils.toast(this, "无法保存上传的头像，请检查SD卡是否挂载");
        }
        // 如果是标准uri
        String ext = FileUtil.getFileFormat(getAbsoluteImagePath(this,
                sourceUri));
        ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String fileName = "router_"
                + String.valueOf(System.currentTimeMillis()) + "." + ext;

        rotateFile = new File(FILE_SAVEPATH, fileName);

         //把bitmap放到 rotateFile 文件中
        try {
            bm.compress(Bitmap.CompressFormat.PNG, 70, new FileOutputStream(
                    rotateFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            bm.recycle();
        }

        protraitPath = rotateFile.getAbsolutePath();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case SELECT_PIC_BY_TACK_PHOTO:

                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                    if (data.getData() != null)
                    {
                        urill = data.getData();
                    }
                    else
                    {
                        urill  = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                    }
//                    urill=data.getData();
                    beginCrop(urill);// 拍照后裁剪
                    break;
                case REQUEST_CODE_GETIMAGE_BYCROP:// 选图后裁剪
                    beginCrop(data.getData());
                    break;
                case Crop.REQUEST_CROP:// 剪裁图像后
                    handleCrop(resultCode, data);
                    break;
            }

        }
    }

    /**
     * 剪裁头像判断
     *
     * @param resultCode
     * @param result
     */
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            uploadNewPhoto();

        } else if (resultCode == Crop.RESULT_ERROR) {
            WarnUtils.toast(this, Crop.getError(result).getMessage());
        }
    }

    private void beginCrop(Uri source) {
        boolean isCircleCrop = true;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            WarnUtils.toast(this, "无法保存上传的头像，请检查SD卡是否挂载");
        }
        // 如果是标准uri
        String ext = FileUtil.getFileFormat(getAbsoluteImagePath(this, source));
        ext = StringUtils.isEmpty(ext) ? "png" : ext;
        // 照片命名
        String fileName = "router_"
                + String.valueOf(System.currentTimeMillis()) + "." + ext;
        protraitPath = FILE_SAVEPATH + fileName;
        File cropFile = new File(savedir, fileName);
        outputUri = Uri.fromFile(cropFile);
        new Crop(source).output(outputUri).setCropType(isCircleCrop)
                .start(PersonActivity.this);
    }

    Uri outputUri;

    private void postHeadPic() {
        showProgressDialog("正在上传头像...");
        final String url = Const.HTTP_HEAD + "/user/updatepic";
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // String result = submitData(HandApplication.user.getOpenid(),
                // rotateFile, url);
                String result = submitData(HandApplication.user.getOpenid(),
                        protraitPath, url);
                Message message = Message.obtain();
                message.obj = result;
                message.what = 1;
                if (handler1 != null) {
                    handler1.sendMessage(message);
                }
            }
        });
        thread.start();
    }


    private String submitData(String userId, String picpath, String url) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (!StringUtils.isEmpty(picpath)) {
            params.add(new BasicNameValuePair("facepic", picpath));
        }
        params.add(new BasicNameValuePair("user_openid", userId));
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("TOKEN", Const.APPTOKEN);
        int responsecode;
        try {
            MultipartEntity entity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            for (int index = 0; index < params.size(); index++) {
                if (params.get(index).getName().equalsIgnoreCase("facepic")) {
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
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 设置左右标签,专门设置title_bar
     */
    private void initTitleBar() {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandApplication.degree = 0;
                finish();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText("个人信息");

        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();

    }

    /**
     * 退出登录
     *
     * @param v
     */
    public void exit(View v) {
        CustomDialog.Builder customDialog = new CustomDialog.Builder(
                PersonActivity.this);
        customDialog.setMessage("确定要注销个人用户信息吗？");
        customDialog.setTitle("退出登录");
        customDialog.setPositiveButton("退出",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        HandApplication.user = null;
                        HandApplication.getInstance().mSpUtil.saveAccount(null);
                        finish();
                    }
                });
        customDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        customDialog.create().show();
    }

}
