package org.planetaccounting.saleAgent.clients;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.planetaccounting.saleAgent.databinding.ClientsListItemBinding;
import org.planetaccounting.saleAgent.events.OpenClientsCardEvent;
import org.planetaccounting.saleAgent.model.clients.Client;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by macb on 13/12/17.
 */

public class ClientsListAdapter extends RecyclerView.Adapter<ClientsListAdapter.ViewHolder> {

    private List<Client> clients = new ArrayList<>();
    private Context ctx;

    @Override
    public ClientsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        ClientsListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(ctx),
                org.planetaccounting.saleAgent.R.layout.clients_list_item, parent, false);
        return new ClientsListAdapter.ViewHolder(binding);
//        return new ViewHolder(bindinag);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClientsListItemBinding binding = holder.binding;
        Client client = clients.get(position);
        double vBilance = Double.parseDouble(client.getBalance());

        binding.numberTextview.setText(client.getNumber());
        binding.emriTextview.setText(client.getName());
        binding.kontaktTextview.setText(client.getPhone());
        binding.bilanciTextview.setText(""+cutTo2(vBilance));

        Glide.with(ctx).load("http://" + clients.get(position).getLogo()).into(binding.imageClient);
        if (clients.get(position).getPhone() != null) {
            if (clients.get(position).getPhone().length() > 0) {

                binding.dail.setVisibility(View.VISIBLE);
            } else {
                binding.dail.setVisibility(View.GONE);
            }
        } else {
            binding.dail.setVisibility(View.GONE);
        }
        binding.dail.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + clients.get(position).getPhone()));
            ctx.startActivity(intent);

        });
        binding.getRoot().setOnClickListener(view -> EventBus.getDefault().post(new OpenClientsCardEvent(clients.get(position))));

    }

    @Override
    public int getItemCount() {
        return clients.size();
    }


    public void setClients(List<Client> clients) {
        this.clients = clients;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ClientsListItemBinding binding;

        ViewHolder(ClientsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public double cutTo2(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", value));
    }

}