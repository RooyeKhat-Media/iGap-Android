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
import android.text.InputType;
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
import net.iGap.adapter.items.SearchItamIGap;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.IClientSearchUserName;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.proto.ProtoClientSearchUsername;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestClientSearchUsername;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class FragmentIgapSearch extends BaseFragment {

    private FastAdapter fastAdapter;
    private EditText edtSearch;
    MaterialDesignTextView btnClose;
    RippleView rippleDown;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private TextView txtEmptyListComment;

    //private TextView txtNothing;
    private ContentLoadingProgressBar loadingProgressBar;
    private ImageView imvNothingFound;

    public static FragmentIgapSearch newInstance() {
        return new FragmentIgapSearch();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.search_fragment_layout, container, false));
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponent(view);
        initRecycleView();
    }

    private void initComponent(View view) {

        view.findViewById(R.id.sfl_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        imvNothingFound = (ImageView) view.findViewById(R.id.sfl_imv_nothing_found);
        imvNothingFound.setImageResource(R.drawable.find2);

        txtEmptyListComment = (TextView) view.findViewById(R.id.sfl_txt_empty_list_comment);


        //txtNothing = (TextView) view.findViewById(R.id.sfl_txt_empty_nothing);
        //txtNothing.setVisibility(View.VISIBLE);

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imvNothingFound.setVisibility(View.VISIBLE);
                txtEmptyListComment.setVisibility(View.VISIBLE);
            }
        }, 150);

        loadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.sfl_progress_loading);
        loadingProgressBar.getIndeterminateDrawable().setColorFilter(G.context.getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);

        edtSearch = (EditText) view.findViewById(R.id.sfl_edt_search);

        edtSearch.setInputType(InputType.TYPE_CLASS_TEXT);

        //edtSearch.setFilters(new InputFilter[] {
        //    new InputFilter() {
        //        public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
        //
        //            Log.e("qqqqqq", src + "  " + start + "  " + end + "    " + dst + "   " + dstart + "    " + dend);
        //
        //            if (src.equals("") || (dst.length() == 0 && src.equals("@"))) {
        //                return src;
        //            }
        //            if (src.toString().matches("\\w")) {
        //                return src;
        //            }
        //            return "";
        //        }
        //    }
        //});

        edtSearch.setText("@");
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                itemAdapter.clear();

                int strSize = edtSearch.getText().toString().trim().length();

                // filter some character
                if (strSize > 1) {
                    String _str = edtSearch.getText().toString().substring(strSize - 1);
                    if (!_str.matches("\\w")) {
                        edtSearch.setText(edtSearch.getText().subSequence(0, strSize - 1));
                        edtSearch.setSelection(edtSearch.getText().length());
                    }
                }

                if (strSize > 1) {
                    txtEmptyListComment.setVisibility(View.GONE);
                    imvNothingFound.setVisibility(View.GONE);
                    //txtNothing.setVisibility(View.GONE);
                } else {
                    txtEmptyListComment.setText(R.string.empty_message);
                    txtEmptyListComment.setVisibility(View.VISIBLE);
                    imvNothingFound.setVisibility(View.VISIBLE);
                    //txtNothing.setVisibility(View.VISIBLE);
                }

                if (strSize > 5) {

                    if (G.userLogin) {
                        new RequestClientSearchUsername().clientSearchUsername(edtSearch.getText().toString().substring(1));
                        loadingProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                    }
                }
            }

            @Override public void afterTextChanged(Editable editable) {
                if (edtSearch.getText().length() == 0 || !edtSearch.getText().toString().substring(0, 1).equals("@")) {
                    edtSearch.setText("@");
                    edtSearch.setSelection(1);
                }
            }
        });
        edtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.sfl_btn_back);
        final RippleView rippleBack = (RippleView) view.findViewById(R.id.sfl_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {

                InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleBack.getWindowToken(), 0);
                //G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentIgapSearch.this).commit();
                G.fragmentActivity.onBackPressed();
            }
        });

        btnClose = (MaterialDesignTextView) view.findViewById(R.id.sfl_btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                edtSearch.setText("@");
                edtSearch.setSelection(1);
            }
        });
        rippleDown = (RippleView) view.findViewById(R.id.sfl_ripple_done);
        ((View) rippleDown).setEnabled(false);
        rippleDown.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {

            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.sfl_recycleview);
    }

    private void initRecycleView() {

        itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);

        fastAdapter.withOnClickListener(new OnClickListener<IItem>() {
            @Override public boolean onClick(View v, IAdapter adapter, IItem currentItem, int position) {

                ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item = ((SearchItamIGap) currentItem).getItem();

                if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.USER) {

                    HelperUrl.checkUsernameAndGoToRoom(item.getUser().getUsername(), HelperUrl.ChatEntry.profile);
                } else if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.ROOM) {

                    if (item.getRoom().getType() == ProtoGlobal.Room.Type.CHANNEL) {
                        HelperUrl.checkUsernameAndGoToRoom(item.getRoom().getChannelRoomExtra().getPublicExtra().getUsername(), HelperUrl.ChatEntry.profile);
                    } else if (item.getRoom().getType() == ProtoGlobal.Room.Type.GROUP) {
                        HelperUrl.checkUsernameAndGoToRoom(item.getRoom().getGroupRoomExtra().getPublicExtra().getUsername(), HelperUrl.ChatEntry.profile);
                    }
                }

                //  G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentIgapSearch.this).commit();

                popBackStackFragment();

                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastAdapter);

        G.onClientSearchUserName = new IClientSearchUserName() {
            @Override public void OnGetList(final ProtoClientSearchUsername.ClientSearchUsernameResponse.Builder builderList) {

                G.handler.post(new Runnable() {
                    @Override public void run() {

                        loadingProgressBar.setVisibility(View.GONE);

                        if (builderList.getResultList().size() == 0) {
                            txtEmptyListComment.setText(R.string.there_is_no_any_result);
                            txtEmptyListComment.setVisibility(View.VISIBLE);
                            imvNothingFound.setVisibility(View.VISIBLE);
                            //txtNothing.setVisibility(View.VISIBLE);

                            return;
                        }

                        List<IItem> items = new ArrayList<>();

                        int i = 0;

                        Realm realm = Realm.getDefaultInstance();

                        for (final ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item : builderList.getResultList()) {

                            if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.USER) {

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override public void execute(Realm realm) {
                                        RealmRegisteredInfo.putOrUpdate(realm, item.getUser());
                                    }
                                });
                            } else if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.ROOM) {

                                final RealmRoom[] realmRoom = { realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, item.getRoom().getId()).findFirst() };

                                if (realmRoom[0] == null) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override public void execute(Realm realm) {
                                            realmRoom[0] = RealmRoom.putOrUpdate(item.getRoom(), realm);
                                            realmRoom[0].setDeleted(true);
                                            G.deletedRoomList.add(realmRoom[0].getId());
                                        }
                                    });
                                }
                            }

                            items.add(new SearchItamIGap().setItem(item).withIdentifier(100 + i++));
                        }
                        itemAdapter.clear();
                        itemAdapter.add(items);

                        realm.close();

                    }
                });
            }

            @Override public void OnErrore() {
                G.handler.post(new Runnable() {
                    @Override public void run() {
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        };
    }
}
