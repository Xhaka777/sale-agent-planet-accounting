package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tahirietrit on 3/20/18.
 */

public class TotalSaleTarget {

    @SerializedName("target")
    @Expose
    public String target;
    @SerializedName("period")
    @Expose
    public List<Period> period = null;
    @SerializedName("total_sale")
    @Expose
    public String totalSale;
    @SerializedName("target_percentage")
    @Expose
    public String targetPercentage;
    @SerializedName("total_collection")
    @Expose
    public String totalCollection;
    @SerializedName("benefit")
    @Expose
    public String benefit;

    public String getTarget() {
        return target;
    }

    public List<Period> getPeriod() {
        return period;
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

    public String getTotalCollection() {
        return totalCollection;
    }
}