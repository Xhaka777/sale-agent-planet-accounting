package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tahirietrit on 3/20/18.
 */

public class BaseTargetResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public List<BaseTargetData> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public List<BaseTargetData> getData() {
        return data;
    }
}