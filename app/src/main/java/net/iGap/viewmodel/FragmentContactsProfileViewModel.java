package net.iGap.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentContactsProfileBinding;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentShearedMedia;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnUserContactEdit;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserUpdateStatus;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestSignalingGetConfiguration;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;

import static net.iGap.G.context;

public class FragmentContactsProfileViewModel implements OnUserContactEdit, OnUserUpdateStatus, OnUserInfoResponse {

    public long shearedId = -2;
    public String firstName;
    public String lastName;
    public boolean isBlockUser = false;
    public boolean disableDeleteContact = true;
    public ObservableInt callVisibility = new ObservableInt(View.GONE);
    public ObservableInt toolbarVisibility = new ObservableInt(View.GONE);
    public ObservableInt bioVisibility = new ObservableInt(View.VISIBLE);
    public ObservableBoolean showNumber = new ObservableBoolean(true);
    public ObservableField<String> username = new ObservableField<>();
    public ObservableField<String> contactName = new ObservableField<>();
    public ObservableField<String> lastSeen = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>("0");
    public ObservableField<String> bio = new ObservableField<>();
    public ObservableField<String> sharedMedia = new ObservableField<>();
    private Realm realm;
    private RealmRoom mRoom;
    private RealmRegisteredInfo registeredInfo;
    private RealmList<RealmAvatar> avatarList;
    private RealmChangeListener<RealmModel> changeListener;
    private FragmentContactsProfileBinding fragmentContactsProfileBinding;
    private long userId;
    private long roomId;
    private long lastSeenValue;
    private String enterFrom;
    private String initials;
    private String color;
    private String userStatus;
    private String avatarPath;

    public FragmentContactsProfileViewModel(FragmentContactsProfileBinding fragmentContactsProfileBinding, long roomId, long userId, String enterFrom) {
        this.fragmentContactsProfileBinding = fragmentContactsProfileBinding;
        this.roomId = roomId;
        this.userId = userId;
        this.enterFrom = enterFrom;

        mainStart();
        startInitCallbacks();
    }

    //===============================================================================
    //=====================================Starts====================================
    //===============================================================================

    private void mainStart() {
        if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString()) || roomId == 0) {
            RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();
            if (realmRoom != null) {
                shearedId = realmRoom.getId();
            }
        } else {
            shearedId = roomId;
        }

        registeredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealm(), userId);

        if (registeredInfo != null) {
            isBlockUser = registeredInfo.isBlockUser();
            registeredInfo.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel element) {
                    isBlockUser = registeredInfo.isBlockUser();
                }
            });
        }


        if (registeredInfo != null) {
            if (registeredInfo.getLastAvatar() != null) {

                String mainFilePath = registeredInfo.getLastAvatar().getFile().getLocalFilePath();

                if (mainFilePath != null && new File(mainFilePath).exists()) { // if main image is exist showing that
                    avatarPath = mainFilePath;
                } else {
                    avatarPath = registeredInfo.getLastAvatar().getFile().getLocalThumbnailPath();
                }

                avatarList = registeredInfo.getAvatars();
            }
        }

        RealmContacts realmUser = getRealm().where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();

        if (registeredInfo != null) {
            if (registeredInfo.getDisplayName() != null && !registeredInfo.getDisplayName().equals("")) {
                contactName.set(registeredInfo.getDisplayName());
            } else {
                contactName.set(G.fragmentActivity.getResources().getString(R.string.nick_name_not_exist));
            }

            if (registeredInfo.getBio() == null || registeredInfo.getBio().length() == 0) {
                bioVisibility.set(View.GONE);
            } else {
                bio.set(registeredInfo.getBio());
            }
            username.set(registeredInfo.getUsername());
            phone.set(registeredInfo.getPhoneNumber());

            firstName = registeredInfo.getFirstName();
            lastName = registeredInfo.getLastName();
            lastSeenValue = registeredInfo.getLastSeen();
            color = registeredInfo.getColor();
            initials = registeredInfo.getInitials();
            userStatus = registeredInfo.getStatus();

        } else if (realmUser != null) {
            if (realmUser.getDisplay_name() != null && !realmUser.getDisplay_name().equals("")) {
                contactName.set(realmUser.getDisplay_name());
            } else {
                contactName.set(G.fragmentActivity.getResources().getString(R.string.nick_name_not_exist));
            }
            username.set(realmUser.getUsername());
            phone.set(Long.toString(realmUser.getPhone()));

            firstName = realmUser.getFirst_name();
            lastName = realmUser.getLast_name();
            lastSeenValue = realmUser.getLast_seen();
            color = realmUser.getColor();
            initials = realmUser.getInitials();
        }

        if (userId != 134 && G.userId != userId) {
            RealmCallConfig callConfig = getRealm().where(RealmCallConfig.class).findFirst();
            if (callConfig != null) {
                if (callConfig.isVoice_calling()) {
                    callVisibility.set(View.VISIBLE);
                } else {
                    callVisibility.set(View.GONE);
                }
            } else {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        }
        RealmContacts realmContacts = getRealm().where(RealmContacts.class).equalTo(RealmContactsFields.PHONE, Long.parseLong(phone.get())).findFirst();

        /**
         * if this user isn't in my contacts don't show phone number
         */
        if (realmContacts == null && enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {
            showNumber.set(false);
            disableDeleteContact = true;
        }

        setUserStatus(userStatus, lastSeenValue);
        setAvatar();
        FragmentShearedMedia.getCountOfSharedMedia(shearedId);
    }

    private void startInitCallbacks() {
        G.onUserUpdateStatus = this;
        G.onUserContactEdit = this;
        G.onUserInfoResponse = this;
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================

    public void onCallClick(View view) {
        FragmentCall.call(userId, false);
    }

    public void onImageClick(View view) {
        if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst() != null) {
            FragmentShowAvatars fragment;
            if (userId == G.userId) {
                fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
            } else {
                fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.chat);
            }

            //fragment.appBarLayout = fab; //TODO- check
            new HelperFragment(fragment).setReplace(false).load();
        }
    }

    //===============================================================================
    //===================================Callbacks===================================
    //===============================================================================

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
        if (userId == user.getId()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (user.getDisplayName() != null && !user.getDisplayName().equals("")) {
                        contactName.set(user.getDisplayName());
                    }

                    setAvatar();
                }
            });
        }
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }

    @Override
    public void onContactEdit(final String firstName, final String lastName, String initials) {
        setAvatar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                contactName.set(firstName + " " + lastName);
            }
        });
    }

    @Override
    public void onContactEditTimeOut() {

    }

    @Override
    public void onContactEditError(int majorCode, int minorCode) {

    }

    @Override
    public void onUserUpdateStatus(long userId, final long time, final String status) {
        if (this.userId == userId) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    setUserStatus(AppUtils.getStatsForUser(status), time);
                }
            });
        }
    }

    //===============================================================================
    //====================================Methods====================================
    //===============================================================================

    private void setUserStatus(String userStatus, long time) {
        this.userStatus = userStatus;
        this.lastSeenValue = time;

        if (userStatus != null) {
            if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                String status = LastSeenTimeUtil.computeTime(userId, time, false);
                lastSeen.set(status);
            } else {
                lastSeen.set(userStatus);
            }
        }
    }

    private void setAvatar() {
        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), fragmentContactsProfileBinding.chiImgCircleImage);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fragmentContactsProfileBinding.chiImgCircleImage.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) fragmentContactsProfileBinding.chiImgCircleImage.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });
    }

    private Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    public void onResume() {
        mRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, shearedId).findFirst();
        if (mRoom != null) {
            if (changeListener == null) {
                changeListener = new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(final RealmModel element) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!((RealmRoom) element).isValid()) {
                                    return;
                                }

                                sharedMedia.set(((RealmRoom) element).getSharedMediaCount());
                            }
                        });
                    }
                };
            }
            mRoom.addChangeListener(changeListener);
            changeListener.onChange(mRoom);
        } else {
            sharedMedia.set(context.getString(R.string.there_is_no_sheared_media));
        }
    }

    public void onPause() {
        if (G.onUpdateUserStatusInChangePage != null) {
            G.onUpdateUserStatusInChangePage.updateStatus(userId, userStatus, lastSeenValue);
        }
    }

    public void onStop() {
        if (registeredInfo != null) {
            registeredInfo.removeAllChangeListeners();
        }

        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
    }

    public void onDestroy() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

}
