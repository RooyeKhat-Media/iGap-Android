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

import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.view.View;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import java.util.ArrayList;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentActiveSessions;
import net.iGap.fragments.FragmentBlockedUser;
import net.iGap.fragments.FragmentPassCode;
import net.iGap.fragments.FragmentSecurity;
import net.iGap.helper.HelperFragment;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.structs.StructSessions;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmPrivacy;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileSetSelfRemove;

import static android.content.Context.MODE_PRIVATE;

public class FragmentPrivacyAndSecurityViewModel {

    private Realm realm;
    private RealmUserInfo realmUserInfo;
    private RealmPrivacy realmPrivacy;
    private RealmChangeListener<RealmModel> userInfoListener;
    private RealmChangeListener<RealmModel> privacyListener;
    private int poWhoCan;
    private int poSeeMyAvatar;
    private int poInviteChannel;
    private int poInviteGroup;
    private int poSeeLastSeen;
    private int poVoiceCall;

    private final int SEE_MY_AVATAR = 0;
    private final int INVITE_CHANNEL = 1;
    private final int INVITE_GROUP = 2;
    private final int LAST_SEEN = 3;
    private final int VOICE_CALL = 4;

    int poSelfRemove;
    private SharedPreferences sharedPreferences;
    private int poRbDialogSelfDestruction = 0;
    private int selfRemove;
    private ArrayList<StructSessions> itemSessionsgetActivelist = new ArrayList<StructSessions>();

    public ObservableField<String> callbackSeeMyAvatar = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackInviteChannel = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackInviteGroup = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackVoiceCall = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackSeeLastSeen = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));
    public ObservableField<String> callbackSelfDestruction = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.everybody));

    public FragmentPrivacyAndSecurityViewModel() {

        realm = Realm.getDefaultInstance();
        getInfo();

    }

    public void onClickBlocked(View view) {
        new HelperFragment(new FragmentBlockedUser()).setReplace(false).load();
    }

    public void onClickSeeMyAvatar(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.AVATAR, poSeeMyAvatar, R.string.title_who_can_see_my_avatar, SEE_MY_AVATAR);
    }

    public void onClickInviteChannel(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.CHANNEL_INVITE, poInviteChannel, R.string.title_who_can_invite_you_to_channel_s, INVITE_CHANNEL);
    }

    public void onClickInviteGroup(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.GROUP_INVITE, poInviteGroup, R.string.title_who_can_invite_you_to_group_s, INVITE_GROUP);
    }

    public void onClickVoiceCall(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.VOICE_CALLING, poVoiceCall, R.string.title_who_is_allowed_to_call, VOICE_CALL);
    }

    public void onClickSeeLastSeen(View view) {
        openDialogWhoCan(ProtoGlobal.PrivacyType.USER_STATUS, poSeeLastSeen, R.string.title_Last_Seen, LAST_SEEN);
    }

    public void onClickPassCode(View view) {
        new HelperFragment(new FragmentPassCode()).setReplace(false).load();
    }

    public void onClickTwoStepVerification(View view) {
        new HelperFragment(new FragmentSecurity()).setReplace(false).load();
    }

    public void onClickActivitySessions(View view) {
        new HelperFragment(new FragmentActiveSessions()).setReplace(false).load();
    }

    public void onClickSelfDestruction(View view) {
        selfDestructs();
    }
    //===============================================================================
    //====================================Methods====================================
    //===============================================================================


    private void getInfo() {
        realmPrivacy = realm.where(RealmPrivacy.class).findFirst();
        realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        RealmPrivacy.getUpdatePrivacyFromServer();
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        poSelfRemove = sharedPreferences.getInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 2);

        updatePrivacyUI(realmPrivacy);

        userInfoListener = new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {

                if (((RealmUserInfo) element).isValid()) {
                    selfRemove = ((RealmUserInfo) element).getSelfRemove();
                    setTextSelfDestructs();
                }
            }
        };

        privacyListener = new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                updatePrivacyUI((RealmPrivacy) element);
            }
        };
    }

    private void openDialogWhoCan(final ProtoGlobal.PrivacyType privacyType, final int position, int title, final int type) {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(title)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.privacy_setting_array).itemsCallbackSingleChoice(position, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {
                        RealmPrivacy.sendUpdatePrivacyToServer(privacyType, ProtoGlobal.PrivacyLevel.ALLOW_ALL);
                        break;
                    }
                    case 1: {
                        RealmPrivacy.sendUpdatePrivacyToServer(privacyType, ProtoGlobal.PrivacyLevel.ALLOW_CONTACTS);
                        break;
                    }
                    case 2: {
                        RealmPrivacy.sendUpdatePrivacyToServer(privacyType, ProtoGlobal.PrivacyLevel.DENY_ALL);
                        break;
                    }
                }
                positionDialog(type, which);
                return false;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
    }

    private String getStringFromEnumString(String str, int type) {

        if (str == null || str.length() == 0) {
            poWhoCan = 0;
            return G.fragmentActivity.getResources().getString(R.string.everybody);
        }

        int resString = 0;

        if (str.equals(ProtoGlobal.PrivacyLevel.ALLOW_ALL.toString())) {
            poWhoCan = 0;
            resString = R.string.everybody;
        } else if (str.equals(ProtoGlobal.PrivacyLevel.ALLOW_CONTACTS.toString())) {
            poWhoCan = 1;
            resString = R.string.my_contacts;
        } else {
            poWhoCan = 2;
            resString = R.string.no_body;
        }

        positionDialog(type, poWhoCan);
        return G.fragmentActivity.getResources().getString(resString);
    }

    private void positionDialog(int type, int poWhoCan) {

        switch (type) {
            case 0:
                poSeeMyAvatar = poWhoCan;
                break;
            case 1:
                poInviteChannel = poWhoCan;
                break;
            case 2:
                poInviteGroup = poWhoCan;
                break;
            case 3:
                poSeeLastSeen = poWhoCan;
                break;
            case 4:
                poVoiceCall = poWhoCan;
                break;
        }
    }

    private void updatePrivacyUI(RealmPrivacy realmPrivacy) {
        if (realmPrivacy.isValid()) {

            callbackSeeMyAvatar.set(getStringFromEnumString(realmPrivacy.getWhoCanSeeMyAvatar(), SEE_MY_AVATAR));
            callbackInviteChannel.set(getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToChannel(), INVITE_CHANNEL));
            callbackInviteGroup.set(getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToGroup(), INVITE_GROUP));
            callbackSeeLastSeen.set(getStringFromEnumString(realmPrivacy.getWhoCanSeeMyLastSeen(), LAST_SEEN));
            callbackVoiceCall.set(getStringFromEnumString(realmPrivacy.getWhoCanVoiceCallToMe(), VOICE_CALL));
        }
    }

    private void selfDestructs() {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.self_destructs)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.account_self_destruct).itemsCallbackSingleChoice(poSelfRemove, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {
                        callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_1));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(1);
                        break;
                    }
                    case 1: {
                        callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_3));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(3);
                        break;
                    }
                    case 2: {

                        callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_6));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(6);
                        break;
                    }
                    case 3: {

                        callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.year_1));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(12);
                        break;
                    }
                }
                return false;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
    }

    private void setTextSelfDestructs() throws IllegalStateException {
        if (selfRemove != 0) {
            switch (selfRemove) {
                case 1:
                    callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_1));
                    poSelfRemove = 0;
                    break;
                case 3:
                    callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_3));
                    poSelfRemove = 1;
                    break;
                case 6:
                    callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_6));
                    poSelfRemove = 2;
                    break;
                case 12:
                    callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.year_1));
                    poSelfRemove = 3;
                    break;
            }
        } else {
            callbackSelfDestruction.set(G.fragmentActivity.getResources().getString(R.string.month_6));
        }
    }


    public void onPause() {

        if (realmUserInfo != null) {
            realmUserInfo.removeAllChangeListeners();
        }

        if (realmPrivacy != null) {
            realmPrivacy.removeAllChangeListeners();
        }

    }


    public void onResume() {

        if (realmUserInfo != null) {
            if (userInfoListener != null) {
                realmUserInfo.addChangeListener(userInfoListener);
            }
            selfRemove = realmUserInfo.getSelfRemove();
            setTextSelfDestructs();
        }

        if (realmPrivacy != null) {
            if (privacyListener != null) {
                realmPrivacy.addChangeListener(privacyListener);
            }
        }
        updatePrivacyUI(realmPrivacy);
    }


    public void onDetach() {
        realm.close();
    }


}
