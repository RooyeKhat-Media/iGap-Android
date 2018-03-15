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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vicmikhailau.maskededittext.MaskedEditText;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAddContact;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.OnCountryCallBack;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.request.RequestUserContactImport;

import java.io.IOException;
import java.util.ArrayList;

import static net.iGap.G.context;

public class FragmentAddContact extends BaseFragment {

    public static OnCountryCallBack onCountryCallBack;
    private EditText edtFirstName;
    private EditText edtLastName;
    private MaskedEditText edtPhoneNumber;
    private ViewGroup parent;
    private RippleView rippleSet;
    private MaterialDesignTextView txtSet;
    private TextView txtChooseCountry;
    private TextView txtCodeCountry;

    public static FragmentAddContact newInstance() {
        return new FragmentAddContact();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_add_contact, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initComponent(view);
    }

    private void initComponent(final View view) {

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.ac_txt_back);
        final RippleView rippleBack = (RippleView) view.findViewById(R.id.ac_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                changePage(rippleView);
            }
        });

        view.findViewById(R.id.fac_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));


        txtSet = (MaterialDesignTextView) view.findViewById(R.id.ac_txt_set);
        txtSet.setTextColor(G.context.getResources().getColor(R.color.line_edit_text));

        parent = (ViewGroup) view.findViewById(R.id.ac_layoutParent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        txtCodeCountry = (TextView) view.findViewById(R.id.ac_txt_codeCountry);
        txtChooseCountry = (TextView) view.findViewById(R.id.ac_txt_chooseCountry);
        txtChooseCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(new FragmentChooseCountry()).setReplace(false).load();
                closeKeyboard(v);
            }
        });

        edtFirstName = (EditText) view.findViewById(R.id.ac_edt_firstName);
        final View viewFirstName = view.findViewById(R.id.ac_view_firstName);
        edtLastName = (EditText) view.findViewById(R.id.ac_edt_lastName);
        final View viewLastName = view.findViewById(R.id.ac_view_lastName);
        edtPhoneNumber = (MaskedEditText) view.findViewById(R.id.ac_edt_phoneNumber);
        final View viewPhoneNumber = view.findViewById(R.id.ac_view_phoneNumber);

        edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                } else {
                    viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });
        edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                } else {
                    viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });
        edtPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewPhoneNumber.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                } else {
                    viewPhoneNumber.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                isEnableSetButton();
            }
        });
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEnableSetButton();
            }
        });
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEnableSetButton();
            }
        });

        G.fragmentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        rippleSet = (RippleView) view.findViewById(R.id.ac_ripple_set);
        rippleSet.setEnabled(false);
        rippleSet.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(final RippleView rippleView) {

                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.add_to_list_contact).content(R.string.text_add_to_list_contact).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        addContactToServer();
                        final int permissionWriteContact = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS);
                        if (permissionWriteContact != PackageManager.PERMISSION_GRANTED) {
                            try {
                                HelperPermission.getContactPermision(G.fragmentActivity, null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            addToContactList(rippleView);
                        }
                    }
                }).negativeText(R.string.no).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        addContactToServer();
                        dialog.dismiss();
                        G.fragmentActivity.onBackPressed();
                    }
                }).show();
            }
        });

        onCountryCallBack = new OnCountryCallBack() {
            @Override
            public void countryName(final String nameCountry, final String code, final String mask) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtChooseCountry.setText(nameCountry);
                        txtCodeCountry.setText("+" + code);
                        edtPhoneNumber.setText("");
                        if (!mask.equals(" ")) {
                            edtPhoneNumber.setMask(mask.replace("X", "#").replace(" ", "-"));
                        } else {
                            edtPhoneNumber.setMask("##################");
                        }
                    }
                });
            }
        };

    }

    private void isEnableSetButton() {

        if ((edtFirstName.getText().toString().length() > 0 || edtLastName.getText().toString().length() > 0) && edtPhoneNumber.getText().toString().length() > 0) {

            txtSet.setTextColor(G.context.getResources().getColor(R.color.white));
            rippleSet.setEnabled(true);
        } else {
            txtSet.setTextColor(G.context.getResources().getColor(R.color.line_edit_text));
            rippleSet.setEnabled(false);
        }
    }

    private void changePage(View view) {
        InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        removeFromBaseFragment(FragmentAddContact.this);
    }

    /**
     * import contact to server with True force
     */
    private void addContactToServer() {

        String _phone = edtPhoneNumber.getText().toString();
        String codeCountry = txtCodeCountry.getText().toString();

        String saveNumber;

        if (edtPhoneNumber.getText().toString().startsWith("0")) {
            saveNumber = codeCountry + _phone.substring(1, _phone.length());
        } else {
            saveNumber = codeCountry + _phone;
        }

        ArrayList<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = edtFirstName.getText().toString();
        contact.lastName = edtLastName.getText().toString();
        contact.phone = saveNumber;

        contacts.add(contact);

        new RequestUserContactImport().contactImport(contacts, true);
    }

    private void addToContactList(View view) {
        if (edtFirstName.getText().toString().length() > 0 || edtLastName.getText().toString().length() > 0) {
            if (edtPhoneNumber.getText().toString().length() > 0) {

                final String phone = edtPhoneNumber.getText().toString();
                final String firstName = edtFirstName.getText().toString();
                final String lastName = edtLastName.getText().toString();
                final String codeNumber = txtCodeCountry.getText().toString();
                String displayName = firstName + " " + lastName;
                HelperAddContact.addContact(displayName, codeNumber, phone);

                changePage(view);
            } else {
                Toast.makeText(G.context, R.string.please_enter_phone_number, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(G.context, R.string.please_enter_firstname_or_lastname, Toast.LENGTH_SHORT).show();
        }
    }

    private void closeKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }
}
