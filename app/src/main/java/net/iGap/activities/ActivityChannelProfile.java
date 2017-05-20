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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentNotification;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.fragments.FragmentShowMember;
import net.iGap.fragments.ShowCustomList;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnAvatarDelete;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChannelAddAdmin;
import net.iGap.interfaces.OnChannelAddMember;
import net.iGap.interfaces.OnChannelAddModerator;
import net.iGap.interfaces.OnChannelAvatarAdd;
import net.iGap.interfaces.OnChannelAvatarDelete;
import net.iGap.interfaces.OnChannelCheckUsername;
import net.iGap.interfaces.OnChannelDelete;
import net.iGap.interfaces.OnChannelEdit;
import net.iGap.interfaces.OnChannelKickAdmin;
import net.iGap.interfaces.OnChannelKickMember;
import net.iGap.interfaces.OnChannelKickModerator;
import net.iGap.interfaces.OnChannelLeft;
import net.iGap.interfaces.OnChannelRemoveUsername;
import net.iGap.interfaces.OnChannelRevokeLink;
import net.iGap.interfaces.OnChannelUpdateSignature;
import net.iGap.interfaces.OnChannelUpdateUsername;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnMenuClick;
import net.iGap.interfaces.OnSelectedList;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.DialogAnimation;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SUID;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoChannelCheckUsername;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelAddAdmin;
import net.iGap.request.RequestChannelAddMember;
import net.iGap.request.RequestChannelAddModerator;
import net.iGap.request.RequestChannelAvatarAdd;
import net.iGap.request.RequestChannelCheckUsername;
import net.iGap.request.RequestChannelDelete;
import net.iGap.request.RequestChannelEdit;
import net.iGap.request.RequestChannelKickAdmin;
import net.iGap.request.RequestChannelKickMember;
import net.iGap.request.RequestChannelKickModerator;
import net.iGap.request.RequestChannelLeft;
import net.iGap.request.RequestChannelRemoveUsername;
import net.iGap.request.RequestChannelRevokeLink;
import net.iGap.request.RequestChannelUpdateSignature;
import net.iGap.request.RequestChannelUpdateUsername;
import net.iGap.request.RequestUserInfo;

import static net.iGap.G.context;

public class ActivityChannelProfile extends ActivityEnhanced implements OnChannelAddMember, OnChannelKickMember, OnChannelAddModerator, OnChannelKickModerator, OnChannelAddAdmin, OnChannelKickAdmin, OnChannelDelete, OnChannelLeft, OnChannelEdit, OnChannelAvatarAdd, OnChannelAvatarDelete, OnChannelRevokeLink {

    private AppBarLayout appBarLayout;
    private TextView txtDescription, txtChannelLink, txtChannelNameInfo;
    private MaterialDesignTextView imgPopupMenu;
    private CircleImageView imgCircleImageView;
    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    private TextView titleToolbar;
    private TextView txtChannelName;
    TextView txtSharedMedia;
    private EditText edtRevoke;

    private String title;
    private String initials;
    private String color;
    private String participantsCountLabel;
    private String description;
    private String inviteLink;
    private String pathSaveImage;
    private ChannelChatRole role;
    private long noLastMessage;

    private RealmList<RealmMember> members;
    private static ProgressBar prgWait;
    private LinearLayout lytListAdmin;
    private LinearLayout lytListModerator;
    private LinearLayout lytDeleteChannel;
    private LinearLayout lytNotification;
    private AttachFile attachFile;
    private long roomId;
    private boolean isPrivate;
    private String linkUsername;
    private boolean isSignature;
    private TextView txtLinkTitle;
    private boolean isPopup = false;

    private boolean isNeedGetMemberList = true;

    private Realm mRealm;
    private RealmChangeListener<RealmModel> changeListener;
    private RealmRoom mRoom;

    @Override
    protected void onStop() {
        super.onStop();

        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) mRealm.close();
    }

    @Override
    protected void onResume() {

        super.onResume();

        mRoom = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (mRoom != null) {

            if (changeListener == null) {

                changeListener = new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(final RealmModel element) {

                        if (((RealmRoom) element).isValid()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String countText = ((RealmRoom) element).getSharedMediaCount();

                                    if (countText == null || countText.length() == 0) {
                                        txtSharedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
                                    } else {

                                        if (HelperCalander.isLanguagePersian) {
                                            txtSharedMedia.setText(HelperCalander.convertToUnicodeFarsiNumber(countText));
                                        } else {
                                            txtSharedMedia.setText(countText);
                                        }
                                    }
                                }
                            });
                        }
                    }
                };
            }

            mRoom.addChangeListener(changeListener);
            changeListener.onChange(mRoom);
        } else {
            txtSharedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_channel);

        mRealm = Realm.getDefaultInstance();

        G.onChannelAddMember = this;
        G.onChannelKickMember = this;
        G.onChannelAddAdmin = this;
        G.onChannelKickAdmin = this;
        G.onChannelAddModerator = this;
        G.onChannelKickModerator = this;
        G.onChannelDelete = this;
        G.onChannelLeft = this;
        G.onChannelEdit = this;
        G.onChannelRevokeLink = this;
        G.onChannelAvatarAdd = this;

        //=========Put Extra Start
        Bundle extras = getIntent().getExtras();
        roomId = extras.getLong(Config.PutExtraKeys.CHANNEL_PROFILE_ROOM_ID_LONG.toString());

        Realm realm = Realm.getDefaultInstance();

        //channel info
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getChannelRoom() == null) {
            //HelperError.showSnackMessage(getClientErrorCode(-2, 0));
            finish();
            return;
        }
        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
        title = realmRoom.getTitle();
        initials = realmRoom.getInitials();
        color = realmRoom.getColor();
        role = realmChannelRoom.getRole();
        inviteLink = realmChannelRoom.getInviteLink();
        isPrivate = realmChannelRoom.isPrivate();
        linkUsername = realmChannelRoom.getUsername();
        isSignature = realmChannelRoom.isSignature();
        fab = (FloatingActionButton) findViewById(R.id.pch_fab_addToChannel);
        try {
            if (realmRoom.getLastMessage() != null) {
                noLastMessage = realmRoom.getLastMessage().getMessageId();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }
        participantsCountLabel = realmChannelRoom.getParticipantsCountLabel();
        members = realmChannelRoom.getMembers();

        description = realmChannelRoom.getDescription();


        //realm.close();
        //=========Put Extra End

        txtSharedMedia = (TextView) findViewById(R.id.txt_shared_media);
        txtChannelNameInfo = (TextView) findViewById(R.id.txt_channel_name_info);
        prgWait = (ProgressBar) findViewById(R.id.agp_prgWaiting);
        AppUtils.setProgresColler(prgWait);

        LinearLayout lytSharedMedia = (LinearLayout) findViewById(R.id.lyt_shared_media);
        LinearLayout lytChannelName = (LinearLayout) findViewById(R.id.lyt_channel_name);
        LinearLayout lytChannelDescription = (LinearLayout) findViewById(R.id.lyt_description);
        lytListAdmin = (LinearLayout) findViewById(R.id.lyt_list_admin);
        lytListModerator = (LinearLayout) findViewById(R.id.lyt_list_moderator);
        lytDeleteChannel = (LinearLayout) findViewById(R.id.lyt_delete_channel);
        lytNotification = (LinearLayout) findViewById(R.id.lyt_notification);
        txtLinkTitle = (TextView) findViewById(R.id.txt_channel_link_title);
        ViewGroup vgRootAddMember = (ViewGroup) findViewById(R.id.agp_root_layout_add_member);
        ViewGroup ltLink = (ViewGroup) findViewById(R.id.layout_channel_link);
        imgPopupMenu = (MaterialDesignTextView) findViewById(R.id.pch_img_menuPopup);
        txtDescription = (TextView) findViewById(R.id.txt_description);
        if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            vgRootAddMember.setVisibility(View.GONE);
        }

        ViewGroup vgSignature = (ViewGroup) findViewById(R.id.agp_layout_signature);
        if (role == ChannelChatRole.OWNER) {

            vgSignature.setVisibility(View.VISIBLE);
        } else {
            lytChannelName.setEnabled(false);
            lytChannelDescription.setEnabled(false);
        }

        if (isPrivate && (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN)) {
            ltLink.setVisibility(View.VISIBLE);
        } else {
            ltLink.setVisibility(View.GONE);
        }

        if (!isPrivate) {
            ltLink.setVisibility(View.VISIBLE);
        }

        if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {

            fab.setVisibility(View.VISIBLE);

            imgPopupMenu.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
            imgPopupMenu.setVisibility(View.GONE);
        }

        if (role != ChannelChatRole.OWNER) {
            if (description.length() == 0) {
                lytChannelDescription.setVisibility(View.GONE);
            }
        }

        lytListAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
            }
        });

        lytListModerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());
            }
        });

        lytDeleteChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChannel();
            }
        });

        lytNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationAndSound();
            }
        });

        final RippleView rippleBack = (RippleView) findViewById(R.id.pch_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });
        appBarLayout = (AppBarLayout) findViewById(R.id.pch_appbar);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.acp_ll_collapsing_toolbar_layout);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));

        titleToolbar = (TextView) findViewById(R.id.pch_txt_titleToolbar);
        titleToolbar.setText("" + title);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                ViewGroup viewGroup = (ViewGroup) findViewById(R.id.pch_root_circleImage);
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

        RippleView rippleMenu = (RippleView) findViewById(R.id.pch_ripple_menuPopup);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                showPopUp();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startDialogSelectPicture(R.array.profile);
            }
        });

        imgCircleImageView = (CircleImageView) findViewById(R.id.pch_img_circleImage);
        imgCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getDefaultInstance();
                if (realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
                    FragmentShowAvatars.appBarLayout = fab;

                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.channel);
                    ActivityChannelProfile.this.getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer_channel_profile, fragment, null).commit();
                }
                realm.close();
            }
        });

        txtChannelLink = (TextView) findViewById(R.id.txt_channel_link);

        lytSharedMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityChannelProfile.this, ActivityShearedMedia.class);
                intent.putExtra("RoomID", roomId);
                startActivity(intent);
            }
        });

        lytChannelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeGroupName();
            }
        });

        lytChannelDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeGroupDescription();
            }
        });

        txtChannelName = (TextView) findViewById(R.id.txt_channel_name);

        TextView txtShowMember = (TextView) findViewById(R.id.agp_txt_show_member);
        txtShowMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString());
            }
        });

        ViewGroup layoutAddMember = (ViewGroup) findViewById(R.id.agp_layout_add_member);
        layoutAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemberToChannel();
            }
        });

        if (description != null && !description.isEmpty()) {
            txtDescription.setText(HelperUrl.setUrlLink(description, true, false, null, true));
            txtDescription.setMovementMethod(LinkMovementMethod.getInstance());
        }
        txtChannelName.setText(title);
        txtChannelNameInfo.setText(title);

        setTextChannelLik();

        ltLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isPopup = false;
                if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
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

        attachFile = new AttachFile(this);

        setAvatar();
        //setAvatarChannel();
        initRecycleView();
        showAdminOrModeratorList();

        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                final long finalMAvatarId = mAvatarId;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HelperAvatar.avatarDelete(roomId, finalMAvatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
                            @Override
                            public void latestAvatarPath(String avatarPath) {
                                setImage(avatarPath);
                            }

                            @Override
                            public void showInitials(final String initials, final String color) {
                                runOnUiThread(new Runnable() {
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

        TextView txtSignature = (TextView) findViewById(R.id.agp_txt_signature);
        final ToggleButton toggleEnableSignature = (ToggleButton) findViewById(R.id.agp_toggle_signature);

        if (isSignature) {
            toggleEnableSignature.setChecked(true);
        } else {
            toggleEnableSignature.setChecked(false);
        }

        G.onChannelUpdateSignature = new OnChannelUpdateSignature() {
            @Override
            public void onChannelUpdateSignatureResponse(final long roomId, final boolean signature) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        realmRoom.getChannelRoom().setSignature(signature);
                    }
                });

                realm.close();
            }

            @Override
            public void onError(int majorCode, int minorCode) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.normal_error), Snackbar.LENGTH_LONG);
                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();

                        if (toggleEnableSignature.isChecked()) {
                            toggleEnableSignature.setChecked(false);
                        } else {
                            toggleEnableSignature.setChecked(true);
                        }
                    }
                });
            }
        };

        txtSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toggleEnableSignature.isChecked()) {
                    toggleEnableSignature.setChecked(false);
                    new RequestChannelUpdateSignature().channelUpdateSignature(roomId, false);
                } else {
                    toggleEnableSignature.setChecked(true);
                    new RequestChannelUpdateSignature().channelUpdateSignature(roomId, true);
                }
            }
        });

        ActivityShearedMedia.getCountOfSharedMedia(roomId);

        realm.close();
    }

    private void setTextChannelLik() {

        if (isPrivate) {
            txtChannelLink.setText(inviteLink);
            txtLinkTitle.setText(getResources().getString(R.string.channel_link));
        } else {
            txtChannelLink.setText("" + linkUsername);
            txtLinkTitle.setText(getResources().getString(R.string.st_username));
        }
    }

    private void dialogRevoke() {

        String link = txtChannelLink.getText().toString();

        final LinearLayout layoutRevoke = new LinearLayout(ActivityChannelProfile.this);
        layoutRevoke.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(ActivityChannelProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputRevoke = new TextInputLayout(ActivityChannelProfile.this);
        edtRevoke = new EditText(ActivityChannelProfile.this);
        edtRevoke.setHint(getResources().getString(R.string.channel_link_hint_revoke));
        edtRevoke.setText(link);
        edtRevoke.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtRevoke.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtRevoke.setPadding(0, 8, 0, 8);
        edtRevoke.setEnabled(false);
        edtRevoke.setSingleLine(true);
        inputRevoke.addView(edtRevoke);
        inputRevoke.addView(viewRevoke, viewParams);

        viewRevoke.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtRevoke.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutRevoke.addView(inputRevoke, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).title(getResources().getString(R.string.channel_link_title_revoke))
                .positiveText(getResources().getString(R.string.revoke))
                .customView(layoutRevoke, true)
                .widgetColor(getResources().getColor(R.color.toolbar_background))
                .negativeText(getResources().getString(R.string.B_cancel))
                .neutralText(R.string.array_Copy)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = txtChannelLink.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                })
                .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestChannelRevokeLink().channelRevokeLink(roomId);
            }
        });
        dialog.show();
    }

    private void dialogCopyLink() {

        String link = txtChannelLink.getText().toString();

        final LinearLayout layoutChannelLink = new LinearLayout(ActivityChannelProfile.this);
        layoutChannelLink.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(ActivityChannelProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputChannelLink = new TextInputLayout(ActivityChannelProfile.this);
        EditText edtLink = new EditText(ActivityChannelProfile.this);
        edtLink.setHint(getResources().getString(R.string.channel_public_hint_revoke));
        edtLink.setText(link);
        edtLink.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtLink.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtLink.setPadding(0, 8, 0, 8);
        edtLink.setEnabled(false);
        edtLink.setSingleLine(true);
        inputChannelLink.addView(edtLink);
        inputChannelLink.addView(viewRevoke, viewParams);

        TextView txtLink = new TextView(ActivityChannelProfile.this);
        txtLink.setText("iGap.net/" + link);
        txtLink.setTextColor(getResources().getColor(R.color.gray_6c));

        viewRevoke.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtLink.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutChannelLink.addView(inputChannelLink, layoutParams);
        layoutChannelLink.addView(txtLink, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(ActivityChannelProfile.this).title(getResources().getString(R.string.channel_link)).positiveText(getResources().getString(R.string.array_Copy)).customView(layoutChannelLink, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = txtChannelLink.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                }).build();

        dialog.show();
    }

    private void setAvatar() {
        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imgCircleImageView);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgCircleImageView.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    /**
     * ************************************* Channel Members *************************************
     */

    public static OnMenuClick onMenuClick;

    private void initRecycleView() {

        onMenuClick = new OnMenuClick() {
            @Override
            public void clicked(View view, StructContactInfo info) {
                new CreatePopUpMessage().show(view, info);
            }
        };
    }

    //******Add And Moderator List

    private void showListForCustomRole(String SelectedRole) {
        FragmentShowMember fragment = FragmentShowMember.newInstance(roomId, role.toString(), G.userId, SelectedRole, isNeedGetMemberList);
        getSupportFragmentManager().beginTransaction().addToBackStack("null").setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer_channel_profile, fragment, "Show_member").commit();

        isNeedGetMemberList = false;
    }

    //****** show admin or moderator list

    private void showAdminOrModeratorList() {
        if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            lytListAdmin.setVisibility(View.GONE);
            lytListModerator.setVisibility(View.GONE);
        } else if (role == ChannelChatRole.ADMIN) {
            lytListAdmin.setVisibility(View.GONE);
        }
    }

    //****** add member

    private void addMemberToChannel() {
        List<StructContactInfo> userList = Contacts.retrieve(null);

        RealmRoom realmRoom = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        RealmList<RealmMember> memberList = realmRoom.getChannelRoom().getMembers();

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
            public void getSelectedList(boolean result, String message, int countForShowLastMessage, final ArrayList<StructContactInfo> list) {

                for (int i = 0; i < list.size(); i++) {
                    new RequestChannelAddMember().channelAddMember(roomId, list.get(i).peerId, 0);
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", false);
        bundle.putLong("COUNT_MESSAGE", noLastMessage);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.coordinator, fragment).commit();
    }

    //****** create popup

    private class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(ActivityChannelProfile.this, view, Gravity.TOP);
            popup.getMenuInflater().inflate(R.menu.menu_item_group_profile, popup.getMenu());

            if (role == ChannelChatRole.OWNER) {

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
            } else if (role == ChannelChatRole.ADMIN) {

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
            } else if (role == ChannelChatRole.MODERATOR) {

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
    }

    //********** select picture

    private void startDialogSelectPicture(int r) {

        new MaterialDialog.Builder(this).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(getString(R.string.from_camera))) {

                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                        try {

                            HelperPermision.getStoragePermision(ActivityChannelProfile.this, new OnGetPermission() {
                                @Override
                                public void Allow() throws IOException {
                                    HelperPermision.getCameraPermission(ActivityChannelProfile.this, new OnGetPermission() {
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
                                }

                                @Override
                                public void deny() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityChannelProfile.this, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        new AttachFile(ActivityChannelProfile.this).requestOpenGalleryForImageSingleSelect();
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
                new AttachFile(ActivityChannelProfile.this).dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(ActivityChannelProfile.this).requestTakePicture();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //********** update member count

    private void setMemberCount(final long roomId, final boolean plus) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null && realmRoom.getChannelRoom() != null) {
                    if (HelperString.isNumeric(realmRoom.getChannelRoom().getParticipantsCountLabel())) {
                        int memberCount = Integer.parseInt(realmRoom.getChannelRoom().getParticipantsCountLabel());
                        if (plus) {
                            memberCount++;
                        } else {
                            memberCount--;
                        }
                        realmRoom.getChannelRoom().setParticipantsCountLabel(memberCount + "");
                    }
                }
            }
        });
        realm.close();
    }

    //********** channel Add Member

    private void channelAddMemberResponse(long roomIdResponse, final long userId, final ProtoGlobal.ChannelRoom.Role role) {
        if (roomIdResponse == roomId) {

            setMemberCount(roomId, true);

            Realm realm = Realm.getDefaultInstance();
            RealmRegisteredInfo realmRegistered = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
            if (realmRegistered == null) {
                new RequestUserInfo().userInfo(userId, roomId + "");
            }

            realm.close();
        }
    }

    private void channelKickMember(final long roomIdResponse, final long memberId) {
        if (roomIdResponse == roomId) {
            setMemberCount(roomId, false);
        }
    }

    //********** dialog for edit channel

    private String dialogDesc;
    private String dialogName;

    private void ChangeGroupDescription() {
        new MaterialDialog.Builder(ActivityChannelProfile.this).title(R.string.channel_description).positiveText(getString(R.string.save)).alwaysCallInputCallback().widgetColor(getResources().getColor(R.color.toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                editChannelRequest(txtChannelNameInfo.getText().toString(), dialogDesc);
                showProgressBar();
            }
        }).negativeText(getString(R.string.cancel)).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT).input(getString(R.string.please_enter_group_description), txtDescription.getText().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                // Do something
                View positive = dialog.getActionButton(DialogAction.POSITIVE);
                dialogDesc = input.toString();
                if (!input.toString().equals(txtDescription.getText().toString())) {

                    positive.setClickable(true);
                    positive.setAlpha(1.0f);
                } else {
                    positive.setClickable(false);
                    positive.setAlpha(0.5f);
                }
            }
        }).show();
    }

    private void ChangeGroupName() {
        final LinearLayout layoutUserName = new LinearLayout(ActivityChannelProfile.this);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(ActivityChannelProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(ActivityChannelProfile.this);
        final EditText edtNameChannel = new EditText(ActivityChannelProfile.this);
        edtNameChannel.setHint(getResources().getString(R.string.st_username));
        edtNameChannel.setText(txtChannelNameInfo.getText().toString());
        edtNameChannel.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtNameChannel.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtNameChannel.setPadding(0, 8, 0, 8);
        edtNameChannel.setSingleLine(true);
        inputUserName.addView(edtNameChannel);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtNameChannel.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).title(getResources().getString(R.string.channel_name)).positiveText(getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);

        final String finalChannelName = title;
        positive.setEnabled(false);
        edtNameChannel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtNameChannel.getText().toString().equals(finalChannelName)) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }
        });

        G.onChannelEdit = new OnChannelEdit() {
            @Override
            public void onChannelEdit(final long roomId, final String name, final String description) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                        txtChannelNameInfo.setText(name);
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RequestChannelEdit().channelEdit(roomId, edtNameChannel.getText().toString(), txtDescription.getText().toString());
                dialog.dismiss();
                showProgressBar();
            }
        });

        edtNameChannel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });
        dialog.show();
    }

    //********** channel edit name and description

    private void editChannelResponse(long roomIdR, final String name, final String description) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                txtChannelNameInfo.setText(name);
                txtDescription.setText(description);

                prgWait.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void editChannelRequest(String name, String description) {
        new RequestChannelEdit().channelEdit(roomId, name, description);
    }

    //********** set roles

    private void setToAdmin(Long peerId) {
        new RequestChannelAddAdmin().channelAddAdmin(roomId, peerId);
    }

    private void setToModerator(Long peerId) {
        new RequestChannelAddModerator().channelAddModerator(roomId, peerId);
    }

    //********* kick user from roles

    public void kickMember(final Long peerId) {

        new MaterialDialog.Builder(ActivityChannelProfile.this).content(R.string.do_you_want_to_kick_this_member)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestChannelKickMember().channelKickMember(roomId, peerId);
                }
            })
            .show();



    }

    public void kickModerator(final Long peerId) {

        new MaterialDialog.Builder(ActivityChannelProfile.this).content(R.string.do_you_want_to_set_modereator_role_to_member)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestChannelKickModerator().channelKickModerator(roomId, peerId);
                }
            })
            .show();
    }

    public void kickAdmin(final Long peerId) {

        new MaterialDialog.Builder(ActivityChannelProfile.this).content(R.string.do_you_want_to_set_admin_role_to_member)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    new RequestChannelKickAdmin().channelKickAdmin(roomId, peerId);
                }
            })
            .show();




    }

    //************************************************** interfaces

    //***On Add Avatar Response From Server

    @Override
    public void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar) {
        /**
         * if another account do this action we haven't avatar source and have
         * to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            setAvatar();
        } else {
            HelperAvatar.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    runOnUiThread(new Runnable() {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HelperAvatar.avatarDelete(roomId, avatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
                    @Override
                    public void latestAvatarPath(String avatarPath) {
                        setImage(avatarPath);
                    }

                    @Override
                    public void showInitials(final String initials, final String color) {
                        runOnUiThread(new Runnable() {
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

    //***Edit Channel

    @Override
    public void onChannelEdit(long roomId, String name, String description) {
        editChannelResponse(roomId, name, description);
    }

    //***Delete Channel

    @Override
    public void onChannelDelete(long roomId) {
        closeActivity();
    }

    //***Left Channel

    @Override
    public void onChannelLeft(long roomId, long memberId) {
        closeActivity();
    }

    //***

    private void closeActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                ActivityChannelProfile.this.finish();
                if (ActivityChat.activityChat != null) {
                    ActivityChat.activityChat.finish();
                }
            }
        });
    }

    //***Get Member List

    //***Member
    @Override
    public void onChannelAddMember(Long roomId, Long userId, ProtoGlobal.ChannelRoom.Role role) {
        channelAddMemberResponse(roomId, userId, role);
    }

    @Override
    public void onChannelKickMember(long roomId, long memberId) {
        channelKickMember(roomId, memberId);
    }

    //***Moderator
    @Override
    public void onChannelAddModerator(long roomId, long memberId) {

    }

    @Override
    public void onChannelKickModerator(long roomId, long memberId) {

    }

    //***Admin
    @Override
    public void onChannelAddAdmin(long roomId, long memberId) {

    }

    @Override
    public void onChannelKickAdmin(long roomId, long memberId) {

    }

    //***time out and errors for either of this interfaces

    @Override
    public void onChannelRevokeLink(long roomId, final String inviteLink, final String inviteToken) {

        hideProgressBar();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                edtRevoke.setText("" + inviteLink);
                txtChannelLink.setText("" + inviteLink);
            }
        });

        Realm realm = Realm.getDefaultInstance();

        //channel info
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        final RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realmChannelRoom.setInviteLink(inviteLink);
                realmChannelRoom.setInvite_token(inviteToken);
            }
        });
        realm.close();
    }

    @Override
    public void onError(int majorCode, int minorCode) {
        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.normal_error), Snackbar.LENGTH_LONG);

                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });
    }

    @Override
    public void onTimeOut() {
        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);

                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });
    }

    private void showPopUp() {

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);

        TextView txtItem1 = (TextView) v.findViewById(R.id.dialog_text_item1_notification);

        TextView iconItem1 = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);

        if (isPrivate) {
            txtItem1.setText(getResources().getString(R.string.channel_title_convert_to_public));
            iconItem1.setText(getResources().getString(R.string.md_convert_to_public));
        } else {
            txtItem1.setText(getResources().getString(R.string.channel_title_convert_to_private));
            iconItem1.setText(getResources().getString(R.string.md_convert_to_private));

        }

        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPopup = true;

                if (isPrivate) {
                    convertToPublic();
                } else {
                    convertToPrivate();
                }
                dialog.dismiss();
            }
        });
    }

    private void convertToPrivate() {

        G.onChannelRemoveUsername = new OnChannelRemoveUsername() {
            @Override
            public void onChannelRemoveUsername(final long roomId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isPrivate = true;
                        setTextChannelLik();
                        Realm realm = Realm.getDefaultInstance();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                realmChannelRoom.setPrivate(true);
                            }
                        });
                        realm.close();
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        new MaterialDialog.Builder(ActivityChannelProfile.this).title(getString(R.string.channel_title_convert_to_private)).content(getString(R.string.channel_text_convert_to_private)).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestChannelRemoveUsername().channelRemoveUsername(roomId);
            }
        }).negativeText(R.string.B_cancel).show();
    }

    private void convertToPublic() {

        new MaterialDialog.Builder(ActivityChannelProfile.this).title(getString(R.string.channel_title_convert_to_public)).content(getString(R.string.channel_text_convert_to_public)).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
                setUsername();
            }
        }).negativeText(R.string.B_cancel).show();
    }

    private void setUsername() {
        final LinearLayout layoutUserName = new LinearLayout(ActivityChannelProfile.this);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(ActivityChannelProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(ActivityChannelProfile.this);
        final EditText edtUserName = new EditText(ActivityChannelProfile.this);
        edtUserName.setHint(getResources().getString(R.string.channel_title_channel_set_username));

        if (isPopup) {
            edtUserName.setText("iGap.net/");
        } else {
            edtUserName.setText("" + linkUsername);
        }

        edtUserName.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).title(getResources().getString(R.string.st_username)).positiveText(getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        G.onChannelCheckUsername = new OnChannelCheckUsername() {
            @Override
            public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {

                            positive.setEnabled(true);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("");
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + getResources().getString(R.string.INVALID));
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + getResources().getString(R.string.TAKEN));
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + getResources().getString(R.string.OCCUPYING_LIMIT_EXCEEDED));
                        }
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

        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().contains("iGap.net/")) {
                    edtUserName.setText("iGap.net/");
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                }

                if (HelperString.regexCheckUsername(editable.toString().replace("iGap.net/", ""))) {
                    String userName = edtUserName.getText().toString().replace("iGap.net/", "");
                    new RequestChannelCheckUsername().channelCheckUsername(roomId, userName);
                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError("" + getResources().getString(R.string.INVALID));
                }
            }
        });

        G.onChannelUpdateUsername = new OnChannelUpdateUsername() {
            @Override
            public void onChannelUpdateUsername(final long roomId, final String username) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        isPrivate = false;
                        dialog.dismiss();

                        linkUsername = username;
                        setTextChannelLik();

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                realmChannelRoom.setUsername("iGap.net" + edtUserName.getText().toString());
                                realmChannelRoom.setPrivate(false);
                            }
                        });
                        realm.close();
                    }
                });
            }

            @Override
            public void onError(final int majorCode, int minorCode, final int time) {

                switch (majorCode) {
                    case 457:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) dialog.dismiss();
                                dialogWaitTime(R.string.CHANNEL_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                            }
                        });
                        break;
                }
            }

            @Override
            public void onTimeOut() {

            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = edtUserName.getText().toString().replace("iGap.net/", "");
                new RequestChannelUpdateUsername().channelUpdateUsername(roomId, userName);
            }
        });

        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        // check each word with server

        dialog.show();
    }

    //*** show delete channel dialog

    private void deleteChannel() {
        String deleteText = "";
        int title;
        if (role.equals(ChannelChatRole.OWNER)) {
            deleteText = context.getString(R.string.do_you_want_delete_this);
            title = R.string.channel_delete;
        } else {
            deleteText = context.getString(R.string.do_you_want_left_this);
            title = R.string.channel_left;
        }

        new MaterialDialog.Builder(ActivityChannelProfile.this).title(title).content(deleteText).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                if (role.equals(ChannelChatRole.OWNER)) {
                    new RequestChannelDelete().channelDelete(roomId);
                } else {
                    new RequestChannelLeft().channelLeft(roomId);
                }

                prgWait.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).negativeText(R.string.B_cancel).show();
    }

    //*** set avatar image

    private void setImage(final String imagePath) {
        runOnUiThread(new Runnable() {
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

    private void notificationAndSound() {
        FragmentNotification fragmentNotification = new FragmentNotification();
        Bundle bundle = new Bundle();
        bundle.putString("PAGE", "CHANNEL");
        bundle.putLong("ID", roomId);
        fragmentNotification.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_channel_profile, fragmentNotification).commit();
    }

    //*** onActivityResult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            long avatarId = SUID.id().get();
            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                        filePath = AttachFile.mCurrentPhotoPath;
                        pathSaveImage = filePath;
                    } else {
                        ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                        filePath = AttachFile.imagePath;
                        pathSaveImage = filePath;
                    }

                    break;
                case AttachFile.request_code_image_from_gallery_single_select:

                    if (data.getData() == null) {
                        return;
                    }
                    filePath = AttachFile.getFilePathFromUri(data.getData());
                    pathSaveImage = filePath;
                    break;
            }

            showProgressBar();
            HelperUploadFile.startUploadTaskAvatar(filePath, avatarId, new HelperUploadFile.UpdateListener() {
                @Override
                public void OnProgress(int progress, FileUploadStructure struct) {
                    if (progress < 100) {
                        prgWait.setProgress(progress);
                    } else {
                        new RequestChannelAvatarAdd().channelAvatarAdd(roomId, struct.token);
                    }
                }

                @Override
                public void OnError() {
                    hideProgressBar();
                }
            });
        }
    }

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (prgWait != null) {
                    prgWait.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prgWait.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
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
}
