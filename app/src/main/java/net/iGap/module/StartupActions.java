package net.iGap.module;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.WebSocketClient;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperFillLookUpClass;
import net.iGap.helper.HelperNotificationAndBadge;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUploadFile;
import net.iGap.realm.RealmDataUsage;
import net.iGap.realm.RealmMigration;
import net.iGap.realm.RealmUserInfo;
import net.iGap.webrtc.CallObserver;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.Config.REALM_SCHEMA_VERSION;
import static net.iGap.G.DIR_AUDIOS;
import static net.iGap.G.DIR_CHAT_BACKGROUND;
import static net.iGap.G.DIR_DOCUMENT;
import static net.iGap.G.DIR_IMAGES;
import static net.iGap.G.DIR_IMAGE_USER;
import static net.iGap.G.DIR_MESSAGES;
import static net.iGap.G.DIR_TEMP;
import static net.iGap.G.DIR_VIDEOS;
import static net.iGap.G.IGAP;
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
    private RealmConfiguration configuration;

    public StartupActions() {

        detectDeviceType();
        //  EmojiManager.install(new EmojiOneProvider()); // This line needs to be executed before any usage of EmojiTextView or EmojiEditText.
        initializeGlobalVariables();
        realmConfiguration();
        mainUserInfo();
        connectToServer();
        manageSettingPreferences();
        makeFolder();
        ConnectionManager.manageConnection();
        configDownloadManager();
        manageTime();
        getiGapAccountInstance();

        new CallObserver();
        /**
         * initialize download and upload listeners
         */
        new HelperUploadFile();
        checkDataUsage();
    }

    private void checkDataUsage() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmDataUsage> realmDataUsage=realm.where(RealmDataUsage.class).findAll();
        if (realmDataUsage.size()==0)
            HelperDataUsage.initializeRealmDataUsage();
    }

    private void manageTime() {
        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        G.isTimeWhole = sharedPreferences.getBoolean(SHP_SETTING.KEY_WHOLE_TIME, false);
    }

    private void configDownloadManager() {

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(G.context, config);


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

            new File(DIR_IMAGES).mkdirs();
            new File(DIR_VIDEOS).mkdirs();
            new File(DIR_AUDIOS).mkdirs();
            new File(DIR_DOCUMENT).mkdirs();
            new File(DIR_MESSAGES).mkdirs();

            String file = ".nomedia";
            new File(DIR_IMAGES + "/" + file).createNewFile();
            new File(DIR_VIDEOS + "/" + file).createNewFile();
            new File(DIR_AUDIOS + "/" + file).createNewFile();
            new File(DIR_DOCUMENT + "/" + file).createNewFile();
            new File(DIR_MESSAGES + "/" + file).createNewFile();


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
            DIR_MESSAGES = rootPath + G.MESSAGES;

        } else {
            String selectedStorage = getSelectedStoragePath(rootPath);
            DIR_IMAGES = selectedStorage + G.IMAGES;
            DIR_VIDEOS = selectedStorage + G.VIDEOS;
            DIR_AUDIOS = selectedStorage + G.AUDIOS;
            DIR_DOCUMENT = selectedStorage + G.DOCUMENT;
            DIR_MESSAGES = selectedStorage + G.MESSAGES;
        }

        DIR_TEMP = rootPath + G.TEMP;
        DIR_CHAT_BACKGROUND = rootPath + G.CHAT_BACKGROUND;
        DIR_IMAGE_USER = rootPath + G.IMAGE_USER;
    }

    private static String getSelectedStoragePath(String cashPath) {

        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        boolean canWrite = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        String selectedStorage = "";

        if (canWrite) {
            selectedStorage = G.DIR_APP;
        } else {
            selectedStorage = cashPath;
        }

        if (sharedPreferences.getInt(SHP_SETTING.KEY_SDK_ENABLE, 0) == 1) {
            if (G.DIR_SDCARD_EXTERNAL.equals("")) {
                List<String> storageList = FileUtils.getSdCardPathList();
                if (storageList.size() > 0) {
                    String sdPath = "";
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        sdPath = storageList.get(0) + IGAP;
                    } else {
                        File exFile = G.context.getExternalFilesDir(null);
                        if (exFile != null) {
                            sdPath = storageList.get(0) + exFile.getAbsolutePath().substring(exFile.getAbsolutePath().indexOf("/Android"));
                        }
                    }
                    File sdFile = new File(sdPath);
                    if ((sdFile.exists() && sdFile.canWrite()) || sdFile.mkdirs()) {
                        G.DIR_SDCARD_EXTERNAL = selectedStorage = sdPath;
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_SDK_ENABLE, 0);
                        editor.apply();
                    }
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(SHP_SETTING.KEY_SDK_ENABLE, 0);
                    editor.apply();
                }
            } else {
                File sdFile = new File(G.DIR_SDCARD_EXTERNAL);
                if ((sdFile.exists() && sdFile.canWrite()) || sdFile.mkdirs()) {
                    selectedStorage = G.DIR_SDCARD_EXTERNAL;
                } else {
                    G.DIR_SDCARD_EXTERNAL = "";
                }
            }
        }
        new File(selectedStorage).mkdirs();
        return selectedStorage;
    }

    /**
     * if iGap Account not created yet, create otherwise just detect and return
     */
    public static Account getiGapAccountInstance() {

        if (G.iGapAccount != null) {
            return G.iGapAccount;
        }

        AccountManager accountManager = AccountManager.get(G.context);
        if (accountManager.getAccounts().length != 0) {
            for (Account account : accountManager.getAccounts()) {
                if (account.type.equals(G.context.getPackageName())) {
                    G.iGapAccount = account;
                    return G.iGapAccount;
                }
            }
        }

        G.iGapAccount = new Account(Config.iGapAccount, G.context.getPackageName());
        String password = "net.iGap";
        try {
            accountManager.addAccountExplicitly(G.iGapAccount, password, null);
        } catch (Exception e1) {
            e1.getMessage();
        }

        return G.iGapAccount;
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

        if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && G.twoPaneMode) {
            G.maxChatBox = metrics.widthPixels - (metrics.widthPixels / 3) - ViewMaker.i_Dp(R.dimen.dp80);
        } else {
            G.maxChatBox = metrics.widthPixels - ViewMaker.i_Dp(R.dimen.dp80);
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

        /** clear map cache and use from new map tile url */
        if (preferences.getBoolean(SHP_SETTING.KEY_MAP_CLEAR_CACHE_GOOGLE, true)) {
            FragmentiGapMap.deleteMapFileCash();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(SHP_SETTING.KEY_MAP_CLEAR_CACHE_GOOGLE, false);
            editor.apply();
        }

//        G.isDarkTheme = preferences.getBoolean(SHP_SETTING.KEY_THEME_DARK, false);

        boolean isDisableAutoDarkTheme = preferences.getBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, true);
        if (!isDisableAutoDarkTheme) {
            checkTimeForAutoTheme(preferences);
        }

        Theme.setThemeColor();

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

    private void checkTimeForAutoTheme(SharedPreferences preferences) {
        long toMs;
        long fromMs;
        boolean auto = preferences.getBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, true);

        int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        long now = System.currentTimeMillis() + offset;

        if (auto) {
            toMs = 28800000;
            fromMs = 68400000;
        } else {
            toMs = preferences.getLong(SHP_SETTING.KEY_SELECTED_MILISECOND_TO, 28800000);
            fromMs = preferences.getLong(SHP_SETTING.KEY_SELECTED_MILISECOND_FROM, 68400000);
        }

        try {
            String string1 = time(fromMs);
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);

            String string2 = time(toMs);
            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);

            String someRandomTime = time(now);
            Date currentTime = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);

            if (currentTime.getTime() > time1.getTime() && currentTime.getTime() < time2.getTime()) {

                //checkes whether the current time is between 14:49:00 and 20:11:13.
                G.isDarkTheme = true;
                appBarColor = Theme.default_dark_appBarColor;
                notificationColor = Theme.default_dark_notificationColor;
                toggleButtonColor = Theme.default_dark_toggleButtonColor;
                attachmentColor = Theme.default_dark_attachmentColor;
                headerTextColor = Theme.default_dark_headerTextColor;
                G.progressColor = Theme.default_dark_progressColor;
            } else {
                G.isDarkTheme = false;
                appBarColor = Theme.default_appBarColor;
                notificationColor = Theme.default_notificationColor;
                toggleButtonColor = Theme.default_toggleButtonColor;
                attachmentColor = Theme.default_attachmentColor;
                headerTextColor = Theme.default_headerTextColor;
                G.progressColor = Theme.default_progressColor;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private String time(long timeNow) {
        long second = (timeNow / 1000) % 60;
        long minute = (timeNow / (1000 * 60)) % 60;
        long hour = (timeNow / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d:%d", hour, minute, second, timeNow);
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

        if (userInfo != null && userInfo.getUserRegistrationState()) {

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


        //  new SecureRandom().nextBytes(key);


        // An encrypted Realm file can be opened in Realm Studio by using a Hex encoded version
        // of the key. Copy the key from Logcat, then download the Realm file from the device using
        // the method described here: https://stackoverflow.com/a/28486297/1389357
        // The path is normally `/data/data/io.realm.examples.encryption/files/default.realm`

     /*   RealmConfiguration configuration = new RealmConfiguration.Builder().name("iGapLocalDatabase.realm")
                .schemaVersion(REALM_SCHEMA_VERSION).migration(new RealmMigration()).build();
        DynamicRealm dynamicRealm = DynamicRealm.getInstance(configuration);*/

        Realm configuredRealm = getInstance();
        DynamicRealm dynamicRealm = DynamicRealm.getInstance(configuredRealm.getConfiguration());



        /*if (configuration!=null)
            Realm.deleteRealm(configuration);*/

        /**
         * Returns version of Realm file on disk
         */
        if (dynamicRealm.getVersion() == -1) {
            Realm.setDefaultConfiguration(new RealmConfiguration.Builder().name("iGapLocalDatabaseEncrypted.realm").schemaVersion(REALM_SCHEMA_VERSION).deleteRealmIfMigrationNeeded().build());
            //   Realm.setDefaultConfiguration(configuredRealm.getConfiguration());
        } else {
            Realm.setDefaultConfiguration(configuredRealm.getConfiguration());

        }
        dynamicRealm.close();
        configuredRealm.close();
        try {
            Realm.compactRealm(configuredRealm.getConfiguration());

        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    public Realm getInstance() {
        SharedPreferences sharedPreferences = G.context.getSharedPreferences("AES-256", Context.MODE_PRIVATE);

        String stringArray = sharedPreferences.getString("myByteArray", null);
        if (stringArray == null) {
            byte[] key = new byte[64];
            new SecureRandom().nextBytes(key);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String saveThis = Base64.encodeToString(key, Base64.DEFAULT);
            editor.putString("myByteArray", saveThis);
            editor.commit();
        }

        byte[] mKey = Base64.decode(sharedPreferences.getString("myByteArray", null), Base64.DEFAULT);
        RealmConfiguration newConfig = new RealmConfiguration.Builder()
                .name("iGapLocalDatabaseEncrypted.realm")
                .encryptionKey(mKey)
                .schemaVersion(REALM_SCHEMA_VERSION)
                .migration(new RealmMigration())
                .build();

        File newRealmFile = new File(newConfig.getPath());
        if (newRealmFile.exists()) {
            return Realm.getInstance(newConfig);
        } else {
            configuration = new RealmConfiguration.Builder().name("iGapLocalDatabase.realm")
                    .schemaVersion(REALM_SCHEMA_VERSION).migration(new RealmMigration()).build();
            Realm realm = Realm.getInstance(configuration);
            realm.writeEncryptedCopyTo(newRealmFile, mKey);
            realm.close();
            Realm.deleteRealm(configuration);
            return Realm.getInstance(newConfig);
        }
    }
}