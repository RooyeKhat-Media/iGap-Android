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
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.lalongooo.videocompressor.video.MediaController;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.wang.avi.AVLoadingIndicatorView;
import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import me.leolin.shortcutbadger.ShortcutBadger;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterBottomSheet;
import net.iGap.adapter.MessagesAdapter;
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
import net.iGap.adapter.items.chat.VoiceItem;
import net.iGap.emoji.EmojiEditText;
import net.iGap.emoji.EmojiTextView;
import net.iGap.emoji.emoji.Emoji;
import net.iGap.emoji.listeners.OnEmojiBackspaceClickListener;
import net.iGap.emoji.listeners.OnEmojiClickedListener;
import net.iGap.emoji.listeners.OnEmojiPopupDismissListener;
import net.iGap.emoji.listeners.OnEmojiPopupShownListener;
import net.iGap.emoji.listeners.OnSoftKeyboardCloseListener;
import net.iGap.emoji.listeners.OnSoftKeyboardOpenListener;
import net.iGap.fragments.FragmentMap;
import net.iGap.fragments.FragmentShowImage;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperGetMessageState;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperNotificationAndBadge;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperTimeOut;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.IResendMessage;
import net.iGap.interfaces.OnActivityChatStart;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChannelAddMessageReaction;
import net.iGap.interfaces.OnChannelGetMessagesStats;
import net.iGap.interfaces.OnChatClearMessageResponse;
import net.iGap.interfaces.OnChatDeleteMessageResponse;
import net.iGap.interfaces.OnChatEditMessageResponse;
import net.iGap.interfaces.OnChatMessageRemove;
import net.iGap.interfaces.OnChatMessageSelectionChanged;
import net.iGap.interfaces.OnChatSendMessage;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.interfaces.OnClearChatHistory;
import net.iGap.interfaces.OnClientJoinByUsername;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnDeleteChatFinishActivity;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnHelperSetAction;
import net.iGap.interfaces.OnLastSeenUpdateTiming;
import net.iGap.interfaces.OnMessageReceive;
import net.iGap.interfaces.OnPathAdapterBottomSheet;
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
import net.iGap.module.ContactUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.FileUtils;
import net.iGap.module.IntentRequests;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MessageLoader;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyAppBarLayout;
import net.iGap.module.MyType;
import net.iGap.module.ResendMessage;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.TimeUtils;
import net.iGap.module.VoiceRecord;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.LocalFileType;
import net.iGap.module.enums.ProgressState;
import net.iGap.module.enums.SendingStep;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.module.structs.StructChannelExtra;
import net.iGap.module.structs.StructCompress;
import net.iGap.module.structs.StructMessageAttachment;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.module.structs.StructUploadVideo;
import net.iGap.proto.ProtoChannelGetMessagesStats;
import net.iGap.proto.ProtoClientGetRoomHistory;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAttachmentFields;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmClientConditionFields;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmOfflineDelete;
import net.iGap.realm.RealmOfflineDeleteFields;
import net.iGap.realm.RealmOfflineEdited;
import net.iGap.realm.RealmOfflineSeen;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomDraft;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageContact;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.realm.RealmRoomMessageLocation;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChannelDeleteMessage;
import net.iGap.request.RequestChannelEditMessage;
import net.iGap.request.RequestChannelUpdateDraft;
import net.iGap.request.RequestChatDelete;
import net.iGap.request.RequestChatDeleteMessage;
import net.iGap.request.RequestChatEditMessage;
import net.iGap.request.RequestChatUpdateDraft;
import net.iGap.request.RequestClientJoinByUsername;
import net.iGap.request.RequestClientSubscribeToRoom;
import net.iGap.request.RequestClientUnsubscribeFromRoom;
import net.iGap.request.RequestGroupDeleteMessage;
import net.iGap.request.RequestGroupEditMessage;
import net.iGap.request.RequestGroupUpdateDraft;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsUnblock;
import net.iGap.request.RequestUserInfo;
import org.parceler.Parcels;

import static java.lang.Long.parseLong;
import static net.iGap.G.chatSendMessageUtil;
import static net.iGap.G.context;
import static net.iGap.R.id.ac_ll_parent;
import static net.iGap.R.id.replyFrom;
import static net.iGap.R.string.member;
import static net.iGap.helper.HelperGetDataFromOtherApp.messageType;
import static net.iGap.module.AttachFile.getFilePathFromUri;
import static net.iGap.module.AttachFile.request_code_VIDEO_CAPTURED;
import static net.iGap.module.MessageLoader.getLocalMessage;
import static net.iGap.module.enums.ProgressState.HIDE;
import static net.iGap.module.enums.ProgressState.SHOW;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.DOWN;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.CONTACT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF_TEXT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_TEXT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.LOG;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_TEXT;

public class ActivityChat extends ActivityEnhanced implements IMessageItem, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse, OnChatMessageSelectionChanged<AbstractMessage>, OnChatMessageRemove, OnVoiceRecord, OnUserInfoResponse, OnSetAction, OnUserUpdateStatus, OnLastSeenUpdateTiming, OnGroupAvatarResponse, OnChannelAddMessageReaction, OnChannelGetMessagesStats {

    public static ActivityChat activityChat;
    public MusicPlayer musicPlayer;
    private AttachFile attachFile;
    private EditText edtSearchMessage;
    private SharedPreferences sharedPreferences;
    private net.iGap.emoji.EmojiEditText edtChat;
    private MaterialDesignTextView imvSendButton;
    private MaterialDesignTextView imvAttachFileButton;
    private MaterialDesignTextView imvMicButton;
    private MaterialDesignTextView btnReplaySelected;
    private ArrayList<String> listPathString;
    private MaterialDesignTextView btnCancelSendingFile;
    private ViewGroup viewGroupLastSeen;
    private ImageView imvUserPicture;
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
    private net.iGap.emoji.EmojiPopup emojiPopup;
    public static OnComplete onMusicListener;
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
    private LinearLayout ll_navigate_Message;
    private LinearLayout ll_navigateHash;
    private LinearLayout lyt_user;
    private LinearLayout mReplayLayout;
    private ProgressBar prgWaiting;
    private AVLoadingIndicatorView avi;
    private ViewGroup vgSpamUser;
    private RecyclerView.OnScrollListener scrollListener;
    private RecyclerView rcvBottomSheet;
    private FrameLayout llScrollNavigate;
    private FastItemAdapter fastItemAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private static List<StructBottomSheet> contacts;
    public static OnPathAdapterBottomSheet onPathAdapterBottomSheet;
    private View viewBottomSheet;


    private RealmRoomMessage voiceLastMessage = null;
    public static OnComplete hashListener;
    public static OnComplete onComplete;
    public static OnUpdateUserOrRoomInfo onUpdateUserOrRoomInfo;
    private static ArrayMap<String, Boolean> compressedPath = new ArrayMap<>(); // keep compressedPath and also keep video path that never be won't compressed
    public static ArrayMap<Long, HelperUploadFile.StructUpload> compressingFiles = new ArrayMap<>();
    private ArrayList<StructBottomSheet> itemGalleryList = new ArrayList<>();
    private static ArrayList<StructUploadVideo> structUploadVideos = new ArrayList<>();
    private RealmRoomMessage firstUnreadMessage;
    private RealmRoomMessage firstUnreadMessageInChat; // when user is in this room received new message

    private class StructBackGroundSeen {
        private Long messageID;
        ProtoGlobal.Room.Type roomType;
    }

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
    private TextView btnUpMessage;
    private TextView btnDownMessage;
    private TextView txtMessageCounter;
    private TextView btnUpHash;
    private TextView btnDownHash;
    private TextView txtHashCounter;
    private TextView txtFileNameForSend;
    private TextView txtNumberOfSelected;
    private TextView txtName;
    private TextView txtLastSeen;
    private TextView txtEmptyMessages;

    public String title;
    public String phoneNumber;
    private String userName = "";
    private String latestFilePath;
    private String mainVideoPath = "";
    private String color;
    private String initialize;
    private String groupParticipantsCountLabel;
    private String channelParticipantsCountLabel;
    private String userStatus;
    public static String titleStatic;

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


    private long replyToMessageId = 0;
    private long userId;
    private long lastSeen;
    private long chatPeerId;
    private long userTime;
    private long messageId;
    private long savedScrollMessageId;
    private long latestButtonClickTime; // use from this field for avoid from show button again after click it
    public long mRoomId = 0;
    public static long mRoomIdStatic = 0;

    private int countNewMessage = 0;
    private int lastPosition = 0;
    private int unreadCount = 0;
    private int latestRequestCode;
    private int messageCounter = 0;
    private int selectedPosition = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        startPageFastInitialize();
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initMain();
            }
        }, Config.FAST_START_PAGE_TIME);
    }

    @Override
    protected void onStart() {
        super.onStart();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RealmRoomMessage.fetchMessages(mRoomId, new OnActivityChatStart() {
                    @Override
                    public void resendMessage(RealmRoomMessage message) {
                        G.chatSendMessageUtil.build(chatType, message.getRoomId(), message);
                    }

                    @Override
                    public void resendMessageNeedsUpload(RealmRoomMessage message) {

                        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, message.getAttachment().getLocalFilePath(), message.getMessageId(), message.getMessageType(), message.getMessage(), RealmRoomMessage.getReplyMessageId(message), new HelperUploadFile.UpdateListener() {
                            @Override
                            public void OnProgress(int progress, FileUploadStructure struct) {
                                insertItemAndUpdateAfterStartUpload(progress, struct);
                            }

                            @Override
                            public void OnError() {

                            }
                        });
                    }

                    @Override
                    public void sendSeenStatus(RealmRoomMessage message) {
                        if (!isNotJoin) {
                            G.chatUpdateStatusUtil.sendUpdateStatus(chatType, mRoomId, message.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                        }
                    }
                });

                // update badge count after open one chat room
                Realm realm = Realm.getDefaultInstance();
                try {
                    int unreadCount = 0;

                    RealmResults<RealmRoom> realmRooms = realm.where(RealmRoom.class).notEqualTo(RealmRoomFields.ID, mRoomId).findAll();
                    for (RealmRoom realmRoom1 : realmRooms) {
                        if (realmRoom1.getUnreadCount() > 0) {
                            unreadCount += realmRoom1.getUnreadCount();
                        }
                    }

                    ShortcutBadger.applyCount(context, unreadCount);
                } catch (Exception e) {

                    e.printStackTrace();
                }

                realm.close();
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initLayoutHashNavigationCallback();
                showSpamBar();

                musicPlayer = new MusicPlayer(mediaLayout);
                if (MusicPlayer.mp != null) {
                    MusicPlayer.initLayoutTripMusic(mediaLayout);
                }

                /**
                 * update view for played music
                 */
                mAdapter.updateChengedItem(MusicPlayer.playedList);
                MusicPlayer.playedList.clear();

                if (isGoingFromUserLink) {
                    new RequestClientSubscribeToRoom().clientSubscribeToRoom(mRoomId);
                }

                final Realm updateUnreadCountRealm = Realm.getDefaultInstance();
                updateUnreadCountRealm.executeTransactionAsync(new Realm.Transaction() {//ASYNC
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            realm.copyToRealmOrUpdate(room);

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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            txtLastSeen.setText(finalMembers + " " + getResources().getString(member));
                                            avi.setVisibility(View.GONE);

                                            if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                                        }
                                    });
                                }
                            } else {
                                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, room.getChatRoom().getPeerId()).findFirst();
                                if (realmRegisteredInfo != null) {
                                    setUserStatus(realmRegisteredInfo.getStatus(), realmRegisteredInfo.getLastSeen());
                                }
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        updateUnreadCountRealm.close();
                    }
                });
            }
        }, Config.LOW_START_PAGE_TIME);

        mRoomIdStatic = mRoomId;
        titleStatic = title;

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.onUserInfoResponse = this;
        G.onChannelAddMessageReaction = this;
        G.onChannelGetMessagesStats = this;
        activityChat = this;
        G.onSetAction = this;
        G.onUserUpdateStatus = this;
        G.onLastSeenUpdateTiming = this;
        G.helperNotificationAndBadge.cancelNotification();

        initCallbacks();
        HelperNotificationAndBadge.isChatRoomNow = true;

        onUpdateUserOrRoomInfo = new OnUpdateUserOrRoomInfo() {
            @Override
            public void onUpdateUserOrRoomInfo(final String messageId) {

                if (messageId != null && messageId.length() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
                                if (mAdapter.getItem(i).mMessage != null && mAdapter.getItem(i).mMessage.messageID.equals(messageId)) {
                                    mAdapter.notifyItemChanged(i);
                                    break;
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGoingFromUserLink && isNotJoin) {
            new RequestClientUnsubscribeFromRoom().clientUnsubscribeFromRoom(mRoomId);
        }
        onMusicListener = null;
        storingLastPosition();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        setDraft();
        HelperNotificationAndBadge.isChatRoomNow = false;

        if (isNotJoin) {

            /**
             * delete all  deleted row from database
             */
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, true).findAll().deleteAllFromRealm();
                    realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).findAll().deleteAllFromRealm();
                }
            });
            realm.close();
        }
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        // room id have to be set to default, otherwise I'm in the room always!
        mRoomId = -1;
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
        if (voiceRecord != null) {
            voiceRecord.dispatchTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {

        FragmentShowImage fragment = (FragmentShowImage) getSupportFragmentManager().findFragmentByTag("ShowImageMessage");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            // for update view that image download in fragment show image
            int count = FragmentShowImage.downloadedList.size();

            for (int i = 0; i < count; i++) {
                String _cashId = FragmentShowImage.downloadedList.get(i) + "";

                for (int j = mAdapter.getAdapterItemCount() - 1; j >= 0; j--) {
                    try {

                        String mCashID = mAdapter.getItem(j).mMessage.forwardedFrom != null ? mAdapter.getItem(j).mMessage.forwardedFrom.getAttachment().getCacheId() : mAdapter.getItem(j).mMessage.attachment.cashID;

                        if (mCashID.equals(_cashId)) {
                            mAdapter.notifyItemChanged(j);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
            FragmentShowImage.downloadedList.clear();
        } else if (mAdapter != null && mAdapter.getSelections().size() > 0) {
            mAdapter.deselect();
        } else if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            if (ActivityPopUpNotification.isGoingToChatFromPopUp) {
                ActivityPopUpNotification.isGoingToChatFromPopUp = false;
                Intent intent = new Intent(context, ActivityMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            super.onBackPressed();
            finish();

        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            HelperSetAction.sendCancel(messageId);

            if (prgWaiting != null) {
                prgWaiting.setVisibility(View.GONE);
            }
        }

        if (requestCode == AttachFile.request_code_position && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                attachFile.requestGetPosition(complete);
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
            } else if (AttachFile.request_code_VIDEO_CAPTURED == requestCode) {

                listPathString = new ArrayList<>();
                listPathString.add(AttachFile.videoPath);

                latestUri = null; // check
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (data.getClipData() != null) { // multi select file
                        listPathString = attachFile.getClipData(data.getClipData());

                        if (listPathString != null) {
                            for (int i = 0; i < listPathString.size(); i++) {
                                listPathString.set(i, getFilePathFromUri(Uri.fromFile(new File(listPathString.get(i)))));
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
                if (requestCode == AttachFile.request_code_VIDEO_CAPTURED) {
                    if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1) {
                        Intent intent = new Intent(ActivityChat.this, ActivityTrimVideo.class);
                        intent.putExtra("PATH", listPathString.get(0));
                        startActivityForResult(intent, AttachFile.request_code_trim_video);
                        return;
                    } else if (sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1) {

                        File mediaStorageDir = new File(G.DIR_VIDEOS);
                        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "video_" + HelperString.getRandomFileName(3) + ".mp4");
                        listPathString = new ArrayList<>();

                        Uri uri = data.getData();
                        File tempFile = com.lalongooo.videocompressor.file.FileUtils.saveTempFile(HelperString.getRandomFileName(5), this, uri);
                        mainVideoPath = tempFile.getPath();
                        String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

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
                    latestRequestCode = AttachFile.request_code_VIDEO_CAPTURED;
                    showDraftLayout();
                    setDraftMessage(AttachFile.request_code_VIDEO_CAPTURED);
                    if ((sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1)) {
                        File mediaStorageDir = new File(G.DIR_VIDEOS);
                        listPathString = new ArrayList<>();

                        String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

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
                            Intent intent = new Intent(ActivityChat.this, ActivityTrimVideo.class);
                            intent.putExtra("PATH", listPathString.get(0));
                            startActivityForResult(intent, AttachFile.request_code_trim_video);
                            return;
                        } else if ((sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1)) {

                            mainVideoPath = listPathString.get(0);

                            String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

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

                            if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect && !path.toLowerCase().endsWith(".gif")) {
                                String localPathNew = attachFile.saveGalleryPicToLocal(path);
                                sendMessage(requestCode, localPathNew);
                            } else {
                                sendMessage(requestCode, path);
                            }
                        }
                    }
                }).start();
            }

            if (listPathString.size() == 1) {

                if (sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {

                    if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect) {
                        if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));

                                Uri uri = Uri.parse(listPathString.get(0));
                                Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);
                                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUri(uri));
                                intent.putExtra("TYPE", "gallery");
                                intent.putExtra("PAGE", "chat");
                                startActivityForResult(intent, IntentRequests.REQ_CROP);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (prgWaiting != null) {
                                            prgWaiting.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));
                                Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);
                                Uri uri = Uri.parse(listPathString.get(0));
                                uri = Uri.parse("file://" + AttachFile.getFilePathFromUri(uri));
                                intent.putExtra("IMAGE_CAMERA", uri.toString());
                                intent.putExtra("TYPE", "gallery");
                                intent.putExtra("PAGE", "chat");
                                startActivityForResult(intent, IntentRequests.REQ_CROP);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (prgWaiting != null) {
                                            prgWaiting.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }


                        } else {
                            runOnUiThread(new Runnable() {
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
                            Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);

                            intent.putExtra("IMAGE_CAMERA", listPathString.get(0));
                            intent.putExtra("TYPE", "camera");
                            intent.putExtra("PAGE", "chat");
                            startActivityForResult(intent, IntentRequests.REQ_CROP);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (prgWaiting != null) {
                                        prgWaiting.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            ImageHelper.correctRotateImage(listPathString.get(0), true);

                            Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);

                            intent.putExtra("IMAGE_CAMERA", "file://" + listPathString.get(0));
                            intent.putExtra("TYPE", "camera");
                            intent.putExtra("PAGE", "chat");
                            startActivityForResult(intent, IntentRequests.REQ_CROP);

                            runOnUiThread(new Runnable() {
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

        attachFile = new AttachFile(this);
        backGroundSeenList.clear();

        Realm realm = Realm.getDefaultInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");
            chatPeerId = extras.getLong("peerId");
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
            pageSettings();


            avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
            txtName = (TextView) findViewById(R.id.chl_txt_name);
            txtLastSeen = (TextView) findViewById(R.id.chl_txt_last_seen);
            viewGroupLastSeen = (ViewGroup) findViewById(R.id.chl_txt_viewGroup_seen);
            imvUserPicture = (ImageView) findViewById(R.id.chl_imv_user_picture);
            /**
             * need this info for load avatar
             */
            if (realmRoom != null) {
                chatType = realmRoom.getType();
                if (chatType == CHAT) {
                    chatPeerId = realmRoom.getChatRoom().getPeerId();
                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
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
                        userStatus = getResources().getString(R.string.last_seen_recently);
                    }
                } else {
                    mRoomId = realmRoom.getId();
                    title = realmRoom.getTitle();
                    if (chatType == GROUP) {
                        groupParticipantsCountLabel = realmRoom.getGroupRoom().getParticipantsCountLabel();
                    } else {
                        groupParticipantsCountLabel = realmRoom.getChannelRoom().getParticipantsCountLabel();
                    }
                }

                if (chatType == CHAT) {
                    setUserStatus(userStatus, lastSeen);
                } else if ((chatType == GROUP) || (chatType == CHANNEL)) {
                    if (groupParticipantsCountLabel != null) {
                        txtLastSeen.setText(groupParticipantsCountLabel + " " + getResources().getString(R.string.member));
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        }
                    }
                }


            } else if (chatPeerId != 0) {
                /**
                 * when user start new chat this block will be called
                 */
                chatType = CHAT;
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
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
            if (HelperCalander.isLanguagePersian) {
                txtName.setText(HelperCalander.convertToUnicodeFarsiNumber(txtName.getText().toString()));
            }
            if (HelperCalander.isLanguagePersian) {
                txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
            }

            setAvatar();
        }

        realm.close();
    }

    private void initMain() {
        HelperGetMessageState.clearMessageViews();
        MusicPlayer.playedList.clear();

        /**
         * define views
         */
        mediaLayout = (LinearLayout) findViewById(R.id.ac_ll_music_layout);
        lyt_user = (LinearLayout) findViewById(R.id.lyt_user);
        viewAttachFile = findViewById(R.id.layout_attach_file);
        viewMicRecorder = findViewById(R.id.layout_mic_recorde);
        prgWaiting = (ProgressBar) findViewById(R.id.chl_prgWaiting);
        AppUtils.setProgresColler(prgWaiting);
        voiceRecord = new VoiceRecord(this, viewMicRecorder, viewAttachFile, this);

        prgWaiting.setVisibility(View.VISIBLE);

        txtEmptyMessages = (TextView) findViewById(R.id.empty_messages);
        //txtEmptyMessages.setVisibility(View.VISIBLE);

        lastDateCalendar.clear();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");
            isGoingFromUserLink = extras.getBoolean("GoingFromUserLink");
            isNotJoin = extras.getBoolean("ISNotJoin");
            userName = extras.getString("UserName");

            if (isNotJoin) {
                final LinearLayout layoutJoin = (LinearLayout) findViewById(R.id.ac_ll_join);
                layoutJoin.setBackgroundColor(Color.parseColor(G.appBarColor));
                layoutJoin.setVisibility(View.VISIBLE);
                layoutJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HelperUrl.showIndeterminateProgressDialog();
                        G.onClientJoinByUsername = new OnClientJoinByUsername() {
                            @Override
                            public void onClientJoinByUsernameResponse() {

                                isNotJoin = false;
                                HelperUrl.closeDialogWaiting();

                                ActivityChat.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        layoutJoin.setVisibility(View.GONE);

                                        findViewById(R.id.ac_ll_parent).invalidate();

                                        if (chatType == GROUP) {
                                            viewAttachFile.setVisibility(View.VISIBLE);
                                            isChatReadOnly = false;
                                        }
                                    }
                                });

                                Realm realm = Realm.getDefaultInstance();
                                final RealmRoom joinedRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                                if (joinedRoom != null) {

                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            joinedRoom.setDeleted(false);
                                            if (joinedRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                                                joinedRoom.setReadOnly(false);
                                            }


                                        }
                                    });
                                }

                                realm.close();
                            }

                            @Override
                            public void onError(int majorCode, int minorCode) {
                                HelperUrl.dialogWaiting.dismiss();
                            }
                        };

                        new RequestClientJoinByUsername().clientJoinByUsername(userName);
                    }
                });
            }
            messageId = extras.getLong("MessageId");

            /**
             * get userId . use in chat set action.
             */

            Realm realm = Realm.getDefaultInstance();

            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo == null) {
                finish();
                return;
            }
            userId = realmUserInfo.getUserId();

            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

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
                    ((RecyclerView) findViewById(R.id.chl_recycler_view_chat)).setPadding(0, 0, 0, 0);
                }

                if (chatType == CHAT) {

                    //RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                    //chatPeerId = realmChatRoom.getPeerId();

                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
                    if (realmRegisteredInfo != null) {
                        //title = realmRegisteredInfo.getDisplayName();
                        initialize = realmRegisteredInfo.getInitials();
                        color = realmRegisteredInfo.getColor();
                        //lastSeen = realmRegisteredInfo.getLastSeen();
                        //userStatus = realmRegisteredInfo.getStatus();
                        phoneNumber = realmRegisteredInfo.getPhoneNumber();
                    } else {
                        title = realmRoom.getTitle();
                        initialize = realmRoom.getInitials();
                        color = realmRoom.getColor();
                        userStatus = getResources().getString(R.string.last_seen_recently);
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
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
                if (realmRegisteredInfo != null) {
                    title = realmRegisteredInfo.getDisplayName();
                    initialize = realmRegisteredInfo.getInitials();
                    color = realmRegisteredInfo.getColor();
                    lastSeen = realmRegisteredInfo.getLastSeen();
                    userStatus = realmRegisteredInfo.getStatus();
                }
            }

            realm.close();
        }

        initComponent();
        initAppbarSelected();
        getDraft();
        getUserInfo();
        checkAction();
        insertShearedData();

        G.onChatSendMessage = new OnChatSendMessage() {
            @Override
            public void Error(int majorCode, int minorCode, final int waitTime) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showErrorDialog(waitTime);
                    }
                });
            }
        };
    }


    /**
     * get settings state and change view
     */
    private void pageSettings() {
        /**
         * get sendByEnter action from setting value
         */
        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        sendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0) == 1;

        /**
         * set background
         */
        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {

            File f = new File(backGroundPath);
            if (f.exists()) {
                Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                getWindow().setBackgroundDrawable(d);
            }
        } else {
            getWindow().setBackgroundDrawableResource(R.drawable.newbg);
        }

        /**
         * set app color to appBar
         */
        appBarLayout = (MyAppBarLayout) findViewById(R.id.ac_appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        findViewById(R.id.ac_green_line).setBackgroundColor(Color.parseColor(G.appBarColor));
    }



    /**
     * initialize some callbacks that used in this page
     */
    public void initCallbacks() {
        chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        G.onChatEditMessageResponse = new OnChatEditMessageResponse() {
            @Override
            public void onChatEditMessage(long roomId, final long messageId, long messageVersion, final String message, ProtoResponse.Response response) {
                if (mRoomId == roomId) {
                    // I'm in the room
                    runOnUiThread(new Runnable() {
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

                    //Realm realm = Realm.getDefaultInstance();
                    //RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                    //if (roomMessage != null) {
                    //    roomMessage.setDeleted(true);
                    //}
                    //realm.close();

                    // if deleted message is for current room clear from adapter
                    if (roomId == mRoomId) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // remove deleted message from adapter
                                mAdapter.removeMessage(messageId);
                                if (mAdapter.getItemCount() > 0) {
                                    txtEmptyMessages.setVisibility(View.GONE);
                                } else {
                                    txtEmptyMessages.setVisibility(View.VISIBLE);
                                }
                                // remove tag from edtChat if the message has deleted
                                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                                    if (Long.toString(messageId).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                        edtChat.setTag(null);
                                    }
                                }
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

                if (result) {

                    txtLastSeen.setText(messageOne + " " + getResources().getString(member));

                    avi.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                    }

                    // change english number to persian number
                    if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                } else {
                    clearHistory(Long.parseLong(messageOne));
                }



            }
        };

        onMusicListener = new OnComplete() {
            @Override
            public void complete(boolean result, String messageID, String beforMessageID) {

                if (beforMessageID != null) {
                    for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
                        if (mAdapter.getItem(i).mMessage.messageID.equals(beforMessageID)) {
                            mAdapter.notifyAdapterItemChanged(i);
                            break;
                        }
                    }
                }

                if (messageID != null) {
                    for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
                        if (mAdapter.getItem(i).mMessage.messageID.equals(messageID)) {
                            mAdapter.notifyAdapterItemChanged(i);
                            break;
                        }
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
                HelperSetAction.sendCancel(messageId);

                String[] split = messageOne.split(",");
                Double latitude = Double.parseDouble(split[0]);
                Double longitude = Double.parseDouble(split[1]);

                FragmentMap fragment = FragmentMap.getInctance(latitude, longitude, FragmentMap.Mode.sendPosition);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(ac_ll_parent, fragment, FragmentMap.flagFragmentMap).commit();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.clear();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        };

        G.onUpdateUserStatusInChangePage = new OnUpdateUserStatusInChangePage() {
            @Override
            public void updateStatus(long peerId, String status, long lastSeen) {
                if (chatType == CHAT) {
                    setUserStatus(status, lastSeen);

                    if (chatType == CHAT) {
                        new RequestUserInfo().userInfo(peerId);
                    }
                }
            }
        };
    }

    private void initComponent() {
        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        iconMute = (MaterialDesignTextView) findViewById(R.id.imgMutedRoom);
        RippleView rippleBackButton = (RippleView) findViewById(R.id.chl_ripple_back_Button);

        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {

            iconMute.setVisibility(realmRoom.getMute() ? View.VISIBLE : View.GONE);
            isMuteNotification = realmRoom.getMute();
        }
        realm.close();

        ll_attach_text = (LinearLayout) findViewById(R.id.ac_ll_attach_text);
        txtFileNameForSend = (TextView) findViewById(R.id.ac_txt_file_neme_for_sending);
        btnCancelSendingFile = (MaterialDesignTextView) findViewById(R.id.ac_btn_cancel_sending_file);
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
                            runOnUiThread(new Runnable() {
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

        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.2);

        RippleView rippleMenuButton = (RippleView) findViewById(R.id.chl_ripple_menu_button);
        rippleMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rippleView) {

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChat.this).customView(R.layout.chat_popup_dialog_custom, true).build();
                View v = dialog.getCustomView();

                DialogAnimation.animationUp(dialog);
                dialog.show();

                ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
                ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
                ViewGroup root3 = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);
                ViewGroup root4 = (ViewGroup) v.findViewById(R.id.dialog_root_item4_notification);
                ViewGroup root5 = (ViewGroup) v.findViewById(R.id.dialog_root_item5_notification);
                ViewGroup root6 = (ViewGroup) v.findViewById(R.id.dialog_root_item6_notification);

                TextView txtSearch = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
                TextView txtClearHistory = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
                TextView txtDeleteChat = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
                TextView txtMuteNotification = (TextView) v.findViewById(R.id.dialog_text_item4_notification);
                TextView txtChatToGroup = (TextView) v.findViewById(R.id.dialog_text_item5_notification);
                TextView txtCleanUp = (TextView) v.findViewById(R.id.dialog_text_item6_notification);

                TextView iconSearch = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
                iconSearch.setText(getResources().getString(R.string.md_searching_magnifying_glass));

                TextView iconClearHistory = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
                iconClearHistory.setText(getResources().getString(R.string.md_clearHistory));

                TextView iconDeleteChat = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
                iconDeleteChat.setText(getResources().getString(R.string.md_rubbish_delete_file));

                TextView iconMuteNotification = (TextView) v.findViewById(R.id.dialog_icon_item4_notification);

                TextView iconChatToGroup = (TextView) v.findViewById(R.id.dialog_icon_item5_notification);
                iconChatToGroup.setText(getResources().getString(R.string.md_users_social_symbol));

                TextView iconCleanUp = (TextView) v.findViewById(R.id.dialog_icon_item6_notification);
                iconCleanUp.setText(getResources().getString(R.string.md_clean_up));

                root1.setVisibility(View.VISIBLE);
                root2.setVisibility(View.VISIBLE);
                root3.setVisibility(View.VISIBLE);
                root4.setVisibility(View.VISIBLE);
                root5.setVisibility(View.VISIBLE);
                root6.setVisibility(View.VISIBLE);

                txtSearch.setText(getResources().getString(R.string.Search));
                txtClearHistory.setText(getResources().getString(R.string.clear_history));
                txtDeleteChat.setText(getResources().getString(R.string.delete_chat));
                txtMuteNotification.setText(getResources().getString(R.string.mute_notification));
                txtChatToGroup.setText(getResources().getString(R.string.chat_to_group));
                txtCleanUp.setText(getResources().getString(R.string.clean_up));


                if (chatType == CHAT) {
                    root3.setVisibility(View.VISIBLE);
                    root5.setVisibility(View.VISIBLE);
                } else {
                    root3.setVisibility(View.GONE);
                    root5.setVisibility(View.GONE);

                    if (chatType == CHANNEL) {
                        root2.setVisibility(View.GONE);
                    }
                }

                final Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {

                    if (realmRoom.getMute()) {
                        txtMuteNotification.setText(getResources().getString(R.string.unmute_notification));
                        iconMuteNotification.setText(getResources().getString(R.string.md_unMuted));


                    } else {
                        txtMuteNotification.setText(getResources().getString(R.string.mute_notification));
                        iconMuteNotification.setText(getResources().getString(R.string.md_muted));
                    }
                } else {
                    root2.setVisibility(View.GONE);
                    root3.setVisibility(View.GONE);
                    root4.setVisibility(View.GONE);
                    root5.setVisibility(View.GONE);
                    root6.setVisibility(View.GONE);
                }
                realm.close();

                root1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        initLayoutSearchNavigation();
                        findViewById(R.id.ac_green_line).setVisibility(View.GONE);
                        findViewById(R.id.toolbarContainer).setVisibility(View.GONE);
                        ll_Search.setVisibility(View.VISIBLE);
                        ll_navigate_Message.setVisibility(View.VISIBLE);
                        viewAttachFile.setVisibility(View.GONE);
                        edtSearchMessage.requestFocus();
                    }
                });
                root2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                        new MaterialDialog.Builder(ActivityChat.this).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                onSelectRoomMenu("txtClearHistory", (int) mRoomId);
                            }
                        }).negativeText(R.string.B_cancel).show();
                    }
                });
                root3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        new MaterialDialog.Builder(ActivityChat.this).title(R.string.delete_chat).content(R.string.delete_chat_content).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                onSelectRoomMenu("txtDeleteChat", (int) mRoomId);
                            }
                        }).negativeText(R.string.B_cancel).show();
                    }
                });
                root4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        onSelectRoomMenu("txtMuteNotification", (int) mRoomId);

                    }
                });
                root5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        new MaterialDialog.Builder(ActivityChat.this).title(R.string.convert_chat_to_group_title).content(R.string.convert_chat_to_group_content).positiveText(R.string.B_ok).negativeText(R.string.B_cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                                dialog.dismiss();
                                G.onConvertToGroup.openFragmentOnActivity("ConvertToGroup", mRoomId);
                            }
                        }).show();
                    }
                });

                root6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        RealmRoomMessage.ClearAllMessage(false, mRoomId);
                        mAdapter.clear();

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
            }
        });

        imvSmileButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_smile_button);

        edtChat = (EmojiEditText) findViewById(R.id.chl_edt_chat);
        edtChat.requestFocus();

        imvSendButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_send_button);
        imvSendButton.setTextColor(Color.parseColor(G.attachmentColor));

        imvAttachFileButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_attach_button);
        layoutAttachBottom = (LinearLayout) findViewById(R.id.layoutAttachBottom);

        imvMicButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_mic_button);

        recyclerView = (RecyclerView) findViewById(R.id.chl_recycler_view_chat);
        //remove blinking for updates on items
        recyclerView.setItemAnimator(null);
        //following lines make scrolling smoother
        //recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(1000);
        recyclerView.setDrawingCacheEnabled(true);

        mAdapter = new MessagesAdapter<>(this, this, this);

        mAdapter.withFilterPredicate(new IItemAdapter.Predicate<AbstractMessage>() {
            @Override
            public boolean filter(AbstractMessage item, CharSequence constraint) {
                return !item.mMessage.messageText.toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(ActivityChat.this);
        /**
         * make start messages from bottom, this is exactly what Telegram and other messengers do
         * for their messages list
         */
        layoutManager.setStackFromEnd(true);

        /**
         * set behavior to RecyclerView
         * CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
         * params.setBehavior(new ShouldScrolledBehavior(layoutManager, mAdapter));
         * recyclerView.setLayoutParams(params);
         */
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        /**
         * load message , use handler for load async
         */
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                getMessages();
                manageForwardedMessage();

                /**
                 * show unread message
                 */
                if (chatType != CHANNEL) {
                    //addLayoutUnreadMessage();
                }
            }
        });

        llScrollNavigate = (FrameLayout) findViewById(R.id.ac_ll_scrool_navigate);
        txtNewUnreadMessage = (TextView) findViewById(R.id.cs_txt_unread_message);
        AndroidUtils.setBackgroundShapeColor(txtNewUnreadMessage, Color.parseColor(G.notificationColor));

        MaterialDesignTextView txtNavigationLayout = (MaterialDesignTextView) findViewById(R.id.ac_txt_down_navigation);
        AndroidUtils.setBackgroundShapeColor(txtNavigationLayout, Color.parseColor(G.appBarColor));

        llScrollNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latestButtonClickTime = System.currentTimeMillis();
                /**
                 * have unread
                 */
                if (countNewMessage > 0) {
                    /**
                     * if unread message is exist in list set position to this item and create
                     * unread layout otherwise should clear list and load from unread again
                     */

                    firstUnreadMessage = firstUnreadMessageInChat;
                    int position = mAdapter.findPositionByMessageId(firstUnreadMessage.getMessageId());
                    if (position > 0) {

                        RealmRoomMessage unreadMessage = new RealmRoomMessage();
                        unreadMessage.setMessageId(TimeUtils.currentLocalTime());
                        // -1 means time message
                        unreadMessage.setUserId(-1);
                        unreadMessage.setMessage(countNewMessage + " " + getString(R.string.unread_message));
                        unreadMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                        mAdapter.add(position, new UnreadMessage(ActivityChat.this).setMessage(StructMessageInfo.convert(unreadMessage)).withIdentifier(SUID.id().get()));

                        LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                        linearLayout.scrollToPositionWithOffset(position, 0);
                    } else {
                        resetMessagingValue();
                        unreadCount = countNewMessage;
                        firstUnreadMessage = firstUnreadMessageInChat;
                        getMessages();

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
                if (ActivityPopUpNotification.isGoingToChatFromPopUp) {
                    ActivityPopUpNotification.isGoingToChatFromPopUp = false;
                    Intent intent = new Intent(context, ActivityMain.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
            }
        });

        imvUserPicture = (ImageView) findViewById(R.id.chl_imv_user_picture);
        imvUserPicture.setOnClickListener(new View.OnClickListener() {
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

                txtEmptyMessages.setVisibility(View.GONE);

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
                    if (!message.equals(messageInfo.messageText)) {

                        final Realm realm1 = Realm.getDefaultInstance();
                        realm1.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoomMessage roomMessage = realm1.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(messageInfo.messageID)).findFirst();

                                RealmClientCondition realmClientCondition = realm1.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, mRoomId).findFirst();

                                RealmOfflineEdited realmOfflineEdited = realm.createObject(RealmOfflineEdited.class, SUID.id().get());
                                realmOfflineEdited.setMessageId(parseLong(messageInfo.messageID));
                                realmOfflineEdited.setMessage(message);
                                realmOfflineEdited = realm.copyToRealm(realmOfflineEdited);

                                realmClientCondition.getOfflineEdited().add(realmOfflineEdited);

                                if (roomMessage != null) {
                                    // update message text in database
                                    roomMessage.setMessage(message);
                                    roomMessage.setEdited(true);
                                    RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
                                    RealmRoomMessage.isEmojiInText(roomMessage, message);

                                }

                                RealmRoom rm = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                                if (rm != null) rm.setUpdatedTime(TimeUtils.currentLocalTime() / 1000);
                            }
                        });

                        realm1.close();
                        //End

                        // I'm in the room
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update message text in adapter
                                mAdapter.updateMessageText(parseLong(messageInfo.messageID), message);
                            }
                        });

                        /**
                         * should be null after requesting
                         */
                        edtChat.setTag(null);
                        clearReplyView();
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
                    }
                } else { // new message has written

                    final Realm realm = Realm.getDefaultInstance();
                    final long senderId = G.userId;

                    String[] messages = HelperString.splitStringEvery(getWrittenMessage(), Config.MAX_TEXT_LENGTH);
                    if (messages.length == 0) {
                        edtChat.setText("");
                        Toast.makeText(G.context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < messages.length; i++) {
                            final String message = messages[i];
                            if (!message.isEmpty()) {
                                final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                                final String identity = Long.toString(SUID.id().get());
                                final long currentTime = TimeUtils.currentLocalTime();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, parseLong(identity));

                                        roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                                        roomMessage.setMessage(message);
                                        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());

                                        RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
                                        RealmRoomMessage.isEmojiInText(roomMessage, message);

                                        roomMessage.setRoomId(mRoomId);
                                        roomMessage.setShowMessage(true);

                                        roomMessage.setUserId(senderId);
                                        roomMessage.setCreateTime(currentTime);

                                        /**
                                         *  user wants to replay to a message
                                         */
                                        if (userTriesReplay()) {
                                            RealmRoomMessage messageToReplay = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID)).findFirst();
                                            if (messageToReplay != null) {
                                                roomMessage.setReplyTo(messageToReplay);
                                            }
                                        }
                                    }
                                });

                                final RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(identity)).findFirst();

                                if (roomMessage != null) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            if (room != null) {
                                                room.setLastMessage(roomMessage);
                                            }
                                        }
                                    });
                                }

                                if (chatType == CHANNEL) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            RealmChannelExtra realmChannelExtra = realm.createObject(RealmChannelExtra.class);
                                            realmChannelExtra.setMessageId(parseLong(identity));
                                            realmChannelExtra.setThumbsUp("0");
                                            realmChannelExtra.setThumbsDown("0");
                                            if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().isSignature()) {
                                                realmChannelExtra.setSignature(G.displayName);
                                            } else {
                                                realmChannelExtra.setSignature("");
                                            }
                                            realmChannelExtra.setViewsLabel("1");
                                        }
                                    });
                                }
                                mAdapter.add(new TextItem(chatType, ActivityChat.this).setMessage(StructMessageInfo.convert(roomMessage)).withIdentifier(SUID.id().get()));

                                realm.close();

                                scrollToEnd();

                                /**
                                 * send splitted message in every one second
                                 */
                                if (messages.length > 1) {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                                        }
                                    }, 1000 * i);
                                } else {
                                    new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                                }


                                edtChat.setText("");

                                /**
                                 * if replay layout is visible, gone it
                                 */
                                clearReplyView();
                            } else {
                                Toast.makeText(G.context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

        imvAttachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!initAttach) {
                    initAttach = true;
                    initAttach();
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                itemAdapterBottomSheet();
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetDialog.show();
                    }
                }, 100);
            }
        });

        imvMicButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (ContextCompat.checkSelfPermission(ActivityChat.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    try {
                        HelperPermision.getMicroPhonePermission(ActivityChat.this, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(ActivityChat.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        try {
                            HelperPermision.getStoragePermision(ActivityChat.this, null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        voiceRecord.setItemTag("ivVoice");
                        viewAttachFile.setVisibility(View.GONE);
                        viewMicRecorder.setVisibility(View.VISIBLE);
                        voiceRecord.startVoiceRecord();
                    }
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imvSendButton.clearAnimation();
                                        imvSendButton.setVisibility(View.VISIBLE);
                                    }
                                });

                            }
                        }).start();
                    } else {
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
                                runOnUiThread(new Runnable() {
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
            }
        });
    }

    private void putExtra(Intent intent, StructMessageInfo messageInfo) {
        try {
            String filePath = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getLocalFilePath() : messageInfo.attachment.getLocalFilePath();

            if (filePath != null) {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Intent makeIntentForForwardMessages(ArrayList<Parcelable> messageInfos) {
        Intent intent = new Intent(ActivityChat.this, ActivitySelectChat.class);
        intent.putParcelableArrayListExtra(ActivitySelectChat.ARG_FORWARD_MESSAGE, messageInfos);
        return intent;
    }

    private Intent makeIntentForForwardMessages(StructMessageInfo messageInfos) {
        return makeIntentForForwardMessages(new ArrayList<>(Arrays.asList(Parcels.wrap(messageInfos))));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * *************************** callbacks ***************************
     */

    @Override
    public void onSenderAvatarClick(View view, StructMessageInfo messageInfo, int position) {
        Intent intent = new Intent(G.context, ActivityContactsProfile.class);
        intent.putExtra("peerId", parseLong(messageInfo.senderID));
        intent.putExtra("RoomId", mRoomId);
        intent.putExtra("enterFrom", GROUP.toString());
        startActivity(intent);
    }

    @Override
    public void onUploadOrCompressCancel(View view, final StructMessageInfo message, int pos, SendingStep sendingStep) {

        if (sendingStep == SendingStep.UPLOADING) {
            HelperSetAction.sendCancel(Long.parseLong(message.messageID));

            if (HelperUploadFile.cancelUploading(message.messageID)) {
                clearItem(Long.parseLong(message.messageID), pos);
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
            clearItem(Long.parseLong(message.messageID), pos);
        }
    }

    @Override
    public void onChatClearMessage(long roomId, long clearId, ProtoResponse.Response response) {

        boolean clearMessage = false;

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
        for (final RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            if (!clearMessage && realmRoomMessage.getMessageId() == clearId) {
                clearMessage = true;
            }

            if (clearMessage) {
                final long messageId = realmRoomMessage.getMessageId();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realmRoomMessage.deleteFromRealm();
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // remove deleted message from adapter

                        mAdapter.removeMessage(messageId);
                        if (mAdapter.getItemCount() > 0) {
                            txtEmptyMessages.setVisibility(View.GONE);
                        } else {
                            txtEmptyMessages.setVisibility(View.VISIBLE);
                        }

                        // remove tag from edtChat if the message has deleted
                        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                            if (Long.toString(messageId).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                edtChat.setTag(null);
                            }
                        }
                    }
                });
            }
        }
        realm.close();
    }

    @Override
    public void onChatUpdateStatus(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, long statusVersion) {

        // I'm in the room
        if (mRoomId == roomId) {
            runOnUiThread(new Runnable() {
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

            txtNumberOfSelected.setText(Integer.toString(selectedCount));

            if (selectedCount > 1) {
                btnReplaySelected.setVisibility(View.INVISIBLE);
            } else {

                if (chatType == CHANNEL) {
                    if (channelRole == ChannelChatRole.MEMBER) {
                        btnReplaySelected.setVisibility(View.INVISIBLE);
                    }
                } else {
                    btnReplaySelected.setVisibility(View.VISIBLE);
                }
            }

            ll_AppBarSelected.setVisibility(View.VISIBLE);
            findViewById(R.id.ac_green_line).setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            ll_AppBarSelected.setVisibility(View.GONE);
            findViewById(R.id.ac_green_line).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPreChatMessageRemove(final StructMessageInfo messageInfo, int position) {
        if (mAdapter.getAdapterItemCount() > 1 && position == mAdapter.getAdapterItemCount() - 1) {
            // if was last message removed
            // update room last message
            Realm realm = Realm.getDefaultInstance();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        long _deletedMessageId = Long.parseLong(messageInfo.messageID);
                        RealmRoomMessage realmRoomMessage = null;
                        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.EDITED, false).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, _deletedMessageId).findAll();
                        if (realmRoomMessages.size() > 0) {
                            realmRoomMessage = realmRoomMessages.last();
                        }

                        if (realmRoom != null && realmRoomMessage != null) {
                            realmRoom.setLastMessage(realmRoomMessage);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });

            realm.close();
        }
    }

    @Override
    public void onMessageUpdate(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, final String identity, ProtoGlobal.RoomMessage roomMessage) {
        // I'm in the room
        if (roomId == mRoomId && mAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateMessageIdAndStatus(messageId, identity, status);
                }
            });
        }
    }

    @Override
    public void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType) {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Realm realm = Realm.getDefaultInstance();
                        final RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();

                        if (realmRoomMessage != null) {
                            if (roomMessage.getAuthor().getUser() != null) {
                                if (roomMessage.getAuthor().getUser().getUserId() != G.userId) {
                                    // I'm in the room
                                    if (roomId == mRoomId) {
                                        // I'm in the room, so unread messages count is 0. it means, I read all messages
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                                                if (room != null) {
                                                    /**
                                                     * client checked  (room.getUnreadCount() <= 1)  because in HelperMessageResponse unreadCount++
                                                     */
                                                    if (room.getUnreadCount() <= 1) {
                                                        realmRoomMessage.setFutureMessageId(realmRoomMessage.getMessageId());
                                                    }
                                                    room.setUnreadCount(0);
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
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, mRoomId).findFirst();
                                                if (!isNotJoin) {
                                                    // make update status to message sender that i've read his message

                                                    StructBackGroundSeen _BackGroundSeen = null;

                                                    ProtoGlobal.RoomMessageStatus roomMessageStatus;
                                                    if (G.isAppInFg) {

                                                        /**
                                                         * I'm in the room, so unread messages count is 0. it means, I read all messages
                                                         */
                                                        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                                                        if (room != null) {
                                                            room.setUnreadCount(0);
                                                        }

                                                        if (realmRoomMessage != null) {
                                                            if (!realmRoomMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {
                                                                realmRoomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());

                                                                RealmOfflineSeen realmOfflineSeen = realm.createObject(RealmOfflineSeen.class, SUID.id().get());
                                                                realmOfflineSeen.setOfflineSeen(realmRoomMessage.getMessageId());
                                                                realm.copyToRealmOrUpdate(realmOfflineSeen);
                                                                realmClientCondition.getOfflineSeen().add(realmOfflineSeen);
                                                            }
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
                                            switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(realmRoomMessage))), false);
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
                                            switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(realmRoomMessage))), false);
                                        }
                                        setBtnDownVisible(realmRoomMessage);
                                    }
                                }
                            }
                        }

                        realm.close();
                    }
                });
            }
        }, 400);
    }

    @Override
    public void onMessageFailed(long roomId, RealmRoomMessage message) {
        if (roomId == mRoomId) {
            mAdapter.updateMessageStatus(message.getMessageId(), ProtoGlobal.RoomMessageStatus.FAILED);
        }
    }

    @Override
    public void onVoiceRecordDone(final String savedPath) {
        Realm realm = Realm.getDefaultInstance();
        final long messageId = SUID.id().get();
        final long updateTime = TimeUtils.currentLocalTime();
        final long senderID = G.userId;
        final long duration = AndroidUtils.getAudioDuration(getApplicationContext(), savedPath) / 1000;

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, messageId);
                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.VOICE);
                roomMessage.setMessage(getWrittenMessage());
                roomMessage.setRoomId(mRoomId);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setAttachment(messageId, savedPath, 0, 0, 0, null, duration, LocalFileType.FILE);
                roomMessage.setUserId(senderID);
                roomMessage.setCreateTime(updateTime);
                voiceLastMessage = roomMessage;

                // in response to a message as replay, so after server done creating replay and
                // forward options, modify this section and sending message as well.
            }
        });

        StructMessageInfo messageInfo;

        if (userTriesReplay()) {
            messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                    RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
        } else {
            if (isMessageWrote()) {
                messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                        RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime);
            } else {
                messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                        RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime);
            }
        }

        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, savedPath, messageId, ProtoGlobal.RoomMessageType.VOICE, getWrittenMessage(), StructMessageInfo.getReplyMessageId(messageInfo), new HelperUploadFile.UpdateListener() {
            @Override
            public void OnProgress(int progress, FileUploadStructure struct) {
                insertItemAndUpdateAfterStartUpload(progress, struct);
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
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (voiceLastMessage != null) {
                    realmRoom.setLastMessage(voiceLastMessage);
                }
            }
        });
        if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().isSignature()) {
            structChannelExtra.signature = G.displayName;
        } else {
            structChannelExtra.signature = "";
        }
        messageInfo.channelExtra = structChannelExtra;
        mAdapter.add(new VoiceItem(chatType, this).setMessage(messageInfo));
        realm.close();
        scrollToEnd();
        clearReplyView();
    }

    @Override
    public void onVoiceRecordCancel() {
        //empty
    }

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
        setAvatar();
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
        Realm realm = Realm.getDefaultInstance();
        if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == IMAGE_TEXT || messageType == ProtoGlobal.RoomMessageType.VIDEO || messageType == VIDEO_TEXT) {
            showImage(message);
        } else if (messageType == ProtoGlobal.RoomMessageType.FILE || messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {

            String _filePath = null;
            String _token = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getToken() : message.attachment.token;
            RealmAttachment _Attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, _token).findFirst();

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
        realm.close();
    }

    @Override
    public void onDownloadAllEqualCashId(String cashId, String messageID) {

        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            try {
                AbstractMessage item = mAdapter.getAdapterItem(i);
                if (item.mMessage.hasAttachment()) {
                    if (item.mMessage.getAttachment().cashID.equals(cashId) && (!item.mMessage.messageID.equals(messageID))) {
                        mAdapter.notifyItemChanged(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemShowingMessageId(StructMessageInfo messageInfo) {
        /**
         * if in current room client have new message that not seen yet
         * after first new message come in the view change view for unread count
         */
        if (firstUnreadMessageInChat != null && firstUnreadMessageInChat.isManaged() && firstUnreadMessageInChat.isValid() && !firstUnreadMessageInChat.isDeleted() && firstUnreadMessageInChat.getMessageId() == Long.parseLong(messageInfo.messageID)) {
            countNewMessage = 0;
            txtNewUnreadMessage.setVisibility(View.GONE);
            txtNewUnreadMessage.setText(countNewMessage + "");

            firstUnreadMessageInChat = null;
        }
    }

    @Override
    public void onContainerClick(View view, final StructMessageInfo message, int pos) {

        final MaterialDialog dialog = new MaterialDialog.Builder(this).customView(R.layout.chat_popup_dialog_custom, true).build();

        View v = dialog.getCustomView();

        DialogAnimation.animationDown(dialog);
        dialog.show();

        ViewGroup rootReplay = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup rootCopy = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
        ViewGroup rootShare = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);
        ViewGroup rootForward = (ViewGroup) v.findViewById(R.id.dialog_root_item4_notification);
        ViewGroup rootDelete = (ViewGroup) v.findViewById(R.id.dialog_root_item5_notification);
        ViewGroup rootEdit = (ViewGroup) v.findViewById(R.id.dialog_root_item6_notification);
        ViewGroup rootSaveToDownload = (ViewGroup) v.findViewById(R.id.dialog_root_item7_notification);

        TextView txtItemReplay = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtItemCopy = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
        TextView txtItemShare = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
        TextView txtItemForward = (TextView) v.findViewById(R.id.dialog_text_item4_notification);
        TextView txtItemDelete = (TextView) v.findViewById(R.id.dialog_text_item5_notification);
        TextView txtItemEdit = (TextView) v.findViewById(R.id.dialog_text_item6_notification);
        final TextView txtItemSaveToDownload = (TextView) v.findViewById(R.id.dialog_text_item7_notification);

        TextView iconReplay = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
        iconReplay.setText(getResources().getString(R.string.md_back_arrow_reply));

        TextView iconCopy = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconCopy.setText(getResources().getString(R.string.md_copy));

        TextView iconShare = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
        iconShare.setText(getResources().getString(R.string.md_share_button));

        TextView iconForward = (TextView) v.findViewById(R.id.dialog_icon_item4_notification);
        iconForward.setText(getResources().getString(R.string.md_right_arrow_button));

        TextView iconDelete = (TextView) v.findViewById(R.id.dialog_icon_item5_notification);
        iconDelete.setText(getResources().getString(R.string.md_rubbish_delete_file));

        TextView iconEdit = (TextView) v.findViewById(R.id.dialog_icon_item6_notification);
        iconEdit.setText(getResources().getString(R.string.md_edit));

        TextView iconItemSaveToDownload = (TextView) v.findViewById(R.id.dialog_icon_item7_notification);
        iconItemSaveToDownload.setText(getResources().getString(R.string.md_save));


        @ArrayRes int itemsRes = 0;
        switch (message.messageType) {
            case TEXT:
                //itemsRes = R.array.textMessageDialogItems;

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


                break;
            case FILE_TEXT:
            case IMAGE_TEXT:
            case VIDEO_TEXT:
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
            case VOICE:
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

        if (itemsRes != 0) {
            // Arrays.asList returns fixed size, doing like this fixes remove object
            // UnsupportedOperationException exception
            List<String> items = new LinkedList<>(Arrays.asList(getResources().getStringArray(itemsRes)));

            Realm realm = Realm.getDefaultInstance();
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, message.roomId).findFirst();
            if (realmRoom != null) {
                // if user clicked on any message which he wasn't its sender, remove edit mList option
                if (chatType == CHANNEL) {
                    if (channelRole == ChannelChatRole.MEMBER) {
                        rootEdit.setVisibility(View.GONE);
                        rootReplay.setVisibility(View.GONE);
                        rootDelete.setVisibility(View.GONE);

                        //items.remove(getString(R.string.edit_item_dialog));
                        //items.remove(getString(R.string.replay_item_dialog));
                        //items.remove(getString(R.string.delete_item_dialog));
                    }
                    final long senderId = G.userId;
                    ChannelChatRole roleSenderMessage = null;
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    RealmList<RealmMember> realmMembers = realmChannelRoom.getMembers();
                    for (RealmMember rm : realmMembers) {
                        if (rm.getPeerId() == Long.parseLong(message.senderID)) {
                            roleSenderMessage = ChannelChatRole.valueOf(rm.getRole());
                        }
                    }
                    if (senderId != Long.parseLong(message.senderID)) {  // if message dose'nt belong to owner
                        if (channelRole == ChannelChatRole.MEMBER) {

                            //items.remove(getString(R.string.delete_item_dialog));
                            rootDelete.setVisibility(View.GONE);
                        } else if (channelRole == ChannelChatRole.MODERATOR) {
                            if (roleSenderMessage == ChannelChatRole.MODERATOR || roleSenderMessage == ChannelChatRole.ADMIN || roleSenderMessage == ChannelChatRole.OWNER) {
                                //items.remove(getString(R.string.delete_item_dialog));
                                rootDelete.setVisibility(View.GONE);
                            }
                        } else if (channelRole == ChannelChatRole.ADMIN) {
                            if (roleSenderMessage == ChannelChatRole.OWNER || roleSenderMessage == ChannelChatRole.ADMIN) {
                                //items.remove(getString(R.string.delete_item_dialog));
                                rootDelete.setVisibility(View.GONE);
                            }
                        }
                        //items.remove(getString(R.string.edit_item_dialog));
                        rootEdit.setVisibility(View.GONE);
                    }
                } else if (chatType == GROUP) {

                    final long senderId = G.userId;

                    GroupChatRole roleSenderMessage = null;
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    RealmList<RealmMember> realmMembers = realmGroupRoom.getMembers();
                    for (RealmMember rm : realmMembers) {
                        if (rm.getPeerId() == Long.parseLong(message.senderID)) {
                            roleSenderMessage = GroupChatRole.valueOf(rm.getRole());
                        }
                    }
                    if (senderId != Long.parseLong(message.senderID)) {  // if message dose'nt belong to owner
                        if (groupRole == GroupChatRole.MEMBER) {
                            //items.remove(getString(R.string.delete_item_dialog));
                            rootDelete.setVisibility(View.GONE);
                        } else if (groupRole == GroupChatRole.MODERATOR) {
                            if (roleSenderMessage == GroupChatRole.MODERATOR || roleSenderMessage == GroupChatRole.ADMIN || roleSenderMessage == GroupChatRole.OWNER) {
                                //items.remove(getString(R.string.delete_item_dialog));
                                rootDelete.setVisibility(View.GONE);
                            }
                        } else if (groupRole == GroupChatRole.ADMIN) {
                            if (roleSenderMessage == GroupChatRole.OWNER || roleSenderMessage == GroupChatRole.ADMIN) {
                                //items.remove(getString(R.string.delete_item_dialog));
                                rootDelete.setVisibility(View.GONE);
                            }
                        }
                        //items.remove(getString(R.string.edit_item_dialog));
                        rootEdit.setVisibility(View.GONE);
                    }
                } else {
                    if (!message.senderID.equalsIgnoreCase(Long.toString(G.userId))) {
                        //items.remove(getString(R.string.edit_item_dialog));
                        rootEdit.setVisibility(View.GONE);
                    }
                }

                realm.close();
            }
        }

        String _savedFolderName = "";
        if (message.messageType.toString().contains("IMAGE") || message.messageType.toString().contains("VIDEO") || message.messageType.toString().contains("GIF")) {
            _savedFolderName = getString(R.string.save_to_gallery);
        } else if (message.messageType.toString().contains("AUDIO") || message.messageType.toString().contains("VOICE")) {
            _savedFolderName = getString(R.string.save_to_Music);
        } else {
            _savedFolderName = getString(R.string.saveToDownload_item_dialog);
        }

        txtItemSaveToDownload.setText(_savedFolderName);
        rootReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                replay(message);
            }
        });
        rootCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String _text = message.forwardedFrom != null ? message.forwardedFrom.getMessage() : message.messageText;
                if (_text != null && _text.length() > 0) {
                    ClipData clip = ClipData.newPlainText("Copied Text", _text);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(G.context, R.string.text_copied, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(G.context, R.string.text_is_empty, Toast.LENGTH_SHORT).show();
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
                // forward selected messages to room list for selecting room
                if (mAdapter != null) {
                    finish();
                    startActivity(makeIntentForForwardMessages(message));
                }
            }
        });
        rootDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        // remove deleted message from adapter
                        mAdapter.removeMessage(parseLong(message.messageID));
                        if (mAdapter.getItemCount() > 0) {
                            txtEmptyMessages.setVisibility(View.GONE);
                        } else {
                            txtEmptyMessages.setVisibility(View.VISIBLE);
                        }
                        // remove tag from edtChat if the
                        // message has deleted
                        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                            if (Long.toString(parseLong(message.messageID)).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                edtChat.setTag(null);
                            }
                        }
                    }
                });
                final Realm realmCondition = Realm.getDefaultInstance();
                final RealmClientCondition realmClientCondition = realmCondition.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, message.roomId).findFirstAsync();
                realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
                    @Override
                    public void onChange(final RealmClientCondition element) {
                        realmCondition.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (element != null) {
                                    if (realmCondition.where(RealmOfflineDelete.class).equalTo(RealmOfflineDeleteFields.OFFLINE_DELETE, parseLong(message.messageID)).findFirst() == null) {
                                        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(message.messageID)).findFirst();
                                        if (roomMessage != null) {
                                            roomMessage.setDeleted(true);
                                        }
                                        RealmOfflineDelete realmOfflineDelete = realmCondition.createObject(RealmOfflineDelete.class, SUID.id().get());
                                        realmOfflineDelete.setOfflineDelete(parseLong(message.messageID));
                                        element.getOfflineDeleted().add(realmOfflineDelete);
                                        // delete message
                                        if (chatType == GROUP) {
                                            new RequestGroupDeleteMessage().groupDeleteMessage(mRoomId, parseLong(message.messageID));
                                        } else if (chatType == CHAT) {
                                            new RequestChatDeleteMessage().chatDeleteMessage(mRoomId, parseLong(message.messageID));
                                        } else if (chatType == CHANNEL) {
                                            new RequestChannelDeleteMessage().channelDeleteMessage(mRoomId, parseLong(message.messageID));
                                        }
                                    }
                                    element.removeAllChangeListeners();
                                }
                            }
                        });
                        realmCondition.close();
                    }
                });
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
                    edtChat.setTag(message);
                }
            }
        });
        rootSaveToDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (txtItemSaveToDownload.getText().toString().equalsIgnoreCase(getString(R.string.saveToDownload_item_dialog))) {
                    String _dPath = message.getAttachment().localFilePath != null ? message.getAttachment().localFilePath : AndroidUtils.getFilePathWithCashId(message.getAttachment().cashID, message.getAttachment().name, message.messageType);
                    HelperSaveFile.saveFileToDownLoadFolder(_dPath, message.getAttachment().name, HelperSaveFile.FolderType.download, R.string.file_save_to_download_folder);
                } else if (txtItemSaveToDownload.getText().toString().equalsIgnoreCase(getString(R.string.save_to_Music))) {
                    String _dPath = message.getAttachment().localFilePath != null ? message.getAttachment().localFilePath : AndroidUtils.getFilePathWithCashId(message.getAttachment().cashID, message.getAttachment().name, message.messageType);
                    HelperSaveFile.saveFileToDownLoadFolder(_dPath, message.getAttachment().name, HelperSaveFile.FolderType.music, R.string.save_to_music_folder);
                } else if (txtItemSaveToDownload.getText().toString().equalsIgnoreCase(getString(R.string.save_to_gallery))) {
                    String _dPath = message.getAttachment().localFilePath != null ? message.getAttachment().localFilePath : AndroidUtils.getFilePathWithCashId(message.getAttachment().cashID, message.getAttachment().name, message.messageType);
                    if (message.messageType.toString().contains("VIDEO")) {
                        HelperSaveFile.saveFileToDownLoadFolder(_dPath, message.getAttachment().name, HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
                        HelperSaveFile.saveVideoToGallary(_dPath, true);
                    } else if (message.messageType.toString().contains("GIF")) {
                        HelperSaveFile.saveFileToDownLoadFolder(_dPath, message.getAttachment().name, HelperSaveFile.FolderType.gif, R.string.file_save_to_picture_folder);
                    } else {
                        HelperSaveFile.savePicToGallary(_dPath, true);
                    }
                }
            }
        });
    }


    @Override
    public void onFailedMessageClick(View view, final StructMessageInfo message, final int pos) {
        final List<StructMessageInfo> failedMessages = mAdapter.getFailedMessages();
        new ResendMessage(this, new IResendMessage() {
            @Override
            public void deleteMessage() {
                mAdapter.remove(pos);
            }

            @Override
            public void resendMessage() {
                mAdapter.updateMessageStatus(parseLong(message.messageID), ProtoGlobal.RoomMessageStatus.SENDING);
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
    public void onSetAction(final long roomId, final long userId, final ProtoGlobal.ClientAction clientAction) {

        final boolean isCloudRoom = RealmRoom.isCloudRoom(mRoomId);
        if (mRoomId == roomId && (this.userId != userId || (isCloudRoom))) {
            Realm realm = Realm.getDefaultInstance();
            final String action = HelperGetAction.getAction(roomId, chatType, clientAction);

            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realmRoom.setActionState(action, userId);
                    }
                });

            }
            realm.close();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (action != null) {
                        avi.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }
                        txtLastSeen.setText(action);
                    } else if (chatType == CHAT) {
                        if (isCloudRoom) {
                            txtLastSeen.setText(getResources().getString(R.string.chat_with_yourself));
                        } else {
                            if (userStatus != null) {
                                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                                } else {
                                    txtLastSeen.setText(userStatus);
                                }
                            }
                        }
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }
                        //txtLastSeen.setText(userStatus);
                    } else if (chatType == GROUP) {
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }
                        txtLastSeen.setText(groupParticipantsCountLabel + " " + getString(member));
                    }

                    // change english number to persian number
                    if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            });
        }
    }

    @Override
    public void onUserUpdateStatus(long userId, final long time, final String status) {
        if (chatType == CHAT && chatPeerId == userId) {
            userStatus = AppUtils.getStatsForUser(status);
            setUserStatus(userStatus, time);
        }
    }

    @Override
    public void onLastSeenUpdate(final long userIdR, final String showLastSeen) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chatType == CHAT && userIdR == chatPeerId && userId != userIdR) { // userId != userIdR means that , this isn't update status for own user
                    txtLastSeen.setText(showLastSeen);
                    avi.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                    }
                    // change english number to persian number
                    if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            }
        });
    }

    /**
     * GroupAvatar and ChannelAvatar
     */
    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {

        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvUserPicture);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imvUserPicture.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
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
        runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateMessageState(stats.getMessageId(), stats.getThumbsUpLabel(), stats.getThumbsDownLabel(), stats.getViewsLabel());
                    }
                });
            }
        }
    }

    /**
     * *************************** common method ***************************
     */

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
    public void clearHistory(long chatId) {
        llScrollNavigate.setVisibility(View.GONE);
        saveMessageIdPositionState(0);
        clearHistoryMessage(chatId);
        addToView = true;
        txtEmptyMessages.setVisibility(View.VISIBLE);

    }


    /**
     * message will be replied or no
     */
    private boolean userTriesReplay() {
        return mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo;
    }

    /**
     * if userTriesReplay() is true use from this method
     */
    private long getReplyMessageId() {
        return parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID);
    }

    /**
     * if userTriesReplay() is true use from this method
     */
    private void clearReplyView() {
        if (mReplayLayout != null) {
            mReplayLayout.setTag(null);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mReplayLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * change foreground color for selected message
     */
    private void selectMessage(int position) {
        try {
            if (mAdapter.getItem(position).mMessage.view != null) {
                ((FrameLayout) mAdapter.getItem(position).mMessage.view).setForeground(new ColorDrawable(getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * clear foreground color for deSelected message
     */
    private void deSelectMessage(int position) {
        if (mAdapter.getItem(position) != null && mAdapter.getItem(position).mMessage.view != null) {
            ((FrameLayout) mAdapter.getItem(position).mMessage.view).setForeground(null);
        }
    }

    /**
     * clear all items that exist in view
     */
    private void clearAdapterItems() {
        mAdapter.clear();
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

        HelperAvatar.getAvatar(idForGetAvatar, type, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvUserPicture);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imvUserPicture.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    private ArrayList<Parcelable> getMessageStructFromSelectedItems() {
        ArrayList<Parcelable> messageInfos = new ArrayList<>(mAdapter.getSelectedItems().size());
        for (AbstractMessage item : mAdapter.getSelectedItems()) {
            messageInfos.add(Parcels.wrap(item.mMessage));
        }
        return messageInfos;
    }

    /**
     * show current state for user if this room is chat
     *
     * @param status current state
     * @param time if state is not online set latest online time
     */
    private void setUserStatus(final String status, final long time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userStatus = status;
                userTime = time;
                if (RealmRoom.isCloudRoom(mRoomId)) {
                    txtLastSeen.setText(getResources().getString(R.string.chat_with_yourself));
                    avi.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                    }
                } else {
                    if (status != null) {
                        if (status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                            txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, time, true, false));
                        } else {
                            txtLastSeen.setText(status);
                        }
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }
                        // change english number to persian number
                        if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));

                        checkAction();
                    }
                }
            }
        });
    }

    private void replay(StructMessageInfo item) {
        if (mAdapter != null) {
            Set<AbstractMessage> messages = mAdapter.getSelectedItems();
            // replay works if only one message selected
            inflateReplayLayoutIntoStub(item == null ? messages.iterator().next().mMessage : item);

            ll_AppBarSelected.setVisibility(View.GONE);
            findViewById(R.id.ac_green_line).setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);

            mAdapter.deselect();
        }
    }

    private void checkAction() {
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null && realmRoom.getActionState() != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (realmRoom.getActionState() != null && (chatType == GROUP || chatType == CHANNEL) || ((RealmRoom.isCloudRoom(mRoomId) || (!RealmRoom.isCloudRoom(mRoomId) && realmRoom.getActionStateUserId() != userId)))) {
                        txtLastSeen.setText(realmRoom.getActionState());
                        avi.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }
                    } else if (chatType == CHAT) {
                        if (RealmRoom.isCloudRoom(mRoomId)) {
                            txtLastSeen.setText(getResources().getString(R.string.chat_with_yourself));
                        } else {
                            if (userStatus != null) {
                                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                                } else {
                                    txtLastSeen.setText(userStatus);
                                }
                            }
                        }
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }

                    } else if (chatType == GROUP) {
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        }

                        txtLastSeen.setText(groupParticipantsCountLabel + " " + getString(member));

                    }
                    // change english number to persian number
                    if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            });
        }
        realm.close();
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
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, fakeMessageId).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, fakeMessageId).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                        }
                        realm.close();
                    }
                });
            }
        });
    }

    private void showErrorDialog(final int time) {

        boolean wrapInScrollView = true;
        final MaterialDialog dialogWait = new MaterialDialog.Builder(G.currentActivity).title(getResources().getString(R.string.title_limit_chat_to_unknown_contact)).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();
        //dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(true);
        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        final TextView txtText = (TextView) v.findViewById(R.id.textRemindTime);
        txtText.setText(getResources().getString(R.string.text_limit_chat_to_unknown_contact));
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addItemAfterStartUpload(struct);
                }
            });
        } else if (progress == 100) {
            String messageId = struct.messageId + "";
            for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
                AbstractMessage item = mAdapter.getAdapterItem(i);

                if (item.mMessage.messageID.equals(messageId)) {
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
            Realm realm = Realm.getDefaultInstance();
            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, struct.messageId).findFirst();
            if (roomMessage != null) {
                AbstractMessage message = null;

                if (mAdapter != null && struct != null) {
                    message = mAdapter.getItemByFileIdentity(struct.messageId);

                    // message doesn't exists
                    if (message == null) {
                        switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(roomMessage))), false);
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
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * open profile for this room or user profile if room is chat
     */
    private void goToProfile() {
        if (chatType == CHAT) {
            Intent intent = new Intent(G.context, ActivityContactsProfile.class);
            intent.putExtra("peerId", chatPeerId);
            intent.putExtra("RoomId", mRoomId);
            intent.putExtra("enterFrom", CHAT.toString());
            startActivity(intent);
        } else if (chatType == GROUP) {
            if (!isChatReadOnly) {
                Intent intent = new Intent(G.context, ActivityGroupProfile.class);
                intent.putExtra("RoomId", mRoomId);
                startActivity(intent);
            }
        } else if (chatType == CHANNEL) {
            Intent intent = new Intent(G.context, ActivityChannelProfile.class);
            intent.putExtra(Config.PutExtraKeys.CHANNEL_PROFILE_ROOM_ID_LONG.toString(), mRoomId);
            startActivity(intent);
        }
    }

    public static void deleteSelectedMessages(final long RoomId, final ArrayList<Long> list, final ProtoGlobal.Room.Type chatType) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // get offline delete list , add new deleted list and update in
                // client condition , then send request for delete message to server
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, RoomId).findFirst();

                for (final Long messageId : list) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                    if (roomMessage != null) {
                        roomMessage.setDeleted(true);
                    }

                    RealmOfflineDelete realmOfflineDelete = realm.createObject(RealmOfflineDelete.class, SUID.id().get());
                    realmOfflineDelete.setOfflineDelete(messageId);

                    realmClientCondition.getOfflineDeleted().add(realmOfflineDelete);

                    if (chatType == GROUP) {
                        new RequestGroupDeleteMessage().groupDeleteMessage(RoomId, messageId);
                    } else if (chatType == CHAT) {
                        new RequestChatDeleteMessage().chatDeleteMessage(RoomId, messageId);
                    } else if (chatType == CHANNEL) {
                        new RequestChannelDeleteMessage().channelDeleteMessage(RoomId, messageId);
                    }
                }
            }
        });
        realm.close();
    }


    /**
     * copy text
     */
    public void copySelectedItemTextToClipboard() {
        for (AbstractMessage _message : mAdapter.getSelectedItems()) {
            String text = _message.mMessage.forwardedFrom != null ? _message.mMessage.forwardedFrom.getMessage() : _message.mMessage.messageText;

            if (text == null || text.length() == 0) {
                continue;
            }

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        mAdapter.deselect();
        toolbar.setVisibility(View.VISIBLE);
        ll_AppBarSelected.setVisibility(View.GONE);
        findViewById(R.id.ac_green_line).setVisibility(View.VISIBLE);
        clearReplyView();
    }


    /**
     * clear tag from edtChat and remove from view and delete from RealmRoomMessage
     */
    private void clearItem(final long messageId, int position) {
        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
            if (Long.toString(messageId).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                edtChat.setTag(null);
            }
        }

        mAdapter.removeMessage(position);
        if (mAdapter.getItemCount() > 0) {
            txtEmptyMessages.setVisibility(View.GONE);
        } else {
            txtEmptyMessages.setVisibility(View.VISIBLE);
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                if (roomMessage != null) {
                    // delete message from database
                    roomMessage.deleteFromRealm();
                }
            }
        });
        realm.close();
    }

    private void onSelectRoomMenu(String message, int item) {
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

    public static void clearHistoryMessage(final long chatId) {

        // make request for clearing messages
        final Realm realm = Realm.getDefaultInstance();

        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatId).findFirst();

        if (realmClientCondition != null && realmClientCondition.isLoaded() && realmClientCondition.isValid()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatId).findFirst();

                    if (realmRoom.isLoaded() && realmRoom.isValid() && realmRoom.getLastMessage() != null) {
                        realmClientCondition.setClearId(realmRoom.getLastMessage().getMessageId());

                        G.clearMessagesUtil.clearMessages(realmRoom.getType(), chatId, realmRoom.getLastMessage().getMessageId());
                    }

                    RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, chatId).findAll();
                    for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                        if (realmRoomMessage != null) {
                            // delete chat history message
                            realmRoomMessage.deleteFromRealm();
                        }
                    }

                    RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatId).findFirst();
                    if (room != null) {
                        room.setUnreadCount(0);
                        room.setLastMessage(null);
                        room.setFirstUnreadMessage(null);
                        room.setUpdatedTime(0);
                    }
                    // finally delete whole chat history
                    realmRoomMessages.deleteAllFromRealm();
                }
            });

            if (G.onClearChatHistory != null) {
                G.onClearChatHistory.onClearChatHistory();
            }
        }
        realm.close();
    }

    private void deleteChat(final int chatId) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatId).findFirstAsync();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(final Realm realm) {
                if (realm.where(RealmOfflineDelete.class).equalTo(RealmOfflineDeleteFields.OFFLINE_DELETE, chatId).findFirst() == null) {
                    RealmOfflineDelete realmOfflineDelete = realm.createObject(RealmOfflineDelete.class, SUID.id().get());
                    realmOfflineDelete.setOfflineDelete(chatId);

                    realmClientCondition.getOfflineDeleted().add(realmOfflineDelete);

                    realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatId).findFirst().deleteFromRealm();
                    realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, chatId).findAll().deleteAllFromRealm();

                    new RequestChatDelete().chatDelete(chatId);

                    finish();
                }
            }
        });

        realm.close();

    }

    private void muteNotification(final int item) {
        Realm realm = Realm.getDefaultInstance();

        isMuteNotification = !isMuteNotification;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, item).findFirst();
                if (realmRoom != null) {
                    realmRoom.setMute(isMuteNotification);
                }
            }
        });

        if (isMuteNotification) {
            ((TextView) findViewById(R.id.chl_txt_mute_channel)).setText(R.string.unmute);
            iconMute.setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.chl_txt_mute_channel)).setText(R.string.mute);
            iconMute.setVisibility(View.GONE);
        }

        realm.close();
    }

    private void setBtnDownVisible(RealmRoomMessage realmRoomMessage) {
        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (addToView && llm.findLastVisibleItemPosition() + 5 > recyclerView.getAdapter().getItemCount()) {
            scrollToEnd();
        } else {
            if (countNewMessage == 0) {
                /**
                 * remove unread layout message if already exist in chat list
                 */
                for (int i = (mAdapter.getItemCount() - 1); i >= 0; i--) {
                    if (mAdapter.getItem(i) instanceof UnreadMessage) {
                        mAdapter.remove(i);
                    }
                }
                firstUnreadMessageInChat = realmRoomMessage;
            }
            countNewMessage++;
            llScrollNavigate.setVisibility(View.VISIBLE);
            txtNewUnreadMessage.setText(countNewMessage + "");
            txtNewUnreadMessage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * open fragment show image and show all image for this room
     */
    private void showImage(final StructMessageInfo messageInfo) {
        // for gone app bar
        FragmentShowImage.appBarLayout = appBarLayout;

        long selectedFileToken = Long.parseLong(messageInfo.messageID);

        FragmentShowImage fragment = FragmentShowImage.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong("RoomId", mRoomId);
        bundle.putLong("SelectedImage", selectedFileToken);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.ac_ll_parent, fragment, "ShowImageMessage").commit();
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

    /**
     * after load message call this method for go to bottom of chat list
     */
    private void scrollToLastPositionMessageId() {

        Realm realm = Realm.getDefaultInstance();

        try {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
            if (realmRoom != null) {
                String lastScrolledMessageId = realmRoom.getLastScrollPositionMessageId() + "";
                if (lastScrolledMessageId.length() > 0) {
                    for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
                        if (mAdapter.getAdapterItem(i).mMessage.messageID.equals(lastScrolledMessageId)) {
                            recyclerView.scrollToPosition(i);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        realm.close();
    }

    private void storingLastPosition() {
        try {
            if (recyclerView != null && mAdapter != null) {
                int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (mAdapter.getItem(firstVisiblePosition) instanceof TimeItem || mAdapter.getItem(firstVisiblePosition) instanceof UnreadMessage) {
                    firstVisiblePosition++;
                }

                if (mAdapter.getItem(firstVisiblePosition) instanceof TimeItem || mAdapter.getItem(firstVisiblePosition) instanceof UnreadMessage) {
                    firstVisiblePosition++;
                }

                long lastScrolledMessageID = 0;

                if (firstVisiblePosition + 15 < mAdapter.getAdapterItemCount()) {
                    lastScrolledMessageID = Long.parseLong(mAdapter.getItem(firstVisiblePosition).mMessage.messageID);
                }

                Realm realm = Realm.getDefaultInstance();

                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {
                    saveMessageIdPositionState(lastScrolledMessageID);
                }

                realm.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * save latest messageId position that user saw in chat before close it
     */
    private void saveMessageIdPositionState(final long position) {

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setLastScrollPositionMessageId(position);
                }
            }
        });

        realm.close();
    }


    /**
     * get images for show in bottom sheet
     */
    public static ArrayList<StructBottomSheet> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data = 0, column_index_folder_name;
        ArrayList<StructBottomSheet> listOfAllImages = new ArrayList<StructBottomSheet>();
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

    /**
     * emoji initialization
     */
    private void setUpEmojiPopup() {
        emojiPopup = net.iGap.emoji.EmojiPopup.Builder.fromRootView(findViewById(ac_ll_parent)).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
            @Override
            public void onEmojiBackspaceClicked(final View v) {

            }
        }).setOnEmojiClickedListener(new OnEmojiClickedListener() {
            @Override
            public void onEmojiClicked(final Emoji emoji) {

            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {

            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
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

                switch (requestCode) {
                    case AttachFile.request_code_TAKE_PICTURE:
                        txtFileNameForSend.setText(getString(R.string.image_selected_for_send));
                        break;
                    case AttachFile.requestOpenGalleryForImageMultipleSelect:
                        if (listPathString.size() == 1) {
                            if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {
                                txtFileNameForSend.setText(getString(R.string.image_selected_for_send));
                            } else {
                                txtFileNameForSend.setText(getString(R.string.gif_selected_for_send));
                            }
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + getString(R.string.image_selected_for_send));
                        }

                        break;

                    case AttachFile.requestOpenGalleryForVideoMultipleSelect:
                        txtFileNameForSend.setText(getString(R.string.multi_video_selected_for_send));
                        break;
                    case request_code_VIDEO_CAPTURED:

                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(getString(R.string.video_selected_for_send));
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + getString(R.string.video_selected_for_send));
                        }
                        break;

                    case AttachFile.request_code_pic_audi:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(getString(R.string.audio_selected_for_send));
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + getString(R.string.audio_selected_for_send));
                        }
                        break;
                    case AttachFile.request_code_pic_file:
                        txtFileNameForSend.setText(getString(R.string.file_selected_for_send));
                        break;
                    case AttachFile.request_code_open_document:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(getString(R.string.file_selected_for_send));
                        }
                        break;
                    case AttachFile.request_code_paint:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(getString(R.string.pain_selected_for_send));
                        }
                        break;
                    case AttachFile.request_code_contact_phone:
                        txtFileNameForSend.setText(getString(R.string.phone_selected_for_send));
                        break;
                    case IntentRequests.REQ_CROP:
                        txtFileNameForSend.setText(getString(R.string.crop_selected_for_send));
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

                if (ll_attach_text == null) { // have null error , so reInitialize for avoid that

                    ll_attach_text = (LinearLayout) findViewById(R.id.ac_ll_attach_text);
                    layoutAttachBottom = (LinearLayout) findViewById(R.id.layoutAttachBottom);
                    imvSendButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_send_button);
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
                        runOnUiThread(new Runnable() {
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

    private void setDraft() {
        if (!isNotJoin) {
            if (mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE) {
                StructMessageInfo info = ((StructMessageInfo) mReplayLayout.getTag());
                replyToMessageId = parseLong(info.messageID);
            } else {
                replyToMessageId = 0;
            }
            if (edtChat == null) {
                return;
            }

            String message = edtChat.getText().toString();

            if (!message.trim().isEmpty() || ((mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE))) {

                hasDraft = true;

                Realm realm = Realm.getDefaultInstance();
                final String finalMessage = message;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        if (realmRoom != null) {

                            RealmRoomDraft draft = realm.createObject(RealmRoomDraft.class);
                            draft.setMessage(finalMessage);
                            draft.setReplyToMessageId(replyToMessageId);

                            realmRoom.setDraft(draft);

                            if (chatType == CHAT) {
                                new RequestChatUpdateDraft().chatUpdateDraft(mRoomId, finalMessage, replyToMessageId);
                            } else if (chatType == GROUP) {
                                new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId, finalMessage, replyToMessageId);
                            } else if (chatType == CHANNEL) {
                                new RequestChannelUpdateDraft().channelUpdateDraft(mRoomId, finalMessage, replyToMessageId);
                            }
                            if (G.onDraftMessage != null) {
                                G.onDraftMessage.onDraftMessage(mRoomId, finalMessage);
                            }
                        }
                    }
                });
                realm.close();
            } else {
                clearDraftRequest();
            }
        }
    }

    private void getDraft() {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {
            RealmRoomDraft draft = realmRoom.getDraft();
            if (draft != null) {
                hasDraft = true;
                edtChat.setText(draft.getMessage());
            }
        }
        realm.close();
        clearLocalDraft();
    }

    private void clearLocalDraft() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setDraft(null);
                    realmRoom.setDraftFile(null);
                }
            }
        });
        realm.close();
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
                    if (messageType == HelperGetDataFromOtherApp.FileType.message) {

                        String message = HelperGetDataFromOtherApp.message;
                        edtChat.setText(message);
                        imvSendButton.performClick();

                    } else if (messageType == HelperGetDataFromOtherApp.FileType.image) {

                        for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {
                            sendMessage(AttachFile.request_code_TAKE_PICTURE, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                        }

                    } else if (messageType == HelperGetDataFromOtherApp.FileType.video) {
                        if (HelperGetDataFromOtherApp.messageFileAddress.size() == 1 && (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && (sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1))) {
                            String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                            mainVideoPath = AttachFile.getFilePathFromUri(Uri.parse(HelperGetDataFromOtherApp.messageFileAddress.get(0).toString()));

                            new VideoCompressor().execute(mainVideoPath, savePathVideoCompress);
                            sendMessage(request_code_VIDEO_CAPTURED, savePathVideoCompress);
                        } else {
                            for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {
                                compressedPath.put(AttachFile.getFilePathFromUri(Uri.parse(HelperGetDataFromOtherApp.messageFileAddress.get(i).toString())), true);
                                sendMessage(request_code_VIDEO_CAPTURED, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                            }
                        }

                    } else if (messageType == HelperGetDataFromOtherApp.FileType.audio) {

                        for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {
                            sendMessage(AttachFile.request_code_pic_audi, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                        }

                    } else if (messageType == HelperGetDataFromOtherApp.FileType.file) {

                        for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {
                            HelperGetDataFromOtherApp.FileType fileType = messageType = HelperGetDataFromOtherApp.FileType.file;
                            if (HelperGetDataFromOtherApp.fileTypeArray.size() > 0) {
                                fileType = HelperGetDataFromOtherApp.fileTypeArray.get(i);
                            }

                            if (fileType == HelperGetDataFromOtherApp.FileType.image) {
                                sendMessage(AttachFile.request_code_TAKE_PICTURE, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                            } else if (fileType == HelperGetDataFromOtherApp.FileType.video) {
                                if (HelperGetDataFromOtherApp.messageFileAddress.size() == 1 && (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && (sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1))) {
                                    String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                                    mainVideoPath = AttachFile.getFilePathFromUri(Uri.parse(HelperGetDataFromOtherApp.messageFileAddress.get(0).toString()));

                                    new VideoCompressor().execute(mainVideoPath, savePathVideoCompress);
                                    sendMessage(request_code_VIDEO_CAPTURED, savePathVideoCompress);
                                } else {
                                    compressedPath.put(AttachFile.getFilePathFromUri(Uri.parse(HelperGetDataFromOtherApp.messageFileAddress.get(i).toString())), true);
                                    sendMessage(request_code_VIDEO_CAPTURED, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                                }
                            } else if (fileType == HelperGetDataFromOtherApp.FileType.audio) {
                                sendMessage(AttachFile.request_code_pic_audi, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                            } else if (fileType == HelperGetDataFromOtherApp.FileType.file) {
                                sendMessage(AttachFile.request_code_open_document, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                            }

                        }
                    }
                    HelperGetDataFromOtherApp.messageType = null;
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
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePathPosition)));
                    }
                    break;
                case "VOICE":
                case "AUDIO":
                case "AUDIO_TEXT":
                    intent.setType("audio/*");
                    putExtra(intent, messageInfo);
                    chooserDialogText = getString(R.string.share_audio_file);
                    break;
                case "IMAGE":
                case "IMAGE_TEXT":
                    intent.setType("image/*");
                    putExtra(intent, messageInfo);
                    chooserDialogText = getString(R.string.share_image);
                    break;
                case "VIDEO":
                case "VIDEO_TEXT":
                    intent.setType("video/*");
                    putExtra(intent, messageInfo);
                    chooserDialogText = getString(R.string.share_video_file);
                    break;
                case "FILE":
                case "FILE_TEXT":
                    String mfilepath = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getLocalFilePath() : messageInfo.attachment.getLocalFilePath();
                    if (mfilepath != null) {
                        Uri uri = Uri.fromFile(new File(mfilepath));
                        String mimeType = FileUtils.getMimeType(ActivityChat.this, uri);

                        if (mimeType == null || mimeType.length() < 1) {
                            mimeType = "*/*";
                        }

                        intent.setType(mimeType);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        chooserDialogText = getString(R.string.share_file);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(G.context, R.string.file_not_download_yet, Toast.LENGTH_SHORT).show();
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
     * *************************** init layout ***************************
     */

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
            }
        };
    }

    /**
     * init layout hashtak for up and down
     */
    private void initHashView() {
        ll_navigateHash = (LinearLayout) findViewById(R.id.ac_ll_hash_navigation);
        btnUpHash = (TextView) findViewById(R.id.ac_btn_hash_up);
        btnDownHash = (TextView) findViewById(R.id.ac_btn_hash_down);
        txtHashCounter = (TextView) findViewById(R.id.ac_txt_hash_counter);

        btnUpHash.setTextColor(Color.parseColor(G.appBarColor));
        btnDownHash.setTextColor(Color.parseColor(G.appBarColor));
        txtHashCounter.setTextColor(Color.parseColor(G.appBarColor));


        searchHash = new SearchHash();

        btnHashLayoutClose = (MaterialDesignTextView) findViewById(R.id.ac_btn_hash_close);
        btnHashLayoutClose.setTextColor(Color.parseColor(G.appBarColor));
        btnHashLayoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_navigateHash.setVisibility(View.GONE);
                viewAttachFile.setVisibility(View.VISIBLE);

                if (mAdapter.getItem(searchHash.currentSelectedPosition).mMessage.view != null) {
                    ((FrameLayout) mAdapter.getItem(searchHash.currentSelectedPosition).mMessage.view).setForeground(null);
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

                Realm realm = Realm.getDefaultInstance();
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
                RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, chatPeerId).findFirst();
                if (realmRegisteredInfo != null && realmRegisteredInfo.getId() != G.userId) {
                    if (phoneNumber == null) {
                        if (realmContacts == null && chatType == CHAT && chatPeerId != 134) {
                            initSpamBarLayout(realmRegisteredInfo);
                            vgSpamUser.setVisibility(View.VISIBLE);
                        }
                    }

                    if (realmRegisteredInfo.getId() != G.userId) {
                        if (!realmRegisteredInfo.getDoNotshowSpamBar()) {

                            if (realmRegisteredInfo.isBlockUser()) {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    if (realmContacts != null && realmRegisteredInfo.getId() != G.userId) {
                        if (realmContacts.isBlockUser()) {
                            if (!realmRegisteredInfo.getDoNotshowSpamBar()) {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            } else {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                realm.close();
            }
        });
    }

    /**
     * init spamBar layout
     */
    private void initSpamBarLayout(final RealmRegisteredInfo registeredInfo) {
        vgSpamUser = (ViewGroup) findViewById(R.id.layout_add_contact);
        txtSpamUser = (TextView) findViewById(R.id.chat_txt_addContact);
        txtSpamClose = (TextView) findViewById(R.id.chat_txt_close);
        txtSpamClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vgSpamUser.setVisibility(View.GONE);
                if (registeredInfo != null) {

                    Realm realm = Realm.getDefaultInstance();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            registeredInfo.setDoNotshowSpamBar(true);
                        }
                    });

                    realm.close();
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = false;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(getResources().getString(R.string.block_user));
                                    }
                                }
                            });
                        }
                    };
                    new RequestUserContactsUnblock().userContactsUnblock(chatPeerId);
                } else {

                    G.onUserContactsBlock = new OnUserContactsBlock() {
                        @Override
                        public void onUserContactsBlock(final long userId) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = true;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(getResources().getString(R.string.un_block_user));
                                    }
                                }
                            });
                        }
                    };
                    new RequestUserContactsBlock().userContactsBlock(chatPeerId);
                }
            }
        });
    }


    /**
     * initialize bottomSheet for use in attachment
     */
    private void initAttach() {

        fastItemAdapter = new FastItemAdapter();

        viewBottomSheet = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

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
            public void path(String path, boolean isCheck) {

                if (isCheck) {
                    listPathString.add(path);
                } else {
                    listPathString.remove(path);
                }

                listPathString.size();
                if (listPathString.size() > 0) {
                    //send.setText(R.mipmap.send2);
                    send.setText(getResources().getString(R.string.icon_send));
                    isCheckBottomSheet = true;
                    txtCountItem.setText("" + listPathString.size() + " item");
                } else {
                    //send.setImageResource(R.mipmap.ic_close);
                    send.setText(getResources().getString(R.string.icon_keyboard_arrow_down));
                    isCheckBottomSheet = false;
                    txtCountItem.setText(getResources().getString(R.string.navigation_drawer_close));
                }
            }
        };

        rcvBottomSheet = (RecyclerView) viewBottomSheet.findViewById(R.id.rcvContent);
        rcvBottomSheet.setLayoutManager(new GridLayoutManager(ActivityChat.this, 1, GridLayoutManager.HORIZONTAL, false));
        rcvBottomSheet.setItemAnimator(new DefaultItemAnimator());
        rcvBottomSheet.setDrawingCacheEnabled(true);
        rcvBottomSheet.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rcvBottomSheet.setItemViewCacheSize(100);
        rcvBottomSheet.setAdapter(fastItemAdapter);
        bottomSheetDialog = new BottomSheetDialog(ActivityChat.this);
        bottomSheetDialog.setContentView(viewBottomSheet);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                dialog.dismiss();
                //send.setImageResource(R.mipmap.ic_close);
                send.setText(getResources().getString(R.string.icon_keyboard_arrow_down));
                txtCountItem.setText(getResources().getString(R.string.navigation_drawer_close));
            }
        });


        fastItemAdapter.withSelectable(true);
        listPathString = new ArrayList<>();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                if (sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {
                    attachFile.showDialogOpenCamera(toolbar, null);
                } else {
                    attachFile.showDialogOpenCamera(toolbar, null);
                }
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestOpenGalleryForImageMultipleSelect();
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
                    attachFile.requestOpenGalleryForVideoMultipleSelect();
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
                    attachFile.requestPickAudio();
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
                    attachFile.requestOpenDocumentFolder();
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
                    send.setText(getResources().getString(R.string.icon_keyboard_arrow_down));
                    txtCountItem.setText(getResources().getString(R.string.navigation_drawer_close));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            for (String path : listPathString) {
                                if (!path.toLowerCase().endsWith(".gif")) {
                                    String localpathNew = attachFile.saveGalleryPicToLocal(path);
                                    sendMessage(AttachFile.requestOpenGalleryForImageMultipleSelect, localpathNew);
                                }
                            }

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
                    attachFile.requestPickFile();
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
                    attachFile.requestPaint();
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
                    attachFile.requestGetPosition(complete);
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
                    attachFile.requestPickContact();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void inflateReplayLayoutIntoStub(StructMessageInfo chatItem) {
        if (findViewById(R.id.replayLayoutAboveEditText) == null) {
            ViewStubCompat stubView = (ViewStubCompat) findViewById(R.id.replayLayoutStub);
            stubView.setInflatedId(R.id.replayLayoutAboveEditText);
            stubView.setLayoutResource(R.layout.layout_chat_reply);
            stubView.inflate();

            inflateReplayLayoutIntoStub(chatItem);
        } else {
            mReplayLayout = (LinearLayout) findViewById(R.id.replayLayoutAboveEditText);
            mReplayLayout.setVisibility(View.VISIBLE);
            TextView replayTo = (TextView) mReplayLayout.findViewById(R.id.replayTo);
            TextView replayFrom = (TextView) mReplayLayout.findViewById(replyFrom);
            ImageView thumbnail = (ImageView) mReplayLayout.findViewById(R.id.thumbnail);
            TextView closeReplay = (TextView) mReplayLayout.findViewById(R.id.cancelIcon);
            closeReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearReplyView();
                }
            });
            Realm realm = Realm.getDefaultInstance();
            thumbnail.setVisibility(View.VISIBLE);
            if (chatItem.forwardedFrom != null) {
                AppUtils.rightFileThumbnailIcon(thumbnail, chatItem.forwardedFrom.getMessageType(), chatItem.forwardedFrom);
                replayTo.setText(chatItem.forwardedFrom.getMessage());
            } else {
                RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(chatItem.messageID)).findFirst();
                AppUtils.rightFileThumbnailIcon(thumbnail, chatItem.messageType, message);
                replayTo.setText(chatItem.messageText);
            }
            if (chatType == CHANNEL) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatItem.roomId).findFirst();
                if (realmRoom != null) {
                    replayFrom.setText(realmRoom.getTitle());
                }
            } else {
                RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(chatItem.senderID)).findFirst();
                if (userInfo != null) {
                    replayFrom.setText(userInfo.getDisplayName());
                }
            }

            realm.close();
            // I set tag to retrieve it later when sending message
            mReplayLayout.setTag(chatItem);
        }
    }

    private void initLayoutChannelFooter() {
        LinearLayout layoutAttach = (LinearLayout) findViewById(R.id.chl_ll_attach);
        RelativeLayout layoutChannelFooter = (RelativeLayout) findViewById(R.id.chl_ll_channel_footer);

        layoutAttach.setVisibility(View.GONE);
        layoutChannelFooter.setVisibility(View.VISIBLE);

        btnUp = (TextView) findViewById(R.id.chl_btn_up);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0) recyclerView.scrollToPosition(0);
            }
        });

        btnDown = (TextView) findViewById(R.id.chl_btn_down);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0) recyclerView.scrollToPosition(position - 1);
            }
        });

        txtChannelMute = (TextView) findViewById(R.id.chl_txt_mute_channel);
        txtChannelMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onSelectRoomMenu("txtMuteNotification", (int) mRoomId);

            }
        });

        if (isMuteNotification) {
            txtChannelMute.setText(R.string.unmute);
        } else {
            txtChannelMute.setText(R.string.mute);
        }
    }

    private void initAppbarSelected() {
        ll_AppBarSelected = (LinearLayout) findViewById(R.id.chl_ll_appbar_selelected);

        RippleView rippleCloseAppBarSelected = (RippleView) findViewById(R.id.chl_ripple_close_layout);
        rippleCloseAppBarSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mAdapter.deselect();
                toolbar.setVisibility(View.VISIBLE);
                ll_AppBarSelected.setVisibility(View.GONE);
                clearReplyView();
            }
        });

        btnReplaySelected = (MaterialDesignTextView) findViewById(R.id.chl_btn_replay_selected);
        RippleView rippleReplaySelected = (RippleView) findViewById(R.id.chl_ripple_replay_selected);

        if (chatType == CHANNEL) {
            if (channelRole == ChannelChatRole.MEMBER) {
                btnReplaySelected.setVisibility(View.GONE);
            }
        } else {
            btnReplaySelected.setVisibility(View.VISIBLE);
        }
        rippleReplaySelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (!mAdapter.getSelectedItems().isEmpty() && mAdapter.getSelectedItems().size() == 1) {
                    replay(mAdapter.getSelectedItems().iterator().next().mMessage);
                }
            }
        });
        RippleView rippleCopySelected = (RippleView) findViewById(R.id.chl_ripple_copy_selected);
        rippleCopySelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                copySelectedItemTextToClipboard();

            }
        });
        RippleView rippleForwardSelected = (RippleView) findViewById(R.id.chl_ripple_forward_selected);
        rippleForwardSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                // forward selected messages to room list for selecting room
                if (mAdapter != null && mAdapter.getSelectedItems().size() > 0) {
                    startActivity(makeIntentForForwardMessages(getMessageStructFromSelectedItems()));
                    finish();
                }
            }
        });
        RippleView rippleDeleteSelected = (RippleView) findViewById(R.id.chl_ripple_delete_selected);
        rippleDeleteSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                final ArrayList<Long> list = new ArrayList<Long>();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (final AbstractMessage messageID : mAdapter.getSelectedItems()) {
                            try {
                                if (messageID != null && messageID.mMessage != null && messageID.mMessage.messageID != null) {
                                    Long messageId = parseLong(messageID.mMessage.messageID);
                                    list.add(messageId);

                                    // remove deleted message from adapter
                                    mAdapter.removeMessage(messageId);
                                    if (mAdapter.getItemCount() > 0) {
                                        txtEmptyMessages.setVisibility(View.GONE);
                                    } else {
                                        txtEmptyMessages.setVisibility(View.VISIBLE);
                                    }


                                    // remove tag from edtChat if the message has deleted
                                    if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                                        if (messageID.mMessage.messageID.equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                            edtChat.setTag(null);
                                        }
                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                deleteSelectedMessages(mRoomId, list, chatType);

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
        });
        txtNumberOfSelected = (TextView) findViewById(R.id.chl_txt_number_of_selected);

        if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER) {
            initLayoutChannelFooter();
        }
    }

    private void initLayoutSearchNavigation() {
        ll_navigate_Message = (LinearLayout) findViewById(R.id.ac_ll_message_navigation);
        btnUpMessage = (TextView) findViewById(R.id.ac_btn_message_up);
        txtClearMessageSearch = (MaterialDesignTextView) findViewById(R.id.ac_btn_clear_message_search);
        btnDownMessage = (TextView) findViewById(R.id.ac_btn_message_down);
        txtMessageCounter = (TextView) findViewById(R.id.ac_txt_message_counter);

        btnUpMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPosition > 0) {
                    deSelectMessage(selectedPosition);
                    selectedPosition--;
                    selectMessage(selectedPosition);
                    recyclerView.scrollToPosition(selectedPosition);
                    txtMessageCounter.setText(selectedPosition + 1 + " " + getString(R.string.of) + " " + messageCounter);
                }
            }
        });

        btnDownMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition < messageCounter - 1) {
                    deSelectMessage(selectedPosition);
                    selectedPosition++;
                    selectMessage(selectedPosition);
                    recyclerView.scrollToPosition(selectedPosition);
                    txtMessageCounter.setText(selectedPosition + 1 + " " + getString(R.string.of) + messageCounter);
                }
            }
        });
        final RippleView rippleClose = (RippleView) findViewById(R.id.chl_btn_close_ripple_search_message);
        rippleClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deSelectMessage(selectedPosition);
                edtSearchMessage.setText("");
            }
        });

        ll_Search = (LinearLayout) findViewById(R.id.ac_ll_search_message);
        RippleView rippleBack = (RippleView) findViewById(R.id.chl_ripple_back);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deSelectMessage(selectedPosition);
                edtSearchMessage.setText("");
                ll_Search.setVisibility(View.GONE);
                findViewById(R.id.toolbarContainer).setVisibility(View.VISIBLE);
                ll_navigate_Message.setVisibility(View.GONE);
                viewAttachFile.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        //btnCloseLayoutSearch = (Button) findViewById(R.id.ac_btn_close_layout_search_message);
        edtSearchMessage = (EditText) findViewById(R.id.chl_edt_search_message);
        edtSearchMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                mAdapter.filter(charSequence);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        messageCounter = mAdapter.getAdapterItemCount();

                        if (messageCounter > 0) {
                            selectedPosition = messageCounter - 1;
                            recyclerView.scrollToPosition(selectedPosition);

                            if (charSequence.length() > 0) {
                                selectMessage(selectedPosition);
                                txtMessageCounter.setText(messageCounter + " " + getString(R.string.of) + messageCounter);
                            } else {
                                txtMessageCounter.setText("0 " + getString(R.string.of) + " 0");
                            }
                        } else {
                            txtMessageCounter.setText("0 " + getString(R.string.of) + messageCounter);
                            selectedPosition = 0;
                        }
                    }
                }, 600);

                if (charSequence.length() > 0) {
                    txtClearMessageSearch.setTextColor(Color.WHITE);
                    ((View) rippleClose).setEnabled(true);
                } else {
                    txtClearMessageSearch.setTextColor(Color.parseColor("#dededd"));
                    ((View) rippleClose).setEnabled(false);
                    txtMessageCounter.setText("0 " + getString(R.string.of) + " 0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void itemAdapterBottomSheet() {
        listPathString.clear();
        fastItemAdapter.clear();
        itemGalleryList = getAllShownImagesPath(ActivityChat.this);
        int itemSize = itemGalleryList.size();
        for (int i = 0; i < itemSize; i++) {
            fastItemAdapter.add(new AdapterBottomSheet(itemGalleryList.get(i)).withIdentifier(100 + i));
        }

        itemGalleryList.clear();
    }


    /**
     * *************************** inner classes ***************************
     */

    /**
     * *** SearchHash ***
     */

    private class SearchHash {
        int currentSelectedPosition = 0;
        private String hashString = "";
        private int currentHashPosition = 0;

        private ArrayList<Integer> hashList = new ArrayList<>();

        void setHashString(String hashString) {
            this.hashString = "#" + hashString;
        }

        public void setPosition(String messageId) {
            try {
                if (mAdapter.getItem(searchHash.currentSelectedPosition).mMessage.view != null) {
                    ((FrameLayout) mAdapter.getItem(searchHash.currentSelectedPosition).mMessage.view).setForeground(null);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            currentHashPosition = 0;
            hashList.clear();

            for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {

                if (mAdapter.getItem(i).mMessage.messageID.equals(messageId)) {
                    currentHashPosition = hashList.size() + 1;
                }

                String mText = mAdapter.getItem(i).mMessage.forwardedFrom != null ? mAdapter.getItem(i).mMessage.forwardedFrom.getMessage() : mAdapter.getItem(i).mMessage.messageText;

                if (mText.contains(hashString)) {
                    hashList.add(i);
                }
            }

            txtHashCounter.setText(currentHashPosition + " / " + hashList.size());

            currentSelectedPosition = hashList.get(currentHashPosition - 1);

            if (mAdapter.getItem(currentSelectedPosition).mMessage.view != null) {
                ((FrameLayout) mAdapter.getItem(currentSelectedPosition).mMessage.view).setForeground(new ColorDrawable(getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
            }
        }

        void downHash() {
            if (currentHashPosition < hashList.size()) {
                goToSelectedPosition(hashList.get(currentHashPosition));
                currentHashPosition++;
                txtHashCounter.setText(currentHashPosition + " / " + hashList.size());
            }
        }

        void upHash() {
            if (currentHashPosition > 1) {
                currentHashPosition--;
                goToSelectedPosition(hashList.get(currentHashPosition - 1));
                txtHashCounter.setText(currentHashPosition + " / " + hashList.size());
            }
        }

        private void goToSelectedPosition(int position) {
            ((FrameLayout) mAdapter.getItem(currentSelectedPosition).mMessage.view).setForeground(null);
            recyclerView.scrollToPosition(position);
            currentSelectedPosition = position;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter.getItem(currentSelectedPosition).mMessage.view != null) {
                        ((FrameLayout) mAdapter.getItem(currentSelectedPosition).mMessage.view).setForeground(new ColorDrawable(getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
                    }
                }
            }, 150);
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
            structCompress.compress = MediaController.getInstance().convertVideo(params[0], params[1]);
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
                        long duration = AndroidUtils.getAudioDuration(getApplicationContext(), structUploadVideo.filePath) / 1000;

                        if (fileSize >= structCompress.originalSize) {
                            structUploadVideo.filePath = structCompress.originalPath;
                            mAdapter.updateVideoInfo(structUploadVideo.messageId, duration, structCompress.originalSize);
                        } else {
                            mAdapter.updateVideoInfo(structUploadVideo.messageId, duration, fileSize);
                        }

                        HelperUploadFile.startUploadTaskChat(structUploadVideo.roomId, chatType, structUploadVideo.filePath, structUploadVideo.messageId, structUploadVideo.messageType, structUploadVideo.message, structUploadVideo.replyMessageId, new HelperUploadFile.UpdateListener() {
                            @Override
                            public void OnProgress(int progress, FileUploadStructure struct) {
                                insertItemAndUpdateAfterStartUpload(progress, struct);
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

    /**
     * *************************** Messaging ***************************
     */

    private void sendMessage(int requestCode, String filePath) {

        if (filePath == null || (filePath.length() == 0 && requestCode != AttachFile.request_code_contact_phone)) {
            clearReplyView();
            return;
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
        String path = AttachFile.getFilePathFromUri(Uri.parse(filePath));
        if (path != null) {
            filePath = path;
        }

        StructMessageInfo messageInfo = null;

        switch (requestCode) {
            case IntentRequests.REQ_CROP:
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;
            case AttachFile.request_code_TAKE_PICTURE:

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                if (AndroidUtils.getImageDimens(filePath)[0] == 0 && AndroidUtils.getImageDimens(filePath)[1] == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(G.context, "Picture Not Loaded", Toast.LENGTH_SHORT).show();
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
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
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

                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
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
                    duration = AndroidUtils.getAudioDuration(getApplicationContext(), filePath) / 1000;
                } else {
                    fileSize = new File(mainVideoPath).length();
                    duration = AndroidUtils.getAudioDuration(getApplicationContext(), mainVideoPath) / 1000;
                }

                if (isMessageWrote()) {
                    messageType = VIDEO_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.VIDEO;
                }
                File videoFile = new File(filePath);
                String videoFileMime = FileUtils.getMimeType(videoFile);
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, videoFileMime, filePath, null, filePath, null, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, videoFileMime, filePath, null, filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_pic_audi:
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                duration = AndroidUtils.getAudioDuration(getApplicationContext(), filePath) / 1000;
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO;
                }
                String songArtist = AndroidUtils.getAudioArtistName(filePath);
                long songDuration = AndroidUtils.getAudioDuration(getApplicationContext(), filePath);

                messageInfo = StructMessageInfo.buildForAudio(mRoomId, messageId, senderID, ProtoGlobal.RoomMessageStatus.SENDING, messageType, MyType.SendType.send, updateTime, getWrittenMessage(), null, filePath, songArtist, songDuration, userTriesReplay() ? parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID) : -1);
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
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, fileFileMime, filePath, null, filePath, null, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, fileFileMime, filePath, null, filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_contact_phone:
                if (latestUri == null) {
                    break;
                }
                ContactUtils contactUtils = new ContactUtils(getApplicationContext(), latestUri);
                String name = contactUtils.retrieveName();
                String number = contactUtils.retrieveNumber();
                messageType = CONTACT;
                messageInfo = StructMessageInfo.buildForContact(mRoomId, messageId, senderID, MyType.SendType.send, updateTime, ProtoGlobal.RoomMessageStatus.SENDING, name, "", number, userTriesReplay() ? parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID) : -1);
                break;
            case AttachFile.request_code_paint:
                fileName = new File(filePath).getName();

                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
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
                roomMessage.setShowMessage(true);
                roomMessage.setCreateTime(updateTime);
                if (userTriesReplay()) {
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
                    RealmRoomMessageContact realmRoomMessageContact = realm.createObject(RealmRoomMessageContact.class, SUID.id().get());
                    realmRoomMessageContact.setFirstName(finalMessageInfo.userInfo.firstName);
                    realmRoomMessageContact.setLastName(finalMessageInfo.userInfo.lastName);
                    realmRoomMessageContact.addPhone(finalMessageInfo.userInfo.phone);
                    roomMessage.setRoomMessageContact(realmRoomMessageContact);
                }

                if (finalMessageType != CONTACT) {
                    finalMessageInfo.attachment = StructMessageAttachment.convert(roomMessage.getAttachment());
                }

                String makeThumbnailFilePath = "";
                if (finalMessageType == ProtoGlobal.RoomMessageType.VIDEO || finalMessageType == VIDEO_TEXT) {
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

                if (finalMessageType == ProtoGlobal.RoomMessageType.VIDEO || finalMessageType == VIDEO_TEXT) {
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
                                insertItemAndUpdateAfterStartUpload(progress, struct);
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
                        if (userTriesReplay()) {
                            uploadVideo.replyMessageId = parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID);
                        } else {
                            uploadVideo.replyMessageId = 0;
                        }
                        structUploadVideos.add(uploadVideo);

                        finalMessageInfo.attachment.compressing = getResources().getString(R.string.compressing);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switchAddItem(new ArrayList<>(Collections.singletonList(finalMessageInfo)), false);
                            }
                        });
                    }
                }



                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                if (realmRoom != null) {
                    realmRoom.setLastMessage(roomMessage);
                    //realmRoom.setUpdatedTime(roomMessage.getUpdateOrCreateTime());
                }
            }
        });

        if (finalMessageType != VIDEO && finalMessageType != VIDEO_TEXT) {
            if (finalFilePath != null && finalMessageType != CONTACT) {

                HelperUploadFile.startUploadTaskChat(mRoomId, chatType, finalFilePath, finalMessageId, finalMessageType, getWrittenMessage(), StructMessageInfo.getReplyMessageId(finalMessageInfo), new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        insertItemAndUpdateAfterStartUpload(progress, struct);
                    }

                    @Override
                    public void OnError() {

                    }
                });
            } else {
                ChatSendMessageUtil messageUtil = new ChatSendMessageUtil().newBuilder(chatType, finalMessageType, mRoomId).message(getWrittenMessage());
                messageUtil.contact(finalMessageInfo.userInfo.firstName, finalMessageInfo.userInfo.lastName, finalMessageInfo.userInfo.phone);
                if (userTriesReplay()) {
                    messageUtil.replyMessage(parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                }
                messageUtil.sendMessage(Long.toString(finalMessageId));
            }


            if (finalMessageType == CONTACT) {
                messageInfo.channelExtra = new StructChannelExtra();
                mAdapter.add(new ContactItem(chatType, this).setMessage(messageInfo));
            }
        }

        if (userTriesReplay()) {
            mReplayLayout.setTag(null);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mReplayLayout.setVisibility(View.GONE);
                }
            });

        }

        realm.close();
        scrollToEnd();
    }


    public void sendPosition(final Double latitude, final Double longitude, final String imagePath) {

        Realm realm = Realm.getDefaultInstance();
        final long id = SUID.id().get();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoomMessageLocation messageLocation = realm.createObject(RealmRoomMessageLocation.class, SUID.id().get());
                messageLocation.setLocationLat(latitude);
                messageLocation.setLocationLong(longitude);
                messageLocation.setImagePath(imagePath);
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, id);
                roomMessage.setLocation(messageLocation);
                roomMessage.setCreateTime(TimeUtils.currentLocalTime());
                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.LOCATION);
                roomMessage.setRoomId(mRoomId);
                roomMessage.setUserId(userId);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());

                if (userTriesReplay()) {
                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID)).findFirst();
                    if (realmRoomMessage != null) {
                        roomMessage.setReplyTo(realmRoomMessage);
                    }
                }

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

                if (realmRoom != null) {
                    realmRoom.setLastMessage(roomMessage);
                }
            }
        });
        realm.close();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Realm realm1 = Realm.getDefaultInstance();
                RealmRoomMessage roomMessage = realm1.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, id).findFirst();
                switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(roomMessage))), false);
                chatSendMessageUtil.build(chatType, mRoomId, roomMessage);
                scrollToEnd();
                realm1.close();
            }
        }, 300);

        clearReplyView();
    }

    /**
     * do forward actions if any message forward to this room
     */
    private void manageForwardedMessage() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelableArrayList(ActivitySelectChat.ARG_FORWARD_MESSAGE) != null) {

            final LinearLayout ll_Forward = (LinearLayout) findViewById(R.id.ac_ll_forward);

            if (hasForward) {
                imvCancelForward.performClick();
                final ArrayList<Parcelable> messageInfos = getIntent().getParcelableArrayListExtra(ActivitySelectChat.ARG_FORWARD_MESSAGE);
                for (int i = 0; i < messageInfos.size(); i++) {
                    /**
                     * send forwarded message with one second delay for each message
                     */
                    final int j = i;
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendForwardedMessage((StructMessageInfo) Parcels.unwrap(messageInfos.get(j)));
                        }
                    }, 1000 * j);
                }
            } else {
                imvCancelForward = (TextView) findViewById(R.id.cslhf_imv_cansel);
                imvCancelForward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ll_Forward.setVisibility(View.GONE);
                        hasForward = false;

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
                                    runOnUiThread(new Runnable() {
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imvSendButton.clearAnimation();
                                imvSendButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).start();

                int _count = getIntent().getExtras().getInt(ActivitySelectChat.ARG_FORWARD_MESSAGE_COUNT);
                String str = _count > 1 ? getString(R.string.messages_selected) : getString(R.string.message_selected);

                EmojiTextView emMessage = (EmojiTextView) findViewById(R.id.cslhf_txt_message);

                if (HelperCalander.isLanguagePersian) {

                    emMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(_count + " " + str));
                } else {

                    emMessage.setText(_count + " " + str);
                }

                hasForward = true;
                ll_Forward.setVisibility(View.VISIBLE);
            }
        }
    }


    private void sendForwardedMessage(final StructMessageInfo messageInfo) {

        final long messageId = SUID.id().get();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(messageInfo.messageID)).findFirst();

                        if (roomMessage != null) {

                            RealmRoomMessage forwardedMessage = realm.createObject(RealmRoomMessage.class, messageId);
                            if (roomMessage.getForwardMessage() != null) {
                                forwardedMessage.setForwardMessage(roomMessage.getForwardMessage());
                                forwardedMessage.setHasMessageLink(roomMessage.getForwardMessage().getHasMessageLink());
                            } else {
                                forwardedMessage.setForwardMessage(roomMessage);
                                forwardedMessage.setHasMessageLink(roomMessage.getHasMessageLink());
                            }

                            forwardedMessage.setCreateTime(TimeUtils.currentLocalTime());
                            forwardedMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                            forwardedMessage.setRoomId(mRoomId);
                            forwardedMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                            forwardedMessage.setUserId(G.userId);
                            forwardedMessage.setShowMessage(true);

                            realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst().setLastMessage(forwardedMessage);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        RealmRoomMessage forwardedMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                        if (forwardedMessage != null && forwardedMessage.isValid() && !forwardedMessage.isDeleted()) {
                            switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(forwardedMessage))), false);
                            scrollToEnd();

                            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(messageInfo.messageID)).findFirst();
                            chatSendMessageUtil.buildForward(chatType, forwardedMessage.getRoomId(), forwardedMessage, roomMessage.getRoomId(), roomMessage.getMessageId());
                        }

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


    private StructMessageInfo makeLayoutTime(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        String timeString = TimeUtils.getChatSettingsTimeAgo(this, calendar.getTime());

        RealmRoomMessage timeMessage = new RealmRoomMessage();
        timeMessage.setMessageId(SUID.id().get());
        // -1 means time message
        timeMessage.setUserId(-1);
        timeMessage.setUpdateTime(time);
        timeMessage.setMessage(timeString);
        timeMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);

        return StructMessageInfo.convert(timeMessage);
    }

    private void switchAddItem(ArrayList<StructMessageInfo> messageInfos, boolean addTop) {
        if (prgWaiting != null && messageInfos.size() > 0) {
            prgWaiting.setVisibility(View.GONE);
        }
        long identifier = SUID.id().get();
        for (StructMessageInfo messageInfo : messageInfos) {

            ProtoGlobal.RoomMessageType messageType = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessageType() : messageInfo.messageType;
            if (!messageInfo.isTimeOrLogMessage() || (messageType == LOG)) {
                int index = 0;
                if (addTop && messageInfo.showTime) {

                    for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
                        if (mAdapter.getAdapterItem(i) instanceof TimeItem) {
                            if (!RealmRoomMessage.isTimeDayDiferent(messageInfo.time, mAdapter.getAdapterItem(i).mMessage.time)) {
                                mAdapter.remove(i);
                            }
                            break;
                        }
                    }
                    mAdapter.add(0, new TimeItem(this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));
                    index = 1;
                }

                if (!addTop && messageInfo.showTime) {

                    if (mAdapter.getItemCount() > 0) {
                        if (mAdapter.getAdapterItem(mAdapter.getItemCount() - 1).mMessage != null && RealmRoomMessage.isTimeDayDiferent(messageInfo.time, mAdapter.getAdapterItem(mAdapter.getItemCount() - 1).mMessage.time)) {
                            mAdapter.add(new TimeItem(this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));
                        }
                    } else {
                        mAdapter.add(new TimeItem(this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));
                    }
                }

                switch (messageType) {
                    case TEXT:
                        if (!addTop) {
                            mAdapter.add(new TextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new TextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case IMAGE:
                        if (!addTop) {
                            mAdapter.add(new ImageItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ImageItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case IMAGE_TEXT:
                        if (!addTop) {
                            mAdapter.add(new ImageWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ImageWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VIDEO:
                        if (!addTop) {
                            mAdapter.add(new VideoItem(chatType, this, ActivityChat.this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VideoItem(chatType, this, ActivityChat.this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VIDEO_TEXT:
                        if (!addTop) {
                            mAdapter.add(new VideoWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VideoWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case LOCATION:
                        if (!addTop) {
                            mAdapter.add(new LocationItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new LocationItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case FILE:
                    case FILE_TEXT:
                        if (!addTop) {
                            mAdapter.add(new FileItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new FileItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VOICE:
                        if (!addTop) {
                            mAdapter.add(new VoiceItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VoiceItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case AUDIO:
                    case AUDIO_TEXT:
                        if (!addTop) {
                            mAdapter.add(new AudioItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new AudioItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case CONTACT:
                        if (!addTop) {
                            mAdapter.add(new ContactItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ContactItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case GIF:
                        if (!addTop) {
                            mAdapter.add(new GifItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new GifItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case GIF_TEXT:
                        if (!addTop) {
                            mAdapter.add(new GifWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new GifWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case LOG:
                        if (!addTop) {
                            mAdapter.add(new LogItem(this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new LogItem(this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                }
            }
            identifier++;
        }
    }

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
    private boolean allowGetHistoryUp = true; // after insuring for get end of message from server set this false. (set false in history error maybe was wrong , because maybe this was for another error not end  of message, (hint: can check error code for end of message from history))
    private boolean allowGetHistoryDown = true; // after insuring for get end of message from server set this false. (set false in history error maybe was wrong , because maybe this was for another error not end  of message, (hint: can check error code for end of message from history))
    private long gapMessageIdUp; // messageId that maybe lost in local
    private long gapMessageIdDown; // messageId that maybe lost in local
    private long reachMessageIdUp; // messageId that will be checked after getHistory for detect reached to that or no
    private long reachMessageIdDown; // messageId that will be checked after getHistory for detect reached to that or no
    private long startFutureMessageIdUp; // for get history from local or online in next step use from this param, ( hint : don't use from adapter items, because maybe this item was deleted and in this state messageId for get history won't be detected.
    private long startFutureMessageIdDown; // for get history from local or online in next step use from this param, ( hint : don't use from adapter items, because maybe this item was deleted and in this state messageId for get history won't be detected.
    private int firstVisiblePosition; // difference between start of adapter item and items that Showing.
    private int visibleItemCount; // visible item in recycler view
    private int totalItemCount; // all item in recycler view
    private int scrollEnd = 10; // to determine the limits to get to the bottom of the list

    private void getMessages() {
        Realm realm = Realm.getDefaultInstance();

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
                    //unreadLayoutMessage();
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
            resultsDown = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).equalTo(RealmRoomMessageFields.MESSAGE_ID, fetchMessageId).findAll();

            addToView = false;
            direction = DOWN;
        } else {
            addToView = true;
            direction = UP;
        }

        resultsUp = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).findAllSorted(RealmRoomMessageFields.CREATE_TIME, Sort.DESCENDING);


        long gapMessageId;
        if (direction == DOWN) {
            resultsUp = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, fetchMessageId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).findAllSorted(RealmRoomMessageFields.CREATE_TIME, Sort.DESCENDING);
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

            Object[] object = MessageLoader.getLocalMessage(mRoomId, results.first().getMessageId(), gapMessageId, 10, true, direction);
            messageInfos = (ArrayList<StructMessageInfo>) object[0];
            if (messageInfos.size() > 0) {
                if (direction == UP) {
                    topMore = (boolean) object[1];
                    startFutureMessageIdUp = Long.parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
                } else {
                    bottomMore = (boolean) object[1];
                    startFutureMessageIdDown = Long.parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
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
                        oldMessageId = Long.parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
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
                        getOnlineMessage(Long.parseLong(messageInfos.get(messageInfos.size() - 1).messageID), direction);
                    } else {
                        getOnlineMessage(0, direction);
                    }
                }
            }
        } else {
            /**
             * send request to server for get message
             */
            getOnlineMessage(0, direction);
        }

        if (messageInfos.size() > 0) {
            txtEmptyMessages.setVisibility(View.GONE);
        } else {
            txtEmptyMessages.setVisibility(View.VISIBLE);
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
        realm.close();
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
            Object[] object = getLocalMessage(mRoomId, startFutureMessageId, gapMessageId, 10, false, direction);
            if (direction == UP) {
                topMore = (boolean) object[1];
            } else {
                bottomMore = (boolean) object[1];
            }
            final ArrayList<StructMessageInfo> structMessageInfos = (ArrayList<StructMessageInfo>) object[0];
            if (structMessageInfos.size() > 0) {
                if (direction == UP) {
                    startFutureMessageIdUp = Long.parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                } else {
                    startFutureMessageIdDown = Long.parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                }
            } else {
                if (direction == UP) {
                    startFutureMessageIdUp = 0;
                } else {
                    startFutureMessageIdDown = 0;
                }
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
                        oldMessageId = Long.parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
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

            MessageLoader.getOnlineMessage(mRoomId, oldMessageId, reachMessageId, direction, new OnMessageReceive() {
                @Override
                public void onMessage(final long roomId, long startMessageId, long endMessageId, boolean gapReached, boolean jumpOverLocal, String directionString) {
                    if (roomId != mRoomId) {
                        return;
                    }
                    hideProgress();
                    long startFutureMessageId;
                    /**
                     * hide progress received history
                     */
                    progressItem(HIDE, directionString);

                    Realm realm = Realm.getDefaultInstance();
                    RealmResults<RealmRoomMessage> realmRoomMessages;
                    Sort sort;
                    ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction directionEnum;
                    if (directionString.equals(UP.toString())) {
                        startFutureMessageId = startFutureMessageIdUp = startMessageId;
                        directionEnum = UP;
                        sort = Sort.DESCENDING;
                        isWaitingForHistoryUp = false;
                    } else {
                        startFutureMessageId = startFutureMessageIdDown = endMessageId;
                        directionEnum = DOWN;
                        sort = Sort.ASCENDING;
                        isWaitingForHistoryDown = false;
                    }
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, startMessageId, endMessageId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, sort);
                    MessageLoader.sendMessageStatus(roomId, realmRoomMessages, chatType, ProtoGlobal.RoomMessageStatus.SEEN, realm);


                    if (realmRoomMessages.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtEmptyMessages.setVisibility(View.GONE);
                            }
                        });
                    }

                    /**
                     * when reached to gap and not jumped over local, set gapMessageIdUp = 0; do this action
                     * means that gap not exist (need this value for future get message) set topMore/bottomMore
                     * local after that gap reached true for allow that get message from
                     */
                    if (gapReached && !jumpOverLocal) {
                        if (directionEnum == UP) {
                            gapMessageIdUp = 0;
                            reachMessageIdUp = 0;
                            topMore = true;
                        } else {
                            gapMessageIdDown = 0;
                            reachMessageIdDown = 0;
                            bottomMore = true;
                        }

                        gapDetection(realmRoomMessages, directionEnum);
                    } else if ((directionEnum == UP && isReachedToTopView()) || directionEnum == DOWN && isReachedToBottomView()) {
                        /**
                         * check this state because if user is near to top view and not scroll get top message from server
                         */
                        //getOnlineMessage(startFutureMessageId, directionEnum);
                    }

                    final ArrayList<StructMessageInfo> structMessageInfos = new ArrayList<>();
                    for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                        structMessageInfos.add(StructMessageInfo.convert(realmRoomMessage));
                    }
                    if (directionString.equals(UP.toString())) {
                        switchAddItem(structMessageInfos, true);
                    } else {
                        switchAddItem(structMessageInfos, false);
                    }

                    realm.close();
                }

                @Override
                public void onError(int majorCode, int minorCode, long messageIdGetHistory, String direction) {
                    hideProgress();
                    /**
                     * hide progress if have any error
                     */
                    progressItem(HIDE, direction);

                    if (majorCode == 617) {

                        if (!isWaitingForHistoryUp && !isWaitingForHistoryDown && mAdapter.getItemCount() == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtEmptyMessages.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        if (direction.equals(UP.toString())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    //recyclerView.removeOnScrollListener(scrollListener); // remove check for up and down
                                }
                            });
                            isWaitingForHistoryUp = false;
                            isWaitingForHistoryUp = false;
                            allowGetHistoryUp = false;
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
                        if (direction.equals(UP.toString())) {
                            getOnlineMessage(messageIdGetHistory, UP);
                        } else {
                            getOnlineMessage(messageIdGetHistory, DOWN);
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
            Object[] objects = MessageLoader.gapExist(mRoomId, results.first().getMessageId(), direction);
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
            //int position = mAdapter.findPositionByMessageId(realmRoomMessage.getMessageId());
            //if (position != -1) {
            RealmRoomMessage unreadMessage = new RealmRoomMessage();
            unreadMessage.setMessageId(TimeUtils.currentLocalTime());
            // -1 means time message
            unreadMessage.setUserId(-1);
            unreadMessage.setMessage(unreadMessageCount + " " + getString(R.string.unread_message));
            unreadMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
            mAdapter.add(0, new UnreadMessage(ActivityChat.this).setMessage(StructMessageInfo.convert(unreadMessage)).withIdentifier(SUID.id().get()));
            //}
        }
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
     * @param direction define direction for show progress in UP or DOWN
     */
    private void progressItem(final ProgressState progressState, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int progressIndex = 0;
                if (direction == DOWN) {
                    // direction down not tested yet
                    progressIndex = mAdapter.getAdapterItemCount() - 1;
                }
                if (progressState == SHOW) {
                    if ((mAdapter.getAdapterItemCount() > 0) && !(mAdapter.getAdapterItem(progressIndex) instanceof ProgressWaiting)) {
                        //final int index = progressIndex;
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (direction == DOWN) {
                                    mAdapter.add(new ProgressWaiting(ActivityChat.this).withIdentifier(SUID.id().get()));
                                } else {
                                    mAdapter.add(0, new ProgressWaiting(ActivityChat.this).withIdentifier(SUID.id().get()));
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
                        //mAdapter.remove(progressIndex);
                        if (direction == DOWN) {
                            int count = 0;
                            if (mAdapter.getItemCount() > 10) {
                                count = mAdapter.getItemCount() - 10;
                            }
                            for (int i = (mAdapter.getItemCount() - 1); i >= count; i--) {
                                if ((mAdapter.getAdapterItem(i) instanceof ProgressWaiting)) {
                                    mAdapter.remove(i);
                                    break;
                                }
                            }
                        } else {
                            int count = 10;
                            if (mAdapter.getItemCount() < 10) {
                                count = mAdapter.getItemCount();
                            }
                            for (int i = 0; i < count; i++) {
                                if ((mAdapter.getAdapterItem(i) instanceof ProgressWaiting)) {
                                    mAdapter.remove(i);
                                    break;
                                }
                            }
                        }
                    } else {
                        //final int index = progressIndex;
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (direction == DOWN) {
                                    int count = 0;
                                    if (mAdapter.getItemCount() > 10) {
                                        count = mAdapter.getItemCount() - 10;
                                    }
                                    for (int i = (mAdapter.getItemCount() - 1); i >= count; i--) {
                                        if ((mAdapter.getAdapterItem(i) instanceof ProgressWaiting)) {
                                            mAdapter.remove(i);
                                            break;
                                        }
                                    }
                                } else {
                                    int count = 10;
                                    if (mAdapter.getItemCount() < 10) {
                                        count = mAdapter.getItemCount();
                                    }
                                    for (int i = 0; i < count; i++) {
                                        if ((mAdapter.getAdapterItem(i) instanceof ProgressWaiting)) {
                                            mAdapter.remove(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        }, 500);
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

}

