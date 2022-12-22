package org.planetaccounting.saleAgent.clients.infos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.model.clientBanks.Bank;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class CustomDpAdapter extends ArrayAdapter<Bank> {

    public CustomDpAdapter(@NonNull Context context, ArrayList<Bank> customList) {
        super(context, 0, customList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }

        Bank item = getItem(position);
        ImageView spinnerIv = convertView.findViewById(R.id.ivSpinnerLayout);
        TextView spinnetTv = convertView.findViewById(R.id.tvSpinnerLayout);

        if (item != null) {
            spinnerIv.setImageResource(Integer.parseInt(item.getLogo()));
            spinnetTv.setText(item.getName());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dropdown_layout, parent, false);
        }

        Bank item = getItem(position);
        ImageView dropDownIv = convertView.findViewById(R.id.ivDropDownLayout);
        TextView dropDownTv = convertView.findViewById(R.id.tvDropDownLayout);

        if(item != null){
            dropDownIv.setImageResource(Integer.parseInt(item.getLogo()));
            dropDownTv.setText(item.getName());
        }
        return convertView;
    }
}
