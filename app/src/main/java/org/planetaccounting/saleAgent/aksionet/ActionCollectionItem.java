package org.planetaccounting.saleAgent.aksionet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SHB on 2/16/2018.
 */

public class ActionCollectionItem extends RealmObject {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("from")
    @Expose
    public String from;
    @SerializedName("to")
    @Expose
    public String to;
    @SerializedName("client_category")
    @Expose
    public String clientCategory;
    @SerializedName("client_category_name")
    @Expose
    public String clientCategoryName;
    @SerializedName("items")
    @Expose
    public RealmList<ActionCollectionSubItem> items = null;

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getClientCategory() {
        return clientCategory;
    }

    public String getClientCategoryName() {
        return clientCategoryName;
    }

    public List<ActionCollectionSubItem> getItems() {
        return items;
    }
}
