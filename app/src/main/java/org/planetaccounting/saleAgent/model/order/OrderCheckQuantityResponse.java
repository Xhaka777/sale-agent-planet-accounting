package org.planetaccounting.saleAgent.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.Error;

public class OrderCheckQuantityResponse {
    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;


    @SerializedName("current_quantity")
    @Expose
    public double currentQuantity;


   public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public double getCurrentQuantity() {
        return currentQuantity;
    }
}
