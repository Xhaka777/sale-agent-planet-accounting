package org.planetaccounting.saleAgent.model.ngarkimet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Uploads {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("in")
    @Expose
    public String in;

    @SerializedName("number")
    @Expose
    public String number;

    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("description")
    @Expose
    public String description;


    @SerializedName("station_name_from")
    @Expose
    public String stationNameFrom;

    @SerializedName("station_name_to")
    @Expose
    public String stationNameTo;

    @SerializedName("employee_from")
    @Expose
    public String employeeFrom;

    @SerializedName("employee_to")
    @Expose
    public String employeeTo;


    public String getId() {
        return id;
    }

    public String getIn() {
        return in;
    }

    public String getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getStationNameFrom() {
        return stationNameFrom;
    }

    public String getStationNameTo() {
        return stationNameTo;
    }

    public String getEmployeeFrom() {
        return employeeFrom;
    }

    public String getEmployeeTo() {
        return employeeTo;
    }
}
