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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
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
import org.planetaccounting.saleAgent.databinding.SaleDayLayoutDemoBinding;
import org.planetaccounting.saleAgent.databinding.TotalSaleTargetActivityBinding;
import org.planetaccounting.saleAgent.databinding.TotalSaleTargetActivityDemoBinding;
import org.planetaccounting.saleAgent.model.target.BaseTargetPost;
import org.planetaccounting.saleAgent.model.target.TotalSaleTarget;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tahirietrit on 3/20/18.
 */

public class TargetCashActivity extends Activity implements OnChartValueSelectedListener {

    TotalSaleTargetActivityDemoBinding binding;
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;

    private Typeface tf;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.total_sale_target_activity_demo);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        getBaseTarget();
        setChart();
        setChartTwo();
        binding.title.setText("Target Inkasim");
    }

    private void getBaseTarget() {
        String month = String.valueOf((Calendar.getInstance().get(Calendar.MONTH) + 1));
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        apiService.getCashTarget(new BaseTargetPost(preferences.getUserId(), preferences.getToken(),
                TargetActivity.MONTH, TargetActivity.YEAR))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    setupView(response.getData().getCashCollection());
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }
    private void setupView(TotalSaleTarget totalSaleTarget) {

        setData(totalSaleTarget);
        setDataTwo(totalSaleTarget);

//        binding.targetiTotal.setText("Targeti: " + totalSaleTarget.getTarget());
//        binding.realizimiTotal.setText("Realizimi: " + totalSaleTarget.getTotalSale());
//        binding.perqindjaTotal.setText("Perqindje: " + totalSaleTarget.getTargetPercentage() + " %");
//        binding.perfitimiTotal.setText("Benefiti: " + totalSaleTarget.getBenefit());
        for (int i = 0; i < totalSaleTarget.getPeriod().size(); i++) {
            SaleDayLayoutDemoBinding binding = DataBindingUtil.inflate(getLayoutInflater(),
                    R.layout.sale_day_layout_demo, this.binding.saleGrid, false);
            binding.dateTextview.setText(totalSaleTarget.getPeriod().get(i).getData());
            binding.valueTextview.setText(totalSaleTarget.getPeriod().get(i).getAmount());
            binding.dailyPercentage.setText(totalSaleTarget.getPeriod().get(i).getPercent() + "");
            int margin = Utils.dpToPx(10);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            binding.getRoot().setLayoutParams(params);
            this.binding.saleGrid.addView(binding.getRoot());
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
//        binding.chartTwo.setPinchZoom(true);

//        // set an alternative background color
//        mChart.setBackgroundColor(Color.LTGRAY);

        // add data
//        setData(20, 30);

        binding.chartTwo.animateX(2000);


        XAxis xAxis = binding.chartTwo.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = binding.chartTwo.getAxisLeft();
        leftAxis.setTypeface(tf);
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

    private void setData(TotalSaleTarget totalSaleTarget) {


        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        entries.add(new PieEntry((float) 1, "Targeti: " + totalSaleTarget.getTarget()));
        entries.add(new PieEntry((float)1 , "Realizimi: " + totalSaleTarget.getTotalSale()));
        entries.add(new PieEntry((float) 1, "Perqindje: " + totalSaleTarget.getTargetPercentage() + " %"));
        entries.add(new PieEntry((float) 1, "Benefiti: " + totalSaleTarget.getBenefit()));




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

    private void setDataTwo(TotalSaleTarget totalSaleTarget) {
        ArrayList<Entry> e1 = new ArrayList<Entry>();
        ArrayList<Entry> e2 = new ArrayList<Entry>();


        float value = 0;

        for (int i = 0; i < totalSaleTarget.getPeriod().size(); i++) {

            value =  Float.valueOf (totalSaleTarget.getPeriod().get(i).getAmount()) + value ;

            e1.add(new Entry((float)Integer.parseInt (totalSaleTarget.getPeriod().get(i).getData().split("-")[0]), (float) value));

            e2.add(new Entry((float)Integer.parseInt (totalSaleTarget.getPeriod().get(i).getData().split("-")[0]), Float.valueOf(totalSaleTarget.getTarget().replace(",",""))));


        }



        for (int i = 0; i < 31; i++) {
        }

        LineDataSet d1 = new LineDataSet(e1, "Realizimi");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);


        for (int i = 0; i < 31; i++) {
        }

        LineDataSet d2 = new LineDataSet(e2, "Targeti");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
        sets.add(d2);

        LineData cd = new LineData(sets);

        binding.chartTwo.setData(cd);

    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Targeti Inkasim\n developed by KAF");
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
