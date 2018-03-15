package net.iGap.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.lalongooo.videocompressor.video.MediaController;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityTrimVideo;
import net.iGap.adapter.AdapterBottomSheet;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.adapter.items.AdapterBottomSheetForward;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.adapter.items.chat.AudioItem;
import net.iGap.adapter.items.chat.ContactItem;
import net.iGap.adapter.items.chat.FileItem;
import net.iGap.adapter.items.chat.GifItem;
import net.iGap.adapter.items.chat.GifWithTextItem;
import net.iGap.adapter.items.chat.ImageItem;
import net.iGap.adapter.items.chat.ImageWithTextItem;
import net.iGap.adapter.items.chat.LocationItem;
import net.iGap.adapter.items.chat.LogItem;
import net.iGap.adapter.items.chat.ProgressWaiting;
import net.iGap.adapter.items.chat.TextItem;
import net.iGap.adapter.items.chat.TimeItem;
import net.iGap.adapter.items.chat.UnreadMessage;
import net.iGap.adapter.items.chat.VideoItem;
import net.iGap.adapter.items.chat.VideoWithTextItem;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.adapter.items.chat.VoiceItem;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperGetMessageState;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperNotificationAndBadge;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperTimeOut;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.FinishActivity;
import net.iGap.interfaces.ICallFinish;
import net.iGap.interfaces.IDispatchTochEvent;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.IOnBackPressed;
import net.iGap.interfaces.IPickFile;
import net.iGap.interfaces.IResendMessage;
import net.iGap.interfaces.ISendPosition;
import net.iGap.interfaces.IUpdateLogItem;
import net.iGap.interfaces.OnActivityChatStart;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnBackgroundChanged;
import net.iGap.interfaces.OnChannelAddMessageReaction;
import net.iGap.interfaces.OnChannelGetMessagesStats;
import net.iGap.interfaces.OnChannelUpdateReactionStatus;
import net.iGap.interfaces.OnChatClearMessageResponse;
import net.iGap.interfaces.OnChatDelete;
import net.iGap.interfaces.OnChatDeleteMessageResponse;
import net.iGap.interfaces.OnChatEditMessageResponse;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnChatMessageRemove;
import net.iGap.interfaces.OnChatMessageSelectionChanged;
import net.iGap.interfaces.OnChatSendMessage;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.interfaces.OnClearChatHistory;
import net.iGap.interfaces.OnClickCamera;
import net.iGap.interfaces.OnClientJoinByUsername;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnConnectionChangeStateChat;
import net.iGap.interfaces.OnDeleteChatFinishActivity;
import net.iGap.interfaces.OnForwardBottomSheet;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnHelperSetAction;
import net.iGap.interfaces.OnLastSeenUpdateTiming;
import net.iGap.interfaces.OnMessageReceive;
import net.iGap.interfaces.OnPathAdapterBottomSheet;
import net.iGap.interfaces.OnReport;
import net.iGap.interfaces.OnSetAction;
import net.iGap.interfaces.OnUpdateUserOrRoomInfo;
import net.iGap.interfaces.OnUpdateUserStatusInChangePage;
import net.iGap.interfaces.OnUserContactsBlock;
import net.iGap.interfaces.OnUserContactsUnBlock;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserUpdateStatus;
import net.iGap.interfaces.OnVoiceRecord;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.CircleImageView;
import net.iGap.module.ContactUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.FileUtils;
import net.iGap.module.IntentRequests;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MessageLoader;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyAppBarLayout;
import net.iGap.module.MyLinearLayoutManager;
import net.iGap.module.MyType;
import net.iGap.module.ResendMessage;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.TimeUtils;
import net.iGap.module.VoiceRecord;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.ConnectionState;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.LocalFileType;
import net.iGap.module.enums.ProgressState;
import net.iGap.module.enums.SendingStep;
import net.iGap.module.structs.StructBackGroundSeen;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.module.structs.StructBottomSheetForward;
import net.iGap.module.structs.StructChannelExtra;
import net.iGap.module.structs.StructCompress;
import net.iGap.module.structs.StructMessageAttachment;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.module.structs.StructUploadVideo;
import net.iGap.proto.ProtoChannelGetMessagesStats;
import net.iGap.proto.ProtoClientGetRoomHistory;
import net.iGap.proto.ProtoClientRoomReport;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAttachmentFields;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomDraft;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageContact;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChannelEditMessage;
import net.iGap.request.RequestChannelUpdateDraft;
import net.iGap.request.RequestChatDelete;
import net.iGap.request.RequestChatEditMessage;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestChatUpdateDraft;
import net.iGap.request.RequestClientJoinByUsername;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.request.RequestClientRoomReport;
import net.iGap.request.RequestClientSubscribeToRoom;
import net.iGap.request.RequestClientUnsubscribeFromRoom;
import net.iGap.request.RequestGroupEditMessage;
import net.iGap.request.RequestGroupUpdateDraft;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsUnblock;
import net.iGap.request.RequestUserInfo;
import net.iGap.viewmodel.ActivityCallViewModel;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.view.CameraRenderer;
import io.fotoapparat.view.CameraView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;
import static java.lang.Long.parseLong;
import static net.iGap.G.chatSendMessageUtil;
import static net.iGap.G.context;
import static net.iGap.R.id.ac_ll_parent;
import static net.iGap.R.string.item;
import static net.iGap.helper.HelperCalander.convertToUnicodeFarsiNumber;
import static net.iGap.helper.HelperGetDataFromOtherApp.messageType;
import static net.iGap.module.AttachFile.getFilePathFromUri;
import static net.iGap.module.AttachFile.getPathN;
import static net.iGap.module.AttachFile.request_code_VIDEO_CAPTURED;
import static net.iGap.module.AttachFile.request_code_open_document;
import static net.iGap.module.AttachFile.request_code_pic_file;
import static net.iGap.module.MessageLoader.getLocalMessage;
import static net.iGap.module.enums.ProgressState.HIDE;
import static net.iGap.module.enums.ProgressState.SHOW;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.DOWN;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.CONTACT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF_TEXT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_TEXT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.LOG;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_TEXT;
import static net.iGap.realm.RealmRoomMessage.makeUnreadMessage;

public class FragmentChat extends BaseFragment
        implements IMessageItem, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse, OnChatMessageSelectionChanged<AbstractMessage>, OnChatMessageRemove, OnVoiceRecord,
        OnUserInfoResponse, OnSetAction, OnUserUpdateStatus, OnLastSeenUpdateTiming, OnGroupAvatarResponse, OnChannelAddMessageReaction, OnChannelGetMessagesStats, OnChatDelete, OnBackgroundChanged,
        OnConnectionChangeStateChat, OnChannelUpdateReactionStatus {

    public static FinishActivity finishActivity;
    public static OnComplete onMusicListener;
    public static IUpdateLogItem iUpdateLogItem;
    public static OnPathAdapterBottomSheet onPathAdapterBottomSheet;
    public static OnForwardBottomSheet onForwardBottomSheet;
    public static OnClickCamera onClickCamera;
    public static OnComplete hashListener;
    public static OnComplete onComplete;
    public static OnUpdateUserOrRoomInfo onUpdateUserOrRoomInfo;
    public static ArrayList<Long> resentedMessageId = new ArrayList<>();
    public static ArrayMap<Long, HelperUploadFile.StructUpload> compressingFiles = new ArrayMap<>();
    public static int forwardMessageCount = 0;
    public static ArrayList<Parcelable> mForwardMessages;
    public static boolean canClearForwardList = true;
    public static Realm realmChat; // static for FragmentTest
    public static boolean canUpdateAfterDownload = false;
    public static String titleStatic;
    public static long messageId;
    public static long mRoomIdStatic = 0;
    public static long lastChatRoomId = 0;
    private static List<StructBottomSheet> contacts;
    private static ArrayMap<String, Boolean> compressedPath = new ArrayMap<>(); // keep compressedPath and also keep video path that never be won't compressed
    private static ArrayList<StructUploadVideo> structUploadVideos = new ArrayList<>();

    /**
     * *************************** common method ***************************
     */

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private final int END_CHAT_LIMIT = 5;
    public MusicPlayer musicPlayer;
    public String title;
    public String phoneNumber;
    public long mRoomId = 0;
    private AttachFile attachFile;
    private EditText edtSearchMessage;
    private SharedPreferences sharedPreferences;
    private net.iGap.module.EmojiEditTextE edtChat;
    private MaterialDesignTextView imvSendButton;
    private MaterialDesignTextView imvAttachFileButton;
    private MaterialDesignTextView imvMicButton;
    //  private MaterialDesignTextView btnReplaySelected;
    private RippleView rippleDeleteSelected;
    private RippleView rippleReplaySelected;
    private ArrayList<String> listPathString;
    private MaterialDesignTextView btnCancelSendingFile;
    private ViewGroup viewGroupLastSeen;
    private CircleImageView imvUserPicture;
    private AppCompatImageView txtVerifyRoomIcon;
    private ImageView imgBackGround;
    private RecyclerView recyclerView;
    private MaterialDesignTextView imvSmileButton;
    private LocationManager locationManager;
    private OnComplete complete;
    private View viewAttachFile;
    private View viewMicRecorder;
    private VoiceRecord voiceRecord;
    private MaterialDesignTextView txtClearMessageSearch;
    private MaterialDesignTextView btnHashLayoutClose;
    private SearchHash searchHash;
    private MessagesAdapter<AbstractMessage> mAdapter;
    private ProtoGlobal.Room.Type chatType;
    private EmojiPopup emojiPopup;
    private GroupChatRole groupRole;
    private ChannelChatRole channelRole;
    private PopupWindow popupWindow;
    private Uri latestUri;
    private Calendar lastDateCalendar = Calendar.getInstance();
    private MaterialDesignTextView iconMute;
    private MyAppBarLayout appBarLayout;
    private LinearLayout mediaLayout;
    private LinearLayout ll_Search;
    private LinearLayout layoutAttachBottom;
    private LinearLayout ll_attach_text;
    private LinearLayout ll_AppBarSelected;
    private LinearLayout toolbar;
    // private LinearLayout ll_navigate_Message;
    private LinearLayout ll_navigateHash;
    private LinearLayout lyt_user;
    private LinearLayout mReplayLayout;
    private ProgressBar prgWaiting;
    //  private AVLoadingIndicatorView avi;
    private ViewGroup vgSpamUser;
    private RecyclerView.OnScrollListener scrollListener;
    private RecyclerView rcvBottomSheet;
    private FrameLayout llScrollNavigate;
    private FastItemAdapter fastItemAdapter;
    private FastItemAdapter fastItemAdapterForward;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog bottomSheetDialogForward;
    private View viewBottomSheet;
    private View viewBottomSheetForward;
    private Fotoapparat fotoapparatSwitcher;
    private ArrayList<StructBottomSheet> itemGalleryList = new ArrayList<StructBottomSheet>();
    private RealmRoomMessage firstUnreadMessage;
    private RealmRoomMessage firstUnreadMessageInChat; // when user is in this room received new message
    private RealmRoomMessage voiceLastMessage = null;
    private boolean showVoteChannel = true;
    private RealmResults<RealmRoom> results = null;
    private RealmResults<RealmContacts> resultsContact = null;
    private List<StructBottomSheetForward> mListBottomSheetForward = new ArrayList<>();
    private ArrayList<StructBackGroundSeen> backGroundSeenList = new ArrayList<>();
    private TextView txtSpamUser;
    private TextView txtSpamClose;
    private TextView send;
    private TextView txtCountItem;
    private TextView txtNewUnreadMessage;
    private TextView imvCancelForward;
    private TextView btnUp;
    private TextView btnDown;
    private TextView txtChannelMute;
    // private TextView btnUpMessage;
    // private TextView btnDownMessage;
    // private TextView txtMessageCounter;
    private TextView btnUpHash;
    private TextView btnDownHash;
    private TextView txtHashCounter;
    private TextView txtFileNameForSend;
    private TextView txtNumberOfSelected;
    private EmojiTextViewE txtName;
    private TextView txtLastSeen;
    private TextView txtEmptyMessages;
    private String userName = "";
    private String latestFilePath;
    private String mainVideoPath = "";
    private String color;
    private String initialize;
    private String groupParticipantsCountLabel;
    private String channelParticipantsCountLabel;
    private String userStatus;
    private Boolean isGoingFromUserLink = false;
    private Boolean isNotJoin = false; // this value will be trued when come to this chat with username
    private boolean isCheckBottomSheet = false;
    private boolean firsInitScrollPosition = false;
    private boolean initHash = false;
    private boolean initAttach = false;
    private boolean initEmoji = false;
    private boolean hasDraft = false;
    private boolean hasForward = false;
    private boolean blockUser = false;
    private boolean isChatReadOnly = false;
    private boolean isMuteNotification;
    private boolean sendByEnter = false;
    private boolean isShowLayoutUnreadMessage = false;
    private boolean isCloudRoom;
    private boolean isEditMessage = false;
    private long biggestMessageId = 0;
    private long replyToMessageId = 0;
    private long userId;
    private long lastSeen;
    private long chatPeerId;
    private long userTime;
    private long savedScrollMessageId;
    private long latestButtonClickTime; // use from this field for avoid from show button again after click it
    private int countNewMessage = 0;
    private int lastPosition = 0;
    private int unreadCount = 0;
    private int latestRequestCode;
    private int messageCounter = 0;
    private int selectedPosition = 0;
    private boolean isNoMessage = true;
    private boolean isEmojiSHow = false;
    private boolean isCameraStart = false;
    private boolean isCameraAttached = false;
    private boolean isPermissionCamera = false;
    private boolean isPublicGroup = false;
    private ArrayList<Long> bothDeleteMessageId;
    private RelativeLayout layoutMute;
    private String report = "";
    private View rootView;
    private boolean isAllSenderId = true;
    private ArrayList<Long> multiForwardList = new ArrayList<>();
    private ArrayList<StructBottomSheetForward> mListForwardNotExict = new ArrayList<>();
    private String messageEdit = "";
    /**
     * **********************************************************************
     * *************************** Message Loader ***************************
     * **********************************************************************
     */

    private boolean addToView; // allow to message for add to recycler view or no
    private boolean topMore = true; // more message exist in local for load in up direction (topMore default value is true for allowing that try load top message )
    private boolean bottomMore; // more message exist in local for load in bottom direction
    private boolean isWaitingForHistoryUp; // client send request for getHistory, avoid for send request again
    private boolean isWaitingForHistoryDown; // client send request for getHistory, avoid for send request again
    private boolean allowGetHistoryUp = true;
    // after insuring for get end of message from server set this false. (set false in history error maybe was wrong , because maybe this was for another error not end  of message, (hint: can check error code for end of message from history))
    private boolean allowGetHistoryDown = true;
    // after insuring for get end of message from server set this false. (set false in history error maybe was wrong , because maybe this was for another error not end  of message, (hint: can check error code for end of message from history))
    private boolean firstUp = true; // if is firstUp getClientRoomHistory with low limit in UP direction
    private boolean firstDown = true; // if is firstDown getClientRoomHistory with low limit in DOWN direction
    private long gapMessageIdUp; // messageId that maybe lost in local
    private long gapMessageIdDown; // messageId that maybe lost in local
    private long reachMessageIdUp; // messageId that will be checked after getHistory for detect reached to that or no
    private long reachMessageIdDown; // messageId that will be checked after getHistory for detect reached to that or no
    private long startFutureMessageIdUp;
    // for get history from local or online in next step use from this param, ( hint : don't use from adapter items, because maybe this item was deleted and in this state messageId for get history won't be detected.
    private long startFutureMessageIdDown;
    // for get history from local or online in next step use from this param, ( hint : don't use from adapter items, because maybe this item was deleted and in this state messageId for get history won't be detected.
    private long progressIdentifierUp; // store identifier for Up progress item and use it if progress not removed from view after check 'instanceOf' in 'progressItem' method
    private long progressIdentifierDown; // store identifier for Down progress item and use it if progress not removed from view after check 'instanceOf' in 'progressItem' method
    private int firstVisiblePosition; // difference between start of adapter item and items that Showing.
    private int visibleItemCount; // visible item in recycler view
    private int totalItemCount; // all item in recycler view
    private int scrollEnd = 80; // (hint: It should be less than MessageLoader.LOCAL_LIMIT ) to determine the limits to get to the bottom or top of the list


    public static Realm getRealmChat() {
        if (realmChat == null || realmChat.isClosed()) {
            realmChat = Realm.getDefaultInstance();
        }
        return realmChat;
    }

    public static boolean allowResendMessage(long messageId) {
        if (resentedMessageId == null) {
            resentedMessageId = new ArrayList<>();
        }

        if (resentedMessageId.contains(messageId)) {
            return false;
        }

        resentedMessageId.add(messageId);
        return true;
    }

    public static void removeResendList(long messageId) {
        if (FragmentChat.resentedMessageId.contains(messageId)) {
            FragmentChat.resentedMessageId.remove(messageId);
        }
    }

    /**
     * get images for show in bottom sheet
     */
    public static ArrayList<StructBottomSheet> getAllShownImagesPath(Activity activity) {

        ArrayList<StructBottomSheet> listOfAllImages = new ArrayList<>();

        if (!HelperPermission.grantedUseStorage()) {
            return listOfAllImages;
        }

        Uri uri;
        Cursor cursor;
        int column_index_data = 0, column_index_folder_name;
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                StructBottomSheet item = new StructBottomSheet();
                item.setPath(absolutePathOfImage);
                item.isSelected = true;
                listOfAllImages.add(0, item);
            }
            cursor.close();
        }

        return listOfAllImages;
    }

    public static void isUiThread(String name, int line) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Log.i("UUU", name + " in line : " + line + " is UI Thread");
        } else {
            Log.i("UUU", name + " in line : " + line + " is NOT UI Thread");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeedResume = true;
        rootView = inflater.inflate(R.layout.activity_chat, container, false);

        return attachToSwipeBack(rootView);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        realmChat = Realm.getDefaultInstance();

        startPageFastInitialize();
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initMain();
            }
        }, Config.FAST_START_PAGE_TIME);
    }

    @Override
    public void onStart() {
        super.onStart();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RealmRoomMessage.fetchMessages(getRealmChat(), mRoomId, new OnActivityChatStart() {
                    @Override
                    public void resendMessage(final RealmRoomMessage message) {
                        if (!allowResendMessage(message.getMessageId())) {
                            return;
                        }
                        chatSendMessageUtil.build(chatType, message.getRoomId(), message);
                    }

                    @Override
                    public void resendMessageNeedsUpload(final RealmRoomMessage message, final long messageId) {
                        if (!allowResendMessage(message.getMessageId())) {
                            return;
                        }
                        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, message.getAttachment().getLocalFilePath(), message.getMessageId(), message.getMessageType(), message.getMessage(), RealmRoomMessage.getReplyMessageId(message), new HelperUploadFile.UpdateListener() {
                            @Override
                            public void OnProgress(int progress, FileUploadStructure struct) {
                                if (canUpdateAfterDownload) {
                                    insertItemAndUpdateAfterStartUpload(progress, struct);
                                }
                            }

                            @Override
                            public void OnError() {

                            }
                        });
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyItemChanged(mAdapter.findPositionByMessageId(messageId));
                            }
                        }, 300);
                    }

                    @Override
                    public void sendSeenStatus(RealmRoomMessage message) {

                        if (!isNotJoin) {
                            G.chatUpdateStatusUtil.sendUpdateStatus(chatType, mRoomId, message.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                        }
                    }
                });


                HelperNotificationAndBadge.updateBadgeOnly(getRealmChat(), mRoomId);

            }
        }, 500);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FragmentShearedMedia.list != null && FragmentShearedMedia.list.size() > 0) {
            deleteSelectedMessageFromAdapter(FragmentShearedMedia.list);
            FragmentShearedMedia.list.clear();
        }

        canUpdateAfterDownload = true;

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initLayoutHashNavigationCallback();
                showSpamBar();

                updateShowItemInScreen();


                if (isGoingFromUserLink) {
                    new RequestClientSubscribeToRoom().clientSubscribeToRoom(mRoomId);
                }

                RealmRoom.setCount(mRoomId, 0);
                //+final Realm updateUnreadCountRealm = Realm.getDefaultInstance();
                getRealmChat().executeTransactionAsync(new Realm.Transaction() {//ASYNC
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        if (room != null) {
                            if (G.onClearUnread != null) {
                                G.onClearUnread.onClearUnread(mRoomId);
                            }

                            if (G.connectionState == ConnectionState.CONNECTING || G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                                setConnectionText(G.connectionState);
                            } else {
                                if (room.getType() != CHAT) {
                                    /**
                                     * set member count
                                     * set this code in onResume for update this value when user
                                     * come back from profile activities
                                     */

                                    String members = null;
                                    if (room.getType() == GROUP && room.getGroupRoom() != null) {
                                        members = room.getGroupRoom().getParticipantsCountLabel();
                                    } else if (room.getType() == CHANNEL && room.getChannelRoom() != null) {
                                        members = room.getChannelRoom().getParticipantsCountLabel();
                                    }

                                    final String finalMembers = members;
                                    if (finalMembers != null) {
                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (finalMembers != null && HelperString.isNumeric(finalMembers) && Integer.parseInt(finalMembers) == 1) {
                                                    txtLastSeen.setText(finalMembers + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                                                } else {
                                                    txtLastSeen.setText(finalMembers + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                                                }
                                                //    avi.setVisibility(View.GONE);

                                                if (HelperCalander.isPersianUnicode)
                                                    txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                                            }
                                        });
                                    }
                                } else {
                                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, room.getChatRoom().getPeerId());
                                    if (realmRegisteredInfo != null) {
                                        setUserStatus(realmRegisteredInfo.getStatus(), realmRegisteredInfo.getLastSeen());
                                    }
                                }

                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        /**
                         * hint: should use from this method here because we need checkAction
                         * state after set members count for avoid from hide action if exist
                         */
                        checkAction();

                        RealmRoom room = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        if (room != null) {
                            if (txtName == null) {
                                txtName = (EmojiTextViewE) rootView.findViewById(R.id.chl_txt_name);
                            }
                            txtName.setText(room.getTitle());
                        }

                        //updateUnreadCountRealm.close();
                    }
                });

                MusicPlayer.chatLayout = mediaLayout;
                ActivityCall.stripLayoutChat = rootView.findViewById(R.id.ac_ll_strip_call);

                ActivityMain.setMediaLayout();
                ActivityMain.setStripLayoutCall();

                if (!G.twoPaneMode) {
                    try {
                        if (G.fragmentActivity != null && G.fragmentActivity instanceof ActivityMain) {
                            ((ActivityMain) G.fragmentActivity).lockNavigation();
                        }
                    } catch (Exception e) {
                        HelperLog.setErrorLog("fragment chat ondestroy   " + e.toString());
                    }
                }

            }
        }, Config.LOW_START_PAGE_TIME);

        mRoomIdStatic = mRoomId;
        lastChatRoomId = mRoomId;
        titleStatic = title;

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.onUserInfoResponse = this;
        G.onChannelAddMessageReaction = this;
        G.onChannelGetMessagesStats = this;
        G.onSetAction = this;
        G.onUserUpdateStatus = this;
        G.onLastSeenUpdateTiming = this;
        G.onChatDelete = this;
        G.onBackgroundChanged = this;
        G.onConnectionChangeStateChat = this;
        G.helperNotificationAndBadge.cancelNotification();
        G.onChannelUpdateReactionStatusChat = this;

        finishActivity = new FinishActivity() {
            @Override
            public void finishActivity() {
                // ActivityChat.this.finish();
                finishChat();
            }
        };

        initCallbacks();
        HelperNotificationAndBadge.isChatRoomNow = true;

        onUpdateUserOrRoomInfo = new OnUpdateUserOrRoomInfo() {
            @Override
            public void onUpdateUserOrRoomInfo(final String messageId) {

                if (messageId != null && messageId.length() > 0) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int start = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                            for (int i = start; i < mAdapter.getItemCount() && i < start + 15; i++) {
                                try {
                                    if (mAdapter.getItem(i).mMessage != null && mAdapter.getItem(i).mMessage.messageID.equals(messageId)) {
                                        mAdapter.notifyItemChanged(i);
                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        };

        if (backGroundSeenList != null && backGroundSeenList.size() > 0) {
            for (int i = 0; i < backGroundSeenList.size(); i++) {

                G.chatUpdateStatusUtil.sendUpdateStatus(backGroundSeenList.get(i).roomType, mRoomId, backGroundSeenList.get(i).messageID, ProtoGlobal.RoomMessageStatus.SEEN);
            }

            backGroundSeenList.clear();
        }

        if (G.isInCall) {
            rootView.findViewById(R.id.ac_ll_strip_call).setVisibility(View.VISIBLE);

            ActivityCallViewModel.txtTimeChat = (TextView) rootView.findViewById(R.id.cslcs_txt_timer);

            TextView txtCallActivityBack = (TextView) rootView.findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(G.fragmentActivity, ActivityCall.class));
                }
            });

            G.iCallFinishChat = new ICallFinish() {
                @Override
                public void onFinish() {
                    try {
                        rootView.findViewById(R.id.ac_ll_strip_call).setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        } else {
            rootView.findViewById(R.id.ac_ll_strip_call).setVisibility(View.GONE);
        }

        if (isCloudRoom) {
            rootView.findViewById(R.id.ac_txt_cloud).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.chl_imv_user_picture).setVisibility(View.GONE);
        } else {
            setAvatar();
        }

        if (mForwardMessages == null) {
            rootView.findViewById(R.id.ac_ll_forward).setVisibility(View.GONE);
        }

        registerListener();
    }

    @Override
    public void onPause() {
        storingLastPosition();
        super.onPause();

        lastChatRoomId = 0;

        if (isGoingFromUserLink && isNotJoin) {
            new RequestClientUnsubscribeFromRoom().clientUnsubscribeFromRoom(mRoomId);
        }
        onMusicListener = null;
        iUpdateLogItem = null;

        unRegisterListener();
    }

    @Override
    public void onStop() {

        canUpdateAfterDownload = false;

        setDraft();
        HelperNotificationAndBadge.isChatRoomNow = false;

        //if (isNotJoin) { // hint : commented this code, because when going to profile and return can't load message
        //
        //    /**
        //     * delete all  deleted row from database
        //     */
        //    RealmRoom.deleteRoom(mRoomId);
        //}


        // room id have to be set to default, otherwise I'm in the room always!

        MusicPlayer.chatLayout = null;
        ActivityCall.stripLayoutChat = null;


        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        mRoomId = -1;

        if (G.fragmentActivity != null && G.fragmentActivity instanceof ActivityMain) {
            ((ActivityMain) G.fragmentActivity).resume();
        }


        if (realmChat != null && !realmChat.isClosed()) {
            realmChat.close();
        }

    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            HelperSetAction.sendCancel(messageId);

            if (prgWaiting != null) {
                prgWaiting.setVisibility(View.GONE);
            }
        }

        if (requestCode == AttachFile.request_code_position && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                attachFile.requestGetPosition(complete, FragmentChat.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (resultCode == Activity.RESULT_OK) {

            HelperSetAction.sendCancel(messageId);

            if (requestCode == AttachFile.request_code_contact_phone) {
                latestUri = data.getData();
                sendMessage(requestCode, "");
                return;
            }

            listPathString = null;
            if (AttachFile.request_code_TAKE_PICTURE == requestCode) {

                listPathString = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    listPathString.add(AttachFile.mCurrentPhotoPath);
                } else {
                    listPathString.add(AttachFile.imagePath);
                }

                latestUri = null; // check
            } else if (request_code_VIDEO_CAPTURED == requestCode) {

                listPathString = new ArrayList<>();
                listPathString.add(AttachFile.videoPath);

                latestUri = null; // check
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (data.getClipData() != null) { // multi select file
                        listPathString = attachFile.getClipData(data.getClipData());

                        if (listPathString != null) {
                            for (int i = 0; i < listPathString.size(); i++) {
                                if (listPathString.get(i) != null) {
                                    listPathString.set(i, getFilePathFromUri(Uri.fromFile(new File(listPathString.get(i)))));
                                }
                            }
                        }
                    }
                }

                if (listPathString == null || listPathString.size() < 1) {
                    listPathString = new ArrayList<>();

                    if (data.getData() != null) {
                        listPathString.add(getFilePathFromUri(data.getData()));
                    }
                }
            }
            latestRequestCode = requestCode;

            /**
             * compress video if BuildVersion is bigger that 18
             */
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (requestCode == request_code_VIDEO_CAPTURED) {
                    if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1) {
                        Intent intent = new Intent(G.fragmentActivity, ActivityTrimVideo.class);
                        intent.putExtra("PATH", listPathString.get(0));
                        startActivityForResult(intent, AttachFile.request_code_trim_video);
                        return;
                    } else if (sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1) {

                        File mediaStorageDir = new File(G.DIR_VIDEOS);
                        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "video_" + HelperString.getRandomFileName(3) + ".mp4");
                        listPathString = new ArrayList<>();

                        Uri uri = Uri.fromFile(new File(AttachFile.videoPath));
                        File tempFile = com.lalongooo.videocompressor.file.FileUtils.saveTempFile(G.DIR_TEMP, HelperString.getRandomFileName(5) + ".mp4", G.fragmentActivity, uri);
                        mainVideoPath = tempFile.getPath();
                        //                        String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                        //                        String savePathVideoCompress = getCacheDir() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                        String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

                        listPathString.add(savePathVideoCompress);

                        new VideoCompressor().execute(tempFile.getPath(), savePathVideoCompress);
                        showDraftLayout();
                        setDraftMessage(requestCode);
                        latestRequestCode = requestCode;
                        return;
                    } else {
                        compressedPath.put(listPathString.get(0), true);
                    }
                }

                if (requestCode == AttachFile.request_code_trim_video) {
                    latestRequestCode = request_code_VIDEO_CAPTURED;
                    showDraftLayout();
                    setDraftMessage(request_code_VIDEO_CAPTURED);
                    if ((sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1)) {
                        File mediaStorageDir = new File(G.DIR_VIDEOS);
                        listPathString = new ArrayList<>();

                        //                        String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                        //                        String savePathVideoCompress = getCacheDir() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                        String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

                        listPathString.add(savePathVideoCompress);
                        mainVideoPath = data.getData().getPath();
                        new VideoCompressor().execute(data.getData().getPath(), savePathVideoCompress);
                    } else {
                        compressedPath.put(data.getData().getPath(), true);
                    }
                    return;
                }
            }

            if (listPathString.size() == 1) {
                /**
                 * compress video if BuildVersion is bigger that 18
                 */
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (requestCode == AttachFile.requestOpenGalleryForVideoMultipleSelect) {
                        if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1) {
                            Intent intent = new Intent(G.fragmentActivity, ActivityTrimVideo.class);
                            intent.putExtra("PATH", listPathString.get(0));
                            startActivityForResult(intent, AttachFile.request_code_trim_video);
                            return;
                        } else if ((sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1)) {

                            mainVideoPath = listPathString.get(0);

                            //                            String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                            String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

                            listPathString.set(0, savePathVideoCompress);

                            new VideoCompressor().execute(mainVideoPath, savePathVideoCompress);

                            showDraftLayout();
                            setDraftMessage(requestCode);
                        } else {
                            compressedPath.put(listPathString.get(0), true);
                        }
                    }
                    showDraftLayout();
                    setDraftMessage(requestCode);
                } else {
                    /**
                     * set compressed true for use this path
                     */
                    compressedPath.put(listPathString.get(0), true);

                    showDraftLayout();
                    setDraftMessage(requestCode);
                }
            } else if (listPathString.size() > 1) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (final String path : listPathString) {
                            /**
                             * set compressed true for use this path
                             */
                            compressedPath.put(path, true);

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect && !path.toLowerCase().endsWith(".gif")) {
                                        String localPathNew = attachFile.saveGalleryPicToLocal(path);
                                        sendMessage(requestCode, localPathNew);
                                    } else {
                                        sendMessage(requestCode, path);
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }

            if (listPathString.size() == 1 && listPathString.get(0) != null) {

                if (sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {

                    if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect) {
                        if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));
                                Uri uri = Uri.parse(listPathString.get(0));
                                new HelperFragment(FragmentEditImage.newInstance(AttachFile.getFilePathFromUriAndCheckForAndroid7(uri, HelperGetDataFromOtherApp.FileType.image), true, false)).setReplace(false).load();

                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (prgWaiting != null) {
                                            prgWaiting.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));
                                Uri uri = Uri.parse(listPathString.get(0));
                                new HelperFragment(FragmentEditImage.newInstance(uri.toString(), true, false)).setReplace(false).load();

                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (prgWaiting != null) {
                                            prgWaiting.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        } else {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (prgWaiting != null) {
                                        prgWaiting.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    } else if (requestCode == AttachFile.request_code_TAKE_PICTURE) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                            ImageHelper.correctRotateImage(listPathString.get(0), true);
                            new HelperFragment(FragmentEditImage.newInstance(listPathString.get(0), true, false)).setReplace(false).load();
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (prgWaiting != null) {
                                        prgWaiting.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            ImageHelper.correctRotateImage(listPathString.get(0), true);
                            new HelperFragment(FragmentEditImage.newInstance(listPathString.get(0), true, false)).setReplace(false).load();
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (prgWaiting != null) {
                                        prgWaiting.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                } else {

                    if (requestCode == AttachFile.request_code_TAKE_PICTURE) {

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ImageHelper.correctRotateImage(listPathString.get(0), true);
                            }
                        });
                        thread.start();
                    } else if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect && !listPathString.get(0).toLowerCase().endsWith(".gif")) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));
                            }
                        });
                        thread.start();
                    }
                }
            }
        }
    }

    /**
     * set just important item to view in onCreate and load another objects in onResume
     * actions : set app color, load avatar, set background, set title, set status chat or member for group or channel
     */
    private void startPageFastInitialize() {

        attachFile = new AttachFile(G.fragmentActivity);
        backGroundSeenList.clear();

        //+Realm realm = Realm.getDefaultInstance();

        Bundle extras = getArguments();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");
            chatPeerId = extras.getLong("peerId");

            RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
            pageSettings();

            // avi = (AVLoadingIndicatorView)  rootView.findViewById(R.id.avi);
            txtName = (EmojiTextViewE) rootView.findViewById(R.id.chl_txt_name);
            txtLastSeen = (TextView) rootView.findViewById(R.id.chl_txt_last_seen);
            viewGroupLastSeen = (ViewGroup) rootView.findViewById(R.id.chl_txt_viewGroup_seen);
            imvUserPicture = (CircleImageView) rootView.findViewById(R.id.chl_imv_user_picture);
            txtVerifyRoomIcon = (AppCompatImageView) rootView.findViewById(R.id.ac_txt_verify);
            txtVerifyRoomIcon.setVisibility(View.GONE);

            /**
             * need this info for load avatar
             */
            if (realmRoom != null) {
                chatType = realmRoom.getType();
                if (chatType == CHAT) {
                    chatPeerId = realmRoom.getChatRoom().getPeerId();
                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
                    ;
                    if (realmRegisteredInfo != null) {
                        title = realmRegisteredInfo.getDisplayName();
                        lastSeen = realmRegisteredInfo.getLastSeen();
                        userStatus = realmRegisteredInfo.getStatus();
                    } else {
                        /**
                         * when userStatus isn't EXACTLY lastSeen time not used so don't need
                         * this time and also this time not exist in room info
                         */
                        title = realmRoom.getTitle();
                        userStatus = G.fragmentActivity.getResources().getString(R.string.last_seen_recently);
                    }
                } else {
                    mRoomId = realmRoom.getId();
                    title = realmRoom.getTitle();
                    if (chatType == GROUP) {
                        groupParticipantsCountLabel = realmRoom.getGroupRoom().getParticipantsCountLabel();
                        isPublicGroup = !realmRoom.getGroupRoom().isPrivate();
                    } else {
                        groupParticipantsCountLabel = realmRoom.getChannelRoom().getParticipantsCountLabel();
                        showVoteChannel = realmRoom.getChannelRoom().isReactionStatus();
                        if (realmRoom.getChannelRoom().isVerified()) {
                            txtVerifyRoomIcon.setVisibility(View.VISIBLE);
                        }

                    }
                }

                if (chatType == CHAT) {
                    setUserStatus(userStatus, lastSeen);
                } else if ((chatType == GROUP) || (chatType == CHANNEL)) {
                    if (groupParticipantsCountLabel != null) {

                        if (HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                        } else {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                        }
                        // avi.setVisibility(View.GONE);
                        ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                    }
                }
            } else if (chatPeerId != 0) {
                /**
                 * when user start new chat this block will be called
                 */
                chatType = CHAT;
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
                title = realmRegisteredInfo.getDisplayName();
                lastSeen = realmRegisteredInfo.getLastSeen();
                userStatus = realmRegisteredInfo.getStatus();
                setUserStatus(userStatus, lastSeen);
            }

            if (title != null) {
                txtName.setText(title);
            }
            /**
             * change english number to persian number
             */
            if (HelperCalander.isPersianUnicode) {
                txtName.setText(txtName.getText().toString());
                txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
            }
        }

        /**
         * hint: don't check isCloudRoom with (( RealmRoom.isCloudRoom(mRoomId, realm); ))
         * because in first time room not exist in RealmRoom and value is false always.
         * so just need to check this value with chatPeerId
         */
        if (chatPeerId == G.userId) {
            isCloudRoom = true;
        }

        //+realm.close();
    }

    private void checkConnection(String action) {
        if (action != null) {
            ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LOCALE);
            txtLastSeen.setText(action);
        } else {

            if (chatType == CHAT) {
                if (isCloudRoom) {
                    txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                } else {
                    if (userStatus != null) {
                        if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                            txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                        } else {
                            txtLastSeen.setText(userStatus);
                        }
                    }
                }
                ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
            } else if (chatType == GROUP) {
                ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                } else {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                }

            } else if (chatType == CHANNEL) {
                ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                } else {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                }

            }
        }

        if (HelperCalander.isPersianUnicode) {
            txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
        }

    }

    private void setConnectionText(final ConnectionState connectionState) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                G.connectionState = connectionState;
                if (connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                    checkConnection(G.context.getResources().getString(R.string.waiting_for_network));
                } else if (connectionState == ConnectionState.CONNECTING) {
                    checkConnection(G.context.getResources().getString(R.string.connecting));
                } else if (connectionState == ConnectionState.UPDATING) {
                    checkConnection(null);
                } else if (connectionState == ConnectionState.IGAP) {
                    checkConnection(null);
                }
            }
        });
    }

    private void initMain() {
        HelperGetMessageState.clearMessageViews();

        /**
         * define views
         */
        mediaLayout = (LinearLayout) rootView.findViewById(R.id.ac_ll_music_layout);
        MusicPlayer.setMusicPlayer(mediaLayout);

        lyt_user = (LinearLayout) rootView.findViewById(R.id.lyt_user);
        viewAttachFile = rootView.findViewById(R.id.layout_attach_file);
        viewMicRecorder = rootView.findViewById(R.id.layout_mic_recorde);
        prgWaiting = (ProgressBar) rootView.findViewById(R.id.chl_prgWaiting);
        AppUtils.setProgresColler(prgWaiting);
        voiceRecord = new VoiceRecord(G.fragmentActivity, viewMicRecorder, viewAttachFile, this);

        prgWaiting.setVisibility(View.VISIBLE);

        txtEmptyMessages = (TextView) rootView.findViewById(R.id.empty_messages);

        lastDateCalendar.clear();

        locationManager = (LocationManager) G.fragmentActivity.getSystemService(LOCATION_SERVICE);

        Bundle extras = getArguments();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");
            isGoingFromUserLink = extras.getBoolean("GoingFromUserLink");
            isNotJoin = extras.getBoolean("ISNotJoin");
            userName = extras.getString("UserName");

            if (isNotJoin) {
                final LinearLayout layoutJoin = (LinearLayout) rootView.findViewById(R.id.ac_ll_join);
                if (layoutMute == null) {
                    layoutMute = (RelativeLayout) rootView.findViewById(R.id.chl_ll_channel_footer);
                }
                layoutJoin.setBackgroundColor(Color.parseColor(G.appBarColor));
                layoutJoin.setVisibility(View.VISIBLE);
                layoutMute.setVisibility(View.GONE);
                layoutJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HelperUrl.showIndeterminateProgressDialog();
                        G.onClientJoinByUsername = new OnClientJoinByUsername() {
                            @Override
                            public void onClientJoinByUsernameResponse() {

                                isNotJoin = false;
                                HelperUrl.closeDialogWaiting();
                                RealmRoom.joinRoom(mRoomId);

                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        layoutJoin.setVisibility(View.GONE);
                                        if (chatType == CHANNEL) {
                                            layoutMute.setVisibility(View.VISIBLE);
                                            initLayoutChannelFooter();
                                        }
                                        rootView.findViewById(ac_ll_parent).invalidate();


                                        if (chatType == GROUP) {
                                            viewAttachFile.setVisibility(View.VISIBLE);
                                            isChatReadOnly = false;
                                        }

                                    }
                                });
                            }

                            @Override
                            public void onError(int majorCode, int minorCode) {
                                HelperUrl.dialogWaiting.dismiss();
                            }
                        };

                        /**
                         * if user joined to this room set lastMessage for that
                         */
                        RealmRoom.setLastMessage(mRoomId);
                        new RequestClientJoinByUsername().clientJoinByUsername(userName);
                    }
                });
            }
            messageId = extras.getLong("MessageId");

            /**
             * get userId . use in chat set action.
             */

            //+Realm realm = Realm.getDefaultInstance();

            RealmUserInfo realmUserInfo = getRealmChat().where(RealmUserInfo.class).findFirst();
            if (realmUserInfo == null) {
                //finish();
                finishChat();
                return;
            }
            userId = realmUserInfo.getUserId();

            RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

            if (realmRoom != null) { // room exist

                title = realmRoom.getTitle();
                initialize = realmRoom.getInitials();
                color = realmRoom.getColor();
                isChatReadOnly = realmRoom.getReadOnly();
                unreadCount = realmRoom.getUnreadCount();
                firstUnreadMessage = realmRoom.getFirstUnreadMessage();
                savedScrollMessageId = realmRoom.getLastScrollPositionMessageId();

                if (isChatReadOnly) {
                    viewAttachFile.setVisibility(View.GONE);
                    (rootView.findViewById(R.id.chl_recycler_view_chat)).setPadding(0, 0, 0, 0);
                }

                if (chatType == CHAT) {

                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
                    if (realmRegisteredInfo != null) {
                        initialize = realmRegisteredInfo.getInitials();
                        color = realmRegisteredInfo.getColor();
                        phoneNumber = realmRegisteredInfo.getPhoneNumber();
                    } else {
                        title = realmRoom.getTitle();
                        initialize = realmRoom.getInitials();
                        color = realmRoom.getColor();
                        userStatus = G.fragmentActivity.getResources().getString(R.string.last_seen_recently);
                    }
                } else if (chatType == GROUP) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    groupRole = realmGroupRoom.getRole();
                    groupParticipantsCountLabel = realmGroupRoom.getParticipantsCountLabel();
                } else if (chatType == CHANNEL) {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    channelRole = realmChannelRoom.getRole();
                    channelParticipantsCountLabel = realmChannelRoom.getParticipantsCountLabel();
                }
            } else {
                chatPeerId = extras.getLong("peerId");
                chatType = CHAT;
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
                if (realmRegisteredInfo != null) {
                    title = realmRegisteredInfo.getDisplayName();
                    initialize = realmRegisteredInfo.getInitials();
                    color = realmRegisteredInfo.getColor();
                    lastSeen = realmRegisteredInfo.getLastSeen();
                    userStatus = realmRegisteredInfo.getStatus();
                }
            }

            //realm.close();
        }

        initComponent();
        initAppbarSelected();
        getDraft();
        getUserInfo();
        insertShearedData();
    }

    private void registerListener() {

        G.dispatchTochEventChat = new IDispatchTochEvent() {
            @Override
            public void getToch(MotionEvent event) {
                if (voiceRecord != null) {
                    voiceRecord.dispatchTouchEvent(event);
                }
            }
        };

        G.onBackPressedChat = new IOnBackPressed() {
            @Override
            public boolean onBack() {
                return onBackPressed();
            }
        };

        G.iSendPositionChat = new ISendPosition() {
            @Override
            public void send(Double latitude, Double longitude, String imagePath) {
                sendPosition(latitude, longitude, imagePath);
            }
        };
    }

    private void unRegisterListener() {

        G.dispatchTochEventChat = null;
        G.onBackPressedChat = null;
        G.iSendPositionChat = null;
    }

    public boolean onBackPressed() {
        boolean stopSuperPress = true;
        try {
            FragmentShowImage fragment = (FragmentShowImage) G.fragmentActivity.getSupportFragmentManager().findFragmentByTag(FragmentShowImage.class.getName());
            if (fragment != null) {
                removeFromBaseFragment(fragment);
            } else if (mAdapter != null && mAdapter.getSelections().size() > 0) {
                mAdapter.deselect();
            } else if (emojiPopup != null && emojiPopup.isShowing()) {
                emojiPopup.dismiss();
            } else {
                stopSuperPress = false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return stopSuperPress;
    }

    /**
     * get settings state and change view
     */
    private void pageSettings() {
        /**
         * get sendByEnter action from setting value
         */
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        sendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0) == 1;

        /**
         * set background
         */

        recyclerView = (RecyclerView) rootView.findViewById(R.id.chl_recycler_view_chat);

        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {
            imgBackGround = (ImageView) rootView.findViewById(R.id.chl_img_view_chat);

            File f = new File(backGroundPath);
            if (f.exists()) {
                try {
                    Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                    imgBackGround.setImageDrawable(d);
                } catch (OutOfMemoryError e) {
                    ActivityManager activityManager = (ActivityManager) G.context.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                    activityManager.getMemoryInfo(memoryInfo);
                    Crashlytics.logException(new Exception("FragmentChat -> Device Name : " + Build.BRAND + " || memoryInfo.availMem : " + memoryInfo.availMem + " || memoryInfo.totalMem : " + memoryInfo.totalMem + " || memoryInfo.lowMemory : " + memoryInfo.lowMemory));
                }
            }
        }

        /**
         * set app color to appBar
         */
        appBarLayout = (MyAppBarLayout) rootView.findViewById(R.id.ac_appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
    }

    /**
     * initialize some callbacks that used in this page
     */
    public void initCallbacks() {
        chatSendMessageUtil.setOnChatSendMessageResponseChatPage(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        G.onChatSendMessage = new OnChatSendMessage() {
            @Override
            public void Error(int majorCode, int minorCode, final int waitTime) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showErrorDialog(waitTime);
                    }
                });
            }
        };

        G.onChatEditMessageResponse = new OnChatEditMessageResponse() {
            @Override
            public void onChatEditMessage(long roomId, final long messageId, long messageVersion, final String message, ProtoResponse.Response response) {
                if (mRoomId == roomId && mAdapter != null) {
                    // I'm in the room
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // update message text in adapter
                            mAdapter.updateMessageText(messageId, message);
                        }
                    });
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        G.onChatDeleteMessageResponse = new OnChatDeleteMessageResponse() {
            @Override
            public void onChatDeleteMessage(long deleteVersion, final long messageId, long roomId, ProtoResponse.Response response) {
                if (response.getId().isEmpty()) { // another account deleted this message

                    // if deleted message is for current room clear from adapter
                    if (roomId == mRoomId) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // remove deleted message from adapter
                                if (mAdapter == null) {
                                    return;
                                }

                                ArrayList list = new ArrayList();
                                list.add(messageId);
                                deleteSelectedMessageFromAdapter(list);

                            }
                        });
                    }
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        /**
         * call from ActivityGroupProfile for update group member number or clear history
         */
        onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {
                clearHistory(parseLong(messageOne));
            }
        };

        onMusicListener = new OnComplete() {
            @Override
            public void complete(boolean result, String messageID, String beforeMessageID) {

                if (result) {
                    updateShowItemInScreen();
                } else {
                    onPlayMusic(messageID);
                }
            }
        };

        iUpdateLogItem = new IUpdateLogItem() {
            @Override
            public void onUpdate(String logText, long messageId) {
                if (mAdapter == null) {
                    return;
                }
                for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {

                    try {
                        AbstractMessage item = mAdapter.getAdapterItem(i);

                        if (item.mMessage != null && item.mMessage.messageID.equals(messageId + "")) {
                            item.mMessage.messageText = logText;
                            mAdapter.notifyAdapterItemChanged(i);
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        /**
         * after get position from gps
         */
        complete = new OnComplete() {
            @Override
            public void complete(boolean result, final String messageOne, String MessageTow) {
                try {
                    String[] split = messageOne.split(",");
                    Double latitude = Double.parseDouble(split[0]);
                    Double longitude = Double.parseDouble(split[1]);
                    FragmentMap fragment = FragmentMap.getInctance(latitude, longitude, FragmentMap.Mode.sendPosition);
                    new HelperFragment(fragment).setReplace(false).load();
                } catch (Exception e) {
                    HelperLog.setErrorLog("Activity Chat   complete   " + e.toString());
                }
            }
        };

        G.onHelperSetAction = new OnHelperSetAction() {
            @Override
            public void onAction(ProtoGlobal.ClientAction ClientAction) {
                HelperSetAction.setActionFiles(mRoomId, messageId, ClientAction, chatType);
            }
        };

        G.onClearChatHistory = new OnClearChatHistory() {
            @Override
            public void onClearChatHistory() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.clear();
                        recyclerView.removeAllViews();

                        /**
                         * remove tag from edtChat if the message has deleted
                         */
                        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                            edtChat.setTag(null);
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        G.onDeleteChatFinishActivity = new OnDeleteChatFinishActivity() {
            @Override
            public void onFinish() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //finish();
                        finishChat();
                    }
                });
            }
        };

        G.onUpdateUserStatusInChangePage = new OnUpdateUserStatusInChangePage() {
            @Override
            public void updateStatus(long peerId, String status, long lastSeen) {
                if (chatType == CHAT) {
                    setUserStatus(status, lastSeen);
                    new RequestUserInfo().userInfo(peerId);
                }
            }
        };
    }

    private void initComponent() {
        toolbar = (LinearLayout) rootView.findViewById(R.id.toolbar);
        iconMute = (MaterialDesignTextView) rootView.findViewById(R.id.imgMutedRoom);
        RippleView rippleBackButton = (RippleView) rootView.findViewById(R.id.chl_ripple_back_Button);

        //+final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {

            iconMute.setVisibility(realmRoom.getMute() ? View.VISIBLE : View.GONE);
            isMuteNotification = realmRoom.getMute();
        }

        if (chatType == CHAT && !isChatReadOnly) {

            if (G.userId != chatPeerId) {

                RippleView rippleCall = (RippleView) rootView.findViewById(R.id.acp_ripple_call);
                // gone or visible view call
                RealmCallConfig callConfig = getRealmChat().where(RealmCallConfig.class).findFirst();
                if (callConfig != null) {
                    if (callConfig.isVoice_calling()) {
                        rippleCall.setVisibility(View.VISIBLE);
                        rippleCall.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                            @Override
                            public void onComplete(RippleView rippleView) {
                                FragmentCall.call(chatPeerId, false);
                            }
                        });
                    } else {
                        rippleCall.setVisibility(View.GONE);
                    }
                } else {
                    new RequestSignalingGetConfiguration().signalingGetConfiguration();
                }
            }
        }

        ll_attach_text = (LinearLayout) rootView.findViewById(R.id.ac_ll_attach_text);
        txtFileNameForSend = (TextView) rootView.findViewById(R.id.ac_txt_file_neme_for_sending);
        btnCancelSendingFile = (MaterialDesignTextView) rootView.findViewById(R.id.ac_btn_cancel_sending_file);
        btnCancelSendingFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_attach_text.setVisibility(View.GONE);
                edtChat.setFilters(new InputFilter[]{});
                edtChat.setText(edtChat.getText());
                edtChat.setSelection(edtChat.getText().length());

                if (edtChat.getText().length() == 0) {

                    layoutAttachBottom.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            layoutAttachBottom.setVisibility(View.VISIBLE);
                        }
                    }).start();
                    imvSendButton.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    imvSendButton.clearAnimation();
                                    imvSendButton.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        // final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.2);

        RippleView rippleMenuButton = (RippleView) rootView.findViewById(R.id.chl_ripple_menu_button);
        rippleMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rippleView) {

                final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
                View v = dialog.getCustomView();

                DialogAnimation.animationUp(dialog);
                dialog.show();

                ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
                ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
                ViewGroup root3 = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);
                ViewGroup root4 = (ViewGroup) v.findViewById(R.id.dialog_root_item4_notification);
                ViewGroup root5 = (ViewGroup) v.findViewById(R.id.dialog_root_item5_notification);
                ViewGroup root6 = (ViewGroup) v.findViewById(R.id.dialog_root_item6_notification);
                ViewGroup root7 = (ViewGroup) v.findViewById(R.id.dialog_root_item7_notification);

                TextView txtSearch = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
                TextView txtClearHistory = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
                TextView txtDeleteChat = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
                TextView txtMuteNotification = (TextView) v.findViewById(R.id.dialog_text_item4_notification);
                TextView txtChatToGroup = (TextView) v.findViewById(R.id.dialog_text_item5_notification);
                TextView txtCleanUp = (TextView) v.findViewById(R.id.dialog_text_item6_notification);
                TextView txtReport = (TextView) v.findViewById(R.id.dialog_text_item7_notification);

                TextView iconSearch = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
                iconSearch.setText(G.fragmentActivity.getResources().getString(R.string.md_searching_magnifying_glass));

                TextView iconClearHistory = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
                iconClearHistory.setText(G.fragmentActivity.getResources().getString(R.string.md_clearHistory));

                TextView iconDeleteChat = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
                iconDeleteChat.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));

                TextView iconMuteNotification = (TextView) v.findViewById(R.id.dialog_icon_item4_notification);

                TextView iconChatToGroup = (TextView) v.findViewById(R.id.dialog_icon_item5_notification);
                iconChatToGroup.setText(G.fragmentActivity.getResources().getString(R.string.md_users_social_symbol));

                TextView iconCleanUp = (TextView) v.findViewById(R.id.dialog_icon_item6_notification);
                iconCleanUp.setText(G.fragmentActivity.getResources().getString(R.string.md_clean_up));

                TextView iconReport = (TextView) v.findViewById(R.id.dialog_icon_item7_notification);
                iconReport.setText(G.fragmentActivity.getResources().getString(R.string.md_igap_alert_box));

                root1.setVisibility(View.VISIBLE);
                root2.setVisibility(View.VISIBLE);
                root3.setVisibility(View.VISIBLE);
                root4.setVisibility(View.VISIBLE);
                root5.setVisibility(View.VISIBLE);
                root6.setVisibility(View.VISIBLE);

                txtSearch.setText(G.fragmentActivity.getResources().getString(R.string.Search));
                txtClearHistory.setText(G.fragmentActivity.getResources().getString(R.string.clear_history));
                txtDeleteChat.setText(G.fragmentActivity.getResources().getString(R.string.delete_chat));
                txtMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.mute_notification));
                txtChatToGroup.setText(G.fragmentActivity.getResources().getString(R.string.chat_to_group));
                txtCleanUp.setText(G.fragmentActivity.getResources().getString(R.string.clean_up));
                txtReport.setText(G.fragmentActivity.getResources().getString(R.string.report));

                if (chatType == CHAT) {
                    root3.setVisibility(View.VISIBLE);
                    if (!isChatReadOnly && !blockUser) {
                        root5.setVisibility(View.VISIBLE);
                    } else {
                        root5.setVisibility(View.GONE);
                    }
                } else {
                    root3.setVisibility(View.GONE);
                    root5.setVisibility(View.GONE);

                    if (chatType == GROUP && isPublicGroup) {
                        root2.setVisibility(View.GONE);
                    }

                    if (chatType == CHANNEL) {
                        root2.setVisibility(View.GONE);
                    }
                    if (channelRole != ChannelChatRole.OWNER || groupRole != GroupChatRole.OWNER || isNotJoin) {
                        root7.setVisibility(View.VISIBLE);
                    } else {
                        root7.setVisibility(View.GONE);
                    }
                }

                //+Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {

                    if (realmRoom.getMute()) {
                        txtMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.unmute_notification));
                        iconMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.md_unMuted));
                    } else {
                        txtMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.mute_notification));
                        iconMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.md_muted));
                    }
                } else {
                    root2.setVisibility(View.GONE);
                    root3.setVisibility(View.GONE);
                    root4.setVisibility(View.GONE);
                    root5.setVisibility(View.GONE);
                    root6.setVisibility(View.GONE);
                }

                if (isNotJoin) {
                    root2.setVisibility(View.GONE);
                    root4.setVisibility(View.GONE);
                    root6.setVisibility(View.GONE);
                }

                if (RealmRoom.isNotificationServices(mRoomId)) {
                    root7.setVisibility(View.GONE);
                }

                //realm.close();

                root1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        initLayoutSearchNavigation();

                        rootView.findViewById(R.id.toolbarContainer).setVisibility(View.GONE);
                        ll_Search.setVisibility(View.VISIBLE);
                        // ll_navigate_Message.setVisibility(View.VISIBLE);
                        //  viewAttachFile.setVisibility(View.GONE);

                        if (!initHash) {
                            initHash = true;
                            initHashView();
                        }

                        edtSearchMessage.requestFocus();
                    }
                });
                root2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                onSelectRoomMenu("txtClearHistory", mRoomId);
                            }
                        }).negativeText(R.string.no).show();
                    }
                });
                root3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.delete_chat).content(R.string.delete_chat_content).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                onSelectRoomMenu("txtDeleteChat", mRoomId);
                            }
                        }).negativeText(R.string.no).show();
                    }
                });
                root4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        onSelectRoomMenu("txtMuteNotification", mRoomId);
                    }
                });
                root5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.convert_chat_to_group_title).content(R.string.convert_chat_to_group_content).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //finish();
                                finishChat();
                                dialog.dismiss();
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        G.onConvertToGroup.openFragmentOnActivity("ConvertToGroup", mRoomId);
                                    }
                                });
                            }
                        }).show();
                    }
                });

                root6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        RealmRoomMessage.ClearAllMessage(getRealmChat(), false, mRoomId);
                        mAdapter.clear();
                        recyclerView.removeAllViews();

                        llScrollNavigate.setVisibility(View.GONE);

                        recyclerView.addOnScrollListener(scrollListener);

                        saveMessageIdPositionState(0);
                        /**
                         * get history from server
                         */
                        resetMessagingValue();
                        topMore = true;
                        getOnlineMessage(0, UP);
                    }
                });

                root7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        dialogReport(false, 0);
                    }
                });
            }
        });

        imvSmileButton = (MaterialDesignTextView) rootView.findViewById(R.id.chl_imv_smile_button);

        edtChat = (EmojiEditTextE) rootView.findViewById(R.id.chl_edt_chat);
        edtChat.requestFocus();

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmojiSHow) {

                    imvSmileButton.performClick();
                }
            }
        });

        imvSendButton = (MaterialDesignTextView) rootView.findViewById(R.id.chl_imv_send_button);
        imvSendButton.setTextColor(Color.parseColor(G.attachmentColor));

        imvAttachFileButton = (MaterialDesignTextView) rootView.findViewById(R.id.chl_imv_attach_button);
        layoutAttachBottom = (LinearLayout) rootView.findViewById(R.id.layoutAttachBottom);

        imvMicButton = (MaterialDesignTextView) rootView.findViewById(R.id.chl_imv_mic_button);

        mAdapter = new MessagesAdapter<>(this, this, this);

        mAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<AbstractMessage>() {
            @Override
            public boolean filter(AbstractMessage item, CharSequence constraint) {
                return !item.mMessage.messageText.toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        //FragmentMain.PreCachingLayoutManager layoutManager = new FragmentMain.PreCachingLayoutManager(ActivityChat.this, 7500);
        MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(G.fragmentActivity);
        layoutManager.setStackFromEnd(true);

        if (recyclerView == null) {
            recyclerView = (RecyclerView) rootView.findViewById(R.id.chl_recycler_view_chat);
        }

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        /**
         * load message , use handler for load async
         */

        if (mAdapter.getItemCount() > 0) {
            txtEmptyMessages.setVisibility(View.GONE);
        } else {
            txtEmptyMessages.setVisibility(View.VISIBLE);
        }

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (mAdapter.getItemCount() > 0) {
                    txtEmptyMessages.setVisibility(View.GONE);
                } else {
                    txtEmptyMessages.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (mAdapter.getItemCount() > 0) {
                    txtEmptyMessages.setVisibility(View.GONE);
                } else {
                    txtEmptyMessages.setVisibility(View.VISIBLE);
                }
            }
        });

        llScrollNavigate = (FrameLayout) rootView.findViewById(R.id.ac_ll_scrool_navigate);
        txtNewUnreadMessage = (TextView) rootView.findViewById(R.id.cs_txt_unread_message);

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                getMessages();
                manageForwardedMessage();
            }
        });

        AndroidUtils.setBackgroundShapeColor(txtNewUnreadMessage, Color.parseColor(G.notificationColor));

        MaterialDesignTextView txtNavigationLayout = (MaterialDesignTextView) rootView.findViewById(R.id.ac_txt_down_navigation);
        AndroidUtils.setBackgroundShapeColor(txtNavigationLayout, Color.parseColor(G.appBarColor));

        llScrollNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latestButtonClickTime = System.currentTimeMillis();
                /**
                 * have unread
                 */
                if (countNewMessage > 0 && firstUnreadMessageInChat != null) {
                    /**
                     * if unread message is exist in list set position to this item and create
                     * unread layout otherwise should clear list and load from unread again
                     */

                    firstUnreadMessage = firstUnreadMessageInChat;
                    if (!firstUnreadMessage.isValid() || firstUnreadMessage.isDeleted()) {
                        resetAndGetFromEnd();
                        return;
                    }

                    int position = mAdapter.findPositionByMessageId(firstUnreadMessage.getMessageId());
                    if (position > 0) {
                        mAdapter.add(position, new UnreadMessage(getRealmChat(), FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), makeUnreadMessage(countNewMessage))).withIdentifier(SUID.id().get()));
                        isShowLayoutUnreadMessage = true;
                        LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                        linearLayout.scrollToPositionWithOffset(position, 0);
                    } else {
                        resetMessagingValue();
                        unreadCount = countNewMessage;
                        firstUnreadMessage = firstUnreadMessageInChat;
                        getMessages();

                        if (firstUnreadMessage == null) {
                            resetAndGetFromEnd();
                            return;
                        }

                        int position1 = mAdapter.findPositionByMessageId(firstUnreadMessage.getMessageId());
                        if (position1 > 0) {
                            LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                            linearLayout.scrollToPositionWithOffset(position1 - 1, 0);
                        }
                    }
                    firstUnreadMessageInChat = null;
                    countNewMessage = 0;
                    txtNewUnreadMessage.setVisibility(View.GONE);
                    txtNewUnreadMessage.setText(countNewMessage + "");
                } else {
                    llScrollNavigate.setVisibility(View.GONE);
                    /**
                     * if addToView is true this means that all new message is in adapter
                     * and just need go to end position in list otherwise we should clear all
                     * items and reload again from bottom
                     */
                    if (!addToView) {
                        resetMessagingValue();
                        getMessages();
                    } else {
                        scrollToEnd();
                    }
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                if (!firsInitScrollPosition) {
                    lastPosition = lastVisiblePosition;
                    firsInitScrollPosition = true;
                }

                int state = lastPosition - lastVisiblePosition;
                if (state > 0) {   // up

                    if (countNewMessage == 0) {
                        llScrollNavigate.setVisibility(View.GONE);
                    } else {
                        llScrollNavigate.setVisibility(View.VISIBLE);

                        txtNewUnreadMessage.setText(countNewMessage + "");
                        txtNewUnreadMessage.setVisibility(View.VISIBLE);
                    }

                    lastPosition = lastVisiblePosition;
                } else if (state < 0) { //down

                    if (mAdapter.getItemCount() - lastVisiblePosition > 10) {
                        /**
                         * show llScrollNavigate if timeout from latest click
                         */
                        if (HelperTimeOut.timeoutChecking(0, latestButtonClickTime, (int) (2 * DateUtils.SECOND_IN_MILLIS))) {
                            llScrollNavigate.setVisibility(View.VISIBLE);
                        }
                        if (countNewMessage > 0) {
                            txtNewUnreadMessage.setText(countNewMessage + "");
                            txtNewUnreadMessage.setVisibility(View.VISIBLE);
                        } else {
                            txtNewUnreadMessage.setVisibility(View.GONE);
                        }
                    } else {
                        /**
                         * if addToView is true means that
                         */
                        if (addToView) {

                            /**
                             * if countNewMessage is bigger than zero in onItemShowingMessageId
                             * callback txtNewUnreadMessage visibility will be managed
                             */
                            if (countNewMessage == 0) {
                                if (mAdapter.getItemCount() - lastVisiblePosition < 10) {
                                    llScrollNavigate.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    lastPosition = lastVisiblePosition;
                }
            }
        });

        rippleBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                popBackStackFragment();
                //finishChat();
            }
        });

        imvUserPicture = (CircleImageView) rootView.findViewById(R.id.chl_imv_user_picture);
        imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });

        rootView.findViewById(R.id.ac_txt_cloud).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });

        lyt_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addToView) {
                    resetAndGetFromEnd();
                }

                if (isShowLayoutUnreadMessage) {
                    removeLayoutUnreadMessage();
                }
                //final Realm realmMessage = Realm.getDefaultInstance();

                HelperSetAction.setCancel(mRoomId);

                clearDraftRequest();

                if (hasForward) {
                    manageForwardedMessage();

                    if (edtChat.getText().length() == 0) {
                        return;
                    }
                }

                if (ll_attach_text.getVisibility() == View.VISIBLE) {

                    if (listPathString.size() == 0) {
                        return;
                    }
                    sendMessage(latestRequestCode, listPathString.get(0));
                    listPathString.clear();
                    ll_attach_text.setVisibility(View.GONE);
                    edtChat.setFilters(new InputFilter[]{});
                    edtChat.setText("");

                    clearReplyView();
                    return;
                }

                /**
                 * if use click on edit message, the message's text will be put to the EditText
                 * i set the message object for that view's tag to obtain it here
                 * request message edit only if there is any changes to the message text
                 */

                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    final StructMessageInfo messageInfo = (StructMessageInfo) edtChat.getTag();
                    final String message = getWrittenMessage();
                    if (!message.equals(messageInfo.messageText) && edtChat.getText().length() > 0) {
                        messageInfo.hasEmojiInText = RealmRoomMessage.isEmojiInText(message);

                        RealmRoomMessage.editMessageClient(mRoomId, parseLong(messageInfo.messageID), message);
                        RealmClientCondition.addOfflineEdit(mRoomId, Long.parseLong(messageInfo.messageID), message);

                        /**
                         * update message text in adapter
                         */
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.updateMessageText(parseLong(messageInfo.messageID), message);
                            }
                        });

                        /**
                         * should be null after requesting
                         */
                        imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_send_button));
                        edtChat.setTag(null);
                        clearReplyView();
                        isEditMessage = false;
                        edtChat.setText("");

                        /**
                         * send edit message request
                         */
                        if (chatType == CHAT) {
                            new RequestChatEditMessage().chatEditMessage(mRoomId, parseLong(messageInfo.messageID), message);
                        } else if (chatType == GROUP) {
                            new RequestGroupEditMessage().groupEditMessage(mRoomId, parseLong(messageInfo.messageID), message);
                        } else if (chatType == CHANNEL) {
                            new RequestChannelEditMessage().channelEditMessage(mRoomId, parseLong(messageInfo.messageID), message);
                        }
                    } else {
                        imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_send_button));
                        edtChat.setTag(null);
                        clearReplyView();
                        isEditMessage = false;
                        edtChat.setText("");
                    }
                } else { // new message has written

                    String[] messages = HelperString.splitStringEvery(getWrittenMessage(), Config.MAX_TEXT_LENGTH);
                    if (messages.length == 0) {
                        edtChat.setText("");
                        Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < messages.length; i++) {
                            final String message = messages[i];

                            final RealmRoomMessage roomMessage = RealmRoomMessage.makeTextMessage(mRoomId, message, replyMessageId());
                            if (roomMessage != null) {
                                edtChat.setText("");
                                mAdapter.add(new TextItem(getRealmChat(), chatType, FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), roomMessage)).withIdentifier(SUID.id().get()));
                                clearReplyView();
                                scrollToEnd();

                                /**
                                 * send splitted message in every one second
                                 */
                                if (messages.length > 1) {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (roomMessage.isValid() && !roomMessage.isDeleted()) {
                                                new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                                            }
                                        }
                                    }, 1000 * i);
                                } else {
                                    new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                                }
                            } else {
                                Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

                //realmMessage.close();
            }
        });

        imvAttachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!initAttach) {
                    initAttach = true;
                    initAttach();
                }

                InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                itemAdapterBottomSheet();
            }
        });

        imvMicButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (ContextCompat.checkSelfPermission(G.fragmentActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    try {
                        HelperPermission.getMicroPhonePermission(G.fragmentActivity, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    voiceRecord.setItemTag("ivVoice");
                    // viewAttachFile.setVisibility(View.GONE);
                    viewMicRecorder.setVisibility(View.VISIBLE);

                    AppUtils.setVibrator(50);
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            voiceRecord.startVoiceRecord();
                        }
                    }, 60);
                }

                return true;
            }
        });

        // to toggle between keyboard and emoji popup
        imvSmileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!initEmoji) {
                    initEmoji = true;
                    setUpEmojiPopup();
                }

                emojiPopup.toggle();
            }
        });

        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                if (text.length() > 0) {
                    HelperSetAction.setActionTyping(mRoomId, chatType);
                }

                // if in the seeting page send by enter is on message send by enter key
                if (text.toString().endsWith(System.getProperty("line.separator"))) {
                    if (sendByEnter) imvSendButton.performClick();
                }
                if (text.toString().equals(messageEdit) && isEditMessage) {
                    imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_close_button));
                } else {
                    imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_send_button));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (ll_attach_text.getVisibility() == View.GONE && hasForward == false) {

                    if (edtChat.getText().length() > 0) {
                        layoutAttachBottom.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layoutAttachBottom.setVisibility(View.GONE);
                            }
                        }).start();
                        imvSendButton.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imvSendButton.clearAnimation();
                                        imvSendButton.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }).start();
                    } else {
                        if (!isEditMessage) {
                            layoutAttachBottom.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);

                                    layoutAttachBottom.setVisibility(View.VISIBLE);
                                }
                            }).start();
                            imvSendButton.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    G.handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            imvSendButton.clearAnimation();
                                            imvSendButton.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }).start();
                        } else {
                            imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_close_button));
                        }
                    }
                }
            }
        });

        //realm.close();
    }

    private void dialogReport(final boolean isMessage, final long messageId) {

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();

        View v = dialog.getCustomView();
        if (v == null) {
            return;
        }

        DialogAnimation.animationDown(dialog);
        dialog.show();

        ViewGroup rooAbuse = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup rootSpam = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
        ViewGroup rootViolence = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);
        ViewGroup rootPornography = (ViewGroup) v.findViewById(R.id.dialog_root_item4_notification);
        ViewGroup rootOther = (ViewGroup) v.findViewById(R.id.dialog_root_item5_notification);

        TextView txtAbuse = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtSpam = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
        TextView txtViolence = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
        TextView txtPornography = (TextView) v.findViewById(R.id.dialog_text_item4_notification);
        TextView txtOther = (TextView) v.findViewById(R.id.dialog_text_item5_notification);

        TextView iconAbuse = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
        iconAbuse.setVisibility(View.GONE);
        iconAbuse.setText(G.fragmentActivity.getResources().getString(R.string.md_back_arrow_reply));

        TextView iconSpam = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconSpam.setVisibility(View.GONE);
        iconSpam.setText(G.fragmentActivity.getResources().getString(R.string.md_copy));

        TextView iconViolence = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
        iconViolence.setVisibility(View.GONE);
        iconViolence.setText(G.fragmentActivity.getResources().getString(R.string.md_share_button));

        TextView iconPornography = (TextView) v.findViewById(R.id.dialog_icon_item4_notification);
        iconPornography.setVisibility(View.GONE);
        iconPornography.setText(G.fragmentActivity.getResources().getString(R.string.md_forward));

        TextView icoOther = (TextView) v.findViewById(R.id.dialog_icon_item5_notification);
        icoOther.setVisibility(View.GONE);
        icoOther.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));

        rooAbuse.setVisibility(View.VISIBLE);
        rootSpam.setVisibility(View.VISIBLE);
        rootViolence.setVisibility(View.VISIBLE);
        rootPornography.setVisibility(View.VISIBLE);
        rootOther.setVisibility(View.VISIBLE);

        txtAbuse.setText(R.string.st_Abuse);
        txtSpam.setText(R.string.st_Spam);
        txtViolence.setText(R.string.st_Violence);
        txtPornography.setText(R.string.st_Pornography);
        txtOther.setText(R.string.st_Other);


        rooAbuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.ABUSE, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.ABUSE, "");
                }

            }
        });
        rootSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.SPAM, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.SPAM, "");
                }
            }
        });
        rootViolence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.VIOLENCE, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.VIOLENCE, "");
                }
            }
        });
        rootPornography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.PORNOGRAPHY, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.PORNOGRAPHY, "");
                }
            }
        });
        rootOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final MaterialDialog dialogReport = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.report).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE).alwaysCallInputCallback().input(G.context.getString(R.string.description), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        if (input.length() > 0) {

                            report = input.toString();
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(true);

                        } else {
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(false);
                        }
                    }
                }).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (isMessage) {
                            new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.OTHER, report);
                        } else {
                            new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.OTHER, report);
                        }
                    }
                }).negativeText(R.string.cancel).build();

                View positive = dialogReport.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                DialogAnimation.animationDown(dialogReport);
                dialogReport.show();

            }
        });

        G.onReport = new OnReport() {
            @Override
            public void success() {
                error(G.fragmentActivity.getResources().getString(R.string.st_send_report));
            }
        };


    }

    private void putExtra(Intent intent, StructMessageInfo messageInfo) {
        try {
            String message = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessage() : messageInfo.messageText;
            if (message != null) {
                intent.putExtra(Intent.EXTRA_TEXT, message);
            }
            String filePath = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getLocalFilePath() : messageInfo.attachment.getLocalFilePath();
            if (filePath != null) {
                intent.putExtra(Intent.EXTRA_STREAM, AppUtils.createtUri(new File(filePath)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *************************** callbacks ***************************
     */

    @Override
    public void onSenderAvatarClick(View view, StructMessageInfo messageInfo, int position) {
        /**
         * set null for avoid from clear group room message adapter if user try for clearChatHistory
         */
        G.onClearChatHistory = null;

        new HelperFragment(FragmentContactsProfile.newInstance(mRoomId, parseLong(messageInfo.senderID), GROUP.toString())).setReplace(false).load();
    }

    @Override
    public void onUploadOrCompressCancel(View view, final StructMessageInfo message, int pos, SendingStep sendingStep) {

        if (sendingStep == SendingStep.UPLOADING) {
            HelperSetAction.sendCancel(parseLong(message.messageID));

            if (HelperUploadFile.cancelUploading(message.messageID)) {
                deleteItem(parseLong(message.messageID), pos);
            }
        } else if (sendingStep == SendingStep.COMPRESSING) {

            /**
             * clear path for avoid from continue uploading after compressed file
             */
            for (StructUploadVideo structUploadVideo : structUploadVideos) {
                if (structUploadVideo.filePath.equals(message.attachment.getLocalFilePath())) {
                    structUploadVideo.filePath = "";
                }
            }
            deleteItem(parseLong(message.messageID), pos);
        } else if (sendingStep == SendingStep.CORRUPTED_FILE) {
            deleteItem(parseLong(message.messageID), pos);
        }
    }

    @Override
    public void onChatClearMessage(final long roomId, final long clearId) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    boolean cleared = false;
                    if (mAdapter.getAdapterItemCount() > 1) {
                        try {
                            if (Long.parseLong(mAdapter.getAdapterItem(mAdapter.getAdapterItemCount() - 1).mMessage.messageID) == clearId) {
                                cleared = true;
                                mAdapter.clear();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (!cleared) {
                        int selectedPosition = -1;
                        for (int i = (mAdapter.getAdapterItemCount() - 1); i >= 0; i--) {
                            try {
                                if (Long.parseLong(mAdapter.getAdapterItem(i).mMessage.messageID) == clearId) {
                                    selectedPosition = i;
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (selectedPosition != -1) {
                            for (int i = selectedPosition; i >= 0; i--) {
                                mAdapter.remove(i);
                            }
                        }
                    }
                }

                /**
                 * remove tag from edtChat if the message has deleted
                 */
                if (edtChat != null && edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    edtChat.setTag(null);
                }
            }
        });
    }

    @Override
    public void onChatUpdateStatus(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, long statusVersion) {

        // I'm in the room
        if (mRoomId == roomId) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.updateMessageStatus(messageId, status);
                    }
                }
            });
        }
    }

    @Override
    public void onChatMessageSelectionChanged(int selectedCount, Set<AbstractMessage> selectedItems) {
        //   Toast.makeText(ActivityChat.this, "selected: " + Integer.toString(selectedCount), Toast.LENGTH_SHORT).show();
        if (selectedCount > 0) {
            toolbar.setVisibility(View.GONE);
            rippleReplaySelected.setVisibility(View.VISIBLE);

            txtNumberOfSelected.setText(selectedCount + "");

            if (HelperCalander.isPersianUnicode) {
                txtNumberOfSelected.setText(convertToUnicodeFarsiNumber(txtNumberOfSelected.getText().toString()));
            }

            if (selectedCount > 1) {
                rippleReplaySelected.setVisibility(View.INVISIBLE);
            } else {

                if (chatType == CHANNEL) {
                    if (channelRole == ChannelChatRole.MEMBER) {
                        rippleReplaySelected.setVisibility(View.INVISIBLE);
                    }
                }
            }

            //+Realm realm = Realm.getDefaultInstance();

            isAllSenderId = true;

            for (AbstractMessage message : selectedItems) {

                RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {

                    long messageSender = 0;
                    if (message != null && message.mMessage != null && message.mMessage.senderID != null) {
                        messageSender = parseLong(message.mMessage.senderID);
                    } else {
                        continue;
                    }

                    // if user clicked on any message which he wasn't its sender, remove edit mList option
                    if (chatType == CHANNEL) {
                        if (channelRole == ChannelChatRole.MEMBER) {
                            rippleReplaySelected.setVisibility(View.INVISIBLE);
                            rippleDeleteSelected.setVisibility(View.GONE);
                            isAllSenderId = false;
                        }
                        final long senderId = G.userId;
                        ChannelChatRole roleSenderMessage = RealmChannelRoom.detectMemberRole(mRoomId, messageSender);
                        if (senderId != messageSender) {  // if message dose'nt belong to owner
                            if (channelRole == ChannelChatRole.MEMBER) {
                                rippleDeleteSelected.setVisibility(View.GONE);
                                isAllSenderId = false;
                            } else if (channelRole == ChannelChatRole.MODERATOR) {
                                if (roleSenderMessage == ChannelChatRole.MODERATOR || roleSenderMessage == ChannelChatRole.ADMIN || roleSenderMessage == ChannelChatRole.OWNER) {
                                    rippleDeleteSelected.setVisibility(View.GONE);
                                    isAllSenderId = false;
                                }
                            } else if (channelRole == ChannelChatRole.ADMIN) {
                                if (roleSenderMessage == ChannelChatRole.OWNER || roleSenderMessage == ChannelChatRole.ADMIN) {
                                    rippleDeleteSelected.setVisibility(View.GONE);
                                    isAllSenderId = false;
                                }
                            }
                        } else {
                            rippleDeleteSelected.setVisibility(View.VISIBLE);
                        }
                    } else if (chatType == GROUP) {

                        final long senderId = G.userId;
                        GroupChatRole roleSenderMessage = RealmGroupRoom.detectMemberRole(mRoomId, messageSender);

                        if (senderId != messageSender) {  // if message dose'nt belong to owner
                            if (groupRole == GroupChatRole.MEMBER) {
                                rippleDeleteSelected.setVisibility(View.GONE);
                                isAllSenderId = false;
                            } else if (groupRole == GroupChatRole.MODERATOR) {
                                if (roleSenderMessage == GroupChatRole.MODERATOR || roleSenderMessage == GroupChatRole.ADMIN || roleSenderMessage == GroupChatRole.OWNER) {
                                    rippleDeleteSelected.setVisibility(View.GONE);
                                    isAllSenderId = false;
                                }
                            } else if (groupRole == GroupChatRole.ADMIN) {
                                if (roleSenderMessage == GroupChatRole.OWNER || roleSenderMessage == GroupChatRole.ADMIN) {
                                    rippleDeleteSelected.setVisibility(View.GONE);
                                    isAllSenderId = false;
                                }
                            }
                        } else {
                            rippleDeleteSelected.setVisibility(View.VISIBLE);
                        }
                    } else if (realmRoom.getReadOnly()) {
                        rippleReplaySelected.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (!isAllSenderId) {
                rippleDeleteSelected.setVisibility(View.GONE);
            }

            //realm.close();

            ll_AppBarSelected.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            ll_AppBarSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPreChatMessageRemove(final StructMessageInfo messageInfo, int position) {
        if (mAdapter.getAdapterItemCount() > 1 && position == mAdapter.getAdapterItemCount() - 1) {
            //RealmRoom.setLastMessageAfterLocalDelete(mRoomId, parseLong(messageInfo.messageID));
            RealmRoom.setLastMessage(mRoomId);
        }
    }

    @Override
    public void onMessageUpdate(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, final String identity, final ProtoGlobal.RoomMessage roomMessage) {
        // I'm in the room
        if (roomId == mRoomId && mAdapter != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateMessageIdAndStatus(messageId, identity, status, roomMessage);
                }
            });
        }
    }

    @Override
    public void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType) {

        if (roomMessage.getMessageId() <= biggestMessageId) {
            return;
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //final Realm realm = Realm.getDefaultInstance();
                final RealmRoomMessage realmRoomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();

                if (realmRoomMessage != null && realmRoomMessage.isValid() && !realmRoomMessage.isDeleted()) {
                    if (roomMessage.getAuthor().getUser() != null) {
                        if (roomMessage.getAuthor().getUser().getUserId() != G.userId) {
                            // I'm in the room
                            if (roomId == mRoomId) {
                                // I'm in the room, so unread messages count is 0. it means, I read all messages
                                getRealmChat().executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                                        if (room != null) {
                                            /**
                                             * client checked  (room.getUnreadCount() <= 1)  because in HelperMessageResponse unreadCount++
                                             */
                                            if (room.getUnreadCount() <= 1 && countNewMessage < 1) {
                                                firstUnreadMessage = realmRoomMessage;
                                            }
                                            room.setUnreadCount(0);
                                            if (G.onClearUnread != null) {
                                                G.onClearUnread.onClearUnread(roomId);
                                            }
                                        }
                                    }
                                });

                                /**
                                 * when user receive message, I send update status as SENT to the message sender
                                 * but imagine user is not in the room (or he is in another room) and received
                                 * some messages when came back to the room with new messages, I make new update
                                 * status request as SEEN to the message sender
                                 */

                                //Start ClientCondition OfflineSeen
                                RealmClientCondition.addOfflineSeen(mRoomId, realmRoomMessage.getMessageId());

                                getRealmChat().executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        if (!isNotJoin) {
                                            // make update status to message sender that i've read his message

                                            StructBackGroundSeen _BackGroundSeen = null;

                                            ProtoGlobal.RoomMessageStatus roomMessageStatus;
                                            if (G.isAppInFg && isEnd()) {

                                                /**
                                                 * I'm in the room, so unread messages count is 0. it means, I read all messages
                                                 */
                                                RealmRoom room = RealmRoom.setCount(realm, mRoomId, 0);
                                                if (room != null) {
                                                    if (G.onClearUnread != null) {
                                                        G.onClearUnread.onClearUnread(roomId);
                                                    }
                                                }

                                                if (realmRoomMessage.isValid() && !realmRoomMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {
                                                    realmRoomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());
                                                }

                                                roomMessageStatus = ProtoGlobal.RoomMessageStatus.SEEN;
                                            } else {

                                                roomMessageStatus = ProtoGlobal.RoomMessageStatus.DELIVERED;

                                                _BackGroundSeen = new StructBackGroundSeen();
                                                _BackGroundSeen.messageID = roomMessage.getMessageId();
                                                _BackGroundSeen.roomType = roomType;
                                            }

                                            if (chatType == CHAT) {
                                                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), roomMessageStatus);

                                                if (_BackGroundSeen != null) {
                                                    backGroundSeenList.add(_BackGroundSeen);
                                                }
                                            } else if (chatType == GROUP && (roomMessage.getStatus() != ProtoGlobal.RoomMessageStatus.SEEN)) {
                                                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), roomMessageStatus);

                                                if (_BackGroundSeen != null) {
                                                    backGroundSeenList.add(_BackGroundSeen);
                                                }
                                            }
                                        }
                                    }
                                });

                                /**
                                 * when client load item from unread and don't come down let's not add the message
                                 * to the list and after insuring that not any more message in DOWN can add message
                                 */
                                if (addToView) {
                                    switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), realmRoomMessage))), false);
                                    if (isShowLayoutUnreadMessage) {
                                        removeLayoutUnreadMessage();
                                    }
                                }

                                setBtnDownVisible(realmRoomMessage);
                            } else {
                                if (!isNotJoin) {
                                    // user has received the message, so I make a new delivered update status request
                                    if (roomType == CHAT) {
                                        G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                                    } else if (roomType == GROUP && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
                                        G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                                    }
                                }
                            }
                        } else {

                            if (roomId == mRoomId) {
                                // I'm sender . but another account sent this message and i received it.
                                if (addToView) {
                                    switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), realmRoomMessage))), false);
                                    if (isShowLayoutUnreadMessage) {
                                        removeLayoutUnreadMessage();
                                    }
                                }
                                setBtnDownVisible(realmRoomMessage);
                            }
                        }
                    }
                }

                //realm.close();
            }
        }, 400);
    }

    @Override
    public void onMessageFailed(long roomId, RealmRoomMessage message) {

        if (mAdapter != null && message != null && roomId == mRoomId) {
            mAdapter.updateMessageStatus(message.getMessageId(), ProtoGlobal.RoomMessageStatus.FAILED);
        }
    }

    @Override
    public void onVoiceRecordDone(final String savedPath) {
        if (isShowLayoutUnreadMessage) {
            removeLayoutUnreadMessage();
        }
        sendCancelAction();

        //+Realm realm = Realm.getDefaultInstance();
        final long messageId = SUID.id().get();
        final long updateTime = TimeUtils.currentLocalTime();
        final long senderID = G.userId;
        final long duration = AndroidUtils.getAudioDuration(G.fragmentActivity, savedPath) / 1000;

        getRealmChat().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                voiceLastMessage = RealmRoomMessage.makeVoiceMessage(realm, mRoomId, messageId, duration, updateTime, savedPath, getWrittenMessage());
            }
        });

        StructMessageInfo messageInfo;
        if (isReply()) {
            messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                    RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
        } else {
            if (isMessageWrote()) {
                messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                        RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime);
            } else {
                messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                        RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime);
            }
        }

        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, savedPath, messageId, ProtoGlobal.RoomMessageType.VOICE, getWrittenMessage(), StructMessageInfo.getReplyMessageId(messageInfo), new HelperUploadFile.UpdateListener() {
            @Override
            public void OnProgress(int progress, FileUploadStructure struct) {
                if (canUpdateAfterDownload) {
                    insertItemAndUpdateAfterStartUpload(progress, struct);
                }
            }

            @Override
            public void OnError() {

            }
        });

        messageInfo.attachment.duration = duration;

        StructChannelExtra structChannelExtra = new StructChannelExtra();
        structChannelExtra.messageId = messageId;
        structChannelExtra.thumbsUp = "0";
        structChannelExtra.thumbsDown = "0";
        structChannelExtra.viewsLabel = "1";

        RealmRoom.setLastMessageWithRoomMessage(mRoomId, voiceLastMessage);

        if (RealmRoom.showSignature(mRoomId)) {
            structChannelExtra.signature = G.displayName;
        } else {
            structChannelExtra.signature = "";
        }
        messageInfo.channelExtra = structChannelExtra;
        mAdapter.add(new VoiceItem(getRealmChat(), chatType, this).setMessage(messageInfo));
        //realm.close();
        scrollToEnd();
        clearReplyView();
    }

    @Override
    public void onVoiceRecordCancel() {
        //empty

        sendCancelAction();
    }

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

        if (isCloudRoom) {
            rootView.findViewById(R.id.ac_txt_cloud).setVisibility(View.VISIBLE);
            imvUserPicture.setVisibility(View.GONE);
        } else {
            setAvatar();
        }
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
    public void onOpenClick(View view, StructMessageInfo message, int pos) {
        ProtoGlobal.RoomMessageType messageType = message.forwardedFrom != null ? message.forwardedFrom.getMessageType() : message.messageType;
        //+Realm realm = Realm.getDefaultInstance();
        if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == IMAGE_TEXT) {
            showImage(message, view);
        } else if (messageType == VIDEO || messageType == VIDEO_TEXT) {
            if (sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 0) == 1) {
                openMessage(message);
            } else {
                showImage(message, view);
            }
        } else if (messageType == ProtoGlobal.RoomMessageType.FILE || messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            openMessage(message);
        }
    }

    private void openMessage(StructMessageInfo message) {
        String _filePath = null;
        String _token = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getToken() : message.attachment.token;
        RealmAttachment _Attachment = getRealmChat().where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, _token).findFirst();

        if (_Attachment != null) {
            _filePath = _Attachment.getLocalFilePath();
        } else if (message.attachment != null) {
            _filePath = message.attachment.getLocalFilePath();
        }

        if (_filePath == null || _filePath.length() == 0) {
            return;
        }

        Intent intent = HelperMimeType.appropriateProgram(_filePath);
        if (intent != null) {
            try {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (Exception e) {
                // to prevent from 'No Activity found to handle Intent'
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDownloadAllEqualCashId(String cashId, String messageID) {

        int start = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        for (int i = start; i < mAdapter.getItemCount() && i < start + 15; i++) {
            try {
                AbstractMessage item = mAdapter.getAdapterItem(i);
                if (item.mMessage.hasAttachment()) {
                    if (item.mMessage.getAttachment().cashID != null && item.mMessage.getAttachment().cashID.equals(cashId) && (!item.mMessage.messageID.equals(messageID))) {
                        mAdapter.notifyItemChanged(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemShowingMessageId(final StructMessageInfo messageInfo) {
        /**
         * if in current room client have new message that not seen yet
         * after first new message come in the view change view for unread count
         */
        if (firstUnreadMessageInChat != null && firstUnreadMessageInChat.isManaged() && firstUnreadMessageInChat.isValid() && !firstUnreadMessageInChat.isDeleted() && firstUnreadMessageInChat.getMessageId() == parseLong(messageInfo.messageID)) {
            countNewMessage = 0;
            txtNewUnreadMessage.setVisibility(View.GONE);
            txtNewUnreadMessage.setText(countNewMessage + "");

            firstUnreadMessageInChat = null;
        }

        if (chatType != CHANNEL && (!messageInfo.isSenderMe() && messageInfo.status != null && !messageInfo.status.equals(ProtoGlobal.RoomMessageStatus.SEEN.toString()) & !messageInfo.status.equals(ProtoGlobal.RoomMessageStatus.LISTENED.toString()))) {
            /**
             * set message status SEEN for avoid from run this block in each bindView
             */
            messageInfo.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();

            RealmClientCondition.addOfflineSeen(mRoomId, Long.parseLong(messageInfo.messageID));
            RealmRoomMessage.setStatusSeenInChat(parseLong(messageInfo.messageID));
            G.chatUpdateStatusUtil.sendUpdateStatus(chatType, mRoomId, parseLong(messageInfo.messageID), ProtoGlobal.RoomMessageStatus.SEEN);
        }
    }

    @Override
    public void onPlayMusic(String messageId) {

        if (messageId != null && messageId.length() > 0) {

            try {
                if (MusicPlayer.downloadNextMusic(messageId)) {
                    mAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                HelperLog.setErrorLog("Activity chat  onPlayMusic    " + e.toString());
            }
        }
    }

    @Override
    public boolean getShowVoteChannel() {
        return showVoteChannel;
    }

    @Override
    public void onContainerClick(View view, final StructMessageInfo message, int pos) {

        if (message == null) {
            return;
        }

        ProtoGlobal.RoomMessageType roomMessageType;
        if (message.forwardedFrom != null) {
            roomMessageType = message.forwardedFrom.getMessageType();
        } else {
            roomMessageType = message.messageType;
        }

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();

        View v = dialog.getCustomView();
        if (v == null) {
            return;
        }

        if (!isAdded() || G.fragmentActivity.isFinishing()) {
            return;
        }

        DialogAnimation.animationDown(dialog);
        dialog.show();

        ViewGroup rootReplay = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup rootCopy = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
        ViewGroup rootShare = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);
        ViewGroup rootForward = (ViewGroup) v.findViewById(R.id.dialog_root_item4_notification);
        ViewGroup rootDelete = (ViewGroup) v.findViewById(R.id.dialog_root_item5_notification);
        ViewGroup rootEdit = (ViewGroup) v.findViewById(R.id.dialog_root_item6_notification);
        final ViewGroup rootSaveToDownload = (ViewGroup) v.findViewById(R.id.dialog_root_item7_notification);
        final ViewGroup rootReport = (ViewGroup) v.findViewById(R.id.dialog_root_item8_notification);

        TextView txtItemReplay = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtItemCopy = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
        TextView txtItemShare = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
        TextView txtItemForward = (TextView) v.findViewById(R.id.dialog_text_item4_notification);
        TextView txtItemDelete = (TextView) v.findViewById(R.id.dialog_text_item5_notification);
        TextView txtItemEdit = (TextView) v.findViewById(R.id.dialog_text_item6_notification);
        final TextView txtItemSaveToDownload = (TextView) v.findViewById(R.id.dialog_text_item7_notification);
        final TextView txtReport = (TextView) v.findViewById(R.id.dialog_text_item8_notification);

        TextView iconReplay = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
        iconReplay.setText(G.fragmentActivity.getResources().getString(R.string.md_back_arrow_reply));

        TextView iconCopy = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconCopy.setText(G.fragmentActivity.getResources().getString(R.string.md_copy));

        TextView iconShare = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
        iconShare.setText(G.fragmentActivity.getResources().getString(R.string.md_share_button));

        TextView iconForward = (TextView) v.findViewById(R.id.dialog_icon_item4_notification);
        iconForward.setText(G.fragmentActivity.getResources().getString(R.string.md_forward));

        TextView iconDelete = (TextView) v.findViewById(R.id.dialog_icon_item5_notification);
        iconDelete.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));

        TextView iconEdit = (TextView) v.findViewById(R.id.dialog_icon_item6_notification);
        iconEdit.setText(G.fragmentActivity.getResources().getString(R.string.md_edit));

        TextView iconItemSaveToDownload = (TextView) v.findViewById(R.id.dialog_icon_item7_notification);
        iconItemSaveToDownload.setText(G.fragmentActivity.getResources().getString(R.string.md_save));

        TextView iconReport = (TextView) v.findViewById(R.id.dialog_icon_item8_notification);
        iconReport.setText(G.fragmentActivity.getResources().getString(R.string.md_igap_alert_box));

        if (channelRole != ChannelChatRole.OWNER || groupRole != GroupChatRole.OWNER || isNotJoin) {
            rootReport.setVisibility(View.VISIBLE);
        } else {
            rootReport.setVisibility(View.GONE);
        }

        @ArrayRes int itemsRes = 0;
        switch (roomMessageType) {
            case TEXT:
                //itemsRes = R.array.textMessageDialogItems;

                txtItemReplay.setText(R.string.replay_item_dialog);
                txtItemCopy.setText(R.string.copy_item_dialog);
                txtItemShare.setText(R.string.share_item_dialog);
                txtItemForward.setText(R.string.forward_item_dialog);
                txtItemDelete.setText(R.string.delete_item_dialog);
                txtItemEdit.setText(R.string.edit_item_dialog);
                txtReport.setText(R.string.report);

                rootReplay.setVisibility(View.VISIBLE);
                rootCopy.setVisibility(View.VISIBLE);
                rootShare.setVisibility(View.VISIBLE);
                rootForward.setVisibility(View.VISIBLE);
                rootDelete.setVisibility(View.VISIBLE);
                rootEdit.setVisibility(View.VISIBLE);

                break;
            case FILE_TEXT:
            case IMAGE_TEXT:
            case VIDEO_TEXT:
            case AUDIO_TEXT:
            case GIF_TEXT:
                //itemsRes = R.array.fileTextMessageDialogItems;

                txtItemReplay.setText(R.string.replay_item_dialog);
                txtItemCopy.setText(R.string.copy_item_dialog);
                txtItemShare.setText(R.string.share_item_dialog);
                txtItemForward.setText(R.string.forward_item_dialog);
                txtItemDelete.setText(R.string.delete_item_dialog);
                txtItemEdit.setText(R.string.edit_item_dialog);

                rootReplay.setVisibility(View.VISIBLE);
                rootCopy.setVisibility(View.VISIBLE);
                rootShare.setVisibility(View.VISIBLE);
                rootForward.setVisibility(View.VISIBLE);
                rootDelete.setVisibility(View.VISIBLE);
                rootEdit.setVisibility(View.VISIBLE);
                rootSaveToDownload.setVisibility(View.VISIBLE);
                break;
            case FILE:
            case IMAGE:
            case VIDEO:
            case AUDIO:
            case GIF:

                txtItemReplay.setText(R.string.replay_item_dialog);
                txtItemShare.setText(R.string.share_item_dialog);
                txtItemForward.setText(R.string.forward_item_dialog);
                txtItemDelete.setText(R.string.delete_item_dialog);

                rootReplay.setVisibility(View.VISIBLE);
                rootShare.setVisibility(View.VISIBLE);
                rootForward.setVisibility(View.VISIBLE);
                rootDelete.setVisibility(View.VISIBLE);
                rootSaveToDownload.setVisibility(View.VISIBLE);

                //itemsRes = R.array.fileMessageDialogItems;
                break;
            case VOICE:
            case LOCATION:
            case CONTACT:
            case LOG:

                txtItemReplay.setText(R.string.replay_item_dialog);
                txtItemShare.setText(R.string.share_item_dialog);
                txtItemForward.setText(R.string.forward_item_dialog);
                txtItemDelete.setText(R.string.delete_item_dialog);
                //itemsRes = R.array.otherMessageDialogItems;

                rootReplay.setVisibility(View.VISIBLE);
                rootShare.setVisibility(View.VISIBLE);
                rootForward.setVisibility(View.VISIBLE);
                rootDelete.setVisibility(View.VISIBLE);

                break;
        }

        if (message.forwardedFrom != null) {
            rootEdit.setVisibility(View.GONE);
        }

        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, message.roomId).findFirst();
        if (realmRoom != null) {
            /**
             * if user clicked on any message which he wasn't its sender, remove edit mList option
             */
            if (chatType == CHANNEL) {
                if (channelRole == ChannelChatRole.MEMBER) {
                    rootEdit.setVisibility(View.GONE);
                    rootReplay.setVisibility(View.GONE);
                    rootDelete.setVisibility(View.GONE);
                }
                ChannelChatRole roleSenderMessage = RealmChannelRoom.detectMemberRole(mRoomId, parseLong(message.senderID));
                if (!G.authorHash.equals(message.authorHash)) {
                    if (channelRole == ChannelChatRole.MEMBER) {
                        rootDelete.setVisibility(View.GONE);
                    } else if (channelRole == ChannelChatRole.MODERATOR) {
                        if (roleSenderMessage == ChannelChatRole.MODERATOR || roleSenderMessage == ChannelChatRole.ADMIN || roleSenderMessage == ChannelChatRole.OWNER) {
                            rootDelete.setVisibility(View.GONE);
                        }
                    } else if (channelRole == ChannelChatRole.ADMIN) {
                        if (roleSenderMessage == ChannelChatRole.OWNER || roleSenderMessage == ChannelChatRole.ADMIN) {
                            rootDelete.setVisibility(View.GONE);
                        }
                    }
                    rootEdit.setVisibility(View.GONE);
                }
            } else if (chatType == GROUP) {

                GroupChatRole roleSenderMessage = RealmGroupRoom.detectMemberRole(mRoomId, parseLong(message.senderID));
                if (!G.authorHash.equals(message.authorHash)) {
                    if (groupRole == GroupChatRole.MEMBER) {
                        rootDelete.setVisibility(View.GONE);
                    } else if (groupRole == GroupChatRole.MODERATOR) {
                        if (roleSenderMessage == GroupChatRole.MODERATOR || roleSenderMessage == GroupChatRole.ADMIN || roleSenderMessage == GroupChatRole.OWNER) {
                            rootDelete.setVisibility(View.GONE);
                        }
                    } else if (groupRole == GroupChatRole.ADMIN) {
                        if (roleSenderMessage == GroupChatRole.OWNER || roleSenderMessage == GroupChatRole.ADMIN) {
                            rootDelete.setVisibility(View.GONE);
                        }
                    }
                    rootEdit.setVisibility(View.GONE);
                }
            } else if (realmRoom.getReadOnly()) {
                rootReplay.setVisibility(View.GONE);
            } else {
                if (!message.senderID.equalsIgnoreCase(Long.toString(G.userId))) {
                    rootEdit.setVisibility(View.GONE);
                }
            }
        }

        String _savedFolderName = "";
        if (roomMessageType.toString().contains("IMAGE") || roomMessageType.toString().contains("VIDEO") || roomMessageType.toString().contains("GIF")) {
            _savedFolderName = G.fragmentActivity.getResources().getString(R.string.save_to_gallery);
        } else if (roomMessageType.toString().contains("AUDIO") || roomMessageType.toString().contains("VOICE")) {
            _savedFolderName = G.fragmentActivity.getResources().getString(R.string.save_to_Music);
        } else {
            _savedFolderName = G.fragmentActivity.getResources().getString(R.string.saveToDownload_item_dialog);
        }

        if (RealmRoom.isNotificationServices(mRoomId)) {
            rootReport.setVisibility(View.GONE);
        }

        txtItemSaveToDownload.setText(_savedFolderName);
        rootReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        replay(message);
                    }
                }, 200);

            }
        });
        rootCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                String _text = message.forwardedFrom != null ? message.forwardedFrom.getMessage() : message.messageText;
                if (_text != null && _text.length() > 0) {
                    ClipData clip = ClipData.newPlainText("Copied Text", _text);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, R.string.text_copied, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.text_is_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });
        rootShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shearedDataToOtherProgram(message);
            }
        });
        rootForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mForwardMessages = new ArrayList<>(Arrays.asList(Parcels.wrap(message)));
                finishChat();
            }
        });
        rootDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean bothDelete = RealmRoomMessage.isBothDelete(message.time);
                bothDeleteMessageId = new ArrayList<Long>();
                if (bothDelete) {
                    bothDeleteMessageId.add(Long.parseLong(message.messageID));
                }

                dialog.dismiss();
                //final Realm realmCondition = Realm.getDefaultInstance();
                final ArrayList<Long> messageIds = new ArrayList<>();
                messageIds.add(Long.parseLong(message.messageID));

                if (chatType == ProtoGlobal.Room.Type.CHAT && !isCloudRoom && bothDeleteMessageId.size() > 0 && message.senderID.equalsIgnoreCase(Long.toString(G.userId))) {
                    // show both Delete check box

                    String delete;
                    String textCheckBox = G.context.getResources().getString(R.string.st_checkbox_delete) + " " + title;
                    if (HelperCalander.isPersianUnicode) {
                        delete = HelperCalander.convertToUnicodeFarsiNumber(G.context.getResources().getString(R.string.st_desc_delete, "1"));
                    } else {
                        delete = HelperCalander.convertToUnicodeFarsiNumber(G.context.getResources().getString(R.string.st_desc_delete, "the"));
                    }

                    new MaterialDialog.Builder(G.fragmentActivity).limitIconToDefaultSize().content(delete).title(R.string.message).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (!dialog.isPromptCheckBoxChecked()) {
                                bothDeleteMessageId = null;
                            }

                            deleteMassage(getRealmChat(), message, messageIds, bothDeleteMessageId, chatType);
                        }
                    }).checkBoxPrompt(textCheckBox, false, null).show();

                } else {

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.message).content(G.context.getResources().getString(R.string.st_desc_delete, "1")).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            bothDeleteMessageId = null;
                            deleteMassage(getRealmChat(), message, messageIds, bothDeleteMessageId, chatType);
                        }
                    }).show();
                }
                //realmCondition.close();
            }
        });
        rootEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // edit message
                // put message text to EditText
                if (message.messageText != null && !message.messageText.isEmpty()) {
                    edtChat.setText(message.messageText);
                    edtChat.setSelection(0, edtChat.getText().length());
                    // put message object to edtChat's tag to obtain it later and
                    // found is user trying to edit a message

                    imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_close_button));
                    isEditMessage = true;
                    messageEdit = message.messageText;
                    edtChat.setTag(message);
                }
            }
        });
        rootSaveToDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                String filename;
                String filepath;
                ProtoGlobal.RoomMessageType fileType;

                if (message.forwardedFrom != null) {
                    fileType = message.forwardedFrom.getMessageType();
                    filename = message.forwardedFrom.getAttachment().getName();
                    filepath = message.forwardedFrom.getAttachment().getLocalFilePath() != null ? message.forwardedFrom.getAttachment().getLocalFilePath() : AndroidUtils.getFilePathWithCashId(message.forwardedFrom.getAttachment().getCacheId(), filename, fileType);
                } else {
                    fileType = message.messageType;
                    filename = message.getAttachment().name;
                    filepath = message.getAttachment().localFilePath != null ? message.getAttachment().localFilePath : AndroidUtils.getFilePathWithCashId(message.getAttachment().cashID, message.getAttachment().name, message.messageType);
                }

                if (new File(filepath).exists()) {
                    if (txtItemSaveToDownload.getText().toString().equalsIgnoreCase(G.fragmentActivity.getResources().getString(R.string.saveToDownload_item_dialog))) {
                        HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.download, R.string.file_save_to_download_folder);
                    } else if (txtItemSaveToDownload.getText().toString().equalsIgnoreCase(G.fragmentActivity.getResources().getString(R.string.save_to_Music))) {
                        HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.music, R.string.save_to_music_folder);
                    } else if (txtItemSaveToDownload.getText().toString().equalsIgnoreCase(G.fragmentActivity.getResources().getString(R.string.save_to_gallery))) {

                        if (fileType.toString().contains(VIDEO.toString())) {
                            HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
                        } else if (fileType.toString().contains(GIF.toString())) {
                            HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.gif, R.string.file_save_to_picture_folder);
                        } else if (fileType.toString().contains(IMAGE.toString())) {
                            HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.image, R.string.picture_save_to_galary);
                        }
                    }
                } else {

                    final ProtoGlobal.RoomMessageType _messageType = message.forwardedFrom != null ? message.forwardedFrom.getMessageType() : message.messageType;
                    String cacheId = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getCacheId() : message.getAttachment().cashID;
                    final String name = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getName() : message.getAttachment().name;
                    String fileToken = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getToken() : message.getAttachment().token;
                    Long size = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getSize() : message.getAttachment().size;

                    if (cacheId == null) {
                        return;
                    }
                    ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

                    final String _path = AndroidUtils.getFilePathWithCashId(cacheId, name, _messageType);
                    if (fileToken != null && fileToken.length() > 0 && size > 0) {
                        HelperDownloadFile.startDownload(message.messageID, fileToken, cacheId, name, size, selector, _path, 0, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(String path, int progress) {

                                if (progress == 100) {
                                    if (canUpdateAfterDownload) {
                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (txtItemSaveToDownload.getText().toString().equalsIgnoreCase(G.fragmentActivity.getResources().getString(R.string.saveToDownload_item_dialog))) {
                                                    HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.download, R.string.file_save_to_download_folder);
                                                } else if (txtItemSaveToDownload.getText().toString().equalsIgnoreCase(G.fragmentActivity.getResources().getString(R.string.save_to_Music))) {
                                                    HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.music, R.string.save_to_music_folder);
                                                } else if (txtItemSaveToDownload.getText().toString().equalsIgnoreCase(G.fragmentActivity.getResources().getString(R.string.save_to_gallery))) {
                                                    if (_messageType.toString().contains(VIDEO.toString())) {
                                                        HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
                                                    } else if (_messageType.toString().contains(GIF.toString())) {
                                                        HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.gif, R.string.file_save_to_picture_folder);
                                                    } else if (_messageType.toString().contains(IMAGE.toString())) {
                                                        HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.image, R.string.picture_save_to_galary);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void OnError(String token) {

                            }
                        });
                    }

                    onDownloadAllEqualCashId(cacheId, message.messageID);
                }
            }
        });

        rootReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                long messageId;
                if (message.forwardedFrom != null) {
                    messageId = message.forwardedFrom.getMessageId();
                } else {
                    messageId = Long.parseLong(message.messageID);
                }
                dialogReport(true, messageId);
            }
        });
    }

    private void deleteMassage(Realm realm, final StructMessageInfo message, final ArrayList<Long> list, final ArrayList<Long> bothDeleteMessageId, final ProtoGlobal.Room.Type chatType) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayList list = new ArrayList();
                list.add(parseLong(message.messageID));
                deleteSelectedMessageFromAdapter(list);
            }
        });
        RealmRoomMessage.deleteSelectedMessages(realm, message.roomId, list, bothDeleteMessageId, chatType);
    }

    @Override
    public void onFailedMessageClick(View view, final StructMessageInfo message, final int pos) {
        final List<StructMessageInfo> failedMessages = mAdapter.getFailedMessages();
        new ResendMessage(G.fragmentActivity, new IResendMessage() {
            @Override
            public void deleteMessage() {
                if (pos >= 0 && mAdapter.getAdapterItemCount() > pos) {
                    mAdapter.remove(pos);
                    removeLayoutTimeIfNeed();
                }
            }

            @Override
            public void resendMessage() {

                for (int i = 0; i < failedMessages.size(); i++) {
                    if (failedMessages.get(i).messageID.equals(message.messageID)) {
                        if (failedMessages.get(i).attachment != null) {
                            if (HelperUploadFile.isUploading(message.messageID)) {
                                HelperUploadFile.reUpload(message.messageID);
                            }
                        }
                        break;
                    }
                }

                mAdapter.updateMessageStatus(parseLong(message.messageID), ProtoGlobal.RoomMessageStatus.SENDING);

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemChanged(mAdapter.findPositionByMessageId(Long.parseLong(message.messageID)));
                    }
                }, 300);


            }

            @Override
            public void resendAllMessages() {
                for (int i = 0; i < failedMessages.size(); i++) {
                    final int j = i;
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.updateMessageStatus(parseLong(failedMessages.get(j).messageID), ProtoGlobal.RoomMessageStatus.SENDING);
                        }
                    }, 1000 * i);

                }
            }
        }, parseLong(message.messageID), failedMessages);
    }

    @Override
    public void onReplyClick(RealmRoomMessage replyMessage) {

        long replyMessageId = replyMessage.getMessageId();
        /**
         * when i add message to RealmRoomMessage(putOrUpdate) set (replyMessageId * (-1))
         * so i need to (replyMessageId * (-1)) again for use this messageId
         */
        int position = mAdapter.findPositionByMessageId((replyMessageId * (-1)));
        if (position == -1) {
            position = mAdapter.findPositionByMessageId(replyMessageId);
        }

        recyclerView.scrollToPosition(position);
    }

    @Override
    public void onForwardClick(StructMessageInfo message) {
        //finishChat();
        if (message == null) {
            mForwardMessages = getMessageStructFromSelectedItems();
            if (ll_AppBarSelected != null && ll_AppBarSelected.getVisibility() == View.VISIBLE) {
                mAdapter.deselect();
                toolbar.setVisibility(View.VISIBLE);
                ll_AppBarSelected.setVisibility(View.GONE);
                clearReplyView();
            }
        } else {
            mForwardMessages = new ArrayList<>(Arrays.asList(Parcels.wrap(message)));
        }

        initAttachForward();
        itemAdapterBottomSheetForward();

        //new HelperFragment().removeAll(true);
    }

    @Override
    public void onSetAction(final long roomId, final long userIdR, final ProtoGlobal.ClientAction clientAction) {
        if (mRoomId == roomId && (userId != userIdR || (isCloudRoom))) {
            final String action = HelperGetAction.getAction(roomId, chatType, clientAction);

            RealmRoom.setAction(roomId, userIdR, action);

            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (action != null) {
                        ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LOCALE);
                        txtLastSeen.setText(action);
                    } else if (chatType == CHAT) {
                        if (isCloudRoom) {
                            txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                        } else {
                            if (userStatus != null) {
                                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                                } else {
                                    txtLastSeen.setText(userStatus);
                                }
                            }
                        }
                        ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                    } else if (chatType == GROUP) {
                        ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                        if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                        } else {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                        }
                    }
                    // change english number to persian number
                    if (HelperCalander.isPersianUnicode)
                        txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            });
        }
    }

    @Override
    public void onUserUpdateStatus(long userId, final long time, final String status) {
        if (chatType == CHAT && chatPeerId == userId && !isCloudRoom) {
            userStatus = AppUtils.getStatsForUser(status);
            setUserStatus(userStatus, time);
        }
    }

    @Override
    public void onLastSeenUpdate(final long userIdR, final String showLastSeen) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (chatType == CHAT && userIdR == chatPeerId && userId != userIdR) { // userId != userIdR means that , this isn't update status for own user
                    txtLastSeen.setText(showLastSeen);
                    //  avi.setVisibility(View.GONE);
                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    //    viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                    //}
                    ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                    // change english number to persian number
                    if (HelperCalander.isPersianUnicode)
                        txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            }
        });
    }

    /**
     * GroupAvatar and ChannelAvatar
     */
    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {

        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (!isCloudRoom) {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvUserPicture);
                        }
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isCloudRoom && imvUserPicture != null) {
                            imvUserPicture.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) G.context.getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onAvatarAddError() {
        //empty
    }

    /**
     * Channel Message Reaction
     */

    @Override
    public void onChannelAddMessageReaction(final long roomId, final long messageId, final String reactionCounterLabel, final ProtoGlobal.RoomMessageReaction reaction, final long forwardedMessageId) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.updateVote(roomId, messageId, reactionCounterLabel, reaction, forwardedMessageId);
            }
        });
    }

    @Override
    public void onError(int majorCode, int minorCode) {
        //empty
    }

    @Override
    public void onChannelGetMessagesStats(final List<ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats> statsList) {

        if (mAdapter != null) {
            for (final ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats stats : statsList) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateMessageState(stats.getMessageId(), stats.getThumbsUpLabel(), stats.getThumbsDownLabel(), stats.getViewsLabel());
                    }
                });
            }
        }
    }

    @Override
    public void onChatDelete(long roomId) {
        if (roomId == mRoomId) {
            //  finish();
            finishChat();
        }
    }

    @Override
    public void onChatDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onChangeState(final ConnectionState connectionState) {
        setConnectionText(connectionState);
    }

    @Override
    public void onBackgroundChanged(final String backgroundPath) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (imgBackGround != null) {
                    File f = new File(backgroundPath);
                    if (f.exists()) {
                        Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                        imgBackGround.setImageDrawable(d);
                    }
                }
            }
        });
    }

    private void updateShowItemInScreen() {
        /**
         * after comeback from other activity or background  the view should update
         */
        try {
            // this only notify item that show on the screen and no more
            recyclerView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * detect that editText have character or just have space
     */
    private boolean isMessageWrote() {
        return !getWrittenMessage().isEmpty();
    }

    /**
     * get message and remove space from start and end
     */
    private String getWrittenMessage() {
        return edtChat.getText().toString().trim();
    }

    /**
     * clear history for this room
     */
    public void clearHistory(long roomId) {
        llScrollNavigate.setVisibility(View.GONE);
        saveMessageIdPositionState(0);
        RealmRoomMessage.clearHistoryMessage(roomId);
        addToView = true;

        if (G.onClearRoomHistory != null) {
            G.onClearRoomHistory.onClearRoomHistory(roomId);
        }
    }

    /**
     * message will be replied or no
     */
    private boolean isReply() {
        return mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo;
    }

    private long replyMessageId() {
        if (isReply()) {
            return parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID);
        }
        return 0;
    }

    /**
     * if isReply() is true use from this method
     */
    private long getReplyMessageId() {
        return parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID);
    }

    /**
     * if isReply() is true use from this method
     * if replay layout is visible, gone it
     */
    private void clearReplyView() {
        if (mReplayLayout != null) {
            mReplayLayout.setTag(null);
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    mReplayLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void hideProgress() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * clear all items that exist in view
     */
    private void clearAdapterItems() {
        mAdapter.clear();
        recyclerView.removeAllViews();
    }

    /**
     * client should send request for get user info because need to update user online timing
     */
    private void getUserInfo() {
        if (chatType == CHAT) {
            new RequestUserInfo().userInfo(chatPeerId);
        }
    }

    /**
     * call this method for set avatar for this room and this method
     * will be automatically detect id and chat type for show avatar
     */
    private void setAvatar() {
        long idForGetAvatar;
        HelperAvatar.AvatarType type;
        if (chatType == CHAT) {
            idForGetAvatar = chatPeerId;
            type = HelperAvatar.AvatarType.USER;
        } else {
            idForGetAvatar = mRoomId;
            type = HelperAvatar.AvatarType.ROOM;
        }

        HelperAvatar.getAvatar(idForGetAvatar, type, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvUserPicture);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imvUserPicture != null) {
                            imvUserPicture.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) G.context.getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    }
                });
            }
        });
    }

    private void resetAndGetFromEnd() {
        llScrollNavigate.setVisibility(View.GONE);
        firstUnreadMessageInChat = null;
        resetMessagingValue();
        countNewMessage = 0;
        txtNewUnreadMessage.setVisibility(View.GONE);
        txtNewUnreadMessage.setText(countNewMessage + "");
        getMessages();
    }

    private ArrayList<Parcelable> getMessageStructFromSelectedItems() {
        ArrayList<Parcelable> messageInfos = new ArrayList<>(mAdapter.getSelectedItems().size());
        for (int item : mAdapter.getSelections()) {
            messageInfos.add(Parcels.wrap(mAdapter.getItem(item).mMessage));
        }
        return messageInfos;
    }

    /**
     * show current state for user if this room is chat
     *
     * @param status current state
     * @param time   if state is not online set latest online time
     */
    private void setUserStatus(final String status, final long time) {
        if (G.connectionState == ConnectionState.CONNECTING || G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
            setConnectionText(G.connectionState);
        } else {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    userStatus = status;
                    userTime = time;
                    if (isCloudRoom) {
                        txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                        //  avi.setVisibility(View.GONE);
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //    viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        //}
                        ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                    } else {
                        if (status != null) {
                            if (status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, time, true, false));
                            } else {
                                txtLastSeen.setText(status);
                            }
                            // avi.setVisibility(View.GONE);
                            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            //    viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                            //}
                            ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                            // change english number to persian number
                            if (HelperCalander.isPersianUnicode)
                                txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));

                            checkAction();
                        }
                    }
                }
            });
        }
    }

    private void replay(StructMessageInfo item) {
        if (mAdapter != null) {
            Set<AbstractMessage> messages = mAdapter.getSelectedItems();
            // replay works if only one message selected
            inflateReplayLayoutIntoStub(item == null ? messages.iterator().next().mMessage : item);

            ll_AppBarSelected.setVisibility(View.GONE);

            toolbar.setVisibility(View.VISIBLE);

            mAdapter.deselect();

            edtChat.requestFocus();
            InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(edtChat, InputMethodManager.SHOW_IMPLICIT);
            }

        }
    }

    private void checkAction() {
        //+Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null && realmRoom.getActionState() != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (realmRoom.getActionState() != null && (chatType == GROUP || chatType == CHANNEL) || ((isCloudRoom || (!isCloudRoom && realmRoom.getActionStateUserId() != userId)))) {
                        txtLastSeen.setText(realmRoom.getActionState());
                        //  avi.setVisibility(View.VISIBLE);
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //    viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                        //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        //}
                        ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                    } else if (chatType == CHAT) {
                        if (isCloudRoom) {
                            txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                        } else {
                            if (userStatus != null) {
                                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                                } else {
                                    txtLastSeen.setText(userStatus);
                                }
                            }
                        }
                        //  avi.setVisibility(View.GONE);
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //    viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        //}
                        ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                    } else if (chatType == GROUP) {
                        //  avi.setVisibility(View.GONE);
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //    viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        //}
                        ViewMaker.setLayoutDirection(viewGroupLastSeen, View.LAYOUT_DIRECTION_LTR);
                        if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                        } else {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                        }
                    }
                    // change english number to persian number
                    if (HelperCalander.isPersianUnicode)
                        txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            });
        }
        //realm.close();
    }

    /**
     * change message status from sending to failed
     *
     * @param fakeMessageId messageId that create when created this message
     */
    private void makeFailed(final long fakeMessageId) {
        // message failed
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoomMessage.setStatusFailedInChat(realm, fakeMessageId);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, fakeMessageId).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                        }
                        realm.close();
                    }
                });
            }
        });
    }

    private void showErrorDialog(final int time) {

        boolean wrapInScrollView = true;
        final MaterialDialog dialogWait = new MaterialDialog.Builder(G.currentActivity).title(G.fragmentActivity.getResources().getString(R.string.title_limit_chat_to_unknown_contact)).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();
        if (v == null) {
            return;
        }
        //dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(true);
        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        final TextView txtText = (TextView) v.findViewById(R.id.textRemindTime);
        txtText.setText(G.fragmentActivity.getResources().getString(R.string.text_limit_chat_to_unknown_contact));
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                long s = seconds % 60;
                long m = (seconds / 60) % 60;
                long h = (seconds / (60 * 60)) % 24;
                remindTime.setText(String.format("%d:%02d:%02d", h, m, s));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    /**
     * update item progress
     */
    private void insertItemAndUpdateAfterStartUpload(int progress, final FileUploadStructure struct) {
        if (progress == 0) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    addItemAfterStartUpload(struct);
                }
            });
        } else if (progress == 100) {
            String messageId = struct.messageId + "";
            for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
                AbstractMessage item = mAdapter.getAdapterItem(i);

                if (item.mMessage != null && item.mMessage.messageID.equals(messageId)) {
                    if (item.mMessage.hasAttachment()) {
                        item.mMessage.attachment.token = struct.token;
                    }
                    break;
                }
            }
        }
    }

    /**
     * add new item to view after start upload
     */
    private void addItemAfterStartUpload(final FileUploadStructure struct) {
        try {
            //Realm realm = Realm.getDefaultInstance();
            RealmRoomMessage roomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, struct.messageId).findFirst();
            if (roomMessage != null) {
                AbstractMessage message = null;

                if (mAdapter != null) {
                    message = mAdapter.getItemByFileIdentity(struct.messageId);

                    // message doesn't exists
                    if (message == null) {
                        switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), roomMessage))), false);
                        if (!G.userLogin) {
                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    makeFailed(struct.messageId);
                                }
                            }, 200);
                        }
                    }
                }
            }
            //realm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * open profile for this room or user profile if room is chat
     */
    private void goToProfile() {
        if (G.fragmentActivity != null) {
            ((ActivityMain) G.fragmentActivity).lockNavigation();
        }

        if (chatType == CHAT) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    new HelperFragment(FragmentContactsProfile.newInstance(mRoomId, chatPeerId, CHAT.toString())).setReplace(false).load();
                }
            });
        } else if (chatType == GROUP) {

            if (!isChatReadOnly) {
                new HelperFragment(FragmentGroupProfile.newInstance(mRoomId, isNotJoin)).setReplace(false).load();

            }
        } else if (chatType == CHANNEL) {
            new HelperFragment(FragmentChannelProfile.newInstance(mRoomId, isNotJoin)).setReplace(false).load();
        }
    }

    /**
     * copy text
     */
    public void copySelectedItemTextToClipboard() {
        String copyText = "";
        for (AbstractMessage _message : mAdapter.getSelectedItems()) {
            String text = _message.mMessage.forwardedFrom != null ? _message.mMessage.forwardedFrom.getMessage() : _message.mMessage.messageText;
            if (text == null || text.length() == 0) {
                continue;
            }

            if (copyText.length() > 0) {
                copyText = copyText + "\n" + text;
            } else {
                copyText = text;
            }
        }

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Copied Text", copyText));

        mAdapter.deselect();
        toolbar.setVisibility(View.VISIBLE);
        ll_AppBarSelected.setVisibility(View.GONE);

        clearReplyView();
    }

    /**
     * *************************** init layout ***************************
     */

    /**
     * clear tag from edtChat and remove from view and delete from RealmRoomMessage
     */
    private void deleteItem(final long messageId, int position) {
        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
            if (Long.toString(messageId).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                edtChat.setTag(null);
            }
        }

        mAdapter.removeMessage(position);
        RealmRoomMessage.deleteMessage(messageId);
    }

    private void onSelectRoomMenu(String message, long item) {
        switch (message) {
            case "txtMuteNotification":
                muteNotification(item);
                break;
            case "txtClearHistory":
                clearHistory(item);
                break;
            case "txtDeleteChat":
                deleteChat(item);
                break;
        }
    }

    private void deleteChat(final long chatId) {
        new RequestChatDelete().chatDelete(chatId);
    }

    private void muteNotification(final long roomId) {
        //+Realm realm = Realm.getDefaultInstance();

        isMuteNotification = !isMuteNotification;
        new RequestClientMuteRoom().muteRoom(roomId, isMuteNotification);

        if (isMuteNotification) {
            ((TextView) rootView.findViewById(R.id.chl_txt_mute_channel)).setText(R.string.unmute);
            iconMute.setVisibility(View.VISIBLE);
        } else {
            ((TextView) rootView.findViewById(R.id.chl_txt_mute_channel)).setText(R.string.mute);
            iconMute.setVisibility(View.GONE);
        }

        if (G.onMute != null) {
            G.onMute.onChangeMuteState(mRoomId, isMuteNotification);
        }
        //realm.close();
    }

    private void removeLayoutUnreadMessage() {
        /**
         * remove unread layout message if already exist in chat list
         */
        if (isShowLayoutUnreadMessage) {
            for (int i = (mAdapter.getItemCount() - 1); i >= 0; i--) {
                if (mAdapter.getItem(i) instanceof UnreadMessage) {
                    mAdapter.remove(i);
                    break;
                }
            }
        }
        isShowLayoutUnreadMessage = false;
    }

    private void setBtnDownVisible(RealmRoomMessage realmRoomMessage) {
        if (isEnd()) {
            scrollToEnd();
        } else {
            if (countNewMessage == 0) {
                removeLayoutUnreadMessage();
                firstUnreadMessageInChat = realmRoomMessage;
            }
            countNewMessage++;
            llScrollNavigate.setVisibility(View.VISIBLE);
            txtNewUnreadMessage.setText(countNewMessage + "");
            txtNewUnreadMessage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * check difference position to end of adapter
     *
     * @return true if lower than END_CHAT_LIMIT otherwise return false
     */
    private boolean isEnd() {
        if (addToView) {
            if (((recyclerView.getLayoutManager()) == null) || ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() + END_CHAT_LIMIT > recyclerView.getAdapter().getItemCount()) {
                return true;
            }
        }
        return false;
        //return addToView && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() + END_CHAT_LIMIT > recyclerView.getAdapter().getItemCount();
    }

    /**
     * open fragment show image and show all image for this room
     */
    private void showImage(final StructMessageInfo messageInfo, View view) {

        if (!isAdded() || G.fragmentActivity.isFinishing()) {
            return;
        }

        // for gone app bar
        InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        long selectedFileToken = parseLong(messageInfo.messageID);

        FragmentShowImage fragment = FragmentShowImage.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong("RoomId", mRoomId);
        bundle.putLong("SelectedImage", selectedFileToken);
        fragment.setArguments(bundle);
        fragment.appBarLayout = appBarLayout;

        new HelperFragment(fragment).setReplace(false).load();
        //FragmentTransitionLauncher.with(G.fragmentActivity).from(view).prepare(fragment);
        //new HelperFragment(fragment).setAnimated(true).setReplace(false).load();

        //getActivity().getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).addToBackStack(null).commit();
    }

    /**
     * scroll to bottom if unread not exits otherwise go to unread line
     * hint : just do in loaded message
     */
    private void scrollToEnd() {
        if (recyclerView == null || recyclerView.getAdapter() == null) return;
        if (recyclerView.getAdapter().getItemCount() < 2) {
            return;
        }

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                    int lastPosition = llm.findLastVisibleItemPosition();
                    if (lastPosition + 30 > mAdapter.getItemCount()) {
                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    } else {
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    private void storingLastPosition() {
        try {
            if (recyclerView != null && mAdapter != null) {
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (mAdapter.getItem(lastVisibleItemPosition) instanceof TimeItem || mAdapter.getItem(lastVisibleItemPosition) instanceof UnreadMessage) {
                    lastVisibleItemPosition--;
                }

                if (mAdapter.getItem(lastVisibleItemPosition) instanceof TimeItem || mAdapter.getItem(lastVisibleItemPosition) instanceof UnreadMessage) {
                    lastVisibleItemPosition--;
                }

                long lastScrolledMessageID = 0;

                //if (firstVisiblePosition + 15 < mAdapter.getAdapterItemCount()) {
                //    lastScrolledMessageID = parseLong(mAdapter.getItem(firstVisiblePosition).mMessage.messageID);
                //}

                if (lastVisibleItemPosition < mAdapter.getAdapterItemCount() - 2) {
                    lastScrolledMessageID = parseLong(mAdapter.getItem(lastVisibleItemPosition).mMessage.messageID);
                }

                saveMessageIdPositionState(lastScrolledMessageID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && G.twoPaneMode) {
            G.maxChatBox = width - (width / 3) - ViewMaker.i_Dp(R.dimen.dp80);
        } else {
            G.maxChatBox = width - ViewMaker.i_Dp(R.dimen.dp80);
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateShowItemInScreen();
            }
        }, 300);

        super.onConfigurationChanged(newConfig);
    }

    /**
     * save latest messageId position that user saw in chat before close it
     */
    private void saveMessageIdPositionState(final long messageId) {
        RealmRoom.setLastScrollPosition(mRoomId, messageId);
    }

    /**
     * emoji initialization
     */
    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView.findViewById(ac_ll_parent)).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {

            @Override
            public void onEmojiBackspaceClick(View v) {

            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
                isEmojiSHow = true;
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {

            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
                isEmojiSHow = false;
            }
        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).build(edtChat);
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        imvSmileButton.setText(drawableResourceId);
    }

    /**
     * *************************** draft ***************************
     */
    private void setDraftMessage(final int requestCode) {

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listPathString == null) return;
                if (listPathString.size() < 1) return;
                if (listPathString.get(0) == null) return;
                String filename = listPathString.get(0).substring(listPathString.get(0).lastIndexOf("/") + 1);
                switch (requestCode) {
                    case AttachFile.request_code_TAKE_PICTURE:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.image_selected_for_send) + "\n" + filename);
                        break;
                    case AttachFile.requestOpenGalleryForImageMultipleSelect:
                        if (listPathString.size() == 1) {
                            if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {
                                txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.image_selected_for_send) + "\n" + filename);
                            } else {
                                txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.gif_selected_for_send) + "\n" + filename);
                            }
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + G.fragmentActivity.getResources().getString(R.string.image_selected_for_send) + "\n" + filename);
                        }

                        break;

                    case AttachFile.requestOpenGalleryForVideoMultipleSelect:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.multi_video_selected_for_send) + "\n" + filename);
                        break;
                    case request_code_VIDEO_CAPTURED:

                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.video_selected_for_send));
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + G.fragmentActivity.getResources().getString(R.string.video_selected_for_send) + "\n" + filename);
                        }
                        break;

                    case AttachFile.request_code_pic_audi:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.audio_selected_for_send) + "\n" + filename);
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + G.fragmentActivity.getResources().getString(R.string.audio_selected_for_send) + "\n" + filename);
                        }
                        break;
                    case AttachFile.request_code_pic_file:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.file_selected_for_send) + "\n" + filename);
                        break;
                    case AttachFile.request_code_open_document:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.file_selected_for_send) + "\n" + filename);
                        }
                        break;
                    case AttachFile.request_code_paint:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.pain_selected_for_send) + "\n" + filename);
                        }
                        break;
                    case AttachFile.request_code_contact_phone:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.phone_selected_for_send) + "\n" + filename);
                        break;
                    case IntentRequests.REQ_CROP:
                        if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.crop_selected_for_send) + "\n" + filename);
                        } else {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.gif_selected_for_send) + "\n" + filename);
                        }
                        break;
                }
            }
        }, 100);
    }

    private void showDraftLayout() {
        /**
         * onActivityResult happens before onResume, so Presenter does not have View attached. because use handler
         */
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listPathString == null) return;
                if (listPathString.size() < 1) return;
                if (listPathString.get(0) == null) return;
                if (ll_attach_text == null) { // have null error , so reInitialize for avoid that

                    ll_attach_text = (LinearLayout) rootView.findViewById(R.id.ac_ll_attach_text);
                    layoutAttachBottom = (LinearLayout) rootView.findViewById(R.id.layoutAttachBottom);
                    imvSendButton = (MaterialDesignTextView) rootView.findViewById(R.id.chl_imv_send_button);
                }

                ll_attach_text.setVisibility(View.VISIBLE);
                // set maxLength  when layout attachment is visible
                edtChat.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Config.MAX_TEXT_ATTACHMENT_LENGTH)});

                layoutAttachBottom.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layoutAttachBottom.setVisibility(View.GONE);
                    }
                }).start();
                imvSendButton.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imvSendButton.clearAnimation();
                                imvSendButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).start();
            }
        }, 100);
    }

    /**
     * *************************** inner classes ***************************
     */

    private void setDraft() {
        if (!isNotJoin) {
            if (edtChat == null) {
                return;
            }

            if (mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE) {
                StructMessageInfo info = ((StructMessageInfo) mReplayLayout.getTag());
                if (info != null) {
                    replyToMessageId = parseLong(info.messageID);
                }
            } else {
                replyToMessageId = 0;
            }

            String message = edtChat.getText().toString();
            if (!message.trim().isEmpty() || ((mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE))) {
                hasDraft = true;
                RealmRoom.setDraft(mRoomId, message, replyToMessageId);

                if (chatType == CHAT) {
                    new RequestChatUpdateDraft().chatUpdateDraft(mRoomId, message, replyToMessageId);
                } else if (chatType == GROUP) {
                    new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId, message, replyToMessageId);
                } else if (chatType == CHANNEL) {
                    new RequestChannelUpdateDraft().channelUpdateDraft(mRoomId, message, replyToMessageId);
                }
                if (G.onDraftMessage != null) {
                    G.onDraftMessage.onDraftMessage(mRoomId, message);
                }
            } else {
                clearDraftRequest();
            }
        }
    }

    private void getDraft() {
        //+Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {
            RealmRoomDraft draft = realmRoom.getDraft();
            if (draft != null && draft.getMessage().length() > 0) {
                hasDraft = true;
                edtChat.setText(draft.getMessage());
            }
        }
        //realm.close();
        clearLocalDraft();
    }

    private void clearLocalDraft() {
        RealmRoom.clearDraft(mRoomId);
    }

    private void clearDraftRequest() {
        if (hasDraft) {
            hasDraft = false;
            if (chatType == CHAT) {
                new RequestChatUpdateDraft().chatUpdateDraft(mRoomId, "", 0);
            } else if (chatType == GROUP) {
                new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId, "", 0);
            } else if (chatType == CHANNEL) {
                new RequestChannelUpdateDraft().channelUpdateDraft(mRoomId, "", 0);
            }

            clearLocalDraft();
        }
    }

    /**
     * *************************** sheared data ***************************
     */


    private void insertShearedData() {
        /**
         * run this method with delay , because client get local message with delay
         * for show messages with async state and before run getLocalMessage this shared
         * item added to realm and view, and after that getLocalMessage called and new item
         * got from realm and add to view again but in this time from getLocalMessage method
         */
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HelperGetDataFromOtherApp.hasSharedData) {
                    HelperGetDataFromOtherApp.hasSharedData = false;

                    ArrayList<String> pathList = new ArrayList<String>();

                    if (messageType != HelperGetDataFromOtherApp.FileType.message) {
                        if (HelperGetDataFromOtherApp.messageFileAddress.size() == 1) {

                            Uri _Uri = HelperGetDataFromOtherApp.messageFileAddress.get(0);
                            String _path = getFilePathFromUri(Uri.parse(_Uri.toString()));
                            if (_path == null) {
                                _path = getPathN(_Uri, messageType);
                            }
                            pathList.add(_path);
                        } else {

                            for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {

                                Uri _Uri = HelperGetDataFromOtherApp.messageFileAddress.get(i);
                                String _path = getFilePathFromUri(Uri.parse(_Uri.toString()));

                                if (_path == null) {
                                    _path = getPathN(_Uri, HelperGetDataFromOtherApp.fileTypeArray.get(i));
                                }
                                pathList.add(_path);
                            }
                        }
                    }

                    if (messageType == HelperGetDataFromOtherApp.FileType.message) {
                        String message = HelperGetDataFromOtherApp.message;
                        edtChat.setText(message);
                        imvSendButton.performClick();
                    } else if (messageType == HelperGetDataFromOtherApp.FileType.image) {

                        for (int i = 0; i < pathList.size(); i++) {
                            sendMessage(AttachFile.request_code_TAKE_PICTURE, pathList.get(i));
                        }
                    } else if (messageType == HelperGetDataFromOtherApp.FileType.video) {
                        if (pathList.size() == 1 && (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && (sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1))) {
//                            final String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                            final String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                            mainVideoPath = pathList.get(0);

                            if (mainVideoPath == null) {
                                return;
                            }

                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new VideoCompressor().execute(mainVideoPath, savePathVideoCompress);
                                }
                            }, 200);
                            sendMessage(request_code_VIDEO_CAPTURED, savePathVideoCompress);
                        } else {
                            for (int i = 0; i < pathList.size(); i++) {
                                compressedPath.put(pathList.get(i), true);
                                sendMessage(request_code_VIDEO_CAPTURED, pathList.get(i));
                            }
                        }
                    } else if (messageType == HelperGetDataFromOtherApp.FileType.audio) {

                        for (int i = 0; i < pathList.size(); i++) {
                            sendMessage(AttachFile.request_code_pic_audi, pathList.get(i));
                        }
                    } else if (messageType == HelperGetDataFromOtherApp.FileType.file) {

                        for (int i = 0; i < pathList.size(); i++) {
                            HelperGetDataFromOtherApp.FileType fileType = messageType = HelperGetDataFromOtherApp.FileType.file;
                            if (HelperGetDataFromOtherApp.fileTypeArray.size() > 0) {
                                fileType = HelperGetDataFromOtherApp.fileTypeArray.get(i);
                            }

                            if (fileType == HelperGetDataFromOtherApp.FileType.image) {
                                sendMessage(AttachFile.request_code_TAKE_PICTURE, pathList.get(i));
                            } else if (fileType == HelperGetDataFromOtherApp.FileType.video) {
                                if (pathList.size() == 1 && (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && (sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1))) {
                                    //                                    final String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format
                                    // (new Date()) + ".mp4";
                                    final String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                                    mainVideoPath = pathList.get(0);

                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new VideoCompressor().execute(mainVideoPath, savePathVideoCompress);
                                        }
                                    }, 200);
                                    sendMessage(request_code_VIDEO_CAPTURED, savePathVideoCompress);
                                } else {
                                    compressedPath.put(pathList.get(i), true);
                                    sendMessage(request_code_VIDEO_CAPTURED, pathList.get(i));
                                }
                            } else if (fileType == HelperGetDataFromOtherApp.FileType.audio) {
                                sendMessage(AttachFile.request_code_pic_audi, pathList.get(i));
                            } else if (fileType == HelperGetDataFromOtherApp.FileType.file) {
                                sendMessage(AttachFile.request_code_open_document, pathList.get(i));
                            }
                        }
                    }
                    messageType = null;
                }
            }
        }, 300);
    }

    private void shearedDataToOtherProgram(StructMessageInfo messageInfo) {

        if (messageInfo == null) return;

        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String chooserDialogText = "";

            ProtoGlobal.RoomMessageType type = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessageType() : messageInfo.messageType;

            switch (type.toString()) {

                case "TEXT":
                    intent.setType("text/plain");
                    String message = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessage() : messageInfo.messageText;
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    break;
                case "CONTACT":
                    intent.setType("text/plain");
                    String messageContact;
                    if (messageInfo.forwardedFrom != null) {
                        messageContact = messageInfo.forwardedFrom.getRoomMessageContact().getFirstName() + " " + messageInfo.forwardedFrom.getRoomMessageContact().getLastName() + "\n" + messageInfo.forwardedFrom.getRoomMessageContact().getLastPhoneNumber();
                    } else {
                        messageContact = messageInfo.userInfo.firstName + "\n" + messageInfo.userInfo.phone;
                    }
                    intent.putExtra(Intent.EXTRA_TEXT, messageContact);
                    break;
                case "LOCATION":
                    String imagePathPosition = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getLocation().getImagePath() : messageInfo.location.getImagePath();
                    intent.setType("image/*");
                    if (imagePathPosition != null) {
                        intent.putExtra(Intent.EXTRA_STREAM, AppUtils.createtUri(new File(imagePathPosition)));
                    }
                    break;
                case "VOICE":
                case "AUDIO":
                case "AUDIO_TEXT":
                    intent.setType("audio/*");
                    AppUtils.shareItem(intent, messageInfo);
                    chooserDialogText = G.fragmentActivity.getResources().getString(R.string.share_audio_file);
                    break;
                case "IMAGE":
                case "IMAGE_TEXT":
                    intent.setType("image/*");
                    AppUtils.shareItem(intent, messageInfo);
                    chooserDialogText = G.fragmentActivity.getResources().getString(R.string.share_image);
                    break;
                case "VIDEO":
                case "VIDEO_TEXT":
                    intent.setType("image/*");
                    AppUtils.shareItem(intent, messageInfo);
                    chooserDialogText = G.fragmentActivity.getResources().getString(R.string.share_video_file);
                    break;
                case "FILE":
                case "FILE_TEXT":
                    String mfilepath = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getLocalFilePath() : messageInfo.attachment.getLocalFilePath();
                    if (mfilepath != null) {
                        Uri uri = AppUtils.createtUri(new File(mfilepath));

                        ContentResolver cR = context.getContentResolver();
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String mimeType = mime.getExtensionFromMimeType(cR.getType(uri));


                        if (mimeType == null || mimeType.length() < 1) {
                            mimeType = "*/*";
                        } else {
                            mimeType = "application/" + mimeType;
                        }
                        intent.setType(mimeType);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        chooserDialogText = G.fragmentActivity.getResources().getString(R.string.share_file);
                    } else {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, R.string.file_not_download_yet, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    break;
            }

            startActivity(Intent.createChooser(intent, chooserDialogText));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init layout for hashtak up and down
     */
    private void initLayoutHashNavigationCallback() {

        hashListener = new OnComplete() {
            @Override
            public void complete(boolean result, String text, String messageId) {

                if (!initHash) {
                    initHash = true;
                    initHashView();
                }

                searchHash.setHashString(text);
                searchHash.setPosition(messageId);
                ll_navigateHash.setVisibility(View.VISIBLE);
                viewAttachFile.setVisibility(View.GONE);

                if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER) {
                    if (layoutMute != null) layoutMute.setVisibility(View.GONE);
                }
            }
        };
    }

    /**
     * init layout hashtak for up and down
     */
    private void initHashView() {
        ll_navigateHash = (LinearLayout) rootView.findViewById(R.id.ac_ll_hash_navigation);
        btnUpHash = (TextView) rootView.findViewById(R.id.ac_btn_hash_up);
        btnDownHash = (TextView) rootView.findViewById(R.id.ac_btn_hash_down);
        txtHashCounter = (TextView) rootView.findViewById(R.id.ac_txt_hash_counter);

        btnUpHash.setTextColor(Color.parseColor(G.appBarColor));
        btnDownHash.setTextColor(Color.parseColor(G.appBarColor));
        txtHashCounter.setTextColor(Color.parseColor(G.appBarColor));

        searchHash = new SearchHash();

        btnHashLayoutClose = (MaterialDesignTextView) rootView.findViewById(R.id.ac_btn_hash_close);
        btnHashLayoutClose.setTextColor(Color.parseColor(G.appBarColor));
        btnHashLayoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_navigateHash.setVisibility(View.GONE);

                mAdapter.toggleSelection(searchHash.lastMessageId, false, null);

                if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER && !isNotJoin) {
                    layoutMute.setVisibility(View.VISIBLE);
                } else {
                    if (!isNotJoin) viewAttachFile.setVisibility(View.VISIBLE);
                }
            }
        });

        btnUpHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchHash.upHash();
            }
        });

        btnDownHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchHash.downHash();
            }
        });
    }

    /**
     * manage need showSpamBar for user or no
     */
    private void showSpamBar() {
        /**
         * use handler for run async
         */
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                //+Realm realm = Realm.getDefaultInstance();
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
                RealmContacts realmContacts = getRealmChat().where(RealmContacts.class).equalTo(RealmContactsFields.ID, chatPeerId).findFirst();
                if (realmRegisteredInfo != null && realmRegisteredInfo.getId() != G.userId) {
                    if (phoneNumber == null) {
                        if (realmContacts == null && chatType == CHAT && !isChatReadOnly) {
                            initSpamBarLayout(realmRegisteredInfo);
                            vgSpamUser.setVisibility(View.VISIBLE);
                        }
                    }

                    if (realmRegisteredInfo.getId() != G.userId) {
                        if (!realmRegisteredInfo.getDoNotshowSpamBar()) {

                            if (realmRegisteredInfo.isBlockUser()) {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            } else {
                                if (vgSpamUser != null) {
                                    vgSpamUser.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    if (realmContacts != null && realmRegisteredInfo.getId() != G.userId) {
                        if (realmContacts.isBlockUser()) {
                            if (!realmRegisteredInfo.getDoNotshowSpamBar()) {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            } else {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (vgSpamUser != null) {
                                vgSpamUser.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                //realm.close();
            }
        });
    }

    /**
     * init spamBar layout
     */
    private void initSpamBarLayout(final RealmRegisteredInfo registeredInfo) {
        vgSpamUser = (ViewGroup) rootView.findViewById(R.id.layout_add_contact);
        txtSpamUser = (TextView) rootView.findViewById(R.id.chat_txt_addContact);
        txtSpamClose = (TextView) rootView.findViewById(R.id.chat_txt_close);
        txtSpamClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vgSpamUser.setVisibility(View.GONE);
                if (registeredInfo != null) {

                    //+Realm realm = Realm.getDefaultInstance();

                    getRealmChat().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            registeredInfo.setDoNotshowSpamBar(true);
                        }
                    });

                    //realm.close();
                }
            }
        });

        txtSpamUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blockUser) {
                    G.onUserContactsUnBlock = new OnUserContactsUnBlock() {
                        @Override
                        public void onUserContactsUnBlock(final long userId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = false;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.block_user));
                                    }
                                }
                            });
                        }
                    };

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.unblock_the_user).content(R.string.unblock_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new RequestUserContactsUnblock().userContactsUnblock(chatPeerId);
                        }
                    }).negativeText(R.string.cancel).show();
                } else {

                    G.onUserContactsBlock = new OnUserContactsBlock() {
                        @Override
                        public void onUserContactsBlock(final long userId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = true;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                                    }
                                }
                            });
                        }
                    };

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.block_the_user).content(R.string.block_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new RequestUserContactsBlock().userContactsBlock(chatPeerId);
                        }
                    }).negativeText(R.string.cancel).show();

                }
            }
        });
    }

    /**
     * initialize bottomSheet for use in attachment for forward
     */


    private void initAttachForward() {
        canClearForwardList = true;
        multiForwardList = new ArrayList<>();
        viewBottomSheetForward = G.fragmentActivity.getLayoutInflater().inflate(R.layout.bottom_sheet_forward, null);

        fastItemAdapterForward = new FastItemAdapter();

        EditText edtSearch = (EditText) viewBottomSheetForward.findViewById(R.id.edtSearch);
        final TextView textSend = (MaterialDesignTextView) viewBottomSheetForward.findViewById(R.id.txtSend);
        textSend.setVisibility(View.INVISIBLE);
        final RecyclerView rcvItem = (RecyclerView) viewBottomSheetForward.findViewById(R.id.rcvBottomSheetForward);
        rcvItem.setLayoutManager(new GridLayoutManager(G.fragmentActivity, 4, GridLayoutManager.VERTICAL, false));
        rcvItem.setItemViewCacheSize(100);
        rcvItem.setAdapter(fastItemAdapterForward);
        bottomSheetDialogForward = new BottomSheetDialog(G.fragmentActivity);
        bottomSheetDialogForward.setContentView(viewBottomSheetForward);
        final BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) viewBottomSheetForward.getParent());

        fastItemAdapterForward.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<AdapterBottomSheetForward>() {
            @Override
            public boolean filter(AdapterBottomSheetForward item, CharSequence constraint) {
                return item.mList.getDisplayName().toLowerCase().startsWith(String.valueOf(constraint));
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fastItemAdapterForward.filter(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        viewBottomSheetForward.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mBehavior.setPeekHeight(viewBottomSheetForward.getHeight());
                    viewBottomSheetForward.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        //height is ready


        textSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canClearForwardList = false;
                forwardToChatRoom(mListForwardNotExict);
                prgWaiting.setVisibility(View.VISIBLE);
                viewBottomSheetForward.setEnabled(false);
            }
        });

        onForwardBottomSheet = new OnForwardBottomSheet() {
            @Override
            public void path(StructBottomSheetForward path, boolean isCheck, boolean isNotExist) {

                if (path.isNotExistRoom()) {
                    if (isCheck) {
                        mListForwardNotExict.add(path);
                    } else {
                        mListForwardNotExict.remove(path);
                    }
                } else {
                    if (isCheck) {
                        multiForwardList.add(path.getId());
                    } else {
                        multiForwardList.remove(path.getId());
                    }
                }

                if (mListForwardNotExict.size() + multiForwardList.size() > 0) {
                    textSend.setVisibility(View.VISIBLE);
                } else {
                    textSend.setVisibility(View.INVISIBLE);
                }
            }
        };

        bottomSheetDialogForward.show();


        bottomSheetDialogForward.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (canClearForwardList) {
                    mForwardMessages = null;
                }
            }
        });
    }

    /**
     * initialize bottomSheet for use in attachment
     */
    private void initAttach() {

        fastItemAdapter = new FastItemAdapter();

        viewBottomSheet = G.fragmentActivity.getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        TextView txtCamera = (TextView) viewBottomSheet.findViewById(R.id.txtCamera);
        TextView textPicture = (TextView) viewBottomSheet.findViewById(R.id.textPicture);
        TextView txtVideo = (TextView) viewBottomSheet.findViewById(R.id.txtVideo);
        TextView txtMusic = (TextView) viewBottomSheet.findViewById(R.id.txtMusic);
        TextView txtDocument = (TextView) viewBottomSheet.findViewById(R.id.txtDocument);
        TextView txtFile = (TextView) viewBottomSheet.findViewById(R.id.txtFile);
        TextView txtPaint = (TextView) viewBottomSheet.findViewById(R.id.txtPaint);
        TextView txtLocation = (TextView) viewBottomSheet.findViewById(R.id.txtLocation);
        TextView txtContact = (TextView) viewBottomSheet.findViewById(R.id.txtContact);
        send = (TextView) viewBottomSheet.findViewById(R.id.txtSend);

        txtCamera.setTextColor(Color.parseColor(G.attachmentColor));
        textPicture.setTextColor(Color.parseColor(G.attachmentColor));
        txtVideo.setTextColor(Color.parseColor(G.attachmentColor));
        txtMusic.setTextColor(Color.parseColor(G.attachmentColor));
        txtDocument.setTextColor(Color.parseColor(G.attachmentColor));
        txtFile.setTextColor(Color.parseColor(G.attachmentColor));
        txtPaint.setTextColor(Color.parseColor(G.attachmentColor));
        txtLocation.setTextColor(Color.parseColor(G.attachmentColor));
        txtContact.setTextColor(Color.parseColor(G.attachmentColor));
        send.setTextColor(Color.parseColor(G.attachmentColor));

        txtCountItem = (TextView) viewBottomSheet.findViewById(R.id.txtNumberItem);
        ViewGroup camera = (ViewGroup) viewBottomSheet.findViewById(R.id.camera);
        ViewGroup picture = (ViewGroup) viewBottomSheet.findViewById(R.id.picture);
        ViewGroup video = (ViewGroup) viewBottomSheet.findViewById(R.id.video);
        ViewGroup music = (ViewGroup) viewBottomSheet.findViewById(R.id.music);
        ViewGroup document = (ViewGroup) viewBottomSheet.findViewById(R.id.document);
        ViewGroup close = (ViewGroup) viewBottomSheet.findViewById(R.id.close);
        ViewGroup file = (ViewGroup) viewBottomSheet.findViewById(R.id.file);
        ViewGroup paint = (ViewGroup) viewBottomSheet.findViewById(R.id.paint);
        ViewGroup location = (ViewGroup) viewBottomSheet.findViewById(R.id.location);
        ViewGroup contact = (ViewGroup) viewBottomSheet.findViewById(R.id.contact);

        onPathAdapterBottomSheet = new OnPathAdapterBottomSheet() {
            @Override
            public void path(String path, boolean isCheck, boolean isEdit) {

                if (isCheck) {
                    listPathString.add(path);
                } else {
                    listPathString.remove(path);
                }

                if (isEdit) {
                    bottomSheetDialog.dismiss();
                    new HelperFragment(FragmentEditImage.newInstance(path, true, false)).setReplace(false).load();
//                    new HelperFragment(FragmentFilterImage.newInstance(path)).setReplace(false).load();
                } else {
                    listPathString.size();
                    if (listPathString.size() > 0) {
                        //send.setText(R.mipmap.send2);
                        send.setText(G.fragmentActivity.getResources().getString(R.string.md_send_button));
                        isCheckBottomSheet = true;
                        txtCountItem.setText("" + listPathString.size() + " " + G.fragmentActivity.getResources().getString(item));
                    } else {
                        //send.setImageResource(R.mipmap.ic_close);
                        send.setText(G.fragmentActivity.getResources().getString(R.string.igap_chevron_double_down));
                        isCheckBottomSheet = false;
                        txtCountItem.setText(G.fragmentActivity.getResources().getString(R.string.navigation_drawer_close));
                    }
                }
            }
        };


        FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
            @Override
            public void result(String path, String message) {
                listPathString = null;
                listPathString = new ArrayList<>();
                listPathString.add(path);
                edtChat.setText(message);
                latestRequestCode = AttachFile.requestOpenGalleryForImageMultipleSelect;
                ll_attach_text.setVisibility(View.VISIBLE);
                imvSendButton.performClick();
            }
        };

        rcvBottomSheet = (RecyclerView) viewBottomSheet.findViewById(R.id.rcvContent);
        rcvBottomSheet.setLayoutManager(new GridLayoutManager(G.fragmentActivity, 1, GridLayoutManager.HORIZONTAL, false));
        rcvBottomSheet.setItemViewCacheSize(100);
        rcvBottomSheet.setAdapter(fastItemAdapter);
        bottomSheetDialog = new BottomSheetDialog(G.fragmentActivity);
        bottomSheetDialog.setContentView(viewBottomSheet);
        final BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) viewBottomSheet.getParent());

        viewBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mBehavior.setPeekHeight(viewBottomSheet.getHeight());
                    viewBottomSheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        //height is ready

        bottomSheetDialog.show();
        onClickCamera = new OnClickCamera() {
            @Override
            public void onclickCamera() {
                try {
                    bottomSheetDialog.dismiss();
                    new AttachFile(G.fragmentActivity).requestTakePicture(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        rcvBottomSheet.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(final View view) {
                if (isPermissionCamera) {

                    if (rcvBottomSheet.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = true;
                    }
                    if (isCameraAttached) {
                        if (fotoapparatSwitcher != null) {
                            if (!isCameraStart) {
                                isCameraStart = true;
                                try {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            fotoapparatSwitcher.start();
                                        }
                                    }, 50);
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        } else {
                            if (!isCameraStart) {
                                isCameraStart = true;
                                try {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            fotoapparatSwitcher = Fotoapparat.with(G.fragmentActivity).into((CameraRenderer) view.findViewById(R.id.cameraView))           // view which will draw the camera preview
                                                    .photoSize(biggestSize())   // we want to have the biggest photo possible
                                                    .lensPosition(back())       // we want back camera
                                                    .build();

                                            fotoapparatSwitcher.start();
                                        }
                                    }, 100);
                                } catch (IllegalStateException e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(final View view) {

                if (isPermissionCamera) {
                    if (rcvBottomSheet.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = false;
                    }
                    if (!isCameraAttached) {
                        if (fotoapparatSwitcher != null) {
                            //                    if (isCameraStart && ( rcvBottomSheet.getChildAdapterPosition(view)> 4  || rcvBottomSheet.computeHorizontalScrollOffset() >200)){
                            if (isCameraStart) {

                                try {
                                    fotoapparatSwitcher.stop();
                                    isCameraStart = false;
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        } else {
                            if (!isCameraStart) {
                                isCameraStart = false;
                                try {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            fotoapparatSwitcher = Fotoapparat.with(G.fragmentActivity).into((CameraRenderer) view.findViewById(R.id.cameraView))           // view which will draw the camera preview
                                                    .photoSize(biggestSize())   // we want to have the biggest photo possible
                                                    .lensPosition(back())       // we want back camera
                                                    .build();

                                            fotoapparatSwitcher.stop();
                                        }
                                    }, 100);
                                } catch (IllegalStateException e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }
                }
            }
        });

        rcvBottomSheet.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(final View v) {
                if (isPermissionCamera) {

                    if (fotoapparatSwitcher != null) {
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isCameraStart) {
                                    fotoapparatSwitcher.start();
                                    isCameraStart = true;
                                }
                            }
                        }, 50);
                    }
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (isPermissionCamera) {
                    if (fotoapparatSwitcher != null) {
                        if (isCameraStart) {
                            fotoapparatSwitcher.stop();
                            isCameraStart = false;
                        }
                    }
                }
            }
        });

        if (HelperPermission.grantedUseStorage()) {
            rcvBottomSheet.setVisibility(View.VISIBLE);
        } else {
            rcvBottomSheet.setVisibility(View.GONE);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
                //send.setImageResource(R.mipmap.ic_close);
                send.setText(G.fragmentActivity.getResources().getString(R.string.igap_chevron_double_down));
                txtCountItem.setText(G.fragmentActivity.getResources().getString(R.string.navigation_drawer_close));
                itemGalleryList.clear();
            }
        });

        listPathString = new ArrayList<>();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                if (sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {
                    attachFile.showDialogOpenCamera(toolbar, null, FragmentChat.this);
                } else {
                    attachFile.showDialogOpenCamera(toolbar, null, FragmentChat.this);
                }
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestOpenGalleryForImageMultipleSelect(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestOpenGalleryForVideoMultipleSelect(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPickAudio(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestOpenDocumentFolder(new IPickFile() {
                        @Override
                        public void onPick(ArrayList<String> selectedPathList) {

                            for (String path : selectedPathList) {
                                Intent data = new Intent();
                                data.setData(Uri.parse(path));
                                onActivityResult(request_code_open_document, Activity.RESULT_OK, data);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCheckBottomSheet) {
                    bottomSheetDialog.dismiss();

                    fastItemAdapter.clear();
                    //send.setImageResource(R.mipmap.ic_close);
                    send.setText(G.fragmentActivity.getResources().getString(R.string.igap_chevron_double_down));
                    txtCountItem.setText(G.fragmentActivity.getResources().getString(R.string.navigation_drawer_close));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<String> pathStrings = listPathString;
                                    if (pathStrings.size() == 1) {
                                        showDraftLayout();
                                        setDraftMessage(AttachFile.requestOpenGalleryForImageMultipleSelect);
                                        latestRequestCode = AttachFile.requestOpenGalleryForImageMultipleSelect;
                                        //sendMessage(AttachFile.requestOpenGalleryForImageMultipleSelect, pathStrings.get(0));
                                    } else {
                                        for (String path : pathStrings) {
                                            //if (!path.toLowerCase().endsWith(".gif")) {
                                            String localPathNew = attachFile.saveGalleryPicToLocal(path);
                                            sendMessage(AttachFile.requestOpenGalleryForImageMultipleSelect, localPathNew);
                                            //}
                                        }
                                    }

                                }
                            });
                        }
                    }).start();
                } else {
                    bottomSheetDialog.dismiss();
                }
            }
        });
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPickFile(new IPickFile() {
                        @Override
                        public void onPick(ArrayList<String> selectedPathList) {
                            for (String path : selectedPathList) {
                                Intent data = new Intent();
                                data.setData(Uri.parse(path));
                                onActivityResult(request_code_pic_file, Activity.RESULT_OK, data);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPaint(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestGetPosition(complete, FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPickContact(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void inflateReplayLayoutIntoStub(StructMessageInfo chatItem) {
        if (rootView.findViewById(R.id.replayLayoutAboveEditText) == null) {
            ViewStubCompat stubView = (ViewStubCompat) rootView.findViewById(R.id.replayLayoutStub);
            stubView.setInflatedId(R.id.replayLayoutAboveEditText);
            stubView.setLayoutResource(R.layout.layout_chat_reply);
            stubView.inflate();

            inflateReplayLayoutIntoStub(chatItem);
        } else {
            mReplayLayout = (LinearLayout) rootView.findViewById(R.id.replayLayoutAboveEditText);
            mReplayLayout.setVisibility(View.VISIBLE);
            TextView replayTo = (TextView) mReplayLayout.findViewById(R.id.replayTo);
            replayTo.setTypeface(G.typeface_IRANSansMobile);
            TextView replayFrom = (TextView) mReplayLayout.findViewById(R.id.replyFrom);
            replayFrom.setTypeface(G.typeface_IRANSansMobile);
            replayFrom.setTextColor(Color.parseColor(G.appBarColor));

            ImageView imvReplayIcon = (ImageView) rootView.findViewById(R.id.lcr_imv_replay);
            imvReplayIcon.setColorFilter(Color.parseColor(G.appBarColor));

            ImageView thumbnail = (ImageView) mReplayLayout.findViewById(R.id.thumbnail);
            TextView closeReplay = (TextView) mReplayLayout.findViewById(R.id.cancelIcon);
            closeReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearReplyView();
                }
            });
            //+Realm realm = Realm.getDefaultInstance();
            thumbnail.setVisibility(View.VISIBLE);
            if (chatItem.forwardedFrom != null) {
                AppUtils.rightFileThumbnailIcon(thumbnail, chatItem.forwardedFrom.getMessageType(), chatItem.forwardedFrom);

                String _text = AppUtils.conversionMessageType(chatItem.forwardedFrom.getMessageType());
                if (_text != null && _text.length() > 0) {
                    replayTo.setText(_text);
                } else {
                    replayTo.setText(chatItem.forwardedFrom.getMessage());
                }
            } else {
                RealmRoomMessage message = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(chatItem.messageID)).findFirst();
                AppUtils.rightFileThumbnailIcon(thumbnail, chatItem.messageType, message);

                String _text = AppUtils.conversionMessageType(chatItem.messageType);
                if (_text != null && _text.length() > 0) {
                    replayTo.setText(_text);
                } else {
                    replayTo.setText(chatItem.messageText);
                }
            }
            if (chatType == CHANNEL) {
                RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatItem.roomId).findFirst();
                if (realmRoom != null) {
                    replayFrom.setText(realmRoom.getTitle());
                }
            } else {
                RealmRegisteredInfo userInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), parseLong(chatItem.senderID));
                if (userInfo != null) {
                    replayFrom.setText(userInfo.getDisplayName());
                }
            }

            //realm.close();
            // I set tag to retrieve it later when sending message
            mReplayLayout.setTag(chatItem);
        }
    }

    private void initLayoutChannelFooter() {
        LinearLayout layoutAttach = (LinearLayout) rootView.findViewById(R.id.layout_attach_file);
        if (layoutMute == null) {
            layoutMute = (RelativeLayout) rootView.findViewById(R.id.chl_ll_channel_footer);
        }


        layoutAttach.setVisibility(View.GONE);
        if (!isNotJoin) layoutMute.setVisibility(View.VISIBLE);


        layoutMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onSelectRoomMenu("txtMuteNotification", mRoomId);
            }
        });
        if (txtChannelMute == null)
            txtChannelMute = (TextView) rootView.findViewById(R.id.chl_txt_mute_channel);
        if (isMuteNotification) {
            txtChannelMute.setText(R.string.unmute);
        } else {
            txtChannelMute.setText(R.string.mute);
        }
    }

    private void initAppbarSelected() {
        ll_AppBarSelected = (LinearLayout) rootView.findViewById(R.id.chl_ll_appbar_selelected);

        RippleView rippleCloseAppBarSelected = (RippleView) rootView.findViewById(R.id.chl_ripple_close_layout);
        rippleCloseAppBarSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mAdapter.deselect();
                toolbar.setVisibility(View.VISIBLE);
                ll_AppBarSelected.setVisibility(View.GONE);
                clearReplyView();
            }
        });

        //  btnReplaySelected = (MaterialDesignTextView)  rootView.findViewById(R.id.chl_btn_replay_selected);
        rippleReplaySelected = (RippleView) rootView.findViewById(R.id.chl_ripple_replay_selected);

        if (chatType == CHANNEL) {
            if (channelRole == ChannelChatRole.MEMBER) {
                rippleReplaySelected.setVisibility(View.INVISIBLE);
            }
        } else {
            rippleReplaySelected.setVisibility(View.VISIBLE);
        }
        rippleReplaySelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (mAdapter != null && !mAdapter.getSelectedItems().isEmpty() && mAdapter.getSelectedItems().size() == 1) {
                    replay(mAdapter.getSelectedItems().iterator().next().mMessage);
                }
            }
        });
        RippleView rippleCopySelected = (RippleView) rootView.findViewById(R.id.chl_ripple_copy_selected);
        rippleCopySelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                copySelectedItemTextToClipboard();
            }
        });
        RippleView rippleForwardSelected = (RippleView) rootView.findViewById(R.id.chl_ripple_forward_selected);
        rippleForwardSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                // forward selected messages to room list for selecting room
                if (mAdapter != null && mAdapter.getSelectedItems().size() > 0) {
                    onForwardClick(null);
                }
            }
        });
        rippleDeleteSelected = (RippleView) rootView.findViewById(R.id.chl_ripple_delete_selected);
        rippleDeleteSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                final ArrayList<Long> list = new ArrayList<Long>();
                bothDeleteMessageId = new ArrayList<Long>();

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (final AbstractMessage item : mAdapter.getSelectedItems()) {
                            try {
                                if (item != null && item.mMessage != null && item.mMessage.messageID != null) {
                                    Long messageId = parseLong(item.mMessage.messageID);
                                    list.add(messageId);
                                    if (RealmRoomMessage.isBothDelete(item.mMessage.time)) {
                                        bothDeleteMessageId.add(messageId);
                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        final String count = list.size() + "";


                        if (chatType == ProtoGlobal.Room.Type.CHAT && !isCloudRoom && bothDeleteMessageId.size() > 0 && mAdapter.getSelectedItems().iterator().next().mMessage.senderID.equalsIgnoreCase(Long.toString(G.userId))) {
                            // show both Delete check box

                            String delete;
                            String textCheckBox = G.context.getResources().getString(R.string.st_checkbox_delete) + " " + title;
                            if (HelperCalander.isPersianUnicode) {
                                delete = HelperCalander.convertToUnicodeFarsiNumber(G.context.getResources().getString(R.string.st_desc_delete, count));

                            } else {
                                delete = HelperCalander.convertToUnicodeFarsiNumber(G.context.getResources().getString(R.string.st_desc_delete, "the"));

                            }
                            new MaterialDialog.Builder(G.fragmentActivity).limitIconToDefaultSize().content(delete).title(R.string.message).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (!dialog.isPromptCheckBoxChecked()) {
                                        bothDeleteMessageId = null;
                                    }

                                    RealmRoomMessage.deleteSelectedMessages(getRealmChat(), mRoomId, list, bothDeleteMessageId, chatType);
                                    deleteSelectedMessageFromAdapter(list);
                                }
                            }).checkBoxPrompt(textCheckBox, false, null).show();

                        } else {

                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.message).content(G.context.getResources().getString(R.string.st_desc_delete, count)).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    bothDeleteMessageId = null;
                                    RealmRoomMessage.deleteSelectedMessages(getRealmChat(), mRoomId, list, bothDeleteMessageId, chatType);
                                    deleteSelectedMessageFromAdapter(list);
                                }
                            }).show();
                        }
                    }
                });
            }
        });
        txtNumberOfSelected = (TextView) G.fragmentActivity.findViewById(R.id.chl_txt_number_of_selected);

        if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER && !isNotJoin) {
            initLayoutChannelFooter();
        }
    }

    private void deleteSelectedMessageFromAdapter(ArrayList<Long> list) {
        for (Long mId : list) {
            try {
                mAdapter.removeMessage(mId);
                // remove tag from edtChat if the message has deleted
                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    if (mId == Long.parseLong(((StructMessageInfo) edtChat.getTag()).messageID)) {
                        edtChat.setTag(null);
                    }
                }

                removeLayoutTimeIfNeed();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void removeLayoutTimeIfNeed() {
        if (mAdapter != null) {
            int size = mAdapter.getItemCount();
            for (int i = 0; i < size; i++) {

                if (mAdapter.getItem(i) instanceof TimeItem) {
                    if (i < size - 1) {
                        if (mAdapter.getItem(i + 1) instanceof TimeItem) {
                            mAdapter.remove(i);
                        }
                    } else {
                        mAdapter.remove(i);
                    }
                }
            }
        }
    }

    private void initLayoutSearchNavigation() {
        //  ll_navigate_Message = (LinearLayout)  rootView.findViewById(R.id.ac_ll_message_navigation);
        //  btnUpMessage = (TextView)  rootView.findViewById(R.id.ac_btn_message_up);
        txtClearMessageSearch = (MaterialDesignTextView) rootView.findViewById(R.id.ac_btn_clear_message_search);
        //  btnDownMessage = (TextView) findViewById(R.id.ac_btn_message_down);
        //  txtMessageCounter = (TextView) findViewById(R.id.ac_txt_message_counter);

        //btnUpMessage.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //
        //        if (selectedPosition > 0) {
        //            deSelectMessage(selectedPosition);
        //            selectedPosition--;
        //            selectMessage(selectedPosition);
        //            recyclerView.scrollToPosition(selectedPosition);
        //            txtMessageCounter.setText(selectedPosition + 1 + " " + getString(of) + " " + messageCounter);
        //        }
        //    }
        //});

        //btnDownMessage.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        if (selectedPosition < messageCounter - 1) {
        //            deSelectMessage(selectedPosition);
        //            selectedPosition++;
        //            selectMessage(selectedPosition);
        //            recyclerView.scrollToPosition(selectedPosition);
        //            txtMessageCounter.setText(selectedPosition + 1 + " " + getString(of) + messageCounter);
        //        }
        //    }
        //});

        final RippleView rippleClose = (RippleView) rootView.findViewById(R.id.chl_btn_close_ripple_search_message);
        rippleClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // deSelectMessage(selectedPosition);
                edtSearchMessage.setText("");
                btnHashLayoutClose.performClick();
            }
        });

        ll_Search = (LinearLayout) rootView.findViewById(R.id.ac_ll_search_message);
        RippleView rippleBack = (RippleView) rootView.findViewById(R.id.chl_ripple_back);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  deSelectMessage(selectedPosition);
                edtSearchMessage.setText("");
                ll_Search.setVisibility(View.GONE);
                rootView.findViewById(R.id.toolbarContainer).setVisibility(View.VISIBLE);
                //  ll_navigate_Message.setVisibility(View.GONE);
                // viewAttachFile.setVisibility(View.VISIBLE);

                btnHashLayoutClose.performClick();

                InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        //btnCloseLayoutSearch = (Button)  rootView.findViewById(R.id.ac_btn_close_layout_search_message);
        edtSearchMessage = (EditText) rootView.findViewById(R.id.chl_edt_search_message);
        edtSearchMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    if (FragmentChat.hashListener != null) {
                        FragmentChat.hashListener.complete(true, charSequence.toString(), "");
                    }
                } else {
                    btnHashLayoutClose.performClick();
                }

                //mAdapter.filter(charSequence);
                //
                //new Handler().postDelayed(new Runnable() {
                //    @Override
                //    public void run() {
                //        messageCounter = mAdapter.getAdapterItemCount();
                //
                //        if (messageCounter > 0) {
                //            selectedPosition = messageCounter - 1;
                //            recyclerView.scrollToPosition(selectedPosition);
                //
                //            if (charSequence.length() > 0) {
                //                selectMessage(selectedPosition);
                //                txtMessageCounter.setText(messageCounter + " " + getString(of) + " " + messageCounter);
                //            } else {
                //                txtMessageCounter.setText("0 " + getString(of) + " 0");
                //            }
                //        } else {
                //            txtMessageCounter.setText("0 " + getString(of) + " " + messageCounter);
                //            selectedPosition = 0;
                //        }
                //    }
                //}, 600);
                //
                if (charSequence.length() > 0) {
                    txtClearMessageSearch.setTextColor(Color.WHITE);
                    ((View) rippleClose).setEnabled(true);
                } else {
                    txtClearMessageSearch.setTextColor(Color.parseColor("#dededd"));
                    ((View) rippleClose).setEnabled(false);
                    //  txtMessageCounter.setText("0 " + getString(of) + " 0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void itemAdapterBottomSheetForward() {

        mListBottomSheetForward = new ArrayList<>();
        String[] fieldNames = {RealmRoomFields.IS_PINNED, RealmRoomFields.PIN_ID, RealmRoomFields.UPDATED_TIME};
        Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING, Sort.DESCENDING};
        results = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).
                equalTo(RealmRoomFields.READ_ONLY, false).notEqualTo(RealmRoomFields.ID, mRoomId).findAll().sort(fieldNames, sort);

        resultsContact = getRealmChat().where(RealmContacts.class).findAllSorted(RealmContactsFields.DISPLAY_NAME);

        List<Long> te = new ArrayList<>();
        te.add(chatPeerId);

        for (RealmRoom r : results) {
            StructBottomSheetForward item = new StructBottomSheetForward();
            item.setId(r.getId());
            if (r.getType() == ProtoGlobal.Room.Type.CHAT) {
                te.add(r.getChatRoom().getPeerId());
            }
            item.setDisplayName(r.getTitle());
            if (r.getChatRoom() != null) item.setPeer_id(r.getChatRoom().getPeerId());
            item.setType(r.getType());
            item.setContactList(false);
            item.setNotExistRoom(false);
            mListBottomSheetForward.add(item);
        }

        for (RealmContacts r : resultsContact) {
            if (!te.contains(r.getId())) {
                StructBottomSheetForward item = new StructBottomSheetForward();
                item.setId(r.getId());
                item.setDisplayName(r.getDisplay_name());
                item.setContactList(true);
                item.setNotExistRoom(true);
                mListBottomSheetForward.add(item);
            }
        }

        for (int i = 0; i < mListBottomSheetForward.size(); i++) {
            fastItemAdapterForward.add(new AdapterBottomSheetForward(mListBottomSheetForward.get(i)).withIdentifier(100 + i));
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    bottomSheetDialogForward.show();
                }
            }
        }, 100);
    }

    public void itemAdapterBottomSheet() {
        listPathString.clear();
        fastItemAdapter.clear();
        itemGalleryList = getAllShownImagesPath(G.fragmentActivity);

        boolean isCameraButtonSheet = sharedPreferences.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);

        if (isCameraButtonSheet) {
            try {
                HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {

                        for (int i = 0; i < itemGalleryList.size(); i++) {
                            if (i == 0) {
                                fastItemAdapter.add(new AdapterCamera("").withIdentifier(99 + i));
                                fastItemAdapter.add(new AdapterBottomSheet(itemGalleryList.get(i)).withIdentifier(100 + i));
                            } else {
                                fastItemAdapter.add(new AdapterBottomSheet(itemGalleryList.get(i)).withIdentifier(100 + i));
                            }
                            isPermissionCamera = true;
                        }
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isAdded()) {
                                    bottomSheetDialog.show();
                                }
                            }
                        }, 100);
                    }

                    @Override
                    public void deny() {

                        loadImageGallery();

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadImageGallery();
        }

    }

    private void loadImageGallery() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < itemGalleryList.size(); i++) {
                    fastItemAdapter.add(new AdapterBottomSheet(itemGalleryList.get(i)).withIdentifier(100 + i));
                }
            }
        });

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    bottomSheetDialog.show();
                    fastItemAdapter.notifyDataSetChanged();
                }
            }
        }, 100);

    }

    @Override
    public void OnChannelUpdateReactionStatusResponse(long roomId, final boolean status) {
        if (roomId == mRoomId) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    showVoteChannel = status;
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void OnChannelUpdateReactionStatusError() {

    }

    /**
     * *************************** Messaging ***************************
     */

    private void sendMessage(int requestCode, String filePath) {

        if (filePath == null || (filePath.length() == 0 && requestCode != AttachFile.request_code_contact_phone)) {
            clearReplyView();
            return;
        }

        if (isShowLayoutUnreadMessage) {
            removeLayoutUnreadMessage();
        }
        Realm realm = Realm.getDefaultInstance();
        long messageId = SUID.id().get();
        final long updateTime = TimeUtils.currentLocalTime();
        ProtoGlobal.RoomMessageType messageType = null;
        String fileName = null;
        long duration = 0;
        long fileSize = 0;
        int[] imageDimens = {0, 0};
        final long senderID = G.userId;

        /**
         * check if path is uri detect real path from uri
         */
        String path = getFilePathFromUri(Uri.parse(filePath));
        if (path != null) {
            filePath = path;
        }

        StructMessageInfo messageInfo = null;

        switch (requestCode) {
            case IntentRequests.REQ_CROP:

                if (!filePath.toLowerCase().endsWith(".gif")) {
                    if (isMessageWrote()) {
                        messageType = IMAGE_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.IMAGE;
                    }
                } else {
                    if (isMessageWrote()) {
                        messageType = GIF_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.GIF;
                    }
                }

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;
            case AttachFile.request_code_TAKE_PICTURE:

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                if (AndroidUtils.getImageDimens(filePath)[0] == 0 && AndroidUtils.getImageDimens(filePath)[1] == 0) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Picture Not Loaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }

                break;

            case AttachFile.requestOpenGalleryForImageMultipleSelect:
                if (!filePath.toLowerCase().endsWith(".gif")) {
                    if (isMessageWrote()) {
                        messageType = IMAGE_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.IMAGE;
                    }
                } else {
                    if (isMessageWrote()) {
                        messageType = GIF_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.GIF;
                    }
                }

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);

                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;

            case AttachFile.requestOpenGalleryForVideoMultipleSelect:
            case request_code_VIDEO_CAPTURED:
                fileName = new File(filePath).getName();
                /**
                 * if video not compressed use from mainPath
                 */
                boolean compress = false;
                if (compressedPath.get(filePath) != null) {
                    compress = compressedPath.get(filePath);
                }
                if (compress) {
                    fileSize = new File(filePath).length();
                    duration = AndroidUtils.getAudioDuration(G.fragmentActivity, filePath) / 1000;
                } else {
                    fileSize = new File(mainVideoPath).length();
                    duration = AndroidUtils.getAudioDuration(G.fragmentActivity, mainVideoPath) / 1000;
                }

                if (isMessageWrote()) {
                    messageType = VIDEO_TEXT;
                } else {
                    messageType = VIDEO;
                }
                File videoFile = new File(filePath);
                String videoFileMime = FileUtils.getMimeType(videoFile);
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, videoFileMime, filePath, null, filePath, null, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, videoFileMime, filePath, null, filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_pic_audi:
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                duration = AndroidUtils.getAudioDuration(G.fragmentActivity, filePath) / 1000;
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO;
                }
                String songArtist = AndroidUtils.getAudioArtistName(filePath);
                long songDuration = AndroidUtils.getAudioDuration(G.fragmentActivity, filePath);

                messageInfo = StructMessageInfo.buildForAudio(getRealmChat(), mRoomId, messageId, senderID, ProtoGlobal.RoomMessageStatus.SENDING, messageType, MyType.SendType.send, updateTime, getWrittenMessage(), null, filePath, songArtist, songDuration, isReply() ? parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID) : -1);
                break;
            case AttachFile.request_code_pic_file:
            case AttachFile.request_code_open_document:

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.FILE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.FILE;
                }
                File fileFile = new File(filePath);
                String fileFileMime = FileUtils.getMimeType(fileFile);
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, fileFileMime, filePath, null, filePath, null, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, fileFileMime, filePath, null, filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_contact_phone:
                if (latestUri == null) {
                    break;
                }
                ContactUtils contactUtils = new ContactUtils(G.fragmentActivity, latestUri);
                String name = contactUtils.retrieveName();
                String number = contactUtils.retrieveNumber();
                messageType = CONTACT;
                messageInfo = StructMessageInfo.buildForContact(getRealmChat(), mRoomId, messageId, senderID, MyType.SendType.send, updateTime, ProtoGlobal.RoomMessageStatus.SENDING, name, "", number, isReply() ? parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID) : -1);
                break;
            case AttachFile.request_code_paint:
                fileName = new File(filePath).getName();

                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;
        }

        final ProtoGlobal.RoomMessageType finalMessageType = messageType;
        final String finalFilePath = filePath;
        final String finalFileName = fileName;
        final long finalDuration = duration;
        final long finalFileSize = fileSize;
        final int[] finalImageDimens = imageDimens;

        final StructMessageInfo finalMessageInfo = messageInfo;
        final long finalMessageId = messageId;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, finalMessageId);

                roomMessage.setMessageType(finalMessageType);
                roomMessage.setMessage(getWrittenMessage());

                RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
                RealmRoomMessage.isEmojiInText(roomMessage, getWrittenMessage());

                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setRoomId(mRoomId);
                roomMessage.setAttachment(finalMessageId, finalFilePath, finalImageDimens[0], finalImageDimens[1], finalFileSize, finalFileName, finalDuration, LocalFileType.FILE);
                roomMessage.setUserId(senderID);
                roomMessage.setAuthorHash(G.authorHash);
                roomMessage.setShowMessage(true);
                roomMessage.setCreateTime(updateTime);
                if (isReply()) {
                    if (finalMessageInfo != null && finalMessageInfo.replayTo != null) {
                        roomMessage.setReplyTo(finalMessageInfo.replayTo);
                    }
                }

                /**
                 * make channel extra if room is channel
                 */
                if (chatType == CHANNEL) {
                    StructChannelExtra structChannelExtra = StructChannelExtra.makeDefaultStructure(finalMessageId, mRoomId);
                    finalMessageInfo.channelExtra = structChannelExtra;
                    RealmChannelExtra.convert(realm, structChannelExtra);
                    //roomMessage.setChannelExtra(RealmChannelExtra.convert(realm, structChannelExtra));
                }

                if (finalMessageType == CONTACT) {
                    roomMessage.setRoomMessageContact(RealmRoomMessageContact.put(realm, finalMessageInfo));
                }

                if (finalMessageType != CONTACT) {
                    finalMessageInfo.attachment = StructMessageAttachment.convert(roomMessage.getAttachment());
                }

                String makeThumbnailFilePath = "";
                if (finalMessageType == VIDEO || finalMessageType == VIDEO_TEXT) {
                    //if (compressedPath.get(finalFilePath)) {//(sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 0) ||
                    boolean compress = false;
                    if (compressedPath.get(finalFilePath) != null) {
                        compress = compressedPath.get(finalFilePath);
                    }
                    if (compress) {
                        makeThumbnailFilePath = finalFilePath;
                    } else {
                        makeThumbnailFilePath = mainVideoPath;
                    }
                }

                if (finalMessageType == VIDEO || finalMessageType == VIDEO_TEXT) {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(makeThumbnailFilePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    if (bitmap != null) {
                        String path = AndroidUtils.saveBitmap(bitmap);
                        roomMessage.getAttachment().setLocalThumbnailPath(path);
                        roomMessage.getAttachment().setWidth(bitmap.getWidth());
                        roomMessage.getAttachment().setHeight(bitmap.getHeight());

                        finalMessageInfo.attachment.setLocalFilePath(roomMessage.getMessageId(), finalFilePath);
                        finalMessageInfo.attachment.width = bitmap.getWidth();
                        finalMessageInfo.attachment.height = bitmap.getHeight();
                    }

                    //if (compressedPath.get(finalFilePath)) {//(sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 0) ||
                    boolean compress = false;
                    if (compressedPath.get(finalFilePath) != null) {
                        compress = compressedPath.get(finalFilePath);
                    }
                    if (compress) {
                        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, finalFilePath, finalMessageId, finalMessageType, getWrittenMessage(), StructMessageInfo.getReplyMessageId(finalMessageInfo), new HelperUploadFile.UpdateListener() {
                            @Override
                            public void OnProgress(int progress, FileUploadStructure struct) {
                                {
                                    insertItemAndUpdateAfterStartUpload(progress, struct);
                                }
                            }

                            @Override
                            public void OnError() {

                            }
                        });
                    } else {
                        compressingFiles.put(finalMessageId, null);
                        StructUploadVideo uploadVideo = new StructUploadVideo();
                        uploadVideo.filePath = finalFilePath;
                        uploadVideo.roomId = mRoomId;
                        uploadVideo.messageId = finalMessageId;
                        uploadVideo.messageType = finalMessageType;
                        uploadVideo.message = getWrittenMessage();
                        if (isReply()) {
                            uploadVideo.replyMessageId = parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID);
                        } else {
                            uploadVideo.replyMessageId = 0;
                        }
                        structUploadVideos.add(uploadVideo);

                        finalMessageInfo.attachment.compressing = G.fragmentActivity.getResources().getString(R.string.compressing);
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                switchAddItem(new ArrayList<>(Collections.singletonList(finalMessageInfo)), false);
                            }
                        });
                    }
                }

                RealmRoom.setLastMessageWithRoomMessage(realm, roomMessage.getRoomId(), roomMessage);
            }
        });

        if (finalMessageType != VIDEO && finalMessageType != VIDEO_TEXT) {
            if (finalMessageType != CONTACT) {

                HelperUploadFile.startUploadTaskChat(mRoomId, chatType, finalFilePath, finalMessageId, finalMessageType, getWrittenMessage(), StructMessageInfo.getReplyMessageId(finalMessageInfo), new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        if (canUpdateAfterDownload) {
                            insertItemAndUpdateAfterStartUpload(progress, struct);
                        }
                    }

                    @Override
                    public void OnError() {

                    }
                });
            } else {
                ChatSendMessageUtil messageUtil = new ChatSendMessageUtil().newBuilder(chatType, finalMessageType, mRoomId).message(getWrittenMessage());
                messageUtil.contact(finalMessageInfo.userInfo.firstName, finalMessageInfo.userInfo.lastName, finalMessageInfo.userInfo.phone);
                if (isReply()) {
                    messageUtil.replyMessage(parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                }
                messageUtil.sendMessage(Long.toString(finalMessageId));
            }

            if (finalMessageType == CONTACT) {
                messageInfo.channelExtra = new StructChannelExtra();
                mAdapter.add(new ContactItem(getRealmChat(), chatType, this).setMessage(messageInfo));
            }
        }

        if (isReply()) {
            mReplayLayout.setTag(null);
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    mReplayLayout.setVisibility(View.GONE);
                }
            });
        }

        realm.close();
        scrollToEnd();
    }

    public void sendCancelAction() {

        HelperSetAction.sendCancel(messageId);
    }

    public void sendPosition(final Double latitude, final Double longitude, final String imagePath) {
        sendCancelAction();

        if (isShowLayoutUnreadMessage) {
            removeLayoutUnreadMessage();
        }
        final long messageId = SUID.id().get();
        RealmRoomMessage.makePositionMessage(mRoomId, messageId, replyMessageId(), latitude, longitude, imagePath);

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RealmRoomMessage roomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), roomMessage))), false);
                chatSendMessageUtil.build(chatType, mRoomId, roomMessage);
                scrollToEnd();
            }
        }, 300);

        clearReplyView();
    }

    /**
     * do forward actions if any message forward to this room
     */
    private void manageForwardedMessage() {
        if ((mForwardMessages != null && !isChatReadOnly) || multiForwardList.size() > 0) {
            final LinearLayout ll_Forward = (LinearLayout) rootView.findViewById(R.id.ac_ll_forward);
            int multiForwardSize = multiForwardList.size();
            if (hasForward || multiForwardSize > 0) {

                for (int i = 0; i < mForwardMessages.size(); i++) {
                    if (hasForward) {
                        sendForwardedMessage((StructMessageInfo) Parcels.unwrap(mForwardMessages.get(i)), mRoomId, true);
                    } else {
                        for (int k = 0; k < multiForwardSize; k++) {
                            sendForwardedMessage((StructMessageInfo) Parcels.unwrap(mForwardMessages.get(i)), multiForwardList.get(k), false);
                        }
                    }
                }

                if (hasForward) {
                    imvCancelForward.performClick();
                } else {
                    multiForwardList.clear();
                    mForwardMessages = null;
                }

            } else {
                imvCancelForward = (TextView) rootView.findViewById(R.id.cslhf_imv_cansel);
                imvCancelForward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ll_Forward.setVisibility(View.GONE);
                        hasForward = false;
                        mForwardMessages = null;

                        if (edtChat.getText().length() == 0) {

                            layoutAttachBottom.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    layoutAttachBottom.setVisibility(View.VISIBLE);
                                }
                            }).start();
                            imvSendButton.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    G.handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            imvSendButton.clearAnimation();
                                            imvSendButton.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });

                layoutAttachBottom.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layoutAttachBottom.setVisibility(View.GONE);
                    }
                }).start();

                imvSendButton.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imvSendButton.clearAnimation();
                                imvSendButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).start();

                int _count = mForwardMessages.size();
                String str = _count > 1 ? G.fragmentActivity.getResources().getString(R.string.messages_selected) : G.fragmentActivity.getResources().getString(R.string.message_selected);

                EmojiTextViewE emMessage = (EmojiTextViewE) rootView.findViewById(R.id.cslhf_txt_message);

                TextView txtForwardMessage = (TextView) rootView.findViewById(R.id.cslhf_txt_forward_from);
                txtForwardMessage.setTextColor(Color.parseColor(G.appBarColor));

                ImageView imvForwardIcon = (ImageView) rootView.findViewById(R.id.cslhs_imv_forward);
                imvForwardIcon.setColorFilter(Color.parseColor(G.appBarColor));

                if (HelperCalander.isPersianUnicode) {

                    emMessage.setText(convertToUnicodeFarsiNumber(_count + " " + str));
                } else {

                    emMessage.setText(_count + " " + str);
                }

                hasForward = true;
                ll_Forward.setVisibility(View.VISIBLE);
            }
        }
    }

    private void sendForwardedMessage(final StructMessageInfo messageInfo, final long mRoomId, final boolean isSingleForward) {

        final long messageId = SUID.id().get();

        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom == null || realmRoom.getReadOnly()) {
            return;
        }

        final ProtoGlobal.Room.Type type = realmRoom.getType();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //final Realm realm = Realm.getDefaultInstance();

                getRealmChat().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoomMessage.makeForwardMessage(realm, mRoomId, messageId, parseLong(messageInfo.messageID));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        RealmRoomMessage forwardedMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();

                        if (forwardedMessage != null && forwardedMessage.isValid() && !forwardedMessage.isDeleted()) {
                            if (isSingleForward) {
                                switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), forwardedMessage))), false);
                                scrollToEnd();
                            }
                            RealmRoomMessage roomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(messageInfo.messageID)).findFirst();
                            if (roomMessage != null) {
                                chatSendMessageUtil.buildForward(type, forwardedMessage.getRoomId(), forwardedMessage, roomMessage.getRoomId(), roomMessage.getMessageId());
                            }
                        }

                        //realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        //realm.close();
                    }
                });
            }
        });
    }

    private StructMessageInfo makeLayoutTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String timeString = TimeUtils.getChatSettingsTimeAgo(G.fragmentActivity, calendar.getTime());

        RealmRoomMessage timeMessage = RealmRoomMessage.makeTimeMessage(time, timeString);

        return StructMessageInfo.convert(getRealmChat(), timeMessage);
    }

    private void switchAddItem(ArrayList<StructMessageInfo> messageInfos, boolean addTop) {
        if (prgWaiting != null && messageInfos.size() > 0) {
            prgWaiting.setVisibility(View.GONE);
        }
        long identifier = SUID.id().get();
        for (StructMessageInfo messageInfo : messageInfos) {

            ProtoGlobal.RoomMessageType messageType;
            if (messageInfo.forwardedFrom != null) {
                if (messageInfo.forwardedFrom.isValid()) {
                    messageType = messageInfo.forwardedFrom.getMessageType();
                } else {
                    return;
                }
            } else {
                messageType = messageInfo.messageType;
            }

            if (!messageInfo.isTimeOrLogMessage() || (messageType == LOG)) {
                int index = 0;
                if (addTop) {
                    if (messageInfo.showTime) {
                        for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
                            if (mAdapter.getAdapterItem(i) instanceof TimeItem) {
                                if (!RealmRoomMessage.isTimeDayDifferent(messageInfo.time, mAdapter.getAdapterItem(i).mMessage.time)) {
                                    mAdapter.remove(i);
                                }
                                break;
                            }
                        }
                        mAdapter.add(0, new TimeItem(getRealmChat(), this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));
                        index = 1;
                    }
                } else {

                    /**
                     * don't allow for add lower messageId to bottom of list
                     */
                    if (parseLong(messageInfo.messageID) > biggestMessageId) {
                        if (!messageInfo.status.equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            biggestMessageId = parseLong(messageInfo.messageID);
                        }
                    } else {
                        continue;
                    }

                    if (messageInfo.showTime) {
                        if (mAdapter.getItemCount() > 0) {
                            if (mAdapter.getAdapterItem(mAdapter.getItemCount() - 1).mMessage != null && RealmRoomMessage.isTimeDayDifferent(messageInfo.time, mAdapter.getAdapterItem(mAdapter.getItemCount() - 1).mMessage.time)) {
                                mAdapter.add(new TimeItem(getRealmChat(), this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));
                            }
                        } else {
                            mAdapter.add(new TimeItem(getRealmChat(), this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));
                        }
                    }
                }

                switch (messageType) {
                    case TEXT:
                        if (!addTop) {
                            mAdapter.add(new TextItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new TextItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case IMAGE:
                        if (!addTop) {
                            mAdapter.add(new ImageItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ImageItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case IMAGE_TEXT:
                        if (!addTop) {
                            mAdapter.add(new ImageWithTextItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ImageWithTextItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VIDEO:
                        if (!addTop) {
                            mAdapter.add(new VideoItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VideoItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VIDEO_TEXT:
                        if (!addTop) {
                            mAdapter.add(new VideoWithTextItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VideoWithTextItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case LOCATION:
                        if (!addTop) {
                            mAdapter.add(new LocationItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new LocationItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case FILE:
                    case FILE_TEXT:
                        if (!addTop) {
                            mAdapter.add(new FileItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new FileItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VOICE:
                        if (!addTop) {
                            mAdapter.add(new VoiceItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VoiceItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case AUDIO:
                    case AUDIO_TEXT:
                        if (!addTop) {
                            mAdapter.add(new AudioItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new AudioItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case CONTACT:
                        if (!addTop) {
                            mAdapter.add(new ContactItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ContactItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case GIF:
                        if (!addTop) {
                            mAdapter.add(new GifItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new GifItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case GIF_TEXT:
                        if (!addTop) {
                            mAdapter.add(new GifWithTextItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new GifWithTextItem(getRealmChat(), chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case LOG:
                        if (messageInfo.showMessage) {
                            if (!addTop) {
                                mAdapter.add(new LogItem(getRealmChat(), this).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new LogItem(getRealmChat(), this).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                }
            }
            identifier++;
        }
    }

    private void getMessages() {
        //+Realm realm = Realm.getDefaultInstance();

        ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction;
        ArrayList<StructMessageInfo> messageInfos = new ArrayList<>();
        /**
         * get message in first enter to chat if has unread get message with down direction
         */
        RealmResults<RealmRoomMessage> results;
        RealmResults<RealmRoomMessage> resultsDown = null;
        RealmResults<RealmRoomMessage> resultsUp;
        long fetchMessageId = 0; // with this value realm will be queried for get message
        if (hasUnread() || hasSavedState()) {

            if (firstUnreadMessage == null || !firstUnreadMessage.isManaged() || !firstUnreadMessage.isValid() || firstUnreadMessage.isDeleted()) {
                firstUnreadMessage = getFirstUnreadMessage(getRealmChat());
            }

            /**
             * show unread layout and also set firstUnreadMessageId in startFutureMessageIdUp
             * for try load top message and also topMore default value is true for this target
             */
            if (hasSavedState()) {
                fetchMessageId = getSavedState();

                if (hasUnread()) {
                    if (firstUnreadMessage == null) {
                        resetMessagingValue();
                        getMessages();
                        return;
                    }
                    countNewMessage = unreadCount;
                    txtNewUnreadMessage.setVisibility(View.VISIBLE);
                    txtNewUnreadMessage.setText(countNewMessage + "");
                    llScrollNavigate.setVisibility(View.VISIBLE);
                    firstUnreadMessageInChat = firstUnreadMessage;
                }
            } else {
                if (firstUnreadMessage == null) {
                    resetMessagingValue();
                    getMessages();
                    return;
                }
                unreadLayoutMessage();
                fetchMessageId = firstUnreadMessage.getMessageId();
            }
            startFutureMessageIdUp = fetchMessageId;

            // we have firstUnreadMessage but for gapDetection method we need RealmResult so get this message with query; if we change gap detection method will be can use from firstUnreadMessage
            resultsDown = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).equalTo(RealmRoomMessageFields.MESSAGE_ID, fetchMessageId).findAll();

            addToView = false;
            direction = DOWN;
        } else {
            addToView = true;
            direction = UP;
        }

        resultsUp = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);

        long gapMessageId;
        if (direction == DOWN) {
            resultsUp =
                    getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, fetchMessageId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).findAllSorted(RealmRoomMessageFields.CREATE_TIME, Sort.DESCENDING);
            /**
             * if for UP state client have message detect gap otherwise try for get online message
             * because maybe client have message but not exist in Realm yet
             */
            if (resultsUp.size() > 1) {
                gapDetection(resultsUp, UP);
            } else {
                getOnlineMessage(fetchMessageId, UP);
            }

            results = resultsDown;
            gapMessageId = gapDetection(results, direction);
        } else {
            results = resultsUp;
            gapMessageId = gapDetection(resultsUp, UP);
        }

        if (results.size() > 0) {

            Object[] object = getLocalMessage(getRealmChat(), mRoomId, results.first().getMessageId(), gapMessageId, true, direction);
            messageInfos = (ArrayList<StructMessageInfo>) object[0];
            if (messageInfos.size() > 0) {
                if (direction == UP) {
                    topMore = (boolean) object[1];
                    startFutureMessageIdUp = parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
                } else {
                    bottomMore = (boolean) object[1];
                    startFutureMessageIdDown = parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
                }
            } else {
                if (direction == UP) {
                    startFutureMessageIdUp = 0;
                } else {
                    startFutureMessageIdDown = 0;
                }
            }

            /**
             * if gap is exist ,check that reached to gap or not and if
             * reached send request to server for clientGetRoomHistory
             */
            if (gapMessageId > 0) {
                boolean hasSpaceToGap = (boolean) object[2];
                if (!hasSpaceToGap) {

                    long oldMessageId = 0;
                    if (messageInfos.size() > 0) {
                        /**
                         * this code is correct for UP or DOWN load message result
                         */
                        oldMessageId = parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
                    }
                    /**
                     * send request to server for clientGetRoomHistory
                     */
                    getOnlineMessage(oldMessageId, direction);
                }
            } else {
                /**
                 * if gap not exist and also not exist more message in local
                 * send request for get message from server
                 */
                if ((direction == UP && !topMore) || (direction == DOWN && !bottomMore)) {
                    if (messageInfos.size() > 0) {
                        getOnlineMessage(parseLong(messageInfos.get(messageInfos.size() - 1).messageID), direction);
                    } else {
                        getOnlineMessage(0, direction);
                    }
                }
            }
        } else {
            /** send request to server for get message.
             * if direction is DOWN check again realmRoomMessage for detection
             * that exist any message without checking deleted state and if
             * exist use from that messageId instead of zero for getOnlineMessage
             */
            long oldMessageId = 0;
            if (direction == DOWN) {
                RealmRoomMessage realmRoomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).equalTo(RealmRoomMessageFields.MESSAGE_ID, fetchMessageId).findFirst();
                if (realmRoomMessage != null) {
                    oldMessageId = realmRoomMessage.getMessageId();
                }
            }

            getOnlineMessage(oldMessageId, direction);
        }

        if (direction == UP) {
            switchAddItem(messageInfos, true);
        } else {
            switchAddItem(messageInfos, false);
            if (hasSavedState()) {
                int position = mAdapter.findPositionByMessageId(savedScrollMessageId);
                LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                linearLayout.scrollToPositionWithOffset(position, 0);
                savedScrollMessageId = 0;
            }
        }

        /**
         * make scrollListener for detect change in scroll and load more in chat
         */
        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = (recyclerView.getLayoutManager()).getChildCount();
                totalItemCount = (recyclerView.getLayoutManager()).getItemCount();
                firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (firstVisiblePosition < scrollEnd) {
                    /**
                     * scroll to top
                     */
                    loadMessage(UP);
                } else if (firstVisiblePosition + visibleItemCount >= (totalItemCount - scrollEnd)) {
                    /**
                     * scroll to bottom
                     */
                    loadMessage(DOWN);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        //realm.close();
    }

    /**
     * manage load message from local or from server(online)
     */
    private void loadMessage(final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        long gapMessageId;
        long startFutureMessageId;
        if (direction == UP) {
            gapMessageId = gapMessageIdUp;
            startFutureMessageId = startFutureMessageIdUp;
        } else {
            gapMessageId = gapMessageIdDown;
            startFutureMessageId = startFutureMessageIdDown;
        }
        if ((direction == UP && topMore) || (direction == DOWN && bottomMore)) {
            Object[] object = getLocalMessage(getRealmChat(), mRoomId, startFutureMessageId, gapMessageId, false, direction);
            if (direction == UP) {
                topMore = (boolean) object[1];
            } else {
                bottomMore = (boolean) object[1];
            }
            final ArrayList<StructMessageInfo> structMessageInfos = (ArrayList<StructMessageInfo>) object[0];
            if (structMessageInfos.size() > 0) {
                if (direction == UP) {
                    startFutureMessageIdUp = parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                } else {
                    startFutureMessageIdDown = parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                }
            } else {
                /**
                 * don't set zero. when user come to room for first time with -@roomId-
                 * for example : @public ,this block will be called and set zero this value and finally
                 * don't allow to user for get top history, also that sounds this block isn't helpful
                 */
                //if (direction == UP) {
                //    startFutureMessageIdUp = 0;
                //} else {
                //    startFutureMessageIdDown = 0;
                //}
            }

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (direction == UP) {
                        switchAddItem(structMessageInfos, true);
                    } else {
                        switchAddItem(structMessageInfos, false);
                    }
                }
            });

            /**
             * if gap is exist ,check that reached to gap or not and if
             * reached send request to server for clientGetRoomHistory
             */
            if (gapMessageId > 0) {
                boolean hasSpaceToGap = (boolean) object[2];
                if (!hasSpaceToGap) {
                    /**
                     * send request to server for clientGetRoomHistory
                     */
                    long oldMessageId;
                    if (structMessageInfos.size() > 0) {
                        oldMessageId = parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                    } else {
                        oldMessageId = gapMessageId;
                    }

                    getOnlineMessage(oldMessageId, direction);
                }
            }
        } else if (gapMessageId > 0) {
            /**
             * detect old messageId that should get history from server with that
             * (( hint : in scroll state never should get online message with messageId = 0
             * in some cases maybe startFutureMessageIdUp Equal to zero , so i used from this if.))
             */
            if (startFutureMessageId != 0) {
                getOnlineMessage(startFutureMessageId, direction);
            }
        } else {

            if (((direction == UP && allowGetHistoryUp) || (direction == DOWN && allowGetHistoryDown)) && startFutureMessageId != 0) {
                getOnlineMessage(startFutureMessageId, direction);
            }
        }
    }

    /**
     * get message history from server
     *
     * @param oldMessageId if set oldMessageId=0 messages will be get from latest message that exist in server
     */
    private void getOnlineMessage(final long oldMessageId, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        if ((direction == UP && !isWaitingForHistoryUp && allowGetHistoryUp) || (direction == DOWN && !isWaitingForHistoryDown && allowGetHistoryDown)) {

            long reachMessageId;
            if (direction == UP) {
                reachMessageId = reachMessageIdUp;
                isWaitingForHistoryUp = true;
            } else {
                reachMessageId = reachMessageIdDown;
                isWaitingForHistoryDown = true;
            }

            /**
             * show progress when start for get history from server
             */
            progressItem(SHOW, direction);

            int limit = Config.LIMIT_GET_HISTORY_NORMAL;
            if ((firstUp && direction == UP) || (firstDown && direction == DOWN)) {
                limit = Config.LIMIT_GET_HISTORY_LOW;
            }

            MessageLoader.getOnlineMessage(getRealmChat(), mRoomId, oldMessageId, reachMessageId, limit, direction, new OnMessageReceive() {
                @Override
                public void onMessage(final long roomId, long startMessageId, long endMessageId, boolean gapReached, boolean jumpOverLocal, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
                    if (roomId != mRoomId) {
                        return;
                    }
                    hideProgress();
                    /**
                     * hide progress received history
                     */
                    progressItem(HIDE, direction);

                    //Realm realm = Realm.getDefaultInstance();
                    RealmResults<RealmRoomMessage> realmRoomMessages;
                    Sort sort;
                    if (direction == UP) {
                        firstUp = false;
                        startFutureMessageIdUp = startMessageId;
                        sort = Sort.DESCENDING;
                        isWaitingForHistoryUp = false;
                    } else {
                        firstDown = false;
                        startFutureMessageIdDown = endMessageId;
                        sort = Sort.ASCENDING;
                        isWaitingForHistoryDown = false;
                    }
                    realmRoomMessages = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, startMessageId, endMessageId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, sort);
                    MessageLoader.sendMessageStatus(roomId, realmRoomMessages, chatType, ProtoGlobal.RoomMessageStatus.SEEN, getRealmChat());

                    //                    if (realmRoomMessages.size() == 0) { // Hint : link browsable ; Commented Now!!!
                    //                        getOnlineMessage(oldMessageId, direction);
                    //                        return;
                    //                    }

                    /**
                     * I do this for set addToView true
                     */
                    if (direction == DOWN && realmRoomMessages.size() < (Config.LIMIT_GET_HISTORY_NORMAL - 1)) {
                        getOnlineMessage(startFutureMessageIdDown, direction);
                    }

                    /**
                     * when reached to gap and not jumped over local, set gapMessageIdUp = 0; do this action
                     * means that gap not exist (need this value for future get message) set topMore/bottomMore
                     * local after that gap reached true for allow that get message from
                     */
                    if (gapReached && !jumpOverLocal) {
                        if (direction == UP) {
                            gapMessageIdUp = 0;
                            reachMessageIdUp = 0;
                            topMore = true;
                        } else {
                            gapMessageIdDown = 0;
                            reachMessageIdDown = 0;
                            bottomMore = true;
                        }

                        gapDetection(realmRoomMessages, direction);
                    } else if ((direction == UP && isReachedToTopView()) || direction == DOWN && isReachedToBottomView()) {
                        /**
                         * check this state because if user is near to top view and not scroll get top message from server
                         */
                        //getOnlineMessage(startFutureMessageId, directionEnum);
                    }

                    final ArrayList<StructMessageInfo> structMessageInfos = new ArrayList<>();
                    for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                        structMessageInfos.add(StructMessageInfo.convert(getRealmChat(), realmRoomMessage));
                    }

                    if (direction == UP) {
                        switchAddItem(structMessageInfos, true);
                    } else {
                        switchAddItem(structMessageInfos, false);
                    }

                    //realm.close();
                }

                @Override
                public void onError(int majorCode, int minorCode, long messageIdGetHistory, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
                    hideProgress();
                    /**
                     * hide progress if have any error
                     */
                    progressItem(HIDE, direction);

                    if (majorCode == 617) {

                        if (!isWaitingForHistoryUp && !isWaitingForHistoryDown && mAdapter.getItemCount() == 0) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }

                        if (direction == UP) {
                            isWaitingForHistoryUp = false;
                            isWaitingForHistoryUp = false;
                            allowGetHistoryUp = false;
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //TODO [Saeed Mozaffari] [2017-03-06 9:50 AM] - for avoid from 'Inconsistency detected. Invalid item position' error i set notifyDataSetChanged. Find Solution And Clear it!!!
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            addToView = true;
                            isWaitingForHistoryDown = false;
                            allowGetHistoryDown = false;
                        }
                    }

                    /**
                     * if time out came up try again for get history with previous value
                     */
                    if (majorCode == 5) {
                        if (direction == UP) {
                            //getOnlineMessage(messageIdGetHistory, UP);
                        } else {
                            //getOnlineMessage(messageIdGetHistory, DOWN);
                        }
                    }
                }
            });
        }
    }

    /**
     * detect gap exist in this room or not
     * (hint : if gapMessageId==0 means that gap not exist)
     * if gapMessageIdUp exist, not compute again
     */
    private long gapDetection(RealmResults<RealmRoomMessage> results, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        if (((direction == UP && gapMessageIdUp == 0) || (direction == DOWN && gapMessageIdDown == 0)) && results.size() > 0) {
            Object[] objects = MessageLoader.gapExist(getRealmChat(), mRoomId, results.first().getMessageId(), direction);
            if (direction == UP) {
                reachMessageIdUp = (long) objects[1];
                return gapMessageIdUp = (long) objects[0];
            } else {
                reachMessageIdDown = (long) objects[1];
                return gapMessageIdDown = (long) objects[0];
            }
        }
        return 0;
    }

    /**
     * return true if now view is near to top
     */
    private boolean isReachedToTopView() {
        return firstVisiblePosition <= 5;
    }

    /**
     * return true if now view is near to bottom
     */
    private boolean isReachedToBottomView() {
        return (firstVisiblePosition + visibleItemCount >= (totalItemCount - 5));
    }

    /**
     * make unread layout message
     */
    private void unreadLayoutMessage() {
        int unreadMessageCount = unreadCount;
        if (unreadMessageCount > 0) {
            RealmRoomMessage unreadMessage = RealmRoomMessage.makeUnreadMessage(unreadMessageCount);
            mAdapter.add(0, new UnreadMessage(getRealmChat(), FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), unreadMessage)).withIdentifier(SUID.id().get()));
            isShowLayoutUnreadMessage = true;
        }
    }

    /**
     * return first unread message for current room
     * (reason : use from this method for avoid from closed realm error)
     */
    private RealmRoomMessage getFirstUnreadMessage(Realm realm) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {
            return realmRoom.getFirstUnreadMessage();
        }
        return null;
    }

    /**
     * check that this room has unread or no
     */
    private boolean hasUnread() {
        return unreadCount > 0;
    }

    /**
     * check that this room has saved state or no
     */
    private boolean hasSavedState() {
        return savedScrollMessageId > 0;
    }

    /**
     * return saved scroll messageId
     */
    private long getSavedState() {
        return savedScrollMessageId;
    }

    /**
     * manage progress state in adapter
     *
     * @param progressState SHOW or HIDE state detect with enum
     * @param direction     define direction for show progress in UP or DOWN
     */
    private void progressItem(final ProgressState progressState, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                int progressIndex = 0;
                if (direction == DOWN) {
                    progressIndex = mAdapter.getAdapterItemCount() - 1;
                }
                if (progressState == SHOW) {
                    if ((mAdapter.getAdapterItemCount() > 0) && !(mAdapter.getAdapterItem(progressIndex) instanceof ProgressWaiting)) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (direction == DOWN) {
                                    progressIdentifierDown = SUID.id().get();
                                    mAdapter.add(new ProgressWaiting(getRealmChat(), FragmentChat.this).withIdentifier(progressIdentifierDown));
                                } else {
                                    progressIdentifierUp = SUID.id().get();
                                    mAdapter.add(0, new ProgressWaiting(getRealmChat(), FragmentChat.this).withIdentifier(progressIdentifierUp));
                                }
                            }
                        });
                    }
                } else {
                    /**
                     * i do this action with delay because sometimes instance wasn't successful
                     * for detect progress so client need delay for detect this instance
                     */
                    if ((mAdapter.getItemCount() > 0) && (mAdapter.getAdapterItem(progressIndex) instanceof ProgressWaiting)) {
                        mAdapter.remove(progressIndex);
                    } else {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * if not detected progress item for remove use from item identifier and remove progress item
                                 */
                                if (direction == DOWN && progressIdentifierDown != 0) {
                                    for (int i = (mAdapter.getItemCount() - 1); i >= 0; i--) {
                                        if (mAdapter.getItem(i).getIdentifier() == progressIdentifierDown) {
                                            mAdapter.remove(i);
                                            progressIdentifierDown = 0;
                                            break;
                                        }
                                    }
                                } else if (direction == UP && progressIdentifierUp != 0) {
                                    for (int i = 0; i < (mAdapter.getItemCount() - 1); i++) {
                                        if (mAdapter.getItem(i).getIdentifier() == progressIdentifierUp) {
                                            mAdapter.remove(i);
                                            progressIdentifierUp = 0;
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void progressItem(final ProgressState progressState, final String direction) {
        if (direction.equals(UP.toString())) {
            progressItem(progressState, UP);
        } else {
            progressItem(progressState, DOWN);
        }
    }

    /**
     * reset to default value for reload message again
     */
    private void resetMessagingValue() {
        clearAdapterItems();

        prgWaiting.setVisibility(View.VISIBLE);

        addToView = true;
        topMore = false;
        bottomMore = false;
        isWaitingForHistoryUp = false;
        isWaitingForHistoryDown = false;
        gapMessageIdUp = 0;
        gapMessageIdDown = 0;
        reachMessageIdUp = 0;
        reachMessageIdDown = 0;
        allowGetHistoryUp = true;
        allowGetHistoryDown = true;
        startFutureMessageIdUp = 0;
        startFutureMessageIdDown = 0;
        firstVisiblePosition = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        unreadCount = 0;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        G.fragmentActivity = (FragmentActivity) activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState); //No call for super(). Bug on API Level > 11.
    }

    public void finishChat() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                if (isAdded()) {
                    Fragment fragment = G.fragmentManager.findFragmentByTag(FragmentChat.class.getName());
                    removeFromBaseFragment(fragment);

                    if (G.iTowPanModDesinLayout != null) {
                        G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.hide);
                    }
                }
            }
        });
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

    private void error(String error) {
        if (isAdded()) {
            try {
                HelperError.showSnackMessage(error, false);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    /**
     * *** SearchHash ***
     */

    private class SearchHash {

        public String lastMessageId = "";
        private String hashString = "";
        private int currentHashPosition;

        private ArrayList<String> hashList = new ArrayList<>();

        void setHashString(String hashString) {
            this.hashString = hashString.toLowerCase();
        }

        public void setPosition(String messageId) {

            if (mAdapter == null) {
                return;
            }

            if (lastMessageId.length() > 0) {
                mAdapter.toggleSelection(lastMessageId, false, null);
            }

            currentHashPosition = 0;
            hashList.clear();

            for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
                if (mAdapter.getItem(i).mMessage != null) {

                    if (messageId.length() > 0) {
                        if (mAdapter.getItem(i).mMessage.messageID.equals(messageId)) {
                            currentHashPosition = hashList.size();
                            lastMessageId = messageId;
                            mAdapter.getItem(i).mMessage.isSelected = true;
                            mAdapter.notifyItemChanged(i);
                        }
                    }

                    String mText = mAdapter.getItem(i).mMessage.forwardedFrom != null ? mAdapter.getItem(i).mMessage.forwardedFrom.getMessage() : mAdapter.getItem(i).mMessage.messageText;

                    if (mText.toLowerCase().contains(hashString)) {
                        hashList.add(mAdapter.getItem(i).mMessage.messageID);
                    }
                }
            }

            if (messageId.length() == 0) {
                txtHashCounter.setText(hashList.size() + " / " + hashList.size());

                if (hashList.size() > 0) {
                    currentHashPosition = hashList.size() - 1;
                    goToSelectedPosition(hashList.get(currentHashPosition));
                }
            } else {
                txtHashCounter.setText((currentHashPosition + 1) + " / " + hashList.size());
            }
        }

        void downHash() {
            if (currentHashPosition < hashList.size() - 1) {

                currentHashPosition++;

                goToSelectedPosition(hashList.get(currentHashPosition));

                txtHashCounter.setText((currentHashPosition + 1) + " / " + hashList.size());
            }
        }

        void upHash() {
            if (currentHashPosition > 0) {

                currentHashPosition--;

                goToSelectedPosition(hashList.get(currentHashPosition));
                txtHashCounter.setText((currentHashPosition + 1) + " / " + hashList.size());
            }
        }

        private void goToSelectedPosition(String messageid) {

            mAdapter.toggleSelection(lastMessageId, false, null);

            lastMessageId = messageid;

            mAdapter.toggleSelection(lastMessageId, true, recyclerView);
        }
    }

    /**
     * *** VideoCompressor ***
     */

    class VideoCompressor extends AsyncTask<String, Void, StructCompress> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected StructCompress doInBackground(String... params) {
            if (params[0] == null) { // if data is null
                StructCompress structCompress = new StructCompress();
                structCompress.compress = false;
                return structCompress;
            }
            File file = new File(params[0]);
            long originalSize = file.length();

            StructCompress structCompress = new StructCompress();
            structCompress.path = params[1];
            structCompress.originalPath = params[0];
            long endTime = AndroidUtils.getAudioDuration(G.fragmentActivity, params[0]);
            try {
                structCompress.compress = MediaController.getInstance().convertVideo(params[0], params[1], endTime);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            structCompress.originalSize = originalSize;
            return structCompress;
        }

        @Override
        protected void onPostExecute(StructCompress structCompress) {
            super.onPostExecute(structCompress);
            if (structCompress.compress) {
                compressedPath.put(structCompress.path, true);
                for (StructUploadVideo structUploadVideo : structUploadVideos) {
                    if (structUploadVideo != null && structUploadVideo.filePath.equals(structCompress.path)) {
                        /**
                         * update new info after compress file with notify item
                         */

                        long fileSize = new File(structUploadVideo.filePath).length();
                        long duration = AndroidUtils.getAudioDuration(G.fragmentActivity, structUploadVideo.filePath) / 1000;

                        if (fileSize >= structCompress.originalSize) {
                            structUploadVideo.filePath = structCompress.originalPath;
                            mAdapter.updateVideoInfo(structUploadVideo.messageId, duration, structCompress.originalSize);
                        } else {
                            RealmAttachment.updateFileSize(structUploadVideo.messageId, fileSize);
                            mAdapter.updateVideoInfo(structUploadVideo.messageId, duration, fileSize);
                        }

                        HelperUploadFile.startUploadTaskChat(structUploadVideo.roomId, chatType, structUploadVideo.filePath, structUploadVideo.messageId, structUploadVideo.messageType, structUploadVideo.message, structUploadVideo.replyMessageId, new HelperUploadFile.UpdateListener() {
                            @Override
                            public void OnProgress(int progress, FileUploadStructure struct) {
                                if (canUpdateAfterDownload) {
                                    insertItemAndUpdateAfterStartUpload(progress, struct);
                                }
                            }

                            @Override
                            public void OnError() {

                            }
                        });
                    }
                }
            }
        }
    }

    public class AdapterCamera extends AbstractItem<AdapterCamera, AdapterCamera.ViewHolder> {

        public String item;

        //public String getItem() {
        //    return item;
        //}

        public AdapterCamera(String item) {
            this.item = item;
        }

        //public void setItem(String item) {
        //    this.item = item;
        //}

        //The unique ID for this type of item
        @Override
        public int getType() {
            return R.id.rootCamera;
        }

        //The layout to be used for this type of item
        @Override
        public int getLayoutRes() {
            return R.layout.adapter_camera;
        }

        //The logic to bind your data to the view

        @Override
        public void unbindView(ViewHolder holder) {
            super.unbindView(holder);
        }

        @Override
        public void bindView(ViewHolder holder, List payloads) {
            super.bindView(holder, payloads);
        }

        @Override
        public ViewHolder getViewHolder(View v) {
            return new ViewHolder(v);
        }

        //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
        protected class ViewHolder extends RecyclerView.ViewHolder {

            CameraView cm2;
            private TextView rootCamera;

            public ViewHolder(View view) {
                super(view);

                cm2 = (CameraView) view.findViewById(R.id.cameraView);
                rootCamera = (TextView) view.findViewById(R.id.txtIconCamera);
                rootCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FragmentChat.onClickCamera != null) {
                            FragmentChat.onClickCamera.onclickCamera();
                        }
                    }
                });
            }
        }
    }

    private void forwardToChatRoom(final ArrayList<StructBottomSheetForward> forwardList) {

        if (forwardList != null && forwardList.size() > 0) {

            final int[] count = {0};

            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final ProtoGlobal.Room room) {

                    if (!multiForwardList.contains(room.getId())) {
                        multiForwardList.add(room.getId());
                        RealmRoom.putOrUpdate(room);
                    }

                    count[0]++;
                    if (count[0] >= forwardList.size()) {
                        G.onChatGetRoom = null;
                        forwardList.clear();
                        manageForwardedMessage();

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bottomSheetDialogForward.dismiss();
                                hideProgress();
                            }
                        });


                    }
                }

                @Override
                public void onChatGetRoomTimeOut() {

                }

                @Override
                public void onChatGetRoomError(int majorCode, int minorCode) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetDialogForward.dismiss();
                            hideProgress();
                            error(G.fragmentActivity.getResources().getString(R.string.faild));
                        }
                    });


                }
            };

            for (int i = 0; i < forwardList.size(); i++) {
                new RequestChatGetRoom().chatGetRoom(forwardList.get(i).getId());
            }


        } else {
            manageForwardedMessage();
            bottomSheetDialogForward.dismiss();
            hideProgress();
        }

    }


}
