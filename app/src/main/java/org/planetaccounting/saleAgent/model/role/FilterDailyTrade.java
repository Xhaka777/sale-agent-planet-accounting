package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FilterDailyTrade extends RealmObject {

    @PrimaryKey
    @SerializedName("date")
    @Expose
    public int date;

    public int getDate() {
        return date;
    }
}


