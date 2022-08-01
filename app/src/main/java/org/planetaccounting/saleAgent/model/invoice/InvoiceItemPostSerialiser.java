package org.planetaccounting.saleAgent.model.invoice;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by macb on 05/02/18.
 */

public class InvoiceItemPostSerialiser implements JsonSerializer<InvoiceItemPost> {

    @Override
    public JsonElement serialize(InvoiceItemPost src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        //    String discount_extra;
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("no_order", src.getNo_order());
        jsonObject.addProperty("no", src.getNo());
        jsonObject.addProperty("id_item_group", src.getId_item_group());
        jsonObject.addProperty("id_item", src.getId_item());
        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("quantity", src.getQuantity());
        jsonObject.addProperty("unit", src.getUnit());
        jsonObject.addProperty("price", src.getPrice());
        jsonObject.addProperty("price_base", src.getPrice_base());
        jsonObject.addProperty("discount", src.getDiscount());
        jsonObject.addProperty("barcode", src.getBarcode());
        jsonObject.addProperty("vat_id", src.getVat_id());
        jsonObject.addProperty("vat_rate", src.getVat_rate());//relacioni
        jsonObject.addProperty("price_vat", src.getPrice_vat());
        jsonObject.addProperty("relacioni", src.getRelacioni());
        jsonObject.addProperty("amount_of_discount", src.getAmount_of_discount());
        jsonObject.addProperty("totalPrice", src.getTotalPrice());
        return jsonObject;
    }
}