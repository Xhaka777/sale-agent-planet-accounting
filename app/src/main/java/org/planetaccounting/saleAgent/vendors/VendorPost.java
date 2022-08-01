package org.planetaccounting.saleAgent.vendors;

import org.planetaccounting.saleAgent.raportet.raportmodels.ReportsList;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by tahirietrit on 3/15/18.
 */

public class VendorPost  extends RealmObject{
    @PrimaryKey
    int id;
    String parite_id;
    String name;
    String amount;
    String date;
    String comment;
    String no_invoice;
    String furnitori;
    String type;
    String typeName;
    boolean isSynced;

    public VendorPost(String parite_id, String type, String amount,
                      String date, String comment, String no_invoice) {
        this.parite_id = parite_id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.comment = comment;
        this.no_invoice = no_invoice;
    }

    public VendorPost(ReportsList reports) {
        this.id = reports.id;
        this.furnitori = reports.partieName;
        this.type = reports.type;
        this.amount = reports.amount;
        this.date =reports.date;
        this.comment = reports.comment;
        this.no_invoice = reports.docNumber;
        this.isSynced = true;
    }


    public VendorPost(){}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getParite_id() {
        return parite_id;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return Double.parseDouble(amount);
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public String getNo_invoice() {
        return no_invoice;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public String getFurnitori() {
        return furnitori;
    }

    public void setFurnitori(String furnitori) {
        this.furnitori = furnitori;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
