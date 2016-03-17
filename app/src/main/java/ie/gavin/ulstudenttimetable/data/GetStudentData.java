package ie.gavin.ulstudenttimetable.data;

import ie.gavin.ulstudenttimetable.StudentTimetableException;

/**
 * Created by Oliver on 05/03/2016.
 */
public class GetStudentData {
    private String LOG_TAG = GetStudentData.class.getSimpleName();

    String studentId;

    public GetStudentData(String studentId) {
        this.studentId = studentId;
    }

    public void execute() throws StudentTimetableException {
        //get all data needed and store
        //throw an exception if the ID was invalid
        if (studentId.length() < 7 || studentId.length() > 8) {
            throw new StudentTimetableException("Invalid student ID");
        }

        //        Week Dates
        GetWeekDatesData weekDatesData = new GetWeekDatesData();
        weekDatesData.execute();

        //        Student Timetable
        GetStudentTimetableData studentTimetableData = new GetStudentTimetableData("14161044");
        studentTimetableData.execute();

        //        Module Timetable
        GetModuleTimetableData moduleTimetableData = new GetModuleTimetableData("cs4014");
        moduleTimetableData.execute();

        //        Module Details
        GetModuleDetailsData moduleDetailsData = new GetModuleDetailsData("cs4014");
        moduleDetailsData.execute();

        //        Exam Timetable?

    }
}
