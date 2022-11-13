package org.lamuela.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

public class PubKey {

    @Expose
    @SerializedName("p")
    private BigInteger p;

    @Expose
    @SerializedName("g")
    private BigInteger g;

    @Expose
    @SerializedName("y")
    private BigInteger y;

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }
}
