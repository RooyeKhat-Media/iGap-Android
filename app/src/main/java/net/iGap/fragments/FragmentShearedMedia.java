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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnClientSearchRoomHistory;
import net.iGap.interfaces.OnComplete;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.PreCachingLayoutManager;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.proto.ProtoClientCountRoomHistory;
import net.iGap.proto.ProtoClientSearchRoomHistory;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestClientCountRoomHistory;
import net.iGap.request.RequestClientSearchRoomHistory;

import org.parceler.Parcels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.fragmentActivity;
import static net.iGap.module.AndroidUtils.suitablePath;

public class FragmentShearedMedia extends BaseFragment {

    private static final String ROOM_ID = "RoomId";
    public static ArrayList<Long> list = new ArrayList<>();
    private static long countOFImage = 0;
    private static long countOFVIDEO = 0;
    private static long countOFAUDIO = 0;
    private static long countOFVOICE = 0;
    private static long countOFGIF = 0;
    private static long countOFFILE = 0;
    private static long countOFLink = 0;
    public ArrayList<Long> SelectedList = new ArrayList<>();
    public ArrayList<Long> bothDeleteMessageId = new ArrayList<>();
    protected ArrayMap<Long, Boolean> needDownloadList = new ArrayMap<>();
    private RealmResults<RealmRoomMessage> mRealmList;
    private ArrayList<StructShearedMedia> mNewList;
    private RealmChangeListener<RealmResults<RealmRoomMessage>> changeListener;
    private ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter mFilter;
    private RecyclerView recyclerView;
    private RecyclerView.OnScrollListener onScrollListener;
    private mAdapter adapter;
    private AppBarLayout appBarLayout;
    private OnComplete complete;
    private ProgressBar progressBar;
    private Realm realmShearedMedia;
    private LinearLayout ll_AppBarSelected;
    private LinearLayout mediaLayout;
    private TextView txtSharedMedia;
    private TextView txtNumberOfSelected;
    private ProtoGlobal.Room.Type roomType;
    private int numberOfSelected = 0;
    private int listCount = 0;
    private int changeSize = 0;
    private int spanItemCount = 3;
    private int offset;
    private boolean isChangeSelectType = false;
    private boolean canUpdateAfterDownload = false;
    private boolean isSelectedMode = false; // for determine user select some file
    private boolean isSendRequestForLoading = false;
    private boolean isThereAnyMoreItemToLoad = false;
    private long nextMessageId = 0;
    private long roomId = 0;

    public static FragmentShearedMedia newInstance(long roomId) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        FragmentShearedMedia fragment = new FragmentShearedMedia();
        fragment.setArguments(args);
        return fragment;
    }

    public static void updateStringSharedMediaCount(ProtoClientCountRoomHistory.ClientCountRoomHistoryResponse.Builder proto, long roomId) {

        if (proto != null) {

            countOFImage = proto.getImage();
            countOFVIDEO = proto.getVideo();
            countOFAUDIO = proto.getAudio();
            countOFVOICE = proto.getVoice();
            countOFGIF = proto.getGif();
            countOFFILE = proto.getFile();
            countOFLink = proto.getUrl();

            String result = countOFImage + "\n" + countOFVIDEO + "\n" + countOFAUDIO + "\n" + countOFVOICE + "\n" + countOFGIF + "\n" + countOFFILE + "\n" + countOFLink;

            RealmRoom.setCountShearedMedia(roomId, result);
        }
    }

    public static void getCountOfSharedMedia(long roomId) {

        new RequestClientCountRoomHistory().clientCountRoomHistory(roomId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_sheared_media, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        realmShearedMedia = Realm.getDefaultInstance();
        mediaLayout = (LinearLayout) view.findViewById(R.id.asm_ll_music_layout);
        MusicPlayer.setMusicPlayer(mediaLayout);

        roomId = getArguments().getLong(ROOM_ID);
        roomType = RealmRoom.detectType(roomId);

        initComponent(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        canUpdateAfterDownload = true;

        setListener();

        MusicPlayer.shearedMediaLayout = mediaLayout;
        ActivityMain.setMediaLayout();
    }

    @Override
    public void onStop() {
        super.onStop();

        canUpdateAfterDownload = false;

        if (mRealmList != null) {
            mRealmList.removeAllChangeListeners();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realmShearedMedia != null && !realmShearedMedia.isClosed()) {
            realmShearedMedia.close();
        }

        MusicPlayer.shearedMediaLayout = null;

        ActivityMain.setMediaLayout();
    }

    private Realm getRealm() {
        if (realmShearedMedia == null || realmShearedMedia.isClosed()) {
            realmShearedMedia = Realm.getDefaultInstance();
        }
        return realmShearedMedia;
    }

    private void initComponent(View view) {

        progressBar = (ProgressBar) view.findViewById(R.id.asm_progress_bar_waiting);
        AppUtils.setProgresColler(progressBar);

        appBarLayout = (AppBarLayout) view.findViewById(R.id.asm_appbar_shared_media);

        view.findViewById(R.id.asm_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBack = (RippleView) view.findViewById(R.id.asm_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });

        RippleView rippleMenu = (RippleView) view.findViewById(R.id.asm_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popUpMenuSharedMedia();
            }
        });

        txtSharedMedia = (TextView) view.findViewById(R.id.asm_txt_sheared_media);

        complete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                int whatAction = 0;
                String number = "0";

                if (messageOne != null) {
                    if (messageOne.length() > 0) whatAction = Integer.parseInt(messageOne);
                }

                if (MessageTow != null) if (MessageTow.length() > 0) number = MessageTow;

                callBack(result, whatAction, number);
            }
        };

        recyclerView = (RecyclerView) view.findViewById(R.id.asm_recycler_view_sheared_media);
        recyclerView.setItemViewCacheSize(400);
        recyclerView.setItemAnimator(null);


        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (lastVisiblePosition + 30 >= offset) {

                            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, nextMessageId, mFilter);

                            isSendRequestForLoading = true;
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };

        recyclerView.addOnScrollListener(onScrollListener);

        openLayout();
        initAppbarSelected(view);
    }

    private void openLayout() {

        if (countOFImage > 0) {
            fillListImage();
        } else if (countOFVIDEO > 0) {
            fillListVideo();
        } else if (countOFAUDIO > 0) {
            fillListAudio();
        } else if (countOFVOICE > 0) {
            fillListVoice();
        } else if (countOFGIF > 0) {
            fillListGif();
        } else if (countOFFILE > 0) {
            fillListFile();
        } else if (countOFFILE > 0) {
            fillListLink();
        } else {
            fillListImage();
        }
    }

    private void initAppbarSelected(View view) {
        RippleView rippleCloseAppBarSelected = (RippleView) view.findViewById(R.id.asm_ripple_close_layout);
        rippleCloseAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.resetSelected();
            }
        });

        MaterialDesignTextView btnForwardSelected = (MaterialDesignTextView) view.findViewById(R.id.asm_btn_forward_selected);
        btnForwardSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Parcelable> messageInfos = new ArrayList<>(SelectedList.size());
                RealmRoomMessage rm;
                for (Long Id : SelectedList) {

                    rm = mRealmList.where().equalTo(RealmRoomMessageFields.MESSAGE_ID, Id).findFirst();
                    if (rm != null) {
                        //+Realm realm = Realm.getDefaultInstance();
                        messageInfos.add(Parcels.wrap(StructMessageInfo.convert(getRealm(), rm)));
                    }
                }
                FragmentChat.mForwardMessages = messageInfos;

                popBackStackFragment();
                if (FragmentChat.finishActivity != null) {
                    FragmentChat.finishActivity.finishActivity();
                }
                adapter.resetSelected();
            }
        });

        RippleView rippleDeleteSelected = (RippleView) view.findViewById(R.id.asm_riple_delete_selected);
        rippleDeleteSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                String count = SelectedList.size() + "";
                final RealmRoom realmRoom = RealmRoom.getRealmRoom(getRealm(), roomId);

                if (roomType == ProtoGlobal.Room.Type.CHAT && bothDeleteMessageId != null && bothDeleteMessageId.size() > 0) {
                    // show both Delete check box
                    String delete;
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
                            if (realmRoom != null) {
                                RealmRoomMessage.deleteSelectedMessages(getRealm(), roomId, SelectedList, bothDeleteMessageId, roomType);
                            }
                            resetItems();
                        }
                    }).checkBoxPromptRes(R.string.delete_item_dialog, false, null).show();

                } else {
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.message).content(G.context.getResources().getString(R.string.st_desc_delete, count)).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            bothDeleteMessageId = null;
                            if (realmRoom != null) {
                                RealmRoomMessage.deleteSelectedMessages(getRealm(), roomId, SelectedList, bothDeleteMessageId, roomType);
                            }
                            resetItems();
                        }
                    }).show();
                }
            }
        });

        txtNumberOfSelected = (TextView) view.findViewById(R.id.asm_txt_number_of_selected);

        ll_AppBarSelected = (LinearLayout) view.findViewById(R.id.asm_ll_appbar_selelected);
    }

    private void resetItems() {
        for (Long Id : SelectedList) {
            list.add(Id);
        }

        switch (mFilter) {
            case IMAGE:
                countOFImage -= SelectedList.size();
                break;
            case VIDEO:
                countOFVIDEO -= SelectedList.size();
                break;
            case AUDIO:
                countOFAUDIO -= SelectedList.size();
                break;
            case FILE:
                countOFFILE -= SelectedList.size();
                break;
            case GIF:
                countOFGIF -= SelectedList.size();
                break;
            case VOICE:
                countOFVOICE -= SelectedList.size();
                break;
            case URL:
                countOFLink -= SelectedList.size();
                break;
        }

        updateStringSharedMediaCount(null, roomId);

        adapter.resetSelected();
    }

    //********************************************************************************************

    private void callBack(boolean result, int whatAction, String number) {

        switch (whatAction) {

            case 1://for show or gone layout appBar selected
                if (result) {
                    ll_AppBarSelected.setVisibility(View.VISIBLE);
                    txtNumberOfSelected.setText(number);
                } else {
                    ll_AppBarSelected.setVisibility(View.GONE);
                }
                break;
        }
    }

    //********************************************************************************************

    public void popUpMenuSharedMedia() {

        final MaterialDialog dialog = new MaterialDialog.Builder(fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();
        if (v == null) {
            return;
        }

        DialogAnimation.animationUp(dialog);
        dialog.show();

        ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
        ViewGroup root3 = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);
        ViewGroup root4 = (ViewGroup) v.findViewById(R.id.dialog_root_item4_notification);
        ViewGroup root5 = (ViewGroup) v.findViewById(R.id.dialog_root_item5_notification);
        ViewGroup root6 = (ViewGroup) v.findViewById(R.id.dialog_root_item6_notification);
        ViewGroup root7 = (ViewGroup) v.findViewById(R.id.dialog_root_item7_notification);

        TextView txtImage = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtVideo = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
        TextView txtAudio = (TextView) v.findViewById(R.id.dialog_text_item3_notification);
        TextView txtVoice = (TextView) v.findViewById(R.id.dialog_text_item4_notification);
        TextView txtGif = (TextView) v.findViewById(R.id.dialog_text_item5_notification);
        TextView txtFile = (TextView) v.findViewById(R.id.dialog_text_item6_notification);
        TextView txtLink = (TextView) v.findViewById(R.id.dialog_text_item7_notification);

        TextView iconImage = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
        iconImage.setText(G.fragmentActivity.getResources().getString(R.string.md_photo));

        TextView iconVideo = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconVideo.setText(G.fragmentActivity.getResources().getString(R.string.md_videocam));

        TextView iconAudio = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
        iconAudio.setText(G.fragmentActivity.getResources().getString(R.string.icon_music));

        TextView iconVoice = (TextView) v.findViewById(R.id.dialog_icon_item4_notification);
        iconVoice.setText(G.fragmentActivity.getResources().getString(R.string.md_surround_sound));

        TextView iconGif = (TextView) v.findViewById(R.id.dialog_icon_item5_notification);
        iconGif.setText(G.fragmentActivity.getResources().getString(R.string.md_emby));

        TextView iconFile = (TextView) v.findViewById(R.id.dialog_icon_item6_notification);
        iconFile.setText(G.fragmentActivity.getResources().getString(R.string.icon_file));

        TextView iconLink = (TextView) v.findViewById(R.id.dialog_icon_item7_notification);
        iconLink.setText(G.fragmentActivity.getResources().getString(R.string.md_link));

        root1.setVisibility(View.VISIBLE);
        root2.setVisibility(View.VISIBLE);
        root3.setVisibility(View.VISIBLE);
        root4.setVisibility(View.VISIBLE);
        root5.setVisibility(View.VISIBLE);
        root6.setVisibility(View.VISIBLE);
        root7.setVisibility(View.VISIBLE);

        txtImage.setText(G.fragmentActivity.getResources().getString(R.string.shared_image));
        txtVideo.setText(G.fragmentActivity.getResources().getString(R.string.shared_video));
        txtAudio.setText(G.fragmentActivity.getResources().getString(R.string.shared_audio));
        txtVoice.setText(G.fragmentActivity.getResources().getString(R.string.shared_voice));
        txtGif.setText(G.fragmentActivity.getResources().getString(R.string.shared_gif));
        txtFile.setText(G.fragmentActivity.getResources().getString(R.string.shared_file));
        txtLink.setText(G.fragmentActivity.getResources().getString(R.string.shared_links));

        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fillListImage();
            }
        });
        root2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fillListVideo();
            }
        });
        root3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fillListAudio();
            }
        });
        root4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fillListVoice();
            }
        });
        root5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fillListGif();
            }
        });
        root6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fillListFile();
            }
        });
        root7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fillListLink();
            }
        });

        //WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //layoutParams.copyFrom(dialog.getWindow().getAttributes());
        //layoutParams.width = (int) G.context.getResources().getDimension(R.dimen.dp260);
        //layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        //dialog.getWindow().setAttributes(layoutParams);

        DialogAnimation.animationUp(dialog);
        dialog.show();
    }

    private void initLayoutRecycleviewForImage() {

        final PreCashGridLayout gLayoutManager = new PreCashGridLayout(fragmentActivity, spanItemCount);

        gLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position < mNewList.size() && mNewList.get(position).isItemTime) {
                    return spanItemCount;
                } else {
                    return 1;
                }
            }
        });

        recyclerView.setLayoutManager(gLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int viewWidth = recyclerView.getMeasuredWidth();
                float cardViewWidth = G.context.getResources().getDimension(R.dimen.dp120);
                int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);

                if (newSpanCount < 3) {
                    newSpanCount = 3;
                }

                spanItemCount = newSpanCount;
                gLayoutManager.setSpanCount(newSpanCount);
                gLayoutManager.requestLayout();
            }
        });
    }

    private void fillListImage() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_image);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.IMAGE;
        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.IMAGE);
        adapter = new ImageAdapter(fragmentActivity, mNewList);
        initLayoutRecycleviewForImage();

        isChangeSelectType = false;
    }

    private void fillListVideo() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_video);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VIDEO;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.VIDEO);
        adapter = new VideoAdapter(fragmentActivity, mNewList);
        initLayoutRecycleviewForImage();

        isChangeSelectType = false;
    }

    private void fillListAudio() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_audio);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.AUDIO);
        adapter = new VoiceAdapter(fragmentActivity, mNewList);

        recyclerView.setLayoutManager(new PreCachingLayoutManager(fragmentActivity, 5000));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;
    }

    private void fillListVoice() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_voice);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VOICE;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.VOICE);
        adapter = new VoiceAdapter(fragmentActivity, mNewList);

        recyclerView.setLayoutManager(new PreCachingLayoutManager(fragmentActivity, 5000));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;
    }

    private void fillListGif() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_gif);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.GIF;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.GIF);
        adapter = new GifAdapter(fragmentActivity, mNewList);

        initLayoutRecycleviewForImage();

        isChangeSelectType = false;
    }

    private void fillListFile() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_file);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.FILE;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.FILE);
        adapter = new FileAdapter(fragmentActivity, mNewList);

        recyclerView.setLayoutManager(new PreCachingLayoutManager(fragmentActivity, 5000));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;
    }

    private void fillListLink() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_links);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.URL;

        if (mRealmList != null) {
            mRealmList.removeAllChangeListeners();
        }
        mRealmList = RealmRoomMessage.filterMessage(getRealm(), roomId, ProtoGlobal.RoomMessageType.TEXT);

        setListener();

        changeSize = mRealmList.size();

        getDataFromServer(mFilter);

        listCount = mRealmList.size();

        mNewList = addTimeToList(mRealmList);
        adapter = new LinkAdapter(fragmentActivity, mNewList);

        recyclerView.setLayoutManager(new PreCachingLayoutManager(fragmentActivity, 5000));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;

        //realm.close();
    }

    //********************************************************************************************

    private ArrayList<StructShearedMedia> loadLocalData(ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter, ProtoGlobal.RoomMessageType type) {

        if (mRealmList != null) {
            mRealmList.removeAllChangeListeners();
        }

        mRealmList = RealmRoomMessage.filterMessage(getRealm(), roomId, type);

        setListener();

        changeSize = mRealmList.size();
        isSendRequestForLoading = false;
        isThereAnyMoreItemToLoad = true;

        getDataFromServer(filter);
        listCount = mRealmList.size();

        return addTimeToList(mRealmList);
    }

    private ArrayList<StructShearedMedia> addTimeToList(RealmResults<RealmRoomMessage> list) {

        ArrayList<StructShearedMedia> result = new ArrayList<>();

        String firstItemTime = "";
        String secondItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");

        int isTimeHijri = HelperCalander.isTimeHijri();

        for (int i = 0; i < list.size(); i++) {

            Long time = list.get(i).getUpdateTime();
            secondItemTime = month_date.format(time);
            if (secondItemTime.compareTo(firstItemTime) > 0 || secondItemTime.compareTo(firstItemTime) < 0) {

                StructShearedMedia timeItem = new StructShearedMedia();

                if (isTimeHijri == 1) {
                    timeItem.messageTime = HelperCalander.getPersianCalander(time);
                } else if (isTimeHijri == 2) {
                    timeItem.messageTime = HelperCalander.getArabicCalander(time);
                } else {
                    timeItem.messageTime = secondItemTime;
                }

                timeItem.isItemTime = true;

                result.add(timeItem);

                firstItemTime = secondItemTime;
            }

            StructShearedMedia _item = new StructShearedMedia();
            _item.item = list.get(i);
            _item.messageId = list.get(i).getMessageId();

            result.add(_item);
        }

        return result;
    }

    private void getDataFromServer(final ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter) {

        offset = 0;
        nextMessageId = 0;

        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, final int notDeletedCount, final List<ProtoGlobal.RoomMessage> resultList, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter identity) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                if (resultList.size() > 0) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            int _listc = listCount;


                            saveDataToLocal(resultList, roomId);

                            nextMessageId = resultList.get(0).getMessageId();

                            isSendRequestForLoading = false;
                            isThereAnyMoreItemToLoad = true;

                            int deletedCount = 0;
                            for (int i = 0; i < resultList.size(); i++) {
                                if (resultList.get(i).getDeleted()) {
                                    deletedCount++;
                                }
                            }

                            offset += resultList.size() - deletedCount;
                        }
                    }).start();
                }
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError(final int majorCode, int minorCode, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter identity) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);

                        if (majorCode == 620) {

                            isThereAnyMoreItemToLoad = false;

                            //if (onScrollListener != null) {
                            //    recyclerView.removeOnScrollListener(onScrollListener);
                            //}
                        } else {
                            isSendRequestForLoading = false;
                        }
                    }
                });
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, nextMessageId, filter);
        progressBar.setVisibility(View.VISIBLE);
        isSendRequestForLoading = true;
    }

    public void saveDataToLocal(final List<ProtoGlobal.RoomMessage> RoomMessages, final long roomId) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //+final Realm realm = Realm.getDefaultInstance();

                getRealm().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (final ProtoGlobal.RoomMessage roomMessage : RoomMessages) {
                            RealmRoomMessage.putOrUpdate(roomMessage, roomId, false, false, realm);
                        }
                    }
                    //}, new Realm.Transaction.OnSuccess() {
                    //    @Override
                    //    public void onSuccess() {
                    //        realm.close();
                    //    }
                    //}, new Realm.Transaction.OnError() {
                    //    @Override
                    //    public void onError(Throwable error) {
                    //        realm.close();
                    //    }
                });
            }
        });
    }

    //********************************************************************************************

    private void setListener() {

        changeListener = new RealmChangeListener<RealmResults<RealmRoomMessage>>() {
            @Override
            public void onChange(RealmResults<RealmRoomMessage> element) {

                if (isChangeSelectType) {
                    return;
                }

                if (changeSize - element.size() != 0) {

                    mNewList.clear();
                    mNewList.addAll(addTimeToList(element));

                    int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (adapter instanceof ImageAdapter) {
                        adapter = new ImageAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof VideoAdapter) {
                        adapter = new VideoAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof VoiceAdapter) {
                        adapter = new VoiceAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof GifAdapter) {
                        adapter = new GifAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof FileAdapter) {
                        adapter = new FileAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof LinkAdapter) {
                        adapter = new LinkAdapter(fragmentActivity, mNewList);
                    }

                    recyclerView.setAdapter(adapter);

                    recyclerView.scrollToPosition(position);

                    listCount = element.size();
                    changeSize = element.size();
                }
            }
        };

        if (changeListener != null) {
            mRealmList.addChangeListener(changeListener);
        }
    }

    /**
     * Simple Class to serialize object to byte arrays
     *
     * @author Nick Russler
     *         http://www.whitebyte.info
     */
    public static class SerializationUtils {

        /**
         * @param obj - object to serialize to a byte array
         * @return byte array containing the serialized obj
         */
        public static byte[] serialize(Object obj) {
            byte[] result = null;
            ByteArrayOutputStream fos = null;

            try {
                fos = new ByteArrayOutputStream();
                ObjectOutputStream o = new ObjectOutputStream(fos);
                o.writeObject(obj);
                result = fos.toByteArray();
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        /**
         * @param arr - the byte array that holds the serialized object
         * @return the deserialized object
         */
        public static Object deserialize(byte[] arr) {
            InputStream fis = null;

            try {
                fis = new ByteArrayInputStream(arr);
                ObjectInputStream o = new ObjectInputStream(fis);
                return o.readObject();
            } catch (IOException e) {
                System.err.println(e);
            } catch (ClassNotFoundException e) {
                System.err.println(e);
            } finally {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }

            return null;
        }

        /**
         * @param obj - object to be cloned
         * @return a clone of obj
         */
        @SuppressWarnings("unchecked")
        public static <T> T cloneObject(T obj) {
            return (T) deserialize(serialize(obj));
        }
    }

    public class StructShearedMedia {
        RealmRoomMessage item;
        boolean isItemTime = false;
        String messageTime;
        long messageId;
    }

    private class PreCashGridLayout extends GridLayoutManager {

        private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 7000;

        public PreCashGridLayout(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        protected int getExtraLayoutSpace(RecyclerView.State state) {
            return DEFAULT_EXTRA_LAYOUT_SPACE;
        }
    }

    //****************************************************    Adapter    ****************************************

    public abstract class mAdapter extends RecyclerView.Adapter {

        protected ArrayList<StructShearedMedia> mList;
        protected Context context;

        public mAdapter(Context context, ArrayList<StructShearedMedia> mList) {
            this.mList = mList;
            this.context = context;
        }

        abstract void openSelectedItem(int position, RecyclerView.ViewHolder holder);

        @Override
        public int getItemViewType(int position) {

            if (mList.get(position).isItemTime) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (holder.getItemViewType() == 1) {
                setBackgroundColor(holder, position);

                mAdapter.mHolder holder1 = (mAdapter.mHolder) holder;

                if (mList.get(position).item.getAttachment() != null) {

                    holder1.messageProgress.setTag(mList.get(position).messageId);
                    holder1.messageProgress.withDrawable(R.drawable.ic_download, true);

                    if (HelperDownloadFile.isDownLoading(mList.get(position).item.getAttachment().getCacheId())) {
                        startDownload(position, holder1.messageProgress);
                    }
                }
            } else {
                mAdapter.ViewHolderTime holder1 = (mAdapter.ViewHolderTime) holder;

                holder1.txtTime.setText(mList.get(position).messageTime);
            }
        }

        public View setLayoutHeaderTime(View parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            view.setBackgroundColor(Color.parseColor("#cccccc"));
            return view;
        }

        private void setBackgroundColor(RecyclerView.ViewHolder holder, int position) {

            try {
                // set blue back ground for selected file
                FrameLayout layout = (FrameLayout) holder.itemView.findViewById(R.id.smsl_fl_contain_main);

                if (SelectedList.indexOf(mList.get(position).messageId) >= 0) {
                    layout.setForeground(new ColorDrawable(Color.parseColor("#99AADFF7")));
                } else {
                    layout.setForeground(new ColorDrawable(Color.TRANSPARENT));
                }
            } catch (Exception e) {

            }
        }

        private void openSelected(int position, RecyclerView.ViewHolder holder) {

            if (needDownloadList.containsKey(mList.get(position).messageId)) {

                // first need to download file

            } else {

                openSelectedItem(position, holder);
            }
        }

        private void setSelectedItem(final int position) {

            if (mList == null || mList.size() == 0) {
                return;
            }

            Long messageId = mList.get(position).messageId;

            int index = SelectedList.indexOf(messageId);

            if (index >= 0) {
                SelectedList.remove(index);
                numberOfSelected--;
                if (bothDeleteMessageId.contains(messageId)) {
                    bothDeleteMessageId.remove(messageId);
                }

                if (numberOfSelected < 1) {
                    isSelectedMode = false;
                }
            } else {
                SelectedList.add(messageId);

                if (RealmRoomMessage.isBothDelete(RealmRoomMessage.getMessageTime(messageId))) {
                    if (bothDeleteMessageId == null) {
                        bothDeleteMessageId = new ArrayList<>();
                    }
                    bothDeleteMessageId.add(messageId);
                }

                numberOfSelected++;
            }
            notifyItemChanged(position);

            if (complete != null) {
                complete.complete(isSelectedMode, "1", numberOfSelected + "");
            }
        }

        private void startDownload(final int position, final MessageProgress messageProgress) {

            messageProgress.withDrawable(R.drawable.ic_cancel, true);

            final RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();
            ProtoGlobal.RoomMessageType messageType = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getMessageType() : mList.get(position).item.getMessageType();

            String dirPath = AndroidUtils.getFilePathWithCashId(at.getCacheId(), at.getName(), messageType);

            messageProgress.withOnProgress(new OnProgress() {
                @Override
                public void onProgressFinished() {
                    if (messageProgress.getTag() != null && messageProgress.getTag().equals(mList.get(position).messageId)) {
                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageProgress.withProgress(0);
                                messageProgress.setVisibility(View.GONE);
                                updateViewAfterDownload(at.getCacheId());
                            }
                        });
                    }
                }
            });

            HelperDownloadFile.startDownload(mList.get(position).messageId + "", at.getToken(), at.getCacheId(), at.getName(), at.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, dirPath, 2, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(String path, final int progress) {

                    if (canUpdateAfterDownload) {
                        if (messageProgress.getTag() != null && messageProgress.getTag().equals(mList.get(position).messageId)) {
                            G.currentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageProgress.withProgress(progress);
                                }
                            });
                        }
                    }
                }

                @Override
                public void OnError(String token) {
                    if (canUpdateAfterDownload) {

                        if (messageProgress.getTag() != null && messageProgress.getTag().equals(mList.get(position).messageId)) {
                            G.currentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageProgress.withProgress(0);
                                    messageProgress.withDrawable(R.drawable.ic_download, true);
                                }
                            });
                        }
                    }
                }
            });
        }

        private void updateViewAfterDownload(String cashId) {
            for (int j = mNewList.size() - 1; j >= 0; j--) {
                try {
                    if (mNewList.get(j).item != null && mNewList.get(j).item.isValid() && !mNewList.get(j).item.isDeleted()) {
                        String mCashId = mNewList.get(j).item.getForwardMessage() != null ? mNewList.get(j).item.getForwardMessage().getAttachment().getCacheId() : mNewList.get(j).item.getAttachment().getCacheId();
                        if (mCashId.equals(cashId)) {
                            needDownloadList.remove(mNewList.get(j).messageId);

                            final int finalJ = j;
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    recyclerView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.getAdapter().notifyItemChanged(finalJ);
                                        }
                                    });
                                }
                            });
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        private void stopDownload(int position) {

            HelperDownloadFile.stopDownLoad(mList.get(position).item.getAttachment().getCacheId());
        }

        private void downloadFile(int position, MessageProgress messageProgress) {

            if (HelperDownloadFile.isDownLoading(mList.get(position).item.getAttachment().getCacheId())) {
                stopDownload(position);
            } else {
                startDownload(position, messageProgress);
            }
        }

        public boolean resetSelected() {

            boolean result = isSelectedMode;

            if (isSelectedMode == true) {
                isSelectedMode = false;

                SelectedList.clear();
                notifyDataSetChanged();
                // TODO: 1/2/2017  nejate best action for notify
                numberOfSelected = 0;
            }
            complete.complete(false, "1", "0");//

            return result;
        }

        public String getThumpnailPath(int position) {

            String result = "";

            RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

            if (at != null) {
                if (at.getLocalThumbnailPath() != null) {
                    result = at.getLocalThumbnailPath();
                }

                if (result.length() < 1) {
                    AndroidUtils.getFilePathWithCashId(at.getCacheId(), at.getName(), G.DIR_TEMP, true);
                }
            }

            return result;
        }

        public String getFilePath(int position) {

            String result = "";

            RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

            if (at != null) {
                if (at.getLocalFilePath() != null) {
                    result = at.getLocalFilePath();
                }

                ProtoGlobal.RoomMessageType messageType = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getMessageType() : mList.get(position).item.getMessageType();

                if (result.length() < 1) {
                    result = AndroidUtils.getFilePathWithCashId(at.getCacheId(), at.getName(), messageType);
                }
            }

            return result;
        }

        public class mHolder extends RecyclerView.ViewHolder {

            public MessageProgress messageProgress;

            public mHolder(View view) {
                super(view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isSelectedMode) {
                            setSelectedItem(getPosition());
                        } else {

                            openSelected(getPosition(), mAdapter.mHolder.this);
                        }
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        isSelectedMode = true;
                        setSelectedItem(getPosition());
                        return true;
                    }
                });

                messageProgress = (MessageProgress) itemView.findViewById(R.id.progress);
                AppUtils.setProgresColor(messageProgress.progressBar);

                messageProgress.withDrawable(R.drawable.ic_download, true);


                messageProgress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        downloadFile(getPosition(), messageProgress);
                    }
                });
            }
        }

        public class ViewHolderTime extends RecyclerView.ViewHolder {
            public TextView txtTime;

            public ViewHolderTime(View view) {
                super(view);
                txtTime = (TextView) itemView.findViewById(R.id.smslt_txt_time);
            }
        }
    }

    //****************************************************

    public class ImageAdapter extends mAdapter {

        public ImageAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new ImageAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {

                final ImageAdapter.ViewHolder vh = (ImageAdapter.ViewHolder) holder;

                vh.tempFilePath = getThumpnailPath(position);
                vh.filePath = getFilePath(position);

                vh.imvPicFile.setImageResource(R.mipmap.difaultimage);

                vh.imvPicFile.setTag(mList.get(position).messageId);

                // if thumpnail not exist download it
                File filet = new File(vh.tempFilePath);
                if (filet.exists()) {

                    if (vh.imvPicFile.getTag() != null && vh.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                        G.imageLoader.displayImage(suitablePath(vh.tempFilePath), vh.imvPicFile);
                    }
                } else {

                    RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

                    if (at.getSmallThumbnail() != null) {
                        if (at.getSmallThumbnail().getSize() > 0) {

                            HelperDownloadFile.startDownload(mList.get(position).messageId + "", at.getToken(), at.getCacheId(), at.getName(), at.getSmallThumbnail().getSize(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override
                                public void OnProgress(final String path, int progress) {

                                    if (progress == 100) {

                                        if (canUpdateAfterDownload) {
                                            G.currentActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if (vh.imvPicFile.getTag() != null && vh.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                                                        G.imageLoader.displayImage(AndroidUtils.suitablePath(path), vh.imvPicFile);
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
                }

                File file = new File(vh.filePath);
                if (file.exists()) {
                    vh.messageProgress.setVisibility(View.GONE);
                } else {

                    needDownloadList.put(mList.get(position).messageId, true);

                    vh.messageProgress.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            showImage(position, holder.itemView);
        }

        private void showImage(int position, View itemView) {
            long selectedFileToken = mList.get(position).messageId;

            FragmentShowImage fragment = FragmentShowImage.newInstance();
            Bundle bundle = new Bundle();
            bundle.putLong("RoomId", roomId);
            bundle.putLong("SelectedImage", selectedFileToken);
            bundle.putString("TYPE", ProtoGlobal.RoomMessageType.IMAGE.toString());
            fragment.setArguments(bundle);

            //FragmentTransitionLauncher.with(G.fragmentActivity).from(itemView).prepare(fragment);

            fragment.appBarLayout = appBarLayout;
            new HelperFragment(fragment).setReplace(false).load();
        }

        public class ViewHolder extends mHolder {

            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view) {
                super(view);

                imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);
            }
        }
    }


    //****************************************************

    public class VideoAdapter extends mAdapter {

        public VideoAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new VideoAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {

                final VideoAdapter.ViewHolder holder1 = (VideoAdapter.ViewHolder) holder;
                holder1.layoutInfo.setVisibility(View.VISIBLE);

                RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

                holder1.imvPicFile.setImageResource(R.mipmap.difaultimage);
                holder1.imvPicFile.setTag(mList.get(position).messageId);

                final String tempFilePath;
                String filePath;

                holder1.txtVideoTime.setText(AppUtils.humanReadableDuration(at.getDuration()));

                holder1.txtVideoSize.setText("(" + AndroidUtils.humanReadableByteCount(at.getSize(), true) + ")");

                tempFilePath = getThumpnailPath(position);

                File filethumpnail = new File(tempFilePath);

                if (filethumpnail.exists()) {
                    if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), holder1.imvPicFile);
                    }
                } else {
                    holder1.imvPicFile.setImageResource(R.mipmap.j_video);

                    if (at.getSmallThumbnail() != null) {
                        if (at.getSmallThumbnail().getSize() > 0) {

                            HelperDownloadFile.startDownload(mList.get(position).messageId + "", at.getToken(), at.getCacheId(), at.getName(), at.getSmallThumbnail().getSize(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override
                                public void OnProgress(final String path, int progress) {

                                    if (progress == 100) {
                                        if (canUpdateAfterDownload) {
                                            G.currentActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                                                        G.imageLoader.displayImage(AndroidUtils.suitablePath(path), holder1.imvPicFile);
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
                }

                filePath = getFilePath(position);

                File file = new File(filePath);
                if (file.exists()) {
                    holder1.messageProgress.setVisibility(View.GONE);
                } else {
                    needDownloadList.put(mList.get(position).messageId, true);
                    holder1.messageProgress.setVisibility(View.VISIBLE);
                }
            }
        }

        void openVideoByDefaultApp(int position) {

            try {
                RealmRoomMessage rm = mNewList.get(position).item;
                if (rm.getAttachment().isFileExistsOnLocal()) {
                    File file = new File(rm.getAttachment().getLocalFilePath());
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                    } else {
                        uri = Uri.fromFile(file);
                    }

                    intent.setDataAndType(uri, "video/*");

                    try {
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } catch (Exception e) {
                        // to prevent from 'No Activity found to handle Intent'
                        e.printStackTrace();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playVideo(position, holder.itemView);
        }

        private void playVideo(int position, View itemView) {
            SharedPreferences sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            if (sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 0) == 1) {
                openVideoByDefaultApp(position);
            } else {
                long selectedFileToken = mNewList.get(position).messageId;
                Fragment fragment = FragmentShowImage.newInstance();
                Bundle bundle = new Bundle();
                bundle.putLong("RoomId", roomId);
                bundle.putLong("SelectedImage", selectedFileToken);
                bundle.putString("TYPE", ProtoGlobal.RoomMessageType.VIDEO.toString());
                fragment.setArguments(bundle);

                //FragmentTransitionLauncher.with(G.fragmentActivity).from(itemView).prepare(fragment);
                new HelperFragment(fragment).setReplace(false).load();
            }
        }

        public class ViewHolder extends mHolder {
            public ImageView imvPicFile;
            public TextView txtVideoIcon;
            public TextView txtVideoTime;
            public LinearLayout layoutInfo;
            public TextView txtVideoSize;

            public ViewHolder(View view) {
                super(view);

                imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);

                layoutInfo = (LinearLayout) itemView.findViewById(R.id.smsl_ll_video);

                txtVideoIcon = (TextView) itemView.findViewById(R.id.smsl_txt_video_icon);

                txtVideoTime = (TextView) itemView.findViewById(R.id.smsl_txt_video_time);

                txtVideoSize = (TextView) itemView.findViewById(R.id.smsl_txt_video_size);
            }
        }
    }

    //****************************************************

    public class VoiceAdapter extends mAdapter {

        public VoiceAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new VoiceAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {

                VoiceAdapter.ViewHolder holder1 = (VoiceAdapter.ViewHolder) holder;

                holder1.imvPicFile.setImageResource(R.drawable.green_music_note);
                holder1.imvPicFile.setTag(mList.get(position).messageId);

                RealmAttachment at = mList.get(position).item.getAttachment();

                String tempFilePath = getThumpnailPath(position);
                holder1.filePath = getFilePath(position);

                holder1.txtFileName.setText(at.getName());

                holder1.txtFileSize.setText("(" + AndroidUtils.humanReadableByteCount(at.getSize(), true) + ")");

                File file = new File(holder1.filePath);

                if (file.exists()) {
                    holder1.messageProgress.setVisibility(View.GONE);

                    try {

                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        Uri uri = Uri.fromFile(file);
                        mediaMetadataRetriever.setDataSource(context, uri);
                        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                        if (artist == null) {
                            artist = context.getString(R.string.unknown_artist);
                        }
                        holder1.txtFileInfo.setText(artist);

                        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                        if (data != null) {
                            Bitmap mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);

                            if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                                holder1.imvPicFile.setImageBitmap(mediaThumpnail);
                            }
                        } else {
                            file = new File(tempFilePath);
                            if (file.exists()) {

                                if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), holder1.imvPicFile);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                } else {
                    needDownloadList.put(mList.get(position).messageId, true);
                    holder1.messageProgress.setVisibility(View.VISIBLE);
                    file = new File(tempFilePath);
                    if (file.exists()) {

                        if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), holder1.imvPicFile);
                        }
                    }
                }
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playAudio(position, holder);
        }

        private void playAudio(int position, RecyclerView.ViewHolder holder) {

            VoiceAdapter.ViewHolder vh = (VoiceAdapter.ViewHolder) holder;

            String name = mList.get(position).item.getAttachment().getName();

            MusicPlayer.startPlayer(name, vh.filePath, name, roomId, true, mList.get(position).messageId + "");
        }

        public class ViewHolder extends mHolder {
            public ImageView imvPicFile;
            public TextView txtFileName;
            public TextView txtFileSize;
            public TextView txtFileInfo;
            public String filePath;

            public ViewHolder(View view) {
                super(view);

                imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);

                txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
                txtFileSize = (TextView) itemView.findViewById(R.id.smslf_txt_file_size);
                txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);
            }
        }
    }

    //****************************************************

    public class GifAdapter extends mAdapter {

        public GifAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_gif, null);
                viewHolder = new GifAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {

                final GifAdapter.ViewHolder holder1 = (GifAdapter.ViewHolder) holder;

                holder1.gifView.setTag(mList.get(position).messageId);

                RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

                holder1.filePath = getFilePath(position);

                File file = new File(holder1.filePath);
                if (file.exists()) {
                    holder1.gifView.setImageURI(Uri.fromFile(file));

                    holder1.gifDrawable = (GifDrawable) holder1.gifView.getDrawable();

                    holder1.messageProgress.withDrawable(R.drawable.photogif, true);
                    holder1.messageProgress.setVisibility(View.GONE);
                    holder1.messageProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (holder1.gifDrawable != null) {
                                holder1.gifDrawable.start();
                                holder1.messageProgress.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    needDownloadList.put(mList.get(position).messageId, true);
                    holder1.messageProgress.setVisibility(View.VISIBLE);

                    holder1.tempFilePath = getThumpnailPath(position);

                    File filethumpnail = new File(holder1.tempFilePath);

                    if (filethumpnail.exists()) {

                        if (holder1.gifView.getTag() != null && holder1.gifView.getTag().equals(mList.get(position).messageId)) {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(holder1.tempFilePath), holder1.gifView);
                        }
                    } else {
                        if (at.getSmallThumbnail() != null) {
                            if (at.getSmallThumbnail().getSize() > 0) {

                                HelperDownloadFile.startDownload(mList.get(position).messageId + "", at.getToken(), at.getCacheId(), at.getName(), at.getSmallThumbnail().getSize(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, "", 4, new HelperDownloadFile.UpdateListener() {
                                    @Override
                                    public void OnProgress(final String path, int progress) {

                                        if (progress == 100) {
                                            if (canUpdateAfterDownload) {
                                                G.currentActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (holder1.gifView.getTag() != null && holder1.gifView.getTag().equals(mList.get(position).messageId)) {
                                                            G.imageLoader.displayImage(AndroidUtils.suitablePath(path), holder1.gifView);
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
                    }
                }
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playAndPusGif(position, holder);
        }

        private void playAndPusGif(int position, RecyclerView.ViewHolder holder) {

            final GifAdapter.ViewHolder vh = (GifAdapter.ViewHolder) holder;

            GifDrawable gifDrawable = vh.gifDrawable;
            if (gifDrawable != null) {
                if (gifDrawable.isPlaying()) {
                    gifDrawable.pause();
                    vh.messageProgress.setVisibility(View.VISIBLE);
                } else {
                    gifDrawable.start();
                    vh.messageProgress.setVisibility(View.GONE);
                }
            } else {
                File file = new File(vh.filePath);
                if (file.exists()) {
                    vh.gifView.setImageURI(Uri.fromFile(file));
                    vh.gifDrawable = (GifDrawable) vh.gifView.getDrawable();
                    vh.messageProgress.withDrawable(R.drawable.ic_play, true);

                    vh.messageProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (vh.gifDrawable != null) {
                                vh.gifDrawable.start();
                                vh.messageProgress.setVisibility(View.GONE);
                            }
                        }
                    });

                    vh.gifDrawable.start();
                    vh.messageProgress.setVisibility(View.GONE);
                }
            }
        }

        public class ViewHolder extends mHolder {

            public String tempFilePath;
            public String filePath;
            GifImageView gifView;
            GifDrawable gifDrawable;

            public ViewHolder(View view) {
                super(view);

                gifView = (GifImageView) itemView.findViewById(R.id.smslg_gif_view);
            }
        }
    }

    //****************************************************

    public class FileAdapter extends mAdapter {

        public FileAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new FileAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {
                FileAdapter.ViewHolder vh = (FileAdapter.ViewHolder) holder;

                RealmAttachment at = mList.get(position).item.getAttachment();

                vh.tempFilePath = getThumpnailPath(position);
                vh.filePath = getFilePath(position);

                File file = new File(vh.filePath);
                if (file.exists()) {
                    vh.messageProgress.setVisibility(View.GONE);
                } else {
                    needDownloadList.put(mList.get(position).messageId, true);
                    vh.messageProgress.setVisibility(View.VISIBLE);
                }

                vh.txtFileSize.setVisibility(View.INVISIBLE);

                vh.txtFileName.setText(at.getName());
                vh.txtFileInfo.setText(AndroidUtils.humanReadableByteCount(at.getSize(), true));

                File fileTemp = new File(vh.tempFilePath);

                if (fileTemp.exists()) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(vh.tempFilePath), vh.imvPicFile);
                } else {
                    Bitmap bitmap = HelperMimeType.getMimePic(context, HelperMimeType.getMimeResource(mList.get(position).item.getAttachment().getName()));
                    if (bitmap != null) vh.imvPicFile.setImageBitmap(bitmap);
                }
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            openFile(position, holder);
        }

        private void openFile(int position, RecyclerView.ViewHolder holder) {

            FileAdapter.ViewHolder vh = (FileAdapter.ViewHolder) holder;

            Intent intent = HelperMimeType.appropriateProgram(vh.filePath);
            if (intent != null) context.startActivity(intent);
        }

        public class ViewHolder extends mHolder {
            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public TextView txtFileName;
            public TextView txtFileInfo;
            public TextView txtFileSize;

            public ViewHolder(View view) {
                super(view);

                imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);

                txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
                txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);
                txtFileSize = (TextView) itemView.findViewById(R.id.smslf_txt_file_size);
            }
        }
    }

    //****************************************************

    public class LinkAdapter extends mAdapter {

        public LinkAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_media_sub_layout_link, null);
                viewHolder = new LinkAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {
                LinkAdapter.ViewHolder vh = (LinkAdapter.ViewHolder) holder;

                vh.txtLink.setText(HelperUrl.setUrlLink(mList.get(position).item.getMessage(), true, false, "", true));

                vh.txtLink.setMovementMethod(LinkMovementMethod.getInstance());
                vh.messageProgress.setVisibility(View.GONE);
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {

        }

        public class ViewHolder extends mHolder {
            public TextView txtLink;

            public ViewHolder(View view) {
                super(view);

                txtLink = (TextView) itemView.findViewById(R.id.smsll_txt_shared_link);
            }
        }
    }
}
