package net.iGap.helper;

import android.util.Log;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmDataUsage;

import io.realm.Realm;
import io.realm.RealmResults;

public class HelperDataUsage {

    public static ProtoGlobal.RoomMessageType convetredDownloadType;
    public static ProtoGlobal.RoomMessageType convetredUploadType;

    // WIFI USAGE
    public static long wUploadedImageSize = 0;
    public static long wUploadedVideoSize = 0;
    public static long wUploadedFileSize = 0;
    public static long wUploadedAudioSize = 0;
    public static long wUploadedOtherSize = 0;

    public static long wDownloadedImageSize = 0;
    public static long wDownloadedVideoSize = 0;
    public static long wDownloadedFileSize = 0;
    public static long wDownloadedAudioSize = 0;
    public static long wDownloadedOtherSize = 0;

    public static int numDownloadImagesW = 0;
    public static int numDownloadVideosW = 0;
    public static int numDownloadFilesW = 0;
    public static int numDownloadAudiosW = 0;
    public static int numDownloadOthersW = 0;

    public static int numUploadImagesW = 0;
    public static int numUploadVideosW = 0;
    public static int numUploadFilesW = 0;
    public static int numUploadAudiosW = 0;
    public static int numUploadOthersW = 0;


    // DATA USAGE
    public static long dUploadedImageSize = 0;
    public static long dUploadedVideoSize = 0;
    public static long dUploadedFileSize = 0;
    public static long dUploadedAudioSize = 0;
    public static long dUploadedOtherSize = 0;

    public static long dDownloadedImageSize = 0;
    public static long dDownloadedVideoSize = 0;
    public static long dDownloadedFileSize = 0;
    public static long dDownloadedAudioSize = 0;
    public static long dDownloadedOtherSize = 0;

    public static int numDownloadImagesD = 0;
    public static int numDownloadVideosD = 0;
    public static int numDownloadFilesD = 0;
    public static int numDownloadAudiosD = 0;
    public static int numDownloadOthersD = 0;

    public static int numUploadImagesD = 0;
    public static int numUploadVideosD = 0;
    public static int numUploadFilesD = 0;
    public static int numUploadAudiosD = 0;
    public static int numUploadOthersD = 0;

    public static void insertDataUsage(ProtoGlobal.RoomMessageType type, boolean conectivityType, boolean isDownloaded) {
        Realm realm = Realm.getDefaultInstance();


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<RealmDataUsage> realmDataUsage = realm.where(RealmDataUsage.class).equalTo("connectivityType", conectivityType).findAll();

                if (conectivityType) {

                    for (RealmDataUsage usage : realmDataUsage) {

                        usage.setConnectivityType(conectivityType);

                        if (isDownloaded) {
                            if (type!=null&&usage.getType().equalsIgnoreCase(type.toString()))
                                usage.setNumDownloadedFile(usage.getNumDownloadedFile() + 1);

                            if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.IMAGE.toString())) {

                                usage.setDownloadSize(usage.getDownloadSize() + wDownloadedImageSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.VIDEO.toString())) {
                                Log.i("WWW", "old usage : "+usage.getDownloadSize());
                                Log.i("WWW", "wDownloadedVideoSize : "+wDownloadedVideoSize);
                                Log.i("WWW", "All : "+usage.getDownloadSize() + wDownloadedVideoSize);
                                usage.setDownloadSize(usage.getDownloadSize() + wDownloadedVideoSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.FILE.toString())) {

                                usage.setDownloadSize(usage.getDownloadSize() + wDownloadedFileSize);


                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.AUDIO.toString())) {

                                usage.setDownloadSize(usage.getDownloadSize()  +wDownloadedAudioSize);


                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.UNRECOGNIZED.toString())) {

                                usage.setDownloadSize(usage.getDownloadSize() + wDownloadedOtherSize);

                            }

                        } else {
                            if (type!=null&&usage.getType().equalsIgnoreCase(type.toString()))
                                usage.setNumUploadedFiles(usage.getNumUploadedFiles() + 1);

                            if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.IMAGE.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + wUploadedImageSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.VIDEO.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + wUploadedVideoSize);


                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.FILE.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + wUploadedFileSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.AUDIO.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + wUploadedAudioSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.UNRECOGNIZED.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + wUploadedOtherSize);


                            }
                        }


                    }
                } else {
                    for (RealmDataUsage usage : realmDataUsage) {

                        usage.setConnectivityType(conectivityType);

                        if (type!=null&&usage.getType().equalsIgnoreCase(type.toString()))
                            usage.setNumDownloadedFile(usage.getNumDownloadedFile() + 1);
                        if (isDownloaded) {


                            if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.IMAGE.toString())) {

                                usage.setDownloadSize(usage.getDownloadSize() + dDownloadedImageSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.VIDEO.toString())) {

                                usage.setDownloadSize(usage.getDownloadSize() + dDownloadedVideoSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.FILE.toString())) {

                                usage.setDownloadSize(usage.getDownloadSize() + dDownloadedFileSize);


                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.AUDIO.toString())) {

                                usage.setDownloadSize(usage.getDownloadSize()  +dDownloadedAudioSize);


                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.UNRECOGNIZED.toString())) {

                                usage.setDownloadSize(usage.getDownloadSize() + dDownloadedOtherSize);

                            }

                        } else {
                            if (type!=null&&usage.getType().equalsIgnoreCase(type.toString()))
                                usage.setNumUploadedFiles(usage.getNumUploadedFiles() + 1);

                            if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.IMAGE.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + dUploadedImageSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.VIDEO.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + dUploadedVideoSize);


                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.FILE.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + dUploadedFileSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.AUDIO.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + dUploadedAudioSize);

                            } else if (usage.getType().equalsIgnoreCase(ProtoGlobal.RoomMessageType.UNRECOGNIZED.toString())) {

                                usage.setUploadSize(usage.getUploadSize() + dUploadedOtherSize);


                            }
                        }



                    }
                }

            }
        });

        realm.close();
        clearStatics();
    }


    public static void progressDownload(boolean connectivityType, long downloadByte, ProtoGlobal.RoomMessageType type) {
        switch (type) {
            case VIDEO_TEXT:
            case VIDEO:
                if (connectivityType) {
                    Log.i("WWW", "downloaded byte: "+downloadByte);
                    Log.i("WWW", "progressDownload 1 : "+HelperDataUsage.wDownloadedVideoSize);
                    HelperDataUsage.wDownloadedVideoSize += downloadByte;
                    Log.i("WWW", "progressDownload 2 : "+HelperDataUsage.wDownloadedVideoSize);
                } else {
                    HelperDataUsage.dDownloadedVideoSize += downloadByte;
                }
                convetredDownloadType = ProtoGlobal.RoomMessageType.VIDEO;
                break;
            case IMAGE_TEXT:
            case IMAGE:
                if (connectivityType)
                    HelperDataUsage.wDownloadedImageSize += downloadByte;
                else
                    HelperDataUsage.dDownloadedImageSize += downloadByte;
                convetredDownloadType = ProtoGlobal.RoomMessageType.IMAGE;
                break;
            case FILE_TEXT:
            case FILE:
                if (connectivityType)
                    HelperDataUsage.wDownloadedFileSize += downloadByte;
                else
                    HelperDataUsage.dDownloadedFileSize += downloadByte;
                convetredDownloadType = ProtoGlobal.RoomMessageType.FILE;
                break;
            case AUDIO_TEXT:
            case VOICE:
            case AUDIO:
                if (connectivityType)
                    HelperDataUsage.wDownloadedAudioSize += downloadByte;
                else
                    HelperDataUsage.dDownloadedAudioSize += downloadByte;

                convetredDownloadType = ProtoGlobal.RoomMessageType.AUDIO;
                break;
            default:
                if (connectivityType)
                    HelperDataUsage.wDownloadedOtherSize += downloadByte;
                else
                    HelperDataUsage.dDownloadedOtherSize += downloadByte;

                convetredDownloadType = ProtoGlobal.RoomMessageType.UNRECOGNIZED;
                break;
        }
    }


    public static void progressUpload(boolean connectivityType, long nextOffset, ProtoGlobal.RoomMessageType type) {
        switch (type) {
            case VIDEO_TEXT:
            case VIDEO:
                if (connectivityType)
                    HelperDataUsage.wUploadedVideoSize += nextOffset;
                else
                    HelperDataUsage.dUploadedVideoSize += nextOffset;
                convetredUploadType = ProtoGlobal.RoomMessageType.VIDEO;
                break;
            case IMAGE_TEXT:
            case IMAGE:
                if (connectivityType)
                    HelperDataUsage.wUploadedImageSize += nextOffset;
                else
                    HelperDataUsage.dUploadedImageSize += nextOffset;
                convetredUploadType = ProtoGlobal.RoomMessageType.IMAGE;
                break;
            case FILE_TEXT:
            case FILE:
                if (connectivityType)
                    HelperDataUsage.wUploadedFileSize += nextOffset;
                else
                    HelperDataUsage.dUploadedFileSize += nextOffset;
                convetredUploadType = ProtoGlobal.RoomMessageType.FILE;
                break;
            case AUDIO_TEXT:
            case VOICE:
            case AUDIO:
                if (connectivityType)
                    HelperDataUsage.wUploadedAudioSize += nextOffset;
                else
                    HelperDataUsage.dUploadedAudioSize += nextOffset;
                convetredUploadType = ProtoGlobal.RoomMessageType.AUDIO;
                break;
            default:
                if (connectivityType)
                    HelperDataUsage.wUploadedOtherSize += nextOffset;
                else
                    HelperDataUsage.dUploadedOtherSize += nextOffset;
                convetredUploadType = ProtoGlobal.RoomMessageType.UNRECOGNIZED;
                break;

        }
    }

    public static void initializeRealmDataUsage() {
        String[] typeList = {"IMAGE", "VIDEO", "AUDIO", "FILE", "UNRECOGNIZED"};
        Realm realm = Realm.getDefaultInstance();
        for (int i = 0; i < typeList.length; i++) {
            int finalI = i;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmDataUsage realmDataUsage = realm.createObject(RealmDataUsage.class);


                    realmDataUsage.setNumUploadedFiles(0);
                    realmDataUsage.setNumDownloadedFile(0);

                    realmDataUsage.setConnectivityType(false);

                    realmDataUsage.setUploadSize(0);


                    realmDataUsage.setDownloadSize(0);


                    realmDataUsage.setType(typeList[finalI]);
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmDataUsage realmDataUsage = realm.createObject(RealmDataUsage.class);


                    realmDataUsage.setNumUploadedFiles(0);
                    realmDataUsage.setNumDownloadedFile(0);

                    realmDataUsage.setConnectivityType(true);

                    realmDataUsage.setUploadSize(0);


                    realmDataUsage.setDownloadSize(0);


                    realmDataUsage.setType(typeList[finalI]);
                }

            });

        }
        realm.close();

    }

    public static void clearUsageRealm(boolean connectivityType) {

        Realm realm = Realm.getDefaultInstance();
        if (connectivityType) {
            RealmResults<RealmDataUsage> wifiRealmList = realm.where(RealmDataUsage.class).equalTo("connectivityType", true).findAll();
            for (RealmDataUsage usage : wifiRealmList) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        usage.setDownloadSize(0);
                        usage.setUploadSize(0);
                        usage.setNumDownloadedFile(0);
                        usage.setNumUploadedFiles(0);
                    }
                });


            }
        } else {
            RealmResults<RealmDataUsage> dataRealmList = realm.where(RealmDataUsage.class).equalTo("connectivityType", false).findAll();
            for (RealmDataUsage usage : dataRealmList) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        usage.setUploadSize(0);
                        usage.setDownloadSize(0);
                        usage.setNumDownloadedFile(0);
                        usage.setNumUploadedFiles(0);
                    }
                });


            }

        }
        clearStatics();
        realm.close();
    }

    private static void clearStatics() {


        // WIFI USAGE
        wUploadedImageSize = 0;
        wUploadedVideoSize = 0;
        wUploadedFileSize = 0;
        wUploadedAudioSize = 0;
        wUploadedOtherSize = 0;

        wDownloadedImageSize = 0;
        wDownloadedVideoSize = 0;
        wDownloadedFileSize = 0;
        wDownloadedAudioSize = 0;
        wDownloadedOtherSize = 0;

        numDownloadImagesW = 0;
        numDownloadVideosW = 0;
        numDownloadFilesW = 0;
        numDownloadAudiosW = 0;
        numDownloadOthersW = 0;

        numUploadImagesW = 0;
        numUploadVideosW = 0;
        numUploadFilesW = 0;
        numUploadAudiosW = 0;
        numUploadOthersW = 0;


        // DATA USAGE
        dUploadedImageSize = 0;
        dUploadedVideoSize = 0;
        dUploadedFileSize = 0;
        dUploadedAudioSize = 0;
        dUploadedOtherSize = 0;

        dDownloadedImageSize = 0;
        dDownloadedVideoSize = 0;
        dDownloadedFileSize = 0;
        dDownloadedAudioSize = 0;
        dDownloadedOtherSize = 0;


        numDownloadImagesD = 0;
        numDownloadVideosD = 0;
        numDownloadFilesD = 0;
        numDownloadAudiosD = 0;
        numDownloadOthersD = 0;

        numUploadImagesD = 0;
        numUploadFilesD = 0;
        numUploadAudiosD = 0;
        numUploadOthersD = 0;
    }


}
