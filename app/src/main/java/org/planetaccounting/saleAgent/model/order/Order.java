package org.planetaccounting.saleAgent.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by macb on 06/02/18.
 */

public class Order {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("no_order")
    @Expose
    public String noOrder;
    @SerializedName("parite_name")
    @Expose
    public String pariteName;
    @SerializedName("data")
    @Expose
    public String data;
    @SerializedName("parite_station_name")
    @Expose
    public String pariteStationName;

    @SerializedName("warehouse")
    @Expose
    public String warehouse;

    @SerializedName("client")
    @Expose
    public String client;

    @SerializedName("client_station")
    @Expose
    public String clientStation;



    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("amount")
    @Expose
    public String amount;

    @SerializedName("cancel_allowed")
    @Expose
    public int cancelAllowed;










    public String getId() {
        return id;
    }

    public String getNoOrder() {
        return noOrder;
    }

    public String getPariteName() {
        return pariteName;
    }

    public String getData() {
        return data;
    }

    public String getPariteStationName() {
        return pariteStationName;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public String getClient() {
        return client;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public int getCancelAllowed() {
        return cancelAllowed;
    }

    public String getClientStation() {
        return clientStation;
    }
}