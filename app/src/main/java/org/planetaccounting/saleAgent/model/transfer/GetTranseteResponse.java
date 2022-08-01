package org.planetaccounting.saleAgent.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.Error;

import java.util.ArrayList;

public class GetTranseteResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public ArrayList<GetTransfere> data;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public ArrayList<GetTransfere> getData() {
        return data;
    }
}
