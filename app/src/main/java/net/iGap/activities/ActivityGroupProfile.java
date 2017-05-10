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
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
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
import io.realm.RealmResults;
import io.realm.Sort;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.SUID;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupCheckUsername;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.realm.RealmUserInfo;
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

import static net.iGap.G.context;
import static net.iGap.R.id.fragmentContainer_group_profile;

public class ActivityGroupProfile extends ActivityEnhanced implements OnGroupAvatarResponse, OnGroupAvatarDelete, OnGroupRevokeLink {

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
    private TextView txtGroupNameTitle;
    private TextView txtGroupName;
    private TextView txtGroupDescription;
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

    public static OnMenuClick onMenuClick;
    private Long userID = 0l;
    private boolean isPrivate;
    private TextView txtLinkTitle;
    private TextView txtGroupLink;
    private boolean isPopup = false;
    private ViewGroup ltLink;

    private long startMessageId = 0;

    private PopupWindow popupWindow;
    private ProgressBar prgWait;

    private boolean isNeedgetContactlist = true;

    private Realm mRealm;
    private RealmChangeListener<RealmModel> changeListener;
    private RealmRoom mRoom;

    @Override protected void onStop() {
        super.onStop();

        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) mRealm.close();
    }

    @Override protected void onResume() {

        super.onResume();

        mRoom = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (mRoom != null) {

            if (changeListener == null) {

                changeListener = new RealmChangeListener<RealmModel>() {
                    @Override public void onChange(final RealmModel element) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                String countText = ((RealmRoom) element).getSharedMediaCount();

                                if (countText == null || countText.length() == 0) {
                                    txtNumberOfSharedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
                                } else {
                                    if (HelperCalander.isLanguagePersian) {
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
            txtNumberOfSharedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(reciverOnGroupChangeName, new IntentFilter("Intent_filter_on_change_group_name"));
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);
        Bundle extras = getIntent().getExtras();
        roomId = extras.getLong("RoomId");

        mRealm = Realm.getDefaultInstance();

        //group info
        RealmRoom realmRoom = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getGroupRoom() == null) {
            //HelperError.showSnackMessage(getClientErrorCode(-2, 0));
            finish();
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

        ViewGroup listMemberGroup = (ViewGroup) findViewById(R.id.agp_root_layout_group_add_member);
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

        RealmUserInfo userInfo = mRealm.where(RealmUserInfo.class).findFirst();
        if (userInfo != null) userID = userInfo.getUserId();

        //realm.close(); // in fillItem when make iterator with members client will be have error . This Realm instance has already been closed, making it unusable.
        initComponent();

        attachFile = new AttachFile(this);
        G.onGroupAvatarResponse = this;
        G.onGroupAvatarDelete = this;
        G.onGroupRevokeLink = this;

        ActivityShearedMedia.getCountOfSharedMedia(roomId);
    }

    @Override protected void onPause() {
        if (ActivityChat.onComplete != null) {
            if (!txtMemberNumber.getText().toString().equals(participantsCountLabel)) {
                //ActivityChat.onComplete.complete(true, txtMemberNumber.getText().toString(), "");

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        if (realmRoom != null && realmRoom.getGroupRoom() != null) {
                            realmRoom.getGroupRoom().setParticipantsCountLabel(txtMemberNumber.getText().toString());
                        }
                    }
                });
                realm.close();
            }
        }

        LocalBroadcastManager.getInstance(ActivityGroupProfile.this).unregisterReceiver(reciverOnGroupChangeName);

        super.onPause();
    }

    private int memberCount;

    private void setMemberCount(final long roomId, final boolean plus) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null && realmRoom.getGroupRoom() != null) {
                    if (HelperString.isNumeric(realmRoom.getGroupRoom().getParticipantsCountLabel())) {

                        memberCount = Integer.parseInt(realmRoom.getGroupRoom().getParticipantsCountLabel());
                        if (plus) {
                            memberCount++;
                        } else {
                            memberCount--;
                        }
                        realmRoom.getGroupRoom().setParticipantsCountLabel(memberCount + "");
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                txtMemberNumber.setText(memberCount + "");
                                if (HelperCalander.isLanguagePersian) {
                                    txtMemberNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtMemberNumber.getText().toString()));
                                }
                            }
                        });
                    }
                }
            }
        });
        realm.close();
    }

    private BroadcastReceiver reciverOnGroupChangeName = new BroadcastReceiver() {

        @Override public void onReceive(Context context, Intent intent) {

            String name = intent.getExtras().getString("Name");
            String description = intent.getExtras().getString("Description");

            txtGroupName.setText(name);
            txtGroupDescription.setText(description);
            txtGroupNameTitle.setText(name);
        }
    };

    private void initComponent() {

        RippleView rippleBack = (RippleView) findViewById(R.id.agp_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        RippleView rippleMenu = (RippleView) findViewById(R.id.agp_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override public void onComplete(RippleView rippleView) {

                LinearLayout layoutDialog = new LinearLayout(ActivityGroupProfile.this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutDialog.setOrientation(LinearLayout.VERTICAL);
                layoutDialog.setBackgroundColor(getResources().getColor(android.R.color.white));
                //TextView text1 = new TextView(ActivityGroupProfile.this);
                TextView text2 = new TextView(ActivityGroupProfile.this);
                TextView text3 = new TextView(ActivityGroupProfile.this);

                //text1.setTextColor(getResources().getColor(android.R.color.black));
                text2.setTextColor(getResources().getColor(android.R.color.black));
                text3.setTextColor(getResources().getColor(android.R.color.black));

                //text1.setText(getResources().getString(R.string.Search));
                text2.setText(getResources().getString(R.string.clear_history));
                if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {

                    text3.setVisibility(View.VISIBLE);
                    if (isPrivate) {
                        text3.setText(getResources().getString(R.string.group_title_convert_to_public));
                    } else {
                        text3.setText(getResources().getString(R.string.group_title_convert_to_private));
                    }
                } else {
                    text3.setVisibility(View.GONE);
                }

                int dim20 = (int) getResources().getDimension(R.dimen.dp20);
                int dim12 = (int) getResources().getDimension(R.dimen.dp12);
                int sp14_Popup = 14;

                /**
                 * change dpi tp px
                 */
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int widthDpi = Math.round(width / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

                if (widthDpi >= 720) {
                    sp14_Popup = 30;
                } else if (widthDpi >= 600) {
                    sp14_Popup = 22;
                } else {
                    sp14_Popup = 15;
                }

                //text1.setTextSize(16);
                text2.setTextSize(sp14_Popup);
                text3.setTextSize(sp14_Popup);

                //text1.setPadding(dim20, dim12, dim12, dim20);
                text2.setPadding(dim20, dim12, dim12, dim12);
                text3.setPadding(dim20, 0, dim12, dim20);

                text3.setVisibility(View.GONE);
                if (role == GroupChatRole.OWNER) {
                    text3.setVisibility(View.VISIBLE);
                }

                //layoutDialog.addView(text1, params);
                layoutDialog.addView(text2, params);
                layoutDialog.addView(text3, params);

                popupWindow = new PopupWindow(layoutDialog, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.shadow3, ActivityGroupProfile.this.getTheme()));
                } else {
                    popupWindow.setBackgroundDrawable((getResources().getDrawable(R.mipmap.shadow3)));
                }
                if (popupWindow.isOutsideTouchable()) {
                    popupWindow.dismiss();
                }
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override public void onDismiss() {
                    }
                });

                popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
                popupWindow.showAtLocation(layoutDialog, Gravity.RIGHT | Gravity.TOP, (int) getResources().getDimension(R.dimen.dp16), (int) getResources().getDimension(R.dimen.dp32));
                //                popupWindow.showAsDropDown(v);

                //text1.setOnClickListener(new View.OnClickListener() {
                //    @Override public void onClick(View view) {
                //
                //        popupWindow.dismiss();
                //    }
                //});
                text2.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        new MaterialDialog.Builder(ActivityGroupProfile.this).title(R.string.clear_history)
                            .content(R.string.clear_history_content)
                            .positiveText(R.string.B_ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    if (ActivityChat.onComplete != null) {
                                        ActivityChat.onComplete.complete(false, roomId + "", "");
                                    }
                                }
                            })
                            .negativeText(R.string.B_cancel)
                            .show();

                        popupWindow.dismiss();
                    }
                });

                text3.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {

                        isPopup = true;

                        if (isPrivate) {
                            convertToPublic();
                        } else {
                            convertToPrivate();
                        }
                        popupWindow.dismiss();
                    }
                });
            }
        });

        layoutSetting = (LinearLayout) findViewById(R.id.agp_ll_seetting);
        nestedScrollView = (NestedScrollView) findViewById(R.id.group_nestedScroll);
        layoutSetAdmin = (LinearLayout) findViewById(R.id.agp_ll_set_admin);
        viewLineAdmin = (View) findViewById(R.id.agp_ll_line_admin);
        layoutSetModereator = (LinearLayout) findViewById(R.id.agp_ll_set_modereator);
        layoutMemberCanAddMember = (LinearLayout) findViewById(R.id.agp_ll_member_can_add_member);
        layoutNotificatin = (LinearLayout) findViewById(R.id.agp_ll_notification);
        layoutDeleteAndLeftGroup = (LinearLayout) findViewById(R.id.agp_ll_delete_and_left_group);
        prgWait = (ProgressBar) findViewById(R.id.agp_prgWaiting_addContact);
        ltLink = (ViewGroup) findViewById(R.id.agp_ll_link);
        prgWait.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), PorterDuff.Mode.MULTIPLY);
        imvGroupAvatar = (CircleImageView) findViewById(R.id.agp_imv_group_avatar);
        TextView txtDeleteGroup = (TextView) findViewById(R.id.agp_txt_str_delete_and_leave_group);
        txtGroupNameTitle = (TextView) findViewById(R.id.agp_txt_group_name_title);
        txtGroupNameTitle.setText(title);

        txtGroupName = (TextView) findViewById(R.id.agp_txt_group_name);
        txtGroupName.setText(title);

        txtGroupDescription = (TextView) findViewById(R.id.agp_txt_group_description);

        txtGroupDescription.setText(HelperUrl.setUrlLink(description, true, false, null, true));
        txtGroupDescription.setMovementMethod(LinkMovementMethod.getInstance());
        txtNumberOfSharedMedia = (TextView) findViewById(R.id.agp_txt_number_of_shared_media);

        txtMemberNumber = (TextView) findViewById(R.id.agp_txt_member_number);
        appBarLayout = (AppBarLayout) findViewById(R.id.agp_appbar);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.agp_colapsing_toolbar);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));

        LinearLayout llGroupName = (LinearLayout) findViewById(R.id.agp_ll_group_name);
        LinearLayout llGroupDescription = (LinearLayout) findViewById(R.id.agp_ll_group_description);

        /**
         *  visibility layout Description
         */
        if (role == GroupChatRole.OWNER) {
            llGroupDescription.setVisibility(View.VISIBLE);

            llGroupName.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ChangeGroupName();
                }
            });

            llGroupDescription.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
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

        LinearLayout llSharedMedia = (LinearLayout) findViewById(R.id.agp_ll_sheared_media);
        llSharedMedia.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                Intent intent = new Intent(ActivityGroupProfile.this, ActivityShearedMedia.class);
                intent.putExtra("RoomID", roomId);
                startActivity(intent);
            }
        });

        final TextView titleToolbar = (TextView) findViewById(R.id.agp_txt_titleToolbar);
        final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.apg_parentLayoutCircleImage);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

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

        fab = (FloatingActionButton) findViewById(R.id.agp_fab_setPic);

        if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
            fab.setVisibility(View.VISIBLE);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {

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

        TextView txtShowMember = (TextView) findViewById(R.id.agp_txt_show_member);
        txtShowMember.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                FragmentShowMember fragment = FragmentShowMember.newInstance(roomId, role.toString(), userID, "", isNeedgetContactlist);
                getSupportFragmentManager().beginTransaction()
                    .addToBackStack("null")
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragmentContainer_group_profile, fragment, "Show_member")
                    .commit();

                isNeedgetContactlist = false;
            }
        });

        ViewGroup layoutAddMember = (ViewGroup) findViewById(R.id.agp_layout_add_member);
        layoutAddMember.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                addMemberToGroup();
            }
        });

        TextView txtSetAdmin = (TextView) findViewById(R.id.agp_txt_set_admin);
        txtSetAdmin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                showListForCustomRole(ProtoGlobal.GroupRoom.Role.ADMIN.toString());
            }
        });

        TextView txtAddModerator = (TextView) findViewById(R.id.agp_txt_add_modereator);
        txtAddModerator.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                showListForCustomRole(ProtoGlobal.GroupRoom.Role.MODERATOR.toString());
            }
        });

        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.agp_toggle_member_can_add_member);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (toggleButton.isChecked()) {

                } else {

                }
            }
        });

        TextView txtNotification = (TextView) findViewById(R.id.agp_txt_str_notification_and_sound);
        txtNotification.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "GROUP");
                bundle.putLong("ID", roomId);
                fragmentNotification.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(fragmentContainer_group_profile, fragmentNotification)
                    .commit();
            }
        });

        if (role == GroupChatRole.OWNER) {
            txtDeleteGroup.setText(getString(R.string.delete_group));
        } else {
            txtDeleteGroup.setText(getString(R.string.left_group));
        }

        txtDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                groupLeft();
            }
        });

        RippleView rippleCircleImage = (RippleView) findViewById(R.id.agp_ripple_group_avatar);
        rippleCircleImage.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {

                Realm realm = Realm.getDefaultInstance();
                if (realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
                    FragmentShowAvatars.appBarLayout = fab;

                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.group);
                    ActivityGroupProfile.this.getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer_group_profile, fragment, null)
                        .commit();
                }
                realm.close();
            }
        });

        txtMemberNumber.setText(participantsCountLabel);
        if (HelperCalander.isLanguagePersian) {
            txtMemberNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtMemberNumber.getText().toString()));
        }
        txtLinkTitle = (TextView) findViewById(R.id.agp_txt_link_title);
        txtGroupLink = (TextView) findViewById(R.id.agp_txt_link);

        setTextGroupLik();

        ltLink.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

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
            @Override public void complete(boolean result, String messageOne, String MessageTow) {

                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                HelperAvatar.avatarDelete(roomId, mAvatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
                    @Override public void latestAvatarPath(final String avatarPath) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                            }
                        });
                    }

                    @Override public void showInitials(final String initials, final String color) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                imvGroupAvatar.setImageBitmap(
                                    net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
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

        final LinearLayout layoutGroupLink = new LinearLayout(ActivityGroupProfile.this);
        layoutGroupLink.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(ActivityGroupProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputGroupLink = new TextInputLayout(ActivityGroupProfile.this);
        EditText edtLink = new EditText(ActivityGroupProfile.this);
        edtLink.setHint(getResources().getString(R.string.group_link_hint_revoke));
        edtLink.setText(link);
        edtLink.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtLink.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtLink.setPadding(0, 8, 0, 8);
        edtLink.setEnabled(false);
        edtLink.setSingleLine(true);
        inputGroupLink.addView(edtLink);
        inputGroupLink.addView(viewRevoke, viewParams);

        TextView txtLink = new TextView(ActivityGroupProfile.this);
        txtLink.setText("http://iGap.net/");
        txtLink.setTextColor(getResources().getColor(R.color.gray_6c));

        viewRevoke.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtLink.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutGroupLink.addView(inputGroupLink, layoutParams);
        layoutGroupLink.addView(txtLink, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(getResources().getString(R.string.group_link))
            .positiveText(getResources().getString(R.string.array_Copy))
            .customView(layoutGroupLink, true)
            .widgetColor(getResources().getColor(R.color.toolbar_background))
            .negativeText(getResources().getString(R.string.B_cancel))
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    String copy;
                    copy = txtGroupLink.getText().toString();
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                    clipboard.setPrimaryClip(clip);
                }
            })
            .build();

        dialog.show();
    }

    private void dialogRevoke() {

        String link = txtGroupLink.getText().toString();

        final LinearLayout layoutRevoke = new LinearLayout(ActivityGroupProfile.this);
        layoutRevoke.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(ActivityGroupProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputRevoke = new TextInputLayout(ActivityGroupProfile.this);
        EditText edtRevoke = new EditText(ActivityGroupProfile.this);
        edtRevoke.setHint(getResources().getString(R.string.group_link_hint_revoke));
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

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(getResources().getString(R.string.group_link_hint_revoke))
            .positiveText(getResources().getString(R.string.revoke))
            .customView(layoutRevoke, true)
            .widgetColor(getResources().getColor(R.color.toolbar_background))
            .negativeText(getResources().getString(R.string.B_cancel))
            .neutralText(R.string.array_Copy)
            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    String copy;
                    copy = txtGroupLink.getText().toString();
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                    clipboard.setPrimaryClip(clip);
                }
            })
            .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                new RequestGroupRevokeLink().groupRevokeLink(roomId);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void convertToPrivate() {

        G.onGroupRemoveUsername = new OnGroupRemoveUsername() {
            @Override public void onGroupRemoveUsername(final long roomId) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        isPrivate = true;
                        setTextGroupLik();
                        Realm realm = Realm.getDefaultInstance();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                realmRoom.getGroupRoom().setPrivate(true);
                            }
                        });
                        realm.close();
                    }
                });
            }

            @Override public void onError(int majorCode, int minorCode) {

            }
        };

        new MaterialDialog.Builder(ActivityGroupProfile.this).title(getString(R.string.group_title_convert_to_private))
            .content(getString(R.string.group_text_convert_to_private))
            .positiveText(R.string.B_ok)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    new RequestGroupRemoveUsername().groupRemoveUsername(roomId);
                }
            })
            .negativeText(R.string.B_cancel)
            .show();
    }

    private void setTextGroupLik() {

        if (isPrivate) {
            txtGroupLink.setText("" + inviteLink);
            txtLinkTitle.setText(getResources().getString(R.string.group_link));
        } else {
            txtGroupLink.setText("" + linkUsername);
            txtLinkTitle.setText(getResources().getString(R.string.st_username));
        }
    }

    private void convertToPublic() {
        new MaterialDialog.Builder(ActivityGroupProfile.this).title(getString(R.string.group_title_convert_to_public))
            .content(getString(R.string.group_text_convert_to_public))
            .positiveText(R.string.B_ok)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    dialog.dismiss();
                    setUsername();
                }
            })
            .negativeText(R.string.B_cancel)
            .show();
    }

    private void setUsername() {
        final LinearLayout layoutUserName = new LinearLayout(ActivityGroupProfile.this);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(ActivityGroupProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(ActivityGroupProfile.this);
        final EditText edtUserName = new EditText(ActivityGroupProfile.this);
        edtUserName.setHint(getResources().getString(R.string.group_title_set_username));

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

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(getResources().getString(R.string.st_username))
            .positiveText(getResources().getString(R.string.save))
            .customView(layoutUserName, true)
            .widgetColor(getResources().getColor(R.color.toolbar_background))
            .negativeText(getResources().getString(R.string.B_cancel))
            .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        G.onGroupCheckUsername = new OnGroupCheckUsername() {
            @Override public void onGroupCheckUsername(final ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status status) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.AVAILABLE) {

                            positive.setEnabled(true);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("");
                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.INVALID) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + getResources().getString(R.string.INVALID));
                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.TAKEN) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + getResources().getString(R.string.TAKEN));
                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + getResources().getString(R.string.OCCUPYING_LIMIT_EXCEEDED));
                        }
                    }
                });
            }

            @Override public void onError(int majorCode, int minorCode) {

            }
        };

        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override public void afterTextChanged(Editable editable) {

                if (!editable.toString().contains("iGap.net/")) {
                    edtUserName.setText("iGap.net/");
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                }

                if (HelperString.regexCheckUsername(editable.toString().replace("iGap.net/", ""))) {
                    String userName = edtUserName.getText().toString().replace("iGap.net/", "");
                    new RequestGroupCheckUsername().GroupCheckUsername(roomId, userName);
                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError("" + getResources().getString(R.string.INVALID));
                }
            }
        });

        G.onGroupUpdateUsername = new OnGroupUpdateUsername() {
            @Override public void onGroupUpdateUsername(final long roomId, final String username) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {

                        isPrivate = false;
                        dialog.dismiss();

                        linkUsername = username;
                        setTextGroupLik();

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                                realmRoom.getGroupRoom().setUsername(edtUserName.getText().toString());
                                realmRoom.getGroupRoom().setPrivate(false);
                            }
                        });
                        realm.close();
                    }
                });
            }

            @Override public void onError(final int majorCode, int minorCode, final int time) {

                switch (majorCode) {
                    case 368:
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                if (dialog.isShowing()) dialog.dismiss();
                                dialogWaitTime(R.string.GROUP_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                            }
                        });

                        break;
                }
            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                String userName = edtUserName.getText().toString().replace("iGap.net/", "");
                new RequestGroupUpdateUsername().groupUpdateUsername(roomId, userName);
            }
        });

        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View view, boolean b) {
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

    private void setAvatarGroup() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmAvatar> avatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findAll();

        if (avatars.isEmpty()) {
            imvGroupAvatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
            return;
        }
        RealmAvatar realmAvatar = null;
        for (int i = avatars.size() - 1; i >= 0; i--) {
            RealmAvatar avatar = avatars.get(i);
            if (avatar.getFile() != null) {
                realmAvatar = avatar;
                break;
            }
        }

        if (realmAvatar == null) {
            imvGroupAvatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
            return;
        }

        if (realmAvatar.getFile().isFileExistsOnLocal()) {
            G.imageLoader.displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalFilePath()), imvGroupAvatar);
        } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
            G.imageLoader.displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalThumbnailPath()), imvGroupAvatar);
        } else {
            imvGroupAvatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
        }

        realm.close();
    }

    private void initRecycleView() {

        onMenuClick = new OnMenuClick() {
            @Override public void clicked(View view, StructContactInfo info) {
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

    private String filePathAvatar;

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            long avatarId = SUID.id().get();
            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                        filePath = AttachFile.mCurrentPhotoPath;
                        filePathAvatar = filePath;
                    } else {
                        ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                        filePath = AttachFile.imagePath;
                        filePathAvatar = filePath;
                    }
                    break;
                case AttachFile.request_code_image_from_gallery_single_select:
                    if (data.getData() == null) {
                        return;
                    }
                    filePath = AttachFile.getFilePathFromUri(data.getData());
                    filePathAvatar = filePath;

                    break;
            }

            showProgressBar();
            HelperUploadFile.startUploadTaskAvatar(filePath, avatarId, new HelperUploadFile.UpdateListener() {
                @Override public void OnProgress(int progress, FileUploadStructure struct) {
                    if (progress < 100) {
                        prgWait.setProgress(progress);
                    } else {
                        new RequestGroupAvatarAdd().groupAvatarAdd(roomId, struct.token);
                    }
                }

                @Override public void OnError() {
                    hideProgressBar();
                }
            });
        }
    }

    //dialog for choose pic from gallery or camera
    private void startDialogSelectPicture(int r) {

        new MaterialDialog.Builder(this).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                if (which == 0) {
                    try {
                        attachFile.requestOpenGalleryForImageSingleSelect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (which == 1) {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { // camera

                        try {
                            HelperPermision.getStoragePermision(ActivityGroupProfile.this, new OnGetPermission() {
                                @Override public void Allow() throws IOException {
                                    HelperPermision.getCameraPermission(ActivityGroupProfile.this, new OnGetPermission() {
                                        @Override public void Allow() {
                                            dialog.dismiss();
                                            useCamera();
                                        }

                                        @Override public void deny() {

                                        }
                                    });
                                }

                                @Override public void deny() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityGroupProfile.this, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(ActivityGroupProfile.this).dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(ActivityGroupProfile.this).requestTakePicture();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //=============get last avatar

    public RealmList<RealmAvatar> getAvatars() {
        RealmList<RealmAvatar> avatars = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findAllSorted(RealmAvatarFields.ID, Sort.ASCENDING)) {
            avatars.add(avatar);
        }
        realm.close();
        return avatars;
    }

    public RealmAvatar getLastAvatar() {
        RealmList<RealmAvatar> avatars = getAvatars();
        if (avatars.isEmpty()) {
            return null;
        }
        // make sure return last avatar which has attachment
        for (int i = avatars.size() - 1; i >= 0; i--) {
            RealmAvatar avatar = getAvatars().get(i);
            if (avatar.getFile() != null) {
                return avatar;
            }
        }
        return null;
    }

    //=============

    private void addMemberToGroup() {

        List<StructContactInfo> userList = Contacts.retrieve(null);

        RealmRoom realmRoom = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        RealmList<RealmMember> memberList = realmRoom.getGroupRoom().getMembers();

        for (int i = 0; i < memberList.size(); i++) {
            for (int j = 0; j < userList.size(); j++) {
                if (userList.get(j).peerId == memberList.get(i).getPeerId()) {
                    userList.remove(j);
                    break;
                }
            }
        }

        Fragment fragment = ShowCustomList.newInstance(userList, new OnSelectedList() {
            @Override public void getSelectedList(boolean result, String message, int countForShowLastMessage, final ArrayList<StructContactInfo> list) {
                for (int i = 0; i < list.size(); i++) {
                    new RequestGroupAddMember().groupAddMember(roomId, list.get(i).peerId, startMessageId);
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", true);
        bundle.putLong("COUNT_MESSAGE", noLastMessage);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
            .addToBackStack(null)
            .replace(fragmentContainer_group_profile, fragment)
            .commit();
    }

    @Override public void onGroupRevokeLink(long roomId, final String inviteLink, final String inviteToken) {
        hideProgressBar();

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        final RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {

                txtGroupLink.setText("" + inviteLink);
                realmGroupRoom.setInvite_link(inviteLink);
                realmGroupRoom.setInvite_token(inviteToken);
            }
        });

        realm.close();
    }

    @Override public void onError(int majorCode, int minorCode) {
        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override public void run() {
                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.normal_error), Snackbar.LENGTH_LONG);

                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });
    }

    @Override public void onTimeOut() {

        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override public void run() {
                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);

                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });
    }

    private class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(ActivityGroupProfile.this, view, Gravity.TOP);
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

    //***********************************************************************************************************************

    /**
     * add member to realm and send request to server for really added this contacts to this group
     */
    private void memberRealmAndRequest(final ArrayList<StructContactInfo> list, int messageCount) {
        Realm realm = Realm.getDefaultInstance();

        if (messageCount == 0) {
            startMessageId = 0;
        } else {
            RealmResults<RealmRoomMessage> realmRoomMessages =
                realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);

            if (messageCount >= realmRoomMessages.size()) {
                // if count is bigger than exist messages get first message id that exist
                RealmResults<RealmRoomMessage> realmRoomMessageRealmResults =
                    realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                for (final RealmRoomMessage realmRoomMessage : realmRoomMessageRealmResults) {

                    if (realmRoomMessage != null) {
                        startMessageId = realmRoomMessage.getMessageId();
                        break;
                    }
                }
            } else {

                for (final RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                    messageCount--;
                    if (messageCount == 0) {
                        startMessageId = realmRoomMessage.getMessageId();
                    }
                }
            }
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                final RealmList<RealmMember> members = new RealmList<>();
                for (int i = 0; i < list.size(); i++) {
                    long peerId = list.get(i).peerId;
                    //add member to realm
                    RealmMember realmMember = new RealmMember();

                    realmMember.setId(SUID.id().get());
                    realmMember.setPeerId(peerId);
                    realmMember.setRole(ProtoGlobal.GroupRoom.Role.MEMBER.toString());
                    realmMember = realm.copyToRealm(realmMember);

                    members.add(realmMember);

                    //request for add member
                    new RequestGroupAddMember().groupAddMember(roomId, peerId, startMessageId);
                }

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                RealmList<RealmMember> memberList = realmRoom.getGroupRoom().getMembers();

                for (int i = 0; i < members.size(); i++) {
                    long id = members.get(i).getPeerId();
                    boolean canAdd = true;
                    for (int j = 0; j < memberList.size(); j++) {
                        if (memberList.get(j).getPeerId() == id) {
                            canAdd = false;
                            break;
                        }
                    }
                    if (canAdd) {
                        memberList.add(members.get(i));
                    }
                }
            }
        });

        realm.close();
    }

    //***********************************************************************************************************************

    private void ChangeGroupName() {

        final LinearLayout layoutUserName = new LinearLayout(ActivityGroupProfile.this);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(ActivityGroupProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(ActivityGroupProfile.this);
        final EditText edtUserName = new EditText(ActivityGroupProfile.this);
        edtUserName.setHint(getResources().getString(R.string.st_username));
        edtUserName.setText(txtGroupNameTitle.getText().toString());
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

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(getResources().getString(R.string.group_name))
            .positiveText(getResources().getString(R.string.save))
            .customView(layoutUserName, true)
            .widgetColor(getResources().getColor(R.color.toolbar_background))
            .negativeText(getResources().getString(R.string.B_cancel))
            .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        final String finalUserName = txtGroupNameTitle.getText().toString();
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override public void afterTextChanged(Editable editable) {
                if (!edtUserName.getText().toString().equals(finalUserName)) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }
        });

        G.onGroupEdit = new OnGroupEdit() {
            @Override public void onGroupEdit(long roomId, String name, String description) {
                hideProgressBar();
                txtGroupNameTitle.setText(name);
            }

            @Override public void onError(int majorCode, int minorCode) {

            }

            @Override public void onTimeOut() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);

                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                            @Override public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                new RequestGroupEdit().groupEdit(roomId, edtUserName.getText().toString(), txtGroupDescription.getText().toString());
                dialog.dismiss();
            }
        });

        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });
        dialog.show();
    }

    private void ChangeGroupDescription() {
        MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(R.string.group_description)
            .positiveText(getString(R.string.save))
            .alwaysCallInputCallback()
            .widgetColor(getResources().getColor(R.color.toolbar_background))
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    G.onGroupEdit = new OnGroupEdit() {
                        @Override public void onGroupEdit(final long roomId, final String name, final String descriptions) {

                            runOnUiThread(new Runnable() {
                                @Override public void run() {

                                    description = descriptions;
                                    txtGroupDescription.setText(descriptions);
                                }
                            });
                        }

                        @Override public void onError(int majorCode, int minorCode) {

                        }

                        @Override public void onTimeOut() {

                        }
                    };

                    new RequestGroupEdit().groupEdit(roomId, txtGroupName.getText().toString(), tmp);
                }
            })
            .negativeText(getString(R.string.cancel))
            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
            .input(getString(R.string.please_enter_group_description), txtGroupDescription.getText().toString(), new MaterialDialog.InputCallback() {
                @Override public void onInput(MaterialDialog dialog, CharSequence input) {
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
            })
            .show();
    }

    private void groupLeft() {

        String text = "";
        if (role == GroupChatRole.OWNER) {
            text = getString(R.string.do_you_want_to_delete_this_group);
        } else {
            text = getString(R.string.do_you_want_to_leave_this_group);
        }

        new MaterialDialog.Builder(ActivityGroupProfile.this).content(text).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                G.onGroupLeft = new OnGroupLeft() {
                    @Override public void onGroupLeft(final long roomId, long memberId) {

                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                ActivityGroupProfile.this.finish();
                                if (ActivityChat.activityChat != null) {
                                    ActivityChat.activityChat.finish();
                                }
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override public void onError(int majorCode, int minorCode) {

                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override public void onTimeOut() {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }
                };

                G.onGroupDelete = new OnGroupDelete() {
                    @Override public void onGroupDelete(final long roomId) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                ActivityGroupProfile.this.finish();
                                if (ActivityChat.activityChat != null) {
                                    ActivityChat.activityChat.finish();
                                }

                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override public void Error(int majorCode, int minorCode) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Just owner can delete", Snackbar.LENGTH_LONG);
                                snack.setAction("CANCEL", new View.OnClickListener() {
                                    @Override public void onClick(View view) {
                                        snack.dismiss();
                                    }
                                });
                                snack.show();
                            }
                        });
                    }

                    @Override public void onTimeOut() {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).show();
    }

    /**
     * if user was admin set  role to member
     */
    public void kickAdmin(final long memberID) {

        new MaterialDialog.Builder(ActivityGroupProfile.this).content(R.string.do_you_want_to_set_admin_role_to_member)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    new RequestGroupKickAdmin().groupKickAdmin(roomId, memberID);
                }
            })
            .show();
    }

    /**
     * delete this member from list of member group
     */
    public void kickMember(final long memberID) {

        new MaterialDialog.Builder(ActivityGroupProfile.this).content(R.string.do_you_want_to_kick_this_member)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestGroupKickMember().groupKickMember(roomId, memberID);
                }
            })
            .show();
    }

    private void onGroupAddMemberCallback() {
        G.onGroupAddMember = new OnGroupAddMember() {
            @Override public void onGroupAddMember(final Long roomIdUser, final Long UserId) {

                setMemberCount(roomIdUser, true);

                Realm realm = Realm.getDefaultInstance();
                RealmRegisteredInfo realmRegistered = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, UserId).findFirst();

                if (realmRegistered == null) {
                    if (roomIdUser == roomId) {
                        new RequestUserInfo().userInfo(UserId, roomId + "");
                    }
                }

                realm.close();
            }

            @Override public void onError(int majorCode, int minorCode) {

            }
        };
    }

    private void onGroupKickMemberCallback() {
        G.onGroupKickMember = new OnGroupKickMember() {
            @Override public void onGroupKickMember(final long roomId, final long memberId) {
                Log.i("WWW", "Kick Member memberId : " + memberId);
                setMemberCount(roomId, false);
            }

            @Override public void onError(int majorCode, int minorCode) {

            }

            @Override public void onTimeOut() {

            }
        };
    }

    public void kickModerator(final long memberID) {

        new MaterialDialog.Builder(ActivityGroupProfile.this).content(R.string.do_you_want_to_set_modereator_role_to_member)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestGroupKickModerator().groupKickModerator(roomId, memberID);
                }
            })
            .show();
    }

    private void showListForCustomRole(String SelectedRole) {
        FragmentShowMember fragment = FragmentShowMember.newInstance(roomId, role.toString(), userID, SelectedRole, isNeedgetContactlist);
        getSupportFragmentManager().beginTransaction()
            .addToBackStack("null")
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.fragmentContainer_group_profile, fragment, "Show_member")
            .commit();

        isNeedgetContactlist = false;
    }

    private void showAvatar() {
        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
            @Override public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                    }
                });
            }

            @Override public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        imvGroupAvatar.setImageBitmap(
                            net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    //***Add Avatar

    @Override public void onAvatarAdd(final long roomId, final ProtoGlobal.Avatar avatar) {
        hideProgressBar();
        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (filePathAvatar == null) {
            showAvatar();
        } else {
            HelperAvatar.avatarAdd(roomId, filePathAvatar, avatar, new OnAvatarAdd() {
                @Override public void onAvatarAdd(final String avatarPath) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                        }
                    });
                }
            });
            filePathAvatar = null;
        }
    }

    @Override public void onAvatarAddError() {
        hideProgressBar();
    }

    //***Delete Avatar

    @Override public void onDeleteAvatar(long roomId, long avatarId) {
        hideProgressBar();
        HelperAvatar.avatarDelete(roomId, avatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
            @Override public void latestAvatarPath(final String avatarPath) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                    }
                });
            }

            @Override public void showInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        imvGroupAvatar.setImageBitmap(
                            net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    @Override public void onDeleteAvatarError(int majorCode, int minorCode) {

    }
    //***Show And Hide Progress

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if (prgWait != null) {
                    prgWait.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                prgWait.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(title)
            .customView(R.layout.dialog_remind_time, wrapInScrollView)
            .positiveText(R.string.B_ok)
            .autoDismiss(false)
            .canceledOnTouchOutside(false)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            })
            .show();

        View v = dialog.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }
}
