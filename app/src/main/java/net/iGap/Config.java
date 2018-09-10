/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap;

import android.text.format.DateUtils;

public class Config {

    public static final int ACCEPT = 1;
    public static final int REJECT = 0;
    public static final int REALM_SCHEMA_VERSION = 23;
    public static final int REALM_LATEST_MIGRATION_VERSION = REALM_SCHEMA_VERSION - 1;
    public static final int LOOKUP_MAP_RESPONSE_OFFSET = 30000;
    public static final int MAX_TEXT_ATTACHMENT_LENGTH = 200;
    public static final int MAX_TEXT_LENGTH = 4096;
    public static final int IMAGE_CORNER = 7;
    public static final int TRY_CONNECTION_COUNT = 5;
    public static final int ACTION_CHECKING = 500;
    public static final int GROUP_SHOW_ACTIONS_COUNT = 3;
    public static final int LIMIT_GET_HISTORY_LOW = 10;
    public static final int LIMIT_GET_HISTORY_NORMAL = 50;
    public static final int LIMIT_LOAD_ROOM = 50;
    public static final int FAST_START_PAGE_TIME = (int) 20;
    public static final int LOW_START_PAGE_TIME = (int) 25;
    public static final int PHONE_CONTACT_MAX_COUNT_LIMIT = 9999;

    public static final int TIME_OUT_DELAY_MS = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int FAKE_PM_DELAY = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int TIME_OUT_MS = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int ALLOW_RECONNECT_AGAIN_NORMAL = (int) (3 * DateUtils.SECOND_IN_MILLIS);
    public static final int REPEAT_CONNECTION_CHECKING = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int DEFAULT_TIME_OUT = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int INSTANCE_SUCCESSFULLY_CHECKING = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int COUNTER_TIMER = (int) (60 * DateUtils.SECOND_IN_MILLIS);
    public static final int COUNTER_TIMER_DELAY = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int UPDATE_STATUS_TIME = (int) (3 * DateUtils.SECOND_IN_MILLIS);// after this time check that program is in background
    public static final int ACTION_TIME_OUT = (int) (2 * DateUtils.SECOND_IN_MILLIS);
    public static final int GET_MESSAGE_STATE_TIME_OUT = (int) (5 * DateUtils.SECOND_IN_MILLIS);
    public static final int GET_MESSAGE_STATE_TIME_OUT_CHECKING = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int LAST_SEEN_DELAY_CHECKING = (int) (60 * DateUtils.SECOND_IN_MILLIS);
    public static final int GET_CONTACT_LIST_TIME_OUT = (int) (60 * DateUtils.SECOND_IN_MILLIS);
    public static final int HEART_BEAT_CHECKING_TIME_OUT = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int UPDATING_TIME_SHOWING = (int) (2 * DateUtils.SECOND_IN_MILLIS);
    public static final int CONNECTION_OPEN_TIME_OUT = (int) (20 * DateUtils.SECOND_IN_MILLIS);
    public static final int FETCH_CONTACT_TIME_OUT = (int) (5 * DateUtils.SECOND_IN_MILLIS);
    public static final int LAST_SEEN_TIME_OUT = (int) (60 * DateUtils.MINUTE_IN_MILLIS); // after this time show exactly time instead of minutes
    public static final int DEFAULT_BOTH_CHAT_DELETE_TIME = (int) (2 * DateUtils.HOUR_IN_MILLIS);
    public static final String iGapAccount = "iGap";
    public static final String URL_WEBSOCKET = "wss://secure.igap.net/hybrid/";
    public static final String URL_MAP = "https://c.tile.openstreetmap.org/";
    public static final String IGAP_LINK_PREFIX = "https://iGap.net/";
    public static final String PUBLIC_KEY_CLIENT = "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo+inlAfd8Qior8IMKaJ+\n"
            + "BREJcEc9J9RhHgh6g/LvHKsnMaiEbAL70jQBQTLpCRu5Cnpj20+isOi++Wtf/pIP\n"
            + "FdJbD/1H+5jS+ja0RA6unp93DnBuYZ2JjV60vF3Ynj6F4Vr1ts5Xg5dJlEaOcOO2\n"
            + "YzOU97ZGP0ozrXIT5S+Y0BC4M9ieQmlGREzt3UZlTBbyUYPS4mMFh88YcT3QTiTA\n"
            + "k897qlJLxkYxVyAgwAD/0ihmWEkBQe9IxwVT/x5/QbixGSl4Zvd+5d+9sTZcSZQS\n"
            + "iJInT4E6DcmgAVYu5jFMWJDTEuurOQZ1W4nbmGyoY1bZXaFoiMPfzy72VIddkoHg\n"
            + "mwIDAQAB\n"
            + "-----END PUBLIC KEY-----\n";
    public static final byte[] SALT = new byte[]{
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64, 89
    };
    public static final String BASE64_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsm4sNLgDVqPf0ZxLWH3vkB1mPzHIkGWIJtNelibcTtzhipRv0iHeS3Z0wzeQpwYcMbkWQ81+WtgJwxUujitPOZnHvBex8qQLJ2JH33DvevWOgLDWPKEnKlfdi3Qg09pfO/Bx7eoWznWhRR6ZNjRgzY+P/2AaW77/f3wq3XHbHldM3jUrqwValwrWrkigIR0MFTkaGkg11T9JCFvO/L/FaZCAybuutje+H1nmNav3r8Xv6eBYS0nSVEm0dm5h46ECQi9PIxOCSMJ1McZMRkb8UaCScCAxh6lkD9fgZrOT5XQa8EOSWOwHx"
                    + "+uQWdR0efHyYbdC3A8zoJZjxBVtvVnDYwIDAQAB";


}
