package ie.gavin.ulstudenttimetable.data;

import java.util.Date;

public class Modules {

    private String _ModuleID;
    private String _startTime;
    private String _endTime;
    private String _weekStart;
    private String _weekEnd;
    private String _room;
    private String _lecturer;
    private Date _day;
    private Date _last_update;

    public Modules() {

    }

    public Modules(String _ModuleID, String _startTime, String _endTime, String _weekStart, String _weekEnd, String _room, String _lecturer, Date _day, Date _last_update) {
        this._ModuleID = _ModuleID;
        this._startTime = _startTime;
        this._endTime = _endTime;
        this._weekStart = _weekStart;
        this._weekEnd = _weekEnd;
        this._room = _room;
        this._lecturer = _lecturer;
        this._day = _day;
        this._last_update = _last_update;
    }

    public String get_ModuleID() {
        return _ModuleID;
    }

    public String get_startTime() {
        return _startTime;
    }

    public String get_endTime() {
        return _endTime;
    }

    public String get_weekStart() {
        return _weekStart;
    }

    public String get_weekEnd() {
        return _weekEnd;
    }

    public String get_room() {
        return _room;
    }

    public String get_lecturer() {
        return _lecturer;
    }

    public Date get_day() {
        return _day;
    }

    public Date get_last_update() {
        return _last_update;
    }

    public void set_ModuleID(String _ModuleID) {
        this._ModuleID = _ModuleID;
    }

    public void set_startTime(String _startTime) {
        this._startTime = _startTime;
    }

    public void set_endTime(String _endTime) {
        this._endTime = _endTime;
    }

    public void set_weekStart(String _weekStart) {
        this._weekStart = _weekStart;
    }

    public void set_weekEnd(String _weekEnd) {
        this._weekEnd = _weekEnd;
    }

    public void set_room(String _room) {
        this._room = _room;
    }

    public void set_lecturer(String _lecturer) {
        this._lecturer = _lecturer;
    }

    public void set_day(Date _day) {
        this._day = _day;
    }

    public void set_last_update(Date _last_update) {
        this._last_update = _last_update;
    }
}
