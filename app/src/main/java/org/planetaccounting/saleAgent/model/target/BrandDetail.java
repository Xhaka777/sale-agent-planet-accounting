package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tahirietrit on 3/23/18.
 */

public class BrandDetail {

    @SerializedName("brand_id")
    @Expose
    public String brandId;
    @SerializedName("article_id")
    @Expose
    public String articleId;
    @SerializedName("brand_name")
    @Expose
    public String brandName;
    @SerializedName("article_name")
    @Expose
    public String articlName;
    @SerializedName("article_number")
    @Expose
    public String articleNumber;
    @SerializedName("article_unit")
    @Expose
    public String articleUnit;
    @SerializedName("target")
    @Expose
    public String target;
    @SerializedName("period")
    @Expose
    public List<Period> period = null;
    @SerializedName("total_sale")
    @Expose
    public float totalSale;
    @SerializedName("target_percentage")
    @Expose
    public Double targetPercentage;
    @SerializedName("benefit")
    @Expose
    public float benefit;

    public String getBrandId() {
        return brandId;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getTarget() {
        return target;
    }

    public List<Period> getPeriod() {
        return period;
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

    public String getArticlName() {
        return articlName;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public String getArticleUnit() {
        return articleUnit;
    }
}