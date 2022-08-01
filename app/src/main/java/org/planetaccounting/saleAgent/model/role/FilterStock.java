package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FilterStock extends RealmObject {

    @PrimaryKey
    @SerializedName("search")
    @Expose
    public int search;

    public int getSearch() {
        return search;
    }
}