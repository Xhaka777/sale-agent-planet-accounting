package org.planetaccounting.saleAgent.model.target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tahirietrit on 4/3/18.
 */

public class SkuDetail {

    @SerializedName("article_id")
    @Expose
    public String articleId;
    @SerializedName("article_name")
    @Expose
    public String articleName;
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
    @SerializedName("benefit")
    @Expose
    public String benefit;
    @SerializedName("brand_id")
    @Expose
    public String brandId;
    @SerializedName("brand_name")
    @Expose
    public String brandName;
    @SerializedName("category_id")
    @Expose
    public String categoryId;
    @SerializedName("category_name")
    @Expose
    public String categoryName;
    @SerializedName("subcategory_id")
    @Expose
    public String subcategoryId;
    @SerializedName("subcategory_name")
    @Expose
    public String subcategoryName;
    @SerializedName("manufacturer_id")
    @Expose
    public String manufacturerId;
    @SerializedName("manufacturer_name")
    @Expose
    public String manufacturerName;

    public String getArticleId() {
        return articleId;
    }

    public String getArticleName() {
        return articleName;
    }

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

    public String getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }
}