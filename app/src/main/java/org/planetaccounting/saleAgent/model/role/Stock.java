package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Stock extends RealmObject {

    @SerializedName("table")
    @Expose
    public TableStock tableS;

    @SerializedName("filter")
    @Expose
    public FilterStock filter;

    public TableStock getTable() {
        return tableS;
    }

    public FilterStock getFilter() {
        return filter;
    }
}
