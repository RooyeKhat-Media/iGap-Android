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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.StickyHeaderAdapter;
import net.iGap.adapter.items.ContactItemGroup;
import net.iGap.interfaces.OnSelectedList;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.ContactChip;
import net.iGap.module.structs.StructContactInfo;

public class ShowCustomList extends BaseFragment {
    private static List<StructContactInfo> contacts;
    private static OnSelectedList onSelectedList;
    private FastAdapter fastAdapter;
    private TextView txtStatus;
    private TextView txtNumberOfMember;
    //private EditText edtSearch;
    private String textString = "";
    private int sizeTextEdittext = 0;
    private boolean dialogShowing = false;
    private long lastId = 0;
    private int count = 0;
    private boolean singleSelect = false;
    private RippleView rippleDown;
    private List<ContactChip> mContactList = new ArrayList<>();
    private ChipsInput chipsInput;
    private boolean isRemove = true;

    public static ShowCustomList newInstance(List<StructContactInfo> list, OnSelectedList onSelectedListResult) {
        onSelectedList = onSelectedListResult;
        contacts = list;

        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).isSelected = false;
        }

        return new ShowCustomList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_contact_group, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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

        txtStatus = (TextView) view.findViewById(R.id.fcg_txt_status);
        txtNumberOfMember = (TextView) view.findViewById(R.id.fcg_txt_number_of_member);
        //edtSearch = (EditText) view.findViewById(R.id.fcg_edt_search);
        chipsInput = (ChipsInput) view.findViewById(R.id.chips_input);


        RippleView rippleBack = (RippleView) view.findViewById(R.id.fcg_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });

        rippleDown = (RippleView) view.findViewById(R.id.fcg_ripple_done);
        rippleDown.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (dialogShowing) {
                    showDialog();
                } else {
                    if (onSelectedList != null) {
                        onSelectedList.getSelectedList(true, "", 0, getSelectedList());
                    }
                    popBackStackFragment();
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
        itemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<ContactItemGroup>() {
            @Override
            public boolean filter(ContactItemGroup item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactItemGroup>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, ContactItemGroup item, int position) {

                if (item.mContact.isSelected) {
                    chipsInput.removeChipByLabel(item.mContact.displayName);
                } else {
                    Uri uri = null;
                    if (item.mContact.avatar != null && item.mContact.avatar.getFile() != null && item.mContact.avatar.getFile().getLocalThumbnailPath() != null) {
                        uri = Uri.fromFile(new File(item.mContact.avatar.getFile().getLocalThumbnailPath()));
                    }
                    if (uri == null) {

                        Drawable d = new BitmapDrawable(getResources(), net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) G.context.getResources().getDimension(R.dimen.dp60), item.mContact.initials, item.mContact.color));
                        chipsInput.addChip(item.mContact.peerId, d, item.mContact.displayName, "");
                    } else {
                        chipsInput.addChip(item.mContact.peerId, uri, item.mContact.displayName, "");
                    }

                }
                if (isRemove) {
                    notifyAdapter(item, position);
                }

                return false;
            }
        });

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
                items.add(new ContactItemGroup().setContact(contact).withIdentifier(contact.peerId));
                Uri uri = null;
                if (contact.avatar != null && contact.avatar.getFile() != null && contact.avatar.getFile().getLocalThumbnailPath() != null) {
                    uri = Uri.fromFile(new File(contact.avatar.getFile().getLocalThumbnailPath()));
                }

                ContactChip contactChip;
                if (uri == null) {
                    Drawable d = new BitmapDrawable(getResources(), net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) G.context.getResources().getDimension(R.dimen.dp60), contact.initials, contact.color));
                    contactChip = new ContactChip(contact.peerId, d, contact.displayName);
                } else {
                    contactChip = new ContactChip(contact.peerId, uri, contact.displayName);
                }

                mContactList.add(contactChip);
            }
        }

        chipsInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        chipsInput.setFilterableList(mContactList);

        itemAdapter.add(items);

        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                // chip added
                // newSize is the size of the updated selected chip list

                notifyAdapter(((ContactItemGroup) fastAdapter.getItem(fastAdapter.getPosition((Long) chip.getId()))), fastAdapter.getPosition((Long) chip.getId()));
                isRemove = false;

            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                notifyAdapter(((ContactItemGroup) fastAdapter.getItem(fastAdapter.getPosition((Long) chip.getId()))), fastAdapter.getPosition((Long) chip.getId()));
                isRemove = false;
            }

            @Override
            public void onTextChanged(CharSequence text) {
                // text changed
            }
        });

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);

        refreshView();
    }

    private void notifyAdapter(ContactItemGroup item, int position) {

        item.mContact.isSelected = !item.mContact.isSelected;
        fastAdapter.notifyItemChanged(position);
        if (singleSelect) {
            if (onSelectedList != null) {
                onSelectedList.getSelectedList(true, "", 0, getSelectedList());
            }
            // G.fragmentActivity.getSupportFragmentManager().popBackStack();

            popBackStackFragment();
        }
        refreshView();
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRemove = true;
            }
        }, 50);

    }

    private void showDialog() {

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.show_message_count).items(R.array.numberCountGroup).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0:
                        count = 0;
                        if (onSelectedList != null) {
                            onSelectedList.getSelectedList(true, "fromBegin", count, getSelectedList());
                        }
                        // G.fragmentActivity.getSupportFragmentManager().popBackStack();

                        popBackStackFragment();
                        break;
                    case 1:
                        count = 0;
                        if (onSelectedList != null) {

                            onSelectedList.getSelectedList(true, "fromNow", count, getSelectedList());
                        }
                        //  G.fragmentActivity.getSupportFragmentManager().popBackStack();

                        popBackStackFragment();

                        break;
                    case 2:
                        count = 50;
                        if (onSelectedList != null) {
                            onSelectedList.getSelectedList(true, "", count, getSelectedList());
                        }
                        // G.fragmentActivity.getSupportFragmentManager().popBackStack();

                        popBackStackFragment();

                        break;
                    case 3:
                        dialog.dismiss();
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.customs).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).alwaysCallInputCallback().widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (onSelectedList != null) {
                                    onSelectedList.getSelectedList(true, "", count, getSelectedList());
                                }
                                //  G.fragmentActivity.getSupportFragmentManager().popBackStack();

                                popBackStackFragment();

                            }
                        }).inputType(InputType.TYPE_CLASS_NUMBER).input(G.fragmentActivity.getResources().getString(R.string.count_of_show_message), null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.toString() != null && !input.toString().isEmpty()) {
                                    if (input.length() < 5) {
                                        count = Integer.parseInt(input.toString());
                                    } else {
                                        count = 0;
                                    }

                                } else {
                                    count = 0;
                                }
                            }
                        }).show();
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
        //edtSearch.setText("");
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
