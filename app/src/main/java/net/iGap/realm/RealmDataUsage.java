package net.iGap.realm;

import io.realm.RealmObject;

public class RealmDataUsage extends RealmObject {
    private String type;
   /* private long wifiDownloadSize;
    private long dataDownloadSize;*/
    private long downloadSize;

   /* private long wifiUploadSize;
    private long dataUploadSize;*/
    private long uploadSize;

    private boolean connectivityType;
    private int numUploadedFiles;
    private int numDownloadedFile;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public long getUploadSize() {
        return uploadSize;
    }

    public void setUploadSize(long uploadSize) {
        this.uploadSize = uploadSize;
    }

    public boolean isConnectivityType() {
        return connectivityType;
    }

    public void setConnectivityType(boolean connectivityType) {
        this.connectivityType = connectivityType;
    }

    public int getNumUploadedFiles() {
        return numUploadedFiles;
    }

    public void setNumUploadedFiles(int numUploadedFiles) {
        this.numUploadedFiles = numUploadedFiles;
    }

    public int getNumDownloadedFile() {
        return numDownloadedFile;
    }

    public void setNumDownloadedFile(int numDownloadedFile) {
        this.numDownloadedFile = numDownloadedFile;
    }
}
