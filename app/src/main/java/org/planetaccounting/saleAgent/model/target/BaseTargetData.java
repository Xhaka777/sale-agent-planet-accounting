package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tahirietrit on 3/20/18.
 */

public class BaseTargetData {

    @SerializedName("target")
    @Expose
    public String target;
    @SerializedName("target_percentage")
    @Expose
    public String targetPercentage;
    @SerializedName("benefit")
    @Expose
    public String benefit;
    @SerializedName("total_sale")
    @Expose
    public String totalSale;
    @SerializedName("base_wage")
    @Expose
    public Integer baseWage;

    public String getTarget() {
        return target;
    }

    public String getTargetPercentage() {
        return targetPercentage;
    }

    public String getBenefit() {
        return benefit;
    }

    public String getTotalSale() {
        return totalSale;
    }

    public Integer getBaseWage() {
        return baseWage;
    }
}
