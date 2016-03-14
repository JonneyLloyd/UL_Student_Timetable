package ie.gavin.ulstudenttimetable;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Oliver on 29/02/2016.
 */
public class GetModuleTimetableData extends GetTimetableData {

    private String LOG_TAG = GetModuleTimetableData.class.getSimpleName();

    public GetModuleTimetableData(String moduleCode) {
        final String BASE_URL = "http://www.timetable.ul.ie/mod_res.asp?";
        String webUrl = String.format(BASE_URL + "T1=%s", moduleCode);
        super.setWebUrl(webUrl);
    }

    @Override
    public void execute() {
        super.execute();
    }

    @Override
    public void processResult(String html) {

        // 16:00|17:00|LEC|&nbsp;|RYAN CONOR PROFESSOR|S205|Wks:1-5|
        // 14:00|15:00|LAB|2A|RYAN CONOR PROFESSOR|CS2044|Wks:1-5|

        Document doc = Jsoup.parse(html);
        Elements els = doc.select("tbody tr:gt(0) td p font b");
        for (Element el : els) {
            String [] row = el.html().trim().split("( )*<font>( )*-( )*</font>( )*|( )*<br>( )*");
            String r = "";
            for (String rel:row) {
                r += rel + "|";
            }
            Log.v(LOG_TAG, r);
        }

    }

}
