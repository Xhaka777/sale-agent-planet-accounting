package org.planetaccounting.saleAgent;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.DatePicker;
import android.widget.Toast;

import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.PazariDitorActivityBinding;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.pazari.PazarResponse;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tahirietrit on 4/10/18.
 */

public class PazariDitorActivity extends Activity {
    PazariDitorActivityBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    PazarAdapter adapter;

    String fDate;
    String dDate;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;

    Locale myLocale;
    String currentLanguage = "sq",currentLang;
    public static final String TAG = "bottom_sheet";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pazari_ditor_activity);

        ((Kontabiliteti) getApplication()).getKontabilitetiComponent().inject(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(mLayoutManager);
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new PazarAdapter();
        binding.recycler.setAdapter(adapter);
        getPazariDitor();

        Date cDate = new Date();
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cDate);
        calendar = Calendar.getInstance();
        binding.textData.setText(fDate);
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                fDate = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
                dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
                binding.textData.setText(fDate);
                getPazariDitor();


            }
        };

        binding.dataLinar.setOnClickListener(v -> getdata());

        currentLanguage = getIntent().getStringExtra(currentLang);

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
            Toast.makeText(PazariDitorActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
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


    private void getdata(){

        new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void getPazariDitor(){

        StockPost stockPost = new StockPost(preferences.getToken(), preferences.getUserId());

        stockPost.setData(dDate);
        apiService.getPazariDitor(stockPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PazarResponse>() {
                    @Override
                    public void call(PazarResponse pazarResponse) {
                        adapter.setCompanies(pazarResponse.getData());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}
