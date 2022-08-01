package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by tahirietrit on 4/1/18.
 */

public class Categorie extends RealmObject {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}