package org.planetaccounting.saleAgent.target;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.SkuTargetActivityBinding;
import org.planetaccounting.saleAgent.databinding.SkuTargetItemBinding;
import org.planetaccounting.saleAgent.model.target.BaseTargetPost;
import org.planetaccounting.saleAgent.model.target.SkuData;
import org.planetaccounting.saleAgent.model.target.SkuDetail;
import org.planetaccounting.saleAgent.model.target.SkuTargetResponse;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.planetaccounting.saleAgent.target.TargetActivity.MONTH;
import static org.planetaccounting.saleAgent.target.TargetActivity.YEAR;

/**
 * Created by tahirietrit on 4/3/18.
 */

public class TargetSkuActivity extends Activity implements OnChartValueSelectedListener {
    SkuTargetActivityBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;

    private Typeface tf;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.sku_target_activity);

        Kontabiliteti.getKontabilitetiComponent().inject(this);
        getSkuTarget();
        setChartTwo();

    }

    private void getSkuTarget() {
        apiService.getSkuTarget(new BaseTargetPost(preferences.getUserId(), preferences.getToken(),
                MONTH, YEAR))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setupSkuTargetViews, Throwable::printStackTrace);
    }

    private void setupSkuTargetViews(SkuTargetResponse skuTargetResponse) {
        setData(skuTargetResponse);
        for (int i = 0; i < skuTargetResponse.getData().size(); i++) {
            SkuData skuData = skuTargetResponse.getData().get(i);
            if (skuData.getDetails() != null && skuData.getDetails().size() > 0) {
                for (int j = 0; j < skuData.getDetails().size(); j++) {
                    SkuDetail skuDetail = skuData.getDetails().get(j);
                    SkuTargetItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.sku_target_item, binding.skuItemHolder, false);
                    System.out.println("sku " + skuData.getType());
                    itemBinding.lloji.setText(skuData.getType());
                    if (skuDetail.getArticleName() != null) {
                        itemBinding.emri.setText(skuDetail.getArticleName());
                    }
                    if (skuDetail.getBrandName() != null) {
                        itemBinding.emri.setText(skuDetail.getBrandName());
                    }
                    if (skuDetail.getCategoryName() != null) {
                        itemBinding.emri.setText(skuDetail.getCategoryName());
                    }
                    if (skuDetail.getManufacturerName() != null) {
                        itemBinding.emri.setText(skuDetail.getManufacturerName());
                    }
                    if (skuDetail.getSubcategoryName() != null) {
                        itemBinding.emri.setText(skuDetail.getSubcategoryName());
                    }
                    itemBinding.targeti.setText(skuDetail.getTarget());
                    itemBinding.realizimi.setText(skuDetail.getTotalSale());
                    itemBinding.perqindja.setText(skuDetail.getTargetPercentage());
                    itemBinding.benefiti.setText(skuDetail.getBenefit());
                    binding.skuItemHolder.addView(itemBinding.getRoot());
                }
            }

        }
    }

    private void setChartTwo(){

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");



//        mChart.setOnChartValueSelectedListener(this);

        // no description text
        binding.chart.getDescription().setEnabled(false);

//        // enable touch gestures
//        binding.chartTwosetTouchEnabled(true);

//        mChart.setDragDecelerationFrictionCoef(0.9f);
//
//        // enable scaling and dragging
//        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
        binding.chart.setDrawGridBackground(false);
//        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        binding.chart.setPinchZoom(true);

//        // set an alternative background color
//        mChart.setBackgroundColor(Color.LTGRAY);

        // add data
//        setData(20, 30);

        binding.chart.animateX(2000);

        LimitLine ll1 = new LimitLine(100f, "Targeti");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);


        XAxis xAxis = binding.chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = binding.chart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.addLimitLine(ll1);
//        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = binding.chart.getAxisRight();
        rightAxis.setTypeface(tf);
//        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)








//        // get the legend (only possible after setting data)
//        Legend l = mChart.getLegend();
//
//        // modify the legend ...
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(mTfLight);
//        l.setTextSize(11f);
//        l.setTextColor(Color.WHITE);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
////        l.setYOffset(11f);
//
//        XAxis xAxis = mChart.getXAxis();
//        xAxis.setTypeface(mTfLight);
//        xAxis.setTextSize(11f);
//        xAxis.setTextColor(Color.WHITE);
//        xAxis.setDrawGridLines(false);
//        xAxis.setDrawAxisLine(false);
//
//        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
//        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
//        leftAxis.setAxisMaximum(200f);
//        leftAxis.setAxisMinimum(0f);
//        leftAxis.setDrawGridLines(true);
//        leftAxis.setGranularityEnabled(true);
//
//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setTypeface(mTfLight);
//        rightAxis.setTextColor(Color.RED);
//        rightAxis.setAxisMaximum(900);
//        rightAxis.setAxisMinimum(-200);
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setDrawZeroLine(false);
//        rightAxis.setGranularityEnabled(false);

    }

    private void setData(SkuTargetResponse skuTargetResponse) {


        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();


        Random rnd = new Random();

        ArrayList<Integer> total = new ArrayList<Integer>();


        for (int i = 0; i < skuTargetResponse.getData().size(); i++) {
            SkuData skuData = skuTargetResponse.getData().get(i);

            if (skuData.getDetails() != null && skuData.getDetails().size() > 0) {
                for (int j = 0; j < skuData.getDetails().size(); j++) {

                    if ( (skuData.getDetails().get(j).getPeriod()!= null) && (skuData.getDetails().get(j).getPeriod().size()>0)) {


                        SkuDetail skuDetail = skuData.getDetails().get(j);


                        ArrayList<Entry> e = new ArrayList<Entry>();


                        for (int k = 0; k < skuDetail.getPeriod().size(); k++) {

                            float percent = skuDetail.getPeriod().get(k).getPercent();
                            total.add( Integer.parseInt (skuDetail.getPeriod().get(k).getData().split("-")[0]));
                            e.add(new Entry((float) Integer.parseInt (skuDetail.getPeriod().get(k).getData().split("-")[0]), percent));
                            }

                        String name = "";

                        if (skuDetail.getArticleName() != null) {
                            name = skuDetail.getArticleName();
                        }
                        if (skuDetail.getBrandName() != null) {
                            name = skuDetail.getBrandName();
                        }
                        if (skuDetail.getCategoryName() != null) {
                            name = skuDetail.getCategoryName();
                        }
                        if (skuDetail.getManufacturerName() != null) {
                            name = skuDetail.getManufacturerName();
                        }
                        if (skuDetail.getSubcategoryName() != null) {
                            name = skuDetail.getSubcategoryName();
                        }

                        LineDataSet d = new LineDataSet(e, name);
                        d.setLineWidth(2.5f);
                        d.setCircleRadius(4.5f);
                        d.setHighLightColor(Color.rgb(244, 117, 117));
                        int color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        d.setColor(color);
                        d.setCircleColor(color);
                        d.setDrawValues(false);

                        sets.add(d);

                    }

                    }
            }

            }


        LineData cd = new LineData(sets);

        binding.chart.setData(cd);

    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
