package ie.gavin.ulstudenttimetable.calendar;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by Oliver on 23/03/2016.
 */
public class CalendarEvent {

    Calendar startDateTime;
    Calendar endDateTime;
    String title;
    int color;
    int databaseId;
    int databaseModuleId;
    boolean originallyAttending = false;
    boolean nowAttending = false;

    public CalendarEvent(Calendar startDateTime, Calendar endDateTime, String title, int color, int databaseId, int databaseModuleId) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.title = title;
        this.color = color;
        this.databaseId = databaseId;
        this.databaseModuleId = databaseModuleId;
    }

    public Calendar getStartDateTime() {
        return startDateTime;
    }

    public Calendar getEndDateTime() {
        return endDateTime;
    }

    public String getTitle() {
        return title;
    }

    public int getColor() {
        return color;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public int getDatabaseModuleId() {
        return databaseModuleId;
    }

    public boolean isOriginallyAttending() {
        return originallyAttending;
    }

    public boolean isNowAttending() {
        return nowAttending;
    }

    public void setOriginallyAttending(boolean originallyAttending) {
        this.originallyAttending = originallyAttending;
    }

    public void setNowAttending(boolean nowAttending) {
        this.nowAttending = nowAttending;
    }

    @Override
    public boolean equals(Object o) {
        Log.v("matcher", "" + this.databaseModuleId + " " + ((CalendarEvent) o).getDatabaseModuleId());
        return this.databaseModuleId == ((CalendarEvent) o).getDatabaseModuleId();
    }
}
