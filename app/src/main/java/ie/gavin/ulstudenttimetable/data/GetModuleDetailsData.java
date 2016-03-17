package ie.gavin.ulstudenttimetable.data;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Oliver on 29/02/2016.
 */
public class GetModuleDetailsData extends GetTimetableData {

    private String LOG_TAG = GetModuleDetailsData.class.getSimpleName();

    public GetModuleDetailsData(String moduleCode) {
        final String BASE_URL = "http://www.timetable.ul.ie/tt_moduledetails_res.asp?";
        String webUrl = String.format(BASE_URL + "T1=%s", moduleCode);
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
        Element el = doc.select("table table tbody tr:eq(1) td:eq(1) b font").first();
        String r = el.html().trim();
        Log.v(LOG_TAG, r);

    }

}
