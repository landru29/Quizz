package fr.noopy.landru.quizz.tools;

import android.graphics.Point;

/**
 * Created by cyrille on 15/02/15.
 */
public class Chart {
    public static String getPie(int ok, int ko, Point size) {
        int sizing = (size.x>size.y ? size.y : size.x);
        sizing = (sizing>500 ? 500 : sizing);
        return "http://chart.apis.google.com//chart?cht=p3&chd=t:" + ko + "," + ok + "&chco=FF0000,00FF00&chdl=KO|OK&chds=a&chxt=y&chxl=&chs=" + sizing + "x" + sizing + "&chf=bg,s,00000000";
    }
}
