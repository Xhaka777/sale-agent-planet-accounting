package org.planetaccounting.saleAgent.model.stock;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.planetaccounting.saleAgent.model.OrderDetailItem;

import java.util.ArrayList;

/**
 * Created by macb on 06/02/18.
 */

public class OrderDetailData {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("no_order")
    @Expose
    public String noOrder;
    @SerializedName("parite_name")
    @Expose
    public String pariteName;
    @SerializedName("parite_station_name")
    @Expose
    public String pariteStationName;
    @SerializedName("data")
    @Expose
    public String data;
    @SerializedName("warehoue_name")
    @Expose
    public String warehoueName;
    @SerializedName("items")
    @Expose
    public ArrayList<OrderDetailItem> items = null;

    public String getId() {
        return id;
    }

    public String getNoOrder() {
        return noOrder;
    }

    public String getPariteName() {
        return pariteName;
    }

    public String getPariteStationName() {
        return pariteStationName;
    }

    public String getData() {
        return data;
    }

    public String getWarehoueName() {
        return warehoueName;
    }

    public ArrayList<OrderDetailItem> getItems() {
        return items;
    }
}