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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.adapter.StickyHeaderAdapter;
import com.iGap.adapter.items.ContactItem;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.Contacts;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.structs.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.request.RequestChatGetRoom;
import com.iGap.request.RequestUserContactsGetList;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.iGap.G.context;

public class RegisteredContactsFragment extends Fragment {
    private FastAdapter fastAdapter;
    private TextView menu_txt_titleToolbar;
    private ViewGroup vgAddContact, vgRoot;
    private List<StructContactInfo> contacts;
    private RecyclerView rv;
    private SharedPreferences sharedPreferences;
    private boolean isImportContactList = false;
    private ProgressBar prgWaiting;
    private ItemAdapter itemAdapter;
    private List<IItem> items;
    private FragmentActivity mActivity;
    private StickyRecyclerHeadersDecoration decoration;
    private StickyHeaderAdapter stickyHeaderAdapter;
    private EditText edtSearch;

    private Realm mRealm;
    RealmChangeListener<RealmResults<RealmContacts>> contactsChangeListener;
    RealmResults<RealmContacts> realmContacts;

    @Override
    public void onResume() {
        super.onResume();

        if (realmContacts != null) {
            if (contactsChangeListener != null) {
                realmContacts.addChangeListener(contactsChangeListener);
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (realmContacts != null) realmContacts.removeChangeListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRealm != null) {
            mRealm.close();
        }
    }

    public static RegisteredContactsFragment newInstance() {
        return new RegisteredContactsFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }


    @Override
    public void onViewCreated(View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRealm = Realm.getDefaultInstance();

        realmContacts = mRealm.where(RealmContacts.class).findAll();
        contactsChangeListener = new RealmChangeListener<RealmResults<RealmContacts>>() {
            @Override
            public void onChange(RealmResults<RealmContacts> element) {
                fillAdapter();
            }
        };


        sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        /**
         * not import contact in every enter to this page
         * for this purpose i comment this code. but not cleared.
         */
        //isImportContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT_IN_FRAGMENT, false);
        //if (!isImportContactList) {
        //    try {
        //        HelperPermision.getContactPermision(getActivity(), new OnGetPermission() {
        //            @Override
        //            public void Allow() throws IOException {
        //                importContactList();
        //            }
        //
        //            @Override
        //            public void deny() {
        //
        //            }
        //        });
        //    } catch (IOException e) {
        //        e.printStackTrace();
        //    }
        //    SharedPreferences.Editor editor = sharedPreferences.edit();
        //    editor.putBoolean(SHP_SETTING.KEY_GET_CONTACT_IN_FRAGMENT, true);
        //    editor.apply();
        //}

        //set interface for get callback here
        prgWaiting = (ProgressBar) view.findViewById(R.id.prgWaiting_addContact);
        prgWaiting.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);
        prgWaiting.setVisibility(View.GONE);
        vgAddContact = (ViewGroup) view.findViewById(R.id.menu_layout_addContact);
        vgRoot = (ViewGroup) view.findViewById(R.id.menu_parent_layout);


        Bundle bundle = this.getArguments();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("TITLE");
        }

        if (title != null) {

            if (title.equals("New Chat")) {
                title = G.context.getString(R.string.New_Chat);
            } else if (title.equals("Contacts")) {
                title = G.context.getString(R.string.contacts);
            }
        }

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.fc_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));



        //create our FastAdapter
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);

        //create our adapters
        stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContactItem>() {
            @Override
            public boolean filter(ContactItem item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactItem>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, ContactItem item, int position) {

                showProgress();

                chatGetRoom(item.mContact.peerId);
                return false;
            }
        });
        menu_txt_titleToolbar = (TextView) view.findViewById(R.id.menu_txt_titleToolbar);
        menu_txt_titleToolbar.setText(title);

        final RippleView txtClose = (RippleView) view.findViewById(R.id.menu_ripple_close);
        edtSearch = (EditText) view.findViewById(R.id.menu_edt_search);
        final TextView txtSearch = (TextView) view.findViewById(R.id.menu_btn_search);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                txtClose.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                edtSearch.setFocusable(true);
                menu_txt_titleToolbar.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
            }
        });

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemAdapter.filter(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });
        vgAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentAddContact fragment = FragmentAddContact.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", G.context.getString(R.string.fac_Add_Contact));
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).add(R.id.fragmentContainer, fragment).commit();

            }
        });

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) view.findViewById(R.id.menu_txtBack);
        RippleView rippleMenu = (RippleView) view.findViewById(R.id.menu_ripple_txtBack);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                // close and remove fragment from stack

                getActivity().getSupportFragmentManager().popBackStack();
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleView.getWindowToken(), 0);
            }
        });
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        //this adds the Sticky Headers within our list
        decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        rv.addItemDecoration(decoration);


        items = new ArrayList<>();

        contacts = Contacts.retrieve(null);


        /**
         * after send contact automatically do get contact list
         * so if !G.isSendContact try for get list for getting
         * contacts if other account imported to server
         */
        if (!G.isSendContact || contacts.size() == 0) {
            /**
             * if contacts size is zero send request for get contacts list
             * for insuring that contacts not exist really or not
             */
            Log.i("MMM", "RequestUserContactsGetList 1");
            new RequestUserContactsGetList().userContactGetList();
        }

        fillAdapter();

    }

    private void chatGetRoom(final long peerId) {

        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();

        if (realmRoom != null) {
            hideProgress();
            Intent intent = new Intent(context, ActivityChat.class);
            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            getActivity().getSupportFragmentManager().beginTransaction().remove(RegisteredContactsFragment.this).commit();
            //            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final long roomId) {
                    hideProgress();
                    getUserInfo(peerId, roomId);
                }

                @Override
                public void onChatGetRoomCompletely(ProtoGlobal.Room room) {

                }

                @Override
                public void onChatGetRoomTimeOut() {

                    hideProgress();

                }

                @Override
                public void onChatGetRoomError(int majorCode, int minorCode) {

                    hideProgress();

                }

            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
        realm.close();
    }

    private void getUserInfo(final long peerId, final long roomId) {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (user.getId() == peerId) {
                            Realm realm = Realm.getDefaultInstance();

                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, user.getId()).findFirst();
                                    if (realmRegisteredInfo == null) {
                                        realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class);
                                        realmRegisteredInfo.setId(user.getId());
                                        realmRegisteredInfo.setDoNotshowSpamBar(false);
                                    }

                                    RealmAvatar.put(user.getId(), user.getAvatar(), true);
                                    realmRegisteredInfo.setUsername(user.getUsername());
                                    realmRegisteredInfo.setPhoneNumber(Long.toString(user.getPhone()));
                                    realmRegisteredInfo.setFirstName(user.getFirstName());
                                    realmRegisteredInfo.setLastName(user.getLastName());
                                    realmRegisteredInfo.setDisplayName(user.getDisplayName());
                                    realmRegisteredInfo.setInitials(user.getInitials());
                                    realmRegisteredInfo.setColor(user.getColor());
                                    realmRegisteredInfo.setStatus(user.getStatus().toString());
                                    realmRegisteredInfo.setAvatarCount(user.getAvatarCount());
                                    realmRegisteredInfo.setMutual(user.getMutual());
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    try {
                                        hideProgress();
                                        Intent intent = new Intent(context, ActivityChat.class);
                                        intent.putExtra("peerId", peerId);
                                        intent.putExtra("RoomId", roomId);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        mActivity.getSupportFragmentManager().popBackStack();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            realm.close();
                        }

                    }
                });

            }

            @Override
            public void onUserInfoTimeOut() {

                hideProgress();
            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

                hideProgress();

            }
        };

        new RequestUserInfo().userInfo(peerId);
    }

    public void updateChatAvatar(long userId) {
        int position = getPosition(contacts, userId);
        if (position != -1) {
            fastAdapter.notifyAdapterItemChanged(position);
        }
    }

    private int getPosition(List<StructContactInfo> structContactInfos, long userId) {

        for (int i = 0; i < structContactInfos.size(); i++) {
            if (structContactInfos.get(i).peerId == userId) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        if (fastAdapter != null) {
            outState = fastAdapter.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    private void hideProgress() {

        G.handler.post(new Runnable() {

            @Override
            public void run() {
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.GONE);
            }
        });
    }

    private void showProgress() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    //@Override public void onAttach(Context context) {
    //    super.onAttach(context);
    //    mActivity = (Activity) context;
    //
    //}

    private void fillAdapter() {

        contacts.clear();
        itemAdapter.clear();
        items.clear();

        contacts = Contacts.retrieve(null);
        if (contacts != null && fastAdapter != null && itemAdapter != null) {
            for (StructContactInfo contact : contacts) {
                items.add(new ContactItem().setContact(contact).withIdentifier(100 + contacts.indexOf(contact)));
            }
            itemAdapter.add(items);

            //so the headers are aware of changes
            stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    decoration.invalidateHeaders();
                }
            });
            //restore selections (this has to be done after the items were added
            //  fastAdapter.withSavedInstanceState(savedInstanceState);
            fastAdapter.notifyDataSetChanged();
        }

        if (edtSearch.getText().length() > 0) itemAdapter.filter(edtSearch.getText());
    }

    @Override public void onDetach() {
        super.onDetach();
        hideProgress();
    }
}
