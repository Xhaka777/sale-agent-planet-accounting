package org.planetaccounting.saleAgent.model.ngarkimet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UploadsResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public ArrayList<Uploads> data;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public ArrayList<Uploads> getData() {
        return data;
    }
}
