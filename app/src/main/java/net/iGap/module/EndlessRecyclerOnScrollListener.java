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

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.iGap.realm.RealmRoomMessage;

import java.util.ArrayList;
import java.util.List;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private int mPreviousTotal = 0;
    private boolean mLoading = true;
    private int mCurrentPage = 0;
    private FastItemAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mTotalItemsInPages = 200;
    private int mVisibleThreshold = -1;
    private OrientationHelper mOrientationHelper;
    private boolean mIsOrientationHelperVertical;
    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;
    private List<RealmRoomMessage> mMessagesList = new ArrayList<>();
    private boolean mAlreadyCalledOnNoMore;

    public EndlessRecyclerOnScrollListener(List<RealmRoomMessage> messageList, FastItemAdapter adapter) {
        mAdapter = adapter;
        mMessagesList = messageList;
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (this.mLayoutManager == null) {
            this.mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        }

        if (mVisibleThreshold == -1) {
            mVisibleThreshold = findLastVisibleItemPosition(recyclerView) - findFirstVisibleItemPosition(recyclerView);
        }

        mVisibleItemCount = recyclerView.getChildCount();
        mTotalItemCount = mLayoutManager.getItemCount();
        mFirstVisibleItem = findFirstVisibleItemPosition(recyclerView);

        mTotalItemCount = mAdapter.getItemCount();

        if (mLoading) {
            if (mTotalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = mTotalItemCount;
            }
        }

        if (!mLoading && mLayoutManager.findFirstVisibleItemPosition() - mVisibleThreshold <= 0) {
            mCurrentPage++;

            onLoadMore(this, mCurrentPage);

            mLoading = true;
        } else {
            //if (mAdapter.getAdapterItemCount() == mMessagesList.size() && mLayoutManager.findFirstVisibleItemPosition() - mVisibleThreshold <= 0) {// && !mAlreadyCalledOnNoMore
            if (mLayoutManager.findFirstVisibleItemPosition() - mVisibleThreshold <= 0) {// && !mAlreadyCalledOnNoMore
                onNoMore(this);
                //mAlreadyCalledOnNoMore = true;
            }
        }
    }

    private int findLastVisibleItemPosition(RecyclerView recyclerView) {
        final View child = findOneVisibleChild(recyclerView.getChildCount() - 1, -1, false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    private int findFirstVisibleItemPosition(RecyclerView recyclerView) {
        final View child = findOneVisibleChild(0, mLayoutManager.getChildCount(), false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    public abstract void onLoadMore(EndlessRecyclerOnScrollListener listener, int page);

    public abstract void onNoMore(EndlessRecyclerOnScrollListener listener);

    public List<RealmRoomMessage> loadMore(int page) {
        int startFrom = page * mTotalItemsInPages;
        List<RealmRoomMessage> messages = new ArrayList<>();

        // +timeMessagesAddedCount because of not counting time messages
        for (int i = startFrom, timeMessagesAddedCount = 0; i < (startFrom) + mTotalItemsInPages + timeMessagesAddedCount; i++) {
            if (i < mMessagesList.size()) {
                // Object is no longer valid to operate on. Was it deleted by another thread?
                // I check validity of the model
                // if it isn't valid, force to pass this item but fetch and check another more item
                messages.add(mMessagesList.get(i));
                if (mMessagesList.get(i).isOnlyTime()) {
                    timeMessagesAddedCount++;
                }
            } else {
                break;
            }
        }

        return messages;
    }

    private View findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible, boolean acceptPartiallyVisible) {
        if (mLayoutManager.canScrollVertically() != mIsOrientationHelperVertical || mOrientationHelper == null) {
            mIsOrientationHelperVertical = mLayoutManager.canScrollVertically();
            mOrientationHelper = mIsOrientationHelperVertical ? OrientationHelper.createVerticalHelper(mLayoutManager) : OrientationHelper.createHorizontalHelper(mLayoutManager);
        }

        final int start = mOrientationHelper.getStartAfterPadding();
        final int end = mOrientationHelper.getEndAfterPadding();
        final int next = toIndex > fromIndex ? 1 : -1;
        View partiallyVisible = null;
        for (int i = fromIndex; i != toIndex; i += next) {
            final View child = mLayoutManager.getChildAt(i);
            if (child != null) {
                final int childStart = mOrientationHelper.getDecoratedStart(child);
                final int childEnd = mOrientationHelper.getDecoratedEnd(child);
                if (childStart < end && childEnd > start) {
                    if (completelyVisible) {
                        if (childStart >= start && childEnd <= end) {
                            return child;
                        } else if (acceptPartiallyVisible && partiallyVisible == null) {
                            partiallyVisible = child;
                        }
                    } else {
                        return child;
                    }
                }
            }
        }
        return partiallyVisible;
    }

    public void resetPageCount() {
        this.resetPageCount(0);
    }

    public void resetPageCount(int page) {
        this.mPreviousTotal = 0;
        this.mLoading = true;
        this.mCurrentPage = page;
        this.onLoadMore(this, this.mCurrentPage);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public int getTotalItemCount() {
        return mTotalItemCount;
    }

    public int getFirstVisibleItem() {
        return mFirstVisibleItem;
    }

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }
}