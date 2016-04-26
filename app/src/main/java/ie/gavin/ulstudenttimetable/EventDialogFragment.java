package ie.gavin.ulstudenttimetable;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import ie.gavin.ulstudenttimetable.data.MyDBHandler;
import ie.gavin.ulstudenttimetable.data.StudentTimetable;

public class EventDialogFragment extends DialogFragment {

    // Defines dialog action
    public static final String DELETE_ACTION =
            "ie.gavin.oliver.ulstudenttimetable.DELETE";

    public static final String CANCEL_ACTION =
            "ie.gavin.oliver.ulstudenttimetable.CANCEL";

    public static final String EDIT_ACTION =
            "ie.gavin.oliver.ulstudenttimetable.EDIT";

    public static final String SAVE_ACTION =
            "ie.gavin.oliver.ulstudenttimetable.SAVE";

    public interface closeEventDialogListener {
        void onCloseEventDialog(String action, StudentTimetable studentTimetable);
    }

    private StudentTimetable studentTimetable;

    // Empty constructor required for DialogFragment
    public EventDialogFragment() {

    }

    public StudentTimetable getStudentTimetable() {
        return studentTimetable;
    }

    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        int eventId = args.getInt("eventId");
        MyDBHandler dbHandler = MyDBHandler.getInstance(this.getActivity());
        studentTimetable = dbHandler.getStudentTimetableFromID(eventId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Forces content above the keyboard so that the user can scroll to the bottom with the keyboard up
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
    }

}