package ie.gavin.ulstudenttimetable.data;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    public void execute() {
        super.execute();
    }

    @Override
    public void processResult(String html) {

//        15:00 <font> - </font> 16:00 <br> CS4004 <font> - </font>LEC <font>- </font> &nbsp; <br> CSG001 <br> Wks:1-8,10-13
//        10:00 <font> - </font> 11:00 <br> EE4013 <font> - </font>TUT <font>- </font> 3B <br> A2011 <br> Wks:1-8,10-13

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
