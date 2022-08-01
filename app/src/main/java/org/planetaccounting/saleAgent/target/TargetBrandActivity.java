package org.planetaccounting.saleAgent.target;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.BrandSaleLayoutBinding;
import org.planetaccounting.saleAgent.databinding.SaleDayLayoutBinding;
import org.planetaccounting.saleAgent.databinding.TargetBrandActivityBinding;
import org.planetaccounting.saleAgent.model.target.BaseTargetPost;
import org.planetaccounting.saleAgent.model.target.BrandDetail;
import org.planetaccounting.saleAgent.model.target.BrandTargetData;
import org.planetaccounting.saleAgent.model.target.BrandTargetResponse;
import org.planetaccounting.saleAgent.model.target.Period;
import org.planetaccounting.saleAgent.model.target.TotalSaleTarget;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tahirietrit on 3/23/18.
 */

public class TargetBrandActivity extends Activity implements OnChartValueSelectedListener {


    TargetBrandActivityBinding binding;
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    RelativeLayout.LayoutParams params;

    private Typeface tf;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.target_brand_activity);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        params = new RelativeLayout.LayoutParams(Utils.getScreenWidth() / 7, Utils.getScreenWidth() / 14);
        setChart();
        setChartTwo();
        getBrandTarget();


    }

    public void getBrandTarget() {
        String month = String.valueOf((Calendar.getInstance().get(Calendar.MONTH) + 1));
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        apiService.getBrandTarget(new BaseTargetPost(preferences.getUserId(), preferences.getToken(),
                TargetActivity.MONTH, TargetActivity.YEAR))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        brandTargetResponse ->
                        setupView(brandTargetResponse.getData())

                        , Throwable::printStackTrace);
    }

    private void setupView(BrandTargetData data) {

        String test = "";
        setData(data);
        setDataTwo(data);
//        binding.targetiTotal.setText("Targeti: " + data.getBrandTotal().getTarget());
//        binding.realizimiTotal.setText("Realizimi: " + data.getBrandTotal().getTotalSale());
//        binding.perqindjaTotal.setText("Perqindja: " + data.getBrandTotal().getTargetPercentage() + "%");
//        binding.perfitimiTotal.setText("Benefiti: " + data.getBrandTotal().getBenefit());
        for (int i = 0; i < data.getBrandDetails().size(); i++) {
            BrandSaleLayoutBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                    R.layout.brand_sale_layout, this.binding.brandHolder, false);
            itemBinding.nr.setText("" + (i + 1));
            itemBinding.brendiTextview.setText("" + data.getBrandDetails().get(i).getBrandName());
            itemBinding.targetiTotal.setText("" + data.getBrandDetails().get(i).getTarget());
            itemBinding.realizimiTotal.setText("" + data.getBrandDetails().get(i).getTotalSale());
            itemBinding.perqindjaTotal.setText("" + data.getBrandDetails().get(i).getTargetPercentage() + "%");
            itemBinding.perfitimiTotal.setText("" + data.getBrandDetails().get(i).getBenefit());
            this.binding.brandHolder.addView(itemBinding.getRoot());
        }
    }


    private void setChart(){


        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");


        binding.chartOne.setUsePercentValues(true);
        binding.chartOne.getDescription().setEnabled(false);
        binding.chartOne.setExtraOffsets(5, 10, 5, 5);

        binding.chartOne.setDragDecelerationFrictionCoef(0.95f);

        binding.chartOne.setCenterTextTypeface(tf);
        binding.chartOne.setCenterText(generateCenterSpannableText());

        binding.chartOne.setDrawHoleEnabled(true);
        binding.chartOne.setHoleColor(Color.WHITE);

        binding.chartOne.setTransparentCircleColor(Color.WHITE);
        binding.chartOne.setTransparentCircleAlpha(110);

        binding.chartOne.setHoleRadius(58f);
        binding.chartOne.setTransparentCircleRadius(61f);

        binding.chartOne.setDrawCenterText(true);

        binding.chartOne.setRotationAngle(0);
        // enable rotation of the chart by touch
        binding.chartOne.setRotationEnabled(true);
        binding.chartOne.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        binding.chartOne.setOnChartValueSelectedListener(this);

        binding.chartOne.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);


        Legend l = binding.chartOne.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        binding.chartOne.setEntryLabelColor(Color.BLACK);
        binding.chartOne.setEntryLabelTypeface(tf);
        binding.chartOne.setEntryLabelTextSize(12f);


    }

    private void setChartTwo(){

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");



//        mChart.setOnChartValueSelectedListener(this);

        // no description text
        binding.chartTwo.getDescription().setEnabled(false);

//        // enable touch gestures
//        binding.chartTwosetTouchEnabled(true);

//        mChart.setDragDecelerationFrictionCoef(0.9f);
//
//        // enable scaling and dragging
//        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
        binding.chartTwo.setDrawGridBackground(false);
//        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        binding.chartTwo.setPinchZoom(true);

//        // set an alternative background color
//        mChart.setBackgroundColor(Color.LTGRAY);

        // add data
//        setData(20, 30);

        binding.chartTwo.animateX(2000);

        LimitLine ll1 = new LimitLine(100f, "Targeti");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);


        XAxis xAxis = binding.chartTwo.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = binding.chartTwo.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.addLimitLine(ll1);
//        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = binding.chartTwo.getAxisRight();
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



    private void setData(BrandTargetData data1) {


        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        entries.add(new PieEntry((float) 1, "Targeti: " + data1.getBrandTotal().getTarget()));
        entries.add(new PieEntry((float)1 , "Realizimi: " + data1.getBrandTotal().getTotalSale()));
        entries.add(new PieEntry((float) 1, "Perqindje: " + data1.getBrandTotal().getTargetPercentage() + " %"));
        entries.add(new PieEntry((float) 1, "Benefiti: " + data1.getBrandTotal().getBenefit()));




        PieDataSet dataSet = new PieDataSet(entries, "Targeti Total");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.TRANSPARENT);
        data.setValueTypeface(tf);
        binding.chartOne.setData(data);

        // undo all highlights
        binding.chartOne.highlightValues(null);

        binding.chartOne.invalidate();
    }

    private void setDataTwo(BrandTargetData data1){



        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();


        Random rnd = new Random();

        ArrayList<Integer> total = new ArrayList<Integer>();


        for (int i = 0; i < data1.getBrandDetails().size(); i++) {
            BrandDetail brandDetail = data1.getBrandDetails().get(i);

            if ( (brandDetail.getPeriod()!= null) && (brandDetail.getPeriod().size()>0)) {

                ArrayList<Entry> e = new ArrayList<Entry>();


                        for (int k = 0; k < brandDetail.getPeriod().size(); k++) {

                            float percent = brandDetail.getPeriod().get(k).getPercent();
                            total.add( Integer.parseInt (brandDetail.getPeriod().get(k).getData().split("-")[0]));
                            e.add(new Entry((float) Integer.parseInt (brandDetail.getPeriod().get(k).getData().split("-")[0]), percent));
                        }

                        String name = brandDetail.getBrandName();

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



        LineData cd = new LineData(sets);

        binding.chartTwo.setData(cd);
    }


    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Targeti Brand\n developed by KAF");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
