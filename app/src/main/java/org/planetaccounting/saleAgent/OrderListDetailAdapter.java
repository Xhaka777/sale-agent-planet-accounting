package org.planetaccounting.saleAgent;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.planetaccounting.saleAgent.databinding.OrderlistDetailListItemBinding;
import org.planetaccounting.saleAgent.model.OrderDetailItem;

import java.util.ArrayList;

/**
 * Created by macb on 06/02/18.
 */

public class OrderListDetailAdapter extends RecyclerView.Adapter<OrderListDetailAdapter.ViewHolder> {

    ArrayList<OrderDetailItem> orderDetailItems = new ArrayList<>();
    private Context ctx;

    public OrderListDetailAdapter(ArrayList<OrderDetailItem> orderDetailItems) {
        this.orderDetailItems = orderDetailItems;
    }

    @Override
    public OrderListDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        OrderlistDetailListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(ctx),
                R.layout.orderlist_detail_list_item, parent, false);
        return new OrderListDetailAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(OrderListDetailAdapter.ViewHolder holder, int position) {
        OrderlistDetailListItemBinding binding = holder.binding;
        binding.shifra.setText(orderDetailItems.get(position).getNumber());
        binding.name.setText(orderDetailItems.get(position).getName());
        binding.sasia.setText(orderDetailItems.get(position).getQuantity());
        binding.barkod.setText(orderDetailItems.get(position).getBarcode());
        binding.njesia.setText(orderDetailItems.get(position).getUnit());
        binding.cmimi.setText(orderDetailItems.get(position).getPrice()+"");
        binding.vlera.setText(orderDetailItems.get(position).getAmount()+"");

    }

    @Override
    public int getItemCount() {
        return orderDetailItems.size();
    }


    public void setOrders(ArrayList<OrderDetailItem> orderDetailItems) {
        this.orderDetailItems = orderDetailItems;
        System.out.println("items "+orderDetailItems.size());
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private OrderlistDetailListItemBinding binding;

        ViewHolder(OrderlistDetailListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}