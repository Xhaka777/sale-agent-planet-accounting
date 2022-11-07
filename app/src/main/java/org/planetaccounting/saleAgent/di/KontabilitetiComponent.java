package org.planetaccounting.saleAgent.di;

import org.planetaccounting.saleAgent.DepozitaActivity;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrderListDetail;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.OrdersListActivity;
import org.planetaccounting.saleAgent.PazariDitorActivity;
import org.planetaccounting.saleAgent.aksionet.ActionActivity;
import org.planetaccounting.saleAgent.aksionet.ActionCollectionDetailActivity;
import org.planetaccounting.saleAgent.aksionet.ActionDetailActivity;
import org.planetaccounting.saleAgent.clients.ClientsActivity;
import org.planetaccounting.saleAgent.clients.ClientsDetailActivity;
import org.planetaccounting.saleAgent.clients.ClientsListActivity;
import org.planetaccounting.saleAgent.clients.infos.AdresaFragment;
import org.planetaccounting.saleAgent.clients.infos.LocationFragment;
import org.planetaccounting.saleAgent.clients.infos.OptionsFragment;
import org.planetaccounting.saleAgent.db.DatabaseOperations;
import org.planetaccounting.saleAgent.inkasimi.InkasimPanel;
import org.planetaccounting.saleAgent.inkasimi.ListaInkasimit;
import org.planetaccounting.saleAgent.invoice.InvoiceActivity;
import org.planetaccounting.saleAgent.invoice.InvoiceActivityOriginal;
import org.planetaccounting.saleAgent.invoice.InvoiceListActivity;
import org.planetaccounting.saleAgent.kthemallin.ktheMallin;
import org.planetaccounting.saleAgent.login.LoginActivity;
import org.planetaccounting.saleAgent.ngarkime.UploadDetailActivity;
import org.planetaccounting.saleAgent.ngarkime.ngarkimeActivity;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.raportet.ReportDetailActivity;
import org.planetaccounting.saleAgent.services.MyFirebaseMessagingService;
import org.planetaccounting.saleAgent.settings.AboutPage;
import org.planetaccounting.saleAgent.settings.SettingsActivity;
import org.planetaccounting.saleAgent.settings.escpostsettngs.ESCSettingsActivity;
import org.planetaccounting.saleAgent.shpenzimet.ShpenzimetActivity;
import org.planetaccounting.saleAgent.stock.StockActivity;
import org.planetaccounting.saleAgent.target.TargetActivity;
import org.planetaccounting.saleAgent.target.TargetArticleActivity;
import org.planetaccounting.saleAgent.target.TargetBrandActivity;
import org.planetaccounting.saleAgent.target.TargetCashActivity;
import org.planetaccounting.saleAgent.target.TargetSkuActivity;
import org.planetaccounting.saleAgent.target.TotalTargetActivity;
import org.planetaccounting.saleAgent.transfere.CreateTransferActivity;
import org.planetaccounting.saleAgent.transfere.DetailTransferActivity;
import org.planetaccounting.saleAgent.transfere.transfereActivity;
import org.planetaccounting.saleAgent.utils.ActivityPrint;
import org.planetaccounting.saleAgent.utils.ClientCardPrintUtil;
import org.planetaccounting.saleAgent.utils.InvoicePrintUtil;
import org.planetaccounting.saleAgent.utils.LocaleManager;
import org.planetaccounting.saleAgent.utils.ReturnPrintUtil;
import org.planetaccounting.saleAgent.utils.StockPrintUtil;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by planetaccounting on 11/8/16.
 */

@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface KontabilitetiComponent {

    void inject(MainActivity activity);
    void inject(LoginActivity activity);
    void inject(StockActivity activity);
    void inject(ClientsActivity activity);
    void inject(ClientsDetailActivity activity);
    void inject(InvoiceActivity activity);
    void inject(OrdersActivity activity);
    void inject(ActivityPrint activity);
    void inject(InvoiceListActivity activity);
    void inject(OrdersListActivity activity);
    void inject(ClientsListActivity activity);
    void inject(OrderListDetail activity);
    void inject(RealmHelper helper);
    void inject(LocaleManager localeManager);

    void inject(InvoicePrintUtil helper);
    void inject(ReturnPrintUtil helper);
    void inject(ClientCardPrintUtil helper);
    void inject(StockPrintUtil helper);
    void inject(InkasimPanel activity);
    void inject(ktheMallin activity);
    void inject(transfereActivity activity);
    void inject(ngarkimeActivity activity);
    void inject(UploadDetailActivity activity);
    void inject(DepozitaActivity activity);
    void inject(ActionCollectionDetailActivity activity);
    void inject(TargetActivity activity);
    void inject(ActionActivity activity);
    void inject(DatabaseOperations activity);
    void inject(ShpenzimetActivity activity);
    void inject(TotalTargetActivity activity);
    void inject(TargetCashActivity activity);
    void inject(TargetBrandActivity activity);
    void inject(TargetArticleActivity activity);
    void inject(InvoiceActivityOriginal activity);
    void inject(TargetSkuActivity activity);
    void inject(ListaInkasimit activity);
    void inject(ReportDetailActivity activity);
    void inject(PazariDitorActivity activity);
    void inject(CreateTransferActivity activity);
    void inject(DetailTransferActivity activity);
    void inject(ActionDetailActivity activity);
    void inject(SettingsActivity activity);
    void inject(ESCSettingsActivity activity);
    void inject(AboutPage activity);
    void inject(LocationFragment locationFragment);
    void inject(OptionsFragment activity);
    void inject(AdresaFragment activity);
    void inject(MyFirebaseMessagingService service);

}
