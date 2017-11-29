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
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperSaveFile;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.TouchImageView;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.module.transition.fragment.ExitFragmentTransition;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.module.AndroidUtils.suitablePath;

public class FragmentShowImage extends BaseFragment {

    private TextView txtImageNumber;
    private TextView txtImageName;
    private TextView txtImageDate;
    private TextView txtImageTime;
    private EmojiTextViewE txtImageDesc;
    private LinearLayout toolbarShowImage;
    private boolean isShowToolbar = true;
    private ViewGroup ltImageName;
    private ViewPager viewPager;
    private boolean isFirstPlay = true;
    private boolean isReadyToPause = true;
    private int selectedFile = 0;
    private AdapterViewPager mAdapter;
    private RealmResults<RealmRoomMessage> mRealmList;

    private ArrayList<RealmRoomMessage> mFList = new ArrayList<>();

    private Long mRoomId;
    private Long selectedFileToken;
    private MediaPlayer mMediaPlayer;
    public static ArrayList<String> downloadedList = new ArrayList<>();

    public View appBarLayout;
    public MediaController videoController;
    public int po;
    private String path;
    private String type = null;
    private boolean isLockScreen = false;
    private Realm realmShowImage;
    public final String ROOM_ID = "roomId";
    public final String SELECTED_IMAGE = "selectedImage";
    public final String TYPE = "type";
    private ViewGroup rooShowImage;
    private ViewGroup mainShowImage;
    private ExitFragmentTransition exitFragmentTransition;

    public static FragmentShowImage newInstance() {
        return new FragmentShowImage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realmShowImage = Realm.getDefaultInstance();

        //View view = inflater.inflate(R.layout.activity_show_image, container, false);
        //exitFragmentTransition = FragmentTransition.with(this).duration(200).interpolator(new LinearOutSlowInInterpolator()).to(view.findViewById(R.id.asi_view_pager)).start(savedInstanceState);
        //
        //exitFragmentTransition.exitListener(new AnimatorListenerAdapter() {
        //    @Override
        //    public void onAnimationStart(Animator animation) {
        //        Log.d("FFFFFFF", "onAnimationStart: ");
        //    }
        //
        //    @Override
        //    public void onAnimationEnd(Animator animation) {
        //        Log.d("FFFFFFF", "onAnimationEnd: ");
        //    }
        //}).interpolator(new FastOutSlowInInterpolator());
        //exitFragmentTransition.startExitListening(view.findViewById(R.id.rooShowImage));

        return inflater.inflate(R.layout.activity_show_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getIntentData(this.getArguments())) {
            initComponent(view);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (appBarLayout != null) {
            appBarLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        if (appBarLayout != null) {
            appBarLayout.setVisibility(View.GONE);
        }
        super.onAttach(context);
    }

    private Realm getRealm() {
        if (realmShowImage == null || !realmShowImage.isClosed()) {
            realmShowImage = Realm.getDefaultInstance();
        }
        return realmShowImage;
    }

    private boolean getIntentData(Bundle bundle) {
        if (G.fragmentActivity != null) {
            G.fragmentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (bundle != null) { // get a list of image
            mRoomId = bundle.getLong("RoomId");
            selectedFileToken = bundle.getLong("SelectedImage");
            if (bundle.getString("TYPE") != null) type = bundle.getString("TYPE");
            if (mRoomId == null) {
                // G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();

                popBackStackFragment();

                return false;
            }

            mRealmList = RealmRoomMessage.findSorted(getRealm(), mRoomId, RealmRoomMessageFields.UPDATE_TIME, Sort.ASCENDING);
            if (mRealmList.size() < 1) {
                popBackStackFragment();
                return false;
            }

            for (RealmRoomMessage roomMessage : mRealmList) {
                if (RealmRoomMessage.isImageOrVideo(roomMessage, convertType(type))) {
                    mFList.add(roomMessage);
                }
            }

            if (selectedFileToken != null) {
                for (int i = mFList.size() - 1; i >= 0; i--) {
                    if (selectedFileToken == mFList.get(i).getMessageId()) {
                        selectedFile = i;
                        break;
                    }
                }
            }

            return true;
        } else {
            if (G.fragmentActivity != null) {
                popBackStackFragment();
            }
            return false;
        }
    }

    private ProtoGlobal.RoomMessageType convertType(String type) {
        if (type != null) {
            if (type.contains(ProtoGlobal.RoomMessageType.VIDEO.toString())) {
                return ProtoGlobal.RoomMessageType.VIDEO;
            } else if (type.contains(ProtoGlobal.RoomMessageType.IMAGE.toString())) {
                return ProtoGlobal.RoomMessageType.IMAGE;
            }
        }
        return null;
    }

    private void initComponent(View view) {

        downloadedList.clear();

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.asi_ripple_back);

        //rippleBack.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //
        //        exitFragmentTransition.startButtonExitListening(rooShowImage);
        //
        //    }
        //});

        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.onBackPressed();
            }
        });

        MaterialDesignTextView btnMenu = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_menu);
        RippleView rippleMenu = (RippleView) view.findViewById(R.id.asi_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                popUpMenuShowImage();
            }
        });

        //rooShowImage = (ViewGroup) view.findViewById(R.id.rooShowImage);
        //
        //int colorFrom = getResources().getColor(R.color.transparent);
        //int colorTo = getResources().getColor(R.color.black);
        //ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        //colorAnimation.setDuration(300); // milliseconds
        //colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        //
        //    @Override
        //    public void onAnimationUpdate(ValueAnimator animator) {
        //        rooShowImage.setBackgroundColor((int) animator.getAnimatedValue());
        //    }
        //
        //});
        //
        //colorAnimation.start();


        viewPager = (ViewPager) view.findViewById(R.id.asi_view_pager);

        txtImageNumber = (TextView) view.findViewById(R.id.asi_txt_image_number);
        ltImageName = (ViewGroup) view.findViewById(R.id.asi_layout_image_name);
        txtImageName = (TextView) view.findViewById(R.id.asi_txt_image_name);
        txtImageDate = (TextView) view.findViewById(R.id.asi_txt_image_date);
        txtImageTime = (TextView) view.findViewById(R.id.asi_txt_image_time);
        txtImageDesc = (EmojiTextViewE) view.findViewById(R.id.asi_txt_image_desc);
        toolbarShowImage = (LinearLayout) view.findViewById(R.id.toolbarShowImage);

        initViewPager();
    }

    //***************************************************************************************

    private void initViewPager() {

        mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);

        viewPager.setCurrentItem(selectedFile);

        txtImageNumber.setText(selectedFile + 1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + mFList.size());
        if (HelperCalander.isPersianUnicode) {
            txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
        }
        if (selectedFile >= mFList.size()) {
            return;
        }

        showImageInfo(mFList.get(selectedFile));

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {

                final float normalizedPosition = Math.abs(Math.abs(position) - 1);
                view.setScaleX(normalizedPosition / 2 + 0.5f);
                view.setScaleY(normalizedPosition / 2 + 0.5f);
            }
        });
    }

    /**
     * show image info, time , name , description
     */
    private void showImageInfo(RealmRoomMessage realmRoomMessage) {
        if (realmRoomMessage == null || G.authorHash == null) {
            return;
        }
        RealmRoomMessage realmRoomMessageFinal = RealmRoomMessage.getFinalMessage(realmRoomMessage);

        if (realmRoomMessageFinal.getMessage() != null && !realmRoomMessageFinal.getMessage().isEmpty()) {
            txtImageDesc.setText(realmRoomMessageFinal.getMessage());
            txtImageDesc.setVisibility(View.VISIBLE);
        } else {
            txtImageDesc.setVisibility(View.GONE);
        }

        //+Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealm(), realmRoomMessageFinal.getUserId());

        if (realmRegisteredInfo != null) {
            txtImageName.setText(realmRegisteredInfo.getDisplayName());
        } else {
            txtImageName.setText("");
        }

        if (realmRoomMessageFinal.getAuthorHash() != null && G.authorHash.equals(realmRoomMessageFinal.getAuthorHash())) {

            txtImageName.setText(R.string.you);
        }

        if (realmRoomMessageFinal.getUpdateTime() != 0) {
            txtImageTime.setText(HelperCalander.getClocktime(realmRoomMessageFinal.getUpdateTime(), true));
            txtImageDate.setText(HelperCalander.checkHijriAndReturnTime(realmRoomMessageFinal.getUpdateTime() / 1000));
        }

        if (HelperCalander.isPersianUnicode) {
            txtImageName.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageName.getText().toString()));
            txtImageTime.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageTime.getText().toString()));
            txtImageDate.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageDate.getText().toString()));
        }

        //realm.close();

    }

    public void popUpMenuShowImage() {

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);

        final TextView txtShare = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtSaveToGallery = (TextView) v.findViewById(R.id.dialog_text_item2_notification);

        TextView iconSaveToGallery = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
        iconSaveToGallery.setText(G.fragmentActivity.getResources().getString(R.string.md_save));

        root1.setVisibility(View.VISIBLE);
        root2.setVisibility(View.VISIBLE);

        txtShare.setText(getResources().getString(R.string.save_to_gallery));
        txtSaveToGallery.setText(G.fragmentActivity.getResources().getString(R.string.share_image));

        TextView iconShare = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconShare.setText(G.fragmentActivity.getResources().getString(R.string.md_share_button));

        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                saveToGallery();
            }
        });

        root2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareImage();
            }
        });

    }

    /**
     * share Image and video
     */
    private void shareImage() {

        RealmRoomMessage roomMessage = mFList.get(viewPager.getCurrentItem());

        if (roomMessage != null) {
            roomMessage = RealmRoomMessage.getFinalMessage(roomMessage);
            String path = getFilePath(viewPager.getCurrentItem());
            File file = new File(path);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                //+Realm realm = Realm.getDefaultInstance();
                AppUtils.shareItem(intent, StructMessageInfo.convert(getRealm(), roomMessage));
                //putExtra(intent, StructMessageInfo.convert(getRealm(), roomMessage));
                startActivity(Intent.createChooser(intent, G.fragmentActivity.getResources().getString(R.string.share_image_from_igap)));
            }
        }
    }


    /**
     * share Image and video
     */
    private void saveToGallery() {
        RealmRoomMessage rm = mFList.get(viewPager.getCurrentItem());
        if (rm != null) {
            String path = getFilePath(viewPager.getCurrentItem());
            ProtoGlobal.RoomMessageType messageType;
            if (rm.getForwardMessage() != null) {
                messageType = rm.getForwardMessage().getMessageType();
            } else {
                messageType = rm.getMessageType();
            }
            File file = new File(path);
            if (file.exists()) {
                if (messageType == ProtoGlobal.RoomMessageType.VIDEO) {
                    HelperSaveFile.saveFileToDownLoadFolder(path, "VIDEO_" + System.currentTimeMillis() + ".mp4", HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
                } else if (messageType == ProtoGlobal.RoomMessageType.IMAGE) {
                    HelperSaveFile.savePicToGallery(path, true);
                }
            }
        }
    }

    /**
     * adapter for view pager
     */
    private class AdapterViewPager extends PagerAdapter implements MediaController.MediaPlayerControl {

        private String videoPath;
        private int lastPosition;

        @Override
        public int getCount() {
            return mFList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            LayoutInflater inflater = LayoutInflater.from(G.fragmentActivity);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container, false);
            final TextureView mTextureView = (TextureView) layout.findViewById(R.id.textureView);
            final ImageView imgPlay = (ImageView) layout.findViewById(R.id.imgPlay);
            final TouchImageView touchImageView = (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);

            final MessageProgress progress = (MessageProgress) layout.findViewById(R.id.progress);
            AppUtils.setProgresColor(progress.progressBar);


            final ContentLoadingProgressBar contentLoading = (ContentLoadingProgressBar) layout.findViewById(R.id.ch_progress_loadingContent);
            contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
            final RealmRoomMessage rm = RealmRoomMessage.getFinalMessage(mFList.get(position));

            if (rm != null) {
                if (HelperDownloadFile.isDownLoading(rm.getAttachment().getCacheId())) {
                    progress.withDrawable(R.drawable.ic_cancel, true);
                    startDownload(position, progress, touchImageView, contentLoading, imgPlay, mTextureView);
                } else {
                    progress.withDrawable(R.drawable.ic_download, true);
                    contentLoading.setVisibility(View.GONE);
                }

                path = getFilePath(position);
                File file = new File(path);
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                    if (videoController != null) videoController.hide();
                }
                if (file.exists()) {
                    progress.setVisibility(View.GONE);
                    G.imageLoader.displayImage(suitablePath(path), touchImageView);
                    if (rm.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE || rm.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                        touchImageView.setVisibility(View.VISIBLE);
                        imgPlay.setVisibility(View.GONE);
                        isFirstPlay = false;
                    } else {
                        if (isFirstPlay) {
                            mTextureView.setVisibility(View.VISIBLE);

                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    playVideo(position, mTextureView, imgPlay, touchImageView);
                                }
                            }, 100);
                            isFirstPlay = false;
                        }
                        imgPlay.setVisibility(View.VISIBLE);
                        mTextureView.setVisibility(View.INVISIBLE);
                        videoPath = path;
                    }
                } else {
                    imgPlay.setVisibility(View.GONE);
                    path = getThumbnailPath(rm);
                    touchImageView.setVisibility(View.VISIBLE);
                    file = new File(path);
                    if (file.exists()) {
                        G.imageLoader.displayImage(suitablePath(path), touchImageView);
                    } else if (rm.getAttachment() != null) {
                        // if thumpnail not exist download it
                        ProtoFileDownload.FileDownload.Selector selector = null;
                        long fileSize = 0;

                        if (rm.getAttachment().getSmallThumbnail() != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                            fileSize = rm.getAttachment().getSmallThumbnail().getSize();
                        } else if (rm.getAttachment().getLargeThumbnail() != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                            fileSize = rm.getAttachment().getLargeThumbnail().getSize();
                        }

                        final String filePathTumpnail = AndroidUtils.getFilePathWithCashId(rm.getAttachment().getCacheId(), rm.getAttachment().getName(), G.DIR_TEMP, true);

                        if (selector != null && fileSize > 0) {
                            HelperDownloadFile.startDownload(System.currentTimeMillis() + "", rm.getAttachment().getToken(), rm.getAttachment().getCacheId(), rm.getAttachment().getName(), fileSize, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override
                                public void OnProgress(final String path, int progress) {

                                    if (progress == 100) {

                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (touchImageView != null) {
                                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), touchImageView);
                                                }

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

            progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String _cashID = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage().getAttachment().getCacheId() : mFList.get(position).getAttachment().getCacheId();

                    if (HelperDownloadFile.isDownLoading(_cashID)) {
                        HelperDownloadFile.stopDownLoad(_cashID);
                    } else {
                        progress.withDrawable(R.drawable.ic_cancel, true);
                        startDownload(position, progress, touchImageView, contentLoading, imgPlay, mTextureView);
                    }
                }
            });

            touchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isShowToolbar) {
                        toolbarShowImage.animate().setDuration(150).alpha(0F).start();
                        ltImageName.setVisibility(View.GONE);
                        ltImageName.animate().setDuration(150).alpha(0F).start();
                        toolbarShowImage.setVisibility(View.GONE);
                        isShowToolbar = false;
                    } else {
                        toolbarShowImage.animate().setDuration(150).alpha(1F).start();
                        toolbarShowImage.setVisibility(View.VISIBLE);
                        ltImageName.animate().setDuration(150).alpha(1F).start();
                        ltImageName.setVisibility(View.VISIBLE);
                        isShowToolbar = true;
                    }
                }
            });

            imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mTextureView.setVisibility(View.VISIBLE);
                    playVideo(position, mTextureView, imgPlay, touchImageView);
                }
            });

            mTextureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMediaPlayer != null) {
                        if (videoController.isShowing()) {
                            videoController.setVisibility(View.GONE);
                        } else if (videoController != null) {
                            videoController.show();
                            videoController.setVisibility(View.VISIBLE);
                        }
                    }
                    touchImageView.performClick();
                }
            });
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {

                    if (isFirstPlay) {
                        if (mFList.get(position).getMessageType() == ProtoGlobal.RoomMessageType.IMAGE || mFList.get(position).getMessageType() == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                            isFirstPlay = false;
                        }
                    }
                }

                @Override
                public void onPageSelected(final int position) {

                    txtImageNumber.setText(position + 1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + mFList.size());
                    if (HelperCalander.isPersianUnicode) {
                        txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
                    }
                    showImageInfo(mFList.get(position));
                    ProtoGlobal.RoomMessageType type;
                    if (mFList.get(position).getForwardMessage() != null) {
                        type = mFList.get(position).getForwardMessage().getMessageType();
                    } else {
                        type = mFList.get(position).getMessageType();
                    }

                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) mMediaPlayer.stop();
                    if (type == ProtoGlobal.RoomMessageType.VIDEO || type == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                        File f = new File(getFilePath(position));
                        if (f.exists()) {
                            imgPlay.setVisibility(View.VISIBLE);
                            touchImageView.setVisibility(View.VISIBLE);
                        } else {
                            imgPlay.setVisibility(View.GONE);
                        }
                    } else if (type == ProtoGlobal.RoomMessageType.IMAGE || type == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                        imgPlay.setVisibility(View.GONE);
                    }
                    if (videoController != null) {
                        videoController.hide();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            ((ViewGroup) container).addView(layout);
            return layout;
        }

        /**
         * start download
         */
        private void startDownload(final int position, final MessageProgress progress, final TouchImageView touchImageView, final ContentLoadingProgressBar contentLoading, final ImageView imgPlay, final TextureView mTextureView) {

            contentLoading.setVisibility(View.VISIBLE);

            final RealmRoomMessage rm = RealmRoomMessage.getFinalMessage(mFList.get(position));

            String dirPath = AndroidUtils.getFilePathWithCashId(rm.getAttachment().getCacheId(), rm.getAttachment().getName(), rm.getMessageType());

            if (downloadedList.indexOf(rm.getAttachment().getToken()) == -1) {
                downloadedList.add(rm.getAttachment().getCacheId());
            }

            HelperDownloadFile.startDownload(System.currentTimeMillis() + "", rm.getAttachment().getToken(), rm.getAttachment().getCacheId(), rm.getAttachment().getName(), rm.getAttachment().getSize(), ProtoFileDownload.FileDownload.Selector.FILE, dirPath, 4, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, final int progres) {

                    if (progress != null) {

                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progres < 100) {
                                    progress.withProgress(progres);
                                } else {
                                    progress.withProgress(0);
                                    progress.setVisibility(View.GONE);
                                    contentLoading.setVisibility(View.GONE);
                                    if (rm.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO) {
                                        imgPlay.setVisibility(View.VISIBLE);
                                        //if (position == viewPager.getCurrentItem()) playVideo(position, mTextureView, imgPlay, touchImageView);
                                    }

                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), touchImageView);
                                }
                            }
                        });
                    }
                }

                @Override
                public void OnError(String token) {

                    if (progress != null) {

                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.withProgress(0);
                                progress.withDrawable(R.drawable.ic_download, true);
                                contentLoading.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * video player
         */
        private void playVideo(final int position, final TextureView mTextureView, final ImageView imgPlay, final TouchImageView touchImageView) {

            if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
            if (videoController == null) videoController = new MediaController(G.fragmentActivity);
            mTextureView.setVisibility(View.VISIBLE);
            videoPath = getFilePath(position);
            ViewCompat.setLayoutDirection(videoController, View.LAYOUT_DIRECTION_LTR);
            videoController.setAnchorView(touchImageView);
            videoController.setMediaPlayer(this);
            imgPlay.setVisibility(View.GONE);
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(G.fragmentActivity, Uri.parse(videoPath));
                mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        setMediaPlayer(mMediaPlayer, mTextureView, imgPlay, touchImageView);
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

                        return false;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                    }
                });
                if (mTextureView.getSurfaceTexture() != null) {
                    setMediaPlayer(mMediaPlayer, mTextureView, imgPlay, touchImageView);
                }
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.seekTo(0);
                    imgPlay.setVisibility(View.VISIBLE);
                }
            });

            //mMediaPlayer.seekTo(100);
        }

        private void setMediaPlayer(MediaPlayer mMediaPlayer, final TextureView mTextureView, final ImageView imgPlay, final TouchImageView touchImageView) {

            if (mTextureView == null) {
                return;
            }
            Surface surfaceTexture = new Surface(mTextureView.getSurfaceTexture());

            if (surfaceTexture == null) {
                return;
            }

            try {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setSurface(surfaceTexture);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepareAsync();
                }
            } catch (IllegalStateException e) {
                e.getMessage();
            }


            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    getRealSize(mp, mTextureView);
                    imgPlay.setVisibility(View.GONE);
                    mp.start();
                    MusicPlayer.pauseSound();
                    mTextureView.setVisibility(View.VISIBLE);
                    touchImageView.animate().setDuration(700).alpha(0F).start();

                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            touchImageView.setVisibility(View.GONE);
                            touchImageView.clearAnimation();
                        }
                    }, 700);

                    videoController.setEnabled(true);
                    videoController.show();
                }
            });

        }

        /**
         * get real width and height video
         */
        private void getRealSize(MediaPlayer mp, TextureView mTextureView) {
            //Get the dimensions of the video
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();

            //Get the width of the screen
            int screenWidth = G.fragmentActivity.getWindowManager().getDefaultDisplay().getWidth();

            //Get the SurfaceView layout parameters
            ViewGroup.LayoutParams lp = mTextureView.getLayoutParams();

            //Set the width of the SurfaceView to the width of the screen
            lp.width = screenWidth;

            //Set the height of the SurfaceView to match the aspect ratio of the video
            //be sure to cast these as floats otherwise the calculation will likely be 0
            lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);

            //Commit the layout parameters
            mTextureView.setLayoutParams(lp);
        }

        @Override
        public void start() {
            if (mMediaPlayer != null) mMediaPlayer.start();
            MusicPlayer.pauseSound();
        }

        @Override
        public void pause() {
            if (mMediaPlayer != null) mMediaPlayer.pause();
        }

        @Override
        public int getDuration() {
            if (mMediaPlayer != null) {
                return mMediaPlayer.getDuration();
            }
            return 0;
        }

        @Override
        public int getCurrentPosition() {
            if (mMediaPlayer != null) {
                return mMediaPlayer.getCurrentPosition();
            }
            return 0;
        }

        @Override
        public void seekTo(int pos) {
            if (mMediaPlayer != null) mMediaPlayer.seekTo(pos);
        }

        @Override
        public boolean isPlaying() {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return 0;
        }
    }

    public String getFilePath(int position) {

        String result = "";

        RealmAttachment at = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage().getAttachment() : mFList.get(position).getAttachment();

        if (at != null) {
            if (at.getLocalFilePath() != null) result = at.getLocalFilePath();
        }

        ProtoGlobal.RoomMessageType messageType = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage().getMessageType() : mFList.get(position).getMessageType();

        if (result.length() < 1) {
            result = AndroidUtils.getFilePathWithCashId(at.getCacheId(), at.getName(), messageType);
        }

        return result;
    }

    public String getThumbnailPath(RealmRoomMessage roomMessage) {

        String result = "";

        if (roomMessage == null) {
            return "";
        }

        if (roomMessage.getAttachment() != null) {
            if (roomMessage.getAttachment().getLocalThumbnailPath() != null) {
                result = roomMessage.getAttachment().getLocalThumbnailPath();
            }

            if (result.length() < 1) {
                result = AndroidUtils.getFilePathWithCashId(roomMessage.getAttachment().getCacheId(), roomMessage.getAttachment().getName(), G.DIR_TEMP, true);
            }
        }

        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (videoController != null) {
            videoController.hide();
            videoController = null;
        }

        if (realmShowImage != null && !realmShowImage.isClosed()) {
            realmShowImage.close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (videoController != null) {
            videoController.hide();
            videoController = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isLockScreen = true;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (isLockScreen) {

            if (videoController != null) {
                videoController.show();
            }
        }
    }
}