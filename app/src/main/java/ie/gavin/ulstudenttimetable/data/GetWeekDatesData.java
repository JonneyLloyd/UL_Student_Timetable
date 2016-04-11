package ie.gavin.ulstudenttimetable.data;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Oliver on 29/02/2016.
 */
public class GetWeekDatesData extends GetTimetableData {

    private String LOG_TAG = GetWeekDatesData.class.getSimpleName();

    public GetWeekDatesData() {
        String webUrl = "http://www.timetable.ul.ie/weeks.htm";
        super.setWebUrl(webUrl);
    }

    @Override
    public void processResult(String html) {

        ArrayList<ArrayList<String>> data = new ArrayList<>();

        // 25 Jan 2016|1|1|
        // 01 Feb 2016|2|2|

        Document doc = Jsoup.parse(html);
        Elements els = doc.select("tbody tr:gt(0)");
        for (Element el : els) {
            String [] row = {
                    el.select("td:eq(0)").html().trim(),
                    el.select("td:eq(1)").html().trim(),
                    el.select("td:eq(2)").html().trim()
            };
            data.add(new ArrayList<String>(Arrays.asList(row)));

        }

        if (data.size() > 0) {
            super.setTimetableData(data);
            super.setRecordsFound(true);

            for (ArrayList<String> row : data) {
                String r = "";
                for (String strrow : row) {
                    r += strrow + "|";
                }
                Log.v(LOG_TAG, r);
            }
        } else {
            Log.v(LOG_TAG, "No Records");
        }

    }

}
