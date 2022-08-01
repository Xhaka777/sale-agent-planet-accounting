package org.planetaccounting.saleAgent.model.clients;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by macb on 10/01/18.
 */

public class Station extends RealmObject implements Parcelable {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("village")
    @Expose
    public String village;
    @SerializedName("city")
    @Expose
    public String city;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVillage() {
        return village;
    }

    public String getCity() {
        return city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.village);
        dest.writeString(this.city);
    }

    public Station() {
    }

    protected Station(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.village = in.readString();
        this.city = in.readString();
    }

    public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel source) {
            return new Station(source);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };

}