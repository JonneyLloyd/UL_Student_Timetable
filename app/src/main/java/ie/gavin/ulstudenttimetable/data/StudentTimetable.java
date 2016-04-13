package ie.gavin.ulstudenttimetable.data;

public class StudentTimetable {
    private int _idTablePointer;
    private String _moduleCode;
    private int _modulePointer;
    private String _start_time;
    private int _day;//int 0-6
    private String _endTime;
    private int _studentID;
    private String _notes;
    private String _groupName;
    private String _type;
    private String _title;
    private String _lecturer;
    private String _room;
    public StudentTimetable() {
    }

    public StudentTimetable(int _modulePointer) {
        this._modulePointer = _modulePointer;
    }

    public StudentTimetable(int _idTablePointer, String _moduleCode, int _modulePointer, String _start_time, int _day, String _endTime, int _studentID, String _notes, String _groupName, String _type, String _title, String _lecturer, String _room) {
        this._idTablePointer = _idTablePointer;
        this._moduleCode = _moduleCode;
        this._modulePointer = _modulePointer;
        this._start_time = _start_time;
        this._day = _day;
        this._endTime = _endTime;
        this._studentID = _studentID;
        this._notes = _notes;
        this._groupName = _groupName;
        this._type = _type;
        this._title = _title;
        this._lecturer = _lecturer;
        this._room = _room;
    }

    public int get_idTablePointer() {
        return _idTablePointer;
    }

    public String get_moduleCode() {
        return _moduleCode;
    }

    public int get_modulePointer() {
        return _modulePointer;
    }

    public String get_start_time() {
        return _start_time;
    }

    public int get_day() {
        return _day;
    }

    public String get_endTime() {
        return _endTime;
    }

    public int get_studentID() {
        return _studentID;
    }

    public String get_notes() {
        return _notes;
    }

    public String get_groupName() {
        return _groupName;
    }

    public String get_type() {
        return _type;
    }

    public String get_title() {
        return _title;
    }

    public String get_lecturer() {
        return _lecturer;
    }

    public String get_room() {
        return _room;
    }

    public void set_idTablePointer(int _idTablePointer) {
        this._idTablePointer = _idTablePointer;
    }

    public void set_moduleCode(String _moduleCode) {
        this._moduleCode = _moduleCode;
    }

    public void set_modulePointer(int _modulePointer) {
        this._modulePointer = _modulePointer;
    }

    public void set_start_time(String _start_time) {
        this._start_time = _start_time;
    }

    public void set_day(int _day) {
        this._day = _day;
    }

    public void set_endTime(String _endTime) {
        this._endTime = _endTime;
    }

    public void set_studentID(int _studentID) {
        this._studentID = _studentID;
    }

    public void set_notes(String _notes) {
        this._notes = _notes;
    }

    public void set_groupName(String _groupName) {
        this._groupName = _groupName;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public void set_lecturer(String _lecturer) {
        this._lecturer = _lecturer;
    }

    public void set_room(String _room) {
        this._room = _room;
    }
}
