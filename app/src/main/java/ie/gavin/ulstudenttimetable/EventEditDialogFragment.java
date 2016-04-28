package ie.gavin.ulstudenttimetable;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ie.gavin.ulstudenttimetable.data.StudentTimetable;

public class EventEditDialogFragment extends EventDialogFragment {

    private StudentTimetable studentTimetable;
//    private EditText mEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        studentTimetable = super.getStudentTimetable();

        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

//        TextView tv = (TextView) view.findViewById(R.id.text);
//        tv.setText("This is an instance of ActionBarDialog edit");

//        mEditText = (EditText) view.findViewById(R.id.username);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                int id = item.getItemId();

                if (id == R.id.action_cancel) {
                    // Return to activity
                    closeEventDialogListener activity = (closeEventDialogListener) getActivity();
                    activity.onCloseEventDialog(CANCEL_ACTION, studentTimetable);
                    dismiss();
                    return true;
                } else if (id == R.id.action_save) {
                    // Return to activity
                    closeEventDialogListener activity = (closeEventDialogListener) getActivity();
                    activity.onCloseEventDialog(SAVE_ACTION, studentTimetable);
                    dismiss();
                    return true;
                }

                return true;
            }
        });
        toolbar.inflateMenu(R.menu.save_cancel);
        toolbar.setTitle(studentTimetable.get_moduleCode());

        return view;

    }

}