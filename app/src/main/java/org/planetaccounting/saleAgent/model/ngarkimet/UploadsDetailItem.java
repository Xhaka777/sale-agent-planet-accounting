package org.planetaccounting.saleAgent.model.ngarkimet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadsDetailItem {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("unit")
    @Expose
    public String unit;
    @SerializedName("number")
    @Expose
    public String number;
    @SerializedName("quantity")
    @Expose
    public String quantity;

    @SerializedName("barcode")
    @Expose
    public String barcode;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getNumber() {
        return number;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getBarcode() {
        return barcode;
    }
}
