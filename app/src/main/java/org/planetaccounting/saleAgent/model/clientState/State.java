package org.planetaccounting.saleAgent.model.clientState;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class State extends RealmObject {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
