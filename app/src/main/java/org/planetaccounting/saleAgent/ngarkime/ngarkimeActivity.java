package org.planetaccounting.saleAgent.ngarkime;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrderListDetail;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityNgarkimeBinding;
import org.planetaccounting.saleAgent.events.OpenOrderDetailEvent;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.ngarkimet.Uploads;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ngarkimeActivity extends AppCompatActivity {


    ActivityNgarkimeBinding binding;

    ArrayList<Uploads> uploads = new ArrayList<>();
    NgarkimeListAdapter adapter;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_ngarkime);

        Kontabiliteti.getKontabilitetiComponent().inject(this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.ngarkimeList.setLayoutManager(mLayoutManager);
        adapter = new NgarkimeListAdapter(uploads);
        binding.ngarkimeList.setAdapter(adapter);

        getUploads();

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
            Toast.makeText(ngarkimeActivity.this, "Language already selected!", Toast.LENGTH_SHORT).show();
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


    private  void getUploads(){
        showLoader();
        apiService.getUploads(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uploadsResponse -> {
                    uploads = uploadsResponse.getData();
                    adapter.setUploads(uploads);
                    hideLoader();
                }, Throwable::printStackTrace);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void onEvent(OpenOrderDetailEvent event){
        Intent i = new Intent(getApplicationContext(), UploadDetailActivity.class);
        String transfer_id  = event.getOrderId();
        i.putExtra("id", transfer_id );
        startActivity(i);
    }

    private void showLoader() {
        binding.loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        binding.loader.setVisibility(View.GONE);
    }


}
