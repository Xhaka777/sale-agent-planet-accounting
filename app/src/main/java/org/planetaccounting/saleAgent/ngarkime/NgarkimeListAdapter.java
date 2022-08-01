package org.planetaccounting.saleAgent.ngarkime;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.databinding.UploadListItemBinding;
import org.planetaccounting.saleAgent.events.OpenOrderDetailEvent;
import org.planetaccounting.saleAgent.model.ngarkimet.Uploads;

import java.util.ArrayList;

public class NgarkimeListAdapter extends RecyclerView.Adapter<NgarkimeListAdapter.ViewHolder> {

    ArrayList<Uploads> uploads = new ArrayList<>();
    private Context ctx;

    public NgarkimeListAdapter(ArrayList<Uploads> uploads) {
        this.uploads = uploads;
    }

    @Override
    public NgarkimeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        UploadListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(ctx),
                R.layout.upload_list_item, parent, false);
        return new NgarkimeListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(NgarkimeListAdapter.ViewHolder holder, int position) {
        UploadListItemBinding binding = holder.binding;

        String[] data = uploads.get(position).getDate().split(" ");
        binding.data.setText(data[0]+"\n"+data[1]);
        binding.uploadNr.setText(uploads.get(position).getNumber());
        binding.wareHouseFrom.setText(uploads.get(position).getStationNameFrom());
        binding.wareHouseTo.setText(uploads.get(position).getStationNameTo());
        binding.employeeFrom.setText(uploads.get(position).getEmployeeFrom());
        binding.employeeTo.setText(uploads.get(position).getEmployeeTo());
        binding.description.setText(uploads.get(position).getDescription());

        binding.getRoot().setOnClickListener(view -> EventBus.getDefault().post(new OpenOrderDetailEvent(uploads.get(position).getId(),"")));
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }


    public void setUploads(ArrayList<Uploads> uploads) {
        this.uploads = uploads;
        notifyDataSetChanged();
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private UploadListItemBinding binding;

        ViewHolder(UploadListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}