package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tahirietrit on 3/20/18.
 */

public class TotalSaleTargetResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public TotalSaleTargetData data;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public TotalSaleTargetData getData() {
        return data;
    }
}