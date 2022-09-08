package org.planetaccounting.saleAgent.raportet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.OrdersListActivity;
import org.planetaccounting.saleAgent.PazariDitorActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.databinding.RaportActivityBinding;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.invoice.InvoiceListActivity;
import org.planetaccounting.saleAgent.invoice.InvoiceListAdapter;

import static org.planetaccounting.saleAgent.MainActivity.isConnected;

import java.util.Locale;

/**
 * Created by tahirietrit on 4/5/18.
 */

public class RaportetActivity extends Activity {
    RaportActivityBinding binding;

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.raport_activity);
        binding.listaFaturaveButton.setOnClickListener(view -> openInvoicesActivity());
        binding.listaPorosiveButton.setOnClickListener(view -> openOrderListActivity());
        binding.listaShpenzimeve.setOnClickListener(view -> openVendorListActivity());
        binding.listaInkasimeve.setOnClickListener(view -> openInkasimiActivity());
        binding.listaDepozitave.setOnClickListener(view -> openDepozitActivity());
        binding.returnList.setOnClickListener(view -> openReturnsActivity() );
        binding.dailyMarket.setOnClickListener(view -> openDailyMarket());

    }
    private void openInvoicesActivity() {
        Log.d("Hap Listen e faturav - ", " InvoiceListActivity");
        Intent i = new Intent(getApplicationContext(), InvoiceListActivity.class).putExtra("from","inv");
        startActivity(i);
    }

    private void openReturnsActivity() {
        Log.d("Hap Listen e kthimit - ", " InvoiceListActivity");
        Intent i = new Intent(getApplicationContext(), InvoiceListActivity.class).putExtra("from","ret");
        startActivity(i);
    }
    private void openVendorListActivity() {
        Log.d("Hap Listen e faturav - ", " InvoiceListActivity");
        Intent i = new Intent(getApplicationContext(), ReportDetailActivity.class);
        i.putExtra("type", 0);
        startActivity(i);
    }
    private void openInkasimiActivity() {
        Log.d("Hap Listen e faturav - ", " InvoiceListActivity");
        Intent i = new Intent(getApplicationContext(), ReportDetailActivity.class);
        i.putExtra("type", 1);
        startActivity(i);
    }
    private void openDepozitActivity() {
        Log.d("Hap Listen e faturav - ", " InvoiceListActivity");
        Intent i = new Intent(getApplicationContext(), ReportDetailActivity.class);
        i.putExtra("type", 2);
        startActivity(i);
    }
    private void openOrderListActivity() {
        if (isConnected) {
            Intent i = new Intent(getApplicationContext(), OrdersListActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), R.string.ju_lutem_kyçuni_ne_internet_që_të_shikoni_porositë, Toast.LENGTH_SHORT).show();
        }

    }

    private void openDailyMarket() {
        Intent i = new Intent(this, PazariDitorActivity.class);
        startActivity(i);
    }


    //methods to change the languages

    public void setLocale(String localeName){
        if(!localeName.equals(currentLang)){
            Context context = LocaleHelper.setLocale(this, localeName);
            //Resources resources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        }else{
            Toast.makeText(RaportetActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }

//    public void onBackPressed(){
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//        System.exit(0);
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }



}
