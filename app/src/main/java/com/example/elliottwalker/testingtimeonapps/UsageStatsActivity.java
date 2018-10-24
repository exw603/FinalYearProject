package com.example.elliottwalker.testingtimeonapps;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.github.mikephil.charting.utils.ColorTemplate;

public class UsageStatsActivity extends Activity implements OnItemSelectedListener {
    private static final String TAG = "UsageStatsActivity";
    private static final boolean localLOGV = false;
    private UsageStatsManager mUsageStatsManager;
    private LayoutInflater mInflater;
    private UsageStatsAdapter mUsageStatsAdapter;
    private PackageManager pkgManager;
    private long totalTime = 0;
    private String sTotalTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usage_stats);

        //Swipe to refresh app time usage.
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GridView gridView = (GridView) findViewById(R.id.pkg_gridview);
                mUsageStatsAdapter = new UsageStatsAdapter();
                gridView.setAdapter(mUsageStatsAdapter);

                TextView text = (TextView) findViewById(R.id.totalTimeForAll);
                Date totalTimeD = new Date((long) totalTime);
                text.setText(new SimpleDateFormat("HH:mm:ss").format(totalTimeD));

                pullToRefresh.setRefreshing(false);
            }
        });

        mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pkgManager = getPackageManager();

        Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        typeSpinner.setOnItemSelectedListener(this);

        //ListView listView = (ListView) findViewById(R.id.pkg_listview);
        GridView gridView = (GridView) findViewById(R.id.pkg_gridview);
        mUsageStatsAdapter = new UsageStatsAdapter();
        gridView.setAdapter(mUsageStatsAdapter);

        TextView totTime = (TextView) findViewById(R.id.totalTimeForAll);
        Date totalTimeD = new Date((long) totalTime);
        totTime.setText(new SimpleDateFormat("HH:mm:ss").format(totalTimeD));
        totTime.setTextSize(30);
        totTime.setTextColor(Color.BLACK);
    }

    //Displaying app attributes
    static class AppView {
        TextView pkgName;
        TextView lastTimeUsed;
        TextView totalUsageTime;
        ImageView icon;
    }

    class UsageStatsAdapter extends BaseAdapter {
        //This will be the order on the display
        private static final int DISPLAY_USAGE_TIME = 0;
        private static final int DISPLAY_APP_NAME = 1;
        private static final int DISPLAY_LAST_TIME_USED = 2;

        private int mDisplayOrder = DISPLAY_USAGE_TIME;
        private SortLastTimeAppUsed mSortLastTimeAppUsed = new SortLastTimeAppUsed();
        private SortTotalTimeUsage mSortTotalTimeUsage = new SortTotalTimeUsage();
        private AppNameComparator appNameComparison;
        private final ArrayMap<String, String> labelArrayMap = new ArrayMap<>();
        private final ArrayList<UsageStats> pkgStats = new ArrayList<>();
        private HashMap<String, Drawable> iconImages = new HashMap<>();

        UsageStatsAdapter() {
            Calendar calendar = Calendar.getInstance();

            //Adds minus one to current day of year.
            //calendar.add(Calendar.DAY_OF_YEAR, -1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            calendar.set(year, month, day, 0, 0, 0);

            long startCal = calendar.getTimeInMillis();
            long finshCal = System.currentTimeMillis();

            final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startCal, finshCal);

            //If an empty list is returned from stats above then return.
            if (stats == null) {
                return;
            }

            ArrayMap<String, UsageStats> map = new ArrayMap<>();
            final int sCounter = stats.size();
            long tempTotalTime = 0;
            for (int i = 0; i < sCounter; i++) {
                final android.app.usage.UsageStats pkgStats = stats.get(i);

                //Removing apps that the user hasn't used i.e. any apps that have been open less than 5 seconds.
                if(stats.get(i).getTotalTimeInForeground() <= 5000){
                    continue;
                }

                String currentDate = DateFormat.getDateInstance().format(new Date());

                //Removes apps that were being displayed that were not from todays date.
                if(stats.get(i).getLastTimeUsed() < startCal){
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

                } catch (NameNotFoundException e) {
                    System.out.println("Package not found! " + e.getLocalizedMessage());
                }
            }
            pkgStats.addAll(map.values());

            //Sorts list
            appNameComparison = new AppNameComparator(labelArrayMap);
            sortList();
        }


        @Override
        public int getCount() {
            return pkgStats.size();
        }

        @Override
        public Object getItem(int position) {
            return pkgStats.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //AppView contains pkgName, lastTimeUsed, totalTimeUsage
            AppView holder;

            //Only use inflate when convertView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.usage_stats_item, null);

                //Creates a new AppView
                holder = new AppView();
                holder.pkgName = (TextView) convertView.findViewById(R.id.pkg_name);
                holder.lastTimeUsed = (TextView) convertView.findViewById(R.id.last_time_used);
                holder.totalUsageTime = (TextView) convertView.findViewById(R.id.usage_time);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (AppView) convertView.getTag();
            }

            //Bind the data efficiently with the holder
            UsageStats pkgStats = this.pkgStats.get(position);
            if (pkgStats != null) {
                String packageName = pkgStats.getPackageName();
                String label = labelArrayMap.get(packageName);
                holder.pkgName.setText(label);
                holder.lastTimeUsed.setText(DateUtils.formatSameDayTime(pkgStats.getLastTimeUsed(),
                        System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM));
                holder.totalUsageTime.setText(
                        DateUtils.formatElapsedTime(pkgStats.getTotalTimeInForeground() / 1000));
                holder.icon.setImageDrawable(iconImages.get(packageName));

            } else {
                Log.w(TAG, "No usage stats info for package:" + position);
            }
            return convertView;
        }

        //Function to sort the list of applications based on what the user selects (usage time, last used, etc.)
        private void sortList() {
            if (mDisplayOrder == DISPLAY_USAGE_TIME) {
                if (localLOGV) Log.i(TAG, "Sorting by usage time");
                Collections.sort(pkgStats, mSortTotalTimeUsage);
            } else if (mDisplayOrder == DISPLAY_LAST_TIME_USED) {
                if (localLOGV) Log.i(TAG, "Sorting by last time used");
                Collections.sort(pkgStats, mSortLastTimeAppUsed);
            } else if (mDisplayOrder == DISPLAY_APP_NAME) {
                if (localLOGV) Log.i(TAG, "Sorting by application name");
                Collections.sort(pkgStats, appNameComparison);
            }
            notifyDataSetChanged();
        }

        //If they try to sort by what it is already sorted by, then do nothing.
        void sortList(int sortOrder) {
            if (mDisplayOrder == sortOrder) {
                // do nothing
                return;
            }
            mDisplayOrder= sortOrder;
            sortList();
        }
    }

    public static class AppNameComparator implements Comparator<UsageStats> {
        private Map<String, String> mAppLabelList;

        AppNameComparator(Map<String, String> appList) {
            mAppLabelList = appList;
        }

        @Override
        public final int compare(UsageStats a, UsageStats b) {
            String alabel = mAppLabelList.get(a.getPackageName());
            String blabel = mAppLabelList.get(b.getPackageName());
            return alabel.compareTo(blabel);
        }
    }

    //Class for sorting apps into order of Last time they were used on the phone.
    public static class SortLastTimeAppUsed implements Comparator<UsageStats> {

        //Method for comparing app names in order to sort them into the correct order.
        @Override
        public final int compare(UsageStats a, UsageStats b) {
            //Return by descending order
            return (int)(b.getLastTimeUsed() - a.getLastTimeUsed());
        }
    }

    //Class for sorting apps into order of time spent on them.
    public static class SortTotalTimeUsage implements Comparator<UsageStats> {

        //Method for comparing apps in terms of when they were last used.
        @Override
        public final int compare(UsageStats a, UsageStats b) {
            //This compares app foreground times.
            return (int)(b.getTotalTimeInForeground() - a.getTotalTimeInForeground());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mUsageStatsAdapter.sortList(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Nothing
    }
}
