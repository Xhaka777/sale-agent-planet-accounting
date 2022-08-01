package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.stock.Brand;
import org.planetaccounting.saleAgent.model.stock.OrderDetailData;

import java.util.List;

/**
 * Created by tahirietrit on 4/1/18.
 */

public class BrandReponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public List<Brand> data;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public List<Brand> getData() {
        return data;
    }
}