package org.planetaccounting.saleAgent.clients.infos;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.model.clients.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientInfoAdapter extends ArrayAdapter<Client> {

    private List<Client> clients = new ArrayList<>();
    private Context context;
    private LayoutInflater mInflater;



    public ClientInfoAdapter(@NonNull Activity context, ArrayList<Client> clients){
        super(context, 0, clients);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View adress = convertView;
        if(adress == null){
            adress = LayoutInflater.from(getContext()).inflate(R.layout.client_adress_layout, parent, false);
        }

        Client client = getItem(position);

        TextView qyteti = adress.findViewById(R.id.city_adress);
        qyteti.setText(client.getName());


        return  adress;


    }
}
