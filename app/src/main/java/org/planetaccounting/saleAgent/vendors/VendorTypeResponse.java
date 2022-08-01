package org.planetaccounting.saleAgent.vendors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.Error;

import java.util.List;

/**
 * Created by tahirietrit on 3/15/18.
 */

public class VendorTypeResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public List<VendorType> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public List<VendorType> getData() {
        return data;
    }

    public void setData(List<VendorType> data) {
        this.data = data;
    }

    public String[] getStringArray() {
        String[] stringData = new String[data.size()];

        for (int i = 0; i < data.size(); i++) {
            stringData[i] = data.get(i).getName();
        }
        return stringData;
    }
}