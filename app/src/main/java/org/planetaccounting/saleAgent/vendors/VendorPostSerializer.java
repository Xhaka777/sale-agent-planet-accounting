package org.planetaccounting.saleAgent.vendors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.planetaccounting.saleAgent.model.invoice.InvoiceItemPost;

import java.lang.reflect.Type;

/**
 * Created by tahirietrit on 4/6/18.
 */

public class VendorPostSerializer  implements JsonSerializer<VendorPost> {

    @Override
    public JsonElement serialize(VendorPost src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        //    String discount_extra;
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("parite_id", src.getParite_id());
        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("amount", src.getAmount());
        jsonObject.addProperty("date", src.getDate());
        jsonObject.addProperty("comment", src.getName());
        jsonObject.addProperty("no_invoice", src.getNo_invoice());
        jsonObject.addProperty("furnitori", src.getFurnitori());
        jsonObject.addProperty("type", src.getType());
        jsonObject.addProperty("typeName", src.getTypeName());
        jsonObject.addProperty("isSynced", src.isSynced());
        return jsonObject;
    }
}