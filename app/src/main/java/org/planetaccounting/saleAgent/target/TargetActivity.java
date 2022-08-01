package org.planetaccounting.saleAgent.target;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityTargetBinding;
import org.planetaccounting.saleAgent.model.target.BaseTargetData;
import org.planetaccounting.saleAgent.model.target.BaseTargetPost;
import org.planetaccounting.saleAgent.model.target.BaseTargetResponse;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.R.attr.checked;

public class TargetActivity extends AppCompatActivity {
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    ActivityTargetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_target);

        Kontabiliteti.getKontabilitetiComponent().inject(this);
        MONTH = String.valueOf((Calendar.getInstance().get(Calendar.MONTH) + 1));
        YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        binding.totalTarget.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), TotalTargetActivity.class)));
        binding.cashTarget.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), TargetCashActivity.class)));
        binding.brendeTarget.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), TargetBrandActivity.class)));
        binding.artikuj.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), TargetArticleActivity.class)));
        binding.sku.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), TargetSkuActivity.class)));
        binding.muajiTextview.setText("Muaji: " + types[Integer.parseInt(MONTH) - 1]);
        binding.vitiTextview.setText("Viti: " + YEAR);
        binding.muajiTextview.setOnClickListener(view -> monthdialog());
        binding.vitiTextview.setOnClickListener(view -> yearDialog());
        getBaseTarget();
    }

    public static String MONTH;
    public static String YEAR;

    private void getBaseTarget() {
        binding.holder.setVisibility(View.GONE);
        binding.loader.setVisibility(View.VISIBLE);
        apiService.getBaseTarget(new BaseTargetPost(preferences.getUserId(), preferences.getToken(),
                MONTH, YEAR))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseTargetResponse -> {
                    if (baseTargetResponse.getSuccess()) {
                        binding.holder.setVisibility(View.VISIBLE);
                        binding.loader.setVisibility(View.GONE);
                        setupTotalTarget(baseTargetResponse.getData().get(0));
                        setupSkuTarget(baseTargetResponse.getData().get(3));
                        setupBrendetTarget(baseTargetResponse.getData().get(1));
                        setupArtikujtTarget(baseTargetResponse.getData().get(2));
                        setupInkasimiTarget(baseTargetResponse.getData().get(4));
                        setupRrogaFikseTarget(baseTargetResponse.getData().get(5));
                        setupRrogaTotaleTarget(baseTargetResponse.getData());
                    } else {
                        Toast.makeText(getApplicationContext(), baseTargetResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    binding.holder.setVisibility(View.VISIBLE);
                    binding.loader.setVisibility(View.GONE);
                }, throwable -> {
                    Toast.makeText(getApplicationContext(), "Nuk keni targete per kete muaj!", Toast.LENGTH_SHORT).show();
                    binding.holder.setVisibility(View.VISIBLE);
                    binding.loader.setVisibility(View.GONE);
                    throwable.printStackTrace();
                });
    }

    private void setupTotalTarget(BaseTargetData data) {
        binding.totalTargetiTextview.setText(data.getTarget());
        binding.totalRealizimiTextview.setText(data.getTotalSale());
        binding.totalPerqindjaRealizimitTextview.setText(data.getTargetPercentage() + " %");
        binding.totalFitimiRealizimitTextview.setText(data.getBenefit());
    }

    private void setupSkuTarget(BaseTargetData data) {
        binding.totalSkuTextview.setText(data.getTarget());
        binding.skuRealizimiTextview.setText(data.getTotalSale());
        binding.skuPerqindjaRealizimitTextview.setText(data.getTargetPercentage() + " %");
        binding.skuFitimiRealizimitTextview.setText(data.getBenefit());
    }

    private void setupBrendetTarget(BaseTargetData data) {
        binding.brendeTargetiTextview.setText(data.getTarget());
        binding.brendeRealizimiTextview.setText(data.getTotalSale());
        binding.brendePerqindjaRealizimitTextview.setText(data.getTargetPercentage() + " %");
        binding.brendeFitimiRealizimitTextview.setText(data.getBenefit());
    }

    private void setupArtikujtTarget(BaseTargetData data) {
        binding.totalArtikujTextview.setText(data.getTarget());
        binding.artikujRealizimiTextview.setText(data.getTotalSale());
        binding.artikujPerqindjaRealizimitTextview.setText(data.getTargetPercentage() + " %");
        binding.artikujFitimiRealizimitTextview.setText(data.getBenefit());
    }

    private void setupInkasimiTarget(BaseTargetData data) {
        binding.totalInkasimiTextview.setText(data.getTarget());
        binding.inkasimiRealizimiTextview.setText(data.getTotalSale());
        binding.inkasimiPerqindjaRealizimitTextview.setText(data.getTargetPercentage() + " %");
        binding.inkasimiFitimiRealizimitTextview.setText(data.getBenefit());
    }

    private void setupRrogaFikseTarget(BaseTargetData data) {
        binding.fikseFitimiRealizimitTextview.setText("Paga Baze: " + data.getBaseWage());
    }

    private void setupRrogaTotaleTarget(List<BaseTargetData> targetData) {
        int rrogaTotale = 0;
        for (int i = 0; i < targetData.size(); i++) {
            if (i != 5) {
                try {
                    rrogaTotale += Double.parseDouble(targetData.get(i).getBenefit());
                } catch (Exception e) {
                    rrogaTotale += 0;
                }
            } else {
                rrogaTotale += targetData.get(i).getBaseWage();
            }
        }
        binding.rrogaFitimiRealizimitTextview.setText("Paga Totale: " + rrogaTotale);
    }

    final String[] types = new String[]{"Janar", "Shkurt", "Mars", "Prill", "Maj", "Qershor",
            "Korrik", "Gusht", "Shtator", "Tetor", "Nentor", "Dhjetor"};

    void monthdialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setIcon(R.mipmap.ic_launcher);
        alt_bld.setSingleChoiceItems(types, checked, (dialog, item) -> {
            MONTH = String.valueOf((item + 1));
            binding.muajiTextview.setText("Muaji: " + types[item]);
            dialog.dismiss();
            getBaseTarget();

        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    void yearDialog() {
        final String[] types = new String[]{"2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026",};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setIcon(R.mipmap.ic_launcher);
        alt_bld.setSingleChoiceItems(types, checked, (dialog, item) -> {
            YEAR = types[item];
            binding.vitiTextview.setText("Viti: " + YEAR);
            dialog.dismiss();
            getBaseTarget();

        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
}
