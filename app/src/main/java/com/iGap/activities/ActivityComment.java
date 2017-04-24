/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterComment;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.CircleImageView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.structs.StructCommentInfo;
import io.github.meness.emoji.EmojiEditText;
import io.github.meness.emoji.emoji.Emoji;
import io.github.meness.emoji.listeners.OnEmojiBackspaceClickListener;
import io.github.meness.emoji.listeners.OnEmojiClickedListener;
import io.github.meness.emoji.listeners.OnEmojiPopupDismissListener;
import io.github.meness.emoji.listeners.OnEmojiPopupShownListener;
import io.github.meness.emoji.listeners.OnSoftKeyboardCloseListener;
import io.github.meness.emoji.listeners.OnSoftKeyboardOpenListener;
import java.util.ArrayList;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityComment extends ActivityEnhanced {

    private int numberOfComment = 0;
    private ArrayList<StructCommentInfo> list;
    private AdapterComment mAdapter;
    private FragmentSubLayoutReplay layoutReplay;

    private Button btnSend;
    private EmojiEditText edtChat;
    private MaterialDesignTextView btnSmile;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_show);

        String messageID = null;
        Bundle bundle = getIntent().getExtras();

        //        if (bundle != null) {
        //            messageID = bundle.getString("MessageID");
        //            if (messageID == null)
        //                finish();
        //        }

        getCommentList(messageID);

        initComponent();

        initRecycleView();

        setUpEmojiPopup();
    }

    private void getCommentList(String messageID) {

        list = new ArrayList<>();

        StructCommentInfo info = new StructCommentInfo();
        info.date = "agust 24";
        info.message =
                "this is a sample comment andu i ma goin gto the steori an dwer at egoid  he steori an dwer at egoid goin gto the ster wh goin o the steori an dwer at egoid goin gto ";
        info.senderName = "ali";
        info.senderID = " ali@kjfkd.com";
        info.time = "10:25";
        info.senderPicturePath = R.mipmap.difaultimage + "";

        info.replayMessageList = new ArrayList<>();
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);

        StructCommentInfo info2 = new StructCommentInfo();
        info2.date = "agust 24";
        info2.message =
                "this is a sample comment and hwo aare you i ma goin gto the steori an dwer at egoid goin gto the ster what is uout yout your name  ";
        info2.senderName = "hasan";
        info2.senderID = " hasan@kjfkd.com";
        info2.time = "10:25";
        info2.senderPicturePath = R.mipmap.empty + "";

        list.add(info2);
        list.add(info2);

        list.add(info);

        list.add(info2);
        list.add(info2);
        list.add(info2);
        list.add(info2);

        numberOfComment = list.size();
    }

    private void initComponent() {

        initLayoutAttachText();

        findViewById(R.id.acs_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        findViewById(R.id.asc_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

        layoutReplay = new FragmentSubLayoutReplay(findViewById(R.id.acs_ll_replay));

        Button btnBack = (Button) findViewById(R.id.acs_btn_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.acs_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        Button btnMenu = (Button) findViewById(R.id.acs_btn_menu);
        RippleView rippleMenu = (RippleView) findViewById(R.id.acs_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Log.e("ddd", "btnMenu  ");
            }
        });

        TextView txtNumberOfComment = (TextView) findViewById(R.id.acs_txt_number_of_comment);
        if (numberOfComment > 0) {
            txtNumberOfComment.setText(getString(R.string.comment) + " (" + numberOfComment + ")");
        } else {
            txtNumberOfComment.setText(R.string.no_comment);
        }
    }

    private io.github.meness.emoji.EmojiPopup emojiPopup;

    private void initRecycleView() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.acs_recycler_view_comment);
        mAdapter = new AdapterComment(ActivityComment.this, list, layoutReplay);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityComment.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }


    private void setUpEmojiPopup() {
        emojiPopup = io.github.meness.emoji.EmojiPopup.Builder.fromRootView(findViewById(R.id.ac_ll_parent))
                .setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
                    @Override
                    public void onEmojiBackspaceClicked(final View v) {
                        Log.d("MainActivity", "Clicked on Backspace");
                    }
                })
                .setOnEmojiClickedListener(new OnEmojiClickedListener() {
                    @Override
                    public void onEmojiClicked(final Emoji emoji) {
                        Log.d("MainActivity", "Clicked on emoji");
                    }
                })
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
                    }
                })
                .setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
                    @Override
                    public void onKeyboardOpen(final int keyBoardHeight) {
                        Log.d("MainActivity", "Opened soft keyboard");
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
                    }
                })
                .setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
                    @Override
                    public void onKeyboardClose() {
                        emojiPopup.dismiss();
                    }
                })
                .build(edtChat);
    }

    private void initLayoutAttachText() {

        btnSend = (Button) findViewById(R.id.acs_btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnSend  ");
            }
        });

        btnSmile = (MaterialDesignTextView) findViewById(R.id.acs_btn_smile);
        edtChat = (EmojiEditText) findViewById(R.id.acs_edt_chat);

        // to toggle between keyboard and emoji popup
        btnSmile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                emojiPopup.toggle();
            }
        });

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
            }
        });
        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (edtChat.getText().length() > 0 && mAdapter.replayCommentNumber >= 0) {
                    btnSend.setBackgroundDrawable(
                            getResources().getDrawable(R.drawable.send_button_blue));
                } else {
                    btnSend.setBackgroundDrawable(
                            getResources().getDrawable(R.drawable.send_button_gray));
                }

                // android emojione doesn't support common space unicode
                // to support space character, a new unicode will be replaced.
                if (editable.toString().contains("\u0020")) {
                    Editable ab =
                            new SpannableStringBuilder(editable.toString().replace("\u0020", "\u2000"));
                    editable.replace(0, editable.length(), ab);
                }
            }
        });
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        btnSmile.setText(drawableResourceId);
    }

    public class FragmentSubLayoutReplay {

        View subLayoutReplay;

        private CircleImageView imvReplayPicture;
        private TextView txtReplayFrom;
        private TextView txtReplayMessage;
        private TextView btnCloseLayout;

        FragmentSubLayoutReplay(View subLayoutReplay) {
            this.subLayoutReplay = subLayoutReplay;
            initView();
        }

        private void initView() {

            imvReplayPicture =
                    (CircleImageView) subLayoutReplay.findViewById(R.id.acs_imv_replay_pic);
            txtReplayFrom = (TextView) subLayoutReplay.findViewById(R.id.acs_txt_replay_from);
            txtReplayMessage = (TextView) subLayoutReplay.findViewById(R.id.acs_txt_replay_message);

            btnCloseLayout = (TextView) subLayoutReplay.findViewById(R.id.acs_btn_close);
            btnCloseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setLayoutVisible(false);
                    mAdapter.closeLayoutReplay();
                }
            });
        }

        public void setLayoutVisible(boolean visible) {

            if (visible) {
                subLayoutReplay.setVisibility(View.VISIBLE);
                if (edtChat.getText().length() > 0) {
                    btnSend.setBackgroundDrawable(
                            getResources().getDrawable(R.drawable.send_button_blue));
                }
            } else {
                subLayoutReplay.setVisibility(View.GONE);
                btnSend.setBackgroundDrawable(
                        getResources().getDrawable(R.drawable.send_button_gray));
            }
        }

        public void setLayoutParameter(String imagePath, String replayFrom, String replayMessage) {

            if (imagePath != null) {
                imvReplayPicture.setVisibility(View.VISIBLE);
                imvReplayPicture.setImageResource(Integer.parseInt(imagePath));
            } else {
                imvReplayPicture.setVisibility(View.GONE);
            }

            if (replayFrom != null) {
                txtReplayFrom.setText(replayFrom);
            } else {
                txtReplayFrom.setText("");
            }

            if (replayMessage != null) {
                txtReplayMessage.setText(replayMessage);
            } else {
                txtReplayMessage.setText("");
            }
        }
    }
}
