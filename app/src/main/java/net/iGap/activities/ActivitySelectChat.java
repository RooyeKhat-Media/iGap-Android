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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wang.avi.AVLoadingIndicatorView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.emoji.EmojiTextView;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnClientGetRoomListResponse;
import net.iGap.interfaces.OnComplete;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.enums.RoomType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestClientGetRoomList;

import static android.view.View.GONE;
import static net.iGap.G.context;
import static net.iGap.G.userId;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.realm.RealmRoom.putChatToDatabase;

public class ActivitySelectChat extends ActivityEnhanced {

    public static final String ARG_FORWARD_MESSAGE = "arg_forward_msg";
    public static final String ARG_FORWARD_MESSAGE_COUNT = "arg_forward_msg_count";
    private RealmRecyclerView mRecyclerView;
    private Realm mRealm;
    private RoomAdapter roomAdapter;
    private ArrayList<Parcelable> mForwardMessages;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Typeface titleTypeface;

    ProgressBar progressBar;

    private int mOffset = 0;
    private int mLimit = 20;
    private RecyclerView.OnScrollListener onScrollListener;
    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = true;


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

        progressBar = (ProgressBar) findViewById(R.id.ac_progress_bar_waiting);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);

        mForwardMessages = getIntent().getExtras().getParcelableArrayList(ARG_FORWARD_MESSAGE);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        findViewById(R.id.loadingContent).setVisibility(View.GONE);

        findViewById(R.id.amr_ripple_menu).setVisibility(View.GONE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        initRecycleView();
        initComponent();
    }

    private void initComponent() {
        //MaterialDesignTextView btnMenu = (MaterialDesignTextView) findViewById(R.id.cl_btn_menu);

        findViewById(R.id.ac_arc_button_add).setVisibility(GONE);

        findViewById(R.id.appBarLayout).setBackgroundColor(Color.parseColor(G.appBarColor));

        MaterialDesignTextView btnSearch = (MaterialDesignTextView) findViewById(R.id.amr_btn_search);
        btnSearch.setVisibility(View.GONE);

        TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);

        if (!HelperCalander.isLanguagePersian) {
            titleTypeface = Typeface.createFromAsset(getAssets(), "fonts/neuropolitical.ttf");
        } else {
            titleTypeface = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf");
        }

        txtIgap.setTypeface(titleTypeface, Typeface.BOLD);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        G.onClientGetRoomListResponse = new OnClientGetRoomListResponse() {
            @Override
            public void onClientGetRoomList(final List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, boolean fromLogin) {

                if (fromLogin) {
                    mOffset = 0;
                }

                boolean deleteBefore = false;
                if (mOffset == 0) {
                    deleteBefore = true;
                }

                if (roomList.size() > 0) {
                    putChatToDatabase(roomList, deleteBefore, false);
                    isThereAnyMoreItemToLoad = true;
                } else {
                    putChatToDatabase(roomList, deleteBefore, true);
                    isThereAnyMoreItemToLoad = false;
                }

                mOffset += roomList.size();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);// swipe refresh is complete and gone
                    }
                });

                isSendRequestForLoading = false;
            }

            @Override
            public void onTimeout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
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
    }

    private void initRecycleView() {

        mRecyclerView = (RealmRecyclerView) findViewById(R.id.cl_recycler_view_contact);

        mRecyclerView.setItemViewCacheSize(100);
        mRecyclerView.setDrawingCacheEnabled(true);

        RealmResults<RealmRoom> results = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAllSorted(RealmRoomFields.UPDATED_TIME, Sort.DESCENDING);
        roomAdapter = new RoomAdapter(ActivitySelectChat.this, results, null);
        mRecyclerView.setAdapter(roomAdapter);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
        params.setBehavior(new ShouldScrolledBehavior((LinearLayoutManager) mRecyclerView.getRecycleView().getLayoutManager(), roomAdapter));
        mRecyclerView.getRecycleView().setLayoutParams(params);

        onScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (lastVisiblePosition + 10 >= mOffset) {
                            isSendRequestForLoading = true;

                            //  mOffset = mRecyclerView.getRecycleView().getAdapter().getItemCount();
                            new RequestClientGetRoomList().clientGetRoomList(mOffset, mLimit);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                ActivityMain.curentMainRoomListPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        };

        mRecyclerView.getRecycleView().addOnScrollListener(onScrollListener);

    }

    public class ShouldScrolledBehavior extends AppBarLayout.ScrollingViewBehavior {
        private LinearLayoutManager mLayoutManager;
        private ActivitySelectChat.RoomAdapter mAdapter;

        public ShouldScrolledBehavior(LinearLayoutManager layoutManager, ActivitySelectChat.RoomAdapter adapter) {
            super();
            this.mLayoutManager = layoutManager;
            this.mAdapter = adapter;
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
            return shouldScrolled();
        }

        public boolean shouldScrolled() {
            // adapter has more items that not shown yet
            if (mLayoutManager.findLastCompletelyVisibleItemPosition() != mAdapter.getItemCount() - 1) {
                return true;
            }
            // last completely visible item is the last item in adapter but it may be occurred in 2 ways:
            // 1) all items are shown
            // 2) scrolled to the last item (implemented following)
            else if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.getItemCount() - 1 && mLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                return true;
            }
            return false;
        }
    }

    public class RoomAdapter extends RealmBasedRecyclerViewAdapter<RealmRoom, ActivitySelectChat.RoomAdapter.ViewHolder> {

        public OnComplete mComplete;
        public String action;
        private Typeface typeFaceIcon;

        public RoomAdapter(Context context, RealmResults<RealmRoom> realmResults, OnComplete complete) {
            super(context, realmResults, true, false, false, "");
            this.mComplete = complete;
        }

        @Override
        public ActivitySelectChat.RoomAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.chat_sub_layout, viewGroup, false);
            return new ActivitySelectChat.RoomAdapter.ViewHolder(v);
        }

        @Override
        public void onBindRealmViewHolder(final ActivitySelectChat.RoomAdapter.ViewHolder holder, int i) {

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
                                RealmResults<RealmRoomMessage> results = realm1.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mInfo.getId()).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                                if (!results.isEmpty()) {
                                    RealmRoomMessage realmRoomMessage = results.first();
                                    if (realmRoomMessage != null) {
                                        RealmRegisteredInfo realmRegisteredInfo = realm1.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, realmRoomMessage.getUserId()).findFirst();
                                        if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName() != null) {
                                            if (Character.getDirectionality(realmRegisteredInfo.getDisplayName().charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
                                                if (HelperCalander.isLanguagePersian) {
                                                    lastMessageSender = realmRegisteredInfo.getDisplayName() + ": ";
                                                } else {
                                                    lastMessageSender = " :" + realmRegisteredInfo.getDisplayName();
                                                }
                                            } else {
                                                if (HelperCalander.isLanguagePersian) {
                                                    lastMessageSender = " :" + realmRegisteredInfo.getDisplayName();
                                                } else {
                                                    lastMessageSender = realmRegisteredInfo.getDisplayName() + ": ";
                                                }
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

                HelperAvatar.getAvatar(idForGetAvatar, avatarType, new OnAvatarGet() {
                    @Override
                    public void onAvatarGet(String avatarPath, long roomId) {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
                    }

                    @Override
                    public void onShowInitials(String initials, String color) {
                        holder.image.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });


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

            /**
             * for change english number to persian number
             */
            if (HelperCalander.isLanguagePersian) {
                holder.lastMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.lastMessage.getText().toString()));
                holder.name.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.name.getText().toString()));
                holder.unreadMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.unreadMessage.getText().toString()));
            }
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

                        if (!realmResults.get(getPosition()).getReadOnly()) {
                            Intent intent = new Intent(ActivitySelectChat.this, ActivityChat.class);
                            intent.putExtra("RoomId", realmResults.get(getPosition()).getId());
                            intent.putParcelableArrayListExtra(ARG_FORWARD_MESSAGE, mForwardMessages);
                            intent.putExtra(ARG_FORWARD_MESSAGE_COUNT, mForwardMessages.size());
                            startActivity(intent);
                            finish();
                        } else {
                            new MaterialDialog.Builder(ActivitySelectChat.this).title(R.string.dialog_readonly_chat).positiveText(R.string.ok).show();
                        }
                    }
                });
            }
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
