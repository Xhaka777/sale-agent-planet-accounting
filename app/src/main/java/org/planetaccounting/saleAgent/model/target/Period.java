package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tahirietrit on 3/20/18.
 */

public class Period {

    @SerializedName("data")
    @Expose
    public String data;
    @SerializedName("amount")
    @Expose
    public String amount;

    @SerializedName("percent")
    @Expose
    public float percent;



    public String getData() {
        return data;
    }

    public String getAmount() {
        return amount;
    }

    public float getPercent() {
        return percent;
    }
}