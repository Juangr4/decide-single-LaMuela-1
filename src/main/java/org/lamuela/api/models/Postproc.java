package org.lamuela.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Postproc {

    @SerializedName("votes")
    @Expose
    private Integer votes;
    @SerializedName("number")
    @Expose
    private Integer number;
    @Expose
    @SerializedName("option")
    private String option;
    @Expose
    @SerializedName("postproc")
    private Integer postproc;

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Integer getPostproc() {
        return postproc;
    }

    public void setPostproc(Integer postproc) {
        this.postproc = postproc;
    }

}
