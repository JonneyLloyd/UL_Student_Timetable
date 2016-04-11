package ie.gavin.ulstudenttimetable.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Oliver on 05/03/2016.
 */
public class GetStudentData {
    private String LOG_TAG = GetStudentData.class.getSimpleName();

    private String studentId;
    private Context context;

    private ArrayList<AsyncTask> tasks = new ArrayList<>();

    private GetStudentTimetableData studentTimetableData;
    private GetModuleTimetableData moduleTimetableData;
    private GetModuleDetailsData moduleDetailsData;
    private GetWeekDatesData weekDatesData;

    private HashMap<String, ArrayList<ArrayList<String>>> studentTimetables = new HashMap<>();
    private HashMap<String, ArrayList<ArrayList<String>>> moduleTimetables = new HashMap<>();
    private HashMap<String, ArrayList<ArrayList<String>>> moduleDetails = new HashMap<>();

    public GetStudentData(String studentId, Context context) {
        this.studentId = studentId;
        this.context = context;
    }

    public void execute() {
        //get all data needed and store

//        if (true) {
//            // Check for an internet connection
//            broadcastStatus("Connection error");
//            // retry
//            return;
//        }

        //throw an exception if the ID was invalid
        if (studentId.length() < 7 || studentId.length() > 8) {
            broadcastStatus("Invalid student ID");
            // retry
            return;
        }

        //        Student Timetable
        studentTimetableData = new GetStudentTimetableData(studentId) {
            @Override
            public void processResult(String html) {
                super.processResult(html);

                if (!checkTimetableDataResult(studentTimetableData)) {
                    return;
                }

                studentTimetables.put(studentId, studentTimetableData.getTimetableData());
                // Get all the module info for a students timetable
                for (ArrayList<String> row : studentTimetables.get(studentId)) {
                    final String moduleCode = row.get(2);
                    if (!studentTimetables.containsKey(moduleCode)) {   // Avoid repetition

                        //        Module Timetable
                        moduleTimetableData = new GetModuleTimetableData(moduleCode) {
                            @Override
                            public void processResult(String html) {
                                super.processResult(html);
                                Log.v(LOG_TAG, "mt" + moduleCode);
                                if (!checkTimetableDataResult(this)) {
                                    return;
                                }
                                moduleTimetables.put(moduleCode, moduleDetailsData.getTimetableData());
                            }
                        };
                        tasks.add(moduleTimetableData);
                        moduleTimetableData.execute();


                        //        Module Details
                        moduleDetailsData = new GetModuleDetailsData(moduleCode) {
                            @Override
                            public void processResult(String html) {
                                super.processResult(html);
                                Log.v(LOG_TAG, "md" + moduleCode);
                                if (!checkTimetableDataResult(this)) {
                                    return;
                                }
                                moduleDetails.put(moduleCode, moduleDetailsData.getTimetableData());
                            }
                        };
                        tasks.add(moduleDetailsData);
                        moduleDetailsData.execute();

                    }

                }

                //        Week Dates
                weekDatesData = new GetWeekDatesData() {
                    @Override
                    protected void onPostExecute(String webData) {
                        super.onPostExecute(webData);
                        if (!checkTimetableDataResult(weekDatesData)) {
                            return;
                        }
                        processData();
                    }
                };
                tasks.add(weekDatesData);
                weekDatesData.execute();

            }
        };
        studentTimetableData.execute();

        //        Exam Timetable?



    }

    public void processData() {
        if (weekDatesData.getDownloadStatus() == DownloadStatus.OK &&
            studentTimetableData.getDownloadStatus() == DownloadStatus.OK &&
            moduleTimetableData.getDownloadStatus() == DownloadStatus.OK &&
            moduleDetailsData.getDownloadStatus() == DownloadStatus.OK) {
            // TODO

            broadcastStatus("Preparing your timetable");

            // SQL here, call function

            broadcastStatus("Success");
        } else {
            // error
            return;
        }
    }

    // All data must be downloaded successfully, otherwise abort
    public boolean checkTimetableDataResult(GetTimetableData timetableData) {
        if (timetableData.getDownloadStatus() != DownloadStatus.OK) {
            broadcastStatus("Error fetching timetable");
            cancelAllTasks();
            return false;
        }
        if (!timetableData.getRecordsFound()) {
            broadcastStatus("No records found");
            cancelAllTasks();
            return false;
        }
        return true;
    }

    public void cancelAllTasks() {
        for (AsyncTask task : tasks) {
            task.cancel(true);
        }
    }

    public void broadcastStatus(String message) {
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
        localIntent.putExtra(Constants.EXTENDED_DATA_STATUS, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }

}
