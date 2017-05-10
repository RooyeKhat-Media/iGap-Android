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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
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
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentShowImage;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnClientSearchRoomHistory;
import net.iGap.interfaces.OnComplete;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.proto.ProtoClientCountRoomHistory;
import net.iGap.proto.ProtoClientSearchRoomHistory;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestClientCountRoomHistory;
import net.iGap.request.RequestClientSearchRoomHistory;
import org.parceler.Parcels;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static net.iGap.G.context;
import static net.iGap.module.AndroidUtils.suitablePath;

public class ActivityShearedMedia extends ActivityEnhanced {

    private RealmResults<RealmRoomMessage> mRealmList;
    private ArrayList<StructShearedMedia> mNewList;
    RealmChangeListener<RealmResults<RealmRoomMessage>> changeListener;

    protected ArrayMap<Long, Boolean> needDownloadList = new ArrayMap<>();

    private RecyclerView recyclerView;
    private mAdapter adapter;
    int mListcount = 0;
    private int spanItemCount = 3;
    private TextView txtSharedMedia;
    private TextView txtNumberOfSelected;
    private LinearLayout ll_AppBarSelected;
    private OnComplete complete;
    private long roomId = 0;
    Handler handler;
    private int changesize = 0;
    ProgressBar progressBar;

    private Realm mRealm;

    private boolean isChangeSelectType = false;

    private RecyclerView.OnScrollListener onScrollListener;

    private static long countOFImage = 0;
    private static long countOFVIDEO = 0;
    private static long countOFAUDIO = 0;
    private static long countOFVOICE = 0;
    private static long countOFGIF = 0;
    private static long countOFFILE = 0;
    private static long countOFLink = 0;

    private LinearLayout mediaLayout;
    private MusicPlayer musicPlayer;

    private AppBarLayout appBarLayout;

    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = false;

    public class StructShearedMedia {
        RealmRoomMessage item;
        boolean isItemTime = false;
        String messageTime;
    }

    ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter mFilter;

    private int offset;

    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {

            mRealm = Realm.getDefaultInstance();
        }

        return mRealm;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicPlayer.mp != null) {
            MusicPlayer.initLayoutTripMusic(mediaLayout);
        }

        setListener();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mRealmList != null) {
            mRealmList.removeAllChangeListeners();
        }

        if (mRealm != null) {
            mRealm.close();
        }
    }

    private void setListener() {

        changeListener = new RealmChangeListener<RealmResults<RealmRoomMessage>>() {
            @Override
            public void onChange(RealmResults<RealmRoomMessage> element) {

                if (isChangeSelectType) {
                    return;
                }

                if (changesize - element.size() != 0) {

                    mNewList.clear();
                    mNewList.addAll(addTimeToList(element));

                    int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (adapter instanceof ImageAdapter) {
                        adapter = new ImageAdapter(ActivityShearedMedia.this, mNewList);
                    } else if (adapter instanceof VideoAdapter) {
                        adapter = new VideoAdapter(ActivityShearedMedia.this, mNewList);
                    } else if (adapter instanceof VoiceAdapter) {
                        adapter = new VoiceAdapter(ActivityShearedMedia.this, mNewList);
                    } else if (adapter instanceof GifAdapter) {
                        adapter = new GifAdapter(ActivityShearedMedia.this, mNewList);
                    } else if (adapter instanceof FileAdapter) {
                        adapter = new FileAdapter(ActivityShearedMedia.this, mNewList);
                    } else if (adapter instanceof LinkAdapter) {
                        adapter = new LinkAdapter(ActivityShearedMedia.this, mNewList);
                    }

                    recyclerView.setAdapter(adapter);

                    recyclerView.scrollToPosition(position);

                    mListcount = element.size();
                    changesize = element.size();
                }
            }
        };

        if (changeListener != null) {
            mRealmList.addChangeListener(changeListener);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheared_media);

        mediaLayout = (LinearLayout) findViewById(R.id.asm_ll_music_layout);
        musicPlayer = new MusicPlayer(mediaLayout);

        roomId = getIntent().getExtras().getLong("RoomID");

        initComponent();

        handler = new Handler();
    }

    @Override
    public void onBackPressed() {
        FragmentShowImage myFragment = (FragmentShowImage) getSupportFragmentManager().findFragmentByTag("Show_Image_fragment_shared_media");

        if (myFragment != null && myFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(myFragment).commit();

            // for update view that image download in fragment show image
            int count = FragmentShowImage.downloadedList.size();

            for (int i = 0; i < count; i++) {
                String _cahsId = FragmentShowImage.downloadedList.get(i);

                for (int j = mNewList.size() - 1; j >= 0; j--) {
                    try {
                        String mCashId = mNewList.get(j).item.getForwardMessage() != null ? mNewList.get(j).item.getForwardMessage().getAttachment().getCacheId() : mNewList.get(j).item.getAttachment().getCacheId();
                        if (mCashId.equals(_cahsId)) {

                            needDownloadList.remove(mNewList.get(j).item.getMessageId());

                            final int finalJ = j;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.getAdapter().notifyItemChanged(finalJ);
                                }
                            });
                        }
                    } catch (NullPointerException e) {
                    }
                }
            }
            FragmentShowImage.downloadedList.clear();
        } else if (!adapter.resetSelected()) {
            super.onBackPressed();
        }
    }

    private void initComponent() {

        progressBar = (ProgressBar) findViewById(R.id.asm_progress_bar_waiting);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);

        appBarLayout = (AppBarLayout) findViewById(R.id.asm_appbar_shared_media);

        findViewById(R.id.asm_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        findViewById(R.id.asm_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

        FragmentShowImage.appBarLayout = appBarLayout;

        MaterialDesignTextView btnBack = (MaterialDesignTextView) findViewById(R.id.asm_btn_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.asm_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        MaterialDesignTextView btnMenu = (MaterialDesignTextView) findViewById(R.id.asm_btn_menu);
        RippleView rippleMenu = (RippleView) findViewById(R.id.asm_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popUpMenuSharedMedai();
            }
        });

        txtSharedMedia = (TextView) findViewById(R.id.asm_txt_sheared_media);

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

        recyclerView = (RecyclerView) findViewById(R.id.asm_recycler_view_sheared_media);
        recyclerView.setItemViewCacheSize(1000);

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (lastVisiblePosition + 30 >= offset) {

                            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offset, mFilter);
                            isSendRequestForLoading = true;
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };

        recyclerView.addOnScrollListener(onScrollListener);

        openLayout();

        initAppbarSelected();
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

    private void initAppbarSelected() {

        RippleView rippleCloseAppBarSelected = (RippleView) findViewById(R.id.asm_ripple_close_layout);
        rippleCloseAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.resetSelected();
            }
        });

        MaterialDesignTextView btnForwardSelected = (MaterialDesignTextView) findViewById(R.id.asm_btn_forward_selected);
        btnForwardSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Parcelable> messageInfos = new ArrayList<>(adapter.SelectedList.size());
                RealmRoomMessage rm;

                for (Long Id : adapter.SelectedList) {

                    rm = mRealmList.where().equalTo(RealmRoomMessageFields.MESSAGE_ID, Id).findFirst();
                    if (rm != null) {
                        messageInfos.add(Parcels.wrap(StructMessageInfo.convert(rm)));
                    }
                }

                Intent intent = new Intent(ActivityShearedMedia.this, ActivitySelectChat.class);
                intent.putParcelableArrayListExtra(ActivitySelectChat.ARG_FORWARD_MESSAGE, messageInfos);

                startActivity(intent);

                adapter.resetSelected();
            }
        });

        RippleView rippleDeleteSelected = (RippleView) findViewById(R.id.asm_riple_delete_selected);
        rippleDeleteSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                final RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                if (realmRoom != null) {
                    ActivityChat.deleteSelectedMessages(roomId, adapter.SelectedList, realmRoom.getType());
                }

                switch (mFilter) {
                    case IMAGE:
                        countOFImage -= adapter.SelectedList.size();
                        break;
                    case VIDEO:
                        countOFVIDEO -= adapter.SelectedList.size();
                        break;
                    case AUDIO:
                        countOFAUDIO -= adapter.SelectedList.size();
                        break;
                    case FILE:
                        countOFFILE -= adapter.SelectedList.size();
                        break;
                    case GIF:
                        countOFGIF -= adapter.SelectedList.size();
                        break;
                    case VOICE:
                        countOFVOICE -= adapter.SelectedList.size();
                        break;
                    case URL:
                        countOFLink -= adapter.SelectedList.size();
                        break;
                }

                updateStringSharedMediaCount(null, roomId);

                adapter.resetSelected();


            }
        });

        txtNumberOfSelected = (TextView) findViewById(R.id.asm_txt_number_of_selected);

        ll_AppBarSelected = (LinearLayout) findViewById(R.id.asm_ll_appbar_selelected);
    }

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

    public void popUpMenuSharedMedai() {

        MaterialDialog dialog = new MaterialDialog.Builder(this).items(R.array.pop_up_shared_media).contentColor(Color.BLACK).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0:
                        fillListImage();
                        break;
                    case 1:
                        fillListVideo();
                        break;
                    case 2:
                        fillListAudio();
                        break;
                    case 3:
                        fillListVoice();
                        break;
                    case 4:
                        fillListGif();
                        break;
                    case 5:
                        fillListFile();
                        break;
                    case 6:
                        fillListLink();
                        break;
                }
            }
        }).show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp260);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        dialog.getWindow().setAttributes(layoutParams);
    }

    //********************************************************************************************

    private void initLayoutRecycleviewForImage() {

        final GridLayoutManager gLayoutManager = new GridLayoutManager(ActivityShearedMedia.this, spanItemCount);

        gLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mNewList.get(position).isItemTime) {
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
                float cardViewWidth = getResources().getDimension(R.dimen.dp120);
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
        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.IMAGE.toString());
        adapter = new ImageAdapter(ActivityShearedMedia.this, mNewList);
        initLayoutRecycleviewForImage();

        isChangeSelectType = false;

    }

    private void fillListVideo() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_video);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VIDEO;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.VIDEO.toString());
        adapter = new VideoAdapter(ActivityShearedMedia.this, mNewList);
        initLayoutRecycleviewForImage();

        isChangeSelectType = false;

    }

    private void fillListAudio() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_audio);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.AUDIO.toString());
        adapter = new VoiceAdapter(ActivityShearedMedia.this, mNewList);

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityShearedMedia.this));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;

    }

    private void fillListVoice() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_voice);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VOICE;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.VOICE.toString());
        adapter = new VoiceAdapter(ActivityShearedMedia.this, mNewList);

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityShearedMedia.this));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;

    }

    private void fillListGif() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_gif);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.GIF;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.GIF.toString());
        adapter = new GifAdapter(ActivityShearedMedia.this, mNewList);

        initLayoutRecycleviewForImage();

        isChangeSelectType = false;

    }

    private void fillListFile() {

        isChangeSelectType = true;

        txtSharedMedia.setText(R.string.shared_file);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.FILE;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.FILE.toString());
        adapter = new FileAdapter(ActivityShearedMedia.this, mNewList);

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityShearedMedia.this));
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

        mRealmList = getRealm().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).
                equalTo(RealmRoomMessageFields.MESSAGE_TYPE, ProtoGlobal.RoomMessageType.TEXT.toString()).
                equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.HAS_MESSAGE_LINK, true).
                findAllSorted(RealmRoomMessageFields.UPDATE_TIME, Sort.DESCENDING);

        setListener();

        changesize = mRealmList.size();

        getDataFromServer(mFilter);

        mListcount = mRealmList.size();

        mNewList = addTimeToList(mRealmList);
        adapter = new LinkAdapter(ActivityShearedMedia.this, mNewList);

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityShearedMedia.this));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;

    }

    //********************************************************************************************

    private ArrayList<StructShearedMedia> loadLocalData(ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter, String type) {

        if (mRealmList != null) {
            mRealmList.removeAllChangeListeners();
        }

        mRealmList = getRealm().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).
                contains(RealmRoomMessageFields.MESSAGE_TYPE, type).equalTo(RealmRoomMessageFields.DELETED, false).findAllSorted(RealmRoomMessageFields.UPDATE_TIME, Sort.DESCENDING);

        setListener();

        changesize = mRealmList.size();

        getDataFromServer(filter);
        mListcount = mRealmList.size();

        ArrayList<StructShearedMedia> list = addTimeToList(mRealmList);

        return list;
    }

    private ArrayList<StructShearedMedia> addTimeToList(RealmResults<RealmRoomMessage> list) {

        ArrayList<StructShearedMedia> result = new ArrayList<>();

        String firstItemTime = "";
        String secondItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");

        boolean isTimeHijri = HelperCalander.isTimeHijri();

        for (int i = 0; i < list.size(); i++) {

            Long time = list.get(i).getUpdateTime();
            secondItemTime = month_date.format(time);
            if (secondItemTime.compareTo(firstItemTime) > 0 || secondItemTime.compareTo(firstItemTime) < 0) {

                StructShearedMedia timeItem = new StructShearedMedia();

                if (isTimeHijri) {
                    timeItem.messageTime = HelperCalander.getPersianCalander(time);
                } else {
                    timeItem.messageTime = secondItemTime;
                }

                timeItem.isItemTime = true;

                result.add(timeItem);

                firstItemTime = secondItemTime;
            }

            StructShearedMedia _item = new StructShearedMedia();
            _item.item = list.get(i);

            result.add(_item);
        }

        return result;
    }

    private void getDataFromServer(final ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter) {

        offset = 0;

        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, final int notDeletedCount, final List<ProtoGlobal.RoomMessage> resultList, String identity) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        isSendRequestForLoading = false;
                        offset += resultList.size();

                        if (notDeletedCount > 0) {
                            saveDataToLocal(resultList, roomId);
                        }

                        if (notDeletedCount > offset && notDeletedCount > mListcount) {
                            isThereAnyMoreItemToLoad = true;
                        } else {
                            isThereAnyMoreItemToLoad = false;

                            if (onScrollListener != null) {
                                recyclerView.removeOnScrollListener(onScrollListener);
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void onError(int majorCode, int minorCode, String identity) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                isSendRequestForLoading = false;
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offset, filter);
        progressBar.setVisibility(View.VISIBLE);
        isSendRequestForLoading = true;
    }

    public void saveDataToLocal(final List<ProtoGlobal.RoomMessage> RoomMessages, final long roomId) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (final ProtoGlobal.RoomMessage roomMessage : RoomMessages) {
                            RealmRoomMessage.putOrUpdate(roomMessage, roomId, false, false, realm);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
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

    //********************************************************************************************

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

    public static void updateStringSharedMediaCount(ProtoClientCountRoomHistory.ClientCountRoomHistoryResponse.Builder proto, long roomId) {

        if (proto != null) {

            countOFImage = proto.getImage();
            countOFVIDEO = proto.getVideo();
            countOFAUDIO = proto.getAudio();
            countOFVOICE = proto.getVoice();
            countOFGIF = proto.getGif();
            countOFFILE = proto.getFile();
            countOFLink = proto.getUrl();
        }

        String result = "";

        if (countOFImage > 0) result += "\n" + countOFImage + " " + context.getString(R.string.shared_image);
        if (countOFVIDEO > 0) result += "\n" + countOFVIDEO + " " + context.getString(R.string.shared_video);
        if (countOFAUDIO > 0) result += "\n" + countOFAUDIO + " " + context.getString(R.string.shared_audio);
        if (countOFVOICE > 0) result += "\n" + countOFVOICE + " " + context.getString(R.string.shared_voice);
        if (countOFGIF > 0) result += "\n" + countOFGIF + " " + context.getString(R.string.shared_gif);
        if (countOFFILE > 0) result += "\n" + countOFFILE + " " + context.getString(R.string.shared_file);
        if (countOFLink > 0) result += "\n" + countOFLink + " " + context.getString(R.string.shared_links);

        result = result.trim();

        if (result.length() < 1) {
            result = context.getString(R.string.there_is_no_sheared_media);
        }

        Realm realm = Realm.getDefaultInstance();

        final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (room != null) {
            final String finalResult = result;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    room.setSharedMediaCount(finalResult);
                }
            });
        }

        realm.close();
    }

    public static void getCountOfSharedMedia(long roomId) {

        new RequestClientCountRoomHistory().clientCountRoomHistory(roomId);
    }

    //****************************************************    Adapter    ****************************************

    public abstract class mAdapter extends RecyclerView.Adapter {

        private boolean isSelectedMode = false;    // for determine user select some file
        private int numberOfSelected = 0;
        protected ArrayList<StructShearedMedia> mList;
        protected Context context;

        public ArrayList<Long> SelectedList = new ArrayList<>();

        abstract void openSelectedItem(int position, RecyclerView.ViewHolder holder);

        public mAdapter(Context context, ArrayList<StructShearedMedia> mList) {
            this.mList = mList;
            this.context = context;
        }

        public class mHolder extends RecyclerView.ViewHolder {

            public MessageProgress messageProgress;

            public ContentLoadingProgressBar contentLoading;

            public mHolder(View view, int position) {
                super(view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isSelectedMode) {
                            setSelectedItem(getPosition());
                        } else {

                            openSelected(getPosition(), mHolder.this);
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
                messageProgress.withDrawable(R.drawable.ic_download, true);

                contentLoading = (ContentLoadingProgressBar) itemView.findViewById(R.id.ch_progress_loadingContent);
                contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

                if (mList.get(position).item.getAttachment() != null) {
                    if (HelperDownloadFile.isDownLoading(mList.get(position).item.getAttachment().getCacheId())) {
                        startDownload(position, messageProgress, contentLoading);
                    }
                }
                messageProgress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        downloadFile(getPosition(), messageProgress, contentLoading);
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (!mList.get(position).isItemTime) {
                setBackgroundColor(holder, position);
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

                if (SelectedList.indexOf(mList.get(position).item.getMessageId()) >= 0) {
                    layout.setForeground(new ColorDrawable(Color.parseColor("#99AADFF7")));
                } else {
                    layout.setForeground(new ColorDrawable(Color.TRANSPARENT));
                }
            } catch (Exception e) {

            }
        }

        private void openSelected(int position, RecyclerView.ViewHolder holder) {

            if (needDownloadList.containsKey(mList.get(position).item.getMessageId())) {

                // first need to download file

            } else {

                openSelectedItem(position, holder);
            }
        }

        private void setSelectedItem(final int position) {

            Long id = mList.get(position).item.getMessageId();

            int index = SelectedList.indexOf(id);

            if (index >= 0) {
                SelectedList.remove(index);
                numberOfSelected--;

                if (numberOfSelected < 1) {
                    isSelectedMode = false;
                }
            } else {
                SelectedList.add(id);
                numberOfSelected++;
            }
            notifyItemChanged(position);

            if (complete != null) {
                complete.complete(isSelectedMode, "1", numberOfSelected + "");
            }
        }

        private void startDownload(final int position, final MessageProgress messageProgress, final ContentLoadingProgressBar contentLoading) {

            contentLoading.setVisibility(View.VISIBLE);

            messageProgress.withDrawable(R.drawable.ic_cancel, true);

            final RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();
            ProtoGlobal.RoomMessageType messageType = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getMessageType() : mList.get(position).item.getMessageType();

            String dirPath = AndroidUtils.getFilePathWithCashId(at.getCacheId(), at.getName(), messageType);

            HelperDownloadFile.startDownload(at.getToken(), at.getCacheId(), at.getName(), at.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, dirPath, 2, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(String path, final int progress) {

                    if (messageProgress != null) {

                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progress < 100) {
                                    messageProgress.withProgress(progress);
                                } else {
                                    messageProgress.withProgress(0);
                                    messageProgress.setVisibility(View.GONE);
                                    contentLoading.setVisibility(View.GONE);

                                    updateViewAfterDownload(at.getCacheId());
                                }
                            }
                        });
                    }
                }

                @Override
                public void OnError(String token) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageProgress.withProgress(0);
                            messageProgress.withDrawable(R.drawable.ic_download, true);
                            contentLoading.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }

        private void updateViewAfterDownload(String cashId) {
            for (int j = mNewList.size() - 1; j >= 0; j--) {
                try {
                    String mCashId = mNewList.get(j).item.getForwardMessage() != null ? mNewList.get(j).item.getForwardMessage().getAttachment().getCacheId() : mNewList.get(j).item.getAttachment().getCacheId();
                    if (mCashId.equals(cashId)) {

                        needDownloadList.remove(mNewList.get(j).item.getMessageId());

                        final int finalJ = j;
                        runOnUiThread(new Runnable() {
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
                } catch (NullPointerException e) {
                }
            }
        }

        private void stopDownload(int position, final MessageProgress messageProgress, final ContentLoadingProgressBar contentLoading) {

            HelperDownloadFile.stopDownLoad(mList.get(position).item.getAttachment().getCacheId());
        }

        private void downloadFile(int position, MessageProgress messageProgress, final ContentLoadingProgressBar contentLoading) {

            if (HelperDownloadFile.isDownLoading(mList.get(position).item.getAttachment().getCacheId())) {
                stopDownload(position, messageProgress, contentLoading);
            } else {
                startDownload(position, messageProgress, contentLoading);
            }
        }

        public boolean resetSelected() {

            boolean result = isSelectedMode;

            if (isSelectedMode == true) {
                isSelectedMode = false;

                SelectedList.clear();
                notifyDataSetChanged();

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

        public class ViewHolderTime extends RealmViewHolder {
            public TextView txtTime;

            public ViewHolderTime(View view, int position) {
                super(view);
                txtTime = (TextView) itemView.findViewById(R.id.smslt_txt_time);
                txtTime.setText(mList.get(position).messageTime);
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

            if (mList.get(position).isItemTime) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (holder instanceof ImageAdapter.ViewHolder) {

                final ImageAdapter.ViewHolder vh = (ImageAdapter.ViewHolder) holder;

                // if thumpnail not exist download it
                File filet = new File(vh.tempFilePath);
                if (filet.exists()) {
                    G.imageLoader.displayImage(suitablePath(vh.tempFilePath), vh.imvPicFile);
                } else {

                    RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

                    if (at.getSmallThumbnail() != null) {
                        if (at.getSmallThumbnail().getSize() > 0) {

                            HelperDownloadFile.startDownload(at.getToken(), at.getCacheId(), at.getName(), at.getSmallThumbnail().getSize(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override
                                public void OnProgress(final String path, int progress) {

                                    if (progress == 100) {
                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                G.imageLoader.displayImage(AndroidUtils.suitablePath(path), vh.imvPicFile);
                                            }
                                        });
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

                    needDownloadList.put(mList.get(position).item.getMessageId(), true);

                    vh.messageProgress.setVisibility(View.VISIBLE);
                }
            }
        }

        public class ViewHolder extends mHolder {

            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                tempFilePath = getThumpnailPath(position);
                filePath = getFilePath(position);

                imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            showImage(position, holder);
        }

        private void showImage(int position, RecyclerView.ViewHolder holder) {

            long selectedFileToken = mList.get(position).item.getMessageId();

            FragmentShowImage fragment = FragmentShowImage.newInstance();
            Bundle bundle = new Bundle();
            bundle.putLong("RoomId", roomId);
            bundle.putLong("SelectedImage", selectedFileToken);
            bundle.putString("TYPE", ProtoGlobal.RoomMessageType.IMAGE.toString());
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.asm_ll_parent, fragment, "Show_Image_fragment_shared_media").commit();
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

            if (mList.get(position).isItemTime) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        public class ViewHolder extends mHolder {
            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

                imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);

                itemView.findViewById(R.id.smsl_ll_video).setVisibility(View.VISIBLE);

                TextView txtVideoIcon = (TextView) itemView.findViewById(R.id.smsl_txt_video_icon);

                TextView txtVideoTime = (TextView) itemView.findViewById(R.id.smsl_txt_video_time);
                txtVideoTime.setText(AppUtils.humanReadableDuration(at.getDuration()));

                TextView txtVideoSize = (TextView) itemView.findViewById(R.id.smsl_txt_video_size);
                txtVideoSize.setText("(" + AndroidUtils.humanReadableByteCount(at.getSize(), true) + ")");

                tempFilePath = getThumpnailPath(position);

                File filethumpnail = new File(tempFilePath);

                if (filethumpnail.exists()) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), imvPicFile);
                } else {
                    imvPicFile.setImageResource(R.mipmap.j_video);

                    if (at.getSmallThumbnail() != null) {
                        if (at.getSmallThumbnail().getSize() > 0) {

                            HelperDownloadFile.startDownload(at.getToken(), at.getCacheId(), at.getName(), at.getSmallThumbnail().getSize(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override
                                public void OnProgress(final String path, int progress) {

                                    if (progress == 100) {
                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                G.imageLoader.displayImage(AndroidUtils.suitablePath(path), imvPicFile);
                                            }
                                        });
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
                    messageProgress.setVisibility(View.GONE);
                } else {
                    needDownloadList.put(mList.get(position).item.getMessageId(), true);
                    messageProgress.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playVideo(position, holder);
        }

        private void playVideo(int position, RecyclerView.ViewHolder holder) {
            //
            //ViewHolder vh = (ViewHolder) holder;
            //
            //Intent intent = HelperMimeType.appropriateProgram(vh.filePath);
            //if (intent != null) context.startActivity(intent);
            long selectedFileToken = mNewList.get(position).item.getMessageId();

            Fragment fragment = FragmentShowImage.newInstance();
            Bundle bundle = new Bundle();
            bundle.putLong("RoomId", roomId);
            bundle.putLong("SelectedImage", selectedFileToken);
            bundle.putString("TYPE", ProtoGlobal.RoomMessageType.VIDEO.toString());
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.asm_ll_parent, fragment, "Show_Image_fragment_shared_media").commit();
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

            if (mList.get(position).isItemTime) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        public class ViewHolder extends mHolder {
            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                RealmAttachment at = mList.get(position).item.getAttachment();

                imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
                imvPicFile.setImageResource(R.drawable.green_music_note);

                tempFilePath = getThumpnailPath(position);
                filePath = getFilePath(position);

                TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
                txtFileName.setText(at.getName());

                TextView txtFileSize = (TextView) itemView.findViewById(R.id.smslf_txt_file_size);
                txtFileSize.setText("(" + AndroidUtils.humanReadableByteCount(at.getSize(), true) + ")");

                TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);
                File file = new File(filePath);

                if (file.exists()) {
                    messageProgress.setVisibility(View.GONE);

                    try {

                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        Uri uri = Uri.fromFile(file);
                        mediaMetadataRetriever.setDataSource(context, uri);
                        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                        if (artist == null) {
                            artist = context.getString(R.string.unknown_artist);
                        }
                        txtFileInfo.setText(artist);

                        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                        if (data != null) {
                            Bitmap mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);
                            imvPicFile.setImageBitmap(mediaThumpnail);
                        } else {
                            file = new File(tempFilePath);
                            if (file.exists()) {
                                G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), imvPicFile);
                            }
                        }
                    } catch (Exception e) {
                    }
                } else {
                    needDownloadList.put(mList.get(position).item.getMessageId(), true);
                    messageProgress.setVisibility(View.VISIBLE);
                    file = new File(tempFilePath);
                    if (file.exists()) {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), imvPicFile);
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

            MusicPlayer.startPlayer(vh.filePath, mList.get(position).item.getAttachment().getName(), roomId, true, mList.get(position).item.getMessageId() + "");
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

            if (mList.get(position).isItemTime) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_gif, null);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        public class ViewHolder extends mHolder {

            GifImageView gifView;
            GifDrawable gifDrawable;

            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                gifView = (GifImageView) itemView.findViewById(R.id.smslg_gif_view);
                RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

                filePath = getFilePath(position);

                File file = new File(filePath);
                if (file.exists()) {
                    gifView.setImageURI(Uri.fromFile(file));

                    gifDrawable = (GifDrawable) gifView.getDrawable();

                    messageProgress.withDrawable(R.drawable.ic_play, true);
                    messageProgress.setVisibility(View.GONE);
                    messageProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (gifDrawable != null) {
                                gifDrawable.start();
                                messageProgress.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    needDownloadList.put(mList.get(position).item.getMessageId(), true);
                    messageProgress.setVisibility(View.VISIBLE);

                    tempFilePath = getThumpnailPath(position);

                    File filethumpnail = new File(tempFilePath);

                    if (filethumpnail.exists()) {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), gifView);
                    } else {
                        if (at.getSmallThumbnail() != null) {
                            if (at.getSmallThumbnail().getSize() > 0) {

                                HelperDownloadFile.startDownload(at.getToken(), at.getCacheId(), at.getName(), at.getSmallThumbnail().getSize(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, "", 4, new HelperDownloadFile.UpdateListener() {
                                    @Override
                                    public void OnProgress(final String path, int progress) {

                                        if (progress == 100) {
                                            G.currentActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), gifView);
                                                }
                                            });
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

            final ViewHolder vh = (ViewHolder) holder;

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
    }

    //****************************************************

    public class FileAdapter extends mAdapter {

        public FileAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (mList.get(position).isItemTime) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (holder instanceof ViewHolder) {
                ViewHolder vh = (ViewHolder) holder;
                File file = new File(vh.tempFilePath);

                if (file.exists()) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(vh.tempFilePath), vh.imvPicFile);
                } else {
                    Bitmap bitmap = HelperMimeType.getMimePic(context, HelperMimeType.getMimeResource(mList.get(position).item.getAttachment().getName()));
                    if (bitmap != null) vh.imvPicFile.setImageBitmap(bitmap);
                }
            }
        }

        public class ViewHolder extends mHolder {
            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                RealmAttachment at = mList.get(position).item.getAttachment();

                imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
                tempFilePath = getThumpnailPath(position);
                filePath = getFilePath(position);

                File file = new File(filePath);
                if (file.exists()) {
                    messageProgress.setVisibility(View.GONE);
                } else {
                    needDownloadList.put(mList.get(position).item.getMessageId(), true);
                    messageProgress.setVisibility(View.VISIBLE);
                }

                TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
                TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);
                TextView txtFileSize = (TextView) itemView.findViewById(R.id.smslf_txt_file_size);
                txtFileSize.setVisibility(View.INVISIBLE);

                txtFileName.setText(at.getName());
                txtFileInfo.setText(AndroidUtils.humanReadableByteCount(at.getSize(), true));
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            openFile(position, holder);
        }

        private void openFile(int position, RecyclerView.ViewHolder holder) {

            ViewHolder vh = (ViewHolder) holder;

            Intent intent = HelperMimeType.appropriateProgram(vh.filePath);
            if (intent != null) context.startActivity(intent);
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

            if (mList.get(position).isItemTime) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_media_sub_layout_link, null);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        public class ViewHolder extends mHolder {
            public TextView txtLink;

            public ViewHolder(View view, int position) {
                super(view, position);

                txtLink = (TextView) itemView.findViewById(R.id.smsll_txt_shared_link);

                txtLink.setText(HelperUrl.setUrlLink(mList.get(position).item.getMessage(), true, false, "", true));

                txtLink.setMovementMethod(LinkMovementMethod.getInstance());
                messageProgress.setVisibility(View.GONE);
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {

        }
    }
}
