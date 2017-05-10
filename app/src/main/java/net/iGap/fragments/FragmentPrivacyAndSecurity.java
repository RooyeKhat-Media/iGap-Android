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
import static net.iGap.R.id.st_layoutParent;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPrivacyAndSecurity extends Fragment {

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

    private LinearLayout layoutWhoCanSeeMyAvatar;
    private LinearLayout layoutWhoCanInviteMeToChannel;
    private LinearLayout layoutWhoCanInviteMeToGroup;
    private LinearLayout layoutWhoCanSeeMyLastSeen;

    private Realm mRealm;
    private RealmChangeListener<RealmModel> userInfoListener;
    private RealmUserInfo realmUserInfo;

    private RealmPrivacy realmPrivacy;
    private RealmChangeListener<RealmModel> privacyListener;
    private int poWhoCan;

    public FragmentPrivacyAndSecurity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy_and_security, container, false);
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
    public void onDestroy() {
        super.onDestroy();

        if (mRealm != null) mRealm.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new RequestUserContactsGetBlockedList().userContactsGetBlockedList();
        view.findViewById(R.id.stps_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.fpac_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

        mRealm = Realm.getDefaultInstance();

        RealmPrivacy.getUpdatePrivacyFromServer();

        realmUserInfo = mRealm.where(RealmUserInfo.class).findFirst();
        userInfoListener = new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                selfRemove = ((RealmUserInfo) element).getSelfRemove();
                setTextSelfDestructs();
            }
        };

        realmPrivacy = mRealm.where(RealmPrivacy.class).findFirst();
        privacyListener = new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                updatePrivacyUI((RealmPrivacy) element);
            }
        };

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
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentPrivacyAndSecurity.this).commit();
            }
        });

        TextView txtActiveSessions = (TextView) view.findViewById(R.id.stps_activitySessions);
        txtActiveSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentActiveSessions fragmentActiveSessions = new FragmentActiveSessions();
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(st_layoutParent, fragmentActiveSessions, null).commit();
            }
        });

        TextView txtBlockedUser = (TextView) view.findViewById(R.id.stps_txt_blocked_user);
        txtBlockedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentBlockedUser fragmentBlockedUser = new FragmentBlockedUser();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.parentPrivacySecurity, fragmentBlockedUser, null).commit();
            }
        });

        txtWhoCanSeeMyAvatar = (TextView) view.findViewById(R.id.stps_txt_who_can_see_my_avatar);
        txtWhoCanInviteMeToChannel = (TextView) view.findViewById(R.id.stps_txt_who_can_invite_me_to_Channel);
        txtWhoCanInviteMeToGroup = (TextView) view.findViewById(R.id.stps_txt_who_can_invite_me_to_group);
        txtWhoCanSeeMyLastSeen = (TextView) view.findViewById(R.id.stps_who_can_see_my_last_seen);

        layoutWhoCanSeeMyAvatar = (LinearLayout) view.findViewById(R.id.stps_ll_who_can_see_my_avatar);
        layoutWhoCanInviteMeToChannel = (LinearLayout) view.findViewById(R.id.stps_ll_who_can_invite_me_to_Channel);
        layoutWhoCanInviteMeToGroup = (LinearLayout) view.findViewById(R.id.stps_ll_who_can_invite_me_to_group);
        layoutWhoCanSeeMyLastSeen = (LinearLayout) view.findViewById(R.id.stps_ll_who_can_see_my_last_seen);

        layoutWhoCanSeeMyAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmPrivacy != null) {
                    getStringFromEnumString(realmPrivacy.getWhoCanSeeMyAvatar());
                    openDialogWhoCan(ProtoGlobal.PrivacyType.AVATAR, poWhoCan);
                }
            }
        });

        layoutWhoCanInviteMeToChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmPrivacy != null) {
                    getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToChannel());
                    openDialogWhoCan(ProtoGlobal.PrivacyType.CHANNEL_INVITE, poWhoCan);
                }
            }
        });

        layoutWhoCanInviteMeToGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmPrivacy != null) {
                    getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToGroup());
                    openDialogWhoCan(ProtoGlobal.PrivacyType.GROUP_INVITE, poWhoCan);
                }
            }
        });

        layoutWhoCanSeeMyLastSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmPrivacy != null) {
                    getStringFromEnumString(realmPrivacy.getWhoCanSeeMyLastSeen());
                    openDialogWhoCan(ProtoGlobal.PrivacyType.USER_STATUS, poWhoCan);
                }
            }
        });

        txtDestruction = (TextView) view.findViewById(R.id.stps_txt_Self_destruction);

        sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        poSelfRemove = sharedPreferences.getInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 2);
        ViewGroup ltSelfDestruction = (ViewGroup) view.findViewById(R.id.stps_layout_Self_destruction);
        ltSelfDestruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfDestructs();
            }
        });

        new RequestUserProfileGetSelfRemove().userProfileGetSelfRemove();
    }

    private void selfDestructs() {

        new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.self_destructs)).titleGravity(GravityEnum.START).titleColor(getResources().getColor(android.R.color.black)).items(R.array.account_self_destruct).itemsCallbackSingleChoice(poSelfRemove, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {
                        txtDestruction.setText(getResources().getString(R.string.month_1));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(1);
                        break;
                    }
                    case 1: {
                        txtDestruction.setText(getResources().getString(R.string.month_3));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(3);
                        break;
                    }
                    case 2: {

                        txtDestruction.setText(getResources().getString(R.string.month_6));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(6);
                        break;
                    }
                    case 3: {

                        txtDestruction.setText(getResources().getString(R.string.year_1));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(12);
                        break;
                    }
                }
                return false;
            }
        }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
    }

    private void setTextSelfDestructs() throws IllegalStateException {
        if (selfRemove != 0) {
            switch (selfRemove) {
                case 1:
                    txtDestruction.setText(getResources().getString(R.string.month_1));
                    poSelfRemove = 0;
                    break;
                case 3:
                    txtDestruction.setText(getResources().getString(R.string.month_3));
                    poSelfRemove = 1;
                    break;
                case 6:
                    txtDestruction.setText(getResources().getString(R.string.month_6));
                    poSelfRemove = 2;
                    break;
                case 12:
                    txtDestruction.setText(getResources().getString(R.string.year_1));
                    poSelfRemove = 3;
                    break;
            }
        } else {
            txtDestruction.setText(getResources().getString(R.string.month_6));
        }
    }

    private void openDialogWhoCan(final ProtoGlobal.PrivacyType privacyType, int position) {

        new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.privacy_setting)).titleGravity(GravityEnum.START).titleColor(getResources().getColor(android.R.color.black)).items(R.array.privacy_setting_array).itemsCallbackSingleChoice(position, new MaterialDialog.ListCallbackSingleChoice() {
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
        }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
    }

    private void updatePrivacyUI(RealmPrivacy realmPrivacy) {

        txtWhoCanSeeMyAvatar.setText(getStringFromEnumString(realmPrivacy.getWhoCanSeeMyAvatar()));
        txtWhoCanInviteMeToChannel.setText(getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToChannel()));
        txtWhoCanInviteMeToGroup.setText(getStringFromEnumString(realmPrivacy.getWhoCanInviteMeToGroup()));
        txtWhoCanSeeMyLastSeen.setText(getStringFromEnumString(realmPrivacy.getWhoCanSeeMyLastSeen()));
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
