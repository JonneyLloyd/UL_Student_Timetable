package ie.gavin.ulstudenttimetable.data;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Oliver on 29/02/2016.
 */
public abstract class GetTimetableData extends GetWebData {

    private ArrayList<ArrayList<String>> timetableData = new ArrayList<>();
    private boolean RECORDS_FOUND = false;

    private String LOG_TAG = GetTimetableData.class.getSimpleName();
    private String webUrl;

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void execute() {
        super.execute(webUrl);
    }

    public abstract void processResult(String html);

    public void setTimetableData(ArrayList<ArrayList<String>> timetableData) {
        this.timetableData = timetableData;
    }

    public ArrayList<ArrayList<String>> getTimetableData() {
        return timetableData;
    }

    public void setRecordsFound(boolean found) {
        this.RECORDS_FOUND = found;
    }

    public boolean getRecordsFound() {
        return RECORDS_FOUND;
    }

    @Override
    protected void onPostExecute(String webData) {
        super.onPostExecute(webData);

        if (super.getDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw file");
            return;
        }
        String html = super.getWebData();
        processResult(html);
    }

}
