package org.planetaccounting.saleAgent.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.Error;

public class BaseTranserResponse {
    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }
}
