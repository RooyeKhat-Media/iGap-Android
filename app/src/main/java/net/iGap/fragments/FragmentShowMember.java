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

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChannelAddAdmin;
import net.iGap.interfaces.OnChannelAddModerator;
import net.iGap.interfaces.OnChannelGetMemberList;
import net.iGap.interfaces.OnChannelKickAdmin;
import net.iGap.interfaces.OnChannelKickMember;
import net.iGap.interfaces.OnChannelKickModerator;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAddAdmin;
import net.iGap.interfaces.OnGroupAddModerator;
import net.iGap.interfaces.OnGroupGetMemberList;
import net.iGap.interfaces.OnGroupKickAdmin;
import net.iGap.interfaces.OnGroupKickMember;
import net.iGap.interfaces.OnGroupKickModerator;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.DeviceUtils;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.PreCachingLayoutManager;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.proto.ProtoChannelGetMemberList;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelGetMemberList;
import net.iGap.request.RequestGroupGetMemberList;
import net.iGap.request.RequestUserInfo;
import net.iGap.viewmodel.FragmentChannelProfileViewModel;
import net.iGap.viewmodel.FragmentGroupProfileViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static net.iGap.G.inflater;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class FragmentShowMember extends BaseFragment implements OnGroupAddAdmin, OnGroupKickAdmin, OnGroupAddModerator, OnGroupKickModerator, OnGroupKickMember, OnChannelAddAdmin, OnChannelKickAdmin, OnChannelAddModerator, OnChannelKickModerator, OnChannelKickMember {

    public static final String ROOMIDARGUMENT = "ROOMID_ARGUMENT";
    public static final String MAINROOL = "MAIN_ROOL";
    public static final String USERID = "USER_ID";
    public static final String SELECTEDROLE = "SELECTED_ROLE";
    public static final String ISNEEDGETMEMBERLIST = "IS_NEED_GET_MEMBER_LIST";
    public static List<StructMessageInfo> lists = new ArrayList<>();
    public static OnComplete infoUpdateListenerCount;
    private static Fragment fragment;
    List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> listMembers = new ArrayList<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member>();
    List<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member> listMembersChannal = new ArrayList<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member>();
    private long mRoomID = 0;
    private RecyclerView mRecyclerView;
    //private MemberAdapterA mAdapter;
    private MemberAdapter mAdapter;
    private String mMainRole = "";
    private ProgressBar progressBar;
    private Long userID = 0l;
    private String role;
    private String selectedRole = ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString();
    private boolean isNeedGetMemberList = false;
    private int mMemberCount = 0;
    private int mCurrentUpdateCount = 0;
    private boolean isFirstFill = true;
    private int offset = 0;
    private int limit = 30;
    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean isDeleteMemberList = true;
    private ProtoGlobal.Room.Type roomType;
    private boolean isOne = true;

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

    public static FragmentShowMember newInstance1(Fragment frg, long roomId, String mainrool, long userid, String selectedRole, boolean isNeedGetMemberList) {
        fragment = frg;
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
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_show_member, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //G.onGroupAddAdmin = this;
        //G.onGroupAddModerator = this;
        //G.onGroupKickAdmin = this;
        //G.onGroupKickModerator = this;
        //G.onGroupKickMember = this;
        //G.onChannelAddAdmin = this;
        //G.onChannelAddModerator = this;
        //G.onChannelKickAdmin = this;
        //G.onChannelKickModerator = this;
        //G.onChannelKickMember = this;

        if (getArguments() != null) {

            view.findViewById(R.id.rootShowMember).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mRoomID = getArguments().getLong(ROOMIDARGUMENT);
            mMainRole = getArguments().getString(MAINROOL);
            userID = getArguments().getLong(USERID);
            selectedRole = getArguments().getString(SELECTEDROLE);
            //isNeedGetMemberList = getArguments().getBoolean(ISNEEDGETMEMBERLIST);
            isNeedGetMemberList = true;

            roomType = RealmRoom.detectType(mRoomID);

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
                            new AsyncMember().execute();
                        }
                    }, 100);
                }
            }
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
                                    if (realmRoom != null) {
                                        if (realmRoom.getType() == GROUP) {
                                            for (ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : listMembers) {
                                                if (Long.parseLong(messageOne) == member.getUserId()) {
                                                    mCurrentUpdateCount++;
                                                    newMemberList.add(RealmMember.put(realm, member));
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
                                                    newMemberList.add(RealmMember.put(realm, member));
                                                    newMemberList.addAll(0, realmRoom.getChannelRoom().getMembers());
                                                    realmRoom.getChannelRoom().setMembers(newMemberList);
                                                    newMemberList.clear();
                                                    break;
                                                }
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

                if (mMemberCount > 0) {
                    listMembersChannal.clear();
                    for (final ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : members) {
                        listMembersChannal.add(member);
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
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                mCurrentUpdateCount = 0;

                RealmMember.deleteAllMembers(mRoomID);
                if (roomType == GROUP) {
                    new RequestGroupGetMemberList().getMemberList(mRoomID, offset, limit, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.valueOf(selectedRole));
                } else {
                    new RequestChannelGetMemberList().channelGetMemberList(mRoomID, offset, limit, ProtoChannelGetMemberList.ChannelGetMemberList.FilterRole.valueOf(selectedRole));
                }
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
                    InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                RealmResults<RealmMember> searchMember = RealmMember.filterMember(mRoomID, s.toString());
                mAdapter = new MemberAdapter(searchMember, roomType, mMainRole, userID);
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fcm_recycler_view_show_member);
        mRecyclerView.setItemViewCacheSize(100);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));

        final PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(G.fragmentActivity, DeviceUtils.getScreenHeight(G.fragmentActivity));
        mRecyclerView.setLayoutManager(preCachingLayoutManager);

        progressBar = (ProgressBar) view.findViewById(R.id.fcg_prgWaiting);
        AppUtils.setProgresColler(progressBar);

        view.findViewById(R.id.fcg_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fcg_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                // G.fragmentActivity.getSupportFragmentManager().popBackStack();

                popBackStackFragment();

            }
        });

        //TextView txtNumberOfMember = (TextView) view.findViewById(R.id.fcg_txt_member);

        //if (selectedRole.toString().equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString())) {
        //    txtNumberOfMember.setText(G.fragmentActivity.getResources().getString(R.string.list_modereator));
        //} else if (selectedRole.toString().equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString())) {
        //    txtNumberOfMember.setText(G.fragmentActivity.getResources().getString(R.string.list_admin));
        //} else {
        //    txtNumberOfMember.setText(G.fragmentActivity.getResources().getString(member));
        //}

        scrollListener = new EndlessRecyclerViewScrollListener(preCachingLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                loadMoreMember();
            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);
    }

    private void loadMoreMember() {
        if (isOne) {
            isOne = false;
            mCurrentUpdateCount = 0;

            offset += limit;
            if (roomType == GROUP) {
                new RequestGroupGetMemberList().getMemberList(mRoomID, offset, limit, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.valueOf(selectedRole));
            } else {
                new RequestChannelGetMemberList().channelGetMemberList(mRoomID, offset, limit, ProtoChannelGetMemberList.ChannelGetMemberList.FilterRole.valueOf(selectedRole));
            }
        }
    }

    private void fillAdapter() {

        if (roomType == GROUP) {
            role = RealmGroupRoom.detectMineRole(mRoomID).toString();
        } else {
            role = RealmChannelRoom.detectMineRole(mRoomID).toString();
        }
        RealmResults<RealmMember> realmMembers = RealmMember.filterRole(mRoomID, roomType, selectedRole);

        if (realmMembers.size() > 0 && G.fragmentActivity != null) {
            mAdapter = new MemberAdapter(realmMembers, roomType, mMainRole, userID);
            mRecyclerView.setAdapter(mAdapter);

            //fastAdapter
            //mAdapter = new MemberAdapterA();
            //for (RealmMember member : mList) {
            //    mAdapter.add(new MemberItem(realmRoom.getType(), mMainRole, userID).setInfo(member).withIdentifier(member.getPeerId()));
            //}
        }
    }

    /**
     * *********************************** Callbacks ***********************************
     */

    @Override
    public void onGroupAddAdmin(final long roomId, final long memberId) {
        resetMemberState(roomId, memberId);
    }

    @Override
    public void onGroupAddModerator(long roomId, long memberId) {
        resetMemberState(roomId, memberId);
    }

    @Override
    public void onGroupKickAdmin(long roomId, long memberId) {
        resetMemberState(roomId, memberId);
    }

    @Override
    public void onGroupKickModerator(long roomId, long memberId) {
        resetMemberState(roomId, memberId);
    }

    @Override
    public void onGroupKickMember(long roomId, long memberId) {
        removeMember(roomId, memberId);
    }

    @Override
    public void onChannelAddAdmin(long roomId, long memberId) {
        resetMemberState(roomId, memberId);
    }

    @Override
    public void onChannelAddModerator(long roomId, long memberId) {
        resetMemberState(roomId, memberId);
    }

    @Override
    public void onChannelKickAdmin(long roomId, long memberId) {
        resetMemberState(roomId, memberId);
    }

    @Override
    public void onChannelKickModerator(long roomId, long memberId) {
        resetMemberState(roomId, memberId);
    }

    @Override
    public void onChannelKickMember(long roomId, long memberId) {
        removeMember(roomId, memberId);
    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void timeOut() {

    }

    @Override
    public void onTimeOut() {

    }

    private void resetMemberState(final long roomId, final long memberId) {
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        Realm realm = Realm.getDefaultInstance();
        //        RealmMember realmMember = realm.where(RealmMember.class).equalTo(RealmMemberFields.PEER_ID, memberId).findFirst();
        //        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        //        if (realmRoom != null && realmMember != null) {
        //            mAdapter.set(mAdapter.getPosition(memberId), new MemberItem(realmRoom.getType(), mMainRole, userID).setInfo(realmMember).withIdentifier(memberId));
        //        }
        //        realm.close();
        //    }
        //});
    }

    private void removeMember(long roomId, final long memberId) {
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        mAdapter.remove(mAdapter.getPosition(memberId));
        //    }
        //});
    }

    class AsyncMember extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            getMemberList();
            return null;
        }
    }

    /**
     * ***********************************************************************************
     * *********************************** FastAdapter ***********************************
     * ***********************************************************************************
     */

    //public class MemberAdapterA<Item extends MemberItem> extends FastItemAdapter<Item> {
    //    @Override
    //    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //        return super.onCreateViewHolder(parent, viewType);
    //    }
    //}
    //
    //class MemberItem extends AbstractItem<MemberItem, MemberItem.ViewHolder> {
    //    RealmMember realmMember;
    //    public String mainRole;
    //    public ProtoGlobal.Room.Type roomType;
    //    public long userId;
    //    private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();
    //
    //    public MemberItem(ProtoGlobal.Room.Type roomType, String mainRole, long userId) {
    //        this.roomType = roomType;
    //        this.mainRole = mainRole;
    //        this.userId = userId;
    //    }
    //
    //    public MemberItem setInfo(RealmMember realmMember) {
    //        this.realmMember = realmMember;
    //        return this;
    //    }
    //
    //    @Override
    //    public void bindView(final ViewHolder holder, List payloads) throws IllegalStateException {
    //        super.bindView(holder, payloads);
    //
    //        final StructContactInfo mContact = convertRealmToStruct(realmMember);
    //
    //        if (mContact == null) {
    //            return;
    //        }
    //
    //        holder.itemView.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                try {
    //                    HelperPermision.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
    //                        @Override
    //                        public void Allow() {
    //                            if (mContact.peerId == userID) {
    //                                new HelperFragment(new FragmentSetting()).setReplace(false).load();
    //                            } else {
    //                                new HelperFragment(FragmentContactsProfile.newInstance(mRoomID, mContact.peerId, GROUP.toString())).setReplace(false).load();
    //                            }
    //                        }
    //
    //                        @Override
    //                        public void deny() {
    //
    //                        }
    //                    });
    //                } catch (IOException e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //        });
    //
    //        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
    //            @Override
    //            public boolean onLongClick(View v) {
    //
    //                if (fragment instanceof FragmentGroupProfile) {
    //
    //                    if (role.equals(GroupChatRole.OWNER.toString())) {
    //
    //                        if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
    //
    //                            ((FragmentGroupProfile) fragment).kickMember(mContact.peerId);
    //                        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
    //
    //                            ((FragmentGroupProfile) fragment).kickAdmin(mContact.peerId);
    //                        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
    //
    //                            ((FragmentGroupProfile) fragment).kickModerator(mContact.peerId);
    //                        }
    //                    } else if (role.equals(GroupChatRole.ADMIN.toString())) {
    //
    //                        if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
    //                            ((FragmentGroupProfile) fragment).kickMember(mContact.peerId);
    //                        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
    //                            ((FragmentGroupProfile) fragment).kickModerator(mContact.peerId);
    //                        }
    //                    } else if (role.equals(GroupChatRole.MODERATOR.toString())) {
    //
    //                        if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
    //                            ((FragmentGroupProfile) fragment).kickMember(mContact.peerId);
    //                        }
    //                    }
    //                } else if (fragment instanceof FragmentChannelProfile) {
    //
    //                    if (role.equals(GroupChatRole.OWNER.toString())) {
    //
    //                        if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
    //
    //                            ((FragmentChannelProfile) fragment).kickMember(mContact.peerId);
    //                        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
    //
    //                            ((FragmentChannelProfile) fragment).kickAdmin(mContact.peerId);
    //                        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
    //
    //                            ((FragmentChannelProfile) fragment).kickModerator(mContact.peerId);
    //                        }
    //                    } else if (role.equals(GroupChatRole.ADMIN.toString())) {
    //
    //                        if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
    //                            ((FragmentChannelProfile) fragment).kickMember(mContact.peerId);
    //                        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
    //                            ((FragmentChannelProfile) fragment).kickModerator(mContact.peerId);
    //                        }
    //                    } else if (role.equals(GroupChatRole.MODERATOR.toString())) {
    //
    //                        if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
    //                            ((FragmentChannelProfile) fragment).kickMember(mContact.peerId);
    //                        }
    //                    }
    //                }
    //
    //                return true;
    //            }
    //        });
    //
    //        if (mContact.isHeader) {
    //            holder.topLine.setVisibility(View.VISIBLE);
    //        } else {
    //            holder.topLine.setVisibility(View.GONE);
    //        }
    //
    //        holder.title.setText(mContact.displayName);
    //
    //        setRoleStarColor(holder.roleStar, mContact);
    //
    //        hashMapAvatar.put(mContact.peerId, holder.image);
    //
    //        HelperAvatar.getAvatar(mContact.peerId, HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
    //            @Override
    //            public void onAvatarGet(String avatarPath, long userId) {
    //                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(userId));
    //            }
    //
    //            @Override
    //            public void onShowInitials(String initials, String color) {
    //                holder.image.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
    //            }
    //        });
    //
    //        if (mContact.status != null) {
    //            if (mContact.status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
    //                holder.subtitle.setText(LastSeenTimeUtil.computeTime(mContact.peerId, mContact.lastSeen, false));
    //            } else {
    //                holder.subtitle.setText(mContact.status);
    //            }
    //        }
    //
    //        if (mainRole.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
    //            holder.btnMenu.setVisibility(View.INVISIBLE);
    //        } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
    //
    //            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString()) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString()))) {
    //                holder.btnMenu.setVisibility(View.INVISIBLE);
    //            } else {
    //                showPopup(holder, mContact);
    //            }
    //        } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
    //
    //            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString()) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString()))) {
    //                holder.btnMenu.setVisibility(View.INVISIBLE);
    //            } else {
    //                showPopup(holder, mContact);
    //            }
    //        } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
    //            showPopup(holder, mContact);
    //        }
    //
    //        /**
    //         * don't allow for use dialog if this item
    //         * is for own user
    //         */
    //        if (mContact.peerId == mContact.userID) {
    //            holder.btnMenu.setVisibility(View.INVISIBLE);
    //        }
    //    }
    //
    //    @Override
    //    public int getType() {
    //        return 0;
    //    }
    //
    //    @Override
    //    public int getLayoutRes() {
    //        return R.layout.contact_item_group_profile;
    //    }
    //
    //    @Override
    //    public ViewHolder getViewHolder(View viewGroup) {
    //        return new ViewHolder(viewGroup);
    //    }
    //
    //    public class ViewHolder extends RecyclerView.ViewHolder {
    //
    //        protected CircleImageView image;
    //        protected CustomTextViewMedium title;
    //        protected CustomTextViewMedium subtitle;
    //        protected View topLine;
    //        protected TextView txtNumberOfSharedMedia;
    //        protected MaterialDesignTextView roleStar;
    //        protected MaterialDesignTextView btnMenu;
    //
    //        public ViewHolder(View itemView) {
    //            super(itemView);
    //
    //            image = (CircleImageView) itemView.findViewById(R.id.cigp_imv_contact_avatar);
    //            title = (CustomTextViewMedium) itemView.findViewById(R.id.cigp_txt_contact_name);
    //            subtitle = (CustomTextViewMedium) itemView.findViewById(R.id.cigp_txt_contact_lastseen);
    //            topLine = itemView.findViewById(R.id.cigp_view_topLine);
    //            txtNumberOfSharedMedia = (TextView) itemView.findViewById(R.id.cigp_txt_nomber_of_shared_media);
    //            roleStar = (MaterialDesignTextView) itemView.findViewById(R.id.cigp_txt_member_role);
    //            btnMenu = (MaterialDesignTextView) itemView.findViewById(R.id.cigp_moreButton);
    //        }
    //    }
    //
    //    private void showPopup(ViewHolder holder, final StructContactInfo mContact) {
    //        holder.btnMenu.setVisibility(View.VISIBLE);
    //
    //        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //
    //                if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
    //                    if (FragmentChannelProfile.onMenuClick != null) {
    //                        FragmentChannelProfile.onMenuClick.clicked(v, mContact);
    //                    }
    //                } else if (roomType == GROUP) {
    //                    if (FragmentGroupProfile.onMenuClick != null) {
    //                        FragmentGroupProfile.onMenuClick.clicked(v, mContact);
    //                    }
    //                }
    //            }
    //        });
    //    }
    //
    //    private void setRoleStarColor(MaterialDesignTextView view, StructContactInfo mContact) {
    //
    //        view.setVisibility(View.GONE);
    //
    //        if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
    //            view.setVisibility(View.VISIBLE);
    //            view.setTextColor(Color.CYAN);
    //        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
    //            view.setVisibility(View.VISIBLE);
    //            view.setTextColor(Color.GREEN);
    //        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
    //            view.setVisibility(View.VISIBLE);
    //            view.setTextColor(Color.BLUE);
    //        }
    //    }
    //
    //    StructContactInfo convertRealmToStruct(RealmMember realmMember) {
    //        Realm realm = Realm.getDefaultInstance();
    //        String role = realmMember.getRole();
    //        long id = realmMember.getPeerId();
    //        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, id).findFirst();
    //        if (realmRegisteredInfo != null) {
    //            StructContactInfo s = new StructContactInfo(realmRegisteredInfo.getId(), realmRegisteredInfo.getDisplayName(), realmRegisteredInfo.getStatus(), false, false, realmRegisteredInfo.getPhoneNumber() + "");
    //            s.role = role;
    //            s.avatar = realmRegisteredInfo.getLastAvatar();
    //            s.initials = realmRegisteredInfo.getInitials();
    //            s.color = realmRegisteredInfo.getColor();
    //            s.lastSeen = realmRegisteredInfo.getLastSeen();
    //            s.status = realmRegisteredInfo.getStatus();
    //            s.userID = userId;
    //            return s;
    //        }
    //
    //        realm.close();
    //        return null;
    //    }
    //}

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    private class MemberAdapter extends RealmRecyclerViewAdapter<RealmMember, MemberAdapter.ViewHolder> {

        public String mainRole;
        public ProtoGlobal.Room.Type roomType;
        public long userId;
        private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();

        public MemberAdapter(RealmResults<RealmMember> realmResults, ProtoGlobal.Room.Type roomType, String mainRole, long userId) {
            super(realmResults, true);
            this.roomType = roomType;
            this.mainRole = mainRole;
            this.userId = userId;
        }

        @Override
        public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.contact_item_group_profile, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final MemberAdapter.ViewHolder holder, int i) {

            final StructContactInfo mContact = convertRealmToStruct(getItem(i));

            if (mContact == null) {
                return;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                if (mContact.peerId == userID) {
                                    new HelperFragment(new FragmentSetting()).setReplace(false).load();
                                } else {
                                    new HelperFragment(FragmentContactsProfile.newInstance(mRoomID, mContact.peerId, GROUP.toString())).setReplace(false).load();
                                }
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

                    if (fragment instanceof FragmentGroupProfile) {

                        if (role.equals(GroupChatRole.OWNER.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {

                                ((FragmentGroupProfile) fragment).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                                ((FragmentGroupProfile) fragment).kickAdmin(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                                ((FragmentGroupProfile) fragment).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.ADMIN.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((FragmentGroupProfile) fragment).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                                ((FragmentGroupProfile) fragment).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.MODERATOR.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((FragmentGroupProfile) fragment).kickMember(mContact.peerId);
                            }
                        }
                    } else if (fragment instanceof FragmentChannelProfile) {

                        if (role.equals(GroupChatRole.OWNER.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {

                                ((FragmentChannelProfile) fragment).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                                ((FragmentChannelProfile) fragment).kickAdmin(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                                ((FragmentChannelProfile) fragment).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.ADMIN.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((FragmentChannelProfile) fragment).kickMember(mContact.peerId);
                            } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                                ((FragmentChannelProfile) fragment).kickModerator(mContact.peerId);
                            }
                        } else if (role.equals(GroupChatRole.MODERATOR.toString())) {

                            if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                                ((FragmentChannelProfile) fragment).kickMember(mContact.peerId);
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
            holder.title.setTextColor(Color.parseColor(G.textTitleTheme));
            holder.subtitle.setTextColor(Color.parseColor(G.textSubTheme));
            holder.btnMenu.setTextColor(Color.parseColor(G.textSubTheme));
            holder.txtNumberOfSharedMedia.setTextColor(Color.parseColor(G.textSubTheme));
            holder.topLine.setBackgroundColor(Color.parseColor(G.textSubTheme));

            setRoleStarColor(holder.roleStar, mContact);

            hashMapAvatar.put(mContact.peerId, holder.image);

            HelperAvatar.getAvatar(mContact.peerId, HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
                @Override
                public void onAvatarGet(String avatarPath, long userId) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(userId));
                }

                @Override
                public void onShowInitials(String initials, String color) {
                    //CircleImageView imageView;
                    //if (hashMapAvatar.get(userId) != null) {
                    //    imageView = hashMapAvatar.get(userId);
                    //} else {
                    //    imageView = holder.image;
                    //}
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
                holder.btnMenu.setVisibility(View.INVISIBLE);
            } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString()) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString()))) {
                    holder.btnMenu.setVisibility(View.INVISIBLE);
                } else {
                    showPopup(holder, mContact);
                }
            } else if (mainRole.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString()) || (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString()))) {
                    holder.btnMenu.setVisibility(View.INVISIBLE);
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
                holder.btnMenu.setVisibility(View.INVISIBLE);
            }
        }

        private void showPopup(ViewHolder holder, final StructContactInfo mContact) {
            holder.btnMenu.setVisibility(View.VISIBLE);

            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
                        if (FragmentChannelProfileViewModel.onMenuClick != null) {
                            FragmentChannelProfileViewModel.onMenuClick.clicked(v, mContact);
                        }
                    } else if (roomType == GROUP) {
                        if (FragmentGroupProfileViewModel.onMenuClick != null) {
                            FragmentGroupProfileViewModel.onMenuClick.clicked(v, mContact);
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
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, id);
            if (realmRegisteredInfo != null) {
                StructContactInfo s = new StructContactInfo(realmRegisteredInfo.getId(), realmRegisteredInfo.getDisplayName(), realmRegisteredInfo.getStatus(), false, false, realmRegisteredInfo.getPhoneNumber() + "");
                s.role = role;
                s.avatar = realmRegisteredInfo.getLastAvatar();
                s.initials = realmRegisteredInfo.getInitials();
                s.color = realmRegisteredInfo.getColor();
                s.lastSeen = realmRegisteredInfo.getLastSeen();
                s.status = realmRegisteredInfo.getStatus();
                s.userID = userId;
                return s;
            }

            realm.close();
            return null;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

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
    }
}
