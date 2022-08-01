package org.planetaccounting.saleAgent.raportet.raportmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.Error;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;

public class GetInvoiceForReportResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public InvoicePost data;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public InvoicePost getData() {
        return data;
    }
}
