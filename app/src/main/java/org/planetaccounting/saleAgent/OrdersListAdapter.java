package org.planetaccounting.saleAgent;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.planetaccounting.saleAgent.databinding.OrderListItemBinding;
import org.planetaccounting.saleAgent.events.OpenOrderDetailEvent;
import org.planetaccounting.saleAgent.model.order.Order;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by macb on 06/02/18.
 */

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {

    ArrayList<Order> orders = new ArrayList<>();
    private Context ctx;

    private CancelOrder listener;

    public OrdersListAdapter(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @Override
    public OrdersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        OrderListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(ctx),
                R.layout.order_list_item, parent, false);
        return new OrdersListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(OrdersListAdapter.ViewHolder holder, int position) {
        OrderListItemBinding binding = holder.binding;

        String[] data = orders.get(position).getData().split(" ");
        binding.data.setText(data[0]+"\n"+data[1]);
        binding.orderNr.setText(orders.get(position).getNoOrder());
        binding.wareHouse.setText(orders.get(position).getWarehouse());
        binding.client.setText(orders.get(position).getClient());
        binding.tipi.setText(orders.get(position).getType());
        binding.vlera.setText(orders.get(position).getAmount());
        binding.njesia.setText(orders.get(position).getClientStation());

        if (orders.get(position).getCancelAllowed() == 1 ){
            binding.anuloButton.setVisibility(View.VISIBLE);
        } else {
            binding.anuloButton.setVisibility(View.GONE);

        }

        binding.anuloButton.setOnClickListener(view ->{ if (listener != null) {
            listener.onCancelPressed(orders.get(position));
            }
        });

        binding.getRoot().setOnClickListener(view -> EventBus.getDefault().post(new OpenOrderDetailEvent(orders.get(position).getId(),orders.get(position).getType())));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    public void setListener(CancelOrder listener) {
        this.listener = listener;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private OrderListItemBinding binding;

        ViewHolder(OrderListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    interface CancelOrder{
        void onCancelPressed(Order order);
    }
}