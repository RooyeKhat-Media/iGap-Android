package net.iGap.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.one.EmojiOneProvider;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperFillLookUpClass;
import net.iGap.helper.HelperNotificationAndBadge;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUploadFile;
import net.iGap.realm.RealmMigration;
import net.iGap.realm.RealmUserInfo;
import net.iGap.webrtc.CallObserver;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.Config.REALM_SCHEMA_VERSION;
import static net.iGap.G.DIR_APP;
import static net.iGap.G.DIR_AUDIOS;
import static net.iGap.G.DIR_CHAT_BACKGROUND;
import static net.iGap.G.DIR_DOCUMENT;
import static net.iGap.G.DIR_IMAGES;
import static net.iGap.G.DIR_IMAGE_USER;
import static net.iGap.G.DIR_TEMP;
import static net.iGap.G.DIR_VIDEOS;
import static net.iGap.G.IMAGE_NEW_CHANEL;
import static net.iGap.G.IMAGE_NEW_GROUP;
import static net.iGap.G.appBarColor;
import static net.iGap.G.attachmentColor;
import static net.iGap.G.authorHash;
import static net.iGap.G.context;
import static net.iGap.G.displayName;
import static net.iGap.G.headerTextColor;
import static net.iGap.G.helperNotificationAndBadge;
import static net.iGap.G.imageFile;
import static net.iGap.G.imageLoader;
import static net.iGap.G.isSaveToGallery;
import static net.iGap.G.notificationColor;
import static net.iGap.G.selectedLanguage;
import static net.iGap.G.toggleButtonColor;
import static net.iGap.G.userId;
import static net.iGap.G.userTextSize;

/**
 * all actions that need doing after open app
 */
public final class StartupActions {

    public StartupActions() {

        detectDeviceType();
        EmojiManager.install(new EmojiOneProvider()); // This line needs to be executed before any usage of EmojiTextView or EmojiEditText.
        initializeGlobalVariables();
        realmConfiguration();
        mainUserInfo();
        connectToServer();
        manageSettingPreferences();
        makeFolder();
        ConnectionManager.manageConnection();


        new CallObserver();
        /**
         * initialize download and upload listeners
         */
        new HelperDownloadFile();
        new HelperUploadFile();
    }

    /**
     * if device is tablet twoPaneMode will be enabled
     */
    private void detectDeviceType() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            G.twoPaneMode = true;
        } else {
            G.twoPaneMode = false;
        }

        if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT || G.twoPaneMode) {
            G.maxChatBox = Math.min(metrics.widthPixels, metrics.heightPixels) - ViewMaker.i_Dp(R.dimen.dp80);
        } else {
            G.maxChatBox = Math.max(metrics.widthPixels, metrics.heightPixels) - ViewMaker.i_Dp(R.dimen.dp80);
        }
    }


    /**
     * start connecting to the sever
     */
    private void connectToServer() {
        WebSocketClient.getInstance();
        new LoginActions();
    }

    /**
     * detect preferences value and initialize setting fields
     */
    private void manageSettingPreferences() {
        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        appBarColor = preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, Config.default_appBarColor);
        notificationColor = preferences.getString(SHP_SETTING.KEY_NOTIFICATION_COLOR, Config.default_notificationColor);
        toggleButtonColor = preferences.getString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, Config.default_toggleButtonColor);
        attachmentColor = preferences.getString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, Config.default_attachmentColor);
        headerTextColor = preferences.getString(SHP_SETTING.KEY_FONT_HEADER_COLOR, Config.default_headerTextColor);
        G.progressColor = preferences.getString(SHP_SETTING.KEY_PROGRES_COLOR, Config.default_progressColor);
        G.multiTab = preferences.getBoolean(SHP_SETTING.KEY_MULTI_TAB, false);

        // setting for show layout vote in channel
        G.showVoteChannelLayout = preferences.getInt(SHP_SETTING.KEY_VOTE, 1) == 1;

        //setting for show layout sender name in group
        G.showSenderNameInGroup = preferences.getInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0) == 1;

        /**
         * detect need save to gallery automatically
         */
        int checkedSaveToGallery = preferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
        isSaveToGallery = checkedSaveToGallery == 1;

        textSizeDetection(preferences);
        languageDetection(preferences);
    }

    /**
     * detect and  initialize text size
     */
    public static void textSizeDetection(SharedPreferences sharedPreferences) {
        userTextSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14);

        if (!G.context.getResources().getBoolean(R.bool.isTablet)) {

            int screenLayout = context.getResources().getConfiguration().screenLayout;
            screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

            switch (screenLayout) {
                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                    userTextSize = (userTextSize * 3) / 4;
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    break;
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    userTextSize = (userTextSize * 3) / 2;
                    break;
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:// or 4
                    userTextSize *= 2;
            }
        }
    }

    /**
     * detect language and set font type face
     */
    private void languageDetection(SharedPreferences sharedPreferences) {

        String language = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());

        switch (language) {
            case "فارسی":
                selectedLanguage = "fa";
                HelperCalander.isPersianUnicode = true;
                G.isAppRtl = true;
                break;
            case "English":
                selectedLanguage = "en";
                HelperCalander.isPersianUnicode = false;
                G.isAppRtl = false;
                break;
            case "العربی":
                selectedLanguage = "ar";
                HelperCalander.isPersianUnicode = true;
                G.isAppRtl = true;
                break;
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/IRANSansMobile.ttf").setFontAttrId(R.attr.fontPath).build());
    }

    /**
     * create app folders if not created or removed from phone storage
     */
    public static void makeFolder() {
        try {
            manageAppDirectories();
            //before used from thread; isn't good idea
            //new Thread(new Runnable() {
            //    @Override
            //    public void run() {
            //    }
            //}).start();

            new File(DIR_APP).mkdirs();
            new File(DIR_IMAGES).mkdirs();
            new File(DIR_VIDEOS).mkdirs();
            new File(DIR_AUDIOS).mkdirs();
            new File(DIR_DOCUMENT).mkdirs();

            String file = ".nomedia";
            new File(DIR_IMAGES + "/" + file).createNewFile();
            new File(DIR_VIDEOS + "/" + file).createNewFile();
            new File(DIR_AUDIOS + "/" + file).createNewFile();
            new File(DIR_DOCUMENT + "/" + file).createNewFile();


            new File(DIR_CHAT_BACKGROUND).mkdirs();
            new File(DIR_IMAGE_USER).mkdirs();
            new File(DIR_TEMP).mkdirs();
            new File(DIR_CHAT_BACKGROUND + "/" + file).createNewFile();
            new File(DIR_IMAGE_USER + "/" + file).createNewFile();
            new File(DIR_TEMP + "/" + file).createNewFile();

            IMAGE_NEW_GROUP = new File(G.DIR_IMAGE_USER, "image_new_group.jpg");
            IMAGE_NEW_CHANEL = new File(G.DIR_IMAGE_USER, "image_new_chanel.jpg");
            imageFile = new File(DIR_IMAGE_USER, "image_user");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void manageAppDirectories() {
        String rootPath = getCacheDir().getPath();

        if (!HelperPermission.grantedUseStorage()) {
            DIR_IMAGES = rootPath + G.IMAGES;
            DIR_VIDEOS = rootPath + G.VIDEOS;
            DIR_AUDIOS = rootPath + G.AUDIOS;
            DIR_DOCUMENT = rootPath + G.DOCUMENT;
        }

        DIR_TEMP = rootPath + G.TEMP;
        DIR_CHAT_BACKGROUND = rootPath + G.CHAT_BACKGROUND;
        DIR_IMAGE_USER = rootPath + G.IMAGE_USER;
    }

    public static File getCacheDir() {
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (state == null || state.startsWith(Environment.MEDIA_MOUNTED)) {
            try {
                File file = G.context.getExternalCacheDir();
                if (file != null) {
                    return file;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            File file = G.context.getCacheDir();
            if (file != null) {
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(G.DIR_APP);
    }

    private void initializeGlobalVariables() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(false).build();
                ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(defaultOptions).build());
                imageLoader = ImageLoader.getInstance();
                helperNotificationAndBadge = new HelperNotificationAndBadge();

                HelperFillLookUpClass.fillArrays();
            }
        }).start();

    }

    /**
     * fill main user info in global variables
     */
    private void mainUserInfo() {

        Realm realm = Realm.getDefaultInstance();

        RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();

        if (userInfo != null) {

            userId = userInfo.getUserId();
            G.isPassCode = userInfo.isPassCode();

            if (userInfo.getAuthorHash() != null) {
                authorHash = userInfo.getAuthorHash();
            }

            if (userInfo.getUserInfo().getDisplayName() != null) {
                displayName = userInfo.getUserInfo().getDisplayName();
            }

        }

        realm.close();
    }

    /**
     * initialize realm and manage migration
     */
    private void realmConfiguration() {
        /**
         * before call RealmConfiguration client need to Realm.init(context);
         */
        Realm.init(context);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("iGapLocalDatabase.realm").schemaVersion(REALM_SCHEMA_VERSION).migration(new RealmMigration()).build();
        DynamicRealm dynamicRealm = DynamicRealm.getInstance(configuration);
        /**
         * Returns version of Realm file on disk
         */
        if (dynamicRealm.getVersion() == -1) {
            Realm.setDefaultConfiguration(new RealmConfiguration.Builder().name("iGapLocalDatabase.realm").schemaVersion(REALM_SCHEMA_VERSION).deleteRealmIfMigrationNeeded().build());
        } else {
            Realm.setDefaultConfiguration(new RealmConfiguration.Builder().name("iGapLocalDatabase.realm").schemaVersion(REALM_SCHEMA_VERSION).migration(new RealmMigration()).build());
        }
        dynamicRealm.close();

        try {
            Realm.compactRealm(configuration);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }
}