/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.view.LayoutInflater;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.iGap.helper.HelperCheckInternetConnection;
import com.iGap.helper.HelperLogMessage;
import com.iGap.helper.HelperNotificationAndBadge;
import com.iGap.interfaces.IClientSearchUserName;
import com.iGap.interfaces.OnChangeUserPhotoListener;
import com.iGap.interfaces.OnChannelAddAdmin;
import com.iGap.interfaces.OnChannelAddMember;
import com.iGap.interfaces.OnChannelAddMessageReaction;
import com.iGap.interfaces.OnChannelAddModerator;
import com.iGap.interfaces.OnChannelAvatarAdd;
import com.iGap.interfaces.OnChannelAvatarDelete;
import com.iGap.interfaces.OnChannelCheckUsername;
import com.iGap.interfaces.OnChannelCreate;
import com.iGap.interfaces.OnChannelDelete;
import com.iGap.interfaces.OnChannelEdit;
import com.iGap.interfaces.OnChannelGetMemberList;
import com.iGap.interfaces.OnChannelGetMessagesStats;
import com.iGap.interfaces.OnChannelKickAdmin;
import com.iGap.interfaces.OnChannelKickMember;
import com.iGap.interfaces.OnChannelKickModerator;
import com.iGap.interfaces.OnChannelLeft;
import com.iGap.interfaces.OnChannelRemoveUsername;
import com.iGap.interfaces.OnChannelRevokeLink;
import com.iGap.interfaces.OnChannelUpdateSignature;
import com.iGap.interfaces.OnChannelUpdateUsername;
import com.iGap.interfaces.OnChatConvertToGroup;
import com.iGap.interfaces.OnChatDelete;
import com.iGap.interfaces.OnChatDeleteMessageResponse;
import com.iGap.interfaces.OnChatEditMessageResponse;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.interfaces.OnClearChatHistory;
import com.iGap.interfaces.OnClientCheckInviteLink;
import com.iGap.interfaces.OnClientCondition;
import com.iGap.interfaces.OnClientGetRoomHistoryResponse;
import com.iGap.interfaces.OnClientGetRoomListResponse;
import com.iGap.interfaces.OnClientGetRoomMessage;
import com.iGap.interfaces.OnClientGetRoomResponse;
import com.iGap.interfaces.OnClientJoinByInviteLink;
import com.iGap.interfaces.OnClientJoinByUsername;
import com.iGap.interfaces.OnClientResolveUsername;
import com.iGap.interfaces.OnClientSearchRoomHistory;
import com.iGap.interfaces.OnClientSubscribeToRoom;
import com.iGap.interfaces.OnClientUnsubscribeFromRoom;
import com.iGap.interfaces.OnConnectionChangeState;
import com.iGap.interfaces.OnDeleteChatFinishActivity;
import com.iGap.interfaces.OnDraftMessage;
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.interfaces.OnFileDownloaded;
import com.iGap.interfaces.OnGetUserInfo;
import com.iGap.interfaces.OnGetWallpaper;
import com.iGap.interfaces.OnGroupAddAdmin;
import com.iGap.interfaces.OnGroupAddMember;
import com.iGap.interfaces.OnGroupAddModerator;
import com.iGap.interfaces.OnGroupAvatarDelete;
import com.iGap.interfaces.OnGroupAvatarResponse;
import com.iGap.interfaces.OnGroupCheckUsername;
import com.iGap.interfaces.OnGroupClearMessage;
import com.iGap.interfaces.OnGroupCreate;
import com.iGap.interfaces.OnGroupDelete;
import com.iGap.interfaces.OnGroupEdit;
import com.iGap.interfaces.OnGroupGetMemberList;
import com.iGap.interfaces.OnGroupKickAdmin;
import com.iGap.interfaces.OnGroupKickMember;
import com.iGap.interfaces.OnGroupKickModerator;
import com.iGap.interfaces.OnGroupLeft;
import com.iGap.interfaces.OnGroupRemoveUsername;
import com.iGap.interfaces.OnGroupRevokeLink;
import com.iGap.interfaces.OnGroupUpdateUsername;
import com.iGap.interfaces.OnHelperSetAction;
import com.iGap.interfaces.OnInfoCountryResponse;
import com.iGap.interfaces.OnInfoTime;
import com.iGap.interfaces.OnLastSeenUpdateTiming;
import com.iGap.interfaces.OnReceiveInfoLocation;
import com.iGap.interfaces.OnReceivePageInfoTOS;
import com.iGap.interfaces.OnRefreshActivity;
import com.iGap.interfaces.OnSecuring;
import com.iGap.interfaces.OnSetAction;
import com.iGap.interfaces.OnSetActionInRoom;
import com.iGap.interfaces.OnUpdateAvatar;
import com.iGap.interfaces.OnUpdateUserStatusInChangePage;
import com.iGap.interfaces.OnUpdating;
import com.iGap.interfaces.OnUserAvatarDelete;
import com.iGap.interfaces.OnUserAvatarGetList;
import com.iGap.interfaces.OnUserAvatarResponse;
import com.iGap.interfaces.OnUserContactDelete;
import com.iGap.interfaces.OnUserContactEdit;
import com.iGap.interfaces.OnUserContactsBlock;
import com.iGap.interfaces.OnUserContactsUnBlock;
import com.iGap.interfaces.OnUserDelete;
import com.iGap.interfaces.OnUserGetDeleteToken;
import com.iGap.interfaces.OnUserInfoForAvatar;
import com.iGap.interfaces.OnUserInfoMyClient;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.interfaces.OnUserLogin;
import com.iGap.interfaces.OnUserProfileCheckUsername;
import com.iGap.interfaces.OnUserProfileGetNickname;
import com.iGap.interfaces.OnUserProfileSetEmailResponse;
import com.iGap.interfaces.OnUserProfileSetGenderResponse;
import com.iGap.interfaces.OnUserProfileSetNickNameResponse;
import com.iGap.interfaces.OnUserProfileUpdateUsername;
import com.iGap.interfaces.OnUserRegistration;
import com.iGap.interfaces.OnUserSessionGetActiveList;
import com.iGap.interfaces.OnUserSessionLogout;
import com.iGap.interfaces.OnUserSessionTerminate;
import com.iGap.interfaces.OnUserUpdateStatus;
import com.iGap.interfaces.OnUserUsernameToId;
import com.iGap.interfaces.OnUserVerification;
import com.iGap.interfaces.OpenFragment;
import com.iGap.interfaces.UpdateListAfterKick;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.ChatUpdateStatusUtil;
import com.iGap.module.ClearMessagesUtil;
import com.iGap.module.StartupActions;
import com.iGap.module.enums.ConnectionState;
import com.iGap.proto.ProtoClientCondition;
import com.iGap.request.RequestWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.spec.SecretKeySpec;

public class G extends MultiDexApplication {

    public static Context context;
    public static Handler handler;
    public static LayoutInflater inflater;
    private Tracker mTracker;

    public static HelperNotificationAndBadge helperNotificationAndBadge;
    public static ConcurrentHashMap<String, RequestWrapper> requestQueueMap = new ConcurrentHashMap<>();
    public static List<Long> smsNumbers = new ArrayList<>();
    public static AtomicBoolean pullRequestQueueRunned = new AtomicBoolean(false);
    public static SecretKeySpec symmetricKey;
    public static ProtoClientCondition.ClientCondition.Builder clientConditionGlobal;
    public static HelperCheckInternetConnection.ConnectivityType latestConnectivityType;
    public static ImageLoader imageLoader;

    public static ArrayList<String> unSecure = new ArrayList<>();
    public static ArrayList<String> unLogin = new ArrayList<>();// list of actionId that can be doing without secure
    public static ArrayList<String> waitingActionIds = new ArrayList<>();

    public static HashMap<Integer, String> lookupMap = new HashMap<>();
    public static HashMap<String, ArrayList<Object>> requestQueueRelationMap = new HashMap<>();
    public static HashMap<Long, HelperLogMessage.StructLog> logMessageUpdatList = new HashMap<>();

    public static Activity currentActivity;
    public static Activity latestActivity;

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
    public static String headerTextColor;

    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;
    public static boolean isUserStatusOnline = false;
    public static boolean isSecure = false;
    public static boolean allowForConnect = true;
    public static boolean userLogin = false;
    public static boolean socketConnection = false;
    public static boolean canRunReceiver = false;
    public static boolean firstTimeEnterToApp = true; // use this field for get room list
    public static boolean firstEnter = true;
    public static boolean isSaveToGallery = false;
    public static boolean hasNetworkBefore;
    public static boolean isSendContact = false;
    public static boolean latestMobileDataState;

    public static int ivSize;
    public static int userTextSize = 0;
    public static int COPY_BUFFER_SIZE = 1024;

    public static long currentTime;
    public static long userId;
    public static long latestHearBeatTime = System.currentTimeMillis();
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
    public static IClientSearchUserName onClientSearchUserName;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        context = getApplicationContext();
        handler = new Handler();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        new StartupActions();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
