package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tahirietrit on 4/3/18.
 */

public class SkuData {

    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("details")
    @Expose
    public List<SkuDetail> details = null;
    @SerializedName("total")
    @Expose
    public SkuTotal total;
    @SerializedName("target_percentage")
    @Expose
    public String targetPercentage;
    @SerializedName("benefit")
    @Expose
    public String benefit;

    public String getType() {
        return type;
    }

    public List<SkuDetail> getDetails() {
        return details;
    }

    public SkuTotal getTotal() {
        return total;
    }

    public String getTargetPercentage() {
        return targetPercentage;
    }

    public String getBenefit() {
        return benefit;
    }
}