/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.iGap.activities.ActivityCustomError;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperNotificationAndBadge;
import net.iGap.interfaces.*;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.ChatUpdateStatusUtil;
import net.iGap.module.ClearMessagesUtil;
import net.iGap.module.MultiDexUtils;
import net.iGap.module.StartupActions;
import net.iGap.module.enums.ConnectionState;
import net.iGap.proto.ProtoClientCondition;
import net.iGap.request.RequestWrapper;

import org.paygear.wallet.model.Card;
import org.paygear.wallet.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.spec.SecretKeySpec;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import io.fabric.sdk.android.Fabric;
import ir.radsense.raadcore.Raad;
import ir.radsense.raadcore.web.WebBase;

import static net.iGap.Config.DEFAULT_BOTH_CHAT_DELETE_TIME;

public class G extends MultiDexApplication {

    public static final String IGAP = "/iGap";
    public static final String IMAGES = "/iGap Images";
    public static final String VIDEOS = "/iGap Videos";
    public static final String AUDIOS = "/iGap Audios";
    public static final String MESSAGES = "/iGap Messages";
    //public static Realm mRealm;
    public static final String DOCUMENT = "/iGap Document";
    public static final String TEMP = "/.temp";
    public static final String CHAT_BACKGROUND = "/.chat_background";
    public static final String IMAGE_USER = "/.image_user";
    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static Context context;
    public static Handler handler;
    public static long mLastClickTime = SystemClock.elapsedRealtime();
    public static LayoutInflater inflater;
    public static HelperNotificationAndBadge helperNotificationAndBadge;
    public static ConcurrentHashMap<String, RequestWrapper> requestQueueMap = new ConcurrentHashMap<>();
    public static List<Long> smsNumbers = new ArrayList<>();
    public static AtomicBoolean pullRequestQueueRunned = new AtomicBoolean(false);
    public static SecretKeySpec symmetricKey;
    public static ProtoClientCondition.ClientCondition.Builder clientConditionGlobal;
    public static HelperCheckInternetConnection.ConnectivityType latestConnectivityType;
    public static ImageLoader imageLoader;
    public static ArrayList<String> unSecure = new ArrayList<>();
    public static ArrayList<String> unSecureResponseActionId = new ArrayList<>();
    public static ArrayList<String> unLogin = new ArrayList<>();// list of actionId that can be doing without secure
    public static ArrayList<String> waitingActionIds = new ArrayList<>();
    public static ArrayList<String> generalImmovableClasses = new ArrayList<>();
    public static ArrayList<Integer> forcePriorityActionId = new ArrayList<>();
    public static ArrayList<Integer> ignoreErrorCodes = new ArrayList<>();
    public static HashMap<Integer, String> lookupMap = new HashMap<>();
    public static HashMap<String, ArrayList<Object>> requestQueueRelationMap = new HashMap<>();
    public static HashMap<Integer, Integer> priorityActionId = new HashMap<>();
    public static Activity currentActivity;
    public static FragmentActivity fragmentActivity;
    public static String latestActivityName;
    public static File IMAGE_NEW_GROUP;
    public static File IMAGE_NEW_CHANEL;
    public static File imageFile;
    public static String DIR_SDCARD_EXTERNAL = "";
    public static String DIR_APP = DIR_SDCARD + IGAP;
    public static String DIR_IMAGES = DIR_APP + IMAGES;
    public static String DIR_VIDEOS = DIR_APP + VIDEOS;
    public static String DIR_AUDIOS = DIR_APP + AUDIOS;
    public static String DIR_DOCUMENT = DIR_APP + DOCUMENT;
    public static String DIR_MESSAGES = DIR_APP + MESSAGES;
    public static String DIR_TEMP = DIR_APP + TEMP;
    public static String DIR_CHAT_BACKGROUND = DIR_APP + CHAT_BACKGROUND;
    public static String DIR_IMAGE_USER = DIR_APP + IMAGE_USER;
    public static String CHAT_MESSAGE_TIME = "H:mm";
    public static String selectedLanguage = null;
    public static String symmetricMethod;
    public static String appBarColor; // default color
    public static String bubbleChatSend; // default color
    public static String bubbleChatReceive; // default color
    public static String fabBottom; // default color
    public static String bubbleChatMusic; // default color
    public static String textChatMusic;
    public static String notificationColor;
    public static String toggleButtonColor;
    public static String attachmentColor;
    public static String iconColorBottomSheet;
    public static String progressColor;
    public static String headerTextColor;
    public static String backgroundTheme;
    public static String backgroundTheme_2;
    public static String logLineTheme;
    public static String voteIconTheme;
    public static String textTitleTheme;
    public static String textBubble;
    public static String linkColor;
    public static String txtIconCheck;
    public static String textBubbleSend;
    public static String textSubTheme;
    public static String tintImage;
    public static String lineBorder;
    public static String menuBackgroundColor;
    public static String authorHash;
    public static String displayName;
    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;
    public static boolean isUserStatusOnline = false;
    public static boolean isSecure = false;
    public static boolean allowForConnect = true; //set allowForConnect to realm , if don't set client try for connect
    public static boolean userLogin = false;
    public static boolean socketConnection = false;
    public static boolean canRunReceiver = false;
    public static boolean firstTimeEnterToApp = true; // use this field for get room list
    public static boolean firstEnter = true;
    public static boolean isSaveToGallery = false;
    public static boolean hasNetworkBefore;
    public static boolean isSendContact = false;
    public static boolean latestMobileDataState;
    public static boolean showVoteChannelLayout = true;
    public static boolean showSenderNameInGroup = false;
    public static boolean needGetSignalingConfiguration = true;
    public static boolean isInCall = false;
    public static boolean isShowRatingDialog = false;
    public static boolean isUpdateNotificaionColorMain = false;
    public static boolean isUpdateNotificaionColorChannel = false;
    public static boolean isUpdateNotificaionColorGroup = false;
    public static boolean isUpdateNotificaionColorChat = false;
    public static boolean isUpdateNotificaionCall = false;
    public static boolean isCalculatKeepMedia = true;
    public static boolean twoPaneMode = false;
    public static boolean isLandscape = false;
    public static boolean isAppRtl = false;
    public static boolean isLinkClicked = false;
    public static boolean isMplActive = false;
    public static boolean isWalletActive = false;
    public static boolean isWalletRegister = false;
    public static boolean isDarkTheme = false;
    public static int themeColor;
    public static String selectedTabInMainActivity = "";
    public static int ivSize;
    public static int userTextSize = 0;
    public static int COPY_BUFFER_SIZE = 1024;
    public static int maxChatBox = 0;
    public static int bothChatDeleteTime = DEFAULT_BOTH_CHAT_DELETE_TIME;
    public static long currentTime;
    public static long userId;
    public static long latestHearBeatTime = System.currentTimeMillis();
    public static long currentServerTime;
    public static long latestResponse = System.currentTimeMillis();
    public static long serverHeartBeatTiming = 60 * 1000;
    public static ClearMessagesUtil clearMessagesUtil = new ClearMessagesUtil();
    public static ChatSendMessageUtil chatSendMessageUtil = new ChatSendMessageUtil();
    public static ChatUpdateStatusUtil chatUpdateStatusUtil = new ChatUpdateStatusUtil();
    public static ConnectionState connectionState;
    public static ConnectionState latestConnectionState;
    public static OnConnectionChangeState onConnectionChangeState;
    public static OnConnectionChangeStateChat onConnectionChangeStateChat;
    public static OnUpdating onUpdating;
    public static OnReceiveInfoLocation onReceiveInfoLocation;
    public static OnUserRegistration onUserRegistration;
    public static OnClientSearchRoomHistory onClientSearchRoomHistory;
    public static OnUserVerification onUserVerification;
    public static OnReceivePageInfoTOS onReceivePageInfoTOS;
    public static OnReceivePageInfoWalletAgreement onReceivePageInfoWalletAgreement;
    public static OnUserLogin onUserLogin;
    public static OnUserProfileSetEmailResponse onUserProfileSetEmailResponse;
    public static OnUserProfileSetGenderResponse onUserProfileSetGenderResponse;
    public static OnUserProfileSetNickNameResponse onUserProfileSetNickNameResponse;
    public static OnInfoCountryResponse onInfoCountryResponse;
    public static OnInfoTime onInfoTime;
    public static OnUserContactEdit onUserContactEdit;
    public static OnUserContactDelete onUserContactdelete;
    public static OnClientGetRoomListResponse onClientGetRoomListResponse;
    public static OnClientGetRoomResponse onClientGetRoomResponse;
    public static OnInquiry onInquiry;
    public static OnSecuring onSecuring;
    public static OnChatGetRoom onChatGetRoom;
    public static OnChatEditMessageResponse onChatEditMessageResponse;
    public static OnChatDeleteMessageResponse onChatDeleteMessageResponse;
    public static OnChatDelete onChatDelete;
    public static OnChatSendMessage onChatSendMessage;
    public static OnUserUsernameToId onUserUsernameToId;
    public static OnUserProfileGetNickname onUserProfileGetNickname;
    public static OnGroupCreate onGroupCreate;
    public static OnGroupAddMember onGroupAddMember;
    public static OnGroupAddAdmin onGroupAddAdmin;
    public static OnGroupAddModerator onGroupAddModerator;
    public static OnGroupClearMessage onGroupClearMessage;
    public static OnGroupEdit onGroupEdit;
    public static OnGroupKickAdmin onGroupKickAdmin;
    public static OnGroupKickMember onGroupKickMember;
    public static OnGroupKickModerator onGroupKickModerator;
    public static OnGroupLeft onGroupLeft;
    public static OnFileDownloadResponse onFileDownloadResponse;
    public static OnUserInfoResponse onUserInfoResponse;
    public static OnUserAvatarResponse onUserAvatarResponse;
    public static OnGroupAvatarResponse onGroupAvatarResponse;
    public static OnChangeUserPhotoListener onChangeUserPhotoListener;
    public static OnClearChatHistory onClearChatHistory;
    public static OnDeleteChatFinishActivity onDeleteChatFinishActivity;
    public static OnClientGetRoomHistoryResponse onClientGetRoomHistoryResponse;
    public static OnUserAvatarDelete onUserAvatarDelete;
    public static OnDraftMessage onDraftMessage;
    public static OnUserDelete onUserDelete;
    public static OnUserProfileCheckUsername onUserProfileCheckUsername;
    public static OnUserProfileUpdateUsername onUserProfileUpdateUsername;
    public static OnGroupGetMemberList onGroupGetMemberList;
    public static OnUserGetDeleteToken onUserGetDeleteToken;
    public static OnGroupDelete onGroupDelete;
    public static OpenFragment onConvertToGroup;
    public static OnChatConvertToGroup onChatConvertToGroup;
    public static OnUserUpdateStatus onUserUpdateStatus;
    public static OnUpdateUserStatusInChangePage onUpdateUserStatusInChangePage;
    public static OnLastSeenUpdateTiming onLastSeenUpdateTiming;
    public static OnSetAction onSetAction;
    public static OnSetActionInRoom onSetActionInRoom;
    public static OnUserSessionGetActiveList onUserSessionGetActiveList;
    public static OnUserSessionTerminate onUserSessionTerminate;
    public static OnUserSessionLogout onUserSessionLogout;
    public static UpdateListAfterKick updateListAfterKick;
    public static OnHelperSetAction onHelperSetAction;
    public static OnChannelCreate onChannelCreate;
    public static OnChannelDelete onChannelDelete;
    public static OnChannelLeft onChannelLeft;
    public static OnChannelAddMember onChannelAddMember;
    public static OnChannelKickMember onChannelKickMember;
    public static OnChannelAddAdmin onChannelAddAdmin;
    public static OnChannelKickAdmin onChannelKickAdmin;
    public static OnChannelAddModerator onChannelAddModerator;
    public static OnChannelKickModerator onChannelKickModerator;
    public static OnChannelGetMemberList onChannelGetMemberList;
    public static OnChannelEdit onChannelEdit;
    public static OnChannelAvatarAdd onChannelAvatarAdd;
    public static OnChannelAvatarDelete onChannelAvatarDelete;
    public static OnChannelCheckUsername onChannelCheckUsername;
    public static OnGroupCheckUsername onGroupCheckUsername;
    public static OnGroupUpdateUsername onGroupUpdateUsername;
    public static OnChannelUpdateUsername onChannelUpdateUsername;
    public static OnGroupAvatarDelete onGroupAvatarDelete;
    public static OnRefreshActivity onRefreshActivity;
    public static OnGetUserInfo onGetUserInfo;
    public static OnFileDownloaded onFileDownloaded;
    public static OnUserInfoMyClient onUserInfoMyClient;
    public static OnChannelAddMessageReaction onChannelAddMessageReaction;
    public static OnChannelGetMessagesStats onChannelGetMessagesStats;
    public static OnChannelRemoveUsername onChannelRemoveUsername;
    public static OnChannelRevokeLink onChannelRevokeLink;
    public static OnChannelUpdateSignature onChannelUpdateSignature;
    public static OnChannelUpdateReactionStatus onChannelUpdateReactionStatus;
    public static OnChannelUpdateReactionStatus onChannelUpdateReactionStatusChat;
    public static OnClientCheckInviteLink onClientCheckInviteLink;
    public static OnClientGetRoomMessage onClientGetRoomMessage;
    public static OnClientJoinByInviteLink onClientJoinByInviteLink;
    public static OnClientJoinByUsername onClientJoinByUsername;
    public static OnClientResolveUsername onClientResolveUsername;
    public static OnClientSubscribeToRoom onClientSubscribeToRoom;
    public static OnClientUnsubscribeFromRoom onClientUnsubscribeFromRoom;
    public static OnGroupRemoveUsername onGroupRemoveUsername;
    public static OnGroupRevokeLink onGroupRevokeLink;
    public static OnUserContactsBlock onUserContactsBlock;
    public static OnUserContactsUnBlock onUserContactsUnBlock;
    public static OnClientCondition onClientCondition;
    public static OnGetWallpaper onGetWallpaper;
    public static OnTwoStepPassword onTwoStepPassword;
    public static TwoStepSecurityConfirmEmail twoStepSecurityConfirmEmail;
    public static OnSecurityCheckPassword onSecurityCheckPassword;
    public static OnRecoverySecurityPassword onRecoverySecurityPassword;
    public static OnRecoveryEmailToken onRecoveryEmailToken;
    public static OnQrCodeNewDevice onQrCodeNewDevice;
    public static OnVerifyNewDevice onVerifyNewDevice;
    public static OnPushLoginToken onPushLoginToken;
    public static OnPushTwoStepVerification onPushTwoStepVerification;
    public static OnBackgroundChanged onBackgroundChanged;
    public static IClientSearchUserName onClientSearchUserName;
    public static OnCallLeaveView onCallLeaveView;
    public static ICallFinish iCallFinishChat;
    public static ICallFinish iCallFinishMain;
    public static IMainFinish iMainFinish;
    public static IActivityFinish iActivityFinish;
    public static OnBlockStateChanged onBlockStateChanged;
    public static OnContactsGetList onContactsGetList;
    public static OnCallLogClear onCallLogClear;
    public static OnMapUsersGet onMapUsersGet;
    public static OnPinedMessage onPinedMessage;
    public static OnSelectMenu onSelectMenu;
    public static OnRemoveFragment onRemoveFragment;
    public static OnChatDeleteInRoomList onChatDeleteInRoomList;
    public static OnGroupDeleteInRoomList onGroupDeleteInRoomList;
    public static OnChannelDeleteInRoomList onChannelDeleteInRoomList;
    public static OnClearUnread onClearUnread;
    public static OnClientGetRoomResponseRoomList onClientGetRoomResponseRoomList;
    public static OnMute onMute;
    public static OnClearRoomHistory onClearRoomHistory;
    public static OnReport onReport;
    public static OnPhoneContact onPhoneContact;
    public static OnContactFetchForServer onContactFetchForServer;
    public static OnQueueSendContact onQueueSendContact;
    public static OnAudioFocusChangeListener onAudioFocusChangeListener;
    public static IDispatchTochEvent dispatchTochEventChat;
    public static IOnBackPressed onBackPressedChat;
    public static ISendPosition iSendPositionChat;
    public static ITowPanModDesinLayout iTowPanModDesinLayout;
    public static OnDateChanged onDateChanged;
    public static IOnBackPressed onBackPressedExplorer;
    public static OnLocationChanged onLocationChanged;
    public static OnGetNearbyCoordinate onGetNearbyCoordinate;
    public static OnGeoGetComment onGeoGetComment;
    public static OnMapRegisterState onMapRegisterState;
    public static OnMapRegisterStateMain onMapRegisterStateMain;
    public static OpenBottomSheetItem openBottomSheetItem;
    public static OnUnreadChange onUnreadChange;
    public static OnMapClose onMapClose;
    public static OnRegistrationInfo onRegistrationInfo;
    public static OnGeoCommentResponse onGeoCommentResponse;
    public static OnGeoGetConfiguration onGeoGetConfiguration;
    public static OnNotifyTime onNotifyTime;
    public static OnPayment onPayment;
    public static OnMplResult onMplResult;
    public static ISignalingOffer iSignalingOffer;
    public static ISignalingRinging iSignalingRinging;
    public static ISignalingAccept iSignalingAccept;
    public static ISignalingCandidate iSignalingCandidate;
    public static ISignalingLeave iSignalingLeave;
    public static ISignalingSessionHold iSignalingSessionHold;
    public static ISignalingGetCallLog iSignalingGetCallLog;
    public static ISignalingCallBack iSignalingCallBack;
    public static ISignalingErrore iSignalingErrore;
    public static Typeface typeface_IRANSansMobile;
    public static Typeface typeface_IRANSansMobile_Bold;
    public static Typeface typeface_Fontico;
    public static Typeface typeface_neuropolitical;
    public static boolean isPassCode;
    public static FingerPrint fingerPrint;
    public static OneFragmentIsOpen oneFragmentIsOpen;
    public static boolean isFragmentMapActive = false; // for check network
    public static boolean isRestartActivity = false; // for check passCode
    public static boolean isFirstPassCode = true; // for check passCode
    public static boolean multiTab = false;
    public static boolean isTimeWhole = false;
    public static FragmentManager fragmentManager;
    private Tracker mTracker;
    public static Account iGapAccount;
    public static Card selectedCard = null;
    public static long cardamount;
    public static String jwt = null;

    public static int rotationState;


    @Override
    public void onCreate() {
        super.onCreate();

        G.firstTimeEnterToApp = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Fabric.with(getApplicationContext(), new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
                CaocConfig.Builder.create().backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT).showErrorDetails(false).showRestartButton(true).trackActivities(true).restartActivity(ActivityMain.class).errorActivity(ActivityCustomError.class).apply();
            }
        }).start();

        context = getApplicationContext();
        handler = new Handler();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Raad.init(getApplicationContext());
        Utils.setInstart(context, "fa");
        WebBase.apiKey = "5aa7e856ae7fbc00016ac5a01c65909797d94a16a279f46a4abb5faa";

        new StartupActions();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateResources(base));
        new MultiDexUtils().getLoadedExternalDexClasses(this);
    }

    public static Context updateResources(Context baseContext) {
        if (G.selectedLanguage == null) {
            G.selectedLanguage = Locale.getDefault().getLanguage();
        }
        Locale locale = new Locale(G.selectedLanguage);
        Locale.setDefault(locale);

        Resources res = baseContext.getResources();
        Configuration configuration = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            baseContext = baseContext.createConfigurationContext(configuration);
        } else {
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        G.context = baseContext;

        return baseContext;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResources(this);
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_track);
        }
        return mTracker;
    }
}
