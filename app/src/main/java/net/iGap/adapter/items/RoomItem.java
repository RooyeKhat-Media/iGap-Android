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
//import android.os.Build;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import com.mikepenz.fastadapter.items.AbstractItem;
//import io.realm.Realm;
//import java.util.HashMap;
//import java.util.List;
//import net.iGap.G;
//import net.iGap.R;
//import net.iGap.adapter.items.chat.ViewMaker;
//import net.iGap.fragments.FragmentMain;
//import net.iGap.helper.HelperAvatar;
//import net.iGap.helper.HelperCalander;
//import net.iGap.helper.HelperImageBackColor;
//import net.iGap.interfaces.OnAvatarGet;
//import net.iGap.interfaces.OnComplete;
//import net.iGap.module.AndroidUtils;
//import net.iGap.module.AppUtils;
//import net.iGap.module.CircleImageView;
//import net.iGap.module.EmojiTextViewE;
//import net.iGap.module.MaterialDesignTextView;
//import net.iGap.module.enums.RoomType;
//import net.iGap.proto.ProtoGlobal;
//import net.iGap.realm.RealmRegisteredInfo;
//import net.iGap.realm.RealmRegisteredInfoFields;
//import net.iGap.realm.RealmRoom;
//
//import static net.iGap.G.context;
//import static net.iGap.G.userId;
//import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
//import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
//
///**
// * chat item for main displaying chats
// */
//public class RoomItem extends AbstractItem<RoomItem, RoomItem.ViewHolder> {
//
//    public OnComplete mComplete;
//    public String action;
//    public HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();
//    public RealmRoom mInfo;
//    private FragmentMain.MainType mainType;
//
//    public RoomItem(OnComplete complete, FragmentMain.MainType mainType) {
//        this.mComplete = complete;
//        this.mainType = mainType;
//    }
//
//    public RoomItem setInfo(RealmRoom mInfo) {
//        this.mInfo = mInfo;
//        return this;
//    }
//
//    @Override
//    public void bindView(final ViewHolder holder, List payloads) throws IllegalStateException {
//        super.bindView(holder, payloads);
//
//        if (holder.itemView.findViewById(R.id.root_chat_sub_layout) == null) {
//            ((ViewGroup) holder.itemView).addView(ViewMaker.getViewItemRoom());
//        }
//
//        holder.image = (CircleImageView) holder.itemView.findViewById(R.id.cs_img_contact_picture);
//        holder.name = (EmojiTextViewE) holder.itemView.findViewById(R.id.cs_txt_contact_name);
//        holder.name.setTypeface(G.typeface_IRANSansMobile_Bold);
//
//        holder.rootChat = (ViewGroup) holder.itemView.findViewById(R.id.root_chat_sub_layout);
//        holder.txtLastMessage = (EmojiTextViewE) holder.itemView.findViewById(R.id.cs_txt_last_message);
//        holder.txtLastMessageFileText = (EmojiTextViewE) holder.itemView.findViewById(R.id.cs_txt_last_message_file_text);
//        holder.txtChatIcon = (MaterialDesignTextView) holder.itemView.findViewById(R.id.cs_txt_chat_icon);
//
//        holder.txtTime = ((TextView) holder.itemView.findViewById(R.id.cs_txt_contact_time));
//        holder.txtTime.setTypeface(G.typeface_IRANSansMobile);
//
//        holder.txtPinIcon = (MaterialDesignTextView) holder.itemView.findViewById(R.id.cs_txt_pinned_message);
//        holder.txtPinIcon.setTypeface(G.typeface_Fontico);
//
//        holder.txtUnread = (TextView) holder.itemView.findViewById(R.id.cs_txt_unread_message);
//        holder.txtUnread.setTypeface(G.typeface_IRANSansMobile);
//
//        holder.mute = (MaterialDesignTextView) holder.itemView.findViewById(R.id.cs_txt_mute);
//
//        holder.lastMessageSender = (EmojiTextViewE) holder.itemView.findViewById(R.id.cs_txt_last_message_sender);
//        holder.lastMessageSender.setTypeface(G.typeface_IRANSansMobile);
//
//        holder.txtTic = (ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic);
//
//        holder.txtCloud = (MaterialDesignTextView) holder.itemView.findViewById(R.id.cs_txt_contact_initials);
//
//        if (mInfo == null || !mInfo.isValid() || mInfo.isDeleted()) {
//            return;
//        }
//
//        final boolean isMyCloud;
//
//        if (mInfo.getChatRoom() != null && mInfo.getChatRoom().getPeerId() > 0 && mInfo.getChatRoom().getPeerId() == userId) {
//            isMyCloud = true;
//        } else {
//            isMyCloud = false;
//        }
//
//        if (mInfo.isValid()) {
//
//            setLastMessage(mInfo, holder, isMyCloud);
//
//            if (isMyCloud) {
//
//                if (holder.txtCloud == null) {
//
//                    MaterialDesignTextView cs_txt_contact_initials = new MaterialDesignTextView(context);
//                    cs_txt_contact_initials.setId(R.id.cs_txt_contact_initials);
//                    cs_txt_contact_initials.setGravity(Gravity.CENTER);
//                    cs_txt_contact_initials.setText(context.getResources().getString(R.string.md_cloud));
//                    cs_txt_contact_initials.setTextColor(Color.parseColor("#ad333333"));
//                    ViewMaker.setTextSize(cs_txt_contact_initials, R.dimen.dp32);
//                    LinearLayout.LayoutParams layout_936 = new LinearLayout.LayoutParams(ViewMaker.i_Dp(R.dimen.dp52), ViewMaker.i_Dp(R.dimen.dp52));
//                    layout_936.gravity = Gravity.CENTER;
//                    layout_936.setMargins(ViewMaker.i_Dp(R.dimen.dp6), ViewMaker.i_Dp(R.dimen.dp6), ViewMaker.i_Dp(R.dimen.dp6), ViewMaker.i_Dp(R.dimen.dp6));
//                    cs_txt_contact_initials.setVisibility(View.GONE);
//                    cs_txt_contact_initials.setLayoutParams(layout_936);
//
//                    holder.txtCloud = cs_txt_contact_initials;
//
//                    holder.rootChat.addView(cs_txt_contact_initials, 0);
//                }
//
//                holder.txtCloud.setVisibility(View.VISIBLE);
//                holder.image.setVisibility(View.GONE);
//            } else {
//
//                if (holder.txtCloud != null) {
//                    holder.txtCloud.setVisibility(View.GONE);
//                }
//
//                if (holder.image.getVisibility() == View.GONE) {
//                    holder.image.setVisibility(View.VISIBLE);
//                }
//
//                setAvatar(mInfo, holder.image);
//            }
//
//            setChatIcon(mInfo, holder.txtChatIcon);
//
//            holder.name.setText(mInfo.getTitle());
//
//            if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateOrCreateTime() != 0) {
//                holder.txtTime.setText(HelperCalander.getTimeForMainRoom(mInfo.getLastMessage().getUpdateOrCreateTime()));
//            }
//
//            /**
//             * ********************* unread *********************
//             */
//
//            if (mInfo.isPinned()) {
//                holder.rootChat.setBackgroundColor(ContextCompat.getColor(context, R.color.pin_color));
//                holder.txtPinIcon.setVisibility(View.VISIBLE);
//
//            } else {
//                holder.rootChat.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//                holder.txtPinIcon.setVisibility(View.GONE);
//            }
//
//            if (mInfo.getUnreadCount() < 1) {
//
//                holder.txtUnread.setVisibility(View.GONE);
//
//            } else {
//                holder.txtUnread.setVisibility(View.VISIBLE);
//                holder.txtPinIcon.setVisibility(View.GONE);
//                holder.txtUnread.setText(mInfo.getUnreadCount() + "");
//
//                if (HelperCalander.isLanguagePersian) {
//                    holder.txtUnread.setBackgroundResource(R.drawable.rect_oval_red);
//                } else {
//                    holder.txtUnread.setBackgroundResource(R.drawable.rect_oval_red_left);
//                }
//
//                if (mInfo.getMute()) {
//                    AndroidUtils.setBackgroundShapeColor(holder.txtUnread, Color.parseColor("#c6c1c1"));
//                } else {
//                    AndroidUtils.setBackgroundShapeColor(holder.txtUnread, Color.parseColor(G.notificationColor));
//                }
//            }
//
//            if (mInfo.getMute()) {
//                holder.mute.setVisibility(View.VISIBLE);
//            } else {
//                holder.mute.setVisibility(View.GONE);
//            }
//        }
//
//        /**
//         * for change english number to persian number
//         */
//        if (HelperCalander.isLanguagePersian) {
//            holder.txtLastMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtLastMessage.getText().toString()));
//            holder.txtUnread.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtUnread.getText().toString()));
//        }
//    }
//
//    @Override
//    public int getType() {
//        return 0;
//    }
//
//    @Override
//    public int getLayoutRes() {
//        return R.layout.chat_sub_layout_code;
//    }
//
//    @Override
//    public ViewHolder getViewHolder(View v) {
//        return new ViewHolder(v);
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        //RealmRoom mInfo;
//
//        protected CircleImageView image;
//        protected EmojiTextViewE name;
//        private ViewGroup rootChat;
//        private EmojiTextViewE txtLastMessage;
//        private EmojiTextViewE txtLastMessageFileText;
//        private MaterialDesignTextView txtChatIcon;
//        private TextView txtTime;
//        private MaterialDesignTextView txtPinIcon;
//        private TextView txtUnread;
//        protected MaterialDesignTextView mute;
//        private EmojiTextViewE lastMessageSender;
//        private ImageView txtTic;
//        private MaterialDesignTextView txtCloud;
//
//
//        public ViewHolder(View view) {
//            super(view);
//
//            //image = (CircleImageView) view.findViewById(R.id.cs_img_contact_picture);
//            //name = (EmojiTextViewE) view.findViewById(R.id.cs_txt_contact_name);
//            //name.setTypeface(G.typeface_IRANSansMobile_Bold);
//            //
//            //rootChat = (ViewGroup) view.findViewById(R.id.root_chat_sub_layout);
//            //txtLastMessage = (EmojiTextViewE) view.findViewById(R.id.cs_txt_last_message);
//            //txtLastMessageFileText = (EmojiTextViewE) view.findViewById(R.id.cs_txt_last_message_file_text);
//            //txtChatIcon = (MaterialDesignTextView) view.findViewById(R.id.cs_txt_chat_icon);
//            //
//            //txtTime = ((TextView) view.findViewById(R.id.cs_txt_contact_time));
//            //txtTime.setTypeface(G.typeface_IRANSansMobile);
//            //
//            //txtPinIcon = (MaterialDesignTextView) view.findViewById(R.id.cs_txt_pinned_message);
//            //txtPinIcon.setTypeface(G.typeface_Fontico);
//            //
//            //txtUnread = (TextView) view.findViewById(R.id.cs_txt_unread_message);
//            //txtUnread.setTypeface(G.typeface_IRANSansMobile);
//            //
//            //mute = (MaterialDesignTextView) view.findViewById(R.id.cs_txt_mute);
//            //
//            //lastMessageSender = (EmojiTextViewE) view.findViewById(R.id.cs_txt_last_message_sender);
//            //lastMessageSender.setTypeface(G.typeface_IRANSansMobile);
//            //
//            //txtTic = (ImageView) view.findViewById(R.id.cslr_txt_tic);
//            //
//            //txtCloud = (MaterialDesignTextView) view.findViewById(R.id.cs_txt_contact_initials);
//
//            //view.setOnClickListener(new View.OnClickListener() {
//            //    @Override
//            //    public void onClick(View v) {
//            //
//            //        if (ActivityMain.isMenuButtonAddShown) {
//            //            mComplete.complete(true, "closeMenuButton", "");
//            //        } else {
//            //            if (mInfo.isValid() && G.fragmentActivity != null) {
//            //
//            //                boolean openChat = true;
//            //
//            //                if (G.twoPaneMode) {
//            //                    Fragment fragment = G.fragmentManager.findFragmentByTag(FragmentChat.class.getName());
//            //                    if (fragment != null) {
//            //
//            //                        FragmentChat fm = (FragmentChat) fragment;
//            //                        if (fm.isAdded() && fm.mRoomId == mInfo.getId()) {
//            //                            openChat = false;
//            //                        } else {
//            //                            //removeFromBaseFragment(fragment); AAAAAAAAAAAAA
//            //                        }
//            //
//            //
//            //                    }
//            //                }
//            //
//            //                if (openChat) {
//            //                    new GoToChatActivity(mInfo.getId()).startActivity();
//            //
//            //                    if (((ActivityMain) G.fragmentActivity).arcMenu != null && ((ActivityMain) G.fragmentActivity).arcMenu.isMenuOpened()) {
//            //                        ((ActivityMain) G.fragmentActivity).arcMenu.toggleMenu();
//            //                    }
//            //                }
//            //            }
//            //        }
//            //    }
//            //});
//            //
//            //view.setOnLongClickListener(new View.OnLongClickListener() {
//            //    @Override
//            //    public boolean onLongClick(View v) {
//            //
//            //        if (ActivityMain.isMenuButtonAddShown) {
//            //
//            //            if (mComplete != null) {
//            //                mComplete.complete(true, "closeMenuButton", "");
//            //            }
//            //
//            //        } else {
//            //            if (mInfo.isValid() && G.fragmentActivity != null) {
//            //                String role = null;
//            //                if (mInfo.getType() == GROUP) {
//            //                    role = mInfo.getGroupRoom().getRole().toString();
//            //                } else if (mInfo.getType() == CHANNEL) {
//            //                    role = mInfo.getChannelRoom().getRole().toString();
//            //                }
//            //
//            //                MyDialog.showDialogMenuItemRooms(G.fragmentActivity, mInfo.getTitle(), mInfo.getType(), mInfo.getMute(), role, new OnComplete() {
//            //                    @Override
//            //                    public void complete(boolean result, String messageOne, String MessageTow) {
//            //                        //onSelectRoomMenu(messageOne, mInfo); aaaaaaaaaaaaaaaaa
//            //                    }
//            //                }, mInfo.isPinned());
//            //            }
//            //        }
//            //        return true;
//            //    }
//            //});
//        }
//    }
//
//    private String subStringInternal(String text) {
//        if (text == null || text.length() == 0) {
//            return "";
//        }
//
//        int subLength = 150;
//        if (text.length() > subLength) {
//            return text.substring(0, subLength);
//        } else {
//            return text;
//        }
//    }
//
//    //*******************************************************************************************
//    private void setLastMessage(RealmRoom mInfo, ViewHolder holder, boolean isMyCloud) {
//
//        holder.txtTic.setVisibility(View.GONE);
//        holder.txtLastMessageFileText.setVisibility(View.GONE);
//        holder.txtLastMessage.setText("");
//
//        if (mInfo.getActionState() != null && ((mInfo.getType() == GROUP || mInfo.getType() == CHANNEL) || ((isMyCloud || (mInfo.getActionStateUserId() != userId))))) {
//
//            holder.lastMessageSender.setVisibility(View.GONE);
//            holder.txtLastMessage.setText(mInfo.getActionState());
//            holder.txtLastMessage.setTextColor(ContextCompat.getColor(G.context, R.color.room_message_blue));
//            holder.txtLastMessage.setEllipsize(TextUtils.TruncateAt.MIDDLE);
//        } else if (mInfo.getDraft() != null && !TextUtils.isEmpty(mInfo.getDraft().getMessage())) {
//
//            holder.txtLastMessage.setText(subStringInternal(mInfo.getDraft().getMessage()));
//            holder.txtLastMessage.setTextColor(ContextCompat.getColor(G.context, R.color.room_message_gray));
//
//            holder.lastMessageSender.setVisibility(View.VISIBLE);
//            holder.lastMessageSender.setText(R.string.txt_draft);
//            holder.lastMessageSender.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
//            holder.lastMessageSender.setTypeface(G.typeface_IRANSansMobile);
//        } else {
//
//            if (mInfo.getLastMessage() != null) {
//                String lastMessage = AppUtils.rightLastMessage(mInfo.getId(), holder.itemView.getResources(), mInfo.getType(), mInfo.getLastMessage(), mInfo.getLastMessage().getForwardMessage() != null ? mInfo.getLastMessage().getForwardMessage().getAttachment() : mInfo.getLastMessage().getAttachment());
//
//                if (lastMessage == null) {
//                    lastMessage = mInfo.getLastMessage().getMessage();
//                }
//
//                if (lastMessage == null || lastMessage.isEmpty()) {
//
//                    holder.lastMessageSender.setVisibility(View.GONE);
//                } else {
//                    if (mInfo.getLastMessage().isAuthorMe()) {
//
//                        holder.txtTic.setVisibility(View.VISIBLE);
//                        AppUtils.rightMessageStatus(holder.txtTic, ProtoGlobal.RoomMessageStatus.valueOf(mInfo.getLastMessage().getStatus()), mInfo.getLastMessage().isAuthorMe());
//                    }
//
//                    if (mInfo.getType() == GROUP) {
//                        /**
//                         * here i get latest message from chat history with chatId and
//                         * get DisplayName with that . when login app client get latest
//                         * message for each group from server , if latest message that
//                         * send server and latest message that exist in client for that
//                         * room be different latest message sender showing will be wrong
//                         */
//
//                        String lastMessageSender = "";
//                        if (mInfo.getLastMessage().isAuthorMe()) {
//                            lastMessageSender = holder.itemView.getResources().getString(R.string.txt_you);
//                        } else {
//
//                            Realm realm = Realm.getDefaultInstance(); // aaaaaaaaaaaaaaa
//                            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mInfo.getLastMessage().getUserId()).findFirst();
//                            if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName() != null) {
//
//                                String _name = realmRegisteredInfo.getDisplayName();
//                                if (_name.length() > 0) {
//
//                                    if (Character.getDirectionality(_name.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
//                                        if (HelperCalander.isLanguagePersian) {
//                                            lastMessageSender = _name + ": ";
//                                        } else {
//                                            lastMessageSender = " :" + _name;
//                                        }
//                                    } else {
//                                        if (HelperCalander.isLanguagePersian) {
//                                            lastMessageSender = " :" + _name;
//                                        } else {
//                                            lastMessageSender = _name + ": ";
//                                        }
//                                    }
//                                }
//                            }
//                            realm.close();
//                        }
//
//                        holder.lastMessageSender.setVisibility(View.VISIBLE);
//
//                        holder.lastMessageSender.setText(lastMessageSender);
//                        holder.lastMessageSender.setTextColor(Color.parseColor("#2bbfbd"));
//                    } else {
//                        holder.lastMessageSender.setVisibility(View.GONE);
//                    }
//
//                    if (mInfo.getLastMessage() != null) {
//                        ProtoGlobal.RoomMessageType _type, tmp;
//
//                        _type = mInfo.getLastMessage().getMessageType();
//                        String fileText = mInfo.getLastMessage().getMessage();
//
//                        //don't use from reply , in reply message just get type and fileText from main message
//                        //try {
//                        //    if (mInfo.getLastMessage().getReplyTo() != null) {
//                        //        tmp = mInfo.getLastMessage().getReplyTo().getMessageType();
//                        //        if (tmp != null) {
//                        //            _type = tmp;
//                        //        }
//                        //        //if (mInfo.getLastMessage().getReplyTo().getMessage() != null) {
//                        //        //    fileText = mInfo.getLastMessage().getReplyTo().getMessage();
//                        //        //}
//                        //    }
//                        //} catch (NullPointerException e) {
//                        //    e.printStackTrace();
//                        //}
//                        //
//                        try {
//                            if (mInfo.getLastMessage().getForwardMessage() != null) {
//                                tmp = mInfo.getLastMessage().getForwardMessage().getMessageType();
//                                if (tmp != null) {
//                                    _type = tmp;
//                                }
//                                if (mInfo.getLastMessage().getForwardMessage().getMessage() != null) {
//                                    fileText = mInfo.getLastMessage().getForwardMessage().getMessage();
//                                }
//                            }
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                        }
//
//                        String result = AppUtils.conversionMessageType(_type, holder.txtLastMessage, R.color.room_message_blue);
//                        if (result.isEmpty()) {
//                            if (!HelperCalander.isLanguagePersian) {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                                    holder.txtLastMessage.setTextDirection(View.TEXT_DIRECTION_LTR);
//                                }
//                            }
//                            holder.txtLastMessage.setTextColor(ContextCompat.getColor(G.context, R.color.room_message_gray));
//                            holder.txtLastMessage.setText(subStringInternal(lastMessage));
//                            holder.txtLastMessage.setEllipsize(TextUtils.TruncateAt.END);
//                        } else {
//                            if (fileText != null && !fileText.isEmpty()) {
//                                holder.txtLastMessageFileText.setVisibility(View.VISIBLE);
//                                holder.txtLastMessageFileText.setText(fileText);
//
//                                holder.txtLastMessage.setText(holder.txtLastMessage.getText() + " : ");
//                            } else {
//                                holder.txtLastMessageFileText.setVisibility(View.GONE);
//                            }
//                        }
//                    } else {
//                        holder.txtLastMessage.setText(subStringInternal(lastMessage));
//                    }
//                }
//            } else {
//
//                holder.lastMessageSender.setVisibility(View.GONE);
//                holder.txtTime.setVisibility(View.GONE);
//            }
//        }
//    }
//
//    private void setAvatar(final RealmRoom mInfo, CircleImageView imageView) {
//        long idForGetAvatar;
//        HelperAvatar.AvatarType avatarType;
//        if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
//            idForGetAvatar = mInfo.getChatRoom().getPeerId();
//            avatarType = HelperAvatar.AvatarType.USER;
//        } else {
//            idForGetAvatar = mInfo.getId();
//            avatarType = HelperAvatar.AvatarType.ROOM;
//        }
//
//        hashMapAvatar.put(idForGetAvatar, imageView);
//
//        HelperAvatar.getAvatar(idForGetAvatar, avatarType, false, new OnAvatarGet() {
//            @Override
//            public void onAvatarGet(String avatarPath, long idForGetAvatar) {
//                if (hashMapAvatar.get(idForGetAvatar) != null) {
//                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(idForGetAvatar));
//                }
//            }
//
//            @Override
//            public void onShowInitials(String initials, String color) {
//                long idForGetAvatar;
//                if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
//                    idForGetAvatar = mInfo.getChatRoom().getPeerId();
//                } else {
//                    idForGetAvatar = mInfo.getId();
//                }
//                if (hashMapAvatar.get(idForGetAvatar) != null) {
//                    hashMapAvatar.get(idForGetAvatar).setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) G.context.getResources().getDimension(R.dimen.dp52), initials, color));
//                }
//            }
//        });
//    }
//
//    private void setChatIcon(RealmRoom mInfo, MaterialDesignTextView textView) {
//        /**
//         * ********************* chat icon *********************
//         */
//        if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT || mainType != FragmentMain.MainType.all) {
//            textView.setVisibility(View.GONE);
//        } else {
//
//            if (mInfo.getType() == GROUP) {
//                textView.setText(getStringChatIcon(RoomType.GROUP));
//            } else if (mInfo.getType() == CHANNEL) {
//                textView.setText(getStringChatIcon(RoomType.CHANNEL));
//            }
//        }
//    }
//
//    //*******************************************************************************************
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
//                return G.context.getString(R.string.md_channel_icon);
//            case GROUP:
//                return G.context.getString(R.string.md_users_social_symbol);
//            default:
//                return null;
//        }
//    }
//}