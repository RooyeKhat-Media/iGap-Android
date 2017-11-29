/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import net.iGap.R;
import net.iGap.adapter.items.ContactItem;
import net.iGap.adapter.items.ContactItemGroup;
import net.iGap.adapter.items.ContactItemNotRegister;
import net.iGap.module.CustomTextViewMedium;

import java.util.List;

public class StickyHeaderAdapter<Item extends IItem> extends RecyclerView.Adapter implements StickyRecyclerHeadersAdapter {
    @Override public long getHeaderId(int position) {
        IItem item = getItem(position);

        //we want a separate header per first letter of our items
        if (item instanceof ContactItem && ((ContactItem) item).mContact != null) {
            return ((ContactItem) item).mContact.displayName.toUpperCase().charAt(0);
        } else if (item instanceof ContactItemNotRegister && ((ContactItemNotRegister) item).mContact != null) {
            return ((ContactItemNotRegister) item).mContact.displayName.toUpperCase().charAt(0);
        } else if (item instanceof ContactItemGroup && ((ContactItemGroup) item).mContact != null) {
            return ((ContactItemGroup) item).mContact.displayName.toUpperCase().charAt(0);
        }
        return -1;
    }

    @Override public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        //we create the view for the header
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        CustomTextViewMedium textView = (CustomTextViewMedium) holder.itemView;

        IItem item = getItem(position);
        if (item instanceof ContactItem && ((ContactItem) item).mContact != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((ContactItem) item).mContact.displayName.toUpperCase().charAt(0)));
        } else if (item instanceof ContactItemNotRegister && ((ContactItemNotRegister) item).mContact != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((ContactItemNotRegister) item).mContact.displayName.toUpperCase().charAt(0)));
        } else if (item instanceof ContactItemGroup && ((ContactItemGroup) item).mContact != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((ContactItemGroup) item).mContact.displayName.toUpperCase().charAt(0)));
        }
    }

    //private AbstractAdapter mParentAdapter;
    //keep a reference to the FastAdapter which contains the base logic
    private FastAdapter<Item> mFastAdapter;

    /**
     * Wrap the FastAdapter with this AbstractAdapter and keep its reference to forward all events correctly
     *
     * @param fastAdapter the FastAdapter which contains the base logic
     * @return this
     */
    public StickyHeaderAdapter<Item> wrap(FastAdapter fastAdapter) {
        //this.mParentAdapter = abstractAdapter;
        this.mFastAdapter = fastAdapter;
        return this;
    }

    /**
     * overwrite the registerAdapterDataObserver to correctly forward all events to the FastAdapter
     *
     * @param observer
     */
    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        if (mFastAdapter != null) {
            mFastAdapter.registerAdapterDataObserver(observer);
        }
    }

    /**
     * overwrite the unregisterAdapterDataObserver to correctly forward all events to the FastAdapter
     *
     * @param observer
     */
    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        if (mFastAdapter != null) {
            mFastAdapter.unregisterAdapterDataObserver(observer);
        }
    }

    /**
     * overwrite the getItemViewType to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mFastAdapter.getItemViewType(position);
    }

    /**
     * overwrite the getItemId to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return mFastAdapter.getItemId(position);
    }

    /**
     * @return the reference to the FastAdapter
     */
    public FastAdapter<Item> getFastAdapter() {
        return mFastAdapter;
    }

    /**
     * make sure we return the Item from the FastAdapter so we retrieve the item from all adapters
     *
     * @param position
     * @return
     */
    public Item getItem(int position) {
        return mFastAdapter.getItem(position);
    }

    /**
     * make sure we return the count from the FastAdapter so we retrieve the count from all adapters
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mFastAdapter.getItemCount();
    }

    /**
     * the onCreateViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mFastAdapter.onCreateViewHolder(parent, viewType);
    }

    /**
     * the onBindViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mFastAdapter.onBindViewHolder(holder, position);
    }

    /**
     * the onBindViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @param position
     * @param payloads
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        mFastAdapter.onBindViewHolder(holder, position, payloads);
    }

    /**
     * the setHasStableIds is managed by the FastAdapter so forward this correctly
     *
     * @param hasStableIds
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        mFastAdapter.setHasStableIds(hasStableIds);
    }

    /**
     * the onViewRecycled is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        mFastAdapter.onViewRecycled(holder);
    }

    /**
     * the onFailedToRecycleView is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @return
     */
    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return mFastAdapter.onFailedToRecycleView(holder);
    }

    /**
     * the onViewDetachedFromWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        mFastAdapter.onViewDetachedFromWindow(holder);
    }

    /**
     * the onViewAttachedToWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mFastAdapter.onViewAttachedToWindow(holder);
    }

    /**
     * the onAttachedToRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mFastAdapter.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * the onDetachedFromRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mFastAdapter.onDetachedFromRecyclerView(recyclerView);
    }
}