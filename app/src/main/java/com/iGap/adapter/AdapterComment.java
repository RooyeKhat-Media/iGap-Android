/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.iGap.R;
import com.iGap.activities.ActivityComment;
import com.iGap.module.CircleImageView;
import com.iGap.module.structs.StructCommentInfo;
import java.util.ArrayList;

public class AdapterComment extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public int replayCommentNumber = -1;
    private ArrayList<StructCommentInfo> list;
    private Context context;
    private ActivityComment.FragmentSubLayoutReplay layoutReplay;

    public AdapterComment(Context context, ArrayList<StructCommentInfo> list,
                          ActivityComment.FragmentSubLayoutReplay layoutReplay) {
        this.list = list;
        this.context = context;
        this.layoutReplay = layoutReplay;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_sub_layout, parent, false);
        return new MyViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (list.get(position).isChange) {
            updateSomething(position, holder.itemView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void addThreeSubLayoutReplay(final int position, int start, final View itemView) {

        int count = 0;

        if (list.get(position).replayMessageList != null) {
            count = list.get(position).replayMessageList.size();
            if (count < 1) {

                return;
            }
        }

        final LinearLayout layout = (LinearLayout) itemView.findViewById(R.id.csl_ll_add_replay);

        for (int i = start; i < count && i < 3 + start; i++) {

            View v = LayoutInflater.from(context).inflate(R.layout.comment_sub_layout, null, false);

            final StructCommentInfo infoReplay = list.get(position).replayMessageList.get(i);

            CircleImageView imvSenderPictureReplay =
                    (CircleImageView) v.findViewById(R.id.csl_img_comment_sender_picture);
            final TextView txtMessageReplay = (TextView) v.findViewById(R.id.csl_txt_message);
            TextView txtDateReplay = (TextView) v.findViewById(R.id.csl_txt_date);
            TextView txtClockReplay = (TextView) v.findViewById(R.id.csl_txt_clock);

            if (infoReplay.senderPicturePath.length() > 0) {
                imvSenderPictureReplay.setImageResource(
                        Integer.parseInt(infoReplay.senderPicturePath));
            } else {
                imvSenderPictureReplay.setImageBitmap(
                        com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                                (int) context.getResources().getDimension(R.dimen.dp60),
                                infoReplay.senderName, ""));
            }

            SpannableString s =
                    new SpannableString(infoReplay.senderName + ": " + infoReplay.message);
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#37B8CC")), 0,
                    infoReplay.senderName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                    infoReplay.senderName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtMessageReplay.setText(s);

            txtDateReplay.setText(infoReplay.date);
            txtClockReplay.setText(infoReplay.time);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (infoReplay.maxLine) {
                        txtMessageReplay.setEllipsize(null);
                        txtMessageReplay.setMaxLines(Integer.MAX_VALUE);
                        infoReplay.maxLine = false;
                    } else {
                        txtMessageReplay.setEllipsize(TextUtils.TruncateAt.END);
                        txtMessageReplay.setMaxLines(2);
                        infoReplay.maxLine = true;
                    }
                }
            });

            layout.addView(v);
        }

        if (count > 3 + start) {
            View vMore =
                    LayoutInflater.from(context).inflate(R.layout.comment_sub_layout_more, null, false);

            vMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    layout.removeViewAt(layout.getChildCount() - 1);
                    addThreeSubLayoutReplay(position, layout.getChildCount(), itemView);
                }
            });

            layout.addView(vMore);
        }
    }

    //************************************************************************************************

    private void updateSomething(int position, View holder) {

        for (int i = 0; i < list.get(position).allChanges.size(); i++) {

            int whatUpdate = Integer.parseInt(list.get(position).allChanges.get(i));

            switch (whatUpdate) {

                case 1: // gone layout replay
                    holder.findViewById(R.id.csl_ll_replay_comment).setVisibility(View.GONE);
                    holder.findViewById(R.id.csl_img_comment_sender_picture)
                            .setVisibility(View.VISIBLE);
                    holder.findViewById(R.id.csl_ll_comment).setBackgroundColor(Color.WHITE);
                    break;
            }
        }

        list.get(position).isChange = false;
        list.get(position).allChanges.clear();
    }

    private void visibleLayoutReplay(int position, View itemView) {

        replayCommentNumber = position;
        itemView.findViewById(R.id.csl_img_comment_sender_picture).setVisibility(View.GONE);
        itemView.findViewById(R.id.csl_ll_replay_comment).setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.csl_ll_comment).setBackgroundColor(Color.parseColor("#999999"));

        layoutReplay.setLayoutVisible(true);
        layoutReplay.setLayoutParameter(list.get(position).senderPicturePath,
                list.get(position).senderName, list.get(position).message);
    }

    private void goneLayoutReplay(int position) {

        list.get(position).isChange = true;
        list.get(position).allChanges.add("1");
        replayCommentNumber = -1;

        layoutReplay.setLayoutVisible(false);

        notifyItemChanged(position);
    }

    public void closeLayoutReplay() {
        goneLayoutReplay(replayCommentNumber);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imvSenderPicture;
        public TextView txtMessage;
        public TextView txtDate;
        public TextView txtClock;

        public MyViewHolder(final View itemView, final int position) {
            super(itemView);

            imvSenderPicture =
                    (CircleImageView) itemView.findViewById(R.id.csl_img_comment_sender_picture);
            txtMessage = (TextView) itemView.findViewById(R.id.csl_txt_message);
            txtDate = (TextView) itemView.findViewById(R.id.csl_txt_date);
            txtClock = (TextView) itemView.findViewById(R.id.csl_txt_clock);

            if (list.get(position).senderPicturePath.length() > 0) {
                imvSenderPicture.setImageResource(
                        Integer.parseInt(list.get(position).senderPicturePath));
            } else {
                imvSenderPicture.setImageBitmap(
                        com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                                (int) context.getResources().getDimension(R.dimen.dp60),
                                list.get(position).senderName, ""));
            }

            SpannableString s = new SpannableString(
                    list.get(position).senderName + ": " + list.get(position).message);
            s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.green)), 0,
                    list.get(position).senderName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                    list.get(position).senderName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtMessage.setText(s);

            txtDate.setText(list.get(position).date);
            txtClock.setText(list.get(position).time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (list.get(position).maxLine) {
                        txtMessage.setEllipsize(null);
                        txtMessage.setMaxLines(Integer.MAX_VALUE);
                        list.get(position).maxLine = false;
                    } else {
                        txtMessage.setEllipsize(TextUtils.TruncateAt.END);
                        txtMessage.setMaxLines(2);
                        list.get(position).maxLine = true;
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (replayCommentNumber == -1) {
                        visibleLayoutReplay(position, itemView);
                    } else if (replayCommentNumber == position) {
                        goneLayoutReplay(position);
                    } else {
                        goneLayoutReplay(replayCommentNumber);
                        visibleLayoutReplay(position, itemView);
                    }

                    return true;
                }
            });

            addThreeSubLayoutReplay(position, 0, itemView);
        }
    }
}
