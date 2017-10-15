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

import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

/**
 * get enum and return appropriate string for use .
 * for example : " SENDING_IMAGE " convert to " sending image "
 */

public class HelperConvertEnumToString {

    /**
     * convert ClientAction
     *
     * @param action ClientAction enum
     * @return appropriate string for use
     */

    public static String convertActionEnum(ProtoGlobal.ClientAction action) {

        String finalActionName = null;

        switch (action) {

            case CANCEL:
                break;
            case TYPING:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.typing);
                break;
            case SENDING_IMAGE:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.sending_image);
                break;
            case CAPTURING_IMAGE:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.capturing_image);
                break;
            case SENDING_VIDEO:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.sending_video);
                break;
            case CAPTURING_VIDEO:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.capturing_video);
                break;
            case SENDING_AUDIO:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.sending_audio);
                break;
            case RECORDING_VOICE:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.recording_voice);
                break;
            case SENDING_VOICE:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.sending_voice);
                break;
            case SENDING_DOCUMENT:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.sending_document);
                break;
            case SENDING_GIF:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.sending_gif);
                break;
            case SENDING_FILE:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.sending_file);
                break;
            case SENDING_LOCATION:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.sending_location);
                break;
            case CHOOSING_CONTACT:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.choosing_contact);
                break;
            case PAINTING:
                finalActionName = G.fragmentActivity.getResources().getString(R.string.painting);
                break;
        }

        return finalActionName;
    }
}
