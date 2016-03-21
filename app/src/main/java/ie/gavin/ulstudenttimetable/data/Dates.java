package ie.gavin.ulstudenttimetable.data;

import java.util.Date;



public class Dates {
    private int _weeks;
    private String _weekLabel;
    private Date _weekStart;

    public Dates() {
    }

    public Dates(int _weeks, String _weekLabel, Date _weekStart) {
        this._weeks = _weeks;
        this._weekLabel = _weekLabel;
        this._weekStart = _weekStart;
    }

    public int get_weeks() {
        return _weeks;
    }

    public String get_weekLabel() {
        return _weekLabel;
    }

    public Date get_weekStart() {
        return _weekStart;
    }

    public void set_weeks(int _weeks) {
        this._weeks = _weeks;
    }

    public void set_weekLabel(String _weekLabel) {
        this._weekLabel = _weekLabel;
    }

    public void set_weekStart(Date _weekStart) {
        this._weekStart = _weekStart;
    }
}
