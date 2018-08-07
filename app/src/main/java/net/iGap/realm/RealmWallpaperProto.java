package net.iGap.realm;

import io.realm.RealmObject;

public class RealmWallpaperProto extends RealmObject {

    private RealmAttachment file;
    private String color;

    public RealmAttachment getFile() {
        return file;
    }

    public void setFile(RealmAttachment file) {
        this.file = file;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
