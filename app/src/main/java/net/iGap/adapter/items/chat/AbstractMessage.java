/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.CallSuper;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lalongooo.videocompressor.video.MediaController;
import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperGetMessageState;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.IChatItemAttachment;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnProgressUpdate;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnMessageProgressClick;
import net.iGap.messageprogress.OnProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyType;
import net.iGap.module.ReserveSpaceGifImageView;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.enums.LocalFileType;
import net.iGap.module.enums.SendingStep;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmChannelExtraFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestChannelAddMessageReaction;

import java.util.List;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.fragments.FragmentChat.getRealmChat;
import static net.iGap.helper.HelperCalander.convertToUnicodeFarsiNumber;

public abstract class AbstractMessage<Item extends AbstractMessage<?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> implements IChatItemAttachment<VH> {//IChatItemAvatar
    public IMessageItem messageClickListener;
    public StructMessageInfo mMessage;
    public boolean directionalBased = true;
    public View messageView;
    private int minWith = 0;
    //protected Realm realmChat;

    public static ArrayMap<Long, String> updateForwardInfo = new ArrayMap<>();// after get user info or room info if need update view in chat activity

    public ProtoGlobal.Room.Type type;

    protected ProtoGlobal.Room.Type getRoomType() {
        return type;
    }

    @Override
    public void onPlayPauseGIF(VH holder, String localPath) throws ClassCastException {
        // empty
    }

    /**
     * add this prt for video player
     */
    //@Override public void onPlayPauseVideo(VH holder, String localPath, int isHide, double time) {
    //    // empty
    //}
    public AbstractMessage(Realm realmChat, boolean directionalBased, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        //this.realmChat = realmChat;
        this.directionalBased = directionalBased;
        this.type = type;
        this.messageClickListener = messageClickListener;
    }

    protected void setTextIfNeeded(TextView view, String msg) {

        if (!TextUtils.isEmpty(msg)) {
            if (mMessage.hasLinkInMessage) {
                view.setText(HelperUrl.getLinkText(msg, mMessage.linkInfo, mMessage.messageID));
            } else {

                msg = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(msg) : msg;

                view.setText(msg);
            }

            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    protected void setTextIfNeeded(EmojiTextViewE view, String msg) {

        if (!TextUtils.isEmpty(msg)) {
            if (mMessage.hasLinkInMessage) {
                view.setText(HelperUrl.getLinkText(msg, mMessage.linkInfo, mMessage.messageID));
            } else {
                msg = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(msg) : msg;
                view.setText(msg);
            }

            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public AbstractMessage setMessage(StructMessageInfo message) {
        this.mMessage = message;
        return this;
    }

    @Override
    public Item withIdentifier(long identifier) {
        return super.withIdentifier(identifier);
    }

    private void addLayoutTime(VH holder) {

        LinearLayout ll_containerTime = (LinearLayout) holder.itemView.findViewById(R.id.contentContainer).getParent();

        if (holder.itemView.findViewById(R.id.csl_ll_time) != null) {
            ll_containerTime.removeView(holder.itemView.findViewById(R.id.csl_ll_time));
        }

        ll_containerTime.addView(ViewMaker.getViewTime(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    @CallSuper
    public void bindView(final VH holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        if (holder instanceof ProgressWaiting.ViewHolder || holder instanceof UnreadMessage.ViewHolder || holder instanceof LogItem.ViewHolder || holder instanceof TimeItem.ViewHolder) {
            return;
        }

        addLayoutTime(holder);

        // remove text view if exist in view
        LinearLayout layoutMessageContainer = (LinearLayout) holder.itemView.findViewById(R.id.csliwt_layout_container_message);
        if (layoutMessageContainer != null) {
            layoutMessageContainer.removeAllViews();
        }

        if (holder instanceof TextItem.ViewHolder || holder instanceof ImageWithTextItem.ViewHolder || holder instanceof AudioItem.ViewHolder || holder instanceof FileItem.ViewHolder || holder instanceof VideoWithTextItem.ViewHolder || holder instanceof GifWithTextItem.ViewHolder) {
            int maxsize = 0;

            if ((type == ProtoGlobal.Room.Type.CHANNEL) || (type == ProtoGlobal.Room.Type.CHAT) && mMessage.forwardedFrom != null) {
                maxsize = G.maxChatBox;
            }

            messageView = ViewMaker.makeTextViewMessage(maxsize, mMessage.hasEmojiInText);
            layoutMessageContainer.addView(messageView);
        }

        /**
         * for return message that start showing to view
         */
        messageClickListener.onItemShowingMessageId(mMessage);

        /**
         * this use for select foreground in activity chat for search item and hash item
         *
         */

        /**
         * noinspection RedundantCast
         */

        if (isSelected() || mMessage.isSelected) {
            ((FrameLayout) holder.itemView).setForeground(new ColorDrawable(G.context.getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
        } else {
            ((FrameLayout) holder.itemView).setForeground(new ColorDrawable(Color.TRANSPARENT));
        }

        /**
         * only will be called when message layout is directional-base (e.g. single chat)
         */
        if (directionalBased) {
            if ((mMessage.sendType == MyType.SendType.recvive) || type == ProtoGlobal.Room.Type.CHANNEL) {
                updateLayoutForReceive(holder);
            } else if (mMessage.sendType == MyType.SendType.send) {
                updateLayoutForSend(holder);
            }
        }

        if (!mMessage.isTimeOrLogMessage()) {
            RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.roomId).findFirst();
            /**
             * check failed state ,because if is failed we want show to user even is in channel
             */
            if (realmRoom != null && realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && ProtoGlobal.RoomMessageStatus.FAILED != ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status)) {
                ((ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic)).setVisibility(View.GONE);
            } else {
                ((ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic)).setVisibility(View.VISIBLE);
                AppUtils.rightMessageStatus((ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic), ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status), mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType, mMessage.isSenderMe());
            }
        }
        /**
         * display 'edited' indicator beside message time if message was edited
         */
        if (holder.itemView.findViewById(R.id.txtEditedIndicator) != null) {
            if (mMessage.isEdited) {
                holder.itemView.findViewById(R.id.txtEditedIndicator).setVisibility(View.VISIBLE);
            } else {
                holder.itemView.findViewById(R.id.txtEditedIndicator).setVisibility(View.GONE);
            }
        }
        /**
         * display user avatar only if chat type is GROUP
         */
        if (holder.itemView.findViewById(R.id.messageSenderAvatar) != null) {
            holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.GONE);
        }

        replyMessageIfNeeded(holder, getRealmChat());
        forwardMessageIfNeeded(holder, getRealmChat());

        if (holder.itemView.findViewById(R.id.messageSenderName) != null) {
            holder.itemView.findViewById(R.id.messageSenderName).setVisibility(View.GONE);
        }

        if (type == ProtoGlobal.Room.Type.GROUP) {
            if (!mMessage.isSenderMe()) {

                LinearLayout mainContainer = (LinearLayout) holder.itemView.findViewById(R.id.mainContainer);
                if (mainContainer != null) {

                    addSenderNameToGroupIfNeed(holder.itemView, getRealmChat());

                    if (holder.itemView.findViewById(R.id.messageSenderAvatar) == null) {
                        mainContainer.addView(ViewMaker.makeCircleImageView(), 0);
                    }

                    holder.itemView.findViewById(R.id.messageSenderAvatar).setVisibility(View.VISIBLE);

                    holder.itemView.findViewById(R.id.messageSenderAvatar).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            messageClickListener.onSenderAvatarClick(v, mMessage, holder.getAdapterPosition());
                        }
                    });

                    //  String[] initialize =
                    HelperAvatar.getAvatar(null, Long.parseLong(mMessage.senderID), HelperAvatar.AvatarType.USER, false, getRealmChat(), new OnAvatarGet() {
                        @Override
                        public void onAvatarGet(final String avatarPath, long ownerId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), (ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar));
                                }
                            });
                        }

                        @Override
                        public void onShowInitials(final String initials, final String color) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar)).setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                                }
                            });
                        }
                    });
                    //if (initialize != null && initialize[0] != null && initialize[1] != null) {
                    //    ((ImageView) holder.itemView.findViewById(R.id.messageSenderAvatar)).setImageBitmap(
                    //        net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initialize[0], initialize[1]));
                    //}
                }
            }
        }
        /**
         * set message time
         */

        TextView txtTime = (TextView) holder.itemView.findViewById(R.id.cslr_txt_time);
        if (txtTime != null) {
            txtTime.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
            txtTime.setText(HelperCalander.getClocktime(mMessage.time, false));

            if (HelperCalander.isPersianUnicode) {
                txtTime.setText(HelperCalander.convertToUnicodeFarsiNumber(txtTime.getText().toString()));
            }
        }

        RealmRoomMessage roomMessage = RealmRoomMessage.getFinalMessage(getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID)).findFirst());
        if (roomMessage != null) {
            prepareAttachmentIfNeeded(holder, roomMessage.getAttachment(), mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType);
        }

        TextView messageText = (TextView) holder.itemView.findViewById(R.id.messageSenderTextMessage);
        if (messageText != null) {
            if (messageText.getParent() instanceof LinearLayout) {
                ((LinearLayout.LayoutParams) ((LinearLayout) messageText.getParent()).getLayoutParams()).gravity = AndroidUtils.isTextRtl(mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessage() : mMessage.messageText) ? Gravity.RIGHT : Gravity.LEFT;
            }
        }


        /**
         * show vote layout for channel otherwise hide layout also get message state for channel
         */

        if (holder.itemView.findViewById(R.id.lyt_vote) != null) {
            holder.itemView.findViewById(R.id.lyt_vote).setVisibility(View.GONE);
        }

        if (holder.itemView.findViewById(R.id.lyt_see) != null) {
            holder.itemView.findViewById(R.id.lyt_see).setVisibility(View.GONE);
        }

        if (G.showVoteChannelLayout) {

            if ((type == ProtoGlobal.Room.Type.CHANNEL)) {
                showVote(holder, getRealmChat());
            } else if ((type == ProtoGlobal.Room.Type.CHAT)) {
                if (mMessage.forwardedFrom != null) {
                    if (mMessage.forwardedFrom.getAuthorRoomId() > 0) {
                        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getAuthorRoomId()).findFirst();
                        if (realmRoom != null && realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                            showVote(holder, getRealmChat());

                            if (mMessage.isSenderMe()) {
                                holder.itemView.findViewById(R.id.cslm_view_left_dis).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        } else if ((type == ProtoGlobal.Room.Type.CHANNEL)) {

            getChannelMessageState();

            // add layout seen in channel
            if (holder.itemView.findViewById(R.id.lyt_see) == null) {
                LinearLayout ll_time_layout = (LinearLayout) holder.itemView.findViewById(R.id.csl_ll_time);
                ll_time_layout.addView(ViewMaker.getViewSeen(), 0);
            }

            TextView txtViewsLabel = (TextView) holder.itemView.findViewById(R.id.txt_views_label);

            if ((mMessage.forwardedFrom != null)) {

                ProtoGlobal.Room.Type roomType = null;
                RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getAuthorRoomId()).findFirst();
                if (realmRoom != null) {
                    roomType = realmRoom.getType();
                }

                if (roomType != null && roomType == ProtoGlobal.Room.Type.CHANNEL) {
                    long messageId = mMessage.forwardedFrom.getMessageId();
                    if (mMessage.forwardedFrom.getMessageId() < 0) {
                        messageId = messageId * (-1);
                    }
                    RealmChannelExtra realmChannelExtra = getRealmChat().where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, messageId).findFirst();
                    if (realmChannelExtra != null) {
                        txtViewsLabel.setText(realmChannelExtra.getViewsLabel());
                    }
                } else {
                    txtViewsLabel.setText(mMessage.channelExtra.viewsLabel);
                }
            } else {
                txtViewsLabel.setText(mMessage.channelExtra.viewsLabel);
            }

            if (HelperCalander.isPersianUnicode) {
                txtViewsLabel.setText(HelperCalander.convertToUnicodeFarsiNumber(txtViewsLabel.getText().toString()));
            }
        }
    }

    /**
     * show vote views
     */
    private void showVote(VH holder, Realm realm) {
        // add layout seen in channel
        if (holder.itemView.findViewById(R.id.lyt_see) == null) {
            LinearLayout ll_time_layout = (LinearLayout) holder.itemView.findViewById(R.id.csl_ll_time);
            ll_time_layout.addView(ViewMaker.getViewSeen(), 0);
        }

        voteAction(holder, getRealmChat());
        getChannelMessageState();
    }

    /**
     * get channel message state, for clear unread message in channel client
     * need send request for getMessageState even show vote layout is hide
     */
    private void getChannelMessageState() {
        if ((mMessage.forwardedFrom != null)) {
            ProtoGlobal.Room.Type roomType = null;
            RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getAuthorRoomId()).findFirst();
            if (realmRoom != null) {
                roomType = realmRoom.getType();
            }
            if ((mMessage.forwardedFrom != null) && (roomType == ProtoGlobal.Room.Type.CHANNEL)) {
                /**
                 * if roomType is Channel don't consider forward
                 *
                 * when i add message to RealmRoomMessage(putOrUpdate) set (replyMessageId * (-1))
                 * so i need to (replyMessageId * (-1)) again for use this messageId
                 */
                long messageId = mMessage.forwardedFrom.getMessageId();
                if (mMessage.forwardedFrom.getMessageId() < 0) {
                    messageId = messageId * (-1);
                }
                HelperGetMessageState.getMessageState(mMessage.forwardedFrom.getAuthorRoomId(), messageId);
            } else {
                HelperGetMessageState.getMessageState(mMessage.roomId, Long.parseLong(mMessage.messageID));
            }
        } else {
            HelperGetMessageState.getMessageState(mMessage.roomId, Long.parseLong(mMessage.messageID));
        }
    }

    private void addSenderNameToGroupIfNeed(final View view, Realm realm) {

        if (G.showSenderNameInGroup) {
            final LinearLayout mContainer = (LinearLayout) view.findViewById(R.id.m_container);
            if (mContainer != null) {

                if (view.findViewById(R.id.messageSenderName) != null) {
                    mContainer.removeView(view.findViewById(R.id.messageSenderName));
                }

                if (view.findViewById(R.id.messageSenderName) == null) {
                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), Long.parseLong(mMessage.senderID));
                    if (realmRegisteredInfo != null) {
                        final EmojiTextViewE _tv = (EmojiTextViewE) ViewMaker.makeHeaderTextView(realmRegisteredInfo.getDisplayName());

                        //_tv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        //    @Override
                        //    public void onGlobalLayout() {
                        //        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        //            _tv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        //        } else {
                        //            _tv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        //        }
                        //
                        //        if (_tv.getWidth() < mContainer.getWidth()) {
                        //            _tv.setWidth(mContainer.getWidth());
                        //        }
                        //    }
                        //});

                        _tv.measure(0, 0);       //must call measure!
                        int maxWith = 0;
                        maxWith = _tv.getMeasuredWidth() + ViewMaker.i_Dp(R.dimen.dp40);

                        if (minWith < maxWith) {
                            minWith = maxWith;
                        }
                        mContainer.setMinimumWidth(minWith);
                        mContainer.addView(_tv, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                }
            }
        }
    }

    protected void voteAction(VH holder, Realm realm) {

        LinearLayout mainContainer = (LinearLayout) holder.itemView.findViewById(R.id.mainContainer);
        if (mainContainer == null) {
            return;
        }

        if (holder.itemView.findViewById(R.id.lyt_vote) == null) {
            //   View voteView = LayoutInflater.from(G.context).inflate(R.layout.chat_sub_layout_messages_vote, null);

            View voteView = ViewMaker.getViewVote();

            mainContainer.addView(voteView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            voteView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        LinearLayout lytContainer = (LinearLayout) holder.itemView.findViewById(R.id.m_container);
        lytContainer.setMinimumWidth((int) G.context.getResources().getDimension(R.dimen.dp160));
        lytContainer.setMinimumHeight((int) G.context.getResources().getDimension(R.dimen.dp100));

        LinearLayout lytVote = (LinearLayout) holder.itemView.findViewById(R.id.lyt_vote);
        if (lytVote != null) {

            LinearLayout lytVoteUp = (LinearLayout) holder.itemView.findViewById(R.id.lyt_vote_up);
            LinearLayout lytVoteDown = (LinearLayout) holder.itemView.findViewById(R.id.lyt_vote_down);
            TextView txtVoteUp = (TextView) holder.itemView.findViewById(R.id.txt_vote_up);
            TextView txtVoteDown = (TextView) holder.itemView.findViewById(R.id.txt_vote_down);
            TextView txtViewsLabel = (TextView) holder.itemView.findViewById(R.id.txt_views_label);
            TextView txtSignature = (TextView) holder.itemView.findViewById(R.id.txt_signature);

            lytVote.setVisibility(View.VISIBLE);

            /**
             * userId != 0 means that this message is from channel
             * because for chat and group userId will be set
             */

            if ((mMessage.forwardedFrom != null)) {

                ProtoGlobal.Room.Type roomType = null;
                RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getAuthorRoomId()).findFirst();
                if (realmRoom != null) {
                    roomType = realmRoom.getType();
                }

                if (roomType != null && roomType == ProtoGlobal.Room.Type.CHANNEL) {
                    long messageId = mMessage.forwardedFrom.getMessageId();
                    if (mMessage.forwardedFrom.getMessageId() < 0) {
                        messageId = messageId * (-1);
                    }
                    RealmChannelExtra realmChannelExtra = getRealmChat().where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, messageId).findFirst();
                    if (realmChannelExtra != null) {
                        txtVoteUp.setText(realmChannelExtra.getThumbsUp());
                        txtVoteDown.setText(realmChannelExtra.getThumbsDown());
                        txtViewsLabel.setText(realmChannelExtra.getViewsLabel());
                        txtSignature.setText(realmChannelExtra.getSignature());
                    }
                } else {
                    txtVoteUp.setText(mMessage.channelExtra.thumbsUp);
                    txtVoteDown.setText(mMessage.channelExtra.thumbsDown);
                    txtViewsLabel.setText(mMessage.channelExtra.viewsLabel);
                    txtSignature.setText(mMessage.channelExtra.signature);
                }
            } else {
                txtVoteUp.setText(mMessage.channelExtra.thumbsUp);
                txtVoteDown.setText(mMessage.channelExtra.thumbsDown);
                txtViewsLabel.setText(mMessage.channelExtra.viewsLabel);
                txtSignature.setText(mMessage.channelExtra.signature);
            }

            if (txtSignature.getText().length() > 0) {
                holder.itemView.findViewById(R.id.lyt_signature).setVisibility(View.VISIBLE);
            }

            if (HelperCalander.isPersianUnicode) {
                txtViewsLabel.setText(HelperCalander.convertToUnicodeFarsiNumber(txtViewsLabel.getText().toString()));
                txtVoteDown.setText(HelperCalander.convertToUnicodeFarsiNumber(txtVoteDown.getText().toString()));
                txtVoteUp.setText(HelperCalander.convertToUnicodeFarsiNumber(txtVoteUp.getText().toString()));
            }

            lytVoteUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    voteSend(ProtoGlobal.RoomMessageReaction.THUMBS_UP);
                }
            });

            lytVoteDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    voteSend(ProtoGlobal.RoomMessageReaction.THUMBS_DOWN);
                }
            });
        }
    }

    /**
     * send vote action to RealmRoomMessage
     *
     * @param reaction Up or Down
     */
    private void voteSend(final ProtoGlobal.RoomMessageReaction reaction) {

        getRealmChat().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID)).findFirst();
                if (realmRoomMessage != null) {
                    /**
                     * userId != 0 means that this message is from channel
                     * because for chat and group userId will be set
                     */

                    if ((mMessage.forwardedFrom != null)) {
                        ProtoGlobal.Room.Type roomType = null;
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getAuthorRoomId()).findFirst();
                        if (realmRoom != null) {
                            roomType = realmRoom.getType();
                        }
                        if ((roomType == ProtoGlobal.Room.Type.CHANNEL)) {
                            long forwardMessageId = mMessage.forwardedFrom.getMessageId();
                            /**
                             * check with this number for detect is multiply now or no
                             * hint : use another solution
                             */
                            if (mMessage.forwardedFrom.getMessageId() < 0) {
                                forwardMessageId = forwardMessageId * (-1);
                            }
                            new RequestChannelAddMessageReaction().channelAddMessageReactionForward(mMessage.forwardedFrom.getAuthorRoomId(), Long.parseLong(mMessage.messageID), reaction, forwardMessageId);
                        } else {
                            new RequestChannelAddMessageReaction().channelAddMessageReaction(mMessage.roomId, Long.parseLong(mMessage.messageID), reaction);
                        }
                    } else {
                        new RequestChannelAddMessageReaction().channelAddMessageReaction(mMessage.roomId, Long.parseLong(mMessage.messageID), reaction);
                    }
                }
            }
        });
    }

    @CallSuper
    protected void updateLayoutForReceive(VH holder) {
        ViewGroup frameLayout = (ViewGroup) holder.itemView.findViewById(R.id.mainContainer);
        ImageView imgTick = (ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic);
        TextView messageText = (TextView) holder.itemView.findViewById(R.id.messageSenderTextMessage);

        if (messageText != null) {
            messageText.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        }
        //   ProtoGlobal.RoomMessageType messageType = mMessage.forwardedFrom == null ? mMessage.messageType : mMessage.forwardedFrom.getMessageType();

        setTextColor(imgTick, R.color.colorOldBlack);

        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).gravity = Gravity.LEFT;

        ((LinearLayout.LayoutParams) holder.itemView.findViewById(R.id.contentContainer).getLayoutParams()).gravity = Gravity.LEFT;

        (holder.itemView.findViewById(R.id.contentContainer)).setBackgroundResource(R.drawable.rectangel_white_round);

        /**
         * add main layout margin to prevent getting match parent completely
         * set to mainContainer not itemView because of selecting item foreground
         */
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp10);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp28);
    }

    private void setTextColor(ImageView imageView, int color) {

        try {
            imageView.setColorFilter(ContextCompat.getColor(G.context, color));
        } catch (NullPointerException e) {
            // imageView.setColorFilter(color,android.graphics.PorterDuff.Mode.MULTIPLY);
            try {
                imageView.setColorFilter(G.context.getResources().getColor(color));
            } catch (Exception e1) {
            }
        }
    }

    @CallSuper
    protected void updateLayoutForSend(VH holder) {

        ViewGroup frameLayout = (ViewGroup) holder.itemView.findViewById(R.id.mainContainer);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).gravity = Gravity.RIGHT;

        ((LinearLayout.LayoutParams) holder.itemView.findViewById(R.id.contentContainer).getLayoutParams()).gravity = Gravity.RIGHT;

        LinearLayout timeLayout = (LinearLayout) holder.itemView.findViewById(R.id.contentContainer).getParent();
        timeLayout.setGravity(Gravity.RIGHT);

        ImageView imgTick = (ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic);
        TextView messageText = (TextView) holder.itemView.findViewById(R.id.messageSenderTextMessage);
        //  TextView iconHearing = (TextView) holder.itemView.findViewById(R.id.cslr_txt_hearing);

        if (messageText != null) {
            messageText.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        }
        //   ProtoGlobal.RoomMessageType messageType = mMessage.forwardedFrom == null ? mMessage.messageType : mMessage.forwardedFrom.getMessageType();

        if (ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status) == ProtoGlobal.RoomMessageStatus.SEEN) {
            setTextColor(imgTick, R.color.iGapColor);
        } else if (ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status) == ProtoGlobal.RoomMessageStatus.LISTENED) {
            // iconHearing.setVisibility(View.VISIBLE);
            setTextColor(imgTick, R.color.iGapColor);
            imgTick.setVisibility(View.VISIBLE);
        } else {
            setTextColor(imgTick, R.color.colorOldBlack);
        }

        (holder.itemView.findViewById(R.id.contentContainer)).setBackgroundResource(R.drawable.rectangle_send_round_color);
        /**
         * add main layout margin to prevent getting match parent completely
         * set to mainContainer not itemView because of selecting item foreground
         */
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).leftMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp28);
        ((FrameLayout.LayoutParams) frameLayout.getLayoutParams()).rightMargin = (int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp10);

        //((LinearLayout.LayoutParams) (holder.itemView.findViewById(R.id.contentContainer).getLayoutParams())).rightMargin = (int) holder.itemView.getResources().getDimension(R.dimen.messageBox_minusLeftRightMargin);
        //((LinearLayout.LayoutParams) (holder.itemView.findViewById(R.id.contentContainer).getLayoutParams())).leftMargin = 0;
    }

    @CallSuper
    protected void replyMessageIfNeeded(VH holder, Realm realm) {

        final LinearLayout mContainer = (LinearLayout) holder.itemView.findViewById(R.id.m_container);
        if (mContainer == null) {
            return;
        }

        mContainer.setMinimumWidth(0);

        /**
         * set replay container visible if message was replayed, otherwise, gone it
         */

        if (holder.itemView.findViewById(R.id.cslr_replay_layout) != null) {
            mContainer.removeView(holder.itemView.findViewById(R.id.cslr_replay_layout));
        }

        if (mMessage.replayTo != null && mMessage.replayTo.isValid()) {

            final View replayView = ViewMaker.getViewReplay();

            if (replayView != null) {

                final TextView replyFrom = (TextView) replayView.findViewById(R.id.chslr_txt_replay_from);
                final EmojiTextViewE replayMessage = (EmojiTextViewE) replayView.findViewById(R.id.chslr_txt_replay_message);
                replayView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageClickListener.onReplyClick(mMessage.replayTo);
                    }
                });

                try {
                    AppUtils.rightFileThumbnailIcon(((ImageView) replayView.findViewById(R.id.chslr_imv_replay_pic)), mMessage.replayTo.getForwardMessage() == null ? mMessage.replayTo.getMessageType() : mMessage.replayTo.getForwardMessage().getMessageType(), mMessage.replayTo.getForwardMessage() == null ? mMessage.replayTo : mMessage.replayTo.getForwardMessage());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                if (type == ProtoGlobal.Room.Type.CHANNEL) {
                    RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.replayTo.getRoomId()).findFirst();
                    if (realmRoom != null) {
                        replyFrom.setText(realmRoom.getTitle());
                    }
                } else {
                    RealmRegisteredInfo replayToInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), mMessage.replayTo.getUserId());
                    if (replayToInfo != null) {
                        replyFrom.setText(replayToInfo.getDisplayName());
                    }
                }

                String forwardMessage = AppUtils.replyTextMessage(mMessage.replayTo, holder.itemView.getResources());
                ((EmojiTextViewE) replayView.findViewById(R.id.chslr_txt_replay_message)).setText(forwardMessage);

                if (mMessage.isSenderMe() && type != ProtoGlobal.Room.Type.CHANNEL) {
                    replayView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.messageBox_replyBoxBackgroundSend));
                    //holder.itemView.findViewById(R.id.verticalLine).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.messageBox_sendColor));
                    replyFrom.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
                    replayMessage.setTextColor(holder.itemView.getResources().getColor(R.color.replay_message_text));
                } else {
                    replayView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.messageBox_replyBoxBackgroundReceive));
                    // holder.itemView.findViewById(R.id.verticalLine).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.messageBox_receiveColor));
                    replyFrom.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
                    replayMessage.setTextColor(holder.itemView.getResources().getColor(R.color.replay_message_text));
                }

                replyFrom.measure(0, 0);       //must call measure!
                replayMessage.measure(0, 0);

                int maxWith = 0, withMessage = 0, withTitle = 0;
                withTitle = replyFrom.getMeasuredWidth();
                withMessage = replayMessage.getMeasuredWidth();
                maxWith = withTitle > withMessage ? withTitle : withMessage;
                maxWith += ViewMaker.i_Dp(R.dimen.dp44);
                if (replayView.findViewById(R.id.chslr_imv_replay_pic).getVisibility() == View.VISIBLE) {
                    maxWith += ViewMaker.i_Dp(R.dimen.dp52);
                }

                minWith = maxWith;
                mContainer.setMinimumWidth(maxWith);

                mContainer.addView(replayView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                replayMessage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                replyFrom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @CallSuper
    protected void forwardMessageIfNeeded(VH holder, Realm realm) {

        final LinearLayout mContainer = (LinearLayout) holder.itemView.findViewById(R.id.m_container);
        if (mContainer == null) {
            return;
        }

        /**
         * set forward container visible if message was forwarded, otherwise, gone it
         */

        if (holder.itemView.findViewById(R.id.cslr_ll_forward) != null) {
            mContainer.removeView(holder.itemView.findViewById(R.id.cslr_ll_forward));
        }

        if (mMessage.forwardedFrom != null) {

            View forwardView = ViewMaker.getViewForward();
            forwardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mMessage.username.length() > 0) {
                        HelperUrl.checkUsernameAndGoToRoom(mMessage.username, HelperUrl.ChatEntry.profile);
                    }
                }
            });

            TextView txtPrefixForwardFrom = (TextView) forwardView.findViewById(R.id.cslr_txt_prefix_forward);
            txtPrefixForwardFrom.setTypeface(G.typeface_IRANSansMobile);
            TextView txtForwardFrom = (TextView) forwardView.findViewById(R.id.cslr_txt_forward_from);
            txtForwardFrom.setTypeface(G.typeface_IRANSansMobile);

            /**
             * if forward message from chat or group , sender is user
             * but if message forwarded from channel sender is room
             */
            RealmRegisteredInfo info = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), mMessage.forwardedFrom.getUserId());
            if (info != null) {

                if (RealmRegisteredInfo.needUpdateUser(info.getId(), info.getCacheId())) {
                    if (!updateForwardInfo.containsKey(info.getId())) {
                        updateForwardInfo.put(info.getId(), mMessage.messageID);
                    }
                }

                txtForwardFrom.setText(info.getDisplayName());
                mMessage.username = info.getUsername();
                if (mMessage.isSenderMe()) {
                    txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.iGapColor));
                } else {
                    txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.iGapColor));
                }
            } else if (mMessage.forwardedFrom.getUserId() != 0) {

                if (RealmRegisteredInfo.needUpdateUser(mMessage.forwardedFrom.getUserId(), null)) {
                    if (!updateForwardInfo.containsKey(mMessage.forwardedFrom.getUserId())) {
                        updateForwardInfo.put(mMessage.forwardedFrom.getUserId(), mMessage.messageID);
                    }
                }
            } else {
                RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getRoomId()).findFirst();
                if (realmRoom != null) {
                    txtForwardFrom.setText(realmRoom.getTitle());
                    if (mMessage.isSenderMe()) {
                        txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.colorOldBlack));
                    } else {
                        txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.iGapColor));
                    }

                    switch (realmRoom.getType()) {
                        case CHANNEL:
                            mMessage.username = realmRoom.getChannelRoom().getUsername();
                            break;
                        case GROUP:
                            mMessage.username = realmRoom.getGroupRoom().getUsername();
                            break;
                    }
                } else {
                    RealmRoom realmRoom1 = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mMessage.forwardedFrom.getAuthorRoomId()).findFirst();
                    if (realmRoom1 != null) {

                        switch (realmRoom1.getType()) {
                            case CHANNEL:
                                if (realmRoom1.getChannelRoom() != null && realmRoom1.getChannelRoom().getUsername() != null) {
                                    mMessage.username = realmRoom1.getChannelRoom().getUsername();
                                } else {
                                    mMessage.username = holder.itemView.getResources().getString(R.string.private_channel);
                                }

                                break;
                            case GROUP:
                                mMessage.username = realmRoom1.getGroupRoom().getUsername();
                                break;
                        }

                        if (RealmRoom.needUpdateRoomInfo(realmRoom1.getId())) {
                            if (!updateForwardInfo.containsKey(realmRoom1.getId())) {
                                updateForwardInfo.put(realmRoom1.getId(), mMessage.messageID);
                            }
                        }

                        txtForwardFrom.setText(realmRoom1.getTitle());
                        if (mMessage.isSenderMe()) {
                            txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.colorOldBlack));
                        } else {
                            txtForwardFrom.setTextColor(G.context.getResources().getColor(R.color.iGapColor));
                        }
                    } else {
                        if (RealmRoom.needUpdateRoomInfo(mMessage.forwardedFrom.getAuthorRoomId())) {
                            if (!updateForwardInfo.containsKey(mMessage.forwardedFrom.getAuthorRoomId())) {
                                updateForwardInfo.put(mMessage.forwardedFrom.getAuthorRoomId(), mMessage.messageID);
                            }
                        }
                    }
                }
            }

            txtPrefixForwardFrom.measure(0, 0);       //must call measure!
            txtForwardFrom.measure(0, 0);
            int maxWith = txtPrefixForwardFrom.getMeasuredWidth() + txtForwardFrom.getMeasuredWidth() + ViewMaker.i_Dp(R.dimen.dp32);

            if (minWith < maxWith) {
                minWith = maxWith;
            }
            mContainer.setMinimumWidth(minWith);
            mContainer.addView(forwardView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    public static void processVideo(final TextView duration, final View holder1, final StructMessageInfo mMessage) {

        MediaController.onPercentCompress = new MediaController.OnPercentCompress() {
            @Override
            public void compress(final long percent, String path) {

                if (mMessage.getAttachment().getLocalFilePath() == null || !mMessage.getAttachment().getLocalFilePath().equals(path)) {
                    return;
                }

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (percent < 98) {

                            String p = percent + "";

                            if (HelperCalander.isLanguagePersian || HelperCalander.isLanguageArabic) {
                                p = convertToUnicodeFarsiNumber(p);
                            }
                            duration.setText(String.format(holder1.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)), AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true) + " " + G.context.getResources().getString(R.string.compressing) + " %" + p));
                        } else {
                            duration.setText(String.format(holder1.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)), AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true) + " " + G.context.getResources().getString(R.string.Uploading)));
                        }
                    }
                });
            }
        };
    }

    /**
     * does item have progress view
     *
     * @param itemView View
     * @return true if item has a progress
     */
    private boolean hasProgress(View itemView) {

        MessageProgress _Progress = (MessageProgress) itemView.findViewById(R.id.progress);

        if (_Progress != null) {
            _Progress.setTag(mMessage.messageID);
            _Progress.setVisibility(View.GONE);
            itemView.findViewById(R.id.ch_progress_loadingContent).setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    private void setClickListener(SharedPreferences sharedPreferences, String key, final VH holder, final RealmAttachment attachment) {

        /**
         * if type was gif auto file start auto download
         */
        if (sharedPreferences.getInt(key, ((key.equals(SHP_SETTING.KEY_AD_DATA_GIF) || key.equals(SHP_SETTING.KEY_AD_WIFI_GIF)) ? 5 : -1)) != -1) {
            autoDownload(holder, attachment);
        } else {

            MessageProgress _Progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
            AppUtils.setProgresColor(_Progress.progressBar);

            _Progress.withOnMessageProgress(new OnMessageProgressClick() {
                @Override
                public void onMessageProgressClick(MessageProgress progress) {
                    forOnCLick(holder, attachment);
                }
            });
        }
    }

    private void checkAutoDownload(final VH holder, final RealmAttachment attachment, Context context, HelperCheckInternetConnection.ConnectivityType connectionMode) {

        if (HelperDownloadFile.manuallyStoppedDownload.contains(attachment.getCacheId())) { // for avoid from reDownload in autoDownload state , after that user manually stopped download.
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        ProtoGlobal.RoomMessageType messageType;
        if (mMessage.forwardedFrom != null) {
            messageType = mMessage.forwardedFrom.getMessageType();
        } else {
            messageType = mMessage.messageType;
        }
        switch (messageType) {
            case IMAGE:
            case IMAGE_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_PHOTO, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_PHOTO, holder, attachment);
                        break;
                }
                break;
            case VOICE:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, holder, attachment);
                        break;
                }
                break;
            case VIDEO:
            case VIDEO_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_VIDEO, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_VIDEO, holder, attachment);
                        break;
                }
                break;
            case FILE:
            case FILE_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_FILE, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_FILE, holder, attachment);
                        break;
                }
                break;
            case AUDIO:
            case AUDIO_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_MUSIC, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_MUSIC, holder, attachment);
                        break;
                }
                break;
            case GIF:
            case GIF_TEXT:
                switch (connectionMode) {
                    case MOBILE:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_DATA_GIF, holder, attachment);
                        break;
                    case WIFI:
                        setClickListener(sharedPreferences, SHP_SETTING.KEY_AD_WIFI_GIF, holder, attachment);
                        break;
                }
                break;
            default:

                MessageProgress _Progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
                AppUtils.setProgresColor(_Progress.progressBar);

                _Progress.withOnMessageProgress(new OnMessageProgressClick() {
                    @Override
                    public void onMessageProgressClick(MessageProgress progress) {
                        forOnCLick(holder, attachment);
                    }
                });
                break;
        }
    }

    private void prepareAttachmentIfNeeded(final VH holder, final RealmAttachment attachment, final ProtoGlobal.RoomMessageType messageType) {
        /**
         * runs if message has attachment
         */

        if (attachment != null) {

            if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT || messageType == ProtoGlobal.RoomMessageType.VIDEO || messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {

                ReserveSpaceRoundedImageView imageViewReservedSpace = (ReserveSpaceRoundedImageView) holder.itemView.findViewById(R.id.thumbnail);
                if (imageViewReservedSpace != null) {

                    int _with = attachment.getWidth();
                    int _hight = attachment.getHeight();

                    if (_with == 0) {
                        if (attachment.getSmallThumbnail() != null) {
                            _with = attachment.getSmallThumbnail().getWidth();
                            _hight = attachment.getSmallThumbnail().getHeight();
                        }
                    }

                    boolean setDefualtImage = false;

                    if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                        if (attachment.getLocalFilePath() == null && attachment.getLocalThumbnailPath() == null && _with == 0) {
                            _with = (int) G.context.getResources().getDimension(R.dimen.dp120);
                            _hight = (int) G.context.getResources().getDimension(R.dimen.dp120);
                            setDefualtImage = true;
                        }
                    } else {
                        if (attachment.getLocalThumbnailPath() == null && _with == 0) {
                            _with = (int) G.context.getResources().getDimension(R.dimen.dp120);
                            _hight = (int) G.context.getResources().getDimension(R.dimen.dp120);
                            setDefualtImage = true;
                        }
                    }

                    int[] dimens = imageViewReservedSpace.reserveSpace(_with, _hight, type);
                    if (dimens[0] != 0 && dimens[1] != 0) {
                        ((ViewGroup) holder.itemView.findViewById(R.id.m_container)).getLayoutParams().width = dimens[0];
                    }

                    if (setDefualtImage) {
                        imageViewReservedSpace.setImageResource(R.mipmap.difaultimage);
                    }
                }
            } else if (messageType == ProtoGlobal.RoomMessageType.GIF || messageType == ProtoGlobal.RoomMessageType.GIF_TEXT) {
                ReserveSpaceGifImageView imageViewReservedSpace = (ReserveSpaceGifImageView) holder.itemView.findViewById(R.id.thumbnail);
                if (imageViewReservedSpace != null) {

                    int _with = attachment.getWidth();
                    int _hight = attachment.getHeight();

                    if (_with == 0) {
                        _with = (int) G.context.getResources().getDimension(R.dimen.dp200);
                        _hight = (int) G.context.getResources().getDimension(R.dimen.dp200);
                    }

                    int[] dimens = imageViewReservedSpace.reserveSpace(_with, _hight, type);
                    ((ViewGroup) holder.itemView.findViewById(R.id.m_container)).getLayoutParams().width = dimens[0];
                }
            }

            /**
             * if file already exists, simply show the local one
             */
            if (attachment.isFileExistsOnLocalAndIsThumbnail()) {
                /**
                 * load file from local
                 */
                onLoadThumbnailFromLocal(holder, getCacheId(mMessage), attachment.getLocalFilePath(), LocalFileType.FILE);
            } else if (messageType == ProtoGlobal.RoomMessageType.VOICE || messageType == ProtoGlobal.RoomMessageType.AUDIO || messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT) {
                onLoadThumbnailFromLocal(holder, getCacheId(mMessage), attachment.getLocalFilePath(), LocalFileType.FILE);
            } else {
                /**
                 * file doesn't exist on local, I check for a thumbnail
                 * if thumbnail exists, I load it into the view
                 */
                if (attachment.isThumbnailExistsOnLocal()) {
                    /**
                     * load thumbnail from local
                     */
                    onLoadThumbnailFromLocal(holder, getCacheId(mMessage), attachment.getLocalThumbnailPath(), LocalFileType.THUMBNAIL);
                } else {
                    if (messageType != ProtoGlobal.RoomMessageType.CONTACT) {
                        downLoadThumbnail(holder, attachment);
                    }
                }
            }

            if (hasProgress(holder.itemView)) {

                final MessageProgress _Progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
                AppUtils.setProgresColor(_Progress.progressBar);

                _Progress.withOnMessageProgress(new OnMessageProgressClick() {
                    @Override
                    public void onMessageProgressClick(MessageProgress progress) {
                        forOnCLick(holder, attachment);
                    }
                });

                if (!attachment.isFileExistsOnLocal()) {
                    if (HelperCheckInternetConnection.currentConnectivityType == null) {
                        checkAutoDownload(holder, attachment, holder.itemView.getContext(), HelperCheckInternetConnection.ConnectivityType.WIFI);
                        checkAutoDownload(holder, attachment, holder.itemView.getContext(), HelperCheckInternetConnection.ConnectivityType.MOBILE);
                    } else {
                        checkAutoDownload(holder, attachment, holder.itemView.getContext(), HelperCheckInternetConnection.currentConnectivityType);
                    }
                }

                _Progress.withOnProgress(new OnProgress() {
                    @Override
                    public void onProgressFinished() {
                        holder.itemView.findViewById(R.id.thumbnail).setOnClickListener(null);
                        _Progress.withDrawable(null, true);

                        switch (messageType) {
                            case VIDEO:
                            case VIDEO_TEXT:
                                holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                                _Progress.withDrawable(R.drawable.ic_play, true);
                                break;
                            case AUDIO:
                            case AUDIO_TEXT:
                                break;
                            case FILE:
                            case FILE_TEXT:
                            case IMAGE:
                            case IMAGE_TEXT:
                                holder.itemView.findViewById(R.id.thumbnail).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        forOnCLick(holder, attachment);
                                    }
                                });
                                break;
                            case VOICE:
                                break;
                            case GIF:
                            case GIF_TEXT:
                                holder.itemView.findViewById(R.id.thumbnail).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        forOnCLick(holder, attachment);
                                    }
                                });

                                SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                if (sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS) == 0) {
                                    holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                                    _Progress.withDrawable(R.drawable.photogif, true);
                                } else {
                                    holder.itemView.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                                }
                                break;
                        }
                    }
                });
            }

            prepareProgress(holder, attachment);
        }
    }

    private void autoDownload(VH holder, RealmAttachment attachment) {
        if (mMessage.messageType == ProtoGlobal.RoomMessageType.FILE || mMessage.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            View thumbnail = holder.itemView.findViewById(R.id.thumbnail);
            if (thumbnail != null) {
                thumbnail.setVisibility(View.INVISIBLE);
            }
        }

        downLoadFile(holder, attachment, 0);
    }

    private void forOnCLick(VH holder, RealmAttachment attachment) {

        final MessageProgress progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
        AppUtils.setProgresColor(progress.progressBar);

        View thumbnail = holder.itemView.findViewById(R.id.thumbnail);

        //if (mMessage.messageType == ProtoGlobal.RoomMessageType.FILE || mMessage.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
        //    if (thumbnail != null) {
        //        thumbnail.setVisibility(View.INVISIBLE);
        //    }
        //}


        if (attachment.getSize() == 0) {
            messageClickListener.onUploadOrCompressCancel(progress, mMessage, holder.getAdapterPosition(), SendingStep.CORRUPTED_FILE);
        } else if (HelperUploadFile.isUploading(mMessage.messageID)) {

            if (mMessage.status.equals(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                if (G.userLogin) {
                    messageClickListener.onFailedMessageClick(progress, mMessage, holder.getAdapterPosition());

                    //HelperUploadFile.reUpload(mMessage.messageID);
                    //progress.withDrawable(R.drawable.ic_cancel, false);
                    //holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                    //final ContentLoadingProgressBar contentLoading = (ContentLoadingProgressBar) holder.itemView.findViewById(R.id.ch_progress_loadingContent);
                    //contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
                    //contentLoading.setVisibility(View.VISIBLE);
                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                }
            } else {
                messageClickListener.onUploadOrCompressCancel(progress, mMessage, holder.getAdapterPosition(), SendingStep.UPLOADING);
            }
        } else if (HelperDownloadFile.isDownLoading(attachment.getCacheId())) {
            HelperDownloadFile.stopDownLoad(attachment.getCacheId());
        } else if (FragmentChat.compressingFiles.containsKey(Long.parseLong(mMessage.messageID))) {
            messageClickListener.onUploadOrCompressCancel(progress, mMessage, holder.getAdapterPosition(), SendingStep.COMPRESSING);
        } else {
            if (thumbnail != null) {
                thumbnail.setVisibility(View.VISIBLE);
            }

            if (attachment.isFileExistsOnLocal()) {

                String _status = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getStatus() : mMessage.status;
                ProtoGlobal.RoomMessageType _type = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType;

                if (_status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                    return;
                }
                if (_status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                    messageClickListener.onFailedMessageClick(progress, mMessage, holder.getAdapterPosition());
                } else {
                    /**
                     * avoid from show GIF in fragment show image
                     */
                    if (_type == ProtoGlobal.RoomMessageType.GIF || _type == ProtoGlobal.RoomMessageType.GIF_TEXT) {
                        try {
                            onPlayPauseGIF(holder, attachment.getLocalFilePath());
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    } else {
                        progress.performProgress();
                        messageClickListener.onOpenClick(progress, mMessage, holder.getAdapterPosition());
                    }
                }
            } else {
                downLoadFile(holder, attachment, 2);
            }
        }
    }

    @Override
    @CallSuper
    public void onLoadThumbnailFromLocal(VH holder, String tag, String localPath, LocalFileType fileType) {

    }

    private void downLoadThumbnail(final VH holder, final RealmAttachment attachment) {

        if (attachment == null) return;

        String token = attachment.getToken();
        String name = attachment.getName();

        long size = 0;

        if (attachment.getSmallThumbnail() != null) size = attachment.getSmallThumbnail().getSize();

        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;

        if (attachment.getCacheId() == null || attachment.getCacheId().length() == 0) {
            return;
        }

        //  final String _path = AndroidUtils.getFilePathWithCashId(attachment.getCacheId(), name, G.DIR_TEMP, true);

        if (token != null && token.length() > 0 && size > 0) {

            HelperDownloadFile.startDownload(mMessage.messageID, token, attachment.getCacheId(), name, size, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, int progress) {

                    if (FragmentChat.canUpdateAfterDownload) {
                        if (progress == 100) {

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String type;
                                    if (mMessage.forwardedFrom != null) {
                                        type = mMessage.forwardedFrom.getMessageType().toString().toLowerCase();
                                    } else {
                                        type = mMessage.messageType.toString().toLowerCase();
                                    }
                                    if (type.contains("image") || type.contains("video") || type.contains("gif")) {
                                        onLoadThumbnailFromLocal(holder, attachment.getCacheId(), path, LocalFileType.THUMBNAIL);
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
    }

    private void downLoadFile(final VH holder, final RealmAttachment attachment, int priority) {

        if (attachment == null || attachment.getCacheId() == null) {
            return;
        }

        boolean _isDownloading = HelperDownloadFile.isDownLoading(attachment.getCacheId());

        final MessageProgress progressBar = (MessageProgress) holder.itemView.findViewById(R.id.progress);
        AppUtils.setProgresColor(progressBar.progressBar);

        final ContentLoadingProgressBar contentLoading = (ContentLoadingProgressBar) holder.itemView.findViewById(R.id.ch_progress_loadingContent);
        contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

        contentLoading.setVisibility(View.VISIBLE);

        final String token = attachment.getToken();
        String name = attachment.getName();
        Long size = attachment.getSize();
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

        final ProtoGlobal.RoomMessageType messageType = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType;

        final String _path = AndroidUtils.getFilePathWithCashId(attachment.getCacheId(), name, messageType);

        if (token != null && token.length() > 0 && size > 0) {

            progressBar.setVisibility(View.VISIBLE);
            progressBar.withDrawable(R.drawable.ic_cancel, false);

            HelperDownloadFile.startDownload(mMessage.messageID, token, attachment.getCacheId(), name, size, selector, _path, priority, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, final int progress) {

                    if (progress == 100) {
                        if (messageType == ProtoGlobal.RoomMessageType.AUDIO || messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT || messageType == ProtoGlobal.RoomMessageType.VOICE) {
                            if (mMessage.roomId == MusicPlayer.roomId) {
                                MusicPlayer.downloadNewItem = true;
                            }
                        }
                    }

                    if (FragmentChat.canUpdateAfterDownload) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {

                                    if (progress == 100) {

                                        progressBar.setVisibility(View.GONE);
                                        contentLoading.setVisibility(View.GONE);

                                        progressBar.performProgress();

                                        onLoadThumbnailFromLocal(holder, attachment.getCacheId(), path, LocalFileType.FILE);
                                    } else {

                                        progressBar.withProgress(progress);
                                    }
                                }
                            }
                        });
                    }


                }

                @Override
                public void OnError(String token) {

                    if (FragmentChat.canUpdateAfterDownload) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {

                                    progressBar.withProgress(0);
                                    progressBar.withDrawable(R.drawable.ic_download, true);
                                    contentLoading.setVisibility(View.GONE);
                                }
                            }
                        });
                    }


                }
            });

            if (!_isDownloading) {
                messageClickListener.onDownloadAllEqualCashId(attachment.getCacheId(), mMessage.messageID);
            }
        }
    }

    public void updateProgress(OnProgressUpdate onProgressUpdate) {
        onProgressUpdate.onProgressUpdate();
    }

    /**
     * automatically update progress if layout has one
     *
     * @param holder VH
     */
    private void prepareProgress(final VH holder, RealmAttachment attachment) {
        if (!hasProgress(holder.itemView)) {
            return;
        }

        if (mMessage.sendType == MyType.SendType.send) {

            final MessageProgress progressBar = (MessageProgress) holder.itemView.findViewById(R.id.progress);
            AppUtils.setProgresColor(progressBar.progressBar);

            progressBar.withDrawable(R.drawable.ic_cancel, false);

            final ContentLoadingProgressBar contentLoading = (ContentLoadingProgressBar) holder.itemView.findViewById(R.id.ch_progress_loadingContent);
            contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
            contentLoading.setVisibility(View.GONE);

            /**
             * update progress when user trying to upload or download also if
             * file is compressing do this action for add listener and use later
             */
            if (HelperUploadFile.isUploading(mMessage.messageID) || (mMessage.status.equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) || FragmentChat.compressingFiles.containsKey(Long.parseLong(mMessage.messageID)))) {//(mMessage.status.equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) this code newly added
                hideThumbnailIf(holder);

                HelperUploadFile.AddListener(mMessage.messageID, new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(final int progress, FileUploadStructure struct) {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {

                                //float p = progress;
                                //if ((mMessage.messageType == ProtoGlobal.RoomMessageType.VIDEO || mMessage.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) && FragmentChat.compressingFiles.containsKey(Long.parseLong(mMessage.messageID))) {
                                //    if (progress < mMessage.uploadProgress) {
                                //        p = mMessage.uploadProgress;
                                //    }
                                //}
                                if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {
                                    progressBar.withProgress(progress);
                                    if (progress == 100) {
                                        progressBar.performProgress();
                                        contentLoading.setVisibility(View.GONE);
                                    }
                                }

                            }
                        });
                    }

                    @Override
                    public void OnError() {
                        if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {
                            progressBar.withProgress(0);
                            progressBar.withDrawable(R.drawable.upload, true);
                            contentLoading.setVisibility(View.GONE);
                            mMessage.status = ProtoGlobal.RoomMessageStatus.FAILED.toString();
                        }
                    }
                });

                //if (mMessage.messageType == ProtoGlobal.RoomMessageType.VIDEO || mMessage.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                //
                //    MediaController.onPercentCompress = new MediaController.OnPercentCompress() {
                //        @Override
                //        public void compress(final long percent, String path) {
                //
                //            G.handler.post(new Runnable() {
                //                @Override
                //                public void run() {
                //                    if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {
                //                        int p = (int) (percent / 10);
                //                        progressBar.withProgress(p);
                //                        mMessage.uploadProgress = p;
                //                    }
                //                }
                //            });
                //        }
                //    };
                //}

                holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                contentLoading.setVisibility(View.VISIBLE);
                progressBar.withProgress(HelperUploadFile.getUploadProgress(mMessage.messageID));
            } else {
                checkForDownloading(holder, attachment);
            }

            String _status = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getStatus() : mMessage.status;
            if (_status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                onFaildUpload(holder);
            }
        } else {
            checkForDownloading(holder, attachment);
        }
    }

    private void onFaildUpload(VH holder) {

        MessageProgress progressBar = (MessageProgress) holder.itemView.findViewById(R.id.progress);
        if (progressBar.getTag() != null && progressBar.getTag().equals(mMessage.messageID)) {
            AppUtils.setProgresColor(progressBar.progressBar);

            final ContentLoadingProgressBar contentLoading = (ContentLoadingProgressBar) holder.itemView.findViewById(R.id.ch_progress_loadingContent);
            // contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

            progressBar.withProgress(0);

            progressBar.withDrawable(R.drawable.upload, true);
            contentLoading.setVisibility(View.GONE);
        }
    }

    private void hideThumbnailIf(VH holder) {
        if (mMessage.messageType == ProtoGlobal.RoomMessageType.FILE || mMessage.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            View view = holder.itemView.findViewById(R.id.thumbnail);
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void checkForDownloading(VH holder, RealmAttachment attachment) {

        MessageProgress progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
        AppUtils.setProgresColor(progress.progressBar);

        if (HelperDownloadFile.isDownLoading(attachment.getCacheId())) {
            hideThumbnailIf(holder);

            downLoadFile(holder, attachment, 0);
        } else {
            if (attachment.isFileExistsOnLocal()) {
                if (!(mMessage.status.equals(ProtoGlobal.RoomMessageStatus.SENDING.toString()) && !(mMessage.status.equals(ProtoGlobal.RoomMessageStatus.FAILED.toString())))) {
                    progress.performProgress();
                }
            } else {
                hideThumbnailIf(holder);
                progress.withDrawable(R.drawable.ic_download, true);
                progress.setVisibility(View.VISIBLE);
            }
        }
    }

    public String getCacheId(StructMessageInfo mMessage) {
        if (mMessage.forwardedFrom != null && mMessage.forwardedFrom.getAttachment() != null && mMessage.forwardedFrom.getAttachment().getCacheId() != null) {
            return mMessage.forwardedFrom.getAttachment().getCacheId();
        } else if (mMessage.getAttachment() != null && mMessage.getAttachment().cashID != null) {
            return mMessage.getAttachment().cashID;
        }

        return "";
    }


}
