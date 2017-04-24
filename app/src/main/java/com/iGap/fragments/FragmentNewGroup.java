/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityCrop;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.HelperUploadFile;
import com.iGap.helper.ImageHelper;
import com.iGap.interfaces.OnAvatarAdd;
import com.iGap.interfaces.OnChannelAvatarAdd;
import com.iGap.interfaces.OnChannelCreate;
import com.iGap.interfaces.OnChatConvertToGroup;
import com.iGap.interfaces.OnClientGetRoomResponse;
import com.iGap.interfaces.OnGetPermission;
import com.iGap.interfaces.OnGroupAvatarResponse;
import com.iGap.interfaces.OnGroupCreate;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AttachFile;
import com.iGap.module.CircleImageView;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.IntentRequests;
import com.iGap.module.LinedEditText;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestChannelAvatarAdd;
import com.iGap.request.RequestChannelCreate;
import com.iGap.request.RequestChatConvertToGroup;
import com.iGap.request.RequestClientGetRoom;
import com.iGap.request.RequestGroupAvatarAdd;
import com.iGap.request.RequestGroupCreate;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.iGap.G.context;
import static com.iGap.R.id.fragmentContainer;
import static com.iGap.module.AttachFile.isInAttach;
import static com.iGap.module.AttachFile.request_code_TAKE_PICTURE;
import static com.iGap.module.AttachFile.request_code_image_from_gallery_single_select;
import static com.iGap.module.MusicPlayer.roomId;

public class FragmentNewGroup extends Fragment implements OnGroupAvatarResponse, OnChannelAvatarAdd {

    private MaterialDesignTextView txtBack;
    private CircleImageView imgCircleImageView;
    private Uri uriIntent;
    private TextView txtNextStep, txtCancel, txtTitleToolbar;
    private static String prefix = "NewGroup";
    private long groomId = 0;
    //  private String path;
    private RelativeLayout parent;

    private EditText edtGroupName;
    private LinedEditText edtDescription;

    private int lastSpecialRequestsCursorPosition = 0;
    private String specialRequests;

    public static ProgressBar prgWaiting;
    public static long avatarId = 0;
    private static ProtoGlobal.Room.Type type;
    private String token;
    private boolean existAvatar = false;
    private String mInviteLink;
    private boolean isChannel = false;
    public static String mCurrentPhotoPath;
    private AttachFile attachFile;
    private FragmentActivity mActivity;
    public static FragmentNewGroup newInstance() {
        return new FragmentNewGroup();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_new_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getIntentData(this.getArguments());
        initComponent(view);
    }

    private void getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image
            prefix = bundle.getString("TYPE");
            if (bundle.getLong("ROOMID") != 0) {
                groomId = bundle.getLong("ROOMID");
            }
        }
    }

    private void showDialogSelectGallery() {
        new MaterialDialog.Builder(getActivity()).title(getString(R.string.choose_picture))
                .negativeText(getString(R.string.cancel))
                .items(R.array.profile)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0: {

                                try {
                                    HelperPermision.getStoragePermision(context, new OnGetPermission() {
                                        @Override
                                        public void Allow() {

                                            if (isAdded()) { // boolean isAdded () Return true if the fragment is currently added to its activity.
                                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            intent.setType("image/*");
                                            startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                                            isInAttach = true;
                                            }
                                        }

                                        @Override
                                        public void deny() {

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            case 1: {

                                if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                    try {

                                        HelperPermision.getStoragePermision(getActivity(), new OnGetPermission() {
                                            @Override
                                            public void Allow() throws IOException {
                                                HelperPermision.getCameraPermission(getActivity(), new OnGetPermission() {
                                                    @Override
                                                    public void Allow() {
                                                        // this dialog show 2 way for choose image : gallery and camera

                                                        if (isAdded()) {
                                                            useCamera();
                                                        }
                                                        dialog.dismiss();
                                                    }

                                                    @Override
                                                    public void deny() {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void deny() {

                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Toast.makeText(context, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }

                    }
                })
                .show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
//                                            new AttachFile(FragmentNewGroup.this.getActivity()).dispatchTakePictureIntent();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        return;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        uriIntent = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", createImageFile());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                        startActivityForResult(takePictureIntent, request_code_TAKE_PICTURE);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (prefix.equals("NewChanel")) {
                uriIntent = Uri.fromFile(G.IMAGE_NEW_CHANEL);
            } else {
                uriIntent = Uri.fromFile(G.IMAGE_NEW_GROUP);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
            startActivityForResult(intent, request_code_TAKE_PICTURE);
        }
    }

    public void initComponent(View view) {
        G.onGroupAvatarResponse = this;
        G.onChannelAvatarAdd = this;

        prgWaiting = (ProgressBar) view.findViewById(R.id.ng_prgWaiting);
        prgWaiting.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);

        view.findViewById(R.id.ng_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.ang_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

        prgWaiting.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        txtBack = (MaterialDesignTextView) view.findViewById(R.id.ng_txt_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.ng_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleView.getWindowToken(), 0);
                if (G.IMAGE_NEW_GROUP.exists()) {
                    G.IMAGE_NEW_GROUP.delete();
                } else {
                    G.IMAGE_NEW_CHANEL.delete();
                }
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
            }
        });

        txtTitleToolbar = (TextView) view.findViewById(R.id.ng_txt_titleToolbar);
        if (prefix.equals("NewChanel")) {
            txtTitleToolbar.setText(getResources().getString(R.string.New_Chanel));
        } else if (prefix.equals("ConvertToGroup")) {
            txtTitleToolbar.setText(getResources().getString(R.string.chat_to_group));
        }

        parent = (RelativeLayout) view.findViewById(R.id.ng_fragmentContainer);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //=======================set image for group
        imgCircleImageView = (CircleImageView) view.findViewById(R.id.ng_profile_circle_image);
        AndroidUtils.setBackgroundShapeColor(imgCircleImageView, Color.parseColor(G.appBarColor));

        RippleView rippleCircleImage = (RippleView) view.findViewById(R.id.ng_ripple_circle_image);
        rippleCircleImage.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) throws IOException {

                HelperPermision.getStoragePermision(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() {
                        showDialogSelectGallery();
                    }

                    @Override
                    public void deny() {

                    }
                });

            }
        });

        //if (prefix.equals("NewChanel")) {
        //    path = G.DIR_NEW_CHANEL;
        //} else {
        //    path = G.DIR_NEW_GROUP;
        //}

        //=======================name of group
        TextInputLayout txtInputNewGroup = (TextInputLayout) view.findViewById(R.id.ng_txtInput_newGroup);

        edtGroupName = (EditText) view.findViewById(R.id.ng_edt_newGroup);
        final View ViewGroupName = view.findViewById(R.id.ng_view_newGroup);
        edtGroupName.setPadding(0, 8, 0, 8);
        edtGroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    ViewGroupName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    ViewGroupName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        switch (prefix) {
            case "NewChanel":
                txtInputNewGroup.setHint(getResources().getString(R.string.new_channel));
                break;
            case "ConvertToGroup":
                txtInputNewGroup.setHint(getResources().getString(R.string.group_name));
                break;
            default:
                txtInputNewGroup.setHint(getResources().getString(R.string.group_name));
                break;
        }

        //=======================description group
        edtDescription = (LinedEditText) view.findViewById(R.id.ng_edt_description);
        edtDescription.setPadding(0, 8, 0, 8);

        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastSpecialRequestsCursorPosition = edtDescription.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtDescription.removeTextChangedListener(this);

                if (edtDescription.getLineCount() > 4) {
                    edtDescription.setText(specialRequests);
                    edtDescription.setSelection(lastSpecialRequestsCursorPosition);
                } else {
                    specialRequests = edtDescription.getText().toString();
                }

                edtDescription.addTextChangedListener(this);
            }
        });

        edtDescription.setSingleLine(false);
        edtDescription.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        edtDescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edtDescription.setLines(4);
        edtDescription.setMaxLines(4);
        //=======================button next step

        txtNextStep = (TextView) view.findViewById(R.id.ng_txt_nextStep);
        txtNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtGroupName.getText().toString().length() > 0) {
                    prgWaiting.setVisibility(View.VISIBLE);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String newName = edtGroupName.getText().toString().replace(" ", "_");
                    //  File file2 = new File(path, prefix + "_" + newName + Math.random() * 10000 + 1 + ".png");
                    if (prefix.equals("NewChanel")) {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        });
        //=======================button cancel
        txtCancel = (TextView) view.findViewById(R.id.ng_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (G.IMAGE_NEW_GROUP.exists()) {
                    G.IMAGE_NEW_GROUP.delete();
                } else {
                    G.IMAGE_NEW_CHANEL.delete();
                }
                try {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
    }

    /**
     * create room with empty info , just Id and inviteLink
     *
     * @param roomId     roomId
     * @param inviteLink inviteLink
     */

    public static void createChannelRoom(final long roomId, final String inviteLink) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.createObject(RealmRoom.class, roomId);

                RealmChannelRoom realmChannelRoom = realm.createObject(RealmChannelRoom.class);
                realmChannelRoom.setInviteLink(inviteLink);

                realmRoom.setChannelRoom(realmChannelRoom);
            }
        });
        realm.close();
    }

    private void createChannel() {

        G.onChannelCreate = new OnChannelCreate() {
            @Override
            public void onChannelCreate(final long roomIdR, final String inviteLink) {
                G.handler.post(new Runnable() {
                    @Override public void run() {
                        createChannelRoom(roomIdR, inviteLink);
                        mInviteLink = inviteLink;
                        if (existAvatar) {
                            //                            showProgressBar();
                            mInviteLink = inviteLink;
                            new RequestChannelAvatarAdd().channelAvatarAdd(roomIdR, token);

                        } else {

                            hideProgressBar();
                            FragmentCreateChannel fragmentCreateChannel = new FragmentCreateChannel();
                            Bundle bundle = new Bundle();
                            bundle.putLong("ROOMID", roomIdR);
                            bundle.putString("INVITE_LINK", inviteLink);
                            bundle.putString("TOKEN", token);
                            fragmentCreateChannel.setArguments(bundle);
                            mActivity.getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                .replace(fragmentContainer, fragmentCreateChannel, "createChannel_fragment")
                                .commitAllowingStateLoss();
                            getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
                            //                        getRoom(roomIdR, ProtoGlobal.Room.Type.CHANNEL);
                        }
                    }
                });
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

        new RequestChannelCreate().channelCreate(edtGroupName.getText().toString(), edtDescription.getText().toString());
    }

    private void chatToGroup() {
        G.onChatConvertToGroup = new OnChatConvertToGroup() {
            @Override
            public void onChatConvertToGroup(long roomId, final String name, final String description, ProtoGlobal.GroupRoom.Role role) {

//                if (existAvatar) {
//                    new RequestGroupAvatarAdd().groupAvatarAdd(roomId, fileUploadStructure.token);
////                    getActivity().runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            Realm realm = Realm.getDefaultInstance();
////                            realm.executeTransaction(new Realm.Transaction() {
////                                @Override
////                                public void execute(Realm realm) {
////                                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, groomId).findFirst();
////                                    realmRoom.setId(roomId);
////                                    realmRoom.setType(RoomType.GROUP);
////                                    realmRoom.setTitle(name);
////                                    RealmGroupRoom realmGroupRoom = realm.createObject(RealmGroupRoom.class);
////                                    realmGroupRoom.setRole(GroupChatRole.OWNER);
////                                    realmGroupRoom.setDescription(description);
////                                    realmGroupRoom.setParticipantsCountLabel("2");
////                                    realmRoom.setGroupRoom(realmGroupRoom);
////                                    realmRoom.setChatRoom(null);
////                                }
////                            });
////                            realm.close();
////                        }
////                    });
//                } else {
//                    G.handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                        }
//                    });
//                    getRoom(roomId, ProtoGlobal.Room.Type.GROUP);
//                }

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

        new RequestChatConvertToGroup().chatConvertToGroup(groomId, edtGroupName.getText().toString(), edtDescription.getText().toString());
    }

    private void createGroup() {
        G.onGroupCreate = new OnGroupCreate() {
            @Override
            public void onGroupCreate(final long roomIdR) {

                G.handler.post(new Runnable() {
                    @Override public void run() {
                        roomId = roomIdR;
                        hideProgressBar();
                        getRoom(roomIdR, ProtoGlobal.Room.Type.GROUP);
                    }
                });

               /* getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (avatarExist) {
                            new RequestGroupAvatarAdd().groupAvatarAdd(roomId, fileUploadStructure.token);
                        } else {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
            }
        };

        new RequestGroupCreate().groupCreate(edtGroupName.getText().toString(), edtDescription.getText().toString());
    }

    private void getRoom(final long roomId, final ProtoGlobal.Room.Type typeCreate) {

        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override public void onClientGetRoomResponse(final ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, String identity) {

                if (!identity.equals(RequestClientGetRoom.CreateRoomMode.requestFromOwner.toString())) return;

                try {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
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
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                            .replace(fragmentContainer, fragment, "contactGroup_fragment")
                                            .commitAllowingStateLoss();
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
                                    //ActivityMain.mLeftDrawerLayout.closeDrawer();
                                }
                            }
                        });
                    }
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

    private void showInitials() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        imgCircleImageView.setImageBitmap(
                HelperImageBackColor.drawAlphabetOnPicture(
                        (int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp100)
                        , realmUserInfo.getUserInfo().getInitials()
                        , realmUserInfo.getUserInfo().getColor()));

        realm.close();
    }

    private void setImage(long roomId) {
        final Realm realm = Realm.getDefaultInstance();

        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst();
        if (realmAvatar != null) {
            imgCircleImageView.setPadding(0, 0, 0, 0);
            if (realmAvatar.getFile().isFileExistsOnLocal()) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalFilePath()), imgCircleImageView);
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalThumbnailPath()), imgCircleImageView);
            } else {
                showInitials();
            }
        } else {
            showInitials();
            imgCircleImageView.setPadding(0, 0, 0, 0);
        }
    }

    private boolean avatarExist = false;
    private FileUploadStructure fileUploadStructure;

    private void setImage(final String imagePath) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (imagePath != null && new File(imagePath).exists()) {
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgCircleImageView);
                } else {
                    showInitials();
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                }
            }
        });
    }



    @Override
    public void onAvatarAdd(final long roomId, final ProtoGlobal.Avatar avatar) {

        HelperAvatar.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
            @Override
            public void onAvatarAdd(final String avatarPath) {

                G.handler.post(new Runnable() {
                    @Override public void run() {
                        hideProgressBar();
                        setImage(avatarPath);

                        if (isChannel) {
                            startChannelRoom(roomId);
                        } else {
                            startRoom(roomId);
                        }
                    }
                });
            }
        });


        /*if (prefix.equals("NewChanel")) {
            HelperAvatar.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            txtNextStep.setEnabled(true);
                            prgWaiting.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            setImage(avatarPath);
                            startRoom();
                        }
                    });

                }
            });

        } else {
            Realm realm = Realm.getDefaultInstance();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmAvatar realmAvatar = realm.createObject(RealmAvatar.class, avatar.getId());
                    realmAvatar.setOwnerId(roomId);
                    realmAvatar.setFile(RealmAttachment.build(avatar.getFile(), AttachmentFor.AVATAR, null));

                    try {
                        AndroidUtils.copyFile(new File(pathSaveImage), new File(G.DIR_IMAGE_USER + "/" + avatar.getFile().getToken() + "_" + avatar.getFile().getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            realm.close();

            // have to be inside a delayed handler
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setImage(roomId);
                }
            }, 500);

            getRoom(roomId, ProtoGlobal.Room.Type.GROUP);
        }*/
    }

    @Override
    public void onAvatarAddError() {
        hideProgressBar();
    }

    private void startRoom(long roomId) {
        Fragment fragment = ContactGroupFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong("RoomId", roomId);

        if (prefix.equals("NewChanel")) {
            bundle.putString("TYPE", ProtoGlobal.Room.Type.CHANNEL.toString());
        } else {
            bundle.putString("TYPE", ProtoGlobal.Room.Type.GROUP.toString());
        }

        bundle.putBoolean("NewRoom", true);
        fragment.setArguments(bundle);
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(fragmentContainer, fragment, "contactGroup_fragment")
                .commitAllowingStateLoss();
        mActivity.getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
        //ActivityMain.mLeftDrawerLayout.closeDrawer();
    }

    private void startChannelRoom(long roomId) {
        hideProgressBar();
        FragmentCreateChannel fragmentCreateChannel = new FragmentCreateChannel();
        Bundle bundle = new Bundle();
        bundle.putLong("ROOMID", roomId);
        bundle.putString("INVITE_LINK", mInviteLink);
        bundle.putString("TOKEN", token);
        fragmentCreateChannel.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(fragmentContainer, fragmentCreateChannel, "createChannel_fragment")
                .commitAllowingStateLoss();
        getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
    }



    //=======================result for picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_code_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(getActivity(), ActivityCrop.class);
                ImageHelper.correctRotateImage(mCurrentPhotoPath, true);
                intent.putExtra("IMAGE_CAMERA", mCurrentPhotoPath);
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", prefix);
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            } else {

                Intent intent = new Intent(getActivity(), ActivityCrop.class);
                if (uriIntent != null) {
                    intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
                    intent.putExtra("TYPE", "camera");
                    intent.putExtra("PAGE", prefix);
                    startActivityForResult(intent, IntentRequests.REQ_CROP);
                } else {
                    Toast.makeText(context, R.string.can_not_save_picture_pleas_try_again, Toast.LENGTH_SHORT).show();
                }
            }


        } else if (requestCode == request_code_image_from_gallery_single_select && resultCode == Activity.RESULT_OK) {// result for gallery
            if (data != null) {

                Intent intent = new Intent(getActivity(), ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUri(data.getData()));
                intent.putExtra("TYPE", "gallery");
                intent.putExtra("PAGE", prefix);
                startActivityForResult(intent, IntentRequests.REQ_CROP);


            }
        } else if (requestCode == IntentRequests.REQ_CROP) {

            if (data != null) {
                pathSaveImage = data.getData().toString();
                avatarId = System.nanoTime();

                showProgressBar();
                HelperUploadFile.startUploadTaskAvatar(pathSaveImage, avatarId, new HelperUploadFile.UpdateListener() {
                    @Override public void OnProgress(int progress, FileUploadStructure struct) {
                        if (progress < 100) {
                            prgWaiting.setProgress(progress);
                        } else {
                            hideProgressBar();
                            existAvatar = true;
                            token = struct.token;
                            setImage(pathSaveImage);
                        }
                    }

                    @Override public void OnError() {
                        hideProgressBar();
                    }
                });





            }
        }
    }

    private String pathSaveImage;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //***Show And Hide ProgressBar
    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                txtNextStep.setEnabled(false);
                prgWaiting.setVisibility(View.VISIBLE);
                if (getActivity() != null)
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                txtNextStep.setEnabled(true);
                prgWaiting.setVisibility(View.GONE);
                if (getActivity() != null)
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }
}
