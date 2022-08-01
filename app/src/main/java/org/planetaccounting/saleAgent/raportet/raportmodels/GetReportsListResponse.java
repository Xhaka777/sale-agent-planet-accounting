package org.planetaccounting.saleAgent.raportet.raportmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.Error;

import java.util.ArrayList;

public class GetReportsListResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public ArrayList<ReportsList> data;

    @SerializedName("totalPage")
    @Expose
    public int totalPage;

    @SerializedName("currentPage")
    @Expose
    public int currentPage;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public ArrayList<ReportsList> getData() {
        return data;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
