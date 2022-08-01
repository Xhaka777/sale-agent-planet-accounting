package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.*;

/**
 * Created by macb on 05/02/18.
 */

public class InvoiceUploadResponse {

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
