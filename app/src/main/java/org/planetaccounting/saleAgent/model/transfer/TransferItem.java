package org.planetaccounting.saleAgent.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferItem {
    @SerializedName("number")
    @Expose
    public String number;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("unit")
    @Expose
    public String unit;

    @SerializedName("quantity")
    @Expose
    public String quantity;


    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getQuantity() {
        return quantity;
    }
}
