package org.planetaccounting.saleAgent.raportet;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.planetaccounting.saleAgent.OrdersListActivity;
import org.planetaccounting.saleAgent.PazariDitorActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.databinding.RaportActivityBinding;
import org.planetaccounting.saleAgent.invoice.InvoiceListActivity;
import org.planetaccounting.saleAgent.invoice.InvoiceListAdapter;

import static org.planetaccounting.saleAgent.MainActivity.isConnected;

/**
 * Created by tahirietrit on 4/5/18.
 */

public class RaportetActivity extends Activity {
    RaportActivityBinding binding;
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
            Toast.makeText(getApplicationContext(), "Ju lutem kyçuni në internet që të shikoni porositë!", Toast.LENGTH_SHORT).show();
        }

    }

    private void openDailyMarket() {
        Intent i = new Intent(this, PazariDitorActivity.class);
        startActivity(i);
    }


}
