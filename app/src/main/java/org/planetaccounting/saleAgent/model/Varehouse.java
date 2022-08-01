package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by macb on 06/02/18.
 */

public class Varehouse {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("number")
    @Expose
    public String number;
    @SerializedName("name")
    @Expose
    public String name;

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
