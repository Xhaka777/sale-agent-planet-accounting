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
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityUploadDetailBinding;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.ngarkimet.UploadDetailPost;
import org.planetaccounting.saleAgent.model.ngarkimet.UploadsDetailItem;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UploadDetailActivity extends AppCompatActivity {

    ActivityUploadDetailBinding binding;
    ArrayList<UploadsDetailItem> uploadsDetailItems = new ArrayList<>();

    UploadDetailListAdapter adapter;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;

    String transfer_id;

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_detail);
        transfer_id = getIntent().getStringExtra("id");
        Kontabiliteti.getKontabilitetiComponent().inject(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.invoiceList.setLayoutManager(mLayoutManager);
        adapter = new UploadDetailListAdapter(uploadsDetailItems);
        binding.invoiceList.setAdapter(adapter);
        getUploadDetail();

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
            Toast.makeText(UploadDetailActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }
//
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


    private void getUploadDetail() {
        apiService.getUploadDetail(new UploadDetailPost(preferences.getToken(), preferences.getUserId(), transfer_id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uploadDetailResponse -> {
                    if (uploadDetailResponse.getData() != null) {
                        this.uploadsDetailItems = uploadDetailResponse.getData();
                        adapter.setOrders(uploadsDetailItems);
                    } else {
                        Toast.makeText(this, R.string.nuk_ka_te_dhena, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, Throwable::printStackTrace);
    }
}
