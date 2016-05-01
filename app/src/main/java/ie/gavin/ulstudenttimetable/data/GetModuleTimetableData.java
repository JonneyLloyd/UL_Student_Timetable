package ie.gavin.ulstudenttimetable.data;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

public class GetModuleTimetableData extends GetTimetableData {

    private ArrayList<ArrayList<String>> data = new ArrayList<>();

    private String LOG_TAG = GetModuleTimetableData.class.getSimpleName();

    public GetModuleTimetableData(String moduleCode) {
        final String BASE_URL = "http://www.timetable.ul.ie/mod_res.asp?";
        String webUrl = String.format(BASE_URL + "T1=%s", moduleCode);
        super.setWebUrl(webUrl);
    }

    @Override
    public void processResult(String html) {

        // 16:00|17:00|LEC|&nbsp;|RYAN CONOR PROFESSOR|S205|Wks:1-5|
        // 14:00|15:00|LAB|2A|RYAN CONOR PROFESSOR|CS2044|Wks:1-5|

        int dayOfWeek = 1;
        Document doc = Jsoup.parse(html);
        Elements cols = doc.select("tbody tr:gt(0) td");
        for (Element col : cols) {
            Elements rows = col.select("p font b");
            for (Element el : rows) {
                String [] row = el.html().trim().split("( )*<font>( )*-( )*</font>( )*|( )*<br>( )*");
                ArrayList<String> event = new ArrayList<String>(Arrays.asList(row));
                event.add(0, ""+dayOfWeek); // for identification
                event.set(4, event.get(4).replace("&nbsp;", ""));
                data.add(event);

            }
            dayOfWeek++;
        }
        super.setTimetableData(data);

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
