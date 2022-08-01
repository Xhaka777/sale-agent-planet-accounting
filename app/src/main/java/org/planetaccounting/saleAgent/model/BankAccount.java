package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by macb on 04/02/18.
 */

public class BankAccount extends RealmObject{

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("bank_account_number")
    @Expose
    public String bankAccountNumber;


    public String getName() {
        return name;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getId(){return id;}
}