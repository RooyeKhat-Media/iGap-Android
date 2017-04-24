/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterChatBackground;
import com.iGap.helper.ImageHelper;
import com.iGap.interfaces.OnGetWallpaper;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AttachFile;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoInfoWallpaper;
import com.iGap.realm.RealmWallpaper;
import com.iGap.request.RequestInfoWallpaper;
import io.realm.Realm;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityChatBackground extends ActivityEnhanced {

    public static String savePath;
    public MaterialDesignTextView txtSet;
    private MaterialDesignTextView txtBack;
    private File addFile;
    private int spanCount = 3;
    private RippleView rippleBack;
    private RecyclerView rcvContent;
    private AdapterChatBackground adapterChatBackgroundSetting;

    private int spanItemCount = 3;

    public ArrayList<StructWallpaper> wList;

    public enum WallpaperType {
        addNew,
        lockal,
        proto
    }

    public class StructWallpaper {

        private WallpaperType wallpaperType;
        private String path;
        private ProtoGlobal.Wallpaper protoWallpaper;

        public WallpaperType getWallpaperType() {
            return wallpaperType;
        }

        public void setWallpaperType(WallpaperType wallpaperType) {
            this.wallpaperType = wallpaperType;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public ProtoGlobal.Wallpaper getProtoWallpaper() {
            return protoWallpaper;
        }

        public void setProtoWallpaper(ProtoGlobal.Wallpaper protoWallpaper) {
            this.protoWallpaper = protoWallpaper;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

        G.currentActivity = this;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_background);

        findViewById(R.id.stcb_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        findViewById(R.id.acb_line_view).setBackgroundColor(Color.parseColor(G.appBarColor));

        txtBack = (MaterialDesignTextView) findViewById(R.id.stcb_txt_back);
        rippleBack = (RippleView) findViewById(R.id.stcb_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });


        final GridLayoutManager gLayoutManager = new GridLayoutManager(ActivityChatBackground.this, spanItemCount);

        fillList(true);

        rcvContent = (RecyclerView) findViewById(R.id.rcvContent);
        adapterChatBackgroundSetting = new AdapterChatBackground(wList);
        rcvContent.setAdapter(adapterChatBackgroundSetting);
        rcvContent.setLayoutManager(gLayoutManager);
        rcvContent.clearAnimation();


        rcvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rcvContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int viewWidth = rcvContent.getMeasuredWidth();
                float cardViewWidth = getResources().getDimension(R.dimen.dp120);
                int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);

                if (newSpanCount < 3) newSpanCount = 3;

                spanItemCount = newSpanCount;
                gLayoutManager.setSpanCount(newSpanCount);
                gLayoutManager.requestLayout();
            }
        });
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        String filePath = null;

        switch (requestCode) {
            case AttachFile.request_code_TAKE_PICTURE:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                    filePath = AttachFile.mCurrentPhotoPath;
                } else {
                    ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                    filePath = AttachFile.imagePath;
                }
                break;
            case AttachFile.request_code_image_from_gallery_single_select:

                if (data != null && data.getData() != null) {

                    if (ActivityChatBackground.this != null) {
                        AttachFile attachFile = new AttachFile(ActivityChatBackground.this);
                        filePath = attachFile.saveGalleryPicToLocal(AttachFile.getFilePathFromUri(data.getData()));
                    }
                }

                break;
        }

        if (filePath != null) {

            if (new File(filePath).exists()) {
                RealmWallpaper.updateField(null, filePath);

                fillList(false);

                adapterChatBackgroundSetting.notifyItemInserted(1);

            }

        }
    }

    private void getImageListFromServer() {

        G.onGetWallpaper = new OnGetWallpaper() {
            @Override public void onGetWallpaperList(final List<ProtoGlobal.Wallpaper> list) {

                RealmWallpaper.updateField(list, "");
                fillList(false);

                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        adapterChatBackgroundSetting.notifyDataSetChanged();
                    }
                });


            }
        };

        ProtoInfoWallpaper.InfoWallpaper.Fit fit = ProtoInfoWallpaper.InfoWallpaper.Fit.PHONE;
        if (getResources().getBoolean(R.bool.isTablet)) {
            fit = ProtoInfoWallpaper.InfoWallpaper.Fit.TABLET;
        }

        new RequestInfoWallpaper().infoWallpaper(fit);
    }

    private void fillList(boolean getInfoFromServer) {

        if (wList == null) wList = new ArrayList<>();

        wList.clear();

        //add item 0 add new background from local
        StructWallpaper sw = new StructWallpaper();
        sw.setWallpaperType(WallpaperType.addNew);
        wList.add(sw);

        Realm realm = Realm.getDefaultInstance();

        RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).findFirst();

        if (realmWallpaper != null) {

            if (realmWallpaper.getLocalList() != null) {
                for (String localPath : realmWallpaper.getLocalList()) {
                    if (new File(localPath).exists()) {
                        StructWallpaper _swl = new StructWallpaper();
                        _swl.setWallpaperType(WallpaperType.lockal);
                        _swl.setPath(localPath);
                        wList.add(_swl);
                    }
                }
            }

            if (realmWallpaper.getWallPaperList() != null) {
                for (ProtoGlobal.Wallpaper wallpaper : realmWallpaper.getWallPaperList()) {
                    StructWallpaper _swp = new StructWallpaper();
                    _swp.setWallpaperType(WallpaperType.proto);
                    _swp.setProtoWallpaper(wallpaper);
                    wList.add(_swp);
                }
            }

            if (getInfoFromServer) {

                long time = realmWallpaper.getLastTimeGetList();
                if (time > 0) {

                    if (time + (2 * 60 * 60 * 1000) < TimeUtils.currentLocalTime()) {
                        getImageListFromServer();
                    }
                } else {
                    getImageListFromServer();
                }
            }
        } else {
            if (getInfoFromServer) {
                getImageListFromServer();
            }
        }

        realm.close();

    }
}
