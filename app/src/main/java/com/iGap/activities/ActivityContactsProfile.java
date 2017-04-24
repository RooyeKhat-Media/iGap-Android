/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.fragments.FragmentNotification;
import com.iGap.fragments.FragmentShowAvatars;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnChatDelete;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.interfaces.OnGetPermission;
import com.iGap.interfaces.OnUserContactDelete;
import com.iGap.interfaces.OnUserContactEdit;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.interfaces.OnUserUpdateStatus;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.LastSeenTimeUtil;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SUID;
import com.iGap.module.structs.StructListOfContact;
import com.iGap.module.structs.StructMessageAttachment;
import com.iGap.module.structs.StructMessageInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmOfflineDeleteFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.request.RequestChatDelete;
import com.iGap.request.RequestChatGetRoom;
import com.iGap.request.RequestUserContactImport;
import com.iGap.request.RequestUserContactsBlock;
import com.iGap.request.RequestUserContactsDelete;
import com.iGap.request.RequestUserContactsEdit;
import com.iGap.request.RequestUserContactsUnblock;
import com.iGap.request.RequestUserInfo;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.G.context;

public class ActivityContactsProfile extends ActivityEnhanced implements OnUserUpdateStatus {
    private long userId = 0;
    private long roomId;
    private String phone = "0";
    private String displayName = "";
    private String username = "";
    private String firstName;
    private String lastName;
    private long lastSeen;
    private String mPhone = "";
    private String initials;
    private String color;
    private String enterFrom;
    private String userStatus;
    private boolean isBlockUser = false;
    private Realm mRealm;
    RealmRegisteredInfo rrg;
    private long sheardId = -2;


    TextView txtCountOfShearedMedia;

    private boolean showNumber = true;

    private AppBarLayout appBarLayout;

    private TextView txtLastSeen, txtUserName, titleToolbar, titleLastSeen, txtBlockContact, txtClearChat, txtPhoneNumber, txtNotifyAndSound, txtNickname;
    private ViewGroup vgPhoneNumber, vgSharedMedia, layoutNickname;
    private com.iGap.module.CircleImageView imgUser;
    private MaterialDesignTextView imgMenu, txtBack;

    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    private PopupWindow popupWindowPhoneNumber;
    private int screenWidth;

    private String avatarPath;
    private RealmList<RealmAvatar> avatarList;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (G.onUpdateUserStatusInChangePage != null) {
            G.onUpdateUserStatusInChangePage.updateStatus(userId, userStatus, lastSeen);
        }
    }

    private RealmChangeListener<RealmModel> changeListener;
    private RealmRoom mRoom;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) mRealm.close();
    }

    @Override
    protected void onResume() {

        super.onResume();

        mRoom = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, sheardId).findFirst();
        if (mRoom != null) {

            if (changeListener == null) {

                changeListener = new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(final RealmModel element) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String countText = ((RealmRoom) element).getSharedMediaCount();

                                if (countText == null || countText.length() == 0) {
                                    txtCountOfShearedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
                                } else {
                                    if (HelperCalander.isLanguagePersian) {
                                        txtCountOfShearedMedia.setText(HelperCalander.convertToUnicodeFarsiNumber(countText));
                                    } else {
                                        txtCountOfShearedMedia.setText(countText);
                                    }
                                }
                            }
                        });
                    }
                };
            }

            mRoom.addChangeListener(changeListener);
            changeListener.onChange(mRoom);
        } else {
            txtCountOfShearedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (rrg != null) {
            rrg.removeChangeListeners();
        }

        if (mRealm != null) {
            mRealm.close();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_profile);
        final Realm realm = Realm.getDefaultInstance();

        mRealm = Realm.getDefaultInstance();


        G.onUserUpdateStatus = this;


        Bundle extras = getIntent().getExtras();
        userId = extras.getLong("peerId");
        roomId = extras.getLong("RoomId");
        enterFrom = extras.getString("enterFrom");

        if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();
            if (realmRoom != null) {
                sheardId = realmRoom.getId();
            }
        } else {
            sheardId = roomId;
        }

        rrg = mRealm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();

        if (rrg != null) {

            isBlockUser = rrg.isBlockUser();

            rrg.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel element) {
                    isBlockUser = rrg.isBlockUser();
                }
            });
        }


        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();

        if (realmRegisteredInfo != null) {
            if (realmRegisteredInfo.getLastAvatar() != null) {

                String mainFilePath = realmRegisteredInfo.getLastAvatar().getFile().getLocalFilePath();

                if (mainFilePath != null && new File(mainFilePath).exists()) { // if main image is exist showing that
                    avatarPath = mainFilePath;
                } else {
                    avatarPath = realmRegisteredInfo.getLastAvatar().getFile().getLocalThumbnailPath();
                }

                avatarList = realmRegisteredInfo.getAvatars();
            }
        }

        RealmContacts realmUser = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();

        if (realmRegisteredInfo != null) {
            phone = realmRegisteredInfo.getPhoneNumber();
            displayName = realmRegisteredInfo.getDisplayName();
            firstName = realmRegisteredInfo.getFirstName();
            lastName = realmRegisteredInfo.getLastName();
            username = realmRegisteredInfo.getUsername();
            lastSeen = realmRegisteredInfo.getLastSeen();
            color = realmRegisteredInfo.getColor();
            initials = realmRegisteredInfo.getInitials();
            userStatus = realmRegisteredInfo.getStatus();
        } else if (realmUser != null) {
            phone = Long.toString(realmUser.getPhone());
            displayName = realmUser.getDisplay_name();
            firstName = realmUser.getFirst_name();
            lastName = realmUser.getLast_name();
            username = realmUser.getUsername();
            lastSeen = realmUser.getLast_seen();
            color = realmUser.getColor();
            initials = realmUser.getInitials();
        }

        RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.PHONE, Long.parseLong(phone)).findFirst();

        // agar ba click roye karbar dar safheye goruh vared in ghesmat shodim va karbar dar list contact haye ma vojud nadasht shomareye karbar
        // namyesh dade nemishavad
        if (realmContacts == null && enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {
            showNumber = false;
        }

        imgUser = (com.iGap.module.CircleImageView) findViewById(R.id.chi_img_circleImage);

        //Set ContactAvatar
       /* if (avatarPath != null) {
            File imgFile = new File(avatarPath);
            if (imgFile.exists()) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                imgUser.setImageBitmap(myBitmap);

                G.imageLoader.displayImage(AndroidUtils.suitablePath(imgFile.getAbsolutePath()), imgUser);

            } else {
                imgUser.setImageBitmap(
                        com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgUser.getContext().getResources().getDimension(R.dimen.dp100),
                                initials, color));
            }
        } else {
            imgUser.setImageBitmap(
                    com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgUser.getContext().getResources().getDimension(R.dimen.dp100),
                            initials, color));
        }*/

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getDefaultInstance();
                if (realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst() != null) {
                    FragmentShowAvatars.appBarLayout = fab;
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chi_layoutParent, FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.chat)).commit();
                }
                realm.close();
            }
        });

        txtBack = (MaterialDesignTextView) findViewById(R.id.chi_txt_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.chi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.chi_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() { //fab button
            @Override
            public void onClick(View view) {

                if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {

                    final Realm realm = Realm.getDefaultInstance();
                    final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();

                    if (realmRoom != null) {
                        // ActivityChat.activityChatForFinish.finish();

                        Intent intent = new Intent(context, ActivityChat.class);
                        intent.putExtra("RoomId", realmRoom.getId());
                        //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        G.onChatGetRoom = new OnChatGetRoom() {
                            @Override
                            public void onChatGetRoom(final long roomId) {
                                G.currentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //  ActivityChat.activityChatForFinish.finish();

                                        Realm realm = Realm.getDefaultInstance();
                                        Intent intent = new Intent(context, ActivityChat.class);
                                        intent.putExtra("peerId", userId);
                                        intent.putExtra("RoomId", roomId);
                                        //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        realm.close();
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onChatGetRoomCompletely(ProtoGlobal.Room room) {

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
                    finish();
                }
            }
        });

        txtNickname = (TextView) findViewById(R.id.chi_txt_nikName);//set nickname
        if (displayName != null && !displayName.equals("")) {
            txtNickname.setText(displayName);
        } else {
            txtNickname.setText(R.string.nick_name_not_exist);
        }

        layoutNickname = (ViewGroup) findViewById(R.id.chi_layout_nickname);
        layoutNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                                                  final LinearLayout layoutNickname = new LinearLayout(ActivityContactsProfile.this);
                                                  layoutNickname.setOrientation(LinearLayout.VERTICAL);

                                                  String splitNickname[] = txtNickname.getText().toString().split(" ");
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
                                                  final View viewFirstName = new View(ActivityContactsProfile.this);
                                                  viewFirstName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));

                LinearLayout.LayoutParams viewParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

                                                  TextInputLayout inputFirstName = new TextInputLayout(ActivityContactsProfile.this);
                                                  final EditText edtFirstName = new EditText(ActivityContactsProfile.this);
                edtFirstName.setHint(R.string.first_name);
                                                  edtFirstName.setText(firsName);
                                                  edtFirstName.setTextColor(getResources().getColor(R.color.text_edit_text));
                                                  edtFirstName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                                                  edtFirstName.setPadding(0, 8, 0, 8);
                                                  edtFirstName.setSingleLine(true);
                                                  inputFirstName.addView(edtFirstName);
                                                  inputFirstName.addView(viewFirstName, viewParams);
                                                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                      edtFirstName.setBackground(getResources().getDrawable(android.R.color.transparent));
                                                  }

                                                  final View viewLastName = new View(ActivityContactsProfile.this);
                                                  viewLastName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));

                                                  TextInputLayout inputLastName = new TextInputLayout(ActivityContactsProfile.this);
                                                  final EditText edtLastName = new EditText(ActivityContactsProfile.this);
                edtLastName.setHint(R.string.last_name);
                                                  edtLastName.setText(lastName);
                                                  edtLastName.setTextColor(getResources().getColor(R.color.text_edit_text));
                                                  edtLastName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
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

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivityContactsProfile.this).title(getResources().getString(R.string.pu_nikname_profileUser)).positiveText(getResources().getString(R.string.B_ok)).customView(layoutNickname, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

                                                  final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                                                  positive.setEnabled(false);

                                                  edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                      @Override
                                                      public void onFocusChange(View view, boolean b) {
                                                          if (b) {
                                                              viewFirstName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                                                          } else {
                                                              viewFirstName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                                                          }
                                                      }
                                                  });

                                                  edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                      @Override
                                                      public void onFocusChange(View view, boolean b) {
                                                          if (b) {
                                                              viewLastName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                                                          } else {
                                                              viewLastName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
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

                                                          long po = Long.parseLong(mPhone);
                                                                                      String firstName = edtFirstName.getText().toString();
                                                                                      String lastName = edtLastName.getText().toString();
                                                          new RequestUserContactsEdit().contactsEdit(po, firstName, lastName);
                                                                                      dialog.dismiss();
                                                                                  }
                                                                              }

                                                  );

                                                  dialog.show();
                                                  G.onUserContactEdit = new OnUserContactEdit() {
                                                      @Override
                                                      public void onContactEdit(final String firstName, final String lastName) {
                                                          Realm realm1 = Realm.getDefaultInstance();
                                                          final RealmContacts realmUser = realm1.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();
                                                          realm1.executeTransaction(new Realm.Transaction() {
                                                              @Override
                                                              public void execute(Realm realm) {
                                                                  realmUser.setFirst_name(firstName);
                                                                  realmUser.setLast_name(lastName);
                                                              }
                                                          });
                                                          realm1.close();
                                                          runOnUiThread(new Runnable() {
                                                              @Override
                                                              public void run() {
                                                                  txtNickname.setText(firstName + " " + lastName);
                                                              }
                                                          });
                                                      }

                                                      @Override
                                                      public void onContactEditTimeOut() {

                                                      }

                                                      @Override
                                                      public void onContactEditError(int majorCode, int minorCode) {

                                                      }
                                                  };
                                              }
                                          }

        );
        //        layoutNickname.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {

        txtLastSeen = (TextView) findViewById(R.id.chi_txt_lastSeen_title);
        titleToolbar = (TextView) findViewById(R.id.chi_txt_titleToolbar_DisplayName);
        titleLastSeen = (TextView) findViewById(R.id.chi_txt_titleToolbar_LastSeen);
        txtUserName = (TextView) findViewById(R.id.chi_txt_userName);
        txtPhoneNumber = (TextView) findViewById(R.id.chi_txt_phoneNumber);
        vgPhoneNumber = (ViewGroup) findViewById(R.id.chi_layout_phoneNumber);
        if (!showNumber) {
            vgPhoneNumber.setVisibility(View.GONE);
        }

        txtCountOfShearedMedia = (TextView) findViewById(R.id.chi_txt_count_of_sharedMedia);


        txtUserName.setText(username);
        mPhone = "" + phone;

        txtPhoneNumber.setText(mPhone);

        if (HelperCalander.isLanguagePersian) {
            txtPhoneNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtPhoneNumber.getText().toString()));
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.acp_collapsing_toolbar_layout);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));


        titleToolbar.setText(displayName);

        appBarLayout = (AppBarLayout) findViewById(R.id.chi_appbar);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()

        {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                ViewGroup viewGroup = (ViewGroup) findViewById(R.id.chi_root_circleImage);
                if (verticalOffset < -5) {
                    viewGroup.animate().alpha(0).setDuration(700);
                    viewGroup.setVisibility(View.GONE);
                    titleToolbar.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(1).setDuration(300);
                    titleLastSeen.setVisibility(View.VISIBLE);
                    titleLastSeen.animate().alpha(1).setDuration(300);
                } else {
                    viewGroup.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(1).setDuration(700);
                    titleToolbar.setVisibility(View.GONE);
                    titleToolbar.animate().alpha(0).setDuration(500);
                    titleLastSeen.setVisibility(View.GONE);
                    titleLastSeen.animate().alpha(0).setDuration(500);
                }
            }
        });

        screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        imgMenu = (MaterialDesignTextView) findViewById(R.id.chi_img_menuPopup);

        RippleView rippleMenu = (RippleView) findViewById(R.id.chi_ripple_menuPopup);

        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                showPopUp();
            }
        });
        vgPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    HelperPermision.getContactPermision(ActivityContactsProfile.this, new OnGetPermission() {
                        @Override
                        public void Allow() throws IOException {
                            showPopupPhoneNumber(vgPhoneNumber, mPhone);
                        }

                        @Override
                        public void deny() {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //popUpMenu(R.menu.chi_popup_phone_number, v);
            }
        });

        vgSharedMedia = (ViewGroup) findViewById(R.id.chi_layout_SharedMedia);

        vgSharedMedia.setOnClickListener(new View.OnClickListener() {// go to the ActivityMediaChanel
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivityContactsProfile.this, ActivityShearedMedia.class);
                intent.putExtra("RoomID", sheardId);
                startActivity(intent);
            }
        });

        txtBlockContact = (TextView) findViewById(R.id.chi_txt_blockContact);

        txtBlockContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(getString(R.string.block_this_contact), getString(R.string.block), getString(R.string.cancel));
            }
        });

        txtClearChat = (TextView) findViewById(R.id.chi_txt_clearChat);

        txtClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(getString(R.string.clear_this_chat), getString(R.string.clear), getString(R.string.cancel));
            }
        });

        txtNotifyAndSound = (TextView) findViewById(R.id.chi_txtNotifyAndSound);

        txtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "CONTACT");
                bundle.putLong("ID", roomId);
                fragmentNotification.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.chi_layoutParent, fragmentNotification).commit();
            }
        });

        realm.close();
        //getUserInfo(); // client should send request for get user info because need to update user online timing
        setUserStatus(userStatus, lastSeen);

        setAvatar();

        ActivityShearedMedia.getCountOfSharedMedia(sheardId);
    }

    private void setAvatar() {
        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imgUser);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgUser.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgUser.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });
    }

    private void setUserStatus(String userStatus, long time) {
        this.userStatus = userStatus;
        this.lastSeen = time;

        if (userStatus != null) {
            if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                String status = LastSeenTimeUtil.computeTime(userId, time, false);
                titleLastSeen.setText(status);
                txtLastSeen.setText(status);
            } else {
                titleLastSeen.setText(userStatus);
                txtLastSeen.setText(userStatus);
            }

            if (HelperCalander.isLanguagePersian) {
                txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
            }
        }
    }

    private void showPopupPhoneNumber(View v, String number) {

        boolean isExist = false;
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = {
                ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME
        };
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur != null) {
                isExist = cur.moveToFirst();
            }
        } finally {
            if (cur != null) cur.close();
        }

        if (isExist) {
            new MaterialDialog.Builder(this).title(R.string.phone_number).items(R.array.phone_number2).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    switch (which) {
                        case 0:
                            String call = "+" + Long.parseLong(mPhone);
                            try {
                                //                                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                //                                        phoneIntent.setData(Uri.parse("tel:" + call));

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
                            copy = mPhone;
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", copy);
                            clipboard.setPrimaryClip(clip);
                            break;
                    }
                }
            }).show();
        } else {
            new MaterialDialog.Builder(this).title(R.string.phone_number).items(R.array.phone_number).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    switch (which) {
                        case 0:

                            String name = txtNickname.getText().toString();
                            String phone = "+" + mPhone;

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
                                Toast.makeText(G.context, getString(R.string.exception) + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case 1:

                            String call = "+" + Long.parseLong(mPhone);
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

                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", mPhone);
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
        ArrayList<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = firstName;
        contact.lastName = lastName;
        contact.phone = phone + "";

        contacts.add(contact);

        new RequestUserContactImport().contactImportAndGetResponse(contacts, true);

    }

    private void showPopUp() {
        LinearLayout layoutDialog = new LinearLayout(ActivityContactsProfile.this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setBackgroundColor(getResources().getColor(android.R.color.white));
        TextView text1 = new TextView(ActivityContactsProfile.this);
        TextView text2 = new TextView(ActivityContactsProfile.this);
        TextView text3 = new TextView(ActivityContactsProfile.this);

        text1.setTextColor(getResources().getColor(android.R.color.black));
        text2.setTextColor(getResources().getColor(android.R.color.black));
        text3.setTextColor(getResources().getColor(android.R.color.black));
        if (isBlockUser) {
            text1.setText(getString(R.string.un_block_user));
        } else {
            text1.setText(getString(R.string.block_user));
        }
        text2.setText(getResources().getString(R.string.clear_history));
        text3.setText(getResources().getString(R.string.delete_contact));

        int dim20 = (int) getResources().getDimension(R.dimen.dp20);
        int dim12 = (int) getResources().getDimension(R.dimen.dp12);
        int sp14_Popup = 14;

        /**
         * change dpi tp px
         */
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int widthDpi = Math.round(width / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        if (widthDpi >= 720) {
            sp14_Popup = 30;
        } else if (widthDpi >= 600) {
            sp14_Popup = 22;
        } else {
            sp14_Popup = 15;
        }

        text1.setTextSize(sp14_Popup);
        text2.setTextSize(sp14_Popup);
        text3.setTextSize(sp14_Popup);

        text1.setPadding(dim20, dim12, dim12, 0);
        text2.setPadding(dim20, dim12, dim20, dim12);
        text3.setPadding(dim20, 0, dim20, dim12);

        layoutDialog.addView(text1, params);
        layoutDialog.addView(text2, params);
        layoutDialog.addView(text3, params);

        popupWindow = new PopupWindow(layoutDialog, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.shadow3, ActivityContactsProfile.this.getTheme()));
        } else {
            popupWindow.setBackgroundDrawable((getResources().getDrawable(R.mipmap.shadow3)));
        }
        if (popupWindow.isOutsideTouchable()) {
            popupWindow.dismiss();
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popupWindow.showAtLocation(layoutDialog, Gravity.RIGHT | Gravity.TOP, (int) getResources().getDimension(R.dimen.dp16), (int) getResources().getDimension(R.dimen.dp32));
        //                popupWindow.showAsDropDown(v);

        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockOrUnblockUser();
                popupWindow.dismiss();
            }
        });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivityContactsProfile.this).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).negativeText(R.string.B_cancel).show();

                popupWindow.dismiss();
            }
        });
        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivityContactsProfile.this).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        deleteContact();
                    }
                }).negativeText(R.string.B_cancel).show();

                popupWindow.dismiss();
            }
        });
    }


    private void blockOrUnblockUser() {

        if (isBlockUser) {

            new RequestUserContactsUnblock().userContactsUnblock(userId);
        } else {

            new RequestUserContactsBlock().userContactsBlock(userId);
        }
    }


    private void showAlertDialog(String message, String positive, String negitive) { // alert dialog for block or clear user

        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityContactsProfile.this);

        builder.setMessage(message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clearHistory();
                dialogInterface.dismiss();
            }
        });

        builder.setMessage(message);
        builder.setNegativeButton(negitive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button nButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(getResources().getColor(R.color.toolbar_background));
        nButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        Button pButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pButton.setTextColor(getResources().getColor(R.color.toolbar_background));
        pButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
    }

    public ArrayList<StructMessageInfo> setItem() {
        ArrayList<StructMessageInfo> items = new ArrayList<>();

        ArrayList<String> currentTokenAdded = new ArrayList<>();

        for (int i = 0; i < avatarList.size(); i++) {
            if (avatarList.get(i).getFile() != null) {
                StructMessageInfo item = new StructMessageInfo();
                RealmAvatar avatar = avatarList.get(i);
                if (!currentTokenAdded.contains(avatar.getFile().getToken())) {
                    currentTokenAdded.add(avatar.getFile().getToken());
                    item.attachment = new StructMessageAttachment(avatarList.get(i).getFile());
                    items.add(item);
                }
            }
        }
        return items;
    }

    private void clearHistory() {

        // make request for clearing messages
        final Realm realm = Realm.getDefaultInstance();

        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override
            public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                        if (realmRoom != null && realmRoom.getLastMessage() != null) {
                            element.setClearId(realmRoom.getLastMessage().getMessageId());
                            G.clearMessagesUtil.clearMessages(realmRoom.getType(), roomId, realmRoom.getLastMessage().getMessageId());
                        }

                        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll();
                        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                            if (realmRoomMessage != null) {
                                // delete chat history message
                                realmRoomMessage.deleteFromRealm();
                            }
                        }

                        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            room.setLastMessage(null);
                        }
                        // finally delete whole chat history
                        realmRoomMessages.deleteAllFromRealm();
                    }
                });

                element.removeChangeListeners();
                realm.close();
            }
        });

        if (G.onClearChatHistory != null) {
            G.onClearChatHistory.onClearChatHistory();
        }
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
        new RequestUserContactsDelete().contactsDelete(phone);
    }

    private void getUserInfo() {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtNickname.setText(user.getDisplayName());
                    }
                });
            }

            @Override
            public void onUserInfoTimeOut() {

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };

        new RequestUserInfo().userInfo(userId);
    }

    private void deleteChat() {
        G.onChatDelete = new OnChatDelete() {
            @Override
            public void onChatDelete(long roomId) {
            }

            @Override
            public void onChatDeleteError(int majorCode, int minorCode) {

            }
        };
        final Realm realm = Realm.getDefaultInstance();
        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override
            public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(final Realm realm) {
                        if (realm.where(RealmOfflineDelete.class).equalTo(RealmOfflineDeleteFields.OFFLINE_DELETE, roomId).findFirst() == null) {
                            RealmOfflineDelete realmOfflineDelete = realm.createObject(RealmOfflineDelete.class, SUID.id().get());
                            realmOfflineDelete.setOfflineDelete(userId);

                            element.getOfflineDeleted().add(realmOfflineDelete);

                            realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst().deleteFromRealm();
                            realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll().deleteAllFromRealm();

                            new RequestChatDelete().chatDelete(roomId);
                        }
                    }
                });

                element.removeChangeListeners();
                realm.close();
                finish();
                // call this for finish activity chat when delete chat
                if (G.onDeleteChatFinishActivity != null) {
                    G.onDeleteChatFinishActivity.onFinish();
                }
            }
        });
    }

    @Override
    public void onUserUpdateStatus(long userId, final long time, final String status) {

        if (this.userId == userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUserStatus(AppUtils.getStatsForUser(status), time);
                }
            });
        }
    }
}


