package net.iGap.adapter.items.chat;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.hanks.library.AnimateCheckBox;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.ReserveSpaceGifImageView;
import net.iGap.module.ReserveSpaceRoundedImageView;

import static android.R.attr.left;
import static android.graphics.Typeface.BOLD;
import static android.support.design.R.id.center;
import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static java.lang.Boolean.TRUE;
import static net.iGap.G.context;
import static net.iGap.G.isDarkTheme;
import static net.iGap.R.dimen.dp1_minus;
import static net.iGap.R.dimen.dp4;
import static net.iGap.R.dimen.dp52;
import static net.iGap.R.dimen.dp8;
import static net.iGap.R.dimen.messageContainerPadding;

public class ViewMaker {

    static View getTextItem() {

        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setId(R.id.mainContainer);
        mainContainer.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_216 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_216);

        LinearLayout linearLayout_683 = new LinearLayout(context);
        linearLayout_683.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_584 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_683.setLayoutParams(layout_584);

        LinearLayout contentContainer = new LinearLayout(context);
        contentContainer.setId(R.id.contentContainer);
        LinearLayout.LayoutParams layout_617 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentContainer.setLayoutParams(layout_617);
        contentContainer.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));

        LinearLayout m_container = new LinearLayout(context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_842 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_842);

        LinearLayout csliwt_layout_container_message = new LinearLayout(context);
        csliwt_layout_container_message.setId(R.id.csliwt_layout_container_message);
        csliwt_layout_container_message.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_577 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        csliwt_layout_container_message.setLayoutParams(layout_577);

        m_container.addView(csliwt_layout_container_message);
        contentContainer.addView(m_container);
        linearLayout_683.addView(contentContainer);

        mainContainer.addView(linearLayout_683);

        return mainContainer;
    }

    static View getVoiceItem() {

        LinearLayout mainContainer = new LinearLayout(G.context);
        mainContainer.setId(R.id.mainContainer);
        mainContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layout_477 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_477);

        LinearLayout linearLayout_349 = new LinearLayout(G.context);
        linearLayout_349.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layout_105 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_349.setLayoutParams(layout_105);

        LinearLayout contentContainer = new LinearLayout(G.context);
        contentContainer.setId(R.id.contentContainer);
        LinearLayout.LayoutParams layout_942 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentContainer.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
        contentContainer.setLayoutParams(layout_942);

        LinearLayout m_container = new LinearLayout(G.context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layout_148 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_148);

        LinearLayout linearLayout_197 = new LinearLayout(G.context);
        linearLayout_197.setGravity(Gravity.CENTER_VERTICAL);
        setLayoutDirection(linearLayout_197, View.LAYOUT_DIRECTION_LTR);
        linearLayout_197.setMinimumHeight(i_Dp(R.dimen.dp95));
        linearLayout_197.setMinimumWidth(i_Dp(R.dimen.dp220));
        linearLayout_197.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layout_80 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_197.setLayoutParams(layout_80);

        LinearLayout audioPlayerViewContainer = new LinearLayout(G.context);
        audioPlayerViewContainer.setId(R.id.audioPlayerViewContainer);
        audioPlayerViewContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layout_868 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        audioPlayerViewContainer.setLayoutParams(layout_868);

        LinearLayout linearLayout_153 = new LinearLayout(G.context);
        LinearLayout.LayoutParams layout_928 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_153.setLayoutParams(layout_928);

        //****************************
        FrameLayout frameLayout_161 = new FrameLayout(G.context);

        int pading = i_Dp(R.dimen.dp4);
        frameLayout_161.setPadding(pading, pading, pading, pading);

        LinearLayout.LayoutParams layout_1488 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp40), i_Dp(R.dimen.dp40));
        layout_1488.gravity = Gravity.CENTER;
        frameLayout_161.setLayoutParams(layout_1488);

        ImageView thumbnail = new ImageView(G.context);
        thumbnail.setId(R.id.thumbnail);
        FrameLayout.LayoutParams layout_152 = new FrameLayout.LayoutParams(i_Dp(R.dimen.dp20), i_Dp(R.dimen.dp20));
        layout_152.gravity = Gravity.CENTER;
        AppUtils.setImageDrawable(thumbnail, R.drawable.microphone_icon);
        thumbnail.setLayoutParams(layout_152);
        frameLayout_161.addView(thumbnail);

        frameLayout_161.addView(getProgressBar(0));
        linearLayout_153.addView(frameLayout_161);

        TextView cslv_txt_author = new TextView(G.context);
        cslv_txt_author.setId(R.id.cslv_txt_author);
        cslv_txt_author.setText("recorded voice");
        cslv_txt_author.setTextColor(Color.parseColor(G.textTitleTheme));
        cslv_txt_author.setSingleLine(true);
        setTextSize(cslv_txt_author, R.dimen.dp14);
        cslv_txt_author.setMaxLines(2);
        setTypeFace(cslv_txt_author);
        // cslv_txt_author.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams layout_799 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_799.topMargin = i_Dp(R.dimen.dp12);
        cslv_txt_author.setLayoutParams(layout_799);
        linearLayout_153.addView(cslv_txt_author);
        audioPlayerViewContainer.addView(linearLayout_153);

        LinearLayout linearLayout_503 = new LinearLayout(G.context);
        linearLayout_503.setGravity(Gravity.LEFT | Gravity.CENTER);
        linearLayout_503.setMinimumHeight(i_Dp(R.dimen.dp32));
        LinearLayout.LayoutParams layout_669 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        linearLayout_503.setLayoutParams(layout_669);

        TextView csla_btn_play_music = new TextView(G.context);
        csla_btn_play_music.setId(R.id.csla_btn_play_music);
        csla_btn_play_music.setBackgroundResource(0);
        csla_btn_play_music.setGravity(Gravity.CENTER);
        csla_btn_play_music.setEnabled(false);
        csla_btn_play_music.setText(G.fragmentActivity.getResources().getString(R.string.md_play_arrow));
        csla_btn_play_music.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
        setTextSize(csla_btn_play_music, R.dimen.dp20);
        csla_btn_play_music.setTypeface(G.typeface_Fontico);
        LinearLayout.LayoutParams layout_978 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp40), ViewGroup.LayoutParams.MATCH_PARENT);
        csla_btn_play_music.setLayoutParams(layout_978);
        linearLayout_503.addView(csla_btn_play_music);

        SeekBar csla_seekBar1 = new SeekBar(G.context);
        csla_seekBar1.setId(R.id.csla_seekBar1);
        LinearLayout.LayoutParams layout_652 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        csla_seekBar1.setLayoutParams(layout_652);
        linearLayout_503.addView(csla_seekBar1);
        audioPlayerViewContainer.addView(linearLayout_503);

        TextView csla_txt_timer = new TextView(G.context);
        csla_txt_timer.setId(R.id.csla_txt_timer);
        csla_txt_timer.setPadding(0, 0, i_Dp(R.dimen.dp8), 0);
        csla_txt_timer.setText("00:00");
        csla_txt_timer.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
        setTextSize(csla_txt_timer, R.dimen.dp10);
        setTypeFace(csla_txt_timer);
        LinearLayout.LayoutParams layout_758 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_758.gravity = Gravity.RIGHT;
        layout_758.leftMargin = i_Dp(R.dimen.dp52);
        csla_txt_timer.setLayoutParams(layout_758);

        audioPlayerViewContainer.addView(csla_txt_timer);
        linearLayout_197.addView(audioPlayerViewContainer);
        m_container.addView(linearLayout_197);
        contentContainer.addView(m_container);
        linearLayout_349.addView(contentContainer);
        mainContainer.addView(linearLayout_349);

        return mainContainer;
    }

    static View getVideoItem(boolean withText) {

        LinearLayout mainContainer = new LinearLayout(G.context);
        mainContainer.setId(R.id.mainContainer);
        mainContainer.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_882 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_882);

        LinearLayout linearLayout_223 = new LinearLayout(G.context);
        linearLayout_223.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_509 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_223.setLayoutParams(layout_509);

        LinearLayout contentContainer = new LinearLayout(G.context);
        LinearLayout.LayoutParams layout_5095 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentContainer.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
        contentContainer.setLayoutParams(layout_5095);
        contentContainer.setId(R.id.contentContainer);

        LinearLayout m_container = new LinearLayout(G.context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_518 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_518);

        FrameLayout frameLayout_642 = new FrameLayout(G.context);
        LinearLayout.LayoutParams layout_535 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout_642.setLayoutParams(layout_535);

        ReserveSpaceRoundedImageView thumbnail = new ReserveSpaceRoundedImageView(G.context);
        thumbnail.setId(R.id.thumbnail);
        FrameLayout.LayoutParams layout_679 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        thumbnail.setLayoutParams(layout_679);
        thumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
        thumbnail.setCornerRadius((int) G.context.getResources().getDimension(R.dimen.messageBox_cornerRadius));
        frameLayout_642.addView(thumbnail);

        TextView duration = new TextView(G.context);
        duration.setId(R.id.duration);
        duration.setBackgroundResource(R.drawable.bg_message_image_time);
        duration.setGravity(Gravity.CENTER_VERTICAL);
        duration.setSingleLine(true);
        duration.setPadding(i_Dp(R.dimen.dp4), dpToPixel(1), i_Dp(R.dimen.dp4), dpToPixel(1));
        duration.setText("3:48 (4.5 MB)");
        duration.setAllCaps(TRUE);
        duration.setTextColor(G.context.getResources().getColor(R.color.gray10));
        setTextSize(duration, R.dimen.dp10);
        setTypeFace(duration);
        FrameLayout.LayoutParams layout_49 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_49.gravity = Gravity.LEFT | Gravity.TOP;
        layout_49.bottomMargin = -dpToPixel(2);
        layout_49.leftMargin = dpToPixel(5);
        layout_49.topMargin = dpToPixel(7);
        duration.setLayoutParams(layout_49);
        frameLayout_642.addView(duration);

        frameLayout_642.addView(getProgressBar(0), new FrameLayout.LayoutParams(i_Dp(R.dimen.dp48), i_Dp(R.dimen.dp48), Gravity.CENTER));

        m_container.addView(frameLayout_642);

        if (withText) {
            m_container.addView(getTextView());
        }

        contentContainer.addView(m_container);
        linearLayout_223.addView(contentContainer);
        mainContainer.addView(linearLayout_223);

        return mainContainer;
    }

    static View getUnreadMessageItem() {

        TextView cslum_txt_unread_message = new TextView(G.context);
        cslum_txt_unread_message.setId(R.id.cslum_txt_unread_message);
        cslum_txt_unread_message.setPadding(0, dpToPixel(2), 0, dpToPixel(2));
        cslum_txt_unread_message.setBackgroundColor(G.context.getResources().getColor(R.color.green));
        setTextSize(cslum_txt_unread_message, R.dimen.dp12);
        setTypeFace(cslum_txt_unread_message);
        cslum_txt_unread_message.setGravity(CENTER);
        cslum_txt_unread_message.setText(G.fragmentActivity.getResources().getString(R.string.unread_message));
        cslum_txt_unread_message.setTextColor(G.context.getResources().getColor(R.color.white));
        LinearLayout.LayoutParams layout_692 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_692.bottomMargin = i_Dp(R.dimen.dp8);
        layout_692.topMargin = i_Dp(R.dimen.dp8);
        cslum_txt_unread_message.setLayoutParams(layout_692);

        return cslum_txt_unread_message;
    }

    static View getTimeItem() {

        LinearLayout linearLayout_33 = new LinearLayout(G.context);
        linearLayout_33.setOrientation(HORIZONTAL);
        linearLayout_33.setGravity(CENTER);
        LinearLayout.LayoutParams layout_509 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_33.setLayoutParams(layout_509);
        linearLayout_33.setPadding(0, i_Dp(R.dimen.dp12), 0, i_Dp(R.dimen.dp12));

        View view_12 = new View(G.context);
        view_12.setBackgroundColor(Color.parseColor(G.logLineTheme));
        LinearLayout.LayoutParams layout_522 = new LinearLayout.LayoutParams(0, 1, 1);
        view_12.setLayoutParams(layout_522);
        linearLayout_33.addView(view_12);

        TextView text = new TextView(G.context);
        text.setId(R.id.cslt_txt_time_date);
        text.setSingleLine(true);
        text.setPadding(i_Dp(R.dimen.dp16), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp16), i_Dp(R.dimen.dp4));
        if (isDarkTheme) {
            text.setBackgroundResource(R.drawable.background_log_time_dark);
            text.setTextColor(Color.parseColor(G.textSubTheme));
        } else {
            text.setBackgroundResource(R.drawable.background_log_time);
            text.setTextColor(G.context.getResources().getColor(R.color.text_log_time));
        }

        text.setText("Today");
        text.setAllCaps(false);
        setTextSize(text, R.dimen.dp12);
        setTypeFace(text);
        LinearLayout.LayoutParams layout_835 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_835.gravity = Gravity.CENTER_HORIZONTAL;
        text.setLayoutParams(layout_835);
        linearLayout_33.addView(text);

        View vew_147 = new View(G.context);
        vew_147.setBackgroundColor(Color.parseColor(G.logLineTheme));
        LinearLayout.LayoutParams layout_270 = new LinearLayout.LayoutParams(0, 1, 1);
        vew_147.setLayoutParams(layout_270);
        linearLayout_33.addView(vew_147);

        return linearLayout_33;
    }

    static View getProgressWaitingItem() {

        ProgressBar cslp_progress_bar_waiting = new ProgressBar(G.context);
        cslp_progress_bar_waiting.setId(R.id.cslp_progress_bar_waiting);
        cslp_progress_bar_waiting.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
        cslp_progress_bar_waiting.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layout_842 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_842.gravity = Gravity.CENTER;
        cslp_progress_bar_waiting.setIndeterminate(true);
        cslp_progress_bar_waiting.setLayoutParams(layout_842);

        return cslp_progress_bar_waiting;
    }

    static View getLogItem() {

        TextView text = new TextView(G.context);
        text.setId(R.id.csll_txt_log_text);

        text.setPadding(i_Dp(R.dimen.dp24), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp24), i_Dp(R.dimen.dp4));
        text.setGravity(CENTER);
        text.setText("Log");
        if (isDarkTheme) {
            text.setTextColor(Color.parseColor(G.textTitleTheme));
            text.setBackgroundResource(R.drawable.recangle_gray_tranceparent_dark);
        } else {
            text.setTextColor(Color.parseColor("#4a5d5c"));
            text.setBackgroundResource(R.drawable.recangle_gray_tranceparent);
        }

        setTextSize(text, R.dimen.dp12);
        setTypeFace(text);
        text.setAllCaps(false);
        FrameLayout.LayoutParams layout_138 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_138.gravity = Gravity.CENTER_HORIZONTAL;
        text.setLayoutParams(layout_138);

        return text;
    }

    static View getLocationItem() {

        LinearLayout mainContainer = new LinearLayout(G.context);
        mainContainer.setId(R.id.mainContainer);
        LinearLayout.LayoutParams layout_761 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_761);

        LinearLayout linearLayout_532 = new LinearLayout(G.context);
        linearLayout_532.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_639 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_532.setLayoutParams(layout_639);

        LinearLayout contentContainer = new LinearLayout(G.context);
        contentContainer.setId(R.id.contentContainer);
        LinearLayout.LayoutParams layoutParamsContentContainer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentContainer.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
        contentContainer.setLayoutParams(layoutParamsContentContainer);

        LinearLayout m_container = new LinearLayout(G.context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_788 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_788);

        FrameLayout frameLayout = new FrameLayout(G.context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        ReserveSpaceRoundedImageView reserveSpaceRoundedImageView = new ReserveSpaceRoundedImageView(G.context);
        reserveSpaceRoundedImageView.setId(R.id.thumbnail);
        reserveSpaceRoundedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        reserveSpaceRoundedImageView.setCornerRadius((int) G.context.getResources().getDimension(R.dimen.messageBox_cornerRadius));
        LinearLayout.LayoutParams layout_758 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reserveSpaceRoundedImageView.setLayoutParams(layout_758);

        mainContainer.addView(linearLayout_532);
        linearLayout_532.addView(contentContainer);
        contentContainer.addView(m_container);
        m_container.addView(frameLayout);

        frameLayout.addView(reserveSpaceRoundedImageView);

        return mainContainer;
    }

    static View getGifItem(boolean withText) {

        LinearLayout mainContainer = new LinearLayout(G.context);
        mainContainer.setId(R.id.mainContainer);
        LinearLayout.LayoutParams layout_761 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_761);

        LinearLayout linearLayout_532 = new LinearLayout(G.context);
        linearLayout_532.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_639 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_532.setLayoutParams(layout_639);

        LinearLayout contentContainer = new LinearLayout(G.context);
        contentContainer.setId(R.id.contentContainer);
        LinearLayout.LayoutParams layout_893 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentContainer.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
        contentContainer.setLayoutParams(layout_893);

        LinearLayout m_container = new LinearLayout(G.context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_788 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_788);

        FrameLayout frameLayout = new FrameLayout(G.context);
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        ReserveSpaceGifImageView reserveSpaceGifImageView = new ReserveSpaceGifImageView(G.context);
        reserveSpaceGifImageView.setId(R.id.thumbnail);
        FrameLayout.LayoutParams layout_758 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reserveSpaceGifImageView.setLayoutParams(layout_758);

        frameLayout.addView(reserveSpaceGifImageView);
        frameLayout.addView(getProgressBar(0), new FrameLayout.LayoutParams(i_Dp(R.dimen.dp60), i_Dp(R.dimen.dp60), Gravity.CENTER));
        m_container.addView(frameLayout);
        if (withText) {
            m_container.addView(getTextView());
        }
        contentContainer.addView(m_container);
        linearLayout_532.addView(contentContainer);
        mainContainer.addView(linearLayout_532);

        return mainContainer;
    }

    static View getViewTime() {

        LinearLayout csl_ll_time = new LinearLayout(context);
        csl_ll_time.setId(R.id.csl_ll_time);
        csl_ll_time.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_189 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_189.bottomMargin = dpToPixel(4);
        csl_ll_time.setPadding(dpToPixel(5), 0, dpToPixel(5), 0);
        csl_ll_time.setLayoutParams(layout_189);

        TextView txtEditedIndicator = new TextView(context);
        txtEditedIndicator.setId(R.id.txtEditedIndicator);
        txtEditedIndicator.setPadding(i_Dp(dp4), 0, 0, 0);
        txtEditedIndicator.setGravity(CENTER);
        txtEditedIndicator.setSingleLine(true);
        txtEditedIndicator.setText(context.getResources().getString(R.string.edited));
        setTextSize(txtEditedIndicator, R.dimen.dp8);
        if (G.isDarkTheme) {
            txtEditedIndicator.setTextAppearance(context, R.style.text_sub_style_setting_dark);
        } else {
            txtEditedIndicator.setTextAppearance(context, R.style.ChatMessages_Time);
        }

        setTypeFace(txtEditedIndicator);
        LinearLayout.LayoutParams layout_927 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_927.rightMargin = i_Dp(dp4);
        layout_927.topMargin = dpToPixel(4);
        txtEditedIndicator.setLayoutParams(layout_927);

        TextView cslr_txt_time = new TextView(context);
        cslr_txt_time.setId(R.id.cslr_txt_time);
        cslr_txt_time.setGravity(CENTER);
        cslr_txt_time.setPadding(dpToPixel(2), 0, dpToPixel(2), 0);
        cslr_txt_time.setText("10:21");
        cslr_txt_time.setSingleLine(true);
        cslr_txt_time.setTextAppearance(context, R.style.ChatMessages_Time);
        setTypeFace(cslr_txt_time);
        LinearLayout.LayoutParams layout_638 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_638.topMargin = i_Dp(dp4);
        cslr_txt_time.setLayoutParams(layout_638);

        // ContextThemeWrapper newContext = new ContextThemeWrapper(G.context, R.style.ChatMessages_MaterialDesignTextView_Tick);
        ImageView cslr_txt_tic = new ImageView(context);
        cslr_txt_tic.setId(R.id.cslr_txt_tic);
//        cslr_txt_tic.setColorFilter(context.getResources().getColor(R.color.colorOldBlack));
        cslr_txt_tic.setColorFilter(Color.parseColor(G.tintImage), PorterDuff.Mode.SRC_IN);
        //AppUtils.setImageDrawable(cslr_txt_tic, R.drawable.ic_double_check);
        LinearLayout.LayoutParams layout_311 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp16), ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_311.leftMargin = i_Dp(dp4);
        layout_311.topMargin = i_Dp(dp1_minus);
        cslr_txt_tic.setLayoutParams(layout_311);

        csl_ll_time.addView(txtEditedIndicator);
        csl_ll_time.addView(cslr_txt_time);
        csl_ll_time.addView(cslr_txt_tic);

        return csl_ll_time;
    }

    static View getViewSeen() {

        LinearLayout lyt_see = new LinearLayout(context);
        lyt_see.setId(R.id.lyt_see);
        lyt_see.setGravity(Gravity.CENTER_VERTICAL);
        lyt_see.setOrientation(HORIZONTAL);
        lyt_see.setPadding(0, 0, i_Dp(dp4), 0);
        LinearLayout.LayoutParams layout_865 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lyt_see.setLayoutParams(layout_865);

        View cslm_view_left_dis = new View(context);
        cslm_view_left_dis.setId(R.id.cslm_view_left_dis);
        cslm_view_left_dis.setVisibility(View.GONE);
        LinearLayout.LayoutParams layout_901 = new LinearLayout.LayoutParams(i_Dp(dp52), dpToPixel(1));
        cslm_view_left_dis.setLayoutParams(layout_901);
        lyt_see.addView(cslm_view_left_dis);

        LinearLayout lyt_signature = new LinearLayout(context);
        lyt_signature.setId(R.id.lyt_signature);
        lyt_signature.setGravity(CENTER | RIGHT);
        lyt_signature.setOrientation(HORIZONTAL);
        lyt_signature.setPadding(0, 0, i_Dp(dp4), 0);
        lyt_signature.setVisibility(View.GONE);
        LinearLayout.LayoutParams layout_483 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        lyt_signature.setLayoutParams(layout_483);

        TextView txt_signature = new TextView(context);
        txt_signature.setId(R.id.txt_signature);
        txt_signature.setGravity(CENTER);
        txt_signature.setText("");
        txt_signature.setSingleLine(true);
        //  txt_signature.setFilters();
        txt_signature.setTextColor(context.getResources().getColor(R.color.room_message_gray));
        txt_signature.setTextAppearance(context, R.style.ChatMessages_Time);
        LinearLayout.LayoutParams layout_266 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.dp18));
        txt_signature.setLayoutParams(layout_266);
        setTypeFace(txt_signature);
        lyt_signature.addView(txt_signature);
        lyt_see.addView(lyt_signature);

        TextView txt_views_label = new TextView(context);
        txt_views_label.setId(R.id.txt_views_label);
        txt_views_label.setGravity(CENTER);
        txt_views_label.setText("0");
        txt_views_label.setTextAppearance(context, R.style.ChatMessages_Time);
        setTypeFace(txt_views_label);

        txt_views_label.setPadding(0, dpToPixel(2), 0, 0);
        txt_views_label.setTextColor(context.getResources().getColor(R.color.room_message_gray));
        LinearLayout.LayoutParams layout_959 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.dp16));
        txt_views_label.setLayoutParams(layout_959);
        lyt_see.addView(txt_views_label);

        MaterialDesignTextView img_eye = new MaterialDesignTextView(context);
        img_eye.setId(R.id.img_eye);
        img_eye.setText(context.getResources().getString(R.string.md_visibility));
        img_eye.setTextColor(context.getResources().getColor(R.color.gray_6c));
        setTextSize(img_eye, R.dimen.dp12);
        // img_eye.setPadding(0, dpToPixel(2), 0, 0);
        img_eye.setSingleLine(true);
        // img_eye.setTextAppearance(G.context, R.style.TextIconAppearance_toolbar);
        LinearLayout.LayoutParams layout_586 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_586.leftMargin = i_Dp(dp4);
        img_eye.setLayoutParams(layout_586);
        lyt_see.addView(img_eye);

        return lyt_see;
    }

    static View getViewReplay() {

        LinearLayout cslr_replay_layout = new LinearLayout(context);
        cslr_replay_layout.setId(R.id.cslr_replay_layout);
        cslr_replay_layout.setBackgroundColor(context.getResources().getColor(R.color.messageBox_replyBoxBackgroundSend));
        cslr_replay_layout.setClickable(true);
        cslr_replay_layout.setOrientation(HORIZONTAL);
        cslr_replay_layout.setPadding(i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(R.dimen.messageContainerPaddingLeftRight));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cslr_replay_layout.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        }

        setLayoutDirection(cslr_replay_layout, View.LAYOUT_DIRECTION_LOCALE);

        LinearLayout.LayoutParams layout_468 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cslr_replay_layout.setLayoutParams(layout_468);

        View verticalLine = new View(context);
        verticalLine.setId(R.id.verticalLine);
        verticalLine.setBackgroundColor(Color.parseColor("#f7ab07"));
        LinearLayout.LayoutParams layout_81 = new LinearLayout.LayoutParams(dpToPixel(3), ViewGroup.LayoutParams.MATCH_PARENT);

        if (HelperCalander.isPersianUnicode) {
            layout_81.leftMargin = i_Dp(dp8);
        } else {
            layout_81.rightMargin = i_Dp(dp8);
        }

        verticalLine.setLayoutParams(layout_81);
        cslr_replay_layout.addView(verticalLine);

        ImageView chslr_imv_replay_pic = new ImageView(context);
        chslr_imv_replay_pic.setId(R.id.chslr_imv_replay_pic);
        chslr_imv_replay_pic.setAdjustViewBounds(true);

        LinearLayout.LayoutParams layout_760 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.dp40));

        if (HelperCalander.isPersianUnicode) {
            layout_760.leftMargin = i_Dp(dp8);
        } else {
            layout_760.rightMargin = i_Dp(dp8);
        }

        chslr_imv_replay_pic.setLayoutParams(layout_760);
        cslr_replay_layout.addView(chslr_imv_replay_pic);

        LinearLayout linearLayout_376 = new LinearLayout(context);
        linearLayout_376.setGravity(LEFT);

        linearLayout_376.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_847 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        linearLayout_376.setLayoutParams(layout_847);

        EmojiTextViewE chslr_txt_replay_from = new EmojiTextViewE(context);
        chslr_txt_replay_from.setId(R.id.chslr_txt_replay_from);
        chslr_txt_replay_from.setSingleLine(true);
        chslr_txt_replay_from.setPadding(0, 0, 0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            chslr_txt_replay_from.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        }

        chslr_txt_replay_from.setText("");

        chslr_txt_replay_from.setTextColor(context.getResources().getColor(R.color.colorOldBlack));
        chslr_txt_replay_from.setTextAppearance(context, R.style.ChatMessages_EmojiTextView);
        setTextSize(chslr_txt_replay_from, R.dimen.dp12);

        chslr_txt_replay_from.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_55 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chslr_txt_replay_from.setLayoutParams(layout_55);
        linearLayout_376.addView(chslr_txt_replay_from);

        EmojiTextViewE chslr_txt_replay_message = new EmojiTextViewE(context);
        chslr_txt_replay_message.setId(R.id.chslr_txt_replay_message);
        chslr_txt_replay_message.setEllipsize(TextUtils.TruncateAt.END);
        chslr_txt_replay_message.setSingleLine(true);
        chslr_txt_replay_message.setPadding(0, 0, 0, 0);
        chslr_txt_replay_message.setText("");

        chslr_txt_replay_message.setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            chslr_txt_replay_message.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        }

        chslr_txt_replay_message.setTextAppearance(context, R.style.ChatMessages_EmojiTextView);
        setTextSize(chslr_txt_replay_message, R.dimen.dp12);
        LinearLayout.LayoutParams layout_641 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chslr_txt_replay_message.setLayoutParams(layout_641);
        chslr_txt_replay_message.setTypeface(G.typeface_IRANSansMobile);
        linearLayout_376.addView(chslr_txt_replay_message);
        cslr_replay_layout.addView(linearLayout_376);

        return cslr_replay_layout;
    }

    static View getViewForward() {

        LinearLayout cslr_ll_forward = new LinearLayout(context);
        cslr_ll_forward.setId(R.id.cslr_ll_forward);
        cslr_ll_forward.setClickable(true);
        cslr_ll_forward.setOrientation(HORIZONTAL);
        cslr_ll_forward.setPadding(i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(messageContainerPadding), i_Dp(R.dimen.messageContainerPaddingLeftRight), i_Dp(messageContainerPadding));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cslr_ll_forward.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        }
        setLayoutDirection(cslr_ll_forward, View.LAYOUT_DIRECTION_LOCALE);

        LinearLayout.LayoutParams layout_687 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cslr_ll_forward.setLayoutParams(layout_687);

        View View_997 = new View(context);
        View_997.setBackgroundColor(Color.parseColor(G.textTitleTheme));
        LinearLayout.LayoutParams layout_547 = new LinearLayout.LayoutParams(dpToPixel(2), ViewGroup.LayoutParams.MATCH_PARENT);
        layout_547.rightMargin = dpToPixel(3);
        View_997.setLayoutParams(layout_547);
        cslr_ll_forward.addView(View_997);


        TextView cslr_txt_prefix_forward = new TextView(context);
        cslr_txt_prefix_forward.setId(R.id.cslr_txt_prefix_forward);
        cslr_txt_prefix_forward.setText(context.getResources().getString(R.string.forwarded_from));
        cslr_txt_prefix_forward.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(cslr_txt_prefix_forward, R.dimen.dp12);
        cslr_txt_prefix_forward.setSingleLine(true);
        cslr_txt_prefix_forward.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_992 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_992.rightMargin = i_Dp(dp4);
        layout_992.leftMargin = i_Dp(R.dimen.dp6);
        cslr_txt_prefix_forward.setLayoutParams(layout_992);
        cslr_ll_forward.addView(cslr_txt_prefix_forward);

        TextView cslr_txt_forward_from = new TextView(context);
        cslr_txt_forward_from.setId(R.id.cslr_txt_forward_from);
        cslr_txt_forward_from.setMinimumWidth(i_Dp(R.dimen.dp100));
        cslr_txt_forward_from.setMaxWidth(i_Dp(R.dimen.dp140));
        cslr_txt_forward_from.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(cslr_txt_forward_from, R.dimen.dp12);
        cslr_txt_forward_from.setSingleLine(true);
        cslr_txt_forward_from.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_119 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cslr_txt_forward_from.setLayoutParams(layout_119);
        cslr_ll_forward.addView(cslr_txt_forward_from);


        return cslr_ll_forward;
    }

    static View getViewVote() {

        LinearLayout lyt_vote = new LinearLayout(context);
        lyt_vote.setId(R.id.lyt_vote);
        lyt_vote.setGravity(BOTTOM);
        setLayoutDirection(lyt_vote, View.LAYOUT_DIRECTION_LTR);
        lyt_vote.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_356 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp48), ViewGroup.LayoutParams.MATCH_PARENT);
        layout_356.gravity = BOTTOM;
        lyt_vote.setLayoutParams(layout_356);

        LinearLayout lyt_vote_sub = new LinearLayout(context);
        lyt_vote_sub.setOrientation(VERTICAL);
        lyt_vote_sub.setId(R.id.lyt_vote_sub);
        if (G.isDarkTheme) {
            lyt_vote_sub.setBackgroundDrawable(G.context.getResources().getDrawable(R.drawable.rectangel_white_round_vote_dark));
        } else {
            lyt_vote_sub.setBackgroundDrawable(G.context.getResources().getDrawable(R.drawable.rectangel_white_round));
        }

        LinearLayout.LayoutParams layout_35644 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_35644.leftMargin = i_Dp(R.dimen.dp2);
        lyt_vote_sub.setLayoutParams(layout_35644);

        LinearLayout lyt_vote_up = new LinearLayout(context);
        lyt_vote_up.setId(R.id.lyt_vote_up);
        lyt_vote_up.setGravity(CENTER);
        lyt_vote_up.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_799 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lyt_vote_up.setPadding(0, 0, 0, i_Dp(R.dimen.dp6));
        layout_799.bottomMargin = i_Dp(dp4);
        lyt_vote_up.setLayoutParams(layout_799);

        TextView txt_vote_up = new TextView(context);
        txt_vote_up.setId(R.id.txt_vote_up);
        txt_vote_up.setText("0");
        txt_vote_up.setTextAppearance(context, R.style.ChatMessages_Time);
        txt_vote_up.setSingleLine(true);
        setTypeFace(txt_vote_up);

        txt_vote_up.setTextColor(Color.parseColor(G.voteIconTheme));
        LinearLayout.LayoutParams layout_713 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dpToPixel(16));
        txt_vote_up.setLayoutParams(layout_713);
        lyt_vote_up.addView(txt_vote_up);

        MaterialDesignTextView img_vote_up = new MaterialDesignTextView(context);
        img_vote_up.setId(R.id.img_vote_up);
        img_vote_up.setText(context.getResources().getString(R.string.md_thumb_up));
        img_vote_up.setTextColor(Color.parseColor(G.voteIconTheme));
        setTextSize(img_vote_up, R.dimen.dp16);
        LinearLayout.LayoutParams layout_216 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        img_vote_up.setLayoutParams(layout_216);
        lyt_vote_up.addView(img_vote_up);
        lyt_vote_sub.addView(lyt_vote_up);

        LinearLayout lyt_vote_down = new LinearLayout(context);
        if (G.isDarkTheme) {
            lyt_vote_down.setBackgroundDrawable(G.context.getResources().getDrawable(R.drawable.rectangel_white_round_vote_dark));
        } else {
            lyt_vote_down.setBackgroundDrawable(G.context.getResources().getDrawable(R.drawable.rectangel_white_round));
        }
        lyt_vote_down.setId(R.id.lyt_vote_down);
        lyt_vote_down.setPadding(0, i_Dp(R.dimen.dp6), 0, 0);
        lyt_vote_down.setGravity(CENTER);
        lyt_vote_down.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_221 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lyt_vote_down.setLayoutParams(layout_221);

        MaterialDesignTextView img_vote_down = new MaterialDesignTextView(context);
        img_vote_down.setId(R.id.img_vote_down);
        img_vote_down.setText(context.getResources().getString(R.string.md_thumb_down));
        img_vote_down.setTextColor(Color.parseColor(G.voteIconTheme));
        setTextSize(img_vote_down, R.dimen.dp16);
        LinearLayout.LayoutParams layout_877 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        img_vote_down.setLayoutParams(layout_877);
        lyt_vote_down.addView(img_vote_down);

        TextView txt_vote_down = new TextView(context);
        txt_vote_down.setId(R.id.txt_vote_down);
        txt_vote_down.setText("0");
        txt_vote_down.setTextAppearance(context, R.style.ChatMessages_Time);
        setTypeFace(txt_vote_down);
        txt_vote_down.setSingleLine(true);
        txt_vote_down.setTextColor(Color.parseColor(G.voteIconTheme));
        LinearLayout.LayoutParams layout_856 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dpToPixel(16));
        txt_vote_down.setLayoutParams(layout_856);

        lyt_vote_down.addView(txt_vote_down);
        lyt_vote_sub.addView(lyt_vote_down);
        lyt_vote.addView(lyt_vote_sub);
        lyt_vote.addView(getForwardButton());

        return lyt_vote;
    }

    private static View getForwardButton() {
        LinearLayout lyt_vote_forward = new LinearLayout(context);
        lyt_vote_forward.setGravity(CENTER);
        lyt_vote_forward.setOrientation(VERTICAL);

        if (G.isDarkTheme) {
            lyt_vote_forward.setBackgroundDrawable(G.context.getResources().getDrawable(R.drawable.circle_white_dark));
        } else {
            lyt_vote_forward.setBackgroundDrawable(G.context.getResources().getDrawable(R.drawable.circle_white));
        }
        LinearLayout.LayoutParams layout_799_f = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp32), i_Dp(R.dimen.dp32));
        layout_799_f.topMargin = i_Dp(R.dimen.dp8);
        layout_799_f.bottomMargin = i_Dp(R.dimen.dp16);
        layout_799_f.leftMargin = i_Dp(R.dimen.dp2);
        lyt_vote_forward.setLayoutParams(layout_799_f);

        MaterialDesignTextView img_vote_forward = new MaterialDesignTextView(context);
        img_vote_forward.setId(R.id.img_vote_forward);
        img_vote_forward.setPadding(i_Dp(R.dimen.dp2), i_Dp(R.dimen.dp4), 0, 0);
        img_vote_forward.setGravity(CENTER);
        LinearLayout.LayoutParams layout_216_f = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        img_vote_forward.setText(context.getResources().getString(R.string.md_forward));
        img_vote_forward.setTextColor(Color.parseColor(G.voteIconTheme));
        setTextSize(img_vote_forward, R.dimen.dp20);
        img_vote_forward.setLayoutParams(layout_216_f);
        lyt_vote_forward.addView(img_vote_forward);

        return lyt_vote_forward;
    }

    static View getAudioItem() {

        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setId(R.id.mainContainer);
        mainContainer.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_859 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_859);

        LinearLayout linearLayout_880 = new LinearLayout(G.context);
        linearLayout_880.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_525 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_880.setLayoutParams(layout_525);

        LinearLayout contentContainer = new LinearLayout(G.context);
        contentContainer.setId(R.id.contentContainer);
        contentContainer.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
        LinearLayout.LayoutParams layout_256 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        contentContainer.setLayoutParams(layout_256);

        LinearLayout m_container = new LinearLayout(G.context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_520 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_520);

        LinearLayout audioBox = new LinearLayout(G.context);
        audioBox.setId(R.id.audioBox);
        setLayoutDirection(audioBox, View.LAYOUT_DIRECTION_LTR);
        audioBox.setMinimumHeight((int) context.getResources().getDimension(R.dimen.dp130));
        audioBox.setMinimumWidth(i_Dp(R.dimen.dp220));
        audioBox.setOrientation(HORIZONTAL);
        audioBox.setPadding(0, (int) G.context.getResources().getDimension(messageContainerPadding), 0, (int) G.context.getResources().getDimension(R.dimen.messageContainerPaddingBottom));
        LinearLayout.LayoutParams layout_262 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        audioBox.setLayoutParams(layout_262);

        LinearLayout linearLayout_39 = new LinearLayout(G.context);
        linearLayout_39.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_803 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout_803.leftMargin = (int) G.context.getResources().getDimension(R.dimen.dp8);
        linearLayout_39.setLayoutParams(layout_803);

        LinearLayout linearLayout_632 = new LinearLayout(G.context);
        LinearLayout.LayoutParams layout_842 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_632.setLayoutParams(layout_842);

        LinearLayout linearLayout_916 = new LinearLayout(G.context);
        linearLayout_916.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout_916.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_916.setLayoutParams(layout_6);

        FrameLayout frameLayout = new FrameLayout(G.context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        ImageView imgThumbnail = new ImageView(G.context);
        imgThumbnail.setId(R.id.thumbnail);
        LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp48), (int) G.context.getResources().getDimension(R.dimen.dp48));
        imgThumbnail.setAdjustViewBounds(true);
        imgThumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
        AppUtils.setImageDrawable(imgThumbnail, R.drawable.green_music_note);
        imgThumbnail.setLayoutParams(thumbnailParams);

        TextView fileSize = new TextView(G.context);
        fileSize.setId(R.id.fileSize);
        fileSize.setTextAppearance(context, android.R.style.TextAppearance_Small);
        fileSize.setGravity(BOTTOM | CENTER_HORIZONTAL);
        fileSize.setSingleLine(true);
        fileSize.setText("3.2 mb");
        fileSize.setAllCaps(TRUE);
        fileSize.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(fileSize, R.dimen.dp12);
        setTypeFace(fileSize);
        LinearLayout.LayoutParams layout_996 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fileSize.setLayoutParams(layout_996);
        linearLayout_632.addView(linearLayout_916);

        LinearLayout linearLayout_222 = new LinearLayout(G.context);
        linearLayout_222.setOrientation(VERTICAL);
        linearLayout_222.setPadding((int) G.context.getResources().getDimension(R.dimen.dp8), 0, 0, 0);
        LinearLayout.LayoutParams layout_114 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout_222.setLayoutParams(layout_114);

        TextView fileName = new TextView(G.context);
        fileName.setId(R.id.fileName);
        fileName.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        fileName.setGravity(LEFT);
        fileName.setSingleLine(true);
        fileName.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        fileName.setMaxWidth((int) G.context.getResources().getDimension(R.dimen.dp160));
        fileName.setText("file_name.ext");
        fileName.setTextColor(Color.parseColor(G.textSubTheme));
        setTextSize(fileName, R.dimen.dp14);
        fileName.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_298 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fileName.setLayoutParams(layout_298);
        linearLayout_222.addView(fileName);

        TextView songArtist = new TextView(G.context);
        songArtist.setId(R.id.songArtist);
        songArtist.setTextAppearance(context, android.R.style.TextAppearance_Small);
        songArtist.setSingleLine(true);
        songArtist.setText("Artist");
        setTypeFace(songArtist);
        songArtist.setTextColor(Color.parseColor(G.textTitleTheme));
        LinearLayout.LayoutParams layout_757 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        songArtist.setLayoutParams(layout_757);
        linearLayout_222.addView(songArtist);
        linearLayout_632.addView(linearLayout_222);
        linearLayout_39.addView(linearLayout_632);

        LinearLayout audioPlayerViewContainer = new LinearLayout(G.context);
        audioPlayerViewContainer.setId(R.id.audioPlayerViewContainer);
        audioPlayerViewContainer.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_435 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        audioPlayerViewContainer.setLayoutParams(layout_435);

        LinearLayout linearLayout_511 = new LinearLayout(G.context);
        linearLayout_511.setGravity(left | center);
        LinearLayout.LayoutParams layout_353 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) G.context.getResources().getDimension(R.dimen.dp36));
        linearLayout_511.setLayoutParams(layout_353);

        final MaterialDesignTextView txt_play_music = new MaterialDesignTextView(G.context);
        txt_play_music.setId(R.id.txt_play_music);
        txt_play_music.setBackgroundResource(0); //txt_play_music.setBackgroundResource(@null);
        txt_play_music.setTypeface(G.typeface_Fontico);
        txt_play_music.setGravity(CENTER);
        txt_play_music.setText(G.fragmentActivity.getResources().getString(R.string.md_play_arrow));
        txt_play_music.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
        setTextSize(txt_play_music, R.dimen.dp20);
        LinearLayout.LayoutParams layout_326 = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp32), LinearLayout.LayoutParams.MATCH_PARENT);
        txt_play_music.setLayoutParams(layout_326);
        linearLayout_511.addView(txt_play_music);

        final SeekBar csla_seekBar1 = new SeekBar(G.context);
        csla_seekBar1.setId(R.id.csla_seekBar1);
        LinearLayout.LayoutParams layout_990 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_990.weight = 1;
        layout_990.gravity = CENTER;
        csla_seekBar1.setEnabled(false);
        csla_seekBar1.setLayoutParams(layout_990);
        csla_seekBar1.setProgress(0);
        linearLayout_511.addView(csla_seekBar1);
        audioPlayerViewContainer.addView(linearLayout_511);

        final TextView csla_txt_timer = new TextView(G.context);
        csla_txt_timer.setId(R.id.csla_txt_timer);
        csla_txt_timer.setPadding(0, 0, (int) G.context.getResources().getDimension(R.dimen.dp8), 0);
        csla_txt_timer.setText("00:00");
        csla_txt_timer.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
        setTextSize(csla_txt_timer, R.dimen.dp10);
        setTypeFace(csla_txt_timer);
        LinearLayout.LayoutParams layout_637 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_637.gravity = RIGHT;
        layout_637.leftMargin = (int) G.context.getResources().getDimension(R.dimen.dp52);
        csla_txt_timer.setLayoutParams(layout_637);
        audioPlayerViewContainer.addView(csla_txt_timer);
        linearLayout_39.addView(audioPlayerViewContainer);
        audioBox.addView(linearLayout_39);
        m_container.addView(audioBox);

        LinearLayout csliwt_layout_container_message = new LinearLayout(G.context);
        csliwt_layout_container_message.setId(R.id.csliwt_layout_container_message);
        csliwt_layout_container_message.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_992 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp220), LinearLayout.LayoutParams.WRAP_CONTENT); // before width was -> LinearLayout.LayoutParams.MATCH_PARENT, for fix text scroll changed it
        csliwt_layout_container_message.setLayoutParams(layout_992);
        m_container.addView(csliwt_layout_container_message);
        contentContainer.addView(m_container);
        linearLayout_880.addView(contentContainer);

        linearLayout_916.addView(frameLayout);
        linearLayout_916.addView(fileSize);
        frameLayout.addView(imgThumbnail);
        frameLayout.addView(getProgressBar(R.dimen.dp48));
        mainContainer.addView(linearLayout_880);

        return mainContainer;
    }

    static View getContactItem() {
        LinearLayout lytMainContainer = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParamsMainContainer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lytMainContainer.setId(R.id.mainContainer);
        lytMainContainer.setOrientation(HORIZONTAL);
        lytMainContainer.setLayoutParams(layoutParamsMainContainer);

        LinearLayout lytContainer1 = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParamsContainer1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lytContainer1.setOrientation(VERTICAL);
        lytContainer1.setLayoutParams(layoutParamsContainer1);

        LinearLayout contentContainer = new LinearLayout(context);
        contentContainer.setId(R.id.contentContainer);
        LinearLayout.LayoutParams layoutParamsContentContainer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentContainer.setLayoutParams(layoutParamsContentContainer);
        contentContainer.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));

        LinearLayout m_container = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParamsM_container = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        m_container.setLayoutParams(layoutParamsM_container);

        LinearLayout container2 = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParamsContainer2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsContainer1.gravity = Gravity.CENTER_VERTICAL;
        container2.setOrientation(HORIZONTAL);
        container2.setPadding((int) G.context.getResources().getDimension(messageContainerPadding), 0, 5, 2);
        container2.setLayoutParams(layoutParamsContainer2);

        ImageView image = new ImageView(G.context);
        LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp48), i_Dp(R.dimen.dp48));
        layoutParamsImage.rightMargin = 14;
        image.setId(R.id.image);
        image.setContentDescription(null);
        AppUtils.setImageDrawable(image, R.drawable.user);
        image.setLayoutParams(layoutParamsImage);

        LinearLayout container3 = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParamsContainer3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container3.setOrientation(VERTICAL);
        container3.setLayoutParams(layoutParamsContainer3);

        TextView name = new TextView(G.context);
        LinearLayout.LayoutParams layoutParamsName = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        name.setId(R.id.name);
        name.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        name.setTextColor(Color.parseColor(G.textTitleTheme));
        name.setText("Contact Name");
        setTextSize(name, R.dimen.dp14);
        setTypeFace(name);
        name.setLayoutParams(layoutParamsName);
        container3.addView(name);

        TextView number = new TextView(G.context);
        LinearLayout.LayoutParams layoutParamsNumber = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        number.setId(R.id.number);
        number.setTextAppearance(context, android.R.style.TextAppearance_Small);
        setTypeFace(number);

        number.setTextColor(Color.parseColor(G.textTitleTheme));
        number.setText("Contact Number");
        number.setLayoutParams(layoutParamsNumber);

        container3.addView(number);
        container2.addView(image);
        container2.addView(container3);
        m_container.addView(container2);
        contentContainer.addView(m_container);
        lytContainer1.addView(contentContainer);
        lytMainContainer.addView(lytContainer1);

        return lytMainContainer;
    }

    static View getFileItem() {

        LinearLayout mainContainer = new LinearLayout(G.context);
        mainContainer.setId(R.id.mainContainer);
        mainContainer.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_106 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_106);

        LinearLayout linearLayout_768 = new LinearLayout(G.context);
        linearLayout_768.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_577 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_768.setLayoutParams(layout_577);

        LinearLayout contentContainer = new LinearLayout(G.context);
        LinearLayout.LayoutParams layoutParamsContentContainer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentContainer.setId(R.id.contentContainer);
        contentContainer.setLayoutParams(layoutParamsContentContainer);
        contentContainer.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
        contentContainer.setLayoutParams(layoutParamsContentContainer);

        LinearLayout m_container = new LinearLayout(G.context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_346 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_346);

        LinearLayout linearLayout_784 = new LinearLayout(G.context);
        linearLayout_784.setGravity(Gravity.CENTER_VERTICAL);
        setLayoutDirection(linearLayout_784, View.LAYOUT_DIRECTION_LTR);
        linearLayout_784.setOrientation(HORIZONTAL);
        linearLayout_784.setPadding(0, 0, (int) G.context.getResources().getDimension(R.dimen.messageContainerPadding), 0);
        LinearLayout.LayoutParams layout_419 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_419.gravity = CENTER;
        linearLayout_784.setLayoutParams(layout_419);

        FrameLayout frameLayout = new FrameLayout(G.context);
        FrameLayout.LayoutParams layoutParamsFrameLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsFrameLayout.gravity = CENTER;
        frameLayout.setPadding(10, 10, 10, 10);
        frameLayout.setLayoutParams(layoutParamsFrameLayout);

        ImageView imgThumbnail = new ImageView(G.context);
        imgThumbnail.setId(R.id.thumbnail);
        LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp48), (int) G.context.getResources().getDimension(R.dimen.dp48));
        thumbnailParams.gravity = CENTER;
        imgThumbnail.setBackgroundColor(Color.TRANSPARENT);
        imgThumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
        AppUtils.setImageDrawable(imgThumbnail, R.drawable.file_icon);
        imgThumbnail.setLayoutParams(thumbnailParams);

        LinearLayout linearLayout_780 = new LinearLayout(G.context);
        linearLayout_780.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_752 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_752.gravity = CENTER;
        linearLayout_780.setLayoutParams(layout_752);

        TextView songArtist = new TextView(G.context);
        songArtist.setId(R.id.songArtist);
        songArtist.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        songArtist.setSingleLine(true);

        songArtist.setMaxWidth((int) G.context.getResources().getDimension(R.dimen.dp180));
        songArtist.setText("file_name.ext");
        songArtist.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(songArtist, R.dimen.dp14);
        songArtist.setTypeface(G.typeface_IRANSansMobile_Bold, BOLD);
        LinearLayout.LayoutParams layout_1000 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        songArtist.setLayoutParams(layout_1000);
        linearLayout_780.addView(songArtist);

        TextView fileSize = new TextView(G.context);
        fileSize.setId(R.id.fileSize);
        fileSize.setSingleLine(true);
        fileSize.setText("3.2 mb");
        fileSize.setAllCaps(TRUE);
        fileSize.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(fileSize, R.dimen.dp10);
        setTypeFace(fileSize);
        LinearLayout.LayoutParams layout_958 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_958.topMargin = 3;
        fileSize.setLayoutParams(layout_958);
        linearLayout_780.addView(fileSize);
        linearLayout_784.addView(frameLayout);
        linearLayout_784.addView(linearLayout_780);
        m_container.addView(linearLayout_784);

        LinearLayout csliwt_layout_container_message = new LinearLayout(G.context);
        csliwt_layout_container_message.setId(R.id.csliwt_layout_container_message);
        csliwt_layout_container_message.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_312 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        csliwt_layout_container_message.setLayoutParams(layout_312);
        m_container.addView(csliwt_layout_container_message);
        contentContainer.addView(m_container);
        linearLayout_768.addView(contentContainer);

        frameLayout.addView(imgThumbnail);
        frameLayout.addView(getProgressBar(R.dimen.dp52));
        mainContainer.addView(linearLayout_768);

        return mainContainer;
    }

    static View getImageItem(boolean withText) {

        LinearLayout mainContainer = new LinearLayout(G.context);
        mainContainer.setId(R.id.mainContainer);
        LinearLayout.LayoutParams layout_761 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainContainer.setLayoutParams(layout_761);

        LinearLayout linearLayout_532 = new LinearLayout(G.context);
        linearLayout_532.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_639 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_532.setLayoutParams(layout_639);

        LinearLayout contentContainer = new LinearLayout(G.context);
        contentContainer.setId(R.id.contentContainer);
        LinearLayout.LayoutParams layoutParamsContentContainer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentContainer.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
        contentContainer.setLayoutParams(layoutParamsContentContainer);

        LinearLayout m_container = new LinearLayout(G.context);
        m_container.setId(R.id.m_container);
        m_container.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_788 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(layout_788);

        FrameLayout frameLayout = new FrameLayout(G.context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        ReserveSpaceRoundedImageView reserveSpaceRoundedImageView = new ReserveSpaceRoundedImageView(G.context);
        reserveSpaceRoundedImageView.setId(R.id.thumbnail);
        reserveSpaceRoundedImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        reserveSpaceRoundedImageView.setCornerRadius((int) G.context.getResources().getDimension(R.dimen.messageBox_cornerRadius));
        LinearLayout.LayoutParams layout_758 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reserveSpaceRoundedImageView.setLayoutParams(layout_758);

        mainContainer.addView(linearLayout_532);
        linearLayout_532.addView(contentContainer);
        contentContainer.addView(m_container);
        m_container.addView(frameLayout);
        if (withText) {
            m_container.addView(getTextView());
        }
        frameLayout.addView(reserveSpaceRoundedImageView);
        frameLayout.addView(getProgressBar(0), new FrameLayout.LayoutParams(i_Dp(R.dimen.dp60), i_Dp(R.dimen.dp60), Gravity.CENTER));

        return mainContainer;
    }

    static View makeTextViewMessage(int maxsize, boolean hasEmoji, boolean hasLink) {

        if (hasEmoji) {
            EmojiTextViewE emojiTextViewE = new EmojiTextViewE(context);
            emojiTextViewE.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (G.isDarkTheme) {
                emojiTextViewE.setTextColor(Color.parseColor(G.textTitleTheme));
            } else {
                emojiTextViewE.setTextColor(Color.parseColor("#333333"));
            }

            emojiTextViewE.setId(R.id.messageSenderTextMessage);
            emojiTextViewE.setPadding(10, 4, 10, 4);
            emojiTextViewE.setTypeface(G.typeface_IRANSansMobile);
            setTextSizeDirect(emojiTextViewE, G.userTextSize);
            emojiTextViewE.setEmojiSize(i_Dp(R.dimen.dp18));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                emojiTextViewE.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
            }
            setLayoutDirection(emojiTextViewE, View.LAYOUT_DIRECTION_LOCALE);
            if (hasLink) {
                emojiTextViewE.setMovementMethod(LinkMovementMethod.getInstance());
            }

            if (maxsize > 0) {
                emojiTextViewE.setMaxWidth(maxsize);
            }

            return emojiTextViewE;
        } else {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (G.isDarkTheme) {
                textView.setTextColor(Color.parseColor(G.textTitleTheme));
            } else {
                textView.setTextColor(Color.parseColor("#333333"));
            }

            textView.setId(R.id.messageSenderTextMessage);
            textView.setPadding(10, 0, 10, 0);
            textView.setTypeface(G.typeface_IRANSansMobile);
            setTextSizeDirect(textView, G.userTextSize);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
            }
            setLayoutDirection(textView, View.LAYOUT_DIRECTION_LOCALE);
            if (hasLink) {
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (maxsize > 0) {
                textView.setMaxWidth(maxsize);
            }

            return textView;
        }
    }

    static View makeHeaderTextView(String text) {

        EmojiTextViewE textView = new EmojiTextViewE(context);

        if (G.isDarkTheme) {
            textView.setTextColor(Color.BLACK);
        } else {
            textView.setTextColor(Color.parseColor(G.textTitleTheme));
        }

        textView.setBackgroundResource(R.drawable.rect_radios_top_gray);
        textView.setId(R.id.messageSenderName);
        textView.setGravity(LEFT);
        textView.setPadding(20, 0, 20, 5);
        //textView.setMinimumWidth((int) G.context.getResources().getDimension(R.dimen.dp220));
        textView.setSingleLine(true);
        textView.setTypeface(G.typeface_IRANSansMobile);
        textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        setTextSize(textView, R.dimen.dp12);

        return textView;
    }

    static View makeCircleImageView() {

        CircleImageView circleImageView = new CircleImageView(context);
        circleImageView.setId(R.id.messageSenderAvatar);

        int size = (int) context.getResources().getDimension(R.dimen.dp48);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(0, 0, (int) context.getResources().getDimension(dp8), 0);

        circleImageView.setLayoutParams(params);

        return circleImageView;
    }

    /**
     * return text view for items that have text (for example : image_text, video_text , ...)
     */
    private static View getTextView() {
        LinearLayout csliwt_layout_container_message = new LinearLayout(G.context);
        csliwt_layout_container_message.setId(R.id.csliwt_layout_container_message);
        csliwt_layout_container_message.setBackgroundColor(Color.parseColor(G.backgroundTheme));
        csliwt_layout_container_message.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_327 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        csliwt_layout_container_message.setLayoutParams(layout_327);
        return csliwt_layout_container_message;
    }

    private static View getProgressBar(int sizeSrc) {
        MessageProgress messageProgress = new MessageProgress(G.context);
        messageProgress.setId(R.id.progress);
        LinearLayout.LayoutParams params;
        if (sizeSrc > 0) {
            params = new LinearLayout.LayoutParams(i_Dp(sizeSrc), i_Dp(sizeSrc));
        } else {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        messageProgress.setLayoutParams(params);

        return messageProgress;
    }

    /**
     * ***************** Common Methods *****************
     */
    private static int dpToPixel(int dp) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;

        //  (2/getApplicationContext().getResources().getDisplayMetrics().density)
    }

    public static int i_Dp(int dpSrc) {

        return (int) context.getResources().getDimension(dpSrc);
    }

    public static void setTextSize(TextView v, int sizeSrc) {

        int mSize = i_Dp(sizeSrc);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSize);
    }

    private static void setTextSizeDirect(TextView v, int size) {
        v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    private static void setTypeFace(TextView v) {
        v.setTypeface(G.typeface_IRANSansMobile);
    }

    public static void setLayoutDirection(View view, int direction) {

        ViewCompat.setLayoutDirection(view, direction);
    }

    //*******************************************************************************************

    public static View getViewItemRoom() {

        LinearLayout root_chat_sub_layout = new LinearLayout(G.context);
        root_chat_sub_layout.setId(R.id.root_chat_sub_layout);
        LinearLayout.LayoutParams layout_553 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp68));
        root_chat_sub_layout.setLayoutParams(layout_553);
        root_chat_sub_layout.setBackgroundColor(Color.parseColor(G.backgroundTheme));

        CircleImageView cs_img_contact_picture = new CircleImageView(G.context);
        cs_img_contact_picture.setId(R.id.cs_img_contact_picture);
        LinearLayout.LayoutParams layout_113 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp52), i_Dp(R.dimen.dp52));
        layout_113.gravity = Gravity.CENTER;
        layout_113.setMargins(i_Dp(R.dimen.dp6), i_Dp(R.dimen.dp6), i_Dp(R.dimen.dp6), i_Dp(R.dimen.dp6));
        cs_img_contact_picture.setLayoutParams(layout_113);

        root_chat_sub_layout.addView(cs_img_contact_picture);

        LinearLayout linearLayout_849 = new LinearLayout(G.context);
        linearLayout_849.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_162 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout_849.setLayoutParams(layout_162);

        LinearLayout linearLayout_938 = new LinearLayout(G.context);
        linearLayout_938.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_347 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_347.weight = 1;
        linearLayout_938.setLayoutParams(layout_347);


        LinearLayout linearLayout_353 = new LinearLayout(G.context);
        linearLayout_353.setGravity(Gravity.TOP | Gravity.LEFT);
        linearLayout_353.setOrientation(VERTICAL);
        linearLayout_353.setPadding(0, i_Dp(R.dimen.dp6), 0, 0);
        LinearLayout.LayoutParams layout_860 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (HelperCalander.isPersianUnicode) {
            layout_860.rightMargin = i_Dp(R.dimen.dp12);
        } else {
            layout_860.leftMargin = i_Dp(R.dimen.dp12);
        }
        layout_860.weight = 1;
        linearLayout_353.setLayoutParams(layout_860);

        LinearLayout linearLayout_922 = new LinearLayout(G.context);
        linearLayout_922.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        if (HelperCalander.isPersianUnicode) {
            linearLayout_922.setGravity(Gravity.RIGHT);
        } else {
            linearLayout_922.setGravity(Gravity.LEFT);
        }

        linearLayout_922.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_256 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_922.setLayoutParams(layout_256);

        MaterialDesignTextView cs_txt_chat_icon = new MaterialDesignTextView(G.context);
        cs_txt_chat_icon.setId(R.id.cs_txt_chat_icon);
        if (G.isDarkTheme) {
            cs_txt_chat_icon.setTextColor(Color.parseColor(G.textTitleTheme));
        } else {
            cs_txt_chat_icon.setTextColor(Color.parseColor("#333333"));
        }


        setTextSize(cs_txt_chat_icon, R.dimen.dp14);
        LinearLayout.LayoutParams layout_525 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_525.gravity = Gravity.CENTER;
        if (HelperCalander.isPersianUnicode) {
            layout_525.leftMargin = i_Dp(R.dimen.dp8);
        } else {
            layout_525.rightMargin = i_Dp(R.dimen.dp8);
        }


        cs_txt_chat_icon.setLayoutParams(layout_525);
        linearLayout_922.addView(cs_txt_chat_icon);

        EmojiTextViewE cs_txt_contact_name = new EmojiTextViewE(G.context);
        cs_txt_contact_name.setId(R.id.cs_txt_contact_name);
        // cs_txt_contact_name.setLineSpacing((0/G.context.getResources().getDisplayMetrics().density), .8);
        cs_txt_contact_name.setMaxWidth(i_Dp(R.dimen.dp160));
        cs_txt_contact_name.setPadding(0, i_Dp(R.dimen.dp4), 0, i_Dp(R.dimen.dp4));
        cs_txt_contact_name.setText("Name");

        setTypeFace(cs_txt_contact_name);
        cs_txt_contact_name.setEllipsize(TextUtils.TruncateAt.END);
        cs_txt_contact_name.setSingleLine(true);
        cs_txt_contact_name.setEmojiSize(i_Dp(R.dimen.dp16));
        if (G.isDarkTheme) {
            cs_txt_contact_name.setTextColor(Color.parseColor(G.textTitleTheme));
        } else {
            cs_txt_contact_name.setTextColor(G.context.getResources().getColor(R.color.black90));
        }

        setTextSize(cs_txt_contact_name, R.dimen.dp15);
        LinearLayout.LayoutParams layout_115 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cs_txt_contact_name.setLayoutParams(layout_115);
        linearLayout_922.addView(cs_txt_contact_name);

        LinearLayout linearLayout_353dd = new LinearLayout(G.context);
        linearLayout_353dd.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_860ss = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        if (HelperCalander.isPersianUnicode) {
            layout_860ss.gravity = Gravity.RIGHT | Gravity.CENTER;
            linearLayout_353dd.setGravity(Gravity.RIGHT);
        } else {
            layout_860ss.gravity = Gravity.LEFT | Gravity.CENTER;
            linearLayout_353dd.setGravity(Gravity.LEFT);
        }
        linearLayout_353dd.setLayoutParams(layout_860ss);

        AppCompatImageView cs_img_verify = new AppCompatImageView(G.context);
        cs_img_verify.setId(R.id.cs_img_verify_room);
        cs_img_verify.setImageResource(R.drawable.ic_verify);
        LinearLayout.LayoutParams layout_152 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp18), i_Dp(R.dimen.dp18));
        layout_152.gravity = CENTER_VERTICAL;
        layout_152.leftMargin = i_Dp(R.dimen.dp4);
        layout_152.rightMargin = i_Dp(R.dimen.dp4);
        cs_img_verify.setLayoutParams(layout_152);

        linearLayout_353dd.addView(cs_img_verify);
        linearLayout_922.addView(linearLayout_353dd);
        linearLayout_353.addView(linearLayout_922);

        LinearLayout lyt_last_message_room = new LinearLayout(G.context);
        lyt_last_message_room.setId(R.id.lyt_last_message_room);
        // lyt_last_message_room.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        if (HelperCalander.isPersianUnicode) {
            lyt_last_message_room.setGravity(Gravity.RIGHT);
        } else {
            lyt_last_message_room.setGravity(Gravity.LEFT);
        }
        lyt_last_message_room.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_338 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lyt_last_message_room.setLayoutParams(layout_338);

        LinearLayout lyt_last_message = new LinearLayout(G.context);
        if (HelperCalander.isPersianUnicode) {
            lyt_last_message.setGravity(Gravity.RIGHT);
        } else {
            lyt_last_message.setGravity(Gravity.LEFT);
        }
        lyt_last_message.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_3382 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lyt_last_message.setLayoutParams(layout_3382);


        EmojiTextViewE cs_txt_last_message_sender = new EmojiTextViewE(G.context);
        cs_txt_last_message_sender.setId(R.id.cs_txt_last_message_sender);
        cs_txt_last_message_sender.setGravity(Gravity.CENTER_VERTICAL);
        cs_txt_last_message_sender.setSingleLine(true);
        cs_txt_last_message_sender.setText("test");
        setTypeFace(cs_txt_last_message_sender);
        cs_txt_last_message_sender.setTextColor(G.context.getResources().getColor(R.color.green));
        setTextSize(cs_txt_last_message_sender, R.dimen.dp13);
        cs_txt_last_message_sender.setEmojiSize(i_Dp(R.dimen.dp14));
        LinearLayout.LayoutParams layout_972 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_972.leftMargin = dpToPixel(2);
        layout_972.rightMargin = dpToPixel(2);
        cs_txt_last_message_sender.setLayoutParams(layout_972);
        lyt_last_message.addView(cs_txt_last_message_sender);

        EmojiTextViewE cs_txt_last_message = new EmojiTextViewE(G.context);
        cs_txt_last_message.setId(R.id.cs_txt_last_message);
        cs_txt_last_message.setGravity(Gravity.CENTER_VERTICAL);
        cs_txt_last_message.setEllipsize(TextUtils.TruncateAt.END);
        cs_txt_last_message.setSingleLine(true);

        //  cs_txt_last_message.setMaxWidth(i_Dp(R.dimen.dp180));
        cs_txt_last_message.setText("LAST MESSAGE");
        setTypeFace(cs_txt_last_message);
        cs_txt_last_message.setTextColor(Color.parseColor("#FF616161"));
        if (G.twoPaneMode) {
            setTextSize(cs_txt_last_message, R.dimen.dp16);
        } else {
            setTextSize(cs_txt_last_message, R.dimen.dp12);
        }
        cs_txt_last_message.setEmojiSize(i_Dp(R.dimen.dp13));
        LinearLayout.LayoutParams layout_1151 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (HelperCalander.isPersianUnicode) {
            layout_1151.leftMargin = dpToPixel(3);
        } else {
            layout_1151.rightMargin = dpToPixel(3);
        }

        layout_1151.bottomMargin = dpToPixel(2);
        cs_txt_last_message.setLayoutParams(layout_1151);
        lyt_last_message.addView(cs_txt_last_message);

        EmojiTextViewE cs_txt_last_message_file_text = new EmojiTextViewE(G.context);
        cs_txt_last_message_file_text.setId(R.id.cs_txt_last_message_file_text);
        cs_txt_last_message_file_text.setGravity(Gravity.CENTER_VERTICAL);
        cs_txt_last_message_file_text.setEllipsize(TextUtils.TruncateAt.END);
        cs_txt_last_message_file_text.setSingleLine(true);
        // cs_txt_last_message_file_text.setMaxWidth(i_Dp(R.dimen.dp180));
        cs_txt_last_message_file_text.setText("");
        setTypeFace(cs_txt_last_message_file_text);
        if (G.isDarkTheme) {
            cs_txt_last_message_file_text.setTextColor(Color.parseColor(G.textSubTheme));
        } else {
            cs_txt_last_message_file_text.setTextColor(Color.parseColor("#FF616161"));
        }
        setTextSize(cs_txt_last_message_file_text, R.dimen.dp12);
        cs_txt_last_message_file_text.setEmojiSize(i_Dp(R.dimen.dp13));
        LinearLayout.LayoutParams layout_1151_file_text = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_1151_file_text.bottomMargin = dpToPixel(2);

        if (HelperCalander.isPersianUnicode) {
            layout_1151_file_text.leftMargin = dpToPixel(4);
        } else {
            layout_1151_file_text.rightMargin = dpToPixel(4);
        }

        cs_txt_last_message_file_text.setLayoutParams(layout_1151_file_text);
        lyt_last_message.addView(cs_txt_last_message_file_text);

        lyt_last_message_room.addView(lyt_last_message);

        linearLayout_353.addView(lyt_last_message_room);
        linearLayout_938.addView(linearLayout_353);

        //LinearLayout linearLayout_604 = new LinearLayout(G.context);
        //linearLayout_604.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        //linearLayout_604.setMinimumWidth(i_Dp(R.dimen.dp28));
        //linearLayout_604.setOrientation(VERTICAL);
        //linearLayout_604.setPadding(0, i_Dp(R.dimen.dp10), 0, 0);
        //LinearLayout.LayoutParams layout_468 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //linearLayout_604.setLayoutParams(layout_468);

        LinearLayout linearLayout_620 = new LinearLayout(G.context);
        linearLayout_620.setOrientation(HORIZONTAL);
        linearLayout_620.setPadding(i_Dp(R.dimen.dp8), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp8), 0);
        LinearLayout.LayoutParams layout_800 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (HelperCalander.isPersianUnicode) {
            layout_800.gravity = Gravity.LEFT;
        } else {
            layout_800.gravity = Gravity.RIGHT;
        }


        linearLayout_620.setLayoutParams(layout_800);

        MaterialDesignTextView cs_txt_mute = new MaterialDesignTextView(G.context);
        cs_txt_mute.setId(R.id.cs_txt_mute);
        cs_txt_mute.setText(G.fragmentActivity.getResources().getString(R.string.md_muted));
        cs_txt_mute.setTextColor(G.context.getResources().getColor(R.color.grayNew));
        setTextSize(cs_txt_mute, R.dimen.dp13);
        LinearLayout.LayoutParams layout_152s = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_152s.leftMargin = i_Dp(R.dimen.dp4);
        layout_152s.rightMargin = i_Dp(R.dimen.dp4);

        cs_txt_mute.setLayoutParams(layout_152s);
        linearLayout_620.addView(cs_txt_mute);

        ImageView cslr_txt_tic = new ImageView(G.context);
        cslr_txt_tic.setId(R.id.cslr_txt_tic);
        cslr_txt_tic.setColorFilter(Color.parseColor(G.tintImage), PorterDuff.Mode.SRC_IN);
        LinearLayout.LayoutParams layout_516 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp16), i_Dp(R.dimen.dp16));

        if (HelperCalander.isPersianUnicode) {
            layout_516.leftMargin = i_Dp(R.dimen.dp4);
        } else {
            layout_516.rightMargin = i_Dp(R.dimen.dp4);
        }
        layout_516.rightMargin = i_Dp(R.dimen.dp4);
        cslr_txt_tic.setLayoutParams(layout_516);
        linearLayout_620.addView(cslr_txt_tic);

        TextView cs_txt_contact_time = new TextView(G.context);
        cs_txt_contact_time.setId(R.id.cs_txt_contact_time);
        cs_txt_contact_time.setSingleLine(true);
        cs_txt_contact_time.setText("time");
        cs_txt_contact_time.setTextColor(G.context.getResources().getColor(R.color.gray));
        setTextSize(cs_txt_contact_time, R.dimen.dp12);
        LinearLayout.LayoutParams layout_574 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_574.gravity = Gravity.CENTER;
        setTypeFace(cs_txt_contact_time);
        cs_txt_contact_time.setLayoutParams(layout_574);
        linearLayout_620.addView(cs_txt_contact_time);

        linearLayout_922.addView(linearLayout_620);

        TextView cs_txt_unread_message = new TextView(G.context);
        cs_txt_unread_message.setId(R.id.cs_txt_unread_message);
        cs_txt_unread_message.setBackgroundResource(R.drawable.rect_oval_red);
        cs_txt_unread_message.setGravity(CENTER);
        cs_txt_unread_message.setMinimumHeight(i_Dp(R.dimen.dp16));
        cs_txt_unread_message.setMinimumWidth(i_Dp(R.dimen.dp24));
        cs_txt_unread_message.setSingleLine(true);
        setTypeFace(cs_txt_unread_message);
        cs_txt_unread_message.setText("1");
        cs_txt_unread_message.setTextColor(Color.WHITE);
        setTextSize(cs_txt_unread_message, R.dimen.dp10);
        LinearLayout.LayoutParams layout_79 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_79.gravity = Gravity.END | Gravity.CENTER;
        cs_txt_unread_message.setLayoutParams(layout_79);
        lyt_last_message_room.addView(cs_txt_unread_message);

        MaterialDesignTextView cs_txt_pinned_message = new MaterialDesignTextView(G.context);
        cs_txt_pinned_message.setId(R.id.cs_txt_pinned_message);
        cs_txt_pinned_message.setGravity(CENTER);
        cs_txt_pinned_message.setText(G.fragmentActivity.getResources().getString(R.string.md_circlePin));
        cs_txt_pinned_message.setTextColor(Color.parseColor(G.textSubTheme));
        cs_txt_pinned_message.setTextSize(i_Dp(R.dimen.dp20));
        setTextSize(cs_txt_pinned_message, R.dimen.dp20);
        LinearLayout.LayoutParams layout_175 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_175.gravity = Gravity.END | Gravity.CENTER;

        layout_175.leftMargin = i_Dp(R.dimen.dp6);
        layout_175.rightMargin = i_Dp(R.dimen.dp6);

        cs_txt_pinned_message.setLayoutParams(layout_175);

        lyt_last_message_room.addView(cs_txt_pinned_message);
        //  linearLayout_938.addView(linearLayout_604);
        linearLayout_849.addView(linearLayout_938);

        View textView_186 = new View(G.context);
        textView_186.setBackgroundColor(Color.parseColor("#52afafaf"));
        LinearLayout.LayoutParams layout_552 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        textView_186.setLayoutParams(layout_552);
        linearLayout_849.addView(textView_186);
        root_chat_sub_layout.addView(linearLayout_849);

        return root_chat_sub_layout;
    }

    public static View getViewItemCall() {

        LinearLayout linearLayout_205 = new LinearLayout(G.context);
        linearLayout_205.setId(R.id.mainContainer);
        LinearLayout.LayoutParams layout_218 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_205.setLayoutParams(layout_218);
        linearLayout_205.setBackgroundColor(Color.parseColor(G.backgroundTheme));

        CircleImageView fcsl_imv_picture = new CircleImageView(G.context);
        fcsl_imv_picture.setId(R.id.fcsl_imv_picture);
        LinearLayout.LayoutParams layout_856 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp48), i_Dp(R.dimen.dp48));
        layout_856.setMargins(i_Dp(R.dimen.dp6), i_Dp(R.dimen.dp6), i_Dp(R.dimen.dp6), i_Dp(R.dimen.dp6));
        layout_856.gravity = Gravity.CENTER;

        fcsl_imv_picture.setLayoutParams(layout_856);
        linearLayout_205.addView(fcsl_imv_picture);

        LinearLayout linearLayout_71 = new LinearLayout(G.context);
        linearLayout_71.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_794 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp68));
        linearLayout_71.setLayoutParams(layout_794);

        LinearLayout linearLayout_470 = new LinearLayout(G.context);
        linearLayout_470.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_822 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        linearLayout_470.setPadding(0, i_Dp(R.dimen.dp12), 0, 0);
        linearLayout_470.setLayoutParams(layout_822);

        LinearLayout linearLayout_983 = new LinearLayout(G.context);
        linearLayout_983.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_313 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layout_313.leftMargin = i_Dp(R.dimen.dp6);
        linearLayout_983.setLayoutParams(layout_313);

        EmojiTextViewE fcsl_txt_name = new EmojiTextViewE(G.context);
        fcsl_txt_name.setId(R.id.fcsl_txt_name);
        fcsl_txt_name.setPadding(0, 0, 0, dpToPixel(1));
        fcsl_txt_name.setText("Name");
        fcsl_txt_name.setSingleLine(true);
        if (G.isDarkTheme) {
            fcsl_txt_name.setTextColor(Color.parseColor(G.textTitleTheme));
        } else {
            fcsl_txt_name.setTextColor(G.context.getResources().getColor(R.color.black90));
        }

        setTextSize(fcsl_txt_name, R.dimen.dp15);
        fcsl_txt_name.setTypeface(G.typeface_IRANSansMobile_Bold);
        LinearLayout.LayoutParams layout_415 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_415.gravity = Gravity.START;
        fcsl_txt_name.setLayoutParams(layout_415);
        linearLayout_983.addView(fcsl_txt_name);

        LinearLayout linearLayout_976 = new LinearLayout(G.context);
        linearLayout_976.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_106 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_976.setLayoutParams(layout_106);

        TextView fcsl_txt_time_info = new TextView(G.context);
        fcsl_txt_time_info.setId(R.id.fcsl_txt_time_info);
        fcsl_txt_time_info.setGravity(Gravity.START);
        fcsl_txt_time_info.setSingleLine(true);
        fcsl_txt_time_info.setText("(4) 9:24 am");
        if (G.isDarkTheme) {
            fcsl_txt_time_info.setTextColor(Color.parseColor(G.textSubTheme));
        } else {
            fcsl_txt_time_info.setTextColor(G.context.getResources().getColor(R.color.gray_5c));
        }
        setTextSize(fcsl_txt_time_info, R.dimen.dp12);
        fcsl_txt_time_info.setTypeface(G.typeface_IRANSansMobile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fcsl_txt_time_info.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        }
        LinearLayout.LayoutParams layout_959 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        fcsl_txt_time_info.setLayoutParams(layout_959);
        linearLayout_976.addView(fcsl_txt_time_info);
        linearLayout_983.addView(linearLayout_976);
        linearLayout_470.addView(linearLayout_983);

        LinearLayout linearLayout_202 = new LinearLayout(G.context);
        linearLayout_202.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_803 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_803.rightMargin = i_Dp(R.dimen.dp8);
        layout_803.leftMargin = i_Dp(R.dimen.dp8);
        linearLayout_202.setLayoutParams(layout_803);

        MaterialDesignTextView fcsl_txt_icon = new MaterialDesignTextView(G.context);
        fcsl_txt_icon.setId(R.id.fcsl_txt_icon);
        fcsl_txt_icon.setText(G.fragmentActivity.getResources().getString(R.string.md_call_made));
        fcsl_txt_icon.setTextColor(G.context.getResources().getColor(R.color.green));
        setTextSize(fcsl_txt_icon, R.dimen.dp18);
        LinearLayout.LayoutParams layout_178 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_178.gravity = Gravity.END;
        fcsl_txt_icon.setLayoutParams(layout_178);
        linearLayout_202.addView(fcsl_txt_icon);

        TextView fcsl_txt_dureation_time = new TextView(G.context);
        fcsl_txt_dureation_time.setId(R.id.fcsl_txt_dureation_time);
        fcsl_txt_dureation_time.setText("2:24");
        fcsl_txt_dureation_time.setTextColor(G.context.getResources().getColor(R.color.btn_start_page5));
        setTextSize(fcsl_txt_dureation_time, R.dimen.dp12);
        fcsl_txt_dureation_time.setTypeface(G.typeface_IRANSansMobile);
        LinearLayout.LayoutParams layout_483 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fcsl_txt_dureation_time.setLayoutParams(layout_483);
        linearLayout_202.addView(fcsl_txt_dureation_time);
        linearLayout_470.addView(linearLayout_202);
        linearLayout_71.addView(linearLayout_470);

        View textView_316 = new View(G.context);
        textView_316.setBackgroundColor(G.context.getResources().getColor(R.color.gray_3c));
        LinearLayout.LayoutParams layout_241 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        textView_316.setLayoutParams(layout_241);
        linearLayout_71.addView(textView_316);
        linearLayout_205.addView(linearLayout_71);

        return linearLayout_205;
    }

    public static View getViewRegisteredContacts() {

        LinearLayout linearLayout_main = new LinearLayout(G.context);
        linearLayout_main.setBackgroundColor(Color.parseColor(G.backgroundTheme));
        linearLayout_main.setOrientation(VERTICAL);

        LinearLayout.LayoutParams layout_main = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_main.setLayoutParams(layout_main);

        SwipeLayout swipeRevealLayout = new SwipeLayout(G.context);
        swipeRevealLayout.setId(R.id.swipeRevealLayout);

        ViewGroup.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        swipeRevealLayout.setLayoutParams(layoutParams);

        RelativeLayout LinearLayout = new RelativeLayout(G.context);
        LinearLayout.setBackgroundColor(G.context.getResources().getColor(R.color.red_swipe));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            LinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.setLayoutParams(layoutParams1);

        TextView textView = new TextView(G.context);
        textView.setText(G.context.getResources().getString(R.string.to_delete_contact));
        textView.setGravity(Gravity.CENTER);
        setTypeFace(textView);
        textView.setTextColor(G.context.getResources().getColor(R.color.white));

        ViewGroup.LayoutParams layoutParams2 = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams2);

        MaterialDesignTextView fcsl_txt_icon = new MaterialDesignTextView(G.context);
        fcsl_txt_icon.setGravity(CENTER_VERTICAL);
        fcsl_txt_icon.setText(G.fragmentActivity.getResources().getString(R.string.md_rubbish_delete_file));
        fcsl_txt_icon.setTextColor(G.context.getResources().getColor(R.color.white));
        setTextSize(fcsl_txt_icon, R.dimen.dp22);
        LinearLayout.LayoutParams layout_178 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp52), ViewGroup.LayoutParams.MATCH_PARENT);
        layout_178.gravity = Gravity.LEFT;
        layout_178.leftMargin = i_Dp(R.dimen.dp32);
        fcsl_txt_icon.setLayoutParams(layout_178);

        LinearLayout.addView(fcsl_txt_icon);
        LinearLayout.addView(textView);
        swipeRevealLayout.addView(LinearLayout);

        LinearLayout linearLayout_578 = new LinearLayout(G.context);
//        linearLayout_578.setId(R.id.rootRegisterContact);
        linearLayout_578.setId(R.id.mainContainer);
        linearLayout_578.setOrientation(VERTICAL);
        linearLayout_578.setBackgroundColor(Color.parseColor(G.backgroundTheme));
        if (HelperCalander.isPersianUnicode) {
            linearLayout_578.setPadding(i_Dp(R.dimen.dp20), 0, i_Dp(R.dimen.dp20), 0);
        } else {
            linearLayout_578.setPadding(i_Dp(R.dimen.dp52), 0, i_Dp(R.dimen.dp20), 0);
        }

        LinearLayout.LayoutParams layout_842 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout_578.setLayoutParams(layout_842);

        TextView topLine = new TextView(G.context);
        topLine.setId(R.id.topLine);
        topLine.setBackgroundColor(G.context.getResources().getColor(R.color.gray));
        LinearLayout.LayoutParams layout_323 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layout_323.bottomMargin = i_Dp(R.dimen.dp6);

        if (HelperCalander.isPersianUnicode) {
            layout_323.leftMargin = i_Dp(R.dimen.dp20);
            layout_323.rightMargin = i_Dp(R.dimen.dp8);
        } else {
            layout_323.leftMargin = i_Dp(R.dimen.dp8);
            layout_323.rightMargin = i_Dp(R.dimen.dp20);
        }

        topLine.setLayoutParams(layout_323);


        LinearLayout linearLayout_823 = new LinearLayout(G.context);
        linearLayout_823.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_692 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_692.topMargin = i_Dp(R.dimen.dp16);
        linearLayout_823.setLayoutParams(layout_692);

        RelativeLayout layoutCheckBoxAndImage = new RelativeLayout(G.context);
        RelativeLayout.LayoutParams layoutCheckBoxAndImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutCheckBoxAndImage.setLayoutParams(layoutCheckBoxAndImageParams);


        CircleImageView imageView = new CircleImageView(G.context);
        imageView.setId(R.id.imageView);
        LinearLayout.LayoutParams layout_54 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp48), i_Dp(R.dimen.dp48));
        imageView.setLayoutParams(layout_54);

        AnimateCheckBox animateCheckBox = new AnimateCheckBox(G.context);
        animateCheckBox.setId(R.id.animateCheckBoxContact);
        animateCheckBox.setVisibility(View.GONE);
        animateCheckBox.setLineColor(R.color.white);
        LinearLayout.LayoutParams animateCheckBoxParams = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp24), i_Dp(R.dimen.dp24));
        animateCheckBox.setLayoutParams(animateCheckBoxParams);
        animateCheckBoxParams.gravity = Gravity.BOTTOM;
        animateCheckBoxParams.gravity = Gravity.RIGHT;
        animateCheckBoxParams.gravity = Gravity.END;


        layoutCheckBoxAndImage.addView(imageView);
        layoutCheckBoxAndImage.addView(animateCheckBox);

        linearLayout_823.addView(layoutCheckBoxAndImage);

        LinearLayout linearLayout_673 = new LinearLayout(G.context);
        linearLayout_673.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layout_445 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (HelperCalander.isPersianUnicode) {
            layout_445.rightMargin = i_Dp(R.dimen.dp8);
        } else {
            layout_445.leftMargin = i_Dp(R.dimen.dp8);
        }
        linearLayout_673.setLayoutParams(layout_445);

        TextView title = new TextView(G.context);
        title.setId(R.id.title);
        title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        title.setSingleLine(true);
        title.setTextColor(Color.parseColor(G.textTitleTheme));
        setTextSize(title, R.dimen.dp16);
        setTypeFace(title);
        LinearLayout.LayoutParams layout_949 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1);
        title.setLayoutParams(layout_949);
        linearLayout_673.addView(title);

        TextView subtitle = new TextView(G.context);
        subtitle.setId(R.id.subtitle);
        subtitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        subtitle.setText(G.fragmentActivity.getResources().getString(R.string.last_seen_recently));
        setTextSize(subtitle, R.dimen.dp14);
        subtitle.setSingleLine(true);
        setTypeFace(subtitle);
        subtitle.setTextColor(Color.parseColor(G.textSubTheme));
        LinearLayout.LayoutParams layout_488 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1);
        subtitle.setLayoutParams(layout_488);
        linearLayout_673.addView(subtitle);
        linearLayout_823.addView(linearLayout_673);
        linearLayout_578.addView(linearLayout_823);

        swipeRevealLayout.addView(linearLayout_578);
        linearLayout_main.addView(topLine);
        linearLayout_main.addView(swipeRevealLayout);


        return linearLayout_main;
    }
}
