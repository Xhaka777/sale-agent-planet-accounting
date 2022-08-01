package org.planetaccounting.saleAgent.model.ngarkimet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.Error;

import java.util.ArrayList;

public class UploadDetailResponse {
    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public ArrayList<UploadsDetailItem> data;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public ArrayList<UploadsDetailItem> getData() {
        return data;
    }
}
