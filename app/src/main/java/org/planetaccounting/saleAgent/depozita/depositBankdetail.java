package org.planetaccounting.saleAgent.depozita;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.OrderDetailItem;

import java.util.ArrayList;

import io.realm.annotations.PrimaryKey;

/**
 * Created by SHB on 2/14/2018.
 */

public class depositBankdetail {

    @SerializedName("bank_account_number")
    @Expose
    public String bank_account_number;
    @SerializedName("name")
    @Expose
    public String name;
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public String id;


    public String getbank_account_number() {
        return bank_account_number;
    }

}
