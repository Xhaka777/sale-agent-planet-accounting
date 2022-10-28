package org.planetaccounting.saleAgent.clients;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ClientsListItemBinding;
import org.planetaccounting.saleAgent.events.OpenClientsCardEvent;
import org.planetaccounting.saleAgent.model.clients.Client;

import org.greenrobot.eventbus.EventBus;
import org.planetaccounting.saleAgent.model.clients.ClientsResponse;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.LogRecord;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by macb on 13/12/17.
 */

public class ClientsListAdapter extends RecyclerView.Adapter<ClientsListAdapter.ViewHolder> implements Filterable {

    private List<Client> clients = new ArrayList<>();
    private List<Client> clientFull = new ArrayList<>();
    private Context ctx;
    private LayoutInflater mInflater;

    @Override
    public ClientsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        ClientsListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(ctx),
                org.planetaccounting.saleAgent.R.layout.clients_list_item, parent, false);
        return new ClientsListAdapter.ViewHolder(binding);
    }

    ClientsListAdapter(List<Client> clients){
        this.clients = clients;
        clientFull = new ArrayList<>(clients);
    }

    public ClientsListAdapter(Context context, @NonNull List<Client> clientList){
        this.clients = clientList;
        mInflater = LayoutInflater.from(context);
        ctx = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClientsListItemBinding binding = holder.binding;
        Client client = clients.get(position);
        double vBilance = Double.parseDouble(client.getBalance());

        binding.numberTextview.setText(client.getNumber());
        binding.uniqueNumberTextview.setText(client.getUnique_number());
        binding.emriTextview.setText(client.getName());
        binding.kontaktTextview.setText(client.getPhone());
        binding.bilanciTextview.setText("" + cutTo2(vBilance));


        Glide.with(ctx).load(clients.get(position).getLogo()).into(binding.imageClient);
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

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Client> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(clientFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Client client : clientFull){
                    if(client.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(client);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clients.clear();
            clients.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    static class ViewHolder extends RecyclerView.ViewHolder {
    private ClientsListItemBinding binding;

    ViewHolder(ClientsListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

    }
}

    public double cutTo2(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", value));
    }


}