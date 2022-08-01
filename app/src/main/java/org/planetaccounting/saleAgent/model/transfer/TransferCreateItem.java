package org.planetaccounting.saleAgent.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferCreateItem {
    @SerializedName("item_id")
    @Expose
    public String itemId;

    @SerializedName("item_group_id")
    @Expose
    public String itemGroupId;

    @SerializedName("unit")
    @Expose
    public String unit;

    @SerializedName("quantity")
    @Expose
    public String quantity;

    public TransferCreateItem(String itemId, String itemGroupId, String unit, String quantity) {
        this.itemId = itemId;
        this.itemGroupId = itemGroupId;
        this.unit = unit;
        this.quantity = quantity;
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemGroupId() {
        return itemGroupId;
    }

    public void setItemGroupId(String itemGroupId) {
        this.itemGroupId = itemGroupId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
