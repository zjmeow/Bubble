package com.stonymoon.bubble.ui.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.ContentBean;
import com.stonymoon.bubble.bean.JUserBean;
import com.stonymoon.bubble.ui.share.MapShareActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.HttpUtil;

import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.UrlUtil;
import com.tamic.novate.callback.RxStringCallback;
import com.vondear.rxtools.RxPhotoTool;
import com.vondear.rxtools.RxSPTool;
import com.vondear.rxtools.activity.ActivityBase;
import com.vondear.rxtools.view.dialog.RxDialogChooseImage;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.json.JSONObject;
import org.w3c.dom.Text;

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
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import static com.vondear.rxtools.view.dialog.RxDialogChooseImage.LayoutType.TITLE;

public class MyProfileActivity extends ActivityBase {

    @BindView(R.id.iv_edit_profile_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_edit_profile_username)
    TextView tvUsername;
    @BindView(R.id.tv_edit_profile_signature)
    TextView tvSignature;

    private UploadManager mUploadManager;
    private Uri resultUri;
    private Map<String, Object> parameters = new HashMap<String, Object>();

    private String url;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MyProfileActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);
        initView();
        initUpload();
        setProfile();
    }

    private void setProfile() {

        JMessageClient.getUserInfo(AuthUtil.getPhone(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                tvUsername.setText(userInfo.getDisplayName());
                tvSignature.setText(userInfo.getSignature());
                url = userInfo.getExtra("url");
                Picasso.with(MyProfileActivity.this).load(url).into(mIvAvatar);

            }
        });

    }



    protected void initView() {

        mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDialogChooseImage();
            }
        });


        mIvAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PhotoActivity.startActivity(MyProfileActivity.this, url);
                return true;
            }
        });
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

            case UCrop.REQUEST_CROP://UCrop裁剪之后的处理
                if (resultCode == RESULT_OK) {

                    resultUri = UCrop.getOutput(data);
                    File image = roadImageView(resultUri, mIvAvatar);
                    uploadHead(image);
                    Glide.with(mContext).
                            load(resultUri).
                            diskCacheStrategy(DiskCacheStrategy.RESULT).
                            thumbnail(0.5f).
                            into(mIvAvatar);

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
                        Toast.makeText(MyProfileActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

                        uploadUrl("http://oupl6wdxc.bkt.clouddn.com/" + key, (String) parameters.get("token"));

                    }
                }, null);
    }

    private void uploadHead(final File file) {
        String token = AuthUtil.getToken();
        String url = UrlUtil.getImageToken();
        parameters.clear();
        parameters.put("token", token);
        final String name = generateName(AuthUtil.getId());
        parameters.put("name", name);

        HttpUtil.sendHttpRequest(MyProfileActivity.this).rxPost(url, parameters, new RxStringCallback() {
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
        String url = UrlUtil.getImageUpload();
        JUserBean bean = new JUserBean();
        bean.setUserExtras("url", headUrl);
        JMessageClient.updateMyInfo(UserInfo.Field.extras, bean, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                LogUtil.v("MyProfile", s);
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
                Toast.makeText(MyProfileActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }
        });
        HttpUtil.updateHead(this, AuthUtil.getLocationId(), headUrl);

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

    @OnClick(R.id.iv_my_profile_back)
    void back() {
        finish();
    }

    @OnClick(R.id.iv_my_profile_edit)
    void edit() {
        showEditTextDialog();

    }


    private void showEditTextDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(MyProfileActivity.this);
        builder.setTitle("分享")
                .setPlaceholder("在此输入想留在地图上的文字")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            JUserBean bean = new JUserBean();
                            bean.setSignature(text.toString());
                            JMessageClient.updateMyInfo(UserInfo.Field.signature, bean, new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    LogUtil.v("MyProfile", s);
                                    Toast.makeText(MyProfileActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MyProfileActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }





}
