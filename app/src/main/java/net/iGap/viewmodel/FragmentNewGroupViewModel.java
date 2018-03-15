package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.content.Context;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.ContactGroupFragment;
import net.iGap.fragments.FragmentCreateChannel;
import net.iGap.fragments.FragmentNewGroup;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnChannelCreate;
import net.iGap.interfaces.OnChatConvertToGroup;
import net.iGap.interfaces.OnClientGetRoomResponse;
import net.iGap.interfaces.OnGroupCreate;
import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoClientGetRoom;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestChannelAvatarAdd;
import net.iGap.request.RequestChannelCreate;
import net.iGap.request.RequestChatConvertToGroup;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestGroupAvatarAdd;
import net.iGap.request.RequestGroupCreate;

import static net.iGap.module.MusicPlayer.roomId;

public class FragmentNewGroupViewModel {

    public static String prefix = "NewGroup";
    public static long avatarId = 0;
    public static ProtoGlobal.Room.Type type;
    public static String mCurrentPhotoPath;
    public Uri uriIntent;
    public String token;
    public boolean existAvatar = false;
    public String mInviteLink;
    public boolean isChannel = false;
    public ObservableField<String> titleToolbar = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.new_group));
    public ObservableField<String> txtInputName = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.group_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
    public ObservableField<String> edtSetNewGroup = new ObservableField<>("");
    public ObservableField<String> edtDescription = new ObservableField<>("");
    public ObservableField<Integer> prgWaiting = new ObservableField<>(View.GONE);
    public ObservableField<Integer> edtDescriptionLines = new ObservableField<>(4);
    public ObservableField<Integer> edtDescriptionMaxLines = new ObservableField<>(4);
    public ObservableField<Integer> edtDescriptionImeOptions = new ObservableField<>(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
    public ObservableField<Integer> edtDescriptionInputType = new ObservableField<>(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    public ObservableField<Boolean> nextStepEnable = new ObservableField<>(true);
    private long groomId = 0;


    public FragmentNewGroupViewModel(Bundle arguments) {
        getInfo(arguments);
    }

    public void onClickNextStep(View view) {

        if (edtSetNewGroup.get().length() > 0) {
            prgWaiting.set(View.VISIBLE);
            G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String newName = edtSetNewGroup.get().replace(" ", "_");
            //  File file2 = new File(path, prefix + "_" + newName + Math.random() * 10000 + 1 + ".png");
            if (prefix.equals("NewChanel")) {
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                isChannel = true;
                createChannel();
            } else if (prefix.equals("ConvertToGroup")) {
                isChannel = false;
                chatToGroup();
            } else {
                isChannel = false;
                createGroup();
            }
        } else {
            if (prefix.equals("NewChanel")) {
                Toast.makeText(G.context, R.string.please_enter_channel_name, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(G.context, R.string.please_enter_group_name, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void onClickCancel(View view) {
        AppUtils.closeKeyboard(view);
        if (G.IMAGE_NEW_GROUP.exists()) {
            G.IMAGE_NEW_GROUP.delete();
        } else {
            G.IMAGE_NEW_CHANEL.delete();
        }
        try {
            G.fragmentActivity.onBackPressed();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void getInfo(Bundle bundle) {
        if (bundle != null) { // get a list of image
            prefix = bundle.getString("TYPE");
            if (bundle.getLong("ROOMID") != 0) {
                groomId = bundle.getLong("ROOMID");
            }
        }

        if (prefix.equals("NewChanel")) {
            titleToolbar.set(G.fragmentActivity.getResources().getString(R.string.New_Chanel));
        } else if (prefix.equals("ConvertToGroup")) {
            titleToolbar.set(G.fragmentActivity.getResources().getString(R.string.chat_to_group));
        }

        switch (prefix) {
            case "NewChanel":
                txtInputName.set(G.fragmentActivity.getResources().getString(R.string.channel_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
                break;
            case "ConvertToGroup":
                txtInputName.set(G.fragmentActivity.getResources().getString(R.string.group_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
                break;
            default:
                txtInputName.set(G.fragmentActivity.getResources().getString(R.string.group_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
                break;
        }


        edtDescriptionImeOptions.set(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        edtDescriptionInputType.set(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edtDescriptionLines.set(4);
        edtDescriptionMaxLines.set(4);


    }

    private void createChannel() {

        G.onChannelCreate = new OnChannelCreate() {
            @Override
            public void onChannelCreate(final long roomIdR, final String inviteLink, final String channelName) {
                getChannelRoom(roomIdR);
                G.onChannelCreate = null;
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.onChannelCreate = null;
                hideProgressBar();
                if (majorCode == 479) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ShowDialogLimitCreate();
                        }
                    });
                }
            }

            @Override
            public void onTimeOut() {
                hideProgressBar();
            }
        };

        new RequestChannelCreate().channelCreate(edtSetNewGroup.get(), edtDescription.get());
    }

    private void getChannelRoom(final long roomId) {
        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(final ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, RequestClientGetRoom.IdentityClientGetRoom identity) {
                if (identity.createRoomMode != RequestClientGetRoom.CreateRoomMode.requestFromOwner) {
                    return;
                }
                G.onClientGetRoomResponse = null;

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mInviteLink = room.getChannelRoomExtra().getPrivateExtra().getInviteLink();
                        if (existAvatar) {
                            new RequestChannelAvatarAdd().channelAvatarAdd(room.getId(), token);
                        } else {
                            hideProgressBar();
                            FragmentCreateChannel fragmentCreateChannel = new FragmentCreateChannel();
                            Bundle bundle = new Bundle();
                            bundle.putLong("ROOMID", room.getId());
                            bundle.putString("INVITE_LINK", mInviteLink);
                            bundle.putString("TOKEN", token);
                            fragmentCreateChannel.setArguments(bundle);


                            if (FragmentNewGroup.onRemoveFragmentNewGroup != null)
                                FragmentNewGroup.onRemoveFragmentNewGroup.onRemove();

                            new HelperFragment(fragmentCreateChannel).load();
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.onClientGetRoomResponse = null;
            }

            @Override
            public void onTimeOut() {
                G.onClientGetRoomResponse = null;
            }
        };
        new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
    }

    private void ShowDialogLimitCreate() {
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.title_limit_Create_Group).content(R.string.text_limit_Create_Group).positiveText(R.string.B_ok).show();
    }

    private void chatToGroup() {
        G.onChatConvertToGroup = new OnChatConvertToGroup() {
            @Override
            public void onChatConvertToGroup(long roomId, final String name, final String description, ProtoGlobal.GroupRoom.Role role) {
                getRoom(roomId, ProtoGlobal.Room.Type.GROUP);
            }

            @Override
            public void Error(int majorCode, int minorCode) {

                hideProgressBar();
            }

            @Override
            public void timeOut() {
                hideProgressBar();
            }
        };

        new RequestChatConvertToGroup().chatConvertToGroup(groomId, edtSetNewGroup.get(), edtDescription.get());
    }

    private void createGroup() {
        G.onGroupCreate = new OnGroupCreate() {
            @Override
            public void onGroupCreate(final long roomIdR) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        roomId = roomIdR;
                        hideProgressBar();
                        getRoom(roomIdR, ProtoGlobal.Room.Type.GROUP);
                    }
                });

               /* G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (avatarExist) {
                            new RequestGroupAvatarAdd().groupAvatarAdd(roomId, fileUploadStructure.token);
                        } else {
                            G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            getRoom(roomId, ProtoGlobal.Room.Type.GROUP);
                        }
                    }
                });*/

            }

            @Override
            public void onTimeOut() {
                hideProgressBar();
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                hideProgressBar();
                if (majorCode == 380) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ShowDialogLimitCreate();
                        }
                    });
                }
            }
        };

        new RequestGroupCreate().groupCreate(edtSetNewGroup.get(), edtDescription.get());
    }

    private void getRoom(final long roomId, final ProtoGlobal.Room.Type typeCreate) {

        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(final ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, RequestClientGetRoom.IdentityClientGetRoom identity) {

                if (identity.createRoomMode != RequestClientGetRoom.CreateRoomMode.requestFromOwner) {
                    return;
                }

                try {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (existAvatar) {
                                showProgressBar();
                                if (room.getType() == ProtoGlobal.Room.Type.GROUP) {
                                    new RequestGroupAvatarAdd().groupAvatarAdd(roomId, token);
                                } else {
                                    new RequestChannelAvatarAdd().channelAvatarAdd(roomId, token);
                                }
                            } else {
                                Fragment fragment = ContactGroupFragment.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putLong("RoomId", roomId);

                                if (room.getType() == ProtoGlobal.Room.Type.GROUP) {
                                    bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                } else {
                                    bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                }
                                bundle.putString("TYPE", typeCreate.toString());
                                bundle.putBoolean("NewRoom", true);
                                fragment.setArguments(bundle);

                                if (FragmentNewGroup.onRemoveFragmentNewGroup != null)
                                    FragmentNewGroup.onRemoveFragmentNewGroup.onRemove();
                                new HelperFragment(fragment).load();
                            }
                        }
                    });
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

                hideProgressBar();
            }

            @Override
            public void onTimeOut() {

                hideProgressBar();
            }
        };

        new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
    }

    //=======================result for picture

    public void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                nextStepEnable.set(false);
                prgWaiting.set(View.VISIBLE);
                if (G.fragmentActivity != null)
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    public void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                nextStepEnable.set(true);
                prgWaiting.set(View.GONE);
                if (G.fragmentActivity != null)
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }
}
