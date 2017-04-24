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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.R;
import com.iGap.WebSocketClient;
import com.iGap.fragments.ContactGroupFragment;
import com.iGap.fragments.FragmentCreateChannel;
import com.iGap.fragments.FragmentIgapSearch;
import com.iGap.fragments.FragmentNewGroup;
import com.iGap.fragments.RegisteredContactsFragment;
import com.iGap.fragments.SearchFragment;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.helper.HelperCalculateKeepMedia;
import com.iGap.helper.HelperClientCondition;
import com.iGap.helper.HelperGetAction;
import com.iGap.helper.HelperGetDataFromOtherApp;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.helper.HelperLogout;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.HelperUrl;
import com.iGap.helper.ServiceContact;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnChangeUserPhotoListener;
import com.iGap.interfaces.OnChatClearMessageResponse;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.interfaces.OnChatSendMessageResponse;
import com.iGap.interfaces.OnChatUpdateStatusResponse;
import com.iGap.interfaces.OnClientCondition;
import com.iGap.interfaces.OnClientGetRoomListResponse;
import com.iGap.interfaces.OnComplete;
import com.iGap.interfaces.OnConnectionChangeState;
import com.iGap.interfaces.OnGetPermission;
import com.iGap.interfaces.OnGroupAvatarResponse;
import com.iGap.interfaces.OnRefreshActivity;
import com.iGap.interfaces.OnSetActionInRoom;
import com.iGap.interfaces.OnUpdateAvatar;
import com.iGap.interfaces.OnUpdating;
import com.iGap.interfaces.OnUserInfoMyClient;
import com.iGap.interfaces.OnUserSessionLogout;
import com.iGap.interfaces.OpenFragment;
import com.iGap.libs.floatingAddButton.ArcMenu;
import com.iGap.libs.floatingAddButton.StateChangeListener;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.LoginActions;
import com.iGap.module.MusicPlayer;
import com.iGap.module.MyAppBarLayout;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.ShouldScrolledBehavior;
import com.iGap.module.enums.ChannelChatRole;
import com.iGap.module.enums.ConnectionState;
import com.iGap.module.enums.GroupChatRole;
import com.iGap.module.enums.RoomType;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestChannelDelete;
import com.iGap.request.RequestChannelLeft;
import com.iGap.request.RequestChatDelete;
import com.iGap.request.RequestChatGetRoom;
import com.iGap.request.RequestClientCondition;
import com.iGap.request.RequestClientGetRoomList;
import com.iGap.request.RequestGroupDelete;
import com.iGap.request.RequestGroupLeft;
import com.iGap.request.RequestUserInfo;
import com.iGap.request.RequestUserSessionLogout;
import com.wang.avi.AVLoadingIndicatorView;
import io.github.meness.emoji.EmojiTextView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import java.io.File;
import java.io.IOException;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;
import static com.iGap.G.clientConditionGlobal;
import static com.iGap.G.context;
import static com.iGap.G.firstTimeEnterToApp;
import static com.iGap.G.isSendContact;
import static com.iGap.G.userId;
import static com.iGap.R.string.updating;
import static com.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static com.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class ActivityMain extends ActivityEnhanced implements OnUserInfoMyClient, OnComplete, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse, OnSetActionInRoom, OnGroupAvatarResponse, OnUpdateAvatar, OnClientCondition {

    public static boolean isMenuButtonAddShown = false;
    FloatingActionButton btnStartNewChat;
    FloatingActionButton btnCreateNewGroup;
    FloatingActionButton btnCreateNewChannel;
    LinearLayout mediaLayout;
    MusicPlayer musicPlayer;
    public static boolean needUpdateSortList = false;

    public static MyAppBarLayout appBarLayout;

    public static ArcMenu arcMenu;
    private int clickPosition = 0;
    private boolean keepMedia;
    private Typeface titleTypeface;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private boolean isGetContactList = false;
    private ImageView imgNavImage;
    private DrawerLayout drawer;
    private Toolbar mainToolbar;

    private RealmRecyclerView mRecyclerView;
    private Realm mRealm;
    private RoomAdapter roomAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) mRealm.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getDefaultInstance();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        G application = (G) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RoomList");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new HelperGetDataFromOtherApp(getIntent());

        mediaLayout = (LinearLayout) findViewById(R.id.amr_ll_music_layout);
        musicPlayer = new MusicPlayer(mediaLayout);

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        isGetContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT, false);
        if (!isGetContactList) {
            try {
                HelperPermision.getContactPermision(ActivityMain.this, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        /**
                         * set G.isSendContact = false to permitted user
                         * for import contacts
                         */
                        G.isSendContact = false;
                        LoginActions.importContact();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SHP_SETTING.KEY_GET_CONTACT, true);
                        editor.apply();
                    }

                    @Override
                    public void deny() {
                        /**
                         * user not allowed to import contact, so client set
                         * isSendContact = true for avoid from try again
                         */
                        isSendContact = true;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        G.helperNotificationAndBadge.cancelNotification();
        G.onGroupAvatarResponse = this;
        G.onUpdateAvatar = this;

        G.onConvertToGroup = new OpenFragment() {
            @Override
            public void openFragmentOnActivity(String type, final Long roomId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentNewGroup fragmentNewGroup = new FragmentNewGroup();
                        Bundle bundle = new Bundle();
                        bundle.putString("TYPE", "ConvertToGroup");
                        bundle.putLong("ROOMID", roomId);
                        fragmentNewGroup.setArguments(bundle);

                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragmentNewGroup, "newGroup_fragment").commitAllowingStateLoss();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                });
            }
        };

        G.onClientGetRoomListResponse = new OnClientGetRoomListResponse() {
            @Override
            public void onClientGetRoomList(final List<ProtoGlobal.Room> roomList, ProtoResponse.Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * to first enter to app , client first compute clientCondition then
                         * getRoomList and finally send condition that before get clientCondition;
                         * in else state compute new client condition with latest messaging state
                         */
                        if (firstTimeEnterToApp) {
                            firstTimeEnterToApp = false;
                            sendClientCondition();
                        } else {
                            new RequestClientCondition().clientCondition(HelperClientCondition.computeClientCondition());
                        }

                        putChatToDatabase(roomList);

                        //swipeRefreshLayout.setRefreshing(false);// swipe refresh is complete and gone
                    }
                });
            }

            @Override
            public void onTimeout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        firstTimeEnterToApp = false;
                        getChatsList();
                        swipeRefreshLayout.setRefreshing(false);// swipe refresh is complete and gone
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);// swipe refresh is complete and gone
                    }
                });

                if (majorCode == 9) {
                    if (G.currentActivity != null) {
                        G.currentActivity.finish();
                    }
                    Intent intent = new Intent(G.context, ActivityProfile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    G.context.startActivity(intent);
                }
            }
        };

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        initComponent();
        connectionState();
        initRecycleView();
        initFloatingButtonCreateNew();
        initDrawerMenu();

        keepMedia = sharedPreferences.getBoolean(SHP_SETTING.KEY_KEEP_MEDIA, false);
        if (keepMedia) {// if Was selected keep media at 1week
            new HelperCalculateKeepMedia().calculateTime();
        }
    }

    /**
     * send client condition
     */
    private void sendClientCondition() {
        if (clientConditionGlobal != null) {
            new RequestClientCondition().clientCondition(clientConditionGlobal);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendClientCondition();
                }
            }, 1000);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //RealmRoomMessage.fetchNotDeliveredMessages(new OnActivityMainStart() {
        //    @Override
        //    public void sendDeliveredStatus(RealmRoom room, RealmRoomMessage message) {
        //        G.chatUpdateStatusUtil.sendUpdateStatus(room.getType(), message.getRoomId(), message.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
        //    }
        //});
    }

    /**
     * init  menu drawer
     */

    private void initDrawerMenu() {

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
            }
        };

        ViewGroup drawerButton = (ViewGroup) findViewById(R.id.amr_ripple_menu);
        drawerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        toggle.setDrawerIndicatorEnabled(false);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        RealmUserInfo realmUserInfo = mRealm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            String username = realmUserInfo.getUserInfo().getDisplayName();
            String phoneNumber = realmUserInfo.getUserInfo().getPhoneNumber();

            imgNavImage = (ImageView) findViewById(R.id.lm_imv_user_picture);
            TextView txtNavName = (TextView) findViewById(R.id.lm_txt_user_name);
            TextView txtNavPhone = (TextView) findViewById(R.id.lm_txt_phone_number);
            txtNavName.setText(username);
            txtNavPhone.setText(phoneNumber);

            if (HelperCalander.isLanguagePersian) {
                txtNavPhone.setText(HelperCalander.convertToUnicodeFarsiNumber(txtNavPhone.getText().toString()));
                txtNavName.setText(HelperCalander.convertToUnicodeFarsiNumber(txtNavName.getText().toString()));
            }
            new RequestUserInfo().userInfo(realmUserInfo.getUserId());
            setImage(realmUserInfo.getUserId());
        }

        final ViewGroup navBackGround = (ViewGroup) findViewById(R.id.lm_layout_user_picture);
        navBackGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawer.closeDrawer(GravityCompat.START);

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Realm realm = Realm.getDefaultInstance();
                        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                        if (realmUserInfo != null) {
                            long username = realmUserInfo.getUserId();
                            chatGetRoom(username);
                        }
                        realm.close();
                    }
                }, 225);
            }
        });

        TextView txtCloud = (TextView) findViewById(R.id.lm_txt_cloud);
        txtCloud.setTextColor(Color.parseColor(G.appBarColor));
        txtCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navBackGround.performClick();
            }
        });

        ViewGroup itemNavChat = (ViewGroup) findViewById(R.id.lm_ll_new_chat);
        itemNavChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        final Fragment fragment = RegisteredContactsFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TITLE", "New Chat");
                        fragment.setArguments(bundle);

                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                }, 256);
            }
        });

        ViewGroup itemNavGroup = (ViewGroup) findViewById(R.id.lm_ll_new_group);
        itemNavGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TYPE", "NewGroup");
                        fragment.setArguments(bundle);

                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                }, 256);
            }
        });

        ViewGroup itemNavChanel = (ViewGroup) findViewById(R.id.lm_ll_new_channle);
        itemNavChanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TYPE", "NewChanel");
                        fragment.setArguments(bundle);
                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                }, 256);
            }
        });

        ViewGroup igapSearch = (ViewGroup) findViewById(R.id.lm_ll_igap_search);
        igapSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Fragment fragment = FragmentIgapSearch.newInstance();

                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "Search_fragment_igap").commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                }, 256);
            }
        });


        ViewGroup itemNavContacts = (ViewGroup) findViewById(R.id.lm_ll_contacts);
        itemNavContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Fragment fragment = RegisteredContactsFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TITLE", "Contacts");
                        fragment.setArguments(bundle);

                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                }, 256);
            }
        });

        ViewGroup itemNavSend = (ViewGroup) findViewById(R.id.lm_ll_invite_friends);
        itemNavSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Join iGap : https://www.igap.net/ I'm waiting for you !");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                }, 256);
            }
        });
        ViewGroup itemNavSetting = (ViewGroup) findViewById(R.id.lm_ll_setting);
        itemNavSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            HelperPermision.getStoragePermision(ActivityMain.this, new OnGetPermission() {
                                @Override
                                public void Allow() {
                                    Intent intent = new Intent(G.context, ActivitySetting.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    //ActivityMain.mLeftDrawerLayout.closeDrawer();
                                }

                                @Override
                                public void deny() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 256);
            }
        });

        ViewGroup itemNavOut = (ViewGroup) findViewById(R.id.lm_ll_igap_faq);
        itemNavOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        new MaterialDialog.Builder(ActivityMain.this).title(getResources().getString(R.string.log_out))
                                .content(R.string.content_log_out)
                                .positiveText(getResources().getString(R.string.B_ok))
                                .negativeText(getResources().getString(R.string.B_cancel))
                                .iconRes(R.mipmap.exit_to_app_button)
                                .maxIconSize((int) getResources().getDimension(R.dimen.dp24))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        G.onUserSessionLogout = new OnUserSessionLogout() {
                                            @Override
                                            public void onUserSessionLogout() {

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        HelperLogout.logout();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
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
                                }).show();
                    }
                }, 256);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            clickPosition = (int) ev.getX();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initComponent() {

        contentLoading = (ContentLoadingProgressBar) findViewById(R.id.loadingContent);
        contentLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);

        RippleView rippleSearch = (RippleView) findViewById(R.id.amr_ripple_search);
        rippleSearch.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Fragment fragment = SearchFragment.newInstance();

                try {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "Search_fragment").commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });

        if (!HelperCalander.isLanguagePersian) {
            titleTypeface = Typeface.createFromAsset(getAssets(), "fonts/neuropolitical.ttf");
        } else {
            titleTypeface = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf");
        }
    }

    private void connectionState() {
        final TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);
        if (G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
            txtIgap.setText(R.string.waiting_for_network);
            txtIgap.setTypeface(null, Typeface.BOLD);
        } else if (G.connectionState == ConnectionState.CONNECTING) {
            txtIgap.setText(R.string.connecting);
            txtIgap.setTypeface(null, Typeface.BOLD);
        } else if (G.connectionState == ConnectionState.UPDATING) {
            txtIgap.setText(updating);
            txtIgap.setTypeface(null, Typeface.BOLD);
        } else {
            txtIgap.setText(R.string.igap);
            txtIgap.setTypeface(titleTypeface, Typeface.BOLD);
        }

        G.onConnectionChangeState = new OnConnectionChangeState() {
            @Override
            public void onChangeState(final ConnectionState connectionStateR) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        G.connectionState = connectionStateR;
                        if (connectionStateR == ConnectionState.WAITING_FOR_NETWORK) {
                            txtIgap.setText(R.string.waiting_for_network);
                            txtIgap.setTypeface(null, Typeface.BOLD);
                        } else if (connectionStateR == ConnectionState.CONNECTING) {
                            txtIgap.setText(R.string.connecting);
                            txtIgap.setTypeface(null, Typeface.BOLD);
                        } else if (connectionStateR == ConnectionState.UPDATING) {
                            txtIgap.setText(R.string.updating);
                            txtIgap.setTypeface(null, Typeface.BOLD);
                        } else {
                            txtIgap.setText(R.string.igap);
                            txtIgap.setTypeface(titleTypeface, Typeface.BOLD);
                        }
                    }
                });
            }
        };

        G.onUpdating = new OnUpdating() {
            @Override
            public void onUpdating() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        G.connectionState = ConnectionState.UPDATING;
                        txtIgap.setText(R.string.updating);
                        txtIgap.setTypeface(null, Typeface.BOLD);
                    }
                });
            }

            @Override
            public void onCancelUpdating() {
                /**
                 * if yet still G.connectionState is in update state
                 * show latestState that was in previous state
                 */
                if (G.connectionState == ConnectionState.UPDATING) {
                    G.onConnectionChangeState.onChangeState(ConnectionState.IGAP);
                }
            }
        };
    }

    private void initFloatingButtonCreateNew() {
        arcMenu = (ArcMenu) findViewById(R.id.ac_arc_button_add);
        arcMenu.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onMenuOpened() {

            }

            @Override
            public void onMenuClosed() {
                isMenuButtonAddShown = false;
            }
        });

        btnStartNewChat = (FloatingActionButton) findViewById(R.id.ac_fab_start_new_chat);
        btnStartNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "New Chat");
                fragment.setArguments(bundle);

                try {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                arcMenu.toggleMenu();

            }
        });

        btnCreateNewGroup = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_group);
        btnCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewGroup");
                fragment.setArguments(bundle);

                try {
                    ActivityMain.this.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }

                arcMenu.toggleMenu();
            }
        });

        btnCreateNewChannel = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_channel);
        btnCreateNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewChanel");
                fragment.setArguments(bundle);
                try {
                    ActivityMain.this.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                arcMenu.toggleMenu();
            }
        });
    }

    private void initRecycleView() {

        mRecyclerView = (RealmRecyclerView) findViewById(R.id.cl_recycler_view_contact);
        mRecyclerView.setItemViewCacheSize(100);
        mRecyclerView.setDrawingCacheEnabled(true);

        //PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(this);
        //mRecyclerView.getRecycleView().setLayoutManager(preCachingLayoutManager);
        //LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityMain.this);
        //mRecyclerView.getRecycleView().setLayoutManager(mLayoutManager);

        RealmResults<RealmRoom> results = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAllSorted(RealmRoomFields.UPDATED_TIME, Sort.DESCENDING);
        roomAdapter = new RoomAdapter(this, results, this);
        mRecyclerView.setAdapter(roomAdapter);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
        params.setBehavior(new ShouldScrolledBehavior((LinearLayoutManager) mRecyclerView.getRecycleView().getLayoutManager(), roomAdapter));
        mRecyclerView.getRecycleView().setLayoutParams(params);

        appBarLayout = (MyAppBarLayout) findViewById(R.id.appBarLayout);
        final ViewGroup toolbar = (ViewGroup) findViewById(R.id.rootToolbar);
        appBarLayout.addOnMoveListener(new MyAppBarLayout.OnMoveListener() {
            @Override
            public void onAppBarLayoutMove(AppBarLayout appBarLayout, int verticalOffset, boolean moveUp) {
                toolbar.clearAnimation();
                if (moveUp) {
                    if (toolbar.getAlpha() != 0F) {
                        toolbar.animate().setDuration(150).alpha(0F).start();
                    }
                } else {
                    if (toolbar.getAlpha() != 1F) {
                        toolbar.animate().setDuration(150).alpha(1F).start();
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (heartBeatTimeOut()) {
                    WebSocketClient.checkConnection();
                }
                new RequestClientGetRoomList().clientGetRoomList();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.room_message_blue, R.color.accent);

        mRecyclerView.getRecycleView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (arcMenu.isMenuOpened()) arcMenu.toggleMenu();

                if (dy > 0) {
                    // Scroll Down
                    if (arcMenu.fabMenu.isShown()) {
                        arcMenu.fabMenu.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!arcMenu.fabMenu.isShown()) {
                        arcMenu.fabMenu.show();
                    }
                }
            }
        });
    }

    /**
     * put fetched chat to database
     *
     * @param rooms ProtoGlobal.Room
     */
    private void putChatToDatabase(final List<ProtoGlobal.Room> rooms) {

        /**
         * (( hint : i don't used from mRealm instance ,because i have an error
         * that realm is closed, and for avoid from that error i used from
         * new instance for this action ))
         */

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RealmRoom> list = realm.where(RealmRoom.class).findAll();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setDeleted(true);
                }

                for (ProtoGlobal.Room room : rooms) {
                    RealmRoom.putOrUpdate(room);
                }

                // delete messages and rooms that was deleted
                RealmResults<RealmRoom> deletedRoomsList = realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, true).equalTo(RealmRoomFields.KEEP_ROOM, false).findAll();
                for (RealmRoom item : deletedRoomsList) {
                    /**
                     * delete all message in deleted room
                     */
                    realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, item.getId()).findAll().deleteAllFromRealm();
                    item.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //mLeftDrawerLayout.toggle();
        return false;
    }

    private void muteNotification(final Long id, final boolean mute) {

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst().setMute(!mute);
            }
        });

        //  roomAdapter.updateUiById(id);
    }

    private void clearHistory(Long id) {

        ActivityChat.clearHistoryMessage(id);

    }

    private void onSelectRoomMenu(String message, RealmRoom item) {
        if (checkValidationForRealm(item)) {
            switch (message) {
                case "txtMuteNotification":
                    muteNotification(item.getId(), item.getMute());
                    break;
                case "txtClearHistory":
                    clearHistory(item.getId());
                    break;
                case "txtDeleteChat":
                    if (item.getType() == ProtoGlobal.Room.Type.CHAT) {
                        new RequestChatDelete().chatDelete(item.getId());
                    } else if (item.getType() == ProtoGlobal.Room.Type.GROUP) {
                        if (item.getGroupRoom().getRole() == GroupChatRole.OWNER) {
                            new RequestGroupDelete().groupDelete(item.getId());
                        } else {
                            new RequestGroupLeft().groupLeft(item.getId());
                        }
                    } else if (item.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                        if (item.getChannelRoom().getRole() == ChannelChatRole.OWNER) {
                            new RequestChannelDelete().channelDelete(item.getId());
                        } else {
                            new RequestChannelLeft().channelLeft(item.getId());
                        }
                    }
                    break;
            }
        }
    }

    private boolean checkValidationForRealm(RealmRoom realmRoom) {
        if (realmRoom != null && realmRoom.isManaged() && realmRoom.isValid() && realmRoom.isLoaded()) {
            return true;
        }
        return false;
    }

    private boolean heartBeatTimeOut() {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - G.latestHearBeatTime);

        if (difference >= Config.HEART_BEAT_CHECKING_TIME_OUT) {
            return true;
        }

        return false;
    }

    private void testIsSecure() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.isSecure && G.userLogin) {
                    new RequestClientGetRoomList().clientGetRoomList();
                } else {
                    testIsSecure();
                }
            }
        }, 1000);
    }

    private ContentLoadingProgressBar contentLoading;

    private void getChatsList() {
        //swipeRefreshLayout.setRefreshing(true);
        /*if (fromServer && G.socketConnection) {
            testIsSecure();
        } else {*/
        if (firstTimeEnterToApp) {
            testIsSecure();
        }

        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        // delete messages and rooms in the deleted room
                        RealmResults<RealmRoom> deletedRoomsList = realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, true).equalTo(RealmRoomFields.KEEP_ROOM, false).findAll();
                        for (RealmRoom item : deletedRoomsList) {
                            Log.i("GGG", "getTitle : " + item.getTitle());
                            realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, item.getId()).findAll().deleteAllFromRealm();
                            item.deleteFromRealm();
                        }

                        if (needUpdateSortList) {

                            for (RealmRoom Room : realm.where(RealmRoom.class).findAll()) {
                                if (Room.getLastMessage() != null) {
                                    if (Room.getLastMessage().getUpdateTime() > 0) {
                                        Room.setUpdatedTime(Room.getLastMessage().getUpdateOrCreateTime());
                                    }
                                }
                            }

                            needUpdateSortList = false;
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {


                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        SearchFragment myFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag("Search_fragment");

        FragmentNewGroup fragmentNeGroup = (FragmentNewGroup) getSupportFragmentManager().findFragmentByTag("newGroup_fragment");
        FragmentCreateChannel fragmentCreateChannel = (FragmentCreateChannel) getSupportFragmentManager().findFragmentByTag("createChannel_fragment");
        ContactGroupFragment fragmentContactGroup = (ContactGroupFragment) getSupportFragmentManager().findFragmentByTag("contactGroup_fragment");
        FragmentIgapSearch fragmentIgapSearch = (FragmentIgapSearch) getSupportFragmentManager().findFragmentByTag("Search_fragment_igap");


        if (fragmentNeGroup != null && fragmentNeGroup.isVisible()) {

            try {
                getSupportFragmentManager().beginTransaction().remove(fragmentNeGroup).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else if (fragmentCreateChannel != null && fragmentCreateChannel.isVisible()) {
            try {
                getSupportFragmentManager().beginTransaction().remove(fragmentCreateChannel).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else if (fragmentContactGroup != null && fragmentContactGroup.isVisible()) {
            try {
                getSupportFragmentManager().beginTransaction().remove(fragmentContactGroup).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }
            ;
        } else if (fragmentIgapSearch != null && fragmentIgapSearch.isVisible()) {
            try {
                getSupportFragmentManager().beginTransaction().remove(fragmentIgapSearch).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else if (myFragment != null && myFragment.isVisible()) {
            try {
                getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * after change language in ActivitySetting this part refresh Activity main
         */
        G.onRefreshActivity = new OnRefreshActivity() {
            @Override
            public void refresh(String changeLanguage) {
                ActivityMain.this.recreate();
            }
        };

        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        arcMenu.setBackgroundTintColor();

        btnStartNewChat.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));
        btnCreateNewGroup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));
        btnCreateNewChannel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));

        if (MusicPlayer.mp != null) {
            MusicPlayer.initLayoutTripMusic(mediaLayout);
        }
        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);
        G.onClientCondition = this;
        G.onSetActionInRoom = this;

        getChatsList();
        startService(new Intent(this, ServiceContact.class));

        HelperUrl.getLinkinfo(getIntent(), ActivityMain.this);
        getIntent().setData(null);
    }

    @Override
    public void complete(boolean result, String messageOne, String MessageTow) {
        if (messageOne.equals("closeMenuButton")) {
            arcMenu.toggleMenu();
        }
    }

    @Override
    public void onChatClearMessage(final long roomId, long clearId, final ProtoResponse.Response response) {
        //empty
    }

    @Override
    public void onMessageUpdate(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        //empty
    }

    @Override
    public void onMessageReceive(final long roomId, final String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int firstVisibleItem = ((LinearLayoutManager) mRecyclerView.getRecycleView().getLayoutManager()).findFirstVisibleItemPosition();
                if (firstVisibleItem < 5) {
                    mRecyclerView.getRecycleView().scrollToPosition(0);
                }
            }
        });

        /**
         * don't send update status for own message
         */
        if (roomMessage.getAuthor().getUser() != null && roomMessage.getAuthor().getUser().getUserId() != G.userId) {
            // user has received the message, so I make a new delivered update status request
            if (roomType == ProtoGlobal.Room.Type.CHAT) {
                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
            } else if (roomType == ProtoGlobal.Room.Type.GROUP && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
            }
        }
    }

    @Override
    public void onMessageFailed(final long roomId, RealmRoomMessage roomMessage) {
        //empty
    }

    @Override
    public void onChatUpdateStatus(final long roomId, long messageId, final ProtoGlobal.RoomMessageStatus status, long statusVersion) {
        //empty
    }

    @Override
    public void onUserInfoTimeOut() {
        //empty
    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {
        //empty
    }

    @Override
    public void onSetAction(final long roomId, final long userId, final ProtoGlobal.ClientAction clientAction) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(final Realm realm) {

                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                        if (realmRoom != null) {

                            if (realmRoom.getType() != null) {
                                String action = HelperGetAction.getAction(roomId, realmRoom.getType(), clientAction);
                                realmRoom.setActionState(action, userId);
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                    }
                });
            }
        });
    }

    //******* GroupAvatar and ChannelAvatar

    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {


    }

    @Override
    public void onAvatarAddError() {

    }

    @Override
    public void onUpdateAvatar(final long roomId) {

    }

    @Override
    public void onClientCondition() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClientConditionError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void setImage(long userId) {

        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imgNavImage);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imgNavImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgNavImage.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });

        G.onChangeUserPhotoListener = new OnChangeUserPhotoListener() {
            @Override
            public void onChangePhoto(final String imagePath) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imagePath == null || !new File(imagePath).exists()) {
                            Realm realm1 = Realm.getDefaultInstance();
                            RealmUserInfo realmUserInfo = realm1.where(RealmUserInfo.class).findFirst();
                            imgNavImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgNavImage.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));
                            realm1.close();
                        } else {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgNavImage);
                        }
                    }
                });
            }

            @Override
            public void onChangeInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imgNavImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgNavImage.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        };
        //realm.close();
    }

    @Override
    public void onUserInfoMyClient(ProtoGlobal.RegisteredUser user, String identity) {
        setImage(user.getId());
    }

    private void chatGetRoom(final long peerId) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();

        if (realmRoom != null) {

            Intent intent = new Intent(context, ActivityChat.class);
            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            //            getActivity().getSupportFragmentManager().popBackStack();
        } else {

            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final long roomId) {
                    Intent intent = new Intent(context, ActivityChat.class);
                    intent.putExtra("peerId", peerId);
                    intent.putExtra("RoomId", roomId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
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

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
        realm.close();
    }

    public class RoomAdapter extends RealmBasedRecyclerViewAdapter<RealmRoom, RoomAdapter.ViewHolder> {

        public OnComplete mComplete;
        public String action;
        private Typeface typeFaceIcon;

        public RoomAdapter(Context context, RealmResults<RealmRoom> realmResults, OnComplete complete) {
            super(context, realmResults, true, false, false, "");
            this.mComplete = complete;
        }

        @Override
        public RoomAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.chat_sub_layout, viewGroup, false);
            return new RoomAdapter.ViewHolder(v);
        }

        @Override
        public void onBindRealmViewHolder(final ViewHolder holder, final int i) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    RealmRoom mInfo = holder.mInfo = realmResults.get(i);

                    if (mInfo != null && mInfo.isValid() && !mInfo.isDeleted()) {
                        if (mInfo.getActionState() != null && ((mInfo.getType() == GROUP || mInfo.getType() == CHANNEL) || ((RealmRoom.isCloudRoom(mInfo.getId()) || (!RealmRoom.isCloudRoom(mInfo.getId()) && mInfo.getActionStateUserId() != userId))))) {
                            //holder.messageStatus.setVisibility(GONE);
                            holder.lastMessageSender.setVisibility(View.GONE);
                            holder.lastMessage.setVisibility(View.VISIBLE);
                            holder.avi.setVisibility(View.VISIBLE);
                            holder.lastMessage.setText(mInfo.getActionState());
                            holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                        } else if (mInfo.getDraft() != null && !TextUtils.isEmpty(mInfo.getDraft().getMessage())) {
                            holder.avi.setVisibility(View.GONE);
                            holder.messageStatus.setVisibility(GONE);
                            holder.lastMessage.setVisibility(View.VISIBLE);
                            holder.lastMessageSender.setVisibility(View.VISIBLE);
                            holder.lastMessageSender.setText(R.string.txt_draft);
                            holder.lastMessageSender.setTextColor(Color.parseColor("#ff4644"));
                            holder.lastMessage.setText(mInfo.getDraft().getMessage());
                            holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_gray));
                        } else {
                            holder.avi.setVisibility(View.GONE);
                            if (mInfo.getLastMessage() != null) {
                                String lastMessage = AppUtils.rightLastMessage(mInfo.getId(), holder.itemView.getResources(), mInfo.getType(), mInfo.getLastMessage(), mInfo.getLastMessage().getForwardMessage() != null ? mInfo.getLastMessage().getForwardMessage().getAttachment() : mInfo.getLastMessage().getAttachment());
                                if (lastMessage == null) {
                                    lastMessage = mInfo.getLastMessage().getMessage();
                                }

                                if (lastMessage == null || lastMessage.isEmpty()) {
                                    holder.messageStatus.setVisibility(GONE);
                                    holder.lastMessage.setVisibility(GONE);
                                    holder.lastMessageSender.setVisibility(GONE);
                                } else {
                                    if (mInfo.getLastMessage().isSenderMe()) {
                                        AppUtils.rightMessageStatus(holder.messageStatus, ProtoGlobal.RoomMessageStatus.valueOf(mInfo.getLastMessage().getStatus()), mInfo.getLastMessage().isSenderMe());
                                        holder.messageStatus.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.messageStatus.setVisibility(GONE);
                                    }

                                    /**
                                     * here i get latest message from chat history with chatId and
                                     * get DisplayName with that . when login app client get latest
                                     * message for each group from server , if latest message that
                                     * send server and latest message that exist in client for that
                                     * room be different latest message sender showing will be wrong
                                     */

                                    String lastMessageSender = "";
                                    if (mInfo.getLastMessage().isSenderMe()) {
                                        lastMessageSender = holder.itemView.getResources().getString(R.string.txt_you);
                                    } else {

                                        Realm realm1 = Realm.getDefaultInstance();
                                        RealmRegisteredInfo realmRegisteredInfo = realm1.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mInfo.getLastMessage().getUserId()).findFirst();
                                        if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName() != null) {

                                            String _name = realmRegisteredInfo.getDisplayName();
                                            if (_name.length() > 0) {

                                                if (Character.getDirectionality(_name.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
                                                    if (HelperCalander.isLanguagePersian) {
                                                        lastMessageSender = _name + ": ";
                                                    } else {
                                                        lastMessageSender = " :" + _name;
                                                    }
                                                } else {
                                                    if (HelperCalander.isLanguagePersian) {
                                                        lastMessageSender = " :" + _name;
                                                    } else {
                                                        lastMessageSender = _name + ": ";
                                                    }
                                                }
                                            }
                                        }
                                        realm1.close();
                                    }

                                    if (mInfo.getType() == ProtoGlobal.Room.Type.GROUP) {
                                        holder.lastMessageSender.setText(lastMessageSender);
                                        holder.lastMessageSender.setTextColor(Color.parseColor("#2bbfbd"));
                                        holder.lastMessageSender.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.lastMessageSender.setVisibility(GONE);
                                    }

                                    holder.lastMessage.setVisibility(View.VISIBLE);

                                    if (mInfo.getLastMessage() != null) {
                                        ProtoGlobal.RoomMessageType _type, tmp;

                                        _type = mInfo.getLastMessage().getMessageType();

                                        try {
                                            if (mInfo.getLastMessage().getReplyTo() != null) {
                                                tmp = mInfo.getLastMessage().getReplyTo().getMessageType();
                                                if (tmp != null) _type = tmp;
                                            }
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            if (mInfo.getLastMessage().getForwardMessage() != null) {
                                                tmp = mInfo.getLastMessage().getForwardMessage().getMessageType();
                                                if (tmp != null) _type = tmp;
                                            }
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }

                                        switch (_type) {
                                            case VOICE:
                                                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                                                holder.lastMessage.setText(R.string.voice_message);
                                                break;
                                            case VIDEO:
                                                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                                                holder.lastMessage.setText(R.string.video_message);
                                                break;
                                            case FILE:
                                                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                                                holder.lastMessage.setText(R.string.file_message);
                                                break;
                                            case AUDIO:
                                                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                                                holder.lastMessage.setText(R.string.audio_message);
                                                break;
                                            case IMAGE:
                                                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                                                holder.lastMessage.setText(R.string.image_message);
                                                break;
                                            case CONTACT:
                                                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                                                holder.lastMessage.setText(R.string.contact_message);
                                                break;
                                            case GIF:
                                                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                                                holder.lastMessage.setText(R.string.gif_message);
                                                break;
                                            case LOCATION:
                                                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                                                holder.lastMessage.setText(R.string.location_message);
                                                break;
                                            default:
                                                if (!HelperCalander.isLanguagePersian) {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                                        holder.lastMessage.setTextDirection(View.TEXT_DIRECTION_LTR);
                                                    }
                                                }
                                                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_gray));
                                                holder.lastMessage.setText(lastMessage);
                                                break;
                                        }
                                    } else {
                                        holder.lastMessage.setText(lastMessage);
                                    }
                                }
                            } else {
                                holder.lastMessage.setVisibility(GONE);
                                holder.lastSeen.setVisibility(GONE);
                                holder.messageStatus.setVisibility(GONE);
                                holder.lastMessageSender.setVisibility(GONE);
                            }
                        }

                        long idForGetAvatar;
                        HelperAvatar.AvatarType avatarType;
                        if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
                            idForGetAvatar = mInfo.getChatRoom().getPeerId();
                            avatarType = HelperAvatar.AvatarType.USER;
                        } else {
                            idForGetAvatar = mInfo.getId();
                            avatarType = HelperAvatar.AvatarType.ROOM;
                        }

                        final RealmAvatar realmAvatar = getLastAvatar(idForGetAvatar);
                        if (realmAvatar != null) {

                            HelperAvatar.updatePath(realmAvatar);

                            if (realmAvatar.getFile().isFileExistsOnLocal()) {
                                G.imageLoader.displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalFilePath()), holder.image);
                            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                                G.imageLoader.displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalThumbnailPath()), holder.image);
                            } else {
                                HelperAvatar.getAvatar1(idForGetAvatar, avatarType, new OnAvatarGet() {
                                    @Override
                                    public void onAvatarGet(final String avatarPath, final long ownerId) {
                                        //empty
                                    }

                                    @Override
                                    public void onShowInitials(final String initials, final String color) {
                                        //empty
                                    }
                                });

                                String[] initials = showInitials(idForGetAvatar, avatarType);
                                if (initials != null) {
                                    holder.image.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials[0], initials[1]));
                                }
                            }
                        } else {
                            String[] initials = showInitials(idForGetAvatar, avatarType);
                            if (initials != null) {
                                holder.image.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials[0], initials[1]));
                            }
                        }

                        holder.chatIcon.setTypeface(typeFaceIcon);
                        if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
                            holder.chatIcon.setVisibility(GONE);
                        } else if (mInfo.getType() == ProtoGlobal.Room.Type.GROUP) {
                            typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/MaterialIcons-Regular.ttf");
                            holder.chatIcon.setTypeface(typeFaceIcon);
                            holder.chatIcon.setVisibility(View.VISIBLE);
                            holder.chatIcon.setText(getStringChatIcon(RoomType.GROUP));
                        } else if (mInfo.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                            typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/iGap_font.ttf");
                            holder.chatIcon.setTypeface(typeFaceIcon);
                            holder.chatIcon.setVisibility(View.VISIBLE);
                            holder.chatIcon.setText(getStringChatIcon(RoomType.CHANNEL));
                        }

                        holder.name.setText(mInfo.getTitle());

                        if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateOrCreateTime() != 0) {
                            holder.lastSeen.setText(HelperCalander.getTimeForMainRoom(mInfo.getLastMessage().getUpdateOrCreateTime()));

                            holder.lastSeen.setVisibility(View.VISIBLE);
                        } else {
                            holder.lastSeen.setVisibility(GONE);
                        }

                        if (mInfo.getUnreadCount() < 1) {
                            holder.unreadMessage.setVisibility(GONE);
                        } else {
                            holder.unreadMessage.setVisibility(View.VISIBLE);
                            holder.unreadMessage.setText(Integer.toString(mInfo.getUnreadCount()));

                            if (mInfo.getMute()) {
                                holder.unreadMessage.setBackgroundResource(R.drawable.oval_gray);
                            } else {
                                holder.unreadMessage.setBackgroundResource(R.drawable.oval_red);
                            }
                        }

                        if (mInfo.getMute()) {
                            holder.mute.setVisibility(View.VISIBLE);
                        } else {
                            holder.mute.setVisibility(GONE);
                        }
                    }

                    // for change english number to persian number
                    if (HelperCalander.isLanguagePersian) {
                        holder.lastMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.lastMessage.getText().toString()));
                        holder.name.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.name.getText().toString()));
                        holder.unreadMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.unreadMessage.getText().toString()));
                    }


                }
            });
        }

        public class ViewHolder extends RealmViewHolder {

            public RealmRoom mInfo;
            protected CircleImageView image;
            protected View distanceColor;
            protected TextView chatIcon;
            protected EmojiTextView name;
            protected EmojiTextView lastMessageSender;
            protected TextView mute;
            protected EmojiTextView lastMessage;
            protected TextView lastSeen;
            protected TextView unreadMessage;
            protected ImageView messageStatus;
            private AVLoadingIndicatorView avi;

            public ViewHolder(View view) {
                super(view);

                avi = (AVLoadingIndicatorView) view.findViewById(R.id.cs_avi);
                image = (CircleImageView) view.findViewById(R.id.cs_img_contact_picture);
                distanceColor = view.findViewById(R.id.cs_view_distance_color);
                chatIcon = (TextView) view.findViewById(R.id.cs_txt_contact_icon);
                name = (EmojiTextView) view.findViewById(R.id.cs_txt_contact_name);
                lastMessage = (EmojiTextView) view.findViewById(R.id.cs_txt_last_message);
                lastMessageSender = (EmojiTextView) view.findViewById(R.id.cs_txt_last_message_sender);
                lastSeen = (TextView) view.findViewById(R.id.cs_txt_contact_time);
                unreadMessage = (TextView) view.findViewById(R.id.cs_txt_unread_message);

                mute = (TextView) view.findViewById(R.id.cs_txt_mute);
                messageStatus = (ImageView) view.findViewById(R.id.cslr_txt_tic);

                AndroidUtils.setBackgroundShapeColor(unreadMessage, Color.parseColor(G.notificationColor));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ActivityMain.isMenuButtonAddShown) {
                            mComplete.complete(true, "closeMenuButton", "");
                        } else {
                            if (mInfo.isValid()) {

                                Intent intent = new Intent(ActivityMain.this, ActivityChat.class);
                                intent.putExtra("RoomId", mInfo.getId());

                                startActivity(intent);
                                overridePendingTransition(0, 0);

                                if (ActivityMain.arcMenu != null && ActivityMain.arcMenu.isMenuOpened()) {
                                    ActivityMain.arcMenu.toggleMenu();
                                }
                            }
                        }
                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (ActivityMain.isMenuButtonAddShown) {
                            mComplete.complete(true, "closeMenuButton", "");
                        } else {
                            if (mInfo.isValid()) {
                                String role = null;
                                if (mInfo.getType() == ProtoGlobal.Room.Type.GROUP) {
                                    role = mInfo.getGroupRoom().getRole().toString();
                                } else if (mInfo.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                                    role = mInfo.getChannelRoom().getRole().toString();
                                }

                                MyDialog.showDialogMenuItemRooms(ActivityMain.this, mInfo.getType(), mInfo.getMute(), role, new OnComplete() {
                                    @Override
                                    public void complete(boolean result, String messageOne, String MessageTow) {
                                        onSelectRoomMenu(messageOne, mInfo);
                                    }
                                });
                            }
                        }
                        return true;
                    }
                });
            }
        }

        private RealmAvatar getLastAvatar(long ownerId) {
            Realm realm = Realm.getDefaultInstance();
            for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAllSorted(RealmAvatarFields.UID, Sort.DESCENDING)) {
                if (avatar.getFile() != null) {
                    return avatar;
                }
            }
            realm.close();
            return null;
        }

        public String[] showInitials(long ownerId, HelperAvatar.AvatarType avatarType) {
            Realm realm = Realm.getDefaultInstance();
            String initials = null;
            String color = null;
            if (avatarType == HelperAvatar.AvatarType.USER) {

                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, ownerId).findFirst();
                if (realmRegisteredInfo != null) {
                    initials = realmRegisteredInfo.getInitials();
                    color = realmRegisteredInfo.getColor();
                } else {
                    for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll()) {
                        if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == ownerId) {
                            new HelperAvatar.UserInfo().getUserInfo(ownerId);
                            initials = realmRoom.getInitials();
                            color = realmRoom.getColor();
                        }
                    }
                }
            } else if (avatarType == HelperAvatar.AvatarType.ROOM) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, ownerId).findFirst();
                if (realmRoom != null) {
                    initials = realmRoom.getInitials();
                    color = realmRoom.getColor();
                }
            }
            realm.close();

            if (initials != null && color != null) {
                return new String[]{initials, color};
            }

            return null;
        }

        /**
         * get string chat icon
         *
         * @param chatType chat type
         * @return String
         */
        private String getStringChatIcon(RoomType chatType) {
            switch (chatType) {
                case CHAT:
                    return "";
                case CHANNEL:
                    return context.getString(R.string.md_channel_icon);
                case GROUP:
                    return context.getString(R.string.md_users_social_symbol);
                default:
                    return null;
            }
        }
    }

}
