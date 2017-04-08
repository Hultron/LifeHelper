package com.hultron.lifehelper.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.entity.MyUser;
import com.hultron.lifehelper.uitils.LogUtil;
import com.hultron.lifehelper.uitils.UtilTools;
import com.hultron.lifehelper.view.CustomDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * UserActivity
 */

public class UserActivity extends BaseActivity implements View.OnClickListener {

    /*
    * 请求码
    * */
    public static final int CAMERA = 1111;
    public static final int PICTURE = 1345;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1567;

    private TextView mEditUser;
    private EditText mUserName, mSex, mAge, mDesc;

    //圆形头像
    private CircleImageView mAddPic;
    private CustomDialog mDialog;
    private Button mCamera, mPicture, mCancel;
    private Button mConfirmUpdate;

    //照片以时间戳命名，避免重复
    public static final String PHOTO_IAMGE_FILE_NAME = "fileImg.jpg";
    public static final int TAKE_PHOTO = 4000;
    public static final int CHOOSE_PHOTO = 4001;
    public static final int CROP = 4002;
    private Uri imageUri;
    private File tempFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
    }

    private void initView() {
        mEditUser = (TextView) findViewById(R.id.edit_user);
        mEditUser.setOnClickListener(this);
        mUserName = (EditText) findViewById(R.id.et_username);
        mSex = (EditText) findViewById(R.id.et_sex);
        mAge = (EditText) findViewById(R.id.et_ages);
        mDesc = (EditText) findViewById(R.id.et_desc);
        mConfirmUpdate = (Button) findViewById(R.id.confirm_update);
        mConfirmUpdate.setOnClickListener(this);
        mAddPic = (CircleImageView) findViewById(R.id.profile_image);
        mAddPic.setOnClickListener(this);

        UtilTools.getImageFromShare(this, mAddPic);

        mDialog = new CustomDialog(this,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                R.layout.dialog_photo, R.style.pop_anim_style, Gravity.BOTTOM);
        //屏幕外点击无效
        mDialog.setCancelable(false);

        mCamera = (Button) mDialog.findViewById(R.id.btn_camera);
        mCamera.setOnClickListener(this);
        mPicture = (Button) mDialog.findViewById(R.id.btn_picture);
        mPicture.setOnClickListener(this);
        mCancel = (Button) mDialog.findViewById(R.id.btn_cancel);
        mCancel.setOnClickListener(this);

        //设置具体的值
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        mUserName.setText(userInfo.getUsername());
        mAge.setText(userInfo.getAge() + "");
        mSex.setText(userInfo.isSex() ? "男" : "女");
        mDesc.setText(userInfo.getDesc());
        setEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void setEnabled(boolean isEnabled) {
        mUserName.setEnabled(isEnabled);
        mSex.setEnabled(isEnabled);
        mAge.setEnabled(isEnabled);
        mDesc.setEnabled(isEnabled);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_user:
                //编辑资料
                mConfirmUpdate.setVisibility(View.VISIBLE);
                setEnabled(true);
                break;
            case R.id.confirm_update:
                //1.拿到输入框的值
                String username = mUserName.getText().toString().trim();
                String sex = mSex.getText().toString().trim();
                String age = mAge.getText().toString().trim();
                String desc = mDesc.getText().toString().trim();

                //2.判断是否为空
                if (!TextUtils.isEmpty(username) &&
                        !TextUtils.isEmpty(sex) &&
                        !TextUtils.isEmpty(age)) {
                    //3.更新属性
                    MyUser newUser = new MyUser();
                    newUser.setUsername(username);
                    newUser.setSex(sex.equals("男"));
                    newUser.setAge(Integer.parseInt(age));
                    if (!TextUtils.isEmpty(desc)) {
                        newUser.setDesc(desc);
                    } else {
                        newUser.setDesc("这个人很懒，什么都没有留下！！！");
                    }
                    setEnabled(false);
                    BmobUser bmobUser = BmobUser.getCurrentUser();
                    newUser.update(bmobUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(UserActivity.this, "更新用户信息成功",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserActivity.this, "更新用户信息失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
                mConfirmUpdate.setVisibility(View.GONE);
                break;
            case R.id.profile_image:
                mDialog.show();
                break;
            case R.id.btn_camera:

                performRequestPermissions("您必须授予应用相机权限，否则无法拍照",
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA, new PermissionsResultListener() {
                            @Override
                            public void onPermissionGranted() {
                                openCamera();
                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(UserActivity.this, "应用没有相机权限",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                mDialog.dismiss();
                break;
            case R.id.btn_picture:
                openPicture();
                mDialog.dismiss();
                break;
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;
        }
    }

    //打开相册
    private void openPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media
                .EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }


    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    //打开相机
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断内存卡是否可用，可用的话就进行储存
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES), "raw_avatar.png")));
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case OVERLAY_PERMISSION_REQ_CODE:
                if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                    //SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(this, "Permission Denied by user.", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this, "Permission allowed", Toast.LENGTH_SHORT).show();
                }
                break;
            //相机
            case TAKE_PHOTO:
                tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "raw_avatar.png");
                photoCrop(Uri.fromFile(tempFile));
                break;
            //相册
            case CHOOSE_PHOTO:
                photoCrop(data.getData());
                break;
            //裁剪
            case CROP:
                if (data == null) {
                    return;
                }
                //将图片设置为头像
                setImageToView(data);
                //将裁剪后的bitmap保存到本地
                Bitmap bitmap = data.getExtras().getParcelable("data");
                File tempAvatar = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "avatar.png");
                if (tempAvatar.exists()) {
                    tempAvatar.delete();
                }
                String avatarPath = saveBitmap(bitmap, Bitmap.CompressFormat.PNG,
                        100, tempAvatar);
                if (avatarPath != null) {
                    File avatarFile = new File(avatarPath);
                    uploadAvatar(avatarFile);
                }

                //既然已经设置了图片，我们原先的应该删除
                if (tempFile != null) {
                    tempFile.delete();
                }
                break;
            default:
                break;
        }
    }

    //把bitmap保存到项目关联目录
    public static String saveBitmap(Bitmap bitmap, Bitmap.CompressFormat format,
                                    int quality, File destFile) {
        try {
            FileOutputStream fos = new FileOutputStream(destFile);
            if (bitmap.compress(format, quality, fos)) {
                fos.flush();
                fos.close();
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    * 云端保存头像文件
    * */
    private void uploadAvatar(File file) {
        BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(UserActivity.this, "头像保存成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    LogUtil.e(e.toString());
                }
            }
        });
    }

    //裁剪图片
    private void photoCrop(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //裁剪宽高
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片的质量
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP);
    }

    //设置图片
    public void setImageToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            mAddPic.setImageBitmap(bitmap);
            UtilTools.putImageToShare(this, mAddPic);
        }
    }
}
