package org.planetaccounting.saleAgent.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import org.planetaccounting.saleAgent.Kontabiliteti;

import java.util.Locale;

import javax.inject.Inject;

public class LocaleManager {

    @Inject
    Preferences preferences;

    Context context;

    public LocaleManager(Context context) {
        Kontabiliteti.getKontabilitetiComponent().inject(this);

        this.context = context;
    }


    public  void setNewLanguage(String language) {
        preferences.saveLanguage(language);
        updateResources();
    }


    public   void updateResources() {
        Locale locale = new Locale(preferences.getLanguage());
        Locale.setDefault(locale);
        Resources res = this.context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
