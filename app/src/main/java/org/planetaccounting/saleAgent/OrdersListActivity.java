package org.planetaccounting.saleAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.OrdersListActivityBinding;
import org.planetaccounting.saleAgent.events.OpenOrderDetailEvent;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.OrderDetailPost;
import org.planetaccounting.saleAgent.model.order.Order;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by macb on 06/02/18.
 */

public class OrdersListActivity extends Activity implements OrdersListAdapter.CancelOrder {
    OrdersListActivityBinding binding;

    ArrayList<Order> orders = new ArrayList<>();
    OrdersListAdapter adapter;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = DataBindingUtil.setContentView(this, R.layout.orders_list_activity);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.invoiceList.setLayoutManager(mLayoutManager);
        adapter = new OrdersListAdapter(orders);
        adapter.setListener(this);
        binding.invoiceList.setAdapter(adapter);

        getOrders();

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
            Toast.makeText( OrdersListActivity.this, "Language already selected!", Toast.LENGTH_SHORT).show();
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


    private  void getOrders(){
        showLoader();
        apiService.getOrders(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ordersResponse -> {
                    orders = ordersResponse.getData();
                    adapter.setOrders(orders);
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
        Intent i = new Intent(getApplicationContext(), OrderListDetail.class);
        i.putExtra("id", event.getOrderId());
        i.putExtra("type",event.getOrderType());
        startActivity(i);
    }
    private void showLoader() {
        binding.loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        binding.loader.setVisibility(View.GONE);
    }

    @Override
    public void onCancelPressed(Order order) {
        showLoader();
        apiService.cancelOrder(new OrderDetailPost(preferences.getToken(), preferences.getUserId(), order.getId(),order.getType())) .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ordersResponse -> {
                    hideLoader();
                    if (ordersResponse.getSuccess()) {
                        Toast.makeText(this,"Order was canceled",Toast.LENGTH_SHORT).show();
                        getOrders();

                    } else  {
                        Toast.makeText(this,ordersResponse.error.getText(),Toast.LENGTH_SHORT).show();

                    }
                }, Throwable::printStackTrace);


    }
}
