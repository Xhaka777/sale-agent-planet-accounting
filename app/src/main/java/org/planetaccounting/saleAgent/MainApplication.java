package org.planetaccounting.saleAgent;

import android.app.Application;
import android.content.Context;

import org.planetaccounting.saleAgent.helper.LocaleHelper;

public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "sq"));
    }
}
