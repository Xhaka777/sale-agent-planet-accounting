package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tahirietrit on 3/23/18.
 */

public class BrandTargetData {

    @SerializedName("brand_details")
    @Expose
    public List<BrandDetail> brandDetails = null;
    @SerializedName("article_details")
    @Expose
    public List<BrandDetail> articleDetails = null;
    @SerializedName("brand_total")
    @Expose
    public BrandTotal brandTotal;
    @SerializedName("article_total")
    @Expose
    public BrandTotal articleTotal;

    public List<BrandDetail> getBrandDetails() {
        return brandDetails;
    }

    public BrandTotal getBrandTotal() {
        return brandTotal;
    }

    public List<BrandDetail> getArticleDetails() {
        return articleDetails;
    }

    public BrandTotal getArticleTotal() {
        return articleTotal;
    }
}