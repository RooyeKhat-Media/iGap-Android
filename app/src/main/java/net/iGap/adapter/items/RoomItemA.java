///*
//* This is the source code of iGap for Android
//* It is licensed under GNU AGPL v3.0
//* You should have received a copy of the license in this archive (see LICENSE).
//* Copyright © 2017 , iGap - www.iGap.net
//* iGap Messenger | Free, Fast and Secure instant messaging application
//* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
//* All rights reserved.
//*/
//
//package net.iGap.adapter.items;
//
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import com.mikepenz.fastadapter.items.AbstractItem;
//import com.wang.avi.AVLoadingIndicatorView;
//import io.realm.Realm;
//import io.realm.RealmResults;
//import io.realm.Sort;
//import java.util.List;
//import net.iGap.G;
//import net.iGap.R;
//import net.iGap.emoji.EmojiTextView;
//import net.iGap.helper.HelperAvatar;
//import net.iGap.helper.HelperCalander;
//import net.iGap.interfaces.OnAvatarGet;
//import net.iGap.interfaces.OnComplete;
//import net.iGap.module.AndroidUtils;
//import net.iGap.module.AppUtils;
//import net.iGap.module.CircleImageView;
//import net.iGap.module.enums.RoomType;
//import net.iGap.proto.ProtoGlobal;
//import net.iGap.realm.RealmAvatar;
//import net.iGap.realm.RealmAvatarFields;
//import net.iGap.realm.RealmRegisteredInfo;
//import net.iGap.realm.RealmRegisteredInfoFields;
//import net.iGap.realm.RealmRoom;
//import net.iGap.realm.RealmRoomFields;
//import net.iGap.realm.RealmRoomMessage;
//import net.iGap.realm.RealmRoomMessageFields;
//
//import static android.view.View.GONE;
//import static net.iGap.G.context;
//import static net.iGap.G.userId;
//import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
//import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
//
///**
// * chat item for main displaying chats
// */
//public class RoomItem extends AbstractItem<RoomItem, RoomItem.ViewHolder> {
//    public RealmRoom mInfo;
//    public OnComplete mComplete;
//    public String action;
//    private Typeface typeFaceIcon;
//
//    public RoomItem setComplete(OnComplete complete) {
//        this.mComplete = complete;
//        return this;
//    }
//
//    public RealmRoom getInfo() {
//        return mInfo;
//    }
//
//    public RoomItem setInfo(RealmRoom info) {
//        this.mInfo = info;
//        return this;
//    }
//
//    @Override public int getType() {
//        return 0;
//    }
//
//    @Override public int getLayoutRes() {
//        return R.layout.chat_sub_layout;
//    }
//
//    /**
//     * get string chat icon
//     *
//     * @param chatType chat type
//     * @return String
//     */
//    private String getStringChatIcon(RoomType chatType) {
//        switch (chatType) {
//            case CHAT:
//                return "";
//            case CHANNEL:
//                return context.getString(R.string.md_channel_icon);
//            case GROUP:
//                return context.getString(R.string.md_users_social_symbol);
//            default:
//                return null;
//        }
//    }
//
//    @Override public void bindView(final ViewHolder holder, List payloads) throws IllegalStateException {
//        super.bindView(holder, payloads);
//
//        if (mInfo != null && mInfo.isValid() && !mInfo.isDeleted()) {
//            if (mInfo.getActionState() != null && ((mInfo.getType() == GROUP || mInfo.getType() == CHANNEL) || ((RealmRoom.isCloudRoom(mInfo.getId()) || (!RealmRoom.isCloudRoom(mInfo.getId())
//                && mInfo.getActionStateUserId() != userId))))) {
//                //holder.messageStatus.setVisibility(GONE);
//                holder.lastMessageSender.setVisibility(View.GONE);
//                holder.lastMessage.setVisibility(View.VISIBLE);
//                holder.avi.setVisibility(View.VISIBLE);
//                holder.lastMessage.setText(mInfo.getActionState());
//                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
//            } else if (mInfo.getDraft() != null && !TextUtils.isEmpty(mInfo.getDraft().getMessage())) {
//                holder.avi.setVisibility(View.GONE);
//                holder.messageStatus.setVisibility(GONE);
//                holder.lastMessage.setVisibility(View.VISIBLE);
//                holder.lastMessageSender.setVisibility(View.VISIBLE);
//                holder.lastMessageSender.setText(R.string.txt_draft);
//                holder.lastMessageSender.setTextColor(Color.parseColor("#ff4644"));
//                holder.lastMessage.setText(mInfo.getDraft().getMessage());
//                holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_gray));
//            } else {
//                holder.avi.setVisibility(View.GONE);
//                if (mInfo.getLastMessage() != null) {
//                    String lastMessage = AppUtils.rightLastMessage(mInfo.getId(), holder.itemView.getResources(), mInfo.getType(), mInfo.getLastMessage(),
//                        mInfo.getLastMessage().getForwardMessage() != null ? mInfo.getLastMessage().getForwardMessage().getAttachment() : mInfo.getLastMessage().getAttachment());
//                    if (lastMessage == null) {
//                        lastMessage = mInfo.getLastMessage().getMessage();
//                    }
//
//                    if (lastMessage == null || lastMessage.isEmpty()) {
//                        holder.messageStatus.setVisibility(GONE);
//                        holder.lastMessage.setVisibility(GONE);
//                        holder.lastMessageSender.setVisibility(GONE);
//                    } else {
//                        if (mInfo.getLastMessage().isSenderMe()) {
//                            AppUtils.rightMessageStatus(holder.messageStatus, ProtoGlobal.RoomMessageStatus.valueOf(mInfo.getLastMessage().getStatus()), mInfo.getLastMessage().isSenderMe());
//                            holder.messageStatus.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.messageStatus.setVisibility(GONE);
//                        }
//
//                        /**
//                         * here i get latest message from chat history with chatId and
//                         * get DisplayName with that . when login app client get latest
//                         * message for each group from server , if latest message that
//                         * send server and latest message that exist in client for that
//                         * room be different latest message sender showing will be wrong
//                         */
//
//                        String lastMessageSender = "";
//                        if (mInfo.getLastMessage().isSenderMe()) {
//                            lastMessageSender = holder.itemView.getResources().getString(R.string.txt_you);
//                        } else {
//                            Realm realm1 = Realm.getDefaultInstance();
//                            RealmResults<RealmRoomMessage> results =
//                                realm1.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mInfo.getId()).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
//                            if (!results.isEmpty()) {
//                                RealmRoomMessage realmRoomMessage = results.first();
//                                if (realmRoomMessage != null) {
//                                    RealmRegisteredInfo realmRegisteredInfo = realm1.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, realmRoomMessage.getUserId()).findFirst();
//                                    if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName() != null) {
//                                        if (Character.getDirectionality(realmRegisteredInfo.getDisplayName().charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
//                                            if (HelperCalander.isPersianUnicode) {
//                                                lastMessageSender = realmRegisteredInfo.getDisplayName() + ": ";
//                                            } else {
//                                                lastMessageSender = " :" + realmRegisteredInfo.getDisplayName();
//                                            }
//                                        } else {
//                                            if (HelperCalander.isPersianUnicode) {
//                                                lastMessageSender = " :" + realmRegisteredInfo.getDisplayName();
//                                            } else {
//                                                lastMessageSender = realmRegisteredInfo.getDisplayName() + ": ";
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            realm1.close();
//                        }
//
//                        if (mInfo.getType() == ProtoGlobal.Room.Type.GROUP) {
//                            holder.lastMessageSender.setText(lastMessageSender);
//                            holder.lastMessageSender.setTextColor(Color.parseColor("#2bbfbd"));
//                            holder.lastMessageSender.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.lastMessageSender.setVisibility(GONE);
//                        }
//
//                        holder.lastMessage.setVisibility(View.VISIBLE);
//
//                        if (mInfo.getLastMessage() != null) {
//                            ProtoGlobal.RoomMessageType _type, tmp;
//
//                            _type = mInfo.getLastMessage().getMessageType();
//
//                            try {
//                                if (mInfo.getLastMessage().getReplyTo() != null) {
//                                    tmp = mInfo.getLastMessage().getReplyTo().getMessageType();
//                                    if (tmp != null) _type = tmp;
//                                }
//                            } catch (NullPointerException e) {
//                                e.printStackTrace();
//                            }
//
//                            try {
//                                if (mInfo.getLastMessage().getForwardMessage() != null) {
//                                    tmp = mInfo.getLastMessage().getForwardMessage().getMessageType();
//                                    if (tmp != null) _type = tmp;
//                                }
//                            } catch (NullPointerException e) {
//                                e.printStackTrace();
//                            }
//
//                            switch (_type) {
//                                case VOICE:
//                                    holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
//                                    holder.lastMessage.setText(R.string.voice_message);
//                                    break;
//                                case VIDEO:
//                                    holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
//                                    holder.lastMessage.setText(R.string.video_message);
//                                    break;
//                                case FILE:
//                                    holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
//                                    holder.lastMessage.setText(R.string.file_message);
//                                    break;
//                                case AUDIO:
//                                    holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
//                                    holder.lastMessage.setText(R.string.audio_message);
//                                    break;
//                                case IMAGE:
//                                    holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
//                                    holder.lastMessage.setText(R.string.image_message);
//                                    break;
//                                case CONTACT:
//                                    holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
//                                    holder.lastMessage.setText(R.string.contact_message);
//                                    break;
//                                case GIF:
//                                    holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
//                                    holder.lastMessage.setText(R.string.gif_message);
//                                    break;
//                                case LOCATION:
//                                    holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
//                                    holder.lastMessage.setText(R.string.location_message);
//                                    break;
//                                default:
//                                    holder.lastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_gray));
//                                    holder.lastMessage.setText(lastMessage);
//                                    break;
//                            }
//                        } else {
//                            holder.lastMessage.setText(lastMessage);
//                        }
//                    }
//                } else {
//                    holder.lastMessage.setVisibility(GONE);
//                    holder.lastSeen.setVisibility(GONE);
//                    holder.messageStatus.setVisibility(GONE);
//                    holder.lastMessageSender.setVisibility(GONE);
//                }
//            }
//
//            long idForGetAvatar;
//            HelperAvatar.AvatarType avatarType;
//            if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
//                idForGetAvatar = mInfo.getChatRoom().getPeerId();
//                avatarType = HelperAvatar.AvatarType.USER;
//            } else {
//                idForGetAvatar = mInfo.getId();
//                avatarType = HelperAvatar.AvatarType.ROOM;
//            }
//
//            final RealmAvatar realmAvatar = getLastAvatar(idForGetAvatar);
//            if (realmAvatar != null) {
//
//                if (realmAvatar.getFile().isFileExistsOnLocal()) {
//                    G.imageLoader.displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalFilePath()), holder.image);
//                } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
//                    G.imageLoader.displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalThumbnailPath()), holder.image);
//                } else {
//                    HelperAvatar.getAvatar1(idForGetAvatar, avatarType, new OnAvatarGet() {
//                        @Override public void onAvatarGet(final String avatarPath, final long ownerId) {
//                        /*G.handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
//                            }
//                        });*/
//                        }
//
//                        @Override public void onShowInitials(final String initials, final String color) {
//                        /*G.handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                holder.image.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
//                            }
//                        });*/
//                        }
//                    });
//
//                    String[] initials = showInitials(idForGetAvatar, avatarType);
//                    if (initials != null) {
//                        holder.image.setImageBitmap(
//                            net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials[0], initials[1]));
//                    }
//                }
//            } else {
//                String[] initials = showInitials(idForGetAvatar, avatarType);
//                if (initials != null) {
//                    holder.image.setImageBitmap(
//                        net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), initials[0], initials[1]));
//                }
//            }
//
//            holder.chatIcon.setTypeface(typeFaceIcon);
//            if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
//                holder.chatIcon.setVisibility(GONE);
//            } else if (mInfo.getType() == ProtoGlobal.Room.Type.GROUP) {
//                typeFaceIcon = G.typeface_Fontico;
//                holder.chatIcon.setTypeface(typeFaceIcon);
//                holder.chatIcon.setVisibility(View.VISIBLE);
//                holder.chatIcon.setText(getStringChatIcon(RoomType.GROUP));
//            } else if (mInfo.getType() == ProtoGlobal.Room.Type.CHANNEL) {
//                typeFaceIcon = G.typeface_Fontico;
//                holder.chatIcon.setTypeface(typeFaceIcon);
//                holder.chatIcon.setVisibility(View.VISIBLE);
//                holder.chatIcon.setText(getStringChatIcon(RoomType.CHANNEL));
//            }
//
//            holder.name.setText(mInfo.getTitle());
//
//           /* if (mInfo.isDeleted()) {
//                long lastMessageTime = AppUtils.computeLastMessageTime(mInfo.getId());
//                if (lastMessageTime != 0) {
//                    holder.lastSeen.setText(HelperCalander.getTimeForMainRoom(lastMessageTime));
//
//                    holder.lastSeen.setVisibility(View.VISIBLE);
//                } else {
//                    holder.lastSeen.setVisibility(GONE);
//                }
//            } else {
//                if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateOrCreateTime() != 0) {
//                    holder.lastSeen.setText(HelperCalander.getTimeForMainRoom(mInfo.getLastMessage().getUpdateOrCreateTime()));
//
//                    holder.lastSeen.setVisibility(View.VISIBLE);
//                } else {
//                    holder.lastSeen.setVisibility(GONE);
//                }
//            }*/
//
//            if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateOrCreateTime() != 0) {
//                holder.lastSeen.setText(HelperCalander.getTimeForMainRoom(mInfo.getLastMessage().getUpdateOrCreateTime()));
//
//                holder.lastSeen.setVisibility(View.VISIBLE);
//            } else {
//                holder.lastSeen.setVisibility(GONE);
//            }
//
//            if (mInfo.getUnreadCount() < 1) {
//                holder.unreadMessage.setVisibility(GONE);
//            } else {
//                holder.unreadMessage.setVisibility(View.VISIBLE);
//                holder.unreadMessage.setText(Integer.toString(mInfo.getUnreadCount()));
//
//                if (mInfo.getMute()) {
//                    holder.unreadMessage.setBackgroundResource(R.drawable.oval_gray);
//                } else {
//                    holder.unreadMessage.setBackgroundResource(R.drawable.oval_red);
//                }
//            }
//
//            if (mInfo.getMute()) {
//                holder.mute.setVisibility(View.VISIBLE);
//            } else {
//                holder.mute.setVisibility(GONE);
//            }
//        }
//
//        // for change english number to persian number
//        if (HelperCalander.isPersianUnicode) {
//            holder.lastMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.lastMessage.getText().toString()));
//            holder.name.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.name.getText().toString()));
//            holder.unreadMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.unreadMessage.getText().toString()));
//        }
//    }
//
//    private RealmAvatar getLastAvatar(long ownerId) {
//        Realm realm = Realm.getDefaultInstance();
//        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAllSorted(RealmAvatarFields.UID, Sort.DESCENDING)) {
//            if (avatar.getFile() != null) {
//                return avatar;
//            }
//        }
//        realm.close();
//        return null;
//    }
//
//    public static String[] showInitials(long ownerId, HelperAvatar.AvatarType avatarType) {
//        Realm realm = Realm.getDefaultInstance();
//        String initials = null;
//        String color = null;
//        if (avatarType == HelperAvatar.AvatarType.USER) {
//
//            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, ownerId).findFirst();
//            if (realmRegisteredInfo != null) {
//                initials = realmRegisteredInfo.getInitials();
//                color = realmRegisteredInfo.getColor();
//            } else {
//                for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll()) {
//                    if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == ownerId) {
//                        new HelperAvatar.UserInfo().getUserInfo(ownerId);
//                        initials = realmRoom.getInitials();
//                        color = realmRoom.getColor();
//                    }
//                }
//            }
//        } else if (avatarType == HelperAvatar.AvatarType.ROOM) {
//
//            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, ownerId).findFirst();
//            if (realmRoom != null) {
//                initials = realmRoom.getInitials();
//                color = realmRoom.getColor();
//            }
//        }
//        realm.close();
//
//        if (initials != null && color != null) {
//            return new String[] { initials, color };
//        }
//
//        return null;
//    }
//
//    private long getRoomId(long ownerId) {
//        Realm realm = Realm.getDefaultInstance();
//        for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll()) {
//            if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == ownerId) {
//                return realmRoom.getId();
//            }
//        }
//        realm.close();
//        return ownerId;
//    }
//
//    protected static class ViewHolder extends RecyclerView.ViewHolder {
//
//        protected CircleImageView image;
//        protected View distanceColor;
//        protected TextView chatIcon;
//        protected EmojiTextView name;
//        protected EmojiTextView lastMessageSender;
//        protected TextView mute;
//        protected EmojiTextView lastMessage;
//        protected TextView lastSeen;
//        protected TextView unreadMessage;
//        protected ImageView messageStatus;
//        private AVLoadingIndicatorView avi;
//
//        public ViewHolder(View view) {
//            super(view);
//
//            avi = (AVLoadingIndicatorView) view.findViewById(R.id.cs_avi);
//            image = (CircleImageView) view.findViewById(R.id.cs_img_contact_picture);
//            distanceColor = view.findViewById(R.id.cs_view_distance_color);
//            chatIcon = (TextView) view.findViewById(R.id.cs_txt_contact_icon);
//            name = (EmojiTextView) view.findViewById(R.id.cs_txt_contact_name);
//            lastMessage = (EmojiTextView) view.findViewById(R.id.cs_txt_last_message);
//            lastMessageSender = (EmojiTextView) view.findViewById(R.id.cs_txt_last_message_sender);
//            lastSeen = (TextView) view.findViewById(R.id.cs_txt_contact_time);
//            unreadMessage = (TextView) view.findViewById(R.id.cs_txt_unread_message);
//
//            mute = (TextView) view.findViewById(R.id.cs_txt_mute);
//            messageStatus = (ImageView) view.findViewById(R.id.cslr_txt_tic);
//
//            AndroidUtils.setBackgroundShapeColor(unreadMessage, Color.parseColor(G.notificationColor));
//        }
//    }
//
//    @Override public ViewHolder getViewHolder(View v) {
//        return new ViewHolder(v);
//    }
//}