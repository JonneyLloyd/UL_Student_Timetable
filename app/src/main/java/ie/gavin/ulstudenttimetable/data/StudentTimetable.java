package ie.gavin.ulstudenttimetable.data;

import java.util.Date;

public class StudentTimetable {
    private String _moduleID;
    private String _start_time;
    private Date _day;
    private String _endTime;
    private int _studentID;
    private String _timetableName;
    private String _notes;
    private Date _lastUpdate;
    private String _groupName;
    private String _attrib;


    public StudentTimetable() {

    }
    public StudentTimetable(String _moduleID, String _start_time, Date _day, String _endTime, int _studentID, String _timetableName, String _notes, Date _lastUpdate, String _groupName, String _attrib) {
        this._moduleID = _moduleID;
        this._start_time = _start_time;
        this._day = _day;
        this._endTime = _endTime;
        this._studentID = _studentID;
        this._timetableName = _timetableName;
        this._notes = _notes;
        this._lastUpdate = _lastUpdate;
        this._groupName = _groupName;
        this._attrib = _attrib;
    }

    public String get_moduleID() {
        return _moduleID;
    }

    public String get_start_time() {
        return _start_time;
    }

    public Date get_day() {
        return _day;
    }

    public String get_endTime() {
        return _endTime;
    }

    public int get_studentID() {
        return _studentID;
    }

    public String get_timetableName() {
        return _timetableName;
    }

    public String get_notes() {
        return _notes;
    }

    public Date get_lastUpdate() {
        return _lastUpdate;
    }

    public String get_groupName() {
        return _groupName;
    }

    public String get_attrib() {
        return _attrib;
    }

    public void set_moduleID(String _moduleID) {
        this._moduleID = _moduleID;
    }

    public void set_start_time(String _start_time) {
        this._start_time = _start_time;
    }

    public void set_day(Date _day) {
        this._day = _day;
    }

    public void set_endTime(String _endTime) {
        this._endTime = _endTime;
    }

    public void set_studentID(int _studentID) {
        this._studentID = _studentID;
    }

    public void set_timetableName(String _timetableName) {
        this._timetableName = _timetableName;
    }

    public void set_notes(String _notes) {
        this._notes = _notes;
    }

    public void set_lastUpdate(Date _lastUpdate) {
        this._lastUpdate = _lastUpdate;
    }

    public void set_groupName(String _groupName) {
        this._groupName = _groupName;
    }

    public void set_attrib(String _attrib) {
        this._attrib = _attrib;
    }
}
