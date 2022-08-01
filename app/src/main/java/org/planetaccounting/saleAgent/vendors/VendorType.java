package org.planetaccounting.saleAgent.vendors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by tahirietrit on 3/15/18.
 */

public class VendorType extends RealmObject {
    @PrimaryKey
    @SerializedName("account")
    @Expose
    public String account;
    @SerializedName("name")
    @Expose
    public String name;

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }
}