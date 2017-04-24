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

import com.iGap.G;
import com.iGap.R;
import com.iGap.proto.ProtoGlobal;

import static com.iGap.proto.ProtoGlobal.ClientAction.CAPTURING_IMAGE;
import static com.iGap.proto.ProtoGlobal.ClientAction.CAPTURING_VIDEO;
import static com.iGap.proto.ProtoGlobal.ClientAction.CHOOSING_CONTACT;
import static com.iGap.proto.ProtoGlobal.ClientAction.PAINTING;
import static com.iGap.proto.ProtoGlobal.ClientAction.RECORDING_VOICE;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_AUDIO;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_DOCUMENT;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_FILE;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_GIF;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_IMAGE;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_LOCATION;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_VIDEO;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_VOICE;
import static com.iGap.proto.ProtoGlobal.ClientAction.TYPING;

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
        String currentAction = action.toString();

        if (currentAction.equals(TYPING.toString())) {
            finalActionName = G.context.getResources().getString(R.string.typing);
        } else if (currentAction.equals(SENDING_IMAGE.toString())) {
            finalActionName = G.context.getResources().getString(R.string.sending_image);
        } else if (currentAction.equals(CAPTURING_IMAGE.toString())) {
            finalActionName = G.context.getResources().getString(R.string.capturing_image);
        } else if (currentAction.equals(SENDING_VIDEO.toString())) {
            finalActionName = G.context.getResources().getString(R.string.sending_video);
        } else if (currentAction.equals(CAPTURING_VIDEO.toString())) {
            finalActionName = G.context.getResources().getString(R.string.capturing_video);
        } else if (currentAction.equals(SENDING_AUDIO.toString())) {
            finalActionName = G.context.getResources().getString(R.string.sending_audio);
        } else if (currentAction.equals(RECORDING_VOICE.toString())) {
            finalActionName = G.context.getResources().getString(R.string.recording_voice);
        } else if (currentAction.equals(SENDING_VOICE.toString())) {
            finalActionName = G.context.getResources().getString(R.string.sending_voice);
        } else if (currentAction.equals(SENDING_DOCUMENT.toString())) {
            finalActionName = G.context.getResources().getString(R.string.sending_document);
        } else if (currentAction.equals(SENDING_GIF.toString())) {
            finalActionName = G.context.getResources().getString(R.string.sending_gif);
        } else if (currentAction.equals(SENDING_FILE.toString())) {
            finalActionName = G.context.getResources().getString(R.string.sending_file);
        } else if (currentAction.equals(SENDING_LOCATION.toString())) {
            finalActionName = G.context.getResources().getString(R.string.sending_location);
        } else if (currentAction.equals(CHOOSING_CONTACT.toString())) {
            finalActionName = G.context.getResources().getString(R.string.choosing_contact);
        } else if (currentAction.equals(PAINTING.toString())) {
            finalActionName = G.context.getResources().getString(R.string.painting);
        }

        return finalActionName;
    }

}
