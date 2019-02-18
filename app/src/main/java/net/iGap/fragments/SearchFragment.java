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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.SearchItem;
import net.iGap.adapter.items.SearchItemHeader;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.IClientSearchUserName;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.proto.ProtoClientSearchUsername;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestClientSearchUsername;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

import static net.iGap.fragments.FragmentChat.messageId;

public class SearchFragment extends BaseFragment {

    public static HashMap<Long, CircleImageView> hashMapAvatarSearchFragment = new HashMap<>();
    MaterialDesignTextView btnClose;
    RippleView rippleDown;
    private FastAdapter fastAdapter;
    private EditText edtSearch;
    private ArrayList<StructSearch> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ImageView imvNothingFound;
    private TextView txtEmptyListComment;
    private long index = 500;
    private String preventRepeatSearch = "";
    private ContentLoadingProgressBar loadingProgressBar;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.search_fragment_layout, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponent(view);
        initRecycleView();
    }

    private void initComponent(View view) {

        index = 500;
        view.findViewById(R.id.sfl_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        loadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.sfl_progress_loading);
        imvNothingFound = (ImageView) view.findViewById(R.id.sfl_imv_nothing_found);
        imvNothingFound.setImageResource(R.drawable.find1);
        txtEmptyListComment = (TextView) view.findViewById(R.id.sfl_txt_empty_list_comment);
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imvNothingFound.setVisibility(View.VISIBLE);
                txtEmptyListComment.setVisibility(View.VISIBLE);
            }
        }, 150);


        txtEmptyListComment.setText(R.string.empty_message3);

        edtSearch = (EditText) view.findViewById(R.id.sfl_edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                fillList(editable.toString());


                if (editable.length() > 0) {
                    btnClose.setTextColor(Color.WHITE);
                    ((View) rippleDown).setEnabled(true);

                } else {
                    btnClose.setTextColor(G.context.getResources().getColor(R.color.colorChatMessageSelectableItemBg));
                    ((View) rippleDown).setEnabled(false);

                }
            }
        });
        edtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.sfl_btn_back);
        final RippleView rippleBack = (RippleView) view.findViewById(R.id.sfl_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleBack.getWindowToken(), 0);
                G.fragmentActivity.onBackPressed();
            }
        });

        btnClose = (MaterialDesignTextView) view.findViewById(R.id.sfl_btn_close);
        rippleDown = (RippleView) view.findViewById(R.id.sfl_ripple_done);
        ((View) rippleDown).setEnabled(false);
        rippleDown.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                edtSearch.setText("");
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.sfl_recycleview);
    }

    private void initRecycleView() {

        itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);

        fastAdapter.withOnClickListener(new OnClickListener<IItem>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem currentItem, int position) {

                if (currentItem instanceof SearchItemHeader) {

                } else {
                    SearchItem si = (SearchItem) currentItem;
                    goToRoom(si.item.id, si.item.type, si.item.messageId, si.item.userName);

                    InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                }

                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastAdapter);
    }

    private void fillList(String text) {

        itemAdapter.clear();
        fastAdapter.clearTypeInstance();

        int strSize = text.length();


        if (strSize < 3) {

            txtEmptyListComment.setVisibility(View.VISIBLE);
            txtEmptyListComment.setText(R.string.empty_message3);
            imvNothingFound.setVisibility(View.VISIBLE);
            return;

        }

        if (text.startsWith("#")) {
            fillListItemHashtag(text);
            return;
        } else if (Character.getDirectionality(text.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
            fillListItemGlobal(text);
            return;
        }


        if (strSize >= 5) {
            if (G.userLogin) {
                if ((!text.equals(preventRepeatSearch))) {
                    itemAdapter.clear();
                    if (text.startsWith("@")) {
                        new RequestClientSearchUsername().clientSearchUsername(text.substring(1));

                    } else {
                        new RequestClientSearchUsername().clientSearchUsername(text);
                    }
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    preventRepeatSearch = text;
                }
            } else {
                HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
            }
        }else {
            preventRepeatSearch = "";
        }


        G.onClientSearchUserName = new IClientSearchUserName() {
            @Override
            public void OnGetList(final ProtoClientSearchUsername.ClientSearchUsernameResponse.Builder builderList) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        loadingProgressBar.setVisibility(View.GONE);

                        Realm realm = Realm.getDefaultInstance();

                        for (final ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item : builderList.getResultList()) {

                            if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.USER) {

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRegisteredInfo.putOrUpdate(realm, item.getUser());
                                    }
                                });

                            } else if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.ROOM) {

                                final RealmRoom[] realmRoom = {realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, item.getRoom().getId()).findFirst()};

                                if (realmRoom[0] == null) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realmRoom[0] = RealmRoom.putOrUpdate(item.getRoom(), realm);
                                            realmRoom[0].setDeleted(true);
                                        }
                                    });
                                }
                            }
                        }
                        realm.close();

                        String text = edtSearch.getText().toString();
                        if (text.startsWith("@")) {
                            fillListItemAtsign(text.substring(1));

                        } else {
                            fillListItemGlobal(text);

                        }
                    }
                });
            }

            @Override
            public void OnErrore() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        };

    }

    private void fillListItemHashtag(String text) {

        list.clear();
        fillHashtag(text);
        updateAdapter();

    }


    private void fillListItemAtsign(String text) {

        list.clear();
        fillBot(text);
        fillChat(text);
        fillRoomListGroup(text);
        fillRoomListChannel(text);
        updateAdapter();

    }


    private void fillListItemGlobal(String text) {

        list.clear();

        fillBot(text);

        fillChat(text);

        fillRoomListGroup(text);

        fillRoomListChannel(text);

        fillMessages(text);

        fillHashtag(text);

        updateAdapter();

    }

    private void fillHashtag(String text) {

        Realm realm = Realm.getDefaultInstance();

        if (!text.startsWith("#")) {
            text = "#" + text;
        }
        final RealmResults<RealmRoomMessage> results = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.HAS_MESSAGE_LINK, true).contains(RealmRoomMessageFields.MESSAGE, text, Case.INSENSITIVE).equalTo(RealmRoomMessageFields.EDITED, false).isNotEmpty(RealmRoomMessageFields.MESSAGE).findAll();

        if (results != null && results.size() > 0) {

            addHeader(G.fragmentActivity.getResources().getString(R.string.hashtag));
            for (RealmRoomMessage roomMessage : results) {


                StructSearch item = new StructSearch();

                item.time = roomMessage.getUpdateTime();
                item.comment = roomMessage.getMessage();
                item.id = roomMessage.getRoomId();
                item.type = SearchType.message;
                item.messageId = roomMessage.getMessageId();

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();

                if (realmRoom != null) { // room exist
                    item.name = realmRoom.getTitle();
                    item.initials = realmRoom.getInitials();
                    item.color = realmRoom.getColor();
                    item.roomType = realmRoom.getType();
                    item.avatar = realmRoom.getAvatar();
                    if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT && realmRoom.getChatRoom() != null) {
                        item.idDetectAvatar = realmRoom.getChatRoom().getPeerId();
                    } else {
                        item.idDetectAvatar = realmRoom.getId();
                    }
                    list.add(item);
                }
            }
        }

        realm.close();

    }


    private void updateAdapter() {
        List<IItem> items = new ArrayList<>();
        for (StructSearch item : list) {
            if (item != null) {
                if (item.type == SearchType.header) {
                    items.add(new SearchItemHeader().setText(item.name).withIdentifier(index++));
                } else {
                    items.add(new SearchItem().setContact(item).withIdentifier(index++));
                }
            }
        }

        if (items.size() > 0) {
            txtEmptyListComment.setVisibility(View.GONE);
            imvNothingFound.setVisibility(View.GONE);
        } else {
            txtEmptyListComment.setVisibility(View.VISIBLE);
            txtEmptyListComment.setText(R.string.there_is_no_any_result);
            imvNothingFound.setVisibility(View.VISIBLE);
        }

        itemAdapter.clear();
        itemAdapter.add(items);
    }

    private void fillRoomListGroup(String text) {

        Realm realm = Realm.getDefaultInstance();

        final RealmResults<RealmRoom> results;

        if (edtSearch.getText().toString().startsWith("@")) {
            results = realm.where(RealmRoom.class).beginGroup().contains(RealmRoomFields.CHANNEL_ROOM.USERNAME, text, Case.INSENSITIVE).or().contains(RealmRoomFields.GROUP_ROOM.USERNAME, text, Case.INSENSITIVE).endGroup().equalTo(RealmRoomFields.TYPE, "GROUP", Case.INSENSITIVE).findAll();

        } else {
            results = realm.where(RealmRoom.class).beginGroup().contains(RealmRoomFields.TITLE, text, Case.INSENSITIVE).or().contains(RealmRoomFields.CHANNEL_ROOM.USERNAME, text, Case.INSENSITIVE).or().contains(RealmRoomFields.GROUP_ROOM.USERNAME, text, Case.INSENSITIVE).endGroup().equalTo(RealmRoomFields.TYPE, "GROUP", Case.INSENSITIVE).findAll();
        }

        if (results != null) {

            if (results.size() > 0)
                addHeader(G.fragmentActivity.getResources().getString(R.string.Groups));


            for (RealmRoom realmRoom : results) {

                StructSearch item = new StructSearch();

                item.roomType = realmRoom.getType();
                item.name = realmRoom.getTitle();
                item.time = realmRoom.getUpdatedTime();
                item.id = realmRoom.getId();
                if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT && realmRoom.getChatRoom() != null) {
                    item.idDetectAvatar = realmRoom.getChatRoom().getPeerId();
                } else {

                    if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP && realmRoom.getGroupRoom() != null) {
                        item.userName = realmRoom.getGroupRoom().getUsername();

                    } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom() != null) {
                        item.userName = realmRoom.getChannelRoom().getUsername();

                    }

                    item.idDetectAvatar = realmRoom.getId();
                }
                item.type = SearchType.room;
                item.initials = realmRoom.getInitials();
                item.color = realmRoom.getColor();
                item.avatar = realmRoom.getAvatar();

                list.add(item);
            }
        }

        realm.close();
    }

    private void fillRoomListChannel(String text) {

        Realm realm = Realm.getDefaultInstance();

        final RealmResults<RealmRoom> results;

        if (edtSearch.getText().toString().startsWith("@")) {
            results = realm.where(RealmRoom.class).beginGroup().contains(RealmRoomFields.CHANNEL_ROOM.USERNAME, text, Case.INSENSITIVE).or().contains(RealmRoomFields.GROUP_ROOM.USERNAME, text, Case.INSENSITIVE).endGroup().equalTo(RealmRoomFields.TYPE, "CHANNEL", Case.INSENSITIVE).findAll();

        } else {
            results = realm.where(RealmRoom.class).beginGroup().contains(RealmRoomFields.TITLE, text, Case.INSENSITIVE).or().contains(RealmRoomFields.CHANNEL_ROOM.USERNAME, text, Case.INSENSITIVE).or().contains(RealmRoomFields.GROUP_ROOM.USERNAME, text, Case.INSENSITIVE).endGroup().equalTo(RealmRoomFields.TYPE, "CHANNEL", Case.INSENSITIVE).findAll();
        }

        if (results != null && results.size() > 0) {

            addHeader(G.fragmentActivity.getResources().getString(R.string.channel));

            for (RealmRoom realmRoom : results) {

                StructSearch item = new StructSearch();

                item.roomType = realmRoom.getType();
                item.name = realmRoom.getTitle();
                item.time = realmRoom.getUpdatedTime();
                item.id = realmRoom.getId();
                if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT && realmRoom.getChatRoom() != null) {
                    item.idDetectAvatar = realmRoom.getChatRoom().getPeerId();
                } else {

                    if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP && realmRoom.getGroupRoom() != null) {
                        item.userName = realmRoom.getGroupRoom().getUsername();

                    } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom() != null) {
                        item.userName = realmRoom.getChannelRoom().getUsername();

                    }

                    item.idDetectAvatar = realmRoom.getId();
                }
                item.type = SearchType.room;
                item.initials = realmRoom.getInitials();
                item.color = realmRoom.getColor();
                item.avatar = realmRoom.getAvatar();

                list.add(item);
            }
        }

        realm.close();
    }

    private void fillBot(String text) {

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmRegisteredInfo> results;

        if (edtSearch.getText().toString().startsWith("@")) {
            results = realm.where(RealmRegisteredInfo.class).contains(RealmRegisteredInfoFields.USERNAME, text, Case.INSENSITIVE).equalTo(RealmRegisteredInfoFields.IS_BOT, true).findAll();

        } else {
            results = realm.where(RealmRegisteredInfo.class).beginGroup().contains(RealmRegisteredInfoFields.USERNAME, text, Case.INSENSITIVE).or().contains(RealmRegisteredInfoFields.DISPLAY_NAME, text).endGroup().equalTo(RealmRegisteredInfoFields.IS_BOT, true).findAll();
        }

        if (results != null && results.size() > 0) {

            addHeader(G.fragmentActivity.getResources().getString(R.string.bot));

            for (RealmRegisteredInfo contact : results) {


                StructSearch item = new StructSearch();

                item.name = contact.getDisplayName();
                item.time = contact.getLastSeen();
                item.userName = contact.getUsername();
                item.comment = "";
                item.id = contact.getId();
                item.idDetectAvatar = contact.getId();
                item.type = SearchType.contact;
                item.initials = contact.getInitials();
                item.color = contact.getColor();
                item.avatar = contact.getLastAvatar();
                list.add(item);
            }
        }

        realm.close();
    }

    private void fillChat(String text) {

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmRegisteredInfo> results;

        if (edtSearch.getText().toString().startsWith("@")) {
            results = realm.where(RealmRegisteredInfo.class).contains(RealmRegisteredInfoFields.USERNAME, text, Case.INSENSITIVE).equalTo(RealmRegisteredInfoFields.IS_BOT, false).findAll();

        } else {
            results = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.IS_BOT, false).beginGroup().contains(RealmRegisteredInfoFields.USERNAME, text, Case.INSENSITIVE).or().contains(RealmRegisteredInfoFields.DISPLAY_NAME, text).endGroup().findAll();
        }

        if (results != null && results.size() > 0) {

            addHeader(G.fragmentActivity.getResources().getString(R.string.member));

            for (RealmRegisteredInfo contact : results) {


                StructSearch item = new StructSearch();

                item.name = contact.getDisplayName();
                item.time = contact.getLastSeen();
                item.userName = contact.getUsername();
                item.comment = "";
                item.id = contact.getId();
                item.idDetectAvatar = contact.getId();
                item.type = SearchType.contact;
                item.initials = contact.getInitials();
                item.color = contact.getColor();
                item.avatar = contact.getLastAvatar();
                list.add(item);
            }
        }

        realm.close();
    }

    private void fillContacts(String text) {
        int size = list.size();

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmContacts> results;

        if (edtSearch.getText().toString().startsWith("@")) {
            results = realm.where(RealmContacts.class).contains(RealmContactsFields.USERNAME, text, Case.INSENSITIVE).findAll();

        } else {
            results = realm.where(RealmContacts.class).beginGroup().contains(RealmContactsFields.USERNAME, text, Case.INSENSITIVE).or().contains(RealmContactsFields.DISPLAY_NAME, text).endGroup().findAll();
        }

        if (results != null) {

            for (RealmContacts contact : results) {
                Long phone = contact.getPhone();
                String str = phone.toString().replaceAll(" ", "");
                if (str.length() > 10) {
                    str = str.substring(str.length() - 10, str.length());
                }

                StructSearch item = new StructSearch();

                item.name = contact.getDisplay_name();
                item.time = contact.getLast_seen();
                item.comment = str;
                item.userName = contact.getUsername();
                item.id = contact.getId();
                item.idDetectAvatar = contact.getId();
                item.type = SearchType.contact;
                item.initials = contact.getInitials();
                item.color = contact.getColor();
                item.avatar = contact.getAvatar();
                list.add(item);
            }
        }

//        if (size == list.size()) list.remove(size - 1);
        realm.close();
    }

    private void addHeader(String header) {
        StructSearch item = new StructSearch();
        item.name = header;
        item.type = SearchType.header;
        list.add(item);
    }

    private void fillMessages(String text) {

        int size = list.size();
        Realm realm = Realm.getDefaultInstance();

        final RealmResults<RealmRoomMessage> results = realm.where(RealmRoomMessage.class).contains(RealmRoomMessageFields.MESSAGE, text, Case.INSENSITIVE).equalTo(RealmRoomMessageFields.EDITED, false).isNotEmpty(RealmRoomMessageFields.MESSAGE).findAll();
        if (results != null && results.size() > 0) {
            addHeader(G.fragmentActivity.getResources().getString(R.string.messages));
            for (RealmRoomMessage roomMessage : results) {

                StructSearch item = new StructSearch();

                item.time = roomMessage.getUpdateTime();
                item.comment = roomMessage.getMessage();
                item.id = roomMessage.getRoomId();
                item.type = SearchType.message;
                item.messageId = roomMessage.getMessageId();

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();

                if (realmRoom != null) { // room exist
                    item.name = realmRoom.getTitle();
                    item.initials = realmRoom.getInitials();
                    item.color = realmRoom.getColor();
                    item.roomType = realmRoom.getType();
                    item.avatar = realmRoom.getAvatar();
                    if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT && realmRoom.getChatRoom() != null) {
                        item.idDetectAvatar = realmRoom.getChatRoom().getPeerId();
                    } else {

                        if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP && realmRoom.getGroupRoom() != null) {
                            item.userName = realmRoom.getGroupRoom().getUsername();

                        } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom() != null) {
                            item.userName = realmRoom.getChannelRoom().getUsername();

                        }

                        item.idDetectAvatar = realmRoom.getId();
                    }
                    list.add(item);
                }
            }
        }

//        if (size == list.size()) {
//            list.remove(size - 1);
//        }

        realm.close();
    }

    private void goToRoom(final long id, SearchType type, long messageId, String userName) {

        final Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = null;

        if (type == SearchType.message) {
            realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst();
            goToRoomWithRealm(realmRoom, type, id);
        } else if (type == SearchType.contact) {
            realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, id).findFirst();
            goToRoomWithRealm(realmRoom, type, id);
        } else if (type == SearchType.room) {

            HelperUrl.checkUsernameAndGoToRoom(userName, HelperUrl.ChatEntry.profile);
            popBackStackFragment();

        }
        realm.close();

    }


    public void goToRoomWithRealm(RealmRoom realmRoom, SearchType type, long id) {

        if (realmRoom != null) {
            removeFromBaseFragment(SearchFragment.this);
            if (type == SearchType.message) {
                new GoToChatActivity(realmRoom.getId()).setMessageID(messageId).startActivity();
            } else {
                new GoToChatActivity(realmRoom.getId()).startActivity();
            }

        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final ProtoGlobal.Room room) {
                    RealmRoom.putOrUpdate(room);
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (G.fragmentActivity != null) {
                                removeFromBaseFragment(SearchFragment.this);
                            }
                            new GoToChatActivity(room.getId()).setPeerID(id).startActivity();
                            G.onChatGetRoom = null;
                        }
                    });
                }

                @Override
                public void onChatGetRoomTimeOut() {

                }

                @Override
                public void onChatGetRoomError(int majorCode, int minorCode) {

                }
            };

            new RequestChatGetRoom().chatGetRoom(id);
        }


    }


    //*********************************************************************************************

    public enum SearchType {
        header, room, contact, message, CHANNEL, GROUP
    }

    public class StructSearch {
        public String name = "";
        public String comment = "";
        public String userName = "";
        public String initials;
        public String color;
        public long time = 0;
        public long id = 0;
        public long idDetectAvatar = 0; // fill roomId for rooms and userId for contacts
        public long messageId = 0;
        public RealmAvatar avatar;
        public ProtoGlobal.Room.Type roomType;
        public SearchType type = SearchType.header;
    }
}
