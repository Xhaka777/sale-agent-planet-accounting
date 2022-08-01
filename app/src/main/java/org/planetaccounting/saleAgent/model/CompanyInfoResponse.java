package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.*;

/**
 * Created by macb on 04/02/18.
 */

public class CompanyInfoResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public CompanyInfo companyInfo;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }
}