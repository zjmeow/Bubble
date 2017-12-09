package com.stonymoon.bubble.ui.share;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.base.StatusBarLightActivity;
import com.stonymoon.bubble.bean.ContentBean;
import com.stonymoon.bubble.ui.common.MyProfileActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.UrlUtil;
import com.stonymoon.bubble.view.MyDialog;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;
import com.vondear.rxtools.RxPhotoTool;
import com.vondear.rxtools.view.dialog.RxDialogChooseImage;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.vondear.rxtools.view.dialog.RxDialogChooseImage.LayoutType.TITLE;


public class ShareActivity extends StatusBarLightActivity {

    private static final String TAG = "ShareActivity";
    private static Map<String, Object> parameters = new HashMap<>();
    @BindView(R.id.et_share_title)
    EditText titleEt;
    @BindView(R.id.et_share_content)
    EditText contentEt;
    @BindView(R.id.iv_share_picture)
    ImageView shareImage;
    @BindView(R.id.checkBox_share)
    CheckBox checkBox;
    @BindView(R.id.iv_share_show_picture)
    ImageView ivShow;

    private double latitude;
    private double longitude;
    private Uri resultUri;
    private UploadManager mUploadManager;
    private String imageUrl;


    public static void startActivity(Context context, double latitude, double longitude) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        context.startActivity(intent);
    }

    @OnClick(R.id.iv_share_picture)
    void selectPicture() {
        initDialogChooseImage();
    }

    @OnClick(R.id.iv_share_back)
    void back() {
        finish();
    }

    @OnClick(R.id.tv_share_submit)
    void submit() {
        parameters.clear();
        String title = titleEt.getText().toString();
        String content = contentEt.getText().toString();
        if (title.equals("")) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("请输入标题")
                    .setConfirmText("嗯")
                    .show();
            return;

        }
        int anonymous;
        if (checkBox.isChecked()) {
            anonymous = 1;

        } else {
            anonymous = 0;
        }

        final MyDialog myDialog = new MyDialog(this);
        myDialog.showProgress("发送中");
        String token = AuthUtil.getToken();
        parameters.put("token", token);
        parameters.put("content", content);
        parameters.put("title", title);
        parameters.put("image", imageUrl);
        parameters.put("latitude", latitude);
        parameters.put("longitude", longitude);
        parameters.put("anonymous", anonymous);
        parameters.put("type", 0);
        String url = UrlUtil.getShare();
        HttpUtil.sendHttpRequest(this).rxPost(url, parameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                myDialog.success("发送成功");
                finish();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                LogUtil.e(TAG, e.toString());
                myDialog.fail("发送失败");
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapView.setCustomMapStylePath(getFilesDir().getAbsolutePath() + "/map_style.json");
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        getLocation();
        initUpload();

    }


    private void getLocation() {
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);


    }

    private void uploadPicture(final File file) {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = UrlUtil.getImageToken();
        parameters.clear();
        parameters.put("token", token);
        final String name = generateName();
        parameters.put("name", name);
        HttpUtil.sendHttpRequest(ShareActivity.this).rxPost(url, parameters, new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        Gson gson = new Gson();
                        String token = gson.fromJson(response, ContentBean.class).getContent();
                        upload(file, token, name);
                    }

                    @Override
                    public void onCancel(Object tag, com.tamic.novate.Throwable e) {

                    }

                    @Override
                    public void onError(Object tag, com.tamic.novate.Throwable e) {

                    }
                }


        );

    }

    private void upload(File data, String token, String name) {
        //data = <File对象、或 文件路径、或 字节数组>

        mUploadManager.put(data, name, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        imageUrl = "http://oupl6wdxc.bkt.clouddn.com/" + key;
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                        if (info.isOK()) {
                            Toast.makeText(ShareActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ShareActivity.this, "请检查网络或者文件大小不能超过5M", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, null);
    }

    private void initDialogChooseImage() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, RxPhotoTool.GET_IMAGE_FROM_PHONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RxPhotoTool.GET_IMAGE_FROM_PHONE://选择相册之后的处理
                if (resultCode == RESULT_OK) {
                    File image = roadImageView(data.getData());
                    uploadPicture(image);
                    Glide.with(ShareActivity.this).
                            load(data.getData()).
                            diskCacheStrategy(DiskCacheStrategy.RESULT).
                            thumbnail(0.5f).
                            into(ivShow);

                }

                break;
            case RxPhotoTool.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == RESULT_OK) {
                   /* data.getExtras().get("data");*/
//                    RxPhotoTool.cropImage(ActivityUser.this, RxPhotoTool.imageUriFromCamera);// 裁剪图片
                    initUCrop(RxPhotoTool.imageUriFromCamera);
                }

                break;
            case UCrop.RESULT_ERROR: //UCrop裁剪错误之后的处理
                final java.lang.Throwable cropError = UCrop.getError(data);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initUCrop(Uri uri) {
        //Uri destinationUri = RxPhotoTool.createImagePathUri(this);

        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));

        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), imageName + ".jpeg"));

        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //设置隐藏底部容器，默认显示
        //options.setHideBottomControls(true);
        //开始设置
        //设置最大缩放比例
        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1000, 1000)
                .withOptions(options)
                .start(this);
    }

    private File roadImageView(Uri uri) {
        return (new File(RxPhotoTool.getImageAbsolutePath(this, uri)));
    }

    private void initUpload() {

        Recorder recorder = new Recorder() {
            @Override
            public void set(String s, byte[] bytes) {
            }

            @Override
            public byte[] get(String s) {
                return new byte[0];
            }

            @Override
            public void del(String s) {
            }
        };

        //上传配置
        Configuration config = new Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认 256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认 512K
                .connectTimeout(10) // 链接超时。默认 10秒
                .responseTimeout(60) // 服务器响应超时。默认 60秒
                .recorder(recorder)  // recorder 分片上传时，已上传片记录器。默认 null
                .recorder(recorder, null)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认 Zone.zone0
                .build();
        // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
        mUploadManager = new UploadManager(config);
    }

    private String generateName() {
        String key = String.valueOf(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder();
        for (byte b : key.getBytes()) {
            builder.append((char) (b + 20));
        }
        return builder.toString();
    }


}
