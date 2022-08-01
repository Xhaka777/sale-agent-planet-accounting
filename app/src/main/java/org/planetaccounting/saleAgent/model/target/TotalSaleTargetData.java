package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tahirietrit on 3/20/18.
 */

public class TotalSaleTargetData {

    @SerializedName("total_sale_target")
    @Expose
    public TotalSaleTarget totalSaleTarget;
    @SerializedName("cash_collection")
    @Expose
    public TotalSaleTarget cashCollection;

    public TotalSaleTarget getTotalSaleTarget() {
        return totalSaleTarget;
    }

    public TotalSaleTarget getCashCollection() {
        return cashCollection;
    }
}