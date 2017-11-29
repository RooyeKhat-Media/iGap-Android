/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentContactsProfile;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnClientCheckInviteLink;
import net.iGap.interfaces.OnClientJoinByInviteLink;
import net.iGap.interfaces.OnClientResolveUsername;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.SHP_SETTING;
import net.iGap.proto.ProtoClientResolveUsername;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestClientCheckInviteLink;
import net.iGap.request.RequestClientJoinByInviteLink;
import net.iGap.request.RequestClientResolveUsername;

import org.chromium.customtabsclient.CustomTabsActivityHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;

import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class HelperUrl {

    enum linkType {
        hash, atSighn, igapLink, igapResolve, webLink

    }

    public enum ChatEntry {
        chat, profile
    }

    public static int LinkColor = Color.GRAY;
    public static String igapSite2 = "igap.net/";
    public static MaterialDialog dialogWaiting;
    public static String igapResolve = "igap://resolve?";

    public static SpannableStringBuilder setUrlLink(String text, boolean withClickable, boolean withHash, String messageID, boolean withAtSign) {

        if (text == null) return null;

        if (text.trim().length() < 1) return null;

        SpannableStringBuilder strBuilder = new SpannableStringBuilder(text);

        if (withAtSign) strBuilder = analaysAtSign(strBuilder);

        if (withHash) strBuilder = analaysHash(strBuilder, messageID);

        String newText = text.toLowerCase();

        String[] list = newText.replace(System.getProperty("line.separator"), " ").split(" ");

        int count = 0;

        for (int i = 0; i < list.length; i++) {

            String str = list[i];

            if (str.contains(igapSite2)) {
                insertIgapLink(strBuilder, count, count + str.length());
            } else if (str.contains(igapResolve)) {
                insertIgapResolveLink(strBuilder, count, count + str.length());
            } else if (isTextLink(str)) {
                insertLinkSpan(strBuilder, count, count + str.length(), withClickable);
            }
            count += str.length() + 1;
        }

        return strBuilder;
    }

    private static boolean isTextLink(String text) {
        Pattern p = Pattern.compile("((http|https)\\:\\/\\/)?[a-zA-Z0-9\\.\\/\\?\\:@\\-_=#]+\\.([a-zA-Z0-9\\&\\.\\/\\?\\:@\\-_=#])*");
        Matcher m = p.matcher(text);
        if (m.find()) {
            String[] strings = new String[]{
                    "abogado", "ac", "academy", "accountants", "active", "actor", "ad", "adult", "ae", "aero", "af", "ag", "agency", "ai", "airforce", "al", "allfinanz", "alsace", "am", "amsterdam", "an", "android", "ao", "apartments", "aq", "aquarelle", "ar", "archi", "army", "arpa", "as", "asia", "associates", "at", "attorney", "au", "auction", "audio", "autos", "aw", "ax", "axa", "az", "ba",
                    "band", "bank", "bar", "barclaycard", "barclays", "bargains", "bayern", "bb", "bd", "be", "beer", "berlin", "best", "bf", "bg", "bh", "bi", "bid", "bike", "bingo", "bio", "biz", "bj", "black", "blackfriday", "bloomberg", "blue", "bm", "bmw", "bn", "bnpparibas", "bo", "boo", "boutique", "br", "brussels", "bs", "bt", "budapest", "build", "builders", "business", "buzz", "bv",
                    "bw", "by", "bz", "bzh", "ca", "cab", "cal", "camera", "camp", "cancerresearch", "canon", "capetown", "capital", "caravan", "cards", "care", "career", "careers", "cartier", "casa", "cash", "cat", "catering", "cc", "cd", "center", "ceo", "cern", "cf", "cg", "ch", "channel", "chat", "cheap", "christmas", "chrome", "church", "ci", "citic", "city", "ck", "cl", "claims", "cleaning",
                    "click", "clinic", "clothing", "club", "cm", "cn", "co", "coach", "codes", "coffee", "college", "cologne", "com", "community", "company", "computer", "condos", "construction", "consulting", "contractors", "cooking", "cool", "coop", "country", "cr", "credit", "creditcard", "cricket", "crs", "cruises", "cu", "cuisinella", "cv", "cw", "cx", "cy", "cymru", "cz", "dabur", "dad",
                    "dance", "dating", "day", "dclk", "de", "deals", "degree", "delivery", "democrat", "dental", "dentist", "desi", "design", "dev", "diamonds", "diet", "digital", "direct", "directory", "discount", "dj", "dk", "dm", "dnp", "do", "docs", "domains", "doosan", "durban", "dvag", "dz", "eat", "ec", "edu", "education", "ee", "eg", "email", "emerck", "energy", "engineer", "engineering",
                    "enterprises", "equipment", "er", "es", "esq", "estate", "et", "eu", "eurovision", "eus", "events", "everbank", "exchange", "expert", "exposed", "fail", "farm", "fashion", "feedback", "fi", "finance", "financial", "firmdale", "fish", "fishing", "fit", "fitness", "fj", "fk", "flights", "florist", "flowers", "flsmidth", "fly", "fm", "fo", "foo", "forsale", "foundation", "fr",
                    "frl", "frogans", "fund", "furniture", "futbol", "ga", "gal", "gallery", "garden", "gb", "gbiz", "gd", "ge", "gent", "gf", "gg", "ggee", "gh", "gi", "gift", "gifts", "gives", "gl", "glass", "gle", "global", "globo", "gm", "gmail", "gmo", "gmx", "gn", "goog", "google", "gop", "gov", "gp", "gq", "gr", "graphics", "gratis", "green", "gripe", "gs", "gt", "gu", "guide", "guitars",
                    "guru", "gw", "gy", "hamburg", "hangout", "haus", "healthcare", "help", "here", "hermes", "hiphop", "hiv", "hk", "hm", "hn", "holdings", "holiday", "homes", "horse", "host", "hosting", "house", "how", "hr", "ht", "hu", "ibm", "id", "ie", "ifm", "il", "im", "immo", "immobilien", "in", "industries", "info", "ing", "ink", "institute", "insure", "int", "international",
                    "investments", "io", "iq", "ir", "irish", "is", "it", "iwc", "jcb", "je", "jetzt", "jm", "jo", "jobs", "joburg", "jp", "juegos", "kaufen", "kddi", "ke", "kg", "kh", "ki", "kim", "kitchen", "kiwi", "km", "kn", "koeln", "kp", "kr", "krd", "kred", "kw", "ky", "kyoto", "kz", "la", "lacaixa", "land", "lat", "latrobe", "lawyer", "lb", "lc", "lds", "lease", "legal", "lgbt", "li",
                    "lidl", "life", "lighting", "limited", "limo", "link", "lk", "loans", "london", "lotte", "lotto", "lr", "ls", "lt", "ltda", "lu", "luxe", "luxury", "lv", "ly", "ma", "madrid", "maison", "management", "mango", "market", "marketing", "marriott", "mc", "md", "me", "media", "meet", "melbourne", "meme", "memorial", "menu", "mg", "mh", "miami", "mil", "mini", "mk", "ml", "mm", "mn",
                    "mo", "mobi", "moda", "moe", "monash", "money", "mormon", "mortgage", "moscow", "motorcycles", "mov", "mp", "mq", "mr", "ms", "mt", "mu", "museum", "mv", "mw", "mx", "my", "mz", "na", "nagoya", "name", "navy", "nc", "ne", "net", "network", "neustar", "new", "nexus", "nf", "ng", "ngo", "nhk", "ni", "nico", "ninja", "nl", "no", "np", "nr", "nra", "nrw", "ntt", "nu", "nyc", "nz",
                    "okinawa", "om", "one", "ong", "onl", "ooo", "org", "organic", "osaka", "otsuka", "ovh", "pa", "paris", "partners", "parts", "party", "pe", "pf", "pg", "ph", "pharmacy", "photo", "photography", "photos", "physio", "pics", "pictures", "pink", "pizza", "pk", "pl", "place", "plumbing", "pm", "pn", "pohl", "poker", "porn", "post", "pr", "praxi", "press", "pro", "prod",
                    "productions", "prof", "properties", "property", "ps", "pt", "pub", "pw", "py", "qa", "qpon", "quebec", "re", "realtor", "recipes", "red", "rehab", "reise", "reisen", "reit", "ren", "rentals", "repair", "report", "republican", "rest", "restaurant", "reviews", "rich", "rio", "rip", "ro", "rocks", "rodeo", "rs", "rsvp", "ru", "ruhr", "rw", "ryukyu", "sa", "saarland", "sale",
                    "samsung", "sarl", "saxo", "sb", "sc", "sca", "scb", "schmidt", "schule", "schwarz", "science", "scot", "sd", "se", "services", "sew", "sg", "sh", "shiksha", "shoes", "shriram", "si", "singles", "sj", "sk", "sky", "sl", "sm", "sn", "so", "social", "software", "sohu", "solar", "solutions", "soy", "space", "spiegel", "sr", "st", "style", "su", "supplies", "supply", "support",
                    "surf", "surgery", "suzuki", "sv", "sx", "sy", "sydney", "systems", "sz", "taipei", "tatar", "tattoo", "tax", "tc", "td", "technology", "tel", "temasek", "tennis", "tf", "tg", "th", "tienda", "tips", "tires", "tirol", "tj", "tk", "tl", "tm", "tn", "to", "today", "tokyo", "tools", "top", "toshiba", "town", "toys", "tp", "tr", "trade", "training", "travel", "trust", "tt", "tui",
                    "tv", "tw", "tz", "ua", "ug", "uk", "university", "uno", "uol", "us", "uy", "uz", "va", "vacations", "vc", "ve", "vegas", "ventures", "versicherung", "vet", "vg", "vi", "viajes", "video", "villas", "vision", "vlaanderen", "vn", "vodka", "vote", "voting", "voto", "voyage", "vu", "wales", "wang", "watch", "webcam", "website", "wed", "wedding", "wf", "whoswho", "wien", "wiki",
                    "williamhill", "wme", "work", "works", "world", "ws", "wtc", "wtf", "佛山", "集团", "在线", "한국", "ভারত", "八卦", "موقع", "公益", "公司", "移动", "我爱你", "москва", "қаз", "онлайн", "сайт", "срб", "淡马锡", "орг", "삼성", "சிங்கப்பூர்", "商标", "商店", "商城", "дети", "мкд", "中文网", "中信", "中国", "中國", "谷歌", "భారత్", "ලංකා", "ભારત", "भारत", "网店", "संगठन", "网络", "укр", "香港", "台湾", "台灣", "手机", "мон",
                    "الجزائر", "عمان", "ایران", "امارات", "بازار", "الاردن", "بھارت", "المغرب", "السعودية", "مليسيا", "شبكة", "გე", "机构", "组织机构", "ไทย", "سورية", "рус", "рф", "تونس", "みんな", "グーグル", "世界", "ਭਾਰਤ", "网址", "游戏", "vermögensberater", "vermögensberatung", "企业", "مصر", "قطر", "广东", "இலங்கை", "இந்தியா", "新加坡", "فلسطين", "政务", "xxx", "xyz", "yachts", "yandex", "ye", "yoga", "yokohama",
                    "youtube", "yt", "za", "zip", "zm", "zone", "zuerich", "zw"
            };

            List<String> urlList = new ArrayList<String>(Arrays.asList(strings));

            for (String matches : urlList) {

                if (text.contains("." + matches) && text.length() > matches.length() + 2) {
                    return true;
                }
            }
        }

        return false;
    }

    private static void insertLinkSpan(final SpannableStringBuilder strBuilder, final int start, final int end, final boolean withclickable) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                if (withclickable) {

                    boolean openLocalWebPage;
                    SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, G.context.MODE_PRIVATE);

                    int checkedInappBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 0);

                    if (checkedInappBrowser == 1) {
                        openLocalWebPage = true;
                    } else {
                        openLocalWebPage = false;
                    }

                    String url = strBuilder.toString().substring(start, end).trim();
                    url = url.replaceAll("[^\\x00-\\x7F]", "");

                    if (!url.startsWith("https://") && !url.startsWith("http://")) {
                        url = "http://" + url;
                    }
                    openBrowser(url);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void openBrowser(String url) {

        final CustomTabsHelperFragment mCustomTabsHelperFragment = CustomTabsHelperFragment.attachTo((FragmentActivity) G.currentActivity);

        int mColorPrimary = Color.parseColor(G.appBarColor);
        final Uri PROJECT_URI = Uri.parse(url);

        CustomTabsIntent mCustomTabsIntent = new CustomTabsIntent.Builder().enableUrlBarHiding().setToolbarColor(mColorPrimary).setShowTitle(true).build();

        mCustomTabsHelperFragment.setConnectionCallback(new CustomTabsActivityHelper.ConnectionCallback() {
            @Override
            public void onCustomTabsConnected() {
                mCustomTabsHelperFragment.mayLaunchUrl(PROJECT_URI, null, null);
            }

            @Override
            public void onCustomTabsDisconnected() {
            }
        });

        CustomTabsHelperFragment.open(G.currentActivity, mCustomTabsIntent, PROJECT_URI, new CustomTabsActivityHelper.CustomTabsFallback() {
            @Override
            public void openUri(Activity activity, Uri uri) {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void insertIgapLink(final SpannableStringBuilder strBuilder, final int start, final int end) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                String url = strBuilder.toString().substring(start, end);

                int index = url.lastIndexOf("/");
                if (index >= 0 && index < url.length() - 1) {
                    String token = url.substring(index + 1);

                    if (url.toLowerCase().contains("join")) {
                        checkAndJoinToRoom(token);
                    } else {
                        checkUsernameAndGoToRoom(token, ChatEntry.profile);
                    }
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void insertIgapResolveLink(final SpannableStringBuilder strBuilder, final int start, final int end) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                try {
                    String url = strBuilder.toString().substring(start, end);

                    Uri path = Uri.parse(url);

                    String domain = path.getQueryParameter("domain");

                    if (domain != null && domain.length() > 0) {
                        checkUsernameAndGoToRoom(domain, ChatEntry.profile);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //*********************************************************************************************************

    private static SpannableStringBuilder analaysHash(SpannableStringBuilder builder, String messageID) {

        if (builder == null) return builder;

        String text = builder.toString();

        if (text.length() < 1) return builder;

        String s = "";
        String tmp = "";
        Boolean isHash = false;
        int start = 0;
        String enter = System.getProperty("line.separator");

        for (int i = 0; i < text.length(); i++) {

            s = text.substring(i, i + 1);
            if (s.equals("#")) {
                isHash = true;
                tmp = "";
                start = i;
                continue;
            }

            if (isHash) {
                if (!(s.matches("\\w") || s.equals("-"))) {
                    if (tmp.length() > 0) {
                        insertHashLink(tmp, builder, start, messageID);
                    }

                    tmp = "";
                    isHash = false;
                } else {
                    tmp += s;
                }
            }
        }

        if (isHash) {
            if (tmp.length() > 0) insertHashLink(tmp, builder, start, messageID);
        }

        return builder;
    }

    private static void insertHashLink(final String text, SpannableStringBuilder builder, int start, final String messageID) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (FragmentChat.hashListener != null) {
                    FragmentChat.hashListener.complete(true, "#" + text, messageID);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        builder.setSpan(clickableSpan, start, start + text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //*********************************************************************************************************

    private static SpannableStringBuilder analaysAtSign(SpannableStringBuilder builder) {

        if (builder == null) return builder;

        String text = builder.toString();

        if (text.length() < 1) return builder;

        String s = "";
        String tmp = "";
        Boolean isAtSign = false;
        int start = 0;
        String enter = System.getProperty("line.separator");

        for (int i = 0; i < text.length(); i++) {

            s = text.substring(i, i + 1);
            if (s.equals("@")) {
                isAtSign = true;
                tmp = "";
                start = i;
                continue;
            }

            if (isAtSign) {
                if (!(s.matches("\\w") || s.equals("-"))) {
                    //if (s.equals("!") || s.equals("#") || s.equals("$") || s.equals("%") || s.equals("^") || s.equals("&") ||
                    //    s.equals("(") || s.equals(")") || s.equals("-") || s.equals("+") || s.equals("=") || s.equals("!") ||
                    //    s.equals("`") || s.equals("{") || s.equals("}") || s.equals("[") || s.equals("]") || s.equals(";") ||
                    //    s.equals(":") || s.equals("'") || s.equals("?") || s.equals("<") || s.equals(">") || s.equals(",") || s.equals(" ") ||
                    //    s.equals("\\") || s.equals("|") || s.equals("//") || s.codePointAt(0) == 8192 || s.equals(enter) || s.equals("")) {
                    if (tmp.length() > 0) {
                        insertAtSignLink(tmp, builder, start);
                    }

                    tmp = "";
                    isAtSign = false;
                } else {
                    tmp += s;
                }
            }
        }

        if (isAtSign) {
            if (tmp.length() > 0) insertAtSignLink(tmp, builder, start);
        }

        return builder;
    }

    private static void insertAtSignLink(final String text, SpannableStringBuilder builder, int start) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                checkUsernameAndGoToRoom(text, ChatEntry.profile);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        builder.setSpan(clickableSpan, start, start + text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    public static SpannableStringBuilder getLinkText(String text, String linkInfo, String messageID) {

        if (text == null) return null;
        if (text.trim().length() < 1) return null;

        SpannableStringBuilder strBuilder = new SpannableStringBuilder(text);

        if (linkInfo != null && linkInfo.length() > 0) {

            String[] list = linkInfo.split("@");

            for (int i = 0; i < list.length; i++) {

                String[] info = list[i].split("_");

                int start = Integer.parseInt(info[0]);
                int end = Integer.parseInt(info[1]);
                String type = info[2];


                try {
                    if (type.equals("hash")) {
                        insertHashLink(text.substring(start + 1, end), strBuilder, start, messageID);
                    } else if (type.equals("atSighn")) {
                        insertAtSignLink(text.substring(start + 1, end), strBuilder, start);
                    } else if (type.equals("igapLink")) {
                        insertIgapLink(strBuilder, start, end);
                    } else if (type.equals("igapResolve")) {
                        insertIgapResolveLink(strBuilder, start, end);
                    } else if (type.equals("webLink")) {
                        insertLinkSpan(strBuilder, start, end, true);
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }

        return strBuilder;
    }

    public static String getLinkInfo(String text) {

        String linkInfo = "";

        if (text == null) {
            return linkInfo;
        }

        if (text.trim().length() < 1) {
            return linkInfo;
        }


        linkInfo += analysisAtSignLinkInfo(text);

        linkInfo += analysisHashLinkInfo(text);

        String newText = text.toLowerCase();

        String[] list = newText.replace(System.getProperty("line.separator"), " ").split(" ");

        int count = 0;

        for (int i = 0; i < list.length; i++) {

            String str = list[i];

            if (str.contains(igapSite2)) {
                linkInfo += count + "_" + (count + str.length()) + "_" + linkType.igapLink.toString() + "@";
            } else if (str.contains(igapResolve)) {
                linkInfo += count + "_" + (count + str.length()) + "_" + linkType.igapResolve.toString() + "@";
            } else if (isTextLink(str)) {
                linkInfo += count + "_" + (count + str.length()) + "_" + linkType.webLink.toString() + "@";
            }
            count += str.length() + 1;
        }

        return linkInfo;
    }


    private static String analysisAtSignLinkInfo(String text) {
        String result = "";
        if (text == null || text.length() < 1) {
            return result;
        }

        Pattern p = Pattern.compile("[@]+[A-Za-z0-9-_]+\\b");
        Matcher m = p.matcher(text);
        while (m.find()) {
            result += m.start() + "_" + m.end() + "_" + linkType.atSighn.toString() + "@";
        }

        return result;
    }

    private static String analysisHashLinkInfo(String text) {
        String result = "";
        if (text == null || text.length() < 1) {
            return result;
        }
        Pattern p = Pattern.compile("[#]+[\\p{L}A-Za-z0-9-_]+\\b");
        Matcher m = p.matcher(text);
        while (m.find()) {
            result += m.start() + "_" + m.end() + "_" + linkType.hash.toString() + "@";
        }

        return result;
    }


    private static void checkAndJoinToRoom(final String token) {

        if (token == null || token.length() < 0) return;

        if (G.userLogin) {
            showIndeterminateProgressDialog();

            G.onClientCheckInviteLink = new OnClientCheckInviteLink() {
                @Override
                public void onClientCheckInviteLinkResponse(ProtoGlobal.Room room) {
                    closeDialogWaiting();
                    openDialogJoin(room, token);
                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    closeDialogWaiting();
                }
            };

            new RequestClientCheckInviteLink().clientCheckInviteLink(token);
        } else {
            closeDialogWaiting();
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
        }
    }

    private static void openDialogJoin(final ProtoGlobal.Room room, final String token) {
        if (room == null) {
            return;

        }
        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).equalTo(RealmRoomFields.IS_DELETED, false).findFirst();
        if (realmRoom != null) {
            new GoToChatActivity(room.getId()).startActivity();

            realm.close();
            return;
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = RealmRoom.putOrUpdate(room, realm);
                realmRoom.setDeleted(true);
                G.deletedRoomList.add(realmRoom.getId());
            }
        });
        realm.close();

        String title = G.context.getString(R.string.do_you_want_to_join_to_this);
        String memberNumber = "";
        final CircleImageView[] imageView = new CircleImageView[1];

        switch (room.getType()) {
            case CHANNEL:

                if (HelperCalander.isPersianUnicode) {
                    title += G.context.getString(R.string.channel) + " " + "عضو شوید؟";
                } else {
                    title += G.context.getString(R.string.channel) + "?";
                }

                memberNumber = room.getChannelRoomExtra().getParticipantsCount() + " " + G.context.getString(R.string.member_chat);
                break;
            case GROUP:

                if (HelperCalander.isPersianUnicode) {
                    title += G.context.getString(R.string.group) + " " + "عضو شوید؟";
                } else {
                    title += G.context.getString(R.string.group) + "?";
                }

                memberNumber = room.getGroupRoomExtra().getParticipantsCount() + " " + G.context.getString(R.string.member_chat);
                break;
        }

        final String finalMemberNumber = memberNumber;
        final String finalTitle = title;

        G.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final MaterialDialog dialog = new MaterialDialog.Builder(G.currentActivity).title(finalTitle).customView(R.layout.dialog_alert_join, true).positiveText(R.string.join).cancelable(true).negativeText(android.R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        joinToRoom(token, room);
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        RealmRoom.deleteRoom(room.getId());
                    }
                }).build();

                imageView[0] = (CircleImageView) dialog.findViewById(R.id.daj_img_room_picture);

                TextView txtRoomName = (TextView) dialog.findViewById(R.id.daj_txt_room_name);
                txtRoomName.setText(room.getTitle());

                TextView txtMemberNumber = (TextView) dialog.findViewById(R.id.daj_txt_member_count);
                txtMemberNumber.setText(finalMemberNumber);

                HelperAvatar.getAvatar(room.getId(), HelperAvatar.AvatarType.ROOM, false, new OnAvatarGet() {
                    @Override
                    public void onAvatarGet(final String avatarPath, long roomId) {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imageView[0]);
                    }

                    @Override
                    public void onShowInitials(String initials, String color) {
                        imageView[0].setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imageView[0].getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
                dialog.show();
            }
        });
    }

    private static void joinToRoom(String token, final ProtoGlobal.Room room) {
        if (G.userLogin) {
            showIndeterminateProgressDialog();

            G.onClientJoinByInviteLink = new OnClientJoinByInviteLink() {
                @Override
                public void onClientJoinByInviteLinkResponse() {

                    closeDialogWaiting();
                    RealmRoom.joinByInviteLink(room.getId());
                    new GoToChatActivity(room.getId()).startActivity();
                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    closeDialogWaiting();
                }
            };

            new RequestClientJoinByInviteLink().clientJoinByInviteLink(token);
        } else {
            closeDialogWaiting();
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
        }
    }

    //************************************  go to room by userName   *********************************************************************

    public static void checkUsernameAndGoToRoom(final String userName, final ChatEntry chatEntery) {

        if (userName == null || userName.length() < 1) return;

        if (G.userLogin) {
            // this methode check user name and if it is ok go to room
            G.onClientResolveUsername = new OnClientResolveUsername() {
                @Override
                public void onClientResolveUsername(ProtoClientResolveUsername.ClientResolveUsernameResponse.Type type, ProtoGlobal.RegisteredUser user, ProtoGlobal.Room room) {
                    openChat(userName, type, user, room, chatEntery);
                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    closeDialogWaiting();
                }
            };

            showIndeterminateProgressDialog();

            new RequestClientResolveUsername().clientResolveUsername(userName);
        } else {
            closeDialogWaiting();
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
        }
    }

    public static void closeDialogWaiting() {
        if (dialogWaiting != null) if (dialogWaiting.isShowing()) dialogWaiting.dismiss();
    }

    private static void openChat(String username, ProtoClientResolveUsername.ClientResolveUsernameResponse.Type type, ProtoGlobal.RegisteredUser user, ProtoGlobal.Room room, ChatEntry chatEntery) {

        switch (type) {
            case USER:
                goToChat(user, chatEntery);
                break;
            case ROOM:
                goToRoom(username, room);
                break;
        }
    }

    private static void goToActivity(final long roomId, final long peerId, ChatEntry chatEntry) {

        switch (chatEntry) {
            case chat:
                new GoToChatActivity(roomId).setPeerID(peerId).startActivity();
                break;
            case profile:
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FragmentContactsProfile contactsProfile = new FragmentContactsProfile();
                        Bundle bundle = new Bundle();
                        bundle.putLong("peerId", peerId);
                        bundle.putLong("RoomId", roomId);
                        bundle.putString("enterFrom", GROUP.toString());
                        contactsProfile.setArguments(bundle);
                        new HelperFragment(contactsProfile).setReplace(false).load();
                    }
                });
                break;
        }
    }

    private static void goToChat(final ProtoGlobal.RegisteredUser user, final ChatEntry chatEntery) {

        Long id = user.getId();

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, id).equalTo(RealmRoomFields.IS_DELETED, false).findFirst();

        if (realmRoom != null) {
            closeDialogWaiting();

            goToActivity(realmRoom.getId(), id, chatEntery);

            realm.close();
        } else {
            if (G.userLogin) {
                addChatToDatabaseAndGoToChat(user, 0, chatEntery);
            } else {
                closeDialogWaiting();
                HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
            }
        }
    }

    public static void showIndeterminateProgressDialog() {

        try {
            if (G.currentActivity != null) {
                G.currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogWaiting != null && dialogWaiting.isShowing()) {

                        } else {
                            dialogWaiting = new MaterialDialog.Builder(G.currentActivity).title("").content(R.string.please_wait).progress(true, 0).cancelable(false).progressIndeterminateStyle(false).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addChatToDatabaseAndGoToChat(final ProtoGlobal.RegisteredUser user, final long roomId, final ChatEntry chatEntery) {

        closeDialogWaiting();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRegisteredInfo.putOrUpdate(realm, user);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        goToActivity(roomId, user.getId(), chatEntery);

                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }

    private static void goToRoom(String username, final ProtoGlobal.Room room) {

        final Realm realm = Realm.getDefaultInstance();

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

        if (realmRoom != null) {

            if (realmRoom.isDeleted()) {
                addRoomToDataBaseAndGoToRoom(username, room);
            } else {
                closeDialogWaiting();

                new GoToChatActivity(room.getId()).startActivity();

            }

            realm.close();
        } else {

            addRoomToDataBaseAndGoToRoom(username, room);
        }
    }

    private static void addRoomToDataBaseAndGoToRoom(final String username, final ProtoGlobal.Room room) {
        closeDialogWaiting();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        RealmRoom realmRoom1 = RealmRoom.putOrUpdate(room, realm);
                        realmRoom1.setDeleted(true);                            // if in chat activity join to room set deleted goes to false
                        G.deletedRoomList.add(realmRoom1.getId());
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        new GoToChatActivity(room.getId()).setfromUserLink(true).setisNotJoin(true).setuserName(username).startActivity();

                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }

    //************************************  go to room by urlLink   *********************************************************************

    public static void getLinkinfo(final Intent intent, final Activity activity) {

        String action = intent.getAction();

        if (action == null) return;

        if (action.equals(Intent.ACTION_VIEW)) {
            G.currentActivity = activity;
            showIndeterminateProgressDialog();
            checkConnection(intent.getData(), 0);
        }
    }

    private static void checkConnection(final Uri path, int countTime) {

        countTime++;

        if (G.userLogin) {
            getToRoom(path);
        } else {
            if (countTime < 15) {
                final int finalCountTime = countTime;
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        checkConnection(path, finalCountTime);
                    }
                }, 1000);
            } else {
                closeDialogWaiting();
                HelperError.showSnackMessage(G.context.getString(R.string.can_not_connent_to_server), false);
            }
        }
    }

    private static void getToRoom(Uri path) {

        if (path != null) {
            if (path.toString().toLowerCase().contains(igapSite2)) {

                String url = path.toString();
                int index = url.lastIndexOf("/");
                if (index >= 0 && index < url.length() - 1) {
                    String token = url.substring(index + 1);

                    if (url.toLowerCase().contains("join")) {
                        checkAndJoinToRoom(token);
                    } else {
                        checkUsernameAndGoToRoom(token, ChatEntry.profile);
                    }
                }
            } else {

                try {
                    String domain = path.getQueryParameter("domain");

                    if (domain != null && domain.length() > 0) {
                        checkUsernameAndGoToRoom(domain, ChatEntry.profile);
                    }
                } catch (Exception e) {

                }
            }
        } else {
            closeDialogWaiting();
        }
    }
}
