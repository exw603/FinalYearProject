/*
package com.example.elliottwalker.testingtimeonapps;


import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class StackedBarChartActivity extends DemoBase implements OnSeekBarChangeListener, OnChartValueSelectedListener {
    private BarChart bChart;
    private SeekBar seekBarX;
    private SeekBar seekBarY;
    private TextView textX;
    private TextView textY;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);

        textX = findViewById(R.id.tvXMax);
        textY = findViewById(R.id.tvYMax);

        seekBarX = findViewBayId(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);

        seekBarY = findViewById(R.id.seekBar2);
        seekBarY.setOnSeekBarChangeListener(this);

        bChart = findViewById(R.id.chart1);
        bChart.setOnChartValueSelectedListener(this);

        bChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        bChart.setMaxVisibleValueCount(40);

        // scaling can now only be done on x- and y-axis separately
        bChart.setPinchZoom(false);

        bChart.setDrawGridBackground(false);
        bChart.setDrawBarShadow(false);

        bChart.setDrawValueAboveBar(false);
        bChart.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = bChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyAxisValueFormatter());
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        bChart.getAxisRight().setEnabled(false);

        XAxis xLabels = bChart.getXAxis();
        xLabels.setPosition(XAxisPosition.TOP);

        // mChart.setDrawXLabels(false);
        // mChart.setDrawYLabels(false);

        // setting data
        seekBarX.setProgress(12);
        seekBarY.setProgress(100);

        Legend l = bChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        // mChart.setDrawLegend(false);

    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textX.setText("" + (seekBarX.getProgress() + 1));
        textY.setText("" + (seekBarY.getProgress()));

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < seekBarX.getProgress() + 1; i++) {
            float mult = (seekBarY.getProgress() + 1);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            float val2 = (float) (Math.random() * mult) + mult / 3;
            float val3 = (float) (Math.random() * mult) + mult / 3;

            yVals1.add(new BarEntry(
                    i,
                    new float[]{val1, val2, val3},
                    getResources().getDrawable(R.drawable.star)));

        }

        BarDataSet set1;

        if (bChart.getData() != null &&
                bChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) bChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            bChart.getData().notifyDataChanged();
            bChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Statistics Vienna 2014");
            set1.setDrawIcons(false);
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{"Births", "Divorces", "Marriages"});

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new MyValueFormatter());
            data.setValueTextColor(Color.WHITE);

            bChart.setData(data);
        }

        bChart.setFitBars(true);
        bChart.invalidate();

    }

    private int[] getColors(){
        int stacksize = 4;
        int[] colours = new int[stacksize];

        for (int i = 0; i < colours.length; i++){
            //TODO: Change these colours.
            colours[i] = ColorTemplate.MATERIAL_COLORS[i];
        }
        return colours;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}

 */