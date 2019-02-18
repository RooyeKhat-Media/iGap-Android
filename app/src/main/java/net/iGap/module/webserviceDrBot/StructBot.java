package net.iGap.module.webserviceDrBot;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StructBot {

    @SerializedName("result")
    @Expose
    private Integer result;
    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("favorite")
    @Expose
    private List<Favorite> favorite = null;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<Favorite> getFavorite() {
        return favorite;
    }

    public void setFavorite(List<Favorite> favorite) {
        this.favorite = favorite;
    }
}
