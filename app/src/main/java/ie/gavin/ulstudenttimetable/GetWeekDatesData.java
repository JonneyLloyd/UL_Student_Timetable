package ie.gavin.ulstudenttimetable;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    public void execute() {
        super.execute();
    }

    @Override
    public void processResult(String html) {

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
            String r = "";
            for (String rel:row) {
                r += rel + "|";
            }
            Log.v(LOG_TAG, r);
        }

    }

}
