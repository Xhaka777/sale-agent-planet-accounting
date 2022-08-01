package org.planetaccounting.saleAgent.login;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import org.planetaccounting.saleAgent.events.CompanySelectedEvent;

import org.planetaccounting.saleAgent.databinding.CompanyListItemBinding;
import org.planetaccounting.saleAgent.model.login.CompanyAllowed;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macb on 09/12/17.
 */

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.ViewHolder> {

    private List<CompanyAllowed> companies = new ArrayList<>();
    private Context ctx;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        CompanyListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(ctx),
                org.planetaccounting.saleAgent.R.layout.company_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CompanyListItemBinding binding = holder.binding;
        binding.companyNameTextview.setText(companies.get(position).getCompanyName());
        Glide.with(ctx).load("http://"+companies.get(position).getLogo());
        binding.getRoot().setOnClickListener(view -> EventBus.getDefault().post(new CompanySelectedEvent(companies.get(position).getCompanyID())));

    }

    @Override
    public int getItemCount() {
        return companies.size();
    }


    public void setCompanies(List<CompanyAllowed> companies) {
        this.companies = companies;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CompanyListItemBinding binding;

        ViewHolder(CompanyListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}