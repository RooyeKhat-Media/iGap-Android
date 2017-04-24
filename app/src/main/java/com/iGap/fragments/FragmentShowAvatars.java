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

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperCalander;
import com.iGap.helper.HelperDownloadFile;
import com.iGap.helper.HelperSaveFile;
import com.iGap.interfaces.OnChannelAvatarDelete;
import com.iGap.interfaces.OnComplete;
import com.iGap.interfaces.OnGroupAvatarDelete;
import com.iGap.interfaces.OnUserAvatarDelete;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.TouchImageView;
import com.iGap.module.enums.ChannelChatRole;
import com.iGap.module.enums.GroupChatRole;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.request.RequestChannelAvatarDelete;
import com.iGap.request.RequestChannelAvatarGetList;
import com.iGap.request.RequestGroupAvatarDelete;
import com.iGap.request.RequestGroupAvatarGetList;
import com.iGap.request.RequestUserAvatarDelete;
import com.iGap.request.RequestUserAvatarGetList;
import io.meness.github.messageprogress.MessageProgress;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import java.io.File;

import static com.iGap.module.AndroidUtils.suitablePath;

public class FragmentShowAvatars extends android.support.v4.app.Fragment {

    private static final String ARG_PEER_ID = "arg_peer_id";
    private static final String ARG_Type = "arg_type";

    From from = From.chat;
    public static final int mChatNumber = 1;
    public static final int mGroupNumber = 2;
    public static final int mChannelNumber = 3;
    public static final int mSettingNumber = 4;

    public enum From {
        chat(mChatNumber),
        group(mGroupNumber),
        channel(mChannelNumber),
        setting(mSettingNumber);

        public int value;

        From(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private TextView txtImageNumber;
    private TextView txtImageName;

    private LinearLayout toolbarShowImage;
    private boolean isShowToolbar = true;
    private ViewGroup ltImageName;
    private ViewPager viewPager;

    private long mPeerId = -1;
    private GroupChatRole roleGroup;
    private ChannelChatRole roleChannel;
    private int avatarListSize = 0;

    private FragmentShowAvatars.AdapterViewPager mAdapter;
    private RealmResults<RealmAvatar> avatarList;
    public static OnComplete onComplete;

    private Realm mRealm;
    public static View appBarLayout;

    public static FragmentShowAvatars newInstance(long peerId, FragmentShowAvatars.From from) {
        Bundle args = new Bundle();
        args.putLong(ARG_PEER_ID, peerId);
        args.putSerializable(ARG_Type, from);

        FragmentShowAvatars fragment = new FragmentShowAvatars();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_show_image, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getIntentData(this.getArguments())) {
            initComponent(view);
        } else {
            getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentShowAvatars.this).commit();
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();

        if (avatarList != null) avatarList.removeChangeListeners();

        if (appBarLayout != null) appBarLayout.setVisibility(View.VISIBLE);

        if (mRealm != null) mRealm.close();
    }

    @Override public void onAttach(Context context) {
        if (appBarLayout != null) appBarLayout.setVisibility(View.GONE);

        super.onAttach(context);
    }

    private boolean getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image

            // init passed data through bundle
            mPeerId = getArguments().getLong(ARG_PEER_ID, -1);

            From result = (From) getArguments().getSerializable(ARG_Type);

            if (result != null) from = result;

            mRealm = Realm.getDefaultInstance();

            fillListAvatar(from);

            if (avatarListSize > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void initComponent(View view) {

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.asi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override public void onComplete(RippleView rippleView) {
                getActivity().onBackPressed();
            }
        });

        MaterialDesignTextView btnMenu = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_menu);
        RippleView rippleMenu = (RippleView) view.findViewById(R.id.asi_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override public void onComplete(RippleView rippleView) {

                switch (from) {
                    case setting:
                        showPopupMenu(R.array.pop_up_menu_show_avatar_setting);
                        break;
                    case group:
                        if (roleGroup == GroupChatRole.OWNER || roleGroup == GroupChatRole.ADMIN) {
                            showPopupMenu(R.array.pop_up_menu_show_avatar_setting);
                        } else {
                            showPopupMenu(R.array.pop_up_menu_show_avatar);
                        }
                        break;
                    case channel:
                        if (roleChannel == ChannelChatRole.OWNER || roleChannel == ChannelChatRole.ADMIN) {
                            showPopupMenu(R.array.pop_up_menu_show_avatar_setting);
                        } else {
                            showPopupMenu(R.array.pop_up_menu_show_avatar);
                        }
                        break;
                    case chat:
                        showPopupMenu(R.array.pop_up_menu_show_avatar);
                        break;
                }
            }
        });
        viewPager = (ViewPager) view.findViewById(R.id.asi_view_pager);

        txtImageNumber = (TextView) view.findViewById(R.id.asi_txt_image_number);
        txtImageName = (TextView) view.findViewById(R.id.asi_txt_image_name);
        ltImageName = (ViewGroup) view.findViewById(R.id.asi_layout_image_name);

        toolbarShowImage = (LinearLayout) view.findViewById(R.id.toolbarShowImage);

        initViewPager();
    }

    private void fillListAvatar(From from) {

        boolean isRoomExist = false;

        switch (from) {
            case chat:
            case setting:
                RealmRegisteredInfo user = mRealm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mPeerId).findFirst();
                if (user != null) {
                    new RequestUserAvatarGetList().userAvatarGetList(mPeerId);
                    isRoomExist = true;
                }
                break;
            case group:
                RealmRoom roomGroup = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mPeerId).findFirst();
                if (roomGroup != null) {
                    new RequestGroupAvatarGetList().groupAvatarGetList(mPeerId);
                    isRoomExist = true;
                    roleGroup = roomGroup.getGroupRoom().getRole();
                }
                break;
            case channel:
                RealmRoom roomChannel = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mPeerId).findFirst();
                if (roomChannel != null) {
                    new RequestChannelAvatarGetList().channelAvatarGetList(mPeerId);
                    isRoomExist = true;
                    roleChannel = roomChannel.getChannelRoom().getRole();
                }
                break;
        }

        if (isRoomExist) {

            avatarList = mRealm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, mPeerId).findAllSorted(RealmAvatarFields.UID, Sort.DESCENDING);
            avatarList.addChangeListener(new RealmChangeListener<RealmResults<RealmAvatar>>() {
                @Override public void onChange(RealmResults<RealmAvatar> element) {

                    if (avatarListSize != element.size()) {

                        avatarListSize = element.size();

                        viewPager.setAdapter(new FragmentShowAvatars.AdapterViewPager());

                        if (avatarListSize > 0) {
                            viewPager.getAdapter().notifyDataSetChanged();
                            txtImageNumber.setText(viewPager.getCurrentItem() + 1 + " " + getString(R.string.of) + " " + avatarListSize);
                            if (HelperCalander.isLanguagePersian) {
                                txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
                            }

                        } else {
                            getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentShowAvatars.this).commit();
                        }
                    }
                }
            });

            avatarListSize = avatarList.size();
        }
    }

    //***************************************************************************************

    private void initViewPager() {

        mAdapter = new FragmentShowAvatars.AdapterViewPager();
        viewPager.setAdapter(mAdapter);


        txtImageNumber.setText(1 + " " + getString(R.string.of) + " " + avatarList.size());
        if (HelperCalander.isLanguagePersian) {
            txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
        }

        if (avatarList.get(0).getFile() != null) {
            txtImageName.setText(avatarList.get(0).getFile().getName());
        }

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override public void onPageSelected(int position) {

                txtImageNumber.setText(position + 1 + " " + getString(R.string.of) + " " + avatarList.size());

                if (avatarList.get(position).getFile() != null) {
                    txtImageName.setText(avatarList.get(position).getFile().getName());
                }
            }

            @Override public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override public void transformPage(View view, float position) {

                final float normalizedPosition = Math.abs(Math.abs(position) - 1);
                view.setScaleX(normalizedPosition / 2 + 0.5f);
                view.setScaleY(normalizedPosition / 2 + 0.5f);
            }
        });
    }

    private void showPopupMenu(int r) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).items(r).contentColor(Color.BLACK).itemsCallback(new MaterialDialog.ListCallback() {
            @Override public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                if (which == 0) {
                    saveToGallery();
                } else if (which == 1) {

                    switch (from) {
                        case setting:
                            deletePhotoSetting();
                            break;
                        case group:
                            deletePhotoGroup();
                            break;
                        case channel:
                            deletePhotoChannel();
                            break;
                        case chat:
                            deletePhotoChat();
                            break;
                    }
                }
            }
        }).show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp200);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    //private void shareImage() {
    //
    //    RealmRoomMessage rm = mFList.get(viewPager.getCurrentItem());
    //
    //    if (rm != null) {
    //
    //        if (rm.getForwardMessage() != null) rm = rm.getForwardMessage();
    //
    //
    //        String path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
    //        File file = new File(path);
    //        if (file.exists()) {
    //
    //            Intent intent = new Intent(Intent.ACTION_SEND);
    //            intent.putExtra(Intent.EXTRA_TEXT, "iGap/download this image");
    //            Uri screenshotUri = Uri.parse(path);
    //
    //            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
    //            intent.setType("image/*");
    //            startActivity(Intent.createChooser(intent, getString(R.string.share_image_from_igap)));
    //        }
    //    }
    //}

    private void saveToGallery() {

        if (avatarList.get(viewPager.getCurrentItem()).getFile() != null) {
            String media = avatarList.get(viewPager.getCurrentItem()).getFile().getLocalFilePath();
            if (media != null) {
                File file = new File(media);
                if (file.exists()) {
                    HelperSaveFile.savePicToGallary(media, true);
                }
            }
        }
    }

    private class AdapterViewPager extends PagerAdapter {

        @Override public int getCount() {
            return avatarList.size();
        }

        @Override public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override public Object instantiateItem(View container, final int position) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container, false);

            final TouchImageView touchImageView = (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);
            final ImageView imgPlay = (ImageView) layout.findViewById(R.id.imgPlay);
            imgPlay.setVisibility(View.GONE);
            final MessageProgress progress = (MessageProgress) layout.findViewById(R.id.progress);

            final ContentLoadingProgressBar contentLoading = (ContentLoadingProgressBar) layout.findViewById(R.id.ch_progress_loadingContent);
            contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

            final RealmAttachment ra = avatarList.get(position).getFile();

            if (HelperDownloadFile.isDownLoading(ra.getCacheId())) {
                progress.withDrawable(R.drawable.ic_cancel, true);
                startDownload(position, progress, touchImageView, contentLoading);
            } else {
                progress.withDrawable(R.drawable.ic_download, true);
                contentLoading.setVisibility(View.GONE);
            }

            if (ra != null) {
                String path = ra.getLocalFilePath() != null ? ra.getLocalFilePath() : "";

                File file = new File(path);
                if (file.exists()) {
                    G.imageLoader.displayImage(suitablePath(path), touchImageView);
                    progress.setVisibility(View.GONE);
                } else {
                    path = ra.getLocalThumbnailPath() != null ? ra.getLocalThumbnailPath() : "";
                    file = new File(path);
                    if (file.exists()) {
                        G.imageLoader.displayImage(suitablePath(path), touchImageView);
                    } else {
                        // if thumpnail not exist download it
                        ProtoFileDownload.FileDownload.Selector selector = null;
                        long fileSize = 0;

                        if (ra.getSmallThumbnail() != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                            fileSize = ra.getSmallThumbnail().getSize();
                        } else if (ra.getLargeThumbnail() != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                            fileSize = ra.getLargeThumbnail().getSize();
                        }

                        final String filePathTumpnail = AndroidUtils.getFilePathWithCashId(ra.getCacheId(), ra.getName(), G.DIR_TEMP, true);


                        if (selector != null && fileSize > 0) {
                            HelperDownloadFile.startDownload(ra.getToken(), ra.getCacheId(), ra.getName(), fileSize, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override public void OnProgress(final String path, int progress) {

                                    if (progress == 100) {

                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                G.imageLoader.displayImage(AndroidUtils.suitablePath(path), touchImageView);
                                            }
                                        });
                                    }
                                }

                                @Override public void OnError(String token) {

                                }
                            });
                        }
                    }
                }
            }

            progress.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {

                    String _cashId = avatarList.get(position).getFile().getCacheId();

                    if (HelperDownloadFile.isDownLoading(_cashId)) {
                        HelperDownloadFile.stopDownLoad(_cashId);
                    } else {
                        progress.withDrawable(R.drawable.ic_cancel, true);
                        startDownload(position, progress, touchImageView, contentLoading);
                    }
                }
            });

            touchImageView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
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

            ((ViewGroup) container).addView(layout);
            return layout;
        }

        private void startDownload(int position, final MessageProgress progress, final TouchImageView touchImageView, final ContentLoadingProgressBar contentLoading) {

            contentLoading.setVisibility(View.VISIBLE);

            final RealmAttachment ra = avatarList.get(position).getFile();

            final String dirPath = AndroidUtils.getFilePathWithCashId(ra.getCacheId(), ra.getName(), G.DIR_IMAGE_USER, false);

            HelperDownloadFile.startDownload(ra.getToken(), ra.getCacheId(), ra.getName(), ra.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, dirPath, 4,
                new HelperDownloadFile.UpdateListener() {
                    @Override public void OnProgress(final String path, final int progres) {

                    if (progress != null) {

                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override public void run() {
                                if (progres < 100) {
                                    progress.withProgress(progres);
                                } else {
                                    progress.withProgress(0);
                                    progress.setVisibility(View.GONE);
                                    contentLoading.setVisibility(View.GONE);

                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), touchImageView);
                                }
                            }
                        });
                    }
                }

                @Override public void OnError(String token) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override public void run() {
                            progress.withProgress(0);
                            progress.withDrawable(R.drawable.ic_download, true);
                            contentLoading.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //******************************************************************************************************

    private void deletePhotoChannel() {

        G.onChannelAvatarDelete = new OnChannelAvatarDelete() {
            @Override public void onChannelAvatarDelete(long roomId, long avatarId) {
                if (onComplete != null) onComplete.complete(true, "" + avatarId, "");
            }

            @Override public void onError(int majorCode, int minorCode) {

            }

            @Override public void onTimeOut() {

            }
        };

        new RequestChannelAvatarDelete().channelAvatarDelete(mPeerId, avatarList.get(viewPager.getCurrentItem()).getId());
    }

    private void deletePhotoGroup() {

        G.onGroupAvatarDelete = new OnGroupAvatarDelete() {
            @Override public void onDeleteAvatar(long roomId, final long avatarId) {
                G.handler.post(new Runnable() {
                    @Override public void run() {
                        if (onComplete != null) onComplete.complete(true, "" + avatarId, "");
                    }
                });
            }

            @Override public void onDeleteAvatarError(int majorCode, int minorCode) {

            }

            @Override public void onTimeOut() {

            }
        };

        new RequestGroupAvatarDelete().groupAvatarDelete(mPeerId, avatarList.get(viewPager.getCurrentItem()).getId());
    }

    private void deletePhotoSetting() {

        G.onUserAvatarDelete = new OnUserAvatarDelete() {
            @Override public void onUserAvatarDelete(long avatarId, String token) {
                if (onComplete != null) onComplete.complete(true, "" + avatarId, "");
            }

            @Override public void onUserAvatarDeleteError() {

            }
        };

        new RequestUserAvatarDelete().userAvatarDelete(avatarList.get(viewPager.getCurrentItem()).getId());
    }

    private void deletePhotoChat() {

    }
}