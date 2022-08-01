package org.planetaccounting.saleAgent.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.Error;
import org.planetaccounting.saleAgent.model.ErrorPost;


public class GetTransferCreateResposnse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("transfer_id")
    @Expose
    public String transfer_id;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public String getTransfer_id() {
        return transfer_id;
    }
}
