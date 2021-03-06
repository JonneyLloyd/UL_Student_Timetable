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
public class GetStudentTimetableData extends GetTimetableData {

    private String LOG_TAG = GetStudentTimetableData.class.getSimpleName();

    public GetStudentTimetableData(String studentId) {
        final String BASE_URL = "http://www.timetable.ul.ie/tt2.asp?";
        String webUrl = String.format(BASE_URL + "T1=%s", studentId);
        super.setWebUrl(webUrl);
    }

    @Override
    public void processResult(String html) {

        ArrayList<ArrayList<String>> data = new ArrayList<>();

//        15:00 <font> - </font> 16:00 <br> CS4004 <font> - </font>LEC <font>- </font> &nbsp; <br> CSG001 <br> Wks:1-8,10-13
//        10:00 <font> - </font> 11:00 <br> EE4013 <font> - </font>TUT <font>- </font> 3B <br> A2011 <br> Wks:1-8,10-13


        int dayOfWeek = 1;
        Document doc = Jsoup.parse(html);
        Elements cols = doc.select("tbody tr:gt(0) td");
        for (Element col : cols) {
            Elements rows = col.select("p font b");
            for (Element el : rows) {
                String [] row = el.html().trim().split("( )*<font>( )*-( )*</font>( )*|( )*<br>( )*");
                ArrayList<String> event = new ArrayList<String>(Arrays.asList(row));
                event.add(0, ""+dayOfWeek); // for identification
                event.set(5, event.get(5).replace("&nbsp;", ""));
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
