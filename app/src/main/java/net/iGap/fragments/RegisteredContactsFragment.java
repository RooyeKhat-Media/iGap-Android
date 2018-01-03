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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnPhoneContact;
import net.iGap.interfaces.OnUserContactDelete;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.LoginActions;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserContactsGetList;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;
import static net.iGap.R.string.contacts;

public class RegisteredContactsFragment extends BaseFragment implements OnUserContactDelete, OnPhoneContact {

    private TextView menu_txt_titleToolbar;
    private ViewGroup vgAddContact, vgRoot;

    private RecyclerView realmRecyclerView;
    private SharedPreferences sharedPreferences;
    private boolean isImportContactList = false;
    private static boolean getPermission = true;
    private Realm realm;
    StickyRecyclerHeadersDecoration decoration;
    private ProgressBar prgWaiting;
    private ProgressBar prgWaitingLoadContact;
    RealmResults<RealmContacts> results;
    private EditText edtSearch;
    private boolean isCallAction = false;
    private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();
    private FastItemAdapter fastItemAdapter;
    private ProgressBar prgWaitingLiadList;
    //private ContactListAdapterA mAdapter;
    private NestedScrollView nestedScrollView;

    private Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }

        return realm;
    }


    public static RegisteredContactsFragment newInstance() {
        return new RegisteredContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return attachToSwipeBack(inflater.inflate(R.layout.fragment_contacts, container, false));
    }

    @Override
    public void onViewCreated(View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        G.onPhoneContact = this;
        Contacts.localPhoneContactId = 0;
        Contacts.getContact = true;

        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        /**
         * not import contact in every enter to this page
         * for this purpose i comment this code. but not cleared.
         */
        //isImportContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT_IN_FRAGMENT, false);
        //if (!isImportContactList) {
        //    try {
        //        HelperPermision.getContactPermision(G.fragmentActivity, new OnGetPermission() {
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

        nestedScrollView = view.findViewById(R.id.nestedScrollContact);

        TextView txtNonUser = (TextView) view.findViewById(R.id.txtNon_User);
        txtNonUser.setTextColor(Color.parseColor(G.appBarColor));
        prgWaitingLiadList = (ProgressBar) view.findViewById(R.id.prgWaiting_loadList);
        prgWaitingLoadContact = (ProgressBar) view.findViewById(R.id.prgWaitingLoadContact);

        prgWaiting = (ProgressBar) view.findViewById(R.id.prgWaiting_addContact);
        AppUtils.setProgresColler(prgWaiting);

        prgWaiting.setVisibility(View.GONE);
        vgAddContact = (ViewGroup) view.findViewById(R.id.menu_layout_addContact);
        vgRoot = (ViewGroup) view.findViewById(R.id.menu_parent_layout);

        Bundle bundle = this.getArguments();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("TITLE");
            isCallAction = bundle.getBoolean("ACTION");
        }

        if (title != null) {

            if (title.equals("New Chat")) {
                title = G.context.getString(R.string.New_Chat);
            } else if (title.equals("Contacts")) {
                title = G.context.getString(contacts);
            } else if (title.equals("call")) {
                title = G.context.getString(R.string.call_with);
            }
        }

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));

        menu_txt_titleToolbar = (TextView) view.findViewById(R.id.menu_txt_titleToolbar);
        menu_txt_titleToolbar.setText(title);

        final RippleView txtClose = (RippleView) view.findViewById(R.id.menu_ripple_close);
        edtSearch = (EditText) view.findViewById(R.id.menu_edt_search);
        final TextView txtSearch = (TextView) view.findViewById(R.id.menu_btn_search);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                if (s.length() > 0) {
                    results = getRealm().where(RealmContacts.class).contains(RealmContactsFields.DISPLAY_NAME, s.toString(), Case.INSENSITIVE).findAllSorted(RealmContactsFields.DISPLAY_NAME);
                } else {
                    results = getRealm().where(RealmContacts.class).findAllSorted(RealmContactsFields.DISPLAY_NAME);
                }

                realmRecyclerView.setAdapter(new ContactListAdapter(results));

                // fastAdapter
                //mAdapter.clear();
                //for (RealmContacts contact : results) {
                //    mAdapter.add(new ContactItem().setInfo(contact).withIdentifier(contact.getId()));
                //}
                //realmRecyclerView.setAdapter(mAdapter);

                realmRecyclerView.removeItemDecoration(decoration);
                decoration = new StickyRecyclerHeadersDecoration(new StickyHeader(results));
                realmRecyclerView.addItemDecoration(decoration);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        vgAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentAddContact fragment = FragmentAddContact.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", G.context.getString(R.string.fac_Add_Contact));
                fragment.setArguments(bundle);
                new HelperFragment(fragment).setReplace(false).load();
            }
        });

        RippleView rippleMenu = (RippleView) view.findViewById(R.id.menu_ripple_txtBack);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.onBackPressed();
                InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleView.getWindowToken(), 0);
            }
        });

        realmRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        realmRecyclerView.setItemViewCacheSize(1000);
        realmRecyclerView.setItemAnimator(null);
        realmRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));
        realmRecyclerView.setNestedScrollingEnabled(false);

        results = getRealm().where(RealmContacts.class).findAllSorted(RealmContactsFields.DISPLAY_NAME);

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                realmRecyclerView.setAdapter(new ContactListAdapter(results));
                prgWaitingLoadContact.setVisibility(View.GONE);
                realmRecyclerView.setVisibility(View.VISIBLE);
            }
        }, 500);


        //fastAdapter
        //mAdapter = new ContactListAdapterA();
        //for (RealmContacts contact : results) {
        //    mAdapter.add(new ContactItem().setInfo(contact).withIdentifier(contact.getId()));
        //}
        //realmRecyclerView.setAdapter(mAdapter);
        //mAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
        //    @Override
        //    public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
        //        if (isCallAction) {
        //            popBackStackFragment();
        //            long userId = ((ContactItem) item).contact.getId();
        //            if (userId != 134 && G.userId != userId) {
        //                FragmentCall.call(userId, false);
        //            }
        //        } else {
        //            showProgress();
        //            HelperPublicMethod.goToChatRoom(((ContactItem) item).contact.getId(), new HelperPublicMethod.OnComplete() {
        //                @Override
        //                public void complete() {
        //                    hideProgress();
        //                    popBackStackFragment();
        //                }
        //            }, new HelperPublicMethod.OnError() {
        //                @Override
        //                public void error() {
        //                    hideProgress();
        //                }
        //            });
        //        }
        //        return true;
        //    }
        //});

        StickyHeader stickyHeader = new StickyHeader(results);
        decoration = new StickyRecyclerHeadersDecoration(stickyHeader);
        realmRecyclerView.addItemDecoration(decoration);

        RecyclerView rcvListContact = (RecyclerView) view.findViewById(R.id.rcv_friends_to_invite);
        fastItemAdapter = new FastItemAdapter();

        try {
            if (getPermission) {
                getPermission = false;
                HelperPermission.getContactPermision(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        /**
                         * if contacts size is zero send request for get contacts list
                         * for insuring that contacts not exist really or not
                         */
                        if (results.size() == 0) {
                            LoginActions.importContact();
                        }
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                prgWaitingLiadList.setVisibility(View.VISIBLE);
                            }
                        });
                        new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    @Override
                    public void deny() {
                        if (results.size() == 0) {
                            new RequestUserContactsGetList().userContactGetList();
                        }
                        prgWaitingLiadList.setVisibility(View.GONE);
                    }
                });
            } else {
                if (results.size() == 0) {
                    new RequestUserContactsGetList().userContactGetList();
                }

                if (HelperPermission.grantedContactPermission()) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            prgWaitingLiadList.setVisibility(View.VISIBLE);
                        }
                    });
                    new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    prgWaitingLiadList.setVisibility(View.GONE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        rcvListContact.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvListContact.setItemAnimator(new DefaultItemAnimator());
        rcvListContact.setAdapter(fastItemAdapter);
        rcvListContact.setNestedScrollingEnabled(false);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (Contacts.isEndLocal) {
                    return;
                }
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

    }


    private void hideProgress() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.GONE);
            }
        });
    }

    private void showProgress() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Contacts.getContact = false;
        hideProgress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        G.onUserContactdelete = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Override onSaveInstanceState method and comment 'super' from avoid from "Can not perform this action after onSaveInstanceState" error
        //super.onSaveInstanceState(outState); //
    }

    @Override
    public void onContactDelete() {
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        mAdapter.remove(mAdapter.getPosition(userId));
        //        edtSearch.setText("");
        //    }
        //});
    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onPhoneContact(final ArrayList<StructListOfContact> contacts, final boolean isEnd) {
        new AddAsync(contacts, isEnd).execute();
    }

    /**
     * ***********************************************************************************
     * *********************************** FastAdapter ***********************************
     * ***********************************************************************************
     */
    //+ manually update
    //public class ContactListAdapterA<Item extends ContactItem> extends FastItemAdapter<Item> {
    //    @Override
    //    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //        return super.onCreateViewHolder(parent, viewType);
    //    }
    //}
    //
    //public class ContactItem extends AbstractItem<ContactItem, ContactItem.ViewHolder> {
    //    String lastHeader = "";
    //    RealmContacts contact;
    //
    //    public ContactItem setInfo(RealmContacts contact) {
    //        this.contact = contact;
    //        return this;
    //    }
    //
    //    @Override
    //    public void bindView(final ViewHolder viewHolder, List payloads) throws IllegalStateException {
    //        super.bindView(viewHolder, payloads);
    //
    //        if (contact == null || !contact.isValid()) {
    //            return;
    //        }
    //
    //        if (viewHolder.itemView.findViewById(R.id.mainContainer) == null) {
    //            ((ViewGroup) viewHolder.itemView).addView(ViewMaker.getViewRegisteredContacts());
    //        }
    //
    //        viewHolder.image = (CircleImageView) viewHolder.itemView.findViewById(R.id.imageView);
    //        viewHolder.title = (TextView) viewHolder.itemView.findViewById(R.id.title);
    //        viewHolder.subtitle = (TextView) viewHolder.itemView.findViewById(R.id.subtitle);
    //        viewHolder.topLine = (View) viewHolder.itemView.findViewById(R.id.topLine);
    //
    //        String header = contact.getDisplay_name();
    //
    //        if (lastHeader.isEmpty() || (!lastHeader.isEmpty() && !header.isEmpty() && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
    //            viewHolder.topLine.setVisibility(View.VISIBLE);
    //        } else {
    //            viewHolder.topLine.setVisibility(View.GONE);
    //        }
    //
    //        lastHeader = header;
    //
    //        viewHolder.title.setText(contact.getDisplay_name());
    //
    //        final RealmRegisteredInfo realmRegisteredInfo = getRealm().where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, contact.getId()).findFirst();
    //        if (realmRegisteredInfo != null) {
    //            viewHolder.subtitle.setTextColor(ContextCompat.getColor(context, R.color.room_message_gray));
    //            if (realmRegisteredInfo.getStatus() != null) {
    //                if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
    //                    viewHolder.subtitle.setText(LastSeenTimeUtil.computeTime(contact.getId(), realmRegisteredInfo.getLastSeen(), false));
    //                } else {
    //                    if (realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.ONLINE.toString())) {
    //                        viewHolder.subtitle.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
    //                    }
    //                    viewHolder.subtitle.setText(realmRegisteredInfo.getStatus());
    //                }
    //            }
    //
    //            if (HelperCalander.isPersianUnicode) {
    //                viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
    //            }
    //        }
    //
    //        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
    //            @Override
    //            public boolean onLongClick(View v) {
    //
    //                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
    //                    @Override
    //                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
    //
    //                        new RequestUserContactsDelete().contactsDelete(realmRegisteredInfo.getPhoneNumber());
    //                    }
    //                }).negativeText(R.string.B_cancel).show();
    //
    //                return false;
    //            }
    //        });
    //
    //        hashMapAvatar.put(contact.getId(), viewHolder.image);
    //        setAvatar(viewHolder, contact.getId());
    //    }
    //
    //    private void setAvatar(final ViewHolder holder, final long userId) {
    //        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
    //            @Override
    //            public void onAvatarGet(final String avatarPath, long ownerId) {
    //                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(ownerId));
    //            }
    //
    //            @Override
    //            public void onShowInitials(final String initials, final String color) {
    //                hashMapAvatar.get(userId).setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
    //            }
    //        });
    //    }
    //
    //    @Override
    //    public int getType() {
    //        return 0;
    //    }
    //
    //    @Override
    //    public int getLayoutRes() {
    //        return R.layout.contact_item_code;
    //    }
    //
    //    @Override
    //    public ViewHolder getViewHolder(View viewGroup) {
    //        //View v = ViewMaker.getViewRegisteredContacts();
    //        //
    //        //if (getData() != null && count != getData().size()) {
    //        //    count = getData().size();
    //        //
    //        //    realmRecyclerView.post(new Runnable() {
    //        //        @Override
    //        //        public void run() {
    //        //            realmRecyclerView.removeItemDecoration(decoration);
    //        //            decoration = new StickyRecyclerHeadersDecoration(new StickyHeader(getData().sort(RealmContactsFields.DISPLAY_NAME)));
    //        //            realmRecyclerView.addItemDecoration(decoration);
    //        //        }
    //        //    });
    //        //}
    //        //View v = ViewMaker.getViewRegisteredContacts();
    //        return new ViewHolder(viewGroup);
    //    }
    //
    //    public class ViewHolder extends RecyclerView.ViewHolder {
    //
    //        private RealmContacts realmContacts;
    //        protected CircleImageView image;
    //        protected TextView title;
    //        protected TextView subtitle;
    //        protected View topLine;
    //
    //        public ViewHolder(View view) {
    //            super(view);
    //
    //            //image = (CircleImageView) view.findViewById(R.id.imageView);
    //            //title = (TextView) view.findViewById(R.id.title);
    //            //subtitle = (TextView) view.findViewById(R.id.subtitle);
    //            //topLine = (View) view.findViewById(R.id.topLine);
    //            //
    //            //itemView.setOnClickListener(new View.OnClickListener() {
    //            //    @Override
    //            //    public void onClick(View v) {
    //            //
    //            //        if (isCallAction) {
    //            //            //  G.fragmentActivity.getSupportFragmentManager().popBackStack();
    //            //
    //            //            popBackStackFragment();
    //            //
    //            //            long userId = realmContacts.getId();
    //            //            if (userId != 134 && G.userId != userId) {
    //            //                FragmentCall.call(userId, false);
    //            //            }
    //            //
    //            //
    //            //        } else {
    //            //            showProgress();
    //            //
    //            //            HelperPublicMethod.goToChatRoom(realmContacts.getId(), new HelperPublicMethod.OnComplete() {
    //            //                @Override
    //            //                public void complete() {
    //            //                    hideProgress();
    //            //                    //  G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(RegisteredContactsFragment.this).commit();
    //            //
    //            //                    popBackStackFragment();
    //            //
    //            //                }
    //            //            }, new HelperPublicMethod.OnError() {
    //            //                @Override
    //            //                public void error() {
    //            //                    hideProgress();
    //            //                }
    //            //            });
    //            //        }
    //            //    }
    //            //});
    //        }
    //    }
    //}

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class ContactListAdapter extends RealmRecyclerViewAdapter<RealmContacts, ContactListAdapter.ViewHolder> {

        String lastHeader = "";
        int count;
        private boolean isSwipe = false;

        ContactListAdapter(RealmResults<RealmContacts> realmResults) {
            super(realmResults, true);
            count = realmResults.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private RealmContacts realmContacts;
            protected CircleImageView image;
            protected TextView title;
            protected TextView subtitle;
            protected View topLine;
            private SwipeLayout swipeLayout;

            public ViewHolder(View view) {
                super(view);

                image = (CircleImageView) view.findViewById(R.id.imageView);
                title = (TextView) view.findViewById(R.id.title);
                subtitle = (TextView) view.findViewById(R.id.subtitle);
                topLine = (View) view.findViewById(R.id.topLine);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipeRevealLayout);
                swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isCallAction) {
                            //  G.fragmentActivity.getSupportFragmentManager().popBackStack();

                            popBackStackFragment();

                            long userId = realmContacts.getId();
                            if (userId != 134 && G.userId != userId) {
                                FragmentCall.call(userId, false);
                            }


                        } else {
                            showProgress();

                            HelperPublicMethod.goToChatRoom(realmContacts.getId(), new HelperPublicMethod.OnComplete() {
                                @Override
                                public void complete() {
                                    hideProgress();
                                    //  G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(RegisteredContactsFragment.this).commit();

                                    popBackStackFragment();

                                }
                            }, new HelperPublicMethod.OnError() {
                                @Override
                                public void error() {
                                    hideProgress();
                                }
                            });
                        }
                    }
                });
            }
        }

        @Override
        public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // View v = inflater.inflate(R.layout.contact_item, viewGroup, false);

            View v = ViewMaker.getViewRegisteredContacts();

            if (getData() != null && count != getData().size()) {
                count = getData().size();

                realmRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        realmRecyclerView.removeItemDecoration(decoration);
                        decoration = new StickyRecyclerHeadersDecoration(new StickyHeader(getData().sort(RealmContactsFields.DISPLAY_NAME)));
                        realmRecyclerView.addItemDecoration(decoration);
                    }
                });
            }

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ContactListAdapter.ViewHolder viewHolder, int i) {

            final RealmContacts contact = viewHolder.realmContacts = getItem(i);
            if (contact == null) {
                return;
            }

            String header = contact.getDisplay_name();

            if (lastHeader.isEmpty() || (!lastHeader.isEmpty() && !header.isEmpty() && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
                viewHolder.topLine.setVisibility(View.VISIBLE);
            } else {
                viewHolder.topLine.setVisibility(View.GONE);
            }

            lastHeader = header;

            viewHolder.title.setText(contact.getDisplay_name());

            final RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, contact.getId());
            if (realmRegisteredInfo != null) {
                viewHolder.subtitle.setTextColor(ContextCompat.getColor(context, R.color.room_message_gray));
                if (realmRegisteredInfo.getStatus() != null) {
                    if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                        viewHolder.subtitle.setText(LastSeenTimeUtil.computeTime(contact.getId(), realmRegisteredInfo.getLastSeen(), false));
                    } else {
                        if (realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.ONLINE.toString())) {
                            viewHolder.subtitle.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                        }
                        viewHolder.subtitle.setText(realmRegisteredInfo.getStatus());
                    }
                }

                if (HelperCalander.isPersianUnicode) {
                    viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
                }
            }


            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    isSwipe = true;
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            new RequestUserContactsDelete().contactsDelete(realmRegisteredInfo.getPhoneNumber());
                        }
                    }).negativeText(R.string.B_cancel).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            viewHolder.swipeLayout.close();
                        }
                    }).build();

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            viewHolder.swipeLayout.close();
                        }
                    });
                    dialog.show();
                }

                @Override
                public void onStartClose(SwipeLayout layout) {


                }

                @Override
                public void onClose(SwipeLayout layout) {
                    isSwipe = false;
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }
            });


            //viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            //    @Override
            //    public boolean onLongClick(View v) {
            //
            //        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            //            @Override
            //            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            //
            //                new RequestUserContactsDelete().contactsDelete(realmRegisteredInfo.getPhoneNumber());
            //            }
            //        }).negativeText(R.string.B_cancel).show();
            //
            //        return false;
            //    }
            //});

            hashMapAvatar.put(contact.getId(), viewHolder.image);
            setAvatar(viewHolder, contact.getId());
        }

        private void setAvatar(final ViewHolder holder, final long userId) {

            HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
                @Override
                public void onAvatarGet(final String avatarPath, long ownerId) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(ownerId));
                }

                @Override
                public void onShowInitials(final String initials, final String color) {
                    hashMapAvatar.get(userId).setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                }
            });
        }
    }

    /**
     * ************************************* show all phone contact *************************************
     */
    private class StickyHeader implements StickyRecyclerHeadersAdapter {

        RealmResults<RealmContacts> realmResults;

        StickyHeader(RealmResults<RealmContacts> realmResults) {
            this.realmResults = realmResults;
        }

        @Override
        public long getHeaderId(int position) {
            return realmResults.get(position).getDisplay_name().toUpperCase().charAt(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

            CustomTextViewMedium textView = (CustomTextViewMedium) holder.itemView;
            textView.setText(realmResults.get(position).getDisplay_name().toUpperCase().substring(0, 1));
        }

        @Override
        public int getItemCount() {
            return realmResults.size();
        }
    }

    public class AdapterListContact extends AbstractItem<AdapterListContact, AdapterListContact.ViewHolder> {

        public String item;
        public String phone;

        //public String getItem() {
        //    return item;
        //}

        public AdapterListContact(String item, String phone) {
            this.item = item;
            this.phone = phone;
        }

        //public void setItem(String item) {
        //    this.item = item;
        //}

        //The unique ID for this type of item
        @Override
        public int getType() {
            return R.id.root_List_contact;
        }

        //The layout to be used for this type of item
        @Override
        public int getLayoutRes() {
            return R.layout.adapter_list_cobtact;
        }

        //The logic to bind your data to the view

        @Override
        public void bindView(ViewHolder holder, List payloads) {
            super.bindView(holder, payloads);

            holder.txtName.setText(item);
            holder.txtPhone.setText(phone);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.igap))

                            .content(G.fragmentActivity.getResources().getString(R.string.invite_friend)).positiveText(G.fragmentActivity.getResources().getString(R.string.ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Join iGap : https://www.igap.net/ I'm waiting for you!");
                            sendIntent.setType("text/plain");
                            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            G.context.startActivity(sendIntent);
                        }
                    }).show();


                }
            });


        }

        //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
        protected class ViewHolder extends RecyclerView.ViewHolder {


            private TextView txtName;
            private TextView txtPhone;
            private ViewGroup root;

            public ViewHolder(View view) {
                super(view);

                txtName = (TextView) view.findViewById(R.id.txtName);
                txtPhone = (TextView) view.findViewById(R.id.txtPhone);
                root = (ViewGroup) view.findViewById(R.id.root_List_contact);

            }
        }

        @Override
        public ViewHolder getViewHolder(View v) {
            return new ViewHolder(v);
        }
    }

    private class AddAsync extends AsyncTask<Void, Void, Void> {

        private ArrayList<StructListOfContact> contacts;
        private boolean isEnd;

        public AddAsync(ArrayList<StructListOfContact> contacts, boolean isEnd) {
            this.contacts = contacts;
            this.isEnd = isEnd;
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (int i = 0; i < contacts.size(); i++) {
                fastItemAdapter.add(new AdapterListContact(contacts.get(i).getDisplayName(), contacts.get(i).getPhone()).withIdentifier(100 + i));
            }
            if (isEnd) {
                prgWaitingLiadList.setVisibility(View.GONE);
            }
            super.onPostExecute(aVoid);
        }
    }
}

