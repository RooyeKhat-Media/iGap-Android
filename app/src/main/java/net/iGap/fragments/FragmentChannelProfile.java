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
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
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
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.IntentRequests;
import net.iGap.module.MEditText;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;

import static android.content.Context.CLIPBOARD_SERVICE;
import static net.iGap.G.context;

public class FragmentChannelProfile extends BaseFragment implements OnChannelAddMember, OnChannelKickMember, OnChannelAddModerator, OnChannelKickModerator, OnChannelAddAdmin, OnChannelKickAdmin, OnChannelDelete, OnChannelLeft, OnChannelEdit, OnChannelAvatarAdd, OnChannelAvatarDelete, OnChannelRevokeLink {


    private AppBarLayout appBarLayout;
    private TextView txtChannelLink;
    private EmojiTextViewE txtChannelNameInfo, txtDescription;
    private MaterialDesignTextView imgPopupMenu;
    private CircleImageView imgCircleImageView;
    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    private TextView titleToolbar;
    private EmojiTextViewE txtChannelName;
    TextView txtSharedMedia;
    private MEditText edtRevoke;

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
    private Fragment fragment;
    private Realm realmChannelProfile;
    private RealmChangeListener<RealmModel> changeListener;
    private RealmRoom mRoom;
    private boolean isNotJoin = false;
    private static final String ROOM_ID = "RoomId";
    private static final String IS_NOT_JOIN = "is_not_join";
    public static final String FRAGMENT_TAG = "FragmentChannelProfile";
    private View v;

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
        return attachToSwipeBack(inflater.inflate(R.layout.activity_profile_channel, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment = this;

        v = view;

        realmChannelProfile = Realm.getDefaultInstance();

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
        Bundle extras = getArguments();
        roomId = extras.getLong(ROOM_ID);
        isNotJoin = extras.getBoolean(IS_NOT_JOIN);

        //+Realm realm = Realm.getDefaultInstance();

        //channel info
        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getChannelRoom() == null) {
            popBackStackFragment();
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
        fab = (FloatingActionButton) G.fragmentActivity.findViewById(R.id.pch_fab_addToChannel);
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

        txtSharedMedia = (TextView) view.findViewById(R.id.txt_shared_media);
        txtChannelNameInfo = (EmojiTextViewE) view.findViewById(R.id.txt_channel_name_info);
        prgWait = (ProgressBar) view.findViewById(R.id.agp_prgWaiting);
        AppUtils.setProgresColler(prgWait);

        LinearLayout lytSharedMedia = (LinearLayout) view.findViewById(R.id.lyt_shared_media);
        LinearLayout lytChannelName = (LinearLayout) view.findViewById(R.id.lyt_channel_name);
        LinearLayout lytChannelDescription = (LinearLayout) view.findViewById(R.id.lyt_description);
        lytListAdmin = (LinearLayout) view.findViewById(R.id.lyt_list_admin);
        lytListModerator = (LinearLayout) view.findViewById(R.id.lyt_list_moderator);
        lytDeleteChannel = (LinearLayout) view.findViewById(R.id.lyt_delete_channel);
        lytNotification = (LinearLayout) view.findViewById(R.id.lyt_notification);
        ViewGroup layoutSetting = (LinearLayout) view.findViewById(R.id.agp_ll_seetting);
        txtLinkTitle = (TextView) view.findViewById(R.id.txt_channel_link_title);
        ViewGroup vgRootAddMember = (ViewGroup) view.findViewById(R.id.agp_root_layout_add_member);
        ViewGroup ltLink = (ViewGroup) view.findViewById(R.id.layout_channel_link);
        imgPopupMenu = (MaterialDesignTextView) view.findViewById(R.id.pch_img_menuPopup);
        txtDescription = (EmojiTextViewE) view.findViewById(R.id.txt_description);

        Log.i("FFFFFFFFF", "onViewCreated: " + isNotJoin);
        if (isNotJoin) {
            layoutSetting.setVisibility(View.GONE);
        }

        if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            vgRootAddMember.setVisibility(View.GONE);
        }

        ViewGroup vgSignature = (ViewGroup) view.findViewById(R.id.agp_layout_signature);
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
            if (description == null || description.isEmpty() || description.length() == 0) {
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

        TextView txtDeletChannel = (TextView) view.findViewById(R.id.txt_delete_channel);
        if (role == ChannelChatRole.OWNER) {
            txtDeletChannel.setText(G.fragmentActivity.getResources().getString(R.string.channel_delete));
        } else {
            txtDeletChannel.setText(G.fragmentActivity.getResources().getString(R.string.channel_left));
        }
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

        final RippleView rippleBack = (RippleView) view.findViewById(R.id.pch_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });
        appBarLayout = (AppBarLayout) view.findViewById(R.id.pch_appbar);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.acp_ll_collapsing_toolbar_layout);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));

        titleToolbar = (TextView) view.findViewById(R.id.pch_txt_titleToolbar);
        titleToolbar.setText("" + title);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                ViewGroup viewGroup = (ViewGroup) G.fragmentActivity.findViewById(R.id.pch_root_circleImage);
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

        RippleView rippleMenu = (RippleView) view.findViewById(R.id.pch_ripple_menuPopup);
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

        imgCircleImageView = (CircleImageView) view.findViewById(R.id.pch_img_circleImage);
        imgCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.channel);
                    fragment.appBarLayout = fab;
                    //new HelperFragment(fragment).setResourceContainer(R.id.fragmentContainer_channel_profile).load();
                    new HelperFragment(fragment).setReplace(false).load();
                }
            }
        });

        txtChannelLink = (TextView) view.findViewById(R.id.txt_channel_link);

        lytSharedMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(FragmentShearedMedia.newInstance(roomId)).setReplace(false).load();
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

        txtChannelName = (EmojiTextViewE) view.findViewById(R.id.txt_channel_name);

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

        attachFile = new AttachFile(G.fragmentActivity);

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
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HelperAvatar.avatarDelete(roomId, finalMAvatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
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

        TextView txtSignature = (TextView) view.findViewById(R.id.agp_txt_signature);
        final ToggleButton toggleEnableSignature = (ToggleButton) view.findViewById(R.id.agp_toggle_signature);

        if (isSignature) {
            toggleEnableSignature.setChecked(true);
        } else {
            toggleEnableSignature.setChecked(false);
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

                        if (toggleEnableSignature.isChecked()) {
                            toggleEnableSignature.setChecked(false);
                        } else {
                            toggleEnableSignature.setChecked(true);
                        }
                    }
                });
            }
        };

        toggleEnableSignature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    new RequestChannelUpdateSignature().channelUpdateSignature(roomId, true);
                } else {
                    new RequestChannelUpdateSignature().channelUpdateSignature(roomId, false);
                }
            }
        });


        txtSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toggleEnableSignature.setChecked(!toggleEnableSignature.isChecked());

            }
        });

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
                                    if (HelperCalander.isPersianUnicode) {
                                        txtSharedMedia.setText(HelperCalander.convertToUnicodeFarsiNumber(countText));
                                    } else {
                                        txtSharedMedia.setText(countText);
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
            if (txtSharedMedia != null) {
                txtSharedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
            }
        }
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
        if (realmChannelProfile != null && !realmChannelProfile.isClosed()) {
            realmChannelProfile.close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FragmentChat fragment = (FragmentChat) getFragmentManager().findFragmentByTag(FragmentChat.class.getName());
        if (fragment != null && fragment.isVisible()) {
            fragment.onResume();
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
            txtChannelLink.setText(inviteLink);
            txtLinkTitle.setText(G.fragmentActivity.getResources().getString(R.string.channel_link));
        } else {
            txtChannelLink.setText("" + linkUsername);
            txtLinkTitle.setText(G.fragmentActivity.getResources().getString(R.string.st_username));
        }
    }

    private void dialogRevoke() {

        String link = txtChannelLink.getText().toString();

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
                        copy = txtChannelLink.getText().toString();
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
            }
        });
        dialog.show();
    }

    private void dialogCopyLink() {

        String link = txtChannelLink.getText().toString();

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
        txtLink.setText("iGap.net/" + link);
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
                        copy = "iGap.net/" + txtChannelLink.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                })
                .build();

        dialog.show();
    }

    private void setAvatar() {
        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, true, new OnAvatarGet() {
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
        FragmentShowMember fragment = FragmentShowMember.newInstance1(this.fragment, roomId, role.toString(), G.userId, SelectedRole, isNeedGetMemberList);
        new HelperFragment(fragment).setReplace(false).load();
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

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(G.fragmentActivity.getResources().getString(R.string.from_camera))) {

                    if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                        try {
                            HelperPermision.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
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

    //********** update member count

    private void setMemberCount(final long roomId, final boolean plus) {
        RealmRoom.updateMemberCount(roomId, plus);
    }

    //********** channel Add Member

    private void channelAddMemberResponse(long roomIdResponse, final long userId, final ProtoGlobal.ChannelRoom.Role role) {
        if (roomIdResponse == roomId) {

            setMemberCount(roomId, true);

            RealmRegisteredInfo realmRegistered = RealmRegisteredInfo.getRegistrationInfo(getRealm(), userId);
            if (realmRegistered == null) {
                new RequestUserInfo().userInfo(userId, roomId + "");
            }
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
        MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.channel_description).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).alwaysCallInputCallback().widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                editChannelRequest(txtChannelNameInfo.getText().toString(), dialogDesc);
                showProgressBar();
            }
        }).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT).input(G.fragmentActivity.getResources().getString(R.string.please_enter_group_description), txtDescription.getText().toString(), new MaterialDialog.InputCallback() {
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
        }).build();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AndroidUtils.closeKeyboard(v);
            }
        });

        dialog.show();
    }

    private void ChangeGroupName() {
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
        edtNameChannel.setText(txtChannelNameInfo.getText().toString());
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

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_name))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.save))
                .customView(layoutUserName, true)
                .widgetColor(G.context.getResources().getColor(R.color.toolbar_background))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .build();

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

    //********** channel edit name and description

    private void editChannelResponse(long roomIdR, final String name, final String description) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                txtChannelNameInfo.setText(name);
                txtDescription.setText(description);

                prgWait.setVisibility(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_kick_this_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestChannelKickMember().channelKickMember(roomId, peerId);
            }
        }).show();



    }

    public void kickModerator(final Long peerId) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_modereator_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestChannelKickModerator().channelKickModerator(roomId, peerId);
            }
        }).show();
    }

    public void kickAdmin(final Long peerId) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_admin_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestChannelKickAdmin().channelKickAdmin(roomId, peerId);
            }
        }).show();




    }

    //************************************************** interfaces

    //***On Add Avatar Response From Server

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

    //***Get Member List

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

    //***time out and errors for either of this interfaces

    @Override
    public void onChannelRevokeLink(final long roomId, final String inviteLink, final String inviteToken) {

        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                edtRevoke.setText("" + inviteLink);
                txtChannelLink.setText("" + inviteLink);

                //+Realm realm = Realm.getDefaultInstance();
                //channel info
                RealmChannelRoom.revokeLink(inviteLink, inviteToken);
            }
        });
        //realm.close();
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

    private void showPopUp() {

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();

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
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isPrivate = true;
                        setTextChannelLik();
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

    private void convertToPublic() {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_title_convert_to_public)).content(G.fragmentActivity.getResources().getString(R.string.channel_text_convert_to_public)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
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
        edtUserName.setHint(G.fragmentActivity.getResources().getString(R.string.channel_title_channel_set_username));
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

                String userName = edtUserName.getText().toString().replace("iGap.net/", "");
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

    //*** show delete channel dialog

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

                prgWait.setVisibility(View.VISIBLE);
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).negativeText(R.string.no).show();
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

    private void notificationAndSound() {
        FragmentNotification fragmentNotification = new FragmentNotification();
        Bundle bundle = new Bundle();
        bundle.putString("PAGE", "CHANNEL");
        bundle.putLong("ID", roomId);
        fragmentNotification.setArguments(bundle);

        new HelperFragment(fragmentNotification).setReplace(false).load();
    }

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
        }
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
}
