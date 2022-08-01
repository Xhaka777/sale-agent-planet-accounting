package org.planetaccounting.saleAgent.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.planetaccounting.saleAgent.depozita.DepositPost;
import org.planetaccounting.saleAgent.vendors.VendorPost;

import java.lang.reflect.Type;

/**
 * Created by tahirietrit on 4/6/18.
 */

public class DepositPostSerializer implements JsonSerializer<DepositPost> {

    @Override
    public JsonElement serialize(DepositPost src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        //    String discount_extra;
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("token", src.getToken());
        jsonObject.addProperty("user_id", src.getUser_id());
        jsonObject.addProperty("bank_id", src.getBank_id());
        jsonObject.addProperty("emri_bankes", src.getEmri_bankes());
        jsonObject.addProperty("date", src.getDate());
        jsonObject.addProperty("amount", src.getAmount());
        jsonObject.addProperty("branch", src.getBranch());
        jsonObject.addProperty("referenc_no", src.getReferenc_no());
        jsonObject.addProperty("comment", src.getComment());
        jsonObject.addProperty("isSynced", src.isSynced());
        return jsonObject;
    }
}