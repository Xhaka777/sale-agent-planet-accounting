package org.planetaccounting.saleAgent.model.invoice;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import io.realm.RealmList;

/**
 * Created by macb on 05/02/18.
 */

public class InvoicePostSerialiser implements JsonSerializer<InvoicePost> {

    @Override
    public JsonElement serialize(InvoicePost src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        RealmList<InvoiceItemPost> items = new RealmList<>();
        boolean synced = false;
        //    String discount_extra;
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("no_invoice", src.getNo_invoice());
        jsonObject.addProperty("partie_id", src.getPartie_id());
        jsonObject.addProperty("partie_name", src.getPartie_name());
        jsonObject.addProperty("partie_station_id", src.getPartie_station_id());
        jsonObject.addProperty("partie_station_name", src.getPartie_station_name());
        jsonObject.addProperty("partie_address", src.getPartie_address());
        jsonObject.addProperty("partie_city", src.getPartie_city());
        jsonObject.addProperty("partie_state_id", src.getPartie_state_id());
        jsonObject.addProperty("sale_station_id", src.getSale_station_id());
        jsonObject.addProperty("sale_station_name", src.getSale_station_name());
        jsonObject.addProperty("invoice_date", src.getInvoice_date());
        jsonObject.addProperty("discount", src.getDiscount());
        jsonObject.addProperty("is_payed", src.getIs_payed());
        jsonObject.addProperty("amount_no_vat", src.getAmount_no_vat());
        jsonObject.addProperty("amount_of_vat", src.getAmount_of_vat());
        jsonObject.addProperty("amount_discount", src.getAmount_discount());
        jsonObject.addProperty("amount_payed", src.getAmount_payed());
        jsonObject.addProperty("amount_with_vat", src.getAmount_with_vat());
        jsonObject.addProperty("id_saler", src.getId_saler());
        jsonObject.addProperty("is_bill", src.getIs_bill());
        jsonObject.addProperty("location", src.getLocation());
        jsonObject.addProperty("total_without_discount", src.getTotal_without_discount());
        jsonObject.addProperty("synced", src.getSynced());
        jsonObject.add("items", context.serialize(src.getItems()));
        return jsonObject;
    }
}