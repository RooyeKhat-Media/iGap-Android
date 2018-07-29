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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentContactsProfileBinding;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnReport;
import net.iGap.interfaces.OnUserContactDelete;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.MEditText;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoUserReport;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserContactsEdit;
import net.iGap.request.RequestUserContactsUnblock;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserReport;
import net.iGap.viewmodel.FragmentContactsProfileViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static android.content.Context.CLIPBOARD_SERVICE;
import static net.iGap.G.context;
import static net.iGap.module.Contacts.showLimitDialog;


public class FragmentContactsProfile extends BaseFragment {

    private static final String ROOM_ID = "RoomId";
    private static final String PEER_ID = "peerId";
    private static final String ENTER_FROM = "enterFrom";
    private long userId = 0;
    private long roomId = 0;
    private String enterFrom;
    private String report;
    private FragmentContactsProfileBinding fragmentContactsProfileBinding;
    private FragmentContactsProfileViewModel fragmentContactsProfileViewModel;

    public static FragmentContactsProfile newInstance(long roomId, long peerId, String enterFrom) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        args.putLong(PEER_ID, peerId);
        args.putString(ENTER_FROM, enterFrom);
        FragmentContactsProfile fragment = new FragmentContactsProfile();
        fragment.setArguments(args);
        return fragment;
    }

    private void initDataBinding() {
        fragmentContactsProfileViewModel = new FragmentContactsProfileViewModel(fragmentContactsProfileBinding, roomId, userId, enterFrom);
        fragmentContactsProfileBinding.setViewModel(fragmentContactsProfileViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContactsProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts_profile, container, false);
        return attachToSwipeBack(fragmentContactsProfileBinding.getRoot());
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle extras = getArguments();
        userId = extras.getLong(PEER_ID);
        roomId = extras.getLong(ROOM_ID);
        enterFrom = extras.getString(ENTER_FROM);
        if (enterFrom == null) {
            enterFrom = "";
        }

        initDataBinding();

        fragmentContactsProfileBinding.chiRippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });

        fragmentContactsProfileBinding.chiFabSetPic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.fabBottom)));
        fragmentContactsProfileBinding.chiFabSetPic.setColorFilter(Color.WHITE);

        fragmentContactsProfileBinding.chiFabSetPic.setOnClickListener(new View.OnClickListener() { //fab button
            @Override
            public void onClick(View view) {

                if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString()) || enterFrom.equals("Others")) { // Others is from FragmentMapUsers adapter

                    final Realm realm = Realm.getDefaultInstance();
                    final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();

                    if (realmRoom != null) {
                        new HelperFragment().removeAll(true);
                        new GoToChatActivity(realmRoom.getId()).startActivity();
                    } else {
                        G.onChatGetRoom = new OnChatGetRoom() {
                            @Override
                            public void onChatGetRoom(final ProtoGlobal.Room room) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new HelperFragment().removeAll(true);
                                        new GoToChatActivity(room.getId()).setPeerID(userId).startActivity();
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

                        new RequestChatGetRoom().chatGetRoom(userId);
                    }
                    realm.close();
                } else {
                    popBackStackFragment();
                }
            }
        });

        if (fragmentContactsProfileViewModel.showNumber.get()) {
            fragmentContactsProfileBinding.chiLayoutNickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (fragmentContactsProfileViewModel.contactName.get() == null) {
                        return;
                    }

                    final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
                    layoutNickname.setOrientation(LinearLayout.VERTICAL);

                    String splitNickname[] = fragmentContactsProfileViewModel.contactName.get().split(" ");
                    String firsName = "";
                    String lastName = "";
                    StringBuilder stringBuilder = null;
                    if (splitNickname.length > 1) {

                        lastName = splitNickname[splitNickname.length - 1];
                        stringBuilder = new StringBuilder();
                        for (int i = 0; i < splitNickname.length - 1; i++) {

                            stringBuilder.append(splitNickname[i]).append(" ");
                        }
                        firsName = stringBuilder.toString();
                    } else {
                        firsName = splitNickname[0];
                    }
                    final View viewFirstName = new View(G.fragmentActivity);
                    viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));

                    LinearLayout.LayoutParams viewParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

                    TextInputLayout inputFirstName = new TextInputLayout(G.fragmentActivity);
                    final EmojiEditTextE edtFirstName = new EmojiEditTextE(G.fragmentActivity);
                    edtFirstName.setHint(R.string.first_name);
                    edtFirstName.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    edtFirstName.setTypeface(G.typeface_IRANSansMobile);
                    edtFirstName.setText(firsName);
                    edtFirstName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
                    edtFirstName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
                    edtFirstName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
                    edtFirstName.setPadding(0, 8, 0, 8);
                    edtFirstName.setSingleLine(true);
                    inputFirstName.addView(edtFirstName);
                    inputFirstName.addView(viewFirstName, viewParams);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        edtFirstName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
                    }

                    final View viewLastName = new View(G.fragmentActivity);
                    viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));

                    TextInputLayout inputLastName = new TextInputLayout(G.fragmentActivity);
                    final MEditText edtLastName = new MEditText(G.fragmentActivity);
                    edtLastName.setHint(R.string.last_name);
                    edtLastName.setTypeface(G.typeface_IRANSansMobile);
                    edtLastName.setText(lastName);
                    edtLastName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
                    edtLastName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
                    edtLastName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
                    edtLastName.setPadding(0, 8, 0, 8);
                    edtLastName.setSingleLine(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        edtLastName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
                    }
                    inputLastName.addView(edtLastName);
                    inputLastName.addView(viewLastName, viewParams);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 0, 15);
                    LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lastNameLayoutParams.setMargins(0, 15, 0, 10);

                    layoutNickname.addView(inputFirstName, layoutParams);
                    layoutNickname.addView(inputLastName, lastNameLayoutParams);

                    final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.pu_nikname_profileUser))
                            .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                            .customView(layoutNickname, true)
                            .widgetColor(Color.parseColor(G.appBarColor))
                            .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                            .build();

                    final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                    positive.setEnabled(false);

                    edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                viewFirstName.setBackgroundColor(Color.parseColor(G.appBarColor));
                            } else {
                                viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                            }
                        }
                    });

                    edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                viewLastName.setBackgroundColor(Color.parseColor(G.appBarColor));
                            } else {
                                viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                            }
                        }
                    });

                    final String finalFirsName = firsName;
                    edtFirstName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            if (!edtFirstName.getText().toString().equals(finalFirsName)) {
                                positive.setEnabled(true);
                            } else {
                                positive.setEnabled(false);
                            }
                        }
                    });

                    final String finalLastName = lastName;
                    edtLastName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (!edtLastName.getText().toString().equals(finalLastName)) {
                                positive.setEnabled(true);
                            } else {
                                positive.setEnabled(false);
                            }
                        }
                    });

                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            long po = Long.parseLong(fragmentContactsProfileViewModel.phone.get());
                            String firstName = edtFirstName.getText().toString().trim();
                            String lastName = edtLastName.getText().toString().trim();
                            new RequestUserContactsEdit().contactsEdit(userId, po, firstName, lastName);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }

        fragmentContactsProfileBinding.chiAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewGroup viewGroup = fragmentContactsProfileBinding.chiRootCircleImage;
                if (verticalOffset < -5) {
                    viewGroup.animate().alpha(0).setDuration(700);
                    viewGroup.setVisibility(View.GONE);
                    fragmentContactsProfileBinding.chiTxtTitleToolbarDisplayName.setVisibility(View.VISIBLE);
                    fragmentContactsProfileBinding.chiTxtTitleToolbarDisplayName.animate().alpha(1).setDuration(300);
                    fragmentContactsProfileBinding.chiTxtTitleToolbarLastSeen.setVisibility(View.VISIBLE);
                    fragmentContactsProfileBinding.chiTxtTitleToolbarLastSeen.animate().alpha(1).setDuration(300);
                } else {
                    viewGroup.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(1).setDuration(700);
                    fragmentContactsProfileBinding.chiTxtTitleToolbarDisplayName.setVisibility(View.GONE);
                    fragmentContactsProfileBinding.chiTxtTitleToolbarDisplayName.animate().alpha(0).setDuration(500);
                    fragmentContactsProfileBinding.chiTxtTitleToolbarLastSeen.setVisibility(View.GONE);
                    fragmentContactsProfileBinding.chiTxtTitleToolbarLastSeen.animate().alpha(0).setDuration(500);
                }
            }
        });

        fragmentContactsProfileBinding.chiRippleMenuPopup.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                showPopUp();
            }
        });

        fragmentContactsProfileBinding.chiLayoutPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HelperPermission.getContactPermision(G.fragmentActivity, new OnGetPermission() {
                        @Override
                        public void Allow() throws IOException {
                            showPopupPhoneNumber(fragmentContactsProfileBinding.chiLayoutPhoneNumber, fragmentContactsProfileViewModel.phone.get());
                        }

                        @Override
                        public void deny() {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        fragmentContactsProfileBinding.chiLayoutSharedMedia.setOnClickListener(new View.OnClickListener() {// go to the ActivityMediaChanel
            @Override
            public void onClick(View view) {
                new HelperFragment(FragmentShearedMedia.newInstance(fragmentContactsProfileViewModel.shearedId)).setReplace(false).load();
            }
        });

        fragmentContactsProfileBinding.chiTxtClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(G.fragmentActivity.getResources().getString(R.string.clear_this_chat), G.fragmentActivity.getResources().getString(R.string.clear), G.fragmentActivity.getResources().getString(R.string.cancel));
            }
        });

        fragmentContactsProfileBinding.chiTxtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "CONTACT");
                bundle.putLong("ID", roomId);
                fragmentNotification.setArguments(bundle);
                new HelperFragment(fragmentNotification).setReplace(false).load();
            }
        });

        getUserInfo(); // client should send request for get user info because need to update user online timing
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentContactsProfileViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentContactsProfileViewModel.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentContactsProfileViewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentContactsProfileViewModel.onDestroy();
    }

    /**
     * ************************************ methods ************************************
     */
    private void showPopupPhoneNumber(View v, String number) {

        boolean isExist = false;
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur != null) {
                isExist = cur.moveToFirst();
            }
        } finally {
            if (cur != null) cur.close();
        }

        if (isExist) {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).items(R.array.phone_number2).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    switch (which) {
                        case 0:
                            String call = "+" + Long.parseLong(fragmentContactsProfileViewModel.phone.get());
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + Uri.encode(call.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } catch (Exception ex) {
                                ex.getStackTrace();
                            }
                            break;
                        case 1:
                            String copy;
                            copy = fragmentContactsProfileViewModel.phone.get();
                            ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", copy);
                            clipboard.setPrimaryClip(clip);
                            break;
                    }
                }
            }).show();
        } else {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).items(R.array.phone_number).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    switch (which) {
                        case 0:

                            String name = fragmentContactsProfileViewModel.contactName.get();
                            String phone = "+" + fragmentContactsProfileViewModel.phone.get();

                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

                            //------------------------------------------------------ Names

                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());

                            //------------------------------------------------------ Mobile Number

                            ops.add(ContentProviderOperation.
                                    newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    .build());

                            try {
                                G.context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                addContactToServer();
                                Toast.makeText(G.context, R.string.save_ok, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(G.context, G.fragmentActivity.getResources().getString(R.string.exception) + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case 1:

                            String call = "+" + Long.parseLong(fragmentContactsProfileViewModel.phone.get());
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + Uri.encode(call.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } catch (Exception ex) {

                                ex.getStackTrace();
                            }
                            break;
                        case 2:

                            ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", fragmentContactsProfileViewModel.phone.get());
                            clipboard.setPrimaryClip(clip);

                            break;
                    }
                }
            }).show();
        }
    }

    /**
     * import contact to server with True force
     */
    private void addContactToServer() {

        if (RealmUserInfo.isLimitImportContacts()) {
            showLimitDialog();
            return;
        }

        List<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = fragmentContactsProfileViewModel.firstName;
        contact.lastName = fragmentContactsProfileViewModel.lastName;
        contact.phone = fragmentContactsProfileViewModel.phone.get() + "";

        contacts.add(contact);

        new RequestUserContactImport().contactImport(contacts, true);
    }

    private void showPopUp() {

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
        ViewGroup root3 = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);
        ViewGroup root4 = (ViewGroup) v.findViewById(R.id.dialog_root_item4_notification);

        TextView txtBlockUser = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtClearHistory = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
        TextView txtDeleteContact = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
        TextView txtReport = (TextView) v.findViewById(R.id.dialog_text_item4_notification);

        TextView iconBlockUser = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);

        TextView iconClearHistory = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconClearHistory.setText(G.fragmentActivity.getResources().getString(R.string.md_clearHistory));

        TextView iconDeleteContact = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
        iconDeleteContact.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));

        TextView iconReport = (TextView) v.findViewById(R.id.dialog_icon_item4_notification);
        iconReport.setText(G.fragmentActivity.getResources().getString(R.string.md_igap_account_alert));

        root1.setVisibility(View.VISIBLE);
        root2.setVisibility(View.VISIBLE);
        root3.setVisibility(View.VISIBLE);
        root4.setVisibility(View.VISIBLE);
        if (G.userId == userId) {
            root1.setVisibility(View.GONE);
            root3.setVisibility(View.GONE);
        }

        if (fragmentContactsProfileViewModel.disableDeleteContact) {
            root3.setVisibility(View.GONE);
        }

        if (fragmentContactsProfileViewModel.isBlockUser) {
            txtBlockUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
            iconBlockUser.setText(G.fragmentActivity.getResources().getString(R.string.md_unblock));
        } else {
            txtBlockUser.setText(G.fragmentActivity.getResources().getString(R.string.block_user));
            iconBlockUser.setText(G.fragmentActivity.getResources().getString(R.string.md_block));
        }
        txtClearHistory.setText(G.fragmentActivity.getResources().getString(R.string.clear_history));
        txtDeleteContact.setText(G.fragmentActivity.getResources().getString(R.string.delete_contact));
        txtReport.setText(G.fragmentActivity.getResources().getString(R.string.report));


        if (RealmRoom.isNotificationServices(roomId)) {
            root4.setVisibility(View.GONE);
        }

        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                blockOrUnblockUser();
            }
        });
        root2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (FragmentChat.onComplete != null) {
                            FragmentChat.onComplete.complete(false, roomId + "", "");
                        }
                    }
                }).negativeText(R.string.B_cancel).show();

            }
        });
        root3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        deleteContact();
                    }
                }).negativeText(R.string.B_cancel).show();

            }
        });
        root4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openDialogReport();
            }

        });
    }

    private void openDialogReport() {

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();
        if (v == null) {
            return;
        }
        DialogAnimation.animationDown(dialog);
        dialog.show();

        ViewGroup rootSpam = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup rootAbuse = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
        ViewGroup rootFaceAccount = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);
        ViewGroup rootOther = (ViewGroup) v.findViewById(R.id.dialog_root_item4_notification);

        TextView txtSpam = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtAbuse = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
        TextView txtFakeAccount = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
        TextView txtOther = (TextView) v.findViewById(R.id.dialog_text_item4_notification);

        TextView iconSpam = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
        iconSpam.setText(G.fragmentActivity.getResources().getString(R.string.md_back_arrow_reply));
        iconSpam.setVisibility(View.GONE);

        TextView iconAbuse = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconAbuse.setText(G.fragmentActivity.getResources().getString(R.string.md_copy));
        iconAbuse.setVisibility(View.GONE);

        TextView iconFakeAccount = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
        iconFakeAccount.setText(G.fragmentActivity.getResources().getString(R.string.md_share_button));
        iconFakeAccount.setVisibility(View.GONE);

        TextView iconOther = (TextView) v.findViewById(R.id.dialog_icon_item4_notification);
        iconOther.setText(G.fragmentActivity.getResources().getString(R.string.md_forward));
        iconOther.setVisibility(View.GONE);


        rootSpam.setVisibility(View.VISIBLE);
        rootAbuse.setVisibility(View.VISIBLE);
        rootFaceAccount.setVisibility(View.VISIBLE);
        rootOther.setVisibility(View.VISIBLE);

        txtSpam.setText(R.string.st_Spam);
        txtAbuse.setText(R.string.st_Abuse);
        txtFakeAccount.setText(R.string.st_FakeAccount);
        txtOther.setText(R.string.st_Other);

        rootSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new RequestUserReport().userReport(userId, ProtoUserReport.UserReport.Reason.SPAM, "");
            }
        });
        rootAbuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new RequestUserReport().userReport(userId, ProtoUserReport.UserReport.Reason.ABUSE, "");
            }
        });
        rootFaceAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                new RequestUserReport().userReport(userId, ProtoUserReport.UserReport.Reason.FAKE_ACCOUNT, "");

            }
        });
        rootOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final MaterialDialog dialogReport = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.report).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE).alwaysCallInputCallback().input(G.context.getString(R.string.description), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        report = input.toString();
                        if (input.length() > 0) {
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(true);
                        } else {
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(false);
                        }
                    }
                }).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new RequestUserReport().userReport(roomId, ProtoUserReport.UserReport.Reason.OTHER, report);
                    }
                }).negativeText(R.string.cancel).build();

                final View positive = dialogReport.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                DialogAnimation.animationDown(dialogReport);

                dialogReport.show();

            }
        });

        G.onReport = new OnReport() {
            @Override
            public void success() {
                error(G.fragmentActivity.getResources().getString(R.string.st_send_report));
            }
        };

    }

    private void blockOrUnblockUser() {

        if (fragmentContactsProfileViewModel.isBlockUser) {

            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.unblock_the_user).content(R.string.unblock_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestUserContactsUnblock().userContactsUnblock(userId);
                }
            }).negativeText(R.string.cancel).show();

        } else {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.block_the_user).content(R.string.block_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestUserContactsBlock().userContactsBlock(userId);
                }
            }).negativeText(R.string.cancel).show();
        }
    }

    private void showAlertDialog(String message, String positive, String negative) { // alert dialog for block or clear user

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clear_history).content(message).positiveText(positive).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                clearHistory();
                if (FragmentChat.onComplete != null) {
                    FragmentChat.onComplete.complete(false, roomId + "", "");
                }
            }
        }).negativeText(negative).show();
    }

    private void deleteContact() {
        G.onUserContactdelete = new OnUserContactDelete() {
            @Override
            public void onContactDelete() {
                /**
                 * get user info after delete it for show nickname
                 */
                getUserInfo();
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };
        new RequestUserContactsDelete().contactsDelete(fragmentContactsProfileViewModel.phone.get());
    }

    private void getUserInfo() {
        new RequestUserInfo().userInfo(userId);
    }

    private void clearHistory() {
        RealmRoomMessage.clearHistoryMessage(fragmentContactsProfileViewModel.shearedId);
    }

    private void error(String error) {
        if (isAdded()) {
            try {
                final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                snack.setAction(G.fragmentActivity.getResources().getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }
}
