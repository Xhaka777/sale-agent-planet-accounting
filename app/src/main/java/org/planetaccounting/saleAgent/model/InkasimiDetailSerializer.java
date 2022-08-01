package org.planetaccounting.saleAgent.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.planetaccounting.saleAgent.inkasimi.InkasimiDetail;
import org.planetaccounting.saleAgent.vendors.VendorPost;

import java.lang.reflect.Type;

/**
 * Created by tahirietrit on 4/6/18.
 */

public class InkasimiDetailSerializer implements JsonSerializer<InkasimiDetail> {

    @Override
    public JsonElement serialize(InkasimiDetail src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        //    String discount_extra;
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("parite_id", src.getParite_id());
        jsonObject.addProperty("klienti", src.getKlienti());
        jsonObject.addProperty("njesia", src.getNjesia());
        jsonObject.addProperty("parite_station_id", src.getDate());
        jsonObject.addProperty("amount", src.getAmount());
        jsonObject.addProperty("date", src.getDate());
        jsonObject.addProperty("comment", src.getComment());
        jsonObject.addProperty("isSynced", src.isSynced());
        return jsonObject;
    }
}