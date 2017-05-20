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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;

import static android.content.Context.MODE_PRIVATE;

public class RegisteredContactsFragment extends Fragment {

    private TextView menu_txt_titleToolbar;
    private ViewGroup vgAddContact, vgRoot;

    private RealmRecyclerView realmRecyclerView;
    private SharedPreferences sharedPreferences;
    private boolean isImportContactList = false;
    StickyRecyclerHeadersDecoration decoration;
    private ProgressBar prgWaiting;
    RealmResults<RealmContacts> results;
    private FragmentActivity mActivity;
    private EditText edtSearch;
    private boolean isCallAction = false;


    public static RegisteredContactsFragment newInstance() {
        return new RegisteredContactsFragment();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override public void onViewCreated(View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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
                title = G.context.getString(R.string.contacts);
            }
        }

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.fc_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

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

                Realm realm = Realm.getDefaultInstance();

                if (s.length() > 0) {
                    results = realm.where(RealmContacts.class).contains(RealmContactsFields.DISPLAY_NAME, s.toString(), Case.INSENSITIVE).findAllSorted(RealmContactsFields.DISPLAY_NAME);
                } else {
                    results = realm.where(RealmContacts.class).findAllSorted(RealmContactsFields.DISPLAY_NAME);
                }
                realmRecyclerView.setAdapter(new ContactListAdapter(getActivity(), results));

                realmRecyclerView.getRecycleView().removeItemDecoration(decoration);
                decoration = new StickyRecyclerHeadersDecoration(new StikyHeader(results));
                realmRecyclerView.getRecycleView().addItemDecoration(decoration);

                realm.close();
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });
        vgAddContact.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                FragmentAddContact fragment = FragmentAddContact.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", G.context.getString(R.string.fac_Add_Contact));
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                    .addToBackStack(null)
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
            }
        });

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) view.findViewById(R.id.menu_txtBack);
        RippleView rippleMenu = (RippleView) view.findViewById(R.id.menu_ripple_txtBack);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                // close and remove fragment from stack

                //getActivity().getSupportFragmentManager().popBackStack();
                getActivity().onBackPressed();
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleView.getWindowToken(), 0);
            }
        });

        Realm realm = Realm.getDefaultInstance();

        realmRecyclerView = (RealmRecyclerView) view.findViewById(R.id.recycler_view);
        realmRecyclerView.setItemViewCacheSize(100);
        realmRecyclerView.setDrawingCacheEnabled(true);
        results = realm.where(RealmContacts.class).findAllSorted(RealmContactsFields.DISPLAY_NAME);
        realmRecyclerView.setAdapter(new ContactListAdapter(getActivity(), results));

        StikyHeader stikyHeader = new StikyHeader(results);
        decoration = new StickyRecyclerHeadersDecoration(stikyHeader);
        realmRecyclerView.getRecycleView().addItemDecoration(decoration);

        realm.close();

        ///**
        // * after send contact automatically do get contact list
        // * so if !G.isSendContact try for get list for getting
        // * contacts if other account imported to server
        // */
        //if (!G.isSendContact || contacts.size() == 0) {
        //    /**
        //     * if contacts size is zero send request for get contacts list
        //     * for insuring that contacts not exist really or not
        //     */
        //    new RequestUserContactsGetList().userContactGetList();
        //}

    }



    private void hideProgress() {

        G.handler.post(new Runnable() {
            @Override public void run() {
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.GONE);
            }
        });
    }

    private void showProgress() {
        G.handler.post(new Runnable() {
            @Override public void run() {
                mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override public void onDetach() {
        super.onDetach();
        hideProgress();
    }

    //********************************************************************************************

    public class ContactListAdapter extends RealmBasedRecyclerViewAdapter<RealmContacts, ContactListAdapter.ViewHolder> {

        String lastHeader = "";
        int count;

        public ContactListAdapter(Context context, RealmResults<RealmContacts> realmResults) {
            super(context, realmResults, true, false, false, "");
            count = realmResults.size();
        }

        public class ViewHolder extends RealmViewHolder {

            protected CircleImageView image;
            protected CustomTextViewMedium title;
            protected CustomTextViewMedium subtitle;
            protected View topLine;

            public ViewHolder(View view) {
                super(view);

                image = (CircleImageView) view.findViewById(R.id.imageView);
                title = (CustomTextViewMedium) view.findViewById(R.id.title);
                subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
                topLine = (View) view.findViewById(R.id.topLine);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {

                        if (isCallAction) {
                            getActivity().getSupportFragmentManager().popBackStack();
                            FragmentCall.call(realmResults.get(getPosition()).getId(), false);
                        } else {
                            showProgress();

                            HelperPublicMethod.goToChatRoom(realmResults.get(getPosition()).getId(), new HelperPublicMethod.Oncomplet() {
                                @Override public void complete() {
                                    hideProgress();
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(RegisteredContactsFragment.this).commit();
                                }
                            }, new HelperPublicMethod.OnError() {
                                @Override public void error() {
                                    hideProgress();
                                }
                            });
                        }




                    }
                });
            }
        }

        @Override public ContactListAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.contact_item, viewGroup, false);

            if (count != realmResults.size()) {

                count = realmResults.size();

                realmRecyclerView.post(new Runnable() {
                    @Override public void run() {

                        realmRecyclerView.getRecycleView().removeItemDecoration(decoration);
                        decoration = new StickyRecyclerHeadersDecoration(new StikyHeader(realmResults));
                        realmRecyclerView.getRecycleView().addItemDecoration(decoration);
                    }
                });
            }

            return new ViewHolder(v);
        }

        @Override public void onBindRealmViewHolder(final ContactListAdapter.ViewHolder viewHolder, int i) {

            String header = realmResults.get(i).getDisplay_name();

            if (lastHeader.isEmpty() || (!lastHeader.isEmpty() && !header.isEmpty() && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {

                viewHolder.topLine.setVisibility(View.VISIBLE);
            } else {

                viewHolder.topLine.setVisibility(View.GONE);
            }

            lastHeader = header;

            viewHolder.title.setText(realmResults.get(i).getDisplay_name());
            Realm realm = Realm.getDefaultInstance();
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, realmResults.get(i).getId()).findFirst();
            if (realmRegisteredInfo != null) {

                if (realmRegisteredInfo.getStatus() != null) {
                    if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                        viewHolder.subtitle.setText(LastSeenTimeUtil.computeTime(realmResults.get(i).getId(), realmRegisteredInfo.getLastSeen(), false));
                    } else {
                        viewHolder.subtitle.setText(realmRegisteredInfo.getStatus());
                    }
                }

                if (HelperCalander.isLanguagePersian) {
                    viewHolder.subtitle.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.subtitle.getText().toString()));
                }
            }
            realm.close();

            setAvatar(viewHolder, realmResults.get(i).getId());
        }

        private void setAvatar(final ViewHolder holder, long userId) {

            HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                @Override public void onAvatarGet(final String avatarPath, long ownerId) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
                }

                @Override public void onShowInitials(final String initials, final String color) {
                    holder.image.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                }
            });
        }
    }

    //********************************************************************************************

    private class StikyHeader implements StickyRecyclerHeadersAdapter {

        RealmResults<RealmContacts> realmResults;

        public StikyHeader(RealmResults<RealmContacts> realmResults) {
            this.realmResults = realmResults;
        }

        @Override public long getHeaderId(int position) {
            return realmResults.get(position).getDisplay_name().toString().toUpperCase().charAt(0);
        }

        @Override public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

            CustomTextViewMedium textView = (CustomTextViewMedium) holder.itemView;
            textView.setText(realmResults.get(position).getDisplay_name().toUpperCase().substring(0, 1));
        }

        @Override public int getItemCount() {
            return realmResults.size();
        }
    }
}

