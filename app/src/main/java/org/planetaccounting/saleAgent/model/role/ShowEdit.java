package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ShowEdit extends RealmObject {

    @SerializedName("show")
    @Expose
    public int show;

    @SerializedName("editable")
    @Expose
    public int editable;


    public int getShow() {
        return show;
    }

    public int getEditable() {
        return editable;
    }
}
