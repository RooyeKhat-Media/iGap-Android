package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.databinding.FragmentSettingBinding;
import net.iGap.fragments.FragmentBio;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentChatBackground;
import net.iGap.fragments.FragmentData;
import net.iGap.fragments.FragmentDeleteAccount;
import net.iGap.fragments.FragmentLanguage;
import net.iGap.fragments.FragmentMain;
import net.iGap.fragments.FragmentNotificationAndSound;
import net.iGap.fragments.FragmentPrivacyAndSecurity;
import net.iGap.fragments.FragmentSetting;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnUserProfileCheckUsername;
import net.iGap.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.interfaces.OnUserProfileSetGenderResponse;
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.interfaces.OnUserProfileUpdateUsername;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.module.AttachFile;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.MEditText;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.StartupActions;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoUserProfileCheckUsername;
import net.iGap.realm.RealmPrivacy;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileCheckUsername;
import net.iGap.request.RequestUserProfileSetEmail;
import net.iGap.request.RequestUserProfileSetGender;
import net.iGap.request.RequestUserProfileSetNickname;
import net.iGap.request.RequestUserProfileUpdateUsername;
import net.iGap.request.RequestUserSessionLogout;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;
import static net.iGap.G.onRefreshActivity;
import static net.iGap.R.string.log_out;

public class FragmentSettingViewModel {

    public static String pathSaveImage;
    public static int KEY_AD_DATA_PHOTO = -1;
    public static int KEY_AD_DATA_VOICE_MESSAGE = -1;
    public static int KEY_AD_DATA_VIDEO = -1;
    public static int KEY_AD_DATA_FILE = -1;
    public static int KEY_AD_DATA_MUSIC = -1;
    public static int KEY_AD_DATA_GIF = -1;
    public static int KEY_AD_WIFI_PHOTO = -1;
    public static int KEY_AD_WIFI_VOICE_MESSAGE = -1;
    public static int KEY_AD_WIFI_VIDEO = -1;
    public static int KEY_AD_WIFI_FILE = -1;
    public static int KEY_AD_WIFI_MUSIC = -1;
    public static int KEY_AD_WIFI_GIF = -1;
    public static int KEY_AD_ROAMING_PHOTO = -1;
    public static int KEY_AD_ROAMING_VOICE_MESSAGE = -1;
    public static int KEY_AD_ROAMING_VIDEO = -1;
    public static int KEY_AD_ROAMING_FILE = -1;
    public static int KEY_AD_ROAMING_MUSIC = -1;
    public static int KEY_AD_ROAMINGN_GIF = -1;
    static boolean isActiveRun = false;
    public long userId;
    public ObservableField<String> callbackSetName = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.first_name));
    public ObservableField<String> callbackTextSize = new ObservableField<>("16");
    public ObservableField<String> callbackSetTitleName = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.first_name));
    public ObservableField<String> callbackGander = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.set_gender));
    public ObservableField<String> callbackSetUserName = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_username));
    public ObservableField<String> callbackSetEmail = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.set_email));
    public ObservableField<String> callbackSetPhoneNumber = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_phoneNumber));
    public ObservableField<String> callbackSetBio = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_bio));
    public ObservableField<String> callbackLanguage = new ObservableField<>("English");
    public ObservableField<String> callbackDataShams = new ObservableField<>("Miladi");
    public ObservableField<String> callbackVersionApp = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.iGap_version));
    public ObservableField<Boolean> isAutoRotate = new ObservableField<>();
    public ObservableField<Boolean> isMultiTab = new ObservableField<>();
    public ObservableField<Boolean> isShowVote = new ObservableField<>();
    public ObservableField<Boolean> isSenderNameGroup = new ObservableField<>();
    public ObservableField<Boolean> isSendEnter = new ObservableField<>();
    public ObservableField<Boolean> isSaveGallery = new ObservableField<>();
    public ObservableField<Boolean> isAutoGif = new ObservableField<>();
    public ObservableField<Boolean> isCompress = new ObservableField<>();
    public ObservableField<Boolean> isTrim = new ObservableField<>();
    public ObservableField<Boolean> isDefaultPlayer = new ObservableField<>();
    public ObservableField<Boolean> isCrop = new ObservableField<>();
    public ObservableField<Boolean> isCameraButtonSheet = new ObservableField<>(true);


    private SharedPreferences sharedPreferences;
    private int poRbDialogTextSize = -1;
    private Uri uriIntent;
    private long idAvatar;
    private String userName;
    private String phoneName;
    private String userEmail;
    private String bio;
    private Realm mRealm;
    private RealmUserInfo realmUserInfo;
    private RealmPrivacy realmPrivacy;
    private RealmRegisteredInfo mRealmRegisteredInfo;
    private FragmentSetting fragmentSetting;
    private FragmentSettingBinding fragmentSettingBinding;
    private int[] fontSizeArray = {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};


    public FragmentSettingViewModel(FragmentSetting fragmentSetting, FragmentSettingBinding fragmentSettingBinding) {
        this.fragmentSetting = fragmentSetting;
        this.fragmentSettingBinding = fragmentSettingBinding;
        getInfo();
    }


    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================


    public void onClickRippleCircleImage(View view) {
        FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
        fragment.appBarLayout = fragmentSettingBinding.stFabSetPic;
        new HelperFragment(fragment).setReplace(false).load();
    }

    public void onClickRippleBack(View v) {

        G.fragmentActivity.onBackPressed();

    }

    public void onClickRippleMore(View view) {

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);

        TextView txtLogOut = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtDeleteAccount = (TextView) v.findViewById(R.id.dialog_text_item2_notification);

        TextView iconLogOut = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
        iconLogOut.setText(G.fragmentActivity.getResources().getString(R.string.md_exit_app));
        TextView iconDeleteAccount = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconDeleteAccount.setText(G.fragmentActivity.getResources().getString(R.string.md_delete_acc));

        root1.setVisibility(View.VISIBLE);
        root2.setVisibility(View.VISIBLE);

        txtLogOut.setText(G.fragmentActivity.getResources().getString(log_out));
        txtDeleteAccount.setText(G.fragmentActivity.getResources().getString(R.string.delete_account));


        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialog.dismiss();

                final MaterialDialog inDialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.dialog_content_custom, true).build();
                View v = inDialog.getCustomView();

                inDialog.show();

                TextView txtTitle = (TextView) v.findViewById(R.id.txtDialogTitle);
                txtTitle.setText(G.fragmentActivity.getResources().getString(log_out));

                TextView iconTitle = (TextView) v.findViewById(R.id.iconDialogTitle);
                iconTitle.setText(R.string.md_exit_app);

                TextView txtContent = (TextView) v.findViewById(R.id.txtDialogContent);
                txtContent.setText(R.string.content_log_out);

                TextView txtCancel = (TextView) v.findViewById(R.id.txtDialogCancel);
                TextView txtOk = (TextView) v.findViewById(R.id.txtDialogOk);

                txtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        inDialog.dismiss();

                        showProgressBar();

                        G.onUserSessionLogout = new OnUserSessionLogout() {
                            @Override
                            public void onUserSessionLogout() {

                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        HelperLogout.logout();
                                        hideProgressBar();
                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onTimeOut() {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressBar();
                                        if (view != null) {

                                            HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.error), false);

                                        }
                                    }
                                });
                            }
                        };

                        new RequestUserSessionLogout().userSessionLogout();
                    }
                });

                txtCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inDialog.dismiss();
                    }
                });

            }
        });

        root2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                final MaterialDialog inDialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.dialog_content_custom, true).build();
                View v = inDialog.getCustomView();

                inDialog.show();

                TextView txtTitle = (TextView) v.findViewById(R.id.txtDialogTitle);
                txtTitle.setText(G.fragmentActivity.getResources().getString(R.string.delete_account));

                TextView iconTitle = (TextView) v.findViewById(R.id.iconDialogTitle);
                iconTitle.setText(R.string.md_delete_acc);

                TextView txtContent = (TextView) v.findViewById(R.id.txtDialogContent);
                String text = G.fragmentActivity.getResources().getString(R.string.delete_account_text) + "\n" + G.fragmentActivity.getResources().getString(R.string.delete_account_text_desc);
                txtContent.setText(text);

                TextView txtCancel = (TextView) v.findViewById(R.id.txtDialogCancel);
                TextView txtOk = (TextView) v.findViewById(R.id.txtDialogOk);


                txtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inDialog.dismiss();
                        FragmentDeleteAccount fragmentDeleteAccount = new FragmentDeleteAccount();

                        Bundle bundle = new Bundle();
                        bundle.putString("PHONE", callbackSetPhoneNumber.get());
                        fragmentDeleteAccount.setArguments(bundle);
                        new HelperFragment(fragmentDeleteAccount).setReplace(false).load();
                    }
                });

                txtCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inDialog.dismiss();
                    }
                });
            }
        });

    }

    public void onClickNickname(View view) {


        final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
        layoutNickname.setOrientation(LinearLayout.VERTICAL);

        String splitNickname[] = callbackSetName.get().split(" ");
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
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        TextInputLayout inputFirstName = new TextInputLayout(G.fragmentActivity);
        final EmojiEditTextE edtFirstName = new EmojiEditTextE(G.fragmentActivity);
        edtFirstName.setHint(G.fragmentActivity.getResources().getString(R.string.fac_First_Name));
        edtFirstName.setText(firsName);
        edtFirstName.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edtFirstName.setTypeface(G.typeface_IRANSansMobile);
        edtFirstName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtFirstName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtFirstName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtFirstName.setPadding(0, 8, 0, 8);
        edtFirstName.setSingleLine(true);
        inputFirstName.addView(edtFirstName);
        inputFirstName.addView(viewFirstName, viewParams);
        final View viewLastName = new View(G.fragmentActivity);
        viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtFirstName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }

        TextInputLayout inputLastName = new TextInputLayout(G.fragmentActivity);
        final EmojiEditTextE edtLastName = new EmojiEditTextE(G.fragmentActivity);
        edtLastName.setHint(G.fragmentActivity.getResources().getString(R.string.fac_Last_Name));
        edtLastName.setText(lastName);
        edtLastName.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edtLastName.setTypeface(G.typeface_IRANSansMobile);
        edtLastName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtLastName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtLastName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
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

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_nickname)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).customView(layoutNickname, true).widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

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

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                G.onUserProfileSetNickNameResponse = new OnUserProfileSetNickNameResponse() {
                    @Override
                    public void onUserProfileNickNameResponse(final String nickName, String initials) {
                        //setAvatar();

                        RealmRoom.updateChatTitle(userId, nickName);

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressBar();
                            }
                        });
                    }

                    @Override
                    public void onUserProfileNickNameError(int majorCode, int minorCode) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressBar();
                            }
                        });
                    }

                    @Override
                    public void onUserProfileNickNameTimeOut() {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressBar();
                            }
                        });
                    }
                };


                String fullName = "";
                if (edtFirstName.length() == 0) {
                    fullName = " " + " " + edtLastName.getText().toString().trim();
                }
                if (edtLastName.length() == 0) {
                    fullName = edtFirstName.getText().toString().trim() + " " + " ";
                }
                if (edtLastName.length() > 0 && edtFirstName.length() > 0) {
                    fullName = edtFirstName.getText().toString().trim() + " " + edtLastName.getText().toString().trim();
                }

                new RequestUserProfileSetNickname().userProfileNickName(fullName);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void onClickUserName(View view) {

        final LinearLayout layoutUserName = new LinearLayout(G.fragmentActivity);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(G.fragmentActivity);
        final MEditText edtUserName = new MEditText(G.fragmentActivity);
        edtUserName.setHint(G.fragmentActivity.getResources().getString(R.string.st_username));
        edtUserName.setText(callbackSetUserName.get());
        edtUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtUserName.setTypeface(G.typeface_IRANSansMobile);
        edtUserName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_username)).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        final String finalUserName = userName;
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (HelperString.regexCheckUsername(editable.toString())) {
                    new RequestUserProfileCheckUsername().userProfileCheckUsername(editable.toString());
                } else {
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                    positive.setEnabled(false);
                }
            }
        });
        G.onUserProfileCheckUsername = new OnUserProfileCheckUsername() {
            @Override
            public void OnUserProfileCheckUsername(final ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status status) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.AVAILABLE) {
                            if (!edtUserName.getText().toString().equals(finalUserName)) {
                                positive.setEnabled(true);
                            } else {
                                positive.setEnabled(false);
                            }
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("");
                        } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.INVALID) {

                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                            positive.setEnabled(false);
                        } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.TAKEN) {
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.TAKEN));
                            positive.setEnabled(false);
                        }
                    }
                });
            }

            @Override
            public void Error(int majorCode, int minorCode) {

            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showProgressBar();
                new RequestUserProfileUpdateUsername().userProfileUpdateUsername(edtUserName.getText().toString());
            }
        });

        G.onUserProfileUpdateUsername = new OnUserProfileUpdateUsername() {
            @Override
            public void onUserProfileUpdateUsername(final String username) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        hideProgressBar();
                    }
                });
            }

            @Override
            public void Error(final int majorCode, int minorCode, final int time) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        switch (majorCode) {
                            case 175:
                                if (dialog.isShowing()) dialog.dismiss();
                                hideProgressBar();
                                dialogWaitTime(R.string.USER_PROFILE_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                                break;
                        }
                    }
                });
            }

            @Override
            public void timeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        };

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                        } else {
                            viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                        }
                    }
                });
            }
        });
        // check each word with server
        dialog.show();


    }

    public void onClickGander(View view) {

        int position = -1;

        try {
            if (getRealm().where(RealmUserInfo.class).findFirst().getGender().getNumber() == 1) {
                position = 0;
            } else if (getRealm().where(RealmUserInfo.class).findFirst().getGender().getNumber() == 2) {
                position = 1;
            } else {
                position = -1;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        G.onUserProfileSetGenderResponse = new OnUserProfileSetGenderResponse() {
            @Override
            public void onUserProfileGenderResponse(final ProtoGlobal.Gender gender, ProtoResponse.Response response) {
                hideProgressBar();
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                hideProgressBar();
            }

            @Override
            public void onTimeOut() {
                hideProgressBar();
            }
        };

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_Gander)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.array_gander).itemsCallbackSingleChoice(position, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {
                        new RequestUserProfileSetGender().setUserProfileGender(ProtoGlobal.Gender.MALE);
                        break;
                    }
                    case 1: {
                        new RequestUserProfileSetGender().setUserProfileGender(ProtoGlobal.Gender.FEMALE);
                        break;
                    }
                }
                return false;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                showProgressBar();
            }
        }).show();

    }

    public void onClickSetEmail(View view) {
        final LinearLayout layoutEmail = new LinearLayout(G.fragmentActivity);
        layoutEmail.setOrientation(LinearLayout.VERTICAL);

        final View viewEmail = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputEmail = new TextInputLayout(G.fragmentActivity);
        final MEditText edtEmail = new MEditText(G.fragmentActivity);
        edtEmail.setHint(G.fragmentActivity.getResources().getString(R.string.set_email));
        edtEmail.setTypeface(G.typeface_IRANSansMobile);
        edtEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));

        if (callbackSetEmail.get() == null || callbackSetEmail.get().equals(G.fragmentActivity.getResources().getString(R.string.set_email))) {
            edtEmail.setText("");
        } else {
            edtEmail.setText(callbackSetEmail.get());
        }

        edtEmail.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtEmail.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtEmail.setPadding(0, 8, 0, 8);
        edtEmail.setSingleLine(true);
        inputEmail.addView(edtEmail);
        inputEmail.addView(viewEmail, viewParams);

        viewEmail.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtEmail.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutEmail.addView(inputEmail, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_email)).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).customView(layoutEmail, true).widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        final String finalEmail = userEmail;
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!edtEmail.getText().toString().equals(finalEmail)) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
                inputEmail.setErrorEnabled(true);
                inputEmail.setError("");
            }
        });

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showProgressBar();
                new RequestUserProfileSetEmail().setUserProfileEmail(edtEmail.getText().toString());
            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewEmail.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                } else {
                    viewEmail.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        G.onUserProfileSetEmailResponse = new OnUserProfileSetEmailResponse() {
            @Override
            public void onUserProfileEmailResponse(final String email, ProtoResponse.Response response) {
                hideProgressBar();
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                hideProgressBar();
                if (majorCode == 114 && minorCode == 1) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            inputEmail.setErrorEnabled(true);
                            positive.setEnabled(false);
                            inputEmail.setError("" + G.fragmentActivity.getResources().getString(R.string.error_email));
                        }
                    });
                } else if (majorCode == 115) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            inputEmail.setErrorEnabled(true);
                            positive.setEnabled(false);
                            inputEmail.setError("" + G.fragmentActivity.getResources().getString(R.string.error_email));
                        }
                    });
                }
            }

            @Override
            public void onTimeOut() {
                hideProgressBar();
            }
        };

        dialog.show();
    }

    public void onClickPhoneNumber(View view) {

    }

    public void onClickBio(View view) {

        FragmentBio fragmentBio = new FragmentBio();
        Bundle bundle = new Bundle();
        bundle.putString("BIO", callbackSetBio.get());
        fragmentBio.setArguments(bundle);
        new HelperFragment(fragmentBio).setReplace(false).load();

    }


    public void onClickNotifyAndSound(View view) {

        new HelperFragment(new FragmentNotificationAndSound()).setReplace(false).load();

    }

    public void onClickPrivacySecurity(View view) {
        new HelperFragment(new FragmentPrivacyAndSecurity()).setReplace(false).load();
    }

    public void onClickDataStorage(View view) {

        fragmentSetting.startActivity(new Intent(G.fragmentActivity, ActivityManageSpace.class));

    }

    public void onClickLanguage(View view) {

        new HelperFragment(new FragmentLanguage()).setReplace(false).load();

    }

    public void onClickDataShams(View view) {

        new HelperFragment(new FragmentData()).setReplace(false).load();

    }

    public void onClickAutoRotate(View view) {
        isAutoRotate.set(!isAutoRotate.get());
    }

    public void onCheckedChangedAutoRotate(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isAutoRotate.set(isChecked);
        if (isChecked) {
            editor.putBoolean(SHP_SETTING.KEY_AUTO_ROTATE, true);
            editor.apply();
            fragmentSetting.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        } else {
            editor.putBoolean(SHP_SETTING.KEY_AUTO_ROTATE, false);
            editor.apply();
            fragmentSetting.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }

    public void onClickMessageTextSize(View view) {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_title_message_textSize))
                .titleGravity(GravityEnum.START)
                .titleColor(G.context.getResources().getColor(android.R.color.black))
                .items(HelperCalander.isPersianUnicode ? R.array.message_text_size_persian : R.array.message_text_size)
                .itemsCallbackSingleChoice(poRbDialogTextSize, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (text != null) {
                            callbackTextSize.set(text.toString().replace("(Hello)", "").trim());

                            if (HelperCalander.isPersianUnicode) {
                                callbackTextSize.set(HelperCalander.convertToUnicodeFarsiNumber(callbackTextSize.get()));
                            }
                        }
                        poRbDialogTextSize = which;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, fontSizeArray[which]);
                        editor.apply();

                        StartupActions.textSizeDetection(sharedPreferences);

                        return false;
                    }
                })
                .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .show();
    }

    public void onClickChatBackground(View view) {
        new HelperFragment(FragmentChatBackground.newInstance()).setReplace(false).load();
    }

    public void onClickShowVote(View view) {
        isShowVote.set(!isShowVote.get());
    }

    public void onCheckedChangedShowVote(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isShowVote.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_VOTE, 1);
            editor.apply();
            G.showVoteChannelLayout = true;
        } else {
            editor.putInt(SHP_SETTING.KEY_VOTE, 0);
            editor.apply();
            G.showVoteChannelLayout = false;
        }
    }

    public void onClickMultiTab(View view) {

        isMultiTab.set(!isMultiTab.get());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isMultiTab.get()) {
            G.multiTab = true;
            editor.putBoolean(SHP_SETTING.KEY_MULTI_TAB, true);
            editor.apply();
        } else {
            G.multiTab = false;
            editor.putBoolean(SHP_SETTING.KEY_MULTI_TAB, false);
            editor.apply();
        }
        FragmentMain.roomAdapterHashMap = null;

        if (onRefreshActivity != null) {
            G.isRestartActivity = true;
            onRefreshActivity.refresh("ar");
        }
        if (FragmentSetting.onRemoveFragmentSetting != null)
            FragmentSetting.onRemoveFragmentSetting.removeFragment();

    }

    public void onCheckedChangedMultiTab(boolean isChecked) {


    }


    public void onClickSenderNameGroup(View view) {

        isSenderNameGroup.set(!isSenderNameGroup.get());
    }

    public void onCheckedChangedSenderNameGroup(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isSenderNameGroup.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 1);
            editor.apply();
            G.showSenderNameInGroup = true;
        } else {
            editor.putInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0);
            editor.apply();
            G.showSenderNameInGroup = false;
        }

    }

    public void onClickSendEnter(View view) {

        isSendEnter.set(!isSendEnter.get());
    }

    public void onCheckedChangedSendEnter(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isSendEnter.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
            editor.apply();
        }
    }

    public void onClickTitleBarColor(View view) {
        showSelectAppColorDialog(R.string.app_theme);
    }

    public void onClickNotificationColor(View view) {
        showSelectAppColorDialog(R.string.app_notif_color);
    }

    public void onClickToggleBottonColor(View view) {
        showSelectAppColorDialog(R.string.toggle_botton_color);
    }

    public void onClickSendAndAttachColor(View view) {
        showSelectAppColorDialog(R.string.send_and_attach_botton_color);
    }

    public void onClickDefaultHeaderFontColor(View view) {

        showSelectAppColorDialog(R.string.default_header_font_color);
    }

    public void onClickDefaultProgressColor(View view) {
        showSelectAppColorDialog(R.string.default_progress_color);
    }

    public void onClickSetColorToDefault(View view) {

        showSetDefaultColorDialog();

    }

    public void onClickAutoDownloadData(View view) {


        KEY_AD_DATA_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1);
        KEY_AD_DATA_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1);
        KEY_AD_DATA_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1);
        KEY_AD_DATA_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_FILE, -1);
        KEY_AD_DATA_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1);
        KEY_AD_DATA_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_GIF, 5);

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.title_auto_download_data).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                KEY_AD_DATA_PHOTO, KEY_AD_DATA_VOICE_MESSAGE, KEY_AD_DATA_VIDEO, KEY_AD_DATA_FILE, KEY_AD_DATA_MUSIC, KEY_AD_DATA_GIF
        }, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_FILE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_GIF, -1);
                editor.apply();

                for (Integer aWhich : which) {

                    if (aWhich == 0) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, aWhich);
                    } else if (aWhich == 1) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, aWhich);
                    } else if (aWhich == 2) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, aWhich);
                    } else if (aWhich == 3) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_FILE, aWhich);
                    } else if (aWhich == 4) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, aWhich);
                    } else if (aWhich == 5) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_GIF, aWhich);
                    }
                    editor.apply();
                }

                return true;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();

    }

    public void onClickAutoDownloadWifi(View view) {

        KEY_AD_WIFI_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1);
        KEY_AD_WIFI_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1);
        KEY_AD_WIFI_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1);
        KEY_AD_WIFI_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1);
        KEY_AD_WIFI_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1);
        KEY_AD_WIFI_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_GIF, 5);

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.title_auto_download_wifi).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                KEY_AD_WIFI_PHOTO, KEY_AD_WIFI_VOICE_MESSAGE, KEY_AD_WIFI_VIDEO, KEY_AD_WIFI_FILE, KEY_AD_WIFI_MUSIC, KEY_AD_WIFI_GIF
        }, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_GIF, -1);
                editor.apply();

                for (Integer aWhich : which) {
                    Log.i("JJJJ", "WIFI: " + aWhich);

                    if (aWhich == 0) {
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, aWhich);
                    } else if (aWhich == 1) {
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, aWhich);
                    } else if (aWhich == 2) {
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, aWhich);
                    } else if (aWhich == 3) {
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_FILE, aWhich);
                    } else if (aWhich == 4) {

                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, aWhich);
                    } else if (aWhich == 5) {
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_GIF, aWhich);
                    }
                    editor.apply();
                }

                return true;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).show();

    }

    public void onClickAutoDownloadRoaming(View view) {

        KEY_AD_ROAMING_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
        KEY_AD_ROAMING_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
        KEY_AD_ROAMING_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
        KEY_AD_ROAMING_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
        KEY_AD_ROAMING_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
        KEY_AD_ROAMINGN_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.title_auto_download_roaming).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                KEY_AD_ROAMING_PHOTO, KEY_AD_ROAMING_VOICE_MESSAGE, KEY_AD_ROAMING_VIDEO, KEY_AD_ROAMING_FILE, KEY_AD_ROAMING_MUSIC, KEY_AD_ROAMINGN_GIF
        }, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                //

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);
                editor.apply();

                for (Integer aWhich : which) {
                    if (aWhich > -1) {
                        if ((aWhich == 0)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, aWhich);
                        } else if ((aWhich == 1)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, aWhich);
                        } else if ((aWhich == 2)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, aWhich);
                        } else if ((aWhich == 3)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, aWhich);
                        } else if ((aWhich == 4)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, aWhich);
                        } else if ((aWhich == 5)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, aWhich);
                        }
                        editor.apply();
                    }
                }
                return true;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
    }

    public void onClickAutoGif(View view) {
        isAutoGif.set(!isAutoGif.get());
    }

    public void onCheckedChangeAutoGif(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isAutoGif.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 0);
            editor.apply();
        }

    }

    public void onClickSaveGallery(View view) {

        isSaveGallery.set(!isSaveGallery.get());
    }

    public void onCheckedChangedSaveGallery(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isSaveGallery.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 1);
            editor.apply();
            G.isSaveToGallery = true;
        } else {
            editor.putInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
            G.isSaveToGallery = false;
            editor.apply();
        }

    }

    public void onClickCompress(View view) {
        isCompress.set(!isCompress.get());
    }

    public void onCheckedChangedCompress(boolean isChecked) {

        isCompress.set(isChecked);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_COMPRESS, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_COMPRESS, 0);
            editor.apply();
        }

    }

    public void onClickTrim(View view) {
        isTrim.set(!isTrim.get());
    }

    public void onCheckedChangedTrim(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isTrim.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_TRIM, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_TRIM, 0);
            editor.apply();
        }

    }

    public void onClickDefaultVideo(View view) {
        isDefaultPlayer.set(!isDefaultPlayer.get());
    }

    public void onCheckedDefaultVideo(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isDefaultPlayer.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 0);
            editor.apply();
        }
    }

    public void onClickCrop(View view) {
        isCrop.set(!isCrop.get());
    }

    public void onCheckedChangedCrop(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isCrop.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_CROP, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_CROP, 0);
            editor.apply();
        }

    }

    public void onClickCameraButtonSheet(View v) {
        isCameraButtonSheet.set(!isCameraButtonSheet.get());
    }

    public void onCheckedChangedCameraButtonSheet(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isCameraButtonSheet.set(isChecked);
        if (isChecked) {
            editor.putBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
            editor.apply();
        } else {
            editor.putBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, false);
            editor.apply();
        }

    }


    public void onClickiGapHome(View view) {
        final String link;
        if (HelperCalander.isPersianUnicode) {
            link = "https://www.igap.net/fa";
        } else {
            link = "https://www.igap.net/";
        }
        HelperUrl.openBrowser(link);
    }

    public void onClickPrivacyBlog(View view) {

        final String blogLink;
        if (HelperCalander.isPersianUnicode) {
            blogLink = "https://blog.igap.net/fa";
        } else {
            blogLink = "https://blog.igap.net";
        }

        HelperUrl.openBrowser(blogLink);
    }
    //public void onClickFabSetPic(View view) {
    //
    //    //startDialog(R.array.profile);
    //}


    public void onClickTicket(View view) {

        final String supportLink;
        if (HelperCalander.isPersianUnicode) {
            supportLink = "https://support.igap.net/fa";
        } else {
            supportLink = "https://support.igap.net";
        }
        HelperUrl.openBrowser(supportLink);

    }



    private void getInfo() {

        realmPrivacy = getRealm().where(RealmPrivacy.class).findFirst();
        realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();

        if (realmUserInfo == null) {
            G.fragmentActivity.onBackPressed();
            return;
        }

        updateUserInfoUI(realmUserInfo);


        if (realmPrivacy == null) {
            RealmPrivacy.updatePrivacy("", "", "", "", "");
        }
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        String textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());
        callbackLanguage.set(textLanguage);

        int checkedEnableCrop = sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1);
        isCrop.set(getBoolean(checkedEnableCrop));

        boolean checkCameraButtonSheet = sharedPreferences.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
        isCameraButtonSheet.set(checkCameraButtonSheet);

        int checkedEnableVote = sharedPreferences.getInt(SHP_SETTING.KEY_VOTE, 1);
        isShowVote.set(getBoolean(checkedEnableVote));

        int checkedEnablShowSenderInGroup = sharedPreferences.getInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0);
        isSenderNameGroup.set(getBoolean(checkedEnablShowSenderInGroup));

        int checkedEnableCompress = sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1);
        isCompress.set(getBoolean(checkedEnableCompress));

        int typeData = sharedPreferences.getInt(SHP_SETTING.KEY_DATA, 0);
        switch (typeData) {
            case 0:
                callbackDataShams.set(G.fragmentActivity.getResources().getString(R.string.miladi));
                break;
            case 1:
                callbackDataShams.set(G.fragmentActivity.getResources().getString(R.string.shamsi));
                break;
            case 2:
                callbackDataShams.set(G.fragmentActivity.getResources().getString(R.string.ghamari));
                break;
        }

        FragmentSetting.dateType = new FragmentSetting.DateType() {
            @Override
            public void dataName(String type) {
                callbackDataShams.set(type);
            }
        };

        boolean checkedEnableAutoRotate = sharedPreferences.getBoolean(SHP_SETTING.KEY_AUTO_ROTATE, true);
        isAutoRotate.set(checkedEnableAutoRotate);

        boolean checkedEnableMultiTab = sharedPreferences.getBoolean(SHP_SETTING.KEY_MULTI_TAB, false);
        isMultiTab.set(checkedEnableMultiTab);

        poRbDialogTextSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14) - 11;
        String textSize = "" + sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14);
        callbackTextSize.set(textSize);

        if (HelperCalander.isPersianUnicode) {
            callbackTextSize.set(HelperCalander.convertToUnicodeFarsiNumber(callbackTextSize.get()));
        }

        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        isSendEnter.set(getBoolean(checkedSendByEnter));


        int checkedAutoGif = sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS);
        isAutoGif.set(getBoolean(checkedAutoGif));

        int checkedSaveToGallery = sharedPreferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
        isSaveGallery.set(getBoolean(checkedSaveToGallery));


        int checkedEnableTrim = sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1);
        isTrim.set(getBoolean(checkedEnableTrim));

        int checkedEnableDefaultPlayer = sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 0);
        isDefaultPlayer.set(getBoolean(checkedEnableDefaultPlayer));

        callbackVersionApp.set(G.fragmentActivity.getResources().getString(R.string.iGap_version) + " " + getAppVersion());


    }

    private void updateUserInfoUI(RealmUserInfo userInfo) {
        if (checkValidationForRealm(userInfo)) {
            userId = userInfo.getUserId();
            String nickName = userInfo.getUserInfo().getDisplayName();
            userName = userInfo.getUserInfo().getUsername();
            phoneName = userInfo.getUserInfo().getPhoneNumber();
            ProtoGlobal.Gender userGender = userInfo.getGender();
            userEmail = userInfo.getEmail();
            bio = userInfo.getUserInfo().getBio();

            if (nickName != null) {
                callbackSetName.set(nickName);
                callbackSetTitleName.set(nickName);
            }
            if (bio != null) {
                callbackSetBio.set(bio);
            }

            if (userName != null) callbackSetUserName.set(userName);

            if (phoneName != null) callbackSetPhoneNumber.set(phoneName);

            if (HelperCalander.isPersianUnicode) {
                callbackSetPhoneNumber.set(HelperCalander.convertToUnicodeFarsiNumber(callbackSetPhoneNumber.get()));
            }

            if (userGender != null) {
                if (userGender == ProtoGlobal.Gender.MALE) {
                    callbackGander.set(G.fragmentActivity.getResources().getString(R.string.Male));
                } else if (userGender == ProtoGlobal.Gender.FEMALE) {
                    callbackGander.set(G.fragmentActivity.getResources().getString(R.string.Female));
                }
            } else {
                callbackGander.set(G.fragmentActivity.getResources().getString(R.string.set_gender));
            }

            if (userEmail != null && userEmail.length() > 0) {
                callbackSetEmail.set(userEmail);
            } else {
                callbackSetEmail.set(G.fragmentActivity.getResources().getString(R.string.set_email));
            }
        }
    }


    private boolean checkValidationForRealm(RealmUserInfo realmUserInfo) {
        if (realmUserInfo != null && realmUserInfo.isManaged() && realmUserInfo.isValid() && realmUserInfo.isLoaded()) {
            return true;
        }
        return false;
    }


    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(fragmentSetting);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                idAvatar = SUID.id().get();
                pathSaveImage = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + idAvatar + ".jpg";
                File nameImageFile = new File(pathSaveImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uriIntent = Uri.fromFile(nameImageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                fragmentSetting.startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);

            } else {
                Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.please_check_your_camera), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
        return mRealm;
    }

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (fragmentSettingBinding.stPrgWaitingAddContact != null) {
                    fragmentSettingBinding.stPrgWaitingAddContact.setVisibility(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (fragmentSettingBinding.stPrgWaitingAddContact != null) {
                    fragmentSettingBinding.stPrgWaitingAddContact.setVisibility(View.GONE);
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        if (isActiveRun) {
            boolean wrapInScrollView = true;
            final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            }).show();

            View v = dialog.getCustomView();

            final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
            CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) ((millisUntilFinished) / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                    //                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }

                @Override
                public void onFinish() {
                    //                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
            };
            countWaitTimer.start();
        }
    }

    private boolean getBoolean(int num) {
        if (num == 0) {
            return false;
        }
        return true;
    }

    private void showSelectAppColorDialog(final int title) {

        boolean wrapInScrollView = true;

        String titleMessage = G.fragmentActivity.getResources().getString(title);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.stns_popup_colorpicer, wrapInScrollView).positiveText(G.fragmentActivity.getResources().getString(R.string.set)).negativeText(G.fragmentActivity.getResources().getString(R.string.DISCARD)).title(titleMessage).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        }).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        }).build();

        View view1 = dialog.getCustomView();
        assert view1 != null;
        final ColorPicker picker = (ColorPicker) view1.findViewById(R.id.picker);
        SVBar svBar = (SVBar) view1.findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) view1.findViewById(R.id.opacitybar);
        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                try {

                    String _strColor = "#" + Integer.toHexString(picker.getColor());
                    int _color = Color.parseColor(_strColor); // if can not parae selected color do not set selected color

                    switch (title) {

                        case R.string.app_theme:
                            appBarColorClick(picker.getColor());
                            break;
                        case R.string.app_notif_color:
                            notificationColorClick(picker.getColor(), true);
                            break;
                        case R.string.toggle_botton_color:
                            toggleBottomClick(picker.getColor());
                            break;
                        case R.string.send_and_attach_botton_color:
                            sendAndAttachColorClick(picker.getColor());
                            break;
                        case R.string.default_header_font_color:
                            headerColorClick(picker.getColor(), true);
                            break;
                        case R.string.default_progress_color:
                            progressColorClick(picker.getColor(), true);
                            break;

                    }
                } catch (IllegalArgumentException e) {

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.selected_color_can_not_set_on_yout_device).cancelable(true).show();
                }
            }
        });

        dialog.show();
    }

    private void showSetDefaultColorDialog() {

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.set_color_to_default).content(R.string.color_default).positiveText(R.string.st_dialog_reset_all_notification_yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                notificationColorClick(Color.parseColor(Config.default_notificationColor), false);
                headerColorClick(Color.parseColor(Config.default_headerTextColor), false);
                toggleBottomClick(Color.parseColor(Config.default_toggleButtonColor));
                sendAndAttachColorClick(Color.parseColor(Config.default_attachmentColor));
                appBarColorClick(Color.parseColor(Config.default_appBarColor));
                progressColorClick(Color.parseColor(Config.default_appBarColor), false);
            }
        }).negativeText(R.string.st_dialog_reset_all_notification_no).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        }).show();
    }

    private void appBarColorClick(int color) {

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        GradientDrawable bgShape = (GradientDrawable) fragmentSettingBinding.asnImgTitleBarColor.getBackground();
        G.appBarColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_APP_BAR_COLOR, G.appBarColor);
        editor.apply();

        // G.fragmentActivity.recreate();
        if (G.onRefreshActivity != null) {
            G.onRefreshActivity.refresh("");
            G.isRestartActivity = true;
        }
    }

    private void notificationColorClick(int color, boolean updateUi) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) fragmentSettingBinding.asnImgNotificationColor.getBackground();
        G.notificationColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_NOTIFICATION_COLOR, G.notificationColor);
        editor.apply();

        G.isUpdateNotificaionColorMain = true;
        G.isUpdateNotificaionColorChannel = true;
        G.isUpdateNotificaionColorGroup = true;
        G.isUpdateNotificaionColorChat = true;

        //if (updateUi && G.onRefreshActivity != null) {
        //    G.onRefreshActivity.refresh("");
        //}
    }

    private void progressColorClick(int color, boolean updateUi) {

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) fragmentSettingBinding.asnImgDefaultProgressColor.getBackground();
        G.progressColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_PROGRES_COLOR, G.progressColor);
        editor.apply();

        //if (updateUi && G.onRefreshActivity != null) {
        //    G.onRefreshActivity.refresh("");
        //}
    }

    private void toggleBottomClick(int color) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) fragmentSettingBinding.asnImgToggleBottonColor.getBackground();
        G.toggleButtonColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, G.toggleButtonColor);
        editor.apply();
    }

    private void headerColorClick(int color, boolean updateUi) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) fragmentSettingBinding.asnImgDefaultHeaderFontColor.getBackground();
        G.headerTextColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_FONT_HEADER_COLOR, G.headerTextColor);
        editor.apply();

        if (updateUi) {
            G.isRestartActivity = true;
            G.fragmentActivity.recreate();
        }
    }

    private void sendAndAttachColorClick(int color) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) fragmentSettingBinding.asnImgSendAndAttachColor.getBackground();
        G.attachmentColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, G.attachmentColor);
        editor.apply();
    }

    private String getAppVersion() {

        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }


    public void onResume() {
        G.onUserAvatarResponse = fragmentSetting;

        realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();

        if (realmUserInfo == null) {
            //finish();
            G.fragmentActivity.onBackPressed();
            return;
        }

        realmUserInfo.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel realmModel) {
                updateUserInfoUI((RealmUserInfo) realmModel);
            }
        });

        mRealmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealm(), G.userId);
        if (mRealmRegisteredInfo != null) {
            mRealmRegisteredInfo.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel realmModel) {
                    updateUserInfoUI(realmUserInfo);
                }
            });

            updateUserInfoUI(realmUserInfo);
        }
    }

    public void onPause() {
        if (realmUserInfo != null && realmUserInfo.isValid()) {
            realmUserInfo.removeAllChangeListeners();
        }

        if (mRealmRegisteredInfo != null && mRealmRegisteredInfo.isValid()) {
            mRealmRegisteredInfo.removeAllChangeListeners();
        }
    }

    public void onStop() {
        updateRoomListIfNeeded();
    }

    public void onDestroy() {

        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
    }

    private void updateRoomListIfNeeded() {

        try {

            for (Fragment f : G.fragmentManager.getFragments()) {

                if (f == null) {
                    continue;
                }

                if (f instanceof FragmentMain || f instanceof FragmentCall) {
                    f.onResume();
                }
            }
        } catch (Exception e) {
            HelperLog.setErrorLog("fragment setting   updateRoomListIfNeeded    " + e.toString());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


    }


}
