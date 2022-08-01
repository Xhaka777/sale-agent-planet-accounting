package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tahirietrit on 4/3/18.
 */

public class SkuTotal {

    @SerializedName("target")
    @Expose
    public String target;
    @SerializedName("total_sale")
    @Expose
    public String totalSale;
    @SerializedName("target_percentage")
    @Expose
    public String targetPercentage;
    @SerializedName("benefit")
    @Expose
    public String benefit;

    public String getTarget() {
        return target;
    }

    public String getTotalSale() {
        return totalSale;
    }

    public String getTargetPercentage() {
        return targetPercentage;
    }

    public String getBenefit() {
        return benefit;
    }
}