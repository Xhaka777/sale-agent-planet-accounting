package org.planetaccounting.saleAgent;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;
import org.planetaccounting.saleAgent.databinding.InvoiceListItemBinding;
import org.planetaccounting.saleAgent.databinding.PazarListItemBinding;
import org.planetaccounting.saleAgent.databinding.SaleDayLayoutBinding;
import org.planetaccounting.saleAgent.events.RePrintInvoiceEvent;
import org.planetaccounting.saleAgent.events.UploadInvoiceEvent;
import org.planetaccounting.saleAgent.invoice.InvoiceListAdapter;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.model.pazari.PazarData;
import org.planetaccounting.saleAgent.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmResults;

/**
 * Created by tahirietrit on 4/10/18.
 */

public class PazarAdapter extends RecyclerView.Adapter<PazarAdapter.ViewHolder> {

    private List<PazarData> pazarData = new ArrayList<>();
    private Context ctx;

    @Override
    public PazarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        PazarListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(ctx),
                R.layout.pazar_list_item, parent, false);
        return new PazarAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(PazarAdapter.ViewHolder holder, int position) {
        PazarListItemBinding binding = holder.binding;
        PazarData pd = pazarData.get(position);
        binding.col1.setText(pd.getData());
        binding.col2.setText(pd.getSaleWithInvoice());
        binding.col3.setText(pd.getSaleWithFiscal());
        binding.col4.setText(pd.getCash());
        binding.col5.setText(pd.getCashCollection());
        binding.col6.setText(pd.getDeposite());
        binding.col7.setText(pd.getPurchase());
        binding.col8.setText(pd.getDebt());
        binding.col10.setText(pd.getTotal());
    }

    @Override
    public int getItemCount() {
        return pazarData.size();
    }


    public void setCompanies(List<PazarData> pazarData) {
        this.pazarData = pazarData;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private PazarListItemBinding binding;

        ViewHolder(PazarListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}