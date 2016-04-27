package ie.gavin.ulstudenttimetable;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ie.gavin.ulstudenttimetable.data.StudentTimetable;

public class eventViewDialogFragment extends EventDialogFragment {

    private StudentTimetable studentTimetable;

    private TextView moduleCodeTextView;
    private TextView titleTextView;
    private TextView locationTextView;
    private TextView notesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        studentTimetable = super.getStudentTimetable();

        View view = inflater.inflate(R.layout.fragment_view_event, container, false);

        moduleCodeTextView = (TextView) view.findViewById(R.id.moduleCodeTextView);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        locationTextView = (TextView) view.findViewById(R.id.locationTextView);
        notesTextView = (TextView) view.findViewById(R.id.notesTextView);

        moduleCodeTextView.setText(studentTimetable.get_moduleCode());
        titleTextView.setText(studentTimetable.get_title());
        locationTextView.setText(studentTimetable.get_room());
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
        toolbar.inflateMenu(R.menu.view_event);
        toolbar.setTitle(studentTimetable.get_moduleCode());

        return view;

    }

}