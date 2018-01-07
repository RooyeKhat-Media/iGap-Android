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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCrop;
import net.iGap.databinding.ActivityNewGroupBinding;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnChannelAvatarAdd;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.IntentRequests;
import net.iGap.module.LinedEditText;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmUserInfo;
import net.iGap.viewmodel.FragmentNewGroupViewModel;

import static net.iGap.G.context;
import static net.iGap.module.AttachFile.isInAttach;
import static net.iGap.module.AttachFile.request_code_TAKE_PICTURE;
import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

public class FragmentNewGroup extends BaseFragment implements OnGroupAvatarResponse, OnChannelAvatarAdd {

    private CircleImageView imgCircleImageView;
    private long groomId = 0;
    //  private String path;

    private EditText edtGroupName;
    private LinedEditText edtDescription;

    private int lastSpecialRequestsCursorPosition = 0;
    private String specialRequests;
    public static long avatarId = 0;
    private static ProtoGlobal.Room.Type type;

    FragmentNewGroupViewModel fragmentNewGroupViewModel;
    ActivityNewGroupBinding fragmentNewGroupBinding;


    public static OnRemoveFragmentNewGroup onRemoveFragmentNewGroup;

    public static FragmentNewGroup newInstance() {
        return new FragmentNewGroup();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentNewGroupBinding = DataBindingUtil.inflate(inflater, R.layout.activity_new_group, container, false);
        return attachToSwipeBack(fragmentNewGroupBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();

        initComponent(view);

        onRemoveFragmentNewGroup = new OnRemoveFragmentNewGroup() {
            @Override
            public void onRemove() {
                try {
                    popBackStackFragment();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void initDataBinding() {
        fragmentNewGroupViewModel = new FragmentNewGroupViewModel(this.getArguments());
        fragmentNewGroupBinding.setFragmentNewGroupVieModel(fragmentNewGroupViewModel);
    }



    private void showDialogSelectGallery() {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.choose_picture)).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {

                        try {
                            HelperPermission.getStoragePermision(context, new OnGetPermission() {
                                @Override
                                public void Allow() {

                                    if (isAdded()) { // boolean isAdded () Return true if the fragment is currently added to its activity.
                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intent.setType("image/*");
                                        startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                                        isInAttach = true;
                                    }
                                }

                                @Override
                                public void deny() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 1: {

                        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                            try {

                                HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                                    @Override
                                    public void Allow() throws IOException {
                                        HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                                            @Override
                                            public void Allow() {
                                                // this dialog show 2 way for choose image : gallery and camera

                                                if (isAdded()) {
                                                    useCamera();
                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void deny() {

                                            }
                                        });
                                    }

                                    @Override
                                    public void deny() {

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                //                                            new AttachFile(FragmentNewGroup.this.G.fragmentActivity).dispatchTakePictureIntent();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(G.fragmentActivity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        return;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        fragmentNewGroupViewModel.uriIntent = FileProvider.getUriForFile(G.fragmentActivity, G.fragmentActivity.getApplicationContext().getPackageName() + ".provider", createImageFile());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fragmentNewGroupViewModel.uriIntent);
                        startActivityForResult(takePictureIntent, request_code_TAKE_PICTURE);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (fragmentNewGroupViewModel.prefix.equals("NewChanel")) {
                fragmentNewGroupViewModel.uriIntent = Uri.fromFile(G.IMAGE_NEW_CHANEL);
            } else {
                fragmentNewGroupViewModel.uriIntent = Uri.fromFile(G.IMAGE_NEW_GROUP);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fragmentNewGroupViewModel.uriIntent);
            startActivityForResult(intent, request_code_TAKE_PICTURE);
        }
    }

    public void initComponent(View view) {
        G.onGroupAvatarResponse = this;
        G.onChannelAvatarAdd = this;

        AppUtils.setProgresColler(fragmentNewGroupBinding.ngPrgWaiting);

        G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        fragmentNewGroupBinding.ngRippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                AppUtils.closeKeyboard(rippleView);
                if (G.IMAGE_NEW_GROUP.exists()) {
                    G.IMAGE_NEW_GROUP.delete();
                } else {
                    G.IMAGE_NEW_CHANEL.delete();
                }
                G.fragmentActivity.onBackPressed();
            }
        });

        //=======================set image for group
        imgCircleImageView = fragmentNewGroupBinding.ngProfileCircleImage;
        AndroidUtils.setBackgroundShapeColor(imgCircleImageView, Color.parseColor(G.appBarColor));

        RippleView rippleCircleImage = fragmentNewGroupBinding.ngRippleCircleImage;
        rippleCircleImage.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) throws IOException {

                HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() {
                        showDialogSelectGallery();
                    }

                    @Override
                    public void deny() {

                    }
                });
            }
        });

        //=======================name of group
        edtGroupName = fragmentNewGroupBinding.ngEdtNewGroup;
        final View ViewGroupName = fragmentNewGroupBinding.ngViewNewGroup;
        edtGroupName.setPadding(0, 8, 0, 8);
        edtGroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    ViewGroupName.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                } else {
                    ViewGroupName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        //=======================description group
        edtDescription = fragmentNewGroupBinding.ngEdtDescription;
        edtDescription.setPadding(0, 8, 0, 8);

        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastSpecialRequestsCursorPosition = edtDescription.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtDescription.removeTextChangedListener(this);

                if (edtDescription.getLineCount() > 4) {
                    edtDescription.setText(specialRequests);
                    edtDescription.setSelection(lastSpecialRequestsCursorPosition);
                } else {
                    specialRequests = edtDescription.getText().toString();
                }

                edtDescription.addTextChangedListener(this);
            }
        });
    }

    private void showInitials() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        imgCircleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));

        realm.close();
    }


    private void setImage(final String imagePath) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (imagePath != null && new File(imagePath).exists()) {
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgCircleImageView);
                } else {
                    showInitials();
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                }
            }
        });
    }

    @Override
    public void onAvatarAdd(final long roomId, final ProtoGlobal.Avatar avatar) {

        HelperAvatar.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
            @Override
            public void onAvatarAdd(final String avatarPath) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fragmentNewGroupViewModel.hideProgressBar();
                        setImage(avatarPath);

                        if (fragmentNewGroupViewModel.isChannel) {
                            startChannelRoom(roomId);
                        } else {
                            startRoom(roomId);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onAvatarAddError() {
        fragmentNewGroupViewModel.hideProgressBar();
        ;
    }

    private void startRoom(long roomId) {
        Fragment fragment = ContactGroupFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong("RoomId", roomId);

        if (fragmentNewGroupViewModel.prefix.equals("NewChanel")) {
            bundle.putString("TYPE", ProtoGlobal.Room.Type.CHANNEL.toString());
        } else {
            bundle.putString("TYPE", ProtoGlobal.Room.Type.GROUP.toString());
        }

        bundle.putBoolean("NewRoom", true);
        fragment.setArguments(bundle);

        popBackStackFragment();
        new HelperFragment(fragment).load();
    }

    private void startChannelRoom(long roomId) {
        fragmentNewGroupViewModel.hideProgressBar();
        ;
        FragmentCreateChannel fragmentCreateChannel = new FragmentCreateChannel();
        Bundle bundle = new Bundle();
        bundle.putLong("ROOMID", roomId);
        bundle.putString("INVITE_LINK", fragmentNewGroupViewModel.mInviteLink);
        bundle.putString("TOKEN", fragmentNewGroupViewModel.token);
        fragmentCreateChannel.setArguments(bundle);

        popBackStackFragment();
        new HelperFragment(fragmentCreateChannel).load();
    }

    //=======================result for picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_code_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                ImageHelper.correctRotateImage(fragmentNewGroupViewModel.mCurrentPhotoPath, true);
                intent.putExtra("IMAGE_CAMERA", fragmentNewGroupViewModel.mCurrentPhotoPath);
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", fragmentNewGroupViewModel.prefix);
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            } else {

                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                if (fragmentNewGroupViewModel.uriIntent != null) {
                    intent.putExtra("IMAGE_CAMERA", fragmentNewGroupViewModel.uriIntent.toString());
                    intent.putExtra("TYPE", "camera");
                    intent.putExtra("PAGE", fragmentNewGroupViewModel.prefix);
                    startActivityForResult(intent, IntentRequests.REQ_CROP);
                } else {
                    Toast.makeText(context, R.string.can_not_save_picture_pleas_try_again, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == request_code_image_from_gallery_single_select && resultCode == Activity.RESULT_OK) {// result for gallery
            if (data != null) {

                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image));
                intent.putExtra("TYPE", "gallery");
                intent.putExtra("PAGE", fragmentNewGroupViewModel.prefix);
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            }
        } else if (requestCode == IntentRequests.REQ_CROP) {

            if (data != null) {
                pathSaveImage = data.getData().toString();
                avatarId = System.nanoTime();

                fragmentNewGroupViewModel.showProgressBar();
                //showProgressBar();
                HelperUploadFile.startUploadTaskAvatar(pathSaveImage, avatarId, new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        if (progress < 100) {
                            fragmentNewGroupBinding.ngPrgWaiting.setProgress(progress);
                        } else {
                            fragmentNewGroupViewModel.hideProgressBar();
                            ;
                            fragmentNewGroupViewModel.existAvatar = true;
                            fragmentNewGroupViewModel.token = struct.token;
                            setImage(pathSaveImage);
                        }
                    }

                    @Override
                    public void OnError() {
                        fragmentNewGroupViewModel.hideProgressBar();
                        ;
                    }
                });
            }
        }
    }

    private String pathSaveImage;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        fragmentNewGroupViewModel.mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public interface OnRemoveFragmentNewGroup {
        void onRemove();
    }



}
