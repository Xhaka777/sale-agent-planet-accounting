package org.planetaccounting.saleAgent.clients.infos;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.model.clients.Client;

import java.util.ArrayList;
import java.util.List;

public class OptionsAdapter extends ArrayAdapter<Client> {

    private List<Client> clients = new ArrayList<>();
    private Context context;
    private LayoutInflater mInlfater;

    public OptionsAdapter(@NonNull Activity context, ArrayList<Client> clients) {
        super(context, 0, clients);
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent){

        View adress = convertView;
        if(adress == null){
            adress = LayoutInflater.from(getContext()).inflate(R.layout.client_options_layout, parent, false);
        }

        TextView options = adress.findViewById(R.id.business_number_edittext);
        options.setText("");
        return adress;
    }
}
