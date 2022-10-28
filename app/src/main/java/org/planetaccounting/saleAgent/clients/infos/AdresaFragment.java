package org.planetaccounting.saleAgent.clients.infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.model.clients.Client;

import java.util.ArrayList;

public class AdresaFragment extends Fragment{

    public AdresaFragment() {
        //na duhet ni empty adapter...
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //Inflate the layout for this fragment...

        View rootview = inflater.inflate(R.layout.client_adress_layout, container, false);
        return rootview;
    }
}
