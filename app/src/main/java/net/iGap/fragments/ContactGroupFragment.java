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
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
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
import net.iGap.helper.GoToChatActivity;
import net.iGap.interfaces.OnChannelAddMember;
import net.iGap.interfaces.OnGroupAddMember;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.ContactChip;
import net.iGap.module.Contacts;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChannelAddMember;
import net.iGap.request.RequestGroupAddMember;

public class ContactGroupFragment extends BaseFragment {
    private FastAdapter fastAdapter;
    private TextView txtStatus;
    private TextView txtNumberOfMember;
    //private EditText edtSearch;
    private ChipsInput chipsInput;
    private String textString = "";
    private String participantsLimit = "5000";

    private long roomId;
    private int countAddMemberResponse = 0;
    private int countAddMemberRequest = 0;
    private static ProtoGlobal.Room.Type type;
    private String typeCreate;
    private List<ContactChip> mContactList = new ArrayList<>();
    private int sizeTextEditText = 0;
    private List<StructContactInfo> contacts;
    private boolean isRemove = true;

    public static ContactGroupFragment newInstance() {
        return new ContactGroupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_contact_group, container, false));
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hideProgressBar(); // some times touch screen remind lock so this method do unlock

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            roomId = bundle.getLong("RoomId");
            typeCreate = bundle.getString("TYPE");
            if (bundle.getString("LIMIT") != null) participantsLimit = bundle.getString("LIMIT");
        }

        view.findViewById(R.id.fcg_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        txtStatus = (TextView) view.findViewById(R.id.fcg_txt_status);
        txtNumberOfMember = (TextView) view.findViewById(R.id.fcg_txt_number_of_member);
        txtNumberOfMember.setText("0" + "/" + participantsLimit + " " + G.fragmentActivity.getResources().getString(R.string.member));

        if (typeCreate.equals("CHANNEL")) {
            txtNumberOfMember.setText("Add Members");
        }

        //edtSearch = (EditText) view.findViewById(R.id.fcg_edt_search);
        chipsInput = (ChipsInput) view.findViewById(R.id.chips_input);


        RippleView rippleBack = (RippleView) view.findViewById(R.id.fcg_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.onBackPressed();
            }
        });

        RippleView rippleDone = (RippleView) view.findViewById(R.id.fcg_ripple_done);
        rippleDone.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (typeCreate.equals("CHANNEL")) { // addMemberChannel
                    G.onChannelAddMember = new OnChannelAddMember() {
                        @Override
                        public void onChannelAddMember(Long RoomId, Long UserId, ProtoGlobal.ChannelRoom.Role role) {
                            countAddMemberResponse++;
                            if (countAddMemberResponse == countAddMemberRequest) {
                                addMember(RoomId, ProtoGlobal.Room.Type.CHANNEL);
                            }
                        }

                        @Override
                        public void onError(int majorCode, int minorCode) {
                            countAddMemberResponse++;
                            if (countAddMemberResponse == countAddMemberRequest) {
                                addMember(roomId, ProtoGlobal.Room.Type.CHANNEL);
                            }
                        }

                        @Override
                        public void onTimeOut() {

                        }
                    };

                    ArrayList<Long> list = getSelectedList();
                    if (list.size() > 0) {
                        for (long peerId : list) {
                            new RequestChannelAddMember().channelAddMember(roomId, peerId, 0);
                        }
                    } else {
                        if (isAdded()) {
                            removeFromBaseFragment(ContactGroupFragment.this);
                            new GoToChatActivity(ContactGroupFragment.this.roomId).startActivity();
                        }
                    }
                }

                if (typeCreate.equals("GROUP")) { //  addMemberGroup
                    G.onGroupAddMember = new OnGroupAddMember() {
                        @Override
                        public void onGroupAddMember(Long roomId, Long UserId) {
                            countAddMemberResponse++;
                            if (countAddMemberResponse == countAddMemberRequest) {
                                addMember(roomId, ProtoGlobal.Room.Type.GROUP);
                            }
                        }

                        @Override
                        public void onError(int majorCode, int minorCode) {
                            countAddMemberResponse++;
                            if (countAddMemberResponse == countAddMemberRequest) {
                                addMember(roomId, ProtoGlobal.Room.Type.GROUP);
                            }
                        }
                    };
                    /**
                     * request add member for group
                     *
                     */
                    ArrayList<Long> list = getSelectedList();
                    if (list.size() > 0) {
                        for (long peerId : list) {
                            new RequestGroupAddMember().groupAddMember(roomId, peerId, 0);
                        }
                    } else {

                        if (isAdded()) {
                            removeFromBaseFragment(ContactGroupFragment.this);
                            new GoToChatActivity(ContactGroupFragment.this.roomId).startActivity();
                        }

                    }
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
        contacts = Contacts.retrieve(null);

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
    }

    private void addMember(long roomId, ProtoGlobal.Room.Type roomType) {
        RealmRoom.addOwnerToDatabase(roomId, roomType);
        RealmRoom.updateMemberCount(roomId, roomType, countAddMemberRequest);
        if (isAdded()) {
            removeFromBaseFragment(ContactGroupFragment.this);
            new GoToChatActivity(roomId).startActivity();
        }
    }

    private void notifyAdapter(ContactItemGroup item, int position) {
        item.mContact.isSelected = !item.mContact.isSelected;
        fastAdapter.notifyItemChanged(position);
        refreshView();
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRemove = true;
            }
        }, 50);
    }


    private void refreshView() {
        int selectedNumber = 0;
        textString = "";
        int size = contacts.size();
        for (int i = 0; i < size; i++) {
            if (contacts.get(i).isSelected) {
                textString += contacts.get(i).displayName + ",";
                selectedNumber++;
            }
        }

        if (typeCreate.equals("CHANNEL")) {
            txtNumberOfMember.setVisibility(View.GONE);
        }
        //  sizeTextEditText = textString.length();
        txtNumberOfMember.setText(selectedNumber + "/" + participantsLimit + " " + G.fragmentActivity.getResources().getString(R.string.member));

        //edtSearch.setText("");
    }

    private ArrayList<Long> getSelectedList() {
        ArrayList<Long> list = new ArrayList<>();

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected) {
                countAddMemberRequest++;
                list.add(contacts.get(i).peerId);
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

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (G.fragmentActivity != null) {
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (G.fragmentActivity != null) {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

}
