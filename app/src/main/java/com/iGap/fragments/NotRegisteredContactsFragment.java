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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.StickyHeaderAdapter;
import com.iGap.adapter.items.ContactItemNotRegister;
import com.iGap.module.Contacts;
import com.iGap.module.SoftKeyboard;
import com.iGap.module.structs.StructContactInfo;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import java.util.ArrayList;
import java.util.List;

public class NotRegisteredContactsFragment extends Fragment {
    private FastAdapter fastAdapter;

    public static NotRegisteredContactsFragment newInstance() {
        return new NotRegisteredContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("TITLE");
        }

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.fc_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));


        //create our FastAdapter
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        final ItemAdapter itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContactItemNotRegister>() {
            @Override
            public boolean filter(ContactItemNotRegister item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase()
                        .startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactItemNotRegister>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, ContactItemNotRegister item,
                                   int position) {


                return false;
            }
        });

        final TextView menu_txt_titleToolbar =
                (TextView) view.findViewById(R.id.menu_txt_titleToolbar);
        menu_txt_titleToolbar.setText(title);

        final android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) view.findViewById(R.id.menu_edtSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                itemAdapter.filter(newText);

                return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_txt_titleToolbar.setVisibility(View.GONE);
            }
        });

        final ViewGroup root = (ViewGroup) view.findViewById(R.id.menu_parent_layout);
        InputMethodManager im =
                (InputMethodManager) G.context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        final SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (searchView.getQuery().toString().length() > 0) {
                            searchView.setIconified(false);
                            menu_txt_titleToolbar.setVisibility(View.GONE);
                        } else {

                            searchView.setIconified(true);
                            menu_txt_titleToolbar.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        menu_txt_titleToolbar.setVisibility(View.GONE);
                    }
                });
            }
        });

        TextView txtMenu = (TextView) view.findViewById(R.id.menu_txtBack);
        txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // close and remove fragment from stack
                softKeyboard.closeSoftKeyboard();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        //ViewGroup layout = (ViewGroup) view.findViewById(R.id.menu_layout);

        //layout.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //
        //        //                searchView.onActionViewExpanded();
        //        searchView.setIconified(false);
        //        menu_txt_titleToolbar.setVisibility(View.GONE);
        //    }
        //});

        searchView.setOnCloseListener(
                new SearchView.OnCloseListener() { // close SearchView and show title again
                    @Override
                    public boolean onClose() {

                        menu_txt_titleToolbar.setVisibility(View.VISIBLE);

                        return false;
                    }
                });

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        //this adds the Sticky Headers within our list
        final StickyRecyclerHeadersDecoration decoration =
                new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        rv.addItemDecoration(decoration);

        List<IItem> items = new ArrayList<>();
        List<StructContactInfo> contacts = Contacts.getInviteFriendList();
        ;

        for (StructContactInfo contact : contacts) {
            items.add(new ContactItemNotRegister().setContact(contact)
                    .withIdentifier(100 + contacts.indexOf(contact)));
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
        fastAdapter.withSavedInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
