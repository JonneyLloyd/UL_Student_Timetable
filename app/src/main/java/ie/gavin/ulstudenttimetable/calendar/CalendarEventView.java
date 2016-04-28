package ie.gavin.ulstudenttimetable.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CalendarEventView extends LinearLayout {

    private boolean isChecked = true;

    public CalendarEventView(Context context) {
        super(context);
    }

    public CalendarEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
