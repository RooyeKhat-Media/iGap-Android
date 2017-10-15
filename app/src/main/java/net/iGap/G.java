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

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.spec.SecretKeySpec;
import net.iGap.activities.ActivityCustomError;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperLogMessage;
import net.iGap.helper.HelperNotificationAndBadge;
import net.iGap.interfaces.FingerPrint;
import net.iGap.interfaces.IActivityFinish;
import net.iGap.interfaces.ICallFinish;
import net.iGap.interfaces.IClientSearchUserName;
import net.iGap.interfaces.IDispatchTochEvent;
import net.iGap.interfaces.IMainFinish;
import net.iGap.interfaces.IOnBackPressed;
import net.iGap.interfaces.ISendPosition;
import net.iGap.interfaces.ISignalingAccept;
import net.iGap.interfaces.ISignalingCallBack;
import net.iGap.interfaces.ISignalingCandidate;
import net.iGap.interfaces.ISignalingErrore;
import net.iGap.interfaces.ISignalingGetCallLog;
import net.iGap.interfaces.ISignalingLeave;
import net.iGap.interfaces.ISignalingOffer;
import net.iGap.interfaces.ISignalingRinging;
import net.iGap.interfaces.ISignalingSessionHold;
import net.iGap.interfaces.ITowPanModDesinLayout;
import net.iGap.interfaces.OnBackgroundChanged;
import net.iGap.interfaces.OnBlockStateChanged;
import net.iGap.interfaces.OnCallLeaveView;
import net.iGap.interfaces.OnCallLogClear;
import net.iGap.interfaces.OnChangeUserPhotoListener;
import net.iGap.interfaces.OnChannelAddAdmin;
import net.iGap.interfaces.OnChannelAddMember;
import net.iGap.interfaces.OnChannelAddMessageReaction;
import net.iGap.interfaces.OnChannelAddModerator;
import net.iGap.interfaces.OnChannelAvatarAdd;
import net.iGap.interfaces.OnChannelAvatarDelete;
import net.iGap.interfaces.OnChannelCheckUsername;
import net.iGap.interfaces.OnChannelCreate;
import net.iGap.interfaces.OnChannelDelete;
import net.iGap.interfaces.OnChannelDeleteInRoomList;
import net.iGap.interfaces.OnChannelEdit;
import net.iGap.interfaces.OnChannelGetMemberList;
import net.iGap.interfaces.OnChannelGetMessagesStats;
import net.iGap.interfaces.OnChannelKickAdmin;
import net.iGap.interfaces.OnChannelKickMember;
import net.iGap.interfaces.OnChannelKickModerator;
import net.iGap.interfaces.OnChannelLeft;
import net.iGap.interfaces.OnChannelRemoveUsername;
import net.iGap.interfaces.OnChannelRevokeLink;
import net.iGap.interfaces.OnChannelUpdateSignature;
import net.iGap.interfaces.OnChannelUpdateUsername;
import net.iGap.interfaces.OnChatConvertToGroup;
import net.iGap.interfaces.OnChatDelete;
import net.iGap.interfaces.OnChatDeleteInRoomList;
import net.iGap.interfaces.OnChatDeleteMessageResponse;
import net.iGap.interfaces.OnChatEditMessageResponse;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnChatSendMessage;
import net.iGap.interfaces.OnClearChatHistory;
import net.iGap.interfaces.OnClearRoomHistory;
import net.iGap.interfaces.OnClearUnread;
import net.iGap.interfaces.OnClientCheckInviteLink;
import net.iGap.interfaces.OnClientCondition;
import net.iGap.interfaces.OnClientGetRoomHistoryResponse;
import net.iGap.interfaces.OnClientGetRoomListResponse;
import net.iGap.interfaces.OnClientGetRoomMessage;
import net.iGap.interfaces.OnClientGetRoomResponse;
import net.iGap.interfaces.OnClientGetRoomResponseRoomList;
import net.iGap.interfaces.OnClientJoinByInviteLink;
import net.iGap.interfaces.OnClientJoinByUsername;
import net.iGap.interfaces.OnClientResolveUsername;
import net.iGap.interfaces.OnClientSearchRoomHistory;
import net.iGap.interfaces.OnClientSubscribeToRoom;
import net.iGap.interfaces.OnClientUnsubscribeFromRoom;
import net.iGap.interfaces.OnConnectionChangeState;
import net.iGap.interfaces.OnContactAdd;
import net.iGap.interfaces.OnDeleteChatFinishActivity;
import net.iGap.interfaces.OnDraftMessage;
import net.iGap.interfaces.OnFileDownloadResponse;
import net.iGap.interfaces.OnFileDownloaded;
import net.iGap.interfaces.OnGeoCommentResponse;
import net.iGap.interfaces.OnGeoGetComment;
import net.iGap.interfaces.OnGeoGetConfiguration;
import net.iGap.interfaces.OnGetNearbyCoordinate;
import net.iGap.interfaces.OnGetUserInfo;
import net.iGap.interfaces.OnGetWallpaper;
import net.iGap.interfaces.OnGroupAddAdmin;
import net.iGap.interfaces.OnGroupAddMember;
import net.iGap.interfaces.OnGroupAddModerator;
import net.iGap.interfaces.OnGroupAvatarDelete;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnGroupCheckUsername;
import net.iGap.interfaces.OnGroupClearMessage;
import net.iGap.interfaces.OnGroupCreate;
import net.iGap.interfaces.OnGroupDelete;
import net.iGap.interfaces.OnGroupDeleteInRoomList;
import net.iGap.interfaces.OnGroupEdit;
import net.iGap.interfaces.OnGroupGetMemberList;
import net.iGap.interfaces.OnGroupKickAdmin;
import net.iGap.interfaces.OnGroupKickMember;
import net.iGap.interfaces.OnGroupKickModerator;
import net.iGap.interfaces.OnGroupLeft;
import net.iGap.interfaces.OnGroupRemoveUsername;
import net.iGap.interfaces.OnGroupRevokeLink;
import net.iGap.interfaces.OnGroupUpdateUsername;
import net.iGap.interfaces.OnHelperSetAction;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnInfoTime;
import net.iGap.interfaces.OnLastSeenUpdateTiming;
import net.iGap.interfaces.OnLocationChanged;
import net.iGap.interfaces.OnMapClose;
import net.iGap.interfaces.OnMapRegisterState;
import net.iGap.interfaces.OnMapUsersGet;
import net.iGap.interfaces.OnMute;
import net.iGap.interfaces.OnPushLoginToken;
import net.iGap.interfaces.OnPushTwoStepVerification;
import net.iGap.interfaces.OnQrCodeNewDevice;
import net.iGap.interfaces.OnReceiveInfoLocation;
import net.iGap.interfaces.OnReceivePageInfoTOS;
import net.iGap.interfaces.OnRecoveryEmailToken;
import net.iGap.interfaces.OnRecoverySecurityPassword;
import net.iGap.interfaces.OnRefreshActivity;
import net.iGap.interfaces.OnRegistrationInfo;
import net.iGap.interfaces.OnRemoveFragment;
import net.iGap.interfaces.OnSecuring;
import net.iGap.interfaces.OnSecurityCheckPassword;
import net.iGap.interfaces.OnSelectMenu;
import net.iGap.interfaces.OnSetAction;
import net.iGap.interfaces.OnSetActionInRoom;
import net.iGap.interfaces.OnTwoStepPassword;
import net.iGap.interfaces.OnUpdateAvatar;
import net.iGap.interfaces.OnUpdateUserStatusInChangePage;
import net.iGap.interfaces.OnUpdating;
import net.iGap.interfaces.OnUserAvatarDelete;
import net.iGap.interfaces.OnUserAvatarGetList;
import net.iGap.interfaces.OnUserAvatarResponse;
import net.iGap.interfaces.OnUserContactDelete;
import net.iGap.interfaces.OnUserContactEdit;
import net.iGap.interfaces.OnUserContactsBlock;
import net.iGap.interfaces.OnUserContactsUnBlock;
import net.iGap.interfaces.OnUserDelete;
import net.iGap.interfaces.OnUserGetDeleteToken;
import net.iGap.interfaces.OnUserInfoForAvatar;
import net.iGap.interfaces.OnUserInfoMyClient;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.interfaces.OnUserProfileCheckUsername;
import net.iGap.interfaces.OnUserProfileGetNickname;
import net.iGap.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.interfaces.OnUserProfileSetGenderResponse;
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.interfaces.OnUserProfileUpdateUsername;
import net.iGap.interfaces.OnUserRegistration;
import net.iGap.interfaces.OnUserSessionGetActiveList;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.interfaces.OnUserSessionTerminate;
import net.iGap.interfaces.OnUserUpdateStatus;
import net.iGap.interfaces.OnUserUsernameToId;
import net.iGap.interfaces.OnUserVerification;
import net.iGap.interfaces.OnVerifyNewDevice;
import net.iGap.interfaces.OneFragmentIsOpen;
import net.iGap.interfaces.OpenFragment;
import net.iGap.interfaces.TwoStepSecurityConfirmEmail;
import net.iGap.interfaces.UpdateListAfterKick;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.ChatUpdateStatusUtil;
import net.iGap.module.ClearMessagesUtil;
import net.iGap.module.MultiDexUtils;
import net.iGap.module.StartupActions;
import net.iGap.module.enums.ConnectionState;
import net.iGap.proto.ProtoClientCondition;
import net.iGap.request.RequestWrapper;

public class G extends MultiDexApplication {

    public static Context context;
    public static Handler handler;
    public static LayoutInflater inflater;
    private Tracker mTracker;
    //public static Realm mRealm;

    public static HelperNotificationAndBadge helperNotificationAndBadge;
    public static ConcurrentHashMap<String, RequestWrapper> requestQueueMap = new ConcurrentHashMap<>();
    public static List<Long> smsNumbers = new ArrayList<>();
    public static AtomicBoolean pullRequestQueueRunned = new AtomicBoolean(false);
    public static SecretKeySpec symmetricKey;
    public static ProtoClientCondition.ClientCondition.Builder clientConditionGlobal;
    public static HelperCheckInternetConnection.ConnectivityType latestConnectivityType;
    public static ImageLoader imageLoader;
    public static ArrayList<Long> deletedRoomList = new ArrayList<>();

    public static ArrayList<String> unSecure = new ArrayList<>();
    public static ArrayList<String> unSecureResponseActionId = new ArrayList<>();
    public static ArrayList<String> unLogin = new ArrayList<>();// list of actionId that can be doing without secure
    public static ArrayList<String> waitingActionIds = new ArrayList<>();
    public static ArrayList<String> generalImmovableClasses = new ArrayList<>();
    public static ArrayList<Integer> forcePriorityActionId = new ArrayList<>();

    public static HashMap<Integer, String> lookupMap = new HashMap<>();
    public static HashMap<String, ArrayList<Object>> requestQueueRelationMap = new HashMap<>();
    public static HashMap<Long, HelperLogMessage.StructLog> logMessageUpdatList = new HashMap<>();
    public static HashMap<Integer, Integer> priorityActionId = new HashMap<>();

    public static Activity currentActivity;
    public static FragmentActivity fragmentActivity;
    public static String latestActivityName;

    public static File IMAGE_NEW_GROUP;
    public static File IMAGE_NEW_CHANEL;
    public static File imageFile;

    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR_APP = DIR_SDCARD + "/iGap";
    public static final String DIR_IMAGES = DIR_APP + "/iGap Images";
    public static final String DIR_VIDEOS = DIR_APP + "/iGap Videos";
    public static final String DIR_AUDIOS = DIR_APP + "/iGap Audios";
    public static final String DIR_DOCUMENT = DIR_APP + "/iGap Document";
    public static final String DIR_TEMP = DIR_APP + "/.temp";
    public static final String DIR_CHAT_BACKGROUND = DIR_APP + "/.chat_background";
    public static final String DIR_IMAGE_USER = DIR_APP + "/.image_user";
    public static final String CHAT_MESSAGE_TIME = "H:mm";

    public static String selectedLanguage = "en";
    public static String symmetricMethod;
    public static String appBarColor; // default color
    public static String notificationColor;
    public static String toggleButtonColor;
    public static String attachmentColor;
    public static String progressColor;
    public static String headerTextColor;
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

    public static boolean twoPaneMode = false;
    public static boolean isLandscape = false;
    public static boolean isAppRtl = false;


    public static String salectedTabInMainActivity = "";

    public static int ivSize;
    public static int userTextSize = 0;
    public static int COPY_BUFFER_SIZE = 1024;
    public static boolean isCalculatKeepMedia = true;
    public static int maxChatBox = 0;

    public static long currentTime;
    public static long userId;
    public static long latestHearBeatTime = System.currentTimeMillis();
    public static long latestResponse = System.currentTimeMillis();
    public static long serverHeartBeatTiming = 60 * 1000;

    public static ClearMessagesUtil clearMessagesUtil = new ClearMessagesUtil();
    public static ChatSendMessageUtil chatSendMessageUtil = new ChatSendMessageUtil();
    public static ChatUpdateStatusUtil chatUpdateStatusUtil = new ChatUpdateStatusUtil();
    public static ConnectionState connectionState;
    public static ConnectionState latestConnectionState;
    public static OnConnectionChangeState onConnectionChangeState;
    public static OnUpdating onUpdating;
    public static OnReceiveInfoLocation onReceiveInfoLocation;
    public static OnUserRegistration onUserRegistration;
    public static OnClientSearchRoomHistory onClientSearchRoomHistory;
    public static OnUserVerification onUserVerification;
    public static OnReceivePageInfoTOS onReceivePageInfoTOS;
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
    public static OnUserInfoForAvatar onUserInfoForAvatar;
    public static OnUserAvatarResponse onUserAvatarResponse;
    public static OnGroupAvatarResponse onGroupAvatarResponse;
    public static OnChangeUserPhotoListener onChangeUserPhotoListener;
    public static OnClearChatHistory onClearChatHistory;
    public static OnDeleteChatFinishActivity onDeleteChatFinishActivity;
    public static OnClientGetRoomHistoryResponse onClientGetRoomHistoryResponse;
    public static OnUserAvatarDelete onUserAvatarDelete;
    public static OnUserAvatarGetList onUserAvatarGetList;
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
    public static OnClientCheckInviteLink onClientCheckInviteLink;
    public static OnClientGetRoomMessage onClientGetRoomMessage;
    public static OnClientJoinByInviteLink onClientJoinByInviteLink;
    public static OnClientJoinByUsername onClientJoinByUsername;
    public static OnClientResolveUsername onClientResolveUsername;
    public static OnClientSubscribeToRoom onClientSubscribeToRoom;
    public static OnClientUnsubscribeFromRoom onClientUnsubscribeFromRoom;
    public static OnGroupRemoveUsername onGroupRemoveUsername;
    public static OnGroupRevokeLink onGroupRevokeLink;
    public static OnUpdateAvatar onUpdateAvatar;
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
    public static OnContactAdd onContactAdd;
    public static OnCallLogClear onCallLogClear;
    public static OnMapUsersGet onMapUsersGet;
    public static OnSelectMenu onSelectMenu;
    public static OnRemoveFragment onRemoveFragment;
    public static OnChatDeleteInRoomList onChatDeleteInRoomList;
    public static OnGroupDeleteInRoomList onGroupDeleteInRoomList;
    public static OnChannelDeleteInRoomList onChannelDeleteInRoomList;
    public static OnClearUnread onClearUnread;
    public static OnClientGetRoomResponseRoomList onClientGetRoomResponseRoomList;
    public static OnMute onMute;
    public static OnClearRoomHistory onClearRoomHistory;

    public static IDispatchTochEvent dispatchTochEventChat;
    public static IOnBackPressed onBackPressedChat;
    public static ISendPosition iSendPositionChat;
    public static ITowPanModDesinLayout iTowPanModDesinLayout;

    public static IOnBackPressed onBackPressedExplorer;

    public static OnLocationChanged onLocationChanged;
    public static OnGetNearbyCoordinate onGetNearbyCoordinate;
    public static OnGeoGetComment onGeoGetComment;
    public static OnMapRegisterState onMapRegisterState;
    public static OnMapClose onMapClose;
    public static OnRegistrationInfo onRegistrationInfo;
    public static OnGeoCommentResponse onGeoCommentResponse;
    public static OnGeoGetConfiguration onGeoGetConfiguration;

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

    public static FragmentManager fragmentManager;

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

        new StartupActions();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
        new MultiDexUtils().getLoadedExternalDexClasses(this);
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public static void checkLanguage() {
        try {
            String selectedLanguage = G.selectedLanguage;
            if (selectedLanguage == null) return;
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
