package org.lamuela.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Voting {

    @Expose
    @SerializedName("id")
    private Integer id;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("desc")
    private String desc;
    @Expose
    @SerializedName("question")
    private Question question;
    @Expose
    @SerializedName("start_date")
    private String startDate;
    @Expose
    @SerializedName("end_date")
    private String endDate;
    @Expose
    @SerializedName("pub_key")
    private PubKey pubKey;
    @Expose
    @SerializedName("auths")
    private List<Auth> auths = null;

    @Expose
    @SerializedName("postproc")
    private List<Postproc> postproc = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public PubKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
    }

    public List<Auth> getAuths() {
        return auths;
    }

    public void setAuths(List<Auth> auths) {
        this.auths = auths;
    }

    public List<Postproc> getPostproc() {
        return postproc;
    }

    public void setPostproc(List<Postproc> postproc) {
        this.postproc = postproc;
    }

}
