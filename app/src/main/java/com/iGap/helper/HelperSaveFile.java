/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.helper;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import com.iGap.G;
import com.iGap.R;
import com.iGap.module.AndroidUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;

public class HelperSaveFile {

    public static Boolean savePicToDownLoadFolder(Bitmap bitmap) {

        try {
            if (bitmap == null) return false;

            File path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();
            Timestamp tsTemp = new Timestamp(now.getTime());
            String ts = tsTemp.toString();

            File file = new File(path, ts + ".jpg");

            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public enum FolderType {
        download, music, gif, video, image
    }

    public static Boolean saveFileToDownLoadFolder(String filePath, String fileName, FolderType folderType, int sucsesMessageSRC) {

        try {
            if (filePath == null || fileName == null) {
                return false;
            }

            File src = new File(filePath);

            if (!src.exists()) {
                return false;
            }

            String destinationPath = " ";

            switch (folderType) {
                case download:

                    if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).exists()) {
                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;
                    } else {
                        destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/" + fileName;
                    }

                    break;
                case music:
                    if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).exists()) {
                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + fileName;
                    }

                    break;

                case gif:

                    if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).exists()) {
                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + fileName;
                    }

                    break;
                case video:

                    if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).exists()) {
                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/" + fileName;
                    }

                    break;
            }

            AndroidUtils.copyFile(src, new File(destinationPath));

            Toast.makeText(G.currentActivity, sucsesMessageSRC, Toast.LENGTH_SHORT).show();

            return true;
        } catch (Exception e) {

            Toast.makeText(G.currentActivity, R.string.file_can_not_save_to_selected_folder, Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    public static void savePicToGallary(Bitmap bitmap, String name) {

        MediaStore.Images.Media.insertImage(G.context.getContentResolver(), bitmap, name, "yourDescription");
    }

    public static void savePicToGallary(String filePath, boolean showToast) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        G.context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (showToast) {
            Toast.makeText(G.context, R.string.picture_save_to_galary, Toast.LENGTH_SHORT).show();
        }
    }

    public static void saveVideoToGallary(String videoFilePath, boolean showToast) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "My video title");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, videoFilePath);
        G.context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        if (showToast) {
            Toast.makeText(G.context, R.string.file_save_to_galary_folder, Toast.LENGTH_SHORT).show();
        }
    }

    public static void saveToMusicFolder(String path, String name) {
        try {
            if (path == null) return;

            InputStream is = new FileInputStream(path);
            File mSavePath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            File file = new File(mSavePath + "/" + name);
            OutputStream os = new FileOutputStream(file);

            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            is.close();
            os.close();

            Toast.makeText(G.context, R.string.save_to_music_folder, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
}
