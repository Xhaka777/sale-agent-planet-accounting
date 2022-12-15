package org.planetaccounting.saleAgent.ngarkime;

import android.annotation.SuppressLint;
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
import org.planetaccounting.saleAgent.model.ngarkimet.UploadsResponse;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ngarkimeActivity extends AppCompatActivity {


    ActivityNgarkimeBinding binding;

    ArrayList<Uploads> uploads = new ArrayList<>();
    NgarkimeListAdapter adapter;
    String uploadsID = "2";

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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_ngarkime);

        Kontabiliteti.getKontabilitetiComponent().inject(this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.ngarkimeList.setLayoutManager(mLayoutManager);
        adapter = new NgarkimeListAdapter(uploads);
        binding.ngarkimeList.setAdapter(adapter);

//        getUploads();
        getOutUploads();
        currentLanguage = getIntent().getStringExtra(currentLang);
    }

    //methods to change the languages

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLang)) {
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
        } else {
            Toast.makeText(ngarkimeActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    /*
     * ne kete metode e thirrim api per te gjitha transferet e krijuara deri me tani
     * kerkese ke me marr vetem transferet me in : out ( mos me marr edhe in edhe out), por vetem si ne web qe po na dergon vetem nje per transfer...
     *
     * */
    @SuppressLint("NotifyDataSetChanged")
    private void getUploads() {
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

    @SuppressLint("NotifyDataSetChanged")
    private void getOutUploads() {

        apiService.getUploads(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uploadsResponse -> {
                    uploads = uploadsResponse.getData();

                    for (int i = 0; i < uploads.size(); i++) {
                        if (uploads.get(i).getIn().equals("out")) {
                            adapter.setUploads(uploads);
                        } else {
                            uploads.remove(uploads.get(i));
                            adapter.setUploads(uploads);
                            adapter.notifyDataSetChanged();

                        }
                    }
                    /**
                     * po hin ne loop kushti i pare po plotsohet ,
                     * edhe kushti ne else po plotsohet (dmth pasi plotsohet kushti else) pe bon remove elementin nga array
                     * pastaj po hin loop perseri ne if me korigju perseri....
                     */


                    //pjesa ku adapteri i merr te gjitha te dhenat nga api...
                    //me filtru listen para se me qit ne adapter
//                    adapter.setUploads(uploads);
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
    public void onEvent(OpenOrderDetailEvent event) {
        Intent i = new Intent(getApplicationContext(), UploadDetailActivity.class);
        i.putExtra("id", event.getOrderId());
        i.putExtra("type", event.getOrderType());
//        String transfer_id  = event.getOrderId();
//        i.putExtra("id", transfer_id );
        startActivity(i);
    }

    private void showLoader() {
        binding.loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        binding.loader.setVisibility(View.GONE);
    }

}
