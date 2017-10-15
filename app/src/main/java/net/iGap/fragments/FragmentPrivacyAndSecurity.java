/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import java.util.ArrayList;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperFragment;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.structs.StructSessions;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmPrivacy;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserContactsGetBlockedList;
import net.iGap.request.RequestUserProfileGetSelfRemove;
import net.iGap.request.RequestUserProfileSetSelfRemove;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPrivacyAndSecurity extends BaseFragment {

    int poSelfRemove;
    private SharedPreferences sharedPreferences;
    private int poRbDialogSelfDestruction = 0;
    private int selfRemove;
    private ArrayList<StructSessions> itemSessionsgetActivelist = new ArrayList<StructSessions>();
    private TextView txtDestruction;

    private TextView txtWhoCanSeeMyAvatar;
    private TextView txtWhoCanInviteMeToChannel;
    private TextView txtWhoCanInviteMeToGroup;
    private TextView txtWhoCanSeeMyLastSeen;
    private TextView txtWhoCanVoiceCallToMe;

    private LinearLayout layoutWhoCanSeeMyAvatar;
    private LinearLayout layoutWhoCanInviteMeToChannel;
    private LinearLayout layoutWhoCanInviteMeToGroup;
    private LinearLayout layoutWhoCanSeeMyLastSeen;
    private LinearLayout layoutWhoCanVoiceCallToMe;

    private RealmChangeListener<RealmModel> userInfoListener;
    private RealmUserInfo realmUserInfo;

    private RealmPrivacy realmPrivacy;
    private RealmChangeListener<RealmModel> privacyListener;
    private int poWhoCan;

    public FragmentPrivacyAndSecurity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_privacy_and_security, container, false));
    }

    @Override
    public void onResume() {
        super.onResume();

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

            updatePrivacyUI(realmPrivacy);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (realmUserInfo != null) {
            realmUserInfo.removeAllChangeListeners();
        }

        if (realmPrivacy != null) {
            realmPrivacy.removeAllChangeListeners();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new RequestUserContactsGetBlockedList().userContactsGetBlockedList();
        view.findViewById(R.id.stps_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        Realm realm = Realm.getDefaultInstance();

        realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        userInfoListener = new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                if (((RealmUserInfo) element).isValid()) {
                    selfRemove = ((RealmUserInfo) element).getSelfRemove();
                    setTextSelfDestructs();
                }
            }
        };

        realmPrivacy = realm.where(RealmPrivacy.class).findFirst();

        privacyListener = new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                updatePrivacyUI((RealmPrivacy) element);
            }
        };

        RealmPrivacy.getUpdatePrivacyFromServer();

        RelativeLayout parentPrivacySecurity = (RelativeLayout) view.findViewById(R.id.parentPrivacySecurity);
        parentPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        RippleView rippleBack = (RippleView) view.findViewById(R.id.stps_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });


        TextView txtPassCode = (TextView) view.findViewById(R.id.stps_passCode);
        txtPassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(new FragmentPassCode()).setReplace(false).load();
            }
        });

        TextView txtSecurity = (TextView) view.findViewById(R.id.stps_twoStepVerification);
        txtSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(new FragmentSecurity()).setReplace(false).load();
            }
        });

        TextView txtActiveSessions = (TextView) view.findViewById(R.id.stps_activitySessions);
        txtActiveSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(new FragmentActiveSessions()).setReplace(false).load();
            }
        });

        TextView txtBlockedUser = (TextView) view.findViewById(R.id.stps_txt_blocked_user);
        txtBlockedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(new FragmentBlockedUser()).setReplace(false).load();
            }
        });

        txtWhoCanSeeMyAvatar = (TextView) view.findViewById(R.id.stps_txt_who_can_see_my_avatar);
        txtWhoCanInviteMeToChannel = (TextView) view.findViewById(R.id.stps_txt_who_can_invite_me_to_Channel);
        txtWhoCanInviteMeToGroup = (TextView) view.findViewById(R.id.stps_txt_who_can_invite_me_to_group);
        txtWhoCanSeeMyLastSeen = (TextView) view.findViewById(R.id.stps_who_can_see_my_last_seen);
        txtWhoCanVoiceCallToMe = (TextView) view.findViewById(R.id.stps_txt_who_can_voice_call_to_me);

        layoutWhoCanSeeMyAvatar = (LinearLayout) view.findViewById(R.id.stps_ll_who_can_see_my_avatar);
        layoutWhoCanInviteMeToChannel = (LinearLayout) view.findViewById(R.id.stps_ll_who_can_invite_me_to_Channel);
        layoutWhoCanInviteMeToGroup = (LinearLayout) view.findViewById(R.id.stps_ll_who_can_invite_me_to_group);
        layoutWhoCanSeeMyLastSeen = (LinearLayout) view.findViewById(R.id.stps_ll_who_can_see_my_last_seen);
        layoutWhoCanVoiceCallToMe = (LinearLayout) view.findViewById(R.id.stps_ll_who_can_voice_call_to_me);

        layoutWhoCanSeeMyAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmPrivacy != null) {
                    getStringFromEnumString(realmPrivacy.getWhoCanSeeMyAvatar());
                    openDialogWhoCan(ProtoGlobal.PrivacyType.AVATAR, poWhoCan, R.string.title_who_can_see_my_avatar);
                }
            }
        });

        layoutWhoCanInviteMeToChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmPrivacy != null) {
                    getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToChannel());
                    openDialogWhoCan(ProtoGlobal.PrivacyType.CHANNEL_INVITE, poWhoCan, R.string.title_who_can_invite_you_to_channel_s);
                }
            }
        });

        layoutWhoCanInviteMeToGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmPrivacy != null) {
                    getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToGroup());
                    openDialogWhoCan(ProtoGlobal.PrivacyType.GROUP_INVITE, poWhoCan, R.string.title_who_can_invite_you_to_group_s);
                }
            }
        });

        layoutWhoCanSeeMyLastSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmPrivacy != null) {
                    getStringFromEnumString(realmPrivacy.getWhoCanSeeMyLastSeen());
                    openDialogWhoCan(ProtoGlobal.PrivacyType.USER_STATUS, poWhoCan, R.string.title_Last_Seen);
                }
            }
        });

        layoutWhoCanVoiceCallToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmPrivacy != null) {
                    getStringFromEnumString(realmPrivacy.getWhoCanVoiceCallToMe());
                    openDialogWhoCan(ProtoGlobal.PrivacyType.VOICE_CALLING, poWhoCan, R.string.title_who_is_allowed_to_call);
                }
            }
        });



        txtDestruction = (TextView) view.findViewById(R.id.stps_txt_Self_destruction);

        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        poSelfRemove = sharedPreferences.getInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 2);
        ViewGroup ltSelfDestruction = (ViewGroup) view.findViewById(R.id.stps_layout_Self_destruction);
        ltSelfDestruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfDestructs();
            }
        });

        new RequestUserProfileGetSelfRemove().userProfileGetSelfRemove();

        realm.close();
    }

    private void selfDestructs() {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.self_destructs)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.account_self_destruct).itemsCallbackSingleChoice(poSelfRemove, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {
                        txtDestruction.setText(G.fragmentActivity.getResources().getString(R.string.month_1));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(1);
                        break;
                    }
                    case 1: {
                        txtDestruction.setText(G.fragmentActivity.getResources().getString(R.string.month_3));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(3);
                        break;
                    }
                    case 2: {

                        txtDestruction.setText(G.fragmentActivity.getResources().getString(R.string.month_6));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(6);
                        break;
                    }
                    case 3: {

                        txtDestruction.setText(G.fragmentActivity.getResources().getString(R.string.year_1));
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
                    txtDestruction.setText(G.fragmentActivity.getResources().getString(R.string.month_1));
                    poSelfRemove = 0;
                    break;
                case 3:
                    txtDestruction.setText(G.fragmentActivity.getResources().getString(R.string.month_3));
                    poSelfRemove = 1;
                    break;
                case 6:
                    txtDestruction.setText(G.fragmentActivity.getResources().getString(R.string.month_6));
                    poSelfRemove = 2;
                    break;
                case 12:
                    txtDestruction.setText(G.fragmentActivity.getResources().getString(R.string.year_1));
                    poSelfRemove = 3;
                    break;
            }
        } else {
            txtDestruction.setText(G.fragmentActivity.getResources().getString(R.string.month_6));
        }
    }

    private void openDialogWhoCan(final ProtoGlobal.PrivacyType privacyType, int position, int title) {

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
                return false;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
    }

    private void updatePrivacyUI(RealmPrivacy realmPrivacy) {
        if (realmPrivacy.isValid()) {
            txtWhoCanSeeMyAvatar.setText(getStringFromEnumString(realmPrivacy.getWhoCanSeeMyAvatar()));
            txtWhoCanInviteMeToChannel.setText(getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToChannel()));
            txtWhoCanInviteMeToGroup.setText(getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToGroup()));
            txtWhoCanSeeMyLastSeen.setText(getStringFromEnumString(realmPrivacy.getWhoCanSeeMyLastSeen()));
            txtWhoCanVoiceCallToMe.setText(getStringFromEnumString(realmPrivacy.getWhoCanVoiceCallToMe()));
        }
    }

    private int getStringFromEnumString(String str) {

        if (str == null || str.length() == 0) {
            poWhoCan = 0;
            return R.string.everybody;
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

        return resString;
    }
}
