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

import android.support.v7.widget.RecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RecyclerViewPauseOnScrollListener extends RecyclerView.OnScrollListener {

    private final boolean pauseOnScroll;
    private final boolean pauseOnSettling;
    private final RecyclerView.OnScrollListener externalListener;
    private ImageLoader imageLoader;

    public RecyclerViewPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling) {
        this(imageLoader, pauseOnScroll, pauseOnSettling, null);
    }

    public RecyclerViewPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling, RecyclerView.OnScrollListener customListener) {
        this.imageLoader = imageLoader;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnSettling = pauseOnSettling;
        externalListener = customListener;
    }

    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                imageLoader.resume();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                if (pauseOnScroll) {
                    imageLoader.pause();
                }
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                if (pauseOnSettling) {
                    imageLoader.pause();
                }
                break;
        }
        if (externalListener != null) {
            externalListener.onScrollStateChanged(recyclerView, newState);
        }
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (externalListener != null) {
            externalListener.onScrolled(recyclerView, dx, dy);
        }
    }
}