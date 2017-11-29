/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCrop;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnChannelAvatarAdd;
import net.iGap.interfaces.OnChannelCreate;
import net.iGap.interfaces.OnChatConvertToGroup;
import net.iGap.interfaces.OnClientGetRoomResponse;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnGroupCreate;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.IntentRequests;
import net.iGap.module.LinedEditText;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.proto.ProtoClientGetRoom;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChannelAvatarAdd;
import net.iGap.request.RequestChannelCreate;
import net.iGap.request.RequestChatConvertToGroup;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestGroupAvatarAdd;
import net.iGap.request.RequestGroupCreate;

import static net.iGap.G.context;
import static net.iGap.module.AttachFile.isInAttach;
import static net.iGap.module.AttachFile.request_code_TAKE_PICTURE;
import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;
import static net.iGap.module.MusicPlayer.roomId;

public class FragmentNewGroup extends BaseFragment implements OnGroupAvatarResponse, OnChannelAvatarAdd {

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

    public static FragmentNewGroup newInstance() {
        return new FragmentNewGroup();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_new_group, container, false));
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
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.choose_picture)).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
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

                                HelperPermision.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                                    @Override
                                    public void Allow() throws IOException {
                                        HelperPermision.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
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
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                //                                            new AttachFile(FragmentNewGroup.this.G.fragmentActivity).dispatchTakePictureIntent();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(G.fragmentActivity.getPackageManager()) != null) {
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
                        uriIntent = FileProvider.getUriForFile(G.fragmentActivity, G.fragmentActivity.getApplicationContext().getPackageName() + ".provider", createImageFile());
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
        AppUtils.setProgresColler(prgWaiting);

        view.findViewById(R.id.ng_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        prgWaiting.setVisibility(View.GONE);
        G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                G.fragmentActivity.onBackPressed();
            }
        });

        txtTitleToolbar = (TextView) view.findViewById(R.id.ng_txt_titleToolbar);
        if (prefix.equals("NewChanel")) {
            txtTitleToolbar.setText(G.fragmentActivity.getResources().getString(R.string.New_Chanel));
        } else if (prefix.equals("ConvertToGroup")) {
            txtTitleToolbar.setText(G.fragmentActivity.getResources().getString(R.string.chat_to_group));
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

                HelperPermision.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
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
                    ViewGroupName.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                } else {
                    ViewGroupName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        switch (prefix) {
            case "NewChanel":
                txtInputNewGroup.setHint(G.fragmentActivity.getResources().getString(R.string.channel_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
                break;
            case "ConvertToGroup":
                txtInputNewGroup.setHint(G.fragmentActivity.getResources().getString(R.string.group_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
                break;
            default:
                txtInputNewGroup.setHint(G.fragmentActivity.getResources().getString(R.string.group_name) + " " + G.fragmentActivity.getResources().getString(R.string.mandatory));
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
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String newName = edtGroupName.getText().toString().replace(" ", "_");
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
                    G.fragmentActivity.onBackPressed();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
    }

    private void createChannel() {

        G.onChannelCreate = new OnChannelCreate() {
            @Override
            public void onChannelCreate(final long roomIdR, final String inviteLink, final String channelName) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        RealmChannelRoom.createChannelRoom(roomIdR, inviteLink, channelName);
                        mInviteLink = inviteLink;
                        if (existAvatar) {
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

                            popBackStackFragment();
                            new HelperFragment(fragmentCreateChannel).load();
                        }
                    }
                });

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

        new RequestChannelCreate().channelCreate(edtGroupName.getText().toString(), edtDescription.getText().toString());
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

        new RequestChatConvertToGroup().chatConvertToGroup(groomId, edtGroupName.getText().toString(), edtDescription.getText().toString());
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

        new RequestGroupCreate().groupCreate(edtGroupName.getText().toString(), edtDescription.getText().toString());
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

                                popBackStackFragment();
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

    private void showInitials() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        imgCircleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));

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

        realm.close();
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
                    @Override
                    public void run() {
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

        popBackStackFragment();
        new HelperFragment(fragment).load();
    }

    private void startChannelRoom(long roomId) {
        hideProgressBar();
        FragmentCreateChannel fragmentCreateChannel = new FragmentCreateChannel();
        Bundle bundle = new Bundle();
        bundle.putLong("ROOMID", roomId);
        bundle.putString("INVITE_LINK", mInviteLink);
        bundle.putString("TOKEN", token);
        fragmentCreateChannel.setArguments(bundle);

        popBackStackFragment();
        new HelperFragment(fragmentCreateChannel).load();
    }

    //=======================result for picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_code_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                ImageHelper.correctRotateImage(mCurrentPhotoPath, true);
                intent.putExtra("IMAGE_CAMERA", mCurrentPhotoPath);
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", prefix);
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            } else {

                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
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

                Intent intent = new Intent(G.fragmentActivity, ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image));
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
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        if (progress < 100) {
                            prgWaiting.setProgress(progress);
                        } else {
                            hideProgressBar();
                            existAvatar = true;
                            token = struct.token;
                            setImage(pathSaveImage);
                        }
                    }

                    @Override
                    public void OnError() {
                        hideProgressBar();
                    }
                });
            }
        }
    }

    private String pathSaveImage;


    //***Show And Hide ProgressBar
    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                txtNextStep.setEnabled(false);
                prgWaiting.setVisibility(View.VISIBLE);
                if (G.fragmentActivity != null) G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                txtNextStep.setEnabled(true);
                prgWaiting.setVisibility(View.GONE);
                if (G.fragmentActivity != null) G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
