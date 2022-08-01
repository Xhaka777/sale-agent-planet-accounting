package org.planetaccounting.saleAgent.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by macb on 07/12/17.
 */

public class CompanyAllowed {

    @SerializedName("companyID")
    @Expose
    public String companyID;
    @SerializedName("companyName")
    @Expose
    public String companyName;
    @SerializedName("logo")
    @Expose
    public String logo;

    public String getCompanyID() {
        return companyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLogo() {
        return logo;
    }
}
