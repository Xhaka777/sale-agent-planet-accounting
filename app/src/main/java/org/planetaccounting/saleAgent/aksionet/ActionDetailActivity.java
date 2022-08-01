package org.planetaccounting.saleAgent.aksionet;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.databinding.ActivityActionDetailBinding;
import org.planetaccounting.saleAgent.databinding.AllActionDetailHolderBinding;
import org.planetaccounting.saleAgent.persistence.RealmHelper;

import java.util.List;

import javax.inject.Inject;

public class ActionDetailActivity extends AppCompatActivity {

   private ActivityActionDetailBinding binding;

    @Inject
    RealmHelper realmHelper;

    ActionData actionData;


    private String barTitle = "";
    private String title = "";
    private String titleContent = "";
    private String status = "";
    private String shifra = "";
    private String client = "";
    private String discount = "";
    private String amount = "";
    private String nga = "";
    private String deri = "";
    private String from = "";
    private String id = "";
    private String type = "";
    private String unit = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_action_detail);

        from =  getIntent().getStringExtra("from");
        id =  getIntent().getStringExtra("id");

        barTitle = getIntent().getStringExtra("bar_title");
        title = getIntent().getStringExtra("title");
        titleContent = getIntent().getStringExtra("title_count");
        status = getIntent().getStringExtra("status");
        shifra = getIntent().getStringExtra("shifra");
        client = getIntent().getStringExtra("client");
        discount = getIntent().getStringExtra("discount");
        amount = getIntent().getStringExtra("amount");
        type = getIntent().getStringExtra("type");
        unit = getIntent().getStringExtra("unit");


        nga = getIntent().getStringExtra("nga");
        deri = getIntent().getStringExtra("deri");

        Kontabiliteti.getKontabilitetiComponent().inject(this);

        binding.barTitle.setText(barTitle);
        binding.title.setText(title);
        binding.titleText.setText(titleContent);
        binding.staus.setText(status);
        binding.shifra.setText(shifra);
        binding.client.setText(client);
        binding.discount.setText(discount);
        binding.amount.setText(amount);
        binding.type.setText(type);
        binding.unit.setText(unit);
        binding.nga.setText(nga);
        binding.deri.setText(deri);
        actionData = realmHelper.getAksionet();
        switch (from){
            case "article" :
                for (ActionArticleItems action: actionData.getArticleItems()) {
                    if (action.getId().equals(id)){
                        fillSteps(action.getSteps(),action.getType());
                    }
                }
                break;

            case "brand" :
                for (ActionBrandItem action: actionData.getArticleBrandItem()) {
                    if (action.getId().equals(id)){
                        fillSteps(action.getSteps(),action.getType());
                    }
                }
                break;

            case "category" :
                for (ActionBrandItem action: actionData.getArticleBrandItem()) {
                    if (action.getId().equals(id)){
                        fillSteps(action.getSteps(),action.getType());
                    }
                }
                break;
        }


        }

        private void fillSteps(List<ActionSteps> steps,String type){

            for (ActionSteps step:steps) {


                AllActionDetailHolderBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                        R.layout.all_action_detail_holder, binding.allActionHolder, false);

                itemBinding.from.setText(step.getFrom());
                itemBinding.to.setText(step.getTo());
                itemBinding.discount.setText(step.getDiscount());

                binding.allActionHolder.addView(itemBinding.getRoot());
                }

        }
}
