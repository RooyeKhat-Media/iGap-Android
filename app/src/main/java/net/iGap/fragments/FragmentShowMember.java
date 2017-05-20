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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityChannelProfile;
import net.iGap.activities.ActivityContactsProfile;
import net.iGap.activities.ActivityGroupProfile;
import net.iGap.activities.ActivitySetting;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperPermision;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChannelGetMemberList;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupGetMemberList;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.DeviceUtils;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SUID;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.proto.ProtoChannelGetMemberList;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmMemberFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelGetMemberList;
import net.iGap.request.RequestGroupGetMemberList;
import net.iGap.request.RequestUserInfo;

public class FragmentShowMember extends Fragment {

    public static final String ROOMIDARGUMENT = "ROOMID_ARGUMENT";
    public static final String MAINROOL = "MAIN_ROOL";
    public static final String USERID = "USER_ID";
    public static final String SELECTEDROLE = "SELECTED_ROLE";
    public static final String ISNEEDGETMEMBERLIST = "IS_NEED_GET_MEMBER_LIST";

    private long mRoomID = 0;

    private RealmRecyclerView mRecyclerView;
    private MemberAdapter mAdapter;
    private String mMainRole = "";
    private ProgressBar progressBar;

    private Long userID = 0l;
    private String role;
    private String selectedRole = ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString();
    private boolean isNeedGetMemberList = false;
    private int mMemberCount = 0;
    private int mCurrentUpdateCount = 0;
    public static List<StructMessageInfo> lists = new ArrayList<>();
    List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> listMembers = new ArrayList<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member>();
    List<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member> listMembersChannal = new ArrayList<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member>();
    private boolean isFirstFill = true;
    private int offset = 0;
    private int limit = 30;
    private FragmentActivity mActivity;
    public static OnComplete infoUpdateListenerCount;
    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean isDeleteMemberList = true;

    public static FragmentShowMember newInstance(long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList) {
        Bundle bundle = new Bundle();
        bundle.putLong(ROOMIDARGUMENT, roomId);
        bundle.putString(MAINROOL, mainrool);
        bundle.putLong(USERID, userid);
        bundle.putString(SELECTEDROLE, selectedRole);
        bundle.putBoolean(ISNEEDGETMEMBERLIST, isNeedGetMemberList);
        FragmentShowMember fragmentShowMember = new FragmentShowMember();
        fragmentShowMember.setArguments(bundle);
        return fragmentShowMember;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_member, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {

            mRoomID = getArguments().getLong(ROOMIDARGUMENT);
            mMainRole = getArguments().getString(MAINROOL);
            userID = getArguments().getLong(USERID);
            selectedRole = getArguments().getString(SELECTEDROLE);
            //isNeedGetMemberList = getArguments().getBoolean(ISNEEDGETMEMBERLIST);
            isNeedGetMemberList = true;

            if (mRoomID > 0) {
                initComponent(view);
            }

            if (isNeedGetMemberList) {
                if (G.userLogin) {
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            new getAcynkMember().execute();
                        }
                    }, 100);
                }
            }
        }
    }

    class getAcynkMember extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            getMemberList();

            return null;
        }
    }


    private void getMemberList() {
        mMemberCount = listMembers.size();
        infoUpdateListenerCount = new OnComplete() {
            @Override
            public void complete(boolean result, final String messageOne, String MessageTow) {

                if (MessageTow.contains("OK")) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            final Realm realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    final RealmList<RealmMember> newMemberList = new RealmList<>();
                                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomID).findFirst();
                                    if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                                        for (ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : listMembers) {
                                            if (Long.parseLong(messageOne) == member.getUserId()) {
                                                mCurrentUpdateCount++;
                                                RealmMember realmMem = realm.createObject(RealmMember.class, SUID.id().get());
                                                realmMem.setRole(member.getRole().toString());
                                                realmMem.setPeerId(member.getUserId());
                                                realmMem = realm.copyToRealm(realmMem);
                                                newMemberList.add(realmMem);
                                                newMemberList.addAll(0, realmRoom.getGroupRoom().getMembers());
                                                realmRoom.getGroupRoom().setMembers(newMemberList);
                                                newMemberList.clear();
                                                break;
                                            }
                                        }
                                    } else {
                                        for (ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : listMembersChannal) {
                                            if (Long.parseLong(messageOne) == member.getUserId()) {
                                                mCurrentUpdateCount++;
                                                RealmMember realmMem = realm.createObject(RealmMember.class, SUID.id().get());
                                                realmMem.setRole(member.getRole().toString());
                                                realmMem.setPeerId(member.getUserId());
                                                realmMem = realm.copyToRealm(realmMem);
                                                newMemberList.add(realmMem);
                                                newMemberList.addAll(0, realmRoom.getChannelRoom().getMembers());
                                                realmRoom.getChannelRoom().setMembers(newMemberList);
                                                newMemberList.clear();
                                                break;
                                            }
                                        }
                                    }

                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    fillItem();
                                    realm.close();

                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    realm.close();
                                }
                            });
                        }
                    });
                } else {
                    mCurrentUpdateCount++;
                }
            }
        };

        G.onGroupGetMemberList = new OnGroupGetMemberList() {
            @Override
            public void onGroupGetMemberList(final List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> members) {

                mMemberCount = members.size();
                if (mMemberCount > 0) {

                    listMembers.clear();
                    for (final ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : members) {
                        listMembers.add(member);
                        new RequestUserInfo().userInfo(member.getUserId(), "" + member.getUserId());
                    }
                } else {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                    isOne = true;
                    if (isFirstFill) {
                        fillAdapter();
                        isFirstFill = false;
                    }
                }
            }
        };

        G.onChannelGetMemberList = new OnChannelGetMemberList() {
            @Override
            public void onChannelGetMemberList(List<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member> members) {

                mMemberCount = members.size();

                Realm realm = Realm.getDefaultInstance();
                for (final ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : members) {
                    final RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, member.getUserId()).findFirst();
                    if (realmRegisteredInfo == null) {
                        listMembersChannal.add(member);
                        new RequestUserInfo().userInfo(member.getUserId());
                    } else {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (progressBar != null) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                        isOne = true;
                        if (isFirstFill) {
                            fillAdapter();
                            isFirstFill = false;
                        }
                    }
                }

                realm.close();
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentUpdateCount = 0;
                Realm realm = Realm.getDefaultInstance();

                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomID).findFirst();
                if (realmRoom != null) {
                    if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (realmRoom.getGroupRoom().getMembers() != null) {
                                    realmRoom.getGroupRoom().getMembers().deleteAllFromRealm();
                                }
                            }
                        });
                        new RequestGroupGetMemberList().getMemberList(mRoomID, offset, limit, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.valueOf(selectedRole));
                    } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (realmRoom.getChannelRoom().getMembers() != null) {
                                    realmRoom.getChannelRoom().getMembers().deleteAllFromRealm();
                                }
                            }
                        });
                        new RequestChannelGetMemberList().channelGetMemberList(mRoomID, offset, limit);
                    }
                }

                realm.close();
            }
        });
    }

    private void fillItem() {

        if (mCurrentUpdateCount >= mMemberCount) {
            if (!isOne && mCurrentUpdateCount > 0) isOne = true;
            try {
                if (isFirstFill) {
                    fillAdapter();
                    isFirstFill = false;
                }

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


    private void initComponent(View view) {

        final TextView menu_txt_titleToolbar = (TextView) view.findViewById(R.id.member_txt_titleToolbar);
        final EditText edtSearch = (EditText) view.findViewById(R.id.menu_edt_search);
        final RippleView txtClose = (RippleView) view.findViewById(R.id.menu_ripple_search);
        final RippleView txtSearch = (RippleView) view.findViewById(R.id.member_edtSearch);

        txtSearch.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                txtClose.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                edtSearch.setFocusable(true);
                menu_txt_titleToolbar.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
            }
        });

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSearch.getText().length() > 0) {
                    edtSearch.setText("");
                } else {
                    txtClose.setVisibility(View.GONE);
                    edtSearch.setVisibility(View.GONE);
                    menu_txt_titleToolbar.setVisibility(View.VISIBLE);
                    txtSearch.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomID).findFirst();
                RealmResults<RealmRegisteredInfo> findMember;

                if (s.length() > 0) {
                    findMember = realm.where(RealmRegisteredInfo.class).contains(RealmRegisteredInfoFields.DISPLAY_NAME, s.toString(), Case.INSENSITIVE).findAllSorted(RealmRegisteredInfoFields.DISPLAY_NAME);
                } else {
                    findMember = realm.where(RealmRegisteredInfo.class).equalTo(RealmRoomFields.ID, mRoomID).findAllSorted(RealmRegisteredInfoFields.DISPLAY_NAME);
                }

                RealmQuery<RealmMember> query = realm.where(RealmMember.class);
                for (int i = 0; i < findMember.size(); i++) {
                    if (i != 0) query = query.or();
                    query = query.equalTo(RealmMemberFields.PEER_ID, findMember.get(i).getId());
                }
                RealmResults<RealmMember> searchMember = query.findAll();
                mAdapter = new MemberAdapter(mActivity, searchMember, realmRoom.getType(), mMainRole, userID);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                realm.close();
            }
        });

        mRecyclerView = (RealmRecyclerView) view.findViewById(R.id.fcm_recycler_view_show_member);
        mRecyclerView.setItemViewCacheSize(100);

        final PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(getActivity());
        mRecyclerView.getRecycleView().setLayoutManager(preCachingLayoutManager);
        preCachingLayoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));

        progressBar = (ProgressBar) view.findViewById(R.id.fcg_prgWaiting);
        AppUtils.setProgresColler(progressBar);

        view.findViewById(R.id.fcg_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fcg_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        //TextView txtNumberOfMember = (TextView) view.findViewById(R.id.fcg_txt_member);

        //if (selectedRole.toString().equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString())) {
        //    txtNumberOfMember.setText(getResources().getString(R.string.list_modereator));
        //} else if (selectedRole.toString().equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString())) {
        //    txtNumberOfMember.setText(getResources().getString(R.string.list_admin));
        //} else {
        //    txtNumberOfMember.setText(getResources().getString(member));
        //}

        scrollListener = new EndlessRecyclerViewScrollListener(preCachingLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                loadMoreMember(page, totalItemsCount, view);
            }
        };

        mRecyclerView.getRecycleView().addOnScrollListener(scrollListener);
    }

    private boolean isOne = true;

    private void loadMoreMember(int page, int totalItemsCount, RecyclerView view) {

        if (isOne) {
            isOne = false;
            mCurrentUpdateCount = 0;

            Realm realm = Realm.getDefaultInstance();

            offset += limit;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomID).findFirst();
            if (realmRoom != null) {
                if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                    new RequestGroupGetMemberList().getMemberList(mRoomID, offset, limit, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.valueOf(selectedRole));
                } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                    new RequestChannelGetMemberList().channelGetMemberList(mRoomID, offset, limit);
                }
            }

            realm.close();
        }
    }

    private void fillAdapter() {

        Realm realm = Realm.getDefaultInstance();
        RealmList<RealmMember> memberList = null;
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomID).findFirst();
        if (realmRoom != null) {

            if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                memberList = realmRoom.getGroupRoom().getMembers();
                role = realmRoom.getGroupRoom().getRole().toString();
            } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                memberList = realmRoom.getChannelRoom().getMembers();
                role = realmRoom.getChannelRoom().getRole().toString();
            }

            if (memberList != null && memberList.size() > 0) {
                RealmResults<RealmMember> mList;

                if (selectedRole.toString().equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString())) {
                    mList = memberList.where().findAll();
                } else {
                    mList = memberList.where().equalTo(RealmMemberFields.ROLE, selectedRole).findAll();
                }

                if (mList.size() > 0 && mActivity != null) {
                    mAdapter = new MemberAdapter(mActivity, mList, realmRoom.getType(), mMainRole, userID);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }

        realm.close();
    }

    private class MemberAdapter extends RealmBasedRecyclerViewAdapter<RealmMember, FragmentShowMember.MemberAdapter.ViewHolder> {

        public String mainRole;
        public ProtoGlobal.Room.Type roomType;
        public long userid;

        public MemberAdapter(Context context, RealmResults<RealmMember> realmResults, ProtoGlobal.Room.Type roomType, String mainRole, long userid) {

            super(context, realmResults, true, false, false, "");

            this.roomType = roomType;
            this.mainRole = mainRole;
            this.userid = userid;
        }

        @Override
        public MemberAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.contact_item_group_profile, viewGroup, false);
            return new ViewHolder(v);
        }

        public class ViewHolder extends RealmViewHolder {

            protected CircleImageView image;
            protected CustomTextViewMedium title;
            protected CustomTextViewMedium subtitle;
            protected View topLine;
            protected TextView txtNumberOfSharedMedia;
            protected MaterialDesignTextView roleStar;
            protected MaterialDesignTextView btnMenu;

            public ViewHolder(View itemView) {
                super(itemView);

                image = (CircleImageView) itemView.findViewById(R.id.cigp_imv_contact_avatar);
                title = (CustomTextViewMedium) itemView.findViewById(R.id.cigp_txt_contact_name);
                subtitle = (CustomTextViewMedium) itemView.findViewById(R.id.cigp_txt_contact_lastseen);
                topLine = itemView.findViewById(R.id.cigp_view_topLine);
                txtNumberOfSharedMedia = (TextView) itemView.findViewById(R.id.cigp_txt_nomber_of_shared_media);
                roleStar = (MaterialDesignTextView) itemView.findViewById(R.id.cigp_txt_member_role);
                btnMenu = (MaterialDesignTextView) itemView.findViewById(R.id.cigp_moreButton);
            }
        }

        @Override
        public void onBindRealmViewHolder(final MemberAdapter.ViewHolder holder, int i) {

            final StructContactInfo mContact = convertRealmToStruct(realmResults.get(i));

            if (mContact == null) {
                return;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        HelperPermision.getStoragePermision(getActivity(), new OnGetPermission() {
                            @Override
                            public void Allow() {

                                Intent intent = null;

                                if (mContact.peerId == userID) {
                                    intent = new Intent(getActivity(), ActivitySetting.class);
                                } else {
                                    intent = new Intent(getActivity(), ActivityContactsProfile.class);

                                    intent.putExtra("peerId", mContact.peerId);
                                    intent.putExtra("RoomId", mRoomID);
                                    intent.putExtra("enterFrom", ProtoGlobal.Room.Type.GROUP.toString());
                                }

                                //getActivity().finish();
                                //if (ActivityChat.activityChat != null) ActivityChat.activityChat.finish();

                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (getActivity() instanceof ActivityGroupProfile) {

                        if (role.equals(GroupChatRole.OWNER.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {

                                ((ActivityGroupProfile) getActivity()).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                                ((ActivityGroupProfile) getActivity()).kickAdmin(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                                ((ActivityGroupProfile) getActivity()).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.ADMIN.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((ActivityGroupProfile) getActivity()).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                                ((ActivityGroupProfile) getActivity()).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.MODERATOR.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((ActivityGroupProfile) getActivity()).kickMember(mContact.peerId);
                            }
                        }
                    } else if (getActivity() instanceof ActivityChannelProfile) {

                        if (role.equals(GroupChatRole.OWNER.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {

                                ((ActivityChannelProfile) getActivity()).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                                ((ActivityChannelProfile) getActivity()).kickAdmin(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                                ((ActivityChannelProfile) getActivity()).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.ADMIN.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((ActivityChannelProfile) getActivity()).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                                ((ActivityChannelProfile) getActivity()).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.MODERATOR.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((ActivityChannelProfile) getActivity()).kickMember(mContact.peerId);
                            }
                        }
                    }

                    return true;
                }
            });

            if (mContact.isHeader) {
                holder.topLine.setVisibility(View.VISIBLE);
            } else {
                holder.topLine.setVisibility(View.GONE);
            }

            holder.title.setText(mContact.displayName);

            setRoleStarColor(holder.roleStar, mContact);


            HelperAvatar.getAvatar(mContact.peerId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                @Override
                public void onAvatarGet(String avatarPath, long roomId) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
                }

                @Override
                public void onShowInitials(String initials, String color) {
                    holder.image.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                }
            });

            if (mContact.status != null) {
                if (mContact.status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                    holder.subtitle.setText(LastSeenTimeUtil.computeTime(mContact.peerId, mContact.lastSeen, false));
                } else {
                    holder.subtitle.setText(mContact.status);
                }
            }

            if (mainRole.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                holder.btnMenu.setVisibility(View.GONE);
            } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString()) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString()))) {
                    holder.btnMenu.setVisibility(View.GONE);
                } else {
                    showPopup(holder, mContact);
                }
            } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString()) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString()))) {
                    holder.btnMenu.setVisibility(View.GONE);
                } else {
                    showPopup(holder, mContact);
                }
            } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
                showPopup(holder, mContact);
            }

            /**
             * don't allow for use dialog if this item
             * is for own user
             */
            if (mContact.peerId == mContact.userID) {
                holder.btnMenu.setVisibility(View.GONE);
            }
        }

        private void showPopup(ViewHolder holder, final StructContactInfo mContact) {
            holder.btnMenu.setVisibility(View.VISIBLE);

            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
                        if (ActivityChannelProfile.onMenuClick != null) {
                            ActivityChannelProfile.onMenuClick.clicked(v, mContact);
                        }
                    } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
                        if (ActivityGroupProfile.onMenuClick != null) {
                            ActivityGroupProfile.onMenuClick.clicked(v, mContact);
                        }
                    }
                }
            });
        }

        private void setRoleStarColor(MaterialDesignTextView view, StructContactInfo mContact) {

            view.setVisibility(View.GONE);

            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                view.setVisibility(View.VISIBLE);
                view.setTextColor(Color.CYAN);
            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
                view.setVisibility(View.VISIBLE);
                view.setTextColor(Color.GREEN);
            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
                view.setVisibility(View.VISIBLE);
                view.setTextColor(Color.BLUE);
            }
        }

        StructContactInfo convertRealmToStruct(RealmMember realmMember) {
            Realm realm = Realm.getDefaultInstance();
            String role = realmMember.getRole();
            long id = realmMember.getPeerId();
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, id).findFirst();
            if (realmRegisteredInfo != null) {
                StructContactInfo s = new StructContactInfo(realmRegisteredInfo.getId(), realmRegisteredInfo.getDisplayName(), realmRegisteredInfo.getStatus(), false, false, realmRegisteredInfo.getPhoneNumber() + "");
                s.role = role;
                s.avatar = realmRegisteredInfo.getLastAvatar();
                s.initials = realmRegisteredInfo.getInitials();
                s.color = realmRegisteredInfo.getColor();
                s.lastSeen = realmRegisteredInfo.getLastSeen();
                s.status = realmRegisteredInfo.getStatus();
                s.userID = userid;
                return s;
            }

            realm.close();
            return null;
        }
    }

    public class PreCachingLayoutManager extends LinearLayoutManager {
        private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
        private int extraLayoutSpace = -1;
        private Context context;

        public PreCachingLayoutManager(Context context) {
            super(context);
            this.context = context;
        }

        public PreCachingLayoutManager(Context context, int extraLayoutSpace) {
            super(context);
            this.context = context;
            this.extraLayoutSpace = extraLayoutSpace;
        }

        public PreCachingLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            this.context = context;
        }

        public void setExtraLayoutSpace(int extraLayoutSpace) {
            this.extraLayoutSpace = extraLayoutSpace;
        }

        @Override
        protected int getExtraLayoutSpace(RecyclerView.State state) {
            if (extraLayoutSpace > 0) {
                return extraLayoutSpace;
            }
            return DEFAULT_EXTRA_LAYOUT_SPACE;
        }

        private static final float MILLISECONDS_PER_INCH = 2000f; //default is 25f (bigger = slower)

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return PreCachingLayoutManager.this.computeScrollVectorForPosition(targetPosition);
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };

            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }
}
