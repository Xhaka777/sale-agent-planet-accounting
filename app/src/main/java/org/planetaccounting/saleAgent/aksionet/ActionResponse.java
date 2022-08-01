package org.planetaccounting.saleAgent.aksionet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by SHB on 2/16/2018.
 */

public class ActionResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public ActionData data;


    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public ActionData getData() {
        return data;
    }
}
