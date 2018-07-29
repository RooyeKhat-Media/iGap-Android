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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;

import java.io.File;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class ActivityTrimVideo extends ActivityEnhanced implements OnTrimVideoListener, OnK4LVideoListener {

    int videoWidth = 641;
    int videoHeight = 481;
    private String path;
    private int duration;
    private TextView txtDetail;
    private TextView txtTime;
    private TextView txtSize;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video_trime);

        txtDetail = (TextView) findViewById(R.id.stfaq_txt_detail);
        txtTime = (TextView) findViewById(R.id.stfaq_txt_time);
        txtSize = (TextView) findViewById(R.id.stfaq_txt_size);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            path = bundle.getString("PATH");

            if (path == null) {
                finish();
                return;
            }
        }

        setInfo(path);

        K4LVideoTrimmer videoTrimmer = (K4LVideoTrimmer) findViewById(R.id.timeLine);
        progressBar = (ProgressBar) findViewById(R.id.fvt_progress);
        if (videoTrimmer != null) {
            videoTrimmer.setVideoURI(Uri.parse(path));
            videoTrimmer.setMaxDuration(duration);
            videoTrimmer.setOnTrimVideoListener(this);
            videoTrimmer.setDestinationPath(G.DIR_VIDEOS + "/");
            videoTrimmer.setVideoInformationVisibility(true);
        }
    }

    private void setInfo(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);

            File file = new File(path);
            if (file.exists()) {
                txtSize.setText("," + net.iGap.module.FileUtils.formatFileSize((long) file.length()));
            }

            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (time != null && time.length() > 0) {
                duration = ((Integer.parseInt(time)) / 1000) + 1;
                int minutes = duration / 60;
                int seconds = duration % 60;
                txtTime.setText("," + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            Bitmap bmp = retriever.getFrameAtTime();

            if (bmp != null) {
                videoHeight = bmp.getHeight();
                videoWidth = bmp.getWidth();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        txtDetail.setText(videoWidth + "X" + videoHeight);
    }

    @Override
    public void onTrimStarted() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void getResult(final Uri uri) {

        Intent data = new Intent();
        data.setData(uri);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void cancelAction() {

        Uri uriCancel = Uri.parse(path);
        Intent data = new Intent();
        data.setData(uriCancel);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void onError(String message) {

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onVideoPrepared() {

    }


}
