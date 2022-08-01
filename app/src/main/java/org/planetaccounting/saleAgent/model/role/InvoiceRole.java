package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class InvoiceRole extends RealmObject {

    @SerializedName("form")
    @Expose
    public Form form;

    public Form getForm() {
        return form;
    }
}