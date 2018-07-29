package org.paygear.wallet.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.paygear.wallet.R;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.utils.BankUtils;

import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;

public class BankCardView extends CardView {

    private ImageView mBackImage;
    private ImageView mLogoImage;
    private TextView mTitleText;
    private TextView mTopCardNumberText;
    private TextView mCardNumberText1;
    private TextView mCardNumberText2;
    private TextView mCardNumberText3;
    private TextView mCardNumberText4;

    private Card mCard;

    public BankCardView(Context context) {
        super(context);
        init();
    }

    public BankCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BankCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Context context = getContext();
        int dp8 = RaadCommonUtils.getPx(8, context);
        int dp16 = RaadCommonUtils.getPx(16, context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        setRadius(RaadCommonUtils.getPx(8, context));
        setPreventCornerOverlap(false);

        mBackImage = new ImageView(context);
        mBackImage.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        int dp1 = RaadCommonUtils.getPx(1, context);
        //mBackImage.setPadding(dp1, dp1, dp1, dp1);
        addView(mBackImage);

        View view = new View(context);
        mBackImage.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(view);
        ViewCompat.setBackground(view, RaadCommonUtils.getRectShape(Color.parseColor("#24000000"), dp8, dp1));

        LinearLayout logoLayout = new LinearLayout(context);
        logoLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams logoLayoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        logoLayoutParams.gravity = Gravity.RIGHT;
        logoLayout.setLayoutParams(logoLayoutParams);
        logoLayout.setPadding(dp16, dp8, dp16, dp8);
        logoLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        //logoLayout.setBackgroundColor(Color.argb(80, 255, 255, 255));
        addView(logoLayout);

        mTopCardNumberText = new TextView(context);
        LinearLayout.LayoutParams cardNumberTitleParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardNumberTitleParams.weight = 1;
        mTopCardNumberText.setLayoutParams(cardNumberTitleParams);
        //mTopCardNumberText.setGravity(Gravity.CENTER);
        mTopCardNumberText.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
        mTopCardNumberText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTopCardNumberText.setTypeface(Typefaces.get(context, Typefaces.IRAN_YEKAN_BOLD));
        //mTopCardNumberText.setPadding(dp8, 0, dp8, 0);
        logoLayout.addView(mTopCardNumberText);

        mTitleText = new TextView(context);
        mTitleText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //mTitleText.setGravity(Gravity.CENTER);
        mTitleText.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
        mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mTitleText.setTypeface(Typefaces.get(context, Typefaces.IRAN_YEKAN_BOLD));
        //mTitleText.setPadding(dp8, 0, dp8, 0);
        logoLayout.addView(mTitleText);

        mLogoImage = new ImageView(context);
        int dp56 = RaadCommonUtils.getPx(56, context);
        LinearLayout.LayoutParams bankLogoImageParams = new LinearLayout.LayoutParams(dp56, dp56);
        mLogoImage.setLayoutParams(bankLogoImageParams);
        logoLayout.addView(mLogoImage);
        //ViewCompat.setBackground(mLogoImage, RaadCommonUtils.getRectShape(context, R.color.bank_logo_back, 8, 0));


        LinearLayout cardNumberLayout = new LinearLayout(context);
        cardNumberLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams cardNumerLayoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cardNumerLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        cardNumberLayout.setLayoutParams(cardNumerLayoutParams);
        cardNumberLayout.setPadding(dp16, 0, dp16, 0);
        addView(cardNumberLayout);


        mCardNumberText1 = new TextView(context);
        LinearLayout.LayoutParams cardNum1Params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardNum1Params.weight = 1.0f;
        mCardNumberText1.setLayoutParams(cardNum1Params);
        mCardNumberText1.setGravity(Gravity.CENTER);
        mCardNumberText1.setTextColor(Color.BLACK);
        mCardNumberText1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mCardNumberText1.setTypeface(Typefaces.get(context, Typefaces.IRAN_YEKAN_BOLD));
        cardNumberLayout.addView(mCardNumberText1);

        mCardNumberText2 = new TextView(context);
        LinearLayout.LayoutParams cardNum2Params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardNum2Params.weight = 1.0f;
        mCardNumberText2.setLayoutParams(cardNum2Params);
        mCardNumberText2.setGravity(Gravity.CENTER);
        mCardNumberText2.setTextColor(Color.BLACK);
        mCardNumberText2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mCardNumberText2.setTypeface(Typefaces.get(context, Typefaces.IRAN_YEKAN_BOLD));
        cardNumberLayout.addView(mCardNumberText2);

        mCardNumberText3 = new TextView(context);
        LinearLayout.LayoutParams cardNum3Params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardNum3Params.weight = 1.0f;
        mCardNumberText3.setLayoutParams(cardNum3Params);
        mCardNumberText3.setGravity(Gravity.CENTER);
        mCardNumberText3.setTextColor(Color.BLACK);
        mCardNumberText3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mCardNumberText3.setTypeface(Typefaces.get(context, Typefaces.IRAN_YEKAN_BOLD));
        cardNumberLayout.addView(mCardNumberText3);

        mCardNumberText4 = new TextView(context);
        LinearLayout.LayoutParams cardNum4Params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardNum4Params.weight = 1.0f;
        mCardNumberText4.setLayoutParams(cardNum4Params);
        mCardNumberText4.setGravity(Gravity.CENTER);
        mCardNumberText4.setTextColor(Color.BLACK);
        mCardNumberText4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mCardNumberText4.setTypeface(Typefaces.get(context, Typefaces.IRAN_YEKAN_BOLD));
        cardNumberLayout.addView(mCardNumberText4);

    }

    public void setCard(Card card, boolean full) {
        Context context = getContext();
        mCard = card;
        BankUtils bankInfo = BankUtils.getBank(getContext(), card.bankCode);

        if (!TextUtils.isEmpty(mCard.backgroundImage)) {
            Picasso.with(context)
                    .load(RaadCommonUtils.getImageUrl(mCard.backgroundImage))
                    .fit()
                    .into(mBackImage);
        } else {
            Picasso.with(context)
                    .load(R.drawable.default_card_pattern)
                    .fit()
                    .into(mBackImage);
        }

        mTitleText.setText(bankInfo.getName());
        mLogoImage.setImageResource(bankInfo.getLogoRes());

        if (full) {
            mCardNumberText1.setText(mCard.cardNumber.substring(0, 4));
            mCardNumberText2.setText(mCard.cardNumber.substring(4, 8));
            mCardNumberText3.setText(mCard.cardNumber.substring(8, 12));
            mCardNumberText4.setText(mCard.cardNumber.substring(12, 16));
        } else {
            mTopCardNumberText.setText(card.cardNumber.length() == 16 ? "**** " + card.cardNumber.substring(12, 16) : card.cardNumber);
        }


        if (!TextUtils.isEmpty(card.textColor)) {
            int textColor = RaadCommonUtils.formatColor(mCard.textColor);
            mTitleText.setTextColor(textColor);
            mTopCardNumberText.setTextColor(textColor);
            mCardNumberText1.setTextColor(textColor);
            mCardNumberText2.setTextColor(textColor);
            mCardNumberText3.setTextColor(textColor);
            mCardNumberText4.setTextColor(textColor);
        }

    }

    public static int getDefaultCardHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int dp16 = RaadCommonUtils.getPx(16, context);
        int cardWidth = screenWidth - (dp16 * 2);
        return (int)(cardWidth / 1.7f);
    }


}
