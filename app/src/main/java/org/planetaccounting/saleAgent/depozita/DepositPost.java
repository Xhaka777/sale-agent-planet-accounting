package org.planetaccounting.saleAgent.depozita;

import org.planetaccounting.saleAgent.raportet.raportmodels.ReportsList;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SHB on 2/14/2018.
 */

public class DepositPost extends RealmObject {

    @PrimaryKey
    int id;

    String token;
    String user_id;
    String bank_id;
    String emri_bankes;
    String date;
    String amount;
    String branch;
    String referenc_no;
    String comment;
    boolean isSynced;

    public DepositPost(String token, String user_id, String bank_id, String date, String amount, String branch, String referenc_no, String comment) {
        this.token = token;
        this.user_id = user_id;
        this.bank_id = bank_id;
        this.date = date;
        this.amount = amount;
        this.branch = branch;
        this.referenc_no = referenc_no;
        this.comment = comment;
    }

    public DepositPost(ReportsList reports) {
        this.id = reports.id;

        this.bank_id = reports.bankAccountnumber;
        this.emri_bankes = reports.bankName;
        this.date =reports.date;
        this.amount = reports.amount;
        this.comment = reports.comment;
        this.isSynced = true;
        this.branch = reports.branch;
    }
    public DepositPost() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getBank_id() {
        return bank_id;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        try {
            return Double.parseDouble(amount);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getBranch() {
        return branch;
    }

    public String getReferenc_no() {
        return referenc_no;
    }

    public String getComment() {
        return comment;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public String getEmri_bankes() {
        return emri_bankes;
    }

    public void setEmri_bankes(String emri_bankes) {
        this.emri_bankes = emri_bankes;
    }
}
