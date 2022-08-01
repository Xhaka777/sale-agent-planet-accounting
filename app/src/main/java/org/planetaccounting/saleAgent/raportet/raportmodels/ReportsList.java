package org.planetaccounting.saleAgent.raportet.raportmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportsList {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("doc_number")
    @Expose
    public String docNumber;

    @SerializedName("partie_name")
    @Expose
    public String partieName;

    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("amount")
    @Expose
    public String amount;


    @SerializedName("type")
    @Expose
    public String type = "";

    @SerializedName("comment")
    @Expose
    public String comment = "";

    @SerializedName("unit")
    @Expose
    public String unit = "";


    @SerializedName("bank_name")
    @Expose
    public String bankName = "";

    @SerializedName("bank_account_number")
    @Expose
    public String bankAccountnumber = "";

    @SerializedName("branch")
    @Expose
    public String branch = "";











}
