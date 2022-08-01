package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by macb on 07/12/17.
 */

public class Error {

    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("line")
    @Expose
    public String line;

    public String getText() {
        return text;
    }

    public String getLine() {
        return line;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLine(String line) {
        this.line = line;
    }
}