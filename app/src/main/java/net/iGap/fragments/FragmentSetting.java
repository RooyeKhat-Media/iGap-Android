package net.iGap.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCrop;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
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
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.interfaces.OnUserProfileUpdateUsername;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.IntentRequests;
import net.iGap.module.MEditText;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.StartupActions;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoUserProfileCheckUsername;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmPrivacy;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserAvatarAdd;
import net.iGap.request.RequestUserProfileCheckUsername;
import net.iGap.request.RequestUserProfileGetBio;
import net.iGap.request.RequestUserProfileGetEmail;
import net.iGap.request.RequestUserProfileGetGender;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;
import static net.iGap.G.onRefreshActivity;
import static net.iGap.R.string.log_out;
import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetting extends BaseFragment implements OnUserAvatarResponse {

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
    private EmojiTextViewE txtNickName;
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
    private String bio;
    private long userId;
    public ProgressBar prgWait;
    private TextView txtGander;
    private TextView txtEmail;
    private EmojiTextViewE txtNickNameTitle;
    static boolean isActiveRun = false;
    private Realm mRealm;
    private Fragment fragment;
    private ViewGroup ltBio;
    private TextView txtBio;
    public static DateType dateType;


    RealmUserInfo realmUserInfo;
    RealmRegisteredInfo mRealmRegisteredInfo;

    public FragmentSetting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_setting, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        RealmPrivacy realmPrivacy = getRealm().where(RealmPrivacy.class).findFirst();

        if (realmPrivacy == null) {
            RealmPrivacy.updatePrivacy("", "", "", "", "");
        }

        fragment = this;
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        txtNickNameTitle = (EmojiTextViewE) view.findViewById(R.id.ac_txt_nickname_title);
        txtNickName = (EmojiTextViewE) view.findViewById(R.id.st_txt_nikName);
        txtUserName = (TextView) view.findViewById(R.id.st_txt_userName);
        txtPhoneNumber = (TextView) view.findViewById(R.id.st_txt_phoneNumber);
        txtGander = (TextView) view.findViewById(R.id.st_txt_gander);
        txtEmail = (TextView) view.findViewById(R.id.st_txt_email);
        prgWait = (ProgressBar) view.findViewById(R.id.st_prgWaiting_addContact);

        txtBio = (TextView) view.findViewById(R.id.st_txt_bio);

        ViewGroup layoutBio = (ViewGroup) view.findViewById(R.id.st_layout_bio);
        layoutBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBio fragmentBio = new FragmentBio();
                Bundle bundle = new Bundle();
                bundle.putString("BIO", txtBio.getText().toString());
                fragmentBio.setArguments(bundle);

                new HelperFragment(fragmentBio).setReplace(false).load();
            }
        });

        AppUtils.setProgresColler(prgWait);

        new RequestUserProfileGetGender().userProfileGetGender();
        new RequestUserProfileGetEmail().userProfileGetEmail();
        new RequestUserProfileGetBio().getBio();

        realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();

        if (realmUserInfo == null) {
            //finish();

            G.fragmentActivity.onBackPressed();

            return;
        }

        updateUserInfoUI(realmUserInfo);
         /*
          set layout and open dialog for set or change Name & Family
         */
        ViewGroup layoutNickname = (ViewGroup) view.findViewById(R.id.st_layout_nickname);
        layoutNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
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

                final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_nickname))
                        .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                        .customView(layoutNickname, true)
                        .widgetColor(G.context.getResources().getColor(R.color.toolbar_background))
                        .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                        .build();

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
                                setAvatar();

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
        });

         /*
          open dialog for set or change gander
         */
        ViewGroup layoutGander = (ViewGroup) view.findViewById(R.id.st_layout_gander);
        layoutGander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        /*
          set layout and open dialog for set or change email address
         */
        ViewGroup ltEmail = (ViewGroup) view.findViewById(R.id.st_layout_email);
        ltEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout layoutEmail = new LinearLayout(G.fragmentActivity);
                layoutEmail.setOrientation(LinearLayout.VERTICAL);

                final View viewEmail = new View(G.fragmentActivity);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

                final TextInputLayout inputEmail = new TextInputLayout(G.fragmentActivity);
                final MEditText edtEmail = new MEditText(G.fragmentActivity);
                edtEmail.setHint(G.fragmentActivity.getResources().getString(R.string.set_email));
                edtEmail.setTypeface(G.typeface_IRANSansMobile);
                edtEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));

                if (txtEmail == null || txtEmail.getText().toString().equals(G.fragmentActivity.getResources().getString(R.string.set_email))) {
                    edtEmail.setText("");
                } else {
                    edtEmail.setText(txtEmail.getText().toString());
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
        });
         /*
          set layout and open dialog for change userName
         */
        ViewGroup layoutUserName = (ViewGroup) view.findViewById(R.id.st_layout_username);
        layoutUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final LinearLayout layoutUserName = new LinearLayout(G.fragmentActivity);
                layoutUserName.setOrientation(LinearLayout.VERTICAL);

                final View viewUserName = new View(G.fragmentActivity);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

                final TextInputLayout inputUserName = new TextInputLayout(G.fragmentActivity);
                final MEditText edtUserName = new MEditText(G.fragmentActivity);
                edtUserName.setHint(G.fragmentActivity.getResources().getString(R.string.st_username));
                edtUserName.setText(txtUserName.getText().toString());
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

                final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_username))
                        .positiveText(G.fragmentActivity.getResources().getString(R.string.save))
                        .customView(layoutUserName, true)
                        .widgetColor(G.context.getResources().getColor(R.color.toolbar_background))
                        .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                        .build();

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
        });

        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.st_appbar);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.st_collapsing_layout);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));

        final TextView titleToolbar = (TextView) view.findViewById(R.id.st_txt_titleToolbar);
        final ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.st_parentLayoutCircleImage);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, final int verticalOffset) {


                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
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
        RippleView rippleBack = (RippleView) view.findViewById(R.id.st_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                //finish();
                G.fragmentActivity.onBackPressed();
            }
        });

        /*
          set layout for popup menu
         */
        RippleView rippleMore = (RippleView) view.findViewById(R.id.st_ripple_more);
        rippleMore.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {

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
                                bundle.putString("PHONE", phoneName);
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
        });

        //fab button for set pic
        fab = (FloatingActionButton) view.findViewById(R.id.st_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog(R.array.profile);
            }
        });

         /*
          page for show all image user
         */
        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                HelperAvatar.avatarDelete(userId, mAvatarId, HelperAvatar.AvatarType.USER, new OnAvatarDelete() {
                    @Override
                    public void latestAvatarPath(final String avatarPath) {
                        setImage(avatarPath);
                    }

                    @Override
                    public void showInitials(final String initials, final String color) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                circleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                                if (G.onChangeUserPhotoListener != null) {
                                    G.onChangeUserPhotoListener.onChangePhoto(null);
                                }
                            }
                        });
                    }
                });
            }
        };

        circleImageView = (net.iGap.module.CircleImageView) view.findViewById(R.id.st_img_circleImage);
        RippleView rippleImageView = (RippleView) view.findViewById(R.id.st_ripple_circleImage);
        rippleImageView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {

                if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).count() > 0) {
                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
                    fragment.appBarLayout = fab;
                    new HelperFragment(fragment).setReplace(false).load();
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
        txtLanguage = (TextView) view.findViewById(R.id.st_txt_language);
        txtLanguage.setText(textLanguage);
        ViewGroup ltLanguage = (ViewGroup) view.findViewById(R.id.st_layout_language);
        ltLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(new FragmentLanguage()).setReplace(false).load();
            }
        });


        ViewGroup ltDataStorage = (ViewGroup) view.findViewById(R.id.st_layout_dataStorage);

        ltDataStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(G.fragmentActivity, ActivityManageSpace.class));
            }
        });

         /*
          setting toggle  crop page
         */

        final TextView txtCrop = (TextView) view.findViewById(R.id.stsp_txt_crop);
        final ToggleButton stsp_toggle_crop = (ToggleButton) view.findViewById(R.id.stsp_toggle_crop);

        int checkedEnableCrop = sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1);
        if (checkedEnableCrop == 1) {
            stsp_toggle_crop.setChecked(true);
        } else {
            stsp_toggle_crop.setChecked(false);
        }

        stsp_toggle_crop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            @Override
            public void onClick(View view) {
                stsp_toggle_crop.setChecked(!stsp_toggle_crop.isChecked());
            }
        });

         /*
          setting toggle vote channel
         */
        final TextView txtVote = (TextView) view.findViewById(R.id.as_txt_show_vote);
        final ToggleButton toggleVote = (ToggleButton) view.findViewById(R.id.as_toggle_show_vote);

        int checkedEnableVote = sharedPreferences.getInt(SHP_SETTING.KEY_VOTE, 1);
        if (checkedEnableVote == 1) {
            toggleVote.setChecked(true);
        } else {
            toggleVote.setChecked(false);
        }

        toggleVote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

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
        });

        txtVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVote.setChecked(!toggleVote.isChecked());
            }
        });


            /*
          setting toggle show sender name in group
         */
        final TextView txtShowSenderNameInGroup = (TextView) view.findViewById(R.id.as_txt_show_sender_name_group);
        final ToggleButton toggleShowSenderInGroup = (ToggleButton) view.findViewById(R.id.as_toggle_show_sender_name_group);

        int checkedEnablShowSenderInGroup = sharedPreferences.getInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0);
        if (checkedEnablShowSenderInGroup == 1) {
            toggleShowSenderInGroup.setChecked(true);
        } else {
            toggleShowSenderInGroup.setChecked(false);
        }

        toggleShowSenderInGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            @Override
            public void onClick(View view) {
                toggleShowSenderInGroup.setChecked(!toggleShowSenderInGroup.isChecked());
            }
        });




         /*
          setting toggle toggle compress
         */

        final TextView txtCompress = (TextView) view.findViewById(R.id.stsp_txt_compress);
        final ToggleButton stsp_toggle_Compress = (ToggleButton) view.findViewById(R.id.stsp_toggle_compress);

        int checkedEnableCompress = sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1);
        if (checkedEnableCompress == 1) {
            stsp_toggle_Compress.setChecked(true);
        } else {
            stsp_toggle_Compress.setChecked(false);
        }

        stsp_toggle_Compress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            @Override
            public void onClick(View view) {

                stsp_toggle_Compress.setChecked(!stsp_toggle_Compress.isChecked());
            }
        });

         /*
          setting toggle toggle trim
         */

        final TextView txtTrim = (TextView) view.findViewById(R.id.stsp_txt_trim);
        final ToggleButton stsp_toggle_Trim = (ToggleButton) view.findViewById(R.id.stsp_toggle_trim);

        int checkedEnableTrim = sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1);
        if (checkedEnableTrim == 1) {
            stsp_toggle_Trim.setChecked(true);
        } else {
            stsp_toggle_Trim.setChecked(false);
        }

        stsp_toggle_Trim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            @Override
            public void onClick(View view) {

                stsp_toggle_Trim.setChecked(!stsp_toggle_Trim.isChecked());
            }
        });


          /*
         open privacy abd security page
         */
        TextView txtPrivacySecurity = (TextView) view.findViewById(R.id.st_txt_privacySecurity);
        txtPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(new FragmentPrivacyAndSecurity()).setReplace(false).load();
            }
        });

         /*
          setting toggle DataShams
         */
        final TextView txtTitleData = (TextView) view.findViewById(R.id.st_txt_st_toggle_dataShams);
        final ViewGroup vgTitleData = (ViewGroup) view.findViewById(R.id.vg_toggle_dataShams);
        final TextView txtData = (TextView) view.findViewById(R.id.st_txt_data);

        dateType = new DateType() {
            @Override
            public void dataName(String type) {
                txtData.setText(type);
            }
        };


        int typeData = sharedPreferences.getInt(SHP_SETTING.KEY_DATA, 0);

        switch (typeData) {
            case 0:
                txtData.setText(G.fragmentActivity.getResources().getString(R.string.miladi));
                break;
            case 1:
                txtData.setText(G.fragmentActivity.getResources().getString(R.string.shamsi));
                break;
            case 2:
                txtData.setText(G.fragmentActivity.getResources().getString(R.string.ghamari));
                break;
        }
        vgTitleData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(new FragmentData()).setReplace(false).load();
            }
        });

        // Auto-Rotate screen

        final TextView txtAutoRotate = (TextView) view.findViewById(R.id.st_txt_st_toggle_auto_rotate);
        final ToggleButton toggleEnableAutoRotate = (ToggleButton) view.findViewById(R.id.st_toggle__auto_rotate);

        boolean checkedEnableAutoRotate = sharedPreferences.getBoolean(SHP_SETTING.KEY_AUTO_ROTATE, true);
        if (checkedEnableAutoRotate) {
            toggleEnableAutoRotate.setChecked(true);
        } else {
            toggleEnableAutoRotate.setChecked(false);
        }

        toggleEnableAutoRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putBoolean(SHP_SETTING.KEY_AUTO_ROTATE, true);
                    editor.apply();
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

                } else {
                    editor.putBoolean(SHP_SETTING.KEY_AUTO_ROTATE, false);
                    editor.apply();
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                }
            }
        });

        txtAutoRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEnableAutoRotate.setChecked(!toggleEnableAutoRotate.isChecked());
            }
        });


        final TextView txtMultiTab = view.findViewById(R.id.st_txt_st_toggle_multi_tab);
        final ToggleButton toggleEnableMultiTab = view.findViewById(R.id.st_toggle_multi_tab);

        boolean checkedEnableMultiTab = sharedPreferences.getBoolean(SHP_SETTING.KEY_MULTI_TAB, false);
        if (checkedEnableMultiTab) {
            toggleEnableMultiTab.setChecked(true);
        } else {
            toggleEnableMultiTab.setChecked(false);
        }

        toggleEnableMultiTab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
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
                removeFromBaseFragment(FragmentSetting.this);
            }
        });

        txtMultiTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEnableMultiTab.setChecked(!toggleEnableMultiTab.isChecked());
            }
        });

         /*
          setting text size for chat room
         */
        poRbDialogTextSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14) - 11;
        txtMessageTextSize = (TextView) view.findViewById(R.id.st_txt_messageTextSize_number);
        txtMessageTextSize.setText("" + sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14));

        if (HelperCalander.isPersianUnicode) {
            txtMessageTextSize.setText(HelperCalander.convertToUnicodeFarsiNumber(txtMessageTextSize.getText().toString()));
        }

        ViewGroup ltMessageTextSize = (ViewGroup) view.findViewById(R.id.st_layout_messageTextSize);
        ltMessageTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_title_message_textSize))
                        .titleGravity(GravityEnum.START)
                        .titleColor(G.context.getResources().getColor(android.R.color.black))
                        .items(HelperCalander.isPersianUnicode ? R.array.message_text_size_persian : R.array.message_text_size)
                        .itemsCallbackSingleChoice(poRbDialogTextSize, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                if (text != null) {
                                    txtMessageTextSize.setText(text.toString().replace("(Hello)", "").trim());

                                    if (HelperCalander.isPersianUnicode) {
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
                        .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                        .show();
            }
        });

        /**
         * open page chat background
         */
        TextView txtChatBackground = (TextView) view.findViewById(R.id.st_txt_chatBackground);
        txtChatBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(FragmentChatBackground.newInstance()).setReplace(false).load();
            }
        });

        //***********************
        imgAppBarSelected = (ImageView) view.findViewById(R.id.asn_img_title_bar_color);
        GradientDrawable bgShape = (GradientDrawable) imgAppBarSelected.getBackground();
        bgShape.setColor(Color.parseColor(G.appBarColor));

        TextView txtSelectAppColor = (TextView) view.findViewById(R.id.asn_txt_app_title_bar_color);
        txtSelectAppColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAppColorDialog(R.string.app_theme);
            }
        });

        imgAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSelectAppColorDialog(R.string.app_theme);
            }
        });

        //***********************

        imgNotificationColor = (ImageView) view.findViewById(R.id.asn_img_notification_color);
        GradientDrawable bgShapeNotification = (GradientDrawable) imgNotificationColor.getBackground();
        bgShapeNotification.setColor(Color.parseColor(G.notificationColor));

        imgNotificationColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectAppColorDialog(R.string.app_notif_color);
            }
        });

        TextView txtNotificatinColor = (TextView) view.findViewById(R.id.asn_txt_app_notification_color);
        txtNotificatinColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAppColorDialog(R.string.app_notif_color);
            }
        });

        //***********************

        imgToggleBottomColor = (ImageView) view.findViewById(R.id.asn_img_toggle_botton_color);
        GradientDrawable bgShapeToggleBottomColor = (GradientDrawable) imgToggleBottomColor.getBackground();
        bgShapeToggleBottomColor.setColor(Color.parseColor(G.toggleButtonColor));

        imgToggleBottomColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectAppColorDialog(R.string.toggle_botton_color);
            }
        });

        TextView txtToggleBottomColor = (TextView) view.findViewById(R.id.asn_txt_app_toggle_botton_color);
        txtToggleBottomColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAppColorDialog(R.string.toggle_botton_color);
            }
        });

        //***********************

        imgSendAndAttachColor = (ImageView) view.findViewById(R.id.asn_img_send_and_attach_color);
        GradientDrawable bgShapeSendAndAttachColor = (GradientDrawable) imgSendAndAttachColor.getBackground();
        bgShapeSendAndAttachColor.setColor(Color.parseColor(G.attachmentColor));

        imgSendAndAttachColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectAppColorDialog(R.string.send_and_attach_botton_color);
            }
        });

        TextView txtSendAndAttachColor = (TextView) view.findViewById(R.id.asn_txt_send_and_attach_color);
        txtSendAndAttachColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAppColorDialog(R.string.send_and_attach_botton_color);
            }
        });

        //***********************

        imgHeaderTextColor = (ImageView) view.findViewById(R.id.asn_img_default_header_font_color);
        GradientDrawable bgShapeHeaderTextColor = (GradientDrawable) imgHeaderTextColor.getBackground();
        bgShapeHeaderTextColor.setColor(Color.parseColor(G.headerTextColor));

        imgHeaderTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectAppColorDialog(R.string.default_header_font_color);
            }
        });

        TextView txtHeaderTextColor = (TextView) view.findViewById(R.id.asn_txt_default_header_font_color);
        txtHeaderTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAppColorDialog(R.string.default_header_font_color);
            }
        });

        TextView txtSetToDefaultColor = (TextView) view.findViewById(R.id.asn_txt_set_color_to_default);
        txtSetToDefaultColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetDefaultColorDialog();
            }
        });

        //***********************

        imgHeaderProgressColor = (ImageView) view.findViewById(R.id.asn_img_default_progress_color);
        GradientDrawable bgShapeProgressColor = (GradientDrawable) imgHeaderProgressColor.getBackground();
        bgShapeProgressColor.setColor(Color.parseColor(G.progressColor));

        imgHeaderProgressColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectAppColorDialog(R.string.default_progress_color);
            }
        });

        TextView txtProgressColor = (TextView) view.findViewById(R.id.asn_txt_default_progress_color);
        txtProgressColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAppColorDialog(R.string.default_progress_color);
            }
        });


        //***********************
         /*
          open browser
         */
        TextView ltInAppBrowser = (TextView) view.findViewById(R.id.st_txt_inAppBrowser);
        toggleInAppBrowser = (ToggleButton) view.findViewById(R.id.st_toggle_inAppBrowser);
        int checkedInappBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
        if (checkedInappBrowser == 1) {
            toggleInAppBrowser.setChecked(true);
        } else {
            toggleInAppBrowser.setChecked(false);
        }

        ltInAppBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        TextView txtNotifyAndSound = (TextView) view.findViewById(R.id.st_txt_notifyAndSound);
        txtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(new FragmentNotificationAndSound()).setReplace(false).load();

            }
        });

        TextView ltSentByEnter = (TextView) view.findViewById(R.id.st_txt_sendEnter);
        toggleSentByEnter = (ToggleButton) view.findViewById(R.id.st_toggle_sendEnter);
        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        if (checkedSendByEnter == 1) {
            toggleSentByEnter.setChecked(true);
        } else {
            toggleSentByEnter.setChecked(false);
        }

        toggleSentByEnter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            @Override
            public void onClick(View view) {

                toggleSentByEnter.setChecked(!toggleSentByEnter.isChecked());
            }
        });


        TextView txtAutoDownloadData = (TextView) view.findViewById(R.id.st_txt_autoDownloadData);
        txtAutoDownloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        });

        TextView txtAutoDownloadWifi = (TextView) view.findViewById(R.id.st_txt_autoDownloadWifi);
        txtAutoDownloadWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        });

        TextView txtAutoDownloadRoaming = (TextView) view.findViewById(R.id.st_txt_autoDownloadRoaming);
        txtAutoDownloadRoaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        });

        TextView ltEnableAnimation = (TextView) view.findViewById(R.id.st_txt_enableAnimation);
        toggleEnableAnimation = (ToggleButton) view.findViewById(R.id.st_toggle_enableAnimation);
        int checkedEnableAnimation = sharedPreferences.getInt(SHP_SETTING.KEY_ENABLE_ANIMATION, 0);
        if (checkedEnableAnimation == 1) {
            toggleEnableAnimation.setChecked(true);
        } else {
            toggleEnableAnimation.setChecked(false);
        }

        ltEnableAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        TextView ltAutoGifs = (TextView) view.findViewById(R.id.st_txt_autoGif);
        toggleAutoGifs = (ToggleButton) view.findViewById(R.id.st_toggle_autoGif);
        int checkedAutoGif = sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS);
        if (checkedAutoGif == 1) {
            toggleAutoGifs.setChecked(true);
        } else {
            toggleAutoGifs.setChecked(false);
        }

        toggleAutoGifs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            @Override
            public void onClick(View view) {
                toggleAutoGifs.setChecked(!toggleAutoGifs.isChecked());
            }
        });

        TextView ltSaveToGallery = (TextView) view.findViewById(R.id.st_txt_saveGallery);
        toggleSaveToGallery = (ToggleButton) view.findViewById(R.id.st_toggle_saveGallery);
        int checkedSaveToGallery = sharedPreferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
        if (checkedSaveToGallery == 1) {
            toggleSaveToGallery.setChecked(true);
        } else {
            toggleSaveToGallery.setChecked(false);
        }

        toggleSaveToGallery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            @Override
            public void onClick(View view) {
                toggleSaveToGallery.setChecked(!toggleSaveToGallery.isChecked());
            }
        });

        TextView txtWebViewHome = (TextView) view.findViewById(R.id.st_txt_iGap_home);
        final String link;
        if (HelperCalander.isPersianUnicode) {
            link = "https://www.igap.net/fa";
        } else {
            link = "https://www.igap.net/";
        }
        txtWebViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperUrl.openBrowser(link);
            }
        });

        final String blogLink;
        if (HelperCalander.isPersianUnicode) {
            blogLink = "https://blog.igap.net/fa";
        } else {
            blogLink = "https://blog.igap.net";
        }

        TextView txtWebViewBlog = (TextView) view.findViewById(R.id.st_txt_privacy_blog);
        txtWebViewBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperUrl.openBrowser(blogLink);
            }
        });


        final String supportLink;
        if (HelperCalander.isPersianUnicode) {
            supportLink = "https://support.igap.net/fa";
        } else {
            supportLink = "https://support.igap.net";
        }
        TextView txtCreateTicket = (TextView) view.findViewById(R.id.st_txt_create_ticket);
        txtCreateTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperUrl.openBrowser(supportLink);
            }
        });

        TextView txtVersionApp = (TextView) view.findViewById(R.id.st_txt_versionApp);

        txtVersionApp.setText(G.fragmentActivity.getResources().getString(R.string.iGap_version) + " " + getAppVersion());

        if (HelperCalander.isPersianUnicode) {
            txtVersionApp.setText(HelperCalander.convertToUnicodeFarsiNumber(txtVersionApp.getText().toString()));
        }

        setAvatar();


    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWait != null) {
                    prgWait.setVisibility(View.GONE);
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

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {

        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            setAvatar();
        } else {
            HelperAvatar.avatarAdd(userId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                            setImage(avatarPath);
                        }
                    });
                }
            });
            pathSaveImage = null;
        }
    }

    @Override
    public void onAvatarAddTimeOut() {
        hideProgressBar();
    }

    @Override
    public void onAvatarError() {
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
                txtNickName.setText(nickName);
                txtNickNameTitle.setText(nickName);
            }
            if (bio != null) {
                txtBio.setText(bio);
            }

            if (userName != null) txtUserName.setText(userName);

            if (phoneName != null) txtPhoneNumber.setText(phoneName);

            if (HelperCalander.isPersianUnicode) {
                txtPhoneNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtPhoneNumber.getText().toString()));
            }

            if (userGender != null) {
                if (userGender == ProtoGlobal.Gender.MALE) {
                    txtGander.setText(G.fragmentActivity.getResources().getString(R.string.Male));
                } else if (userGender == ProtoGlobal.Gender.FEMALE) {
                    txtGander.setText(G.fragmentActivity.getResources().getString(R.string.Female));
                }
            } else {
                txtGander.setText(G.fragmentActivity.getResources().getString(R.string.set_gender));
            }

            if (userEmail != null && userEmail.length() > 0) {
                txtEmail.setText(userEmail);
            } else {
                txtEmail.setText(G.fragmentActivity.getResources().getString(R.string.set_email));
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
                //  toggleBottomClick(Color.parseColor(Config.default_toggleButtonColor));
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

        GradientDrawable bgShape = (GradientDrawable) imgAppBarSelected.getBackground();
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
        GradientDrawable bgShape = (GradientDrawable) imgNotificationColor.getBackground();
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
        GradientDrawable bgShape = (GradientDrawable) imgHeaderProgressColor.getBackground();
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

        if (updateUi) {
            G.isRestartActivity = true;
            G.fragmentActivity.recreate();
        }
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
    private void setAvatar() {
        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), circleImageView);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        circleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });
    }

    //dialog for choose pic from gallery or camera
    private void startDialog(int r) {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.choose_picture)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(G.fragmentActivity.getResources().getString(R.string.array_From_Camera))) { // camera
                    try {
                        HelperPermision.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                dialog.dismiss();
                                useCamera();
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        HelperPermision.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentSetting.this);
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
                startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);

            } else {
                Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.please_check_your_camera), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setImage(String path) {
        if (path != null) {
            G.imageLoader.displayImage(AndroidUtils.suitablePath(path), circleImageView);
            if (G.onChangeUserPhotoListener != null) {
                G.onChangeUserPhotoListener.onChangePhoto(path);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        G.onUserAvatarResponse = this;

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

    @Override
    public void onPause() {
        super.onPause();

        if (realmUserInfo != null && realmUserInfo.isValid()) {
            realmUserInfo.removeAllChangeListeners();
        }

        if (mRealmRegisteredInfo != null && mRealmRegisteredInfo.isValid()) {
            mRealmRegisteredInfo.removeAllChangeListeners();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                intent.putExtra("IMAGE_CAMERA", AttachFile.mCurrentPhotoPath);
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", "setting");
                intent.putExtra("ID", (int) (idAvatar + 1L));
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            } else {
                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                if (uriIntent != null) {
                    ImageHelper.correctRotateImage(pathSaveImage, true);
                    intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
                    intent.putExtra("TYPE", "camera");
                    intent.putExtra("PAGE", "setting");
                    intent.putExtra("ID", (int) (idAvatar + 1L));
                    startActivityForResult(intent, IntentRequests.REQ_CROP);
                }
            }
        } else if (requestCode == request_code_image_from_gallery_single_select && resultCode == RESULT_OK) {// result for gallery
            if (data != null) {
                if (data.getData() == null) {
                    return;
                }
                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image));
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
                @Override
                public void OnProgress(int progress, FileUploadStructure struct) {
                    if (progress < 100) {
                        prgWait.setProgress(progress);
                    } else {
                        new RequestUserAvatarAdd().userAddAvatar(struct.token);
                    }
                }

                @Override
                public void OnError() {
                    hideProgressBar();
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        updateRoomListIfNeeded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Override onSaveInstanceState method and comment 'super' from avoid from "Can not perform this action after onSaveInstanceState" error
        //super.onSaveInstanceState(outState);
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
                if (prgWait != null) {
                    prgWait.setVisibility(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    public interface DateType {

        void dataName(String type);
    }
}
