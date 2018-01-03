package net.iGap.viewmodel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import java.util.ArrayList;
import java.util.List;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentGroupProfile;
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
import net.iGap.interfaces.OnGroupAddMember;
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
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.Contacts;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.MEditText;
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
import net.iGap.request.RequestGroupCheckUsername;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupEdit;
import net.iGap.request.RequestGroupLeft;
import net.iGap.request.RequestGroupRemoveUsername;
import net.iGap.request.RequestGroupRevokeLink;
import net.iGap.request.RequestGroupUpdateUsername;
import net.iGap.request.RequestUserInfo;

import static android.content.Context.CLIPBOARD_SERVICE;
import static net.iGap.G.context;

/**
 * Created by amir on 15/12/2017.
 */

public class FragmentGroupProfileViewModel implements OnGroupRevokeLink {

    NestedScrollView nestedScrollView;
    AttachFile attachFile;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private String tmp = "";

    public long roomId;
    private String title;
    private String description;
    private String initials;
    private String inviteLink;
    private String linkUsername;
    private String color;
    public GroupChatRole role;
    private long noLastMessage;
    private String participantsCountLabel;
    private String pathSaveImage;

    public static OnMenuClick onMenuClick;

    public boolean isPrivate;
    private boolean isPopup = false;

    private long startMessageId = 0;


    private boolean isNeedgetContactlist = true;

    private RealmChangeListener<RealmModel> changeListener;
    private RealmRoom mRoom;
    private Realm realmGroupProfile;
    private static final String ROOM_ID = "RoomId";
    private Fragment fragment;
    public static final String FRAGMENT_TAG = "FragmentGroupProfile";
    private static final String IS_NOT_JOIN = "is_not_join";
    private boolean isNotJoin = false;



    public FragmentGroupProfileViewModel(FragmentGroupProfile fragmentGroupProfile, Bundle arguments) {
        this.fragment = fragmentGroupProfile;
        getInfo(arguments);
    }

    public ObservableField<String> callbackGroupName = new ObservableField<>("");
    public ObservableField<String> callbackMemberNumber = new ObservableField<>("");
    public ObservableField<String> callbackGroupLink = new ObservableField<>("");
    public ObservableField<String> callbackGroupDescription = new ObservableField<>("");
    public ObservableField<String> callbackGroupShearedMedia = new ObservableField<>("");
    public ObservableField<String> callBackDeleteLeaveGroup = new ObservableField<>(G.context.getResources().getString(R.string.Delete_and_leave_Group));
    public ObservableField<String> callbackGroupLinkTitle = new ObservableField<>(G.context.getResources().getString(R.string.group_link));
    public ObservableField<Integer> callbackAddMemberVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> prgWaitingVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> settingVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> groupDescriptionVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> groupSetAdminVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> lineAdminVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> setModereatorVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> layoutMemberCanAddMember = new ObservableField<>(View.GONE);


    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================


    public void onClickRippleBack(View v) {
        if (FragmentGroupProfile.onBackFragment != null) FragmentGroupProfile.onBackFragment.onBack();
    }


    public void onClickRippleMenu(View view) {

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

        if (role == GroupChatRole.MODERATOR) {
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
                    convertToPublic(v);
                } else {
                    convertToPrivate();
                }
                dialog.dismiss();
            }
        });

    }

    public void onClickRippleGroupAvatar(View v) {
        if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
            FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.group);
            fragment.appBarLayout = fab;
            //new HelperFragment(fragment).setResourceContainer(R.id.fragmentContainer_group_profile).load();
            new HelperFragment(fragment).setReplace(false).load();
        }
    }

    public void onClickGroupName(View v) {
        ChangeGroupName(v);
    }

    public void onClickGroupLink(View v) {
        isPopup = false;

        if (role == GroupChatRole.OWNER) {
            if (isPrivate) {
                dialogRevoke();
            } else {
                setUsername(v);
            }
        } else {
            dialogCopyLink();
        }
    }

    public void onClickGroupDescription(View v) {
        ChangeGroupDescription();
    }

    public void onClickGroupShearedMedia(View v) {

        new HelperFragment(FragmentShearedMedia.newInstance(roomId)).setReplace(false).load();

    }

    public void onClickGroupAddMember(View v) {
        addMemberToGroup();
    }

    public void onClickGroupShowMember(View v) {
        showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString());

    }

    public void onClickGroupSetAdmin(View v) {
        showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
    }

    public void onClickGroupModereator(View v) {
        showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());
    }

    public void onClickGroupNotification(View v) {
        FragmentNotification fragmentNotification = new FragmentNotification();
        Bundle bundle = new Bundle();
        bundle.putString("PAGE", "GROUP");
        bundle.putLong("ID", roomId);
        fragmentNotification.setArguments(bundle);
        new HelperFragment(fragmentNotification).setReplace(false).load();
    }

    public void onClickGroupLeftGroup(View v) {
        groupLeft();
    }


    //===============================================================================
    //================================Method========================================
    //===============================================================================


    private void getInfo(Bundle arguments) {

        G.onGroupRevokeLink = this;

        realmGroupProfile = Realm.getDefaultInstance();

        roomId = arguments.getLong(ROOM_ID);
        isNotJoin = arguments.getBoolean(IS_NOT_JOIN);

        //group info
        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getGroupRoom() == null) {
            if (FragmentGroupProfile.onBackFragment != null) FragmentGroupProfile.onBackFragment.onBack();
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

        callbackGroupName.set(title);
        SpannableStringBuilder ds = HelperUrl.setUrlLink(description, true, false, null, true);
        if (ds != null) callbackGroupDescription.set(ds.toString());

        callbackAddMemberVisibility.set(View.VISIBLE);
        if (role == GroupChatRole.MODERATOR || role == GroupChatRole.MEMBER) {
            if (!isPrivate) {
                callbackAddMemberVisibility.set(View.GONE);
            }
        }
        try {
            if (realmRoom.getLastMessage() != null) {
                noLastMessage = realmRoom.getLastMessage().getMessageId();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }

        FragmentShearedMedia.getCountOfSharedMedia(roomId);

        if (isNotJoin) {
            settingVisibility.set(View.GONE);
        }

        /**
         *  visibility layout Description
         */

        if (role == GroupChatRole.OWNER) {
            groupDescriptionVisibility.set(View.VISIBLE);

        } else {
            if (description.length() == 0) {
                groupDescriptionVisibility.set(View.GONE);
            }
        }
        if (role != GroupChatRole.OWNER) {
            if (description.equals("")) {
                groupDescriptionVisibility.set(View.GONE);
            }
        }

        if (role == GroupChatRole.OWNER) {
            callBackDeleteLeaveGroup.set(G.fragmentActivity.getResources().getString(R.string.delete_group));
        } else {
            callBackDeleteLeaveGroup.set(G.fragmentActivity.getResources().getString(R.string.left_group));
        }


        callbackMemberNumber.set(participantsCountLabel);
        if (HelperCalander.isPersianUnicode) {
            callbackMemberNumber.set(HelperCalander.convertToUnicodeFarsiNumber(callbackMemberNumber.get()));
        }

        setTextGroupLik();
        setUiIndependentRole();
        initRecycleView();
        onGroupAddMemberCallback();
        onGroupKickMemberCallback();
    }

    private Realm getRealm() {
        if (realmGroupProfile == null || realmGroupProfile.isClosed()) {
            realmGroupProfile = Realm.getDefaultInstance();
        }
        return realmGroupProfile;
    }

    private void convertToPublic(final View view) {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.group_title_convert_to_public)).content(G.fragmentActivity.getResources().getString(R.string.group_text_convert_to_public)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
                setUsername(view);
            }
        }).negativeText(R.string.no).show();
    }

    private void convertToPrivate() {

        G.onGroupRemoveUsername = new OnGroupRemoveUsername() {
            @Override
            public void onGroupRemoveUsername(final long roomId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isPrivate = true;
                        if (inviteLink == null || inviteLink.isEmpty() || inviteLink.equals("https://")) {
                            new RequestGroupRevokeLink().groupRevokeLink(roomId);
                        } else {
                            setTextGroupLik();
                        }
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
            callbackGroupLink.set("" + inviteLink);
            callbackGroupLinkTitle.set(G.fragmentActivity.getResources().getString(R.string.group_link));
        } else {
            callbackGroupLink.set("" + linkUsername);
            callbackGroupLinkTitle.set(G.fragmentActivity.getResources().getString(R.string.st_username));
        }
    }

    private void setUsername(final View v) {
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

                String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
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
                                        callbackGroupShearedMedia.set(HelperCalander.convertToUnicodeFarsiNumber(countText));
                                    } else {
                                        callbackGroupShearedMedia.set(countText);
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

            callbackGroupShearedMedia.set(context.getString(R.string.there_is_no_sheared_media));
        }
    }

    public void onStop() {
        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
    }

    public void onDestroy() {
        if (realmGroupProfile != null && !realmGroupProfile.isClosed()) {
            realmGroupProfile.close();
        }
    }

    private void showListForCustomRole(String SelectedRole) {
        FragmentShowMember fragment = FragmentShowMember.newInstance1(this.fragment, roomId, role.toString(), G.userId, SelectedRole, isNeedgetContactlist);
        new HelperFragment(fragment).setReplace(false).load();
        isNeedgetContactlist = false;
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
                                prgWaitingVisibility.set(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWaitingVisibility.set(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onTimeOut() {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWaitingVisibility.set(View.GONE);
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
                                prgWaitingVisibility.set(View.GONE);
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
                                prgWaitingVisibility.set(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onTimeOut() {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWaitingVisibility.set(View.GONE);
                            }
                        });
                    }
                };

                if (role == GroupChatRole.OWNER) {
                    new RequestGroupDelete().groupDelete(roomId);
                } else {
                    new RequestGroupLeft().groupLeft(roomId);
                }
                prgWaitingVisibility.set(View.VISIBLE);
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).show();
    }

    private void dialogRevoke() {

        String link = callbackGroupLink.get();

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
                    copy = callbackGroupLink.get();
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

    private void dialogCopyLink() {

        String link = callbackGroupLink.get();

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
        txtLink.setText(Config.IGAP_LINK_PREFIX);
        txtLink.setTextColor(G.context.getResources().getColor(R.color.gray_6c));

        viewRevoke.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtLink.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutGroupLink.addView(inputGroupLink, layoutParams);
        layoutGroupLink.addView(txtLink, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.group_link))
            .positiveText(G.fragmentActivity.getResources().getString(R.string.array_Copy))
            .customView(layoutGroupLink, true)
            .widgetColor(G.context.getResources().getColor(R.color.toolbar_background))
            .negativeText(G.fragmentActivity.getResources().getString(R.string.no))
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    String copy;
                    copy = callbackGroupLink.get();
                    ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                    clipboard.setPrimaryClip(clip);
                }
            })
            .build();

        dialog.show();
    }

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

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                prgWaitingVisibility.set(View.GONE);
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

    @Override
    public void onGroupRevokeLink(final long roomId, final String inviteLink, final String inviteToken) {
        hideProgressBar();

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                callbackGroupLink.set("" + inviteLink);
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
                            ((FragmentGroupProfile) fragment).kickAdmin(info.peerId);
                            return true;
                        case R.id.menu_remove_moderator:
                            ((FragmentGroupProfile) fragment).kickModerator(info.peerId);
                            return true;
                        case R.id.menu_kick:
                            ((FragmentGroupProfile) fragment).kickMember(info.peerId);
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

    private int memberCount;

    private void setMemberCount(final long roomId, final boolean plus) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                memberCount = RealmRoom.updateMemberCount(realm, roomId, plus);
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbackMemberNumber.set(memberCount + "");
                        if (HelperCalander.isPersianUnicode) {
                            callbackMemberNumber.set(HelperCalander.convertToUnicodeFarsiNumber(callbackMemberNumber.get()));
                        }
                    }
                });
            }
        });

    }

    private void setUiIndependentRole() {

        if (role == GroupChatRole.MEMBER) {

            groupSetAdminVisibility.set(View.GONE);
            lineAdminVisibility.set(View.GONE);
            setModereatorVisibility.set(View.GONE);
            layoutMemberCanAddMember.set(View.GONE);
        } else if (role == GroupChatRole.MODERATOR) {
            groupSetAdminVisibility.set(View.GONE);
            lineAdminVisibility.set(View.GONE);
            setModereatorVisibility.set(View.GONE);
        } else if (role == GroupChatRole.ADMIN) {
            groupSetAdminVisibility.set(View.GONE);
            lineAdminVisibility.set(View.GONE);
        } else if (role == GroupChatRole.OWNER) {

        }
    }

    private void initRecycleView() {

        onMenuClick = new OnMenuClick() {
            @Override
            public void clicked(View view, StructContactInfo info) {
                new CreatePopUpMessage().show(view, info);
            }
        };
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
        edtUserName.setText(callbackGroupName.get());
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
            new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.group_name)).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        final String finalUserName = callbackGroupName.get();
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
                callbackGroupDescription.set(description);
                callbackGroupName.set(name);
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

                new RequestGroupEdit().groupEdit(roomId, edtUserName.getText().toString(), callbackGroupDescription.get());
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

                                callbackGroupDescription.set(descriptions);
                                callbackGroupName.set(name);
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

                new RequestGroupEdit().groupEdit(roomId, callbackGroupName.get(), tmp);
            }
        }).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT).input(G.fragmentActivity.getResources().getString(R.string.please_enter_group_description), callbackGroupDescription.get(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                // Do something

                View positive = dialog.getActionButton(DialogAction.POSITIVE);
                tmp = input.toString();
                if (!input.toString().equals(callbackGroupDescription.get())) {

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


}
