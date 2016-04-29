package ie.gavin.ulstudenttimetable.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import ie.gavin.ulstudenttimetable.R;

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
    private ArrayList<ArrayList<String>> weekDates = new ArrayList<>();

    MyDBHandler dbHandler;

    Module tempModule;



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
        dbHandler = MyDBHandler.getInstance(this.context);
        if (dbHandler.userExists(Integer.parseInt(studentId))){
            broadcastStatus("Student ID already exists");
            // retry
            return;
        }


        // TODO check if already exists


        //        Student Timetable
        studentTimetableData = new GetStudentTimetableData(studentId) {
            @Override
            public void processResult(String html) {
                super.processResult(html);

                if (!checkTimetableDataResult(studentTimetableData)) {
                    return;
                }

                studentTimetables.put(studentId, super.getTimetableData());

                // Get all the module info for a students timetable
                ArrayList<String> modules = new ArrayList<>();  // keep track of modules already fetched
                for (ArrayList<String> row : studentTimetables.get(studentId)) {
                    final String moduleCode = row.get(3);
                    if (!modules.contains(moduleCode)) {   // Avoid repetition
                        modules.add(moduleCode);

                        //        Module Timetable
                        moduleTimetableData = new GetModuleTimetableData(moduleCode) {
                            @Override
                            public void processResult(String html) {
                                super.processResult(html);
                                //Log.v(LOG_TAG, "mt" + moduleCode);
                                if (!checkTimetableDataResult(this)) {
                                    return;
                                }
                                moduleTimetables.put(moduleCode, super.getTimetableData());
                            }
                        };
                        tasks.add(moduleTimetableData);
                        moduleTimetableData.execute();


                        //        Module Details
                        moduleDetailsData = new GetModuleDetailsData(moduleCode) {
                            @Override
                            public void processResult(String html) {
                                super.processResult(html);
                                //Log.v(LOG_TAG, "md" + moduleCode);
                                if (!checkTimetableDataResult(this)) {
                                    return;
                                }
                                moduleDetails.put(moduleCode, super.getTimetableData());
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
                        weekDates = super.getTimetableData();
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

        int[] colors = context.getResources().getIntArray(R.array.pickerColors);
        HashMap<String, Integer> moduleColors = new HashMap<String, Integer>();


        Module module;
//        dbHandler = new MyDBHandler(this.context, null, null, 3);
        String startTime, endTime, room, lecturer, group , type, weeks, moduleName, prevModule = "";
        int day;



        if (weekDatesData.getDownloadStatus() == DownloadStatus.OK &&
            studentTimetableData.getDownloadStatus() == DownloadStatus.OK &&
            moduleTimetableData.getDownloadStatus() == DownloadStatus.OK &&
            moduleDetailsData.getDownloadStatus() == DownloadStatus.OK) {
            // TODO

            broadcastStatus("Preparing your timetable");

            // SQL here, call function

            int week = 0;
            Date weekStart = null;
            for (ArrayList<String> weekDate : weekDates) {
                week = Integer.parseInt(weekDate.get(2));
                String weekLabel = weekDate.get(1);
                if (week == 14) weekLabel = "Study Week";

                SimpleDateFormat inFormat = new SimpleDateFormat("dd MMM yyyy");
                try {
                    weekStart = inFormat.parse(weekDate.get(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }

                dbHandler.addToWeekDetails(week, weekLabel, weekStart);
            }
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(weekStart);
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            dbHandler.addToWeekDetails(++week, "Exams", calendar.getTime());

            //moduleDetails
            for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : moduleDetails.entrySet()) {
                String moduleCode = entry.getKey();

                // The data for ONE module
                ArrayList<ArrayList<String>> moduleDetails = entry.getValue();

                // go through all lecture/lab/tut for a module
                for (ArrayList<String> moduleEvent : moduleDetails) {

                    // Output everything for one lecture/lab/tut
                    String r = moduleCode + " -> "; // module code is not in the data, added so we know what is what
                    for (String strrow : moduleEvent) {
                        r += strrow + "|";

                    }
                    //Log.v(LOG_TAG, "Module Name: " + r);
                    moduleName =  moduleEvent.get(0);
                    dbHandler.addToModuleNamesTable(moduleCode, moduleName );

                    //below tests DB names
                    String test;
                   // test = dbHandler.getModuleName(moduleCode);
                    //Log.v(LOG_TAG, "Database TEST: " + test);
                }

            }



            //moduleTimetable
            // iterate through the hashmap (list of ALL module data) getting modulecode and data

            for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : moduleTimetables.entrySet()) {
                String moduleCode = entry.getKey();

                moduleColors.put(moduleCode, colors[(moduleColors.size())]);

                // The data for ONE module
                ArrayList<ArrayList<String>> moduleTimetable = entry.getValue();
                ArrayList<Module> newModule = new ArrayList<>();

                // go through all lecture/lab/tut for a module
                for (ArrayList<String> moduleEvent : moduleTimetable) {

                    // Output everything for one lecture/lab/tut
                    String r = moduleCode + " -> "; // module code is not in the data, added so we know what is what
                    for (String strrow : moduleEvent) {
                        r += strrow + "|";

                    }
                    //Log.v(LOG_TAG, "event: " + r);
                    day = Integer.parseInt(moduleEvent.get(0));
                    startTime =  moduleEvent.get(1);
                    endTime =  moduleEvent.get(2);
                    type =  moduleEvent.get(3);
                    group =  moduleEvent.get(4);
                    lecturer =  moduleEvent.get(5);
                    room =  moduleEvent.get(6);
                    weeks =  moduleEvent.get(7);
                    tempModule = new Module(0, moduleCode, startTime , endTime, room, lecturer, day, group, type);
                    weeks = weeks.substring(weeks.indexOf(":") + 1);
                    ArrayList<Pair<Integer,Integer>> weeksList = new ArrayList<>();
                    if (weeks.contains(",")){
                        String[] splitArray = weeks.split(",");
                        for(int i = 0; i <splitArray.length; i++) {
                            String[] secondSplitArray;
                            if (splitArray[i].contains("-")) {
                                secondSplitArray = splitArray[i].split("-");
                            } else {
                                secondSplitArray = new String[] {splitArray[i],splitArray[i]};
                            }
                            Pair weeksToAdd;
                            weeksToAdd = new Pair<Integer,Integer>(Integer.parseInt(secondSplitArray[0]),Integer.parseInt(secondSplitArray[1]));
                            weeksList.add(weeksToAdd);
                        }

                    }
//
                    else {
                        String[] splitArray = weeks.split("-");
                        Pair weeksToAdd;
                        weeksToAdd = new Pair<Integer,Integer>(Integer.parseInt(splitArray[0]),Integer.parseInt(splitArray[1]));
                        weeksList.add(weeksToAdd);
//                        String[] splitArray = weeks.split("-");
//                        temp.add(splitArray[0] + "-" + splitArray[1]);
                    }
                    tempModule.set_weeks(weeksList);
                    //Log.v(LOG_TAG, "ModulesWeekTest: " + tempModule.get_weeks().get(0).first+ "-" + tempModule.get_weeks().get(0).second);
                    newModule.add(tempModule);
                    //dbHandler.addToModuleTable(tempModule);

                    //Log.v(LOG_TAG, "ADDED MODULE: " + dbHandler.getModuleName(moduleCode));
                }

                //testing module table
                ArrayList<Module> oldModules = new ArrayList<>();
                oldModules = dbHandler.getAllFromModuleTable(moduleCode);
                dbHandler.compareModuleLists(oldModules, newModule);
                ArrayList<Module> test = new ArrayList<>();
                //testing method to add/remove modules on student table

                test = dbHandler.getAllFromModuleTable(moduleCode);

                for(int i = 0; i < test.size(); i++){
                    Log.v(LOG_TAG, "Modules: " + (test.get(i)).get_idTablePointer() + " - " + (test.get(i)).get_ModuleCode() + " - " + test.get(i).get_startTime()  + " - " + test.get(i).get_endTime()+ " - " + test.get(i).get_day() + " - " + test.get(i).get_type() + " - " + test.get(i).get_groupName() + " - " + test.get(i).get_weeks().get(0).first+ "-" + test.get(i).get_weeks().get(0).second+ " - " + test.get(i).get_lecturer());
                    //dbHandler.updateModuleTable(test.get(i));
                    //Log.v(LOG_TAG, "UpdatedMod: " + (test.get(i)).get_idTablePointer() + " - " + (test.get(i)).get_ModuleCode() + " - " + test.get(i).get_startTime() + " - " + test.get(i).get_endTime() + " - " + test.get(i).get_day() + " - " + test.get(i).get_type() + " - " + test.get(i).get_groupName() + " - " + test.get(i).get_weeks());
                    if (test.get(i).get_weeks().size() > 1){
                        Log.v(LOG_TAG, "Modules: " + "second Week: " + (test.get(i).get_weeks().get(1).first)+ "-" + (test.get(i).get_weeks().get(1).second));
                    }
                }



                //dbHandler.addOrRemoveModuleFromTimetable(test, 11111111, 1);

                //Code snippet testing updateModuleTable()
//                tempModule = new Module(0, "CS4416", "10:00" , "11:00", "HSG037", "NIKOLOV NIKOLA DR", 2, "", "LEC");
//                boolean ok = dbHandler.updateModuleTable(tempModule);
//                Log.v(LOG_TAG, "UpdatedSuccess: " + ok);

            }


            //studentTimetables
            // iterate through the hashmap (list of ALL module data) getting modulecode and data

            for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : studentTimetables.entrySet()) {
                String studentID = entry.getKey();
                String moduleCode, notes;
                int color;
                int UID = 0;
                StudentTimetable tempStudent;


                // The data for ONE module
                ArrayList<ArrayList<String>> studentTimetables = entry.getValue();

                // go through all lecture/lab/tut for a module
                for (ArrayList<String> moduleEvent : studentTimetables) {

                    // Output everything for one lecture/lab/tut
                    String r = studentID + " -> "; // module code is not in the data, added so we know what is what
                    for (String strrow : moduleEvent) {
                        r += strrow + "|";

                    }
                    //Log.v(LOG_TAG, "event: " + r);
                    day = Integer.parseInt(moduleEvent.get(0));
                    startTime =  moduleEvent.get(1);
                    endTime =  moduleEvent.get(2);
                    moduleCode = moduleEvent.get(3);
                    type =  moduleEvent.get(4);
                    group =  moduleEvent.get(5);
                    room =  moduleEvent.get(6);
                    weeks =  moduleEvent.get(7);
                    color = moduleColors.get(moduleCode);
                    lecturer = null;
                    notes = null;
                    //Log.v(LOG_TAG, "day: " + day);
                    //Log.v(LOG_TAG, "startTime: " + startTime);
                    //Log.v(LOG_TAG, "endTime: " + endTime);
//                    Log.v(LOG_TAG, "moduleCode: " + moduleCode);
//                    Log.v(LOG_TAG, "type: " + type);
//                    Log.v(LOG_TAG, "group: " + group);
//                    Log.v(LOG_TAG, "room: " + room);
//                    Log.v(LOG_TAG, "weeks: " + weeks);
                    UID = dbHandler.getUIDForModuleEntry(moduleCode, day, startTime, endTime);
                   //tempStudent = new StudentTimetable(0, moduleCode, UID, startTime, day, endTime, Integer.parseInt(studentID),  notes, group, type, null, lecturer,  room, color);
                    tempStudent = new StudentTimetable(0, null, UID, null, 0, null, Integer.parseInt(studentID),  null, null, null, null, null,  null, color);
                    dbHandler.addToStudentTimetable(tempStudent);

                }

                //testing Student database contents
                ArrayList<StudentTimetable> test = new ArrayList<>();
                test = dbHandler.getAllFromStudentTimetable(14117495, 3);
                for(int i = 0; i < test.size(); i++){
                    Log.v(LOG_TAG, "LOOP_STUDENTS: " + (test.get(i)).get_moduleCode() + " - " + (test.get(i).get_idTablePointer()) + " - " + (test.get(i).get_start_time())+ " - " + (test.get(i).get_endTime())+ " - " + (test.get(i).get_day())+ " - " + (test.get(i).get_modulePointer())+ " - " + (test.get(i).get_weeks().get(0).first)+ "-" + (test.get(i).get_weeks().get(0).second)+ " - " + (test.get(i).get_title()));
                   if (test.get(i).get_weeks().size() > 1){
                       Log.v(LOG_TAG, "LOOP_STUDENTS: " + "second Week: " + (test.get(i).get_weeks().get(1).first)+ "-" + (test.get(i).get_weeks().get(1).second));
                   }
                    //testing multiple weeks
//                    StudentTimetable tempSTU = dbHandler.getStudentTimetableFromID(test.get(i).get_idTablePointer());
//                    Log.v(LOG_TAG, "tempSTU: " + tempSTU.get_moduleCode() + " - " + (tempSTU.get_idTablePointer()) + " - " + (tempSTU).get_start_time()+ " - " + (tempSTU.get_endTime())+ " - " + (tempSTU.get_day())+ " - " + (tempSTU.get_modulePointer())+ " - " + (tempSTU.get_weeks().get(0).first)+ "-" + (tempSTU.get_weeks().get(0).second)+ " - " + (tempSTU.get_title()));
//                    if (tempSTU.get_weeks().size() > 1){
//                        Log.v(LOG_TAG, "tempSTU: " + "second Week: " + (tempSTU.get_weeks().get(1).first)+ "-" + (tempSTU.get_weeks().get(1).second));
//                    }
//                      if(test.get(i).get_moduleCode().equals("CS4014")){
//                        dbHandler.insertNoteOnTimetableEntry(test.get(i).get_idTablePointer(), "Note For CS4014");
//                        Log.v(LOG_TAG, "NOTE ADDED TO STUD" + dbHandler.getStudentTimetableFromID(test.get(i).get_idTablePointer()).get_notes());
//                    }

                }


            }





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
