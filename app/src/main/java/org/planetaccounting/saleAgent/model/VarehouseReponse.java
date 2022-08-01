package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.*;
import java.util.List;

/**
 * Created by macb on 06/02/18.
 */

public class VarehouseReponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("stations")
    @Expose
    public List<Varehouse> stations = null;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public List<Varehouse> getStations() {
        return stations;
    }
}
