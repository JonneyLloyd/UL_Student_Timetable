package ie.gavin.ulstudenttimetable.calendar;

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

    public CalendarEvent(Calendar startDateTime, Calendar endDateTime, String title, int color, int databaseId) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.title = title;
        this.color = color;
        this.databaseId = databaseId;
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
}
