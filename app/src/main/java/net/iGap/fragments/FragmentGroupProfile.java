package net.iGap.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.io.IOException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCrop;
import net.iGap.databinding.ActivityGroupProfileBinding;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnAvatarDelete;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAvatarDelete;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.IntentRequests;
import net.iGap.module.SUID;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestGroupAvatarAdd;
import net.iGap.request.RequestGroupKickAdmin;
import net.iGap.request.RequestGroupKickMember;
import net.iGap.request.RequestGroupKickModerator;
import net.iGap.viewmodel.FragmentGroupProfileViewModel;

/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/
public class FragmentGroupProfile extends BaseFragment implements OnGroupAvatarResponse, OnGroupAvatarDelete {

    NestedScrollView nestedScrollView;
    AttachFile attachFile;
    private CircleImageView imvGroupAvatar;
    private AppBarLayout appBarLayout;
    private String pathSaveImage;
    private ProgressBar prgWait;
    private static final String ROOM_ID = "RoomId";
    private Fragment fragment;
    private static final String IS_NOT_JOIN = "is_not_join";
    public static OnBackFragment onBackFragment;
    private FragmentGroupProfileViewModel fragmentGroupProfileViewModel;
    private ActivityGroupProfileBinding fragmentGroupProfileBinding;


    public static FragmentGroupProfile newInstance(long roomId, Boolean isNotJoin) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        args.putBoolean(IS_NOT_JOIN, isNotJoin);
        FragmentGroupProfile fragment = new FragmentGroupProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentGroupProfileBinding = DataBindingUtil.inflate(inflater, R.layout.activity_group_profile, container, false);
        return attachToSwipeBack(fragmentGroupProfileBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();

        fragment = this;
        onBackFragment = new OnBackFragment() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };


        initComponent(view);

        attachFile = new AttachFile(G.fragmentActivity);
        G.onGroupAvatarResponse = this;
        G.onGroupAvatarDelete = this;

    }

    private void initDataBinding() {
        fragmentGroupProfileViewModel = new FragmentGroupProfileViewModel(this, getArguments());
        fragmentGroupProfileBinding.setFragmentGroupProfileViewModel(fragmentGroupProfileViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();

        fragmentGroupProfileViewModel.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            long avatarId = SUID.id().get();
            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                        ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                        intent.putExtra("IMAGE_CAMERA", AttachFile.mCurrentPhotoPath);
                        intent.putExtra("TYPE", "camera");
                        intent.putExtra("PAGE", "setting");
                        intent.putExtra("ID", (int) (avatarId + 1L));
                        startActivityForResult(intent, IntentRequests.REQ_CROP);
                    } else {
                        Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                        ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                        intent.putExtra("IMAGE_CAMERA", AttachFile.imagePath);
                        intent.putExtra("TYPE", "camera");
                        intent.putExtra("PAGE", "setting");
                        intent.putExtra("ID", (int) (avatarId + 1L));
                        startActivityForResult(intent, IntentRequests.REQ_CROP);
                    }

                    break;
                case AttachFile.request_code_image_from_gallery_single_select:
                    if (data.getData() == null) {
                        return;
                    }
                    Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                    intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image));
                    intent.putExtra("TYPE", "gallery");
                    intent.putExtra("PAGE", "setting");
                    intent.putExtra("ID", (int) (avatarId + 1L));
                    startActivityForResult(intent, IntentRequests.REQ_CROP);

                    //filePath = AttachFile.getFilePathFromUri(data.getData());
                    //filePathAvatar = filePath;

                    break;

                case IntentRequests.REQ_CROP: { // save path image on data base ( realmGroupProfile )

                    pathSaveImage = null;
                    if (data != null) {
                        pathSaveImage = data.getData().toString();
                    }


                    long lastUploadedAvatarId = avatarId + 1L;

                    showProgressBar();
                    HelperUploadFile.startUploadTaskAvatar(pathSaveImage, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                        @Override
                        public void OnProgress(int progress, FileUploadStructure struct) {
                            if (progress < 100) {
                                prgWait.setProgress(progress);
                            } else {
                                new RequestGroupAvatarAdd().groupAvatarAdd(fragmentGroupProfileViewModel.roomId, struct.token);
                            }
                        }

                        @Override
                        public void OnError() {
                            hideProgressBar();
                        }
                    });
                }
            }
        }
    }

    /**
     * ************************************** methods **************************************
     */


    private void initComponent(final View view) {

        nestedScrollView = fragmentGroupProfileBinding.groupNestedScroll;
        prgWait = fragmentGroupProfileBinding.agpPrgWaitingAddContact;
        AppUtils.setProgresColler(prgWait);

        //        txtGroupDescription.setText(HelperUrl.setUrlLink(description, true, false, null, true));
        fragmentGroupProfileBinding.agpTxtGroupDescription.setMovementMethod(LinkMovementMethod.getInstance());
        appBarLayout = (fragmentGroupProfileBinding.agpAppbar);
        imvGroupAvatar = fragmentGroupProfileBinding.agpImvGroupAvatar;

        //CollapsingToolbarLayout collapsingToolbarLayout = fragmentGroupProfileBinding.agpColapsingToolbar;
        //collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        //collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));

        //

        final TextView titleToolbar = fragmentGroupProfileBinding.agpTxtTitleToolbar;
        final ViewGroup viewGroup = fragmentGroupProfileBinding.apgParentLayoutCircleImage;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset < -5) {
                    viewGroup.setVisibility(View.GONE);
                    titleToolbar.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(0).setDuration(500);
                    titleToolbar.animate().alpha(1).setDuration(250);
                } else {
                    titleToolbar.setVisibility(View.GONE);
                    viewGroup.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(0).setDuration(250);
                    viewGroup.animate().alpha(1).setDuration(500);
                }
            }
        });

        FloatingActionButton fab = fragmentGroupProfileBinding.agpFabSetPic;
        if (fragmentGroupProfileViewModel.role == GroupChatRole.OWNER || fragmentGroupProfileViewModel.role == GroupChatRole.ADMIN) {
            fab.setVisibility(View.VISIBLE);
            //
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //
                    startDialogSelectPicture(R.array.profile);
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }
        //

        //final ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.agp_toggle_member_can_add_member);
        //toggleButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        if (toggleButton.isChecked()) {
        //
        //        } else {
        //
        //        }
        //    }
        //});

        //
        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                HelperAvatar.avatarDelete(fragmentGroupProfileViewModel.roomId, mAvatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
                    @Override
                    public void latestAvatarPath(final String avatarPath) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                            }
                        });
                    }

                    @Override
                    public void showInitials(final String initials, final String color) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imvGroupAvatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                            }
                        });
                    }
                });
            }
        };

        showAvatar();
    }


    //dialog for choose pic from gallery or camera
    private void startDialogSelectPicture(int r) {

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                if (which == 0) {
                    try {
                        //attachFile.requestOpenGalleryForImageSingleSelect();
                        attachFile.requestOpenGalleryForImageSingleSelect(fragment);
                        //HelperPermision.getStoragePermision(context, new OnGetPermission() {
                        //    @Override
                        //    public void Allow() {
                        //        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //        intent.setType("image/*");
                        //        fragment.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                        //    }
                        //
                        //    @Override
                        //    public void deny() {
                        //
                        //    }
                        //});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (which == 1) {
                    if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { // camera

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
                        Toast.makeText(G.fragmentActivity, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentGroupProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(FragmentGroupProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAvatar() {
        HelperAvatar.getAvatar(fragmentGroupProfileViewModel.roomId, HelperAvatar.AvatarType.ROOM, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imvGroupAvatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWait != null) {
                    prgWait.setVisibility(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                prgWait.setVisibility(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }


    /**
     * ************************************** Callbacks **************************************
     */
    //
    @Override
    public void onAvatarAdd(final long roomId, final ProtoGlobal.Avatar avatar) {
        hideProgressBar();
        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            showAvatar();
        } else {
            HelperAvatar.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                        }
                    });
                }
            });
            pathSaveImage = null;
        }
    }

    @Override
    public void onAvatarAddError() {
        hideProgressBar();
    }

    @Override
    public void onDeleteAvatar(long roomId, long avatarId) {
        hideProgressBar();
        HelperAvatar.avatarDelete(roomId, avatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
            @Override
            public void latestAvatarPath(final String avatarPath) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                    }
                });
            }

            @Override
            public void showInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imvGroupAvatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    @Override
    public void onDeleteAvatarError(int majorCode, int minorCode) {

    }


    @Override
    public void onTimeOut() {

        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.time_out), false);

            }
        });
    }



    /**
     * if user was admin set  role to member
     */
    public void kickAdmin(final long memberID) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_admin_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestGroupKickAdmin().groupKickAdmin(fragmentGroupProfileViewModel.roomId, memberID);
            }
        }).show();
    }

    /**
     * delete this member from list of member group
     */
    public void kickMember(final long memberID) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_kick_this_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestGroupKickMember().groupKickMember(fragmentGroupProfileViewModel.roomId, memberID);
            }
        }).show();
    }

    public void kickModerator(final long memberID) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_modereator_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestGroupKickModerator().groupKickModerator(fragmentGroupProfileViewModel.roomId, memberID);
            }
        }).show();
    }



    public interface OnBackFragment {
        void onBack();
    }

}

