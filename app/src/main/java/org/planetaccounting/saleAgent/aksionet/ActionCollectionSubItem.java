package org.planetaccounting.saleAgent.aksionet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SHB on 2/16/2018.
 */

public class ActionCollectionSubItem extends RealmObject {

    @SerializedName("item_id")
    @Expose
    public String item_id;
    @SerializedName("quantity")
    @Expose
    public String quantity;
    @SerializedName("discount")
    @Expose
    public String discount;

    public String getItem_id() {
        return item_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDiscount() {
        return discount;
    }
}
