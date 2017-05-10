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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;
import java.io.File;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.emoji.EmojiTextView;
import net.iGap.fragments.SearchFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoGlobal;

public class SearchItem extends AbstractItem<SearchItem, SearchItem.ViewHolder> {
    public SearchFragment.StructSearch item;
    private Typeface typeFaceIcon;

    public SearchItem setContact(SearchFragment.StructSearch item) {
        this.item = item;
        return this;
    }

    @Override public int getType() {
        return R.id.sfsl_imv_contact_avatar;
    }

    @Override public int getLayoutRes() {
        return R.layout.search_fragment_sub_layout;
    }

    @Override public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        setAvatar(holder);

        holder.name.setText(item.name);
        holder.lastSeen.setText(item.comment);

        holder.txtTime.setText(TimeUtils.toLocal(item.time, G.CHAT_MESSAGE_TIME));

        if (HelperCalander.isLanguagePersian) {
            holder.name.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.name.getText().toString()));
            holder.lastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.lastSeen.getText().toString()));
            holder.txtTime.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtTime.getText().toString()));
        }

        holder.txtIcon.setVisibility(View.GONE);

        if (item.roomType == ProtoGlobal.Room.Type.CHAT) {
            //holder.txtIcon.setVisibility(View.VISIBLE);
            //holder.txtIcon.setText(G.context.getString(R.string.md_user_shape));
        } else if (item.roomType == ProtoGlobal.Room.Type.GROUP) {
            typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/MaterialIcons-Regular.ttf");
            holder.txtIcon.setTypeface(typeFaceIcon);
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.txtIcon.setText(G.context.getString(R.string.md_users_social_symbol));
        } else if (item.roomType == ProtoGlobal.Room.Type.CHANNEL) {
            typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/iGap_font.ttf");
            holder.txtIcon.setTypeface(typeFaceIcon);
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.txtIcon.setText(G.context.getString(R.string.md_channel_icon));
        }
    }

    private void setAvatar(ViewHolder holder) {
        if (item.avatar != null && item.avatar.getFile() != null) {

            String filepath = item.avatar.getFile().getLocalFilePath();
            if (filepath != null && new File(filepath).exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                holder.avatar.setImageBitmap(bitmap);
            } else {
                String filepathThumbnail = item.avatar.getFile().getLocalThumbnailPath();
                if (filepathThumbnail != null && new File(filepathThumbnail).exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(filepathThumbnail);
                    holder.avatar.setImageBitmap(bitmap);
                } else {
                    holder.avatar.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(holder.avatar.getLayoutParams().width, item.initials, item.color));
                }
            }
        } else {
            holder.avatar.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(holder.avatar.getLayoutParams().width, item.initials, item.color));
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView avatar;
        protected CustomTextViewMedium name;
        protected TextView txtIcon;
        protected EmojiTextView lastSeen;
        protected TextView txtTime;

        public ViewHolder(View view) {
            super(view);

            avatar = (CircleImageView) view.findViewById(R.id.sfsl_imv_contact_avatar);
            name = (CustomTextViewMedium) view.findViewById(R.id.sfsl_txt_contact_name);
            lastSeen = (EmojiTextView) view.findViewById(R.id.sfsl_txt_contact_lastseen);
            txtIcon = (TextView) view.findViewById(R.id.sfsl_txt_icon);
            txtTime = (TextView) view.findViewById(R.id.sfsl_txt_time);
        }
    }

    @Override public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}


