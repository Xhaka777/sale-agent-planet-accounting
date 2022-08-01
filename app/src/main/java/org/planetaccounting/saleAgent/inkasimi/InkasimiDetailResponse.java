package org.planetaccounting.saleAgent.inkasimi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.Error;

/**
 * Created by SHB on 2/11/2018.
 */

public class InkasimiDetailResponse {

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
