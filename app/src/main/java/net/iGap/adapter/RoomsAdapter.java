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
//package net.iGap.adapter;
//
//import android.support.v4.app.Fragment;
//import android.view.View;
//import com.mikepenz.fastadapter.IAdapter;
//import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
//import io.realm.Realm;
//import java.util.ArrayList;
//import java.util.List;
//import net.iGap.G;
//import net.iGap.activities.ActivityMain;
//import net.iGap.adapter.items.RoomItem;
//import net.iGap.fragments.FragmentChat;
//import net.iGap.helper.GoToChatActivity;
//import net.iGap.interfaces.OnComplete;
//import net.iGap.module.MyDialog;
//import net.iGap.realm.RealmRoom;
//import net.iGap.realm.RealmRoomFields;
//import net.iGap.realm.RealmRoomMessage;
//import net.iGap.realm.RealmRoomMessageFields;
//
//import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
//import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
//
//public class RoomsAdapter<Item extends RoomItem> extends FastItemAdapter<Item> {
//    public static List<Long> userInfoAlreadyRequests = new ArrayList<>();
//    private Realm realmMain;
//    private Realm realmRoomsAdapter;
//
//    /**
//     * Hint: this method created for test
//     */
//    private Realm getRealmRoomsAdapter() {
//        if (realmRoomsAdapter == null || realmRoomsAdapter.isClosed()) {
//            realmRoomsAdapter = Realm.getDefaultInstance();
//        }
//        return realmRoomsAdapter;
//    }
//
//    public RoomsAdapter(Realm realmMain) {
//        this.realmMain = realmMain;
//        // as we provide id's for the items we want the hasStableIds enabled to speed up things
//        //setHasStableIds(true);
//
//        withOnClickListener(new OnClickListener<Item>() {
//            @Override
//            public boolean onClick(View v, IAdapter<Item> adapter, Item item, int position) {
//                if (ActivityMain.isMenuButtonAddShown) {
//                    item.mComplete.complete(true, "closeMenuButton", "");
//                } else {
//                    if (item.mInfo.isValid() && G.fragmentActivity != null) {
//
//                        boolean openChat = true;
//
//                        if (G.twoPaneMode) {
//                            Fragment fragment = G.fragmentManager.findFragmentByTag(FragmentChat.class.getName());
//                            if (fragment != null) {
//
//                                FragmentChat fm = (FragmentChat) fragment;
//                                if (fm.isAdded() && fm.mRoomId == item.mInfo.getId()) {
//                                    openChat = false;
//                                } else {
//                                    if (G.onRemoveFragment != null) {
//                                        G.onRemoveFragment.onRemoveFragment(fragment);
//                                    }
//                                }
//                            }
//                        }
//
//                        if (openChat) {
//                            new GoToChatActivity(item.mInfo.getId()).startActivity();
//
//                            if (((ActivityMain) G.fragmentActivity).arcMenu != null && ((ActivityMain) G.fragmentActivity).arcMenu.isMenuOpened()) {
//                                ((ActivityMain) G.fragmentActivity).arcMenu.toggleMenu();
//                            }
//                        }
//                    }
//                }
//                return true;
//            }
//        });
//
//        withOnLongClickListener(new OnLongClickListener<Item>() {
//            @Override
//            public boolean onLongClick(View v, IAdapter<Item> adapter, final Item item, int position) {
//                if (ActivityMain.isMenuButtonAddShown) {
//                    if (item.mComplete != null) {
//                        item.mComplete.complete(true, "closeMenuButton", "");
//                    }
//                } else {
//                    if (item.mInfo.isValid() && G.fragmentActivity != null) {
//                        String role = null;
//                        if (item.mInfo.getType() == GROUP) {
//                            role = item.mInfo.getGroupRoom().getRole().toString();
//                        } else if (item.mInfo.getType() == CHANNEL) {
//                            role = item.mInfo.getChannelRoom().getRole().toString();
//                        }
//
//                        MyDialog.showDialogMenuItemRooms(G.fragmentActivity, item.mInfo.getTitle(), item.mInfo.getType(), item.mInfo.getMute(), role, new OnComplete() {
//                            @Override
//                            public void complete(boolean result, String messageOne, String MessageTow) {
//                                if (G.onSelectMenu != null) {
//                                    G.onSelectMenu.onSelectMenu(messageOne, item.mInfo);
//                                }
//                            }
//                        }, item.mInfo.isPinned());
//                    }
//                }
//                return true;
//            }
//        });
//    }
//
//    public void updateChat(long chatId, int addPosition, Item item) {
//        List<Item> items = getAdapterItems();
//        for (Item chat : items) {
//            if (checkValidationForRealm(chat, chat.mInfo) && chat.mInfo.getId() == chatId) {
//                int pos = items.indexOf(chat);
//                remove(pos);
//                add(addPosition, item);
//                break;
//            }
//        }
//    }
//
//    public void updateChatStatus(long chatId, final String status) {
//        List<Item> items = getAdapterItems();
//        //+Realm realm = Realm.getDefaultInstance();
//        Realm realm = Realm.getDefaultInstance();
//        for (final Item chat : items) {
//            if (checkValidationForRealm(chat, chat.mInfo) && chat.mInfo.getId() == chatId) {
//                final int pos = items.indexOf(chat);
//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, chat.mInfo.getLastMessage().getMessageId()).findFirst();
//                        if (realmRoomMessage != null) {
//                            realmRoomMessage.setStatus(status);
//                            notifyAdapterItemChanged(pos);
//                        }
//                    }
//                });
//                break;
//            }
//        }
//        realm.close();
//    }
//
//    public void goToTop(long roomId, boolean isPin) {
//        Item item = null;
//        List<Item> items = getAdapterItems();
//        for (Item chat : items) {
//            if (checkValidationForRealm(chat, chat.mInfo) && chat.mInfo.getId() == roomId) {
//                item = chat;
//                break;
//            }
//        }
//
//        if (isPin) {
//            updateChat(roomId, 0, item);
//        } else {
//            updateChat(roomId, getPinPosition(roomId) + 1, item);
//        }
//    }
//
//    public void goToPosition(long roomId, int position) {
//        Item item = null;
//        List<Item> items = getAdapterItems();
//        for (Item chat : items) {
//            if (checkValidationForRealm(chat, chat.mInfo) && chat.mInfo.getId() == roomId) {
//                item = chat;
//                break;
//            }
//        }
//
//        updateChat(roomId, position, item);
//    }
//
//    /**
//     * detect first item position that don't have pin
//     *
//     * @param roomId if roomId was pinned before return -1 because now pin state in not important
//     */
//    public int getPinPosition(long roomId) {
//        int count = -1;
//        for (Item chat : getAdapterItems()) {
//            if (chat.mInfo.isValid() && !chat.mInfo.isDeleted() && chat.mInfo.isPinned()) {
//                if (chat.mInfo.getId() == roomId) {
//                    return -1;
//                }
//                count++;
//            }
//        }
//        return count;
//    }
//
//    public void notifyDraft(long chatId, final String draftMessage) {
//        List<Item> items = getAdapterItems();
//        for (final Item chat : items) {
//            if (checkValidationForRealm(chat, chat.mInfo) && chat.mInfo.getId() == chatId) {
//
//                final int position = items.indexOf(chat);
//
//                // because of nested transactions, following lines should not be into a transaction method
//                chat.mInfo.getDraft().setMessage(draftMessage);
//                notifyItemChanged(position);
//            }
//        }
//    }
//
//    public void notifyWithRoomId(long roomId) {
//        notifyItemChanged(getPosition(roomId));
//    }
//
//    public void updateItem(long roomId) {
//        //+Realm realm = Realm.getDefaultInstance();
//        RealmRoom realmRoom = getRealmRoomsAdapter().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
//        if (realmRoom != null) {
//            int pos = getPosition(roomId);
//            if (pos != -1) {
//                set(pos, (Item) getItem(pos).setInfo(realmRoom));
//            }
//        }
//        //realm.close();
//    }
//
//    public boolean existRoom(long roomId) {
//        List<Item> items = getAdapterItems();
//        for (final Item chat : items) {
//            if (checkValidationForRealm(chat, chat.mInfo) && chat.mInfo.getId() == roomId) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public int getPositionCustom(long chatId) {
//        List<Item> items = getAdapterItems();
//        for (final Item chat : items) {
//            if (checkValidationForRealm(chat, chat.mInfo) && chat.mInfo.getId() == chatId) {
//                return items.indexOf(chat);
//            }
//        }
//        return -1;
//    }
//
//    /**
//     * check realm for detect that item is exist or changed
//     * info with another thread
//     *
//     * @return true if is valid and is exist otherwise return false
//     */
//    private boolean checkValidationForRealm(RoomItem roomItem, RealmRoom realmRoom) {
//        if (roomItem != null && roomItem.isEnabled() && realmRoom != null && realmRoom.isValid() && realmRoom.isManaged() && !realmRoom.isDeleted()) {
//            return true;
//        }
//        return false;
//    }
//
//    public void removeItemFromAdapter(final Long roomId) {
//
//        List<Item> items = getAdapterItems();
//
//        for (int i = 0; i < items.size(); i++) {
//            try {
//                if (items.get(i).mInfo.getId() == roomId) {
//                    items.remove(i);
//                    notifyAdapterItemRemoved(i);
//                    break;
//                }
//            } catch (IllegalStateException e) {
//                items.remove(i);
//                notifyAdapterItemRemoved(i);
//            }
//        }
//    }
//
//    public void removeChat(final long roomId) {
//        G.handler.post(new Runnable() {
//            @Override
//            public void run() {
//                int position = getPosition(roomId);
//                if (position != -1) {
//                    remove(position);
//                }
//            }
//        });
//    }
//
//
//    /*public void setAction(long roomId, ProtoGlobal.ClientAction clientAction) {
//        List<Item> items = getAdapterItems();
//        for (final Item chat : items) {
//            if (chat.mInfo.getId() == roomId) {
//                String action = HelperGetAction.getAction(chat.getInfo().getType(), clientAction);
//
//                if (action != null) {
//                    chat.getInfo().setActionState(action);
//                    notifyItemChanged(items.indexOf(chat));
//                } else {
//                    chat.getInfo().setActionState(null);
//                    notifyItemChanged(items.indexOf(chat));
//                }
//                break;
//            }
//        }
//    }*/
//
//    //public void setAction(long roomId, ProtoGlobal.ClientAction clientAction) {
//    //    List<Item> items = getAdapterItems();
//    //    for (final Item chat : items) {
//    //        if (chat.mInfo.getId() == roomId) {
//    //            String action = HelperGetAction.getAction(chat.getInfo().getType(), clientAction);
//    //
//    //            if (action != null) {
//    //                //chat.getInfo().setActionState(action);
//    //                chat.action = action;
//    //                notifyItemChanged(items.indexOf(chat));
//    //            } else {
//    //                //chat.getInfo().setActionState(null);
//    //                chat.action = null;
//    //                notifyItemChanged(items.indexOf(chat));
//    //            }
//    //            break;
//    //        }
//    //    }
//    //}
//}