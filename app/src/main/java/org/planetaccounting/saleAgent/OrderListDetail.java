package org.planetaccounting.saleAgent;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.OrderListDetailBinding;
import org.planetaccounting.saleAgent.model.OrderDetailItem;
import org.planetaccounting.saleAgent.model.OrderDetailPost;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by macb on 06/02/18.
 */

public class OrderListDetail extends Activity {
    OrderListDetailBinding binding;

    ArrayList<OrderDetailItem> orderDetailItems = new ArrayList<>();
    OrderListDetailAdapter adapter;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    String orderId;
    String orderType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.order_list_detail);
        orderId = getIntent().getStringExtra("id");
        orderType = getIntent().getStringExtra("type");
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.invoiceList.setLayoutManager(mLayoutManager);
        adapter = new OrderListDetailAdapter(orderDetailItems);
        binding.invoiceList.setAdapter(adapter);
        getOrders();
    }

    private void getOrders() {
        apiService.getOrderDetail(new OrderDetailPost(preferences.getToken(), preferences.getUserId(), orderId,orderType))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ordersResponse -> {
                    if (ordersResponse.getData().getItems() != null) {
                        binding.titleTextivew.setText("Porosia nr:" + ordersResponse.getData().getNoOrder());
                        this.orderDetailItems = ordersResponse.getData().getItems();
                        adapter.setOrders(orderDetailItems);
                    } else  {
                        Toast.makeText(this,"Nuk ka te dhena",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, Throwable::printStackTrace);
    }
}
