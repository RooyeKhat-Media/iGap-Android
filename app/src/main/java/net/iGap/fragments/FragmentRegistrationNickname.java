/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentRegistrationNicknameBinding;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnUserAvatarResponse;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.EditTextAdjustPan;
import net.iGap.module.FileUploadStructure;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestUserAvatarAdd;
import net.iGap.viewmodel.FragmentRegistrationNicknameViewModel;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class FragmentRegistrationNickname extends BaseFragment implements OnUserAvatarResponse {

    public final static String ARG_USER_ID = "arg_user_id";
    public static boolean IsDeleteFile;
    public static Bitmap decodeBitmapProfile = null;
    private TextView txtTitle;
    private net.iGap.module.CircleImageView btnSetImage;
    private Uri uriIntent;
    private String pathImageUser;
    private int idAvatar;
    private boolean existAvatar = false;


    private FragmentRegistrationNicknameViewModel fragmentRegistrationNicknameViewModel;
    private FragmentRegistrationNicknameBinding fragmentRegistrationNicknameBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentRegistrationNicknameBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration_nickname, container, false);
        return fragmentRegistrationNicknameBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();

        ProgressBar prgWait = fragmentRegistrationNicknameBinding.prg;
        AppUtils.setProgresColler(prgWait);

        txtTitle = fragmentRegistrationNicknameBinding.puTitleToolbar;

        Typeface titleTypeface;
        if (!HelperCalander.isPersianUnicode) {
            titleTypeface = G.typeface_neuropolitical;
        } else {
            titleTypeface = G.typeface_IRANSansMobile;
        }
        txtTitle.setTypeface(titleTypeface);

        btnSetImage = fragmentRegistrationNicknameBinding.puProfileCircleImage;

        AndroidUtils.setBackgroundShapeColor(btnSetImage, Color.parseColor(G.appBarColor));
        btnSetImage.setOnClickListener(new View.OnClickListener() { // button for set image
            @Override
            public void onClick(View view) {
                if (!existAvatar) {
                    startDialog();
                }
            }
        });

        //        txtInputNickName.setHint("Nickname");

        final EditTextAdjustPan edtNikName = fragmentRegistrationNicknameBinding.puEdtNikeName;

        edtNikName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
            @Override
            public void result(String path, String message) {

                pathImageUser = path;

                int lastUploadedAvatarId = idAvatar + 1;

                fragmentRegistrationNicknameViewModel.showProgressBar();
                HelperUploadFile.startUploadTaskAvatar(pathImageUser, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        if (progress < 100) {
                            fragmentRegistrationNicknameBinding.prg.setProgress(progress);
                        } else {
                            new RequestUserAvatarAdd().userAddAvatar(struct.token);
                        }
                    }

                    @Override
                    public void OnError() {
                        fragmentRegistrationNicknameViewModel.hideProgressBar();
                    }
                });
            }
        };

    }

    private void initDataBinding() {

        fragmentRegistrationNicknameViewModel = new FragmentRegistrationNicknameViewModel(getArguments(), fragmentRegistrationNicknameBinding);
        fragmentRegistrationNicknameBinding.setFragmentRegistrationNicknameViewModel(fragmentRegistrationNicknameViewModel);

    }

    @Override
    public void onResume() {
        super.onResume();
        G.onUserAvatarResponse = this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new HelperFragment(FragmentEditImage.newInstance(AttachFile.mCurrentPhotoPath, false, true)).setReplace(false).setStateLoss(true).load();

            } else {
                new HelperFragment(FragmentEditImage.newInstance(AttachFile.imagePath, false, true)).setReplace(false).setStateLoss(true).load();
                ImageHelper.correctRotateImage(AttachFile.imagePath, true); //rotate image
            }
        } else if (requestCode == AttachFile.request_code_image_from_gallery_single_select && resultCode == RESULT_OK) {// result for gallery
            if (data != null) {
                if (data.getData() == null) {
                    return;
                }
                new HelperFragment(FragmentEditImage.newInstance(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false, true)).setReplace(false).setStateLoss(true).load();
            }
        }
    }


    public void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentRegistrationNickname.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(FragmentRegistrationNickname.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void useGallery() {
        try {
            HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                @Override
                public void Allow() {
                    try {
                        new AttachFile(G.fragmentActivity).requestOpenGalleryForImageSingleSelect(FragmentRegistrationNickname.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deny() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDialog() {
        MaterialDialog.Builder imageDialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.choose_picture))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .items(R.array.profile)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0: {
                                useGallery();
                                dialog.dismiss();
                                break;
                            }
                            case 1: {
                                if (G.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                    try {
                                        HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                                            @Override
                                            public void Allow() {
                                                // this dialog show 2 way for choose image : gallery and camera
                                                dialog.dismiss();
                                                useCamera();
                                            }

                                            @Override
                                            public void deny() {

                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {

                                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.please_check_your_camera), false);
                                }
                                break;
                            }
                        }
                    }
                });
        if (!(G.fragmentActivity).isFinishing()) {
            imageDialog.show();
        }
    }

    private void setImage(String path) {
        if (path != null) {

            G.imageLoader.displayImage(AndroidUtils.suitablePath(path), btnSetImage);
            btnSetImage.setPadding(0, 0, 0, 0);
            //Bitmap bitmap = BitmapFactory.decodeFile(path);
            //btnSetImage.setImageBitmap(bitmap);
        }
    }

    /**
     * ************************************ Callbacks ************************************
     */

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {

        HelperAvatar.avatarAdd(G.userId, pathImageUser, avatar, new OnAvatarAdd() {
            @Override
            public void onAvatarAdd(final String avatarPath) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        existAvatar = true;
                        fragmentRegistrationNicknameViewModel.hideProgressBar();
                        setImage(avatarPath);
                    }
                });
            }
        });
    }

    @Override
    public void onAvatarAddTimeOut() {
        fragmentRegistrationNicknameViewModel.hideProgressBar();
    }

    @Override
    public void onAvatarError() {
        fragmentRegistrationNicknameViewModel.hideProgressBar();
    }

}
