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

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import net.iGap.G;
import net.iGap.activities.ActivityMain;

/**
 * this class use when get share file in another app
 */

public class HelperGetDataFromOtherApp {

    public static boolean hasSharedData = false;
    // after use intent set this to false
    public static FileType messageType = null;
    public static String message = "";
    public static ArrayList<Uri> messageFileAddress;
    public static ArrayList<FileType> fileTypeArray = new ArrayList<FileType>();
    private Intent intent;

    public HelperGetDataFromOtherApp(Intent intent) {

        this.intent = intent;

        if (intent == null) {
            return;
        }

        checkData(intent);
    }

    public static FileType getMimeType(Uri uri) {
        String extension;
        FileType fileType = FileType.file;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(G.context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        if (extension == null) {
            return null;
        }
        extension = extension.toLowerCase();

        if (extension.endsWith("jpg") || extension.endsWith("jpeg") || extension.endsWith("png") || extension.endsWith("bmp") || extension.endsWith(".tiff")) {
            fileType = FileType.image;
        } else if (extension.endsWith("mp3") || extension.endsWith("ogg") || extension.endsWith("wma") || extension.endsWith("m4a") || extension.endsWith("amr") || extension.endsWith("wav") || extension.endsWith(".mid") || extension.endsWith(".midi")) {
            fileType = FileType.audio;
        } else if (extension.endsWith("mp4") || extension.endsWith("3gp") || extension.endsWith("avi") || extension.endsWith("mpg") || extension.endsWith("flv") || extension.endsWith("wmv") || extension.endsWith("m4v") || extension.endsWith(".mpeg")) {
            fileType = FileType.video;
        }

        return fileType;
    }

    /**
     * check intent data and get type and address message
     */
    private void checkData(Intent intent) {

        String action = intent.getAction();
        String type = intent.getType();
        if (action == null || type == null) return;

        if (Intent.ACTION_SEND.equals(action)) {

            if (type.equals("text/plain")) {

                handleSendText(intent);
            } else if (type.startsWith("image/")) {

                SetOutPutSingleFile(FileType.image);
            } else if (type.startsWith("video/")) {

                SetOutPutSingleFile(FileType.video);
            } else if (type.startsWith("audio/")) {

                SetOutPutSingleFile(FileType.audio);
            } else {

                SetOutPutSingleFile(FileType.file);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {

            if (type.startsWith("image/")) {

                SetOutPutMultipleFile(FileType.image);
            } else if (type.startsWith("video/")) {

                SetOutPutMultipleFile(FileType.video);
            } else if (type.startsWith("audio/")) {

                SetOutPutMultipleFile(FileType.audio);
            } else {

                SetOutPutMultipleFile(FileType.file);
            }
        }

        if (hasSharedData && ActivityMain.isOpenChatBeforeSheare) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    new HelperFragment().removeAll(true);
                }
            });
        }
    }

    //*****************************************************************************************************

    private void SetOutPutSingleFile(FileType type) {
        Uri fileAddressUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM); // get file attachment
        //String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT); get text
        if (fileAddressUri != null) {
            hasSharedData = true;
            messageType = type;
            String extension = HelperString.dotSplit(fileAddressUri.getPath());
            /**
             * check mp4 because telegram sometimes send mp4 format with image type!!!
             */
            if (extension != null && extension.equals("mp4")) {
                messageType = FileType.video;
            }
            messageFileAddress = new ArrayList<Uri>();
            messageFileAddress.add(fileAddressUri);
        }
    }

    //*****************************************************************************************************

    private void SetOutPutMultipleFile(FileType type) {

        ArrayList<Uri> fileAddressUri = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (fileAddressUri != null) {

            hasSharedData = true;
            messageType = type;
            messageFileAddress = fileAddressUri;

            for (int i = 0; i < messageFileAddress.size(); i++) {
                FileType fileType = getMimeType(fileAddressUri.get(i));
                if (fileType == null) {
                    messageFileAddress.clear();
                    return;
                }

                String extension = HelperString.dotSplit(fileAddressUri.get(i).getPath());
                if (extension != null && extension.equals("mp4")) {
                    fileType = FileType.video;
                }

                fileTypeArray.add(fileType);
            }
        }
    }

    //*****************************************************************************************************

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            hasSharedData = true;
            messageType = FileType.message;
            message = sharedText;
        } else {
            SetOutPutSingleFile(FileType.file);
        }
    }

    //*****************************************************************************************************

    /**
     * get every data in bundle from intent
     */
    private void getAllDAtaInIntent(Intent intent) {

        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();

            while (it.hasNext()) {
                String key = it.next();
                Log.i("LOG", key + "=" + bundle.get(key));
            }
        }
    }

    //*****************************************************************************************************

    public ArrayList<Uri> getInfo() {
        return messageFileAddress;
    }

    public enum FileType {
        message, video, file, audio, image
    }
}
