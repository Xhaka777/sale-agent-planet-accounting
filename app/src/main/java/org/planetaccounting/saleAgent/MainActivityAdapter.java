package org.planetaccounting.saleAgent;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.planetaccounting.saleAgent.databinding.RecyclerviewMainActivityBinding;
import org.planetaccounting.saleAgent.model.MainAdaperModel;
import org.planetaccounting.saleAgent.model.role.Role;

import java.util.ArrayList;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {
    private Context mContext;
    Listener listener;

    private int[] title = {R.string.title_fatura, R.string.title_stoku, R.string.title_inkasimi, R.string.title_depozita, R.string.title_raportet, R.string.title_kthimMalli, R.string.title_transfere, R.string.title_klientet, R.string.title_target, R.string.title_porositeInterne, R.string.title_shpenzimet, R.string.title_aksionet};

    private int[] icon = {R.drawable.ic_fatura, R.drawable.ic_stokut, R.drawable.ic_inkasimet, R.drawable.ic_bank, R.drawable.ic_raportet, R.drawable.ic_return, R.drawable.ic_transferet, R.drawable.ic_klientet, R.drawable.ic_targetet, R.drawable.ic_prosit, R.drawable.ic_shpenzim, R.drawable.ic_action };

    private ArrayList<MainAdaperModel> rols =  new ArrayList<>();


    public MainActivityAdapter(Listener listener , Role role){

        this.listener = listener;

        int counter  = 0;
        for (Integer items:role.getMain().isInRole()) {


            if (items == 1 || items.equals(0)){
                MainAdaperModel main = new MainAdaperModel(title[counter],icon[counter]);
                rols.add(main);
                counter++;
            }
        }
        }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        RecyclerviewMainActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.recyclerview_main_activity,parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecyclerviewMainActivityBinding binding = holder.binding;


        binding.titleLabel.setText(rols.get(position).getTitle());

        binding.mainImg.setImageResource(rols.get(position).getIcon());


        binding.container.setOnClickListener(v ->
                listener.onClick(rols.get(position).getTitle(),position));

    }

    @Override
    public int getItemCount() {
        return rols.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewMainActivityBinding binding;

        ViewHolder( RecyclerviewMainActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            }
    }

    interface Listener{
        void onClick(int title, int positon);
    }
}
