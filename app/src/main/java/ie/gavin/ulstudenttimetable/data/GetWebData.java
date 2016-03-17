package ie.gavin.ulstudenttimetable.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Oliver on 05/03/2016.
 */

enum DownloadStatus { IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK }

                                        // params, progress, result
public class GetWebData extends AsyncTask<String, Void, String> {
    private String LOG_TAG = GetWebData.class.getSimpleName();
    private String webUrl;
    private String webData;
    private DownloadStatus downloadStatus;

    public GetWebData() {
        this.webUrl = null;
        this.downloadStatus = DownloadStatus.IDLE;
    }

    public String getWebData() {
        return webData;
    }

    public DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    @Override
    protected void onPostExecute(String data) {
        webData = data;
        if (webData == null) {
            if (webUrl == null) {
                downloadStatus = DownloadStatus.NOT_INITIALISED;
            } else {
                downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
            }
        } else {
            // Success
            downloadStatus = DownloadStatus.OK;
        }
    }

    protected String doInBackground(String... params) {
        downloadStatus = DownloadStatus.PROCESSING;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        if (params == null)
            return null;

        webUrl = params[0];

        try {
            URL url = new URL(webUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream == null) {
                return null;
            }

            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ( (line = reader.readLine()) != null ) {
                buffer.append(line);
                Log.v(LOG_TAG, line);
            }

            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return null;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

    }

}
