package net.iGap.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCrop;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnAvatarDelete;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAddMember;
import net.iGap.interfaces.OnGroupAvatarDelete;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnGroupCheckUsername;
import net.iGap.interfaces.OnGroupDelete;
import net.iGap.interfaces.OnGroupEdit;
import net.iGap.interfaces.OnGroupKickMember;
import net.iGap.interfaces.OnGroupLeft;
import net.iGap.interfaces.OnGroupRemoveUsername;
import net.iGap.interfaces.OnGroupRevokeLink;
import net.iGap.interfaces.OnGroupUpdateUsername;
import net.iGap.interfaces.OnMenuClick;
import net.iGap.interfaces.OnSelectedList;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.IntentRequests;
import net.iGap.module.MEditText;
import net.iGap.module.SUID;
import net.iGap.module.enums.EnumCustomMessageId;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupCheckUsername;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestGroupAddAdmin;
import net.iGap.request.RequestGroupAddMember;
import net.iGap.request.RequestGroupAddModerator;
import net.iGap.request.RequestGroupAvatarAdd;
import net.iGap.request.RequestGroupCheckUsername;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupEdit;
import net.iGap.request.RequestGroupKickAdmin;
import net.iGap.request.RequestGroupKickMember;
import net.iGap.request.RequestGroupKickModerator;
import net.iGap.request.RequestGroupLeft;
import net.iGap.request.RequestGroupRemoveUsername;
import net.iGap.request.RequestGroupRevokeLink;
import net.iGap.request.RequestGroupUpdateUsername;
import net.iGap.request.RequestUserInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;

import static android.content.Context.CLIPBOARD_SERVICE;
import static net.iGap.G.context;

/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/
public class FragmentGroupProfile extends BaseFragment implements OnGroupAvatarResponse, OnGroupAvatarDelete, OnGroupRevokeLink {

    LinearLayout layoutSetting;
    NestedScrollView nestedScrollView;
    LinearLayout layoutSetAdmin;
    View viewLineAdmin;
    LinearLayout layoutSetModereator;
    LinearLayout layoutMemberCanAddMember;
    LinearLayout layoutNotificatin;
    LinearLayout layoutDeleteAndLeftGroup;

    AttachFile attachFile;
    private CircleImageView imvGroupAvatar;
    private EmojiTextViewE txtGroupNameTitle;
    private EmojiTextViewE txtGroupName;
    private EmojiTextViewE txtGroupDescription;
    private TextView txtNumberOfSharedMedia;
    private TextView txtMemberNumber;

    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private String tmp = "";

    private long roomId;
    private String title;
    private String description;
    private String initials;
    private String inviteLink;
    private String linkUsername;
    private String color;
    private GroupChatRole role;
    private long noLastMessage;
    private String participantsCountLabel;
    private String pathSaveImage;

    public static OnMenuClick onMenuClick;

    private boolean isPrivate;
    private TextView txtLinkTitle;
    private TextView txtGroupLink;
    private boolean isPopup = false;
    private ViewGroup ltLink;

    private long startMessageId = 0;

    private PopupWindow popupWindow;
    private ProgressBar prgWait;

    private boolean isNeedgetContactlist = true;

    private RealmChangeListener<RealmModel> changeListener;
    private RealmRoom mRoom;
    private Realm realmGroupProfile;
    private static final String ROOM_ID = "RoomId";
    private Fragment fragment;
    public static final String FRAGMENT_TAG = "FragmentGroupProfile";
    private static final String IS_NOT_JOIN = "is_not_join";
    private boolean isNotJoin = false;
    private View v;

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
        return attachToSwipeBack(inflater.inflate(R.layout.activity_group_profile, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment = this;
        realmGroupProfile = Realm.getDefaultInstance();

        v = view;

        Bundle extras = getArguments();
        roomId = extras.getLong(ROOM_ID);
        isNotJoin = extras.getBoolean(IS_NOT_JOIN);

        //group info
        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getGroupRoom() == null) {
            popBackStackFragment();
            return;
        }
        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
        title = realmRoom.getTitle();
        initials = realmRoom.getInitials();
        color = realmRoom.getColor();
        role = realmGroupRoom.getRole();
        inviteLink = realmGroupRoom.getInvite_link();
        linkUsername = realmGroupRoom.getUsername();
        isPrivate = realmGroupRoom.isPrivate();
        participantsCountLabel = realmGroupRoom.getParticipantsCountLabel();
        description = realmGroupRoom.getDescription();

        ViewGroup listMemberGroup = (ViewGroup) view.findViewById(R.id.agp_root_layout_group_add_member);
        listMemberGroup.setVisibility(View.VISIBLE);
        if (role == GroupChatRole.MODERATOR || role == GroupChatRole.MEMBER) {
            if (!isPrivate) {
                listMemberGroup.setVisibility(View.GONE);
            }
        }

        try {
            if (realmRoom.getLastMessage() != null) {
                noLastMessage = realmRoom.getLastMessage().getMessageId();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }


        initComponent(view);

        attachFile = new AttachFile(G.fragmentActivity);
        G.onGroupAvatarResponse = this;
        G.onGroupAvatarDelete = this;
        G.onGroupRevokeLink = this;

        FragmentShearedMedia.getCountOfSharedMedia(roomId);
    }

    @Override
    public void onResume() {
        super.onResume();

        mRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (mRoom != null) {

            if (changeListener == null) {

                changeListener = new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(final RealmModel element) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (((RealmRoom) element).isValid()) {
                                    String countText = ((RealmRoom) element).getSharedMediaCount();
                                    if (txtNumberOfSharedMedia == null) {
                                        txtNumberOfSharedMedia = (TextView) G.fragmentActivity.findViewById(R.id.agp_txt_number_of_shared_media);
                                    }
                                    if (HelperCalander.isPersianUnicode) {
                                        txtNumberOfSharedMedia.setText(HelperCalander.convertToUnicodeFarsiNumber(countText));
                                    } else {
                                        txtNumberOfSharedMedia.setText(countText);
                                    }
                                }
                            }
                        });
                    }
                };
            }

            mRoom.addChangeListener(changeListener);
            changeListener.onChange(mRoom);
        } else {
            if (txtNumberOfSharedMedia == null) {
                txtNumberOfSharedMedia = (TextView) G.fragmentActivity.findViewById(R.id.agp_txt_number_of_shared_media);
            }
            txtNumberOfSharedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realmGroupProfile != null && !realmGroupProfile.isClosed()) {
            realmGroupProfile.close();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Override onSaveInstanceState method and comment 'super' from avoid from "Can not perform this action after onSaveInstanceState" error
        //super.onSaveInstanceState(outState); //
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
                                new RequestGroupAvatarAdd().groupAvatarAdd(roomId, struct.token);
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

    private Realm getRealm() {
        if (realmGroupProfile == null || realmGroupProfile.isClosed()) {
            realmGroupProfile = Realm.getDefaultInstance();
        }
        return realmGroupProfile;
    }

    private int memberCount;

    private void setMemberCount(final long roomId, final boolean plus) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                memberCount = RealmRoom.updateMemberCount(realm, roomId, plus);
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtMemberNumber.setText(memberCount + "");
                        if (HelperCalander.isPersianUnicode) {
                            txtMemberNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtMemberNumber.getText().toString()));
                        }
                    }
                });
            }
        });

    }

    private void initComponent(final View view) {

        RippleView rippleBack = (RippleView) view.findViewById(R.id.agp_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });

        final int screenWidth = (int) (G.context.getResources().getDisplayMetrics().widthPixels / 1.7);
        RippleView rippleMenu = (RippleView) view.findViewById(R.id.agp_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {

                LinearLayout layoutDialog = new LinearLayout(G.context);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutDialog.setOrientation(LinearLayout.VERTICAL);
                layoutDialog.setBackgroundColor(G.context.getResources().getColor(android.R.color.white));
                TextView text2 = new TextView(G.context);
                TextView text3 = new TextView(G.context);

                text2.setTextColor(G.context.getResources().getColor(android.R.color.black));
                text3.setTextColor(G.context.getResources().getColor(android.R.color.black));

                final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
                View v = dialog.getCustomView();

                DialogAnimation.animationUp(dialog);
                dialog.show();

                ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
                ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);

                TextView txtClearHistory = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
                TextView txtConvert = (TextView) v.findViewById(R.id.dialog_text_item2_notification);

                TextView iconClearHistory = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
                iconClearHistory.setText(G.fragmentActivity.getResources().getString(R.string.md_clearHistory));
                TextView iconConvert = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);

                root1.setVisibility(View.VISIBLE);
                root2.setVisibility(View.VISIBLE);

                txtClearHistory.setText(G.fragmentActivity.getResources().getString(R.string.clear_history));
                if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {

                    root2.setVisibility(View.VISIBLE);
                    if (isPrivate) {
                        txtConvert.setText(G.fragmentActivity.getResources().getString(R.string.group_title_convert_to_public));
                        iconConvert.setText(G.fragmentActivity.getResources().getString(R.string.md_convert_to_public));
                    } else {
                        txtConvert.setText(G.fragmentActivity.getResources().getString(R.string.group_title_convert_to_private));
                        iconConvert.setText(G.fragmentActivity.getResources().getString(R.string.md_convert_to_private));
                    }
                } else {
                    root2.setVisibility(View.GONE);
                }

                if (role == GroupChatRole.OWNER) {
                    root2.setVisibility(View.VISIBLE);
                }
                root1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                if (FragmentChat.onComplete != null) {
                                    FragmentChat.onComplete.complete(false, roomId + "", "");
                                }
                            }
                        }).negativeText(R.string.no).show();

                        dialog.dismiss();
                    }
                });

                root2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        isPopup = true;

                        if (isPrivate) {
                            convertToPublic(view);
                        } else {
                            convertToPrivate();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        layoutSetting = (LinearLayout) view.findViewById(R.id.agp_ll_seetting);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.group_nestedScroll);
        layoutSetAdmin = (LinearLayout) view.findViewById(R.id.agp_ll_set_admin);
        viewLineAdmin = (View) view.findViewById(R.id.agp_ll_line_admin);
        layoutSetModereator = (LinearLayout) view.findViewById(R.id.agp_ll_set_modereator);
        layoutMemberCanAddMember = (LinearLayout) view.findViewById(R.id.agp_ll_member_can_add_member);
        layoutNotificatin = (LinearLayout) view.findViewById(R.id.agp_ll_notification);
        layoutDeleteAndLeftGroup = (LinearLayout) view.findViewById(R.id.agp_ll_delete_and_left_group);
        prgWait = (ProgressBar) view.findViewById(R.id.agp_prgWaiting_addContact);
        AppUtils.setProgresColler(prgWait);
        ltLink = (ViewGroup) view.findViewById(R.id.agp_ll_link);
        imvGroupAvatar = (CircleImageView) view.findViewById(R.id.agp_imv_group_avatar);
        TextView txtDeleteGroup = (TextView) view.findViewById(R.id.agp_txt_str_delete_and_leave_group);
        txtGroupNameTitle = (EmojiTextViewE) view.findViewById(R.id.agp_txt_group_name_title);
        txtGroupNameTitle.setText(title);

        txtGroupName = (EmojiTextViewE) view.findViewById(R.id.agp_txt_group_name);
        txtGroupName.setText(title);

        txtGroupDescription = (EmojiTextViewE) view.findViewById(R.id.agp_txt_group_description);

        txtGroupDescription.setText(HelperUrl.setUrlLink(description, true, false, null, true));
        txtGroupDescription.setMovementMethod(LinkMovementMethod.getInstance());
        txtNumberOfSharedMedia = (TextView) view.findViewById(R.id.agp_txt_number_of_shared_media);

        txtMemberNumber = (TextView) view.findViewById(R.id.agp_txt_member_number);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.agp_appbar);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.agp_colapsing_toolbar);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));

        LinearLayout llGroupName = (LinearLayout) view.findViewById(R.id.agp_ll_group_name);
        LinearLayout llGroupDescription = (LinearLayout) view.findViewById(R.id.agp_ll_group_description);

        if (isNotJoin) {
            layoutSetting.setVisibility(View.GONE);
        }

        /**
         *  visibility layout Description
         */

        if (role == GroupChatRole.OWNER) {
            llGroupDescription.setVisibility(View.VISIBLE);

            llGroupName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeGroupName(view);
                }
            });

            llGroupDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeGroupDescription();
                }
            });
        } else {
            if (description.length() == 0) {
                llGroupDescription.setVisibility(View.GONE);
            }
        }

        if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
            ltLink.setVisibility(View.VISIBLE);
        } else {
            ltLink.setVisibility(View.GONE);
        }

        LinearLayout llSharedMedia = (LinearLayout) view.findViewById(R.id.agp_ll_sheared_media);
        llSharedMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(FragmentShearedMedia.newInstance(roomId)).setReplace(false).load();
            }
        });

        final TextView titleToolbar = (TextView) view.findViewById(R.id.agp_txt_titleToolbar);
        final ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.apg_parentLayoutCircleImage);
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

        //        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
        //            @Override
        //            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //
        //                TextView titleToolbar = (TextView) findViewById(R.id.agp_txt_titleToolbar);
        //                if (verticalOffset < -appBarLayout.getTotalScrollRange() / 4) {
        //
        //                    titleToolbar.animate().alpha(1).setDuration(300);
        //                    titleToolbar.setVisibility(View.VISIBLE);
        //                } else {
        //                    titleToolbar.animate().alpha(0).setDuration(500);
        //                    titleToolbar.setVisibility(View.GONE);
        //                }
        //            }
        //        });

        fab = (FloatingActionButton) view.findViewById(R.id.agp_fab_setPic);

        if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
            fab.setVisibility(View.VISIBLE);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startDialogSelectPicture(R.array.profile);
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }
        if (role != GroupChatRole.OWNER) {
            if (description.equals("")) {
                llGroupDescription.setVisibility(View.GONE);
            }
        }

        ViewGroup layoutShowMember = (ViewGroup) view.findViewById(R.id.agp_layout_show_member);
        layoutShowMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString());
            }
        });

        TextView txtShowMember = (TextView) view.findViewById(R.id.agp_txt_show_member);

        ViewGroup layoutAddMember = (ViewGroup) view.findViewById(R.id.agp_layout_add_member);
        layoutAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemberToGroup();
            }
        });

        TextView txtSetAdmin = (TextView) view.findViewById(R.id.agp_txt_set_admin);
        txtSetAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
            }
        });

        TextView txtAddModerator = (TextView) view.findViewById(R.id.agp_txt_add_modereator);
        txtAddModerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());
            }
        });

        final ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.agp_toggle_member_can_add_member);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()) {

                } else {

                }
            }
        });

        TextView txtNotification = (TextView) view.findViewById(R.id.agp_txt_str_notification_and_sound);
        txtNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "GROUP");
                bundle.putLong("ID", roomId);
                fragmentNotification.setArguments(bundle);
                new HelperFragment(fragmentNotification).setReplace(false).load();
            }
        });

        if (role == GroupChatRole.OWNER) {
            txtDeleteGroup.setText(G.fragmentActivity.getResources().getString(R.string.delete_group));
        } else {
            txtDeleteGroup.setText(G.fragmentActivity.getResources().getString(R.string.left_group));
        }

        txtDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupLeft();
            }
        });

        RippleView rippleCircleImage = (RippleView) view.findViewById(R.id.agp_ripple_group_avatar);
        rippleCircleImage.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.group);
                    fragment.appBarLayout = fab;
                    //new HelperFragment(fragment).setResourceContainer(R.id.fragmentContainer_group_profile).load();
                    new HelperFragment(fragment).setReplace(false).load();
                }
            }
        });

        txtMemberNumber.setText(participantsCountLabel);
        if (HelperCalander.isPersianUnicode) {
            txtMemberNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtMemberNumber.getText().toString()));
        }
        txtLinkTitle = (TextView) view.findViewById(R.id.agp_txt_link_title);
        txtGroupLink = (TextView) view.findViewById(R.id.agp_txt_link);

        setTextGroupLik();

        ltLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isPopup = false;

                if (role == GroupChatRole.OWNER) {
                    if (isPrivate) {
                        dialogRevoke();
                    } else {
                        setUsername();
                    }
                } else {
                    dialogCopyLink();
                }
            }
        });

        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                HelperAvatar.avatarDelete(roomId, mAvatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
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
        setUiIndependentRole();
        initRecycleView();

        onGroupAddMemberCallback();
        onGroupKickMemberCallback();
    }

    private void dialogCopyLink() {

        String link = txtGroupLink.getText().toString();

        final LinearLayout layoutGroupLink = new LinearLayout(G.fragmentActivity);
        layoutGroupLink.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputGroupLink = new TextInputLayout(G.fragmentActivity);
        MEditText edtLink = new MEditText(G.fragmentActivity);
        edtLink.setHint(G.fragmentActivity.getResources().getString(R.string.group_link_hint_revoke));
        edtLink.setTypeface(G.typeface_IRANSansMobile);
        edtLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtLink.setText(link);
        edtLink.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtLink.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtLink.setPadding(0, 8, 0, 8);
        edtLink.setEnabled(false);
        edtLink.setSingleLine(true);
        inputGroupLink.addView(edtLink);
        inputGroupLink.addView(viewRevoke, viewParams);

        TextView txtLink = new TextView(G.fragmentActivity);
        txtLink.setText("http://iGap.net/");
        txtLink.setTextColor(G.context.getResources().getColor(R.color.gray_6c));

        viewRevoke.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtLink.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutGroupLink.addView(inputGroupLink, layoutParams);
        layoutGroupLink.addView(txtLink, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.group_link)).positiveText(G.fragmentActivity.getResources().getString(R.string.array_Copy)).customView(layoutGroupLink, true).widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).negativeText(G.fragmentActivity.getResources().getString(R.string.no))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = txtGroupLink.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                })
                .build();

        dialog.show();
    }

    private void dialogRevoke() {

        String link = txtGroupLink.getText().toString();

        final LinearLayout layoutRevoke = new LinearLayout(G.fragmentActivity);
        layoutRevoke.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputRevoke = new TextInputLayout(G.fragmentActivity);
        MEditText edtRevoke = new MEditText(G.fragmentActivity);
        edtRevoke.setHint(G.fragmentActivity.getResources().getString(R.string.group_link_hint_revoke));
        edtRevoke.setTypeface(G.typeface_IRANSansMobile);
        edtRevoke.setText(link);
        edtRevoke.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtRevoke.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtRevoke.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtRevoke.setPadding(0, 8, 0, 8);
        edtRevoke.setEnabled(false);
        edtRevoke.setSingleLine(true);
        inputRevoke.addView(edtRevoke);
        inputRevoke.addView(viewRevoke, viewParams);

        viewRevoke.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtRevoke.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutRevoke.addView(inputRevoke, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.group_link_hint_revoke))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.revoke))
                .customView(layoutRevoke, true)
                .widgetColor(G.context.getResources().getColor(R.color.toolbar_background))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .neutralText(R.string.array_Copy)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = txtGroupLink.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                })
                .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestGroupRevokeLink().groupRevokeLink(roomId);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void convertToPrivate() {

        G.onGroupRemoveUsername = new OnGroupRemoveUsername() {
            @Override
            public void onGroupRemoveUsername(final long roomId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isPrivate = true;
                        setTextGroupLik();
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.group_title_convert_to_private)).content(G.fragmentActivity.getResources().getString(R.string.group_text_convert_to_private)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestGroupRemoveUsername().groupRemoveUsername(roomId);
            }
        }).negativeText(R.string.no).show();
    }

    private void setTextGroupLik() {

        if (isPrivate) {
            txtGroupLink.setText("" + inviteLink);
            txtLinkTitle.setText(G.fragmentActivity.getResources().getString(R.string.group_link));
        } else {
            txtGroupLink.setText("" + linkUsername);
            txtLinkTitle.setText(G.fragmentActivity.getResources().getString(R.string.st_username));
        }
    }

    private void convertToPublic(View view) {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.group_title_convert_to_public)).content(G.fragmentActivity.getResources().getString(R.string.group_text_convert_to_public)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
                setUsername();
            }
        }).negativeText(R.string.no).show();
    }

    private void setUsername() {
        final LinearLayout layoutUserName = new LinearLayout(G.fragmentActivity);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(G.fragmentActivity);
        final MEditText edtUserName = new MEditText(G.fragmentActivity);
        edtUserName.setHint(G.fragmentActivity.getResources().getString(R.string.group_title_set_username));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            edtUserName.setTextDirection(View.TEXT_DIRECTION_LTR);
        }
        edtUserName.setTypeface(G.typeface_IRANSansMobile);
        edtUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        if (isPopup) {
            edtUserName.setText("iGap.net/");
        } else {
            edtUserName.setText("iGap.net/" + linkUsername);
        }

        edtUserName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_username))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.save))
                .customView(layoutUserName, true)
                .widgetColor(G.context.getResources().getColor(R.color.toolbar_background))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        G.onGroupCheckUsername = new OnGroupCheckUsername() {
            @Override
            public void onGroupCheckUsername(final ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status status) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.AVAILABLE) {

                            positive.setEnabled(true);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("");
                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.INVALID) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.TAKEN) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.TAKEN));
                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.OCCUPYING_LIMIT_EXCEEDED));
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().contains("iGap.net/")) {
                    //edtUserName.setText("iGap.net/");
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                }

                if (HelperString.regexCheckUsername(editable.toString().replace("iGap.net/", ""))) {
                    String userName = edtUserName.getText().toString().replace("iGap.net/", "");
                    new RequestGroupCheckUsername().GroupCheckUsername(roomId, userName);
                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                }
            }
        });

        G.onGroupUpdateUsername = new OnGroupUpdateUsername() {
            @Override
            public void onGroupUpdateUsername(final long roomId, final String username) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        isPrivate = false;
                        dialog.dismiss();

                        linkUsername = username;
                        setTextGroupLik();
                    }
                });
            }

            @Override
            public void onError(final int majorCode, int minorCode, final int time) {

                switch (majorCode) {
                    case 368:
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) dialog.dismiss();
                                dialogWaitTime(R.string.GROUP_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                            }
                        });

                        break;
                }
            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = edtUserName.getText().toString().replace("iGap.net/", "");
                new RequestGroupUpdateUsername().groupUpdateUsername(roomId, userName);
            }
        });

        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                } else {
                    viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        // check each word with server
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AndroidUtils.closeKeyboard(v);
            }
        });

        dialog.show();
    }

    private void initRecycleView() {

        onMenuClick = new OnMenuClick() {
            @Override
            public void clicked(View view, StructContactInfo info) {
                new CreatePopUpMessage().show(view, info);
            }
        };
    }

    private void setUiIndependentRole() {

        if (role == GroupChatRole.MEMBER) {

            layoutSetAdmin.setVisibility(View.GONE);
            viewLineAdmin.setVisibility(View.GONE);
            layoutSetModereator.setVisibility(View.GONE);
            layoutMemberCanAddMember.setVisibility(View.GONE);
        } else if (role == GroupChatRole.MODERATOR) {
            layoutSetAdmin.setVisibility(View.GONE);
            viewLineAdmin.setVisibility(View.GONE);
            layoutSetModereator.setVisibility(View.GONE);
        } else if (role == GroupChatRole.ADMIN) {
            layoutSetAdmin.setVisibility(View.GONE);
            viewLineAdmin.setVisibility(View.GONE);
        } else if (role == GroupChatRole.OWNER) {

        }
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
                            HelperPermision.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
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

    private void addMemberToGroup() {
        List<StructContactInfo> userList = Contacts.retrieve(null);
        RealmList<RealmMember> memberList = RealmMember.getMembers(getRealm(), roomId);

        for (int i = 0; i < memberList.size(); i++) {
            for (int j = 0; j < userList.size(); j++) {
                if (userList.get(j).peerId == memberList.get(i).getPeerId()) {
                    userList.remove(j);
                    break;
                }
            }
        }

        Fragment fragment = ShowCustomList.newInstance(userList, new OnSelectedList() {
            @Override
            public void getSelectedList(boolean result, final String type, final int countForShowLastMessage, final ArrayList<StructContactInfo> list) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < list.size(); i++) {
                            new RequestGroupAddMember().groupAddMember(roomId, list.get(i).peerId, RealmRoomMessage.findCustomMessageId(roomId, EnumCustomMessageId.convertType(type), countForShowLastMessage));
                        }
                    }
                });
            }
        });

        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", true);
        bundle.putLong("COUNT_MESSAGE", noLastMessage);
        fragment.setArguments(bundle);
        new HelperFragment(fragment).setReplace(false).load();
    }

    private void ChangeGroupName(final View view) {

        final LinearLayout layoutUserName = new LinearLayout(G.fragmentActivity);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(G.fragmentActivity);
        final EmojiEditTextE edtUserName = new EmojiEditTextE(G.fragmentActivity);
        edtUserName.setHint(G.fragmentActivity.getResources().getString(R.string.st_username));
        edtUserName.setTypeface(G.typeface_IRANSansMobile);
        edtUserName.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edtUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtUserName.setText(txtGroupNameTitle.getText().toString());
        edtUserName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.group_name))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.save))
                .customView(layoutUserName, true)
                .widgetColor(G.context.getResources().getColor(R.color.toolbar_background))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        final String finalUserName = txtGroupNameTitle.getText().toString();
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtUserName.getText().toString().equals(finalUserName)) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }
        });

        G.onGroupEdit = new OnGroupEdit() {
            @Override
            public void onGroupEdit(long roomId, String name, String description) {
                hideProgressBar();
                txtGroupName.setText(name);
                txtGroupDescription.setText(description);
                txtGroupNameTitle.setText(name);
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.time_out), false);

                    }
                });
            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RequestGroupEdit().groupEdit(roomId, edtUserName.getText().toString(), txtGroupDescription.getText().toString());
                dialog.dismiss();
            }
        });

        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                } else {
                    viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AndroidUtils.closeKeyboard(view);
            }
        });

        dialog.show();
    }

    private void ChangeGroupDescription() {
        MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.group_description).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).alwaysCallInputCallback().widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                G.onGroupEdit = new OnGroupEdit() {
                    @Override
                    public void onGroupEdit(final long roomId, final String name, final String descriptions) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                description = descriptions;
                                txtGroupName.setText(name);
                                txtGroupDescription.setText(descriptions);
                                txtGroupNameTitle.setText(name);
                            }
                        });
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {

                    }

                    @Override
                    public void onTimeOut() {

                    }
                };

                new RequestGroupEdit().groupEdit(roomId, txtGroupName.getText().toString(), tmp);
            }
        }).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT).input(G.fragmentActivity.getResources().getString(R.string.please_enter_group_description), txtGroupDescription.getText().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                // Do something

                View positive = dialog.getActionButton(DialogAction.POSITIVE);
                tmp = input.toString();
                if (!input.toString().equals(txtGroupDescription.getText().toString())) {

                    positive.setClickable(true);
                    positive.setAlpha(1.0f);
                } else {
                    positive.setClickable(false);
                    positive.setAlpha(0.5f);
                }
            }
        }).show();

        final View v = dialog.getView();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AndroidUtils.closeKeyboard(v);
            }
        });
    }

    private void groupLeft() {

        String text = "";
        int title;
        if (role == GroupChatRole.OWNER) {
            text = G.fragmentActivity.getResources().getString(R.string.do_you_want_to_delete_this_group);
            title = R.string.delete_group;
        } else {
            text = G.fragmentActivity.getResources().getString(R.string.do_you_want_to_leave_this_group);
            title = R.string.left_group;
        }

        new MaterialDialog.Builder(G.fragmentActivity).title(title).content(text).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                G.onGroupLeft = new OnGroupLeft() {
                    @Override
                    public void onGroupLeft(final long roomId, long memberId) {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //   G.fragmentActivity.finish();
                                if (FragmentChat.finishActivity != null) {
                                    FragmentChat.finishActivity.finishActivity();
                                }
                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onTimeOut() {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }
                };

                G.onGroupDelete = new OnGroupDelete() {
                    @Override
                    public void onGroupDelete(final long roomId) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //G.fragmentActivity.finish();
                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                                if (FragmentChat.finishActivity != null) {
                                    FragmentChat.finishActivity.finishActivity();
                                }
                            }
                        });
                    }

                    @Override
                    public void Error(int majorCode, int minorCode) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onTimeOut() {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }
                };

                if (role == GroupChatRole.OWNER) {
                    new RequestGroupDelete().groupDelete(roomId);
                } else {
                    new RequestGroupLeft().groupLeft(roomId);
                }
                prgWait.setVisibility(View.VISIBLE);
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).show();
    }

    /**
     * if user was admin set  role to member
     */
    public void kickAdmin(final long memberID) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_admin_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestGroupKickAdmin().groupKickAdmin(roomId, memberID);
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
                new RequestGroupKickMember().groupKickMember(roomId, memberID);
            }
        }).show();
    }

    private void onGroupAddMemberCallback() {
        G.onGroupAddMember = new OnGroupAddMember() {
            @Override
            public void onGroupAddMember(final Long roomIdUser, final Long userId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setMemberCount(roomIdUser, true);
                        //+Realm realm = Realm.getDefaultInstance();
                        RealmRegisteredInfo realmRegistered = RealmRegisteredInfo.getRegistrationInfo(getRealm(), userId);

                        if (realmRegistered == null) {
                            if (roomIdUser == roomId) {
                                new RequestUserInfo().userInfo(userId, roomId + "");
                            }
                        }
                        //realm.close();
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };
    }

    private void onGroupKickMemberCallback() {
        G.onGroupKickMember = new OnGroupKickMember() {
            @Override
            public void onGroupKickMember(final long roomId, final long memberId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setMemberCount(roomId, false);
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };
    }

    public void kickModerator(final long memberID) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_modereator_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestGroupKickModerator().groupKickModerator(roomId, memberID);
            }
        }).show();
    }

    private void showListForCustomRole(String SelectedRole) {
        FragmentShowMember fragment = FragmentShowMember.newInstance1(this.fragment, roomId, role.toString(), G.userId, SelectedRole, isNeedgetContactlist);
        new HelperFragment(fragment).setReplace(false).load();
        isNeedgetContactlist = false;
    }

    private void showAvatar() {
        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, true, new OnAvatarGet() {
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

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

        View v = dialog.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    /**
     * ************************************** Callbacks **************************************
     */
    @Override
    public void onGroupRevokeLink(final long roomId, final String inviteLink, final String inviteToken) {
        hideProgressBar();

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                txtGroupLink.setText("" + inviteLink);
            }
        });
    }

    @Override
    public void onError(int majorCode, int minorCode) {
        hideProgressBar();
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

    /**
     * inner class
     */

    class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(G.fragmentActivity, view, Gravity.TOP);
            popup.getMenuInflater().inflate(R.menu.menu_item_group_profile, popup.getMenu());

            if (role == GroupChatRole.OWNER) {

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                }
            } else if (role == GroupChatRole.ADMIN) {

                /**
                 *  ----------- Admin ---------------
                 *  1- admin dose'nt access set another admin
                 *  2- admin can set moderator
                 *  3- can remove moderator
                 *  4- can kick moderator and Member
                 */
                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                }
            } else if (role == GroupChatRole.MODERATOR) {

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                }
            } else {

                return;
            }

            // Setup menu item selection
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_setAdmin:
                            setToAdmin(info.peerId);
                            return true;
                        case R.id.menu_set_moderator:
                            setToModerator(info.peerId);
                            return true;
                        case R.id.menu_remove_admin:
                            kickAdmin(info.peerId);
                            return true;
                        case R.id.menu_remove_moderator:
                            kickModerator(info.peerId);
                            return true;
                        case R.id.menu_kick:
                            kickMember(info.peerId);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            // Handle dismissal with: popup.setOnDismissListener(...);
            // Show the menu
            popup.show();
        }

        private void setToAdmin(Long peerId) {
            new RequestGroupAddAdmin().groupAddAdmin(roomId, peerId);
        }

        private void setToModerator(Long peerId) {
            new RequestGroupAddModerator().groupAddModerator(roomId, peerId);
        }
    }
}

