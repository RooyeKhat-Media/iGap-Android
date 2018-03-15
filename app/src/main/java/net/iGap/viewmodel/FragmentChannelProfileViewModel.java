package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChannelProfile;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentNotification;
import net.iGap.fragments.FragmentShearedMedia;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.fragments.FragmentShowMember;
import net.iGap.fragments.ShowCustomList;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnChannelAddAdmin;
import net.iGap.interfaces.OnChannelAddMember;
import net.iGap.interfaces.OnChannelAddModerator;
import net.iGap.interfaces.OnChannelCheckUsername;
import net.iGap.interfaces.OnChannelDelete;
import net.iGap.interfaces.OnChannelEdit;
import net.iGap.interfaces.OnChannelKickAdmin;
import net.iGap.interfaces.OnChannelKickMember;
import net.iGap.interfaces.OnChannelKickModerator;
import net.iGap.interfaces.OnChannelLeft;
import net.iGap.interfaces.OnChannelRemoveUsername;
import net.iGap.interfaces.OnChannelRevokeLink;
import net.iGap.interfaces.OnChannelUpdateReactionStatus;
import net.iGap.interfaces.OnChannelUpdateSignature;
import net.iGap.interfaces.OnChannelUpdateUsername;
import net.iGap.interfaces.OnMenuClick;
import net.iGap.interfaces.OnSelectedList;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.Contacts;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.MEditText;
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
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelAddAdmin;
import net.iGap.request.RequestChannelAddMember;
import net.iGap.request.RequestChannelAddModerator;
import net.iGap.request.RequestChannelCheckUsername;
import net.iGap.request.RequestChannelDelete;
import net.iGap.request.RequestChannelEdit;
import net.iGap.request.RequestChannelLeft;
import net.iGap.request.RequestChannelRemoveUsername;
import net.iGap.request.RequestChannelRevokeLink;
import net.iGap.request.RequestChannelUpdateReactionStatus;
import net.iGap.request.RequestChannelUpdateSignature;
import net.iGap.request.RequestChannelUpdateUsername;
import net.iGap.request.RequestUserInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;

import static android.content.Context.CLIPBOARD_SERVICE;
import static net.iGap.G.context;

public class FragmentChannelProfileViewModel
        implements OnChannelAddMember, OnChannelKickMember, OnChannelAddModerator, OnChannelUpdateReactionStatus, OnChannelKickModerator, OnChannelAddAdmin, OnChannelKickAdmin, OnChannelDelete,
        OnChannelLeft, OnChannelEdit, OnChannelRevokeLink {

    public static final String FRAGMENT_TAG = "FragmentChannelProfile";
    private static final String ROOM_ID = "RoomId";
    private static final String IS_NOT_JOIN = "is_not_join";
    public static OnMenuClick onMenuClick;
    public ChannelChatRole role;
    public long roomId;
    public boolean isPrivate;
    public boolean isVerified = false;
    public ObservableBoolean isCheckedSignature = new ObservableBoolean(false);
    public ObservableBoolean isReactionStatus = new ObservableBoolean(true);
    public ObservableField<Integer> showLayoutReactStatus = new ObservableField<>(View.GONE);
    public ObservableBoolean channelNameEnable = new ObservableBoolean(true);
    public ObservableBoolean channelDescriptionEnable = new ObservableBoolean(true);
    public ObservableField<String> callbackChannelName = new ObservableField<>("");
    public ObservableField<String> callbackChannelLink = new ObservableField<>("");
    public ObservableField<SpannableStringBuilder> callbackChannelDescription = new ObservableField<>();
    public ObservableField<String> callbackChannelSharedMedia = new ObservableField<>("");
    public ObservableField<String> callBackDeleteLeaveChannel = new ObservableField<>(G.context.getResources().getString(R.string.delete_and_leave_channel));
    public ObservableField<String> callbackChannelLinkTitle = new ObservableField<>(G.context.getResources().getString(R.string.channel_link));
    public ObservableField<Integer> addMemberVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> prgWaitingVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> settingVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> descriptionVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> signatureVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> fabVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> menuPopupVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> verifyTextVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> listAdminVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> moderatorVisibility = new ObservableField<>(View.VISIBLE);
    private String title;
    private String initials;
    private String color;
    private String participantsCountLabel;
    private String description;
    private String inviteLink;
    private long noLastMessage;
    private MEditText edtRevoke;
    private RealmList<RealmMember> members;
    private AttachFile attachFile;
    private String linkUsername;
    private boolean isSignature;
    private boolean isPopup = false;
    private boolean isNeedGetMemberList = true;
    private Fragment fragment;
    private Realm realmChannelProfile;
    private RealmChangeListener<RealmModel> changeListener;
    private RealmRoom mRoom;
    private boolean isNotJoin = false;
    private String dialogDesc;
    private String dialogName;

    public FragmentChannelProfileViewModel(Bundle arguments, FragmentChannelProfile fragmentChannelProfile) {
        this.fragment = fragmentChannelProfile;
        getInfo(arguments);
    }

    public void onClickRippleBack(View v) {
        if (FragmentChannelProfile.onBackFragment != null)
            FragmentChannelProfile.onBackFragment.onBack();
    }

    public void onClickRippleMenuPopup(View v) {
        showPopUp(v);
    }

    public void onClickChannelListAdmin(View v) {
        showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
    }

    public void onClickChannelListModerator(View v) {

        showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());

    }

    public void onClickChannelNotification(View v) {
        notificationAndSound();
    }

    public void onClickChannelDeleteChannel(View v) {
        deleteChannel();
    }

    public void onClickCircleImage(View v) {
        if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
            FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.channel);
            //fragment.appBarLayout = fab;
            //new HelperFragment(fragment).setResourceContainer(R.id.fragmentContainer_channel_profile).load();
            new HelperFragment(fragment).setReplace(false).load();
        }

    }

    public void onClickChannelSharedMedia(View v) {
        new HelperFragment(FragmentShearedMedia.newInstance(roomId)).setReplace(false).load();
    }

    public void onClickChannelName(View v) {
        ChangeGroupName(v);
    }

    public void onClickChannelDescription(View v) {
        if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
            ChangeGroupDescription(v);
        }
    }

    public void onClickChannelShowMember(View v) {
        showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString());
    }

    public void onClickChannelAddMember(View v) {
        addMemberToChannel();

    }

    public void onClickChannelLink(View v) {
        isPopup = false;
        if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
            if (isPrivate) {
                dialogRevoke();
            } else {
                setUsername(v);
            }
        } else {
            dialogCopyLink();
        }

    }

    public void onClickChannelSignature(View v) {

        isCheckedSignature.set(!isCheckedSignature.get());
        if (isCheckedSignature.get()) {
            new RequestChannelUpdateSignature().channelUpdateSignature(roomId, true);
        } else {
            new RequestChannelUpdateSignature().channelUpdateSignature(roomId, false);
        }
    }

    public void onClickChannelReactionStatus(View v) {
        if (isReactionStatus.get()) {
            new RequestChannelUpdateReactionStatus().channelUpdateReactionStatus(roomId, false);
        } else {
            new RequestChannelUpdateReactionStatus().channelUpdateReactionStatus(roomId, true);
        }
        showProgressBar();
    }

    private void getInfo(Bundle arguments) {

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


        realmChannelProfile = Realm.getDefaultInstance();

        roomId = arguments.getLong(ROOM_ID);
        isNotJoin = arguments.getBoolean(IS_NOT_JOIN);

        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getChannelRoom() == null) {
            if (FragmentChannelProfile.onBackFragment != null)
                FragmentChannelProfile.onBackFragment.onBack();
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
        isVerified = realmChannelRoom.isVerified();


        if (realmChannelRoom.isReactionStatus()) {
            isReactionStatus.set(true);
        } else {
            isReactionStatus.set(false);
        }

        if (role == ChannelChatRole.OWNER) {
            showLayoutReactStatus.set(View.VISIBLE);
            G.onChannelUpdateReactionStatus = this;
        } else {
            showLayoutReactStatus.set(View.GONE);
            G.onChannelUpdateReactionStatus = null;
        }

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
        callbackChannelName.set("" + title);

        if (isNotJoin) {
            settingVisibility.set(View.GONE);
        }

        if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            addMemberVisibility.set(View.GONE);
        }

        if (role == ChannelChatRole.OWNER) {

            signatureVisibility.set(View.VISIBLE);
        } else {
            channelNameEnable.set(false);
            channelDescriptionEnable.set(false);
        }

        if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {

            fabVisibility.set(View.VISIBLE);

            menuPopupVisibility.set(View.VISIBLE);
        } else {
            fabVisibility.set(View.GONE);
            menuPopupVisibility.set(View.GONE);
        }

        if (role != ChannelChatRole.OWNER) {
            if (description == null || description.isEmpty() || description.length() == 0) {
                descriptionVisibility.set(View.GONE);
            }
        }
        if (role == ChannelChatRole.OWNER) {
            callBackDeleteLeaveChannel.set(G.fragmentActivity.getResources().getString(R.string.channel_delete));
        } else {
            callBackDeleteLeaveChannel.set(G.fragmentActivity.getResources().getString(R.string.channel_left));
        }

        callbackChannelDescription.set(new SpannableStringBuilder(""));

        if (description != null && !description.isEmpty()) {
            SpannableStringBuilder spannableStringBuilder = HelperUrl.setUrlLink(description, true, false, null, true);
            if (spannableStringBuilder != null) {
                callbackChannelDescription.set(spannableStringBuilder);
            }
        }

        callbackChannelName.set(title);

        if (isSignature) {
            isCheckedSignature.set(true);
        } else {
            isCheckedSignature.set(false);
        }

        if (realmChannelRoom.isVerified()) {
            verifyTextVisibility.set(View.VISIBLE);
        } else {
            verifyTextVisibility.set(View.INVISIBLE);
        }

        G.onChannelUpdateSignature = new OnChannelUpdateSignature() {
            @Override
            public void onChannelUpdateSignatureResponse(final long roomId, final boolean signature) {
                // handle realm to response class
            }

            @Override
            public void onError(int majorCode, int minorCode) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (isCheckedSignature.get()) {
                            isCheckedSignature.set(false);
                        } else {
                            isCheckedSignature.set(true);
                        }
                    }
                });
            }
        };

        setTextChannelLik();
        attachFile = new AttachFile(G.fragmentActivity);
        initRecycleView();
        showAdminOrModeratorList();

        FragmentShearedMedia.getCountOfSharedMedia(roomId);

    }

    public void onResume() {

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
                                    if (HelperCalander.isPersianUnicode) {
                                        callbackChannelSharedMedia.set(HelperCalander.convertToUnicodeFarsiNumber(countText));
                                    } else {
                                        callbackChannelSharedMedia.set(countText);
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
            if (callbackChannelSharedMedia.get() != null) {
                callbackChannelSharedMedia.set(context.getString(R.string.there_is_no_sheared_media));
            }
        }
    }

    public void onStop() {
        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
        hideProgressBar();
    }

    public void onDestroy() {
        if (realmChannelProfile != null && !realmChannelProfile.isClosed()) {
            realmChannelProfile.close();
        }
    }

    private Realm getRealm() {
        if (realmChannelProfile == null || realmChannelProfile.isClosed()) {
            realmChannelProfile = Realm.getDefaultInstance();
        }
        return realmChannelProfile;
    }

    private void setTextChannelLik() {

        if (isPrivate) {
            callbackChannelLink.set(inviteLink);
            callbackChannelLinkTitle.set(G.fragmentActivity.getResources().getString(R.string.channel_link));
        } else {
            callbackChannelLink.set("" + linkUsername);
            callbackChannelLinkTitle.set(G.fragmentActivity.getResources().getString(R.string.st_username));
        }
    }

    private void dialogRevoke() {

        String link = callbackChannelLink.get();

        final LinearLayout layoutRevoke = new LinearLayout(G.fragmentActivity);
        layoutRevoke.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputRevoke = new TextInputLayout(G.fragmentActivity);
        edtRevoke = new MEditText(G.fragmentActivity);
        edtRevoke.setHint(G.fragmentActivity.getResources().getString(R.string.channel_link_hint_revoke));
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

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_link_title_revoke))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.revoke))
                .customView(layoutRevoke, true)
                .widgetColor(G.context.getResources().getColor(R.color.toolbar_background))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .neutralText(R.string.array_Copy)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = callbackChannelLink.get();
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
                new RequestChannelRevokeLink().channelRevokeLink(roomId);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //******Add And Moderator List

    private void dialogCopyLink() {

        String link = callbackChannelLink.get();

        final LinearLayout layoutChannelLink = new LinearLayout(G.fragmentActivity);
        layoutChannelLink.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputChannelLink = new TextInputLayout(G.fragmentActivity);
        MEditText edtLink = new MEditText(G.fragmentActivity);
        edtLink.setHint(G.fragmentActivity.getResources().getString(R.string.channel_public_hint_revoke));
        edtLink.setTypeface(G.typeface_IRANSansMobile);
        edtLink.setText(link);
        edtLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtLink.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtLink.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtLink.setPadding(0, 8, 0, 8);
        edtLink.setEnabled(false);
        edtLink.setSingleLine(true);
        inputChannelLink.addView(edtLink);
        inputChannelLink.addView(viewRevoke, viewParams);

        TextView txtLink = new TextView(G.fragmentActivity);
        txtLink.setText(Config.IGAP_LINK_PREFIX + link);
        txtLink.setTextColor(G.context.getResources().getColor(R.color.gray_6c));

        viewRevoke.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtLink.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutChannelLink.addView(inputChannelLink, layoutParams);
        layoutChannelLink.addView(txtLink, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_link))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.array_Copy))
                .customView(layoutChannelLink, true)
                .widgetColor(G.context.getResources().getColor(R.color.toolbar_background))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = Config.IGAP_LINK_PREFIX + callbackChannelLink.get();
                        ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                })
                .build();

        dialog.show();
    }

    //****** show admin or moderator list

    private void initRecycleView() {

        onMenuClick = new OnMenuClick() {
            @Override
            public void clicked(View view, StructContactInfo info) {
                new CreatePopUpMessage().show(view, info);
            }
        };
    }

    //****** add member

    private void showListForCustomRole(String SelectedRole) {
        if (role != null) {
            FragmentShowMember fragment = FragmentShowMember.newInstance1(this.fragment, roomId, role.toString(), G.userId, SelectedRole, isNeedGetMemberList);
            new HelperFragment(fragment).setReplace(false).load();
            isNeedGetMemberList = false;
        }
    }

    private void showAdminOrModeratorList() {
        if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            listAdminVisibility.set(View.GONE);
            moderatorVisibility.set(View.GONE);
        } else if (role == ChannelChatRole.ADMIN) {
            listAdminVisibility.set(View.GONE);
        }
    }

    private void addMemberToChannel() {
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
            public void getSelectedList(boolean result, String message, int countForShowLastMessage, final ArrayList<StructContactInfo> list) {

                for (int i = 0; i < list.size(); i++) {
                    new RequestChannelAddMember().channelAddMember(roomId, list.get(i).peerId);
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", false);
        bundle.putLong("COUNT_MESSAGE", noLastMessage);
        fragment.setArguments(bundle);

        new HelperFragment(fragment).setReplace(false).load();
    }

    //****** create popup

    @Override
    public void OnChannelUpdateReactionStatusResponse(long roomId, boolean status) {
        if (roomId == this.roomId) {
            isReactionStatus.set(status);
        }
        hideProgressBar();
    }

    @Override
    public void OnChannelUpdateReactionStatusError() {
        hideProgressBar();
    }

    //********** channel Add Member

    private void setMemberCount(final long roomId, final boolean plus) {
        RealmRoom.updateMemberCount(roomId, plus);
    }

    private void channelAddMemberResponse(long roomIdResponse, final long userId, final ProtoGlobal.ChannelRoom.Role role) {
        if (roomIdResponse == roomId) {

            setMemberCount(roomId, true);

            RealmRegisteredInfo realmRegistered = RealmRegisteredInfo.getRegistrationInfo(getRealm(), userId);
            if (realmRegistered == null) {
                new RequestUserInfo().userInfo(userId, roomId + "");
            }
        }
    }

    //********** dialog for edit channel

    private void channelKickMember(final long roomIdResponse, final long memberId) {
        if (roomIdResponse == roomId) {
            setMemberCount(roomId, false);
        }
    }

    private void ChangeGroupDescription(final View v) {
        MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.channel_description).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).alwaysCallInputCallback().widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                editChannelRequest(callbackChannelName.get(), dialogDesc);
                showProgressBar();
            }
        }).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT).input(G.fragmentActivity.getResources().getString(R.string.please_enter_group_description), callbackChannelDescription.get().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                // Do something
                View positive = dialog.getActionButton(DialogAction.POSITIVE);
                dialogDesc = input.toString();
                if (!input.toString().equals(callbackChannelDescription.get().toString())) {

                    positive.setClickable(true);
                    positive.setAlpha(1.0f);
                } else {
                    positive.setClickable(false);
                    positive.setAlpha(0.5f);
                }
            }
        }).build();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AndroidUtils.closeKeyboard(v);
            }
        });

        dialog.show();
    }

    private void ChangeGroupName(final View v) {
        final LinearLayout layoutUserName = new LinearLayout(G.fragmentActivity);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(G.fragmentActivity);
        final EmojiEditTextE edtNameChannel = new EmojiEditTextE(G.fragmentActivity);
        edtNameChannel.setHint(G.fragmentActivity.getResources().getString(R.string.st_username));
        edtNameChannel.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edtNameChannel.setTypeface(G.typeface_IRANSansMobile);
        edtNameChannel.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtNameChannel.setText(callbackChannelName.get());
        edtNameChannel.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtNameChannel.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtNameChannel.setPadding(0, 8, 0, 8);
        edtNameChannel.setSingleLine(true);
        inputUserName.addView(edtNameChannel);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtNameChannel.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_name)).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

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
                        callbackChannelName.set(name);
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

                new RequestChannelEdit().channelEdit(roomId, edtNameChannel.getText().toString(), callbackChannelDescription.get().toString());
                dialog.dismiss();
                showProgressBar();
            }
        });

        edtNameChannel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                AndroidUtils.closeKeyboard(v);
            }
        });

        dialog.show();

    }

    private void editChannelResponse(long roomIdR, final String name, final String description) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                callbackChannelName.set(name);

                SpannableStringBuilder spannableStringBuilder = HelperUrl.setUrlLink(description, true, false, null, true);
                if (spannableStringBuilder != null) {
                    callbackChannelDescription.set(spannableStringBuilder);
                } else {
                    callbackChannelDescription.set(new SpannableStringBuilder(""));
                }

                prgWaitingVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    //********** channel edit name and description

    private void editChannelRequest(String name, String description) {
        new RequestChannelEdit().channelEdit(roomId, name, description);
    }

    private void setToAdmin(Long peerId) {
        new RequestChannelAddAdmin().channelAddAdmin(roomId, peerId);
    }

    //********** set roles

    private void setToModerator(Long peerId) {
        new RequestChannelAddModerator().channelAddModerator(roomId, peerId);
    }

    @Override
    public void onChannelEdit(long roomId, String name, String description) {
        editChannelResponse(roomId, name, description);
    }


    //************************************************** interfaces

    //***On Add Avatar Response From Server

    //***Edit Channel

    @Override
    public void onChannelDelete(long roomId) {
        closeActivity();
    }

    //***Delete Channel

    @Override
    public void onChannelLeft(long roomId, long memberId) {
        closeActivity();
    }

    //***Left Channel

    private void closeActivity() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                if (FragmentChat.finishActivity != null) {
                    FragmentChat.finishActivity.finishActivity();
                }
            }
        });
    }

    //***

    //***Member
    @Override
    public void onChannelAddMember(final Long roomId, final Long userId, final ProtoGlobal.ChannelRoom.Role role) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                channelAddMemberResponse(roomId, userId, role);
            }
        });
    }

    @Override
    public void onChannelKickMember(final long roomId, final long memberId) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                channelKickMember(roomId, memberId);
            }
        });
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

    @Override
    public void onChannelRevokeLink(final long roomId, final String inviteLink, final String inviteToken) {

        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                callbackChannelLink.set("" + inviteLink);
                RealmChannelRoom.revokeLink(roomId, inviteLink, inviteToken);
            }
        });
    }

    //***time out and errors for either of this interfaces

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

    private void showPopUp(View view) {

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        final View v = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);

        TextView txtItem1 = (TextView) v.findViewById(R.id.dialog_text_item1_notification);

        TextView iconItem1 = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);

        if (isPrivate) {
            txtItem1.setText(G.fragmentActivity.getResources().getString(R.string.channel_title_convert_to_public));
            iconItem1.setText(G.fragmentActivity.getResources().getString(R.string.md_convert_to_public));
        } else {
            txtItem1.setText(G.fragmentActivity.getResources().getString(R.string.channel_title_convert_to_private));
            iconItem1.setText(G.fragmentActivity.getResources().getString(R.string.md_convert_to_private));

        }

        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void convertToPrivate() {

        G.onChannelRemoveUsername = new OnChannelRemoveUsername() {
            @Override
            public void onChannelRemoveUsername(final long roomId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isPrivate = true;
                        if (inviteLink == null || inviteLink.isEmpty() || inviteLink.equals("https://")) {
                            new RequestChannelRevokeLink().channelRevokeLink(roomId);
                        } else {
                            setTextChannelLik();
                        }
                        RealmRoom.setPrivate(roomId);
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_title_convert_to_private)).content(G.fragmentActivity.getResources().getString(R.string.channel_text_convert_to_private)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestChannelRemoveUsername().channelRemoveUsername(roomId);
            }
        }).negativeText(R.string.no).show();
    }

    private void convertToPublic(final View v) {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_title_convert_to_public)).content(G.fragmentActivity.getResources().getString(R.string.channel_text_convert_to_public)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
                setUsername(v);
            }
        }).negativeText(R.string.no).show();
    }

    private void setUsername(final View v) {
        final LinearLayout layoutUserName = new LinearLayout(G.fragmentActivity);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(G.fragmentActivity);
        final MEditText edtUserName = new MEditText(G.fragmentActivity);
        edtUserName.setHint(G.fragmentActivity.getResources().getString(R.string.channel_title_channel_set_username));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            edtUserName.setTextDirection(View.TEXT_DIRECTION_LTR);
        }
        edtUserName.setTypeface(G.typeface_IRANSansMobile);
        edtUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));

        if (isPopup) {
            edtUserName.setText(Config.IGAP_LINK_PREFIX);
        } else {
            edtUserName.setText(Config.IGAP_LINK_PREFIX + linkUsername);
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

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_username)).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        G.onChannelCheckUsername = new OnChannelCheckUsername() {
            @Override
            public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {

                            positive.setEnabled(true);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("");
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.TAKEN));
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
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

            @Override
            public void onTimeOut() {

            }
        };

        edtUserName.setSelection((edtUserName.getText().toString().length()));
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtUserName.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(edtUserName, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().startsWith(Config.IGAP_LINK_PREFIX)) {
                    edtUserName.setText(Config.IGAP_LINK_PREFIX);
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                } else {
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                }


                if (HelperString.regexCheckUsername(editable.toString().replace(Config.IGAP_LINK_PREFIX, ""))) {
                    String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
                    new RequestChannelCheckUsername().channelCheckUsername(roomId, userName);
                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                }
            }
        });

        G.onChannelUpdateUsername = new OnChannelUpdateUsername() {
            @Override
            public void onChannelUpdateUsername(final long roomId, final String username) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        isPrivate = false;
                        dialog.dismiss();

                        linkUsername = username;
                        setTextChannelLik();
                    }
                });
            }

            @Override
            public void onError(final int majorCode, int minorCode, final int time) {

                switch (majorCode) {
                    case 457:
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) dialog.dismiss();
                                dialogWaitTime(R.string.limit_for_set_username, time, majorCode);
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

                String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
                new RequestChannelUpdateUsername().channelUpdateUsername(roomId, userName);
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

    private void deleteChannel() {
        String deleteText = "";
        int title;
        if (role.equals(ChannelChatRole.OWNER)) {
            deleteText = context.getString(R.string.do_you_want_delete_this_channel);
            title = R.string.channel_delete;
        } else {
            deleteText = context.getString(R.string.do_you_want_leave_this_channel);
            title = R.string.channel_left;
        }

        new MaterialDialog.Builder(G.fragmentActivity).title(title).content(deleteText).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                if (role.equals(ChannelChatRole.OWNER)) {
                    new RequestChannelDelete().channelDelete(roomId);
                } else {
                    new RequestChannelLeft().channelLeft(roomId);
                }

                prgWaitingVisibility.set(View.VISIBLE);
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).negativeText(R.string.no).show();
    }

    //*** show delete channel dialog

    private void notificationAndSound() {
        FragmentNotification fragmentNotification = new FragmentNotification();
        Bundle bundle = new Bundle();
        bundle.putString("PAGE", "CHANNEL");
        bundle.putLong("ID", roomId);
        fragmentNotification.setArguments(bundle);

        new HelperFragment(fragmentNotification).setReplace(false).load();
    }

    //*** notification and sounds

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaitingVisibility.get() != null) {
                    prgWaitingVisibility.set(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    //*** onActivityResult

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaitingVisibility.get() != null) {
                    prgWaitingVisibility.set(View.GONE);
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
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

    private class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(G.fragmentActivity, view, Gravity.TOP);
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
                            ((FragmentChannelProfile) fragment).kickAdmin(info.peerId);
                            return true;
                        case R.id.menu_remove_moderator:
                            ((FragmentChannelProfile) fragment).kickModerator(info.peerId);
                            return true;
                        case R.id.menu_kick:
                            ((FragmentChannelProfile) fragment).kickMember(info.peerId);
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


}
