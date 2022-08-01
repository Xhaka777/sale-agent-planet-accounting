package org.planetaccounting.saleAgent.aksionet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ActionSteps extends RealmObject {


    @SerializedName("from")
    @Expose
    public String from;


    @SerializedName("to")
    @Expose
    public String to;

    @SerializedName("discount")
    @Expose
    public String discount;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDiscount() {
        return discount;
    }
}
