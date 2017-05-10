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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperString;
import net.iGap.helper.ImageHelper;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.HelperCopyFile;

public class ActivityCrop extends ActivityEnhanced {

    private ImageView imgPic;
    private Uri uri;
    private String page;
    private String type;
    private int id;
    private String pathImageUser;
    private File mediaStorageDir;
    private File fileChat;
    private String result;
    AttachFile attachFile;
    private String path;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        attachFile = new AttachFile(this);

        ProgressBar prgWaiting = (ProgressBar) findViewById(R.id.crop_prgWaiting);
        imgPic = (ImageView) findViewById(R.id.pu_img_imageBefore);
        TextView txtAgreeImage = (TextView) findViewById(R.id.pu_txt_agreeImage);

        TextView txtCancel = (TextView) findViewById(R.id.pu_txt_cancel_crop);
        TextView txtSet = (TextView) findViewById(R.id.pu_txt_set_crop);
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

                @Override public void onComplete(RippleView rippleView) {
                    CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(120, 120)
                        .setAutoZoomEnabled(false)
                        .setInitialCropWindowPaddingRatio(.08f) // padding window from all
                        .setBorderCornerLength(50)
                        .setBorderCornerOffset(0)
                        .setAllowCounterRotation(true)
                        .setBorderCornerThickness(8.0f)
                        .setShowCropOverlay(true)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(false)
                        .setBorderCornerColor(getResources().getColor(R.color.whit_background))
                        .setBackgroundColor(getResources().getColor(R.color.ou_background_crop))
                        .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                        .start(ActivityCrop.this);
                }
            });
        }

        RippleView rippleBack = (RippleView) findViewById(R.id.pu_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) throws IOException {

                if (type.equals("camera") || type.equals("crop_camera")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        new AttachFile(ActivityCrop.this).dispatchTakePictureIntent();
                    } else {
                        new AttachFile(ActivityCrop.this).requestTakePicture();
                    }
                } else if (type.equals("gallery")) {
                    attachFile.requestOpenGalleryForImageSingleSelect();
                }
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                finish();
            }
        });

        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (uri != null && type.equals("crop_camera")) {
                    pathImageUser = getRealPathFromURI(uri);
                    switch (page) {
                        case "NewGroup":
                            String timeStampGroup = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                            result = G.IMAGE_NEW_GROUP.toString() + " " + timeStampGroup;
                            HelperCopyFile.copyFile(pathImageUser, result);

                            break;
                        case "NewChanel":
                            String timeStampChannel = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                            result = G.IMAGE_NEW_CHANEL.toString() + " " + timeStampChannel;
                            HelperCopyFile.copyFile(pathImageUser, result);

                            break;
                        case "chat":
                            mediaStorageDir = new File(G.DIR_IMAGES);
                            fileChat = new File(mediaStorageDir.getPath() + File.separator + "image_" + HelperString.getRandomFileName(3) + ".jpg");
                            result = fileChat.toString();
                            HelperCopyFile.copyFile(pathImageUser, result);
                            break;
                        default:

                            result = G.imageFile.toString() + "_" + id + ".jpg";
                            HelperCopyFile.copyFile(pathImageUser, result);
                            break;
                    }
                } else {
                    result = getRealPathFromURI(uri);
                }
                if (page != null) {
                    Intent data = new Intent();
                    data.setData(Uri.parse(result));
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }

                //}
            }
        });
    }

    // result from crop

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        } else if (resultCode == Activity.RESULT_OK && requestCode == AttachFile.request_code_image_from_gallery_single_select) {
            String filePath = null;

            if (data.getData() == null) {
                return;
            }
            filePath = "file://" + AttachFile.getFilePathFromUri(data.getData());
            uri = Uri.parse(filePath);
            imgPic.setImageURI(uri);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { // result for crop
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                if (type.equals("camera")) {
                    type = "crop_camera";
                } else {
                    type = "gallery";
                }
                uri = result.getUri();
                imgPic.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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
