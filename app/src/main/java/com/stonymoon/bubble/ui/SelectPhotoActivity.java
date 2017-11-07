package com.stonymoon.bubble.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.ContentBean;
import com.stonymoon.bubble.bean.JUserBean;
import com.stonymoon.bubble.util.HttpUtil;

import com.stonymoon.bubble.util.LogUtil;
import com.tamic.novate.callback.RxStringCallback;
import com.vondear.rxtools.RxBarTool;
import com.vondear.rxtools.RxPhotoTool;
import com.vondear.rxtools.RxSPTool;
import com.vondear.rxtools.activity.ActivityBase;
import com.vondear.rxtools.view.dialog.RxDialogChooseImage;
import com.vondear.rxtools.view.dialog.RxDialogScaleView;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.apaches.commons.codec.digest.DigestUtils;
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
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import static com.vondear.rxtools.view.dialog.RxDialogChooseImage.LayoutType.TITLE;

public class SelectPhotoActivity extends ActivityBase {
    //此处有bug，拿到token可能能给别人的用户替换头像


    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_profile_username)
    TextView tvUsername;
    private UploadManager mUploadManager;
    private Uri resultUri;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private String userId = "25";
    private String locationId = "926042754864151714";
    private String phone;
    private String url;
    public static void startActivity(Context context, String url, String username, String userId, String locationId, String phone) {
        Intent intent = new Intent(context, SelectPhotoActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("username", username);
        intent.putExtra("userId", userId);
        intent.putExtra("locationId", locationId);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }

    @OnClick(R.id.btn_profile_make_friend)
    void sendMessage() {
        ContactManager.sendInvitationRequest(phone, "", "请求加你为好友", new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                Log.v("SelectPhoto", responseMessage);
                Toast.makeText(SelectPhotoActivity.this, responseMessage, Toast.LENGTH_SHORT);
                if (0 == responseCode) {
                    //好友请求请求发送成功
                } else {
                    Toast.makeText(SelectPhotoActivity.this, "请求发送失败", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_select_photo);
        ButterKnife.bind(this);
        initView();
        initUpload();
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        locationId = intent.getStringExtra("locationId");
        url = intent.getStringExtra("url");
        String username = intent.getStringExtra("username");
        phone = intent.getStringExtra("phone");
        Glide.with(this).load(url).into(mIvAvatar);
        tvUsername.setText(username);
    }

    protected void initView() {
        Picasso.with(SelectPhotoActivity.this).load(url).into(mIvAvatar);
        mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDialogChooseImage();
            }
        });
        //todo 查看大图
//        mIvAvatar.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
////                RxImageTool.showBigImageView(mContext, resultUri);
//                RxDialogScaleView rxDialogScaleView = new RxDialogScaleView(mContext);
//
//                rxDialogScaleView.show();
//                return false;
//            }
//        });
    }

    private void initDialogChooseImage() {
        RxDialogChooseImage dialogChooseImage = new RxDialogChooseImage(mContext, TITLE);
        dialogChooseImage.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RxPhotoTool.GET_IMAGE_FROM_PHONE://选择相册之后的处理
                if (resultCode == RESULT_OK) {
//                    RxPhotoTool.cropImage(ActivityUser.this, );// 裁剪图片
                    initUCrop(data.getData());
                }

                break;
            case RxPhotoTool.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == RESULT_OK) {
                   /* data.getExtras().get("data");*/
//                    RxPhotoTool.cropImage(ActivityUser.this, RxPhotoTool.imageUriFromCamera);// 裁剪图片
                    initUCrop(RxPhotoTool.imageUriFromCamera);
                }

                break;
            case RxPhotoTool.CROP_IMAGE://普通裁剪后的处理
                Glide.with(mContext).
                        load(RxPhotoTool.cropImageUri).
                        diskCacheStrategy(DiskCacheStrategy.RESULT).
                        bitmapTransform(new CropCircleTransformation(mContext)).
                        thumbnail(0.5f).
                        placeholder(R.drawable.circle_elves_ball).
                        priority(Priority.LOW).
                        error(R.drawable.circle_elves_ball).
                        fallback(R.drawable.circle_elves_ball).
                        into(mIvAvatar);
//                RequestUpdateAvatar(new File(RxPhotoTool.getRealFilePath(mContext, RxPhotoTool.cropImageUri)));
                break;

            case UCrop.REQUEST_CROP://UCrop裁剪之后的处理
                if (resultCode == RESULT_OK) {

                    resultUri = UCrop.getOutput(data);
                    File image = roadImageView(resultUri, mIvAvatar);
                    uploadHead(image);


                    RxSPTool.putContent(mContext, "AVATAR", resultUri.toString());
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
                break;
            case UCrop.RESULT_ERROR: //UCrop裁剪错误之后的处理
                final Throwable cropError = UCrop.getError(data);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //从Uri中加载图片 并将其转化成File文件返回
    private File roadImageView(Uri uri, ImageView imageView) {
        Glide.with(mContext).
                load(uri).
                diskCacheStrategy(DiskCacheStrategy.RESULT).
                bitmapTransform(new CropCircleTransformation(mContext)).
                thumbnail(0.5f).
                placeholder(R.drawable.circle_elves_ball).
                priority(Priority.LOW).
                error(R.drawable.circle_elves_ball).
                fallback(R.drawable.circle_elves_ball).
                into(imageView);

        return (new File(RxPhotoTool.getImageAbsolutePath(this, uri)));
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
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimaryDark));

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

    //todo删除多了按钮
    //@OnClick(R.id.btn_exit)
    public void onClick() {
        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(this);
        rxDialogSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxDialogSureCancel.cancel();
            }
        });
        rxDialogSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rxDialogSureCancel.show();
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

    /***
     * 表单上传
     */
    private void upload(File data, String token, String name) {
        // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象

        //data = <File对象、或 文件路径、或 字节数组>

        mUploadManager.put(data, name, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                        Toast.makeText(SelectPhotoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

                        uploadUrl("http://oupl6wdxc.bkt.clouddn.com/" + key, (String) parameters.get("token"));

                    }
                }, null);
    }

    private void uploadHead(final File file) {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = "imageToken";
        parameters.clear();
        parameters.put("token", token);
        final String name = generateName(userId);
        parameters.put("name", name);

        HttpUtil.sendHttpRequest(SelectPhotoActivity.this).rxPost(url, parameters, new RxStringCallback() {
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

    //上传新的头像地址到java服务器和极光服务器上
    private void uploadUrl(final String headUrl, String token) {
        parameters.clear();
        parameters.put("token", token);
        parameters.put("url", headUrl);
        String url = "image";
        JUserBean bean = new JUserBean();
        bean.setUserExtras("url", headUrl);
        JMessageClient.updateMyInfo(UserInfo.Field.extras, bean, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                LogUtil.v("SelectPhoto", s);
            }
        });


        HttpUtil.sendHttpRequest(this).rxPost(url, parameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {

            }

            @Override
            public void onCancel(Object tag, com.tamic.novate.Throwable e) {

            }

            @Override
            public void onError(Object tag, com.tamic.novate.Throwable e) {
                Toast.makeText(SelectPhotoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }
        });
        HttpUtil.updateHead(this, locationId, headUrl);

    }

    //根据用户信息来生成头像名字
    private String generateName(String id) {
        int result = 0x93499820 ^ (7 * Integer.valueOf(id) - 1);
        String key = result + "";
        StringBuilder builder = new StringBuilder();
        for (byte b : key.getBytes()) {
            builder.append((char) (b + 20));
        }
        return builder.toString() + System.currentTimeMillis();
    }


}
