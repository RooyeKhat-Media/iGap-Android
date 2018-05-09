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
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.module.FileUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StartupActions;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;

import java.io.File;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.module.FileUtils.getFolderSize;

public class ActivityManageSpaceViewModel {

    public ObservableField<String> callbackKeepMedia = new ObservableField<>("1Week");
    public ObservableField<String> callbackClearCache = new ObservableField<>("0 KB");
    public ObservableField<String> callbackCleanUp = new ObservableField<>("0 KB");
    public ObservableField<Integer> showLayoutSdk = new ObservableField<>(View.GONE);
    public ObservableField<Boolean> isSdkEnable = new ObservableField<>();
    private Context context;
    private SharedPreferences sharedPreferences;
    private int isForever;
    private File fileMap;

    public ActivityManageSpaceViewModel(Context context) {

        this.context = context;
        getInfo();
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================

    public void onClickKeepMedia(View view) {


        isForever = sharedPreferences.getInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0);
        final int position;
        if (isForever == 30) {
            callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_1month));
            position = 1;
        } else if (isForever == 180) {
            position = 2;
        } else {
            callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_forever));
            position = 0;
        }

        new MaterialDialog.Builder(context).title(G.context.getResources().getString(R.string.st_keepMedia)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.keepMedia).itemsCallbackSingleChoice(position, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0);
                        editor.apply();
                        callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_forever));
                        break;
                    }
                    case 1: {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 30);
                        editor.apply();
                        callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_1month));
                        break;
                    }
                    case 2: {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 180);
                        editor.apply();
                        callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_6month));
                        break;
                    }
                }
                return false;
            }
        }).positiveText(G.context.getResources().getString(R.string.B_ok)).negativeText(G.context.getResources().getString(R.string.B_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            }
        }).show();


    }

    public void onClickClearCache(View v) {

        final long sizeFolderPhotoDialog = getFolderSize(new File(G.DIR_IMAGES));
        final long sizeFolderVideoDialog = getFolderSize(new File(G.DIR_VIDEOS));
        final long sizeFolderDocumentDialog = getFolderSize(new File(G.DIR_DOCUMENT));
        final long sizeFolderAudio = getFolderSize(new File(G.DIR_AUDIOS));
        final long sizeFolderMap = FileUtils.getFolderSize(fileMap);
        final long sizeFolderOtherFiles = getFolderSize(new File(G.DIR_TEMP));
        final long sizeFolderOtherFilesBackground = getFolderSize(new File(G.DIR_CHAT_BACKGROUND));
        final long sizeFolderOtherFilesImageUser = getFolderSize(new File(G.DIR_IMAGE_USER));

        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(context).title(G.context.getResources().getString(R.string.st_title_Clear_Cache)).customView(R.layout.st_dialog_clear_cach, wrapInScrollView).positiveText(G.context.getResources().getString(R.string.st_title_Clear_Cache)).show();

        View view = dialog.getCustomView();

        final File filePhoto = new File(G.DIR_IMAGES);
        assert view != null;
        TextView photo = (TextView) view.findViewById(R.id.st_txt_sizeFolder_photo);
        photo.setText(FileUtils.formatFileSize(sizeFolderPhotoDialog));

        final CheckBox checkBoxPhoto = (CheckBox) view.findViewById(R.id.st_checkBox_photo);
        final File fileVideo = new File(G.DIR_VIDEOS);
        TextView video = (TextView) view.findViewById(R.id.st_txt_sizeFolder_video);
        video.setText(FileUtils.formatFileSize(sizeFolderVideoDialog));

        final CheckBox checkBoxVideo = (CheckBox) view.findViewById(R.id.st_checkBox_video_dialogClearCash);

        final File fileDocument = new File(G.DIR_DOCUMENT);
        TextView document = (TextView) view.findViewById(R.id.st_txt_sizeFolder_document_dialogClearCash);
        document.setText(FileUtils.formatFileSize(sizeFolderDocumentDialog));

        final CheckBox checkBoxDocument = (CheckBox) view.findViewById(R.id.st_checkBox_document_dialogClearCash);

        final File fileAudio = new File(G.DIR_AUDIOS);
        TextView txtAudio = (TextView) view.findViewById(R.id.st_txt_audio_dialogClearCash);
        txtAudio.setText(FileUtils.formatFileSize(sizeFolderAudio));
        final CheckBox checkBoxAudio = (CheckBox) view.findViewById(R.id.st_checkBox_audio_dialogClearCash);

        //final File fileMap = new File(G.DIR_AUDIOS);
        TextView txtMap = (TextView) view.findViewById(R.id.st_txt_map_dialogClearCash);
        txtMap.setText(FileUtils.formatFileSize(sizeFolderMap));
        final CheckBox checkBoxMap = (CheckBox) view.findViewById(R.id.st_checkBox_map_dialogClearCash);

        final File fileOtherFiles = new File(G.DIR_TEMP);
        TextView txtOtherFiles = (TextView) view.findViewById(R.id.st_txt_otherFiles);
        txtOtherFiles.setText(FileUtils.formatFileSize(sizeFolderOtherFiles + sizeFolderOtherFilesImageUser + sizeFolderOtherFilesBackground));
        final CheckBox checkBoxOtherFiles = (CheckBox) view.findViewById(R.id.st_checkBox_otherFiles);

        long rTotalSize = sizeFolderPhotoDialog + sizeFolderVideoDialog + sizeFolderDocumentDialog + sizeFolderAudio + sizeFolderMap + sizeFolderOtherFiles;
        final TextView txtTotalSize = (TextView) view.findViewById(R.id.st_txt_totalSize_dialogClearCash);
        txtTotalSize.setText(FileUtils.formatFileSize(rTotalSize));


        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkBoxPhoto.isChecked()) {
                    for (File file : filePhoto.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                }
                if (checkBoxVideo.isChecked()) {
                    for (File file : fileVideo.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                }
                if (checkBoxDocument.isChecked()) {
                    for (File file : fileDocument.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                }
                if (checkBoxAudio.isChecked()) {
                    for (File file : fileAudio.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                }
                if (checkBoxMap.isChecked()) {
                    FragmentiGapMap.deleteMapFileCash();
                }

                if (checkBoxOtherFiles.isChecked()) {
                    for (File file : fileOtherFiles.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                    final File fileOtherFilesBackground = new File(G.DIR_CHAT_BACKGROUND);

                    if (fileOtherFilesBackground.listFiles() != null)
                        for (File fileBackground : fileOtherFilesBackground.listFiles()) {
                            if (!fileBackground.isDirectory()) fileBackground.delete();
                        }

                    final File fileOtherFilesImageUser = new File(G.DIR_IMAGE_USER);

                    if (fileOtherFilesImageUser.listFiles() != null)
                        for (File fileImageUser : fileOtherFilesImageUser.listFiles()) {
                            if (!fileImageUser.isDirectory()) fileImageUser.delete();
                        }
                }

                long afterClearSizeFolderPhoto = FileUtils.getFolderSize(new File(G.DIR_IMAGES));
                long afterClearSizeFolderVideo = FileUtils.getFolderSize(new File(G.DIR_VIDEOS));
                long afterClearSizeFolderDocument = FileUtils.getFolderSize(new File(G.DIR_DOCUMENT));
                long afterClearSizeFolderAudio = FileUtils.getFolderSize(new File(G.DIR_AUDIOS));
                long afterClearSizeFolderMap = FileUtils.getFolderSize(fileMap);
                long afterClearSizeFolderOtherFiles = FileUtils.getFolderSize(new File(G.DIR_TEMP));
                long afterClearSizeFolderOtherFilesBackground = FileUtils.getFolderSize(new File(G.DIR_CHAT_BACKGROUND));
                long afterClearSizeFolderOtherFilesImageUser = FileUtils.getFolderSize(new File(G.DIR_IMAGE_USER));
                long afterClearTotal = afterClearSizeFolderPhoto + afterClearSizeFolderVideo + afterClearSizeFolderDocument + afterClearSizeFolderAudio + afterClearSizeFolderMap + afterClearSizeFolderOtherFiles + afterClearSizeFolderOtherFilesImageUser + afterClearSizeFolderOtherFilesBackground;
                callbackClearCache.set(FileUtils.formatFileSize(afterClearTotal));
                txtTotalSize.setText(FileUtils.formatFileSize(afterClearTotal));
                dialog.dismiss();
            }
        });


    }

    public void onClickCleanUp(View v) {
        final MaterialDialog inDialog = new MaterialDialog.Builder(context).customView(R.layout.dialog_content_custom, true).build();
        View view = inDialog.getCustomView();

        inDialog.show();

        TextView txtTitle = (TextView) view.findViewById(R.id.txtDialogTitle);
        txtTitle.setText(G.context.getResources().getString(R.string.clean_up_chat_rooms));

        TextView iconTitle = (TextView) view.findViewById(R.id.iconDialogTitle);
        iconTitle.setText(R.string.md_clean_up);

        TextView txtContent = (TextView) view.findViewById(R.id.txtDialogContent);
        txtContent.setText(R.string.do_you_want_to_clean_all_data_in_chat_rooms);

        TextView txtCancel = (TextView) view.findViewById(R.id.txtDialogCancel);
        TextView txtOk = (TextView) view.findViewById(R.id.txtDialogOk);

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                RealmRoomMessage.ClearAllMessage(realm, true, 0);
                RealmRoom.clearAllScrollPositions();
                final long DbTotalSize = new File(realm.getConfiguration().getPath()).length();
                realm.close();
                callbackCleanUp.set(FileUtils.formatFileSize(DbTotalSize));

                MusicPlayer.closeLayoutMediaPlayer();

                inDialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDialog.dismiss();
            }
        });

    }

    public void onClickSdkEnable(View view) {

        new MaterialDialog.Builder(context)
                .title(G.context.getResources().getString(R.string.are_you_sure))
                .negativeText(G.context.getResources().getString(R.string.B_cancel))
                .content(G.context.getResources().getString(R.string.change_storage_place))
                .positiveText(G.context.getResources().getString(R.string.B_ok))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        isSdkEnable.set(!isSdkEnable.get());

                    }
                }).show();

    }

    public void onCheckedSdkEnable(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isSdkEnable.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_SDK_ENABLE, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_SDK_ENABLE, 0);
            editor.apply();
        }

        StartupActions.makeFolder();
    }

    private boolean getBoolean(int num) {
        if (num == 0) {
            return false;
        }
        return true;
    }

    //===============================================================================
    //====================================Methods====================================
    //===============================================================================

    private void getInfo() {

        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        isForever = sharedPreferences.getInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0);
        if (isForever == 30) {
            callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_1month));
        } else if (isForever == 180) {
            callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_6month));
        } else {
            callbackKeepMedia.set(G.context.getResources().getString(R.string.keep_media_forever));
        }


        final long sizeFolderPhoto = getFolderSize(new File(G.DIR_IMAGES));
        final long sizeFolderVideo = getFolderSize(new File(G.DIR_VIDEOS));
        final long sizeFolderDocument = getFolderSize(new File(G.DIR_DOCUMENT));
        final long sizeFolderAudio = getFolderSize(new File(G.DIR_AUDIOS));
        final long sizeFolderOtherFiles = getFolderSize(new File(G.DIR_TEMP));
        final long sizeFolderOtherFilesBackground = getFolderSize(new File(G.DIR_CHAT_BACKGROUND));
        final long sizeFolderOtherFilesImageUser = getFolderSize(new File(G.DIR_IMAGE_USER));

        final IConfigurationProvider configurationProvider = Configuration.getInstance();
        fileMap = configurationProvider.getOsmdroidBasePath();
        final long sizeFolderMap = FileUtils.getFolderSize(fileMap);
        final long total = sizeFolderPhoto + sizeFolderVideo + sizeFolderDocument + sizeFolderAudio + sizeFolderMap + sizeFolderOtherFiles + sizeFolderOtherFilesBackground + sizeFolderOtherFilesImageUser;

        callbackClearCache.set(FileUtils.formatFileSize(total));

        Realm realm = Realm.getDefaultInstance();
        final long DbTotalSize = new File(realm.getConfiguration().getPath()).length();
        realm.close();

        callbackCleanUp.set(FileUtils.formatFileSize(DbTotalSize));

        isSdkEnable.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_SDK_ENABLE, 0)));

        if (FileUtils.getSdCardPathList().size() > 0) {
            showLayoutSdk.set(View.VISIBLE);
        } else {
            showLayoutSdk.set(View.GONE);
        }

    }


}
