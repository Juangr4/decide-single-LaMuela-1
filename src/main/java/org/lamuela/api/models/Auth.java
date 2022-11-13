package org.lamuela.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Auth {

    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("url")
    private String url;
    @Expose
    @SerializedName("me")
    private Boolean me;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getMe() {
        return me;
    }

    public void setMe(Boolean me) {
        this.me = me;
    }

}
