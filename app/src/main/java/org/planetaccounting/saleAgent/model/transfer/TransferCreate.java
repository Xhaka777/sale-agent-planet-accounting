package org.planetaccounting.saleAgent.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TransferCreate {
    @SerializedName("warehouse_id")
    @Expose
    public String warehouseId;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("items")
    @Expose
    public ArrayList<TransferCreateItem>items;

    public TransferCreate(String warehouseId, String description, ArrayList<TransferCreateItem>items) {
        this.warehouseId = warehouseId;
        this.description = description;
        this.items = items;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<TransferCreateItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<TransferCreateItem> items) {
        this.items = items;
    }
}
