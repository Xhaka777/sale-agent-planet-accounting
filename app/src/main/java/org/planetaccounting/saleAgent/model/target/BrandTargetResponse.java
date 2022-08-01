package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tahirietrit on 3/23/18.
 */

public class BrandTargetResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public BrandTargetData data;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public BrandTargetData getData() {
        return data;
    }
}