package ie.gavin.ulstudenttimetable.data;

import java.util.ArrayList;

public class Module implements Comparable <Module> {

    private int _idTablePointer;
    private String _ModuleCode;
    private String _startTime;
    private String _endTime;
    private String _room;
    private String _lecturer;
    private int _day;//int 0-6
    private String _groupName;
    private String _type;
    private ArrayList<String> _weeks;

    public Module() {

    }

    public Module(int _idTablePointer, String _ModuleCode, String _startTime, String _endTime, String _room, String _lecturer, int _day, String _groupName, String _type) {
        this._idTablePointer = _idTablePointer;
        this._ModuleCode = _ModuleCode;
        this._startTime = _startTime;
        this._endTime = _endTime;
        this._room = _room;
        this._lecturer = _lecturer;
        this._day = _day;
        this._groupName = _groupName;
        this._type = _type;
    }

    public ArrayList<String> get_weeks() {
        return _weeks;
    }

    public int get_idTablePointer() {
        return _idTablePointer;
    }

    public String get_ModuleCode() {
        return _ModuleCode;
    }

    public String get_startTime() {
        return _startTime;
    }

    public String get_endTime() {
        return _endTime;
    }

    public String get_room() {
        return _room;
    }

    public String get_lecturer() {
        return _lecturer;
    }

    public int get_day() {
        return _day;
    }

    public String get_groupName() {
        return _groupName;
    }

    public String get_type() {
        return _type;
    }

    public void set_idTablePointer(int _idTablePointer) {
        this._idTablePointer = _idTablePointer;
    }

    public void set_ModuleCode(String _ModuleCode) {
        this._ModuleCode = _ModuleCode;
    }

    public void set_startTime(String _startTime) {
        this._startTime = _startTime;
    }

    public void set_endTime(String _endTime) {
        this._endTime = _endTime;
    }

    public void set_room(String _room) {
        this._room = _room;
    }

    public void set_lecturer(String _lecturer) {
        this._lecturer = _lecturer;
    }

    public void set_day(int _day) {
        this._day = _day;
    }

    public void set_groupName(String _groupName) {
        this._groupName = _groupName;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public void set_weeks(ArrayList<String> _weeks) {
        this._weeks = _weeks;
    }

    @Override
    public int compareTo(Module another) {
        if((this._ModuleCode.equals(another._ModuleCode) &&
                this._startTime.equals(another._startTime) &&
                this._endTime.equals(another._endTime) &&
                this._room.equals(another._room) &&
                this._lecturer.equals(another._lecturer) &&
                this._day == another._day &&
                this._groupName.equals(another._groupName) &&
                this._type.equals(another._type)))
            return 0;
        else return 1;
    }
}
