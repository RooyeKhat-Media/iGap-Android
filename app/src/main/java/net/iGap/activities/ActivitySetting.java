/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentDeleteAccount;
import net.iGap.fragments.FragmentPrivacyAndSecurity;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnAvatarDelete;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnUserAvatarResponse;
import net.iGap.interfaces.OnUserProfileCheckUsername;
import net.iGap.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.interfaces.OnUserProfileSetGenderResponse;
import net.iGap.interfaces.OnUserProfileUpdateUsername;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.DialogAnimation;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.FileUtils;
import net.iGap.module.IntentRequests;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.StartupActions;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoUserProfileCheckUsername;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmPrivacy;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserAvatarAdd;
import net.iGap.request.RequestUserProfileCheckUsername;
import net.iGap.request.RequestUserProfileGetEmail;
import net.iGap.request.RequestUserProfileGetGender;
import net.iGap.request.RequestUserProfileSetEmail;
import net.iGap.request.RequestUserProfileSetGender;
import net.iGap.request.RequestUserProfileSetNickname;
import net.iGap.request.RequestUserProfileUpdateUsername;
import net.iGap.request.RequestUserSessionLogout;
import org.chromium.customtabsclient.CustomTabsActivityHelper;

import static net.iGap.G.context;
import static net.iGap.G.onRefreshActivity;
import static net.iGap.R.string.log_out;

public class ActivitySetting extends ActivityEnhanced implements OnUserAvatarResponse {

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
    private SharedPreferences sharedPreferences;
    private TextView txtMessageTextSize;
    private TextView txtLanguage;
    private TextView txtSizeClearCach;
    private PopupWindow popupWindow;
    private int poRbDialogLangouage = -1;
    private int poRbDialogTextSize = -1;
    private TextView txtNickName;
    private TextView txtUserName;
    private TextView txtPhoneNumber;
    private ToggleButton toggleSentByEnter, toggleEnableAnimation, toggleAutoGifs, toggleSaveToGallery, toggleInAppBrowser, toggleCrop;
    private Uri uriIntent;
    private ImageView imgAppBarSelected;
    private ImageView imgNotificationColor;
    private ImageView imgToggleBottomColor;
    private ImageView imgSendAndAttachColor;
    private ImageView imgHeaderTextColor;
    private ImageView imgHeaderProgressColor;
    private long idAvatar;
    private FloatingActionButton fab;
    private net.iGap.module.CircleImageView circleImageView;
    private String userName;
    private String phoneName;
    private String userEmail;
    private long userId;
    public ProgressBar prgWait;
    private TextView txtGander;
    private TextView txtEmail;
    TextView txtNickNameTitle;

    Realm mRealm;

    RealmChangeListener<RealmModel> userInfoListener;
    RealmUserInfo realmUserInfo;

    private void setImage(String path) {
        if (path != null) {
            G.imageLoader.displayImage(AndroidUtils.suitablePath(path), circleImageView);
            if (G.onChangeUserPhotoListener != null) {
                G.onChangeUserPhotoListener.onChangePhoto(path);
            }
        }
    }

    private void showInitials() {

        RealmUserInfo realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();
        circleImageView.setImageBitmap(
            HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(),
                realmUserInfo.getUserInfo().getColor()));


        if (G.onChangeUserPhotoListener != null) {
            G.onChangeUserPhotoListener.onChangePhoto(null);
        }
    }


    @Override protected void onResume() {
        super.onResume();
        G.onUserAvatarResponse = this;

        if (realmUserInfo != null) {
            if (userInfoListener != null) {
                realmUserInfo.addChangeListener(userInfoListener);
            }

            updateUserInfoUI(realmUserInfo);
        }
    }

    @Override protected void onPause() {
        super.onPause();
        if (realmUserInfo != null) {
            realmUserInfo.removeAllChangeListeners();
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        if (mRealm != null) {
            mRealm.close();
        }
    }

    private Realm getRealm() {

        if (mRealm != null && !mRealm.isClosed()) {
            return mRealm;
        }

        mRealm = Realm.getDefaultInstance();
        return mRealm;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();
        userInfoListener = new RealmChangeListener<RealmModel>() {
            @Override public void onChange(RealmModel element) {
                updateUserInfoUI((RealmUserInfo) element);
            }
        };

        RealmPrivacy realmPrivacy = getRealm().where(RealmPrivacy.class).findFirst();

        if (realmPrivacy == null) {
            RealmPrivacy.updatePrivacy("", "", "", "");
        }

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        txtNickNameTitle = (TextView) findViewById(R.id.ac_txt_nickname_title);
        txtNickName = (TextView) findViewById(R.id.st_txt_nikName);
        txtUserName = (TextView) findViewById(R.id.st_txt_userName);
        txtPhoneNumber = (TextView) findViewById(R.id.st_txt_phoneNumber);
        txtGander = (TextView) findViewById(R.id.st_txt_gander);
        txtEmail = (TextView) findViewById(R.id.st_txt_email);
        prgWait = (ProgressBar) findViewById(R.id.st_prgWaiting_addContact);
        AppUtils.setProgresColler(prgWait);

        updateUserInfoUI(realmUserInfo);

        new RequestUserProfileGetGender().userProfileGetGender();
        new RequestUserProfileGetEmail().userProfileGetEmail();


         /*
          set layout and open dialog for set or change Name & Family
         */
        ViewGroup layoutNickname = (ViewGroup) findViewById(R.id.st_layout_nickname);
        layoutNickname.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                final LinearLayout layoutNickname = new LinearLayout(ActivitySetting.this);
                layoutNickname.setOrientation(LinearLayout.VERTICAL);

                String splitNickname[] = txtNickName.getText().toString().split(" ");
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
                final View viewFirstName = new View(ActivitySetting.this);
                viewFirstName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

                TextInputLayout inputFirstName = new TextInputLayout(ActivitySetting.this);
                final EditText edtFirstName = new EditText(ActivitySetting.this);
                edtFirstName.setHint(getResources().getString(R.string.fac_First_Name));
                edtFirstName.setText(firsName);
                edtFirstName.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtFirstName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtFirstName.setPadding(0, 8, 0, 8);
                edtFirstName.setSingleLine(true);
                inputFirstName.addView(edtFirstName);
                inputFirstName.addView(viewFirstName, viewParams);
                final View viewLastName = new View(ActivitySetting.this);
                viewLastName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtFirstName.setBackground(getResources().getDrawable(android.R.color.transparent));
                }

                TextInputLayout inputLastName = new TextInputLayout(ActivitySetting.this);
                final EditText edtLastName = new EditText(ActivitySetting.this);
                edtLastName.setHint(getResources().getString(R.string.fac_Last_Name));
                edtLastName.setText(lastName);
                edtLastName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtLastName.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtLastName.setPadding(0, 8, 0, 8);
                edtLastName.setSingleLine(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtLastName.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                inputLastName.addView(edtLastName);
                inputLastName.addView(viewLastName, viewParams);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 15);
                LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lastNameLayoutParams.setMargins(0, 15, 0, 10);

                layoutNickname.addView(inputFirstName, layoutParams);
                layoutNickname.addView(inputLastName, lastNameLayoutParams);

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_nickname))
                    .positiveText(getResources().getString(R.string.B_ok))
                    .customView(layoutNickname, true)
                    .widgetColor(getResources().getColor(R.color.toolbar_background))
                    .negativeText(getResources().getString(R.string.B_cancel))
                    .build();

                final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                final String finalFirsName = firsName;
                edtFirstName.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override public void afterTextChanged(Editable editable) {

                        if (!edtFirstName.getText().toString().equals(finalFirsName)) {
                            positive.setEnabled(true);
                        } else {
                            positive.setEnabled(false);
                        }
                    }
                });

                final String finalLastName = lastName;
                edtLastName.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override public void afterTextChanged(Editable editable) {
                        if (!edtLastName.getText().toString().equals(finalLastName)) {
                            positive.setEnabled(true);
                        } else {
                            positive.setEnabled(false);
                        }
                    }
                });

                edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override public void onFocusChange(View view, boolean b) {
                        if (b) {
                            viewFirstName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                        } else {
                            viewFirstName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                        }
                    }
                });

                edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override public void onFocusChange(View view, boolean b) {
                        if (b) {
                            viewLastName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                        } else {
                            viewLastName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                        }
                    }
                });

                positive.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        String fullName = "";
                        if (edtFirstName.length() == 0) {
                            fullName = " " + " " + edtLastName.getText().toString();
                        }
                        if (edtLastName.length() == 0) {
                            fullName = edtFirstName.getText().toString() + " " + " ";
                        }
                        if (edtLastName.length() > 0 && edtFirstName.length() > 0) {
                            fullName = edtFirstName.getText().toString() + " " + edtLastName.getText().toString();
                        }

                        new RequestUserProfileSetNickname().userProfileNickName(fullName);

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

         /*
          open dialog for set or change gander
         */
        ViewGroup layoutGander = (ViewGroup) findViewById(R.id.st_layout_gander);
        layoutGander.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
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
                    @Override public void onUserProfileGenderResponse(final ProtoGlobal.Gender gender, ProtoResponse.Response response) {
                        hideProgressBar();
                    }

                    @Override public void Error(int majorCode, int minorCode) {
                        hideProgressBar();
                    }

                    @Override public void onTimeOut() {
                        hideProgressBar();
                    }
                };

                new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_Gander))
                    .titleGravity(GravityEnum.START)
                    .titleColor(getResources().getColor(android.R.color.black))
                    .items(R.array.array_gander)
                    .itemsCallbackSingleChoice(position, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

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
                    })
                    .positiveText(getResources().getString(R.string.B_ok))
                    .negativeText(getResources().getString(R.string.B_cancel))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            showProgressBar();
                        }
                    })
                    .show();
            }
        });

        /*
          set layout and open dialog for set or change email address
         */
        ViewGroup ltEmail = (ViewGroup) findViewById(R.id.st_layout_email);
        ltEmail.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                final LinearLayout layoutEmail = new LinearLayout(ActivitySetting.this);
                layoutEmail.setOrientation(LinearLayout.VERTICAL);

                final View viewEmail = new View(ActivitySetting.this);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

                final TextInputLayout inputEmail = new TextInputLayout(ActivitySetting.this);
                final EditText edtEmail = new EditText(ActivitySetting.this);
                edtEmail.setHint(getResources().getString(R.string.set_email));

                if (txtEmail == null || txtEmail.getText().toString().equals(getResources().getString(R.string.set_email))) {
                    edtEmail.setText("");
                } else {
                    edtEmail.setText(txtEmail.getText().toString());
                }

                edtEmail.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtEmail.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtEmail.setPadding(0, 8, 0, 8);
                edtEmail.setSingleLine(true);
                inputEmail.addView(edtEmail);
                inputEmail.addView(viewEmail, viewParams);

                viewEmail.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtEmail.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutEmail.addView(inputEmail, layoutParams);

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_email))
                    .positiveText(getResources().getString(R.string.save))
                    .customView(layoutEmail, true)
                    .widgetColor(getResources().getColor(R.color.toolbar_background))
                    .negativeText(getResources().getString(R.string.B_cancel))
                    .build();

                final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                final String finalEmail = userEmail;
                edtEmail.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override public void afterTextChanged(Editable editable) {

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
                    @Override public void onClick(View view) {
                        dialog.dismiss();
                        showProgressBar();
                        new RequestUserProfileSetEmail().setUserProfileEmail(edtEmail.getText().toString());
                    }
                });

                edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override public void onFocusChange(View view, boolean b) {
                        if (b) {
                            viewEmail.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                        } else {
                            viewEmail.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                        }
                    }
                });

                G.onUserProfileSetEmailResponse = new OnUserProfileSetEmailResponse() {
                    @Override public void onUserProfileEmailResponse(final String email, ProtoResponse.Response response) {
                        hideProgressBar();
                    }

                    @Override public void Error(int majorCode, int minorCode) {
                        hideProgressBar();
                        if (majorCode == 114 && minorCode == 1) {
                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    inputEmail.setErrorEnabled(true);
                                    positive.setEnabled(false);
                                    inputEmail.setError("" + getResources().getString(R.string.error_email));
                                }
                            });
                        } else if (majorCode == 115) {
                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    inputEmail.setErrorEnabled(true);
                                    positive.setEnabled(false);
                                    inputEmail.setError("" + getResources().getString(R.string.error_email));
                                }
                            });
                        }
                    }

                    @Override public void onTimeOut() {
                        hideProgressBar();
                    }
                };

                dialog.show();
            }
        });
         /*
          set layout and open dialog for change userName
         */
        ViewGroup layoutUserName = (ViewGroup) findViewById(R.id.st_layout_username);
        layoutUserName.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                final LinearLayout layoutUserName = new LinearLayout(ActivitySetting.this);
                layoutUserName.setOrientation(LinearLayout.VERTICAL);

                final View viewUserName = new View(ActivitySetting.this);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

                final TextInputLayout inputUserName = new TextInputLayout(ActivitySetting.this);
                final EditText edtUserName = new EditText(ActivitySetting.this);
                edtUserName.setHint(getResources().getString(R.string.st_username));
                edtUserName.setText(txtUserName.getText().toString());
                edtUserName.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtUserName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtUserName.setPadding(0, 8, 0, 8);
                edtUserName.setSingleLine(true);
                inputUserName.addView(edtUserName);
                inputUserName.addView(viewUserName, viewParams);

                viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtUserName.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutUserName.addView(inputUserName, layoutParams);

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_username))
                    .positiveText(getResources().getString(R.string.save))
                    .customView(layoutUserName, true)
                    .widgetColor(getResources().getColor(R.color.toolbar_background))
                    .negativeText(getResources().getString(R.string.B_cancel))
                    .build();

                final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                final String finalUserName = userName;
                edtUserName.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override public void afterTextChanged(Editable editable) {

                        if (HelperString.regexCheckUsername(editable.toString())) {
                            new RequestUserProfileCheckUsername().userProfileCheckUsername(editable.toString());
                        } else {
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + getResources().getString(R.string.INVALID));
                            positive.setEnabled(false);
                        }
                    }
                });
                G.onUserProfileCheckUsername = new OnUserProfileCheckUsername() {
                    @Override public void OnUserProfileCheckUsername(final ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status status) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
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
                                    inputUserName.setError("" + getResources().getString(R.string.INVALID));
                                    positive.setEnabled(false);
                                } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.TAKEN) {
                                    inputUserName.setErrorEnabled(true);
                                    inputUserName.setError("" + getResources().getString(R.string.TAKEN));
                                    positive.setEnabled(false);
                                }
                            }
                        });
                    }

                    @Override public void Error(int majorCode, int minorCode) {

                    }
                };

                positive.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        new RequestUserProfileUpdateUsername().userProfileUpdateUsername(edtUserName.getText().toString());
                    }
                });

                G.onUserProfileUpdateUsername = new OnUserProfileUpdateUsername() {
                    @Override public void onUserProfileUpdateUsername(final String username) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                dialog.dismiss();
                            }
                        });
                    }

                    @Override public void Error(final int majorCode, int minorCode, final int time) {

                        switch (majorCode) {
                            case 175:
                                if (dialog.isShowing()) dialog.dismiss();

                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        dialogWaitTime(R.string.USER_PROFILE_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                                    }
                                });

                                break;
                        }
                    }
                };

                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override public void onFocusChange(View view, boolean b) {
                                if (b) {
                                    viewUserName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                                } else {
                                    viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                                }
                            }
                        });
                    }
                });
                // check each word with server
                dialog.show();
            }
        });

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.st_appbar);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.st_collapsing_layout);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));

        final TextView titleToolbar = (TextView) findViewById(R.id.st_txt_titleToolbar);
        final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.st_parentLayoutCircleImage);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override public void onOffsetChanged(AppBarLayout appBarLayout, final int verticalOffset) {

                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        if (verticalOffset < -5) {
                            viewGroup.animate().alpha(0).setDuration(500);
                            titleToolbar.animate().alpha(1).setDuration(250);
                            viewGroup.clearAnimation();
                            titleToolbar.clearAnimation();
                            titleToolbar.setVisibility(View.VISIBLE);
                            viewGroup.setVisibility(View.GONE);
                        } else {

                            titleToolbar.animate().alpha(0).setDuration(250);
                            viewGroup.animate().alpha(1).setDuration(500);
                            viewGroup.clearAnimation();
                            titleToolbar.clearAnimation();
                            viewGroup.setVisibility(View.VISIBLE);
                            titleToolbar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        // button back in toolbar
        RippleView rippleBack = (RippleView) findViewById(R.id.st_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        /*
          set layout for popup menu
         */
        RippleView rippleMore = (RippleView) findViewById(R.id.st_ripple_more);
        rippleMore.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override public void onComplete(RippleView rippleView) {

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).customView(R.layout.chat_popup_dialog_custom, true).build();
                View v = dialog.getCustomView();

                DialogAnimation.animationUp(dialog);
                dialog.show();

                ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
                ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);

                TextView txtLogOut = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
                TextView txtDeleteAccount = (TextView) v.findViewById(R.id.dialog_text_item2_notification);

                TextView iconLogOut = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
                iconLogOut.setText(getResources().getString(R.string.md_exit_app));
                TextView iconDeleteAccount = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
                iconDeleteAccount.setText(getResources().getString(R.string.md_delete_acc));

                root1.setVisibility(View.VISIBLE);
                root2.setVisibility(View.VISIBLE);

                txtLogOut.setText(getResources().getString(log_out));
                txtDeleteAccount.setText(getResources().getString(R.string.delete_account));



                root1.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        dialog.dismiss();

                        final MaterialDialog inDialog = new MaterialDialog.Builder(ActivitySetting.this).customView(R.layout.dialog_content_custom, true).build();
                        View v = inDialog.getCustomView();

                        inDialog.show();

                        TextView txtTitle = (TextView) v.findViewById(R.id.txtDialogTitle);
                        txtTitle.setText(getResources().getString(R.string.log_out));

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

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                HelperLogout.logout();
                                                hideProgressBar();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideProgressBar();
                                                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                                                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        snack.dismiss();
                                                    }
                                                });
                                                snack.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onTimeOut() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideProgressBar();
                                                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                                                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        snack.dismiss();
                                                    }
                                                });
                                                snack.show();
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
                    @Override public void onClick(View view) {

                        dialog.dismiss();

                        final MaterialDialog inDialog = new MaterialDialog.Builder(ActivitySetting.this).customView(R.layout.dialog_content_custom, true).build();
                        View v = inDialog.getCustomView();

                        inDialog.show();

                        TextView txtTitle = (TextView) v.findViewById(R.id.txtDialogTitle);
                        txtTitle.setText(getResources().getString(R.string.delete_account));

                        TextView iconTitle = (TextView) v.findViewById(R.id.iconDialogTitle);
                        iconTitle.setText(R.string.md_remove_circle);

                        TextView txtContent = (TextView) v.findViewById(R.id.txtDialogContent);
                        String text = getResources().getString(R.string.delete_account_text) + "\n" + getResources().getString(R.string.delete_account_text_desc);
                        txtContent.setText(text);

                        TextView txtCancel = (TextView) v.findViewById(R.id.txtDialogCancel);
                        TextView txtOk = (TextView) v.findViewById(R.id.txtDialogOk);


                        txtOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                inDialog.dismiss();
                                FragmentDeleteAccount fragmentDeleteAccount = new FragmentDeleteAccount();

                                Bundle bundle = new Bundle();
                                bundle.putString("PHONE", phoneName);
                                fragmentDeleteAccount.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.st_layoutParent, fragmentDeleteAccount, null).commit();
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
        });

        //fab button for set pic
        fab = (FloatingActionButton) findViewById(R.id.st_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                startDialog(R.array.profile);
            }
        });

         /*
          page for show all image user
         */
        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                HelperAvatar.avatarDelete(userId, mAvatarId, HelperAvatar.AvatarType.USER, new OnAvatarDelete() {
                    @Override public void latestAvatarPath(final String avatarPath) {
                        setImage(avatarPath);
                    }

                    @Override public void showInitials(final String initials, final String color) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                circleImageView.setImageBitmap(
                                    HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                                if (G.onChangeUserPhotoListener != null) {
                                    G.onChangeUserPhotoListener.onChangePhoto(null);
                                }
                            }
                        });
                    }
                });
            }
        };

        circleImageView = (net.iGap.module.CircleImageView) findViewById(R.id.st_img_circleImage);
        RippleView rippleImageView = (RippleView) findViewById(R.id.st_ripple_circleImage);
        rippleImageView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override public void onComplete(RippleView rippleView) {

                if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).count() > 0) {
                    FragmentShowAvatars.appBarLayout = fab;
                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
                    ActivitySetting.this.getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.st_layoutParent, fragment, null)
                        .commit();
                }
            }
        });

        String textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());
        if (textLanguage.equals("English")) {
            poRbDialogLangouage = 0;
        } else if (textLanguage.equals("فارسی")) {
            poRbDialogLangouage = 1;
        } else if (textLanguage.equals("العربی")) {
            poRbDialogLangouage = 2;
        } else if (textLanguage.equals("Deutsch")) {
            poRbDialogLangouage = 3;
        }

         /*
         choose language farsi or english ,arabic , .....
         */
        txtLanguage = (TextView) findViewById(R.id.st_txt_language);
        txtLanguage.setText(textLanguage);
        ViewGroup ltLanguage = (ViewGroup) findViewById(R.id.st_layout_language);
        ltLanguage.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_Language))
                    .titleGravity(GravityEnum.START)
                    .titleColor(getResources().getColor(android.R.color.black))
                    .items(R.array.language)
                    .itemsCallbackSingleChoice(poRbDialogLangouage, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                            txtLanguage.setText(text.toString());
                            poRbDialogLangouage = which;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SHP_SETTING.KEY_LANGUAGE, text.toString());
                            editor.apply();

                            switch (which) {
                                case 0:
                                    setLocale("en");
                                    if (onRefreshActivity != null) onRefreshActivity.refresh("en");
                                    HelperCalander.isLanguagePersian = false;
                                    G.selectedLanguage = "en";
                                    break;
                                case 1:
                                    G.selectedLanguage = "fa";
                                    setLocale("fa");
                                    if (onRefreshActivity != null) onRefreshActivity.refresh("fa");
                                    HelperCalander.isLanguagePersian = true;
                                    break;
                                case 2:
                                    G.selectedLanguage = "ar";
                                    setLocale("ar");
                                    if (onRefreshActivity != null) onRefreshActivity.refresh("ar");
                                    HelperCalander.isLanguagePersian = false;
                                    break;
                                case 3:
                                    G.selectedLanguage = "nl";
                                    setLocale("nl");
                                    if (onRefreshActivity != null) onRefreshActivity.refresh("nl");
                                    HelperCalander.isLanguagePersian = false;
                                    break;
                            }

                            return false;
                        }
                    })
                    .positiveText(getResources().getString(R.string.B_ok))
                    .negativeText(getResources().getString(R.string.B_cancel))
                    .show();
            }
        });

        final long sizeFolderPhoto = FileUtils.getFolderSize(new File(G.DIR_IMAGES));
        final long sizeFolderVideo = FileUtils.getFolderSize(new File(G.DIR_VIDEOS));
        final long sizeFolderDocument = FileUtils.getFolderSize(new File(G.DIR_DOCUMENT));

        final long total = sizeFolderPhoto + sizeFolderVideo + sizeFolderDocument;

        txtSizeClearCach = (TextView) findViewById(R.id.st_txt_clearCache);
        txtSizeClearCach.setText(FileUtils.formatFileSize(total));

        RelativeLayout lyCleanUp = (RelativeLayout) findViewById(R.id.st_layout_cleanup);
        lyCleanUp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                final MaterialDialog inDialog = new MaterialDialog.Builder(ActivitySetting.this).customView(R.layout.dialog_content_custom, true).build();
                View view = inDialog.getCustomView();

                inDialog.show();

                TextView txtTitle = (TextView) view.findViewById(R.id.txtDialogTitle);
                txtTitle.setText(getResources().getString(R.string.clean_up_chat_rooms));

                TextView iconTitle = (TextView) view.findViewById(R.id.iconDialogTitle);
                iconTitle.setText(R.string.md_clean_up);

                TextView txtContent = (TextView) view.findViewById(R.id.txtDialogContent);
                txtContent.setText(R.string.do_you_want_to_clean_all_data_in_chat_rooms);

                TextView txtCancel = (TextView) view.findViewById(R.id.txtDialogCancel);
                TextView txtOk = (TextView) view.findViewById(R.id.txtDialogOk);

                txtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inDialog.dismiss();
                        RealmRoomMessage.ClearAllMessage(true, 0);
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

         /*
          clear all video , image , document cache
         */
        LinearLayout ltClearCache = (LinearLayout) findViewById(R.id.st_layout_clearCache);
        ltClearCache.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                final long sizeFolderPhotoDialog = FileUtils.getFolderSize(new File(G.DIR_IMAGES));
                final long sizeFolderVideoDialog = FileUtils.getFolderSize(new File(G.DIR_VIDEOS));
                final long sizeFolderDocumentDialog = FileUtils.getFolderSize(new File(G.DIR_DOCUMENT));

                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_title_Clear_Cache))
                    .customView(R.layout.st_dialog_clear_cach, wrapInScrollView)
                    .positiveText(getResources().getString(R.string.st_title_Clear_Cache))
                    .show();

                View view = dialog.getCustomView();

                final File filePhoto = new File(G.DIR_IMAGES);
                assert view != null;
                TextView photo = (TextView) view.findViewById(R.id.st_txt_sizeFolder_photo);
                photo.setText(FileUtils.formatFileSize(sizeFolderPhotoDialog));

                final CheckBox checkBoxPhoto = (CheckBox) view.findViewById(R.id.st_checkBox_photo);
                final File fileVideo = new File(G.DIR_VIDEOS);
                TextView video = (TextView) view.findViewById(R.id.st_txt_sizeFolder_video);
                video.setText(FileUtils.formatFileSize(sizeFolderVideoDialog));

                final CheckBox checkBoxVideo = (CheckBox) view.findViewById(R.id.st_checkBox_video_dialogClearCash);

                final File fileDocument = new File(G.DIR_DOCUMENT);
                TextView document = (TextView) view.findViewById(R.id.st_txt_sizeFolder_document_dialogClearCash);
                document.setText(FileUtils.formatFileSize(sizeFolderDocumentDialog));

                final CheckBox checkBoxDocument = (CheckBox) view.findViewById(R.id.st_checkBox_document_dialogClearCash);

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        if (checkBoxPhoto.isChecked()) {
                            for (File file : filePhoto.listFiles()) {
                                if (!file.isDirectory()) file.delete();
                            }
                        }
                        if (checkBoxVideo.isChecked()) {
                            for (File file : fileVideo.listFiles()) {
                                if (!file.isDirectory()) file.delete();
                            }
                        }
                        if (checkBoxDocument.isChecked()) {
                            for (File file : fileDocument.listFiles()) {
                                if (!file.isDirectory()) file.delete();
                            }
                        }
                        long afterClearSizeFolderPhoto = FileUtils.getFolderSize(new File(G.DIR_IMAGES));
                        long afterClearSizeFolderVideo = FileUtils.getFolderSize(new File(G.DIR_VIDEOS));
                        long afterClearSizeFolderDocument = FileUtils.getFolderSize(new File(G.DIR_DOCUMENT));
                        long afterClearTotal = afterClearSizeFolderPhoto + afterClearSizeFolderVideo + afterClearSizeFolderDocument;
                        txtSizeClearCach.setText(FileUtils.formatFileSize(afterClearTotal));
                        dialog.dismiss();
                    }
                });
            }
        });

         /*
          setting toggle  crop page
         */

        final TextView txtCrop = (TextView) findViewById(R.id.stsp_txt_crop);
        final ToggleButton stsp_toggle_crop = (ToggleButton) findViewById(R.id.stsp_toggle_crop);

        int checkedEnableCrop = sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1);
        if (checkedEnableCrop == 1) {
            stsp_toggle_crop.setChecked(true);
        } else {
            stsp_toggle_crop.setChecked(false);
        }

        stsp_toggle_crop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_CROP, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_CROP, 0);
                    editor.apply();
                }
            }
        });

        txtCrop.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                stsp_toggle_crop.setChecked(!stsp_toggle_crop.isChecked());
            }
        });

         /*
          setting toggle vote channel
         */
        final TextView txtVote = (TextView) findViewById(R.id.as_txt_show_vote);
        final ToggleButton toggleVote = (ToggleButton) findViewById(R.id.as_toggle_show_vote);

        int checkedEnableVote = sharedPreferences.getInt(SHP_SETTING.KEY_VOTE, 1);
        if (checkedEnableVote == 1) {
            toggleVote.setChecked(true);
        } else {
            toggleVote.setChecked(false);
        }

        toggleVote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_VOTE, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_VOTE, 0);
                    editor.apply();
                }
            }
        });

        txtVote.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                toggleVote.setChecked(!toggleVote.isChecked());
            }
        });


            /*
          setting toggle show sender name in group
         */
        final TextView txtShowSenderNameInGroup = (TextView) findViewById(R.id.as_txt_show_sender_name_group);
        final ToggleButton toggleShowSenderInGroup = (ToggleButton) findViewById(R.id.as_toggle_show_sender_name_group);

        int checkedEnablShowSenderInGroup = sharedPreferences.getInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0);
        if (checkedEnablShowSenderInGroup == 1) {
            toggleShowSenderInGroup.setChecked(true);
        } else {
            toggleShowSenderInGroup.setChecked(false);
        }

        toggleShowSenderInGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

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
        });

        txtShowSenderNameInGroup.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                toggleShowSenderInGroup.setChecked(!toggleShowSenderInGroup.isChecked());
            }
        });




         /*
          setting toggle toggle compress
         */

        final TextView txtCompress = (TextView) findViewById(R.id.stsp_txt_compress);
        final ToggleButton stsp_toggle_Compress = (ToggleButton) findViewById(R.id.stsp_toggle_compress);

        int checkedEnableCompress = sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1);
        if (checkedEnableCompress == 1) {
            stsp_toggle_Compress.setChecked(true);
        } else {
            stsp_toggle_Compress.setChecked(false);
        }

        stsp_toggle_Compress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_COMPRESS, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_COMPRESS, 0);
                    editor.apply();
                }
            }
        });

        txtCompress.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                stsp_toggle_Compress.setChecked(!stsp_toggle_Compress.isChecked());
            }
        });

         /*
          setting toggle toggle trim
         */

        final TextView txtTrim = (TextView) findViewById(R.id.stsp_txt_trim);
        final ToggleButton stsp_toggle_Trim = (ToggleButton) findViewById(R.id.stsp_toggle_trim);

        int checkedEnableTrim = sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1);
        if (checkedEnableTrim == 1) {
            stsp_toggle_Trim.setChecked(true);
        } else {
            stsp_toggle_Trim.setChecked(false);
        }

        stsp_toggle_Trim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_TRIM, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_TRIM, 0);
                    editor.apply();
                }
            }
        });

        txtTrim.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                stsp_toggle_Trim.setChecked(!stsp_toggle_Trim.isChecked());
            }
        });


          /*
         open privacy abd security page
         */
        TextView txtPrivacySecurity = (TextView) findViewById(R.id.st_txt_privacySecurity);
        txtPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                FragmentPrivacyAndSecurity fragmentPrivacyAndSecurity = new FragmentPrivacyAndSecurity();
                getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.st_layoutParent, fragmentPrivacyAndSecurity, null)
                    .commit();
            }
        });

         /*
          setting toggle DataShams
         */
        final TextView txtDataShams = (TextView) findViewById(R.id.st_txt_st_toggle_dataShams);
        final ToggleButton toggleEnableDataShams = (ToggleButton) findViewById(R.id.st_toggle_dataShams);

        int checkedEnableDataShams = sharedPreferences.getInt(SHP_SETTING.KEY_ENABLE_DATA_SHAMS, 0);
        if (checkedEnableDataShams == 1) {
            toggleEnableDataShams.setChecked(true);
        } else {
            toggleEnableDataShams.setChecked(false);
        }

        toggleEnableDataShams.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_ENABLE_DATA_SHAMS, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_ENABLE_DATA_SHAMS, 0);
                    editor.apply();
                }

                if (G.onRefreshActivity != null) {
                    G.onRefreshActivity.refresh(G.selectedLanguage);
                }
            }
        });

        txtDataShams.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                toggleEnableDataShams.setChecked(!toggleEnableDataShams.isChecked());
            }
        });


         /*
          setting text size for chat room
         */
        poRbDialogTextSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14) - 11;
        txtMessageTextSize = (TextView) findViewById(R.id.st_txt_messageTextSize_number);
        txtMessageTextSize.setText("" + sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14));

        if (HelperCalander.isLanguagePersian) {
            txtMessageTextSize.setText(HelperCalander.convertToUnicodeFarsiNumber(txtMessageTextSize.getText().toString()));
        }

        ViewGroup ltMessageTextSize = (ViewGroup) findViewById(R.id.st_layout_messageTextSize);
        ltMessageTextSize.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_title_message_textSize))
                    .titleGravity(GravityEnum.START)
                    .titleColor(getResources().getColor(android.R.color.black))
                    .items(HelperCalander.isLanguagePersian ? R.array.message_text_size_persian : R.array.message_text_size)
                    .itemsCallbackSingleChoice(poRbDialogTextSize, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                            if (text != null) {
                                txtMessageTextSize.setText(text.toString().replace("(Hello)", "").trim());

                                if (HelperCalander.isLanguagePersian) {
                                    txtMessageTextSize.setText(HelperCalander.convertToUnicodeFarsiNumber(txtMessageTextSize.getText().toString()));
                                }
                            }
                            poRbDialogTextSize = which;
                            int size = Integer.parseInt(text.toString().replace("(Hello)", "").trim());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, size);
                            editor.apply();

                            StartupActions.textSizeDetection(sharedPreferences);

                            return false;
                        }
                    })
                    .positiveText(getResources().getString(R.string.B_ok))
                    .show();
            }
        });

         /*
          open page chat background
         */
        TextView txtChatBackground = (TextView) findViewById(R.id.st_txt_chatBackground);
        txtChatBackground.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startActivity(new Intent(ActivitySetting.this, ActivityChatBackground.class));
            }
        });

        //***********************
        imgAppBarSelected = (ImageView) findViewById(R.id.asn_img_title_bar_color);
        GradientDrawable bgShape = (GradientDrawable) imgAppBarSelected.getBackground();
        bgShape.setColor(Color.parseColor(G.appBarColor));

        TextView txtSelectAppColor = (TextView) findViewById(R.id.asn_txt_app_title_bar_color);
        txtSelectAppColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showSelectAppColorDialog(R.string.app_theme);
            }
        });

        imgAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                showSelectAppColorDialog(R.string.app_theme);
            }
        });

        //***********************

        imgNotificationColor = (ImageView) findViewById(R.id.asn_img_notification_color);
        GradientDrawable bgShapeNotification = (GradientDrawable) imgNotificationColor.getBackground();
        bgShapeNotification.setColor(Color.parseColor(G.notificationColor));

        imgNotificationColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                showSelectAppColorDialog(R.string.app_notif_color);
            }
        });

        TextView txtNotificatinColor = (TextView) findViewById(R.id.asn_txt_app_notification_color);
        txtNotificatinColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showSelectAppColorDialog(R.string.app_notif_color);
            }
        });

        //***********************

        imgToggleBottomColor = (ImageView) findViewById(R.id.asn_img_toggle_botton_color);
        GradientDrawable bgShapeToggleBottomColor = (GradientDrawable) imgToggleBottomColor.getBackground();
        bgShapeToggleBottomColor.setColor(Color.parseColor(G.toggleButtonColor));

        imgToggleBottomColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                showSelectAppColorDialog(R.string.toggle_botton_color);
            }
        });

        TextView txtToggleBottomColor = (TextView) findViewById(R.id.asn_txt_app_toggle_botton_color);
        txtToggleBottomColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showSelectAppColorDialog(R.string.toggle_botton_color);
            }
        });

        //***********************

        imgSendAndAttachColor = (ImageView) findViewById(R.id.asn_img_send_and_attach_color);
        GradientDrawable bgShapeSendAndAttachColor = (GradientDrawable) imgSendAndAttachColor.getBackground();
        bgShapeSendAndAttachColor.setColor(Color.parseColor(G.attachmentColor));

        imgSendAndAttachColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                showSelectAppColorDialog(R.string.send_and_attach_botton_color);
            }
        });

        TextView txtSendAndAttachColor = (TextView) findViewById(R.id.asn_txt_send_and_attach_color);
        txtSendAndAttachColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showSelectAppColorDialog(R.string.send_and_attach_botton_color);
            }
        });

        //***********************

        imgHeaderTextColor = (ImageView) findViewById(R.id.asn_img_default_header_font_color);
        GradientDrawable bgShapeHeaderTextColor = (GradientDrawable) imgHeaderTextColor.getBackground();
        bgShapeHeaderTextColor.setColor(Color.parseColor(G.headerTextColor));

        imgHeaderTextColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                showSelectAppColorDialog(R.string.default_header_font_color);
            }
        });

        TextView txtHeaderTextColor = (TextView) findViewById(R.id.asn_txt_default_header_font_color);
        txtHeaderTextColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showSelectAppColorDialog(R.string.default_header_font_color);
            }
        });

        TextView txtSetToDefaultColor = (TextView) findViewById(R.id.asn_txt_set_color_to_default);
        txtSetToDefaultColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showSetDefaultColorDialog();
            }
        });

        //***********************

        imgHeaderProgressColor = (ImageView) findViewById(R.id.asn_img_default_progress_color);
        GradientDrawable bgShapeProgressColor = (GradientDrawable) imgHeaderProgressColor.getBackground();
        bgShapeProgressColor.setColor(Color.parseColor(G.progressColor));

        imgHeaderProgressColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                showSelectAppColorDialog(R.string.default_progress_color);
            }
        });

        TextView txtProgressColor = (TextView) findViewById(R.id.asn_txt_default_progress_color);
        txtProgressColor.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showSelectAppColorDialog(R.string.default_progress_color);
            }
        });


        //***********************
         /*
          open browser
         */
        TextView ltInAppBrowser = (TextView) findViewById(R.id.st_txt_inAppBrowser);
        toggleInAppBrowser = (ToggleButton) findViewById(R.id.st_toggle_inAppBrowser);
        int checkedInappBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
        if (checkedInappBrowser == 1) {
            toggleInAppBrowser.setChecked(true);
        } else {
            toggleInAppBrowser.setChecked(false);
        }

        ltInAppBrowser.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (toggleInAppBrowser.isChecked()) {
                    toggleInAppBrowser.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_IN_APP_BROWSER, 0);
                    editor.apply();
                } else {
                    toggleInAppBrowser.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
                    editor.apply();
                }
            }
        });

        TextView txtNotifyAndSound = (TextView) findViewById(R.id.st_txt_notifyAndSound);
        txtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startActivity(new Intent(ActivitySetting.this, ActivitySettingNotification.class));
            }
        });

        TextView ltSentByEnter = (TextView) findViewById(R.id.st_txt_sendEnter);
        toggleSentByEnter = (ToggleButton) findViewById(R.id.st_toggle_sendEnter);
        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        if (checkedSendByEnter == 1) {
            toggleSentByEnter.setChecked(true);
        } else {
            toggleSentByEnter.setChecked(false);
        }

        toggleSentByEnter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
                    editor.apply();
                }
            }
        });

        ltSentByEnter.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                toggleSentByEnter.setChecked(!toggleSentByEnter.isChecked());
            }
        });

        final TextView txtSubKeepMedia = (TextView) findViewById(R.id.st_txt_sub_keepMedia);
        ViewGroup ltKeepMedia = (ViewGroup) findViewById(R.id.st_layout_keepMedia);
        TextView txtKeepMedia = (TextView) findViewById(R.id.st_txt_keepMedia);
        boolean isForever = sharedPreferences.getBoolean(SHP_SETTING.KEY_KEEP_MEDIA, true);
        Log.i("KKKKKKK", "isForever: " + isForever);
        if (isForever) {
            txtSubKeepMedia.setText(getResources().getString(R.string.keep_media_forever));
        } else {
            txtSubKeepMedia.setText(getResources().getString(R.string.keep_media_1week));
        }

        ltKeepMedia.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.st_keepMedia)
                    .content(R.string.st_dialog_content_keepMedia)
                    .positiveText(getResources().getString(R.string.keep_media_forever))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(SHP_SETTING.KEY_KEEP_MEDIA, false);
                            editor.apply();
                            txtSubKeepMedia.setText(getResources().getString(R.string.keep_media_forever));
                        }
                    })
                    .negativeText(getResources().getString(R.string.keep_media_1week))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(SHP_SETTING.KEY_KEEP_MEDIA, true);
                            editor.apply();
                            txtSubKeepMedia.setText(getResources().getString(R.string.keep_media_1week));
                        }
                    })
                    .show();
            }
        });

        TextView txtAutoDownloadData = (TextView) findViewById(R.id.st_txt_autoDownloadData);
        txtAutoDownloadData.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                KEY_AD_DATA_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1);
                KEY_AD_DATA_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1);
                KEY_AD_DATA_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1);
                KEY_AD_DATA_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_FILE, -1);
                KEY_AD_DATA_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1);
                KEY_AD_DATA_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_GIF, 5);

                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.title_auto_download_data).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[] {
                    KEY_AD_DATA_PHOTO, KEY_AD_DATA_VOICE_MESSAGE, KEY_AD_DATA_VIDEO, KEY_AD_DATA_FILE, KEY_AD_DATA_MUSIC, KEY_AD_DATA_GIF
                }, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

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
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
            }
        });

        TextView txtAutoDownloadWifi = (TextView) findViewById(R.id.st_txt_autoDownloadWifi);
        txtAutoDownloadWifi.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                KEY_AD_WIFI_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1);
                KEY_AD_WIFI_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1);
                KEY_AD_WIFI_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1);
                KEY_AD_WIFI_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1);
                KEY_AD_WIFI_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1);
                KEY_AD_WIFI_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_GIF, 5);

                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.title_auto_download_wifi).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[] {
                    KEY_AD_WIFI_PHOTO, KEY_AD_WIFI_VOICE_MESSAGE, KEY_AD_WIFI_VIDEO, KEY_AD_WIFI_FILE, KEY_AD_WIFI_MUSIC, KEY_AD_WIFI_GIF
                }, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

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
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.cancel)).show();
            }
        });

        TextView txtAutoDownloadRoaming = (TextView) findViewById(R.id.st_txt_autoDownloadRoaming);
        txtAutoDownloadRoaming.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                KEY_AD_ROAMING_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
                KEY_AD_ROAMING_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
                KEY_AD_ROAMING_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
                KEY_AD_ROAMING_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
                KEY_AD_ROAMING_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
                KEY_AD_ROAMINGN_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);

                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.title_auto_download_roaming).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[] {
                    KEY_AD_ROAMING_PHOTO, KEY_AD_ROAMING_VOICE_MESSAGE, KEY_AD_ROAMING_VIDEO, KEY_AD_ROAMING_FILE, KEY_AD_ROAMING_MUSIC, KEY_AD_ROAMINGN_GIF
                }, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

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
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
            }
        });

        TextView ltEnableAnimation = (TextView) findViewById(R.id.st_txt_enableAnimation);
        toggleEnableAnimation = (ToggleButton) findViewById(R.id.st_toggle_enableAnimation);
        int checkedEnableAnimation = sharedPreferences.getInt(SHP_SETTING.KEY_ENABLE_ANIMATION, 0);
        if (checkedEnableAnimation == 1) {
            toggleEnableAnimation.setChecked(true);
        } else {
            toggleEnableAnimation.setChecked(false);
        }

        ltEnableAnimation.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (toggleEnableAnimation.isChecked()) {
                    toggleEnableAnimation.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_ENABLE_ANIMATION, 0);
                    editor.apply();
                } else {
                    toggleEnableAnimation.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_ENABLE_ANIMATION, 1);
                    editor.apply();
                }
            }
        });

        TextView ltAutoGifs = (TextView) findViewById(R.id.st_txt_autoGif);
        toggleAutoGifs = (ToggleButton) findViewById(R.id.st_toggle_autoGif);
        int checkedAutoGif = sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS);
        if (checkedAutoGif == 1) {
            toggleAutoGifs.setChecked(true);
        } else {
            toggleAutoGifs.setChecked(false);
        }

        toggleAutoGifs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 0);
                    editor.apply();
                }
            }
        });

        ltAutoGifs.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                toggleAutoGifs.setChecked(!toggleAutoGifs.isChecked());
            }
        });

        TextView ltSaveToGallery = (TextView) findViewById(R.id.st_txt_saveGallery);
        toggleSaveToGallery = (ToggleButton) findViewById(R.id.st_toggle_saveGallery);
        int checkedSaveToGallery = sharedPreferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
        if (checkedSaveToGallery == 1) {
            toggleSaveToGallery.setChecked(true);
        } else {
            toggleSaveToGallery.setChecked(false);
        }

        toggleSaveToGallery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

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
        });

        ltSaveToGallery.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                toggleSaveToGallery.setChecked(!toggleSaveToGallery.isChecked());
            }
        });

        TextView txtWebViewHome = (TextView) findViewById(R.id.st_txt_iGap_home);
        txtWebViewHome.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                HelperUrl.openBrowser("https://www.igap.net/");
            }
        });

        TextView txtWebViewBlog = (TextView) findViewById(R.id.st_txt_privacy_blog);
        txtWebViewBlog.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                HelperUrl.openBrowser("https://blog.igap.net");
            }
        });

        TextView txtCreateTicket = (TextView) findViewById(R.id.st_txt_create_ticket);
        txtCreateTicket.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                HelperUrl.openBrowser("https://support.igap.net");
            }
        });

        TextView txtVersionApp = (TextView) findViewById(R.id.st_txt_versionApp);

        txtVersionApp.setText(getString(R.string.iGap_version) + " " + getAppVersion());

        if (HelperCalander.isLanguagePersian) {
            txtVersionApp.setText(HelperCalander.convertToUnicodeFarsiNumber(txtVersionApp.getText().toString()));
        }

        showImage();
    }

    private void updateUserInfoUI(RealmUserInfo userInfo) {
        if (checkValidationForRealm(userInfo)) {
            userId = userInfo.getUserId();
            String nickName = userInfo.getUserInfo().getDisplayName();
            userName = userInfo.getUserInfo().getUsername();
            phoneName = userInfo.getUserInfo().getPhoneNumber();
            ProtoGlobal.Gender userGender = userInfo.getGender();
            userEmail = userInfo.getEmail();

            if (nickName != null) {
                txtNickName.setText(nickName);
                txtNickNameTitle.setText(nickName);
            }

            if (userName != null) txtUserName.setText(userName);

            if (phoneName != null) txtPhoneNumber.setText(phoneName);

            if (HelperCalander.isLanguagePersian) {
                txtPhoneNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtPhoneNumber.getText().toString()));
            }

            if (userGender != null) {
                if (userGender == ProtoGlobal.Gender.MALE) {
                    txtGander.setText(getResources().getString(R.string.Male));
                } else if (userGender == ProtoGlobal.Gender.FEMALE) {
                    txtGander.setText(getResources().getString(R.string.Female));
                }
            } else {
                txtGander.setText(getResources().getString(R.string.set_gender));
            }

            if (userEmail != null && userEmail.length() > 0) {
                txtEmail.setText(userEmail);
            } else {
                txtEmail.setText(getResources().getString(R.string.set_email));
            }
        }
    }

    private boolean checkValidationForRealm(RealmUserInfo realmUserInfo) {
        if (realmUserInfo != null && realmUserInfo.isManaged() && realmUserInfo.isValid() && realmUserInfo.isLoaded()) {
            return true;
        }
        return false;
    }

    // call this method for show choose color dialog
    private void showSelectAppColorDialog(final int title) {

        boolean wrapInScrollView = true;

        String titleMessage = getResources().getString(title);

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).customView(R.layout.stns_popup_colorpicer, wrapInScrollView)
            .positiveText(getResources().getString(R.string.set))
            .negativeText(getResources().getString(R.string.DISCARD))
            .title(titleMessage)
            .onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                }
            })
            .build();

        View view1 = dialog.getCustomView();
        assert view1 != null;
        final ColorPicker picker = (ColorPicker) view1.findViewById(R.id.picker);
        SVBar svBar = (SVBar) view1.findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) view1.findViewById(R.id.opacitybar);
        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

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

                    new MaterialDialog.Builder(ActivitySetting.this).title(R.string.selected_color_can_not_set_on_yout_device).cancelable(true).show();
                }
            }
        });

        dialog.show();
    }

    private void showSetDefaultColorDialog() {

        new MaterialDialog.Builder(ActivitySetting.this).title(R.string.set_color_to_default)
            .positiveText(R.string.st_dialog_reset_all_notification_yes)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    notificationColorClick(Color.parseColor(Config.default_notificationColor), false);
                    headerColorClick(Color.parseColor(Config.default_headerTextColor), false);
                    //  toggleBottomClick(Color.parseColor(Config.default_toggleButtonColor));
                    sendAndAttachColorClick(Color.parseColor(Config.default_attachmentColor));
                    appBarColorClick(Color.parseColor(Config.default_appBarColor));
                    progressColorClick(Color.parseColor(Config.default_appBarColor), false);
                }
            })
            .negativeText(R.string.st_dialog_reset_all_notification_no)
            .onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                }
            })
            .show();
    }

    private void appBarColorClick(int color) {

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        GradientDrawable bgShape = (GradientDrawable) imgAppBarSelected.getBackground();
        G.appBarColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_APP_BAR_COLOR, G.appBarColor);
        editor.apply();

        ActivitySetting.this.recreate();
        if (G.onRefreshActivity != null) {
            G.onRefreshActivity.refresh(G.selectedLanguage);
        }
    }

    private void notificationColorClick(int color, boolean updateUi) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) imgNotificationColor.getBackground();
        G.notificationColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_NOTIFICATION_COLOR, G.notificationColor);
        editor.apply();

        if (updateUi && G.onRefreshActivity != null) {
            G.onRefreshActivity.refresh(G.selectedLanguage);
        }
    }

    private void progressColorClick(int color, boolean updateUi) {

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) imgHeaderProgressColor.getBackground();
        G.progressColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_PROGRES_COLOR, G.progressColor);
        editor.apply();

        if (updateUi && G.onRefreshActivity != null) {
            G.onRefreshActivity.refresh(G.selectedLanguage);
        }
    }

    private void toggleBottomClick(int color) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) imgToggleBottomColor.getBackground();
        G.toggleButtonColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, G.toggleButtonColor);
        editor.apply();
    }

    private void headerColorClick(int color, boolean updateUi) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) imgHeaderTextColor.getBackground();
        G.headerTextColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_FONT_HEADER_COLOR, G.headerTextColor);
        editor.apply();

        if (updateUi) ActivitySetting.this.recreate();
    }

    private void sendAndAttachColorClick(int color) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        GradientDrawable bgShape = (GradientDrawable) imgSendAndAttachColor.getBackground();
        G.attachmentColor = "#" + Integer.toHexString(color);
        bgShape.setColor(color);
        editor.putString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, G.attachmentColor);
        editor.apply();
    }

    // call this method for show image in enter to this activity
    private void showImage() {
        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
            @Override public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), circleImageView);
                    }
                });
            }

            @Override public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        circleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });
    }

    //dialog for choose pic from gallery or camera
    private void startDialog(int r) {

        new MaterialDialog.Builder(this).title(getResources().getString(R.string.choose_picture))
            .negativeText(getResources().getString(R.string.B_cancel))
            .items(r)
            .itemsCallback(new MaterialDialog.ListCallback() {
                @Override public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                    if (text.toString().equals(getResources().getString(R.string.array_From_Camera))) { // camera

                        try {
                            HelperPermision.getStoragePermision(ActivitySetting.this, new OnGetPermission() {
                                @Override public void Allow() throws IOException {
                                    HelperPermision.getCameraPermission(ActivitySetting.this, new OnGetPermission() {
                                        @Override public void Allow() {
                                            dialog.dismiss();
                                            useCamera();
                                        }

                                        @Override public void deny() {

                                        }
                                    });
                                }

                                @Override public void deny() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            new AttachFile(ActivitySetting.this).requestOpenGalleryForImageSingleSelect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }
            })
            .show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(ActivitySetting.this).dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                idAvatar = SUID.id().get();
                pathSaveImage = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + idAvatar + ".jpg";
                File nameImageFile = new File(pathSaveImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uriIntent = Uri.fromFile(nameImageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);
            } else {
                Toast.makeText(ActivitySetting.this, getString(R.string.please_check_your_camera), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //=====================================================================================result
    // from camera , gallery and crop
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                intent.putExtra("IMAGE_CAMERA", AttachFile.mCurrentPhotoPath);
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", "setting");
                intent.putExtra("ID", (int) (idAvatar + 1L));
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            } else {
                Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
                if (uriIntent != null) {
                    ImageHelper.correctRotateImage(pathSaveImage, true);
                    intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
                    intent.putExtra("TYPE", "camera");
                    intent.putExtra("PAGE", "setting");
                    intent.putExtra("ID", (int) (idAvatar + 1L));
                    startActivityForResult(intent, IntentRequests.REQ_CROP);
                }
            }
        } else if (requestCode == AttachFile.request_code_image_from_gallery_single_select && resultCode == RESULT_OK) {// result for gallery
            if (data != null) {
                if (data.getData() == null) {
                    return;
                }
                Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUri(data.getData()));
                intent.putExtra("TYPE", "gallery");
                intent.putExtra("PAGE", "setting");
                intent.putExtra("ID", (int) (idAvatar + 1L));
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            }
        } else if (requestCode == IntentRequests.REQ_CROP && resultCode == RESULT_OK) { // save path image on data base ( realm )

            if (data != null) {
                pathSaveImage = data.getData().toString();
            }

            long lastUploadedAvatarId = idAvatar + 1L;

            showProgressBar();
            HelperUploadFile.startUploadTaskAvatar(pathSaveImage, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                @Override public void OnProgress(int progress, FileUploadStructure struct) {
                    if (progress < 100) {
                        prgWait.setProgress(progress);
                    } else {
                        new RequestUserAvatarAdd().userAddAvatar(struct.token);
                    }
                }

                @Override public void OnError() {
                    hideProgressBar();
                }
            });
        }
    }

    // change language
    public void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        startActivity(new Intent(ActivitySetting.this, ActivitySetting.class));
        overridePendingTransition(0, 0);
        finish();
    }

    @Override public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {

        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            showImage();
        } else {
            HelperAvatar.avatarAdd(userId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override public void onAvatarAdd(final String avatarPath) {

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            hideProgressBar();
                            setImage(avatarPath);
                        }
                    });
                }
            });
            pathSaveImage = null;
        }
    }

    @Override public void onAvatarAddTimeOut() {
        hideProgressBar();
    }

    @Override public void onAvatarError() {
        hideProgressBar();
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

    //***Show And Hide Progress

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if (prgWait != null) {
                    prgWait.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if (prgWait != null) {
                    prgWait.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(title)
            .customView(R.layout.dialog_remind_time, wrapInScrollView)
            .positiveText(R.string.B_ok)
            .autoDismiss(false)
            .canceledOnTouchOutside(false)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            })
            .show();

        View v = dialog.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                //                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            @Override public void onFinish() {
                //                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            }
        };
        countWaitTimer.start();
    }

    private final CustomTabsActivityHelper.CustomTabsFallback mCustomTabsFallback = new CustomTabsActivityHelper.CustomTabsFallback() {
        @Override public void openUri(Activity activity, Uri uri) {

            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    };
}
