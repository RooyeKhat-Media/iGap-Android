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
import java.io.File;
import java.io.IOException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCrop;
import net.iGap.databinding.ActivityProfileChannelBinding;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnAvatarDelete;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChannelAvatarAdd;
import net.iGap.interfaces.OnChannelAvatarDelete;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.IntentRequests;
import net.iGap.module.SUID;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestChannelAvatarAdd;
import net.iGap.request.RequestChannelKickAdmin;
import net.iGap.request.RequestChannelKickMember;
import net.iGap.request.RequestChannelKickModerator;
import net.iGap.viewmodel.FragmentChannelProfileViewModel;

public class FragmentChannelProfile extends BaseFragment implements OnChannelAvatarAdd, OnChannelAvatarDelete {


    private CircleImageView imgCircleImageView;
    private TextView titleToolbar;
    private String pathSaveImage;
    private static ProgressBar prgWait;
    private AttachFile attachFile;
    private Fragment fragment;
    private static final String ROOM_ID = "RoomId";
    private static final String IS_NOT_JOIN = "is_not_join";
    public static final String FRAGMENT_TAG = "FragmentChannelProfile";
    public static OnBackFragment onBackFragment;

    private FragmentChannelProfileViewModel fragmentChannelProfileViewModel;
    private ActivityProfileChannelBinding fragmentProfileChannelBinding;


    public static FragmentChannelProfile newInstance(long roomId, Boolean isNotJoin) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        args.putBoolean(IS_NOT_JOIN, isNotJoin);
        FragmentChannelProfile fragment = new FragmentChannelProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentProfileChannelBinding = DataBindingUtil.inflate(inflater, R.layout.activity_profile_channel, container, false);
        return attachToSwipeBack(fragmentProfileChannelBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        fragment = this;
        G.onChannelAvatarAdd = this;
        G.onChannelAvatarDelete = this;


        FloatingActionButton fab = fragmentProfileChannelBinding.pchFabAddToChannel;
        prgWait = fragmentProfileChannelBinding.agpPrgWaiting;
        AppUtils.setProgresColler(prgWait);

        onBackFragment = new OnBackFragment() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };

        AppBarLayout appBarLayout = fragmentProfileChannelBinding.pchAppbar;

        titleToolbar = fragmentProfileChannelBinding.pchTxtTitleToolbar;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                ViewGroup viewGroup = fragmentProfileChannelBinding.pchRootCircleImage;
                if (verticalOffset < -5) {
                    viewGroup.animate().alpha(0).setDuration(700);
                    viewGroup.setVisibility(View.GONE);
                    titleToolbar.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(1).setDuration(300);
                } else {
                    viewGroup.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(1).setDuration(700);
                    titleToolbar.setVisibility(View.GONE);
                    titleToolbar.animate().alpha(0).setDuration(500);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startDialogSelectPicture(R.array.profile);
            }
        });

        imgCircleImageView = fragmentProfileChannelBinding.pchImgCircleImage;

        fragmentProfileChannelBinding.txtDescription.setMovementMethod(LinkMovementMethod.getInstance());
        attachFile = new AttachFile(G.fragmentActivity);

        setAvatar();

        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                final long finalMAvatarId = mAvatarId;
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HelperAvatar.avatarDelete(fragmentChannelProfileViewModel.roomId, finalMAvatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
                            @Override
                            public void latestAvatarPath(String avatarPath) {
                                setImage(avatarPath);
                            }

                            @Override
                            public void showInitials(final String initials, final String color) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imgCircleImageView.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };

    }

    private void initDataBinding() {
        fragmentChannelProfileViewModel = new FragmentChannelProfileViewModel(getArguments(), this);
        fragmentProfileChannelBinding.setFragmentChannelProfileViewModel(fragmentChannelProfileViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentChannelProfileViewModel.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentChannelProfileViewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentChannelProfileViewModel.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FragmentChat fragment = (FragmentChat) getFragmentManager().findFragmentByTag(FragmentChat.class.getName());
        if (fragment != null && fragment.isVisible()) {
            fragment.onResume();
        }
    }

    private void setAvatar() {
        HelperAvatar.getAvatar(fragmentChannelProfileViewModel.roomId, HelperAvatar.AvatarType.ROOM, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imgCircleImageView);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imgCircleImageView.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }


    //********** select picture
    private void startDialogSelectPicture(int r) {

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(G.fragmentActivity.getResources().getString(R.string.from_camera))) {

                    if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

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
                        Toast.makeText(G.fragmentActivity, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        new AttachFile(G.fragmentActivity).requestOpenGalleryForImageSingleSelect(FragmentChannelProfile.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).show();
    }

    private void useCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentChannelProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(FragmentChannelProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    ////************************************************** interfaces
    //
    ////***On Add Avatar Response From Server

    @Override
    public void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar) {
        /**
         * if another account do this action we haven't avatar source and have
         * to download avatars . for do this action call HelperAvatar.getAvatar
         */

        hideProgressBar();
        if (pathSaveImage == null) {
            setAvatar();
        } else {
            HelperAvatar.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
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
    public void onAvatarAddError() {
        hideProgressBar();
    }

    //***On Avatar Delete

    @Override
    public void onChannelAvatarDelete(final long roomId, final long avatarId) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                HelperAvatar.avatarDelete(roomId, avatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
                    @Override
                    public void latestAvatarPath(String avatarPath) {
                        setImage(avatarPath);
                    }

                    @Override
                    public void showInitials(final String initials, final String color) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imgCircleImageView.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onError(int majorCode, int minorCode) {
        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.normal_error), false);
            }
        });
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



    //*** set avatar image

    private void setImage(final String imagePath) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (new File(imagePath).exists()) {
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgCircleImageView);
                }
            }
        });
    }

    //*** notification and sounds


    //*** onActivityResult

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

                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //    ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                    //    filePath = AttachFile.mCurrentPhotoPath;
                    //    filePathAvatar = filePath;
                    //} else {
                    //    ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                    //    filePath = AttachFile.imagePath;
                    //    filePathAvatar = filePath;
                    //}
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

                case IntentRequests.REQ_CROP: { // save path image on data base ( realm )

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
                                new RequestChannelAvatarAdd().channelAvatarAdd(fragmentChannelProfileViewModel.roomId, struct.token);
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


    //********* kick user from roles
    public void kickMember(final Long peerId) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_kick_this_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestChannelKickMember().channelKickMember(fragmentChannelProfileViewModel.roomId, peerId);
            }
        }).show();

    }

    public void kickModerator(final Long peerId) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_modereator_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestChannelKickModerator().channelKickModerator(fragmentChannelProfileViewModel.roomId, peerId);
            }
        }).show();
    }

    public void kickAdmin(final Long peerId) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_admin_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestChannelKickAdmin().channelKickAdmin(fragmentChannelProfileViewModel.roomId, peerId);
            }
        }).show();


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
                if (prgWait != null) {
                    prgWait.setVisibility(View.GONE);
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    public interface OnBackFragment {
        void onBack();
    }

}
