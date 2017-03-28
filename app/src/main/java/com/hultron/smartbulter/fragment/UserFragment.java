package com.hultron.smartbulter.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hultron.smartbulter.R;
import com.hultron.smartbulter.entity.MyUser;
import com.hultron.smartbulter.ui.BaseActivity;
import com.hultron.smartbulter.ui.CourierActivity;
import com.hultron.smartbulter.ui.LoginActivity;
import com.hultron.smartbulter.ui.PhoneActivity;
import com.hultron.smartbulter.ui.UserActivity;
import com.hultron.smartbulter.uitils.L;
import com.hultron.smartbulter.uitils.UtilTools;
import com.hultron.smartbulter.view.CustomDialog;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class UserFragment extends Fragment implements View.OnClickListener {

    private Button mExitLogin, mConUpdate;
    private TextView mEditUser;
    private EditText mUserName, mSex, mAge, mDesc;

    //圆形头像
    private CircleImageView mAddPic;
    private CustomDialog mDialog;
    private Button mCamera, mPicture, mCancel;

    //快递查询
    private TextView mCourier;
    //归属地查询
    private TextView mPhoLocation;


    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";
    public static final int TAKE_PHOTO = 0;
    public static final int CHOOSE_PHOTO = 1;
    public static final int CROP = 100;
    private File tempFile = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        findView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存圆形头像
        UtilTools.putImageToShare(getActivity(), mAddPic);
    }

    private void findView(View v) {
        mEditUser = (TextView) v.findViewById(R.id.edit_user);
        mEditUser.setOnClickListener(this);
        mUserName = (EditText) v.findViewById(R.id.et_username);
        mAge = (EditText) v.findViewById(R.id.et_ages);
        mSex = (EditText) v.findViewById(R.id.et_sex);
        mDesc = (EditText) v.findViewById(R.id.et_desc);
        mAddPic = (CircleImageView) v.findViewById(R.id.profile_image);
        mAddPic.setOnClickListener(this);


        UtilTools.getImageFromShare(getActivity(), mAddPic);

        mDialog = new CustomDialog(getActivity(), WindowManager.LayoutParams.MATCH_PARENT,
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
                mConUpdate.setVisibility(View.VISIBLE);
                setEnabled(true);
                break;
            case R.id.profile_image:
                mDialog.show();
                break;
            case R.id.btn_camera:
                openCamera();
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


    //打开相机
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断内存卡是否可用，可用的话就进行储存
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent, TAKE_PHOTO);
    }

    //打开相册
    private void openPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            //相机
            case TAKE_PHOTO:
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        PHOTO_IMAGE_FILE_NAME);
                photoCrop(Uri.fromFile(tempFile));
                break;
            //相册
            case CHOOSE_PHOTO:
                photoCrop(data.getData());
                break;
            //缩放
            case CROP:
                if (data == null) {
                    return;
                }
                //拿到图片设置
                setImageToView(data);
                //既然已经设置了图片，我们原先的应该删除
                if (tempFile != null) {
                    tempFile.delete();
                }
                break;
            default:
                break;
        }
    }

    //裁剪图片
    private void photoCrop(Uri uri) {
        if (uri == null) {
            L.e("uri == null");
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
        }
    }
}
