package ie.gavin.ulstudenttimetable;

import android.util.Log;

/**
 * Created by Oliver on 29/02/2016.
 */
public abstract class GetTimetableData extends GetWebData {

    private String LOG_TAG = GetTimetableData.class.getSimpleName();
    private String webUrl;

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void execute() {
        super.execute(webUrl);
    }

    public abstract void processResult(String html);

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
