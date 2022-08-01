package org.planetaccounting.saleAgent.ngarkime;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.databinding.UploadDetailListItemBinding;
import org.planetaccounting.saleAgent.model.ngarkimet.UploadsDetailItem;

import java.util.ArrayList;

public class UploadDetailListAdapter extends RecyclerView.Adapter<UploadDetailListAdapter.ViewHolder> {

    ArrayList<UploadsDetailItem> uploadsDetailItems = new ArrayList<>();
    private Context ctx;

    public UploadDetailListAdapter(ArrayList<UploadsDetailItem> uploadsDetailItems) {
        this.uploadsDetailItems = uploadsDetailItems;
    }

    @Override
    public UploadDetailListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        UploadDetailListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(ctx),
                R.layout.upload_detail_list_item, parent, false);
        return new UploadDetailListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(UploadDetailListAdapter.ViewHolder holder, int position) {
        UploadDetailListItemBinding binding = holder.binding;
        binding.shifra.setText(uploadsDetailItems.get(position).getNumber());
        binding.name.setText(uploadsDetailItems.get(position).getName());
        binding.sasia.setText(uploadsDetailItems.get(position).getQuantity());
        binding.barkod.setText(uploadsDetailItems.get(position).getBarcode());
        binding.njesia.setText(uploadsDetailItems.get(position).getUnit());
        }

    @Override
    public int getItemCount() {
        return uploadsDetailItems.size();
    }

    public void setOrders(ArrayList<UploadsDetailItem> uploadsDetailItems) {
        this.uploadsDetailItems = uploadsDetailItems;
        System.out.println("items "+uploadsDetailItems.size());
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private UploadDetailListItemBinding binding;

        ViewHolder(UploadDetailListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            }
    }
}
