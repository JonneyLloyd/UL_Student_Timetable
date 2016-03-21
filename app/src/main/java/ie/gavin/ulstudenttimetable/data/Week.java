package ie.gavin.ulstudenttimetable.data;

import java.util.Date;



public class Week{
    private int _week;
    private String _weekLabel;
    private Date _weekStart;

    public Week() {
    }

    public Week(int _week, String _weekLabel, Date _weekStart) {
        this._week = _week;
        this._weekLabel = _weekLabel;
        this._weekStart = _weekStart;
    }

    public int get_week() {
        return _week;
    }

    public String get_weekLabel() {
        return _weekLabel;
    }

    public Date get_weekStart() {
        return _weekStart;
    }

    public void set_week(int _week) {
        this._week = _week;
    }

    public void set_weekLabel(String _weekLabel) {
        this._weekLabel = _weekLabel;
    }

    public void set_weekStart(Date _weekStart) {
        this._weekStart = _weekStart;
    }
}
