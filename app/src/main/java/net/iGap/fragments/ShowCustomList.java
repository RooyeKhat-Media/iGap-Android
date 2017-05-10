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

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.StickyHeaderAdapter;
import net.iGap.adapter.items.ContactItemGroup;
import net.iGap.interfaces.OnSelectedList;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.structs.StructContactInfo;

public class ShowCustomList extends Fragment {
    private static List<StructContactInfo> contacts;
    private static OnSelectedList onSelectedList;
    private FastAdapter fastAdapter;
    private TextView txtStatus;
    private TextView txtNumberOfMember;
    private EditText edtSearch;
    private String textString = "";
    private int sizeTextEdittext = 0;
    private boolean dialogShowing = false;
    private long lastId = 0;
    private int count = 0;
    private boolean singleSelect = false;
    private RippleView rippleDown;

    public static ShowCustomList newInstance(List<StructContactInfo> list, OnSelectedList onSelectedListResult) {
        onSelectedList = onSelectedListResult;
        contacts = list;

        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).isSelected = false;
        }

        return new ShowCustomList();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_group, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            dialogShowing = bundle.getBoolean("DIALOG_SHOWING");
            if (bundle.getLong("COUNT_MESSAGE") != 0) {
                lastId = bundle.getLong("COUNT_MESSAGE");
            }

            singleSelect = bundle.getBoolean("SINGLE_SELECT");
        }

        view.findViewById(R.id.fcg_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.fcg_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

        txtStatus = (TextView) view.findViewById(R.id.fcg_txt_status);
        txtNumberOfMember = (TextView) view.findViewById(R.id.fcg_txt_number_of_member);
        edtSearch = (EditText) view.findViewById(R.id.fcg_edt_search);

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.fcg_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.fcg_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        rippleDown = (RippleView) view.findViewById(R.id.fcg_ripple_done);
        rippleDown.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {

                if (dialogShowing) {
                    showDialog();
                } else {
                    if (onSelectedList != null) {
                        onSelectedList.getSelectedList(true, "", 0, getSelectedList());
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        //create our FastAdapter
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        final ItemAdapter itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContactItemGroup>() {
            @Override public boolean filter(ContactItemGroup item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactItemGroup>() {
            @Override public boolean onClick(View v, IAdapter adapter, ContactItemGroup item, int position) {

                item.mContact.isSelected = !item.mContact.isSelected;

                fastAdapter.notifyItemChanged(position);

                if (singleSelect) {
                    if (onSelectedList != null) {
                        onSelectedList.getSelectedList(true, "", 0, getSelectedList());
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                }

                refreshView();

                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() + i + i1 + i2 > 0) itemAdapter.filter(charSequence);

                //if (charSequence.length() > sizeTextEdittext) {
                //    String s = edtSearch.getText().toString().substring(sizeTextEdittext, charSequence.length());
                //    itemAdapter.filter(s);
                //} else {
                //    itemAdapter.filter("");
                //}
                //
                //edtSearch.setSelection(edtSearch.getText().length());
                //fastAdapter.notifyDataSetChanged();

            }

            @Override public void afterTextChanged(Editable editable) {

            }
        });

        edtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyCode == KeyEvent.KEYCODE_DEL)) {
                    if (edtSearch.getText().length() <= sizeTextEdittext) {
                        return true;
                    }
                }

                return false;
            }
        });

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.fcg_recycler_view_add_item_to_group);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        //this adds the Sticky Headers within our list
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        rv.addItemDecoration(decoration);

        List<IItem> items = new ArrayList<>();

        for (StructContactInfo contact : contacts) {
            if (contact != null) {
                items.add(new ContactItemGroup().setContact(contact).withIdentifier(100 + contacts.indexOf(contact)));
            }
        }
        itemAdapter.add(items);

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                decoration.invalidateHeaders();
            }
        });

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);

        refreshView();
    }

    private void showDialog() {

        new MaterialDialog.Builder(getActivity()).title(R.string.show_message_count).items(R.array.numberCountGroup).itemsCallback(new MaterialDialog.ListCallback() {
            @Override public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0:
                        count = 0;
                        if (onSelectedList != null) {
                            onSelectedList.getSelectedList(true, "", count, getSelectedList());
                        }
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                    case 1:
                        count = (int) lastId;
                        if (onSelectedList != null) {

                            onSelectedList.getSelectedList(true, "", count, getSelectedList());
                        }
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                    case 2:
                        count = 50;
                        if (onSelectedList != null) {
                            onSelectedList.getSelectedList(true, "", count, getSelectedList());
                        }
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                    case 3:
                        dialog.dismiss();
                        new MaterialDialog.Builder(getActivity()).title(R.string.customs)
                            .positiveText(getString(R.string.B_ok))
                            .alwaysCallInputCallback()
                            .widgetColor(getResources().getColor(R.color.toolbar_background))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (onSelectedList != null) {
                                        onSelectedList.getSelectedList(true, "", count, getSelectedList());
                                    }
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            })
                            .inputType(InputType.TYPE_CLASS_PHONE)
                            .input(getString(R.string.count_of_show_message), "50", new MaterialDialog.InputCallback() {
                                @Override public void onInput(MaterialDialog dialog, CharSequence input) {
                                    if (input.toString() != null && !input.toString().isEmpty()) {
                                        count = Integer.parseInt(input.toString());
                                    } else {
                                        count = 0;
                                    }
                                }
                            })
                            .show();
                        break;
                }
            }
        }).show();
    }

    private void refreshView() {

        int selectedNumber = 0;
        textString = "";

        int size = contacts.size();

        for (int i = 0; i < size; i++) {
            if (contacts.get(i).isSelected) {
                selectedNumber++;
                textString += contacts.get(i).displayName + ",";
            }
        }

        txtNumberOfMember.setText(selectedNumber + " / " + size);
        // sizeTextEdittext = textString.length();
        edtSearch.setText("");
    }

    private ArrayList<StructContactInfo> getSelectedList() {

        ArrayList<StructContactInfo> list = new ArrayList<>();

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected) {
                list.add(contacts.get(i));
            }
        }

        return list;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
