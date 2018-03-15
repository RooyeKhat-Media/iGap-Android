/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter.items.chat;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentMap;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.AndroidUtils;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageLocation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.realm.Realm;

public class LocationItem extends AbstractMessage<LocationItem, LocationItem.ViewHolder> {

    public LocationItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLocation;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {

        if (holder.itemView.findViewById(R.id.mainContainer) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getLocationItem());

        }

        holder.imgMapPosition = (ReserveSpaceRoundedImageView) holder.itemView.findViewById(R.id.thumbnail);

        super.bindView(holder, payloads);

        holder.imgMapPosition.reserveSpace(G.context.getResources().getDimension(R.dimen.dp240), G.context.getResources().getDimension(R.dimen.dp120), getRoomType());

        RealmRoomMessageLocation item = null;

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getLocation() != null) {
                item = mMessage.forwardedFrom.getLocation();
            }
        } else {
            if (mMessage.location != null) {
                item = mMessage.location;
            }
        }

        if (item != null) {
            if (item.getImagePath() != null && new File(item.getImagePath()).exists()) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(item.getImagePath()), holder.imgMapPosition);
            } else {
                FragmentMap.loadImageFromPosition(item.getLocationLat(), item.getLocationLong(), new FragmentMap.OnGetPicture() {
                    @Override
                    public void getBitmap(Bitmap bitmap) {
                        holder.imgMapPosition.setImageBitmap(bitmap);

                        final String savedPath = FragmentMap.saveBitmapToFile(bitmap);

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                if (mMessage.forwardedFrom != null) {
                                    if (mMessage.forwardedFrom.getLocation() != null) {
                                        mMessage.forwardedFrom.getLocation().setImagePath(savedPath);
                                    }
                                } else {
                                    if (mMessage.location != null) {
                                        mMessage.location.setImagePath(savedPath);
                                    }
                                }
                            }
                        });
                        realm.close();
                    }
                });
            }

            final RealmRoomMessageLocation finalItem = item;
            holder.imgMapPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        HelperPermission.getLocationPermission(G.currentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        FragmentMap fragment = FragmentMap.getInctance(finalItem.getLocationLat(), finalItem.getLocationLong(), FragmentMap.Mode.seePosition);
                                        new HelperFragment(fragment).setReplace(false).load();
                                    }
                                });
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        ReserveSpaceRoundedImageView imgMapPosition;

        public ViewHolder(View view) {
            super(view);
        }
    }
}
