package org.planetaccounting.saleAgent.inkasimi;

import org.planetaccounting.saleAgent.raportet.raportmodels.ReportsList;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SHB on 2/11/2018.
 */

public class
InkasimiDetail extends RealmObject {

    @PrimaryKey
    int id;
    String parite_id;
    String klienti;
    String njesia;
    String parite_station_id;
    String amount;
    String date;
    String comment;
    boolean isSynced;

    public InkasimiDetail( String parite_id, String parite_station_id,
                          String amount, String date, String comment) {
        this.parite_id = parite_id;
        this.parite_station_id = parite_station_id;
        this.amount = amount;
        this.date = date;
        this.comment = comment;
    }

    public InkasimiDetail( ReportsList reports) {
        this.id = reports.id;
        this.klienti = reports.partieName;
        this.amount = reports.amount;
        this.date =reports.date;
        this.comment = reports.comment;
        this.isSynced = true;
        this.njesia = reports.unit;
    }


    public InkasimiDetail() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getParite_id() {
        return parite_id;
    }

    public String getParite_station_id() {
        return parite_station_id;
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

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public String getKlienti() {
        return klienti;
    }

    public String getNjesia() {
        return njesia;
    }

    public void setKlienti(String klienti) {
        this.klienti = klienti;
    }

    public void setNjesia(String njesia) {
        this.njesia = njesia;
    }
}
