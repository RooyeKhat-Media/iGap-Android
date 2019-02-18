package net.iGap.module.webserviceDrBot;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Favorite {
    @SerializedName("favoriteImage")
    @Expose
    private String favoriteImage;
    @SerializedName("favoriteEnable")
    @Expose
    private Boolean favoriteEnable;
    @SerializedName("favoriteValue")
    @Expose
    private String favoriteValue;
    @SerializedName("favoriteName")
    @Expose
    private String favoriteName;
    @SerializedName("favoriteOrderId")
    @Expose
    private Integer favoriteOrderId;
    @SerializedName("favoriteColor")
    @Expose
    private String favoriteColor;

    @SerializedName("favoriteBgColor")
    @Expose
    private String favoriteBgColor;

    public String getFavoriteImage() {
        return favoriteImage;
    }

    public void setFavoriteImage(String favoriteImage) {
        this.favoriteImage = favoriteImage;
    }

    public Boolean getFavoriteEnable() {
        return favoriteEnable;
    }

    public void setFavoriteEnable(Boolean favoriteEnable) {
        this.favoriteEnable = favoriteEnable;
    }

    public String getFavoriteValue() {
        return favoriteValue;
    }

    public void setFavoriteValue(String favoriteValue) {
        this.favoriteValue = favoriteValue;
    }

    public String getFavoriteName() {
        return favoriteName;
    }

    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
    }

    public Integer getFavoriteOrderId() {
        return favoriteOrderId;
    }

    public void setFavoriteOrderId(Integer favoriteOrderId) {
        this.favoriteOrderId = favoriteOrderId;
    }

    public String getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(String favoriteColor) {
        this.favoriteColor = favoriteColor;
    }

    public String getFavoriteBgColor() {
        return favoriteBgColor;
    }

    public void setFavoriteBgColor(String favoriteBgColor) {
        this.favoriteBgColor = favoriteBgColor;
    }
}
