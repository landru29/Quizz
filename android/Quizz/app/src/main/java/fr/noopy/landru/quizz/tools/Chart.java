package fr.noopy.landru.quizz.tools;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by cyrille on 15/02/15.
 */
public class Chart {
    public static String getPie(int ok, int ko, Point size) {
        int sizing = (size.x>size.y ? size.y : size.x);
        sizing = (sizing>500 ? 500 : sizing);
        return "http://chart.apis.google.com//chart?cht=p3&chd=t:" + ko + "," + ok + "&chco=FF0000,00FF00&chdl=KO|OK&chds=a&chxt=y&chxl=&chs=" + sizing + "x" + sizing + "&chf=bg,s,00000000";
    }

    public static String getStacked(ArrayList<Double> ok, Point size) {
        int sizing = (size.x>size.y ? size.y : size.x);
        sizing = (sizing>500 ? 500 : sizing);
        String kos = "";
        String oks = "";
        for (Double percent:ok) {
            kos += "," + (percent>0 ? (int)Math.round((1-percent)*100) : 0);
            oks += "," + (percent>0 ? (int)Math.round(percent*100) : 0);
        }
        oks = (oks.length()>0 ? oks.substring(1) : oks);
        kos = (kos.length()>0 ? kos.substring(1) : kos);
        Log.i("STATS", "http://chart.apis.google.com/chart?cht=bvs&chs=" + sizing + "x" + sizing + "&chco=00ff00,ff0000&chdl=OK%7CKO&chbh=a&chtt=&chts=000000,24&chd=t:" + oks + "|" + kos + "&chf=bg,s,00000000");
        return "http://chart.apis.google.com/chart?cht=bvs&chs=" + sizing + "x" + sizing + "&chco=00ff00,ff0000&chdl=OK%7CKO&chbh=a&chtt=&chts=000000,24&chd=t:" + oks + "|" + kos + "&chf=bg,s,00000000";
    }
}
