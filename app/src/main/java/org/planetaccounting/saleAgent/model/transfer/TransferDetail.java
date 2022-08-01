package org.planetaccounting.saleAgent.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TransferDetail {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("number")
    @Expose
    public int number;

    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("from_station_id")
    @Expose
    public String fromStationId;

    @SerializedName("items")
    @Expose
    public ArrayList<TransferItem> items;

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public ArrayList<TransferItem> getItems() {
        return items;
    }
}
