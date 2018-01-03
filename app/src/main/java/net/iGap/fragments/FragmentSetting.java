package net.iGap.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCrop;
import net.iGap.databinding.FragmentSettingBinding;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnAvatarDelete;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnUserAvatarResponse;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.IntentRequests;
import net.iGap.module.SUID;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestUserAvatarAdd;
import net.iGap.request.RequestUserProfileGetBio;
import net.iGap.request.RequestUserProfileGetEmail;
import net.iGap.request.RequestUserProfileGetGender;
import net.iGap.viewmodel.FragmentSettingViewModel;

import static android.app.Activity.RESULT_OK;
import static net.iGap.G.context;
import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetting extends BaseFragment implements OnUserAvatarResponse {

    public static String pathSaveImage;
    private SharedPreferences sharedPreferences;
    private EmojiTextViewE txtNickName;
    private Uri uriIntent;
    private long idAvatar;
    public ProgressBar prgWait;
    private Realm mRealm;
    public static DateType dateType;
    private FragmentSettingBinding fragmentSettingBinding;
    private FragmentSettingViewModel fragmentSettingViewModel;
    public static onRemoveFragmentSetting onRemoveFragmentSetting;
    public static onClickBack onClickBack;

    public FragmentSetting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentSettingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        return attachToSwipeBack(fragmentSettingBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initDataBinding();

        AppUtils.setProgresColler(fragmentSettingBinding.stPrgWaitingAddContact);

        new RequestUserProfileGetGender().userProfileGetGender();
        new RequestUserProfileGetEmail().userProfileGetEmail();
        new RequestUserProfileGetBio().getBio();

        final TextView titleToolbar = fragmentSettingBinding.stTxtTitleToolbar;
        final ViewGroup viewGroup = fragmentSettingBinding.stParentLayoutCircleImage;
        fragmentSettingBinding.stAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, final int verticalOffset) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (verticalOffset < -5) {
                            viewGroup.animate().alpha(0).setDuration(500);
                            titleToolbar.animate().alpha(1).setDuration(250);
                            viewGroup.clearAnimation();
                            titleToolbar.clearAnimation();
                            titleToolbar.setVisibility(View.VISIBLE);
                            viewGroup.setVisibility(View.GONE);
                        } else {

                            titleToolbar.animate().alpha(0).setDuration(250);
                            viewGroup.animate().alpha(1).setDuration(500);
                            viewGroup.clearAnimation();
                            titleToolbar.clearAnimation();
                            viewGroup.setVisibility(View.VISIBLE);
                            titleToolbar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        onClickBack = new onClickBack() {
            @Override
            public void back() {
                G.fragmentActivity.onBackPressed();
            }
        };

        //***********************
        GradientDrawable bgShape = (GradientDrawable) fragmentSettingBinding.asnImgTitleBarColor.getBackground();
        bgShape.setColor(Color.parseColor(G.appBarColor));

        //***********************

        GradientDrawable bgShapeNotification = (GradientDrawable) fragmentSettingBinding.asnImgNotificationColor.getBackground();
        bgShapeNotification.setColor(Color.parseColor(G.notificationColor));

        //***********************

        GradientDrawable bgShapeToggleBottomColor = (GradientDrawable) fragmentSettingBinding.asnImgToggleBottonColor.getBackground();
        bgShapeToggleBottomColor.setColor(Color.parseColor(G.toggleButtonColor));

         /*
          page for show all image user
         */
        GradientDrawable bgShapeSendAndAttachColor = (GradientDrawable) fragmentSettingBinding.asnImgSendAndAttachColor.getBackground();
        bgShapeSendAndAttachColor.setColor(Color.parseColor(G.attachmentColor));


        //***********************

        GradientDrawable bgShapeHeaderTextColor = (GradientDrawable) fragmentSettingBinding.asnImgDefaultHeaderFontColor.getBackground();
        bgShapeHeaderTextColor.setColor(Color.parseColor(G.headerTextColor));

        //***********************

        GradientDrawable bgShapeProgressColor = (GradientDrawable) fragmentSettingBinding.asnImgDefaultProgressColor.getBackground();
        bgShapeProgressColor.setColor(Color.parseColor(G.progressColor));


        fragmentSettingBinding.stFabSetPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog(R.array.profile);
            }
        });


        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                HelperAvatar.avatarDelete(fragmentSettingViewModel.userId, mAvatarId, HelperAvatar.AvatarType.USER, new OnAvatarDelete() {
                    @Override
                    public void latestAvatarPath(final String avatarPath) {
                        setImage(avatarPath);
                    }

                    @Override
                    public void showInitials(final String initials, final String color) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                fragmentSettingBinding.stImgCircleImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) fragmentSettingBinding.stImgCircleImage.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                                if (G.onChangeUserPhotoListener != null) {
                                    G.onChangeUserPhotoListener.onChangePhoto(null);
                                }
                            }
                        });
                    }
                });
            }
        };

        setAvatar();


        onRemoveFragmentSetting = new onRemoveFragmentSetting() {
            @Override
            public void removeFragment() {
                removeFromBaseFragment(FragmentSetting.this);
            }
        };


    }

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {

        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            setAvatar();
        } else {
            HelperAvatar.avatarAdd(fragmentSettingViewModel.userId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                            setImage(avatarPath);
                        }
                    });
                }
            });
            pathSaveImage = null;
        }
    }

    @Override
    public void onAvatarAddTimeOut() {
        hideProgressBar();
    }

    @Override
    public void onAvatarError() {
        hideProgressBar();
    }




    @Override
    public void onResume() {
        super.onResume();
        fragmentSettingViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        fragmentSettingViewModel.onPause();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //fragmentSettingViewModel.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                intent.putExtra("IMAGE_CAMERA", AttachFile.mCurrentPhotoPath);
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", "setting");
                intent.putExtra("ID", (int) (idAvatar + 1L));
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            } else {
                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                if (uriIntent != null) {
                    ImageHelper.correctRotateImage(pathSaveImage, true);
                    intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
                    intent.putExtra("TYPE", "camera");
                    intent.putExtra("PAGE", "setting");
                    intent.putExtra("ID", (int) (idAvatar + 1L));
                    startActivityForResult(intent, IntentRequests.REQ_CROP);
                }
            }
        } else if (requestCode == request_code_image_from_gallery_single_select && resultCode == RESULT_OK) {// result for gallery
            if (data != null) {
                if (data.getData() == null) {
                    return;
                }
                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image));
                intent.putExtra("TYPE", "gallery");
                intent.putExtra("PAGE", "setting");
                intent.putExtra("ID", (int) (idAvatar + 1L));
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            }
        } else if (requestCode == IntentRequests.REQ_CROP && resultCode == RESULT_OK) { // save path image on data base ( realm )

            if (data != null) {
                pathSaveImage = data.getData().toString();
            }

            long lastUploadedAvatarId = idAvatar + 1L;

            showProgressBar();
            HelperUploadFile.startUploadTaskAvatar(pathSaveImage, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                @Override
                public void OnProgress(int progress, FileUploadStructure struct) {
                    if (progress < 100) {
                        fragmentSettingBinding.stPrgWaitingAddContact.setProgress(progress);
                    } else {
                        new RequestUserAvatarAdd().userAddAvatar(struct.token);
                    }
                }

                @Override
                public void OnError() {
                    hideProgressBar();
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentSettingViewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentSettingViewModel.onDestroy();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Override onSaveInstanceState method and comment 'super' from avoid from "Can not perform this action after onSaveInstanceState" error
        //super.onSaveInstanceState(outState);
    }


    public interface DateType {

        void dataName(String type);
    }


    private void initDataBinding() {
        fragmentSettingViewModel = new FragmentSettingViewModel(this, fragmentSettingBinding);
        fragmentSettingBinding.setFragmentSettingViewModel(fragmentSettingViewModel);
    }

    private void startDialog(int r) {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.choose_picture)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(G.fragmentActivity.getResources().getString(R.string.array_From_Camera))) { // camera
                    try {
                        HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
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
                    try {
                        HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentSetting.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                idAvatar = SUID.id().get();
                pathSaveImage = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + idAvatar + ".jpg";
                File nameImageFile = new File(pathSaveImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uriIntent = Uri.fromFile(nameImageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);

            } else {
                Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.please_check_your_camera), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
        return mRealm;
    }

    private void setAvatar() {
        HelperAvatar.getAvatar(fragmentSettingViewModel.userId, HelperAvatar.AvatarType.USER, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), fragmentSettingBinding.stImgCircleImage);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fragmentSettingBinding.stImgCircleImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) fragmentSettingBinding.stImgCircleImage.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });
    }


    private void setImage(String path) {
        if (path != null) {
            G.imageLoader.displayImage(AndroidUtils.suitablePath(path), fragmentSettingBinding.stImgCircleImage);
            if (G.onChangeUserPhotoListener != null) {
                G.onChangeUserPhotoListener.onChangePhoto(path);
            }
        }
    }

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (fragmentSettingBinding.stPrgWaitingAddContact != null) {
                    fragmentSettingBinding.stPrgWaitingAddContact.setVisibility(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (fragmentSettingBinding.stPrgWaitingAddContact != null) {
                    fragmentSettingBinding.stPrgWaitingAddContact.setVisibility(View.GONE);
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    public interface onRemoveFragmentSetting {

        void removeFragment();

    }

    public interface onClickBack {

        void back();

    }

}
