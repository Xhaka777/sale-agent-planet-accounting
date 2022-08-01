package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tahirietrit on 3/23/18.
 */

public class BrandTotal {

    @SerializedName("target")
    @Expose
    public Integer target;
    @SerializedName("total_sale")
    @Expose
    public float totalSale;
    @SerializedName("target_percentage")
    @Expose
    public Double targetPercentage;
    @SerializedName("benefit")
    @Expose
    public float benefit;

    public Integer getTarget() {
        return target;
    }

    public float getTotalSale() {
        return totalSale;
    }

    public Double getTargetPercentage() {
        return targetPercentage;
    }

    public float getBenefit() {
        return benefit;
    }
}