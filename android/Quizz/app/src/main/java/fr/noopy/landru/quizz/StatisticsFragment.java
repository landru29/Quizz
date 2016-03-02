package fr.noopy.landru.quizz;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;

import fr.noopy.landru.quizz.database.Database;
import fr.noopy.landru.quizz.database.StatisticTable;
import fr.noopy.landru.quizz.tools.Chart;
import fr.noopy.landru.quizz.tools.DownloadImageTask;


public class StatisticsFragment extends Fragment {

    private Database db;

    public StatisticsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity parent = (MainActivity)getActivity();
        db = parent.db;
        loadGlobalStat();
        loadWeekStat();
    }

    private Point getWindowsSize() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private void loadGlobalStat() {
        PieChart pieChart = (PieChart) getView().findViewById(R.id.globalPieChart);
        HashMap<String, Integer> dbData = StatisticTable.getGlobalStat(db);

        //pieChart.setTransparentCircleAlpha(110);
        pieChart.setRotationAngle(0);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        String[] labels = new String[] {"OK", "KO"};
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        yValues.add(new Entry(dbData.get("ok"), 0));
        yValues.add(new Entry(dbData.get("ko"), 1));

        PieDataSet set = new PieDataSet(yValues, getString(R.string.pie_chart_legend));

        set.setColors(new int[]{Color.rgb(0, 255, 0), Color.rgb(255, 0, 0)});

        PieData data = new PieData(labels, set);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void loadWeekStat() {
        BarChart barChart = (BarChart) getView().findViewById(R.id.weekBarChart);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.setDescription("");
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        ArrayList<BarEntry> yValues = StatisticTable.getEvolutionStat(db, 5);

        BarDataSet set = new BarDataSet(yValues, getString(R.string.bar_chart_legend));
        set.setColors(new int[]{Color.rgb(0, 255, 0), Color.rgb(255, 0, 0)});
        set.setStackLabels(new String[]{"ok", "ko"});

        ArrayList<String> labels = new ArrayList<String>();
        for(int i=0; i<yValues.size(); i++) {
            labels.add("" + i);
        }

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set);

        BarData data = new BarData(labels, dataSets);

        barChart.setData(data);
        barChart.invalidate();
    }
}
