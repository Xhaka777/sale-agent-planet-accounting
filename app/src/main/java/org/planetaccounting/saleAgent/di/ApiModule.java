package org.planetaccounting.saleAgent.di;

import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.api.ServiceFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by planetaccounting on 11/8/16.
 */

@Module
public final class ApiModule {

    @Provides
    @Singleton
    ApiService provideApiServiceWithoutBus() {
        return ServiceFactory.createRetrofitService(ApiService.class, ServiceFactory.BASE_URL);
    }
}
