package org.planetaccounting.saleAgent.di;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.persistence.RealmMigrationConfig;
import org.planetaccounting.saleAgent.utils.LocaleManager;
import org.planetaccounting.saleAgent.utils.Preferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by planetaccounting on 11/8/16.
 */

@Module
public class AppModule {

    private final Kontabiliteti app;

    public AppModule(Kontabiliteti app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return app;
    }
    @Provides
    @Singleton
    public Preferences providePreferences(){
        return new Preferences(app);
    }
    @Provides
    @Singleton
    public LocaleManager provideLocalProvider(){
        return new LocaleManager(app);
    }

    @Provides
    @Singleton
    public RealmHelper provideRealm(){

        PackageInfo pinfo = null;
        try {
            pinfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;
//        if (versionNumber <= 18){

            final RealmConfiguration configuration = new RealmConfiguration.Builder().name("tasky.realm")
                    .schemaVersion(7).deleteRealmIfMigrationNeeded()
                    .build();
            Realm.setDefaultConfiguration(configuration);
            return new RealmHelper(  Realm.getInstance(configuration));
//        } else  {
//
//            final RealmConfiguration configuration = new RealmConfiguration.Builder().name("tasky.realm")
//                    .schemaVersion(8).migration(new RealmMigrationConfig()).build();
//            Realm.setDefaultConfiguration(configuration);
//            return new RealmHelper(Realm.getInstance(configuration));
//        }


    }

}