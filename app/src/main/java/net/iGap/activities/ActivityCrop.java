/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageHelper;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.HelperCopyFile;

import java.io.File;
import java.io.IOException;

public class ActivityCrop extends ActivityEnhanced {

    AttachFile attachFile;
    private ImageView imgPic;
    private Uri uri;
    private String page;
    private String type;
    private int id;
    private String pathImageUser;
    private File mediaStorageDir;
    private File fileChat;
    private String result;
    private String path;
    private TextView txtSet;
    private String nzmeFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        attachFile = new AttachFile(this);

        ProgressBar prgWaiting = (ProgressBar) findViewById(R.id.crop_prgWaiting);
        AppUtils.setProgresColler(prgWaiting);
        imgPic = (ImageView) findViewById(R.id.pu_img_imageBefore);
        TextView txtAgreeImage = (TextView) findViewById(R.id.pu_txt_agreeImage);

        TextView txtCancel = (TextView) findViewById(R.id.pu_txt_cancel_crop);
        txtSet = (TextView) findViewById(R.id.pu_txt_set_crop);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            path = bundle.getString("IMAGE_CAMERA");
            String newPath = "file://" + path;
            uri = Uri.parse(newPath);

            page = bundle.getString("PAGE");
            type = bundle.getString("TYPE");
            id = bundle.getInt("ID");
        }
        if (uri != null || path != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(path), imgPic);
            } else {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(uri.getPath()), imgPic);
            }
            prgWaiting.setVisibility(View.GONE);
        }
        RippleView rippleCrop = (RippleView) findViewById(R.id.pu_ripple_crop);
        TextView txtCrop = (TextView) findViewById(R.id.pu_txt_crop);

        /*
        open crop page
         */
        if (uri != null && !uri.toString().equals("")) {
            rippleCrop.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

                @Override
                public void onComplete(RippleView rippleView) {

                    nzmeFile = path.substring(path.lastIndexOf("/"));
                    String newPath = "file://" + path;
                    Uri uri = Uri.parse(newPath);
                    UCrop.Options options = new UCrop.Options();
                    options.setStatusBarColor(ContextCompat.getColor(G.context, R.color.black));
                    options.setToolbarColor(ContextCompat.getColor(G.context, R.color.black));
                    options.setCompressionQuality(80);

                    UCrop.of(uri, Uri.fromFile(new File(G.DIR_IMAGES, nzmeFile)))
                            .withAspectRatio(16, 9)
                            .withOptions(options)
                            .start(ActivityCrop.this);

                }
            });
        }

        RippleView rippleBack = (RippleView) findViewById(R.id.pu_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) throws IOException {

                if (type.equals("camera") || type.equals("crop_camera")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        new AttachFile(ActivityCrop.this).dispatchTakePictureIntent();
                    } else {
                        new AttachFile(ActivityCrop.this).requestTakePicture();
                    }
                } else if (type.equals("gallery")) {
                    attachFile.requestOpenGalleryForImageMultipleSelect();
                }
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null && type.equals("crop_camera")) {
                    pathImageUser = getRealPathFromURI(uri);

                    result = G.imageFile.toString() + "_" + id + ".jpg";
                    HelperCopyFile.copyFile(pathImageUser, result);

                } else {
                    result = getRealPathFromURI(uri);
                }
                if (page != null) {
                    Intent data = new Intent();
                    data.setData(Uri.parse(result));
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    // result from crop

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == AttachFile.request_code_TAKE_PICTURE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = Uri.parse(AttachFile.mCurrentPhotoPath);
                imgPic.setImageURI(uri);
            } else {
                String filePath = null;
                ImageHelper.correctRotateImage(AttachFile.imagePath, true); //rotate image
                filePath = "file://" + AttachFile.imagePath;
                uri = Uri.parse(filePath);
                imgPic.setImageURI(uri);
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect) {

            String filePath = null;

            if (data.getData() == null) {
                return;
            }
            filePath = "file://" + AttachFile.getFilePathFromUri(data.getData());
            uri = Uri.parse(filePath);
            if (!filePath.toLowerCase().endsWith(".gif")) {
                imgPic.setImageURI(uri);
            } else {
                txtSet.performClick();
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            final Uri resultUri = UCrop.getOutput(data);
//            path = AttachFile.getFilePathFromUri(resultUri);
            if (resultUri != null) {
                path = resultUri.toString();
            }
            if (type.equals("camera")) {
                type = "crop_camera";
            } else {
                type = "gallery";
            }
            uri = resultUri;
            imgPic.setImageURI(resultUri);

        } else {
            Toast.makeText(ActivityCrop.this, R.string.can_not_save_image, Toast.LENGTH_SHORT).show();
        }
    }

    //======================================================================================================//uri to string

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
