package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DailyTrade extends RealmObject {

    @SerializedName("table")
    @Expose
    public TableDaily xs;

    @SerializedName("filter")
    @Expose
    public FilterDailyTrade filter;

    public TableDaily getTable() {
        return xs;
    }

    public FilterDailyTrade getFilter() {
        return filter;
    }
}