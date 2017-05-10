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
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.view.View;
import io.realm.Realm;
import io.realm.RealmResults;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnFileDownloadResponse;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAttachmentFields;
import net.iGap.request.RequestFileDownload;
import net.iGap.request.RequestWrapper;

public class HelperDownloadFile {

    private static ArrayMap<String, StructDownLoad> list = new ArrayMap<>();
    private OnFileDownloadResponse onFileDownloadResponse;

    private static int maxDownloadSize = 4;

    private static ArrayList<StructQueue> mQueue = new ArrayList<>();

    public HelperDownloadFile() {

        onFileDownloadResponse = new OnFileDownloadResponse() {
            @Override public void onFileDownload(String cashId, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {

                String PrimaryKey = cashId + selector;

                if (list.size() > 0 && list.containsKey(PrimaryKey)) {
                    StructDownLoad item = list.get(PrimaryKey);
                    item.offset = offset;
                    item.progress = progress;

                    if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE) {

                        if (mQueue.size() > 0) {
                            if (mQueue.get(0).priority > item.priority) {

                                if (item.priority < 3) {
                                    ++item.priority;
                                }

                                addItemToQueue(item.cashId + item.selector, item.priority);
                                addDownloadFromQueue();
                                return;
                            }
                        }
                    }

                    requestDownloadFile(item);
                }
            }

            @Override public void onError(int majorCode, int minorCode, String cashId, ProtoFileDownload.FileDownload.Selector selector) {

                String primaryKey = cashId + selector;

                if (list.size() > 0 && list.containsKey(primaryKey)) {
                    StructDownLoad item = list.get(primaryKey);

                    item.attampOnError--;
                    if (item.attampOnError >= 0) {
                        requestDownloadFile(item);
                    } else {

                        for (UpdateListener listener : item.listeners) {
                            if (listener != null) {
                                listener.OnError(item.Token);
                            }
                        }

                        list.remove(primaryKey);

                        // if (selector == ProtoFileDownload.FileDownload.Selector.FILE) {
                        addDownloadFromQueue();
                        //  }
                    }
                }
            }
        };

        G.onFileDownloadResponse = onFileDownloadResponse;
    }

    private static class StructDownLoad {
        public String Token = "";
        public String cashId = "";
        public ArrayList<UpdateListener> listeners = new ArrayList<>();
        public int progress = 0;
        public long offset = 0;
        public String name = "";
        public String moveToDirectoryPAth = "";
        public long size = 0;
        public String identity = "";
        public int attampOnError = 2;
        public ProtoFileDownload.FileDownload.Selector selector;
        public String path = "";
        int priority = 0;
    }

    private static class StructQueue {
        String primaryKey;
        int priority;
    }

    public interface UpdateListener {
        void OnProgress(String path, int progress);

        void OnError(String token);
    }

    private static boolean isNeedItemGoToQueue() {

        if (mQueue.size() > 0) return true;

        int count = 0;

        for (int i = 0; i < list.size(); i++) {

            StructDownLoad _sd = list.valueAt(i);
            // if (_sd.selector == ProtoFileDownload.FileDownload.Selector.FILE){
            count++;
            //  }
        }

        if ((count) > maxDownloadSize) return true;

        return false;
    }

    private static void addItemToQueue(String primaryKey, int priority) {

        boolean additem = false;

        StructQueue sq = new StructQueue();
        sq.priority = priority;
        sq.primaryKey = primaryKey;

        for (int i = mQueue.size() - 1; i >= 0; i--) {
            if (priority > mQueue.get(i).priority) {
                continue;
            } else {
                mQueue.add(i + 1, sq);
                additem = true;
                break;
            }
        }

        if (!additem) {
            mQueue.add(0, sq);
        }
    }

    public static void startDownload(String token, String cashId, String name, long size, ProtoFileDownload.FileDownload.Selector selector, String moveToDirectoryPAth, int periority,
        UpdateListener update) {

        StructDownLoad item;

        String primaryKey = cashId + selector;

        if (!list.containsKey(primaryKey)) {

            item = new StructDownLoad();
            item.Token = token;
            item.cashId = cashId;
            item.listeners.add(update);
            item.name = name;
            item.moveToDirectoryPAth = moveToDirectoryPAth;
            item.size = size;
            item.priority = periority;
            list.put(primaryKey, item);
        } else {
            item = list.get(primaryKey);
            item.listeners.add(update);
            updateView(item);

            return;
        }

        item.selector = selector;

        switch (item.selector) {
            case FILE:
                item.path = AndroidUtils.getFilePathWithCashId(item.cashId, item.name, G.DIR_TEMP, false);
                break;
            case SMALL_THUMBNAIL:
            case LARGE_THUMBNAIL:
                item.path = AndroidUtils.getFilePathWithCashId(item.cashId, item.name, G.DIR_TEMP, true);
                break;
        }

        File tmpFile = new File(item.path);

        if (tmpFile.exists()) {
            item.offset = tmpFile.length();

            if (item.offset > 0 && size > 0) {
                item.progress = (int) ((item.offset * 100) / size);
            }
        }

        if (moveToDirectoryPAth != null && moveToDirectoryPAth.length() > 0) {
            File _lockalFile = new File(moveToDirectoryPAth);
            if (_lockalFile.exists()) {
                item.progress = 100;
            }
        }

        if (item.progress < 100) {
            // if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE) {
            if (isNeedItemGoToQueue()) {
                addItemToQueue(primaryKey, periority);
                return;
            }
            // }
        }

        requestDownloadFile(item);
    }

    public static void stopDownLoad(String cashID) {

        String primaryKey = cashID + ProtoFileDownload.FileDownload.Selector.FILE;

        if (list.size() > 0 && list.containsKey(primaryKey)) {

            removeRequestQueue(list.get(primaryKey).identity);

            StructDownLoad item = list.get(primaryKey);

            if (item != null && item.listeners != null) {
                for (UpdateListener listener : item.listeners) {
                    if (listener != null) {
                        listener.OnError(item.Token);
                    }
                }
            }

            list.remove(primaryKey);

            addDownloadFromQueue();
        }
    }

    private static void addDownloadFromQueue() {

        // if any file exist in download queue add one to start download

        int count = mQueue.size();
        for (int i = 0; i < count; i++) {

            String _primaryKey = mQueue.get(0).primaryKey;
            mQueue.remove(0);

            if (list.size() > 0 && list.containsKey(_primaryKey)) {
                requestDownloadFile(list.get(_primaryKey));
                break;
            }
        }
    }

    private static void requestDownloadFile(final StructDownLoad item) {

        if (item.progress == 100 || item.offset >= item.size) {
            moveTmpFileToOrginFolder(item.Token, item.selector, item.cashId);

            updateView(item);

            list.remove(item.cashId + item.selector);

            // if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE){
            addDownloadFromQueue();
            // }

            // save downloaded file to gallery

            if (G.isSaveToGallery && item.selector == ProtoFileDownload.FileDownload.Selector.FILE && item.moveToDirectoryPAth != null) {
                File file = new File(item.moveToDirectoryPAth);
                if (file.exists()) {

                    if (HelperMimeType.isFileImage(item.moveToDirectoryPAth) || HelperMimeType.isFileVideo(item.moveToDirectoryPAth)) {
                        HelperSaveFile.savePicToGallary(item.moveToDirectoryPAth, false);
                    }
                }
            }

            return;
        }

        updateView(item);

        ProtoFileDownload.FileDownload.Selector selector = item.selector;
        item.identity = item.cashId + '*' + selector.toString() + '*' + item.size + '*' + item.path + '*' + item.offset + '*' + true;
        new RequestFileDownload().download(item.Token, item.offset, (int) item.size, selector, item.identity);
    }

    private static void moveTmpFileToOrginFolder(String token, ProtoFileDownload.FileDownload.Selector selector, String cashId) {

        StructDownLoad item = list.get(cashId + selector);

        if (item != null) {

            if (item.moveToDirectoryPAth.length() > 0) {
                try {

                    File _File = new File(item.moveToDirectoryPAth);
                    if (!_File.exists()) {
                        AndroidUtils.cutFromTemp(item.path, item.moveToDirectoryPAth);
                    }
                } catch (IOException e) {
                }
            }

            switch (item.selector) {
                case FILE:
                    setFilePAthToDataBaseAttachment(cashId, item.moveToDirectoryPAth);
                    break;
                case SMALL_THUMBNAIL:
                case LARGE_THUMBNAIL:
                    setThumbnailPathDataBaseAttachment(cashId, item.path);
                    break;
            }
        }
    }

    private static void setThumbnailPathDataBaseAttachment(final String cashID, final String path) {

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmResults<RealmAttachment> attachments = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.CACHE_ID, cashID).findAll();
                for (RealmAttachment attachment : attachments) {
                    attachment.setLocalThumbnailPath(path);
                }
            }
        });

        realm.close();
    }

    public static boolean isDownLoading(String cashID) {

        String primaryKey = cashID + ProtoFileDownload.FileDownload.Selector.FILE;

        if (list.containsKey(primaryKey)) return true;

        return false;
    }

    private static void setFilePAthToDataBaseAttachment(final String cashID, final String path) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmResults<RealmAttachment> attachments = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.CACHE_ID, cashID).findAll();

                for (RealmAttachment attachment : attachments) {
                    attachment.setLocalFilePath(path);
                }
            }
        });
        realm.close();
    }

    private static void updateView(final StructDownLoad item) {
        for (UpdateListener listener : item.listeners) {
            if (listener != null) {

                String _path = item.moveToDirectoryPAth.length() > 0 ? item.moveToDirectoryPAth : item.path;

                listener.OnProgress(_path, item.progress);
            }
        }
    }

    public static boolean removeRequestQueue(String identity) {
        for (Iterator<Map.Entry<String, RequestWrapper>> it = G.requestQueueMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RequestWrapper> entry = it.next();

            if (entry.getValue().identity != null && entry.getValue().identity.contains(identity)) {
                G.requestQueueMap.remove(entry.getKey());
                return true;
            }
        }
        return false;
    }

    private void onError(int majorCode, int minorCode, final Context context) {
        if (majorCode == 713 && minorCode == 1) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_1), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 2) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_2), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 3) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_3), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 4) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_4), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 5) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_5), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 714) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_714), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 715) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_715), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }
}
