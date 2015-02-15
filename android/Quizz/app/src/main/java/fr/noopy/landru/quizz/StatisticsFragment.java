package fr.noopy.landru.quizz;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    }

    private Point getWindowsSize() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private void loadGlobalStat() {
        HashMap<String, Integer> data = StatisticTable.getGlobalStat(db);
        Point windowSize = getWindowsSize();
        String pieUrl = Chart.getPie(data.get("ok"), data.get("ko"), windowSize);
        ImageView pie = (ImageView)getView().findViewById(R.id.pieChart);
        new DownloadImageTask(pie).execute(pieUrl);
    }
}
