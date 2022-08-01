package org.planetaccounting.saleAgent.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTransfere {
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

    @SerializedName("to_station_id")
    @Expose
    public String toStationId;

    @SerializedName("from_station_name")
    @Expose
    public String fromStationName;

    @SerializedName("type")
    @Expose
    public String type;


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

    public String getFromStationName() {
        return fromStationName;
    }

    public String getFromStationId() {
        return fromStationId;
        }


    public String getType() {
        return type;
    }

    public String getToStationId() {
        return toStationId;
    }
}
