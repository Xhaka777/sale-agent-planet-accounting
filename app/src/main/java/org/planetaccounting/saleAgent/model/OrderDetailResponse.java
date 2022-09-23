package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.planetaccounting.saleAgent.model.stock.OrderDetailData;

import java.lang.*;
import java.util.ArrayList;

/**
 * Created by macb on 06/02/18.
 */

public class OrderDetailResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public ArrayList<OrderDetailItem> data;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public ArrayList<OrderDetailItem> getData() {
        return data;
    }
}