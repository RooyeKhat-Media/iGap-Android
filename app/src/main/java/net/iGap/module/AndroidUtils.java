/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

public final class AndroidUtils {
    private AndroidUtils() throws InstantiationException {

        throw new InstantiationException("This class is not for instantiation.");
    }

    public static int getWindowWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static String formatDuration(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static String getAudioArtistName(String filePath) throws IllegalArgumentException {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

        Uri uri;
        File file = new File(filePath);

        if (file.exists()) {
            uri = Uri.fromFile(file);

            try {
                metaRetriever.setDataSource(G.context, uri);
                return metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            } catch (Exception e) {

            }
        }

        return "";
    }

    public static long getAudioDuration(Context context, String filePath) throws IllegalArgumentException {

        if (filePath == null || filePath.length() == 0) {
            return 1;
        }

        Uri uri;
        File file = new File(filePath);

        if (file.exists()) {
            uri = Uri.fromFile(file);

            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(context, uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                return Integer.parseInt(durationStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 1;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Audio.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static int[] getImageDimens(String filePath) {

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(filePath, options);

            int width = options.outWidth;
            int height = options.outHeight;

            return new int[]{width, height};
        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }

    /**
     * return suitable path for using with UIL
     *
     * @param path String path
     * @return correct local path/passed path
     */
    public static String suitablePath(String path) {
        if (path.matches("\\w+?://")) {
            return path;
        } else {
            String encoded = Uri.fromFile(new File(path)).toString();
            return Uri.decode(encoded);
        }
    }

    public static String saveBitmap(Bitmap bmp) {
        FileOutputStream out = null;
        String outPath = G.DIR_TEMP + "/thumb_" + Long.toString(SUID.id().get()) + ".jpg";
        try {
            out = new FileOutputStream(outPath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outPath;
    }

    /**
     * get n bytes from file, starts from beginning
     *
     * @param uploadStructure FileUploadStructure
     * @param bytesCount total bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getBytesFromStart(FileUploadStructure uploadStructure, int bytesCount) throws IOException {
        // FileChannel has better performance than BufferedInputStream
        uploadStructure.fileChannel.position(0);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        uploadStructure.fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void writeBytesToFile(String filePath, byte[] chunk) {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(filePath);
            fop = new FileOutputStream(file, true);
            fop.write(chunk);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get n bytes from file, starts from end
     *
     * @param uploadStructure FileUploadStructure
     * @param bytesCount total bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getBytesFromEnd(FileUploadStructure uploadStructure, int bytesCount) throws IOException {
        // FileChannel has better performance than RandomAccessFile
        uploadStructure.fileChannel.position(uploadStructure.fileChannel.size() - bytesCount);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        uploadStructure.fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    /**
     * get n bytes from specified offset
     *
     * @param uploadStructure FileUploadStructure
     * @param offset start reading from
     * @param bytesCount total reading bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getNBytesFromOffset(FileUploadStructure uploadStructure, int offset, int bytesCount) throws IOException {
        // FileChannel has better performance than RandomAccessFile
        uploadStructure.fileChannel.position(offset);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        uploadStructure.fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    /**
     * get SHA-256 from file
     * note: our server needs 32 bytes, so always pass true as second parameter.
     *
     * @param uploadStructure FileUploadStructure
     */
    public static byte[] getFileHash(FileUploadStructure uploadStructure) throws NoSuchAlgorithmException, IOException {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = fileToBytes(uploadStructure);
            return sha256.digest(fileBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getFileHashFromPath(String path) {
        File file = new File(path);
        if (!file.exists()) return null;

        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] hash;
        int read;
        byte[] buffer = new byte[8192];

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            hash = digest.digest();
        } catch (Exception e) {
            return null;
        }

        return hash;
    }

    public static void cutFromTemp(String pathTmp, String newPath) throws IOException {
        File cutTo = new File(newPath);
        File cutFrom = new File(pathTmp);

        copyFile(cutFrom, cutTo);
        deleteFile(cutFrom);
    }

    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);

        copyFile(in, dst);
    }

    public static void copyFile(InputStream in, File dst) throws IOException {

        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private static boolean deleteFile(File src) {
        return src.delete();
    }

    public static String suitableAppFilePath(ProtoGlobal.RoomMessageType messageType) {
        switch (messageType) {
            case AUDIO:
            case AUDIO_TEXT:
            case VOICE:
                return G.DIR_AUDIOS;
            case FILE:
            case FILE_TEXT:
                return G.DIR_DOCUMENT;
            case IMAGE:
            case IMAGE_TEXT:
            case GIF:
            case GIF_TEXT:
                return G.DIR_IMAGES;
            case VIDEO:
            case VIDEO_TEXT:
                return G.DIR_VIDEOS;
            default:
                return G.DIR_APP;
        }
    }

    /**
     * convert bytes to human readable length
     *
     * @param bytes bytes
     * @param si Boolean
     * @return String
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * return file to bytes
     *
     * @param uploadStructure FileUploadStructure
     * @return bytes
     * @throws IOException
     */
    public static byte[] fileToBytes(FileUploadStructure uploadStructure) throws IOException, OutOfMemoryError, RuntimeException {
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) uploadStructure.fileSize);
        uploadStructure.fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    public static int[] scaleDimenWithSavedRatio(Context context, float width, float height, ProtoGlobal.Room.Type roomType) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        float density = display.density * 0.9f;
        float maxWidth;
        if (roomType == ProtoGlobal.Room.Type.CHANNEL || roomType == ProtoGlobal.Room.Type.CHAT) {
            maxWidth = context.getResources().getDimension(R.dimen.dp240);
        } else {
            maxWidth = context.getResources().getDimension(R.dimen.dp200);
        }
        float newWidth;
        float newHeight;

        if (width < maxWidth) {
            newWidth = width * density;

           /* if (newWidth > maxWidth) {
                if (maxWidth < width) {
                    newWidth = maxWidth;
                } else {
                    newWidth = width;
                }
            }*/

            while (newWidth > maxWidth) {
                newWidth = (newWidth * 90) / 100;
            }
        } else {
            newWidth = maxWidth;
        }

        newHeight = Math.round((height / width) * newWidth);

        return new int[]{Math.round(newWidth), Math.round(newHeight)};
    }

    /**
     * @param s mixed language text
     * @return true if text is RTL
     */
    public static boolean isTextRtl(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }

        int c = s.codePointAt(0);
        return c >= 0x0600 && c <= 0x06FF;
    }

    public static void setBackgroundShapeColor(View view, int color) {

        Drawable background = view.getBackground();
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(color);
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(color);
        }
    }

    //*****************************************************************************************************************
    private static String makeSHA1Hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
        }
        return hexStr;
    }

    public static String getFilePathWithCashId(String cashId, String name, ProtoGlobal.RoomMessageType messageType) {

        String _Dir = suitableAppFilePath(messageType);
        String _hash = cashId;
        String _mimeType = "";

        int index = name.lastIndexOf(".");
        if (index >= 0) {
            _mimeType = name.substring(index);
        }

        String _result = "";

        try {
            if (cashId != null && cashId.length() > 0) {
                _hash = makeSHA1Hash(cashId);
            }
        } catch (Exception e) {

        }

        _result = _Dir + "/" + _hash + _mimeType;

        return _result;
    }

    public static String getFilePathWithCashId(String cashId, String name, String selectDir, boolean isThumbNail) {

        String _hash = cashId;
        String _mimeType = "";

        if (isThumbNail) {

            _mimeType = ".jpg";
        } else {
            int index = name.lastIndexOf(".");
            if (index >= 0) {
                _mimeType = name.substring(index);
            }
        }

        String _result = "";

        try {
            if (cashId != null && cashId.length() > 0) {
                _hash = makeSHA1Hash(cashId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (selectDir.equals(G.DIR_TEMP)) {

            if (isThumbNail) {
                _result = G.DIR_TEMP + "/" + "thumb_" + _hash + _mimeType;
            } else {
                _result = G.DIR_TEMP + "/" + _hash + _mimeType;
            }
        } else if (selectDir.equals(G.DIR_IMAGE_USER)) {

            if (isThumbNail) {
                _result = G.DIR_IMAGE_USER + "/" + "thumb_" + _hash + _mimeType;
            } else {
                _result = G.DIR_IMAGE_USER + "/" + _hash + _mimeType;
            }
        }
        // AppUtils.suitableThumbFileName(name);

        return _result;
    }

    //*****************************************************************************************************************

    public static void closeKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (IllegalStateException e) {
            e.getStackTrace();
        }
    }
}