package org.planetaccounting.saleAgent.ngarkime;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityUploadDetailBinding;
import org.planetaccounting.saleAgent.model.ngarkimet.UploadDetailPost;
import org.planetaccounting.saleAgent.model.ngarkimet.UploadsDetailItem;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_upload_detail);
        transfer_id = getIntent().getStringExtra("id");
        Kontabiliteti.getKontabilitetiComponent().inject(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.invoiceList.setLayoutManager(mLayoutManager);
        adapter = new UploadDetailListAdapter(uploadsDetailItems);
        binding.invoiceList.setAdapter(adapter);
        getUploadDetail();

        }

    private void getUploadDetail() {
        apiService.getUploadDetail(new UploadDetailPost(preferences.getToken(), preferences.getUserId(), transfer_id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uploadDetailResponse -> {
                    if (uploadDetailResponse.getData() != null) {
                        this.uploadsDetailItems = uploadDetailResponse.getData();
                        adapter.setOrders(uploadsDetailItems);
                    } else  {
                        Toast.makeText(this,"Nuk ka te dhena",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, Throwable::printStackTrace);
    }
}
