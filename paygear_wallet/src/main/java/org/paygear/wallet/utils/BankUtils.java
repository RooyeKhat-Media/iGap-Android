package org.paygear.wallet.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import org.paygear.wallet.R;

import ir.radsense.raadcore.utils.RaadCommonUtils;


/**
 * Created by Ghaisar on 5/1/2016 AD.
 */
public class BankUtils {

    private int code;
    private String name;
    private String cardNumber;
    private int logoRes;
    private Drawable backgroundDrawable;
    private int color;

    private static int dp10;

    private BankUtils() {
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getLogoRes() {
        return logoRes;
    }

    public int getColor() {
        return color;
    }

    public Drawable getBackgroundDrawable() {
        return backgroundDrawable;
    }


    private static Drawable getBack(int color) {
        /*float radius = dp10;
        float[] outerR = {radius, radius, radius, radius, 0, 0, 0, 0};
        ShapeDrawable backShape = new ShapeDrawable(new RoundRectShape(outerR, null, null));
        backShape.getPaint().setColor(color);*/
        //backShape.getPaint().setShadowLayer(2, 5, 5, 0xFF000000);
        //backShape.setPadding(15, 15, 15, 15);
        return null;
    }

    public static BankUtils getBank(Context context, String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 6) {
            return getBank(context, 0);
        }

        String bankCodeNumber = cardNumber.substring(0, 6);
        switch (bankCodeNumber) {
            case "622106": //parsian
                return getBank(context, 1);
            case "621986": //saman
                return getBank(context, 2);
            case "610433": //mellat
                return getBank(context, 3);
            case "627412": //eghtesad novin
                return getBank(context, 4);
            case "502229": //pasargad
            case "639347":
                return getBank(context, 5);
            case "627488": //karafarin
                return getBank(context, 6);
            case "639607": //sarmayeh
                return getBank(context, 7);
            case "603799": //melli
                return getBank(context, 8);
            case "589210": //sepah
                return getBank(context, 9);
            case "502938": //dey
                return getBank(context, 10);
            case "627353": //tejarat
                return getBank(context, 11);
            case "589463": //refah
                return getBank(context, 12);
            case "603769": //saderat
                return getBank(context, 13);
            case "628023": //maskan
                return getBank(context, 14);
            case "502806": //shahr
                return getBank(context, 15);
            case "639346": //sina
                return getBank(context, 16);
            case "603770": //keshavarzi
                return getBank(context, 17);
            case "636795": //markazi
                return getBank(context, 18);
            case "505416": //gardeshgari
                return getBank(context, 19);
            case "627760": //post bank
                return getBank(context, 20);
            case "627381": //ansar
                return getBank(context, 21);
            case "505785": //iran zamin
                return getBank(context, 22);
            case "636214": //ayandeh
                return getBank(context, 23);
            case "504172": //resalat
                return getBank(context, 24);
            case "502908": //tosee taavon
                return getBank(context, 25);
            case "627648": //tosee saderat
            case "207177":
                return getBank(context, 26);
            case "636949": //hekmat iranian
                return getBank(context, 27);
            case "627961": //sanat o madan
                return getBank(context, 28);
            case "639599": //ghavamin
                return getBank(context, 29);
            case "606373": //mehr iran
                return getBank(context, 30);
            case "639370": //mehr eghtesad
                return getBank(context, 31);
            case "505801": //etebari kosar
                return getBank(context, 32);
            case "628157": //etebari tosee
                return getBank(context, 33);
            case "606256": //etebari asgarieh
                return getBank(context, 34);
            default:
                return getBank(context, 0);
        }

    }

    public static BankUtils getBank(Context context, int code) {
        BankUtils bank;

        if (dp10 == 0)
            dp10 = RaadCommonUtils.getPx(10, context);

        switch (code) {
            case 1: //parsian
                bank = new BankUtils();
                bank.code = 1;
                bank.name = context.getString(R.string.bank_parsian);
                bank.logoRes = R.drawable.bank_logo_parsian;
                bank.color = 0xFF92000e;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 2: //saman
                bank = new BankUtils();
                bank.code = 2;
                bank.name = context.getString(R.string.bank_saman);
                bank.logoRes = R.drawable.bank_logo_saman;
                bank.color = 0xFF2ab1ea;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 3: //mellat
                bank = new BankUtils();
                bank.code = 3;
                bank.name = context.getString(R.string.bank_mellat);
                bank.logoRes = R.drawable.bank_logo_mellat;
                bank.color = 0xFFa7002f;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 4: //eghtesad novin
                bank = new BankUtils();
                bank.code = 4;
                bank.name = context.getString(R.string.bank_eghtesad_novin);
                bank.logoRes = R.drawable.bank_logo_eghtesad_novin;
                bank.color = 0xFFBEA4C7;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 5: //pasargad
                bank = new BankUtils();
                bank.code = 5;
                bank.name = context.getString(R.string.bank_pasargad);
                bank.logoRes = R.drawable.bank_logo_pasargad;
                bank.color = 0xFF797676;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 6: //karafarin
                bank = new BankUtils();
                bank.code = 6;
                bank.name = context.getString(R.string.bank_karafarin);
                bank.logoRes = R.drawable.bank_logo_karafarin;
                bank.color = 0xFF90B39D;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 7: //sarmayeh
                bank = new BankUtils();
                bank.code = 7;
                bank.name = context.getString(R.string.bank_sarmayeh);
                bank.logoRes = R.drawable.bank_logo_sarmayeh;
                bank.color = 0xFFA3AEBE;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 8: //melli
                bank = new BankUtils();
                bank.code = 8;
                bank.name = context.getString(R.string.bank_melli);
                bank.logoRes = R.drawable.bank_logo_melli;
                bank.color = 0xFF8BA3DC;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 9: //sepah
                bank = new BankUtils();
                bank.code = 9;
                bank.name = context.getString(R.string.bank_sepah);
                bank.logoRes = R.drawable.bank_logo_sepah;
                bank.color = 0xFF94B1D6;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 10: //dey
                bank = new BankUtils();
                bank.code = 10;
                bank.name = context.getString(R.string.bank_dey);
                bank.logoRes = R.drawable.bank_logo_dey;
                bank.color = 0xFF74B1BF;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 11: //tejarat
                bank = new BankUtils();
                bank.code = 11;
                bank.name = context.getString(R.string.bank_tejarat);
                bank.logoRes = R.drawable.bank_logo_tejarat;
                bank.color = 0xFF94BEE2;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 12: //refah
                bank = new BankUtils();
                bank.code = 12;
                bank.name = context.getString(R.string.bank_refah);
                bank.logoRes = R.drawable.bank_logo_refah;
                bank.color = 0xFF959BB4;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 13: //saderat
                bank = new BankUtils();
                bank.code = 13;
                bank.name = context.getString(R.string.bank_saderat);
                bank.logoRes = R.drawable.bank_logo_saderat;
                bank.color = 0xFF93BAD6;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 14: //maskan
                bank = new BankUtils();
                bank.code = 14;
                bank.name = context.getString(R.string.bank_maskan);
                bank.logoRes = R.drawable.bank_logo_maskan;
                bank.color = 0xFFDAB68C;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 15: //shahr
                bank = new BankUtils();
                bank.code = 15;
                bank.name = context.getString(R.string.bank_shahr);
                bank.logoRes = R.drawable.bank_logo_shahr;
                bank.color = 0xFFE19196;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 16: //sina
                bank = new BankUtils();
                bank.code = 16;
                bank.name = context.getString(R.string.bank_sina);
                bank.logoRes = R.drawable.bank_logo_sina;
                bank.color = 0xFF94BEE2;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 17: //keshavarzi
                bank = new BankUtils();
                bank.code = 17;
                bank.name = context.getString(R.string.bank_keshavarzi);
                bank.logoRes = R.drawable.bank_logo_keshavarzi;
                bank.color = 0xFFD1C78A;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 18: //markazi
                bank = new BankUtils();
                bank.code = 18;
                bank.name = context.getString(R.string.bank_markazi);
                bank.logoRes = R.drawable.bank_logo_markazi;
                bank.color = 0xFF97A1D6;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 19: //gardeshgari
                bank = new BankUtils();
                bank.code = 19;
                bank.name = context.getString(R.string.bank_gardeshgari);
                bank.logoRes = R.drawable.bank_logo_gardeshgari;
                bank.color = 0xFFE6767D;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 20: //post bank
                bank = new BankUtils();
                bank.code = 20;
                bank.name = context.getString(R.string.bank_post_bank);
                bank.logoRes = R.drawable.bank_logo_post;
                bank.color = 0xFF81B67D;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 21: //ansar
                bank = new BankUtils();
                bank.code = 21;
                bank.name = context.getString(R.string.bank_ansar);
                bank.logoRes = R.drawable.bank_logo_ansar;
                bank.color = 0xFFBFB174;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 22: //iran zamin
                bank = new BankUtils();
                bank.code = 22;
                bank.name = context.getString(R.string.bank_iran_zamin);
                bank.logoRes = R.drawable.bank_logo_iran_zamin;
                bank.color = 0xFFCAA4DC;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 23: //ayandeh
                bank = new BankUtils();
                bank.code = 23;
                bank.name = context.getString(R.string.bank_ayandeh);
                bank.logoRes = R.drawable.bank_logo_ayandeh;
                bank.color = 0xFFBF9D74;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 24: //resalat
                bank = new BankUtils();
                bank.code = 24;
                bank.name = context.getString(R.string.bank_resalat);
                bank.logoRes = R.drawable.bank_logo_resalat;
                bank.color = 0xFF97C0CA;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 25: //tosee taavon
                bank = new BankUtils();
                bank.code = 25;
                bank.name = context.getString(R.string.bank_tosee_taavon);
                bank.logoRes = R.drawable.bank_logo_tosee_taavon;
                bank.color = 0xFF99C4C9;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 26: //tosee saderat
                bank = new BankUtils();
                bank.code = 26;
                bank.name = context.getString(R.string.bank_tosee_saderat);
                bank.logoRes = R.drawable.bank_logo_tosee_saderat;
                bank.color = 0xFF92B691;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 27: //hekmat iranian
                bank = new BankUtils();
                bank.code = 27;
                bank.name = context.getString(R.string.bank_hekmat_iranian);
                bank.logoRes = R.drawable.bank_logo_hekmat_iranian;
                bank.color = 0xFF89A3E9;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 28: //sanat o madan
                bank = new BankUtils();
                bank.code = 28;
                bank.name = context.getString(R.string.bank_sanato_madan);
                bank.logoRes = R.drawable.bank_logo_sanato_madan;
                bank.color = 0xFF95B0D9;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 29: //ghavamin
                bank = new BankUtils();
                bank.code = 29;
                bank.name = context.getString(R.string.bank_ghavamin);
                bank.logoRes = R.drawable.bank_logo_ghavamin;
                bank.color = 0xFF74A776;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 30: //mehr iran
                bank = new BankUtils();
                bank.code = 30;
                bank.name = context.getString(R.string.bank_mehr_iran);
                bank.logoRes = R.drawable.bank_logo_mehr_iran;
                bank.color = 0xFFA4C79D;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 31: //mehr eghtesad
                bank = new BankUtils();
                bank.code = 31;
                bank.name = context.getString(R.string.bank_mehr_eghtesad);
                bank.logoRes = R.drawable.bank_logo_mehr_eghtesad;
                bank.color = 0xFFA4C79D;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 32: //etebari kosar
                bank = new BankUtils();
                bank.code = 32;
                bank.name = context.getString(R.string.bank_etebari_kosar);
                bank.logoRes = R.drawable.bank_logo_etebari_kosar;
                bank.color = 0xFFCC8581;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 33: //etebari tosee
                bank = new BankUtils();
                bank.code = 33;
                bank.name = context.getString(R.string.bank_etebari_tosee);
                bank.logoRes = R.drawable.bank_logo_etebari_tosee;
                bank.color = 0xFFB36C70;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 34: //etebari asgarieh
                bank = new BankUtils();
                bank.code = 34;
                bank.name = context.getString(R.string.bank_etebari_asgarieh);
                bank.logoRes = R.drawable.bank_logo_etebari_asgarieh;
                bank.color = 0xFF8998BF;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            case 69: //paygear
                bank = new BankUtils();
                bank.code = 69;
                bank.name = context.getString(R.string.paygear_card);
                bank.logoRes = 0;//R.drawable.paygear_logo_wide;
                bank.color = Color.BLACK;
                bank.backgroundDrawable = getBack(bank.color);
                break;
            default:
                bank = new BankUtils();
                bank.code = 0;
                bank.name = context.getString(R.string.card_bank);
                bank.logoRes = R.drawable.bank_logo_default;
                bank.color = 0xFF2ab1ea;
                bank.backgroundDrawable = getBack(bank.color);
                break;
        }



        return bank;
    }

}
