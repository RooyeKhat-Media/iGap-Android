/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import net.iGap.R;

import java.io.File;

import static net.iGap.G.context;

public class HelperMimeType {

    public static boolean isFileImage(String path) {
        return path.endsWith(".jpg") || path.endsWith(".bmp") || path.endsWith(".png") || path.endsWith(".gif") || path.endsWith(".jpeg") || path.endsWith(".tiff") || path.endsWith(".tif");
    }

    public static boolean isFileVideo(String path) {
        return path.endsWith(".mp4")
                || path.endsWith(".3gp")
                || path.endsWith(".avi")
                || path.endsWith(".mpg")
                || path.endsWith(".mpeg")
                || path.endsWith(".flv")
                || path.endsWith(".wmv")
                || path.endsWith(".m4v") || path.endsWith(".mov");
    }

    private static boolean isFileAudio(String path) {
        return path.endsWith(".mp3")
                || path.endsWith(".ogg")
                || path.endsWith(".wma")
                || path.endsWith(".m4a")
                || path.endsWith(".amr")
                || path.endsWith(".wav")
                || path.endsWith(".mid") || path.endsWith(".midi");
    }

    private static boolean isFileText(String path) {
        return path.endsWith(".txt") || path.endsWith(".csv") || path.endsWith(".xml") || path.endsWith(".html");
    }

    private static boolean isFilePakage(String path) {
        return path.endsWith(".gz") || path.endsWith(".gz") || path.endsWith(".zip");
    }

    /**
     * open a file by appropriate Program
     *
     * @param filePath for realize type of file like image.png or dd.pdf
     * @return intent for open file
     */
    public static Intent appropriateProgram(String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            return null;
        }

        String path = filePath.toLowerCase();

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        if (isFileText(path)) {
            intent.setDataAndType(uri, "text/*");
        } else if (isFileAudio(path)) {
            intent.setDataAndType(uri, "audio/*");
        } else if (isFileVideo(path)) {
            intent.setDataAndType(uri, "video/*");
        } else if (path.endsWith(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if (isFileImage(path)) {
            intent.setDataAndType(uri, "image/*");
        } else if (path.endsWith(".apk")) {

            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else if (isFilePakage(path)) {
            intent.setDataAndType(uri, "package/*");
        } else if (path.endsWith(".ppt") || path.endsWith(".pptx")) {

            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (path.endsWith(".xls") || path.endsWith(".xlsx")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (path.endsWith(".rtf")) {
            intent.setDataAndType(uri, "application/rtf");
        }

        return intent;
    }

    /**
     * get a picture for this extension
     */
    public static Integer getMimeResource(String extention) {

        Integer x = null;

        if (extention == null) return null;

        extention = extention.toLowerCase();

        if (isFileImage(extention)) {
            x = R.mipmap.j_pic;
        } else if (extention.endsWith("apk")) {
            x = R.mipmap.j_apk;
        } else if (extention.endsWith("mp3") || extention.endsWith("ogg") || extention.endsWith("wma")) {
            x = R.mipmap.j_mp3;
        } else if (isFileVideo(extention)) {
            x = R.mipmap.j_video;
        } else if (extention.endsWith("m4a") || extention.endsWith("amr") || extention.endsWith("wav")) {
            x = R.mipmap.j_audio;
        } else if (extention.endsWith("html") || extention.endsWith("htm")) {
            x = R.mipmap.j_html;
        } else if (extention.endsWith("pdf")) {
            x = R.mipmap.j_pdf;
        } else if (extention.endsWith("ppt")) {
            x = R.mipmap.j_ppt;
        } else if (extention.endsWith("snb")) {
            x = R.mipmap.j_snb;
        } else if (extention.endsWith("txt")) {
            x = R.mipmap.j_txt;
        } else if (extention.endsWith("doc")) {
            x = R.mipmap.j_word;
        } else if (extention.endsWith("xls")) {
            x = R.mipmap.j_xls;
        } else {
            x = R.mipmap.j_ect;
        }

        return x;
    }

    public static Bitmap getMimePic(Context context, Integer src) {

        Bitmap bitmap = null;

        if (src == null) return null;

        bitmap = BitmapFactory.decodeResource(context.getResources(), src);

        return bitmap;
    }

    public void LoadImageTumpnail(ImageView imageView, String path) {

        new LoadImageToImageView(imageView, path).execute();
    }

    public void loadVideoThumbnail(ImageView imageView, String path) {
        new getVideoThumbnail(imageView, path).execute();
    }

    /**
     * return Thumbnail bitmap from file path image
     */
    class LoadImageToImageView extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String path;

        public LoadImageToImageView(ImageView imageView, String path) {
            imv = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {

            Bitmap bitmap = null;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 8;
            File file = new File(path);

            if (file.exists()) bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && imv != null) {
                imv.setImageBitmap(result);
            }
        }
    }

    /**
     * return Thumbnail bitmap from file path video
     */
    class getVideoThumbnail extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String path;

        public getVideoThumbnail(ImageView imageView, String path) {
            imv = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {

            return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && imv != null) {
                imv.setImageBitmap(result);
            }
        }
    }
}
