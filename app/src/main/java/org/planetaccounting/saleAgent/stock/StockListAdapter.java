package org.planetaccounting.saleAgent.stock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bumptech.glide.Glide;

import org.planetaccounting.saleAgent.model.stock.Item;
import org.planetaccounting.saleAgent.model.stock.SubItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by macb on 09/12/17.
 */

public class StockListAdapter extends ExpandableRecyclerAdapter<Item, SubItem, StockListAdapter.ParentViewHolder, StockListAdapter.ChildViewHolder> {

    List<Item> recipeList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context ctx;

    public StockListAdapter(Context context, @NonNull List<Item> recipeList) {
        super(recipeList);
        this.recipeList = recipeList;
        mInflater = LayoutInflater.from(context);
        ctx = context;
    }

    public void setRecipeList(List<Item> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    @Override
    public ParentViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View recipeView = mInflater.inflate(org.planetaccounting.saleAgent.R.layout.parent_stock_item, parentViewGroup, false);
        return new ParentViewHolder(recipeView);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View ingredientView = mInflater.inflate(org.planetaccounting.saleAgent.R.layout.child_stock_item, childViewGroup, false);
        return new ChildViewHolder(ingredientView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull ParentViewHolder parentViewHolder, int parentPosition, @NonNull Item item) {
        if (item.getItems().size() > 0) {
            parentViewHolder.bind(item, "http://" + item.getItems().get(0).getCoverImg());
        } else {
            parentViewHolder.bind(item, "");
        }

    }

    @Override
    public void onBindChildViewHolder(@NonNull ChildViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull SubItem ingredient) {
        childViewHolder.bind(ingredient, parentPosition);
    }

    public class ParentViewHolder extends com.bignerdranch.expandablerecyclerview.ParentViewHolder {

        private TextView tipiTextView;
        private TextView shifraTextView;
        private TextView artikulliTextView;
        private TextView sasiaTextView;
        private TextView vleraTextView;
        private ImageView articleImage;

        public ParentViewHolder(View itemView) {
            super(itemView);
            tipiTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.tipi_textview);
            shifraTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.shifra_textview);
            artikulliTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.artikulli_textview);
            sasiaTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.sasia_textview);
            vleraTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.vlera_textview);
            articleImage = itemView.findViewById(org.planetaccounting.saleAgent.R.id.article_image);
        }

        public void bind(Item item, String image) {
            tipiTextView.setText(item.getDefaultUnit());
            shifraTextView.setText(item.getNumber());
            artikulliTextView.setText(item.getName());
            sasiaTextView.setText(item.getQuantity());
            vleraTextView.setText(String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(item.getAmount()))+"");
            double pQuantity = Double.parseDouble(item.getQuantity());
            double relation = Double.parseDouble(item.getRelacion());
            if(item.getType().equalsIgnoreCase("action")){
                sasiaTextView.setText("" + cutTo2(pQuantity ));
            }
            else{
                sasiaTextView.setText("" + cutTo2(pQuantity / relation));
            }

            Glide.with(ctx).load(image).into(articleImage);
        }
    }

    public class ChildViewHolder extends com.bignerdranch.expandablerecyclerview.ChildViewHolder {

        private TextView tipiTextView;
        private TextView shifraTextView;
        private TextView artikulliTextView;
        private TextView sasiaTextView;
        private TextView vleraTextView;
        private ImageView articleImage;

        public ChildViewHolder(View itemView) {
            super(itemView);
            tipiTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.tipi_textview);
            shifraTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.shifra_textview);
            artikulliTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.artikulli_textview);
            sasiaTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.sasia_textview);
            vleraTextView = itemView.findViewById(org.planetaccounting.saleAgent.R.id.vlera_textview);
            articleImage = itemView.findViewById(org.planetaccounting.saleAgent.R.id.article_image);
        }

        public void bind(SubItem item, int parentPos) {
            tipiTextView.setText(item.getUnit());
            shifraTextView.setText(item.getNumber());
            artikulliTextView.setText(item.getName());
            double pQuantity = Double.parseDouble(recipeList.get(parentPos).getQuantity());
            double relation = item.getRelacion();
            if(recipeList.get(parentPos).getType().equalsIgnoreCase("action")){

                sasiaTextView.setText("" + cutTo2(Double.parseDouble(item.getQuantity())));
            }
            else{
                sasiaTextView.setText("" + cutTo2(pQuantity / relation));
            }
            vleraTextView.setText(""+cutTo2(Double.parseDouble(recipeList.get(parentPos).getAmount())));
            Glide.with(ctx).load("http://" + item.getCoverImg()).into(articleImage);
        }
    }
    public double cutTo2(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", value));
    }
}