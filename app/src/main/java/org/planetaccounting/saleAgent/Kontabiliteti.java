package org.planetaccounting.saleAgent;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import org.planetaccounting.saleAgent.di.ApiModule;
import org.planetaccounting.saleAgent.di.AppModule;
import org.planetaccounting.saleAgent.di.DaggerKontabilitetiComponent;
import org.planetaccounting.saleAgent.di.KontabilitetiComponent;
import org.planetaccounting.saleAgent.persistence.RealmMigrationConfig;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**

 */

public class Kontabiliteti extends Application {
    private static Kontabiliteti instance;
    private static KontabilitetiComponent kontabilitetiComponent;
    private boolean appInForeground = false;

    public Kontabiliteti() {
        super();
        instance = this;
    }

    public static KontabilitetiComponent getKontabilitetiComponent() {
        return kontabilitetiComponent;
    }

    public static Kontabiliteti getInstance() {
        return instance;
    }

    public boolean isAppInForeground() {
        return appInForeground;
    }

    public void setAppInForeground(boolean appInForeground) {
        this.appInForeground = appInForeground;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        if (kontabilitetiComponent == null) {
            kontabilitetiComponent = DaggerKontabilitetiComponent.builder()
                    .apiModule(new ApiModule())
                    .appModule(new AppModule(this))
                    .build();

        }

        Realm.init(instance);

    }
}
