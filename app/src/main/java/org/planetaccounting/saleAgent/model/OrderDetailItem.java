package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by macb on 06/02/18.
 */

public class OrderDetailItem {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("unit")
    @Expose
    public String unit;
    @SerializedName("no")
    @Expose
    public String number;
    @SerializedName("quantity")
    @Expose
    public String quantity;

    @SerializedName("price")
    @Expose
    public float price;


    @SerializedName("barcode")
    @Expose
    public String barcode;


    @SerializedName("amount")
    @Expose
    public float amount;





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

    public float getPrice() {
        return price;
    }

    public String getBarcode() {
        return barcode;
    }

    public float getAmount() {
        return amount;
    }

    public String getQuantity() {
        return quantity;
    }
}
