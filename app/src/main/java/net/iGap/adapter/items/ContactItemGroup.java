/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter.items;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;

import com.hanks.library.AnimateCheckBox;
import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoGlobal;

import java.util.HashMap;
import java.util.List;

/**
 * Contact item used with FastAdapter for Navigation drawer contacts fragment.
 */
public class ContactItemGroup extends AbstractItem<ContactItemGroup, ContactItemGroup.ViewHolder> {
    public static OnClickAdapter OnClickAdapter;
    public StructContactInfo mContact;
    private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();

    public ContactItemGroup setContact(StructContactInfo contact) {
        this.mContact = contact;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.contact_item_group;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.checkBoxSelect.setChecked(true);

        if (mContact.isHeader) {
            holder.topLine.setVisibility(View.VISIBLE);
        } else {
            holder.topLine.setVisibility(View.GONE);
        }

        if (mContact.isSelected) {
            holder.checkBoxSelect.setVisibility(View.VISIBLE);
        } else {
            holder.checkBoxSelect.setVisibility(View.INVISIBLE);
        }

        holder.title.setText(mContact.displayName);

        if (mContact.status != null) {
            if (mContact.status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                String timeUser = HelperCalander.getClocktime(mContact.lastSeen * DateUtils.SECOND_IN_MILLIS, false);

                holder.subtitle.setText(G.fragmentActivity.getResources().getString(R.string.last_seen_at) + " " + timeUser);
            } else {
                holder.subtitle.setText(mContact.status);
            }

            if (HelperCalander.isPersianUnicode) {
                holder.subtitle.setText(holder.subtitle.getText().toString());
            }
        }

        hashMapAvatar.put(mContact.peerId, holder.image);

        HelperAvatar.getAvatar(mContact.peerId, HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, final long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(ownerId));
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.image.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public interface OnClickAdapter {
        void onclick(long peerId, Uri uri, String displayName, boolean checked);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView image;
        protected CustomTextViewMedium title;
        protected CustomTextViewMedium subtitle;
        protected View topLine;
        protected AnimateCheckBox checkBoxSelect;

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.imageView);
            title = (CustomTextViewMedium) view.findViewById(R.id.title);
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
            topLine = (View) view.findViewById(R.id.topLine);
            checkBoxSelect = (AnimateCheckBox) view.findViewById(R.id.cig_checkBox_select_user);

        }

    }
}
