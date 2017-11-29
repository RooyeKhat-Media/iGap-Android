/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import java.util.List;
import net.iGap.G;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoInfoWallpaper;

public class InfoWallpaperResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public InfoWallpaperResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoInfoWallpaper.InfoWallpaperResponse.Builder builder = (ProtoInfoWallpaper.InfoWallpaperResponse.Builder) message;
        List<ProtoGlobal.Wallpaper> wallpaperList = builder.getWallpaperList();

        if (G.onGetWallpaper != null) {
            G.onGetWallpaper.onGetWallpaperList(wallpaperList);
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


