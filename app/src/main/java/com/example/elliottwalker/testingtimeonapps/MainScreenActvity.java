package com.example.elliottwalker.testingtimeonapps;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.util.ArrayMap;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainScreenActvity extends AppCompatActivity {
    private long totalTime = 0;
    private HashMap<String, Drawable> iconImages = new HashMap<>();
    private final ArrayMap<String, String> labelArrayMap = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen_actvity);

        //Dates for the x axis.
        //TODO: SHOULD THIS INCLUDE TODAY OR NOT
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d4 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d5 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d6 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d7 = calendar.getTime();

        GraphView graph = (GraphView) findViewById(R.id.graph);

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        int[] totals = new int[7];
        PackageManager pkgManager = getPackageManager();
        
        //Method to get total time usage data for each day
        //For loop where i is day
        for(int i = 0; i < 7; i++){
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            calendar.set(year, month, day, 0, 0, 0);

            long startCal = calendar.getTimeInMillis();
            calendar.set(year, month, day, 23, 59, 59);
            long finishCal = calendar.getTimeInMillis();

            final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startCal, finishCal);

            //If an empty list is returned from stats above then return.
            if (stats == null) {
                return;
            }


            ArrayMap<String, UsageStats> map = new ArrayMap<>();
            final int sCounter = stats.size();
            long tempTotalTime = 0;
            for (int j = 0; j < sCounter; j++) {
                final android.app.usage.UsageStats pkgStats = stats.get(j);

                //Removing apps that the user hasn't used i.e. any apps that have been open less than 5 seconds.
                if (stats.get(j).getTotalTimeInForeground() <= 5000) {
                    continue;
                }

                String currentDate = DateFormat.getDateInstance().format(new Date());

                //Removes apps that were being displayed that were not from todays date.
                if (stats.get(j).getLastTimeUsed() < startCal) {
                    continue;
                }

                //Loads all app labels
                try {
                    ApplicationInfo appInfo = pkgManager.getApplicationInfo(pkgStats.getPackageName(), 0);

                    //Converts app label into string
                    String label = appInfo.loadLabel(pkgManager).toString();
                    String packageName = pkgStats.getPackageName();
                    iconImages.put(packageName, getPackageManager().getApplicationIcon(pkgStats.getPackageName()));
                    labelArrayMap.put(pkgStats.getPackageName(), label);

                    UsageStats currentStats = map.get(pkgStats.getPackageName());
                    if (currentStats == null) {
                        map.put(pkgStats.getPackageName(), pkgStats);
                    } else {
                        currentStats.add(pkgStats);
                    }

                    //Divide by 1000 to get in correct time
                    tempTotalTime = (tempTotalTime + (pkgStats.getTotalTimeInForeground()));
                    totalTime = tempTotalTime;

                    //Total app usage times
                    totals[6 - i] = (int) totalTime;
                } catch (PackageManager.NameNotFoundException e) {
                    System.out.println("Package not found! " + e.getLocalizedMessage());
                }
            }
        }


        //This should be the total usage time for each day.
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d1, totals[0]),
                new DataPoint(d2, totals[1]),
                new DataPoint(d3, totals[2]),
                new DataPoint(d4, totals[3]),
                new DataPoint(d5, totals[4]),
                new DataPoint(d6, totals[5]),
                new DataPoint(d7, totals[6])
        });

        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)));

        //Sets number of horizontal labels
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d7.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }
}
