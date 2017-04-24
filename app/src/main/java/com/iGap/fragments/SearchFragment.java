/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.adapter.items.SearchItem;
import com.iGap.adapter.items.SearchItemHeader;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.request.RequestChatGetRoom;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FastAdapter fastAdapter;
    private EditText edtSearch;
    MaterialDesignTextView btnClose;
    RippleView rippleDown;
    private ArrayList<StructSearch> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private boolean isFillList = false;
    private boolean chatHeaderGone = true;
    private boolean contactHeaderGone = true;
    private boolean messageHeaderGone = true;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponent(view);
        initRecycleView();
    }

    private void initComponent(View view) {

        view.findViewById(R.id.sfl_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.sfl_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));


        edtSearch = (EditText) view.findViewById(R.id.sfl_edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == 1 && !isFillList) fillList();

                itemAdapter.filter(charSequence);

                if (charSequence.length() > 0) {
                    btnClose.setTextColor(Color.WHITE);
                    ((View) rippleDown).setEnabled(true);

                } else {
                    btnClose.setTextColor(getResources().getColor(R.color.colorChatMessageSelectableItemBg));
                    ((View) rippleDown).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        chatHeaderGone = contactHeaderGone = messageHeaderGone = true;

                        for (int i = 0; i < itemAdapter.getAdapterItemCount(); i++) {
                            if (itemAdapter.getItem(i) instanceof SearchItem) {
                                SearchItem s = (SearchItem) itemAdapter.getItem(i);
                                if (s.item.type == SearchType.room) {
                                    chatHeaderGone = false;
                                } else if (s.item.type == SearchType.contact) {
                                    contactHeaderGone = false;
                                } else if (s.item.type == SearchType.message) {
                                    messageHeaderGone = false;
                                }
                            }
                        }

                        itemAdapter.filter(edtSearch.getText().toString());
                    }
                }, 200);
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

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleBack.getWindowToken(), 0);
                getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();
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

        fastAdapter = new FastAdapter();
        itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<IItem>() {
            @Override
            public boolean filter(IItem currentItem, CharSequence constraint) {

                try {
                    if (currentItem instanceof SearchItemHeader) {
                        SearchItemHeader sih = (SearchItemHeader) currentItem;
                        if (sih.text.equals(getString(R.string.chats)) && chatHeaderGone) {
                            return true;
                        } else if (sih.text.equals(getString(R.string.contacts)) && contactHeaderGone) {
                            return true;
                        } else if (sih.text.equals(getString(R.string.messages)) && messageHeaderGone) {
                            return true;
                        }

                        return false;
                    } else {
                        SearchItem si = (SearchItem) currentItem;

                        if (si.item.type == SearchType.message) {
                            return !si.item.comment.toLowerCase().contains(String.valueOf(constraint).toLowerCase());
                        } else {
                            return !si.item.name.toLowerCase().contains(String.valueOf(constraint).toLowerCase());
                        }
                    }
                } catch (Exception e) {
                    return true;
                }

            }
        });

        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<IItem>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem currentItem, int position) {

                if (currentItem instanceof SearchItemHeader) {

                } else {
                    SearchItem si = (SearchItem) currentItem;
                    goToRoom(si.item.id, si.item.type, si.item.messageId);

                    InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                }

                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter.wrap(fastAdapter));
    }

    private void fillList() {

        list.clear();
        addHeader(getString(R.string.chats));
        fillRoomList();
        addHeader(getString(R.string.contacts));
        fillContacts();
        addHeader(getString(R.string.messages));
        fillMessages();

        List<IItem> items = new ArrayList<>();

        for (StructSearch item : list) {
            if (item != null) {
                if (item.type == SearchType.header) {
                    items.add(new SearchItemHeader().setText(item.name).withIdentifier(100 + list.indexOf(item)));
                } else {
                    items.add(new SearchItem().setContact(item).withIdentifier(100 + list.indexOf(item)));
                }
            }
        }

        itemAdapter.add(items);

        isFillList = true;
    }

    private void fillRoomList() {

        Realm realm = Realm.getDefaultInstance();

        int size = list.size();

        for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll()) {
            StructSearch item = new StructSearch();

            item.roomType = realmRoom.getType();
            item.name = realmRoom.getTitle();
            item.time = realmRoom.getUpdatedTime();
            item.id = realmRoom.getId();
            item.type = SearchType.room;
            item.initials = realmRoom.getInitials();
            item.color = realmRoom.getColor();
            item.avatar = realmRoom.getAvatar();

            list.add(item);
        }

        if (size == list.size()) list.remove(size - 1);

        realm.close();
    }

    private void fillContacts() {
        int size = list.size();

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmContacts> results = realm.where(RealmContacts.class).findAll();
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
                item.id = contact.getId();
                item.type = SearchType.contact;
                item.initials = contact.getInitials();
                item.color = contact.getColor();
                item.avatar = contact.getAvatar();
                list.add(item);
            }
        }

        if (size == list.size()) list.remove(size - 1);
        realm.close();
    }

    private void addHeader(String header) {
        StructSearch item = new StructSearch();
        item.name = header;
        item.type = SearchType.header;
        list.add(item);
    }

    private void fillMessages() {

        int size = list.size();
        Realm realm = Realm.getDefaultInstance();

        for (RealmRoomMessage roomMessage : realm.where(RealmRoomMessage.class).findAll()) {
            if (roomMessage != null) {

                if (roomMessage.getMessage() == null) continue;
                if (roomMessage.getMessage().length() < 1) continue;

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
                }

                list.add(item);
            }
        }

        if (size == list.size()) list.remove(size - 1);
    }

    private void goToRoom(final long id, SearchType type, long messageId) {

        final Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = null;

        if (type == SearchType.room || type == SearchType.message) {
            realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst();
        } else if (type == SearchType.contact) {
            realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, id).findFirst();
        }

        if (realmRoom != null) {
            Intent intent = new Intent(G.context, ActivityChat.class);

            if (type == SearchType.message) intent.putExtra("MessageId", messageId);

            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            G.context.startActivity(intent);
            getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();
        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final long roomId) {
                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Realm realm = Realm.getDefaultInstance();
                            Intent intent = new Intent(G.context, ActivityChat.class);
                            intent.putExtra("peerId", id);
                            intent.putExtra("RoomId", roomId);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            realm.close();
                            G.context.startActivity(intent);
                            if (getActivity() != null) {
                                getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();
                            }
                        }
                    });
                }

                @Override
                public void onChatGetRoomCompletely(ProtoGlobal.Room room) {

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
        realm.close();
    }

    //*********************************************************************************************

    public enum SearchType {
        header,
        room,
        contact,
        message;
    }

    public class StructSearch {
        public String name = "";
        public String comment = "";
        public String initials;
        public String color;
        public long time = 0;
        public long id = 0;
        public long messageId = 0;
        public RealmAvatar avatar;
        public ProtoGlobal.Room.Type roomType;
        public SearchType type = SearchType.header;
    }
}
