package net.iGap.realm;

import io.realm.RealmObject;

public class RealmIceServer extends RealmObject {

    private String url;
    private String username;
    private String credential;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
