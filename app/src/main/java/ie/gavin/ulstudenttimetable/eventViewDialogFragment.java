package ie.gavin.ulstudenttimetable;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;

import ie.gavin.ulstudenttimetable.data.MyDBHandler;
import ie.gavin.ulstudenttimetable.data.StudentTimetable;

public class EventViewDialogFragment extends EventDialogFragment {

    private StudentTimetable studentTimetable;

    private TextView dateTimeTextView;
    private TextView moduleCodeTextView;
    private TextView titleTextView;
    private TextView typeTextView;
    private TextView locationTextView;
    private TextView weeksTextView;
    private TextView notesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        studentTimetable = super.getStudentTimetable();

        View view = inflater.inflate(R.layout.fragment_view_event, container, false);

        dateTimeTextView = (TextView) view.findViewById(R.id.dateTimeTextView);
        moduleCodeTextView = (TextView) view.findViewById(R.id.moduleCodeTextView);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        typeTextView = (TextView) view.findViewById(R.id.typeTextView);
        locationTextView = (TextView) view.findViewById(R.id.locationTextView);
        weeksTextView = (TextView) view.findViewById(R.id.weeksTextView);
        notesTextView = (TextView) view.findViewById(R.id.notesTextView);


//        return (cal.get(Calendar.DAY_OF_WEEK) + 7 - 2) % 7;
        dateTimeTextView.setText(DateFormatSymbols.getInstance().getWeekdays()[studentTimetable.get_day() + 1] + " " + studentTimetable.get_start_time() + " - " + studentTimetable.get_endTime());
        moduleCodeTextView.setText(studentTimetable.get_moduleCode());
        titleTextView.setText(studentTimetable.get_title());
        typeTextView.setText(studentTimetable.get_type());
        locationTextView.setText(studentTimetable.get_room());

//        String weeks = "Weeks: ";
//        for (Pair<Integer, Integer> week : studentTimetable.get_weeks()) weeks += week.first + "-" + week.second + " ";
        weeksTextView.setText("Weeks: " + studentTimetable.get_weeksFormattedList());

        notesTextView.setText(studentTimetable.get_notes());


//        TextView tv = (TextView) view.findViewById(R.id.text);
//        tv.setText("This is an instance of ActionBarDialog");

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(studentTimetable.get_color());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                int id = item.getItemId();

                if (id == R.id.action_delete) {
                    // Return to activity
                    closeEventDialogListener activity = (closeEventDialogListener) getActivity();
                    activity.onCloseEventDialog(DELETE_ACTION, studentTimetable);
                    MyDBHandler dbHandler;
                    dbHandler = MyDBHandler.getInstance(getActivity());
                    dbHandler.deleteStudentTimetableEntry(studentTimetable);
                    //instead of above pass studentTimetable to deleteSingleStudentTimetable()
                    // need to set current week as only week on the object
                    //this will remove current week entry and leave all others
                    dismiss();
                    return true;
                } else if (id == R.id.action_edit) {
                    // Return to activity
                    closeEventDialogListener activity = (closeEventDialogListener) getActivity();
                    activity.onCloseEventDialog(EDIT_ACTION, studentTimetable);
                    dismiss();
                    return true;
                }

                return true;
            }
        });
        toolbar.inflateMenu(R.menu.edit_delete);
        toolbar.setTitle(studentTimetable.get_moduleCode());

        return view;

    }

}