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

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentIgapSearch;
import net.iGap.fragments.FragmentLanguage;
import net.iGap.fragments.FragmentMain;
import net.iGap.fragments.FragmentMediaPlayer;
import net.iGap.fragments.FragmentNewGroup;
import net.iGap.fragments.FragmentQrCodeNewDevice;
import net.iGap.fragments.FragmentSetting;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.fragments.RegisteredContactsFragment;
import net.iGap.fragments.SearchFragment;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperCalculateKeepMedia;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperNotificationAndBadge;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ServiceContact;
import net.iGap.interfaces.FinishActivity;
import net.iGap.interfaces.ICallFinish;
import net.iGap.interfaces.ITowPanModDesinLayout;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChangeUserPhotoListener;
import net.iGap.interfaces.OnChatClearMessageResponse;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnClientCondition;
import net.iGap.interfaces.OnClientGetRoomListResponse;
import net.iGap.interfaces.OnConnectionChangeState;
import net.iGap.interfaces.OnGeoGetConfiguration;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnMapRegisterState;
import net.iGap.interfaces.OnMapRegisterStateMain;
import net.iGap.interfaces.OnRefreshActivity;
import net.iGap.interfaces.OnUnreadChange;
import net.iGap.interfaces.OnUpdating;
import net.iGap.interfaces.OnUserInfoMyClient;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.interfaces.OnVerifyNewDevice;
import net.iGap.interfaces.OneFragmentIsOpen;
import net.iGap.interfaces.OpenFragment;
import net.iGap.libs.floatingAddButton.ArcMenu;
import net.iGap.libs.floatingAddButton.StateChangeListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.libs.tabBar.NavigationTabStrip;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileUtils;
import net.iGap.module.LoginActions;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyAppBarLayout;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.enums.ConnectionState;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestGeoGetConfiguration;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserSessionLogout;
import net.iGap.viewmodel.ActivityCallViewModel;
import net.iGap.viewmodel.FragmentSettingViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static net.iGap.G.context;
import static net.iGap.G.isSendContact;
import static net.iGap.G.userId;
import static net.iGap.R.string.updating;
import static net.iGap.fragments.FragmentiGapMap.mapUrls;

public class ActivityMain extends ActivityEnhanced implements OnUserInfoMyClient, OnUnreadChange, OnClientGetRoomListResponse, OnChatClearMessageResponse, OnChatSendMessageResponse, OnClientCondition, OnGroupAvatarResponse, DrawerLayout.DrawerListener, OnMapRegisterStateMain {

    public static final String openChat = "openChat";
    public static final String openMediaPlyer = "openMediaPlyer";


    public static boolean isMenuButtonAddShown = false;
    public static boolean isOpenChatBeforeSheare = false;
    public static boolean isLock = true;
    public static boolean isActivityEnterPassCode = false;
    public static FinishActivity finishActivity;
    public static boolean disableSwipe = false;
    public static OnBackPressedListener onBackPressedListener;
    private static long oldTime;
    private static long currentTime;
    public TextView iconLock;
    public MainInterface mainActionApp;
    public MainInterface mainActionChat;
    public MainInterface mainActionGroup;
    public MainInterface mainActionChannel;
    public MainInterfaceGetRoomList mainInterfaceGetRoomList;
    public ArcMenu arcMenu;
    FragmentCall fragmentCall;
    FloatingActionButton btnStartNewChat;
    FloatingActionButton btnCreateNewGroup;
    FloatingActionButton btnCreateNewChannel;
    SampleFragmentPagerAdapter sampleFragmentPagerAdapter;
    boolean waitingForConfiguration = false;
    private LinearLayout mediaLayout;
    private FrameLayout frameChatContainer;
    private FrameLayout frameMainContainer;
    private FrameLayout frameFragmentBack;
    private FrameLayout frameFragmentContainer;
    private NavigationTabStrip navigationTabStrip;
    private MyAppBarLayout appBarLayout;
    private Typeface titleTypeface;
    private SharedPreferences sharedPreferences;
    private ImageView imgNavImage;
    private DrawerLayout drawer;
    private ProgressBar contentLoading;
    private TextView iconLocation;
    private Realm mRealm;
    private boolean isNeedToRegister = false;
    private ViewPager mViewPager;
    private ArrayList<Fragment> pages = new ArrayList<Fragment>();
    private String phoneNumber;

    public static void setWeight(View view, int value) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = value;
        view.setLayoutParams(params);

        if (value > 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public static void setMediaLayout() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {

                try {

                    if (MusicPlayer.mp != null) {

                        if (MusicPlayer.shearedMediaLayout != null) {
                            MusicPlayer.initLayoutTripMusic(MusicPlayer.shearedMediaLayout);

                            if (MusicPlayer.chatLayout != null) {
                                MusicPlayer.chatLayout.setVisibility(View.GONE);
                            }

                            if (MusicPlayer.mainLayout != null) {
                                MusicPlayer.mainLayout.setVisibility(View.GONE);
                            }
                        } else if (MusicPlayer.chatLayout != null) {
                            MusicPlayer.initLayoutTripMusic(MusicPlayer.chatLayout);

                            if (MusicPlayer.mainLayout != null) {
                                MusicPlayer.mainLayout.setVisibility(View.GONE);
                            }
                        } else if (MusicPlayer.mainLayout != null) {
                            MusicPlayer.initLayoutTripMusic(MusicPlayer.mainLayout);
                        }
                    } else {

                        if (MusicPlayer.mainLayout != null) {
                            MusicPlayer.mainLayout.setVisibility(View.GONE);
                        }

                        if (MusicPlayer.chatLayout != null) {
                            MusicPlayer.chatLayout.setVisibility(View.GONE);
                        }

                        if (MusicPlayer.shearedMediaLayout != null) {
                            MusicPlayer.shearedMediaLayout.setVisibility(View.GONE);
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void setStripLayoutCall() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {

                if (G.isInCall) {

                    if (ActivityCall.stripLayoutChat != null) {
                        ActivityCall.stripLayoutChat.setVisibility(View.VISIBLE);

                        if (ActivityCall.stripLayoutMain != null) {
                            ActivityCall.stripLayoutMain.setVisibility(View.GONE);
                        }
                    } else {
                        if (ActivityCall.stripLayoutMain != null) {
                            ActivityCall.stripLayoutMain.setVisibility(View.VISIBLE);
                        }
                    }
                } else {

                    if (ActivityCall.stripLayoutMain != null) {
                        ActivityCall.stripLayoutMain.setVisibility(View.GONE);
                    }

                    if (ActivityCall.stripLayoutChat != null) {
                        ActivityCall.stripLayoutChat.setVisibility(View.GONE);
                    }
                }
            }
        });


    }

    public Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {

            mRealm = Realm.getDefaultInstance();
        }

        return mRealm;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
        if (G.imageLoader != null) {
            G.imageLoader.clearMemoryCache();
        }
        RealmRoom.clearAllActions();
        if (G.onAudioFocusChangeListener != null) {
            G.onAudioFocusChangeListener.onAudioFocusChangeListener(AudioManager.AUDIOFOCUS_LOSS);
        }

    }

    /**
     * delete content of folder chat background in the first registration
     */
    private void deleteContentFolderChatBackground() {
        FileUtils.deleteRecursive(new File(G.DIR_CHAT_BACKGROUND));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        isOpenChatBeforeSheare = true;
        checkIntent(intent);
    }

    private void checkIntent(Intent intent) {

        if (G.isRestartActivity) {
            return;
        }

        new HelperGetDataFromOtherApp(intent);

        Bundle extras = intent.getExtras();
        if (extras != null) {

            long roomId = extras.getLong(ActivityMain.openChat);
            if (!FragmentLanguage.languageChanged && roomId > 0) { // if language changed not need check enter to chat
                GoToChatActivity goToChatActivity = new GoToChatActivity(roomId);
                long peerId = extras.getLong("PeerID");
                if (peerId > 0) {
                    goToChatActivity.setPeerID(peerId);
                }
                goToChatActivity.startActivity();
            }
            FragmentLanguage.languageChanged = false;

            boolean openMediaPlayer = extras.getBoolean(ActivityMain.openMediaPlyer);
            if (openMediaPlayer) {
                if (getSupportFragmentManager().findFragmentByTag(FragmentMediaPlayer.class.getName()) == null) {
                    FragmentMediaPlayer fragment = new FragmentMediaPlayer();
                    new HelperFragment(fragment).setReplace(false).load();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

//        setTheme(R.style.AppThemeTranslucent);

        if (G.isFirstPassCode) {
            openActivityPassCode();
        }
        //if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        //isNeedToRegister = true; // continue app even don't have storage permission
        //isOnGetPermission = true;
        //}
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        finishActivity = new FinishActivity() {
            @Override
            public void finishActivity() {
                // ActivityChat.this.finish();
                finish();
            }
        };


        if (isNeedToRegister) {

            Intent intent = new Intent(this, ActivityRegisteration.class);
            startActivity(intent);

            finish();
            return;
        }


        G.fragmentManager = getSupportFragmentManager();

        //checkAppAccount();

        try {
            HelperPermission.getPhonePermision(this, null);
        } catch (IOException e) {
            e.printStackTrace();
        }


        RealmUserInfo userInfo = getRealm().where(RealmUserInfo.class).findFirst();

        if (userInfo == null) { // user registered before
            isNeedToRegister = true;
            Intent intent = new Intent(this, ActivityRegisteration.class);
            startActivity(intent);

            if (mRealm != null && !mRealm.isClosed()) {
                mRealm.close();
            }

            finish();
            return;
        }


        if (G.firstTimeEnterToApp) {
            /**
             * set true mFirstRun for get room history after logout and login again
             */

            //licenceChecker();

            sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

            boolean deleteFolderBackground = sharedPreferences.getBoolean(SHP_SETTING.DELETE_FOLDER_BACKGROUND, true);

            if (deleteFolderBackground) {
                deleteContentFolderChatBackground();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SHP_SETTING.DELETE_FOLDER_BACKGROUND, false);
                editor.apply();
            }
        }
        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (G.isDarkTheme) {
            this.setTheme(R.style.Material_blackCustom);
        } else {
            this.setTheme(R.style.Material_lightCustom);
        }
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FrameLayout _mainframe = (FrameLayout) findViewById(R.id.frame_main);

        if (G.isAppRtl) {
            ViewCompat.setLayoutDirection(_mainframe, ViewCompat.LAYOUT_DIRECTION_RTL);
        } else {
            ViewCompat.setLayoutDirection(_mainframe, ViewCompat.LAYOUT_DIRECTION_LTR);
        }


        frameChatContainer = (FrameLayout) findViewById(R.id.am_frame_chat_container);
        frameMainContainer = (FrameLayout) findViewById(R.id.am_frame_main_container);

        if (G.twoPaneMode) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                G.isLandscape = true;
            } else {
                G.isLandscape = false;
            }

            frameFragmentBack = (FrameLayout) findViewById(R.id.am_frame_fragment_back);
            frameFragmentContainer = (FrameLayout) findViewById(R.id.am_frame_fragment_container);

            G.oneFragmentIsOpen = new OneFragmentIsOpen() {
                @Override
                public void justOne() {

                    if (frameFragmentContainer.getChildCount() == 0) {
                        disableSwipe = true;
                    } else {
                        disableSwipe = false;
                    }


                }
            };

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            int size = Math.min(width, height) - 50;

            ViewGroup.LayoutParams lp = frameFragmentContainer.getLayoutParams();
            lp.width = size;
            lp.height = size;


            desighnLayout(chatLayoutMode.none);

            frameFragmentBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onBackPressed();
                }
            });

            G.iTowPanModDesinLayout = new ITowPanModDesinLayout() {
                @Override
                public void onLayout(chatLayoutMode mode) {
                    desighnLayout(mode);
                }

                @Override
                public boolean getBackChatVisibility() {

                    if (frameFragmentBack != null && frameFragmentBack.getVisibility() == View.VISIBLE) {
                        return true;
                    }

                    return false;
                }

                @Override
                public void setBackChatVisibility(boolean visibility) {

                    if (true) {
                        if (frameFragmentBack != null) {
                            frameFragmentBack.setVisibility(View.VISIBLE);
                        }
                    }
                }
            };


        } else {
            frameChatContainer.setVisibility(View.GONE);
        }

        isOpenChatBeforeSheare = false;
        checkIntent(getIntent());


        initTabStrip();

        initFloatingButtonCreateNew();

        arcMenu.setBackgroundTintColor();

        btnStartNewChat.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));
        btnCreateNewGroup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));
        btnCreateNewChannel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));

        final G application = (G) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RoomList");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mediaLayout = (LinearLayout) findViewById(R.id.amr_ll_music_layout);

        MusicPlayer.setMusicPlayer(mediaLayout);
        MusicPlayer.mainLayout = mediaLayout;

        ActivityCall.stripLayoutMain = findViewById(R.id.am_ll_strip_call);


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

        initComponent();

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        boolean isGetContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT, false);
        /**
         * just do this action once
         */
        if (!isGetContactList) {
            try {
                HelperPermission.getContactPermision(ActivityMain.this, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        if (!G.isSendContact) {
                            G.isSendContact = true;
                            LoginActions.importContact();
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SHP_SETTING.KEY_GET_CONTACT, true);
                        editor.apply();
                    }

                    @Override
                    public void deny() {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SHP_SETTING.KEY_GET_CONTACT, true);
                        editor.apply();

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
                            new HelperFragment(fragmentNewGroup).setStateLoss(true).load();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                        lockNavigation();
                    }
                });
            }
        };

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);


        connectionState();

        initDrawerMenu();

        verifyAccount();

        checkKeepMedia();


        G.onVerifyNewDevice = new OnVerifyNewDevice() {
            @Override
            public void verifyNewDevice(String appName, int appId, int appBuildVersion, String appVersion, ProtoGlobal.Platform platform, String platformVersion, ProtoGlobal.Device device, String deviceName, boolean twoStepVerification) {

                final String content = "" + "App name: " + appName + "\n" + "Build version: " + appBuildVersion + "\n" + "App version: " + appVersion + "\n" + "Platform: " + platform + "\n" + "Platform version: " + platformVersion + "\n" + "Device: " + device + "\n" + "Device name: " + deviceName;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (HelperCalander.isPersianUnicode) {
                            new MaterialDialog.Builder(ActivityMain.this).title(R.string.Input_device_specification).contentGravity(GravityEnum.END).content(content).positiveText(R.string.B_ok).show();
                        } else {
                            new MaterialDialog.Builder(ActivityMain.this).title(R.string.Input_device_specification).contentGravity(GravityEnum.START).content(content).positiveText(R.string.B_ok).show();
                        }
                    }
                });
            }

            @Override
            public void errorVerifyNewDevice(final int majorCode, final int minCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
    }

    private void checkKeepMedia() {

        final int keepMedia = sharedPreferences.getInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0);
        if (keepMedia != 0 && G.isCalculatKeepMedia) {// if Was selected keep media at 1week
            G.isCalculatKeepMedia = false;
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    long last;
                    long currentTime = G.currentTime;
                    long saveTime = sharedPreferences.getLong(SHP_SETTING.KEY_KEEP_MEDIA_TIME, -1);
                    if (saveTime == -1) {
                        last = keepMedia;
                    } else {
                        long days = (long) keepMedia * 1000L * 60 * 60 * 24;

                        long b = currentTime - saveTime;
                        last = b / days;
                    }

                    if (last >= keepMedia) {
                        new HelperCalculateKeepMedia().calculateTime();
                    }
                }
            }, 5000);
        }
    }


    //*******************************************************************************************************************************************

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (G.twoPaneMode) {

            boolean beforeState = G.isLandscape;

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                G.isLandscape = true;
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                G.isLandscape = false;
            }

            if (beforeState != G.isLandscape) {
                desighnLayout(chatLayoutMode.none);
            }


        }

        super.onConfigurationChanged(newConfig);
    }

    //*******************************************************************************************************************************************

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
                    //getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                    //    R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).replace(R.id.fragmentContainer, fragment, "register_contact_fragment").commit();

                    new HelperFragment(fragment).load();

                } catch (Exception e) {
                    e.getStackTrace();
                }
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }

                lockNavigation();
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
                    new HelperFragment(fragment).load();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();

                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
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
                    new HelperFragment(fragment).load();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
            }
        });

        arcMenu.fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mViewPager == null || mViewPager.getAdapter() == null) {
                    return;
                }

                try {

                    FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
                    if (adapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentMain) {

                        FragmentMain fm = (FragmentMain) adapter.getItem(mViewPager.getCurrentItem());
                        switch (fm.mainType) {

                            case all:
                                arcMenu.toggleMenu();
                                break;
                            case chat:
                                btnStartNewChat.performClick();
                                break;
                            case group:
                                btnCreateNewGroup.performClick();
                                break;
                            case channel:
                                btnCreateNewChannel.performClick();
                                break;
                        }
                    } else if (adapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentCall) {

                        ((FragmentCall) adapter.getItem(mViewPager.getCurrentItem())).showContactListForCall();
                    }
                } catch (Exception e) {
                    HelperLog.setErrorLog(" Activity main   arcMenu.fabMenu.setOnClickListener   " + e.toString());
                }
            }
        });
    }

    private void onSelectItem(int position) {
        FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();

        if (adapter.getItem(position) instanceof FragmentMain) {

            findViewById(R.id.amr_ripple_search).setVisibility(View.VISIBLE);
            findViewById(R.id.am_btn_menu).setVisibility(View.GONE);
            setFabIcon(R.mipmap.plus);
        } else if (adapter.getItem(position) instanceof FragmentCall) {

            findViewById(R.id.amr_ripple_search).setVisibility(View.GONE);
            findViewById(R.id.am_btn_menu).setVisibility(View.VISIBLE);
            setFabIcon(R.drawable.ic_call_black_24dp);
        }

        if (arcMenu.isMenuOpened()) {
            arcMenu.toggleMenu();
        }

        arcMenu.fabMenu.show();
    }

    private void setFabIcon(int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arcMenu.fabMenu.setImageDrawable(getResources().getDrawable(res, context.getTheme()));
        } else {
            arcMenu.fabMenu.setImageDrawable(getResources().getDrawable(res));
        }
    }

    private void setViewPagerSelectedItem() {
        if (!G.multiTab) {
            return;
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mViewPager == null || mViewPager.getAdapter() == null || mViewPager.getAdapter().getCount() == 0) {
                    return;
                }

                int index;

                if (G.selectedTabInMainActivity.length() > 0) {

                    if (HelperCalander.isPersianUnicode) {

                        if (G.selectedTabInMainActivity.equals(FragmentMain.MainType.all.toString())) {
                            index = 4;
                        } else if (G.selectedTabInMainActivity.equals(FragmentMain.MainType.chat.toString())) {
                            index = 3;
                        } else if (G.selectedTabInMainActivity.equals(FragmentMain.MainType.group.toString())) {
                            index = 2;
                        } else if (G.selectedTabInMainActivity.equals(FragmentMain.MainType.channel.toString())) {
                            index = 1;
                        } else {
                            index = 0;
                        }

                    } else {

                        if (G.selectedTabInMainActivity.equals(FragmentMain.MainType.all.toString())) {
                            index = 0;
                        } else if (G.selectedTabInMainActivity.equals(FragmentMain.MainType.chat.toString())) {
                            index = 1;
                        } else if (G.selectedTabInMainActivity.equals(FragmentMain.MainType.group.toString())) {
                            index = 2;
                        } else if (G.selectedTabInMainActivity.equals(FragmentMain.MainType.channel.toString())) {
                            index = 3;
                        } else {
                            index = 4;
                        }
                    }

                    G.selectedTabInMainActivity = "";


                } else {

                    if (HelperCalander.isPersianUnicode) {
                        index = 4;
                    } else {
                        index = 0;
                    }
                }

                navigationTabStrip.setViewPager(mViewPager, index);
                if (!HelperCalander.isPersianUnicode) {
                    navigationTabStrip.updatePointIndicator();
                }

                navigationTabStrip.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
                    @Override
                    public void onStartTabSelected(String title, int index) {

                    }

                    @Override
                    public void onEndTabSelected(String title, int index) {
                        onSelectItem(index);
                    }
                });

                if (G.onUnreadChange != null) {
                    G.onUnreadChange.onChange();
                }

            }
        }, 100);
    }

    private void initTabStrip() {

        navigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts);
        navigationTabStrip.setBackgroundColor(Color.parseColor(G.appBarColor));

        if (HelperCalander.isPersianUnicode) {
            navigationTabStrip.setTitles(getString(R.string.md_phone), getString(R.string.md_channel_icon), getString(R.string.md_users_social_symbol), getString(R.string.md_user_account_box), getString(R.string.md_apps));
        } else {
            navigationTabStrip.setTitles(getString(R.string.md_apps), getString(R.string.md_user_account_box), getString(R.string.md_users_social_symbol), getString(R.string.md_channel_icon), getString(R.string.md_phone));
        }

        navigationTabStrip.setTitleSize(getResources().getDimension(R.dimen.dp20));
        navigationTabStrip.setStripColor(Color.WHITE);

        mViewPager = findViewById(R.id.viewpager);

        if (G.multiTab) {
            navigationTabStrip.setVisibility(View.VISIBLE);
            mViewPager.setOffscreenPageLimit(5);
        } else {
            navigationTabStrip.setVisibility(View.GONE);
            mViewPager.setOffscreenPageLimit(1);
        }

        findViewById(R.id.loadingContent).setVisibility(View.VISIBLE);

        if (HelperCalander.isPersianUnicode) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (G.multiTab) {
                        fragmentCall = FragmentCall.newInstance(true);
                        pages.add(fragmentCall);

                        pages.add(FragmentMain.newInstance(FragmentMain.MainType.channel));
                        pages.add(FragmentMain.newInstance(FragmentMain.MainType.group));
                        pages.add(FragmentMain.newInstance(FragmentMain.MainType.chat));
                    }

                    pages.add(FragmentMain.newInstance(FragmentMain.MainType.all));
                    sampleFragmentPagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
                    mViewPager.setAdapter(sampleFragmentPagerAdapter);
                    setViewPagerSelectedItem();
                    findViewById(R.id.loadingContent).setVisibility(View.GONE);
                }
            }, 400);
        } else {

            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pages.add(FragmentMain.newInstance(FragmentMain.MainType.all));

                    sampleFragmentPagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
                    mViewPager.setAdapter(sampleFragmentPagerAdapter);

                    findViewById(R.id.loadingContent).setVisibility(View.GONE);

                }
            }, 400);

            if (G.multiTab) {
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pages.add(FragmentMain.newInstance(FragmentMain.MainType.chat));
                        pages.add(FragmentMain.newInstance(FragmentMain.MainType.group));
                        pages.add(FragmentMain.newInstance(FragmentMain.MainType.channel));

                        fragmentCall = FragmentCall.newInstance(true);
                        pages.add(fragmentCall);

                        mViewPager.getAdapter().notifyDataSetChanged();

                        setViewPagerSelectedItem();

                    }
                }, 800);
            }
        }

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) findViewById(R.id.am_btn_menu);

        txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragmentCall.openDialogMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (HelperCalander.isPersianUnicode) {
            ViewMaker.setLayoutDirection(mViewPager, View.LAYOUT_DIRECTION_RTL);
        }
    }

    //******************************************************************************************************************************

    /**
     * send client condition
     */

    @Override
    protected void onStart() {
        super.onStart();

        if (!G.isFirstPassCode) {
            openActivityPassCode();
        }
        G.isFirstPassCode = false;

        //RealmRoomMessage.fetchNotDeliveredMessages(new OnActivityMainStart() {
        //    @Override
        //    public void sendDeliveredStatus(RealmRoom room, RealmRoomMessage message) {
        //        G.chatUpdateStatusUtil.sendUpdateStatus(room.getType(), message.getRoomId(), message.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
        //    }
        //});
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    public void openActivityPassCode() {
        if (!isActivityEnterPassCode && G.isPassCode && isLock && !G.isRestartActivity) {
            enterPassword();
        } else if (!isActivityEnterPassCode && !G.isRestartActivity) {
            currentTime = System.currentTimeMillis();
            SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            long timeLock = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
            long calculatorTimeLock = currentTime - oldTime;

            if (timeLock > 0 && calculatorTimeLock > (timeLock * 1000)) {
                enterPassword();
            }
        }

        G.isRestartActivity = false;
    }

    /**
     * init  menu drawer
     */

    private void initDrawerMenu() {

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here

                //if (arcMenu.isMenuOpened()) {
                //    arcMenu.toggleMenu();
                //}

            }
        };

        final ViewGroup drawerButton = (ViewGroup) findViewById(R.id.amr_ripple_menu);
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

        setDrawerInfo(true);

        findViewById(R.id.lm_layout_header).setBackgroundColor(Color.parseColor(G.appBarColor));

        final ViewGroup navBackGround = (ViewGroup) findViewById(R.id.lm_layout_user_picture);
        navBackGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatGetRoom(userId);
                closeDrawer();
                //drawer.closeDrawer(GravityCompat.START);
                //pageDrawer = 1;
            }
        });

        TextView txtCloud = (TextView) findViewById(R.id.lm_txt_cloud);
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
                final Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "New Chat");
                fragment.setArguments(bundle);
                try {
                    new HelperFragment(fragment).load();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();
                closeDrawer();
            }
        });

        ViewGroup itemNavGroup = (ViewGroup) findViewById(R.id.lm_ll_new_group);
        itemNavGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewGroup");
                fragment.setArguments(bundle);
                try {
                    new HelperFragment(fragment).load();

                } catch (Exception e) {
                    e.getStackTrace();
                }

                lockNavigation();
                closeDrawer();
            }
        });

        ViewGroup itemNavChanel = (ViewGroup) findViewById(R.id.lm_ll_new_channle);
        itemNavChanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewChanel");
                fragment.setArguments(bundle);
                try {
                    new HelperFragment(fragment).load();
                } catch (Exception e) {
                    e.getStackTrace();
                }

                lockNavigation();
                closeDrawer();
            }
        });

        ViewGroup igapSearch = (ViewGroup) findViewById(R.id.lm_ll_igap_search);
        igapSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Fragment fragment = FragmentIgapSearch.newInstance();
                try {
                    new HelperFragment(fragment).load();
                } catch (Exception e) {
                    e.getStackTrace();
                }

                lockNavigation();
                closeDrawer();
            }
        });

        ViewGroup itemNavContacts = (ViewGroup) findViewById(R.id.lm_ll_contacts);
        itemNavContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "Contacts");
                fragment.setArguments(bundle);
                try {
                    new HelperFragment(fragment).load();
                } catch (Exception e) {
                    e.getStackTrace();
                }

                lockNavigation();
                closeDrawer();
            }
        });

        ViewGroup itemNavCall = (ViewGroup) findViewById(R.id.lm_ll_call);

        // gone or visible view call
        RealmCallConfig callConfig = getRealm().where(RealmCallConfig.class).findFirst();
        if (callConfig != null) {
            if (callConfig.isVoice_calling()) {
                itemNavCall.setVisibility(View.VISIBLE);

                itemNavCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Fragment fragment = FragmentCall.newInstance(false);
                        try {
                            new HelperFragment(fragment).load();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                        lockNavigation();
                        closeDrawer();
                    }
                });
            } else {
                itemNavCall.setVisibility(View.GONE);
            }
        } else {
            new RequestSignalingGetConfiguration().signalingGetConfiguration();
        }


        ViewGroup itemNavMap = (ViewGroup) findViewById(R.id.lm_ll_map);
        itemNavMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapFragment();
            }
        });

        ViewGroup itemNavSend = (ViewGroup) findViewById(R.id.lm_ll_invite_friends);
        itemNavSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Join iGap : https://www.igap.net I'm waiting for you!");
                sendIntent.setType("text/plain");
                Intent openInChooser = Intent.createChooser(sendIntent, "Open in...");
                startActivity(openInChooser);
                closeDrawer();
            }
        });
        ViewGroup itemNavSetting = (ViewGroup) findViewById(R.id.lm_ll_setting);
        itemNavSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(new FragmentSetting()).load();
                closeDrawer();
                lockNavigation();
            }
        });
        ViewGroup itemQrCode = (ViewGroup) findViewById(R.id.lm_ll_qrCode);

        itemQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HelperPermission.getCameraPermission(ActivityMain.this, new OnGetPermission() {
                        @Override
                        public void Allow() throws IOException, IllegalStateException {
                            new HelperFragment(FragmentQrCodeNewDevice.newInstance()).setStateLoss(true).load();
                        }

                        @Override
                        public void deny() {
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                lockNavigation();
                closeDrawer();
            }
        });
        final ToggleButton toggleButton = findViewById(R.id.st_txt_st_toggle_theme_dark);
        ViewGroup rootDarkTheme = (ViewGroup) findViewById(R.id.lt_txt_st_theme_dark);
        rootDarkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton.performClick();
            }
        });
        boolean checkedThemeDark = sharedPreferences.getBoolean(SHP_SETTING.KEY_THEME_DARK, false);

        toggleButton.setChecked(checkedThemeDark);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (toggleButton.isChecked()) {
                    FragmentSettingViewModel.setDarkTheme(editor);
                } else {
                    FragmentSettingViewModel.setLightTheme(editor);
                }
            }
        });

        ViewGroup itemNavOut = (ViewGroup) findViewById(R.id.lm_ll_igap_faq);
        itemNavOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                                    }

                                    @Override
                                    public void onTimeOut() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                HelperError.showSnackMessage(getResources().getString(R.string.error), false);

                                            }
                                        });
                                    }
                                };
                                new RequestUserSessionLogout().userSessionLogout();
                            }
                        })
                        .show();
                closeDrawer();
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

                /*switch (pageDrawer) {
                    case 1:
                        chatGetRoom(userId);
                        pageDrawer = 0;
                        break;
                    case 2: {
                        final Fragment fragment = RegisteredContactsFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TITLE", "New Chat");
                        fragment.setArguments(bundle);
                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                        pageDrawer = 0;
                        break;
                    }
                    case 3: {
                        FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TYPE", "NewGroup");
                        fragment.setArguments(bundle);
                        try {
                              HelperFragment.loadFragment(getSupportFragmentManager(),fragment);
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                        pageDrawer = 0;
                        break;
                    }
                    case 4: {
                        FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TYPE", "NewChanel");
                        fragment.setArguments(bundle);
                        try {
                            HelperFragment.loadFragment(getSupportFragmentManager(),fragment);
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        pageDrawer = 0;
                        break;
                    }
                    case 5: {
                        final Fragment fragment = FragmentIgapSearch.newInstance();
                        try {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "Search_fragment_igap").commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        pageDrawer = 0;
                        break;
                    }
                    case 6: {
                        Fragment fragment = RegisteredContactsFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TITLE", "Contacts");
                        fragment.setArguments(bundle);
                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        pageDrawer = 0;
                        break;
                    }
                    case 7: {
                        Fragment fragment = FragmentCall.newInstance(false);
                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        pageDrawer = 0;
                        break;
                    }
                    case 8: {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Join iGap : https://www.igap.net/ I'm waiting for you !");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                        pageDrawer = 0;
                        break;
                    }
                    case 9: {
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

                        pageDrawer = 0;
                        break;
                    }
                    case 10: {


                        try {
                            HelperPermision.getCameraPermission(ActivityMain.this, new OnGetPermission() {
                                @Override
                                public void Allow() throws IOException {
                                    startActivity(new Intent(ActivityMain.this, ActivityQrCodeNewDevice.class));
                                }

                                @Override
                                public void deny() {
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        pageDrawer = 0;
                    }
                    break;

                    case 11: {
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
                                })
                                .show();
                        pageDrawer = 0;
                    }
                    break;
                }*/
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void closeDrawer() {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (drawer != null) drawer.closeDrawer(GravityCompat.START);
            }
        }, 1000);
    }

    private void openMapFragment() {
        try {
            HelperPermission.getLocationPermission(ActivityMain.this, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {
                    try {
                        if (!waitingForConfiguration) {
                            waitingForConfiguration = true;
                            if (mapUrls == null || mapUrls.isEmpty() || mapUrls.size() == 0) {
                                G.onGeoGetConfiguration = new OnGeoGetConfiguration() {
                                    @Override
                                    public void onGetConfiguration() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                G.handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        waitingForConfiguration = false;
                                                    }
                                                }, 2000);
                                                new HelperFragment(FragmentiGapMap.getInstance()).load();
                                            }
                                        });
                                    }

                                    @Override
                                    public void getConfigurationTimeOut() {
                                        G.handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                waitingForConfiguration = false;
                                            }
                                        }, 2000);
                                    }
                                };
                                new RequestGeoGetConfiguration().getConfiguration();
                            } else {
                                G.handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        waitingForConfiguration = false;
                                    }
                                }, 2000);
                                new HelperFragment(FragmentiGapMap.getInstance()).load();
                            }
                        }

                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lockNavigation();
                        }
                    });
                }

                @Override
                public void deny() {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeDrawer();
    }

    private void initComponent() {


        iconLock = (TextView) findViewById(R.id.am_btn_lock);

        iconLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLock) {
                    iconLock.setText(getResources().getString(R.string.md_igap_lock_open_outline));
                    isLock = false;
                } else {
                    iconLock.setText(getResources().getString(R.string.md_igap_lock));
                    isLock = true;
                }

            }
        });


        contentLoading = (ProgressBar) findViewById(R.id.loadingContent);
        iconLocation = (TextView) findViewById(R.id.am_btn_location);

        SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isRegisterStatus = sharedPreferences.getBoolean(SHP_SETTING.REGISTER_STATUS, false);
        if (isRegisterStatus) {
            startAnimationLocation();
        } else {
            stopAnimationLocation();
        }

        iconLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapFragment();
            }
        });
        G.onMapRegisterState = new OnMapRegisterState() {
            @Override
            public void onState(final boolean state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state) {
                            startAnimationLocation();
                            editor.putBoolean(SHP_SETTING.REGISTER_STATUS, true);
                            editor.apply();
                        } else {
                            stopAnimationLocation();
                            editor.putBoolean(SHP_SETTING.REGISTER_STATUS, false);
                            editor.apply();
                        }
                    }
                });
            }
        };

        RippleView rippleSearch = (RippleView) findViewById(R.id.amr_ripple_search);
        rippleSearch.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Fragment fragment = SearchFragment.newInstance();

                try {
                    //  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "Search_fragment").commit();
                    new HelperFragment(fragment).load();


                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();
            }
        });

        if (!HelperCalander.isPersianUnicode) {
            titleTypeface = G.typeface_neuropolitical;
        } else {
            titleTypeface = G.typeface_IRANSansMobile;
        }
    }

    public void stopAnimationLocation() {
        if (iconLocation != null) {
            iconLocation.setVisibility(View.GONE);
        }
    }

    public void startAnimationLocation() {
        if (iconLocation != null) {
            iconLocation.setVisibility(View.VISIBLE);
        }
    }

    private void connectionState() {
        final TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);

        Typeface typeface = null;
        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
            typeface = titleTypeface;
        }
        if (G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
            txtIgap.setText(R.string.waiting_for_network);
            txtIgap.setTypeface(typeface, Typeface.BOLD);
        } else if (G.connectionState == ConnectionState.CONNECTING) {
            txtIgap.setText(R.string.connecting);
            txtIgap.setTypeface(typeface, Typeface.BOLD);
        } else if (G.connectionState == ConnectionState.UPDATING) {
            txtIgap.setText(updating);
            txtIgap.setTypeface(typeface, Typeface.BOLD);
        } else {
            txtIgap.setText(R.string.app_name);
            txtIgap.setTypeface(titleTypeface, Typeface.BOLD);
        }

        G.onConnectionChangeState = new OnConnectionChangeState() {
            @Override
            public void onChangeState(final ConnectionState connectionStateR) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Typeface typeface = null;
                        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                            typeface = titleTypeface;
                        }
                        G.connectionState = connectionStateR;
                        if (connectionStateR == ConnectionState.WAITING_FOR_NETWORK) {
                            txtIgap.setText(R.string.waiting_for_network);
                            txtIgap.setTypeface(typeface, Typeface.BOLD);
                        } else if (connectionStateR == ConnectionState.CONNECTING) {
                            txtIgap.setText(R.string.connecting);
                            txtIgap.setTypeface(typeface, Typeface.BOLD);
                        } else if (connectionStateR == ConnectionState.UPDATING) {
                            txtIgap.setText(R.string.updating);
                            txtIgap.setTypeface(titleTypeface, Typeface.BOLD);
                        } else if (connectionStateR == ConnectionState.IGAP) {
                            txtIgap.setText(R.string.app_name);
                            txtIgap.setTypeface(titleTypeface, Typeface.BOLD);
                        } else {
                            txtIgap.setTypeface(typeface, Typeface.BOLD);
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
                        Typeface typeface = null;
                        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                            typeface = titleTypeface;
                        }
                        G.connectionState = ConnectionState.UPDATING;
                        txtIgap.setText(R.string.updating);
                        txtIgap.setTypeface(typeface, Typeface.BOLD);
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

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //mLeftDrawerLayout.toggle();
        return false;
    }

    /**
     * set drawer info
     *
     * @param updateFromServer if is set true send request to sever for get own info
     */
    private void setDrawerInfo(boolean updateFromServer) {
        RealmUserInfo realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            String username = realmUserInfo.getUserInfo().getDisplayName();
            phoneNumber = realmUserInfo.getUserInfo().getPhoneNumber();
            imgNavImage = (ImageView) findViewById(R.id.lm_imv_user_picture);
            EmojiTextViewE txtNavName = (EmojiTextViewE) findViewById(R.id.lm_txt_user_name);
            TextView txtNavPhone = (TextView) findViewById(R.id.lm_txt_phone_number);
            txtNavName.setText(username);
            txtNavPhone.setText(phoneNumber);

            if (HelperCalander.isPersianUnicode) {
                txtNavPhone.setText(HelperCalander.convertToUnicodeFarsiNumber(txtNavPhone.getText().toString()));
                txtNavName.setText(HelperCalander.convertToUnicodeFarsiNumber(txtNavName.getText().toString()));
            }
            if (updateFromServer) {
                //getUserInfo(realmUserInfo);
            }
            setImage();
        }
    }

    private void getUserInfo(final RealmUserInfo realmUserInfo) {
        if (!realmUserInfo.isValid()) {
            return;
        }
        if (G.userLogin) {
            new RequestUserInfo().userInfo(realmUserInfo.getUserId());
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getUserInfo(realmUserInfo);
                }
            }, 1000);
        }
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener, boolean isDisable) {
        if (!isDisable) {
            ActivityMain.onBackPressedListener = onBackPressedListener;
        } else {
            ActivityMain.onBackPressedListener = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (G.dispatchTochEventChat != null) {
            G.dispatchTochEventChat.getToch(ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {


        if (G.onBackPressedExplorer != null) {
            if (G.onBackPressedExplorer.onBack()) {
                return;
            }
        } else if (G.onBackPressedChat != null) {
            if (G.onBackPressedChat.onBack()) {
                return;
            }
        }


        if (onBackPressedListener != null) {
            onBackPressedListener.doBack();
        }

        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {

            openNavigation();

            // this call for create group   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            super.onBackPressed();

            if (G.fragmentManager != null && G.fragmentManager.getBackStackEntryCount() < 1) {
                if (!this.isFinishing()) {
                    resume();
                }
            }

            desighnLayout(chatLayoutMode.none);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        resume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void resume() {
        /**
         * after change language in ActivitySetting this part refresh Activity main
         */
        G.onRefreshActivity = new OnRefreshActivity() {
            @Override
            public void refresh(String changeLanguag) {

                G.isUpdateNotificaionColorMain = false;
                G.isUpdateNotificaionColorChannel = false;
                G.isUpdateNotificaionColorGroup = false;
                G.isUpdateNotificaionColorChat = false;
                G.isUpdateNotificaionCall = false;

                new HelperFragment().removeAll(false);

                ActivityMain.this.recreate();

            }
        };

        desighnLayout(chatLayoutMode.none);


        if (contentLoading != null) {
            AppUtils.setProgresColler(contentLoading);
        }

        if (G.isInCall) {
            findViewById(R.id.am_ll_strip_call).setVisibility(View.VISIBLE);

            ActivityCallViewModel.txtTimerMain = (TextView) findViewById(R.id.cslcs_txt_timer);

            TextView txtCallActivityBack = (TextView) findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActivityMain.this, ActivityCall.class));
                }
            });

            G.iCallFinishMain = new ICallFinish() {
                @Override
                public void onFinish() {
                    try {

                        findViewById(R.id.am_ll_strip_call).setVisibility(View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        } else {
            findViewById(R.id.am_ll_strip_call).setVisibility(View.GONE);
        }

        if (drawer != null) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            drawer.closeDrawer(GravityCompat.START);
        }

        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));


        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponseRoomList(this);
        G.onClientCondition = this;
        G.onClientGetRoomListResponse = this;
        G.onUserInfoMyClient = this;
        G.onMapRegisterStateMain = this;
        G.onUnreadChange = this;

        startService(new Intent(this, ServiceContact.class));

        HelperUrl.getLinkinfo(getIntent(), ActivityMain.this);
        getIntent().setData(null);
        setDrawerInfo(false);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        ActivityMain.setMediaLayout();

        if (G.isPassCode) {
            iconLock.setVisibility(View.VISIBLE);

            if (isLock) {
                iconLock.setText(getResources().getString(R.string.md_igap_lock));
            } else {
                iconLock.setText(getResources().getString(R.string.md_igap_lock_open_outline));
            }
        } else {
            iconLock.setVisibility(View.GONE);
        }

    }

    private void enterPassword() {

        closeDrawer();
        Intent intent = new Intent(ActivityMain.this, ActivityEnterPassCode.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isNeedToRegister) {
            return;
        }

        HelperNotificationAndBadge.updateBadgeOnly(getRealm(), -1);

        G.onUnreadChange = null;

        if (mViewPager != null && mViewPager.getAdapter() != null) {

            try {

                FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();

                if (adapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentMain) {

                    FragmentMain fm = (FragmentMain) adapter.getItem(mViewPager.getCurrentItem());
                    G.selectedTabInMainActivity = fm.mainType.toString();
                } else if (adapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentCall) {

                    G.selectedTabInMainActivity = adapter.getItem(mViewPager.getCurrentItem()).getClass().getName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onChatClearMessage(final long roomId, long clearId) {
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
    public void onStateMain(boolean state) {
        if (state) {
            startAnimationLocation();
        } else {
            stopAnimationLocation();
        }
    }

    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {

    }

    @Override
    public void onAvatarAddError() {

    }
    //@Override
    //public void onSetAction(final long roomId, final long userId, final ProtoGlobal.ClientAction clientAction) {
    //    //+Realm realm = Realm.getDefaultInstance();
    //    getRealm().executeTransactionAsync(new Realm.Transaction() {
    //        @Override
    //        public void execute(Realm realm) {
    //            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
    //            if (realmRoom != null && realmRoom.isValid() && !realmRoom.isDeleted() && realmRoom.getType() != null) {
    //                String action = HelperGetAction.getAction(roomId, realmRoom.getType(), clientAction);
    //                realmRoom.setActionState(action, userId);
    //            }
    //        }
    //    });
    //    //realm.close();
    //}

    //******* GroupAvatar and ChannelAvatar

    public void setImage() {
        HelperAvatar.getAvatar(G.userId, HelperAvatar.AvatarType.USER, true, new OnAvatarGet() {
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
                            //Realm realm1 = Realm.getDefaultInstance();
                            RealmUserInfo realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();
                            imgNavImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgNavImage.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));
                            //realm1.close();
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
    }

    @Override
    public void onUserInfoMyClient() {
        setImage();
    }

    private void chatGetRoom(final long peerId) {
        //final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();

        if (realmRoom != null) {

            new GoToChatActivity(realmRoom.getId()).startActivity();

        } else {

            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(ProtoGlobal.Room room) {

                    new GoToChatActivity(room.getId()).setPeerID(peerId).startActivity();

                    G.onChatGetRoom = null;
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
        //realm.close();
    }

    @Override
    public void onMessageUpdate(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        //empty
    }

    @Override
    public void onMessageReceive(final long roomId, final String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType) {

        //Realm realm = Realm.getDefaultInstance();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        final RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();
                        if (room != null && realmRoomMessage != null) {
                            /**
                             * client checked  (room.getUnreadCount() <= 1)  because in HelperMessageResponse unreadCount++
                             */
                            if (room.getUnreadCount() <= 1) {
                                realmRoomMessage.setFutureMessageId(realmRoomMessage.getMessageId());
                                room.setFirstUnreadMessage(realmRoomMessage);
                            }
                        }
                    }
                });
                //realm.close();

                switch (roomType) {

                    case CHAT:
                        if (mainActionChat != null) {
                            mainActionChat.onAction(MainAction.downScrool);
                        }
                        break;
                    case GROUP:
                        if (mainActionGroup != null) {
                            mainActionGroup.onAction(MainAction.downScrool);
                        }
                        break;
                    case CHANNEL:
                        if (mainActionChannel != null) {
                            mainActionChannel.onAction(MainAction.downScrool);
                        }
                        break;
                }

                if (mainActionApp != null) {
                    mainActionApp.onAction(MainAction.downScrool);
                }

                /**
                 * don't send update status for own message
                 */
                if (roomMessage.getAuthor().getUser() != null && roomMessage.getAuthor().getUser().getUserId() != userId) {
                    // user has received the message, so I make a new delivered update status request
                    if (roomType == ProtoGlobal.Room.Type.CHAT) {
                        G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                    } else if (roomType == ProtoGlobal.Room.Type.GROUP && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
                        G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                    }
                }
            }
        });
    }

    //*****************************************************************************************************************************

    @Override
    public void onMessageFailed(final long roomId, RealmRoomMessage roomMessage) {
        //empty
    }

    //************************
    @Override
    public void onClientCondition() {

        notifySubFragmentForCondition();
    }

    @Override
    public void onClientConditionError() {
        notifySubFragmentForCondition();
    }

    private void notifySubFragmentForCondition() {

        if (mainActionApp != null) {
            mainActionApp.onAction(MainAction.clinetCondition);
        }

        if (mainActionChat != null) {
            mainActionChat.onAction(MainAction.clinetCondition);
        }

        if (mainActionGroup != null) {
            mainActionGroup.onAction(MainAction.clinetCondition);
        }

        if (mainActionChannel != null) {
            mainActionChannel.onAction(MainAction.clinetCondition);
        }
    }

    @Override
    public void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, String identity) {

        if (mainInterfaceGetRoomList != null) {
            mainInterfaceGetRoomList.onClientGetRoomList(roomList, response, identity);
        }
    }

    @Override
    public void onError(int majorCode, int minorCode) {

        if (mainInterfaceGetRoomList != null) {
            mainInterfaceGetRoomList.onError(majorCode, minorCode);
        }
    }

    //************************

    @Override
    public void onTimeout() {

        if (mainInterfaceGetRoomList != null) {
            mainInterfaceGetRoomList.onTimeout();
        }
    }

    public void lockNavigation() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void openNavigation() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    //*************************************************************

    public void verifyAccount() {
        boolean bereitsAngelegt = false;
        String accountType;
        accountType = this.getPackageName();

        AccountManager accountManager = AccountManager.get(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Account[] accounts = accountManager.getAccounts();
        for (int i = 0; i < accounts.length; i++) {
            if ((accounts[i].type != null) && (accounts[i].type.contentEquals(accountType))) {
                bereitsAngelegt = true;
            }
        }

        if (!bereitsAngelegt) {
            AccountManager accMgr = AccountManager.get(this);
            String password = "";

            final Account account = new Account("" + phoneNumber, accountType);
            try {
                accMgr.addAccountExplicitly(account, password, null);
            } catch (Exception e1) {
                e1.getMessage();
            }
        }
    } // end of

    public void desighnLayout(final chatLayoutMode mode) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (G.twoPaneMode) {
                    if (frameFragmentContainer != null) {
                        if (frameFragmentContainer.getChildCount() == 0) {
                            if (frameFragmentBack != null) {
                                frameFragmentBack.setVisibility(View.GONE);
                            }
                        } else if (frameFragmentContainer.getChildCount() == 1) {
                            disableSwipe = true;
                        } else {
                            disableSwipe = false;
                        }
                    } else {
                        if (frameFragmentBack != null) {
                            frameFragmentBack.setVisibility(View.GONE);
                        }
                    }

                    if (G.isLandscape) {
                        setWeight(frameChatContainer, 2);
                        setWeight(frameMainContainer, 1);
                        openNavigation();
                    } else {

                        if (mode == chatLayoutMode.show) {
                            setWeight(frameChatContainer, 1);
                            setWeight(frameMainContainer, 0);
                            lockNavigation();
                        } else if (mode == chatLayoutMode.hide) {
                            setWeight(frameChatContainer, 0);
                            setWeight(frameMainContainer, 1);
                            openNavigation();
                        } else {
                            if (frameChatContainer.getChildCount() > 0) {
                                setWeight(frameChatContainer, 1);
                                setWeight(frameMainContainer, 0);
                                lockNavigation();
                            } else {
                                setWeight(frameChatContainer, 0);
                                setWeight(frameMainContainer, 1);
                                openNavigation();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        oldTime = System.currentTimeMillis();
    }

    @Override
    public void onChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (navigationTabStrip != null && navigationTabStrip.getVisibility() == View.VISIBLE) {
                    navigationTabStrip.setTitleBadge(RealmRoom.getUnreadCountPages());
                }
            }
        });
    }

    public enum MainAction {
        downScrool, clinetCondition
    }

    public enum chatLayoutMode {
        none, show, hide
    }

    public interface MainInterface {
        void onAction(MainAction action);
    }

    public interface MainInterfaceGetRoomList {

        void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, String identity);

        void onError(int majorCode, int minorCode);

        void onTimeout();
    }

    public interface OnBackPressedListener {
        void doBack();
    }

    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return pages.get(i);
        }

        @Override
        public int getCount() {
            return pages.size();
        }
    }

}
