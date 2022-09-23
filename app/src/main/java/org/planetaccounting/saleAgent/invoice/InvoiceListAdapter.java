package org.planetaccounting.saleAgent.invoice;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.databinding.InvoiceListItemBinding;
import org.planetaccounting.saleAgent.events.RePrintInvoiceEvent;
import org.planetaccounting.saleAgent.events.UploadInvoiceEvent;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.RealmResults;

/**
 * Created by macb on 31/01/18.
 */

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ViewHolder> {

    private ArrayList<InvoicePost> invoices;
    private Context ctx;

    public InvoiceListAdapter(ArrayList<InvoicePost> invoices) {
        this.invoices = invoices;
    }

    @Override
    public InvoiceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        InvoiceListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(ctx),
                org.planetaccounting.saleAgent.R.layout.invoice_list_item, parent, false);
        return new InvoiceListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(InvoiceListAdapter.ViewHolder holder, int position) {
        InvoiceListItemBinding binding = holder.binding;
        double pVlera = Double.parseDouble(invoices.get(position).getAmount_with_vat());
        binding.companyNameTextview.setText(invoices.get(position).getPartie_name());
        binding.companyUnitTextview.setText(invoices.get(position).getPartie_station_name());
        binding.data.setText(invoices.get(position).getInvoice_date());
        binding.invoiceNr.setText(invoices.get(position).getNo_invoice());
        binding.vlera.setText(""+cutTo2(pVlera));

        if (invoices.get(position).getSynced()) {
            binding.syncedIndicator.setImageResource(R.drawable.ic_green);

        } else {
            binding.syncedIndicator.setImageResource(R.drawable.ic_red);
        }
        binding.reprintInvoice.setOnClickListener(v -> EventBus.getDefault().post(new RePrintInvoiceEvent(invoices.get(position).getId(),invoices.get(position).getIsFromServer())));
        binding.getRoot().setOnClickListener(view -> {
            EventBus.getDefault().post(new UploadInvoiceEvent(invoices.get(position).getId()));
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }


    public void setCompanies(ArrayList<InvoicePost> invoices) {
        this.invoices = invoices;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private InvoiceListItemBinding binding;

        ViewHolder(InvoiceListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public double cutTo2(double value){
        return Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", value));
    }
}