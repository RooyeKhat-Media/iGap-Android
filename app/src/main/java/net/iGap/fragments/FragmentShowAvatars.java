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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperSaveFile;
import net.iGap.interfaces.OnChannelAvatarDelete;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGroupAvatarDelete;
import net.iGap.interfaces.OnUserAvatarDelete;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.TouchImageView;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelAvatarDelete;
import net.iGap.request.RequestChannelAvatarGetList;
import net.iGap.request.RequestGroupAvatarDelete;
import net.iGap.request.RequestGroupAvatarGetList;
import net.iGap.request.RequestUserAvatarDelete;
import net.iGap.request.RequestUserAvatarGetList;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.R.string.array_Delete_photo;
import static net.iGap.module.AndroidUtils.suitablePath;

public class FragmentShowAvatars extends BaseFragment {

    public static final int mChatNumber = 1;
    public static final int mGroupNumber = 2;
    public static final int mChannelNumber = 3;
    public static final int mSettingNumber = 4;
    private static final String ARG_PEER_ID = "arg_peer_id";
    private static final String ARG_Type = "arg_type";
    public static OnComplete onComplete;
    public View appBarLayout;
    From from = From.chat;
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
    private Realm realm;

    public static FragmentShowAvatars newInstance(long peerId, FragmentShowAvatars.From from) {
        Bundle args = new Bundle();
        args.putLong(ARG_PEER_ID, peerId);
        args.putSerializable(ARG_Type, from);

        FragmentShowAvatars fragment = new FragmentShowAvatars();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_show_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getIntentData(this.getArguments())) {
            initComponent(view);
        } else {
            popBackStackFragment();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (avatarList != null) {
            avatarList.removeAllChangeListeners();
        }

        if (appBarLayout != null) {
            appBarLayout.setVisibility(View.VISIBLE);
        }

        if (realm != null) {
            realm.close();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        if (appBarLayout != null) appBarLayout.setVisibility(View.GONE);

        super.onAttach(context);
    }

    private boolean getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image

            // init passed data through bundle
            mPeerId = getArguments().getLong(ARG_PEER_ID, -1);

            From result = (From) getArguments().getSerializable(ARG_Type);

            if (result != null) from = result;

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

        //ViewGroup rooShowImage = (ViewGroup) view.findViewById(R.id.rooShowImage);
        //rooShowImage.setBackgroundColor(G.fragmentActivity.getResources().getColor(R.color.black));

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.asi_ripple_back);
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


                final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
                View v = dialog.getCustomView();

                DialogAnimation.animationUp(dialog);
                dialog.show();

                ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);


                final TextView txtSearch = (TextView) v.findViewById(R.id.dialog_text_item1_notification);


                TextView iconSearch = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);


                root1.setVisibility(View.VISIBLE);

                txtSearch.setText(G.fragmentActivity.getResources().getString(R.string.Search));

                switch (from) {
                    case setting:
                        //showPopupMenu(R.array.pop_up_menu_show_avatar_setting);
                        txtSearch.setText(G.fragmentActivity.getResources().getString(R.string.array_Delete_photo));
                        iconSearch.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));
                        break;
                    case group:
                        if (roleGroup == GroupChatRole.OWNER || roleGroup == GroupChatRole.ADMIN) {
                            //showPopupMenu(R.array.pop_up_menu_show_avatar_setting);
                            txtSearch.setText(G.fragmentActivity.getResources().getString(R.string.array_Delete_photo));
                            iconSearch.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));
                        } else {
                            //showPopupMenu(R.array.pop_up_menu_show_avatar);
                            txtSearch.setText(G.fragmentActivity.getResources().getString(R.string.save_to_gallery));
                            iconSearch.setText(G.fragmentActivity.getResources().getString(R.string.md_save));
                        }
                        break;
                    case channel:
                        if (roleChannel == ChannelChatRole.OWNER || roleChannel == ChannelChatRole.ADMIN) {
                            //showPopupMenu(R.array.pop_up_menu_show_avatar_setting);
                            txtSearch.setText(G.fragmentActivity.getResources().getString(R.string.array_Delete_photo));
                            iconSearch.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));
                        } else {
                            //showPopupMenu(R.array.pop_up_menu_show_avatar);
                            txtSearch.setText(G.fragmentActivity.getResources().getString(R.string.save_to_gallery));
                            iconSearch.setText(G.fragmentActivity.getResources().getString(R.string.md_save));
                        }
                        break;
                    case chat:
                        //showPopupMenu(R.array.pop_up_menu_show_avatar);
                        txtSearch.setText(G.fragmentActivity.getResources().getString(R.string.save_to_gallery));
                        iconSearch.setText(G.fragmentActivity.getResources().getString(R.string.md_save));
                        break;
                }
                root1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (txtSearch.getText().equals(G.fragmentActivity.getResources().getString(R.string.save_to_gallery))) {
                            saveToGallery();
                        } else if (txtSearch.getText().equals(G.fragmentActivity.getResources().getString(array_Delete_photo))) {
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
                });
            }
        });
        viewPager = (ViewPager) view.findViewById(R.id.asi_view_pager);

        txtImageNumber = (TextView) view.findViewById(R.id.asi_txt_image_number);
        txtImageName = (TextView) view.findViewById(R.id.asi_txt_image_name);
        ltImageName = (ViewGroup) view.findViewById(R.id.asi_layout_image_name);
        ltImageName.setVisibility(View.GONE);

        toolbarShowImage = (LinearLayout) view.findViewById(R.id.toolbarShowImage);

        initViewPager();
    }

    private void fillListAvatar(From from) {

        realm = Realm.getDefaultInstance();

        boolean isRoomExist = false;

        switch (from) {
            case chat:
            case setting:
                RealmRegisteredInfo user = RealmRegisteredInfo.getRegistrationInfo(realm, mPeerId);
                if (user != null) {
                    new RequestUserAvatarGetList().userAvatarGetList(mPeerId);
                    isRoomExist = true;
                }
                break;
            case group:
                RealmRoom roomGroup = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mPeerId).findFirst();
                if (roomGroup != null) {
                    new RequestGroupAvatarGetList().groupAvatarGetList(mPeerId);
                    isRoomExist = true;
                    roleGroup = roomGroup.getGroupRoom().getRole();
                }
                break;
            case channel:
                RealmRoom roomChannel = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mPeerId).findFirst();
                if (roomChannel != null) {
                    new RequestChannelAvatarGetList().channelAvatarGetList(mPeerId);
                    isRoomExist = true;
                    roleChannel = roomChannel.getChannelRoom().getRole();
                }
                break;
        }

        if (isRoomExist) {

            avatarList = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, mPeerId).findAll().sort(RealmAvatarFields.ID, Sort.DESCENDING);
            avatarList.addChangeListener(new RealmChangeListener<RealmResults<RealmAvatar>>() {
                @Override
                public void onChange(RealmResults<RealmAvatar> element) {

                    if (avatarListSize != element.size()) {

                        avatarListSize = element.size();

                        viewPager.setAdapter(new FragmentShowAvatars.AdapterViewPager());

                        if (avatarListSize > 0) {
                            viewPager.getAdapter().notifyDataSetChanged();
                            txtImageNumber.setText(viewPager.getCurrentItem() + 1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + avatarListSize);
                            if (HelperCalander.isPersianUnicode) {
                                txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
                            }
                        } else {
                            //  G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentShowAvatars.this).commit();

                            popBackStackFragment();
                        }
                    }
                }
            });

            avatarListSize = avatarList.size();
        }
    }

    private void initViewPager() {

        mAdapter = new FragmentShowAvatars.AdapterViewPager();
        viewPager.setAdapter(mAdapter);

        txtImageNumber.setText(1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + avatarList.size());
        if (HelperCalander.isPersianUnicode) {
            txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
        }

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                txtImageNumber.setText(position + 1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + avatarList.size());
                if (HelperCalander.isPersianUnicode) {
                    txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

    //***************************************************************************************

    private void showPopupMenu(int r) {
        MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).items(r).contentColor(Color.BLACK).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                if (text.equals(G.fragmentActivity.getResources().getString(R.string.save_to_gallery))) {
                    saveToGallery();
                } else if (text.equals(G.fragmentActivity.getResources().getString(array_Delete_photo))) {
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
        }).build();

        DialogAnimation.animationUp(dialog);

        dialog.show();
        //WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //layoutParams.copyFrom(dialog.getWindow().getAttributes());
        //layoutParams.width = (int) G.context.getResources().getDimension(R.dimen.dp200);
        //layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        //dialog.getWindow().setAttributes(layoutParams);
    }

    private void saveToGallery() {

        if (avatarList.get(viewPager.getCurrentItem()).getFile() != null) {
            String media = avatarList.get(viewPager.getCurrentItem()).getFile().getLocalFilePath();
            if (media != null) {
                File file = new File(media);
                if (file.exists()) {
                    HelperSaveFile.savePicToGallery(media, true);
                }
            }
        }
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
    //            startActivity(Intent.createChooser(intent, G.fragmentActivity.getResources().getString(R.string.share_image_from_igap)));
    //        }
    //    }
    //}

    private void deletePhotoChannel() {

        G.onChannelAvatarDelete = new OnChannelAvatarDelete() {
            @Override
            public void onChannelAvatarDelete(long roomId, long avatarId) {
                if (onComplete != null) onComplete.complete(true, "" + avatarId, "");
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };

        if (viewPager.getCurrentItem() >= avatarList.size()) {
            return;
        }

        new RequestChannelAvatarDelete().channelAvatarDelete(mPeerId, avatarList.get(viewPager.getCurrentItem()).getId());
    }

    private void deletePhotoGroup() {

        G.onGroupAvatarDelete = new OnGroupAvatarDelete() {
            @Override
            public void onDeleteAvatar(long roomId, final long avatarId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onComplete != null) {
                            onComplete.complete(true, "" + avatarId, "");
                        }
                    }
                });
            }

            @Override
            public void onDeleteAvatarError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };

        if (viewPager.getCurrentItem() >= avatarList.size()) {
            return;
        }

        new RequestGroupAvatarDelete().groupAvatarDelete(mPeerId, avatarList.get(viewPager.getCurrentItem()).getId());
    }

    //******************************************************************************************************

    private void deletePhotoSetting() {

        G.onUserAvatarDelete = new OnUserAvatarDelete() {
            @Override
            public void onUserAvatarDelete(long avatarId, String token) {
                if (onComplete != null) onComplete.complete(true, "" + avatarId, "");
            }

            @Override
            public void onUserAvatarDeleteError() {

            }
        };

        if (viewPager.getCurrentItem() >= avatarList.size()) {
            return;
        }

        new RequestUserAvatarDelete().userAvatarDelete(avatarList.get(viewPager.getCurrentItem()).getId());
    }

    private void deletePhotoChat() {

    }

    public enum From {
        chat(mChatNumber), group(mGroupNumber), channel(mChannelNumber), setting(mSettingNumber);

        public int value;

        From(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private class AdapterViewPager extends PagerAdapter {

        @Override
        public int getCount() {
            return avatarList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(View container, final int position) {

            LayoutInflater inflater = LayoutInflater.from(G.fragmentActivity);
            final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container, false);

            final TouchImageView touchImageView = (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);
            final ImageView imgPlay = (ImageView) layout.findViewById(R.id.imgPlay);
            imgPlay.setVisibility(View.GONE);

            final MessageProgress progress = (MessageProgress) layout.findViewById(R.id.progress);
            AppUtils.setProgresColor(progress.progressBar);

            final RealmAttachment ra = avatarList.get(position).getFile();

            if (HelperDownloadFile.isDownLoading(ra.getCacheId())) {
                progress.withDrawable(R.drawable.ic_cancel, true);
                startDownload(position, progress, touchImageView);
            } else {
                progress.withDrawable(R.drawable.ic_download, true);
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
                            HelperDownloadFile.startDownload(System.currentTimeMillis() + "", ra.getToken(), ra.getUrl(), ra.getCacheId(), ra.getName(), fileSize, selector, "", 4, new HelperDownloadFile.UpdateListener() {
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

                    String _cashId = avatarList.get(position).getFile().getCacheId();

                    if (HelperDownloadFile.isDownLoading(_cashId)) {
                        HelperDownloadFile.stopDownLoad(_cashId);
                    } else {
                        progress.withDrawable(R.drawable.ic_cancel, true);
                        startDownload(position, progress, touchImageView);
                    }
                }
            });

            touchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isShowToolbar) {
                        toolbarShowImage.animate().setDuration(150).alpha(0F).start();
                        //  ltImageName.setVisibility(View.GONE);
                        // ltImageName.animate().setDuration(150).alpha(0F).start();
                        toolbarShowImage.setVisibility(View.GONE);
                        isShowToolbar = false;
                    } else {
                        toolbarShowImage.animate().setDuration(150).alpha(1F).start();
                        toolbarShowImage.setVisibility(View.VISIBLE);
                        //  ltImageName.animate().setDuration(150).alpha(1F).start();
                        //  ltImageName.setVisibility(View.VISIBLE);
                        isShowToolbar = true;
                    }
                }
            });

            ((ViewGroup) container).addView(layout);
            return layout;
        }

        private void startDownload(int position, final MessageProgress progress, final TouchImageView touchImageView) {
            final RealmAttachment ra = avatarList.get(position).getFile();
            final String dirPath = AndroidUtils.getFilePathWithCashId(ra.getCacheId(), ra.getName(), G.DIR_IMAGE_USER, false);

            progress.withOnProgress(new OnProgress() {
                @Override
                public void onProgressFinished() {
                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.withProgress(0);
                            progress.setVisibility(View.GONE);
                        }
                    });
                }
            });


            HelperDownloadFile.startDownload(System.currentTimeMillis() + "", ra.getToken(), ra.getUrl(), ra.getCacheId(), ra.getName(), ra.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, dirPath, 4, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, final int progres) {
                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.withProgress(progres);
                            if (progres == 100) {
                                G.imageLoader.displayImage(AndroidUtils.suitablePath(path), touchImageView);
                            }
                        }
                    });

                }

                @Override
                public void OnError(String token) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.withProgress(0);
                            progress.withDrawable(R.drawable.ic_download, true);
                        }
                    });
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}